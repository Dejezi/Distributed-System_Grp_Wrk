package Sequencer;

import java.io.Serializable;
import java.net.InetAddress;

public class SequencerJoinInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private InetAddress address;
    private long initialSequenceNumber;

    public SequencerJoinInfo(InetAddress address, long initialSequenceNumber) {
        this.address = address;
        this.initialSequenceNumber = initialSequenceNumber;
    }

    public InetAddress getAddress() {
        return address;
    }

    public long getInitialSequenceNumber() {
        return initialSequenceNumber;
    }
}
