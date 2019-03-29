import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class which triggers whole simulation
 */
public class Simulation {
    /**
     *
     * @param args First argument (numberOfFloors) - number of floors in simulation
     *             Second argument (numberOfElevators)  - Number of elevators
     *             Third argument (numberOfSteps) - Number of steps of simulation
     *             Fourth argument (maxNumberOfOrdersPerStep) - Maximum number of callings from different floors per step of simulation
     *             Fifth argument (timeToProcess) - Number of steps between elevator opening on an ordered floor and starting moving
     *             Sixth argument (maxNumberOfPressedButtonsInside) - Maximum number of orders for elevator to move on a floor (passengers press buttons inside the elevator
     *
     */
    public static void main(String [] args)
    {
        Integer numberOfFloors = Integer.parseInt(args[0]);
        int numberOfElevators = Integer.parseInt(args[1]);
        int numberOfSteps = Integer.parseInt(args[2]);
        Integer maxNumberOfOrdersPerStep = Integer.parseInt(args[3]);
        Integer timeToProcess = Integer.parseInt(args[4]);
        Integer maxNumberOfPressedButtonsInside = Integer.parseInt(args[5]);
        ArrayList<Elevator> elevators = new ArrayList<>();
        for(int i=0;i<numberOfElevators;i++)
        {
            elevators.add(new Elevator(i,(int)Math.floor(Math.random()*(numberOfFloors+1)),new LinkedList<>(),numberOfFloors,timeToProcess,maxNumberOfPressedButtonsInside));
        }
        ElevatorSystem elevatorSystem = new ElevatorSystem(elevators,numberOfFloors,maxNumberOfOrdersPerStep,timeToProcess);
        for(int i=0;i<numberOfSteps;i++)
        {
            elevatorSystem.step();
        }
    }

    /**
     * Generates orders inside the elevator (passengers press the buttons)
     * @param elevator - Elevator in which buttons are pressed
     * @return - list of buttons (buttons hold number of their floors)
     */
    static ArrayList<ElevatorButton> generateInsideOrders(Elevator elevator)
    {
        int newElevatorOrders = (int) Math.ceil(Math.random() * elevator.getMaxNumOfPressedButtonsInside());
        ArrayList<ElevatorButton> pressedButtons = new ArrayList<>();
        for (int i = 0; i < newElevatorOrders; i++) {
            Integer pressedButtonNumber = (int) Math.floor(Math.random() * elevator.getNumberOfFloors());
            if (!elevator.getFloorButtons()[pressedButtonNumber]&&!pressedButtonNumber.equals(elevator.getActualFloor())) {
                pressedButtons.add(new ElevatorButton(pressedButtonNumber));
                elevator.getFloorButtons()[pressedButtonNumber]=true;
            }
        }
        return pressedButtons;
    }

    /**
     * Generates orders from different floors for the system to serve
     * @param maxNumberOfOrders - maximum number of orders from different floors
     * @param numberOfFloors
     * @param elevatorSystem
     * @return - list of orders from different floors
     */
    static ArrayList<ElevatorCalling> generateElevatorCallings(Integer maxNumberOfOrders,Integer numberOfFloors,ElevatorSystem elevatorSystem)
    {
        int numOfElevatorCallings = (int) Math.ceil(Math.random() * maxNumberOfOrders);
        ArrayList<ElevatorCalling> elevatorCallings = new ArrayList<>();
        for (int i = 0; i < numOfElevatorCallings; i++) {
            Integer floor = (int) Math.floor(Math.random() * numberOfFloors);
            int direction;
            if (Math.random() >= 0.5) {
                direction = 1;
            } else {
                direction = -1;
            }
            ElevatorCalling elevatorCalling = elevatorSystem.callElevator(floor,direction);
            boolean oppositeDirectionExists = false;
            for(ElevatorCalling calling:elevatorCallings)
            {
                if(!calling.equals(elevatorCalling)&&calling.getFloor().equals(elevatorCalling.getFloor()))
                {
                    oppositeDirectionExists= true;
                }
            }
            if (!elevatorCallings.contains(elevatorCalling)&&!oppositeDirectionExists) {
                elevatorCallings.add(elevatorCalling);
            }
        }
        return elevatorCallings;
    }
}
