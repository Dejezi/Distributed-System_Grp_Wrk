import java.rmi.*;
import java.rmi.registry.*;

public class ClientServer { 
    public static void main(String args[]){
		try{
			SequencerImpl implobj = new SequencerImpl("Sequencer");
			LocateRegistry.createRegistry(1800);
			Naming.rebind("rmi://localhost:1800"+"/seq", implobj);
            System.out.println("\nServer initailized");
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
} 