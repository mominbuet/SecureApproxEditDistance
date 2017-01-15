/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.idashtask2;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class PreProcessServer extends Thread {
    
    static final int timeout = 5 * 60 * 1000;//5min
    private ServerSocket serverSocket;
    int lowest, datasize;
    
    public PreProcessServer(int port, int lowest, int datasize) {
        try {
//            Scanner sc = new Scanner(new File("SequenceSizeOwner.txt"));
            this.lowest = lowest;
            this.datasize = datasize;
//            sc.close();
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeout);
            
        } catch (IOException ex) {
            System.out.println("Server Socket Creation Error " + ex.getMessage());
        }
    }
    
    public void run() {
        while (true) {
            try {
//                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();
                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF(lowest + "," + datasize);
                System.exit(0);
            } catch (IOException ex) {
                System.out.println("Socket error " + ex.getMessage());
            }
            
        }
    }
}
