package pl.orange.isamConfiguration.connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class IsamTelnetTestIntegration {

    private IsamConnectionTelnet ict;




    @Test
    public void checkAuthorization() throws Exception {
        assertThat(ict.authorize()).isEqualTo(true);
    }

    @Test
    public void checkCommandOutput() throws Exception {
        assertThat(ict.sendCommand("info configure equipment ont interface")).contains("admin-state");
    }

    @Before
    public void before() {
        ict = new IsamConnectionTelnet("isadmin;ANS#150;10.0.0.100;telnet;");
        try {
            ict.setConnection();
        } catch (IOException  e) {
            fail(e.getMessage());
        }
    }


    @After
    public void after() {
        try {
            ict.disconnect();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}