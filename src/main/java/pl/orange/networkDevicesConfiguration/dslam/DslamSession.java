package pl.orange.networkDevicesConfiguration.dslam;

import org.apache.log4j.Logger;
import pl.orange.networkDevicesConfiguration.nokiaIsam.connection.NetworkDeviceConnectionSsh;
import pl.orange.queueComposite.Host;
import pl.orange.queueComposite.HostListAgregate;
import pl.orange.util.HostListException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DslamSession {
    private static final Logger log = Logger.getLogger(DslamSession.class);
    private boolean stopSession = false;
    private NetworkDeviceConnectionSsh ict;

    public DslamSession() {
    }

    public void startSession() {
        startSessionSsh();
        startLoop();
    }

    private void startSessionSsh() {
        try {
            ict = new NetworkDeviceConnectionSsh("isadmin;ANS#150;10.0.0.100;ssh;dslam;");
            ict.setConnection();
        } catch (IOException | HostListException e) {
            e.printStackTrace();
        }
    }

    public void stopSessionSsh() {
        stopSession = true;
    }

    private void startLoop() {
        while (!stopSession) {
            try {
                String result = ict.sendCommand("");
                ArrayList<ONT> onts = getOntFromAlarms(result);
                for (ONT ont : onts) {
                    log.info("Detected: " + ont);
                    manageOnt(ont);
                }
                Thread.sleep(20 * 1000);
            } catch (HostListException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            ict.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageOnt(ONT ont) {
        if(ont.getAction().equalsIgnoreCase("occurred")){
            try {
                HostListAgregate.get().addItem(new Host(ont.getSerialNumber(), "ontPL"));
            } catch (HostListException e) {
                e.printStackTrace();
            }
        } else if(ont.getAction().equalsIgnoreCase("cleared")) {
            /*try {
                OntRegistrator reg = new OntRegistrator(1, Integer.parseInt(ont.getSlot()));
                reg.unregisterONT();
            } catch (HostListException e) {
                e.printStackTrace();
            }*/
        }
    }

    private ArrayList<ONT> getOntFromAlarms(String response) {
        ArrayList<ONT> onts = new ArrayList<>();
        Scanner sc = new Scanner(response);
        sc.useDelimiter(" alarm ");

        while (sc.hasNext()) {
            String alarm = sc.next();
            if (alarm.contains(" for pon ") && alarm.contains("SERNUM")) {
                String[] alarmParts = alarm.split(" ");
                String[] slotParts = getSlotInformation(alarmParts).split("/");
                String slot = slotParts[slotParts.length - 1];
                String serialNumber = extractText(alarm, "SERNUM =", ",");
                String action = alarmParts[0];
                onts.add(new ONT(slot, serialNumber, action));
            }
        }
        return onts;
    }

    private String getSlotInformation(String[] informations) {
        for (String information : informations) {
            if (information.contains("1/1/")) {
                return information;
            }
        }
        return "";
    }

    private String extractText(String source, String startText, String stopText) {
        int p = source.indexOf(startText) + startText.length();
        int k = source.indexOf(stopText, p);
        return source.substring(p, k);
    }
}
