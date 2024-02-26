import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;

public class Group extends UnicastRemoteObject implements GroupInterface {
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private int multicastPort;

    // Constructor
    public Group(String multicastAddress, int multicastPort) throws RemoteException {
        super();
        try {
            this.multicastAddress = InetAddress.getByName(multicastAddress);
            this.multicastPort = multicastPort;
            this.multicastSocket = new MulticastSocket(multicastPort);
            // Join the multicast group
            this.multicastSocket.joinGroup(this.multicastAddress);
            // Disable loopback mode if not needed
            this.multicastSocket.setLoopbackMode(true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to resolve multicast address.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to initialize multicast socket.");
        }
    }

    // Method to send a message to the multicast group
    public void sendMessage(byte[] message) throws RemoteException {
        try {
            DatagramPacket packet = new DatagramPacket(message, message.length, multicastAddress, multicastPort);
            multicastSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to send message to multicast group.");
        }
    }

    // Method to close the multicast socket
    public void close() throws IOException {
        if (multicastSocket != null) {
            multicastSocket.leaveGroup(multicastAddress);
            multicastSocket.close();
        }
    }

    // Other methods specific to group communication could be added here
}
