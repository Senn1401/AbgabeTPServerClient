package TCP_UDP_Server_Client.Client;

import TCP_UDP_Server_Client.Server.ServerSession;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class UDP_Client extends Thread {
    private String username;
    private ArrayList<ServerSession> threadList;
    public UDP_Client(String username){
        System.out.println("UDP Client started");
        this.username = username;
        //this.threadList = threadList;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        try {
            DatagramSocket udpSocket = new DatagramSocket();
            byte[] udpData = new byte[1024];
            DatagramPacket datagramPackage = new DatagramPacket(udpData, udpData.length, InetAddress.getByName("localhost"), 9485);
            udpSocket.receive(datagramPackage);
            System.out.println(new String(datagramPackage.getData()));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
