package pl.orange.isamConfiguration;

import org.apache.commons.lang.StringUtils;
import pl.orange.util.HostListException;

public class OntRegistrator {

    private final Object test;
    String ontId;
    String slot;


    public OntRegistrator(int ontId, int slot, boolean test) {
        this.ontId = Integer.toString(ontId);
        this.slot = Integer.toString(slot);
        this.test = test;
    }



    public String register(String serialNumber) throws HostListException{
        StringBuilder commands = new StringBuilder();
        commands.append(downPort());
        commands.append("\n");
        commands.append(configureSernum(serialNumber));
        commands.append("\n");
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
        return managePortState() + "up";
    }

    //configure equipment ont interface 1/1/8/1/2 admin-state down
    private String downPort() {
        return managePortState() +"down";
    }

    //configure equipment ont interface 1/1/8/1/2 sernum SMBS:21000936 sw-ver-pland disabled fec-up enable enable-aes enable
    private String configureSernum(String serialNumber) {
        StringBuilder tmp = new StringBuilder();
        tmp.append(managePortState());
        tmp.append(prepareSerialNUmber(serialNumber));
        tmp.append("sw-ver-pland disabled fec-up enable enable-aes enable");
        return tmp.toString();
    }

    private String prepareSerialNUmber(String serialNumber) {
       return  StringUtils.substring(serialNumber, 0, 4) + ":" + StringUtils.substring(serialNumber, 5, 13);
    }

}
