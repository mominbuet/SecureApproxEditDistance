/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.idashtask2.PSI;

import util.ConfigParser;
import static Utilities.Helpers.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author azizmma
 */
public class DataOwnerPSI extends Thread {

    static final int timeout = 5 * 60 * 1000;//5min
    private ServerSocket serverSocket;
    public static Map<String, List<Integer>> shingleMap = new HashMap<>();
    public static Map<Integer, String> data = new HashMap<>();
    int gramSize = 15;
    int PSItype = 0;

    public DataOwnerPSI(int port, int gramSize, int PSItype, String filename) {
        try {
            this.PSItype = PSItype;
            this.gramSize = gramSize;
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeout);
            File fl = new File(gramSize + "db.txt");
            Scanner sc = new Scanner(fl);
            int index = 0;
            while (sc.hasNext()) {
                String next = sc.nextLine();
                next = next.replace("-", ",");
                String[] keyValues = next.split(",");
                List<Integer> ids = new ArrayList<Integer>();
                for (int i = 1; i < keyValues.length; i++) {
                    ids.add(Integer.parseInt(keyValues[i]));
                }
                shingleMap.put(keyValues[0], ids);
            }
            /**
             * inserting data
             */
            sc = new Scanner(new File(filename));
            while (sc.hasNext()) {
                String next = sc.nextLine();
                if (next.toCharArray()[0] == '>') {
                    index = Integer.parseInt(next.replace(">", ""));
                } else {
                    data.put(index, next);
                    index = 0;
                }
            }
            sc.close();
        } catch (IOException ex) {
            System.out.println("File not found, Please run PreProcessPSI");
        }
    }

    public static Map<Integer, Integer> runUnsecureProtocol(String[] queryShingles) {
        Map<Integer, Integer> scores = new HashMap<>();

//                        for (Map.Entry<String, List<Integer>> entrySet : shingleMap.entrySet()) {
        for (String queryShingle : queryShingles) {
            if (shingleMap.containsKey(queryShingle)) {
                for (Integer incoming : shingleMap.get(queryShingle)) {
                    if (scores.containsKey(incoming)) {
                        scores.put(incoming, scores.get(incoming) + 1);
                    } else {
                        scores.put(incoming, 1);
                    }
                }
            }
        }
//                        }
        scores = sortByValue(scores, false);
//        int ind = 0, tmp = 99999;
//
//        for (Map.Entry<Integer, Integer> entrySet : scores.entrySet()) {
//            if (tmp != entrySet.getValue()) {
//                ind++;
//            }
//            System.out.println("dist " + entrySet.getValue() + " index " + entrySet.getKey() + " ind " + ind);//+" refdist "+refDistanceMap.get(entrySet.getKey())+
//
//            tmp = entrySet.getValue();
//
//        }
        return scores;
    }

    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();
                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());
                String input = in.readUTF();
//                in.close();
//                BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
//                String input = br.readLine();
                
                System.out.println(input);
                JsonObject jsonObject = Json.createReader(new StringReader(input)).readObject();
//                int type = jsonObject.getInt("type");
//                System.out.println(type + "Type");
                Map<Integer, Integer> scores;
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                switch (PSItype) {
//                    case 0://processquery without security
//
//                        String[] queryShingles = jsonObject.getString("msg").split(",");
//                        scores = runUnsecureProtocol(queryShingles);
//                        sendResult(out, jsonObject, scores);
//                        break;
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        //case 4:
                        System.out.println("./demo.exe -r 1 -p " + (PSItype) + " -f " + gramSize + "onlyShingles.txt");
                        String output = executeConsoleCommand("./demo.exe -r 1 -p " + (PSItype) + " -f " + gramSize + "onlyShingles.txt");

//                        System.out.println("output from command line " + output);
//                        output = output.replace("Computation finished. Found 2921 intersecting elements:", "");
                        String[] intersections = output.split("\n");
                        int matches = Integer.parseInt(intersections[0]);
                        if (matches == 0) {
                            System.out.println("No match found, sorry.");
                        } else {

                            scores = runPSIProtocol(Arrays.copyOfRange(intersections, 1, matches + 1));

                            sendResult(out, jsonObject, scores);
                        }
                        break;
                }
                in.close();
                System.exit(0);
//                server.close();
            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (Exception ex) {
                System.out.println("Error " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ConfigParser config = new ConfigParser("Config.conf");
        int port = config.getInt("SecondPort");
        Thread t = new DataOwnerPSI(port,
                config.getInt("GramSize"), (config.getInt("PSIProtocol") > 0) ? config.getInt("PSIProtocol") + 1 : config.getInt("PSIProtocol"),
                args[0]);
        t.start();
    }

    private Map<Integer, Integer> runPSIProtocol(String[] intersection) {
        Map<Integer, Integer> scores = new HashMap<>();
        for (int i = 0; i < intersection.length; i++) {
            if (!intersection[i].equals("")) {
                for (Integer incoming : shingleMap.get(intersection[i])) {
                    if (scores.containsKey(incoming)) {
                        scores.put(incoming, scores.get(incoming) + 1);
                    } else {
                        scores.put(incoming, 1);
                    }
                }
            }
        }

        scores = sortByValue(scores, false);
        return scores;
    }

    private void sendResult(DataOutputStream out, JsonObject jsonObject, Map<Integer, Integer> scores) throws IOException {
        List<Integer> results = new ArrayList<>();
        int limit = jsonObject.getInt("limit");
        int offset = jsonObject.getInt("offset");
        List<Integer> keys = new ArrayList<>(scores.keySet());
        for (int i = offset; i < keys.size() && i < limit; i++) {
//                            results.add(data.get(keys.get(i)));
            results.add(keys.get(i));
        }
        out.writeUTF(getResult(1, results));

    }

}
