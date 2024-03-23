import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Group.GroupException {
        String name = "Tester";
        int nUsers = 4;
        int nMessages = 512;

        try {
            List<Group> groupList = new ArrayList<>();
            // Creating groups and adding them to the list
            for (int i = 1; i < (nUsers + 1); i++) {
                Group group = new Group("240.0.0.0", (name + i));
                groupList.add(group);
            }

            System.out.println("\nRunning test ...\n");
            // Running stress test for each group
            for (Group group : groupList) {
                Thread thread = new Thread(() -> {
                    try {
                        // Sending messages for stress test
                        for (int j = 1; j < (nMessages + 1); j++) {
                            String message = "Test message number " + j;
                            group.send(message);
                            System.out.println(group.name + ": " + message);
                        }
                    } catch (IOException | Group.GroupException e) {
                        // Handling exceptions during stress test
                        System.out.println("Error during stress test");
                        e.printStackTrace();
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            // Handling IO exceptions during stress test setup
            System.out.println("IOError during stress test");
            e.printStackTrace();
        }
    }
}