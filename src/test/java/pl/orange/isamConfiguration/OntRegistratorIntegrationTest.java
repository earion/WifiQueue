package pl.orange.isamConfiguration;

import org.junit.Test;

import static org.assertj.core.api.Assertions.fail;

public class OntRegistratorIntegrationTest {

    @Test
    public void ontRegistrationIntegrationTest() throws Exception {
        OntRegistrator ontReg = new OntRegistrator(1,1);
        ontReg.registerONT("SMBS12345678");
    }

    @Test
    public void ontUnregisterIntegrationTest() throws Exception {
        OntRegistrator ontReg = new OntRegistrator(1,1);
        ontReg.unregisterONT();
    }

    @Test
    public void TestReperatibilityOntRegistration() throws Exception {
        try {
            OntRegistrator ontReg = new OntRegistrator(1, 1);
            ontReg.registerONT("SMBS12345679");
            ontReg.unregisterONT();
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail("NULL");
        }
    }
}