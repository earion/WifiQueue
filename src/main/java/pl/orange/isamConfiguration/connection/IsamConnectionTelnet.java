package pl.orange.isamConfiguration.connection;

import pl.orange.util.HostListException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IsamConnectionTelnet extends  IsamConnectionAbstract implements IsamConnectable {


    Socket telnetSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    private boolean isLoggedin = false;

    @Override
    public void setConnection() throws IOException, HostListException {
        try {
            telnetSocket = new Socket(this.getConnectionDestination(), 23);
            out = new PrintWriter(telnetSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(telnetSocket.getInputStream()));
        } catch (IOException e) {
            return;
        }
    }

    @Override
    public String sendCommand(String command) throws IOException, HostListException {
        try {
            authorize();
            Thread.sleep(3000);
            sendTelnetCommand(command);
            return readOutput();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "";
    }


    boolean authorize() throws IOException, InterruptedException, HostListException {
        if(isLoggedin) {
            return true;
        }
        in.readLine(); //Enter on the beggining
        out.println(this.getUser());
        in.readLine();
        out.println(this.getPassword());
        in.readLine();
        in.readLine();
        if(in.readLine().contains("Welcome")) {
            in.readLine(); // read about last login
            this.isLoggedin = true;
            return true;
        }
        else {
            return false;
        }
    }


    private void sendTelnetCommand(String command) throws IOException, InterruptedException {
        out.println(command);
    }


    @Override
    public void disconnect() throws IOException, InterruptedException {
       if(isLoggedin) {
           out.println("logout");
           isLoggedin = false;
       }
        in.close();
        out.close();
        telnetSocket.close();
    }

    public IsamConnectionTelnet(String connectionParameters) {
        super(connectionParameters);

    }



    String readOutput() throws IOException {
        StringBuilder sb = new StringBuilder();
        String output;
        while(!(output = in.readLine()).isEmpty()) {
            if(output.contains("alarm")) {
                continue;
            }
            sb.append(output + "\n");
        }
        return sb.toString();
    }

}
