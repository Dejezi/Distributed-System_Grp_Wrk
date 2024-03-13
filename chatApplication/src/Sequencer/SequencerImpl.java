package Sequencer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SequencerImpl extends UnicastRemoteObject implements Sequencer {
    private static final long serialVersionUID = 1L;
    private InetAddress multicastAddress;
    private int multicastPort;
    private MulticastSocket multicastSocket;
    private History history;

    public SequencerImpl(String multicastAddress, int multicastPort) throws RemoteException {
        super();
        try {
            this.multicastAddress = InetAddress.getByName(multicastAddress);
            this.multicastPort = multicastPort;
            this.multicastSocket = new MulticastSocket(multicastPort);
            this.multicastSocket.setTimeToLive(1); // Set TTL to 1
            this.multicastSocket.joinGroup(this.multicastAddress);
            this.history = new History();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to initialize multicast socket.");
        }
    }

    @Override
    public SequencerJoinInfo join(String sender) throws RemoteException, SequencerException {
        long initialSequenceNumber = history.getNextSequenceNumber();
        return new SequencerJoinInfo(multicastAddress, initialSequenceNumber);
    }

    @Override
    public void send(String sender, byte[] msg, long msgID, long lastSequenceReceived) throws RemoteException {
        try {
            ByteArrayOutputStream bstream = new ByteArrayOutputStream();
            DataOutputStream dstream = new DataOutputStream(bstream);
            dstream.writeLong(history.getNextSequenceNumber());
            dstream.writeLong(msgID);
            dstream.writeUTF(sender); // Include sender's identifier
            dstream.write(msg); // Include message content
            byte[] data = bstream.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, multicastPort);
            multicastSocket.send(packet);
            history.addMessage(sender, msgID);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to send message to multicast group.");
        }
    }

    @Override
    public void leave(String sender) throws RemoteException {
        // Placeholder for leaving the multicast group
    }

    @Override
    public byte[] getMissing(String sender, long sequence) throws RemoteException, SequencerException {
        // Placeholder for retrieving missing messages
        return null;
    }

    @Override
    public void heartbeat(String sender, long lastSequenceReceived) throws RemoteException {
        // Placeholder for handling heartbeat messages
    }
}
