/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.util.HashMap;

/**
 *
 * @author aj3x
 */
public class ChatRoom {
    
    private String chatLog;
    private boolean isrunning;
    private HashMap<String, Boolean> clientList;
    private ChatServer s;
    
    ChatRoom(ChatServer mainServer){
        chatLog = "";
        isrunning = true;
        clientList = new HashMap<>();
        s = mainServer;
    }
}
