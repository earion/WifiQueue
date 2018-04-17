package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import net.sf.expectit.Result;
import org.apache.log4j.Logger;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.IOException;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;
import static net.sf.expectit.matcher.Matchers.contains;

class NetworkDeviceConnectionSsh extends NetworkDeviceConnectionAbstract implements NetworkDeviceConnectable {

    private static final Logger log = Logger.getLogger(NetworkDeviceConnectionSsh.class);
    private static Expect expect;
    private static SSHClient ssh = null;
    private static volatile boolean keepSession = false;


    NetworkDeviceConnectionSsh(String connectionParameters) throws HostListException {
        super(connectionParameters);
    }

    @Override
    public void setConnection() throws IOException, HostListException {
        try {
            if (ssh == null) {
                ssh = new SSHClient();
                ssh.addHostKeyVerifier((s, i, publicKey) -> true);
                ssh.connect(this.getConnectionDestination());
                ssh.authPassword(this.getUser(), this.getPassword());

                Session session = ssh.startSession();
                session.allocateDefaultPTY();
                Session.Shell shell = session.startShell();
                expect = new ExpectBuilder()
                        .withOutput(shell.getOutputStream())
                        .withInputs(shell.getInputStream(), shell.getErrorStream())
                        .withEchoInput(System.out)
                        .withEchoOutput(System.err)
                        .withInputFilters(removeColors(), removeNonPrintable())
                        .withExceptionOnFailure()
                        .build();

                Result r1 = expect.expect(contains("#"));
                if (!r1.isSuccessful()) {
                    throw new HostListException(ExceptionMessages.DSLAM_SETTINGS_ERROR, "");
                }
                startThreadKeepingSession();
            }
        } catch (IOException e) {
            throw new HostListException(ExceptionMessages.SSH_FAILURE, e.getMessage());
        }

    }

    private void startThreadKeepingSession() {
        log.info("Start thread of keeping session");
        Thread keepingSession = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(20 * 60 * 1000);
                    if (keepSession) {
                        log.info("Keeping session, send command = [info environment]");
                        sendCommand("info environment");
                    }
                } catch (ArrayIndexOutOfBoundsException | InterruptedException | HostListException e) {
                    e.printStackTrace();
                }
            }
        });
        keepingSession.start();
    }

    @Override
    public String sendCommand(String command) throws HostListException {
        Result result;
        try {
            expect.sendLine(command);
            result = expect.expect(contains("#"));
        } catch (IOException e) {
            throw new HostListException(ExceptionMessages.SSH_FAILURE, e.getMessage());
        }
        return result.getBefore();
    }


    @Override
    public void disconnect() throws IOException {
        expect.close();
    }

    @Override
    public void startKeepingSession() {
        keepSession = true;
    }

    @Override
    public void stopKeepingSession() {
        keepSession = false;
    }
}
