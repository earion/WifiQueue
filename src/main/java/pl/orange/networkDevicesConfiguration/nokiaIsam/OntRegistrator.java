package pl.orange.networkDevicesConfiguration.nokiaIsam;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.util.Scanner;

public class OntRegistrator {

    private static final Logger log = Logger.getLogger(OntRegistrator.class);
    private String ontId;
    private String slot;

    public OntRegistrator(int ontId, int slot) {
        this.ontId = Integer.toString(ontId);
        this.slot = Integer.toString(slot);
    }

    public String prepareRegisterCommands(String serialNumber) throws HostListException {
        if (!serialNumber.matches("^[A-Z]{4}[A-F0-9]{8}$")) {
            throw new HostListException(ExceptionMessages.SERIAL_NUMBER_DO_NOT_MATCH_PREFIX, "Serial number " + serialNumber + " format must be XXXXDDDDDDDD");
        }
        return downPort() + configureSernum(serialNumber.toUpperCase()) + upPort();

    }

    public void registerONT(String serialNumber) throws HostListException {
        IsamConfigurator isc = new IsamConfigurator("dslam");
        checkIfOntExistsInConfiguration(isc, serialNumber);
        String commands = prepareRegisterCommands(serialNumber);
        isc.sendConfiguration(commands);
    }

    public void unregisterONT() throws HostListException {
        String commands = preperareUnregisterCommands();
        IsamConfigurator isc = new IsamConfigurator("dslam");
        printInformationAboutPowerOptics(isc);
        isc.sendConfiguration(commands);
    }

    private void printInformationAboutPowerOptics(IsamConfigurator isc) throws HostListException {
        String powerOptics = isc.sendConfiguration("show equipment ont optics 1/1/8/" + ontId + "/" + slot);
        log.info("[POWER OPTICS]: " + powerOptics);
    }

    public String preperareUnregisterCommands() throws HostListException {
        return downPort() + managePortState() + " no sernum \n";
    }


    private String managePortState() {
        return "configure equipment ont interface 1/1/8/" + ontId + "/" + slot;
    }

    //configure equipment ont interface 1/1/8/1/2 admin-state up
    private String upPort() {
        return managePortState() + " admin-state up\n";
    }

    //configure equipment ont interface 1/1/8/1/2 admin-state down
    private String downPort() {
        return managePortState() + " admin-state down\n";
    }

    //configure equipment ont interface 1/1/8/1/2 sernum SMBS:21000936 sw-ver-pland disabled fec-up enable enable-aes enable
    private String configureSernum(String serialNumber) {
        return managePortState() + " sernum " + prepareSerialNUmber(serialNumber)
                + " sw-ver-pland disabled fec-up enable enable-aes enable\n";
    }

    private String prepareSerialNUmber(String serialNumber) {
        return StringUtils.substring(serialNumber, 0, 4) + ":" + StringUtils.substring(serialNumber, 4, 13);
    }

    private void checkIfOntExistsInConfiguration(IsamConfigurator isc, String serialNumber) {
        try {
            String response = getConfigurationFromDSLAM(isc);
            serialNumber = serialNumber.replace("SMBS", "SMBS:");
            unregisterOntIfExistsInConfiguration(serialNumber, response);
        } catch (NullPointerException | HostListException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getConfigurationFromDSLAM(IsamConfigurator isc) throws HostListException, InterruptedException {
        String response = isc.sendConfiguration("info configure equipment ont interface");
        int attempts = 0;
        while (!response.contains("interface 1/1/8") && attempts < 10) {
            response = isc.sendConfiguration("info configure equipment ont interface");
            Thread.sleep(3000);
            attempts++;
        }
        return response;
    }

    private void unregisterOntIfExistsInConfiguration(String serialNumber, String response) throws HostListException {
        if (response.contains("sernum " + serialNumber)) {
            System.out.println("Host [" + serialNumber + "] has already been configured for the DSLAM.");
            String oldSlot = slot;
            slot = getSlotOfSerialNumberFromConfiguration(serialNumber, response);
            if (!slot.isEmpty()) {
                System.out.println("Call unregisterONT() with slot = [" + slot + "]");
                unregisterONT();
            } else {
                System.out.println("The extract slot is empty. Something went wrong.");
            }
            slot = oldSlot;
        }
    }

    private String getSlotOfSerialNumberFromConfiguration(String serialNumber, String response) {
        Scanner sc = new Scanner(response);
        sc.useDelimiter("interface ");
        while (sc.hasNext()) {
            String slotConfiguration = sc.next();
            String[] slotParts = slotConfiguration.split(" ")[0].split("/");
            String slot = slotParts[slotParts.length - 1];
            if (slotConfiguration.contains(serialNumber)) {
                return slot;
            }
        }
        return "";
    }
}
