package org.hjujgfg;

import org.apache.log4j.Logger;
import org.hjujgfg.exceptions.FileLoadingException;
import org.hjujgfg.imageprocessing.ImageProcessor;
import org.hjujgfg.imageprocessing.convolve.Convolutor;
import org.hjujgfg.io.FileHelper;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {


    private final static Logger log = Logger.getLogger(Main.class);

    private final static String EXIT_TEXT = "exit";

    public static void main(String[] args) throws IOException, FileLoadingException {
        String text = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!EXIT_TEXT.equals(text)) {
            System.out.println("Enter text");
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
        }
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
