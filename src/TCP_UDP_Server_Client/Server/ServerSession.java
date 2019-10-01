package TCP_UDP_Server_Client.Server;

import com.sun.deploy.security.SessionCertStore;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    public String getUsername() {
        return username;
    }

    public String getEntity() {
        return entity;
    }

    public ServerSession(Socket socket, ArrayList<ServerSession> threadList, ArrayList<ServerSession> connectedUsers) {
        try {
            this.threadList = threadList;
            this.socket = socket;
            this.outputToClient = new ObjectOutputStream(socket.getOutputStream());
            this.inputFromClient = new ObjectInputStream(socket.getInputStream());
            this.connectedUsers = connectedUsers;
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
                this.username = (String) inputFromClient.readObject();
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

    private void chat(String command) throws IOException {
        DatagramSocket sendSocket = new DatagramSocket();
        byte[] sendData = command.getBytes();
        InetAddress ip = InetAddress.getByName("localhost");
        DatagramPacket sendPackage = new DatagramPacket(sendData, sendData.length, ip, 9486);
        sendSocket.send(sendPackage);

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
        if (command.contains(commandPrefix + "time")) return time(); //return time
        else if (command.contains(commandPrefix + "date")) return date(); //return date
        else if (command.contains(commandPrefix + "auth")) return auth(command); //return if authenticated or not
        else if (command.equals(commandPrefix + "stop")) stopServer();
        else if (command.equals(commandPrefix + "users")) users(); //return all users
        else if (command.toCharArray()[0] == chatPrefix.toCharArray()[0]) chat(command);
        //If command was not found search on subserver for it
        return executeOnSubServer(command);
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
        for(ServerSession i : connectedUsers){
            if (i.getEntity().equals("Client")){
                s += i.getUsername() + "\n";
            }
        }
        return s;
    }
    private String executeOnSubServer(String command) throws IOException, ClassNotFoundException {
        for (ServerSession i : threadList){         //go through all servers and search for command
            if (i.getEntity().equals("SubsServer")){
                i.outputToClient.writeObject(command);
                if (command.equals(".stop")) break; //If provided command is .stop shut down subserver
                String res = (String) i.inputFromClient.readObject();
                System.out.println(res);
                if (!res.equals(null)){             //If command was found return the result
                    return res;
                }
            }
        }
        return null;
    }
}
