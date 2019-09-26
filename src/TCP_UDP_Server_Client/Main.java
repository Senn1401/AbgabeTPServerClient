package TCP_UDP_Server_Client;

import TCP_UDP_Server_Client.Server.MainServer;

public class Main {
    public static void main(String[] args) {
        MainServer mainServer = new MainServer(9485);
        mainServer.start();
    }
}
