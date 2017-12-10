package org.hjujgfg.test;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;
import java.util.function.Function;

public class Utils {

    public static void randomizeMatrix(RealMatrix matrix) {
        Random r = new Random();
        for (int i = 0; i < matrix.getRowDimension(); i ++) {
            for (int j = 0; j < matrix.getColumnDimension(); j ++) {
                matrix.setEntry(i, j, r.nextDouble());
            }
        }
    }

    public static void randomizeVector(RealVector r) {
        Random rand = new Random();
        for (int i = 0; i < r.getDimension(); i ++) {
            r.setEntry(i, rand.nextDouble());
        }
    }


    public static double sigmoid(double val) {
        return 1 / (1 + Math.exp(-val));
    }

    public static double sigmoidDerivative(double val) {
        return val * (1 - val);
    }

    public static double tahn(double val) {
        double ez = Math.exp(val);
        double mez = Math.exp(-val);
        return (ez - mez) / (ez + mez);
    }

    public static double tahnDerivation(double val) {
        return 1 - val * val;
    }

    public static double relu(double val) {
        return val > 0 ? val : 0;
    }

    public static double reluDerivative(double val) {
        return val > 0 ? 1 : 0;
    }


    public static List<TrainingExample> createNormalizedSinTrainingSet(int size, int step) {
        List<TrainingExample> examples = new ArrayList<>(size);
        for (int i = 0; i < size * step; i += step) {
            RealVector inp = new ArrayRealVector(new double[]{i / 360.});
            //double sin = scale(Math.sin(i * Math.PI / 180), -1, 1, 0, 1);
            double sin = Math.sin(i * Math.PI / 180);
            RealVector out = new ArrayRealVector(new double[]{sin});
            examples.add(new TrainingExample(inp, out));
        }
        return examples;
    }

    public static List<TrainingExample> createNormalizedLineTrainingSet(int size, int step) {
        List<TrainingExample> examples = new ArrayList<>(size);
        double k = 7.;
        double b = -12.;
        double max = k * size * step + b;
        for (int i = 0; i < size * step; i += step) {
            RealVector inp = new ArrayRealVector(new double[]{(double)i / ((double)size * step)});
            //double sin = scale(Math.sin(i * Math.PI / 180), -1, 1, 0, 1);
            double value = i * k + b;
            value = scale(value, 0, max, 0, 1);
            RealVector out = new ArrayRealVector(new double[]{value});
            examples.add(new TrainingExample(inp, out));
        }
        return examples;
    }

    private static double scale(double value, double oldMin, double oldMax, double newMin, double newMax) {
        return (((newMax - newMin) * (value - oldMin)) / (oldMax - oldMin) ) + newMin;
    }

}
