package pl.orange.isamConfiguration.connection;

import org.junit.Ignore;

public class IsamConnectionSSHIntegrationTest {

    @Ignore
    public void setConnection() throws Exception {
        IsamConnectionSSH ict = new IsamConnectionSSH("isadmin;ANS#150;10.0.0.100;ssh;");
        ict.setConnection();
        ict.disconnect();
    }

}