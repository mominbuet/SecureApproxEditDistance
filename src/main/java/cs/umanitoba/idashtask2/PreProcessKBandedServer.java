/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.idashtask2;

import cs.umanitoba.idashtask2.PSI.DataOwnerPSI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import util.ConfigParser;

/**
 *
 * @author azizmma
 */
public class PreProcessKBandedServer {

    /**
     * @param args the command line arguments
     */
    static int gramSize = 20;

    public static void main(String[] args) {
        try {
            final ConfigParser config = new ConfigParser("Config.conf");
            File fl;
            Scanner sc;
            List<Integer> searchSpace = new ArrayList<>();
            if (config.getInt("SearchSpaceShorten") == 1) {
                sc = new Scanner(new File("Results_PSI.txt"));
                while (sc.hasNext()) {
                    String next = sc.nextLine();
                    if (!next.isEmpty()) {
                        String[] tmp = next.split(":");
                        searchSpace.add(Integer.parseInt(tmp[1].trim()));
                    }
                }
            }
            fl = new File(args[0].trim());
//            fl = new File("db.fa");
            sc = new Scanner(fl);//
            int index = 0;
            PrintWriter printWriter = new PrintWriter(new File("db_processed.txt"));
            Map<Integer, String> data = new HashMap<>();
            int lowest = 9999999;
//        Map<String, List<Integer>> shingleMap = new HashMap<>();
            while (sc.hasNext()) {
                String next = sc.nextLine();
                if (next.contains(">")) {
                    index = Integer.parseInt(next.replace(">", ""));
                } else {
                    if (searchSpace.contains(index) || searchSpace.isEmpty()) {
                        data.put(index, next);
                        printWriter.println(">" + index);
                        printWriter.println(next);
                        if (next.length() < lowest) {
                            lowest = next.length();
                        }
                    }
//                printWriter.println(index + ":" + next.length());
                    if (data.size() > 1000) {
                        break;
                    }
                    index = 0;
                }
            }
            printWriter.flush();
            printWriter.close();
            System.out.println("wrote db_processed.txt");

            printWriter = new PrintWriter(new File("SequenceSizeOwner.txt"));
            printWriter.println(lowest);
            printWriter.println(data.size());
            printWriter.close();
            sc.close();

            int port = config.getInt("Port");
            Thread t = new PreProcessServer(port + 1, lowest, data.size());
            t.run();
            
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
        }

    }
}
