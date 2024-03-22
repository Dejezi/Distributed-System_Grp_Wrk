import java.io.*;

@SuppressWarnings("unused")
public class SequencerException extends Exception implements Serializable
{
    public SequencerException(String s)
    {
        super(s);
    }
}