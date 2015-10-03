package pl.orange.queueComposite;

import org.junit.Test;
import pl.orange.util.HostListException;
import pl.orange.util.ExceptionMessages;

import static org.assertj.core.api.Assertions.assertThat;

public class WifiListComponentTest {

    @Test
    public void shouldNotAddSimpleHostList()  {
        WifiListComponent wlc = new WifiListComponent("Wifi",1);
        try {
            wlc.addItem(new SimpleHostsList("1", 10));
            wlc.addItem(new SimpleHostsList("2", 10));
        } catch (HostListException e) {
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.MULTIPLE_LIST_FULL);
        }
    }


    @Test
    public void shouldNotAddSimpleHostListBecauseOfNotProperName()  {
        WifiListComponent wlc = new WifiListComponent("Wifi",1);
        try {
            wlc.addItem(new SimpleHostsList("niepoprawnaNazwa1", 10));
        } catch (HostListException e) {
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.LOGIC_ERROR);
        }
    }


    @Test
    public void shouldAddOnePcToWifiListOnSpecyficChannel() throws Exception{
        //Given
        WifiListComponent wlc = new WifiListComponent("Wifi",3);
        wlc.addItem(new SimpleHostsList("1", 10));
        wlc.addItem(new SimpleHostsList("6", 10));
        wlc.addItem(new SimpleHostsList("11", 10));
        //When
        int channel = wlc.addItem(new Host("L1-01-02", "empty"));
        //Then
        assertThat(channel).isEqualTo(1);
    }


    @Test
    public void shouldthrowExcpetionBecauseOfHostPresentOnList() throws Exception {
        //Given
        WifiListComponent wlc = new WifiListComponent("Wifi",3);
        wlc.addItem(new SimpleHostsList("1", 10));
        wlc.addItem(new SimpleHostsList("6", 10));
        wlc.addItem(new SimpleHostsList("11", 10));
       //when
        wlc.addItem(new Host("L1-01-02", "empty"));
        try {
            wlc.addItem(new Host("L1-01-02", "empty"));
        } catch (HostListException e) {
            //Then
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.INPROGRESS);
        }
    }

    @Test
    public void shouldThrowExceptionBecauseAllInternalListsAreFull() throws Exception {
        //Given
        WifiListComponent wlc = new WifiListComponent("Wifi",3);
        wlc.addItem(new SimpleHostsList("1", 1));
        wlc.addItem(new SimpleHostsList("6", 1));
        wlc.addItem(new SimpleHostsList("11", 1));
        wlc.addItem(new Host("L1-01-02", "empty"));
        wlc.addItem(new Host("L1-01-03", "empty"));
        wlc.addItem(new Host("L1-01-04", "empty"));
        //When
        try {
            wlc.addItem(new Host("L1-01-05", "empty"));
        } catch (HostListException e) {
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.WAIT);
        }
   }

    @Test
    public void shouldRemoveItem() throws Exception {
        //Given
        WifiListComponent wlc = new WifiListComponent("Wifi",3);
        wlc.addItem(new SimpleHostsList("1", 2));
        wlc.addItem(new SimpleHostsList("6", 2));
        wlc.addItem(new SimpleHostsList("11", 2));
        wlc.addItem(new Host("L1-01-02", "empty"));
        //When
        wlc.removeItem(new Host("L1-01-02", "empty"));
        //Then
        assertThat(wlc.getSize()).isEqualTo(0);
    }


    @Test
    public void shouldRemoveAllItems() throws Exception {
        //Given
        WifiListComponent wlc = new WifiListComponent("Wifi",3);
        wlc.addItem(new SimpleHostsList("1", 2));
        wlc.addItem(new SimpleHostsList("6", 2));
        wlc.addItem(new SimpleHostsList("11", 2));
        wlc.addItem(new Host("L1-01-02", "empty"));
        //When
        wlc.removeAllItems();
        //Then
        assertThat(wlc.getSize()).isEqualTo(0);
    }

    @Test
    public void shouldThrowExceptionBecauseCurentItemWasRemovedEarlier() throws Exception {
        //Given
        WifiListComponent wlc = new WifiListComponent("Wifi",3);
        wlc.addItem(new SimpleHostsList("1", 2));
        wlc.addItem(new SimpleHostsList("6", 2));
        wlc.addItem(new SimpleHostsList("11", 2));
        wlc.addItem(new Host("L1-01-02", "empty"));
        //When
        wlc.removeItem(new Host("L1-01-02", "empty"));
        try {
            wlc.removeItem(new Host("L1-01-02", "empty"));
        } catch (HostListException e) {
            //Then
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.NOT_PRESENT);
        }
    }


    @Test
    public void shouldNotAllowToAddWifiListToWifiList() throws Exception{
        WifiListComponent wlc = new WifiListComponent("Wifi",3);
        WifiListComponent wlc2 = new WifiListComponent("Wifi",3);
        try {
            wlc.addItem(wlc2);
        } catch (HostListException e) {
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.LOGIC_ERROR);
        }


    }

}