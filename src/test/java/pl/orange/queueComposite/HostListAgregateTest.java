package pl.orange.queueComposite;

import org.junit.Test;
import pl.orange.util.HostListException;
import pl.orange.util.ExceptionMessages;
import static org.assertj.core.api.Assertions.assertThat;


public class HostListAgregateTest {
    private HostListAgregate hla;

    @Test
    public void shouldNotRemoveHostIfNotAdded() {
       //Given
        hla =  HostListAgregate.getInstanceForTestPurpose();
        //When
        try {
            hla.removeItem(new Host("L2-01-01", "Wifi"));
        } catch (HostListException e) {
            //Then
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.NOT_PRESENT);
        }
    }

    @Test
    public void shouldAddItemToSimpleListInsideAgregate() throws HostListException {
        // Given
        hla = HostListAgregate.getInstanceForTestPurpose();
        hla.addItem(new SimpleHostsList("CAT_IQ",10));
        // When
        hla.addItem(new Host("L2-01-01","CAT_IQ"));
        // Then
        assertThat(hla.getSizeOfInternalList("CAT_IQ")).isEqualTo(1);
    }


    @Test
    public void shouldNotAddtoNotSetUpSimpleList() throws Exception {
        // Given
        hla = HostListAgregate.getInstanceForTestPurpose();
        // When
        try {
            hla.addItem(new Host("L2-01-01", "CAT_IQ"));
        } catch (HostListException e) {
            // Then
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.NOT_PRESENT);
        }
    }


    @Test
    public void shouldNotGetSizeOfNotInitializedSimpleList() throws Exception {
        // Given
        hla = HostListAgregate.getInstanceForTestPurpose();
        // When
        try {
            hla.getSizeOfInternalList("CAT_IQ");
        } catch (HostListException e) {
            // Then
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.NOT_PRESENT);
        }
    }

    @Test
    public void shouldRemoveItemFromSimpleHistsList() throws Exception {
        // Given
        hla = HostListAgregate.getInstanceForTestPurpose();
        hla.addItem(new SimpleHostsList("CAT_IQ",10));
        hla.addItem(new Host("L2-01-01","CAT_IQ"));
        assertThat(hla.getSizeOfInternalList("CAT_IQ")).isEqualTo(1);
        //When
        hla.removeItem(new Host("L2-01-01","CAT_IQ"));
        //Then
        assertThat(hla.getSizeOfInternalList("CAT_IQ")).isEqualTo(0);
    }


    @Test
    public void shouldRemoveAllLists() throws Exception {
        // Given
        hla = HostListAgregate.getInstanceForTestPurpose();
        hla.addItem(new SimpleHostsList("CAT_IQ",10));
        hla.addItem(new Host("L2-01-01","CAT_IQ"));
        assertThat(hla.getSizeOfInternalList("CAT_IQ")).isEqualTo(1);
        //When
        hla.removeAllItems();
        //Then
        try {
            hla.getSizeOfInternalList("CAT_IQ");
        } catch (HostListException e) {
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.NOT_PRESENT);
        }
        assertThat(hla.getListsNumber()).isEqualTo(0);
    }

    @Test
    public void shouldAddItemToWifiListComponent() throws Exception {
        // Given
        hla = HostListAgregate.getInstanceForTestPurpose();
        WifiListComponent wlc =  new WifiListComponent("Wifi",3);
        wlc.addItem(new SimpleHostsList("1",10));
        wlc.addItem(new SimpleHostsList("6",10));
        wlc.addItem(new SimpleHostsList("11",10));
        hla.addItem(wlc);
        // When
        hla.addItem(new Host("L2-01-01","Wifi"));
        // Then
        assertThat(hla.getSizeOfInternalList("Wifi")).isEqualTo(1);
    }

    @Test
    public void checkIfRemovingSimpleHostListIsImposible() throws Exception {
        // Given
        hla = HostListAgregate.getInstanceForTestPurpose();
        hla.addItem(new SimpleHostsList("CAT_IQ",10));
        try {
            hla.removeItem(new SimpleHostsList("CAT_IQ", 10));
        } catch (HostListException e) {
            assertThat(e.getStatusMessage()).isEqualTo(ExceptionMessages.LOGIC_ERROR);
        }
    }



}