package org.hjujgfg.test.net_core;

import org.apache.commons.math3.linear.*;
import org.hjujgfg.test.Utils;

public class Layer {

    private RealMatrix weights;
    private RealMatrix deltasWeights;
    private RealMatrix partialDerivativeWeights;
    private RealVector biases;
    private RealVector deltasBiases;
    private RealVector partialDerivativesBiases;

    private RealVector errorTerms;
    private RealVector zeds;
    RealVector outputs;
    RealVector inputs;

    private ActivationFunction activationFunction;

    public Layer(int neurons, int prevNeurons, ActivationFunction activationFunction) {
        weights = new Array2DRowRealMatrix(neurons, prevNeurons);
        Utils.randomizeMatrix(weights);
        deltasWeights = new Array2DRowRealMatrix(neurons, prevNeurons);
        partialDerivativeWeights = new Array2DRowRealMatrix(neurons, prevNeurons);

        biases = new ArrayRealVector(neurons);
        Utils.randomizeVector(biases);
        deltasBiases = new ArrayRealVector(neurons);
        partialDerivativesBiases = new ArrayRealVector(neurons);

        this.activationFunction = activationFunction;
    }

    public void activate(RealVector prevOutputs) {
        zeds = weights.operate(prevOutputs).add(biases);
        outputs = zeds.map(activationFunction.getActivationFunction());
        inputs = prevOutputs;
    }

    public RealVector computeErrors(RealVector preparedVector, boolean isLast) {
        errorTerms = preparedVector.ebeMultiply(outputs.map(activationFunction.getDerivationFunction()));
        return errorTerms;
    }

    RealMatrix getWeights() {
        return weights;
    }

    RealMatrix getWeightsTransposed() {
        return weights.transpose();
    }


    public void computePartialDerivatives(RealVector prevOutputs) {
        partialDerivativeWeights = errorTerms.outerProduct(prevOutputs);
        partialDerivativesBiases = errorTerms;
    }

    public void updateDeltas() {
        deltasWeights = deltasWeights.add(partialDerivativeWeights);
        deltasBiases = deltasBiases.add(partialDerivativesBiases);
    }


    public void updateWeights(double alpha, double lambda, int trainNumber) {
        weights = weights
                .subtract(
                    weights.scalarMultiply(lambda)
                    .add(deltasWeights.scalarMultiply(1./trainNumber))
                    .scalarMultiply(alpha)
                );
        biases = biases.subtract(
                deltasBiases.mapMultiply(1./trainNumber)
                    .mapMultiply(alpha)
                );
    }


    public double getWeightsSum() {
        double sum = 0;
        RealMatrixPreservingVisitor visitor = new SummatingVisitor();
        return weights.walkInOptimizedOrder(visitor);
    }


    private class SummatingVisitor extends DefaultRealMatrixPreservingVisitor implements RealMatrixPreservingVisitor {
        double sum = 0;
        public void visit(int row, int column, double value) {
            sum += value * value;
        }
        public double end() {
            return sum;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nWeights: \n").append(weights.toString())
                .append("\nBiases: \n").append(biases.toString())
                .append("\nInputs: \n").append(inputs.toString())
                .append("\nOutputs: \n").append(outputs.toString())
                .append("\n");
        return sb.toString();
    }
}
