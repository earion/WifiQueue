package pl.orange.isamConfiguration.connection;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;
import pl.orange.util.ExceptionMessages;
import pl.orange.util.HostListException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

class IsamConnectionSSH  extends IsamConnectionAbstract implements IsamConnectable    {

    private static final Logger log = Logger.getLogger(IsamConnectionSSH.class);

    private final Session session;

    IsamConnectionSSH(String connectionParameters) throws HostListException {
        super(connectionParameters);
        JSch sshClient = new JSch();
        try {
            session = sshClient.getSession(this.getUser(), this.getConnectionDestination());
            session.setPassword(this.getPassword());
            Hashtable<String,String> sessionConfig = new Hashtable<>();
            sessionConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sessionConfig);
        } catch (JSchException e) {
            throw new HostListException(ExceptionMessages.SSH_FAILURE,e.getMessage());
        }
    }

    @Override
    public void setConnection() throws IOException, HostListException {
        try {
            session.connect();
        } catch (JSchException e) {
            throw new HostListException(ExceptionMessages.SSH_FAILURE,e.getMessage());
        }

    }

    @Override
    public String sendCommand(String command) throws HostListException {
        StringBuilder outputBuffer = new StringBuilder();
        try
        {
            System.out.println(session.getServerVersion() + " " + session.getTimeout());
            ChannelExec channel=(ChannelExec) session.openChannel("exec");
            BufferedReader in=new BufferedReader(new InputStreamReader(channel.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(channel.getErrStream()));
            channel.getExitStatus();
            channel.setCommand(command);
            channel.connect();
            channel.setPty(true);
            Thread.sleep(10000);
            String msg;
            while((msg=in.readLine())!=null){
                log.info(msg);
            }
            while((msg=err.readLine())!=null){
                log.info(msg);
            }
            channel.disconnect();
        }
        catch(IOException  | InterruptedException| JSchException e)  {
            throw new HostListException(ExceptionMessages.SSH_FAILURE,e.getMessage());

        }
        return outputBuffer.toString();
    }


    @Override
    public void disconnect() throws IOException {

    }
}
