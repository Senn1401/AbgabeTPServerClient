package TCP_UDP_Server_Client.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
                DatagramPacket sendPackage = new DatagramPacket(recivedData, recivedData.length, InetAddress.getByName("localhost"), 9486);

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
