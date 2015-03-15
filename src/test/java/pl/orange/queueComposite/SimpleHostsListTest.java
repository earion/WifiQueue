package pl.orange.queueComposite;

import org.junit.Test;
import pl.orange.util.HostListException;
import pl.orange.util.ListMessages;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleHostsListTest {

    @Test
    public void shouldHaveTwoElements() throws Exception {
        SimpleHostsList shl = new SimpleHostsList("CAT-IQ",10);
        shl.addItem(new Host("L1-01-01", "empty"));
        shl.addItem(new Host("L1-0-02", "empty"));
        assertThat(shl.getSize()).isEqualTo(2);
    }

    @Test
    public void shouldThrowExceptionBecauseOfListSizeExceeded() throws Exception {
        SimpleHostsList shl = new SimpleHostsList("CAT-IQ",2);
        String errorHost = "L2-01-03";
        try {
            shl.addItem(new Host("L1-01-01", "empty"));
            shl.addItem(new Host("L1-01-02", "empty"));
            shl.addItem(new Host(errorHost, "empty"));
        } catch (HostListException e) {
            assertThat(e.getMessage()).contains(errorHost);
            assertThat(e.getStatusMessage()).isEqualTo(ListMessages.WAIT);
        }
    }

    @Test
    public void shouldNotAddTheSameElementToTheList() throws Exception {
        SimpleHostsList shl = new SimpleHostsList("CAT-IQ",2);
        String errorHost = "L2-01-03";
        shl.addItem(new Host(errorHost, "empty"));
        try {
            shl.addItem(new Host(errorHost, "empty"));
        } catch (HostListException e) {
            assertThat(e.getStatusMessage()).isEqualTo(ListMessages.INPROGRESS);
        }
   }


    @Test
    public void shouldRemoveHost() throws Exception {
        SimpleHostsList shl = new SimpleHostsList("CAT-IQ", 2);
        String addedHost = "L2-01-03";
        shl.addItem(new Host(addedHost, "empty"));
        shl.removeItem(new Host(addedHost, "empty"));
        assertThat(shl.getSize()).isEqualTo(0);
    }

    @Test
    public void shouldNotRemoveHostBecauseHostNotPresent() {
        // Given
        SimpleHostsList shl = new SimpleHostsList("CAT-IQ", 2);
        String addedHost = "L2-01-03";
        // When
        try {
            shl.removeItem(new Host(addedHost, "empty"));
        } catch (HostListException e) {
            // Then
            assertThat(e.getStatusMessage()).isEqualTo(ListMessages.NOT_PRESENT);
        }
    }



}