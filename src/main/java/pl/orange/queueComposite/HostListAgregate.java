package pl.orange.queueComposite;

import org.apache.log4j.Logger;
import pl.orange.config.ConfigurationEntry;
import pl.orange.config.ConfigurationManager;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;


public class HostListAgregate extends HostListComponent {

    private static final Logger log = Logger.getLogger(HostListAgregate.class);
    private static HostListAgregate instance;

    private final LinkedList<HostListComponent> agregateList;

    private HostListAgregate() {
        super("Aggregate");
        agregateList = new LinkedList<>();
    }

    private void reloadConfiguration() throws HostListException, IOException {
        instance.removeAllItems();
        for (Map.Entry<String, ConfigurationEntry> entry : ConfigurationManager.getInstance().getConfiguration().entrySet()) {
            ConfigurationEntry confEntry = entry.getValue();
            String queueName = entry.getKey();
            switch (confEntry.getType()) {
                case "multiple": {
                    String[] wlcInnerNames = confEntry.getInner().split("\\,");
                    WifiListComponent wlc = new WifiListComponent(queueName, wlcInnerNames.length);
                    for (String innerName : wlcInnerNames) {
                        wlc.addItem(new SimpleHostsList(innerName, confEntry.getSize()));
                    }
                    HostListAgregate.instance.addItem(wlc);
                    break;
                }
                case "configuration": break;
                case "ont": {
                    HostListAgregate.instance.addItem(new OntListComponent(queueName, confEntry.getSlot(), confEntry.getSize()));
                    break;
                }
                default: {
                    HostListAgregate.instance.addItem(new SimpleHostsList(queueName, confEntry.getSize()));
                    break;
                }
            }
        }
    }




    public LinkedList<HostListComponent> getAgregateList() {
        return agregateList;
    }

    public static HostListAgregate get() throws HostListException {
        if (instance == null) {
            synchronized (HostListAgregate.class) {
                if (instance == null) {
                    instance = new HostListAgregate();
                    try {
                        instance.reloadConfiguration();
                    } catch (IOException e) {
                        throw new HostListException(ExceptionMessages.CONFIGURATION_READING_FAILURE,e.getMessage());
                    }
                }
            }
        }
        return instance;
    }

    static HostListAgregate getInstanceForTestPurpose() {
        if (instance == null) {
            synchronized (HostListAgregate.class) {
                if (instance == null) {
                    instance = new HostListAgregate();
                }
            }
        }
        return instance;
    }

    @Override
    public void removeItem(HostListComponent item) throws HostListException {
        checkIfOnlyHostIsRemoved(item);
        Host hostToRemove = (Host) item;

        for(HostListComponent hlc: agregateList) {
            if(hlc.getName().equals(hostToRemove.getListName())) {
                hlc.removeItem(item);
                return;
            }
        }
        throw new HostListException(ExceptionMessages.NOT_PRESENT,"No Item " + hostToRemove.getListName() + " in HostListAgregate");
    }

    public void removeAllItems() throws HostListException {
        while(agregateList.size() >0 ) {
            agregateList.get(0).removeAllItems();
            agregateList.remove(0);
        }
    }

    private void checkIfOnlyHostIsRemoved(HostListComponent item) throws HostListException {
        if(!(item instanceof Host)) throw  new HostListException(ExceptionMessages.LOGIC_ERROR,"It is possible to remove hosts only");
    }

    @Override
    public int addItem(HostListComponent item) throws HostListException {
        if(item instanceof Host) {
            String targetList = ((Host) item).getListName();
            for(HostListComponent hlc : agregateList) {
                if(hlc.getName().equals(targetList)) {
                    return hlc.addItem(item);
                }
            }
            throw new HostListException(ExceptionMessages.NOT_PRESENT, "Target list " + targetList + " was not add to Agregate");
        } else {
            agregateList.add(item);
        }
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void setSize(int size) {

    }

    @Override
    public int getMaxSize() {
        return 0;
    }

    public void setSizeOfInternalList(String targetList,int size) throws HostListException {
        for(HostListComponent hlc : agregateList) {
            if(hlc.getName().equals(targetList)) {
                hlc.setSize(size);
                try {
                    ConfigurationManager.getInstance().setQueueSize(targetList,size);
                } catch (IOException e) {
                    throw new HostListException(ExceptionMessages.CONFIGURATION_READING_FAILURE,e.getMessage());
                }
                return;
            }
        }
        throw new HostListException(ExceptionMessages.NOT_PRESENT,"Internal list " + targetList + " not present in agregate");
    }


    int getListsNumber() {
        return agregateList.size();
    }

    int getSizeOfInternalList(String targetList) throws HostListException {
        try {
            return agregateList.stream().filter(e -> e.getName()
                    .equals(targetList))
                    .findFirst().get().getSize();
        } catch (NoSuchElementException e) {
            new HostListException(ExceptionMessages.NOT_PRESENT, "Internal list " + targetList + " not present in agregate");
        }
        return 0;
    }



}
