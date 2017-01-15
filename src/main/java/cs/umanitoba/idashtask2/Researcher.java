/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.idashtask2;

import flexsc.Flag;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import util.ConfigParser;
import util.EvaRunnable;
import util.WorkerListenerEvaluator;

/**
 *
 * @author azizmma
 */
public class Researcher {

    static String host;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException, IOException {
        Scanner sc = new Scanner(new File("SequenceSizeClient.txt"));
        int lowest = Integer.parseInt(sc.nextLine());
        int datasize = Integer.parseInt(sc.nextLine());
        sc.close();

        File fl = new File(args[0]);
        host = args[1];
//        File fl = new File("query.fa");
        final ConfigParser config = new ConfigParser("Config.conf");
        sc = new Scanner(fl);
        String query = "";
//        List<String> queryShingles = new ArrayList<>();
        int index = 0;
        while (sc.hasNext()) {
            String next = sc.nextLine();
            if (next.toCharArray()[0] == '>') {
                index = Integer.parseInt(next.replace(">", ""));
            } else {
                query = next;
//                queryShingles = makeNGrams(query);
                index = 0;
            }

        }
        sc.close();
////        ConfigParser config = new ConfigParser("Config.conf");
//        String serverName = config.getString("Server").trim();
//        int port = config.getInt("Port") + 1;
//
//        System.out.println("Connecting to " + serverName + " on port " + port);
//        Socket client = new Socket();
//        client.connect(new InetSocketAddress(serverName, port), 5 * 1000);
//        System.out.println("Just connected to " + client.getRemoteSocketAddress());
//        InputStream inFromServer = client.getInputStream();
//        DataInputStream in = new DataInputStream(inFromServer);
//        String[] incoming =in.readUTF().split(",");
//        int lowest = Integer.parseInt(incoming[0]);
//        int datasize = Integer.parseInt(incoming[1]);
        if (query.length() < lowest) {
            System.out.println("Query must be " + lowest + " length atleast");
        }
        
        Map<Integer, String> queryCopies = new HashMap<>();
//        Map<Integer, Integer> dataSize = new HashMap<>();

        for (int i = 0; i < datasize; i++) {
            queryCopies.put(i, query.substring(0, lowest));
        }
//        long seed = System.nanoTime();
//        Collections.shuffle(queryShingles, new Random(seed));
//        seed = System.nanoTime();
//        Collections.shuffle(queryShingles, new Random(seed));
//
//        System.out.println("query shingles size " + queryShingles.size());
//        Global.queryShingleSize = queryShingles.size();
//        Global.queryShingleSize = 10;
//        int queryIndex = 0;
//        for (String queryShingle : queryShingles) {
        args = new String[2];
        args[0] = "example.EditDistanceString";
        args[1] = queryCopies.get(0);
//        for (int i = 0; i < Global.queryShingleSize; i++) {
//            args[1] += queryShingles.get(i).substring(0,Global.shingleSize);
//        }
//        System.out.println(args[1].length());
        Class<?> clazz = Class.forName(args[0] + "$Evaluator");
        EvaRunnable run = (EvaRunnable) clazz.newInstance();

        run.registerWorkerListener(new WorkerListenerEvaluator() {
            int running = 0;

            @Override
            public void workDoneEvaluator(EvaRunnable run, Map<Integer, String> queryCopies, int queryIndex) {
                try {
                    running++;
//                    run.disconnect();
//                    System.out.println(queryIndex + " res from GC " + run.getResult());
//                    if (running < 8093) {
                    if (run.getResult() > -9999) {
                        running = 0;
                        queryIndex++;
                    }
//                    System.out.println(queryCopies.size() + " " + queryIndex);
                    if (queryIndex != queryCopies.size()) {
                        String[] args = new String[2];
                        args[0] = "example.EditDistanceString";
                        args[1] = queryCopies.get(queryIndex);
                        run.setParameter(config, Arrays.copyOfRange(args, 1, args.length), host, queryCopies, queryIndex);
                        new Thread(run).start();
                    } else {
                        System.out.println("Ended");
                        System.exit(0);
                    }
//                    }

                } catch (Exception ex) {
                    System.out.println("Exception " + ex.getMessage());
                }
            }
        });
        run.setParameter(config, Arrays.copyOfRange(args, 1, args.length),host, queryCopies, 0);//removing the classname
//        run.run();
        System.out.println("Started GC as EVA");
        new Thread(run).start();

//            break;
//        }
        if (Flag.CountTime) {
            Flag.sw.print();
        }
//        return 0;
    }

}
