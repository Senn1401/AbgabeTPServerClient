package TCP_UDP_Server_Client.Client;

import TCP_UDP_Server_Client.Server.ServerSession;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            ArrayList<ServerSession> threadlist;
            System.out.println("Client started");
            String prefix = ".";
            Socket socket = new Socket("0:0:0:0:0:0:0:1", 9485);
            ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            String input;
            String username = null;

            outputToServer.writeObject("Client");
            //threadlist = (ArrayList<ServerSession>) inputFromServer.readObject();

            while(!(input = (String) inputFromServer.readObject()).equals("OK")){
                System.out.println(input);
                username = scanner.next();
                outputToServer.writeObject(username);
            }
            System.out.println(input);

            new UDP_Client(username, inputFromServer, outputToServer);

            while (!(input = scanner.next()).equals(prefix + "quit")){
                outputToServer.writeObject(input);
                input = (String) inputFromServer.readObject();
                System.out.println(input);
            }
            outputToServer.writeObject(".quit");
            socket.close();
        } catch (SocketException e){
            System.out.println("No servers are online");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
