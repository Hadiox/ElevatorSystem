/**
 * Class representing orders from inside of the elevator
 * floorNumber - number of a floor in order
 * ifPressed - determines if a button is pressed - if pressed calling inside is ignored by the system (elevator will
 * get there anyway)
 */
class ElevatorButton {
    private Integer floorNumber;
    private Boolean ifPressed;

    ElevatorButton(Integer floorNumber) {
        this.floorNumber = floorNumber;
        this.ifPressed = false;
    }

    void setIgnored() {
        this.ifPressed = true;
    }

    Integer getFloorNumber() {
        return floorNumber;
    }

    Boolean isIgnored() {
        return ifPressed;
    }
}
