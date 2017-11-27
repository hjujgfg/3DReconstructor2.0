package org.hjujgfg.test;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hjujgfg.test.Utils.randomizeMatrix;

public class NN {
    ArrayList<Array2DRowRealMatrix> weights = new ArrayList();
    ArrayList<Array2DRowRealMatrix> deltas = new ArrayList<>();
    ArrayList<RealVector> layerOutputs = new ArrayList<>();
    ArrayList<RealVector> smallDeltas = new ArrayList<>();
    RealVector output;

    public void init(String[] args) {
        int prev = Integer.parseInt(args[0]);
        for (int i = 1; i < args.length; i ++) {
            int dim = Integer.parseInt(args[i]);
            Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(dim, prev);
            randomizeMatrix(matrix);
            weights.add(matrix);
            deltas.add(new Array2DRowRealMatrix(dim, prev));
            smallDeltas.add(new ArrayRealVector(dim));
            prev = dim;
        }
    }



    public void runForward(RealVector input) {
        RealVector toMultiply = input;
        for (Array2DRowRealMatrix w : weights) {
            RealVector out = w.preMultiply(toMultiply);
            RealVector activated = out.map(this::activateSigmoid);
            layerOutputs.add(activated);
            toMultiply = activated;
        }
    }

    private double activateSigmoid(double v) {
        return 1 / (1 - Math.pow(Math.E, - v));
    }

    private double sigmoidDerivative(double v) {
        return v * (1 - v);
    }



    public void backPropagate(RealVector actualOutput) {
        for (int i = weights.size() - 1; i >= 0; i --) {
            if (i == weights.size() - 1) {
                RealVector res = layerOutputs.get(i).subtract(actualOutput);

            }

        }
    }
}
