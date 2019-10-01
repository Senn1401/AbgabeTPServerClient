package TCP_UDP_Server_Client.Client;

import java.io.IOException;
import java.net.*;

public class UDP_Client extends Thread {

    public UDP_Client(){
        this.start();
    }

    @Override
    public void run() {
        super.run();
        try {
            DatagramSocket udpSocket = new DatagramSocket();
            byte[] udpData = new byte[1024];
            DatagramPacket datagramPackage = new DatagramPacket(udpData, udpData.length, InetAddress.getByName("localhost"), 9485);
            System.out.println(new String(datagramPackage.getData()));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
