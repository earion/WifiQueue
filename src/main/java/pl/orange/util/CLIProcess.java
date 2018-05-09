package pl.orange.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CLIProcess extends Thread {
    private int result = -1;
    private String errorMessage = "";
    private String successMessage = "";
    private Process process;
    private ProcessBuilder builder;
    private String executedCommand;

    public CLIProcess(String command) {
        String[] listOfArguments = {"/bin/bash", "-c"};
        List<String> commandList = new ArrayList<>();
        Collections.addAll(commandList, listOfArguments);
        commandList.add(command);
        builder = new ProcessBuilder(commandList);
        executedCommand = commandList.toString();
    }

    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public CLIProcess executeWithDefaultTimeout() {
        Integer timeout = 2;
        executeWithTimeoutInSeconds(timeout);
        return this;
    }

    public CLIProcess executeWithTimeoutInSeconds(Integer n) {
        start();
        try {
            join(n * 1000);
        } catch (InterruptedException ignored) {
        }
        if (isAlive()) {
            interrupt();
            killProc();
        }
        return this;
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public CLIProcess executeAndKillProcessAfterNSeconds(Integer n) {
        start();

        try {
            join(n * 1000);
        } catch (InterruptedException ignored) {
        }

        if (isAlive()) {
            String program = executedCommand.substring(16).split("\\s")[0];
            if (program.equals("sudo")) {
                program = executedCommand.substring(16).split("\\s")[1];
            }
            new CLIProcess("sudo killall " + program)
                    .executeWithDefaultTimeout();
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        if (isAlive()) {
            interrupt();
            killProc();
        }
        return this;
    }

    public void run() {
        try {
            process = builder.start();
            process.waitFor();
            try (InputStream inputStream = process.getInputStream()) {
                successMessage = processStream(inputStream);
            } catch (Exception ignored) {
            }
            try (InputStream errorStream = process.getErrorStream()) {
                errorMessage = processStream(errorStream);
            } catch (Exception ignored) {
            }
            result = process.exitValue();
        } catch (IOException | InterruptedException e) {
            result = -2;
            errorMessage = "timeout";
        }
    }

    private String processStream(InputStream is) {
        StringBuilder bs = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while (true) {
                String c = br.readLine();
                if (c == null)
                    break;
                bs.append(c);
                bs.append("\n");
            }
            return bs.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }

    @SuppressWarnings("unused")
    public int getResult() {
        return result;
    }

    @SuppressWarnings("unused")
    public String getErrorMessage() {
        if (errorMessage != null) {
            return errorMessage;
        } else {
            return "";
        }
    }

    public String getSuccessMessage() {
        if (successMessage != null) {
            return successMessage;
        } else {
            return "";
        }
    }

    @SuppressWarnings("unused")
    public String getCmd() {
        return executedCommand;
    }

    private void killProc() {
        if (process != null) {
            process.destroy();
        }
    }

    public static void main(String[] args) {
        CLIProcess cp = new CLIProcess("dhclient eth0");
        cp.executeWithDefaultTimeout();
    }

}