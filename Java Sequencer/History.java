import java.io.*;
import java.util.LinkedList;

public class History 
{
    private FileWriter fileWriter;
    private BufferedReader fileReader;
    private static final int MAX_ENTRIES = 1024;
    private int entryCount = 0;

    // Initialize the history file by creating it and opening it in append mode
    public History() throws IOException 
    {
        File file = new File("backup.txt");
        file.delete(); // Delete the existing file if it exists
        file.createNewFile();
        fileWriter = new FileWriter(file, true);
        fileReader = new BufferedReader(new FileReader(file));
    }

    // Message format is "sender|message|sequence"
    // Write a message to the history file
    public void writeMessage(String sender, long sequence, byte[] message) throws IOException 
    {
        if (entryCount >= MAX_ENTRIES) 
        {
            removeEntries(20); // Remove the oldest 20 entries
            System.out.println("Deleting the oldest " + 20 + " records.");
        }

        String msg = new String(message, "UTF-8");
        fileWriter.write(msg + "|" + String.valueOf(sequence) + "\n");
        fileWriter.flush();
        entryCount++;
    }

    // Read the history file and retrieve a requested message based on sender and sequence
    public byte[] readMessage(String sender, long sequence) throws IOException 
    {
        String line;
        while ((line = fileReader.readLine()) != null) 
        {
            String[] parts = line.split("\\|"); 
            String messageSender = parts[0].trim(); // trim removes any leading or trailing whitespace from string
            long messageSequence = Long.parseLong(parts[1].trim());
            if (messageSender.equals(sender) && messageSequence == sequence) 
            {
                String messageString = parts[2].trim();
                return messageString.getBytes();
            }
        }
        return null; // If message with sender and sequence is not found
    }

    private void removeEntries(int count) throws IOException 
    {
        LinkedList<String> lines = new LinkedList<>();
        String line;
        while ((line = fileReader.readLine()) != null) 
        {
            lines.add(line);
        }

        // Remove the specified number of oldest entries
        for (int i = 0; i < count && !lines.isEmpty(); i++) 
        {
            lines.removeFirst();
        }

        // Rewrite the remaining lines back to the file
        fileWriter.close();
        fileWriter = new FileWriter("backup.txt");
        for (String entry : lines) 
        {
            fileWriter.write(entry + "\n");
        }
        fileWriter.flush();
        entryCount -= count;
    }

    // Close the opened file
    public void closeFile() throws IOException 
    {
        if (fileWriter != null) 
        {
            fileWriter.close();
        }
    }
} 
