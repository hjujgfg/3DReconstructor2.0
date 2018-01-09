package org.hjujgfg.test.representation.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;

public class DoubleImagePanel extends JPanel {

    private ImagePanel leftPanel, rightPanel;

    private BiConsumer<BufferedImage, BufferedImage> updater = (img1, img2) -> {
        if (img1 != null) {
            leftPanel.changeImage(img1);
        }
        if (img2 != null) {
            rightPanel.changeImage(img2);
        }
    };

    public DoubleImagePanel(BufferedImage img1, BufferedImage img2) {
        super(new GridLayout(1, 2));
        leftPanel = new ImagePanel(img1);
        rightPanel = new ImagePanel(img2);
        add(leftPanel);
        add(rightPanel);
    }

    public BiConsumer<BufferedImage, BufferedImage> getImageUpdater() {
        return updater;
    }

    private class ImagePanel extends JPanel {

        private BufferedImage img;

        ImagePanel (BufferedImage img) {
            super();
            this.img = img;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, this); // see javadoc for more info on the parameters
        }

        void changeImage(BufferedImage img) {
            this.img = img;
            this.repaint();
        }

    }

}
