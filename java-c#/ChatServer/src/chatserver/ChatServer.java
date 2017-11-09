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
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
/**
 *
 * @author aj3x
 */
public class ChatServer implements Runnable{
    
    public String name;
    private boolean isrunning;
    
    private ServerSocket serverSocket;
    private HashMap<String, ServerClient> clientSockets;
    private HashMap<String, ChatRoom> rooms;
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
            this.clientSockets = new HashMap<>();
            this.rooms = new HashMap<>();
            new Thread(this).start();
            
            
            System.out.println("Boot complete on port: "+ port );
        }
        catch(IOException e){
            System.err.println("Exception on server start");
            e.printStackTrace();
        }
    }
    
    
 
    
     
     private BufferedReader buf;
     private PrintWriter out;
     
    @Override
    public void run() {
        String username = "";
        while(isrunning){
            try{
                Socket tempClientSocket;
                tempClientSocket = serverSocket.accept();
                buf = new BufferedReader(new InputStreamReader(tempClientSocket.getInputStream()));
                out = new PrintWriter(tempClientSocket.getOutputStream(), true);
                
                
                //tell client server greeting
                this.sendMessageTo(tempClientSocket, this.serverGreeting);
                
                this.checkHeaderClient(tempClientSocket, buf.readLine());
                //ask for username
                username = buf.readLine();
                
                // validate username
                username = this.validateUsername(username);
                //received exception causing username: kill client
                
                
                //Make new client thread
                ServerClient tClient = new ServerClient(this, tempClientSocket, username);
                
                if(tempClientSocket.isConnected())
                    this.clientSockets.put(username, tClient);
                else
                    break;
                
                //output to new client previous chat messages
                //System.out.print(serverString);
                String toSend = chatLog;
                this.sendMessageTo(tempClientSocket, toSend);
                
                
                //start client thread
                Thread thread = new Thread(tClient);
                
                thread.start();
                
                //User has now completed connection with data: display
                this.roomMsg(username + " entered the chat.\n",this.name);
            }
            catch(IOException e){
                System.err.println("Exception on run");
                e.printStackTrace();
            }
            
        }
    }
    
    
    /**
     * Say to all clients on server message and add message to chat log
     * @param msg to send
     */
    protected void sendMessage(String msg){
        this.chatLog += msg;
        for (ServerClient c : this.clientSockets.values()){
            sendMessageTo(c.client,msg);
        }
    }
    /**
     * Say to all clients on server message and add message to chat log
     * @param msg to send
     * @param printToConsole prints on console if true
     */
    protected void sendMessage(String msg, boolean printToConsole){
        if (printToConsole)
            System.out.print(msg);
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
            if(client.isConnected())
                client.getOutputStream().write(msgToSend.getBytes(),0,msgToSend.length());
        }catch(IOException e){
            System.err.println("Couldn't send messge");
            e.printStackTrace();
        }
    }
    private void sendMessageTo(String client, String msgToSend){
        sendMessageTo(this.clientSockets.get(client).client,msgToSend,true);
    }
    
    private void sendMessageTo(Socket client, String msgToSend, boolean printToConsole){
        if(printToConsole)
            System.out.println("Told " + msgToSend);
        this.sendMessageTo(client, msgToSend);
    }
    
    /**
     * Send private message to client
     * @param client
     * @param msgToSend 
     */
    private void tell(String client, String msgToSend){
        this.sendMessageTo(client, msgToSend);
    }
    private void tell(String client, String msgToSend, boolean printToConsole){
        if(printToConsole)
            System.out.println("Server told "+client+":"+msgToSend);
        this.tell(client, msgToSend);
    }
    /**
     * Send private message to both clients from user 'from'
     * @param from
     * @param client
     * @param msgToSend 
     */
    public void tell(String from, String client, String msgToSend){
        this.tell(from, "whispered to "+client+":"+msgToSend);
        this.tell(client, from+" whispers:"+msgToSend);
    }
    
    
    /**
     * 
     * @param msg
     * @param username 
     */
    public void clientMsg(String msg, String username){
        if(msg.charAt(0)=='/'){
            msg = msg.substring(1);
            if (msg.startsWith("join")){
                String temp = msg;
                String[]arr = temp.split(" ");
                this.joinRoom(username, arr[1]);
            }else if (msg.startsWith("create")){
                String temp = msg;
                String[]arr = temp.split(" ");
                this.createRoom(username, arr[1]);
            }else{
                switch (msg){
                    case "exit": 
                        this.closeClient(username);break;
                    case "list": this.tell(username, this.listClients());break;//list clients currently in server
                    //case "leave": break;//leave this chat and go to main chat
                    default: this.tell(username, "\\"+msg+" is an invalid command\n");
                        break;
                }
            }
        }else{
            String userOut = msg+"\n";
            String serverOut = username + ": " + userOut;
            System.out.print(serverOut);//tell all clients

            this.roomMsg(serverOut,this.clientSockets.get(username).room);


        }
    }
    
    /**
     * Checks if client has the correct header
     * if not close their connection
     * @param client
     * @param head 
     */
    private void checkHeaderClient(Socket client, String head){
        if(head == null || !head.equals("GET / HTTP/1.0")){
            System.err.println("Invalid header");
            this.closeClient(client);
        }
    }
    
    /**
     * Takes in a username and removes not allowed characters.
     * Replaces input with "user" if completely invalid
     * If username already exists add a digit to the end
     * @param username proposed username
     * @return valid username string
     */
    private String validateUsername(String username){
        if (username == null){
            username = "user";
        }
        username = username.trim();
        username = username.replaceAll(" ", "");
        username = username.replaceAll("\r\n", "");
        username = username.replaceAll("\n", "");
        if(username == "" || username.length()<1){
            username = "user";
        }
        if (this.clientSockets.containsKey(username)){
            int i=0;
            while(this.clientSockets.containsKey(username+i)){
                i++;
            }
            username += i;
            
        }
        return username;
    }
    
    /**
     * Takes formatted message and sends it to users in specified room
     * @param msg 
     * @param room 
     */
    private void roomMsg(String msg, String room){
        
        if(room == this.name){
            this.chatLog += msg;
        }else{
            this.rooms.get(room).chatLog += msg;
        }
        for (ServerClient c : this.clientSockets.values()){
            if(c.room.equals(room))
                sendMessageTo(c.client,msg);
        }
    }
    /**
     * Makes a list of all clients in server
     * @return list of clients
     */
    private String listClients(){
        String rStr = "clients{";
        for (String c : this.clientSockets.keySet()){
            rStr += c+", ";
        }
        rStr = rStr.substring(0, rStr.length()-2);
        return rStr+"}\n";
    }
    
    protected boolean createRoom(String username, String room){
        if(this.rooms.containsKey(room) || room.equals("")){
            this.tell(name, "Failed to create room."+"\n");
            return false;
        }else{
            this.rooms.put(room, new ChatRoom());
            this.tell(username, "Created new room: "+room+"\n");
            return true;
        }
    }
    protected boolean joinRoom(String username, String room){
        if(this.rooms.containsKey(room) || room.equals("")){
            this.clientSockets.get(username).room = room;
            
            this.tell(username, this.rooms.get(room).chatLog);
            this.roomMsg(username+" joined the server.\n", room);
            return true;
        }else{
            this.tell(username, "Failed to join room.\n");
            return false;
        }
    }
    protected void leaveRoom(String user,String room){
        
    }
    protected void deleteRoom(String room){
        ChatRoom r = this.rooms.get(room);
        r.close();
    }
    
    private void closeClient(ServerClient client){
        try{
            //this.sendMessageTo(client,"Disconnected from server.\n");
            client.exit();
        }catch (IOException e){
            System.err.println("disconnect error");
        }catch (NullPointerException e){
        } 
    }
    private void closeClient(Socket client){
        try{
            //this.sendMessageTo(client,"Disconnected from server.\n");
            client.close();
        }catch (IOException e){
            System.err.println("disconnect error");
        }catch (NullPointerException e){
        } 
    }
    protected void closeClient(String clientIndex){
        this.closeClient(clientSockets.get(clientIndex));
        this.clientSockets.remove(clientIndex);
    }
    private void closeClient(String clientIndex,String reason){
        this.tell(clientIndex, reason);
        this.closeClient(clientSockets.get(clientIndex));
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
                case "hello": x.sendMessage("Hello World\n");break;
                case "list": System.out.println(x.listClients());break;
                case "tell": x.tell(arr[1],temp.substring(temp.indexOf(arr[1])));break;
                case "say": x.sendMessage("Server:"+temp.substring(4)+"\n");break;
                case "kick": x.closeClient(arr[1], "Kicked by server");break;
                
            }
        }
    }
}
