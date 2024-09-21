package Proxy;

import java.net.*;
import java.io.*;

/**
 *Class that manages the browser petitions to the proxy
 * 
 */
public class Server{
    private ServerSocket server;
    private InetSocketAddress address;
    
    public Server(String ip, int port) throws IOException{
        address = new InetSocketAddress(ip, port);
        server = new ServerSocket(port);
        server.setReuseAddress(true);
        //server.bind(address);
    }
    
    public void open() throws IOException{
     while (true){
            Socket user = server.accept();
            System.out.println("Connection from " + user.getLocalSocketAddress() + " has "
                    + "been established.");
            
            //Using threads to implement simultaneous connections
            Client client = new Client(user);
            new Thread(client).start();
        }   
    }
}
