package pl.orange.isamConfiguration.connection;

import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

public class IsamConnectionFactory{


    public static IsamConnectable  build(String connectionParameters) throws HostListException {
            String tmp[] = connectionParameters.split(";");
            String connectionType = tmp[3];
            switch (connectionType) {
                case "ssh" : return new IsamConnectionSSH(connectionParameters);
                case "serial" : return new IsamConnectionSerial(connectionParameters);
                case "telnet" : return new IsamConnectionTelnet(connectionParameters);
                default: throw new HostListException(ExceptionMessages.CONFIGURATION_ERROR, "Bad configuration value " + connectionParameters);
            }
    }
}
