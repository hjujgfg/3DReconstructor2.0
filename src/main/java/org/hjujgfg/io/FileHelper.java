package org.hjujgfg.io;

import org.apache.log4j.Logger;
import org.hjujgfg.exceptions.FileLoadingException;
import org.hjujgfg.imageprocessing.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

}
