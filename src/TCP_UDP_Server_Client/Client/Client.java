package TCP_UDP_Server_Client.Client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            System.out.println("Client started");
            String prefix = ".";
            Socket socket = new Socket("0:0:0:0:0:0:0:1", 9485);
            ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            String input;

            outputToServer.writeObject("Client");

            while (!(input = scanner.next()).equals(prefix + "quit")){
                outputToServer.writeObject(input);
                input = (String) inputFromServer.readObject();
                System.out.println(input);
            }
            outputToServer.writeObject(".quit");
            socket.close();
        } catch (SocketException e){
            System.out.println("Server is no longer online");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
