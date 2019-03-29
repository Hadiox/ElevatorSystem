/**
 * A wrapper class for amounts of time in which elevators come to ordered floor. It's stored in a matrix and in a priority queue
 * as the same object in both of these structures
 * timeToCome - amount of time (in steps of simulation)
 * elevator - elevator which handles order
 * elevatorCalling - a wrapper class which holds eg. number of an ordered floor
 * placeInQueue - an index in elevator task queue in which an order would be set if it was sent to an elevator
 */
public class TimeToComeWrapper implements Comparable<TimeToComeWrapper> {
    private Integer timeToCome;
    private Elevator elevator;
    private ElevatorCalling elevatorCalling;
    private int placeInQueue;

    TimeToComeWrapper(Integer timeToCome, Elevator elevator, ElevatorCalling elevatorCalling, int placeInQueue) {
        this.timeToCome = timeToCome;
        this.elevator = elevator;
        this.elevatorCalling = elevatorCalling;
        this.placeInQueue=placeInQueue;
    }
    int getMatrixRowId()
    {
        return this.getElevatorCalling().getId().getRowId();
    }
    int getFloor()
    {
        return this.getElevatorCalling().getFloor();
    }
    void setButtonPressed(int buttonFloorNumber)
    {
        getElevator().getFloorButtons()[buttonFloorNumber] = true;
    }
    void addToElevatorTaskQueue(int placeInQueue,int floor)
    {
        getElevator().getTaskQueue().add(placeInQueue,floor);
    }
    void addToElevatorTaskQueue(int floor)
    {
        this.getElevator().getTaskQueue().add(floor);
    }
    int getSizeOfElevatorTaskQueue()
    {
        return this.getElevator().getTaskQueue().size();
    }
    int getPlaceInQueue() {
        return placeInQueue;
    }

    Elevator getElevator() {
        return elevator;
    }

    ElevatorCalling getElevatorCalling() {
        return elevatorCalling;
    }

    int getElevatorId()
    {
        return this.getElevator().getElevatorId();
    }

    public int compareTo(TimeToComeWrapper timeToComeWrapper)
    {
        if(this.timeToCome>timeToComeWrapper.timeToCome)
        {
            return 1;
        }
        else
        {
            if(this.timeToCome<timeToComeWrapper.timeToCome)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }
}
