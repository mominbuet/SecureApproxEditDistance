/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Date;

/**
 *
 * @author azizmma
 */
public class EditDistance {

    public static int getNewEditDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
//                System.out.println("costJ " + costs[j] + " costJPrev " + costs[j - 1]);

                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);//ThreadLocalRandom.current().nextInt(2, 5)

//                System.out.println("nw " + nw + " cost " + cj);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public static int getGeneralHalfEditDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() / 2 + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length() / 2; i += 2) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length() / 2; j += 2) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), (a.charAt(i - 1) == b.charAt(j - 1) && a.charAt(i) == b.charAt(j)) ? nw : nw + 1);//ThreadLocalRandom.current().nextInt(2, 5)
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length() / 2];
    }

    public static int getKbandEditDistanceOld(String a, String b, int kband) {
        a = a.toLowerCase();
        b = b.toLowerCase();
//        String shorter = (a.length() > b.length()) ? b : a;

//        int kband = 10;
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        int i = 1, j = 1;

        for (i = (j > kband ? j - kband : 1); i <= a.length(); i++) {
//            System.out.println("i " + i + " j " + j + " start " + ((i > kband) ? i - kband : 1) + " dimention " + ((i + kband >= b.length()) ? b.length() : i + kband));
//            System.out.println("cost " + (((i > kband) ? i - kband : 1) - 1) + " ");
//            costs[((i > kband) ? i - kband : 1) - 1] = i;
            costs[0] = i;
            int nw = i - 1;
            for (j = ((i > kband) ? i - kband : 1); j <= ((i + kband >= b.length()) ? b.length() : i + kband); j++) {

                //j <= b.length(); j++) {
//                if (j > i + 10 && i + 10 <= b.length()) {
//                    break;
//                }
//                System.out.println("costJ " + costs[j] + " costJPrev " + costs[j - 1]);
//                System.out.println("j " + j + " i " + i);
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);//ThreadLocalRandom.current().nextInt(2, 5)
//                System.out.println("j " + j + " i " + i + " cost " + cj);
//                System.out.println("nw " + nw + " cost " + cj);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public static int getKbandEditDistance(String a, String b, int kband) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        String longer = (a.length() > b.length()) ? a : b;
        if (a.length() > b.length()) {
            a = b;
            b = longer;
        }

//        int kband = 50;
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        int i = 1, j = 1, center = 1;

        for (i = 1; i <= a.length(); i++) {
//            System.out.println("cost " + (((i > kband) ? i - kband : 1) - 1) + " ");
//            costs[((i > kband) ? i - kband : 1) - 1] = i;
            costs[0] = i;
            int nw = i - 1;
            j = ((i > kband) ? i - kband : 1);
            int cost_counter = 1;
            while (true) {
                if (j > costs.length - 1) {
                    break;
                }
//                for(j = ((i > kband) ? i - kband : 1); j < costs.length; j++) {
//                int cj = 0;
//                ((i + kband >= b.length()) ? b.length() : i + kband)
//                if (j + 1 > costs.length) {
//                    System.out.println("center " + center + " j " + j + " i " + i);
//                    cj = Math.min(1 + Math.min(costs[j - center + 1], costs[j - center]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
//                    nw = costs[j - center + 1];
//                    costs[j - center + 1] = cj;
//                } else {
//                    cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
//                    nw = costs[j];
//                    costs[j] = cj;
//                }
//                System.out.println("j " + j + " i " + i);

                int cj = Math.min(1 + Math.min(costs[cost_counter], costs[cost_counter - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[cost_counter];
                costs[cost_counter] = cj;
                j++;
                cost_counter++;
                //j <= b.length(); j++) {
//                if (j > i + 10 && i + 10 <= b.length()) {
//                    break;
//                }
//                System.out.println("costJ " + costs[j] + " costJPrev " + costs[j - 1]);
                //ThreadLocalRandom.current().nextInt(2, 5)
//                System.out.println("j " + j + " i " + i + " cost " + cj);
//                System.out.println("nw " + nw + " cost " + cj);

            }
            center++;
        }
        return costs[2 * kband];
    }

    public static int getDistance(String a, String b) {
        int len1 = a.length();
        int len2 = b.length();

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = a.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = b.charAt(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[len1][len2];
    }

//    public static void main(String[] args) {
//        Date d1 = new Date();
//        String str1 = getRandomSeq(5000);
//        String str2 = getRandomSeq(5000);
//
//        String str3 = getRandomSeq(5000);
////        String str1 = "TGTGTCTAGAGTCCTCGATAGGTTCTGTTCGGGCTATACTCATCATCGTTGACGTATGGTAGTTTATAGGCTGGGTAATGCTCCCCCGATACTTTCGCCTTTAGCAGTGTCTCAAGGTGATGAAACAAAGCTGGGCATCGCGTAGAAGACCGTGATTTATTCTATAGTATGTACATAAGGTATCTGACATCCGCCACTTATTCGTTTCAAGACGCTTGTCTCCCGCAGGGGGGGTAGGGGAGAAGCAGAACAAAGGAAAAGGAAGCCCTGACTTTTATCAGAACCCGCCTCCTGGTCCTTCTAGGAGTTATTGCAAGTCGGGGCTTGTTGCTGGGTTCTGGTCGCCTGCCTCTTAACCTGACAGCCGTTAATGCTGTCCGAAAGTAATGGGAATAAGCTGGAGATGCTATCGCTAGACCTTTACTATCTCACAGACCGGTCGCCGGAAGCGCAGTTACAAGCCCGGCGTTAATGGTAAACACCGCGTCAATAGGCCTCCTAGACCGCCGACGTTGGAGTGCACGTCAAGTTTATCAGCATCCTACTCCGCCGCATCGTCGACGTATTCTTGTGGCTACCGAATGTTGCGCCTACCTAACATCGACCCCCACAAGCTCTTGTATTCGTACGAAGCTTTGGCCTCTCAGCAGCCTAGTTTCTTTTATCATTATTACTTACGATGAAGCCTGCAAGTGAGTACATCTATTATGTGCCCTTCCGAGAGGAAGCCACAATGCGGAAGTAGTCCTATAAATGAATTCCCCATGAGATTGCATGTAGACAGGATGCACACCGTCTACAGACGAACTAATGCGCCCCTTGACTCGTGCATACGTGGCGGCCAGTCCGAGGTTTGCCAGAATCCCTAACCAGAAAACTATTGTCTCGACGTTTGTGCTGATTACTGGTTCATGAAGGGCATTGAATTGATGATACGCGAAGAGCTTGAATTACGCAGCTATTTTGTTCCTCACAGTACAGTAGTCTAGCGACCGAGTACTGAGCGAGGTGCCCATTCATGGTCCTGTTGGTCAGATAAACGGGGGGGCGCCGCTCCATTCGAACAGATATCACGGCAAGTCACCTCGCGGTGACTGTATCCAGGCTGCAGACACAGTTATACTAGTTCAGGCCACTATTATCTTACGGCGTGTCGTCTGTATGGTGTCGTTCCTTAGACCGTCTCGTATACTCCCAGTCACGGTCCCGCTATTAGCTCAATAACCGCCAGACATCAGAAACTGGGTATCCTATAGTTTGATTATCCGGAAATAACGTCTCCGGATGCAGTGCCCACATTTAGAATTCGAAGATACGTAACCTCCCCGGACAGTAGACTAGCATAAGAGTCGTGTTTTTATACAGATCCAAAGAACTGCACGACTGTTATGCACCATGACCGGCCTGTATTGACCTCAATTCACCAGCAAATAGGAAAAATGGGTCCGAAGAACCGTATGCACAAAAAGTTTGACTGAATGTTAAGCGAAAGACTCGGACGCAGAGACGTGAATATCTTAATACCGCACCAAGTTTTCCATGCGGTGGCCGGAGAGGCATGAACCGGATTGTACCTGTTCCTAACAACCCCACAATACAAACCCATAAGTAAAAAAACGGTACTTGATCAAGCGCCGTTTGTCCCTATTCCAAACACTCTCGCCCAACAGTTCGGAAGAACCCTTGGCACTGTATTCAGCAGTAAGAAGAAACCCGTTAGACTCAACTGGCGAAGCGCCTTTTCCTCGAAGGCCCCCTCTCTCGTTCACAACCAGCTGGCGACATTGGGTATAGGTGAATTACCACTTGATCCGCGCTAACCAGGATGTAACCTAACATGGCATTTCTAAGCGCACAAGTTCCTGTGTGCGCGAAGCCATTCCGGAAAACGATTAAAACCCCAACCGCCGTCCGTATCCGGTTTGGGCAATAGCGTATATAGACAAAACTCATCCATACGCTCGTAGCTCCACCGTCTTCTCCGCCGCCCTGTTTTTAGCTTAAGCACGACAAAGTCGTCTTTACACCAATCGGTTCGAGTCTGGCACATTCTTGCTTCTGTGCACACAATCAAGGTAAACAGCGAACCCACTAGAGCGGGCTGGCAGAGCTCATAGCTTGCCGATAGTCATAAACAGGGCGCCGTTCAGTAATCAACCATGATAGGGGGAAACGCGCGCTTTCAGCGCAAGGATCCTGCACGGGGAGCTCGGGGTCTCGGAGCAGGTATGAACATTTCTACCCCGTTTAAAAATGACACGGGGTACTCACACGCCACAACTAAGAAATGCGATCCCCGGACTTCCCGTATACGAAATTCCTATCTCCTTGCCATTGATTGCTCGTCTTACGGATGGGCTAGAAGGTGTGGCCTGCTTATGCGACTCAACCAGAGTAAAGGTTTTAGGCATAGCAAACTAATGAATTATAAAGAGTACTACTTGGTGCACATAAACATTCCTTCGAAGTGTTAATAGTTGACGCAGTAAGTCATCTCCTAACTAAACTGGCTTTAGGCCTTTTTACAAGTCATGATCTTCGGGCAACCGCTTGTGTTCCGAAGAGCCGGGGTGGAACTTAAGGGCTCTCGGGCTTCCAGGGGTCGAACCATACGGGCGCGGGGCCTCTGCCCGTATGAGCTGTGGTTAACAATGATTGGACTGCAGATTTCCGGCTAGCAACTAGTTAATAATGCGTTTAGTGTAGTGCATTCATATTCTGATTTGCGCCATTAGTAGTCAACCTCACATGGGACAGAGGAGGTAGCGATCCAATGTACACTCAGCCTGAGTGGGACCGTTAGGCGTCGGGACAAAGACTACAGCGTACTAGCTTTTCCGCACCGGCCGTTCCGCCTGCCGAGCAGATTGTTACCATAGTCAGGGTCGCTATACAATAGACTGACGAACCTGGGCCACTGTACTACGCAGCGAAATGCTCTGGGGCATCGTACCTTGGGTAGGGTCACTATTCTGGAGGAGCCTCTTTGGAGGAA";
//        System.out.println(str1);
//        System.out.println("total edit dist " + getNewEditDistance(str1, str2));
//        System.out.println("total edit dist " + getNewEditDistance(str1, str3));
//
//        System.out.println("total new edit dist " + getKbandEditDistanceOld(str1, str2));
////        System.out.println("total new edit dist " + getKbandEditDistanceOld(str1, str3));
//
//        System.out.println("Edit distance1 " + getNewEditDistance(str1.substring(0, str1.length() / 3), str2.substring(0, str2.length() / 3)));
//        System.out.println("Edit distance2 " + getNewEditDistance(str1.substring(str1.length() / 3 + 1, str1.length() * 2 / 3),
//                str2.substring(str2.length() / 3 + 1, str2.length() * 2 / 3)));
//        System.out.println("Edit distance3 " + getNewEditDistance(str1.substring(str1.length() * 2 / 3 + 1, str1.length()),
//                str2.substring(str2.length() * 2 / 3 + 1, str2.length())));
//        System.out.println("diff " + ((new Date()).getTime() - d1.getTime()));
//        System.out.println("distance last " + getNewEditDistance("ACGTGCA", "CGTTCAT"));
//    }
}
