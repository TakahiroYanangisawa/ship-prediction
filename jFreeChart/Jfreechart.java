package jFreeChart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;


public class Jfreechart extends JFrame{
	double[] pre_xdata,pre_ydata,rea_xdata,rea_ydata;
	XYPlot plot = new XYPlot();
	public Jfreechart(double[][] forpaint, String string){
		XYSeriesCollection data = new XYSeriesCollection();
		JFreeChart chart =
				ChartFactory.createScatterPlot("",
						"lon",
						"lat",
						createData(forpaint),
						PlotOrientation.VERTICAL,
						true,
						true,
						true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(1,1,500,500);
		setVisible(false);
		TextTitle title = new TextTitle(string);
		title.setFont(new Font("MS 明朝", Font.PLAIN,28));
		title.setPosition(RectangleEdge.TOP);

		chart.setTitle(title);

		plot = (XYPlot)chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);

		ChartPanel cpanel = new ChartPanel(chart);
		getContentPane().add(cpanel, BorderLayout.CENTER);

	}
	public  XYSeriesCollection createData(double[][] forpaint){
		XYSeriesCollection data = new XYSeriesCollection();

		pre_xdata = new double[forpaint.length];//全部緯度経度
		pre_ydata = new double[forpaint.length];
		rea_xdata = new double[forpaint.length];
		rea_ydata = new double[forpaint.length];

		for(int i = 0; i<forpaint.length ; i++){
			pre_xdata[i] = forpaint[i][0];
			pre_ydata[i] = forpaint[i][1];
			rea_xdata[i] = forpaint[i][2];
			rea_ydata[i] = forpaint[i][3];

		}
		XYSeries series1 = new XYSeries("prediction");
		XYSeries series2 = new XYSeries("real");

		for (int i = 0 ; i < forpaint.length ; i++){
			series1.add(pre_ydata[i], pre_xdata[i]);
			series2.add(rea_ydata[i], rea_xdata[i]);
		}
		data.addSeries(series1);
		data.addSeries(series2);
		return data;
	}

	public void configXYScatterChart(Jfreechart chart){
		NumberAxis xAxis = (NumberAxis)plot.getDomainAxis();
		//xAxis.centerRange(135);
		xAxis.setAutoRange(true);

		/* 縦軸の設定 */
		NumberAxis yAxis = (NumberAxis)plot.getRangeAxis();
		//yAxis.centerRange(35);
		yAxis.setAutoRange(true);
	}
}
