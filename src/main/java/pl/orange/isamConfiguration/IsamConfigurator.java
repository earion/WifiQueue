package pl.orange.isamConfiguration;

import pl.orange.config.ConfigurationManager;
import pl.orange.isamConfiguration.connection.IsamConnectable;
import pl.orange.isamConfiguration.connection.IsamConnectionFactory;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.*;

public class IsamConfigurator {


private static IsamConfigurator instance;
private IsamConnectable isam;



    public static IsamConfigurator getInstance() throws HostListException {
        if(instance==null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new IsamConfigurator();
                }
            }
        }
        return instance;
    }


    private IsamConfigurator() throws HostListException {
        String isamConfiguration = null;
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



    public void sendConfiguration(String commands) throws HostListException {
        try {
            isam.setConnection();
            String out = isam.sendCommand(commands);
            if(out.contains("invalid token")) {
                throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE,out);
            }
            System.out.println(out);
            isam.disconnect();
        } catch (IOException   e) {
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE,e.getMessage());
        }
    }

}
