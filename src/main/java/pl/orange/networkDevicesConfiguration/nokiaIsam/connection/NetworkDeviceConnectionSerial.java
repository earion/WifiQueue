package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import pl.orange.util.HostListException;

import java.io.IOException;

public class NetworkDeviceConnectionSerial extends NetworkDeviceConnectionAbstract implements NetworkDeviceConnectable {


    NetworkDeviceConnectionSerial(String connectionParameters) {
        super(connectionParameters);
    }

    @Override
    public void setConnection() {

    }

    @Override
    public String sendCommand(String command) throws HostListException {
        return null;
    }

    @Override
    public String sendCommand(String command, boolean outputToLog) throws HostListException, IOException {
        return null;
    }


    @Override
    public void disconnect() {

    }

    @Override
    public void startKeepingSession() {
    }

    @Override
    public void stopKeepingSession() {
    }

}
