package pl.orange.isamConfiguration.connection;

import pl.orange.util.HostListException;

import java.io.IOException;

public interface IsamConnectable {



    public void setConnection() throws IOException, HostListException;
    public String sendCommand(String command) throws IOException, HostListException;
    public void disconnect() throws IOException, InterruptedException;



}
