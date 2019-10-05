package TCP_UDP_Server_Client.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDP_Client extends Thread {
    private int port;

    public UDP_Client(int port){
        this.port = port;
        this.start();
    }

    public void run() {
        super.run();
        try {
            DatagramSocket datagramSocket = new DatagramSocket(port); //Open UDP connection on the given port
            byte[] data = new byte[1024]; //where data will be saved
            DatagramPacket packet = new DatagramPacket(data, data.length); //wait for a package that has a byte array as data with the length given
            while (true){
                datagramSocket.receive(packet); //Wait for message
                System.out.println(new String(packet.getData())); //print message
            }
        } catch (SocketException e) {
            System.exit(0); //Close the client if connection is down
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
