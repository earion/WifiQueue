package pl.orange.queueComposite;

import pl.orange.util.HostListException;
import pl.orange.util.ListMessages;


public class Host extends HostListComponent {

    private final String listName;

    public Host(String name, String list) {
        super(name);
        listName = list;
    }

    public String getListName() {
        return listName;
    }

    @Override
    public void removeItem(HostListComponent item) throws HostListException {
        throw new HostListException(ListMessages.LOGIC_ERROR,"Host Cannot have internalItems");
    }

    @Override
    public int addItem(HostListComponent item) throws HostListException {
        throw new HostListException(ListMessages.LOGIC_ERROR,"Host Cannot have internalItems");
    }

    @Override
    public int getSize() {
        return 0;
    }
}
