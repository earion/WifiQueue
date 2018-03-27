package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

public class NetworkDeviceConnectionFactory {


    public static NetworkDeviceConnectable build(String connectionParameters) throws HostListException {
        String tmp[] = connectionParameters.split(";");
        String connectionType = tmp[3];
        String device = tmp[4];
        switch (connectionType) {
            case "ssh": {
                if (device.equalsIgnoreCase("cisco")) {
                    return new NetworkDeviceConnectionSshCisco(connectionParameters);
                } else {
                    return new NetworkDeviceConnectionSsh(connectionParameters);
                }
            }
            case "serial":
                return new NetworkDeviceConnectionSerial(connectionParameters);
            case "telnet":
                return new NetworkDeviceConnectionTelnet(connectionParameters);
            default:
                throw new HostListException(ExceptionMessages.CONFIGURATION_ERROR, "Bad configuration value " + connectionParameters);
        }
    }
}
