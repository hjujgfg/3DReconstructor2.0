package org.hjujgfg.test;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {

    private final static Logger log = Logger.getLogger(Main.class);


    public static void main(String... args) {
        Network net = new Network();
        net.init(new String[] {"1"});
        List<TrainingExample> examplesSorted = Utils.createNormalizedSinTrainingSet(9, 10);
        List<TrainingExample> examplesShuffled = Utils.createNormalizedSinTrainingSet(20, 10);
        Collections.shuffle(examplesShuffled);
        net.train(15000, 0.003, 0.0000000003, examplesSorted);
        log.info("Testing!");
        test(net, examplesSorted);
    }


    /*private static void optimizator(int iterations)  {

        GradientMultivariateOptimizer optimizer = new NonLinearConjugateGradientOptimizer(
                NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                (iteration, previous, current) -> iteration >= iterations || previous.getPoint()[0] >= current.getPoint()[0]
        );
        optimizer.optimize(new ObjectiveFunction(v -> ));
    }*/

    private static void test(Network net, List<TrainingExample> examples) {
        List<Double> expected = new ArrayList<>(examples.size());
        List<Double> netOuts = new ArrayList<>(examples.size());
        double inputChecker = Double.MAX_VALUE;
        for (TrainingExample example : examples) {
            RealVector netOutput = net.run(example.input);
            if (inputChecker == example.input.getEntry(0)) {
                log.error(String.format("set is not generated correctly! inputs are equal: %f", inputChecker ));
            }
            inputChecker = example.input.getEntry(0);
            log.info(String.format("For input: %f, net out: %f, expected out: %s, error: %f",
                    example.input.getEntry(0),
                    netOutput.getEntry(0),
                    example.expectedOutput.toString(),
                    netOutput.getDistance(example.expectedOutput)
                    ));
            expected.add(example.expectedOutput.getEntry(0));
            netOuts.add(netOutput.getEntry(0));
        }
        Plotter.buildGraphs(examples.size(), expected, netOuts);
        //log.info("Net: \n" + net.toString());
    }

    private static void testSingle(Map<RealVector,RealVector> set) {
        for (Map.Entry<RealVector, RealVector> entry : set.entrySet()) {
            
        }
    }

}
