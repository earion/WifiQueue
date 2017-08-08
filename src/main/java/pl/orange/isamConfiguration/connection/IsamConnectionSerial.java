package pl.orange.isamConfiguration.connection;

public class IsamConnectionSerial extends IsamConnectionAbstract implements IsamConnectable {


    public IsamConnectionSerial(String connectionParameters) {
        super(connectionParameters);
    }

    @Override
    public void setConnection() {

    }

    @Override
    public int sendCommand(String command) {
        return 0;
    }

    @Override
    public String getResponse() {
        return null;
    }
}
