package chatserver;

import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author aj3x
 */
public class ChatRoom {
    
    protected String chatLog;
    private boolean isrunning;
    private HashMap<String, Boolean> clientList;
    private Calendar tO;
    
    ChatRoom(){
        chatLog = "";
        isrunning = true;
        clientList = new HashMap<>();
        updateTimeout();
    }
    
    
    
    private void updateTimeout(){
        tO = Calendar.getInstance();
        tO.add(Calendar.DAY_OF_YEAR, 7);
    }
    private boolean checkTimeout(){
        return tO.after(Calendar.getInstance());
    }
    
    
    
    
    protected void close(){
        
    }
}
