package pl.orange.queueComposite;

import pl.orange.util.HostListException;

import java.util.HashMap;


public class OntListComponent extends HostListComponent {

    int oltId;
    int size;

    private HashMap<HostListComponent,Integer> ontPoole;

    public OntListComponent(String name,int oltId, int size) {
        super(name);
        this.oltId = oltId;
        this.size = size;
        ontPoole = new HashMap<>(size);
    }



    @Override
    protected void removeItem(HostListComponent item) throws HostListException {
        ontPoole.remove(item);
    }

    @Override
    protected int addItem(HostListComponent item) throws HostListException {
        ontPoole.put(item,ontPoole.size());
        return 0;
    }

    @Override
    protected void removeAllItems() throws HostListException {


    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    protected void setSize(int size) {

    }

    @Override
    public int getMaxSize() {
        return 0;
    }
}
