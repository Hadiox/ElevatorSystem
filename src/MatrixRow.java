import java.util.ArrayList;

/**
 * Class representing the row of the matrix - basically single order. Elements in the row of matrix
 * represent amounts of time (TimeToComeWrapper) for each elevator to get on the ordered floor.
 * RowPointer is an object held in each row and also in each element of a row - represents a row.
 * If changed as a property of a row once - then changed for each element of a row.
 */
class MatrixRow {
    private ArrayList<TimeToComeWrapper> row;
    private RowPointer rowId;

    RowPointer getRowId() {
        return rowId;
    }
    void setRowId(int rowId)
    {
        this.rowId.setRowId(rowId);
    }
    void setInRow(int id,TimeToComeWrapper timeToComeWrapper)
    {
        this.getRow().set(id,timeToComeWrapper);
    }
    TimeToComeWrapper getTimeWrapperFromRow(int elevatorId)
    {
        return this.getRow().get(elevatorId);
    }
    ArrayList<TimeToComeWrapper> getRow() {
        return row;
    }
    ElevatorCalling getSpecElevatorCalling(TimeToComeWrapper timeToComeWrapper)
    {
        return this.getTimeWrapperFromRow(timeToComeWrapper.getElevatorId()).getElevatorCalling();
    }
    void add(TimeToComeWrapper timeToComeWrapper)
    {
        this.getRow().add(timeToComeWrapper);
    }


    MatrixRow(RowPointer rowId) {
        this.row = new ArrayList<>();
        this.rowId = rowId;
    }
}
