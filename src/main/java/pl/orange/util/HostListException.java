package pl.orange.util;


public class HostListException extends Exception {

    private final ListMessages listMessage;

    public HostListException(ListMessages listMessage,String s) {
        super(s);
        this.listMessage = listMessage;
    }

    public ListMessages getStatusMessage() {
        return listMessage;
    }


}
