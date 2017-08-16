package pl.orange.isamConfiguration.connection;

import java.io.IOException;

public class IsamConnectionSerial extends IsamConnectionAbstract implements IsamConnectable {


    public IsamConnectionSerial(String connectionParameters) {
        super(connectionParameters);
    }

    @Override
    public void setConnection() {

    }

    @Override
    public String sendCommand(String command) throws IOException {
        return null;
    }


    @Override
    public void disconnect() {

    }

}
