package org.hjujgfg.test.representation.gui;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class PlotPanel extends JPanel {

    private final static Logger log = Logger.getLogger(PlotPanel.class);

    private JFreeChart chart;

    public PlotPanel(XYSeries first, XYSeries... series) {
        setMinimumSize(new Dimension(600, 400));
        XYSeriesCollection xyDataset = new XYSeriesCollection(first);
        for (XYSeries s : series) {
            xyDataset.addSeries(s);
        }
        chart = ChartFactory.createXYLineChart(
                "ShitFuckGraph",
                "input",
                "output",
                xyDataset
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumSize(new Dimension(600,400));
        this.add(chartPanel);
    }
}
