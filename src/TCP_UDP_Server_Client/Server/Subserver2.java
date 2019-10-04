package TCP_UDP_Server_Client.Server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.NumberFormat;

public class Subserver2 {
    static String prefix = ".";
    public static void main(String[] args){
        try {
            System.out.println("SubServer started");
            Socket socket = new Socket("0:0:0:0:0:0:0:1", 9485);
            ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());

            outputToServer.writeObject("SubServer");

            while (true){
                outputToServer.writeObject(execute_command((String) inputFromServer.readObject()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static String getDiskSpace(String command) {
        try {
            String[] driveLetter = command.split(" ");
            driveLetter[1].replace(":", "");
            if(driveLetter[1] == null || driveLetter[1] == " " || driveLetter[1] == "") { //check if any driveletter is given
                return "Drive didn't found";
            }
            File file = new File(driveLetter[1] + ":\\");
            long totalSpace = file.getTotalSpace(); //total disk space in bytes.
            long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
            long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.

            return " === Partition Detail === \n" + "Total size : " + totalSpace /1024 /1024 + " mb \n" + "Space free : " + usableSpace /1024 /1024 + " mb \n" + "Space free : " + freeSpace /1024 /1024 + " mb \n";
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return "Drive didn't found";
        }
        catch (IndexOutOfBoundsException e) {
            return "Drive didn't found";
        }
        //return  "Error in DiskSpace method";
    }
    public static String getRamUsage() { //method to get actual memory usage
        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
        sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");

        return sb.toString();
    }
    private static String execute_command(String command) {
        if (command.equals(prefix + "diskSpace")) return getDiskSpace(command); //return free disk space,
        else if(command.equals(prefix + "threads")) return Integer.toString(java.lang.Thread.activeCount());
        else if(command.equals(prefix + "Ram")) return getRamUsage();
        else if (command.equals(prefix + "stop")) System.exit(0);
        if (command.contains(prefix + "diskSpace")) return getDiskSpace(command); //return free disk space,
        else if(command.contains(prefix + "threads")) return Integer.toString(java.lang.Thread.activeCount());
        else if(command.contains(prefix + "Ram")) return getRamUsage();
        else if (command.contains(prefix + "stop")) System.exit(0);
        else if (command.contains(prefix + "whoAmI")) return "senn";
        return null;
    }
}
