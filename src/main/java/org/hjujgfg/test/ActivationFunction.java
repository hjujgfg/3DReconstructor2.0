package org.hjujgfg.test;

import org.apache.commons.math3.analysis.UnivariateFunction;

public enum ActivationFunction {


    SIGMOID(Utils::sigmoid, Utils::sigmoidDerivative),
    TAHN(Utils::tahn, Utils::tahnDerivation),
    RELU(Utils::relu, Utils::reluDerivative);

    private UnivariateFunction activate;
    private UnivariateFunction derive;

    ActivationFunction(UnivariateFunction act, UnivariateFunction der) {
        this.activate = act;
        this.derive = der;
    }

    public UnivariateFunction getActivationFunction() {
        return activate;
    }

    public UnivariateFunction getDerivationFunction() {
        return derive;
    }
}
