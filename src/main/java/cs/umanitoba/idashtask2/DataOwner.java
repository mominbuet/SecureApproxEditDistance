/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.idashtask2;

import static Accuracy.OriginalEditdistance.sortByValue;
import flexsc.Flag;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ConfigParser;
import util.GenRunnable;
import util.WorkerListenerGenerator;

/**
 *
 * @author azizmma
 */
public class DataOwner {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException {
        Scanner sc = new Scanner(new File("SequenceSizeOwner.txt"));
        int lowest = Integer.parseInt(sc.nextLine());
        sc.close();

        File fl = new File("db_processed.txt");
        final ConfigParser config = new ConfigParser("Config.conf");
        sc = new Scanner(fl);
        int index = 0;
//        int lowest = 999999999;
        Map<Integer, String> data = new LinkedHashMap<>();
        while (sc.hasNext()) {
            String next = sc.nextLine();
            if (next.contains(">")) {
                index = Integer.parseInt(next.replace(">", ""));
            } else {
                data.put(index, next.substring(0, lowest));
                index = 0;
//                if (data.size() > 1000) {
//                    break;
//                }
            }
        }
        sc.close();
//        int queryLength = 0;
//        try {
//            ServerDataOwner serverDataOwner = new ServerDataOwner(config.getInt("SecondPort"), data, lowest);
//            serverDataOwner.start();
//            while (serverDataOwner.getQuerylength() == 0) {
//            }
//            queryLength = serverDataOwner.getQuerylength();
//            System.out.println(queryLength);
////                serverDataOwner.join();
////                if (queryLength < lowest) {
//            lowest = queryLength;
//            serverDataOwner.interrupt();
////                }
//
//        } catch (IOException ex) {
//            System.out.println("ex " + ex.getMessage());
//        }

        args = new String[2];
        args[0] = "example.EditDistanceString";
//        Iterator<HashMap.Entry<Integer, String>> mapIterator = data.entrySet().iterator();
        Integer[] keys = data.keySet().toArray(new Integer[data.size()]);
        args[1] = data.get(keys[0]);
//        args[2] = queryLength + "";
//        System.out.println(args[1].length());
        final Map<Integer, Integer> resMap = new HashMap<>();
        Class<?> clazz = Class.forName(args[0] + "$Generator");
        GenRunnable run = (GenRunnable) clazz.newInstance();
        run.registerWorkerListener(new WorkerListenerGenerator() {
            int running = 0;

            @Override
            public void workDoneGenerator(GenRunnable run, Map<Integer, String> allkeys, Integer[] keys, int keyIndex) {
                System.out.println(keyIndex + ": res from GC " + run.getResult());
                running++;
//                run.disconnect();
//                if (running < 2963) {

                if (run.getResult() > -2) {
                    running = 0;
//                    if (resMap.containsKey(keyIndex)) {
//                        resMap.put(keyIndex, resMap.get(keyIndex) + 1);
//                    } else {
//                        resMap.put(keyIndex, 1);
//                    }
//                    System.out.println(((HashMap.Entry) keyIndex).getKey());
                    resMap.put(keys[keyIndex], run.getResult());
                }
                if (keyIndex < keys.length - 1) {
                    keyIndex++;
//                    keyIndex.next();
//                    int rand = ThreadLocalRandom.current().nextInt(0, 1000);
                    String[] args = new String[2];
                    args[0] = "example.EditDistanceString";
                    args[1] = allkeys.get(keys[keyIndex]);
//                    args[2] = queryLength + "";
                    run.setParameter(config, Arrays.copyOfRange(args, 1, args.length), allkeys, keys, keyIndex);
                    new Thread(run).start();
                } else {
                    System.out.println("Ended");
                    try {
                        PrintWriter writer = new PrintWriter(new File("Results_KBandedEditDistance.txt"));
                        Map<Integer, Integer> resMapsorted = sortByValue(resMap);
                        int i = 1;
                        for (Map.Entry<Integer, Integer> entrySet : resMapsorted.entrySet()) {
                            writer.println((i++) + ":" + entrySet.getKey());

                        }
                        writer.close();
                        System.out.println("Result in Results_KBandedEditDistance.txt");
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DataOwner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.exit(0);
                }
            }
        });

        run.setParameter(config, Arrays.copyOfRange(args, 1, args.length), data, keys, 0);
        System.out.println("Started GC as GEN");
        new Thread(run).start();
        if (Flag.CountTime) {
            Flag.sw.print();
        }
    }

}
