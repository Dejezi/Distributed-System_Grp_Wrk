import java.rmi.*;

public interface Sequencer extends Remote {
    // Method for a sender to request joining the sequencer's multicasting service;
    // Returns an object specifying the multicast address and the first sequence number to expect
    public SequencerJoinInfo join(String sender) throws RemoteException, SequencerException;

    // Method for a sender to send a message, its identifier, and the sequence number of the last received message
    public void send(String sender, byte[] msg, long msgID, long lastSequenceReceived) throws RemoteException;

    // Method for a sender to inform the sequencer that it will no longer need its services
    public void leave(String sender) throws RemoteException;

    // Method for a sender to request the message with a given sequence number
    public byte[] getMissing(String sender, long sequence) throws RemoteException, SequencerException;

    // Method for a sender to inform the sequencer of received messages up to a specific sequence number
    public void heartbeat(String sender, long lastSequenceReceived) throws RemoteException;

    // Method to send a message directly to the sequencer
    public void sendToSequencer(String msg, String sender) throws RemoteException;

    // Method for a sender to send a heartbeat to the sequencer
    public void heart(String sender) throws RemoteException;
}
