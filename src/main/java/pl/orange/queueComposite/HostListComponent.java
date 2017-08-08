package pl.orange.queueComposite;

import pl.orange.util.HostListException;


public abstract class HostListComponent {

    private final String name;
    protected abstract void removeItem(HostListComponent item) throws HostListException;
    protected abstract int addItem(HostListComponent item) throws HostListException;
    protected abstract void removeAllItems() throws  HostListException;
    public abstract int getSize();
    protected abstract void setSize(int size);

    HostListComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HostListComponent && ((HostListComponent) obj).getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public abstract int getMaxSize();
}
