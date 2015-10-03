package pl.orange.util;


public class HostListException extends Exception {

    private final ExceptionMessages listMessage;

    public HostListException(ExceptionMessages listMessage,String s) {
        super(s);
        this.listMessage = listMessage;
    }

    public ExceptionMessages getStatusMessage() {
        return listMessage;
    }


}
