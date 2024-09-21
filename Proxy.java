package Proxy;

import java.io.*;

public class Proxy {
    private static final String HOST_IP = "127.0.0.1";
    private static final int PORT = 18080;
    
    public static void main(String[] args) throws IOException{
        Server server = new Server(HOST_IP, PORT);
        server.open();
    }
}
