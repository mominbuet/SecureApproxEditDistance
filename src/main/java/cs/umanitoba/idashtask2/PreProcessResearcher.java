/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.idashtask2;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import util.ConfigParser;

/**
 *
 * @author user
 */
public class PreProcessResearcher {

    public static void main(String[] args) throws IOException {
        ConfigParser config = new ConfigParser("Config.conf");
        String serverName = args[0];
        int port = config.getInt("Port") + 1;

        System.out.println("Connecting to " + serverName + " on port " + port);
        Socket client = new Socket();
        client.connect(new InetSocketAddress(serverName, port), 5 * 1000);
        System.out.println("Just connected to " + client.getRemoteSocketAddress());
        InputStream inFromServer = client.getInputStream();
        DataInputStream in = new DataInputStream(inFromServer);
        String[] incoming = in.readUTF().split(",");
        int lowest = Integer.parseInt(incoming[0]);
        int datasize = Integer.parseInt(incoming[1]);
        
        PrintWriter printWriter = new PrintWriter(new File("SequenceSizeClient.txt"));
        printWriter.println(lowest);
        printWriter.println(datasize);
        printWriter.flush();
        printWriter.close();
//        sc.close();
        System.out.println("wrote SequenceSizeClient.txt");
    }
}
