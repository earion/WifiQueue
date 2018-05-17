package pl.orange.util;

import airbrake.AirbrakeNotice;
import airbrake.AirbrakeNoticeBuilder;
import airbrake.AirbrakeNotifier;
import com.google.common.hash.Hashing;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import pl.orange.queueComposite.HostListAgregate;
import pl.orange.queueComposite.HostListComponent;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class ErrbitUtils {
    private static final Logger log = Logger.getLogger(ErrbitUtils.class);
    private static final Map<String, Object> environment = new TreeMap<>();
    private static final Map<String, Object> session = new TreeMap<>();
    private static final Map<String, Object> parameters = new TreeMap<>();
    private static Properties properties = new Properties();

    public static void notifyError(Exception e) {
        log.info("Exception", e);
        loadProperties();
        String apiKey = properties.getProperty("api_key");
        String host = properties.getProperty("host");

        initializeEnvironment();
        initializeParameters();
        initializeSessions();

        final AirbrakeNotice notice = new AirbrakeNoticeBuilder(apiKey, hashMessage(e), "prod") {
            {
                environment(environment);
                session(session);
                request(parameters);
                setRequest("", "TOMCAT");
            }
        }.newNotice();
        AirbrakeNotifier notifier = new AirbrakeNotifier(host);
        notifier.notify(notice);
        log.info("Sent notify to Errbit.");
    }

    private static void loadProperties() {
        try {
            properties.load(ErrbitUtils.class.getClassLoader().getResourceAsStream("errbit.properties"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static void initializeEnvironment() {
        environment.put("APP_VERSION", VersionUtils.getVersion());
        environment.put("HOSTNAME", getCliResult("hostname"));
    }

    private static void initializeParameters() {
        try {
            for (HostListComponent hostListComponent : HostListAgregate.get().getAgregateList()) {
                parameters.put("STATUS_" + hostListComponent.getName(),
                        hostListComponent.getSize() + " / " + hostListComponent.getMaxSize());
            }
        } catch (Exception ignored) {
        }
        parameters.put("CONFIGURATION_OLT", getResourcesFileContent("configurationOLT.properties"));
        parameters.put("CONFIGURATION_WAW", getResourcesFileContent("configurationWAW.properties"));
        parameters.put("CONFIGURATION_ERRBIT", getResourcesFileContent("errbit.properties"));
    }

    private static void initializeSessions() {
        session.put("LOGS", getFileContent(DateFileAppender.FILENAME));
    }

    @SuppressWarnings("deprecation")
    private static <T extends Throwable> T hashMessage(T t) {
        String message = Hashing.sha1().hashString(getStackTrace(t),
                Charsets.UTF_8).toString().substring(0, 5);
        return setThrowableMessage(t, message);
    }

    @SuppressWarnings("SameParameterValue")
    private static String getCliResult(String command) {
        return new CLIProcess(command).executeWithTimeoutInSeconds(60).getSuccessMessage();
    }

    private static String getResourcesFileContent(String fileName) {
        try {
            return IOUtils.readLines(ErrbitUtils.class.getClassLoader().getResourceAsStream(fileName),
                    StandardCharsets.UTF_8).stream().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return "";
        }
    }

    private static String getFileContent(String fileName) {
        try {
            return Files.lines(Paths.get(fileName)).collect(Collectors.joining("\n")).replaceAll("\\x1F", "");
        } catch (IOException e) {
            return "";
        }
    }

    private static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ExceptionUtils.printRootCauseStackTrace(e, pw);
        return sw.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T setThrowableMessage(T t, String message) {
        try {
            Field detailMessageField = Throwable.class.getDeclaredField("detailMessage");
            detailMessageField.setAccessible(true);
            detailMessageField.set(t, message);
            return t;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return (T) new Throwable(message, t);
    }
}
