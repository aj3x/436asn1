package chatserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
/**
 *
 * @author aj3x
 */
public class ChatServer implements Runnable{
    
    private String name;
    private boolean isrunning;
    
    private ServerSocket serverSocket;
    private HashMap<String, Socket> clientSockets;
//    private ArrayList<Socket> clientSockets;
//    private HashMap<String,Integer> clientNames;
    private int numOfClients;
    private String serverGreeting;
    
    private String chatLog;

    /**
     * 
     * @param name
     * @param port 
    */
    public ChatServer(String name, int port){
        System.out.println("Attempting server boot...");
        try{
            serverGreeting = "Greetings from the server!\n\n";
            this.chatLog = "";
            this.name = name;
            this.serverSocket = new ServerSocket(port);
            this.isrunning = true;
            this.numOfClients = 0;
            this.clientSockets = new HashMap<>();
            new Thread(this).start();
            
            
            System.out.println("Boot complete on port: "+ port );
        }
        catch(IOException e){
            System.err.println("Exception on server start");
            e.printStackTrace();
        }
    }
    
    
    public void acceptClients(){
        while(true){
            try{
                Socket socket = serverSocket.accept();
                
            }catch (IOException e){
                System.out.println("Didn't accept client.");
                e.printStackTrace();
            }
        }
    }
 
    
     
     private BufferedReader buf;
     private PrintWriter out;
     
    @Override
    public void run() {
        String username = "";
        Socket tempClientSocket;
        while(isrunning){
            try{
                //acceptClients();
                tempClientSocket = serverSocket.accept();
                //this.clientSockets.put("first", serverSocket.accept());
                //System.out.println(this.clientSockets.getInetAddress());
                //tempClientSocket = this.clientSockets.get("first");
                buf = new BufferedReader(new InputStreamReader(tempClientSocket.getInputStream()));
                out = new PrintWriter(tempClientSocket.getOutputStream(), true);
                
                
                //tell client server greeting
                this.sendMessageTo(tempClientSocket, this.serverGreeting);
                //out.close();
                //clientSockets.setSendBufferSize(18);
                // clientSockets.setReceiveBufferSize(toSend.length());
                //clientSockets.getOutputStream().write(toSend.getBytes(),0,toSend.length());
                
                //client is now connected
                boolean clientConnected = true;
                
                this.checkHeaderClient(tempClientSocket, buf.readLine());
                username = buf.readLine();
                //ask for username if it is unique add it to hashmap
                //else ask again
                username = this.validateUsername(username);
                
                if(tempClientSocket.isConnected())
                    this.clientSockets.put(username, tempClientSocket);
                else
                    break;
                
                //output to new client previous chat messages
                //System.out.print(serverString);
                String toSend = chatLog;
                this.sendMessageTo(tempClientSocket, toSend);
                
                //User has now completed connection with data: display
                this.sendMessage(username + " entered the chat.\n",true);
                
                
                
                //Make new client thread
                try{
                while(clientConnected){
                    String temp = buf.readLine();
                    //is string command?
                    String msg = buf.readLine();
                    if(msg.charAt(0)=='/'){
                        System.out.println("IF");
                        msg = temp.substring(1);
                        if (msg.startsWith("join")){
                            //join a chat room
                        }else if (msg.startsWith("create")){
                            //create a chat room
                        }else{
                            switch (msg){
                                case "exit": clientConnected = false;break;
                                case "list": break;//list clients currently in server
                                case "leave": break;//leave this chat and go to main chat
                                default: this.tell(username, msg+" is an invalid command\n");
                                    break;
                            }
                        }
                        System.out.println("FI");
                    }else{
                        System.out.println("ElSE");
                        this.tell(username, "waiting");
                        //this.checkHeaderClient(tempClientSocket, buf.readLine());
                        String userOut = msg+"\n";
                        String serverOut = username + ": " + userOut;
                        System.out.print(serverOut);//tell all clients
                        
                        this.sendMessage(serverOut);
                        
//                        toSend = "Echo: " + "recieved";
//                        toSendBytes = toSend.getBytes();
//                        toSendLen = toSendBytes.length;
//                        toSendLenBytes = new byte[4];
//                        toSendLenBytes[0] = (byte)(toSendLen & 0xff);
//                        toSendLenBytes[1] = (byte)((toSendLen >> 8) & 0xff);
//                        toSendLenBytes[2] = (byte)((toSendLen >> 16) & 0xff);
//                        toSendLenBytes[3] = (byte)((toSendLen >> 24) & 0xff);
//                        clientSockets.getOutputStream().write(toSendLenBytes);
//                        clientSockets.getOutputStream().write(toSendBytes);
                        
                        
                    }
                }
                }catch(IOException e){
                    this.closeClient(username);
                }
                this.closeClient(username);
                
                System.out.println(username + " left the chat.");
                
                
            }
            catch(IOException e){
                System.err.println("Exception on run");
                e.printStackTrace();
            }
            
            this.closeClient(username);
        }
    }
    
    /**
     * Say to all clients on server message and add message to chat log
     * @param msg to send
     */
    private void sendMessage(String msg){
        this.chatLog += msg;
        for (Socket c : this.clientSockets.values()){
            sendMessageTo(c,msg);
        }
    }
    /**
     * Say to all clients on server message and add message to chat log
     * @param msg to send
     * @param printToConsole prints on console if true
     */
    private void sendMessage(String msg, boolean printToConsole){
        if (printToConsole)
            System.out.println(msg);
        sendMessage(msg);
    }
    
    /**
     * Sends message to client
     * @param client receives message
     * @param msgToSend message to be sent
     */
    private void sendMessageTo(Socket client, String msgToSend){
        try{
            //client.getOutputStream();
            client.getOutputStream().write(msgToSend.getBytes(),0,msgToSend.length());
        }catch(IOException e){
            System.err.println("Couldn't send messge");
            e.printStackTrace();
        }
    }
    private void sendMessageTo(String client, String msgtoSend){
        this.clientSockets.get(client);
    }
    
    private void sendMessageTo(Socket client, String msgToSend, boolean printToConsole){
        if(printToConsole)
            System.out.println("Told " + msgToSend);
        this.sendMessageTo(client, msgToSend);
    }
    
    
    private void tell(String client, String msgToSend){
        this.sendMessageTo(client, "SERVER:"+msgToSend);
    }
    private void tell(String from, String client, String msgToSend){
        this.sendMessageTo(client, from+" whispers:"+msgToSend);
    }
    private void tell(String client, String msgToSend, boolean printToConsole){
        if(printToConsole)
            System.out.println("Server told "+client+":"+msgToSend);
        this.tell(client, msgToSend);
    }
    
    private void checkHeaderClient(Socket client, String head){
        if(!head.equals("GET / HTTP/1.0")){
            System.err.println("Invalid header");
            this.closeClient(client);
        }
    }
    
    private String validateUsername(String username){
        username = username.trim();
        username = username.replaceAll(" ", "");
        username = username.replaceAll("\n", "");
        username = username.replaceAll("\r", "");
        if (this.clientSockets.containsKey(username)){
            int i=0;
            while(this.clientSockets.containsKey(username+i)){
                i++;
            }
            username += i;
            
        }
        return username;
    }
    
    private String listClients(){
        String rStr = "clients{";
        for (String c : this.clientSockets.keySet()){
            rStr += c+", ";
        }
        return rStr+"}\n";
    }
    
    
    private void closeClient(Socket client){
        this.sendMessageTo(client,"Disconnected from server.\n");
        try{
            client.close();
        }catch (IOException e){
            System.err.println("disconnect error");
        }catch (NullPointerException e){
            
        }
        
    }
    private void closeClient(String clientIndex){
        this.closeClient(clientSockets.get(clientIndex));
        this.clientSockets.remove(clientIndex);
    }
    private void closeClient(String clientIndex,String reason){
        this.tell(clientIndex, reason);
        this.closeClient(clientSockets.get(clientIndex));
        this.clientSockets.remove(clientIndex);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ChatServer x = new ChatServer("localhost",8888);
        
        while(true){
            Scanner scan = new Scanner(System.in);
            String temp = scan.nextLine();
            String[]arr = temp.split(" ");
            switch(arr[0]){
                case "print": System.out.println("string{\n"+x.chatLog+"}");break;
                case "hello": x.sendMessage(temp);break;
                case "list": System.out.println(x.listClients());break;
                case "tell": x.tell(arr[1],temp.substring(temp.indexOf(arr[1])));break;
                case "tell2": x.sendMessageTo(arr[1], "test\n");
                case "say": x.sendMessage(":"+temp.substring(4)+"\n");
                    
            }
        }
    }
}
