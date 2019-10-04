package TCP_UDP_Server_Client.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainServer {
    private static ServerSocket serverSocket;
    private static ArrayList<ServerSession> threadList = new ArrayList<>();
    private static HashMap<String, Integer> map = new HashMap<>();
    private static Integer port;

    public MainServer(Integer port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.port = port;
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
                System.out.println("New session started");
                port++;
                ServerSession user = new ServerSession(socket, threadList, map, port);
                threadList.add(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
