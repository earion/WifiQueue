package pl.orange.queue;
import pl.orange.queueComposite.Host;
import pl.orange.queueComposite.HostListAgregate;
import pl.orange.response.RestResponse;
import pl.orange.util.HostListException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class AddQueue {

    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse addToQueue( @DefaultValue("empty") @QueryParam("name") String name,
                                  @DefaultValue("empty") @QueryParam("host") String host ) {
        try {
            int result = HostListAgregate.get().addItem(new Host(host,name));
            String msg = "Host " + host + " added to queue " + name;
            if(result != 0) {
                msg += " on channel " + result;
            }
            return new RestResponse(true,result,msg);
        } catch (HostListException e) {
            return new RestResponse(false, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());
        }
    }

    @GET
    @Path("/del")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse removeFromQueue( @DefaultValue("empty") @QueryParam("name") String name,
                                  @DefaultValue("empty") @QueryParam("host") String host ) {
        try {
            HostListAgregate.get().removeItem(new Host(host,name));
            String msg = "Host " + host + " removed from queue " + name;
            return new RestResponse(true,1,msg);
        } catch (HostListException e) {
            return new RestResponse(false, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());
        }
    }
}
