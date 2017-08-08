package pl.orange.isamConfiguration.connection;

import org.apache.commons.net.telnet.TelnetClient;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.IOException;

public class IsamConnectionTelnet extends IsamConnectionAbstract {


    public IsamConnectionTelnet(String connectionParameters) {
        super(connectionParameters);
    }

    TelnetClient telnet;




    @Override
    public void setConnection() throws IOException, HostListException {
        telnet = new TelnetClient();
        try
        {
            telnet.connect("10.0.0.1", 23);

            telnet.disconnect();
        }
        catch (IOException e)
        {
           throw new HostListException(ExceptionMessages.TELNET_FAILURE, "connection to dslam  ont telnet failure");
        }
    }

    @Override
    public int sendCommand(String command) {
        return 0;
    }

    @Override
    public String getResponse() {
        return null;
    }




}
