package example;

import util.EvaRunnable;
import util.GenRunnable;
import circuits.arithmetic.StringLib;
import flexsc.CompEnv;
import gc.BadLabelException;
import util.Utils;

public class EditDistanceString {

    static public <T> T[] compute(CompEnv<T> gen, T[] inputA, T[] inputB) {
        //(T[]) new Object[];
//        T[] t = new IntegerLib<T>(gen).hammingDistance(Arrays.copyOfRange(inputA, 0, inputA.length), Arrays.copyOfRange(inputB, 0, inputB.length));
//        /*
//         this block is not working
//         */
//        T[] ret = gen.newTArray(t.length);
//        boolean init = false;
//        for (int i = 0; i < ret.length; i++) {
//            ret[i] = gen.newT(false);
//        }
//        int res = 0;
//
//        for (int i = 0; i < inputA.length; i += 8) {
//            T[] tmp = new IntegerLib<T>(gen).hammingDistance(Arrays.copyOfRange(inputA, i, i + 8), Arrays.copyOfRange(inputB, i, i + 8));
//            if (Utils.toInt(gen.outputToAlice(tmp)) > 0) {
//                if (init) {
//                    ret = new IntegerLib<T>(gen).add(tmp, ret);
//
//                } else {
//                    System.arraycopy(ret, 0, tmp, 0, tmp.length);
//                    init = true;
//                }
//                res++;
////                System.out.println("Result " + res);
//            }
//        }

        //gen.flush();
        //System.out.println(Arrays.toString(gen.outputToAlice(ret)));
//        return ret;
        return new StringLib<T>(gen).editDistanceKBand(inputA, inputB);//editDistance
    }

    public static class Generator<T> extends GenRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
            //getting binary from string
//            System.out.println("input in gen " + args[0]);
            boolean[] in = Utils.fromString(args[0]);
            inputA = gen.inputOfAlice(in);

            gen.flush();
//            System.out.println("Input: "+Utils.toString(gen.outputToAlice(inputA)));
            inputB = gen.inputOfBob(new boolean[in.length]);
        }

        @Override
        public void secureCompute(CompEnv<T> gen) {

            scResult = compute(gen, inputA, inputB);
        }

        @Override
        public int prepareOutput(CompEnv<T> gen) throws BadLabelException {
//            System.out.println("Output boolean " +Arrays.toString( Utils.toBooleanArray(gen.outputToAlice(scResult))));
//            gen.flush();
//            System.out.println(gen.outputToAlice(scResult));
            return Utils.toInt(gen.outputToAlice(scResult));
//            System.out.println("Output " + Utils.toInt(gen.outputToAlice(scResult)));//Utils.toString
        }

    }

    public static class Evaluator<T> extends EvaRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
            boolean[] in = Utils.fromString(args[0]);
//            System.out.println("input in eva " + args[0]);
            inputA = gen.inputOfAlice(new boolean[in.length]);
            gen.flush();
            inputB = gen.inputOfBob(in);
        }

        @Override
        public void secureCompute(CompEnv<T> gen) {
            scResult = compute(gen, inputA, inputB);
        }

        @Override
        public int prepareOutput(CompEnv<T> gen) throws BadLabelException {
//            gen.outputToAlice(scResult);
            return Utils.toInt(gen.outputToAlice(scResult));
//             System.out.println("Output " + Utils.toInt(gen.outputToAlice(scResult)));//Utils.toString
        }
    }
}
