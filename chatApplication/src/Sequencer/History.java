package Sequencer;

import java.util.HashMap;
import java.util.Map;

public class History {
    private Map<String, Long> history;

    public History() {
        history = new HashMap<>();
    }

    public synchronized void addMessage(String sender, long sequenceNumber) {
        history.put(sender, sequenceNumber);
    }

    public synchronized long getNextSequenceNumber() {
        long nextSequenceNumber = 0;

        // Find the maximum sequence number in the history
        for (long sequenceNumber : history.values()) {
            nextSequenceNumber = Math.max(nextSequenceNumber, sequenceNumber);
        }

        // Increment the maximum sequence number to get the next sequence number
        return nextSequenceNumber + 1;
    }
}
