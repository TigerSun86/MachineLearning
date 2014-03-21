package util;

/**
 * FileName: DisplayChart.java
 * @Description: Display a chart by given data.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Feb 25, 2014
 */
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class DisplayChart {
    private static final double FACTOR = 0.05;
    public static void display (
            final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet,
            final String windowTitle, final String chartTitle, final String xAxisTitle, final String yAxisTitle) {
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Entry<String, LinkedHashMap<Double, Double>> g : dataSet
                .entrySet()) { // Each group.
            final String groupName = g.getKey();
            for (Entry<Double, Double> m : g.getValue().entrySet()) {
                final double value = m.getValue();
                // Each pair of data.
                dataset.addValue(value, groupName, m.getKey());
                if (max < value) {
                    max = value;
                }
                if (min > value) {
                    min = value;
                }
            }
        }
        if (Double.compare(max, Double.NEGATIVE_INFINITY) == 0) { // No data.
            max = 0;
            min = 0;
        }

        JFreeChart chart =
                ChartFactory.createLineChart(chartTitle,
                        xAxisTitle, yAxisTitle, dataset,
                        PlotOrientation.VERTICAL, true, true, true);
        CategoryPlot plot = chart.getCategoryPlot();
        max += Math.abs(max * FACTOR);
        min -= Math.abs(min * FACTOR);
        plot.getRangeAxis().setUpperBound(max);
        plot.getRangeAxis().setLowerBound(min);

        LineAndShapeRenderer lineAndShapeRenderer =
                (LineAndShapeRenderer) plot.getRenderer();
        lineAndShapeRenderer.setBaseLinesVisible(true);
        lineAndShapeRenderer.setBaseShapesVisible(true);
        lineAndShapeRenderer
                .setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        lineAndShapeRenderer.setBaseItemLabelsVisible(true);

        ChartFrame frame = new ChartFrame(windowTitle, chart);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void display (final ChartData cd) {
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Entry<String, LinkedHashMap<Double, Double>> g : cd.dataSet
                .entrySet()) { // Each group.
            final String groupName = g.getKey();
            for (Entry<Double, Double> m : g.getValue().entrySet()) {
                final double value = m.getValue();
                // Each pair of data.
                dataset.addValue(value, groupName, m.getKey());
                if (max < value) {
                    max = value;
                }
                if (min > value) {
                    min = value;
                }
            }
        }
        if (Double.compare(max, Double.NEGATIVE_INFINITY) == 0) { // No data.
            max = 0;
            min = 0;
        }

        JFreeChart chart =
                ChartFactory.createLineChart(cd.chartTitle,
                        cd.categoryAxisLabel, cd.valueAxisLabel, dataset,
                        PlotOrientation.VERTICAL, true, true, true);
        CategoryPlot plot = chart.getCategoryPlot();

        max += Math.abs(max * FACTOR);
        min -= Math.abs(min * FACTOR);
        plot.getRangeAxis().setUpperBound(max);
        plot.getRangeAxis().setLowerBound(min);

        LineAndShapeRenderer lineAndShapeRenderer =
                (LineAndShapeRenderer) plot.getRenderer();
        lineAndShapeRenderer.setBaseLinesVisible(true);
        lineAndShapeRenderer.setBaseShapesVisible(true);
        lineAndShapeRenderer
                .setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        lineAndShapeRenderer.setBaseItemLabelsVisible(true);

        ChartFrame frame = new ChartFrame(cd.windowTitle, chart);
        frame.pack();
        frame.setVisible(true);
    }
}
