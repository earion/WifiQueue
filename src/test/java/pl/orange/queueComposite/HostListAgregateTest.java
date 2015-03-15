package pl.orange.queueComposite;

import org.junit.Test;
import pl.orange.util.HostListException;
import pl.orange.util.ListMessages;
import static org.assertj.core.api.Assertions.assertThat;


public class HostListAgregateTest {
    private HostListAgregate hla;

    @Test
    public void shouldNotRemoveHostIfNotAdded() {
       //Given
        hla = new HostListAgregate();
        //When
        try {
            hla.removeItem(new Host("L2-01-01", "Wifi"));
        } catch (HostListException e) {
            //Then
            assertThat(e.getStatusMessage()).isEqualTo(ListMessages.NOT_PRESENT);
        }
    }

    @Test
    public void shouldAddItemToSimpleListInsideAgregate() throws HostListException {
        // Given
        hla = new HostListAgregate();
        hla.addItem(new SimpleHostsList("CAT_IQ",10));
        // When
        hla.addItem(new Host("L2-01-01","CAT_IQ"));
        // Then
        assertThat(hla.getSizeofInternalList("CAT_IQ")).isEqualTo(1);
    }


    @Test
    public void shouldNotAddtoNotSetUpSimpleList() throws Exception {
        // Given
        hla = new HostListAgregate();
        // When
        try {
            hla.addItem(new Host("L2-01-01", "CAT_IQ"));
        } catch (HostListException e) {
            // Then
            assertThat(e.getStatusMessage()).isEqualTo(ListMessages.NOT_PRESENT);
        }
    }


    @Test
    public void shouldNotGetSizeOfNotInitializedSimpleList() throws Exception {
        // Given
        hla = new HostListAgregate();
        // When
        try {
            hla.getSizeofInternalList("CAT_IQ");
        } catch (HostListException e) {
            // Then
            assertThat(e.getStatusMessage()).isEqualTo(ListMessages.NOT_PRESENT);
        }
    }

    @Test
    public void shouldRemoveItemFromSimpleHistsList() throws Exception {
        // Given
        hla = new HostListAgregate();
        hla.addItem(new SimpleHostsList("CAT_IQ",10));
        hla.addItem(new Host("L2-01-01","CAT_IQ"));
        assertThat(hla.getSizeofInternalList("CAT_IQ")).isEqualTo(1);
        //When
        hla.removeItem(new Host("L2-01-01","CAT_IQ"));
        //Then
        assertThat(hla.getSizeofInternalList("CAT_IQ")).isEqualTo(0);
    }

    @Test
    public void shouldAddItemToWifiListComponent() throws Exception {
        // Given
        hla = new HostListAgregate();
        WifiListComponent wlc =  new WifiListComponent("Wifi",3);
        wlc.addItem(new SimpleHostsList("1",10));
        wlc.addItem(new SimpleHostsList("6",10));
        wlc.addItem(new SimpleHostsList("11",10));
        hla.addItem(wlc);
        // When
        hla.addItem(new Host("L2-01-01","Wifi"));
        // Then
        assertThat(hla.getSizeofInternalList("Wifi")).isEqualTo(1);
    }

    @Test
    public void checkIfRemovingSimpleHostListIsImposible() throws Exception {
        // Given
        hla = new HostListAgregate();
        hla.addItem(new SimpleHostsList("CAT_IQ",10));
        try {
            hla.removeItem(new SimpleHostsList("CAT_IQ", 10));
        } catch (HostListException e) {
            assertThat(e.getStatusMessage()).isEqualTo(ListMessages.LOGIC_ERROR);
        }
    }



}