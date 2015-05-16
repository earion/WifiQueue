package pl.orange.queueComposite;

import org.apache.log4j.Logger;
import pl.orange.util.HostListException;
import pl.orange.util.ListMessages;
import java.util.LinkedList;


public class HostListAgregate extends HostListComponent {

    private static final Logger log = Logger.getLogger(HostListAgregate.class);
    private static HostListAgregate instance;

    private final LinkedList<HostListComponent> agregateList;

    private HostListAgregate() {
        super("Aggregate");
        agregateList = new LinkedList<>();

    }

    private void createConfiguration() throws HostListException {
        WifiListComponent wlc =  new WifiListComponent("wifi24",3);
        wlc.addItem(new SimpleHostsList("1",10));
        wlc.addItem(new SimpleHostsList("6",10));
        wlc.addItem(new SimpleHostsList("11",10));
        instance.addItem(wlc);
        SimpleHostsList slh = new SimpleHostsList("wifi5",10);
        instance.addItem(slh);
    }


    public static HostListAgregate get() throws HostListException {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (HostListAgregate.class) {
                if (instance == null) {
                    instance = new HostListAgregate();
                    instance.createConfiguration();
                }
            }
        }
        return instance;
    }




    public static HostListAgregate getInstanceForTestPurpose() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
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
        throw new HostListException(ListMessages.NOT_PRESENT,"No Item " + hostToRemove.getListName() + " in HostListAgregate");
    }

    private void checkIfOnlyHostIsRemoved(HostListComponent item) throws HostListException {
        if(!(item instanceof Host)) throw  new HostListException(ListMessages.LOGIC_ERROR,"It is possible to remove hosts only");
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
            throw new HostListException(ListMessages.NOT_PRESENT, "Target list " + targetList + " was not add to Agregate");
        } else {
            agregateList.add(item);
        }
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }

    public int getSizeofInternalList(String targetList) throws HostListException {
       for(HostListComponent hlc : agregateList) {
           if(hlc.getName().equals(targetList)) {
               return hlc.getSize();
           }
       }
       throw new HostListException(ListMessages.NOT_PRESENT,"Internal list " + targetList + " not present in agregate");
    }

}
