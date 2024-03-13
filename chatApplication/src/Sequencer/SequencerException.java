package Sequencer;

import java.io.Serializable;

public class SequencerException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    public SequencerException(String message) {
        super(message);
    }
}
