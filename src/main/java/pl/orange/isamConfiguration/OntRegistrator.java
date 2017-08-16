package pl.orange.isamConfiguration;

import org.apache.commons.lang.StringUtils;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.IOException;

public class OntRegistrator {

    String ontId;
    String slot;


    public OntRegistrator(int ontId, int slot) throws IOException {
        this.ontId = Integer.toString(ontId);
        this.slot = Integer.toString(slot);
    }



    public String register(String serialNumber) throws HostListException{
        if(!serialNumber.matches("^[A-Z]{4}\\d{8}$")) {
            throw new HostListException(ExceptionMessages.SERIAL_NUMBER_DO_NOT_MATCH_PREFIX,"Serial number " + serialNumber + " format must be XXXXDDDDDDDD");
        }
        StringBuilder commands = new StringBuilder();
        commands.append(downPort());
        commands.append(configureSernum(serialNumber));
        commands.append(upPort());
        return commands.toString();
    }

    public String unregister() throws HostListException {
       String command = downPort();
       return command.toString();
    }


    private String managePortState() {
        StringBuilder sb = new StringBuilder();
        sb.append("configure equipment ont interface 1/1/8/");
        sb.append(ontId);
        sb.append("/");
        sb.append(slot);
        sb.append(" admin-state ");
        return sb.toString();
    }

    //configure equipment ont interface 1/1/8/1/2 admin-state up
    private String upPort() {
        return managePortState() + "up\n";
    }

    //configure equipment ont interface 1/1/8/1/2 admin-state down
    private String downPort() {
        return managePortState() +"down\n";
    }

    //configure equipment ont interface 1/1/8/1/2 sernum SMBS:21000936 sw-ver-pland disabled fec-up enable enable-aes enable
    private String configureSernum(String serialNumber) {
        StringBuilder tmp = new StringBuilder();
        tmp.append(managePortState());
        tmp.append(prepareSerialNUmber(serialNumber));
        tmp.append(" sw-ver-pland disabled fec-up enable enable-aes enable\n");
        return tmp.toString();
    }

    private String prepareSerialNUmber(String serialNumber) {
       return  StringUtils.substring(serialNumber, 0, 4) + ":" + StringUtils.substring(serialNumber, 5, 13);
    }

}
