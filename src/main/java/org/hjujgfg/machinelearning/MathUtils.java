package org.hjujgfg.machinelearning;

/**
 * Created by 12_12 on 09.07.2017.
 */
public class MathUtils {

    public static double sigm(double val) {
        return 1.d / (1.d + Math.exp(-val));
    }

    public static double derivative(double val) {
        return val * (1 - val);
    }
}
