package pl.orange.config;

import pl.orange.queueComposite.HostListAgregate;
import pl.orange.response.RestResponse;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

/**
 * Created by mateusz on 31.05.15.
 */

@Path("/")
public class Configuration {

    @GET
    @Path("/show")
    @Produces(MediaType.APPLICATION_JSON)
    public String showConfiguration() {
        try {
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<String,ConfigurationEntry> entry :  ConfigurationManager.getInstance().getConfiguration().entrySet()) {
                sb.append(entry.getValue().getName() + " " +
                        "\n  size: " +entry.getValue().getSize() + " " +
                        "\n  inner: " +entry.getValue().getInner() + " " +
                        "\n  type: " +entry.getValue().getType() + " " +
                        "\n  slot: " +entry.getValue().getSlot() + " " +
                        "\n  configuration: "+ entry.getValue().getConfiguration() + " " +
                        "\n");
            }
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    @GET
    @Path("/modify")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse changeQueueSize(@DefaultValue("empty") @QueryParam("name") String name,
                                        @DefaultValue("empty") @QueryParam("size") String size) {
        try {
            HostListAgregate.get().setSizeOfInternalList(name, Integer.decode(size));
            return new RestResponse(true,1,"Internal list " + name + "size modified to " + size);
        }  catch (HostListException e) {
            return new RestResponse(false,-1, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());
        } catch (NumberFormatException e) {
            return new RestResponse(false, -1, e.getMessage(), ExceptionMessages.SIZE_MUST_BE_INTEGER.name());
        }
    }
}
