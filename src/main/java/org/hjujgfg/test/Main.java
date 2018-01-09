package org.hjujgfg.test;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;
import org.hjujgfg.exceptions.FileLoadingException;
import org.hjujgfg.io.FileHelper;
import org.hjujgfg.test.net_core.Network;
import org.hjujgfg.test.representation.gui.ImageDisplay;
import org.hjujgfg.test.representation.gui.PlotDisplay;
import org.hjujgfg.test.to.TrainingExample;
import org.hjujgfg.test.utils.Utils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Main {

    private final static Logger log = Logger.getLogger(Main.class);


    public static void main(String... args) {
        /* - images autoencoder */
        //List<BufferedImage> images = Utils.transformDigitsImage("input/digits/digits_compound.png");
        List<BufferedImage> images = Utils.loadDigitsImages("input/digits/digit");
        doImageAutoencoderStuff(images);


        /* - single input autoencoder */
        //doAutoEncoderStuff();

        //doNetStuffOnOneLengthVectors();
    }


    public static void doImageAutoencoderStuff(List<BufferedImage> images) {
        Network net = new Network();
        net.init(new String[] {"100", "64", "64", "100"});
        List<TrainingExample> examplesSorted = images.stream()
                .map(Main::imageToRealVector)
                .map(v -> new TrainingExample(v, v))
                .collect(Collectors.toList());


        ImageDisplay display = new ImageDisplay();
        Thread separateThreadForSomeReason = new Thread(() -> {
            net.train(10000, 0.003, 0.3, examplesSorted,
                    (d,v)->{}, (d,v)->{},
                    (expected, calculated)->{
                        BufferedImage expectedImg = realVectorToBufferedImage(expected);
                        BufferedImage calculatedImg = realVectorToBufferedImage(calculated);
                        display.getUpdater().accept(expectedImg, calculatedImg);
                    },
                    () -> {}
            );
            List<RealVector> netTestResults = test(net, examplesSorted
                    .stream()
                    .map(ex -> ex.input)
                    .collect(Collectors.toList())
            );
            FileHelper fh = new FileHelper();
            int counter = 0;
            for (RealVector rv : netTestResults) {
                fh.saveImage(realVectorToBufferedImage(rv), "output/digits/digit" + counter++ + ".png");
            }
        });

        display.createViewAndStartInSeparateThread(separateThreadForSomeReason::start);

        //separateThreadForSomeReason.start();

    }

    //not used?
    public static List<BufferedImage> splitImgIntoSeveral() {
        FileHelper fh = new FileHelper();
        List<BufferedImage> splitted = new ArrayList<>();
        try {
            BufferedImage all = fh.loadImage("digits_compind.png");
            int step = all.getWidth() / 10;
            for (int x = 0; x < all.getWidth(); x += step) {
                for (int y = 0; y < all.getHeight(); y += step) {
                    splitted.add(all.getSubimage(x, y, step, step));
                }
            }
        } catch (FileLoadingException e) {
            log.error("Error opening file", e);
        }
        return splitted;
    }

    private static RealVector imageToRealVector(BufferedImage img) {
        double[] values = new double[img.getHeight() * img.getWidth()];
        int counter = 0;
        for (int x = 0; x < img.getWidth(); x ++) {
            for (int y = 0; y < img.getHeight(); y ++) {
                values[counter ++] = ((double)(new Color(img.getRGB(x,y)).getRed())) / 255.;
            }
        }
        return new ArrayRealVector(values);
    }

    private static BufferedImage realVectorToBufferedImage(RealVector rv) {
        int sideLength = (int) Math.sqrt(rv.getDimension());
        BufferedImage img = new BufferedImage(sideLength, sideLength, BufferedImage.TYPE_INT_RGB);
        int rowCounter = 0;
        int colCounter = 0;
        for (double d : rv.toArray()) {
            int brightness = getColorBack(d);
            Color c = new Color(brightness, brightness, brightness);
            img.setRGB(rowCounter, colCounter ++, c.getRGB());
            if (colCounter == sideLength) {
                colCounter = 0;
                rowCounter ++;
            }
        }
        return img;
    }

    private static int getColorBack(double value) {
        int val = (int) (value * 255.);
        return val > 255 ? 255 : val;
    }

    public static void doAutoEncoderStuff() {
        Network net = new Network();
        net.init(new String[] {"1", "5", "5", "1"});
        List<TrainingExample> examplesSorted = Utils.createRandomSet(20, 1);
        List<TrainingExample> examplesShuffled = Utils.createRandomSet(20, 1);
        Collections.shuffle(examplesShuffled);

        XYSeries truth = new XYSeries("Ground truth", true, false);
        XYSeries netResult = new XYSeries("Net results", true, false);
        XYSeries testingResults = new XYSeries("Testing results", true, false);

        Thread trainingThread = new Thread(() -> {
            net.train(30000, 0.0003, 0.3, examplesShuffled,
                    truth::addOrUpdate, netResult::addOrUpdate,
                    (r,v)->{},
                    () -> {
                        log.info("Printing netResult series: ");
                        log.info(netResult.getItems().stream().map(Object::toString).collect(Collectors.joining(", ")));
                        log.info("Printing net: ");
                        log.info(net.toString());
                    }
            );
            test(net, examplesSorted, testingResults::addOrUpdate);
        });
        PlotDisplay.startViewInSeparateThread(trainingThread::start, truth, netResult, testingResults);

    }

    public static void doNetStuffOnOneLengthVectors() {
        Network net = new Network();
        net.init(new String[] {"1", "100", "1"});
        List<TrainingExample> examplesSorted = Utils.createNormalizedSinTrainingSet(180, 1);
        List<TrainingExample> examplesShuffled = Utils.createNormalizedSinTrainingSet(90, 2);
        Collections.shuffle(examplesShuffled);

        XYSeries truth = new XYSeries("Ground truth", true, false);
        XYSeries netResult = new XYSeries("Net results", true, false);
        XYSeries testingResults = new XYSeries("Testing results", true, false);

        Thread trainingThread = new Thread(() -> {
            net.train(15000, 0.000003, 0.3, examplesShuffled,
                    truth::addOrUpdate, netResult::addOrUpdate,
                    (r,v)->{},
                    () -> {
                        log.info("Printing netResult series: ");
                        log.info(netResult.getItems().stream().map(Object::toString).collect(Collectors.joining(", ")));
                        log.info("Printing net: ");
                        log.info(net.toString());
                    }
            );
            test(net, examplesSorted, testingResults::addOrUpdate);
        });
        PlotDisplay.startViewInSeparateThread(trainingThread::start, truth, netResult, testingResults);
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

    private static List<RealVector> test(Network net, List<RealVector> inputs) {
        List<RealVector> vectors = new ArrayList<>();
        FileHelper fh = new FileHelper();
        int counter = 0;
        for (RealVector inp : inputs) {
            vectors.add(net.run(inp));
            fh.saveImage(
                    realVectorToBufferedImage(net.getLayerOutputs(0)),
                    "output/digits/intermediate/digit"+ counter ++ + "_1.png"
            );
            fh.saveImage(
                    realVectorToBufferedImage(net.getLayerOutputs(1)),
                    "output/digits/intermediate/digit"+ counter ++ + ".png"
            );
        }
        return vectors;
    }

    private static void test(Network net, List<TrainingExample> examples, BiConsumer<Double, Double> testSeries) {
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
            testSeries.accept(example.input.getEntry(0), netOutput.getEntry(0));
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


}
