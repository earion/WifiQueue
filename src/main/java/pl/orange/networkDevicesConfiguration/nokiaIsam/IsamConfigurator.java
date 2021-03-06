package pl.orange.networkDevicesConfiguration.nokiaIsam;

import org.apache.log4j.Logger;
import pl.orange.networkDevicesConfiguration.NetworkDeviceConfigurator;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.IOException;
import java.net.SocketException;

class IsamConfigurator extends NetworkDeviceConfigurator {

    private static final Logger log = Logger.getLogger(IsamConfigurator.class);

    IsamConfigurator(String name) throws HostListException {
        super(name, null);
    }

    protected String sendConfiguration(String commands) throws HostListException {
        String errorMessage = "";
        String response = "";
        try {
            log.info("Sending command:" + commands);
            networkDevice.setConnection();
            for (int i = 1; i < 4; i++) {
                try {
                    response = sendCommand(commands);
                    errorMessage = "";
                    break;
                } catch (NullPointerException | SocketException | IllegalStateException e) {
                    try {
                        networkDevice.disconnect();
                    } catch (NullPointerException ignored) { }
                    log.error("Try send: " + commands);
                    log.error("Caught Exception: " + e.getCause());
                    log.info("Try to re-execute previous command " + i + " try");
                    try {
                        Thread.sleep(60 * 1000);
                        networkDevice.setConnection();
                        errorMessage = e.getMessage();
                    } catch (NullPointerException | InterruptedException ignored) { }
                }
            }
            if (!errorMessage.isEmpty()) {
                throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE, errorMessage);
            }
        } catch (IOException e) {
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE, e.getMessage());
        }
        return response;
    }

    private String sendCommand(String commands) throws HostListException, IOException {
        return sendCommand(commands, true);
    }

    String sendCommand(String commands, boolean outputToLog) throws HostListException, IOException {
        networkDevice.stopKeepingSession();
        String out = networkDevice.sendCommand(commands, outputToLog);
        if (out.contains("invalid token")) {
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE, out);
        }
        if(outputToLog) {
            if (!commands.equalsIgnoreCase("info configure equipment ont interface")) {
                log.info("Received output " + out);
            } else {
                log.info("Confirm received output. Response is too long.");
            }
        }
        networkDevice.startKeepingSession();
        return out;
    }

}
