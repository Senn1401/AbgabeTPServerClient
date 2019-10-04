package TCP_UDP_Server_Client.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class UDP_Client extends Thread {
    private String username;
    private ObjectOutputStream outputToServer;
    private ObjectInputStream inputFromServer;
    private int port;

    public UDP_Client(String username, ObjectInputStream inputFromServer, ObjectOutputStream outputToServer, int port){
        this.inputFromServer = inputFromServer;
        this.outputToServer = outputToServer;
        this.username = username;
        this.port = port;
        this.start();
    }

    public void run() {
        super.run();
        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            while (true){
                datagramSocket.receive(packet);
                System.out.println(new String(packet.getData()));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
