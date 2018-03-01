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
        super("dslam");
    }


    protected String sendConfiguration(String commands) throws HostListException {
        String errorMessage = "";
        try {
            log.info("Sending command:" + commands);
            networkDevice.setConnection();
            for(int i=1;i<4;i++) {
                try {
                    sendCommand(commands);
                    errorMessage = "";
                    break;
                } catch (SocketException | IllegalStateException e) {
                    log.error("Caught Exception" + e.getCause().getMessage());
                    log.info("Try to re-execute previous command " + i + " try") ;
                    networkDevice.setConnection();
                    errorMessage = e.getMessage();
                }
            }
            networkDevice.disconnect();
            if(!errorMessage.isEmpty()) {
                throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE,errorMessage);
            }
        } catch (IOException   e) {
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE,e.getMessage());
        }
        return "";
    }

    private void sendCommand(String commands) throws HostListException, IOException {
        String out = networkDevice.sendCommand(commands);
        if(out.contains("invalid token")) {
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE, out);
        }
        log.info("Received output " +out);
    }

}
