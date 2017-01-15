/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shingles;

import static KBanded.KbandedAlignment.kbandSize;
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
public class SpaceTest {

//    static int substringSize = 1000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        int index = 0;
        Map<Integer, String> data = new HashMap<>();
        Scanner sc = new Scanner(new File("syntheticDB.fa"));
        while (sc.hasNext()) {
            String next = sc.nextLine();
            if (next.contains(">")) {
                index = Integer.parseInt(next.replace(">", ""));
            } else {
                data.put(index,next);
                if (index > 3000) {
                    break;
                }
            }
        }
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("shingles\\original.txt", false)));
                
        for (Map.Entry<Integer, String> entrySet : data.entrySet()) {
            Integer key = entrySet.getKey();
            String value = entrySet.getValue();
            pw.println(">"+key);
            pw.println(value);
        }
        pw.flush();
        pw.close();
    }

}
