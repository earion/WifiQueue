package pl.orange.networkDevicesConfiguration.ciscoSwitch2960;

import org.junit.Before;
import org.junit.Test;
import pl.orange.util.HostListException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CiscoSwitchIntegrationTest {


    private CiscoSwitch ciscoSwitch;

    @Before
    public  void before() {
        ciscoSwitch = new CiscoSwitch();
    }

    @Test
    public void shouldCheckPortStatus() throws HostListException, IOException {
        String out = ciscoSwitch.showPortStatus("20");
        System.out.println(out);
    }

    @Test
    public void shouldShutodnwPort() throws HostListException, IOException {
        ciscoSwitch.changePortState("20",false);
        assertThat(ciscoSwitch.showPortStatus("20")).contains("shutdown");
    }

    @Test
    public void shouldSetAccessMode() throws HostListException, IOException {
        ciscoSwitch.changeVlanMode("20",VlanMode.ACCESS,"106");
        assertThat(ciscoSwitch.showPortStatus("20").contains("acess vlan 106"));
        assertThat(ciscoSwitch.showPortStatus("20").contains("mode access"));
    }

    @Test
    public void shoulSetTrunkModeOnPort() throws HostListException, IOException {
        ciscoSwitch.changeVlanMode("20",VlanMode.TRUNK,"102","106");
        assertThat(ciscoSwitch.showPortStatus("20").contains("switchport trunk alloved vlan 102,106"));
        assertThat(ciscoSwitch.showPortStatus("20").contains("mode trunk"));
    }


}