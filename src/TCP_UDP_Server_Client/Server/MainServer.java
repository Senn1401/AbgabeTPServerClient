package TCP_UDP_Server_Client.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainServer {
    private ServerSocket serverSocket;
    private ArrayList<ServerSession> threadList = new ArrayList<>();

    public MainServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("IOExeption: Could not start Server on this port");
        }
    }

    public void start() {
        System.out.println("Server started");
        while (true){
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New user started");
                threadList.add(new ServerSession(socket, threadList));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
