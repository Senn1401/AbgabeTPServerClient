package TCP_UDP_Server_Client.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDP_Server {
    public static void main(String[] args) {
        System.out.println("UDP Server started");
        try {
            DatagramSocket serverSocket = new DatagramSocket(9486);
            byte[] recivedData = new byte[1024];
            DatagramPacket recivedPackage = new DatagramPacket(recivedData, recivedData.length);
            while (true){
                serverSocket.receive(recivedPackage);
                System.out.println(new String(recivedPackage.getData()));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
