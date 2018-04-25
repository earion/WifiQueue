package pl.orange.queueComposite;

import java.util.Date;

public class ResetConfiguration {
    private Date lastChangedQueue;
    private Date lastSuccessSynchronization;
    private boolean lastStateSynchronization;

    public ResetConfiguration() {
        this.lastStateSynchronization = true;
        this.lastSuccessSynchronization = new Date();
    }

    public Date getLastChangedQueue() {
        return lastChangedQueue;
    }

    public Date getLastSuccessSynchronization() {
        return lastSuccessSynchronization;
    }

    public boolean isLastStateSynchronization() {
        return lastStateSynchronization;
    }

    public synchronized void setLastStateSynchronization(boolean lastStateSynchronization) {
        this.lastStateSynchronization = lastStateSynchronization;
        if(lastStateSynchronization){
            updateLastSuccessSynchronization();
        }
    }

    private synchronized void updateLastSuccessSynchronization(){
        lastSuccessSynchronization = new Date();
    }

    public synchronized void updateLastChangedQueue(){
        lastChangedQueue = new Date();
    }

    public synchronized boolean isRestartConfigurationNeeded(){
        if(lastSuccessSynchronization == null || lastChangedQueue == null){
            return false;
        } else {
            return !lastStateSynchronization && lastSuccessSynchronization.before(lastChangedQueue);
        }
    }
}
