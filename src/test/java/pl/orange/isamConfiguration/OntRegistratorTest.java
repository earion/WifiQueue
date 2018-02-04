package pl.orange.isamConfiguration;

import org.junit.Test;
import pl.orange.util.HostListException;

import static org.assertj.core.api.Assertions.assertThat;

public class OntRegistratorTest {

    @Test(expected = HostListException.class)
    public void setNosPossibleSn() throws Exception {
        OntRegistrator ontReg = new OntRegistrator(1,1);
        ontReg.prepareRegisterCommands("niepoprawna nazwa");
    }

    @Test
    public void setPossibleSn() throws Exception {
        OntRegistrator ontReg = new OntRegistrator(1,1);
        String out = ontReg.prepareRegisterCommands("SMBS02005F6C");
        assertThat(out).contains("SMBS:02005F6C");
    }

    @Test
    public void registeWhatIsExpected() throws Exception {
        int slot = 1;
        int ont = 1;
        OntRegistrator ontReg = new OntRegistrator(ont,slot);
        String output = ontReg.prepareRegisterCommands("SMBS12345678");
        String expectedOutput = "configure equipment ont interface 1/1/8/1/1 admin-state down\n" +
        "configure equipment ont interface 1/1/8/1/1 sernum SMBS:12345678 sw-ver-pland disabled fec-up enable enable-aes enable\n" +
        "configure equipment ont interface 1/1/8/1/1 admin-state up\n";
        assertThat(output).isEqualTo(expectedOutput);
    }

    @Test
    public void unregisterTest() throws Exception {
        OntRegistrator ontReg = new OntRegistrator(1,1);
        String output = ontReg.preperareUnregisterCommands();
        assertThat(output).isEqualTo("configure equipment ont interface 1/1/8/1/1 admin-state down\n" +
                "configure equipment ont interface 1/1/8/1/1 no sernum \n");
    }

}