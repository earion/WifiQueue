package pl.orange.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by mateusz on 31.05.15.
 */
public class ConfigurationManager {

    private static ConfigurationManager instance;
    Properties prop = new Properties();
    private HashMap<String, ConfigurationEntry> configMap;


    public void setQueueSize(String queueName, Integer size) {
        if(configMap.containsKey(queueName)) {
            ConfigurationEntry tmpEntry = configMap.get(queueName);
            configMap.remove(queueName);
            tmpEntry.setSize(size);
            configMap.put(queueName,tmpEntry);
        }
    }

    public Set<String> getQueues() {
        return configMap.keySet();
    }

    public HashMap<String,ConfigurationEntry> getConfiguration() {
        return configMap;
    }


    public ConfigurationEntry getConfigurationForQueue(String queueName) {
        return configMap.get(queueName);
    }

    public static ConfigurationManager getInstance() throws IOException {
        if(instance==null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    private ConfigurationManager() throws IOException {
        String propFileName = "configuration.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        configMap = loadConfigurationFromFile();
    }

    public HashMap<String,ConfigurationEntry> loadConfigurationFromFile() {
        HashMap<String, ConfigurationEntry> returnedMap = new HashMap<>();
        ConfigurationEntry tmp;
        for(String key: prop.stringPropertyNames()) {
            String value = prop.getProperty(key);
            String[] keyType= key.split("\\.");
            String name = keyType[0];
            if(returnedMap.containsKey(name)){
                tmp = returnedMap.get(name);
                returnedMap.remove(name);
            } else {
                tmp = new ConfigurationEntry(name);
            }
            switch (keyType[1]) {
                case "type" : tmp.setType(QueueType.valueOf(value)); break;
                case "inner" : tmp.setInner(value); break;
                case "size": tmp.setSize(Integer.decode(value)); break;
                case "slot": tmp.setSlot(Integer.decode(value)); break;
                case "configuration": tmp.setConfiguration(value); break;
            }
            returnedMap.put(name, tmp);
        }
        return returnedMap;
    }


    public int getConfigSize() {
        return prop.size();
    }
}
