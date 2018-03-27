package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import pl.orange.util.HostListException;

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
    public void disconnect() {

    }

    @Override
    public void startKeepingSession() {
    }

    @Override
    public void stopKeepingSession() {
    }

}
