package TCP_UDP_Server_Client;

import TCP_UDP_Server_Client.Server.MainServer;
import TCP_UDP_Server_Client.Server.UDP_Server;

public class Main {
    public static void main(String[] args) {
        MainServer.start(9485);
    }
}
