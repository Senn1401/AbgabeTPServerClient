package TCP_UDP_Server_Client.Client;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("0:0:0:0:0:0:0:1", 9485);
            ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());
            String input;

            while (true){
                outputToServer.writeObject("Client to Server");
                input = (String) inputFromServer.readObject();
                System.out.println(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
