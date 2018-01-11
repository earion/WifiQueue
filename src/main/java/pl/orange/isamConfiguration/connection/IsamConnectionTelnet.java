package pl.orange.isamConfiguration.connection;

import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class IsamConnectionTelnet extends  IsamConnectionAbstract implements IsamConnectable {

    private static final Logger log = Logger.getLogger(IsamConnectable.class);

    private Socket telnetSocket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private boolean isLoggedIn = false;
    private int port;

    @Override
    public void setConnection() throws IOException {
        telnetSocket = new Socket(this.getConnectionDestination(), port);
        out = new PrintWriter(telnetSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(telnetSocket.getInputStream()));
    }

    @Override
    public String sendCommand(String command) throws IOException {
        authorize();
        sendTelnetCommand(command);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return readAnswer();
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
            log.info(in.readLine());
            out.println(this.getUser());
            log.info(in.readLine());
            out.println(this.getPassword());
            log.info(in.readLine());
            log.info(in.readLine());
            String out = readOutputWithIterator();
            if (out.contains("Welcome") || out.contains("alarm")) {
                readOutputWithIterator();
                this.isLoggedIn = true;
                return true;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    private void sendTelnetCommand(String command) throws IOException {
        log.info(command);
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
        port = 23;
    }

    IsamConnectionTelnet(String connectionParameters,int port) {
        super(connectionParameters);
        this.port = port;
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
       log.info("Welcome to ISAM");
        return sb.toString();
    }



    private String readAnswer()  {
        StringBuilder sb = new StringBuilder();
        final LineIterator it = new LineIterator(in);
        while (it.hasNext()) {
            String line = it.nextLine();
            if(line.contains("Error") || line.contains("invalid token")) {
                log.info(line);
                return line;
            }
            if(line.contains("alarm")) {
                continue;
            }
            if(line.endsWith("# ")) break;
            sb.append(line).append("\n");
        }
        return sb.toString();
    }


}
