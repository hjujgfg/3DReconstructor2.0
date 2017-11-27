package org.hjujgfg.test;

import org.apache.commons.math3.linear.RealVector;

public class TrainingExample {

    public RealVector input;
    public RealVector expectedOutput;

    public TrainingExample(RealVector input, RealVector expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }
}
