package TCP_UDP_Server_Client.Client;

import TCP_UDP_Server_Client.Server.ServerSession;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

public class UDP_Client extends Thread {
    private String username;
    private ObjectOutputStream outputToServer;
    private ObjectInputStream inputFromServer;

    public UDP_Client(String username, ObjectInputStream inputFromServer, ObjectOutputStream outputToServer){
        this.inputFromServer = inputFromServer;
        this.outputToServer = outputToServer;
        this.username = username;
        this.start();
    }

    public void run() {
        System.out.println("UDP_Client online");
        super.run();
        while (true){

        }

        /*try {
            DatagramSocket udpSocket = new DatagramSocket(9486);
            byte[] udpData = new byte[1024];
            DatagramPacket datagramPackage = new DatagramPacket(udpData, udpData.length);
            udpSocket.receive(datagramPackage);
            System.out.println("recived");
            System.out.println(new String(datagramPackage.getData()));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
