package pl.orange.queueComposite;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class OntListComponentTest {

    private  OntListComponent olc;


    @Before
    public void beforeEachTest() {
        olc = new OntListComponent("LO2", 1, 64);
    }


    @Test
    public void removeItem() throws Exception {
        //given
        Host host1 = new Host("L2-02-01", "empty");
        //when
        olc.addItem(host1);
        olc.removeItem(host1);
        assertThat(olc.getSize()).isEqualTo(0);
    }

    @Test
    public void shouldAddTwoItems() throws Exception {
        //given
        Host host1 = new Host("L2-02-01", "empty");
        Host host2 = new Host("L2-02-02", "empty");
        Host host3 = new Host("L2-02-02", "empty");
        //when
        olc.addItem(host1);
        olc.addItem(host2);
        olc.removeItem(host1);
        int oltid = olc.addItem(host3);
        assertThat(oltid).isEqualTo(1);
    }



    @Test
    public void ShouldaddOneItem() throws Exception {
        //Given
        Host host = new Host("L2-01-01", "empty");
        //when
        int slotNumber = olc.addItem(host);
        //then
        assertThat(olc.getSize()).isEqualTo(1);
        assertThat(slotNumber).isEqualTo(1);
    }

    @Test
    public void removeAllItems() throws Exception {
        //given
        Host host1 = new Host("L2-02-01", "empty");
        Host host2 = new Host("L2-02-02", "empty");
        olc.addItem(host1);
        olc.addItem(host2);
        olc.removeAllItems();
        Host host3 = new Host("L2-02-02", "empty");
        assertThat(olc.getSize()).isEqualTo(0);
        assertThat(olc.addItem(host3)).isEqualTo(1);
    }

}