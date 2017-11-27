package org.hjujgfg.test;

import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {

    private final static Logger log = Logger.getLogger(Main.class);


    public static void main(String... args) {
        Network net = new Network();
        net.init(new String[] {"1", "500", "1"});
        List<TrainingExample> examplesSorted = Utils.createNormalizedSinTrainingSet(90);
        List<TrainingExample> examplesShuffled = Utils.createNormalizedSinTrainingSet(90);
        Collections.shuffle(examplesShuffled);
        net.train(10000, 0.3, 0.003, examplesShuffled);
        log.info("Testing!");
        test(net, examplesSorted);
    }

    private static void test(Network net, List<TrainingExample> examples) {
        List<Double> expected = new ArrayList<>(examples.size());
        List<Double> netOuts = new ArrayList<>(examples.size());
        for (TrainingExample example : examples) {
            RealVector netOutput = net.run(example.input);
            log.info(String.format("For input: %s, net out: %s, expected out: %s, error: %f",
                    example.input.toString(),
                    netOutput.toString(),
                    example.expectedOutput.toString(),
                    netOutput.getDistance(example.expectedOutput)
                    ));
            expected.add(example.expectedOutput.getEntry(0));
            netOuts.add(netOutput.getEntry(0));
        }
        Plotter.buildGraphs(examples.size(), expected, netOuts);
        log.info("Net: \n" + net.toString());
    }

    private static void testSingle(Map<RealVector,RealVector> set) {
        for (Map.Entry<RealVector, RealVector> entry : set.entrySet()) {
            
        }
    }

}
