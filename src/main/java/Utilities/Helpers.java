/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author azizmma
 */
public class Helpers {

    public static int gramSize = 20;

    public static String executeConsoleCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
//            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public static List<String> makeNGrams(String input, int gramSize) {
        List<String> ret = new ArrayList<String>();
        char[] arr = input.toCharArray();
        for (int i = 0; i < input.length() - gramSize; i++) {
            if (!ret.contains(input.substring(i, i + gramSize))) {
                ret.add(input.substring(i, i + gramSize));
            }
//            if(ret.size()>10)
//                break;
        }
        return ret;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
            sortByValue(Map<K, V> map, final boolean ascending) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (ascending) ? (o1.getValue()).compareTo(o2.getValue())
                        : (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static List<String> makeNGrams(String input) {
        List<String> ret = new ArrayList<String>();
        char[] arr = input.toCharArray();
        for (int i = 0; i < 3000; i++) {
            if (!ret.contains(input.substring(i, i + gramSize))) {
                ret.add(input.substring(i, i + gramSize));
            }
//            if(ret.size()>10)
//                break;
        }
        return ret;
    }

    public static String getResult(int type, List<Integer> results) {
        JsonArrayBuilder jsonArrayWords = Json.createArrayBuilder();
        for (int i = 0; i < results.size(); i++) {
            jsonArrayWords.add(results.get(i));
        }
        return Json.createObjectBuilder()
                .add("type", type)
                .add("msg", jsonArrayWords)
                .build()
                .toString();
    }

    public static String getQuery(int type, String message, int limit, int offset) {
        return Json.createObjectBuilder()
                .add("type", type)
                .add("limit", limit)
                .add("offset", offset)
                .add("msg", message)
                .build()
                .toString();
    }

    public static String getMessage(int type, String message) {
        return Json.createObjectBuilder()
                .add("type", type)
                .add("msg", message)
                .build()
                .toString();
    }
}
