import java.io.*;
import java.util.LinkedList;

public class History {
    private FileWriter fileWriter;
    private BufferedReader fileReader; 
    private static final int MAX_ENTRIES = 1024; // Maximum number of entries allowed in the history
    private int entryCount = 0;

    // Initialization of the history file
    public History() throws IOException {
        File file = new File("backup.txt");
        file.delete();
        file.createNewFile();
        fileWriter = new FileWriter(file, true);
        fileReader = new BufferedReader(new FileReader(file));
    }

    public void writeMessage(String sender, long sequence, byte[] message) throws IOException {
        if (entryCount >= MAX_ENTRIES) {
            int c = 20;
            removeEntries(c); // Removes the oldest entries when maximum is reached
            System.out.println("Oldest " + c + " records deleted.");
        }

        String msg = new String(message, "UTF-8");
        fileWriter.write(msg + "|" + String.valueOf(sequence) + "\n");
        fileWriter.flush();
        entryCount++;
    }

    public byte[] readMessage(String sender, long sequence) throws IOException {
        String line;
        while ((line = fileReader.readLine()) != null) {
            String[] parts = line.split("\\|");
            String messageSender = parts[0].trim();
            long messageSequence = Long.parseLong(parts[1].trim());
            if (messageSender.equals(sender) && messageSequence == sequence) {
                String messageString = parts[2].trim();
                return messageString.getBytes();
            }
        }
        return null; 
    }

    private void removeEntries(int count) throws IOException {
        LinkedList<String> lines = new LinkedList<>();
        String line;
        
        // Close the current file reader
        fileReader.close();
        
        // Reopen the file reader to read from the beginning of the file
        fileReader = new BufferedReader(new FileReader("backup.txt"));
    
        // Read lines from the file again
        while ((line = fileReader.readLine()) != null) {
            lines.add(line);
        }
    
        // Remove the specified number of entries from the beginning of the list
        for (int i = 0; i < count && !lines.isEmpty(); i++) {
            lines.removeFirst();
        }
    
        // Close the file writer before opening a new one
        fileWriter.close();
        
        // Open a new file writer to rewrite the file
        fileWriter = new FileWriter("backup.txt");
    
        // Rewrite the file with the remaining entries
        for (String entry : lines) {
            fileWriter.write(entry + "\n");
        }
        fileWriter.flush();
        entryCount -= count;
    }
    

    // Closes the file
    public void closeFile() throws IOException {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
