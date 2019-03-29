import java.util.ArrayList;
import java.util.PriorityQueue;

class ElevatorSystem {
    private ArrayList<Elevator> elevators;
    private Integer numberOfFloors;
    private Integer maxNumberOfOrders;
    private Integer timeToProcess;
    private Integer numberOfStep;
    ElevatorSystem(ArrayList<Elevator> elevators, Integer numberOfFloors, Integer maxNumberOfOrders, Integer timeToProcess) {
        this.elevators = elevators;
        this.numberOfFloors = numberOfFloors;
        this.maxNumberOfOrders = maxNumberOfOrders;
        this.timeToProcess = timeToProcess;
        this.numberOfStep = 0;
    }

    /**
     * Method of a desired in task interface. Calls an elevator on a floor - used in simulation (step method)
     * @param floor - floor on which an elevator has to get
     * @param direction - direction where an elevator is supposed to go after reaching the floor
     * @return
     */
    public ElevatorCalling callElevator(Integer floor,Integer direction)
    {
        return new ElevatorCalling(floor, direction);
    }

    /**
     * Method of a desired in task interface. Within a step: moves an elevator up or down or operates processing an elevator.
     * @param elevator - elevator operated by the system
     */
    public void moveElevator(Elevator elevator) {

        if (elevator.getTimeLeftToProcess() > 0) {
            elevator.setTimeLeftToProcess();
            if(elevator.getTimeLeftToProcess()==0)
            {
                elevator.setState(ElevatorState.DEPARTING);
            }
            return;
        }
        if (!elevator.getTaskQueue().isEmpty()) {
            if (elevator.getActualFloor() - elevator.getTaskQueue().get(0) > 0) {
                elevator.goDown();
                elevator.startProcessing();
            } else if (elevator.getActualFloor() - elevator.getTaskQueue().get(0) < 0) {
                elevator.goUp();
                elevator.startProcessing();
            }
        }
        else
        {
            elevator.setState(ElevatorState.STOPPED);
        }
    }

    /**
     * Method of desired in task interface. Shows the status of whole system of elevators within the step (actual floor
     * of elevator, its task queue, state and pressed buttons after processing).
     */
    public void printStatus()
    {
        for(Elevator elevator:elevators) {
            System.out.println("ELEVATOR " + elevator.getElevatorId() + " ON FLOOR: " + elevator.getActualFloor());
            System.out.print("ELEVATOR QUEUE: ");
            for (int i : elevator.taskQueue) {
                System.out.print(i + " ");
            }
            System.out.print("\n");
            System.out.print("ELEVATOR STATE: ");
            if (elevator.getState() == ElevatorState.PROCESSING_OR_MOVING) {
                System.out.print(" PROCESSING... STEPS LEFT: " + elevator.getTimeLeftToProcess());
            } else {
                if (elevator.getState() == ElevatorState.STOPPED) {
                    if (!elevator.getTaskQueue().isEmpty()) {
                        System.out.println("ELEVATOR IS MOVING...");
                    } else {
                        System.out.println("ELEVATOR IS NOT MOVING");
                    }
                } else {
                    System.out.println("ELEVATOR DEPARTING NEXT STEP");
                    System.out.print("PRESSED BUTTONS DURING PROCESSING: ");
                    for (Integer i : elevator.buttonsPressedOnActualFloor) {
                        System.out.print(i + " ");
                    }
                }
            }
            System.out.println("\n");
        }
    }

    /**
     * Method of desired in task interface. Makes a step of simulation (moving elevators, printing status of the system,
     * generating new elevator callings from different floors, scheduling new callings).
     */
   public void step()
    {
        System.out.println("STEP NUMBER "+numberOfStep+"\n");
        printStatus();
        System.out.println("\n");
        for(Elevator elevator:elevators)
        {
            moveElevator(elevator);
        }
        ArrayList<ElevatorCalling> elevatorCallings = Simulation.generateElevatorCallings(maxNumberOfOrders,numberOfFloors,this);
        this.scheduleElevatorCallings(elevatorCallings);
        numberOfStep++;
    }

    /**
     * Method which schedules new elevator callings - info in readme.
     * @param elevatorCallings - list of new elevator callings
     */
    private void scheduleElevatorCallings(ArrayList<ElevatorCalling> elevatorCallings)
    {
        ArrayList<MatrixRow> matrix = new ArrayList<>();
        PriorityQueue<TimeToComeWrapper> priorityQueue = new PriorityQueue<>();
        int rowNumber = 0;
        for(ElevatorCalling elevatorCalling:elevatorCallings)
        {
            if(checkIfNotNecessaryToSchedule(elevatorCalling))
            {
                continue;
            }
            matrix.add(new MatrixRow(new RowPointer(rowNumber)));
            for(Elevator elevator:elevators)
            {
                TimeToComeWrapper timeToComeWrapper = countTimeToCome(elevatorCalling,elevator);
                timeToComeWrapper.getElevatorCalling().setId(matrix.get(rowNumber).getRowId());
                priorityQueue.add(timeToComeWrapper);
                matrix.get(rowNumber).add(timeToComeWrapper);
            }
            rowNumber++;
        }
        while(!priorityQueue.isEmpty())
        {
            TimeToComeWrapper timeToComeWrapper = priorityQueue.poll();
            for(TimeToComeWrapper toRemove:matrix.get(timeToComeWrapper.getMatrixRowId()).getRow())
            {
                priorityQueue.remove(toRemove);
            }
            if(timeToComeWrapper.getPlaceInQueue()>=timeToComeWrapper.getSizeOfElevatorTaskQueue())
            {
                timeToComeWrapper.addToElevatorTaskQueue(timeToComeWrapper.getFloor());
                timeToComeWrapper.setButtonPressed(timeToComeWrapper.getFloor());
            }
            else
            {
                timeToComeWrapper.addToElevatorTaskQueue(timeToComeWrapper.getPlaceInQueue(),timeToComeWrapper.getFloor());
                timeToComeWrapper.setButtonPressed(timeToComeWrapper.getFloor());

            }
            for(int j = timeToComeWrapper.getMatrixRowId()+1;j<matrix.size();j++)
            {
                matrix.get(j).setRowId(j-1);
            }
            matrix.remove(timeToComeWrapper.getMatrixRowId());
            for(MatrixRow callingRowToReschedule:matrix)
            {
                TimeToComeWrapper newTimeToComeWrapper = countTimeToCome(callingRowToReschedule.getSpecElevatorCalling(timeToComeWrapper),timeToComeWrapper.getElevator());
                priorityQueue.remove(callingRowToReschedule.getTimeWrapperFromRow(timeToComeWrapper.getElevatorId()));
                priorityQueue.add(newTimeToComeWrapper);
                callingRowToReschedule.setInRow(timeToComeWrapper.getElevatorId(),newTimeToComeWrapper);
            }
        }
    }

    /**
     * Method which begins counting amount of time needed for an elevator to get on a floor in elevatorCalling (in steps of simulation).
     * Operates case when elevator has an empty task queue.
     * @param elevatorCalling
     * @param elevator
     * @return
     */
    private TimeToComeWrapper countTimeToCome(ElevatorCalling elevatorCalling, Elevator elevator)
    {
        Integer distance = 0;
        if(elevator.getTaskQueue().isEmpty())
        {
            if(elevator.getTimeLeftToProcess()!=0)
            {
                distance+=elevator.getTimeLeftToProcess();
            }
            return new TimeToComeWrapper(countDistanceBetweenFloors(elevator.getActualFloor(),elevatorCalling.getFloor())+distance,elevator,elevatorCalling,0);
        }
        else
        {
            return countTimeWithQueue(elevatorCalling,elevator);
        }
    }
    /**
     * Method which counts amount of time needed for an elevator to get on a floor in elevatorCalling (in steps of simulation).
     * @param elevatorCalling
     * @param elevator
     * @return
     */
    private TimeToComeWrapper countTimeWithQueue(ElevatorCalling elevatorCalling, Elevator elevator)
    {
        Integer distance = elevator.getTimeLeftToProcess();
        int placeInQueue = 0;
        if(checkIfDestinationBetweenFloors(elevator.getActualFloor(),elevator.getFromQueue(0),elevatorCalling.getFloor(),elevatorCalling.getDirection()))
        {
            return new TimeToComeWrapper(countDistanceBetweenFloors(elevator.getActualFloor(),elevatorCalling.getFloor())+distance,elevator,elevatorCalling,placeInQueue);
        }
        else
        {
            distance+=countDistanceBetweenFloors(elevator.getActualFloor(),elevator.getFromQueue(0));
            placeInQueue++;
        }
            for (int i = 0; i < elevator.getTaskQueue().size() - 1; i++) {
                if(checkIfDestinationBetweenFloors(elevator.getFromQueue(i),elevator.getFromQueue(i+1),elevatorCalling.getFloor(),elevatorCalling.getDirection()))
                {
                    return new TimeToComeWrapper(distance+countDistanceBetweenFloors(elevator.getFromQueue(i),elevatorCalling.getFloor()),elevator,elevatorCalling,placeInQueue);
                }
                else
                {
                    distance+=(countDistanceBetweenFloors(elevator.getFromQueue(i),elevator.getFromQueue(i+1))+timeToProcess);
                    placeInQueue++;
                }
            }
            return new TimeToComeWrapper (distance+countDistanceBetweenFloors(elevator.getFromQueue(elevator.getTaskQueue().size()-1),elevatorCalling.getFloor()),elevator,elevatorCalling,placeInQueue);
    }

    /**
     * Counts distance between floor1 and floor2 (in steps of simulation)
     * @param floor1
     * @param floor2
     * @return
     */
    static Integer countDistanceBetweenFloors(Integer floor1,Integer floor2)
    {
        return Math.abs(floor1-floor2);
    }

    /**
     * Checks if an elevator calling is necessary to schedule (because maybe there is an elevator on desired floor
     * or there is an elevator which has this floor in a queue
     * @param elevatorCalling
     * @return
     */
    private boolean checkIfNotNecessaryToSchedule(ElevatorCalling elevatorCalling)
    {
        for(Elevator elevator:elevators)
        {
            if(checkIfElevatorOnFloor(elevator,elevatorCalling.getFloor()))
            {
                return true;
            }
            if(checkIfCallingInQueue(elevator,elevatorCalling))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is an elevator on a desired floor.
     * @param elevator
     * @param floor
     * @return
     */
    private boolean checkIfElevatorOnFloor(Elevator elevator,Integer floor)
    {
        if(elevator.getActualFloor().equals(floor))
        {
            if(elevator.getProcessingTime()==0)
            {
                elevator.getTaskQueue().add(0,floor);
                moveElevator(elevator);
                return true;
            }
            else
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the desired floor specified in a calling is already in an elevator.
     * @param elevator
     * @param elevatorCalling
     * @return
     */
    private boolean checkIfCallingInQueue(Elevator elevator,ElevatorCalling elevatorCalling)
    {
        for(Integer task:elevator.getTaskQueue())
        {
            if(task.equals(elevatorCalling.getFloor()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the checkedFloor is between floor1 and floor2. Takes direction into account.
     * @param floor1
     * @param floor2
     * @param checkedFloor
     * @param direction
     * @return
     */
    private boolean checkIfDestinationBetweenFloors(Integer floor1,Integer floor2,Integer checkedFloor,int direction)
    {
        return (floor1 > floor2&&floor1 > checkedFloor && floor2 < checkedFloor&&direction == -1)||
                (floor1 < floor2 && floor1 < checkedFloor && floor2 > checkedFloor&&direction==1);
    }
}
