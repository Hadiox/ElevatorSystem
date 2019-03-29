import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class representing an elevator.
 * maxNumOfPressedButtonsInside - maximum amount of buttons pressed by passengers after entering the elevator specified in simulation
 * processingTime - time between elevator opening on ordered floor and closing (in steps of simulation) - specified in simulation
 * timeLeftToProcess - time left to close the elevator on ordered floor
 * floorButtons - a structure representing buttons inside the elevator - true if pressed, false if not pressed
 * taskQueue - structure holding ordered elevator callings
 * buttonsPressedOnActualFloor - structure necessary to present pressed buttons by passengers on specific floor on the screen
 * state - state of an elevator
 */
class Elevator {
    private Integer maxNumOfPressedButtonsInside;
    private Integer elevatorId;
    private Integer actualFloor;
    private Integer processingTime;
    private Integer timeLeftToProcess;
    private boolean[] floorButtons;
    private Integer numberOfFloors;
    LinkedList<Integer> taskQueue;
    LinkedList<Integer> buttonsPressedOnActualFloor;
    private ElevatorState state;

    Elevator(Integer elevatorId, Integer actualFloor, LinkedList<Integer> taskQueue, Integer numberOfFloors, Integer processingTime, Integer maxNumOfPressedButtonsInside) {
        this.elevatorId = elevatorId;
        this.actualFloor = actualFloor;
        this.taskQueue = taskQueue;
        this.processingTime = processingTime;
        this.timeLeftToProcess = 0;
        this.numberOfFloors=numberOfFloors;
        this.maxNumOfPressedButtonsInside = maxNumOfPressedButtonsInside;
        this.state = ElevatorState.STOPPED;
        this.buttonsPressedOnActualFloor = new LinkedList<>();
        floorButtons = new boolean[numberOfFloors];
        for (int i = 0; i < numberOfFloors; i++) {
            floorButtons[i] = false;
        }
    }

    ElevatorState getState() {
        return state;
    }

    Integer getProcessingTime() {
        return processingTime;
    }

    Integer getTimeLeftToProcess() {
        return timeLeftToProcess;
    }

    boolean[] getFloorButtons() {
        return floorButtons;
    }

    void setTimeLeftToProcess() {
        this.timeLeftToProcess--;
    }

    void setState(ElevatorState state) {
        this.state = state;
    }
    int getFromQueue(int index)
    {
        return this.getTaskQueue().get(index);
    }

    Integer getElevatorId() {
        return elevatorId;
    }

    Integer getNumberOfFloors() {
        return numberOfFloors;
    }

    Integer getMaxNumOfPressedButtonsInside() {
        return maxNumOfPressedButtonsInside;
    }
    void goDown()
    {
        this.actualFloor--;
    }
    void goUp()
    {
        this.actualFloor++;
    }

    Integer getActualFloor() {
        return actualFloor;
    }

    LinkedList<Integer> getTaskQueue() {
        return taskQueue;
    }

    /**
     * Schedules orders from inside of elevator. Inserts orders between the orders in the taskQueue - case when
     * an order floor is between earlier ordered floors
     * @param pressedButtons - structure holding pressed buttons (with numbers of floors)
     * @param elevator - elevator in which orders are scheduled
     */

    private void arrangeElevatorTrail(ArrayList<ElevatorButton> pressedButtons, Elevator elevator) {
        LinkedList<Integer> pressedButtonsOnActualFloor = new LinkedList<>();
        ArrayList<ElevatorButton> stillToAdd = new ArrayList<>();
        if(!elevator.taskQueue.isEmpty())
        {
        for (ElevatorButton pressedButton : pressedButtons) {
            if (checkIfDestinationBetweenFloors(actualFloor,taskQueue.get(0),pressedButton.getFloorNumber())) {
                addButtonTaskToQueue(elevator,pressedButton);
                continue;
            }
            for (int i = 0; i < elevator.getTaskQueue().size() - 1; i++) {
                if(checkIfDestinationBetweenFloors(taskQueue.get(i),taskQueue.get(i+1),pressedButton.getFloorNumber()))
                {
                    elevator.getTaskQueue().add(i + 1, pressedButton.getFloorNumber());
                    pressedButton.setIgnored();
                    elevator.getFloorButtons()[pressedButton.getFloorNumber()] = true;
                    pressedButtonsOnActualFloor.add(pressedButton.getFloorNumber());
                    break;
                }
            }
        }
            for (ElevatorButton elevatorButton : pressedButtons) {
                if (!elevatorButton.isIgnored()) {
                    stillToAdd.add(elevatorButton);
                }
            }
    }
        if(!stillToAdd.isEmpty())
        {
            scheduleAtTheEnd(stillToAdd,pressedButtonsOnActualFloor);

        }
        this.buttonsPressedOnActualFloor=pressedButtonsOnActualFloor;
    }

    /**
     * Schedules left orders at the end of the queue - each time finds the closest floor to get and inserts it in the
     * end of taskQueue
     * @param pressedButtons - look in arrangeElevatorTrail
     * @param pressedButtonsOnActualFloor - structure to hold recently pressed buttons
     */
    private void scheduleAtTheEnd(ArrayList<ElevatorButton> pressedButtons,LinkedList<Integer>pressedButtonsOnActualFloor) {
        if(taskQueue.isEmpty())
        {
            setClosestFloorAtEnd(pressedButtons,actualFloor,pressedButtonsOnActualFloor);
        }
        for(int i=0;i<pressedButtons.size();i++)
        {
            setClosestFloorAtEnd(pressedButtons,taskQueue.get(taskQueue.size()-1),pressedButtonsOnActualFloor);
        }
    }

    /**
     * Communicates with simulation to get the orders from pressed buttons by passengers after processing on ordered floor
     */

    private void getNewOrdersFromInside() {
        ArrayList<ElevatorButton> pressedButtons = Simulation.generateInsideOrders(this);
        arrangeElevatorTrail(pressedButtons, this);
    }

    /**
     * Checks the condition if elevator arrived on ordered floor
     * @return true if arrived, false if not
     */
    private boolean checkIfElevatorArrived()
    {
       return !taskQueue.isEmpty()&&timeLeftToProcess == 0 && taskQueue.get(0).equals(actualFloor);
    }

    /**
     * Checks if checkedFloor is between floor1 and floor2
     * @param floor1
     * @param floor2
     * @param checkedFloor
     * @return
     */
    private boolean checkIfDestinationBetweenFloors(Integer floor1,Integer floor2,Integer checkedFloor)
    {
        return (floor1 > floor2&&floor1 > checkedFloor && floor2 < checkedFloor)||
                (floor1 < floor2 && floor1 < checkedFloor && floor2 > checkedFloor);
    }
    private void addButtonTaskToQueue(Elevator elevator, ElevatorButton pressedButton)
    {
        elevator.getTaskQueue().add(0, pressedButton.getFloorNumber());
        pressedButton.setIgnored();
        elevator.getFloorButtons()[pressedButton.getFloorNumber()] = true;
    }

    /**
     * Finds the closest floor from pressedButtons after the last calling from the queue and inserts it as the last element of the queue
     * @param pressedButtons
     * @param endFloor
     * @param pressedButtonsOnActualFloor
     */
    private void setClosestFloorAtEnd(ArrayList<ElevatorButton> pressedButtons,Integer endFloor,LinkedList<Integer>pressedButtonsOnActualFloor)
    {
        int distance = Integer.MAX_VALUE;
        ElevatorButton closestFloorButton = null;
        for(ElevatorButton pressedButton:pressedButtons)
        {
            if(!pressedButton.isIgnored()&&distance>ElevatorSystem.countDistanceBetweenFloors(endFloor,pressedButton.getFloorNumber()))
            {
                distance = ElevatorSystem.countDistanceBetweenFloors(endFloor,pressedButton.getFloorNumber());
                closestFloorButton = pressedButton;
            }
        }
        if(closestFloorButton!=null)
        {
            pressedButtonsOnActualFloor.add(closestFloorButton.getFloorNumber());
            taskQueue.add(closestFloorButton.getFloorNumber());
            closestFloorButton.setIgnored();
        }
    }

    /**
     * Sets an elevator in processing state
     */
    void startProcessing()
    {
        state = ElevatorState.STOPPED;
        if (checkIfElevatorArrived())
        {
            timeLeftToProcess = processingTime;
            state = ElevatorState.PROCESSING_OR_MOVING;
            floorButtons[taskQueue.get(0)]=false;
            getNewOrdersFromInside();
            taskQueue.remove(0);
        }
    }
}
