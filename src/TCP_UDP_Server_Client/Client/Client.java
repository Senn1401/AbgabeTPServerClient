package TCP_UDP_Server_Client.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            String prefix = "."; //the prefix for the commands
            Socket socket = new Socket("localhost", 9485);
            ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in); //User can write onto the System.in (Console)
            scanner.useDelimiter("\n"); //After enter the command is beeing send
            String input;
            String username;

            outputToServer.writeObject("Client"); //Send this as notaition

            while(!(input = (String) inputFromServer.readObject()).equals("OK")){ //Ask for a username until the server says its ok
                System.out.println(input);
                username = scanner.next();
                outputToServer.writeObject(username);
            }
            System.out.println(input);

            //create udp client to listen if something was send to this user
            //Read the port that gives the server to the client
            Thread udp_client = new UDP_Client((Integer) inputFromServer.readObject());

            //read the input of the user until he sends quit
            while (!(input = scanner.next()).equals(prefix + "quit")){
                outputToServer.writeObject(input);
                System.out.print(inputFromServer.readObject());
            }
            outputToServer.writeObject(".quit"); //send quit command to serversession
            udp_client.stop();
            socket.close(); //close connection
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
