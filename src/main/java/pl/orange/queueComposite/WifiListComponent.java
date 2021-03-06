package pl.orange.queueComposite;

import org.apache.log4j.Logger;
import pl.orange.util.HostListException;
import pl.orange.util.ExceptionMessages;

import java.util.ArrayList;
import java.util.List;


public class WifiListComponent extends HostListComponent {

    private static final Logger log = Logger.getLogger(WifiListComponent.class);
    private final int maxSize;

    ArrayList<SimpleHostsList> wifiChannelsList;

    public WifiListComponent(String name,int size) {
        super(name);
        maxSize = size;
        wifiChannelsList = new ArrayList<>();
    }

    @Override
    public void removeItem(HostListComponent item) throws HostListException {
        for (SimpleHostsList wifiChannel : wifiChannelsList) {
            if (wifiChannel.isHostOnList(item)) {
                wifiChannel.removeItem(item);
                log.info("remove in WifiListComponent item " + item.getName()  + " to: " + getName() + " " + wifiChannel.getName());
                return;
            }
        }
        throw new HostListException(ExceptionMessages.NOT_PRESENT,item.getName() + " "+ ExceptionMessages.NOT_PRESENT.name() + " in " + getName());
    }

    @Override
    public int addItem(HostListComponent item) throws HostListException {
        if(item instanceof Host) {
            checkIfInternalListsWasInitialized();
            checkIfWasAddedBefore(item);
            SimpleHostsList hostListToAddItem = findAtLeastCrowdedChannel();
            hostListToAddItem.addItem(item);
            int channel = Integer.decode(hostListToAddItem.getName());
            log.info("add in WifiListComponent item " + item.getName() + " to: " + getName() + " " + channel);
            return channel;
        } else if (item instanceof HostListComponent) {
            checkIfWifiSimpleListNameIsNumber(item);
            checkIfPossibleAddSimpleHostsList(item);
            wifiChannelsList.add((SimpleHostsList) item);
            log.info("Add SimpleHostsList wifi " + item.getName() + " to " + getName());
            return  0;
        } else {
            throw new HostListException(ExceptionMessages.LOGIC_ERROR, "WifiListComponent could contains only Host or SimpleHostsList");
        }
    }

    @Override
    protected void removeAllItems() throws HostListException {
        while(wifiChannelsList.size() >0 ) {
            wifiChannelsList.get(0).removeAllItems();
            wifiChannelsList.remove(0);
        }
    }

    private void checkIfPossibleAddSimpleHostsList(HostListComponent wifiList ) throws HostListException {
        if(wifiChannelsList.size() == maxSize) throw new HostListException(ExceptionMessages.MULTIPLE_LIST_FULL, "Unable to add " +
                wifiList.getName() + ". \n " + getName() +
                "have only " + this.maxSize + " SimpleHostsLists");
    }

    private void checkIfWifiSimpleListNameIsNumber(HostListComponent wifiList) throws HostListException {
        try {
            Integer.decode(wifiList.getName());
        } catch (NumberFormatException e) {
            throw new HostListException(ExceptionMessages.LOGIC_ERROR,"Unable to addSimpleHostsList  " + wifiList.getName() + " SimpleHostsList name should be casted to integer");
        }
    }

    private void checkIfWasAddedBefore(HostListComponent item) throws HostListException {
      for(SimpleHostsList shl : wifiChannelsList) {
          if(shl.isHostOnList(item)) {
              String creationTime = ((Host)shl.get(item).get()).getCreationDate();
              String listNumber = shl.getName();
              throw new HostListException(ExceptionMessages.INPROGRESS, item.getName() + " is present on list " + getName() + ":" + listNumber + " from " + creationTime);
          }
      }
    }



    private void checkIfInternalListsWasInitialized() throws HostListException {
        if(wifiChannelsList.size() == 0) {
            throw new HostListException(ExceptionMessages.EMPTY,getName() + " " + ExceptionMessages.EMPTY.name() + " use addSimpleHostsList to fulfill wifiChannelsList ");
        }
    }

    private SimpleHostsList findAtLeastCrowdedChannel() {
        SimpleHostsList hostListToAddItem = wifiChannelsList.get(0);
        for(SimpleHostsList wifiChanel: wifiChannelsList) {
            if(wifiChanel.getSize() < hostListToAddItem.getSize()) {
                hostListToAddItem = wifiChanel;
            }
        }
        return hostListToAddItem;
    }

    public int getSize() {
        int size = 0;
        for(SimpleHostsList wifiChanel: wifiChannelsList) {
            size +=wifiChanel.getSize();
        }
        return size;
    }

    public ArrayList<SimpleHostsList> getItems() {
        return wifiChannelsList;
    }

    @Override
    public void setSize(int size) {
        for(SimpleHostsList wifiChanel: wifiChannelsList) {
            wifiChanel.setSize(size);
        }
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }
}
