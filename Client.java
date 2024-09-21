package Proxy;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 *Class that manages the proxy petition to the webpage
 * 
 */
public class Client implements Runnable{
    private Socket client;
    
    public Client(Socket socket) throws IOException{
        client = socket;
    }
    
    @Override
    public void run(){
        try {
            
            System.out.println("Thread created for a new connection.");
            
            //Buffers to read the HTTP request from the browser
            BufferedReader inFromBrowser = new BufferedReader
                (new InputStreamReader(client.getInputStream()));
            BufferedWriter outToBrowser = new BufferedWriter
                (new OutputStreamWriter(client.getOutputStream()));
            
            String header = inFromBrowser.readLine();
            
            System.out.println("Browsers request: " + header);
            
            if(header == null || header.isEmpty()){
                client.close();
                return;
            }
            
            String[] headerList = header.split(" ");
            if(headerList.length < 2){
                client.close();
            }
            
            URI url = new URI(headerList[1]);
            URL target = url.toURL();
            String host = target.getHost();
            int port;
            if(target.getPort() == -1){
                port = 80;
            } else {
                port = target.getPort();
            }
            
            System.out.println("Connecting to: " + host + ":" + port);
            
            
            
            try(Socket web = new Socket(host, port)){
                BufferedWriter outToWeb = new BufferedWriter
                    (new OutputStreamWriter(web.getOutputStream()));
                BufferedReader inFromWeb = new BufferedReader
                    (new InputStreamReader(web.getInputStream()));
                
                
                
                outToWeb.write(header + "\r\n");
                String headerLine;
                while((headerLine = inFromBrowser.readLine()) != null && !headerLine.isEmpty()){
                    System.out.println("HEADERLINE: " + headerLine);
                    outToWeb.write(headerLine + "\r\n");
                }
                outToWeb.write("\r\n");
                outToWeb.flush();
                
                
                //TODO truesize no se cuenta bien
                int trueSize = 0;
                
                ArrayList<String> responseLines = new ArrayList<>();
                
                do{
                    responseLines.add(inFromWeb.readLine());
                }while(responseLines.get(responseLines.size() - 1) != null);
                responseLines.remove(responseLines.size()-1);
                
                boolean onText = false;
                for(int i = 0; i < responseLines.size(); i++){
                    if(onText){
                        responseLines.set(i, responseLines.get(i).replace("Smiley", "Trolly"));
                        responseLines.set(i, responseLines.get(i).replace("Stockholm", "Linköping"));
                        byte[] bytes = responseLines.get(i).getBytes();
                        trueSize += bytes.length;
                    }
                    if(responseLines.get(i).matches("")){
                        onText = true;
                    }
                }
                
                for(int i = 0; i < responseLines.size(); i++){
                    if(responseLines.get(i).contains("Content-Length:"))
                        responseLines.set(i, responseLines.get(i).replace(responseLines.get(i), "Content-Length: " + trueSize));
                    
                    System.out.println("RESPONSELINE: " + responseLines.get(i));
                    outToBrowser.write(responseLines.get(i));
                    outToBrowser.newLine();
                }
                
                
                /*
                String responseLine;
                while((responseLine = inFromWeb.readLine()) != null){
                    System.out.println("RESPONSELINE: " + responseLine);
                    outToBrowser.write(responseLine + "\r\n");
                }
                */
                outToBrowser.flush();
                
                System.out.println("Response sent.");
            } catch (IOException ex){
                System.err.println("Web Connection error: " + ex.getMessage());
            }
            
            client.close();
        } catch (IOException | URISyntaxException ex) {
            System.err.println("Browser Connection error: " + ex.getMessage());
        } 
    }  
}
