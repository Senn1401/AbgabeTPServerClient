package TCP_UDP_Server_Client.Server;

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
        if(command.contains(prefix + "threads")) return Integer.toString(java.lang.Thread.activeCount());
        else if(command.contains(prefix + "Ram")) return getRamUsage();
        else if (command.contains(prefix + "stop")) System.exit(0);
        return null;
    }
}
