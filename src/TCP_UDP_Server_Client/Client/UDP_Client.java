package TCP_UDP_Server_Client.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDP_Client extends Thread{
    private DatagramSocket udpSocket;
    private byte[] recivedData = new byte[1024];
    private byte[] sendData = new byte[1024];
    {
        try {
            udpSocket = new DatagramSocket(9486);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public UDP_Client(){
        this.start();
    }

    @Override
    public void run() {
        super.run();
        while (true){
            try {
                DatagramPacket recivedPackage = new DatagramPacket(recivedData, recivedData.length);
                udpSocket.receive(recivedPackage);
                String string = new String(recivedPackage.getData());
                System.out.println("RECIVED: " + string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
