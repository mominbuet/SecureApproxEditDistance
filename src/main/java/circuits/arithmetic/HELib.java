package circuits.arithmetic;

import circuits.CircuitLib;
import static circuits.arithmetic.IntegerLib.S;
import flexsc.CompEnv;
import java.math.BigInteger;
import java.util.Arrays;
import util.Paillier;
import util.Utils;

/**
 *
 * @author momin.aziz.cse @ gmail this class decrypts the biginteger( Paillier
 * crypto-system)
 */
public class HELib<T> extends CircuitLib<T> {

    static final int COUT = 1;

    public HELib(CompEnv<T> e) {
        super(e);
    }

    // full 1-bit adder
    public T[] add(T x, T y, T cin) {
        T[] res = env.newTArray(2);

        T t1 = xor(x, cin);
        T t2 = xor(y, cin);
        res[S] = xor(x, t2);
        t1 = and(t1, t2);
        res[COUT] = xor(cin, t1);

        return res;
    }

    // full n-bit adder
    public T[] addFull(T[] x, T[] y, boolean cin) {
        assert (x != null && y != null && x.length == y.length) : "add: bad inputs.";

        T[] res = env.newTArray(x.length + 1);
        T[] t = add(x[0], y[0], env.newT(cin));
        res[0] = t[S];
        for (int i = 0; i < x.length - 1; i++) {
            t = add(x[i + 1], y[i + 1], t[COUT]);
            res[i + 1] = t[S];
        }
        res[res.length - 1] = t[COUT];
        return res;
    }

    public T[] add(T[] x, T[] y, boolean cin) {
        return Arrays.copyOf(addFull(x, y, cin), x.length);
    }

    public T[] add(T[] x, T[] y) {

        return add(x, y, false);
    }

    public T[] multiplyFull(T[] x, T[] y) {
        return multiplyInternal(x, y);
    }

    private T[] multiplyInternal(T[] x, T[] y) {
//		return karatsubaMultiply(x,y);
        assert (x != null && y != null) : "multiply: bad inputs";
        T[] res = zeros(x.length + y.length);
        T[] zero = zeros(x.length);

        T[] toAdd = mux(zero, x, y[0]);
        System.arraycopy(toAdd, 0, res, 0, toAdd.length);

        for (int i = 1; i < y.length; ++i) {
            toAdd = Arrays.copyOfRange(res, i, i + x.length);
            toAdd = add(toAdd, mux(zero, x, y[i]), false);
            System.arraycopy(toAdd, 0, res, i, toAdd.length);
        }
        return res;
    }

    public T[] sub(T[] x, T[] y) {
        assert (x != null && y != null && x.length == y.length) : "sub: bad inputs.";

        return add(x, not(y), true);
    }

    public T[] leftShift(T[] x) {
        assert (x != null) : "leftShift: bad inputs";
        return leftPublicShift(x, 1);
    }

    public T[] leftPublicShift(T[] x, int s) {
        assert (x != null && s < x.length) : "leftshift: bad inputs";

        T res[] = env.newTArray(x.length);
        System.arraycopy(zeros(s), 0, res, 0, s);
        System.arraycopy(x, 0, res, s, x.length - s);

        return res;
    }

    public T[] divInternal(T[] x, T[] y) {
        T[] PA = zeros(x.length + y.length);
        T[] B = y;
        System.arraycopy(x, 0, PA, 0, x.length);
        for (int i = 0; i < x.length; ++i) {
            PA = leftShift(PA);
            T[] tempP = sub(Arrays.copyOfRange(PA, x.length, PA.length), B);
            PA[0] = not(tempP[tempP.length - 1]);
            System.arraycopy(
                    mux(tempP, Arrays.copyOfRange(PA, x.length, PA.length),
                            tempP[tempP.length - 1]), 0, PA, x.length, y.length);
        }
        return PA;
    }

    public T[] mod(T[] x, T[] y) {
        T Xneg = x[x.length - 1];
        T[] absoluteX = absolute(x);
        T[] absoluteY = absolute(y);
        T[] PA = divInternal(absoluteX, absoluteY);
        T[] res = Arrays.copyOfRange(PA, y.length, PA.length);
        return mux(res, sub(toSignals(0, res.length), res), Xneg);
    }

    public T[] absolute(T[] x) {
        T[] mask = env.newTArray(x.length);
        for (int i = 0; i < x.length; ++i) {
            mask[i] = x[x.length - 1];
        }
        T[] res = add(mask, x);
        return xor(res, mask);
    }

    public T[] decrypt(T[] a, T[] b, T[] nsquare) {

        return mod(multiplyFull(a, b), nsquare);
    }

    public Integer outputToAlice(T[] a) {
        return Utils.toInt(env.outputToAlice(a));
    }

    public T[] inputOfAlice(BigInteger d) {
        return env.inputOfAlice(Utils.fromBigInteger(d, 512));
    }

    public T[] inputOfBob(Integer d) {
        return env.inputOfBob(Utils.fromInt(d, 32));
    }
}
