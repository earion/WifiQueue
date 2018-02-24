package pl.orange.networkDevicesConfiguration.ciscoSwitch2960;

import org.apache.log4j.Logger;
import pl.orange.networkDevicesConfiguration.NetworkDeviceConfigurator;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.Closeable;
import java.io.IOException;

public class CiscoSwitchConfigurator extends NetworkDeviceConfigurator implements Closeable {

    private static final Logger log = Logger.getLogger(CiscoSwitchConfigurator.class);

    protected CiscoSwitchConfigurator(String name) throws HostListException {
        super("switch");
        try {
            networkDevice.setConnection();
        } catch (IOException e) {
            throw new HostListException(ExceptionMessages.SSH_FAILURE,e.getMessage());
        }
    }

    protected CiscoSwitchConfigurator(String name,Boolean mode) throws HostListException {
        this(name);
        if (mode) {
            sendConfiguration("configure terminal");
        }
    }

    @Override
    protected void disconnect() throws IOException {
            networkDevice.disconnect();
    }

    @Override
    protected String sendConfiguration(String commands) throws HostListException {
        try {
            String out = "";
            log.info("Sending command:" + commands);
            out = networkDevice.sendCommand(commands);
            return out;
        } catch (IOException e) {
            throw new HostListException(ExceptionMessages.SWITCH_CONNECTION_ISSUE,e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
            disconnect();
    }
}
