package org.hjujgfg.test.representation.gui;

import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;

public class ImageDisplay {

    private JFrame frame;

    private BiConsumer<BufferedImage, BufferedImage> updater;

    private BiConsumer<BufferedImage, BufferedImage> createGUI(Runnable panelReadyCallback) {
        //Create and set up the window.
        frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMinimumSize(new Dimension(600, 400));
        DoubleImagePanel panel = new DoubleImagePanel(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB), new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB));
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {
                panelReadyCallback.run();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        //scrollPane.add(panel);
        //frame.add(scrollPane);
        frame.add(panel);
        return panel.getImageUpdater();
    }

    private void showGUI() {
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void createViewAndStartInSeparateThread(Runnable panelReadyCallback) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        this.updater = createGUI(panelReadyCallback);
        javax.swing.SwingUtilities.invokeLater(this::showGUI);
    }


    public BiConsumer<BufferedImage, BufferedImage> getUpdater() {
        return this.updater;
    }

}
