package pl.orange.isamConfiguration.connection.mockServer;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

class DdslamTelnetEmulatorThread extends Thread {

    private static final Logger log = Logger.getLogger(DdslamTelnetEmulatorThread.class);

    private BufferedReader input;
    private PrintWriter output;
    private Socket clientSocket;

    DdslamTelnetEmulatorThread(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            input = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
            output =new PrintWriter( clientSocket.getOutputStream(),true);
            this.start();
        }
        catch(IOException e) {
            log.error("mockServer:"+e.getMessage());
        }
    }

    public void run() {
        try {
            log.info("Created new connection...");
            output.println("login:");
            while(true) {
                String whatUserWrite = input.readLine();
                if (whatUserWrite ==null) {
                    input.close();
                    output.close();
                    clientSocket.close();
                    return;
                }
                handleMockResponse(whatUserWrite);
            }

        }
        catch(EOFException e) {
            log.error("EOF:"+e.getMessage()); }
        catch(IOException e) {
            log.error("IO:"+e.getMessage());}
        finally {
            try {
                input.close();
                output.close();
                clientSocket.close();
            }
            catch (IOException e){/*close failed*/}
        }
    }

    private void handleMockResponse(String whatUserWrite) {
        switch( whatUserWrite) {
            case "isadmin" : {
                output.println("password:");
                break;
            }
            case "ANS#150" : {
                output.println("Today is 1960-01-01");
                output.println("");
                output.println("Welcome to test environment");
                output.println("typ:isadmin># ");
                sendLoginFooter();
                break;
            }
            case "info configure equipment ont interface": {
                output.println("admin-state");
                sendLoginFooter();
                break;
            }
            default: {
                output.println("error ");
                sendLoginFooter();
                break;
            }
        }
    }

    private void sendLoginFooter() {
        output.flush();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        output.println("typ:isadmin># ");
        output.flush();
    }
}


