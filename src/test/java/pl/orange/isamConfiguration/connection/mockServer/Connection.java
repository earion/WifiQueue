package pl.orange.isamConfiguration.connection.mockServer;

import java.io.*;
import java.net.Socket;

/**
 * Created by mateusz on 20.08.17.
 */
public class Connection extends Thread {
    private BufferedReader input;
    private PrintWriter output;
    private Socket clientSocket;

    Connection (Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            input = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
            output =new PrintWriter( clientSocket.getOutputStream(),true);
            this.start();
        }
        catch(IOException e) {
            System.out.println("mockServer:"+e.getMessage());
        }
    }

    public void run() {
        try { // an echo server
            System.out.println("Created new connection...");
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
            System.out.println("EOF:"+e.getMessage()); }
        catch(IOException e) {
            System.out.println("IO:"+e.getMessage());}
        finally {
            try {
                input.close();
                output.close();
                clientSocket.close();
                return;
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
                output.println("DATA");
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


