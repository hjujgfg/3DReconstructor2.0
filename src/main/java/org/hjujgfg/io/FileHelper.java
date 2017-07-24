package org.hjujgfg.io;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;
import org.hjujgfg.exceptions.FileLoadingException;
import org.hjujgfg.imageprocessing.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hjujgfg.consts.ImageConsts.*;

/**
 * Created by egor.lapidus on 30/06/2017.
 */
public class FileHelper {



    private final static Logger logger = Logger.getLogger(FileHelper.class);

    public BufferedImage loadImage(Path path) throws FileLoadingException {
        File f = path.toFile();
        return loadImage(f);
    }

    public BufferedImage loadImage(String path) throws FileLoadingException {
        logger.info("Started loading " + path);
        File f = new File(path);
        return loadImage(f);
    }

    public BufferedImage loadImage(File path) throws FileLoadingException {
        BufferedImage img = null;
        try {
            img = ImageIO.read(path);
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("error loading " + path + "\n" + e.toString());
            throw new FileLoadingException("Error with file: " + path, e);
        }
        logger.info("Successfully loaded " + path);
        return img;
    }

    public void saveImage(BufferedImage img, String fileName) {
        try {
            ImageIO.write(img, "png", new File(fileName));
            logger.info("Saved image " + fileName);
        } catch (IOException e) {
            logger.error("Error saving image", e);
        }
    }

    public void prepareFilesByModel(String modelName) {
        File f = new File(INPUT_DIR + modelName + ".jpg");
        ImageProcessor processor = new ImageProcessor();
        if (!f.exists()) {
            try {
                FuckingCounter counter = new FuckingCounter();
                Files.walk(Paths.get(RAW_INPUT), 1)
                        .filter(p ->
                                p.toString().contains(modelName) && (p.toString().endsWith(".jpg") || p.toString().endsWith(".png")))
                        .forEach(p -> {
                            try {
                                BufferedImage img = loadImage(p);
                                img = processor.resizeImage(img, WIDTH, HEIGHT);
                                saveImage(img, String.format("%s%s%d.png", INPUT_DIR, READY_MODEL_NAME, counter.getValue()));
                                counter.increase();
                            } catch (FileLoadingException ex) {
                                logger.warn("Error processing path " + p.toString());
                            }
                        });
            } catch (IOException ex) {
                logger.error("error creating model files", ex);
            }
        }
    }

   private class FuckingCounter {
        int value = 0;
        void increase() {
            value ++;
        }
        int getValue() {
            return value;
        }
   }


    public BufferedImage tryGetCurrentModelByCounter(String modelName, int number) throws FileLoadingException {
        File f = new File(String.format("%s%s%d.png", INPUT_DIR, READY_MODEL_NAME, number));
        if (f.exists()) {
            return loadImage(f);
        } else {
            return null;
        }
    }

    /**
     * reads digit images from file of MNIST database format
     * @see http://yann.lecun.com/exdb/mnist/
     * @param file name of the input file
     * @param imgNumber number of images to read and visualize
     * @param offset number of first integers to skip in file
     * @return buffered image of combined stuff
     * @throws IOException u kno
     */
    public List<RealVector> readImagesFromMNISTDataBase(String file, int imgNumber, int offset) throws IOException {
        File f = new File(file);
        InputStream is = new BufferedInputStream(new FileInputStream(f));
        List<Integer> bytes = new ArrayList<>();
        int next;
        int counter = 0;
        while ((next = is.read()) != -1) {
            bytes.add(next);
        }
        int numberOfImages = imgNumber;
        int pixelsPerImg = bytes.size() / 60000;
        logger.info(String.format("Pixels per img: %d\n", pixelsPerImg));
        int hw = (int) Math.sqrt(pixelsPerImg);
        logger.info(String.format("HW: %d\n", hw));
        BufferedImage res = new BufferedImage(hw * numberOfImages, hw, BufferedImage.TYPE_BYTE_GRAY);
        counter = offset;
        logger.info(String.format("printing read values"));
        for (int k = 0; k < numberOfImages; k ++) {
            for (int i = 0; i < hw; i++) {
                for (int j = 0; j < hw; j++) {
                    int val = bytes.get(counter++);
                    int colorRgb = new Color(val, val, val).getRGB();
                    res.setRGB(j + k * hw, i, colorRgb);
                }
            }
        }
        saveImage(res, "digits.png");
        return convertListToRealVector(bytes, numberOfImages);
    }

    private List<RealVector> convertListToRealVector(List<Integer> bytes, int numOfImages) {
        int bytesPerImage = 28 * 28;
        List<RealVector> vectors = new ArrayList<>(numOfImages);
        double[] buffer = new double[bytesPerImage];
        int counter = 0;
        int imageCounter = 0;
        logger.info("NUmber of images: " + numOfImages);
        for (int i = 0; i < bytes.size(); i ++) {
            buffer[counter ++] = normalize(bytes.get(i));
            logger.info(String.format("Counter: %d, bytesPerImg: %d", counter, bytesPerImage));
            if (counter == bytesPerImage) {
                logger.info("Counter value :" + counter);
                vectors.add(new ArrayRealVector(buffer));
                counter = 0;
                imageCounter ++;
            }
            if (imageCounter == numOfImages) {
                break;
            }
        }
        return vectors;
    }

    private double normalize(int value) {
        return (double) value / 255;
    }

    private int denoramalize(double value) {
        int res = (int) (value * 255);
        if (res < 0) {
            res = 0;
        }
        if (res >= 256) {
            res = 255;
        }
        return res;
    }

    /**
     * assumes each vector contains square image
     * @param vectors
     */
    public void convertRealVectorToImg(List<RealVector> vectors) {
        int number = 0;
        for (RealVector v : vectors) {
            int size = v.getDimension();
            int side = (int) Math.sqrt(size);
            int counter = 0;
            BufferedImage img = new BufferedImage(side, side, BufferedImage.TYPE_BYTE_GRAY);
            for (int i = 0; i < side; i ++) {
                for (int j = 0; j < side; j ++) {
                    double intensity = denoramalize(v.getEntry(counter++));
                    Color c = new Color((int) intensity, (int) intensity, (int) intensity);
                    img.setRGB(j, i, c.getRGB());
                }
            }
            saveImage(img, String.format("res%d.png", number++));
        }
    }

}
