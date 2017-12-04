package pl.orange.config;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationManagerTest {

    @Test
    public void loadConfiguration() throws Exception {
        ConfigurationManager  cm=  ConfigurationManager.getInstance();
        assertThat(cm.getConfigSize()).isGreaterThan(0);
        assertThat(cm.getQueues().contains("wifi24"));
    }

}