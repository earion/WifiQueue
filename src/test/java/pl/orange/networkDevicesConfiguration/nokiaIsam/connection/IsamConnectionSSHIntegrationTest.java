package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import org.junit.Ignore;

public class IsamConnectionSSHIntegrationTest {

    @Ignore
    public void setConnection() throws Exception {
        NetworkDeviceConnectionSSH ict = new NetworkDeviceConnectionSSH("isadmin;ANS#150;10.0.0.100;ssh;");
        ict.setConnection();
        ict.disconnect();
    }

}