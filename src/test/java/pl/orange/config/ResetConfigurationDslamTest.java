package pl.orange.config;

import org.junit.Assert;
import org.junit.Test;
import pl.orange.queueComposite.ResetConfiguration;

public class ResetConfigurationDslamTest {

    @Test
    public void resetConfigurationIsNeeded_1() throws Exception {
        ResetConfiguration configuration = new ResetConfiguration();
        //box is synchronized
        configuration.updateLastChangedQueue();
        configuration.setLastStateSynchronization(true);
        Thread.sleep(10);
        //box isnt synchronized
        configuration.updateLastChangedQueue();
        configuration.setLastStateSynchronization(false);
        Assert.assertTrue(configuration.isRestartConfigurationNeeded());
    }

    @Test
    public void resetConfigurationIsNeeded_2() throws Exception {
        ResetConfiguration configuration = new ResetConfiguration();
        //box isnt synchronized
        configuration.updateLastChangedQueue();
        configuration.setLastStateSynchronization(false);
        Thread.sleep(10);
        configuration.updateLastChangedQueue();
        Assert.assertTrue(configuration.isRestartConfigurationNeeded());
    }

    @Test
    public void resetConfigurationIsntNeeded_1() throws Exception {
        ResetConfiguration configuration = new ResetConfiguration();
        //box is synchronized
        configuration.updateLastChangedQueue();
        configuration.setLastStateSynchronization(false);
        Thread.sleep(10);
        //box isnt synchronized
        configuration.updateLastChangedQueue();
        configuration.setLastStateSynchronization(true);
        Assert.assertFalse(configuration.isRestartConfigurationNeeded());
    }

    @Test
    public void resetConfigurationIsntNeeded_2() throws Exception {
        ResetConfiguration configuration = new ResetConfiguration();
        //box is synchronized
        configuration.updateLastChangedQueue();
        configuration.setLastStateSynchronization(false);
        Thread.sleep(10);
        //box isnt synchronized
        configuration.setLastStateSynchronization(true);
        configuration.updateLastChangedQueue();
        Assert.assertFalse(configuration.isRestartConfigurationNeeded());
    }
}
