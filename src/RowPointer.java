/**
 * Class which is a representant of a row - an object stored as a reference - changed as a property of a row once -
 * then changed in every row element
 */
class RowPointer {
    private int rowId;

    void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    int getRowId() {
        return rowId;
    }

    RowPointer(Integer rowId) {
        this.rowId = rowId;
    }
}
