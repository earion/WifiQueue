package pl.orange.isamConfiguration.connection.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mateusz on 20.08.17.
 */
public class TCPMockServer {

    public TCPMockServer(int serverPort) {
        try{

            final ServerSocket listenSocket = new ServerSocket(serverPort);
            System.out.println("server start listening... ... ...");

            (new Thread() {
                public void run() {
                    while(true) {
                        Socket clientSocket = null;
                        try {
                            clientSocket = listenSocket.accept();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Connection c = new Connection(clientSocket);
                    }
                }
            }).start();



        }
        catch(IOException e) {
            System.out.println("Listen :"+e.getMessage());}
    }
}
