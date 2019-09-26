package TCP_UDP_Server_Client.Server;

import java.io.*;
import java.net.Socket;

public class ServerSession extends Thread
{
    private Socket socket;
    private ObjectInputStream inputFromClient;
    private ObjectOutputStream outputToClient;
    private String prefix = ".";

    public ServerSession(Socket socket) {
        try {
            this.socket = socket;
            this.outputToClient = new ObjectOutputStream(socket.getOutputStream());
            this.inputFromClient = new ObjectInputStream(socket.getInputStream());
            this.start();
        } catch (IOException e) {
            System.err.println("IOExeption: Could not create Reader, Writer or could not start Thread");
        }
    }

    @Override
    public void run() {
        super.run();
        while (true){
            try {
                outputToClient.writeObject(new String("Im the server"));
                String s = (String)inputFromClient.readObject();
                System.out.println(s);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
