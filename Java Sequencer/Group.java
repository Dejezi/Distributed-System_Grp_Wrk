import java.net.UnknownHostException;
import java.rmi.*;

public class Group {
    String name;
    Sequencer rmseq;
    SequencerJoinInfo joininfo;

    public Group(String host, String senderName) throws GroupException, UnknownHostException {
        this.name = senderName;
        try {
            // Lookup the Sequencer object using RMI
            rmseq = (Sequencer) Naming.lookup("rmi://localhost:1800" + "/seq");
            // Join the sequencer with the provided username
            joininfo = rmseq.join(name);
            // Check if the username is already taken
            if (joininfo == null) {
                System.out.println("Username " + name + " is already taken. Please choose another username.");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Failed to initialize sequencer " + e);
        }
    }

    public void send(String msg) throws GroupException, RemoteException {
        rmseq.sendToSequencer(msg, name);
    }

    public void leave() throws RemoteException {
        rmseq.leave(name);
    }

    public class GroupException extends Exception {
        public GroupException(String s) {
            super(s);
        }
    }

    public void heartBeater() throws RemoteException {
        rmseq.heart(name);
    }
}