package pl.orange.queueComposite;

import org.apache.log4j.Logger;
import pl.orange.util.HostListException;
import pl.orange.util.ListMessages;

import java.util.LinkedList;


public class SimpleHostsList extends HostListComponent{

    private static final Logger log = Logger.getLogger(SimpleHostsList.class);

    private  final int maxSize;
    private final LinkedList<HostListComponent> hostsList;

    public SimpleHostsList(String name, int size) {
        super(name);
        hostsList = new LinkedList<>();
        maxSize = size;
    }

    public int addItem(HostListComponent item) throws HostListException {
        if(getSize() < maxSize) {
            checkIfElementIsNotOnList(item);
            hostsList.add(item);
            log.info("add in SimpleHostsList " + item.getName()  + " to " + getName());
            return 1;
        } else {
            throw new HostListException(ListMessages.WAIT,"List " + getName() + " if full, impossible to add " + item.getName());
        }
    }

    private void checkIfElementIsNotOnList(HostListComponent item) throws HostListException{
        if(hostsList.contains(item)) throw new HostListException(ListMessages.INPROGRESS, item.getName() + " is present on list " + getName());
    }

    public void removeItem(HostListComponent item) throws HostListException {
        if(isHostOnList(item)) {
            hostsList.remove(item);
            log.info("remove in SimpleHostsList item  " + item.getName()  + " from " + getName());

        } else {
            throw new HostListException(ListMessages.NOT_PRESENT, item.getName() + " " + ListMessages.NOT_PRESENT.name() + " in " + getName());
        }
    }

    protected boolean isHostOnList(HostListComponent itemName) {
        for(HostListComponent hostOnList : hostsList) {
            if(hostOnList.equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    public int getSize() {
        return hostsList.size();
    }
}
