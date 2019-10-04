package TCP_UDP_Server_Client.Server;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerSession extends Thread
{
    private Socket socket;
    private String commandPrefix = ".";
    private String chatPrefix = "@";
    private ArrayList<ServerSession> threadList;
    private ArrayList<ServerSession> connectedUsers;
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
            System.err.println("IOExeption: Could not create Reader, Writer or could not start Thread");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        super.run();
        try {
            this.entity = (String) inputFromClient.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (entity.equals("Client")){
            //Get Username
            try {
                outputToClient.writeObject("Whats your username?");
                while (true){
                    String tempUsername = (String) inputFromClient.readObject();
                    if (!tempUsername.contains(" ")){
                        this.username = tempUsername;
                        outputToClient.writeObject("OK");
                        break;
                    }
                    outputToClient.writeObject("Username can not contain a space");
                }
                outputToClient.writeObject(port);
                map.put(username, port);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            //Wait for commands
            while (true){
                try {
                    String command = (String) inputFromClient.readObject();
                    if (command.equals(commandPrefix + "quit")){ //If User closes its session colse the server session
                        return;
                    }else {
                        outputToClient.writeObject(executeCommand(command));
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
        String[] content = command.split(" ");
        DatagramSocket sendSocket = new DatagramSocket();
        String data = username + ": ";
        if (content.length < 2){
            return "Please provide a message";
        }
        for (int i = 1; i < content.length; i++){
            data += content[i] + " ";
        }
        byte[] sendData = data.getBytes();
        System.out.println(new String(sendData));
        InetAddress ip = InetAddress.getByName("localhost");
        if (!map.containsKey(content[0].replace("@", ""))){
            return "User not found";
        }
        DatagramPacket sendPackage = new DatagramPacket(sendData, sendData.length, ip, map.get(content[0].replace("@", "")));
        sendSocket.send(sendPackage);
        return "";
    }

    public ObjectInputStream getInputFromClient() {
        return inputFromClient;
    }

    public ObjectOutputStream getOutputToClient() {
        return outputToClient;
    }

    //execute Commands send from user
    private String executeCommand(String command) throws IOException, ClassNotFoundException {
        if (!command.contains(commandPrefix) && !command.contains(chatPrefix)){
            return "Input is not a command";
        }
        if (command.contains(commandPrefix + "time")) return time() + "\n"; //return time
        else if (command.contains(commandPrefix + "date")) return date() + "\n"; //return date
        else if (command.contains(commandPrefix + "auth")) return auth(command) + "\n"; //return if authenticated or not
        else if (command.equals(commandPrefix + "stop")) stopServer();
        else if (command.equals(commandPrefix + "users")) return users(); //return all users
        else if (command.toCharArray()[0] == chatPrefix.toCharArray()[0]) return chat(command);
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
    private String executeOnSubServer(String command) throws IOException, ClassNotFoundException {
        for (ServerSession i : threadList){         //go through all servers and search for command
            if (i.getEntity().equals("SubServer")){
                i.outputToClient.writeObject(command);
                if (command.equals(".stop")) break; //If provided command is .stop shut down subserver
                String res = (String) i.inputFromClient.readObject();
                if (res != null){             //If command was found return the result
                    return res;
                }
            }
        }
        return null;
    }
}
