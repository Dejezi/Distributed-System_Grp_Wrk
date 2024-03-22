import java.rmi.*;
import java.rmi.registry.*;

public class ClientServer { 
    public static void main(String args[])
	{
		try
		{
			SequencerImpl obj = new SequencerImpl("Sequencer");
			LocateRegistry.createRegistry(1919);
			Naming.rebind("rmi://localhost:1919"+"/seq", obj);
            System.out.println("\nServer initailized");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
} 