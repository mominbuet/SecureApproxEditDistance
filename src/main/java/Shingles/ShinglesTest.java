/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shingles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author azizmma
 */
public class ShinglesTest {

    public static <K, V extends Comparable<? super V>> Map<K, V>
            sortByValueRev(Map<K, V> map) {
        List<Map.Entry<K, V>> list
                = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    /**
     * @param args the command line arguments
     */
//    public final static int stringSize = 2000;
    public final static int gramSize = 10;

    public static List<String> makeNGrams(String input) {
        List<String> ret = new ArrayList<String>();
        char[] arr = input.toCharArray();
        for (int i = 0; i < input.length() - gramSize; i++) {
            if (!ret.contains(input.substring(i, i + gramSize))) {
                ret.add(input.substring(i, i + gramSize));
            }
        }
        return ret;
    }

    public static Map<String, Integer> makeNGramsCount(String input, int index) {
        Map<String, Integer> res = new HashMap<>();
        List<String> ret = new ArrayList<String>();
        char[] arr = input.toCharArray();
        for (int i = 0; i < input.length() - gramSize; i++) {
            if (!ret.contains(input.substring(i, i + gramSize))) {
                ret.add(input.substring(i, i + gramSize));
                res.put(input.substring(i, i + gramSize), 1);
            } else {
                res.put(input.substring(i, i + gramSize) + "," + index,
                        res.get(input.substring(i, i + gramSize)) + 1);
            }
        }
        return res;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("gram size "+gramSize);
        File fl = new File("syntheticDB.fa");//syntheticDB
        Scanner sc = new Scanner(fl);
        int index = 0;
//        Map<Integer, String> data = new HashMap<>();
        Map<String, List<String>> shingleMap = new HashMap<>();
//        Map<String, Integer> shingleCountMap = new HashMap<>();
        while (sc.hasNext()) {
            String next = sc.nextLine();
            if (next.contains(">")) {
                index = Integer.parseInt(next.replace(">", ""));
            } else {
                next = next;//.substring(0, stringSize);
//                data.put(index, next);
                List<String> shingles = makeNGrams(next);
//                for (Map.Entry<String, Integer> entrySet : makeNGramsCount(next, index).entrySet()) {
//                    shingleCountMap.put(entrySet.getKey(), entrySet.getValue());
//                } 
               Map<String, Integer> shingMapCount = makeNGramsCount(next, index);
                shingMapCount = sortByValueRev(shingMapCount);
                for (String shingle : shingles) {
                    if (shingleMap.containsKey(shingle)) {
                        shingleMap.get(shingle).add(index + "-" + (shingMapCount.containsKey(shingle) ? shingMapCount.get(shingle) : 1));
//                        shingleCountMap.put(shingle + "," + index, shingleCountMap.get(shingle + "," + index) + 1);
                    } else {
                        List<String> tmp = new ArrayList<>();
                        tmp.add(index + "-" + (shingMapCount.containsKey(shingle) ? shingMapCount.get(shingle) : 1));
//                        shingleCountMap.put(shingle + "," + index, 1);
                        shingleMap.put(shingle, tmp);
                    }
                }
                if (index > 3000) {
                    System.out.println("max " + index);
                    break;
                }
                index = 0;
            }
        }
        //sc.close();
        System.out.println("data shingles size " + shingleMap.size());
        writeShingles(shingleMap, "shingles//dataShingles_" +  gramSize + ".txt");
        fl = new File("query_500.fa");
        sc = new Scanner(fl);
        String query = "";
        List<String> queryShingles = new ArrayList<>();

        while (sc.hasNext()) {
            String next = sc.nextLine();
            if (next.contains(">")) {
                index = Integer.parseInt(next.replace(">", ""));
                System.out.println("doing " + index);
            } else {
//                query = next;
                query = next;//.substring(0, stringSize);
                queryShingles = makeNGrams(query);

                System.out.println("query shingles size " + queryShingles.size());
//        writeShingles(queryShingles, "queryShingles.txt");
                Map<Integer, Integer> scores = new HashMap<>();
                for (Map.Entry<String, List<String>> entrySet : shingleMap.entrySet()) {
                    for (String queryShingle : queryShingles) {
                        if (queryShingle.equals(entrySet.getKey())) {
                            for (String incomingAll : entrySet.getValue()) {
                                Integer incoming = Integer.parseInt(incomingAll.split("-")[0]);
                                if (scores.containsKey(incoming)) {
//                                    scores.put(incoming,
//                                            scores.get(incoming) +1);
                                    scores.put(incoming,
                                            scores.get(incoming) + Integer.parseInt(incomingAll.split("-")[1]));
                                } else {
                                    scores.put(incoming, Integer.parseInt(incomingAll.split("-")[1]));
//                                    scores.put(incoming, 1);
                                }
                            }
                        }
//                else {
//                    int distance = getNewEditDistance(queryShingle, entrySet.getKey());
//                    if (distance <=3) {
//                        for (Integer incoming : entrySet.getValue()) {
//                            if (scores.containsKey(incoming)) {
//                                scores.put(incoming, scores.get(incoming) + 1);
//                            } else {
//                                scores.put(incoming, 1);
//                            }
//                        }
//                    }
//                }
                    }
                }
                scores = sortByValueRev(scores);
                int ind = 0, tmp = 99999;
                new File("query_results\\" + gramSize+"_1" ).mkdir();
                System.out.println("writen query_results\\" + gramSize  + "_1\\" + index + ".txt");
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("query_results\\" + gramSize  + "_1\\results_" + index + ".txt", false)));

                for (Map.Entry<Integer, Integer> entrySet : scores.entrySet()) {
                    if (tmp != entrySet.getValue()) {
                        ind++;
                    }
                    //System.out.println(ind + ":" + entrySet.getKey() + " dist " + entrySet.getValue());//+" refdist "+refDistanceMap.get(entrySet.getKey())+
                    pw.println(ind + ":" + entrySet.getKey());
                    tmp = entrySet.getValue();

                }
                pw.flush();
                pw.close();
                if(index==5550) break;
                index = 0;
               //s break;
            }
        }
        sc.close();
    }

    private static void writeShingles(Map<String, List<String>> shingleMap, String filename) {
        PrintWriter pw = null;
        try {
//            File output = new File(filename);
            pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, false)));
            for (Map.Entry<String, List<String>> entrySet : shingleMap.entrySet()) {
                pw.println(entrySet.getKey());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ShinglesTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ShinglesTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }

    private static void writeShingles(List<String> shingleMap, String filename) {
        PrintWriter pw = null;
        try {
//            File output = new File(filename);
            pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
            for (String tmp : shingleMap) {
                pw.println(tmp);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ShinglesTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ShinglesTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }
}
