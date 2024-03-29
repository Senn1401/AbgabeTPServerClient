package TCP_UDP_Server_Client.Server;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerSession extends Thread
{
    private Socket socket;
    private String commandPrefix = ".";
    private String chatPrefix = "@";
    private String filePrefix = "!";
    private ArrayList<ServerSession> threadList;
    private ObjectInputStream inputFromClient;
    private ObjectOutputStream outputToClient;
    private static String password = "Kennwort0";
    private boolean auth;
    private String entity;
    private String username;
    private HashMap<String, Integer> map;
    private Integer port;

    public String getUsername() {
        return username;
    }

    public String getEntity() {
        return entity;
    }

    public ServerSession(Socket socket, ArrayList<ServerSession> threadList, HashMap<String, Integer> map, Integer port) {
        try {
            this.threadList = threadList;
            this.socket = socket;
            this.outputToClient = new ObjectOutputStream(socket.getOutputStream());
            this.inputFromClient = new ObjectInputStream(socket.getInputStream());
            this.map = map;
            this.port = port;
            this.start();
        } catch (IOException e) {
            System.err.println("IOExeption: Could not create Reader, Writer or start Thread");
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            this.entity = ((String) inputFromClient.readObject()).replace("\r", ""); //get the notation of the connected device
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (entity.equals("Client")){ //if connected device is a client
            //Get Username
            try {
                outputToClient.writeObject("Whats your username?");
                while (true){ //get username of client
                    String username = ((String) inputFromClient.readObject()).replace("\r", "");
                    if (!username.contains(" ")){ //if username has correct format send ok
                        outputToClient.writeObject("OK");
                        break;
                    }
                    outputToClient.writeObject("Username can not contain a space");
                }
                outputToClient.writeObject(port); //send client the port for the udp connection
                map.put(username, port); //create a map of usernames and ports
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            //Wait for commands
            while (true){
                try {
                    String command = ((String) inputFromClient.readObject()).replace("\r", "");
                    if (command.equals(commandPrefix + "quit")){ //If User closes its session colse the server session
                        return;
                    }else {
                        outputToClient.writeObject(executeCommand(command).replace("\r", ""));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String chat(String command) throws IOException {
        byte[] sendData = new byte[1024];
        String[] content = command.split(" ");
        if (content.length < 2){ //if userinput contains no message and oly username
            return "Please provide a message\n";
        }
        if (!map.containsKey(content[0].replace(chatPrefix, ""))){ //get port of user
            return "User not found\n";
        }
        InetAddress ip = InetAddress.getByName("localhost"); //get ip of localhost
        DatagramSocket sendSocket = new DatagramSocket(); //open connection to the destination port
        if (content[1].toCharArray()[0] == filePrefix.toCharArray()[0]){
            File file = new File(content[1].replace(filePrefix, ""));
            if (file.exists()){ //check if file exists
                sendData = Files.readAllBytes(file.toPath()); //read data of file
                byte[] infoBefore = new byte[1024];
                infoBefore = (filePrefix + " " + username + " " + content[1].replace("!", "")).getBytes(); //create informations before sending file
                DatagramPacket sendPacket = new DatagramPacket(infoBefore, infoBefore.length, ip, map.get(content[0].replace("@", "")));
                sendSocket.send(sendPacket); //send informations
                sendPacket = new DatagramPacket(sendData, sendData.length, ip, map.get(content[0].replace("@", "")));
                try{
                    sendSocket.send(sendPacket); //send fie
                }catch (SocketException e){
                    byte[] errData = new byte[1024];
                    errData = "?fail".getBytes();
                    DatagramPacket errPackage = new DatagramPacket(errData, errData.length, ip,  map.get(content[0].replace("@", "")));
                    sendSocket.send(errPackage);
                    return "File is too large to send\n";
                }
            }else{
                return "IOError: File does not exist\n";
            }
        }else{
            String data = username + ": "; //add the username to the message
            for (int i = 1; i < content.length; i++){ //concat the data of the message to string
                data += content[i] + " ";
            }
            sendData = data.getBytes();
            //send content to destination user connection
            DatagramPacket sendPackage = new DatagramPacket(sendData, sendData.length, ip, map.get(content[0].replace("@", "")));
            sendSocket.send(sendPackage);
        }
        return "";
    }

    //execute Commands send from user
    private String executeCommand(String command) throws IOException, ClassNotFoundException {
        String tmp[] = command.split(" ");
        tmp[0] = tmp[0].toLowerCase();
        if (!command.contains(commandPrefix) && !command.contains(chatPrefix)){ //if command not contains valid prefix return
            return "Input is not a command \n";
        }
        if (tmp[0].contains(commandPrefix + "time")) return time() + "\n"; //return time
        else if (tmp[0].contains(commandPrefix + "date")) return date() + "\n"; //return date
        else if (tmp[0].contains(commandPrefix + "auth")) return auth(command) + "\n"; //return if authenticated or not
        else if (tmp[0].equals(commandPrefix + "stop")) stopServer();
        else if (tmp[0].equals(commandPrefix + "users")) return users(); //return all users
        else if (tmp[0].toCharArray()[0] == chatPrefix.toCharArray()[0]) return chat(command);
        //If command was not found search on subserver for it
        return executeOnSubServer(command) + "\n";
    }

    private String time(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    private String date(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String auth(String command){
        String[] splitted = command.split(" ");
        if (splitted.length < 2){
            return "No password provided";
        }
        if (splitted[1].equals(password)){
            auth = true;                    //Set authenticated to true
            return "Authenticated";
        }else{
            return "Password was incorrect";
        }
    }

    public String stopServer(){
        if (auth){
            try {
                executeOnSubServer(".stop");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.exit(0);     //Stop server session
        }else {
            return "Permission denied";
        }
        return null;
    }

    private String users(){
        String s = "";
        for(ServerSession i : threadList){
            if (i.getEntity().equals("Client")){
                s += i.getUsername() + "\n";
            }
        }
        return s;
    }

    //if server has not found the command send it to a subserver
    private String executeOnSubServer(String command) throws IOException, ClassNotFoundException {
        for (ServerSession i : threadList){         //go through all servers and search for command
            if (i.getEntity().equals("SubServer")){
                i.outputToClient.writeObject(command.replace("\r", ""));
                if (command.equals(commandPrefix + "stop")) break; //If provided command is .stop shut down subserver
                String res = ((String) i.inputFromClient.readObject()).replace("\r", "");
                if (res != null){             //If command was found return the result
                    return res;
                }
            }
        }
        return "Command not found";
    }
}
