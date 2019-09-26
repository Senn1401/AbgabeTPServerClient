package TCP_UDP_Server_Client.Server;

import com.sun.deploy.security.SessionCertStore;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ServerSession extends Thread
{
    private Socket socket;
    private String prefix = ".";
    private ArrayList<ServerSession> threadList;
    private ObjectInputStream inputFromClient;
    private ObjectOutputStream outputToClient;
    private static String password = "Kennwort0";
    private boolean auth;
    private String entity;

    public String getEntity() {
        return entity;
    }

    public ServerSession(Socket socket, ArrayList<ServerSession> threadList) {
        try {
            this.threadList = threadList;
            this.socket = socket;
            this.outputToClient = new ObjectOutputStream(socket.getOutputStream());
            this.inputFromClient = new ObjectInputStream(socket.getInputStream());
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
            while (true){
                try {
                    String command = (String) inputFromClient.readObject();
                    if (command.equals(prefix + "quit")){
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
        }else{
            while (true){
                try {
                    String in = (String) inputFromClient.readObject();
                    System.out.println(in);
                    outputToClient.writeObject(in);
                    in = (String) inputFromClient.readObject();
                    System.out.println(in);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ObjectInputStream getInputFromClient() {
        return inputFromClient;
    }

    public ObjectOutputStream getOutputToClient() {
        return outputToClient;
    }

    private String executeCommand(String command) throws IOException, ClassNotFoundException {
        if (!command.contains(prefix)){
            return "Input is not a command";
        }
        if (command.contains(prefix + "time")){
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(cal.getTime());
        }else if (command.contains(prefix + "date")){
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            return dateFormat.format(date);
        }else if (command.contains(prefix + "auth")){
            String[] splitted = command.split(" ");
            if (splitted.length < 2){
                return "No password provided";
            }
            if (splitted[1].equals(password)){
                auth = true;
                return "Authenticated";
            }else{
                return "Password was incorrect";
            }
        }else if (command.equals(prefix + "stop")){
            if (auth){
                System.exit(0);
            }else {
                return "Permission denied";
            }
        }
        for (ServerSession i : threadList){
            if (i.getEntity().equals("SubsServer")){
                ObjectOutputStream out = new ObjectOutputStream(i.getOutputToClient());
                out.writeObject("test");
            }
        }
        return "null";
    }
}
