package pl.orange.isamConfiguration;

import org.apache.commons.lang3.StringUtils;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

public class OntRegistrator {

    private String ontId;
    private String slot;


    public OntRegistrator(int ontId, int slot) {
        this.ontId = Integer.toString(ontId);
        this.slot = Integer.toString(slot);
    }



    String prepareRegisterCommands(String serialNumber) throws HostListException{
        if(!serialNumber.matches("^[A-Z]{4}[A-F0-9]{8}$")) {
            throw new HostListException(ExceptionMessages.SERIAL_NUMBER_DO_NOT_MATCH_PREFIX,"Serial number " + serialNumber + " format must be XXXXDDDDDDDD");
        }
        StringBuilder commands = new StringBuilder();
        commands.append(downPort())
                .append(configureSernum(serialNumber.toUpperCase()))
                .append(upPort());
        return commands.toString();

    }

    public void registerONT(String serialNumber) throws HostListException {
        String commands = prepareRegisterCommands(serialNumber);
        IsamConfigurator.getInstance().sendConfiguration(commands);
    }

    void unregisterONT() throws HostListException {
        String commands = preperareUnregisterCommands();
        IsamConfigurator.getInstance().sendConfiguration(commands);

    }

    String preperareUnregisterCommands() throws HostListException {
       return downPort();
    }


    private String managePortState() {
        StringBuilder sb = new StringBuilder();
        sb.append("configure equipment ont interface 1/1/8/")
        .append(ontId)
        .append("/")
        .append(slot);
        return sb.toString();
    }

    //configure equipment ont interface 1/1/8/1/2 admin-state up
    private String upPort() {
        return managePortState() + " admin-state up\n";
    }

    //configure equipment ont interface 1/1/8/1/2 admin-state down
    private String downPort() {
        return managePortState() +" admin-state down\n";
    }

    //configure equipment ont interface 1/1/8/1/2 sernum SMBS:21000936 sw-ver-pland disabled fec-up enable enable-aes enable
    private String configureSernum(String serialNumber) {
        StringBuilder tmp = new StringBuilder();
        tmp.append(managePortState())
                .append( " sernum ")
                .append(prepareSerialNUmber(serialNumber))
                .append(" sw-ver-pland disabled fec-up enable enable-aes enable\n");
        return tmp.toString();
    }

    private String prepareSerialNUmber(String serialNumber) {
       return  StringUtils.substring(serialNumber, 0, 4) + ":" + StringUtils.substring(serialNumber, 4, 13);
    }


}
