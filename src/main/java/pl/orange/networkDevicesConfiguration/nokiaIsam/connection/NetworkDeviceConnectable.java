package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import pl.orange.util.HostListException;

import java.io.IOException;

public interface NetworkDeviceConnectable {

    public void setConnection() throws IOException, HostListException;

    public String sendCommand(String command) throws HostListException, IOException;

    public String sendCommand(String command, boolean outputToLog) throws HostListException, IOException;

    public void disconnect() throws IOException;

    public String getPassword();

    public void startKeepingSession();

    public void stopKeepingSession();

}
