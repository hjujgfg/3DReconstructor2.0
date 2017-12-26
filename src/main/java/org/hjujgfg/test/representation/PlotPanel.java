package org.hjujgfg.test.representation;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

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
