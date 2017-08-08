package pl.orange.isamConfiguration.connection;

import pl.orange.util.HostListException;

import java.io.IOException;

public interface IsamConnectable {



    public void setConnection() throws IOException, HostListException;
    public int sendCommand(String command);
    public String getResponse();

}
