package pl.orange.networkDevicesConfiguration.dslam;

public class ONT {
    private String slot;
    private String serialNumber;
    private String action;

    public ONT(String slot, String serialNumber, String action) {
        this.slot = slot;
        this.serialNumber = serialNumber;
        this.action = action;
    }

    public String getSlot() {
        return slot;
    }


    public String getSerialNumber() {
        return serialNumber;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "OntSlot{" +
                "slot='" + slot + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
