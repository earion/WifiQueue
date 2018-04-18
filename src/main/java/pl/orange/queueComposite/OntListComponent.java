package pl.orange.queueComposite;

import org.apache.log4j.Logger;
import pl.orange.networkDevicesConfiguration.nokiaIsam.OntRegistrator;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class OntListComponent extends HostListComponent {
    private static final Logger log = Logger.getLogger(OntListComponent.class);
    private int oltId;
    private int size;
    private Map<HostListComponent, Integer> ontPoole;
    private CopyOnWriteArrayList<Boolean> ocupiedSlots;
    private OntRegistrator ontr;

    OntListComponent(String name, int oltId, int size) {
        super(name);
        this.oltId = oltId;
        this.size = size;
        ontPoole = Collections.synchronizedMap(new LinkedHashMap<>(size));
        ocupiedSlots = new CopyOnWriteArrayList<>();
        fillAllSlotsAsEmpty(size);
    }

    private void fillAllSlotsAsEmpty(int size) {
        ocupiedSlots.clear();
        for (int i = 0; i < size + 1; i++) {
            ocupiedSlots.add(false);
        }
    }

    public int getOltId() {
        return oltId;
    }

    public LinkedList<HostListComponent> getItems() {
        LinkedList<HostListComponent> hosts = new LinkedList<>();
        ontPoole.forEach((key, value) -> hosts.add(key));
        return hosts;
    }

    @Override
    protected void removeItem(HostListComponent item) throws HostListException {
        if (!ontPoole.containsKey(item)) {
            throw new HostListException(ExceptionMessages.NOT_PRESENT, item.getName() + " is not present on list " + getName());
        }
        int index = ontPoole.get(item);
        ontPoole.remove(item);
        ocupiedSlots.set(index, false);
        unregisterOnt(index);
        log.info("Remove ONT from " + item.getName());
    }

    @Override
    protected int addItem(HostListComponent item) throws HostListException {
        checkIfElementIsNotOnList(item);
        int freeSlotId = findFirstFreeSlot();
        if (freeSlotId == 0) {
            throw new HostListException(ExceptionMessages.WAIT, "List " + getName() + " if full, impossible to add " + item.getName());
        } else {
            ocupiedSlots.set(freeSlotId, true);
            ontPoole.put(item, freeSlotId);
            registerOnt(item.getName(), freeSlotId);
            log.info("add ONT from " + item.getName() + " to " + getName() + " on slot " + freeSlotId);
        }
        return freeSlotId;
    }

    private void checkIfElementIsNotOnList(HostListComponent item) throws HostListException {
        if (ontPoole.containsKey(item)) {
            throw new HostListException(ExceptionMessages.INPROGRESS, item.getName() + " is present on list " + getName());
        }
    }

    private int findFirstFreeSlot() {
        for (int i = 1; i < size; i++) {
            if (ocupiedSlots.get(i).equals(false)) return i;
        }
        return 0;
    }

    private void registerOnt(String name, int freeSlotId) throws HostListException {
        try {
            ontr = new OntRegistrator(oltId, freeSlotId);
            ontr.registerONT(name);
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            log.error("Problem with receiving a response from the ssh server.");
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE, e.getClass().getName());
        } catch (NullPointerException e) {
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE, e.getClass().getName());
        }
    }

    @Override
    protected void removeAllItems() throws HostListException {
        ontPoole = Collections.synchronizedMap(new LinkedHashMap<>(size));
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

    private void unregisterOnt(int slotId) throws HostListException {
        try {
            ontr = new OntRegistrator(oltId, slotId);
            ontr.unregisterONT();
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            log.error("Problem with receiving a response from the ssh server.");
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE, e.getClass().getName());
        } catch (NullPointerException e) {
            throw new HostListException(ExceptionMessages.DSLAM_CONNECTION_ISSUE, e.getClass().getName());
        }
    }
}
