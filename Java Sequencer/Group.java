import java.net.UnknownHostException;
import java.rmi.*;

public class Group
{
    String name;
    Sequencer rmseq;
    SequencerJoinInfo joininfo;

    public Group(String host, String senderName)  throws GroupException, UnknownHostException
    {
        this.name = senderName;
        try 
        {
            rmseq = (Sequencer)Naming.lookup("rmi://localhost:1800"+"/seq");            
            joininfo = rmseq.join(name); 
            if(joininfo == null)
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
        rmseq.sendToSequencer(msg, name);
    }

    public void leave() throws RemoteException
    {
       rmseq.leave(name);
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
        rmseq.heart(name);
    }
} 
