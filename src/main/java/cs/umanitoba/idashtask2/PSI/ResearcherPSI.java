/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.idashtask2.PSI;

import util.ConfigParser;
import static Utilities.Helpers.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 *
 * @author azizmma
 */
public class ResearcherPSI {

    /**
     * @param args the command line arguments
     */
    public static Map<String, List<Integer>> getQueryShingles(int gramSize, String filename) {
        int index = 0;

        Map<Integer, String> data = new HashMap<>();
        Map<String, List<Integer>> shingleMap = new HashMap<>();
        try {
            File fl = new File(filename);
            Scanner sc = new Scanner(fl);

            while (sc.hasNext()) {
                String next = sc.nextLine();
                if (next.toCharArray()[0] == '>') {
                    index = Integer.parseInt(next.replace(">", ""));
                } else {
                    data.put(index, next);
                    List<String> shingles = makeNGrams(next, gramSize);
                    for (String shingle : shingles) {
                        if (shingleMap.containsKey(shingle)) {
                            shingleMap.get(shingle).add(index);
                        } else {
                            List<Integer> tmp = new ArrayList<>();
                            tmp.add(index);
                            shingleMap.put(shingle, tmp);
                        }
                    }

                    index = 0;
                }
            }
            sc.close();

        } catch (FileNotFoundException ex) {
            System.out.println("Query file " + filename + " not found");
        }
        return shingleMap;
    }
    static int gramSize = 15;

    public static void main(String[] args) throws InterruptedException {
        ConfigParser config = new ConfigParser("Config.conf");
//        String serverName = config.getString("Server").trim();
        String serverName = args[2];
        int port = config.getInt("SecondPort");
        int limit = Integer.parseInt(args[1]);
        gramSize = config.getInt("GramSize");
        Map<String, List<Integer>> shingleMap = getQueryShingles(gramSize, args[0]);
        if (!shingleMap.isEmpty()) {
           
			while(true){
				try {
					Date startWithInit = new Date();
					System.out.println("Connecting to " + serverName + " on port " + port);
					Socket client = new Socket();
					client.connect(new InetSocketAddress(serverName, port), 5 * 1000);
					System.out.println("Just connected to " + client.getRemoteSocketAddress());

					List<String> queryList = new ArrayList<>(shingleMap.keySet());//for one query
					Collections.shuffle(queryList, new Random(System.nanoTime()));//shuffling the query

	//                runNativeQuery(queryList, client);
					/*0-naive hash
					 #1-diffie helman
					 #2-ot hash
					 #3-permutation hash*/
					runPSI(shingleMap.values().iterator().next().get(0), queryList, client, (config.getInt("PSIProtocol") > 0) ? config.getInt("PSIProtocol") + 1 : config.getInt("PSIProtocol"), limit*config.getInt("C"));//PSI +1
					client.close();
					System.out.println("time Diff " + (double) (new Date().getTime() - startWithInit.getTime()) / (1000) + " s");
					break;
				} catch (IOException e) {
					System.out.println("Server not found, please wait");
					Thread.sleep(2000);
				}
			}
        }

    }

    private static void runNativeQuery(List<String> queryList, Socket client) throws IOException {
        String query = "";
        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        for (int i = 0; i < queryList.size(); i++) {
            query += queryList.get(i) + ",";
        }

        out.writeUTF(getQuery(0, query, 12, 0));
        InputStream inFromServer = client.getInputStream();
        DataInputStream in = new DataInputStream(inFromServer);
        JsonObject jsonObject = Json.createReader(new StringReader(in.readUTF())).readObject();
        JsonArray results = jsonObject.getJsonArray("msg");
        for (int i = 1; i <= results.size(); i++) {
            System.out.println(i + ": " + results.get(i - 1));
        }
    }

    private static void runPSI(int index, List<String> queryList, Socket client, int type, int limit) throws FileNotFoundException, IOException {
        PrintWriter writer = new PrintWriter(gramSize + "onlyQuery.txt");
        for (int i = 0; i < queryList.size(); i++) {
            writer.println(queryList.get(i));
        }
        writer.close();
//	OutputStreamWriter  osw =new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);

        String sent = getQuery(type, "", limit, 0);

        out.writeUTF(sent);
        out.flush();
//        out.close();
        System.out.println("sent to server " + sent);
        System.out.println("./demo.exe -r 0 -p " + (type) + " -f " + gramSize + "onlyQuery.txt");
        executeConsoleCommand("./demo.exe -r 0 -p " + (type) + " -f " + gramSize + "onlyQuery.txt");

        InputStream inFromServer = client.getInputStream();
        DataInputStream in = new DataInputStream(inFromServer);
        JsonObject jsonObject = Json.createReader(new StringReader(in.readUTF())).readObject();
        JsonArray results = jsonObject.getJsonArray("msg");

        writer = new PrintWriter("Results_PSI.txt");
        if (results.size() > 0) {
            for (int i = 1; i <= results.size(); i++) {
                writer.println(i + ": " + results.get(i - 1));
                System.out.println(i + ": " + results.get(i - 1));
            }
        } else {
            writer.println("no results found");
            System.out.println("");
        }
//        writer.flush();
        writer.close();
    }
}
