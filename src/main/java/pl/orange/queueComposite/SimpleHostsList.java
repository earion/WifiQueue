package pl.orange.queueComposite;

import com.google.common.base.Optional;
import org.apache.log4j.Logger;
import pl.orange.util.HostListException;
import pl.orange.util.ExceptionMessages;

import java.util.LinkedList;


public class SimpleHostsList extends HostListComponent{

    private static final Logger log = Logger.getLogger(SimpleHostsList.class);

    private  int maxSize;
    private final LinkedList<HostListComponent> hostsList;

    SimpleHostsList(String name, int size) {
        super(name);
        hostsList = new LinkedList<>();
        maxSize = size;
    }

    public int addItem(HostListComponent item) throws HostListException {
        if(getSize() < maxSize) {
            checkIfElementIsNotOnList(item);
            hostsList.add(item);
            log.info("add in SimpleHostsList " + item.getName()  + " to " + getName());
            return 0;
        } else {
            throw new HostListException(ExceptionMessages.WAIT,"List " + getName() + " if full, impossible to add " + item.getName());
        }
    }

    @Override
    protected void removeAllItems() throws HostListException {
       while(hostsList.size() >0 ) {
            hostsList.remove(0);
        }
    }

    private void checkIfElementIsNotOnList(HostListComponent item) throws HostListException{
        if(hostsList.contains(item)) {
            String creationTime = ((Host) hostsList.get(hostsList.indexOf(item))).getCreationDate();
            throw new HostListException(ExceptionMessages.INPROGRESS, item.getName() + " is present on list " + getName() + " from " + creationTime);
        }
    }

    public Optional<HostListComponent> get(HostListComponent item) {
        if(hostsList.contains(item)) {
            return Optional.of(hostsList.get(hostsList.indexOf(item)));
        }
        return Optional.absent();
    }

    public void removeItem(HostListComponent item) throws HostListException {
        if(isHostOnList(item)) {
            hostsList.remove(item);
            log.info("remove in SimpleHostsList item  " + item.getName()  + " from " + getName());
        } else {
            throw new HostListException(ExceptionMessages.NOT_PRESENT, item.getName() + " " + ExceptionMessages.NOT_PRESENT.name() + " in " + getName());
        }
    }

    boolean isHostOnList(HostListComponent itemName) {
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

    public int getMaxSize() { return maxSize;}

    public LinkedList<HostListComponent> getItems() { return hostsList;}

    @Override
    protected void setSize(int size) {
        maxSize = size;
    }
}
