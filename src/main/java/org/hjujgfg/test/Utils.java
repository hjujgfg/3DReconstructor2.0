package org.hjujgfg.test;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.hjujgfg.test.to.TrainingExample;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Utils {

    public static void randomizeMatrix(RealMatrix matrix) {
        double rangeMin = 0.0001;
        double rangeMax = 0.1;
        Random r = new Random();
        double previous = r.nextDouble();
        for (int i = 0; i < matrix.getRowDimension(); i ++) {
            for (int j = 0; j < matrix.getColumnDimension(); j ++) {
                double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                matrix.setEntry(i, j, randomValue * previous);
                previous = randomValue;
            }
        }
    }

    public static void addNoiseToMatrix(RealMatrix matrix) {
        double rangeMin = -0.0001;
        double rangeMax = 0.0001;
        Random r = new Random();
        int column = r.nextInt(matrix.getColumnDimension());
        for (int i = 0; i < matrix.getRowDimension(); i ++) {
            //for (int j = 0; j < matrix.getColumnDimension(); j ++) {
            double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            matrix.setEntry(i, column, matrix.getEntry(i, column) + randomValue);
            //}
        }
    }

    public static void randomizeVector(RealVector r) {
        double rangeMin = 0.0001;
        double rangeMax = 0.001;
        Random rand = new Random();
        for (int i = 0; i < r.getDimension(); i ++) {
            double randomValue = rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
            r.setEntry(i, randomValue);
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
            RealVector inp = new ArrayRealVector(new double[]{round(i / 360., 5)});
            double sin = Math.sin(i * Math.PI / 180);
            double doubleSin = Math.sin(2 * i * Math.PI / 180);
            double outValue = scale(sin * sin *  doubleSin, -1, 1, 0, 1);
            //double sin = Math.sin(i * Math.PI / 180);
            RealVector out = new ArrayRealVector(new double[]{outValue});
            examples.add(new TrainingExample(inp, out));
        }
        return examples;
    }

    public static List<TrainingExample> createRandomSet(int size, int bound, int seed) {
        List<TrainingExample> examples = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            RealVector inp = new ArrayRealVector(new double[]{i / 360.});
            //double sin = scale(Math.sin(i * Math.PI / 180), -1, 1, 0, 1);
            Random r = new Random(seed);
            double randValue = r.nextInt();
            RealVector out = new ArrayRealVector(new double[]{randValue});
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

    /**
     * Rounding method from stackoverflow @see https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
     * @param value to round
     * @param places - decimal digits
     * @return rounded double
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
