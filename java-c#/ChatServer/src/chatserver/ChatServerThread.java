/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author aj3x
 */
public class ChatServerThread extends Thread implements Runnable{
    private Socket socket;
    
    public ChatServerThread(Socket s){
        this.socket = s;
    }
    
}
