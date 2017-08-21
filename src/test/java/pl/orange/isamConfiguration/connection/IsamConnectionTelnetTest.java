package pl.orange.isamConfiguration.connection;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.orange.isamConfiguration.connection.mockServer.TCPMockServer;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class IsamConnectionTelnetTest {

    private IsamConnectionTelnet ict;




    @Test
    public void checkAuthorization() throws Exception {
        assertThat(ict.authorize()).isEqualTo(true);
    }

    @Test
    public void checkCommandOutput() throws Exception {
        assertThat(ict.sendCommand("info configure equipment ont interface")).contains("admin-state");
    }


    @BeforeClass
    public static void initialization() {
        TCPMockServer telnetServer = new TCPMockServer(2000);
    }


    @Before
    public void before() {
        ict = new IsamConnectionTelnet("isadmin;ANS#150;127.0.0.1;telnet;",2000);
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