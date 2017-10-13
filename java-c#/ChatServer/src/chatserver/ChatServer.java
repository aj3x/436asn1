package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 *
 * @author aj3x
 */
public class ChatServer implements Runnable{
    
    private String name;
    private boolean isrunning;
    
    private ServerSocket serverSocket;
    private Socket clientSockets;

    /**
     * 
     * @param name
     * @param port 
     */
     public ChatServer(String name, int port){
         try{
             this.name = name;
             this.serverSocket = new ServerSocket(port);
             this.isrunning = true;
             new Thread(this).start();
             
         }
         catch(IOException e){
             System.err.println("Exception on server start");
             e.printStackTrace();
         }
     }
    
     private BufferedReader buf;
     
    @Override
    public void run() {
        while(isrunning){
            try{
                this.clientSockets = serverSocket.accept();
                //System.out.println(this.clientSockets.getInetAddress());
                
                buf = new BufferedReader(new InputStreamReader(clientSockets.getInputStream()));
                
                
                buf.readLine();
                //System.out.println("READER:"+buf.readLine());
                String username = buf.readLine();
                System.out.println(username + " entered the chat.");
                boolean clientConnected = true;
                try{
                while(clientConnected){
                    if(buf.readLine().equals("/exit")){
                        clientConnected = false;
                    }
                    System.out.println(username + ": " +buf.readLine());
                    
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
