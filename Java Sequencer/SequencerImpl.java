import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.List;

public class SequencerImpl extends UnicastRemoteObject implements Sequencer 
{
    History history;
    InetAddress group;
    MulticastSocket socket;
    int port;
    String name;
    long sequenceNo;
    long lastSequence;
    List<String> clients;

    @SuppressWarnings("deprecation")
    public SequencerImpl(String seqName) throws IOException 
    {
        this.name = seqName;
        this.group = InetAddress.getByName("239.0.0.0"); 
        this.port = 5555;
        this.socket = new MulticastSocket(port); 
        this.history = new History();
        this.sequenceNo = 0;
        this.lastSequence = 0;
        this.clients = new ArrayList<>();

        socket.setTimeToLive(1); 
        socket.joinGroup(group);

        ReadThread listener =new ReadThread(name, socket, group, port); 
        Thread t = new Thread(listener); 
        t.start();
    }

    @Override
    public SequencerJoinInfo join(String sender) throws RemoteException, SequencerException 
    {
        if(!clients.contains(sender))
        {
            clients.add(sender);
            System.out.println(sender + " joined the chat.");
            return new SequencerJoinInfo(group, sequenceNo);
        }
        System.out.println("Rejected: " + sender + " username is already taken.");
        return null;
    }

    @Override
    public void send(String sender, byte[] msg, long msgID, long lastSequenceReceived) throws RemoteException 
    { 
        String strMsg;
        DatagramPacket datagram;
        try {
            strMsg = new String(msg, "UTF-8");
            ByteArrayOutputStream bstream = new ByteArrayOutputStream(512);
            DataOutputStream dstream = new DataOutputStream(bstream);
            dstream.writeUTF(strMsg);
            byte[] data = bstream.toByteArray();
            datagram = new DatagramPacket(data, data.length, group, port);
            dstream.close();
            bstream.close();
            socket.send(datagram);
            history.writeMessage(sender, msgID, msg);
            sequenceNo++;
            lastSequence = sequenceNo;
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @Override
    public void leave(String sender) 
    {
        clients.remove(sender);
        System.out.println(sender + " left the chat.");
    }

    @Override
    public byte[] getMissing(String sender, long sequence) throws RemoteException, SequencerException 
    {    
        byte[] request = "...null...".getBytes();
    
        try
        {
            request = history.readMessage(sender, sequence);
        } catch (IOException e) {
            System.out.println("Could not retrieve message: " + e);
        }

        if (request != null) 
            System.out.println("Sequencer gets missing: " + sequence);
        else 
            System.out.println("Sequencer couldn't get sequence number: " + sequence);
        
        return request;
    }

    @Override
    public void heartbeat(String sender, long lastSequenceReceived) throws RemoteException 
    {
        System.out.print("Heartbeat: " + sender + " is online.\n");
    }

    public void close() throws IOException 
    {
        history.closeFile();
        System.out.println("File closed.");
    }

    public void sendToSequencer(String msg, String sender) throws RemoteException
    {
        String message = sender + ": " + msg; 
        byte[] msgBytes = message.getBytes();
        send(sender, msgBytes, this.sequenceNo, this.lastSequence);
    }

    public void heart(String sender) throws RemoteException
    {
        heartbeat(sender, lastSequence);
    }
}