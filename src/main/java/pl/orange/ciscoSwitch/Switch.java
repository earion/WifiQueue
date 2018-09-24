package pl.orange.ciscoSwitch;

import pl.orange.networkDevicesConfiguration.ciscoSwitch2960.CiscoSwitch;
import pl.orange.response.RestResponse;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

@Path("/")
public class Switch {
    @GET
    @Path("/portState")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse addToQueue(@DefaultValue("empty") @QueryParam("port") String port,
                                   @DefaultValue("empty") @QueryParam("state") String state,
                                   @DefaultValue("empty") @QueryParam("switchName") String switchName) {

        RestResponse errorResponse = handleInputValidation(port, state, switchName);
        if (errorResponse != null) return errorResponse;
        try {
            String ip = getSwitchIp(switchName);
            CiscoSwitch ciscoSwitch = new CiscoSwitch();
            changePortState(port, state, switchName, ciscoSwitch);
            String checkPortStatus = ciscoSwitch.showPortStatus(port, ip);
            return new RestResponse(true, 0, checkPortStatus);
        } catch (HostListException e) {
            return new RestResponse(false, -1, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());
        }

    }

    private void changePortState(@DefaultValue("empty") @QueryParam("port") String port,
                                 @DefaultValue("empty") @QueryParam("state") String state,
                                 @DefaultValue("empty") @QueryParam("switchName") String switchName,
                                 CiscoSwitch ciscoSwitch) throws HostListException {
        if (!state.equals("empty")) {
            Boolean portState = true;
            if (state.equals("down")) {
                portState = false;
            }
            String ip = getSwitchIp(switchName);
            ciscoSwitch.changePortState(port, portState, ip);
        }
    }


    @GET
    @Path("/portVlan")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse addToQueue(@DefaultValue("empty") @QueryParam("port") String port,
                                   @DefaultValue("empty") @QueryParam("mode") String mode,
                                   @DefaultValue("empty") @QueryParam("vlans") String vlans,
                                   @DefaultValue("empty") @QueryParam("switchName") String switchName) {

        RestResponse validationError = handleVlanChangeValidation(port, mode, vlans, switchName);
        if (validationError != null) return validationError;


        try {
            String ip = getSwitchIp(switchName);
            CiscoSwitch ciscoSwitch = new CiscoSwitch();
            ciscoSwitch.changeVlanMode(port, mode, vlans, ip);
            String checkPortStatus = ciscoSwitch.showPortStatus(port, ip);
            return new RestResponse(true, 0, checkPortStatus);
        } catch (HostListException e) {
            return new RestResponse(false, -1, e.getStatusMessage().name() + " " + e.getMessage(), e.getStatusMessage().name());

        }
    }

    private String getSwitchIp(String switchName) {
        try {
            Properties properties = new Properties();
            properties.load(Switch.class.getClassLoader().getResourceAsStream("ciscoDevices.properties"));
            return properties.getProperty(switchName);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private RestResponse handleInputValidation(String port, String state, String switchName) {
        if (port.equals("empty")) {
            return new RestResponse(false, -1, "port not defined", ExceptionMessages.LOGIC_ERROR.name());
        }
        if (!state.equals("empty") && !Arrays.asList(new String[]{"up", "down"}).contains(state)) {
            return new RestResponse(false, -1, "state should be up or down", ExceptionMessages.LOGIC_ERROR.name());
        }
        if (!isSwitchNameCorrect(switchName)) {
            return new RestResponse(false, -1, "switchName not defined.", ExceptionMessages.LOGIC_ERROR.name());
        }
        return null;
    }

    private RestResponse handleVlanChangeValidation(String port, String mode, String vlans, String switchName) {
        if (port.equals("empty")) {
            return new RestResponse(false, -1, "port not defined", ExceptionMessages.LOGIC_ERROR.name());
        }
        if (mode.equals("empty")) {
            return new RestResponse(false, -1, "mode not defined", ExceptionMessages.LOGIC_ERROR.name());
        }
        if (!mode.equals("empty") && vlans.equals("empty")) {
            return new RestResponse(false, -1, "mode parameters should  have defined vlans parameter", ExceptionMessages.LOGIC_ERROR.name());
        }
        if (!Arrays.asList(new String[]{"trunk", "access"}).contains(mode)) {
            return new RestResponse(false, -1, "mode parameter should  be trunk or access", ExceptionMessages.LOGIC_ERROR.name());

        }
        if (!isSwitchNameCorrect(switchName)) {
            return new RestResponse(false, -1, "switchName not defined.", ExceptionMessages.LOGIC_ERROR.name());
        }
        return null;
    }

    private boolean isSwitchNameCorrect(String switchName){
        Properties properties = new Properties();
        try {
            properties.load(Switch.class.getClassLoader().getResourceAsStream("ciscoDevices.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.keySet().contains(switchName);
    }
}
