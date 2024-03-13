package Sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Group extends UnicastRemoteObject {

    private static final long serialVersionUID = 1L;
    private InetAddress multicastAddress;
    private int multicastPort;
    private MulticastSocket multicastSocket;
    private MsgHandler msgHandler;

    public Group(String multicastAddress, int multicastPort, MsgHandler msgHandler) throws RemoteException {
        super();
        try {
            this.multicastAddress = InetAddress.getByName(multicastAddress);
            this.multicastPort = multicastPort;
            this.multicastSocket = new MulticastSocket(multicastPort);
            this.multicastSocket.joinGroup(this.multicastAddress);
            this.msgHandler = msgHandler; // Set the message handler
            startReceiver(); // Start the message receiver thread
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Failed to initialize Group.");
        }
    }

    private void startReceiver() {
        Thread receiverThread = new Thread(() -> {
            try {
                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);
                    msgHandler.handle(packet.getLength(), packet.getData()); // Pass the message to the handler
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiverThread.start();
    }

    public void send(byte[] message) throws RemoteException {
        // Multicast the message to all group members
        try {
            DatagramPacket packet = new DatagramPacket(message, message.length, multicastAddress, multicastPort);
            multicastSocket.send(packet);
            System.out.println("Message sent to group members: " + new String(message));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to send message to group members.");
        }
    }

    public void leave() throws RemoteException {
        // Close the group and release resources
        try {
            multicastSocket.leaveGroup(multicastAddress);
            multicastSocket.close();
            System.out.println("Group closed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to close the group.");
        }
    }

    public interface MsgHandler {
        void handle(int count, byte[] msg);
    }
}
