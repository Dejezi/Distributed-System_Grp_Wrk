package Sequencer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class TestSequencer implements Group.MsgHandler, Runnable {
    String returned;
    Group group;
    Thread myThread;
    String clientName;
    boolean paused;
    int rate;

    public TestSequencer(String host, String clientName) {
        returned = "Fred";
        paused = false;
        this.clientName = clientName;
        try {
            group = new Group(host, 4446, this); // Pass this as the message handler
            myThread = new Thread(this);
            myThread.start();
        } catch (Exception grp) {
            System.out.println("Can't create group" + grp);
            grp.printStackTrace();
        }
    }

    // Main method
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        if (args.length < 2) {
            System.out.println("Usage: java TestSequencer <host_name> <client_name>");
            System.exit(1);
        } else {
            TestSequencer ts = new TestSequencer(args[0], args[1]);
        }
    }

    // Run method
    public void run() {
        try {
            rate = 8;
            int i = 0;
            do {
                do {
                    if (rate <= 90)
                        try {
                            Thread.sleep((90 - rate) * 10);
                        } catch (Exception d) {
                        }

                } while (paused);
                BufferedReader write = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Enter your message:");
                String message = write.readLine();
                if (message.trim().equals("exit")) {
                    try {
                        group.leave(); // Call the leave method of the Group
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    System.exit(1);
                }
                group.send((new String(clientName + message + i++)).getBytes());
            } while (true);
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    // Handle method implementation
    @Override
    public void handle(int count, byte[] msg) {
        String msg1 = new String(msg, 0, count);
        System.out.println("Received message: " + msg1);
    }
}
