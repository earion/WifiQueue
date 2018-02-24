package pl.orange.networkDevicesConfiguration.ciscoSwitch2960;

import org.apache.log4j.Logger;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static pl.orange.networkDevicesConfiguration.ciscoSwitch2960.VlanMode.ACCESS;

public class CiscoSwitch {

    private static final Logger log = Logger.getLogger(CiscoSwitch.class);


    public String showPortStatus(String port) throws HostListException, IOException {
        portValidation(port);
        try (CiscoSwitchConfigurator ciscoSwitchConfigurator = new CiscoSwitchConfigurator("switch")) {
            String command = "sh ru int gi 0/" + port;
            String output = ciscoSwitchConfigurator.sendConfiguration(command);
            return output.substring(output.indexOf("!") + 1, output.indexOf("end"));
        }
    }

    public void changePortState(String port,Boolean state) throws HostListException, IOException {
        portValidation(port);
        try (CiscoSwitchConfigurator ciscoSwitchConfigurator = new CiscoSwitchConfigurator("switch",true)) {
            ciscoSwitchConfigurator.sendConfiguration("int gi 0/" + port);
            String command = "shutdown";
            if (state) {
                command = "no " + command;
            }
            ciscoSwitchConfigurator.sendConfiguration(command);
        }
    }

    public void changeVlanMode(String port,VlanMode mode,String...vlans) throws HostListException, IOException {
        portValidation(port);
        vlanValidation(mode, vlans);
        switch (mode) {
            case ACCESS : {
                setAccessMode(port,vlans[0]);
                return;
            }
            case TRUNK:  {
                setTrunkMode(port,vlans);
                return;
            }
        }

    }

    private void setTrunkMode(String port, String[] vlans) throws HostListException, IOException {
        try (CiscoSwitchConfigurator ciscoSwitchConfigurator = new CiscoSwitchConfigurator("switch", true)) {
            ciscoSwitchConfigurator.sendConfiguration("int gi 0/" + port);
            ciscoSwitchConfigurator.sendConfiguration("switchport mode trunk");
            String commands = "switchport trunk alloved vlans ";
            commands += "  " + Arrays.asList(vlans).stream().map(Object::toString).collect(Collectors.joining(","));
            ciscoSwitchConfigurator.sendConfiguration(commands);
        }
    }

    private void setAccessMode(String port, String vlan) throws HostListException, IOException {
        try (CiscoSwitchConfigurator ciscoSwitchConfigurator = new CiscoSwitchConfigurator("switch", true)) {
            ciscoSwitchConfigurator.sendConfiguration("int gi 0/" + port);
            ciscoSwitchConfigurator.sendConfiguration("switchport mode access");
            String commands = "switchport access vlan " + vlan;
            ciscoSwitchConfigurator.sendConfiguration(commands);
        }
    }

    private void vlanValidation(VlanMode mode, String[] vlans) throws HostListException {
        if(mode.equals(ACCESS) && vlans.length > 1) {
            throw new HostListException(ExceptionMessages.LOGIC_ERROR,"Access mode should have only one vlan");
        }
    }

    private void portValidation(String port) throws HostListException{
        try {
            int i =   Integer.parseInt(port);
            if (i > 48 || i < 0) throw new HostListException(ExceptionMessages.LOGIC_ERROR,"Port number should be between 0 and 48");
        }catch (NumberFormatException e ) {
            throw  new HostListException(ExceptionMessages.LOGIC_ERROR,e.getMessage());
        }
    }


}
