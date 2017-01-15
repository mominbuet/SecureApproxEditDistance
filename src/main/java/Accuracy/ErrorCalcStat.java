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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author azizmma
 */
public class ErrorCalcStat {

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
                if (res[1].trim().contains(",")) {
                    res[1] = res[1].split(",")[0];
                }
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
        Map<Integer, StatMeasures> TPR = new TreeMap<>();
        TPR.put(1, new StatMeasures());
        TPR.put(5, new StatMeasures());
        TPR.put(10, new StatMeasures());
        TPR.put(15, new StatMeasures());
        TPR.put(20, new StatMeasures());
        TPR.put(25, new StatMeasures());
        TPR.put(30, new StatMeasures());
        TPR.put(35, new StatMeasures());
        TPR.put(40, new StatMeasures());

//        Map<Integer, Double> errorMapFalse = new TreeMap<>();
//        errorMapFalse.put(1, 0.0);
//        errorMapFalse.put(5, 0.0);
//        errorMapFalse.put(10, 0.0);
//        errorMapFalse.put(15, 0.0);
//        errorMapFalse.put(20, 0.0);
//        errorMapFalse.put(25, 0.0);
//        errorMapFalse.put(30, 0.0);
//        errorMapFalse.put(35, 0.0);
//        errorMapFalse.put(40, 0.0);
        int totalQueries = 1, datasetsize = 500;
        for (int t = 82; t < 82+ totalQueries; t++) {

            Map<Integer, List<Integer>> originalRank = getranks(
                    new File("query_results\\original_results\\" + (t < 10 ? "0" + t : t) + ".txt"));//" + (t < 10 ? "0" + t : t) + "

            Map<Integer, List<Integer>> approxRank = getranks(new File("query_results\\kbanded_joined_20\\" + (t < 10 ? "0" + t : t) + ".txt"));//" + (t < 10 ? "0" + t : t) + "
//            System.out.println("T " + t);
//            System.out.println("Top K\t Error Percentage");
            //try {
            for (int i = 1; i <= 40; i += 5) {
                int error = 0;
                int true_positives = 0, true_negetives = 0, false_positives = 0, false_negetives = 0, totalPostitives = 0;
                List<Integer> approxRanks = new ArrayList<>();
                List<Integer> originalRanks = new ArrayList<>();
                List<Integer> originalRanksNegetives = new ArrayList<>();
                List<Integer> approxRanksNegetives = new ArrayList<>();
                for (int j = 1; j <= i; j++) {
                    if (!approxRank.containsKey(j)) {
                        break;
                    }
                    approxRanks = union(approxRank.get(j), approxRanks);
                }
                for (int j = 1; j <= i; j++) {
                    originalRanks = union(originalRanks, originalRank.get(j));
                }
                for (int j = i + 1; j <= originalRank.size(); j++) {
                    originalRanksNegetives = union(originalRanksNegetives, originalRank.get(j));
                }
                for (int j = i + 1; j <= approxRank.size(); j++) {
                    if (!approxRank.containsKey(j)) {
                        break;
                    }
                    approxRanksNegetives = union(approxRanksNegetives, approxRank.get(j));
                }

                totalPostitives = originalRanks.size();
                int totalNegetives = datasetsize - totalPostitives;
                List<Integer> intersected = intersection(approxRanks, originalRanks);
                true_positives = intersected.size();
                true_negetives = intersection(approxRanksNegetives, originalRanksNegetives).size();
                false_negetives = totalPostitives - true_positives;//P=TP+FN
                false_positives = totalNegetives - true_negetives;
                StatMeasures statMeasures = new StatMeasures(true_positives, false_positives, true_negetives, false_negetives);
                StatMeasures statMeasuresOld = TPR.get(i);
                statMeasuresOld.Acc += statMeasures.Acc;
                statMeasuresOld.F1Score += statMeasures.F1Score;
                statMeasuresOld.FPR += statMeasures.FPR;
                statMeasuresOld.MCC += statMeasures.MCC;
                statMeasuresOld.Precision += statMeasures.Precision;
                statMeasuresOld.TNR += statMeasures.TNR;
                statMeasuresOld.TPR += statMeasures.TPR;

                TPR.put(i, statMeasuresOld);

                //            System.out.println("sum " + (int) (true_negetives + true_positives + false_negetives + false_positives));
                //            System.out.print("acc " + (double) ((int) (true_positives + true_negetives) / ((int) true_negetives + true_positives + false_negetives + false_positives)));
                //            System.out.println(", TPR " + (double) true_positives / totalPostitives);
                //                    if (res != null) {
                //                        boolean found = false;
                //                        for (Integer approx_item : res) {
                //                            List<Integer> originalrank_in_k = new ArrayList<>();
                //                            for (int k = 1; k <= i; k++) {
                //                                for (int i1 : originalRank.get(k)) {
                //                                    originalrank_in_k.add(i1);
                //                                }
                //
                //                                if (originalRank.get(k).contains(approx_item)) {
                //                                    found = true;
                //                                    break;
                //                                }
                //                            }
                //
                //                            if (!originalrank_in_k.contains(approx_item) && j == i) {
                //                                false_positives++;
                //                            }
                //                        }
                //                        if (!found) {
                //                            error++;
                //                        }
                //                    }
                //                }
                //                for (int j = 1; j <= i; j++) {
                //                    if (originalRank.containsKey(j)) {
                //                        totalPostitives += originalRank.get(j).size();
                //                    }
                //                }
                //            TPR.put(i, TPR.get(i) + ((double) error / totalPostitives));
                ////                errorMap.put(i, errorMap.get(i) + ((double) error));
                //            errorMapFalse.put(i, errorMapFalse.get(i) + ((double) false_positives / (false_positives + 3000 - totalPostitives)));
                    /*if (i == 1 && error == 0) {
                 System.out.println(i + "\t" + formatter.format((double) error / i) + "\t" + formatter.format((double) false_positives / total) + "\t" + t);
                 }*/
                if (i == 1) {
                    i = 0;
                }
            }
            /*} catch (Exception ex) {
             System.out.println(ex.getMessage());
             }*/
        }
        System.out.println("Top-K\tAcc \t MCC \t F1 \t Preci \t TPR \t TNR \t FPR");
        for (Map.Entry<Integer, StatMeasures> entrySet : TPR.entrySet()) {
            Integer key = entrySet.getKey();
            System.out.println(key + "\t" + formatter.format(entrySet.getValue().Acc / totalQueries)
                    + "\t" + formatter.format(entrySet.getValue().MCC / totalQueries) + "\t"
                    + formatter.format(entrySet.getValue().F1Score / totalQueries) + "\t"
                    + formatter.format(entrySet.getValue().Precision / totalQueries) + "\t"
                    + formatter.format(entrySet.getValue().TPR / totalQueries) + "\t"
                    + formatter.format(entrySet.getValue().TNR / totalQueries) + "\t"
                    + formatter.format(entrySet.getValue().FPR / totalQueries));
//            Double value = entrySet.getValue();
//            System.out.println(key + "\t" + formatter.format(100 * (1 - (double) value / totalQueries))
//                    + "\t" + formatter.format(100 * (errorMapFalse.get(key) / totalQueries)));
//            System.out.println(key + "\t" + formatter.format(100 * (1 - (double) value / totalQueries))
//                    + "\t" + formatter.format(100 * (errorMapFalse.get(key) / totalQueries)));
        }
    }

    public static <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    private static class StatMeasures {

        public double TPR, TNR, FPR, F1Score, Acc, MCC, Precision;

        public StatMeasures() {
            TPR = 0.0;
            TNR = 0.0;
            FPR = 0.0;
            F1Score = 0.0;
            Acc = 0.0;
            MCC = 0.0;
            Precision = 0.0;
        }

        public StatMeasures(int TP, int FP, int TN, int FN) {
            Acc = 100 * (double) (TP + TN) / (TN + TP + FN + FP);

            Precision = 100 * (double) TP / (TP + FP);
            MCC = 100 * (double) (TP * TN - FP * FN) / Math.sqrt((double) (TP + FP) * (TP + FN) * (TN + FP) * (TN + FN));
            TPR = 100 * (double) TP / (TP + FN);
            TNR = 100 * (double) TN / (TN + FP);
            FPR = 100 * (double) FP / (FP + TN);
//            F1Score = (double) 2 * TP / (2 * TP + FP + FN);
            F1Score = 100 * (double) 2 * Precision * TPR / (double) (Precision + TPR);
//            if(F1Score==Double.NaN)
//                F1Score
        }
    }
}
