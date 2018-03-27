package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class NetworkDeviceConnectionTelnet extends NetworkDeviceConnectionAbstract implements NetworkDeviceConnectable {

    private static final Logger log = Logger.getLogger(NetworkDeviceConnectable.class);
    private static Socket telnetSocket = null;
    private static PrintWriter out = null;
    private static BufferedReader in = null;
    private static boolean isLoggedIn = false;
    private static Thread keepingSession = null;
    private int port;

    NetworkDeviceConnectionTelnet(String connectionParameters) {
        super(connectionParameters);
        port = 23;
    }

    NetworkDeviceConnectionTelnet(String connectionParameters, int port) {
        super(connectionParameters);
        this.port = port;
    }

    @Override
    public void setConnection() throws IOException {
        if (!isLoggedIn) {
            telnetSocket = new Socket(this.getConnectionDestination(), port);
            out = new PrintWriter(telnetSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(telnetSocket.getInputStream()));
        }
    }

    @Override
    public String sendCommand(String command) throws IOException {
        if (authorize()) {
            sendTelnetCommand(command);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return readAnswer();
    }

    @Override
    public void stopKeepingSession() {
        try {
            if (keepingSession != null) {
                keepingSession.interrupt();
                keepingSession = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startKeepingSession() {
        if (keepingSession == null) {
            keepingSession = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(20 * 60 * 1000);
                        log.info("Keeping session, send command = [info environment]");
                        sendTelnetCommand("info environment");
                    } catch (IOException | InterruptedException ignored) {
                    }
                }
            });
            keepingSession.start();
        }
    }

    boolean authorize() throws IOException {
        for (int i = 0; i < 2; i++) {
            if (tryAuthorize()) {
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
        if (isLoggedIn) {
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
                isLoggedIn = true;
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
        if (isLoggedIn) {
            out.println("logout");
            isLoggedIn = false;
        }

        in.close();
        out.close();
        telnetSocket.close();
    }

    private String readOutputWithIterator() {
        StringBuilder sb = new StringBuilder();
        final LineIterator it = new LineIterator(in);
        while (it.hasNext()) {
            String line = it.nextLine();
            if (line.contains("alarm")) {
                continue;
            }
            if (line.isEmpty() || line.equals("typ:isadmin># ")) break;
            sb.append(line).append("\n");
        }
        log.info("Welcome to ISAM");
        return sb.toString();
    }


    private String readAnswer() {
        StringBuilder sb = new StringBuilder();
        final LineIterator it = new LineIterator(in);
        while (it.hasNext()) {
            String line = it.nextLine();
            if (line.contains("Error") || line.contains("invalid token")) {
                log.info(line);
                return line;
            }
            if (line.contains("alarm")) {
                continue;
            }
            if (line.endsWith("# ")) break;
            sb.append(line).append("\n");
        }
        return sb.toString();
    }


}
