package org.hjujgfg.test.representation.file;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Plotter {


    public static BufferedImage createImg(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public static void buildGraphs(int itemNumber, List<Double>... lists) {
        int verticalScale = 1000;
        BufferedImage img = createImg(itemNumber, verticalScale);
        Graphics2D g = img.createGraphics();
        g.setBackground(Color.WHITE);
        Iterator<Color> colorIterator = colors.iterator();
        for (List<Double> list : lists) {
            Color c = colorIterator.next();
            int i = 0;
            g.setColor(c);
            for (Double d : list) {
                g.drawOval(i, (int)(d * verticalScale), 2, 2);
                i ++;
            }
        }
        try {
            ImageIO.write(img, "png", new File("graph.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private final static List<Color> colors = new ArrayList<>();
    static {
        colors.add(Color.BLACK);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
    }
}
