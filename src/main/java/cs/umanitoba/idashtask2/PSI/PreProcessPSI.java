/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.idashtask2.PSI;

import util.ConfigParser;
import static Utilities.Helpers.makeNGrams;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author azizmma
 */
/**
 *
 * @author azizmma
 */
public class PreProcessPSI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        ConfigParser config = new ConfigParser("Config.conf");
        int gramSize = config.getInt("GramSize");
        File fl = new File(args[0]);
        Scanner sc = new Scanner(fl);
        int index = 0;
        Map<Integer, String> data = new HashMap<>();
        Map<String, List<Integer>> shingleMap = new HashMap<>();
        PrintWriter writer = new PrintWriter("SequenceSize.txt");
        while (sc.hasNext()) {
            String next = sc.nextLine();
            if (next.contains(">")) {
                index = Integer.parseInt(next.replace(">", ""));
            } else {
                data.put(index, next);
                writer.println(index + ":" + next.length());
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

                if (data.size() > 3000){
                    break;
                }
                index = 0;
            }
        }
        sc.close();
        writer.flush();
        writer.close();
        System.out.println("data shingles size " + shingleMap.size());
        fl = new File(gramSize + "db.txt");
        if (fl.exists()) {
            fl.delete();
        }
        fl = new File(gramSize + "onlyShingles.txt");
        if (fl.exists()) {
            fl.delete();
        }
        writer = new PrintWriter(gramSize + "db.txt");
        PrintWriter writerShingles = new PrintWriter(gramSize + "onlyShingles.txt");
        for (Map.Entry<String, List<Integer>> entrySet : shingleMap.entrySet()) {
            writer.print(entrySet.getKey() + "-");
            for (Integer tmp : entrySet.getValue()) {
                writer.print(tmp + ",");
            }
            writer.println();
            writerShingles.println(entrySet.getKey());
        }
        writer.close();
        writerShingles.close();
    }
}
