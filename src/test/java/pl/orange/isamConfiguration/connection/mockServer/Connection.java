package pl.orange.isamConfiguration.connection.Connection;

import java.io.*;
import java.net.Socket;

/**
 * Created by mateusz on 20.08.17.
 */
public class Connection extends Thread {
    BufferedReader input;
    PrintWriter output;
    Socket clientSocket;

    public Connection (Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            input = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
            output =new PrintWriter( clientSocket.getOutputStream(),true);
            this.start();
        }
        catch(IOException e) {
            System.out.println("Connection:"+e.getMessage());
        }
    }

    public void run() {
        try { // an echo server
            System.out.println("Created new connection...");
            output.println("login:");
            while(true) {
                String whatUserWrite = input.readLine();
                if (whatUserWrite ==null) {
                    return;
                }
                switch( whatUserWrite) {
                    case "isadmin" : {
                        output.println("password:");
                    }
                    case "ANS#150" : {
                        output.println("DATA");
                        output.println("");
                        output.println("Welcome to test environment");
                    }
                    default: {
                        output.println("typ:isadmin># ");
                    }
                }
            }
        }
        catch(EOFException e) {
            System.out.println("EOF:"+e.getMessage()); }
        catch(IOException e) {
            System.out.println("IO:"+e.getMessage());}
        finally {
            try {
                clientSocket.close();
            }
            catch (IOException e){/*close failed*/}
        }
    }
}


