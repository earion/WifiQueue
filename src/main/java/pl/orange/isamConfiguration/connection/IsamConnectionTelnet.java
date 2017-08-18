package pl.orange.isamConfiguration.connection;

import org.apache.commons.io.LineIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IsamConnectionTelnet extends  IsamConnectionAbstract implements IsamConnectable {


    private Socket telnetSocket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private boolean isLoggedIn = false;

    @Override
    public void setConnection() throws IOException {
        telnetSocket = new Socket(this.getConnectionDestination(), 23);
        out = new PrintWriter(telnetSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(telnetSocket.getInputStream()));
    }

    @Override
    public String sendCommand(String command) throws IOException {
        authorize();
        sendTelnetCommand(command);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return readOutputWithIterator();
    }


    boolean authorize() throws IOException {
        for(int i=0;i<2;i++) {
            if(tryAuthorize()) {
                return true;
            } else {
                disconnect();
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setConnection();
            }
        }
        return false;
    }



    private boolean tryAuthorize() {
        if(isLoggedIn) {
            return true;
        }
        try {
            System.out.println(in.readLine());
            out.println(this.getUser());
            System.out.println(in.readLine());
            out.println(this.getPassword());
            System.out.println(in.readLine());
            System.out.println(in.readLine());
            String out = readOutputWithIterator();
            if (out.contains("Welcome") || out.contains("alarm")) {
                readOutputWithIterator();
                this.isLoggedIn = true;
                return true;
            }
        } catch (IOException e) {

        }
        return false;
    }


    private void sendTelnetCommand(String command) throws IOException {
        System.out.println(command);
        out.println(command);
    }


    @Override
    public void disconnect() throws IOException {
        if(isLoggedIn) {
            out.println("logout");
            isLoggedIn = false;
        }
        in.close();
        out.close();
        telnetSocket.close();
    }

    IsamConnectionTelnet(String connectionParameters) {
        super(connectionParameters);

    }




    private String readOutputWithIterator()  {
        StringBuilder sb = new StringBuilder();
        final LineIterator it = new LineIterator(in);
            while (it.hasNext()) {
                String line = it.nextLine();
                if(line.contains("alarm")) {
                    continue;
                }
                if(line.isEmpty() || line.equals("typ:isadmin># ")) break;
                sb.append(line).append("\n");
            }

        System.out.println(sb.toString());
        return sb.toString();
    }


}
