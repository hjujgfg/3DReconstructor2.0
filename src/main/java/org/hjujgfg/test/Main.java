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
import org.hjujgfg.test.representation.PlotDisplay;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private final static Logger log = Logger.getLogger(Main.class);


    public static void main(String... args) {
        Network net = new Network();
        net.init(new String[] {"1", "100", "1"});
        List<TrainingExample> examplesSorted = Utils.createNormalizedSinTrainingSet(360, 1);
        List<TrainingExample> examplesShuffled = Utils.createNormalizedSinTrainingSet(360, 1);
        Collections.shuffle(examplesShuffled);

        XYSeries truth = new XYSeries("Ground truth", true, false);
        XYSeries netResult = new XYSeries("Net results", true, false);

        Thread trainingThread = new Thread(() -> {
            net.train(15000, 0.00003, 0.3, examplesShuffled,
                    truth::addOrUpdate, netResult::addOrUpdate,
                    () -> {
                        log.info("Printing netResult series: ");
                        log.info(netResult.getItems().stream().map(Object::toString).collect(Collectors.joining(", ")));
                        log.info("Printing net: ");
                        log.info(net.toString());
                    }
            );
        });
        PlotDisplay.startViewInSeparateThread(truth, netResult, trainingThread::start);
        //log.info("Testing!");
        //test(net, examplesSorted);
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
        //Plotter.buildGraphs(examples.size(), expected, netOuts);
        //createAndSaveChart(expected, netOuts);
        //log.info("Net: \n" + net.toString());
    }


    private static void createAndSaveChart(List<Double> expected, List<Double> netRes) {
        XYSeries truth = new XYSeries("Ground truth");
        XYSeries netResult = new XYSeries("Net Results");
        for (int i = 0; i < expected.size(); i ++) {
            truth.add(i, expected.get(i));
            netResult.add(i, netRes.get(i));
        }
        XYSeriesCollection xyDataset = new XYSeriesCollection(truth);
        xyDataset.addSeries(netResult);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "ShitFuckGraph",
                "input",
                "output",
                xyDataset
        );
        try {
            ChartUtils.saveChartAsJPEG(new File("chart.jpg"), chart, 500, 300);
        } catch (IOException e) {
            log.error("error saving chart", e);
        }
    }

    private static void testSingle(Map<RealVector,RealVector> set) {
        for (Map.Entry<RealVector, RealVector> entry : set.entrySet()) {
            
        }
    }

}
