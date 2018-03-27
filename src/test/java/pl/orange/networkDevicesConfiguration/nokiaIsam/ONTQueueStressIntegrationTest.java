package pl.orange.networkDevicesConfiguration.nokiaIsam;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Random;

@RunWith(JUnitParamsRunner.class)
public class ONTQueueStressIntegrationTest {

    private String getHost(boolean production) {
        return production ? "wifi.odnowa:80/HostsQueue" : "localhost:8081";
    }

    @Test
    public void keepingSessionTest(){
        stressServer(1, 5, 360000, false);
        try {
            Thread.sleep(3 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stressServer(1, 5, 360000, false);
    }

    @Test
    @Parameters({"30,10,360000,false",})
    public void stressServer(int minutes, int threads, int timeout, boolean production) {
        String host = getHost(production);
        for (int i = 0; i < 6 * minutes; i++) {
            for (int j = 0; j < threads; j++) {
                Thread thread = new Thread(() -> {
                    try {
                        String r = "SMBS" + getRandomHexString(8).toUpperCase();
                        String body = Jsoup.connect("http://" + host + "/queue/add?name=ontPL&host="
                                + r)
                                .method(Connection.Method.GET)
                                .timeout(timeout)
                                .ignoreHttpErrors(true)
                                .ignoreContentType(true).execute().body();
                        System.out.println(body);
                        Thread.sleep(10 * 1000);
                        body = Jsoup.connect("http://" + host + "/queue/del?name=ontPL&host=" + r)
                                .method(Connection.Method.GET).ignoreHttpErrors(true)
                                .timeout(timeout)
                                .ignoreContentType(true).execute().body();
                        System.out.println(body);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("SameParameterValue")
    private String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }


    @SuppressWarnings("unused")
    private void deleteHosts(int timeout) {
        System.out.println("Deleting all hosts from queue...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 10; i++) {
            try {
                String body = Jsoup.connect("http://wifi.odnowa:80/HostsQueue/queue/del?name=ontPL2&host=SMBS020079F" + i)
                        .method(Connection.Method.GET).ignoreHttpErrors(true)
                        .timeout(timeout)
                        .ignoreContentType(true).execute().body();
                System.out.println(body);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}