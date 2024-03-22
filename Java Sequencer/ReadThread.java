import java.net.*; 
import java.io.*; 

class ReadThread implements Runnable 
{ 
    private String name;
    private MulticastSocket socket; 
    private InetAddress group; 
    private int port; 
    private static final int MAX_LEN = 512; 
    ReadThread(String sender, MulticastSocket socket, InetAddress group, int port) 
    { 
        this.socket = socket; 
        this.group = group; 
        this.port = port; 
        this.name = sender;
    } 
    
    @Override
    public void run() 
    { 
        while(true) 
        { 
                byte[] buffer = new byte[ReadThread.MAX_LEN]; 
                DatagramPacket datagram = new
                DatagramPacket(buffer, buffer.length, group, port); 
                String message; 
            try
            { 
                socket.receive(datagram);
                //////////// Unmarshalling of data.
                ByteArrayInputStream bstream = new ByteArrayInputStream(buffer);
                DataInputStream dstream = new DataInputStream(bstream);
                // Read data from the DataInputStream
                message = dstream.readUTF();
                dstream.close();
                bstream.close(); 
                ////////////
                if(!message.startsWith(name)) 
                    System.out.println(message); 
            } 
            catch(IOException e) 
            { 
                System.exit(0);
            } 
        } 
    } 
}