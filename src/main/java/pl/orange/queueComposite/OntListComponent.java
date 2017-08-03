package pl.orange.queueComposite;

import pl.orange.util.HostListException;

import java.util.HashMap;


public class OntListComponent extends HostListComponent {

    String country;
    int size;

    private HashMap<HostListComponent,Integer> ontPoole;

    public OntListComponent(String name,String country, int size) {
        super(name);
        this.country = country;
        this.size = size;
        ontPoole = new HashMap<>(size);
    }



    @Override
    protected void removeItem(HostListComponent item) throws HostListException {

    }

    @Override
    protected int addItem(HostListComponent item) throws HostListException {
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
