package pl.orange.queueComposite;

import org.apache.log4j.Logger;
import pl.orange.isamConfiguration.OntRegistrator;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class OntListComponent extends HostListComponent {



    private static final Logger log = Logger.getLogger(OntListComponent.class);

    private int oltId;

    private int size;
    private LinkedHashMap<HostListComponent,Integer> ontPoole;

    private ArrayList<Boolean> ocupiedSlots;


    private OntRegistrator ontr;

    public int getOltId() {
        return oltId;
    }

    public LinkedList<HostListComponent> getItems() {
       LinkedList<HostListComponent> hosts = new LinkedList<>();
        for( Map.Entry<HostListComponent,Integer> entry : ontPoole.entrySet()) {
           hosts.add(entry.getKey());
        }
        return hosts;
    }


    public OntListComponent()
    {
        super("void");
    }

    OntListComponent(String name,int oltId, int size) {
        super(name);
        this.oltId = oltId;
        this.size = size;
        ontPoole = new LinkedHashMap<>(size);
        ocupiedSlots = new ArrayList<>(size);
        fillAllSlotsAsEmpty(size);

    }

    private void fillAllSlotsAsEmpty(int size) {
        for(int i=0;i<size+1;i++) {
            if(ocupiedSlots.size() == i) {
                ocupiedSlots.add(false);
            } else {
                ocupiedSlots.add(i,false);
            }
        }
    }

    private int findFirstFreeSlot() {
        for(int i=1;i<size;i++) {
            if(ocupiedSlots.get(i).equals(false)) return i;
        }
        return 0;
    }


    @Override
    protected void removeItem(HostListComponent item) throws HostListException {
        if(!ontPoole.containsKey(item)) {
            throw new HostListException(ExceptionMessages.NOT_PRESENT, item.getName() + " is not present on list " + getName());
        }
        ocupiedSlots.add(ontPoole.get(item),false);
        ontPoole.remove(item);
    }

    @Override
    protected int addItem(HostListComponent item) throws HostListException {
        checkIfElementIsNotOnList(item);
        int freeSlotId = findFirstFreeSlot();
        if(freeSlotId == 0)   throw new HostListException(ExceptionMessages.WAIT,"List " + getName() + " if full, impossible to add " + item.getName());
        if(findFirstFreeSlot() != 0) {
            try {
                ontr = new OntRegistrator(oltId,freeSlotId);
                ontr.registerONT(item.getName());
            } catch (NullPointerException   e) {
                throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE,e.getClass().getName());
            }
            ontPoole.put(item, freeSlotId);
            ocupiedSlots.add(freeSlotId,true);
            log.info("add ONT from " + item.getName() + " to " + getName() + " " + freeSlotId);
        }
        return freeSlotId;
    }


    private void checkIfElementIsNotOnList(HostListComponent item) throws HostListException{
        if(ontPoole.containsKey(item)) {
            throw new HostListException(ExceptionMessages.INPROGRESS, item.getName() + " is present on list " + getName());
        }
    }


    @Override
    protected void removeAllItems() throws HostListException {
        ontPoole = new LinkedHashMap<>(size);
        fillAllSlotsAsEmpty(size);
    }

    @Override
    public int getSize() {
        return ontPoole.values().size();
    }

    @Override
    protected void setSize(int size) {
    }

    @Override
    public int getMaxSize() {
        return size;
    }
}
