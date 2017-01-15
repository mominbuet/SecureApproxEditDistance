/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Accuracy;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author azizmma
 */
public class ErrorCalculation {

    /**
     * @param args the command line arguments
     */
    private static Map<Integer, List<Integer>> getranks(File fl) throws FileNotFoundException {
        Scanner sc = new Scanner(fl);
        Map<Integer, List<Integer>> resRank = new TreeMap<>();
        while (sc.hasNext()) {
            String line = sc.nextLine();
            if (!line.isEmpty()) {
                String[] res = line.split(":");
                int rank = Integer.parseInt(res[0].trim());
                int data = Integer.parseInt(res[1].trim());
                if (resRank.containsKey(rank)) {
                    resRank.get(rank).add(data);
                } else {
                    List<Integer> tmp = new ArrayList<>();
                    tmp.add(data);
                    resRank.put(rank, tmp);
                }
            }
        }
        return resRank;
    }
    final static NumberFormat formatter = new DecimalFormat("#0.000");

    public static void main(String[] args) throws FileNotFoundException {

        Map<Integer, List<Integer>> originalRank = getranks(new File(args[0]));

        Map<Integer, List<Integer>> approxRank = getranks(new File(args[1]));
        System.out.println("Top K\t Error Percentage");
        try {
            for (int i = 5; i <= Integer.parseInt(args[2].trim()); i += 5) {
                int error = 0;
                for (int j = 1; j < i; j++) {
                    List<Integer> res = approxRank.get(j);
                    if (res != null) {
                        boolean found = false;
                        for (Integer item : res) {

                            for (int k = 1; k < i; k++) {
                                if (originalRank.get(k).contains(item)) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            error++;
                        }
                    }
                }
                System.out.println(i + "\t" + formatter.format((double) error / i));
                
            }
        } catch (Exception ex) {
        }
    }
}
