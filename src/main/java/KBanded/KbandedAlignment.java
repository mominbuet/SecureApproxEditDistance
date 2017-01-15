/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KBanded;

import static Accuracy.OriginalEditdistance.sortByValue;
import util.EditDistance;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author azizmma
 */
public class KbandedAlignment {

    public final static int kbandSize =20;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        int index = 0, indexQ = 0;
        Map<Integer, String> data = new HashMap<>();
        Map<Integer, Integer> distanceMap = new HashMap<>();
//        Map<Integer, Integer> refDistanceMap = new HashMap<>();
//        String ref = new Scanner(new File("cnnref.txt")).nextLine();
        Scanner sc2 = new Scanner(new File("query_500.fa"));
        while (sc2.hasNext()) {
            String nextQ = sc2.nextLine();
            if (nextQ.contains(">")) {
                indexQ = Integer.parseInt(nextQ.replace(">", ""));
            } else {
//        System.out.println("querylength " + query.length());
//        int rand = ThreadLocalRandom.current().nextInt(0, query.length());
                Scanner sc = new Scanner(new File("syntheticDB.fa"));
                while (sc.hasNext()) {
                    String next = sc.nextLine();
                    if (next.contains(">")) {
                        index = Integer.parseInt(next.replace(">", ""));
                    } else {
                        data.put(index, next);
                        distanceMap.put(index, EditDistance.getKbandEditDistanceOld(nextQ, next, kbandSize));

//                distanceMap.put(index, EditDistance.getNewEditDistance(query.substring(0,3*query.length()/4), next.substring(0,3*query.length()/4)));
//                refDistanceMap.put(index, EditDistance.getDistance(next, ref));
                        if (index > 3000) {
                            break;
                        }
                        index = 0;
                    }
                }
//                sc.close();

                distanceMap = sortByValue(distanceMap);
//        refDistanceMap = sortByValue(refDistanceMap);
                int ind = 0, tmp = 99999;
                new File("query_results\\kbanded_"+kbandSize).mkdir();
                System.out.println("writing query_results\\kbanded_"+kbandSize+"\\" + indexQ + ".txt");
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("query_results\\kbanded_"+kbandSize+"\\" + indexQ + ".txt", false)));
                for (Map.Entry<Integer, Integer> entrySet : distanceMap.entrySet()) {
                    if (tmp != entrySet.getValue()) {
                        ind++;
                    }
//            System.out.println("dist " + entrySet.getValue() + " index " + entrySet.getKey() + " ind " + ind);//+" refdist "+refDistanceMap.get(entrySet.getKey())+
//                    System.out.println(ind + ":" + entrySet.getKey() + " cost " + entrySet.getValue());
                    pw.println(ind + ":" + entrySet.getKey());
                    tmp = entrySet.getValue();
                }
                pw.flush();
                pw.close();
                if (indexQ == 5650) {
                    break;
                }

                indexQ = 0;

            }
        }
    }
}
