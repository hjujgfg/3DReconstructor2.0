package org.hjujgfg.machinelearning;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 12_12 on 09.07.2017.
 */
public class Layer {

    List<Neuron> neurons;
    Neuron normalizer;

    public Layer(int size, int prevLayerSize, boolean hasNormalizer) {
        neurons = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            neurons.add(new Neuron(prevLayerSize + 1));
        }
        if (hasNormalizer) {
            normalizer = Neuron.getNormalizer();
        }
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public RealVector getOutputs() {
        double[] res;
        if (normalizer != null) {
            res = new double[neurons.size() + 1];
        } else {
            res = new double[neurons.size()];
        }
        for (int i = 0; i < neurons.size(); i ++) {
            res[i] = neurons.get(i).getOutput();
        }
        if (normalizer != null) {
            res[res.length - 1] = normalizer.getOutput();
        }
        return new ArrayRealVector(res);
    }

    public RealVector calcErrors(Layer next) {
        double [] errors = new double[neurons.size()];
        for (int i = 0; i < neurons.size(); i ++) {
            double neuronError = next.getErrorDeltaForNeuron(i);
            errors[i] = neuronError;
        }
        return new ArrayRealVector(errors);
    }

    public void setDeltas(RealVector errors) {
        for (int i = 0; i < errors.getDimension(); i ++) {
            neurons.get(i).calcDelta(errors.getEntry(i));
        }
    }

    public int size() {
        if (normalizer == null) {
            return neurons.size();
        } else {
            return neurons.size() + 1;
        }

    }


    public double getErrorDeltaForNeuron(int index) {
        double sumError = 0;
        for (Neuron n : neurons) {
            double weight = n.getWight(index);
            double delta = n.delta;
            sumError += weight * delta;
        }
        return sumError;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------------------\n");
        int i = 0;
        for (Neuron n : neurons) {
            sb.append(String.format("\tNeuron %d:\n%s", i++, n.toString()));
        }
        if (normalizer != null) {
            sb.append(String.format("\tnormalizer: %s", normalizer.toString()));
        } else {
            sb.append("\tNo Normalizer\n");
        }
        sb.append("-------------------------------------\n");
        return sb.toString();
    }

}
