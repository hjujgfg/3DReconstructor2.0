package org.hjujgfg.machinelearning;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.ml.neuralnet.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 12_12 on 09.07.2017.
 */
public class NeuralNetwork {


    private final static Logger log = Logger.getLogger(NeuralNetwork.class);
    int nHiddenLayers;
    int inputSize;
    List<Layer> nn;

    public static double LEARING_RATE = 0.5d;
    public static int EPOCH_NUMBER = 100;

    /**
     * Creates and initializes neural network with random values
     * @param layersSizes - array of sizes, where 0th element represents dimensionality of imput vector,
     *                    last element - dimensionality of output vector,
     *                    all in between - dimensionality of respective hidden layers
     */
    public NeuralNetwork (int[] layersSizes) {
        inputSize = layersSizes[0];
        nn = new ArrayList<>(layersSizes.length - 1);
        for (int i = 1; i < layersSizes.length; i ++) {
            if (i == 1) {
                nn.add(new Layer(layersSizes[i], layersSizes[i - 1] - 1, i != layersSizes.length - 1));
            } else {
                nn.add(new Layer(layersSizes[i], layersSizes[i - 1], i != layersSizes.length - 1));
            }
        }
    }

    public RealVector forwardPropagate(RealVector input) {
        RealVector inp = input.copy();
        for (Layer layer : nn) {
            for (Neuron neuron : layer.getNeurons()) {
                neuron.activate(inp);
            }
            inp = layer.getOutputs();
        }
        return nn.get(nn.size() - 1).getOutputs();
    }

    public void backPropagate(RealVector input, RealVector expectedOutput) {
        for (int i = nn.size() - 1; i >= 0; i--) {
            Layer l = nn.get(i);
            RealVector errors;
            //log.info("Back propagating layer " + i);
            if (i == nn.size() - 1) {
                errors = expectedOutput.subtract(l.getOutputs());
            } else {
                errors = l.calcErrors(nn.get(i + 1));
            }
            l.setDeltas(errors);
            //log.info(String.format("Layer #%d: \n%s\n", i, l.toString()));
        }
    }

    private void updateWeights(RealVector nnInput) {
        nn.get(0).updateWeights(nnInput);
        for (int i = 1; i < nn.size(); i ++) {
            nn.get(i).updateWeights(nn.get(i - 1).getOutputs());
        }
    }

    public void trainNetwork(List<RealVector> inputData, List<RealVector> realOutputs) {
        log.info(String.format("Training network on %d expamples, in %d epochs", inputData.size(), EPOCH_NUMBER));
        for (int i = 0; i < EPOCH_NUMBER; i ++) {
            double sumError = 0;
            for (int j = 0; j < inputData.size(); j ++) {
                RealVector currentInput = inputData.get(j);
                RealVector output = forwardPropagate(currentInput);
                RealVector expected = realOutputs.get(j);
                sumError += output.getDistance(expected);
                backPropagate(currentInput, expected);
                updateWeights(currentInput);
            }
            log.info(String.format("Epoch: %d, summError: %f", i, sumError));
        }
    }

    public int getInputSize() {
        return inputSize;
    }

    public int getOutputSize() {
        return nn.get(nn.size() - 1).size();
    }

    public int getNumberOfLayers() {
        return nn.size();
    }

    public RealVector getResult() {
        return nn.get(nn.size() - 1).getOutputs();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Printing network:\n");
        int i = 0;
        for (Layer l : nn) {
            sb.append(String.format("Layer %s\n", i++));
            sb.append(l.toString());
        }
        return sb.toString();
    }

}
