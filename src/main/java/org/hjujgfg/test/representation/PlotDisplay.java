package org.hjujgfg.test.representation;

import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PlotDisplay {

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI(Runnable chartStartedCallback, XYSeries truth, XYSeries... series) {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMinimumSize(new Dimension(600, 400));
        JPanel panel = new PlotPanel(truth, series);
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
                chartStartedCallback.run();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        //scrollPane.add(panel);
        //frame.add(scrollPane);
        frame.add(panel);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void startViewInSeparateThread(Runnable chartStartedCallback, XYSeries truth, XYSeries... other) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI(chartStartedCallback, truth, other);
        });
    }

}
