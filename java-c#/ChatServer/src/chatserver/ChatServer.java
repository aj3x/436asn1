package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    
     public ChatServer(String name, int port){
         try{
             this.name = name;
             this.serverSocket = new ServerSocket(port);
             this.isrunning = true;
             new Thread(this).start();
         }
         catch(IOException e){
             System.err.println(e.getMessage());
         }
     }
    
    @Override
    public void run() {
        
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ChatServer x = new ChatServer("lol",8888);
    }
}
