package org.hjujgfg.imageprocessing.pool;

import org.apache.log4j.Logger;
import org.hjujgfg.exceptions.FileLoadingException;
import org.hjujgfg.io.FileHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class Pooler {

    private final static Logger log = Logger.getLogger(Pooler.class);

    private FileHelper fileHelper = new FileHelper();

    public void pool(String inputPath, String outputPath, int windowSize) throws FileLoadingException {
        log.info("Pooling started");
        BufferedImage img = fileHelper.loadImage(inputPath);
        int newHeight = img.getHeight() / windowSize;
        int remainder = img.getHeight() % windowSize;
        newHeight = remainder > 0 ? newHeight + 1 : newHeight;
        int newWidth = img.getWidth() / windowSize;
        remainder = img.getWidth() % windowSize;
        newWidth = remainder > 0 ? newWidth + 1 : newWidth;
        BufferedImage out = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        int outXCounter = 0;
        int outYCounter = 0;
        for (int x = windowSize; x < img.getWidth(); x += windowSize) { // such implementation seems to be cutting of right edge of the image
            for (int y = windowSize; y < img.getHeight(); y += windowSize) {
                out.setRGB(outXCounter, outYCounter, getMaximums(img.getSubimage(x - windowSize, y - windowSize, windowSize, windowSize)));
                outYCounter ++;
            }
            outXCounter ++;
            outYCounter = 0;
        }
        fileHelper.saveImage(out, outputPath);
        log.info("Pooling ended");
    }

    private int getMaximums(BufferedImage subImage) {
        int[] colors = subImage.getRGB(0,0, subImage.getWidth(), subImage.getHeight(), null, 0, subImage.getWidth());
        return findMaxColor(colors);
    }

    private int findMaxColor(int[] colors) {
        int maxRed = Arrays.stream(colors)
                .mapToObj(Color::new).max(RED_COMPARATOR).get().getRed();
        int maxGreen = Arrays.stream(colors)
                .mapToObj(Color::new).max(GREEN_COMPARATOR).get().getGreen();
        int maxBlue = Arrays.stream(colors)
                .mapToObj(Color::new).max(BLUE_COMPARATOR).get().getBlue();
        return new Color(maxRed, maxGreen, maxBlue).getRGB();

    }

    private final static Comparator<Color> RED_COMPARATOR = Comparator.comparingInt(Color::getRed);
    private final static Comparator<Color> GREEN_COMPARATOR = Comparator.comparingInt(Color::getGreen);
    private final static Comparator<Color> BLUE_COMPARATOR = Comparator.comparingInt(Color::getBlue);

}
