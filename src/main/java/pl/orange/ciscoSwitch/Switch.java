package pl.orange.ciscoSwitch;

import pl.orange.networkDevicesConfiguration.ciscoSwitch2960.CiscoSwitch;
import pl.orange.networkDevicesConfiguration.ciscoSwitch2960.VlanMode;
import pl.orange.response.RestResponse;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;

@Path("/")
public class Switch {

    @GET
    @Path("/portState")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse addToQueue(@DefaultValue("empty") @QueryParam("port") String port,
                                   @DefaultValue("empty") @QueryParam("state") String state) {

        RestResponse errorResponse = handleInputValidation(port, state);
        if (errorResponse != null) return errorResponse;
        try {
            CiscoSwitch ciscoSwitch = new CiscoSwitch();
            changePortState(port, state, ciscoSwitch);
            String checkPortStatus = ciscoSwitch.showPortStatus(port);
            return new RestResponse(true, 0, checkPortStatus);
        }
        catch (HostListException e) {
            return new RestResponse(false,-1, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());
        }

    }

    private void changePortState(@DefaultValue("empty") @QueryParam("port") String port, @DefaultValue("empty") @QueryParam("state") String state, CiscoSwitch ciscoSwitch) throws HostListException {
        if(!state.equals("empty")) {
            Boolean portState = true;
            if (state.equals("down")) {
                portState = false;
            }
            ciscoSwitch.changePortState(port, portState);
        }
    }

    private RestResponse handleInputValidation(@DefaultValue("empty") @QueryParam("port") String port, @DefaultValue("empty") @QueryParam("state") String state) {
        if(port.equals("empty")) {
            return new RestResponse(false,-1,"port not defined", ExceptionMessages.LOGIC_ERROR.name());
        }
        if(!state.equals("empty") && !Arrays.asList(new String[]{"up", "down"}).contains(state)) {
            return new RestResponse(false, -1, "state should be up or down", ExceptionMessages.LOGIC_ERROR.name());
        }
        return null;
    }






    @GET
    @Path("/portVlan")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse addToQueue(@DefaultValue("empty") @QueryParam("port") String port,
                                   @DefaultValue("empty") @QueryParam("mode") String mode,
                                   @DefaultValue("empty") @QueryParam("vlans") String vlans) {

        RestResponse validationError = handleVlanChangeValidation(port, mode, vlans);
        if (validationError != null) return validationError;
        VlanMode vm = VlanMode.ACCESS;
        if(mode.equals("trunk")) {
            vm = VlanMode.TRUNK;
        }

        try {
            CiscoSwitch ciscoSwitch = new CiscoSwitch();
            ciscoSwitch.changeVlanMode(port,vm,vlans);
            String checkPortStatus = ciscoSwitch.showPortStatus(port);
            return new RestResponse(true, 0, checkPortStatus);
        } catch (HostListException e) {
            return new RestResponse(false,-1, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());

        }
    }

    private RestResponse handleVlanChangeValidation(@DefaultValue("empty") @QueryParam("port") String port, @DefaultValue("empty") @QueryParam("mode") String mode, @DefaultValue("empty") @QueryParam("vlans") String vlans) {
        if(port.equals("empty")) {
            return new RestResponse(false,-1,"port not defined", ExceptionMessages.LOGIC_ERROR.name());
        }
        if(mode.equals("empty")) {
            return new RestResponse(false,-1,"define", ExceptionMessages.LOGIC_ERROR.name());
        }
        if(!mode.equals("empty") && vlans.equals("empty")) {
            return new RestResponse(false,-1,"mode parameters should  have defined vlans parameter", ExceptionMessages.LOGIC_ERROR.name());
        }
        if(!Arrays.asList(new String[]{"trunk", "access"}).contains(mode)) {
            return new RestResponse(false,-1,"mode parameter should  be trunk or access", ExceptionMessages.LOGIC_ERROR.name());

        }
        return null;
    }
}
