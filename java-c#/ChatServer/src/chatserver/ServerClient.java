package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author aj3x
 */
public class ServerClient implements Runnable{
    boolean clientConnected = false;
    String username;
    Socket client;
    private BufferedReader buf;
    ChatServer server;
    
    /**
     * Client attached to server class
     * @param serv Server that client is attached to
     * @param cl client socket to be attached to channel
     * @param user Username
     */
    ServerClient(ChatServer serv, Socket cl, String user){
        this.server = serv;
        this.client = cl;
        this.username = user;
        this.clientConnected = true;
    }
    
    
    @Override
    public void run() {
        try{
            while(clientConnected){
                buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String temp = buf.readLine();

                //is string command?
                String msg = buf.readLine();

                // sent 
                if(msg==null){
                    break;
                }

                if (msg.length() != 0){
                    server.clientMsg(msg, username);
                }
                
            }
        }catch(IOException e){
            server.closeClient(username);
        }
        server.closeClient(username);
        server.sendMessage(username+" left the chat.\n",true);
        
    }
    
    private void close() throws IOException{
        this.buf.close();
        this.client.close();
    }

    protected void exit() throws IOException{
        this.clientConnected = false;
        this.close();
    }
    
}
