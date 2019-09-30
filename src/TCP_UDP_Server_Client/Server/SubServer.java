package TCP_UDP_Server_Client.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SubServer {
    static String prefix = ".";
    public static void main(String[] args){
        try {
            System.out.println("SubServer started");
            Socket socket = new Socket("0:0:0:0:0:0:0:1", 9485);
            ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());

            outputToServer.writeObject("SubsServer");

            while (true){
                outputToServer.writeObject(execute_command((String) inputFromServer.readObject()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String execute_command(String command) {
        if (command.equals(prefix + "hi")){
            return "hi";
        }else if (command.equals(prefix + "stop")){
            System.exit(0);
        }
        return null;
    }
}
