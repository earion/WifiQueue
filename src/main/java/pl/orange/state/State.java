package pl.orange.state;
import pl.orange.queueComposite.HostListAgregate;
import pl.orange.util.ErrbitUtils;
import pl.orange.util.HostListException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class State {

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public String presentQueesState() {
        QueueState state= null;
        try {
            state = new QueueState();
        } catch (HostListException e) {
            e.printStackTrace();
        } catch (Exception e){
            ErrbitUtils.notifyError(e);
            throw e;
        }
        return state.getQueueState();
    }

    @GET
    @Path("/modify")
    @Produces(MediaType.TEXT_HTML)
    public String changeQueueSize(@DefaultValue("empty") @QueryParam("name") String name,
                                  @DefaultValue("empty") @QueryParam("size") String size) throws HostListException {
        try {
            HostListAgregate.get().setSizeOfInternalList(name, Integer.decode(size));
            return new QueueState().getQueueState();
        }  catch (HostListException e) {
            return  new QueueState().setError(e.getMessage()).getQueueState();
        } catch (NumberFormatException e) {
            return  new QueueState().setError(e.getMessage() + " size must be integer").getQueueState();
        } catch (Exception e){
            ErrbitUtils.notifyError(e);
            throw e;
        }
    }
}
