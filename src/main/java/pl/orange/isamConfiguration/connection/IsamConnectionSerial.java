package pl.orange.isamConfiguration.connection;

import pl.orange.util.HostListException;

public class IsamConnectionSerial extends IsamConnectionAbstract implements IsamConnectable {


    IsamConnectionSerial(String connectionParameters) {
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

}
