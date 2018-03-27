package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import org.junit.Ignore;

public class IsamConnectionSSHIntegrationTest {

    @Ignore
    public void setConnection() throws Exception {
        NetworkDeviceConnectionSsh ict = new NetworkDeviceConnectionSsh("isadmin;ANS#150;10.0.0.100;ssh;dslam;");
        ict.setConnection();
        ict.disconnect();
    }

}