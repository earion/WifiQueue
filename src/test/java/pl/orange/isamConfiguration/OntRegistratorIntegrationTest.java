package pl.orange.isamConfiguration;

import org.junit.Ignore;
import org.junit.Test;
import pl.orange.util.HostListException;

import static org.assertj.core.api.Assertions.assertThat;

public class OntRegistratorIntegrationTest {


    @Ignore
    public static void beforeClass() throws Exception {
        OntRegistrator ontReg = new OntRegistrator(1,1);
        ontReg.unregisterONT();
        ontReg = new OntRegistrator(1,2);
        ontReg.unregisterONT();
        assertThat(true);
    }


    @Test
    public void ontRegistrationIntegrationTest() throws Exception {
         OntRegistrator ontReg = new OntRegistrator(1,1);
        ontReg.registerONT("SMBS12345678");
        ontReg.unregisterONT();
        assertThat(true);
    }


    @Test
    public void ontRegistrationUnknowSlot() throws Exception {
        OntRegistrator ontReg = new OntRegistrator(1,11);
        ontReg.registerONT("SMBS12345009");
        ontReg.unregisterONT();
        assertThat(true);
    }



    @Test
    public void ontFailureDueToSerialNumberPresentOnDSLAM() throws Exception {
        OntRegistrator ontReg = new OntRegistrator(1,1);
        ontReg.unregisterONT();
        ontReg.registerONT("SMBS12345678");
        try {
            ontReg = new OntRegistrator(1, 2);
            ontReg.unregisterONT();
            ontReg.registerONT("SMBS12345678");
        } catch (HostListException e ){
            ontReg = new OntRegistrator(1,1);
            ontReg.unregisterONT();
            assertThat(e.getMessage()).contains("Inconsistent ONT parameter configuration : Serial number already exists");
        }
    }



}