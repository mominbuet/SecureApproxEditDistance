/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.idash2.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author azizmma
 */
public class ServerDataOwner extends Thread {

    /**
     * @param args the command line arguments
     */
    Map<Integer, String> data;
    static final int timeout = 5 * 60 * 1000;//5min
    private ServerSocket serverSocket;
//    String dataLength = "";
    public int querylength = 0;
    int lowest = 0;
    
    public int getQuerylength() {
        return querylength;
    }
    
    public ServerDataOwner(int port, Map<Integer, String> data, int lowest) throws IOException {
        this.data = data;
        this.lowest = lowest;
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(timeout);
//        for (Map.Entry<Integer, String> entrySet : data.entrySet()) {
//            dataLength += entrySet.getKey() + ":" + entrySet.getValue().length() + ",";
//        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();
                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());
                String input = in.readUTF();
                JsonObject jsonObject = Json.createReader(new StringReader(input)).readObject();
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                querylength = jsonObject.getInt("query_length");
                if (querylength < lowest) {
                    lowest = querylength;
                    
                } else {
                    querylength = lowest;
                }
                out.writeUTF(lowest + ":" + data.size());
                
            } catch (Exception ex) {
                System.out.println("exception " + ex.getMessage());
            }
        }
    }
}
