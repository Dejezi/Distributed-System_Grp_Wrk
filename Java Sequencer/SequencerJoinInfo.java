import java.io.*;
import java.net.*;

public class SequencerJoinInfo implements Serializable {
    public InetAddress addr;
    public long sequence;

    // Constructor to initialize SequencerJoinInfo with the multicast address and sequence number
    public SequencerJoinInfo(InetAddress addr, long sequence) {
        this.addr = addr;
        this.sequence = sequence;
    }
}
