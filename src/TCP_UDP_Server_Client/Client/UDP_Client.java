package TCP_UDP_Server_Client.Client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;

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
                String message = new String(packet.getData());
                if (message.toCharArray()[0] == '!'){ //message has fileprefix
                    datagramSocket.receive(packet); //recive package
                    if (new String(packet.getData()).equals("?fail")){
                        continue;
                    }
                    String fileName = (message.split(" "))[2].split("\\\\")[(message.split("\\\\").length) - 1]; //extract filename of file
                    Files.write(new File("C:\\Users\\Senn\\" + fileName.trim()).toPath(), packet.getData()); //write data to file
                    System.out.println("Recived file from " + (message.split(" "))[1]); //output to user
                }else{
                    System.out.println(message); //print message
                }
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
