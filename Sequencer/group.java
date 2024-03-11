package sequencer;
import java.net.*;
import java.util.*;
import java.io.*;
import java.rmi.*;

public class Group implements Runnable
{
    Thread t;
    Thread HeartBeater;
    Sequencer sequencer;
    MulticastSocket socket;
    MsgHandler handler;
    long lastSequenceReceived;
    long lastSequenceSent;
    InetAddress groupAddress;
    InetAddress myAddress;
    String myName;
    long lastSendTime;

    public Group(String host, MsgHandler handler, String senderName)  throws GroupException
    {
        lastSequenceReceived = -1L;
        lastSequenceSent = -1L;
        try{
            // contact Sequencer on "host" to join group,
            String Host[] = Naming.list("//mpc2/");
            for (int i = 0; i< Host.length; i++) {
                System.out.println(String.valueOf(Host[i]));
            } 
            // create MulticastSocket and thread to listen on it  
            myAddress = InetAddress.getLocalHost();
            sequencer = (Sequencer)Naming.lookup("//"+ host +"/TKSequencer");
            myName = senderName + myAddress;
            SequencerJoinInfo joinInfo = sequencer.join(myName);
            groupAddress = joinInfo.addr;
            lastSequenceReceived = joinInfo.sequence;
            System.out.println("Ip of Group " + groupAddress);
            socket = new MulticastSocket(10000);
            socket.joinGroup(groupAddress);
            // perform other initialisations
            this.handler = handler;
            t = new Thread(this);
            t.start();
            HeartBeater = new HeartBeater(5);
            HeartBeater.start();
        } catch(SequencerException e){
            System.out.println("Couldnt create group "+ e);
            throw new GroupException(String.valueOf(e));
        } catch (Exception e){
            System.out.println("Couldnt create group "+ e);
            throw new GroupException("Couldn't create group");
        }    
    }

    public void send(byte[] msg) throws GroupException
    {
        // send the given message to all instances of Group using the same sequencer
        if (socket != null){
            try {
                sequencer.send(myName, msg, ++lastSequenceSent, lastSequenceReceived);
                lastSendTime = (new Date()).getTime();
            } catch (Exception e) {
                System.out.println("Couldn't contact the sequencer " +e);
                throw new GroupException("Couldn't contact the sequencer");
            }
        }
        else {
            throw new GroupException("Group not joined");
        }
    }

    public void leave()
    {
       // leave group
       if (socket != null)
        try
            {
                socket.leaveGroup(groupAddress);
                sequencer.leave(myName);
            }
            catch (Exception e){
                System.out.println("Couldn't leave group" + e);
            }
    }

    public void run()
    {
        try {
            while (true) {
                byte buf[] = new byte[10240];
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                socket.receive(datagramPacket);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf, 0, datagramPacket.getLength());
                DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                long gotSequence = dataInputStream.readLong();
                int count = dataInputStream.read(buf);
                long wantSeq = lastSequenceReceived + 1L;
                lastSequenceReceived = gotSequence;
                handler.handle(count, buf);
                if (lastSequenceReceived < 0L && wantSeq < gotSequence){
                    for (long getSeq = wantSeq; wantSeq < gotSequence; getSeq++){
                        byte bufExtra[] = sequencer.getMissing(myName, getSeq); // TODO: Work here
                        int countExtra = bufExtra.length;
                        System.out.println("Group: Fetch missing: "+getSeq);
                        handler.handle(countExtra, bufExtra);
                    }
                }                 
            }
        } catch (Exception e) {
            System.out.println("Bad in run: " + e);
        }
        // repeatedly: listen to MulticastSocket created in constructor, and on receipt
        // of a datagram call "handle" on the instance
        // of Group.MsgHandler which was supplied to the constructor
    }

    public interface MsgHandler
    {
         public void handle(int count, byte[] msg);
    }

    public class GroupException extends Exception
    {
        public GroupException(String s)
        {
            super(s);
        }
    }

    public class HeartBeater extends Thread
    {
        // This thread sends heartbeat messages when required
        int period;
        
        public HeartBeater(int period) {
            this.period = period;
        }
        public void run() {
            do 
                try{
                    do
                        Thread.sleep(period*1000);
                        while (new Date().getTime() - lastSendTime < (long)(period*1000));
                        sequencer.heartbeat(myName, lastSequenceSent);                    
                }
                catch(Exception e){}
                while (true);
        }
        
    }

}