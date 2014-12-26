package jFreeChart;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Test extends JFrame{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		Test frame = new Test();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(1,1,500,500);
		frame.setTitle("test");
		frame.setVisible(true);

	}
	Test(){
	    JFreeChart chart =
	      ChartFactory.createScatterPlot("来店者傾向",
	                                     "temp",
	                                     "count",
	                                     createData(),
	                                     PlotOrientation.VERTICAL,
	                                     true,
	                                     false,
	                                     false);

	    XYPlot plot = (XYPlot)chart.getPlot();
	    plot.setBackgroundPaint(Color.ORANGE);

	    ChartPanel cpanel = new ChartPanel(chart);
	    getContentPane().add(cpanel, BorderLayout.CENTER);
	  }

	  private XYSeriesCollection createData(){
	    XYSeriesCollection data = new XYSeriesCollection();

	    int xdata1[] = {10, 15, 20, 22, 24, 26,  4,  8, 28, 30, 25, 12, 33};
	    int ydata1[] = {26, 42, 54, 56, 52, 58, 20, 24, 51, 49, 54, 38, 44};

	    int xdata2[] = {29, 14, 12, 20, 28, 35, 32, 31, 25};
	    int ydata2[] = {52,  7,  6,  5, 56, 64, 58, 62, 30};

	    XYSeries series1 = new XYSeries("動物園");

	    for (int i = 0 ; i < 13 ; i++){
	      series1.add(xdata1[i], ydata1[i]);
	    }

	    XYSeries series2 = new XYSeries("アイスクリーム屋");

	    for (int i = 0 ; i < 9 ; i++){
	      series2.add(xdata2[i], ydata2[i]);
	    }

	    data.addSeries(series1);
	    data.addSeries(series2);

	    return data;
	  }
}
