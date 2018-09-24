package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import java.io.IOException;

abstract class NetworkDeviceConnectionAbstract implements NetworkDeviceConnectable {
    private String conectionType;
    private String user;
    private String password;
    private String connectionDestination;

    NetworkDeviceConnectionAbstract(String connectionParameters) {
        parseParameters(connectionParameters);

    }

    private void parseParameters(String connectionParameters) {
        String[] params = connectionParameters.split(";");
        connectionDestination = params[2];
        conectionType = params[3];
        user = params[0];
        password = params[1];
    }

    public String getConectionType() {
        return conectionType;
    }

    String getUser() {
        return user;
    }

    String getConnectionDestination() {
        return connectionDestination;
    }

    public void setConnectionDestination(String connectionDestination) {
        this.connectionDestination = connectionDestination;
    }

    public abstract void disconnect() throws IOException;

    public String getPassword() {
        return password;
    }
}
