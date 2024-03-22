import java.io.*;

@SuppressWarnings("unused")
public class SequencerException extends Exception implements Serializable {
    // constructor for a SequencerException with a specified message
    public SequencerException(String s) {
        super(s);
    }
}
