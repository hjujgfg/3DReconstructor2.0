package org.hjujgfg.test.net_core;


import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;
import org.hjujgfg.test.to.TrainingExample;

import java.util.*;
import java.util.function.BiConsumer;


public class Network {

    private final static Logger log = Logger.getLogger(Network.class);

    private List<Layer> layers;
    private RealVector input;
    private RealVector output;

    public void init(String[] args) {
        int prev = Integer.parseInt(args[0]);
        layers = new ArrayList<>();
        for (int i = 1; i < args.length; i ++) {
            int dim = Integer.parseInt(args[i]);
            Layer layer = new Layer(dim, prev, ActivationFunction.SIGMOID);
            layers.add(layer);
            prev = dim;
        }
    }


    public RealVector run(RealVector input) {
        this.input = input;
        RealVector prevOutput = input;
        for (Layer l : layers) {
            l.activate(prevOutput);
            prevOutput = l.outputs;
        }
        this.output = prevOutput;
        return prevOutput;
    }

    public void back(RealVector expectedOutput) {
        RealVector nextErrors = expectedOutput;
        Layer previousVisited = null;
        for (int i = layers.size() - 1; i >= 0; i --) {
            Layer l = layers.get(i);
            if (i == layers.size() - 1) {
                nextErrors = l.computeErrors(l.outputs.subtract(expectedOutput), true);
            } else {
                nextErrors = l.computeErrors(previousVisited.getWeightsTransposed().operate(nextErrors), false);
            }
            previousVisited = l;
        }
        RealVector prevOutputs = input;
        for (Layer l : layers) {
            l.computePartialDerivatives(prevOutputs);
            l.updateDeltas();
            prevOutputs = l.outputs;
        }
    }

    public void train(int epochs, double alpha, double lambda, List<TrainingExample> trainSet,
                      BiConsumer<Double, Double> truthUpdater,
                      BiConsumer<Double, Double> resUpdater,
                      Runnable finalizer) {
        double prevError = Double.MAX_VALUE;
        for (int i = 0; i < epochs; i ++) {
            double summError = 0;
            for (TrainingExample example : trainSet) {
                RealVector calculated = run(example.input);
                back(example.expectedOutput);
                double rootDistance = calculated.getDistance(example.expectedOutput);
                summError += 0.5 * rootDistance * rootDistance;

                layers.forEach(l -> l.updateWeights(alpha, lambda, 1));
                if (i == 0) {
                    truthUpdater.accept(example.input.getEntry(0), example.expectedOutput.getEntry(0));
                }
                resUpdater.accept(example.input.getEntry(0), calculated.getEntry(0));
            }
            double weightsSum = calcWeightsSum();
            double error =  1 / trainSet.size() * summError + lambda * 0.5 * weightsSum;
            log.info(String.format("Epoch #%d: Error = %f, errorChange = %f, alpha = %f, lambda = %f, weightsSum = %f", i, error, error - prevError, alpha, lambda, weightsSum));
            if (error > prevError) {
                //log.info("Shit happened, current error is higher that previous one, stopping without weights update");
                //toString();
                //break;
            }
            prevError = error;
            //layers.forEach(l -> l.updateWeights(alpha, lambda, trainSet.size()));
        }
        finalizer.run();
    }


    private double calcWeightsSum() {
        return layers.stream().mapToDouble(Layer::getWeightsSum).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Num of layers: ")
                .append(layers.size())
                .append(";\n")
                .append("Input: ").append(input.toString())
                .append("\nOutput: ").append(output.toString());
        int i = 0;
        for (Layer l : layers) {
            sb.append("\nLayer #").append(i).append("\n");
            sb.append(l.toString());
            i++;
        }
        return sb.toString();
    }
}
