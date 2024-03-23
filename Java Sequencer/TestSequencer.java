import java.net.*;
import java.io.*;
import java.util.*;

public class TestSequencer {
    static String name;

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Group.GroupException {
        try {
            InetAddress group = InetAddress.getByName("240.0.0.0");
            int port = 2525;
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter username: ");
            name = scanner.nextLine();
            MulticastSocket socket = new MulticastSocket(port);
            Group grp = new Group(group.getHostAddress(), name);
            socket.setTimeToLive(1);
            socket.joinGroup(group);
            ReadThread rthread = new ReadThread(name, socket, group, port);
            Thread t = new Thread(rthread);
            // Spawn a thread for reading messages
            t.start();

            // Heartbeat thread
            Thread heartbeatThread = new Thread(() -> {
                try {
                    while (true) {
                        grp.heartBeater();
                        Thread.sleep(10000); // Sleep for 10 seconds
                    }
                } catch (InterruptedException | IOException e) {
                    System.out.println("Error during heartbeat");
                    e.printStackTrace();
                }
            });
            heartbeatThread.start();

            // Send messages to the current group
            System.out.println("\nEnter 'exit' to leave group.\n");
            while (true) {
                String message;
                message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    grp.leave();
                    // Stop thread that reads messages
                    t.interrupt();
                    socket.close();
                    break;
                }
                grp.send(message);
            }
            scanner.close();
        } catch (SocketException se) {
            System.out.println("Socket Creation Error");
            se.printStackTrace();
        } catch (IOException ie) {
            System.out.println("Socket Access Error");
            ie.printStackTrace();
        }
    }
}