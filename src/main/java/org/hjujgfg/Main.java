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
import java.util.Random;

public class Main {


    private final static Logger log = Logger.getLogger(Main.class);

    private final static String EXIT_TEXT = "exit";

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
        String command = params[0];
        String[] args = new String[params.length - 1];
        for (int i = 1; i < params.length; i ++) {
            args[i-1] = params[i];
        }
        switch (command) {
            case "convolve":
                doConvolve(args);
                break;
            case "gray":
                doGray(args);
                break;
            case "nn":
                doNeuralNetwork(args);
                break;
        }
    }

    private static void doNeuralNetwork(String... args) {
        int[] sizes = new int[args.length];
        int i = 0;
        for (String s : args) {
            sizes[i++] = Integer.parseInt(s);
        }
        NeuralNetwork nn = new NeuralNetwork(sizes);
        log.info("Created Net: " + nn.toString());
        RealVector input = createTestEntry(nn.getInputSize());
        RealVector expectedOutput = input;
        log.info(String.format("input: %s\n", input.toString()));
        nn.forwardPropagate(input);
        log.info(String.format("Calculated OutPut: %s\n", nn.getResult().toString()));
        nn.backPropagate(input, expectedOutput);
        log.info(String.format("Net after backPropagation:\n%s\n", nn.toString()));
    }

    private static RealVector createTestEntry(int size) {
        Random r = new Random();
        double[] res = new double[size];
        for (int i = 0; i < size; i ++) {
            res[i] = r.nextDouble();
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
