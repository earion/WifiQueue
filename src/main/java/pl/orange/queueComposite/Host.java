package pl.orange.queueComposite;

import pl.orange.util.HostListException;
import pl.orange.util.ExceptionMessages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Host extends HostListComponent {

    private final String listName;
    private final Date creationDate;

    public Host(String name, String list) {
        super(name);
        listName = list;
        creationDate = new Date();
    }

    public String getListName() {
        return listName;
    }

    @Override
    public void removeItem(HostListComponent item) throws HostListException {
        throw new HostListException(ExceptionMessages.LOGIC_ERROR,"Host Cannot have internalItems");
    }

    @Override
    public int addItem(HostListComponent item) throws HostListException {
        throw new HostListException(ExceptionMessages.LOGIC_ERROR,"Host Cannot have internalItems");
    }

    @Override
    protected void removeAllItems() throws HostListException {
        throw new HostListException(ExceptionMessages.LOGIC_ERROR,"Host Cannot have internalItems");
    }

    @Override
    public int getSize() {
        return 0;
    }


    @Override
    public void setSize(int size) {}

    @Override
    public int getMaxSize() {
        return 0;
    }

    public String getCreationDate() {
       return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
               .format(creationDate);
    }
}
