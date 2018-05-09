package pl.orange.queue;

import pl.orange.queueComposite.Host;
import pl.orange.queueComposite.HostListAgregate;
import pl.orange.queueComposite.HostListComponent;
import pl.orange.queueComposite.OntListComponent;
import pl.orange.response.RestResponse;
import pl.orange.util.ErrbitUtils;
import pl.orange.util.HostListException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Queue {

    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse addToQueue(@DefaultValue("empty") @QueryParam("name") String name,
                                   @DefaultValue("empty") @QueryParam("host") String host) {
        try {
            int result = HostListAgregate.get().addItem(new Host(host, name));
            String msg = "Host " + host + " added to queue " + name;
            if (result != 0) {
                msg += " on channel " + result;
            }
            return new RestResponse(true, result, msg);
        } catch (HostListException e) {
            return new RestResponse(false, -1, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());
        } catch (Exception e){
            ErrbitUtils.notifyError(e);
            throw e;
        }
    }

    @GET
    @Path("/del")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse removeFromQueue(@DefaultValue("empty") @QueryParam("name") String name,
                                        @DefaultValue("empty") @QueryParam("host") String host) {
        try {
            HostListAgregate.get().removeItem(new Host(host, name));
            String msg = "Host " + host + " removed from queue " + name;
            return new RestResponse(true, 1, msg);
        } catch (HostListException e) {
            return new RestResponse(false, -1, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());
        } catch (Exception e){
            ErrbitUtils.notifyError(e);
            throw e;
        }
    }

    @GET
    @Path("/unregisterAllOnts")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse unregisterOnts(@DefaultValue("empty") @QueryParam("name") String name) {
        try {
            HostListComponent queue = HostListAgregate.get().getQueue(name);
            if(queue instanceof OntListComponent) {
                ((OntListComponent) queue).unregisterAllOnts();
            }
            String msg = "All ONTS have been unregistered";
            return new RestResponse(true, 1, msg);
        } catch (HostListException e) {
            return new RestResponse(false, -1, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());
        } catch (Exception e){
            ErrbitUtils.notifyError(e);
            throw e;
        }
    }

    @GET
    @Path("/synchronization")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse synchronization(@DefaultValue("empty") @QueryParam("name") String name, @DefaultValue("empty") @QueryParam("state") String state) {
        try {
            String msg;
            HostListComponent queue = HostListAgregate.get().getQueue(name);
            if(queue instanceof OntListComponent) {
                ((OntListComponent) queue).getConfiguration().setLastStateSynchronization(Boolean.valueOf(state));
                msg = "State of synchronization was changed";
            } else {
                msg = "Queue isn't initialize";
            }
            return new RestResponse(true, 1, msg);
        } catch (HostListException e) {
            return new RestResponse(false, -1, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());
        } catch (Exception e){
            ErrbitUtils.notifyError(e);
            throw e;
        }
    }
}
