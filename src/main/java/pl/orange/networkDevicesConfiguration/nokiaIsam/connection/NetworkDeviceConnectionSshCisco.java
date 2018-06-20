package pl.orange.networkDevicesConfiguration.nokiaIsam.connection;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import net.sf.expectit.Result;
import org.apache.log4j.Logger;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.IOException;
import java.security.PublicKey;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;
import static net.sf.expectit.matcher.Matchers.contains;

class NetworkDeviceConnectionSshCisco extends NetworkDeviceConnectionAbstract implements NetworkDeviceConnectable {

    private static final Logger log = Logger.getLogger(NetworkDeviceConnectionSshCisco.class);
    private Expect expect;
    private final SSHClient ssh;


    NetworkDeviceConnectionSshCisco(String connectionParameters) throws HostListException {
        super(connectionParameters);
        try {

            ssh = new SSHClient();
            ssh.addHostKeyVerifier(
                    new HostKeyVerifier() {
                        @Override
                        public boolean verify(String s, int i, PublicKey publicKey) {
                            return true;
                        }
                    });
            ssh.connect(this.getConnectionDestination());
            ssh.authPassword(this.getUser(), this.getPassword());
        } catch (IOException e) {
            throw new HostListException(ExceptionMessages.SSH_FAILURE, e.getMessage());
        }
    }

    @Override
    public void setConnection() throws IOException, HostListException {
        try {
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

            expect.sendLine("ena");
            expect.expect(contains("word"));
            expect.sendLine(this.getPassword());
            Result r1 = expect.expect(contains("#"));
            if (!r1.isSuccessful()) {
                throw new HostListException(ExceptionMessages.CISCO_SWITCH_ENABLE_FAILURE, "");
            }
        } catch (IOException e) {
            throw new HostListException(ExceptionMessages.SSH_FAILURE, e.getMessage());
        }

    }

    @Override
    public String sendCommand(String command) throws HostListException {
        return sendCommand(command, true);
    }

    @Override
    public String sendCommand(String command, boolean outputToLog) throws HostListException {
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

    }

    @Override
    public void stopKeepingSession() {

    }
}
