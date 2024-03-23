import java.io.*;
import java.net.*;

public class SequencerJoinInfo implements Serializable {
    public InetAddress address;
    public long sequence;

    // Constructor to initialize SequencerJoinInfo with the multicast address and sequence number
    public SequencerJoinInfo(InetAddress address, long sequence) {
        this.address = address;
        this.sequence = sequence;
    }
}
