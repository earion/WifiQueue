package pl.orange.networkDevicesConfiguration.ciscoSwitch2960;

import org.junit.Before;
import org.junit.Test;
import pl.orange.util.HostListException;

import static org.assertj.core.api.Assertions.assertThat;

public class CiscoSwitchIntegrationTest {


    private CiscoSwitch ciscoSwitch;

    @Before
    public  void before() {
        ciscoSwitch = new CiscoSwitch();
    }

    @Test
    public void shouldCheckPortStatus() throws HostListException {
        String out = ciscoSwitch.showPortStatus("20", null);
        System.out.println(out);
    }

    @Test
    public void shouldShutdownPort() throws HostListException {
        ciscoSwitch.changePortState("20",false, null);
        assertThat(ciscoSwitch.showPortStatus("20", null)).contains("shutdown");
        ciscoSwitch.changePortState("20",true, null);
    }

    @Test
    public void shouldSetAccessMode() throws HostListException {
        ciscoSwitch.changeVlanMode("20","access","106", null);
        assertThat(ciscoSwitch.showPortStatus("20", null).contains("acess vlan 106"));
        assertThat(ciscoSwitch.showPortStatus("20", null).contains("mode access"));
    }

    @Test
    public void shoulSetTrunkModeOnPort() throws HostListException {
        ciscoSwitch.changeVlanMode("20","trunk","102,106", null);
        assertThat(ciscoSwitch.showPortStatus("20", null).contains("switchport trunk alloved vlan 102,106"));
        assertThat(ciscoSwitch.showPortStatus("20", null).contains("mode trunk"));
    }


}