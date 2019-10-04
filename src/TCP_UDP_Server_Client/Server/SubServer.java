package TCP_UDP_Server_Client.Server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SubServer {
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
    private static String getFiles(String command) { //return list of all files and subdirs in a given dir
        String[] path = command.split(" ");
        String ret = null;

        try {
            if(path == null || path[1] == " " || path[1] == "") {
                return "No path given";
            }

            File folder = new File(path[1]);
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    ret += "File: " + listOfFiles[i].getName().toString() + "\n";
                } else if (listOfFiles[i].isDirectory()) {
                    ret += "Directory: " + listOfFiles[i].getName().toString() + "\n";
                }
            }
            return ret;
        }
        catch (NullPointerException e) {
            return "No path given";
        }
        catch (IndexOutOfBoundsException e) {
            return "No path given";
        }
    }
    private static String execute_command(String command) {
        if (command.equals(prefix + "hi")) return "hi";
        else if(command.equals(prefix + "files")) return getFiles(command);
        else if(command.equals(prefix + "username"))return System.getProperty("user.name"); //return Username
        else if(command.equals(prefix + "hostname" ))
        if (command.contains(prefix + "hi")) {
            return "hi";
        }
        else if(command.contains(prefix + "files")) {
            return getFiles(command);
        }
        else if(command.contains(prefix + "username"))
            return System.getProperty("user.name"); //return Username
        else if(command.contains(prefix + "hostname" )) {
            try {
                return InetAddress.getLocalHost().getHostName(); //return Hostname
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            finally {
                return "Hostname not found!";
            }
        }
        else if(command.equals(prefix + "loggedInUser")) return System.getProperty("user.home");
        else if (command.equals(prefix + "stop")) System.exit(0);
        return null;
    }
}
