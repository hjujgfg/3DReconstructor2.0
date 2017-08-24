package org.hjujgfg.machinelearning;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;
import java.util.Random;

import static org.hjujgfg.machinelearning.MathUtils.derivative;
import static org.hjujgfg.machinelearning.MathUtils.sigm;

/**
 * Created by 12_12 on 09.07.2017.
 */
public class Neuron {


    public RealVector weightsS;
    public List<Double> weights;

    public Double output;

    public Double delta;

    private static Random r = new Random();

    private Neuron(){
        output = Double.valueOf(1);
    }

    public Neuron(int weightsSize) {
        weightsS = new ArrayRealVector(weightsSize);
        for (int i = 0; i < weightsSize; i ++) {
            weightsS.addToEntry(i, r.nextDouble());
        }
    }

    public double activate(RealVector inputs) {
        output = sigm(weightsS.dotProduct(inputs));
        return output;
    }

    public void calcDelta(double error) {
        delta = error * derivative(output);
    }

    public void updateWeights(RealVector prevOutputs) {
        RealVector scaled = prevOutputs.mapMultiply(NeuralNetwork.LEARNING_RATE_CHANGEABLE * delta);
        weightsS = weightsS.add(scaled);
    }

    public double getWight(int index) {
        return weightsS.getEntry(index);
    }

    public double getOutput() {
        return output;
    }

    public static Neuron getNormalizer(){
        return new Neuron();
    }

    public String toString() {
        return String.format("\t\tWeights: %s\n\t\tOutput: %s\n\t\tDelta:%s\n",
                weightsS != null ? weightsS.toString() : NULL,
                output != null ? output.toString() : NULL,
                delta != null ? delta.toString() : NULL);
    }

    private final static String NULL = "null";
}
