package pl.orange.config;

/**
 * Created by mateusz on 31.05.15.
 */
public class ConfigurationEntry {

    String name;
    String type;
    String inner;
    Integer size;
    Integer slot;

    public ConfigurationEntry(String name) {
        this.name = name;
    }

    public void setType(QueueType type) {
        this.type = type.name().toLowerCase();
    }

    public void setInner(String inner) {
        this.inner = inner;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getInner() {
        return inner;
    }

    public Integer getSize() {
        return size;
    }


    public Integer getSlot() {
        return slot;
    }

}
