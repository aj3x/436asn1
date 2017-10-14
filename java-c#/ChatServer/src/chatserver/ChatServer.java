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
/**
 *
 * @author aj3x
 */
public class ChatServer implements Runnable{
    
    private String name;
    private boolean isrunning;
    
    private ServerSocket serverSocket;
    private Socket clientSockets;
    
    private String serverString;

    /**
     * 
     * @param name
     * @param port 
    */
    public ChatServer(String name, int port){
        System.out.println("Attempting server boot...");
        try{
            this.serverString = "";
            this.name = name;
            this.serverSocket = new ServerSocket(port);
            this.isrunning = true;
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
        while(isrunning){
            try{
                //acceptClients();
                this.clientSockets = serverSocket.accept();
                //System.out.println(this.clientSockets.getInetAddress());
                
                buf = new BufferedReader(new InputStreamReader(clientSockets.getInputStream()));
                out = new PrintWriter(clientSockets.getOutputStream(), true);
                buf.readLine();
                //System.out.println("READER:"+buf.readLine());
                String username = buf.readLine();
                
                serverString += username + " entered the chat.\n";
                
                //output to new client serverString
                
                out.print("Hello from the server.\n\n");
                //out.close();
                
                System.out.print(serverString);
                String toSend = serverString;
                        byte[] toSendBytes = toSend.getBytes();
                        int toSendLen = toSendBytes.length;
                        //clientSockets.getOutputStream().write(serverString.length());
                        clientSockets.getOutputStream().write(serverString.getBytes());
//                        clientSockets.getOutputStream();
                boolean clientConnected = true;
                try{
                while(clientConnected){
                    String temp = buf.readLine();
                    
                    //is string command?
                    if(temp.charAt(0) == '/'){
                        temp = temp.substring(1);
                        if (temp.startsWith("join")){
                            //join a chat room
                        }else if (temp.startsWith("create")){
                            //create a chat room
                        }else{
                            switch (temp){
                                case "exit": clientConnected = false;break;
                                case "list": break;//list clients currently in server
                                default: //tell client of invalid command
                                    break;
                            }
                        }
                    }else{
                        String serverOut = username + ": " +buf.readLine()+"\n";
                        System.out.print(serverOut);//tell all clients
                        serverString += serverOut;
                        String delete = "hello"+null+null;
                        clientSockets.getOutputStream().write(delete.getBytes(),0,"hello".getBytes().length);
                        
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
                    clientSockets.close();
                }
                clientSockets.close();
                
                System.out.println(username + " left the chat.");
                
                
            }
            catch(IOException e){
                System.err.println("Exception on run");
                e.printStackTrace();
            }
            
            try{
                clientSockets.close();
            }catch(IOException e){
                System.err.println("You are really fucked if this displays.");
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ChatServer x = new ChatServer("localhost",8888);
    }
}
