package TCP_UDP_Server_Client.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainServer {
    private static ServerSocket serverSocket;
    private static ArrayList<ServerSession> threadList = new ArrayList<>();
    private static ArrayList<ServerSession> connectedUser = new ArrayList<>();

    public MainServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("IOExeption: Could not start Server on this port");
        }
    }

    public static void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server started");
        while (true){
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New user started");
                ServerSession user = new ServerSession(socket, threadList, connectedUser);
                threadList.add(user);
                connectedUser.add(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
