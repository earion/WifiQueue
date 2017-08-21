package pl.orange.isamConfiguration.connection;

import java.io.IOException;

abstract class IsamConnectionAbstract implements IsamConnectable {


    public String getConectionType() {
        return conectionType;
    }

    String getUser() {
        return user;
    }

    String getPassword() {
        return password;
    }

    String getConnectionDestination() {
        return connectionDestination;
    }

    public abstract void disconnect() throws IOException;

    private String conectionType;
    private String user;
    private String password;
    private String connectionDestination;


    IsamConnectionAbstract(String connectionParameters) {
        parseParameters(connectionParameters);

    }

    private void parseParameters(String connectionParameters) {
        String[] params = connectionParameters.split(";");
        connectionDestination = params[2];
        conectionType = params[3];
        user = params[0];
        password = params[1];
    }
}