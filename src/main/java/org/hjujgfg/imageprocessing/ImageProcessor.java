package org.hjujgfg.imageprocessing;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Created by egor.lapidus on 30/06/2017.
 */
public class ImageProcessor {

    public BufferedImage joinImages(BufferedImage img1, BufferedImage img2) {
        BufferedImage combined = new BufferedImage(img1.getWidth() + img2.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = combined.createGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, img1.getWidth(), 0, null);
        return combined;
    }

    /**
     * IDK WTF is this - analise
     * @param a
     * @param bb
     * @param threshold
     * @return
     */
    public BufferedImage subtract(BufferedImage a, BufferedImage bb, int threshold) {
        if (a.getHeight() != bb.getHeight() || a.getWidth() != bb.getWidth()) {
            throw new IllegalArgumentException("Dimmensions does not correspond");
        }
        BufferedImage res = new BufferedImage(a.getWidth(), a.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < a.getWidth(); x ++) {
            for (int y = 0; y < a.getHeight(); y ++) {
                Color c1 = new Color(a.getRGB(x, y));
                Color c2 = new Color(bb.getRGB(x, y));
                int r = c1.getRed() - c2.getRed();
                int g = c1.getGreen() - c2.getGreen();
                int b = c1.getBlue() - c2.getBlue();
                if (r < threshold && b < threshold && g < threshold) {
                    res.setRGB(x, y, Color.GREEN.getRGB());
                } else {
                    res.setRGB(x, y, c2.getRGB());
                }
            }
        }
        return res;
    }

    public BufferedImage resizeImage(BufferedImage img, int newWidth, int newHeight) {
        if (img == null || newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException(String.format("Cant resize this to %d x %d ", newWidth, newHeight));
        }
        BufferedImage res = new BufferedImage(newWidth, newHeight, img.getType());
        Graphics2D g = res.createGraphics();
        g.drawImage(img, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return res;
    }

    public BufferedImage toGrayScale(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = res.createGraphics();
        g.drawImage(img, 0, 0, null);
        return res;
    }

    /**
     * Rotates specified image
     * @param img image to rotate
     * @param angle counter clockwise angle to rotate
     */
    public void rotateImage(BufferedImage img, double angle) {
        Graphics2D g = img.createGraphics();
        g.rotate(Math.toRadians(angle));
        g.dispose();
    }

}
