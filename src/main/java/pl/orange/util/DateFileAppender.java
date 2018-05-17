package pl.orange.util;

import org.apache.log4j.FileAppender;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFileAppender extends FileAppender {
    static String FILENAME;

    @Override
    public void setFile(String fileName) {
        if (fileName.contains("%date%")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmm");
            FILENAME = fileName.replaceAll("%date%", format.format(new Date()));
        }
        super.setFile(FILENAME);
    }
}