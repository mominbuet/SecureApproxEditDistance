package example;

import circuits.arithmetic.StringLib;
import flexsc.CompEnv;
import gc.BadLabelException;
import util.EvaRunnable;
import util.GenRunnable;
import util.Global;
import util.Utils;

/**
 *
 * @author momin.aziz.cse@gmail.com
 */
public class StringEquality {

    static public <T> T compute(CompEnv<T> gen, T[] inputA, T[] inputB) {

        return new StringLib<T>(gen).eq(inputA, inputB);
    }
//
//    public static class Generator<T> extends GenRunnable<T> {
////data owner
//
//        T[] inputA;
//        T[] inputB;
//        T scResult;
//
//        @Override
//        public void prepareInput(CompEnv<T> gen) {
//            //getting binary from string
//
//            boolean[] in = Utils.fromString(args[0]);
////            System.out.println("input in gen " + in.length);
//            inputA = gen.inputOfAlice(in);
//
//            gen.flush();
////            System.out.println("Input of eva expecting " + 8 * Global.shingleSize);
//            inputB = gen.inputOfBob(new boolean[8 * Global.shingleSize]);
//        }
//
//        @Override
//        public void secureCompute(CompEnv<T> gen) {
//
//            scResult = compute(gen, inputA, inputB);
//        }
//
//        @Override
//        public boolean prepareOutput(CompEnv<T> gen) throws BadLabelException {
//            boolean res = gen.outputToAlice(scResult);
////            for (int i = 0; i < Global.dataOwnerSize; i++) {
////                System.out.println(i + ": " + Utils.toInt(gen.outputToAlice(scResult[i])));
////            }
//            if (res) {
//                System.out.println("Output " + res);//Utils.toString
//            }
//            return res;
//
//        }
//
//    }
//
//    public static class Evaluator<T> extends EvaRunnable<T> {
////researcher
//
//        T[] inputA;
//        T[] inputB;
////        T[][] scResult;
//        T scResult;
//
//        @Override
//        public void prepareInput(CompEnv<T> gen) {
//            boolean[] in = Utils.fromString(args[0]);
////            System.out.println("input in eva " + in.length);
////            inputA = gen.inputOfAlice(new boolean[8 * Global.dataOwnerSize * Global.shingleSize]);
//            inputA = gen.inputOfAlice(new boolean[8 * Global.shingleSize]);
////            System.out.println("Input of gen expecting " + 8 * Global.dataOwnerSize * Global.shingleSize);
//            gen.flush();
//            inputB = gen.inputOfBob(in);
//        }
//
//        @Override
//        public void secureCompute(CompEnv<T> gen) {
//            scResult = compute(gen, inputA, inputB);
//        }
//
//        @Override
//        public boolean prepareOutput(CompEnv<T> gen) throws BadLabelException {
////            Map<Integer, Integer> mapResult = new HashMap<>();
////            for (int i = 0; i < Global.dataOwnerSize; i++) {
////                System.out.println(i + ": " + Utils.toInt(gen.outputToAlice(scResult[i])));
////            }
////            return mapResult;
//            boolean res = gen.outputToAlice(scResult);
////            for (int i = 0; i < Global.dataOwnerSize; i++) {
////                System.out.println(i + ": " + Utils.toInt(gen.outputToAlice(scResult[i])));
////            }
//            if (res) {
//                System.out.println("Output " + res);//Utils.toString    
//            }
//
//            return res;
//
//        }
//    }
}
