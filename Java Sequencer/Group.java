import java.net.UnknownHostException;
import java.rmi.*;

public class Group
{
    String name;
    Sequencer remote;
    SequencerJoinInfo info;

    public Group(String host, String senderName)  throws GroupException, UnknownHostException
    {
        this.name = senderName;
        try 
        {
            remote = (Sequencer)Naming.lookup("rmi://localhost:1919"+"/seq");            
            info = remote.join(name); 
            if(info == null)
            {
                System.out.println("Username " + name + " is already taken choose another username.");
                System.exit(0);
            }           
        } catch (Exception e) 
        {
            System.out.println("Failed to initialize sequencer " + e);
        }
    }

    public void send(String msg) throws GroupException, RemoteException
    {
        remote.sendToSequencer(msg, name);
    }

    public void leave() throws RemoteException
    {
       remote.leave(name);
    }

    public class GroupException extends Exception
    {
        public GroupException(String s)
        {
            super(s);
        }
    }

    public void heartBeater() throws RemoteException 
    {
        remote.heart(name);
    }
} 
