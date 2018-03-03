package pl.orange.networkDevicesConfiguration;

import pl.orange.config.ConfigurationManager;
import pl.orange.networkDevicesConfiguration.nokiaIsam.connection.NetworkDeviceConnectable;
import pl.orange.networkDevicesConfiguration.nokiaIsam.connection.NetworkDeviceConnectionFactory;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.IOException;

public abstract class NetworkDeviceConfigurator {
    protected NetworkDeviceConnectable networkDevice;

    protected  NetworkDeviceConfigurator(String name) throws HostListException {
        String deviceConfiguration;
        try {
            deviceConfiguration = getIsamConnectionParameters(name);
        } catch (IOException e) {
            throw new HostListException(ExceptionMessages.CONFIGURATION_READING_FAILURE,e.getMessage());
        }
        networkDevice = NetworkDeviceConnectionFactory.build(deviceConfiguration);
    }

    protected String getIsamConnectionParameters(String name) throws IOException {
        return ConfigurationManager
                .getInstance()
                .getConfigurationForQueue(name)
                .getConfiguration();
    }

    protected abstract String sendConfiguration(String commands) throws HostListException;




    protected void disconnect() throws IOException, HostListException {

    }

}
