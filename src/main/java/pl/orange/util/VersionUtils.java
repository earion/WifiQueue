package pl.orange.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VersionUtils {

    public static String getVersion() {
        BufferedReader is = new BufferedReader(( new InputStreamReader(VersionUtils.class.getClassLoader().getResourceAsStream("version"))));
        try {
            return is.readLine();
        } catch (IOException e) {
            return "";
        }
    }
}
