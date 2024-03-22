import java.net.*;
import java.io.*;

class ReadThread implements Runnable {
    private String name;
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private static final int MAX_LEN = 512;

    ReadThread(String sender, MulticastSocket socket, InetAddress group, int port) {
        this.name = sender;
        this.socket = socket;
        this.group = group;
        this.port = port;
    }

    @Override
    public void run() {
        while (true) {
            byte[] buffer = new byte[ReadThread.MAX_LEN];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
            String message;
            try {
                socket.receive(datagram);
                ByteArrayInputStream instream = new ByteArrayInputStream(buffer);
                DataInputStream datastream = new DataInputStream(instream);
                message = datastream.readUTF();
                datastream.close();
                instream.close();
                if (!message.startsWith(name))
                    System.out.println(message);
            } catch (IOException e) {
                System.exit(0);
            }
        }
    }
}
