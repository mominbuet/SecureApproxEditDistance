/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Accuracy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author azizmma
 */
public class OriginalEditdistance {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File fl = new File(args[0]);
        Scanner sc = new Scanner(fl);
        int index = 0;
        Map<Integer, String> data = new HashMap<>();
        Map<Integer, Integer> distanceMap = new HashMap<>();
//        Map<Integer, Integer> refDistanceMap = new HashMap<>();
//        String ref = new Scanner(new File("cnnref.txt")).nextLine();
        Scanner sc2 = new Scanner(new File(args[1]));
        sc2.nextLine();
        String query = sc2.nextLine();
        sc2.close();
        System.out.println("querylength " + query.length() + ", calculating top-" + args[2] + " results");
        int rand = ThreadLocalRandom.current().nextInt(0, query.length());
        while (sc.hasNext()) {
            String next = sc.nextLine();
            if (next.contains(">")) {
                index = Integer.parseInt(next.replace(">", ""));
            } else {
                data.put(index, next);
                distanceMap.put(index, Utilities.EditDistance.getNewEditDistance(query, next));

//                distanceMap.put(index, EditDistance.getNewEditDistance(query.substring(0,3*query.length()/4), next.substring(0,3*query.length()/4)));
//                refDistanceMap.put(index, EditDistance.getDistance(next, ref));
//                if (index > 1001 + 500) {
//                    break;
//                }
                if (data.size() > 1000) {
                    break;
                }
                index = 0;
            }
        }
        sc.close();

        distanceMap = sortByValue(distanceMap);
//        refDistanceMap = sortByValue(refDistanceMap);
        int ind = 0, tmp = 99999;
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("original_results", false)));
        for (Map.Entry<Integer, Integer> entrySet : distanceMap.entrySet()) {
            if (tmp != entrySet.getValue()) {
                ind++;
            }
            if (ind > Integer.parseInt(args[2].trim())) {
                break;
            }
//            System.out.println("dist " + entrySet.getValue() + " index " + entrySet.getKey() + " ind " + ind);//+" refdist "+refDistanceMap.get(entrySet.getKey())+
            System.out.println(ind + ":" + entrySet.getKey() + " cost " + entrySet.getValue());
            pw.println(ind + ":" + entrySet.getKey());
            tmp = entrySet.getValue();
        }
        pw.flush();
        pw.close();
        System.out.println("original_results has the real edit distance results.");
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
            sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list
                = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
