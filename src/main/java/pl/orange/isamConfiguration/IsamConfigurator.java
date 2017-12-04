package pl.orange.isamConfiguration;

import org.apache.log4j.Logger;
import pl.orange.config.ConfigurationManager;
import pl.orange.isamConfiguration.connection.IsamConnectable;
import pl.orange.isamConfiguration.connection.IsamConnectionFactory;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.IOException;

class IsamConfigurator {

    private static IsamConfigurator instance;
    private IsamConnectable isam;
    private static final Logger log = Logger.getLogger(IsamConfigurator.class);

    IsamConfigurator() throws HostListException {
        String isamConfiguration;
        try {
            isamConfiguration = getIsamConnectionParameters();
        } catch (IOException e) {
            throw new HostListException(ExceptionMessages.CONFIGURATION_READING_FAILURE,e.getMessage());
        }
        isam = IsamConnectionFactory.build(isamConfiguration);
    }

    private String getIsamConnectionParameters() throws IOException {
        return ConfigurationManager
                .getInstance()
                .getConfigurationForQueue("dslam")
                .getConfiguration();
    }

    void sendConfiguration(String commands) throws HostListException {
        try {
            log.info("Sending command:" + commands);
            isam.setConnection();
            String out = isam.sendCommand(commands);
            if(out.contains("invalid token")) {
                throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE,out);
            }
            log.info("Received output " +out);
            isam.disconnect();
        } catch (IOException   e) {
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE,e.getMessage());
        }
    }

}
