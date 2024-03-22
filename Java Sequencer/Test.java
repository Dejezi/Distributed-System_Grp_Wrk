import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Group.GroupException 
    {
        String name = "Tester";
        int numUsers = 4;
        int numMessages = 512;

        try
        {           
            List<Group> groups = new ArrayList<>();
            for (int i = 1; i < (numUsers + 1); i++) 
            {
                Group group = new Group("239.0.0.0", (name + i));
                groups.add(group);
            }

            System.out.println("\nRunning test ...\n");             
            for (Group group : groups) 
            {
                Thread thread = new Thread(() -> {
                    try {
                        for (int j = 1; j < (numMessages + 1); j++)
                        {
                            String message = "Test message number " + j;
                            group.send(message);
                            System.out.println(group.name + ": " + message);
                        }
                    } catch (IOException | Group.GroupException e)
                    {
                        System.out.println("Error during stress test");
                        e.printStackTrace();
                    }
                });
                thread.start();
            }
        }
        catch(IOException e) 
        { 
            System.out.println("IOError during stress test"); 
            e.printStackTrace(); 
        } 
    }
}