/**
 * Wrapper class for an order from different floors for system to deal with
 * floor - number of floor where an elevator has to get
 * direction - direction where passengers from destination floor want to go after entering the elevator
 * id - id of the row in a matrix of elevator callings - an object
 * shared with each element of the row of matrix and a property of a row. If changed
 * as a property of a row once - then changed for each element of a row (needed to distinguish after pulling out of priority queue
 * which order to remove from matrix)
 */
public class ElevatorCalling {
    private Integer floor;
    private Integer direction;
    private RowPointer id;

    Integer getFloor() {
        return floor;
    }

    Integer getDirection() {
        return direction;
    }

    ElevatorCalling(Integer floor, Integer direction) {
        this.floor = floor;
        this.direction = direction;
        this.id = null;
    }

    void setId(RowPointer id) {
        this.id = id;
    }

    RowPointer getId() {
        return id;
    }
}
