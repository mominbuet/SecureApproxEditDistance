/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author azizmma
 */
public class SyntheticDataGen {

    public static String images = "imagesColor\\";
    public static String features = "features\\";

    public static String getRandomSeq(int length) {
        String seq = "";
        for (int j = 0; j < length; j++) {
            int rand = ThreadLocalRandom.current().nextInt(0, 3 + 1);
            switch (rand) {
                case 0:
                    seq += "A";
                    break;
                case 1:
                    seq += "T";
                    break;
                case 2:
                    seq += "G";
                    break;
                case 3:
                    seq += "C";
                    break;
            }
        }
        return seq;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        List<String> allSeqs = new ArrayList<String>();
        File file = new File("SyntheticDataFull.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            allSeqs.add(sc.nextLine());
        }
//        List<Integer[][]> listMats = new ArrayList<>();
//        String ref = new Scanner(new File("Ref.txt")).nextLine();
//        System.out.println(ref);
//        char[] refChars = ref.toCharArray();
        for (int index = 0; index < allSeqs.size(); index++) {
            double[][] mat = new double[allSeqs.get(0).length()][allSeqs.get(0).length()];
            char[] chars = allSeqs.get(index).toCharArray();
            char[] refChars = chars;
            for (int i = 0; i < chars.length; i++) {
                for (int j = 0; j < refChars.length; j++) {
                    if (refChars[j] == chars[i]) {
                        mat[i][j] = 255;
                    } else {
                        if (((refChars[j] == 'A' || refChars[j] == 'G') && (chars[j] == 'A' || chars[j] == 'G'))
                                || ((refChars[j] == 'C' || refChars[j] == 'T') && (chars[j] == 'C' || chars[j] == 'T'))) {
                            mat[i][j] = 255 / 4;
                        } else {
                            mat[i][j] = 255 / 16;
                        }
//                        mat[j][i] = 0;
                    }
                }
            }
//            listMats.add(mat);
            //feature extraction block
//            ComogPhogFeatureExtractor comogPhogFeatureExtractor = new ComogPhogFeatureExtractor();
//            String feature = comogPhogFeatureExtractor.runFeatureExtraction(mat, allSeqs.get(0).length());
////            String feature = new GetComogPhogFeature().getFeature(allSeqs.get(index), allSeqs.get(index));
//            File outputfile = new File(features + index + ".txt");
//            try {
//                PrintWriter pw = new PrintWriter(outputfile);
//                pw.println(feature);
//                pw.close();
//            } catch (IOException ex) {
//                Logger.getLogger(SyntheticDataGen.class.getName()).log(Level.SEVERE, null, ex);
//            }
            BufferedImage image = new BufferedImage(allSeqs.get(0).length(), allSeqs.get(0).length(), BufferedImage.TYPE_INT_RGB);
            
            for (int i = 0; i < chars.length; i++) {
                for (int j = 0; j < refChars.length; j++) {
                    int value = (int) mat[j][i] << 16 | (int) mat[j][i] << 8 | (int) mat[j][i];
                    image.setRGB(i, j, value);
                }
            }
            image = resize(image, 512, 512);
            File outputfile = new File(images + index + ".jpg");
            try {
                ImageIO.write(image, "jpg", outputfile);
            } catch (IOException ex) {
                Logger.getLogger(SyntheticDataGen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        double[][] avg = new double[allSeqs.get(0).length()][allSeqs.get(0).length()];
//        File outputfile = new File("avg.txt");
//        PrintWriter pw = new PrintWriter(new FileOutputStream(outputfile, true));
//        for (int i = 0; i < listMats.get(0).length; i++) {
//            for (int j = 0; j < listMats.get(0)[0].length; j++) {
//                double total = 0;
//                for (Integer[][] listMat : listMats) {
//                    total += listMat[i][j];
//                }
//                total = (double) total / listMats.size();
//                avg[i][j] = total;
//                pw.append(avg[i][j]+" ");
//            }
//            pw.append("\r\n");
//        }
//        pw.close();
//        
//        BufferedImage image = new BufferedImage(allSeqs.get(0).length(), allSeqs.get(0).length(), BufferedImage.TYPE_INT_RGB);
//        for (int i = 0; i < allSeqs.get(0).toCharArray().length; i++) {
//            for (int j = 0; j < allSeqs.get(0).toCharArray().length; j++) {
//                int value = (int) avg[j][i] << 16 | (int) avg[j][i] << 8 | (int) avg[j][i];
//                image.setRGB(i, j, value);
//            }
//        }
////            image = resize(image, 64, 64);
//        outputfile = new File("avg.jpg");
//        try {
//            ImageIO.write(image, "jpg", outputfile);
//        } catch (IOException ex) {
//            Logger.getLogger(SyntheticDataGen.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }
}
