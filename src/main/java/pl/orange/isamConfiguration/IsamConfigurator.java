package pl.orange.isamConfiguration;

import pl.orange.config.ConfigurationManager;
import pl.orange.isamConfiguration.connection.IsamConnectable;
import pl.orange.isamConfiguration.connection.IsamConnectionFactory;
import pl.orange.util.HostListException;

import java.io.*;

public class IsamConfigurator {


private static IsamConfigurator instance;
private IsamConnectable isam;
private InputStream isamReader;
private OutputStream isamWriter;


    public static IsamConfigurator getInstance() throws IOException, HostListException {
        if(instance==null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new IsamConfigurator();
                }
            }
        }
        return instance;
    }


    private IsamConfigurator() throws IOException, HostListException {
       String isamConfiguration = getIsamConnectionParameters();
        isam = IsamConnectionFactory.build(isamConfiguration);
    }


    private String getIsamConnectionParameters() throws IOException {
        return ConfigurationManager
                .getInstance()
                .getConfigurationForQueue("dslam")
                .getConfiguration();
    }



    public void sendConfiguration(String commands) throws IOException {
        try {
            isam.setConnection();
            String out = isam.sendCommand(commands);
            System.out.println(out);
            isam.disconnect();
        } catch (IOException| InterruptedException| HostListException   e) {
            e.printStackTrace();
        }
    }


    private void logout() {
        try {
            isam.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
