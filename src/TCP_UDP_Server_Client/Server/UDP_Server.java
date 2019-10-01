package TCP_UDP_Server_Client.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class UDP_Server extends Thread{
    ArrayList<ServerSession> threadList;

    public UDP_Server(ArrayList<ServerSession> threadList){
        this.threadList = threadList;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        System.out.println("UDP Server started");
        try {
            DatagramSocket serverSocket = new DatagramSocket(9486);
            byte[] recivedData = new byte[1024];
            DatagramPacket recivedPackage = new DatagramPacket(recivedData, recivedData.length);
            while (true){
                serverSocket.receive(recivedPackage);
                String data = new String(recivedPackage.getData());
                String[] content = data.split(" ");
                String out = "";
                for (int i = 1; i < content.length; i++){
                    out += content[i] + " ";
                }
                System.out.println(out);
                String user = content[0].replace("@", "").toLowerCase();
                for (ServerSession thread : threadList){
                    if (thread.getUsername().toLowerCase().equals(user)){
                        System.out.println("found user");
                    }
                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
