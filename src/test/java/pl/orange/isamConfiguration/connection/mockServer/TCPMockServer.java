package pl.orange.isamConfiguration.connection.mockServer;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPMockServer {

    private static final Logger log = Logger.getLogger(TCPMockServer.class);

    public TCPMockServer(int serverPort) {
        try{
            final ServerSocket listenSocket = new ServerSocket(serverPort);
           log.info("TCPMockServer start listening... on port " + serverPort);
            new Thread() {
                public void run() {
                    while(true) {
                        Socket clientSocket;
                        try {
                            clientSocket = listenSocket.accept();
                            new DdslamTelnetEmulatorThread(clientSocket);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }.start();
        }
        catch(IOException e) {
            log.error("Listen :"+e.getMessage());}
    }
}
