package org.hjujgfg;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;
import org.hjujgfg.exceptions.FileLoadingException;
import org.hjujgfg.imageprocessing.ImageProcessor;
import org.hjujgfg.imageprocessing.convolve.Convolutor;
import org.hjujgfg.io.FileHelper;
import org.hjujgfg.machinelearning.NeuralNetwork;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {


    private enum Commands {
        CONVOLVE("convolve", "convolves image with specified filter"),
        GRAY("gray", "to grayscale"),
        NN("nn", "run neural network"),
        TEST_NN("testnn", "forward propagate specified vector, NN must be run first"),
        NN_INFO("nninfo", "info about created net"),
        SET_EPOCHS("setepochs", "sets number of learning epochs"),
        SET_LEARNING_RATE("setlr", "sets lerning rate"),
        SET_TRAINING_SIZE("setts", "sets training size"),
        SET_MIN_MAX("setmm", "sets bounds for generated test vector elements"),
        LEARN_MORE("learnmore", "runs training on a new set one more time"),
        READ_SOMETHING("tryread", "tries somthing"),
        NN_FROM_SOMETHING("nn_img", "runs nn on images from dataset"),
        HELP("help", "Show help");

        private String cmd;
        private String description;

        private Commands(String cmd, String description) {
            this.cmd = cmd;
            this.description = description;
        }

        static Commands getByCommand(String command) {
            return Arrays.stream(values())
                    .filter(i -> i.cmd.toLowerCase().equals(command))
                    .findFirst().orElse(HELP);
        }
    }

    private final static Logger log = Logger.getLogger(Main.class);

    private final static String EXIT_TEXT = "exit";

    private static double min = 0;

    private static double max = 1;

    private static int testSetSize = 500;

    private static NeuralNetwork nn;

    public static void main(String[] args) throws IOException, FileLoadingException {
        String text = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!EXIT_TEXT.equals(text)) {
            log.info("Enter text");
            text = reader.readLine();
            parseInputAndRun(text);
        }
    }


    private static void parseInputAndRun(String input) throws FileLoadingException {
        String [] params = input.split(" ");
        String commandStr = params[0];
        String[] args = new String[params.length - 1];
        for (int i = 1; i < params.length; i ++) {
            args[i-1] = params[i];
        }
        Commands command = Commands.getByCommand(commandStr.toLowerCase());
        switch (command) {
            case CONVOLVE:
                doConvolve(args);
                break;
            case GRAY:
                doGray(args);
                break;
            case NN:
                doNeuralNetwork(args);
                break;
            case TEST_NN:
                doTestNN(args);
                break;
            case NN_INFO:
                doGetNNInfo();
                break;
            case SET_EPOCHS:
                NeuralNetwork.EPOCH_NUMBER = Integer.parseInt(args[0]);
                break;
            case SET_LEARNING_RATE:
                NeuralNetwork.LEARNING_RATE = Double.parseDouble(args[0]);
                break;
            case SET_TRAINING_SIZE:
                testSetSize = Integer.parseInt(args[0]);
                break;
            case SET_MIN_MAX:
                doSetMinMax(args);
                break;
            case HELP:
                doHelp();
                break;
            case LEARN_MORE:
                doLearnMore();
                break;
            case READ_SOMETHING:
                doRead(args);
                break;
            case NN_FROM_SOMETHING:
                doNNFromSomething(args);
                break;
            default:
                log.error("Unknown command, please retype");
                doHelp();
        }
    }

    private static void doNNFromSomething(String... args) {
        FileHelper fh = new FileHelper();
        int[] sizes = new int[args.length - 3];
        for (int i = 3; i < args.length; i ++) {
            sizes[i - 3] = Integer.parseInt(args[i]);
        }
        nn = new NeuralNetwork(sizes);
        log.info("Created Net: " + nn.toString());
        try {
            List<RealVector> inputs = fh.readImagesFromMNISTDataBase(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            nn.trainNetwork(inputs, inputs);
            fh.convertRealVectorToImg(Arrays.asList(nn.getResult()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void doRead(String... args) {
        FileHelper fh = new FileHelper();
        try {
            fh.readImagesFromMNISTDataBase(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void doHelp() {
        Arrays.stream(Commands.values()).forEach(c -> {
            log.info(String.format("Command \'%s\': %s\n", c.cmd, c.description));
        });
    }

    private static void doSetMinMax(String... args) {
        min = Double.parseDouble(args[0]);
        max = Double.parseDouble(args[1]);
    }

    private static void doGetNNInfo() {
        log.info("\nPrinting nn info:\n");
        if (nn == null) {
            log.info("Network not created");
        } else {
            log.info(String.format("\nInput size: %d\nOutput size: %d\nNumber of layers: %d\n",
                    nn.getInputSize(), nn.getOutputSize(), nn.getNumberOfLayers()));
        }
        log.info(String.format("\nLearning rate: %f\nTrain set size: %d\nNumber of epochs: %d\n",
                NeuralNetwork.LEARNING_RATE,
                testSetSize,
                NeuralNetwork.EPOCH_NUMBER));
    }

    private static void doTestNN(String... args) {
        double [] test = new double[args.length];
        for (int i = 0; i < test.length; i ++) {
            test[i] = Double.parseDouble(args[i]);
        }
        nn.forwardPropagate(new ArrayRealVector(test));
        log.info(String.format("Calculated result: %s\n", nn.getResult().toString()));
    }

    private static void doNeuralNetwork(String... args) {
        int[] sizes = new int[args.length];
        int i = 0;
        for (String s : args) {
            sizes[i++] = Integer.parseInt(s);
        }
        nn = new NeuralNetwork(sizes);
        log.info("Created Net: " + nn.toString());
        List<RealVector> inputs = createEntries(testSetSize, nn.getInputSize());
        nn.trainNetwork(inputs, inputs);

        /*RealVector expectedOutput = input;
        log.info(String.format("input: %s\n", input.toString()));
        nn.forwardPropagate(input);
        log.info(String.format("Calculated OutPut: %s\n", nn.getResult().toString()));
        nn.backPropagate(input, expectedOutput);
        log.info(String.format("Net after backPropagation:\n%s\n", nn.toString()));*/
    }

    private static void doLearnMore() {
        List<RealVector> inputs = createEntries(testSetSize, nn.getInputSize());
        nn.trainNetwork(inputs, inputs);
    }

    private static List<RealVector> createEntries(int size, int entrySize) {
        List<RealVector> res = new ArrayList<>(size);
        log.info("creating input of size " + size + "\n");
        for (int i = 0; i < size; i ++) {
            RealVector rv = createTestEntry(entrySize);
            res.add(rv);
            //log.info(String.format("Output #%d: %s\n", i, rv.toString()));
        }
        return res;
    }

    private static RealVector createTestEntry(int size) {
        Random r = new Random();
        double[] res = new double[size];
        for (int i = 0; i < size; i ++) {
            res[i] = min + (max - min) * r.nextDouble();
        }
        return new ArrayRealVector(res);
    }


    private static void doConvolve(String... args) throws FileLoadingException {
        Convolutor convolutor = new Convolutor();
        Integer size = null;
        if (args.length == 4) {
            size = Integer.parseInt(args[3]);
        }
        try {
            convolutor.convolve(args[0], args[1], args[2], size);
        } catch (Exception ex) {
            log.error("Something terrible happened", ex);
        }
    }

    private static void doGray(String... args) {
        FileHelper fileHelper = new FileHelper();
        ImageProcessor processor = new ImageProcessor();
        try {
            BufferedImage img = fileHelper.loadImage(args[0]);
            img = processor.toGrayScale(img);
            fileHelper.saveImage(img, args[1]);
        } catch (FileLoadingException ex) {
            log.error("Error loadgin file", ex);
        }
    }
}
