package liner;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import Jama.Matrix;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Liner_pre extends JFrame {
	/**
	 * @param args
	 */
	static JProgressBar bar;
	static JLabel label;
	static int count;
	static int filenum;
	static String title;
	public static void main(String[] args) throws FileNotFoundException{
		// TODO 自動生成されたメソッド・スタブ
		File temp = new File("D:\\ais_2012\\data");
		ENU enu = new ENU();
		SHIP S = new SHIP();
		File temps[] = temp.listFiles();
		filenum= temps.length;
		title = "liner";
		String filenam[] = new String[filenum];
		String data;
		String[] strShipData;
		double[][] total_err = new double[3601][2];
		double[][] all_var = new double[3601][2];
		double enu_err;
		double TE = 0;
		Liner_pre frame = new Liner_pre();
		frame.setTitle("進捗状況 　総データ数"+filenum );
		frame.setSize(400, 150);
		frame.setVisible(true);
		//分散取得時解除
		/*
		Variance VA = new Variance();
		double[][] TAdata = VA.getTAdata();
		double var;
*/
		for(int i=0; i<filenum;i++){
			filenam[i] = temps[i].getName();
			if(filenam[i].length()<13){
				continue;
			}
			StartProbar(i+1);
			File newfile = new File("D:\\ais_2012\\data\\"+filenam[i]);
			System.out.println("読み込みファイル::"+filenam[i]);
			try {
				BufferedReader br = new BufferedReader(new FileReader(newfile));
				int DataCount = 0;
				List<String> list = new ArrayList<String>();
				while((data = br.readLine()) != null){
					if(false == S.DataCheck(data)){
						continue;
					}
					DataCount +=1;
					list.add(data);
				}
				if(DataCount <1802){
					continue;
				}
				strShipData = list.toArray(new String[0]);
				if(strShipData.length ==1 || strShipData.length ==0){
					continue;
				}
				SHIP[] ShipData = new SHIP[strShipData.length];
				for(int j = 0;j<strShipData.length ; j++){
					ShipData[j] = new SHIP(strShipData[j]);
				}
				double pre_pos_x ,pre_pos_y;
				for(int j = 0;j <DataCount-1801; j++ ){
					for(int t =1;t<1801;t++){
						int diff_date = SHIP.CheckDate(((SHIP)ShipData[j+t]).date, ((SHIP)ShipData[j]).date);
						if(t ==1 &&diff_date !=0){
							/*forpaint[j][0] =  ShipData[j].lat;
							forpaint[j][1] =  ShipData[j].lon;
							forpaint[j][2] =  ShipData[j].lat;
							forpaint[j][3] =  ShipData[j].lon;*/
							break;
						}else if(diff_date !=0){
							/*forpaint[j][0] =  ShipData[j].lat;
							forpaint[j][1] =  ShipData[j].lon;
							forpaint[j][2] =  ShipData[j].lat;
							forpaint[j][3] =  ShipData[j].lon;*/
							break;
						}
						int diff_time = SHIP.getSec(((SHIP)ShipData[j+t]).time) -SHIP.getSec(((SHIP)ShipData[j]).time);
						if( (t == 1 && diff_time>3600) || (t ==1 &&diff_time <=0)){
							/*forpaint[j][0] =  ShipData[j].lat;
							forpaint[j][1] =  ShipData[j].lon;
							forpaint[j][2] =  ShipData[j].lat;
							forpaint[j][3] =  ShipData[j].lon;*/
							break;
						}else if(diff_time > 3600 || diff_time <=0){
							/*forpaint[j][0] =  ShipData[j].lat;
							forpaint[j][1] =  ShipData[j].lon;
							forpaint[j][2] =  ShipData[j].lat;
							forpaint[j][3] =  ShipData[j].lon;*/
							break;
						}
						double[][] ecef_o = ENU.ChangeToEcef(((SHIP)ShipData[j]).lat, ((SHIP)ShipData[j]).lon, 0);
						double[][] ecef_next = ENU.ChangeToEcef(((SHIP)ShipData[j+t]).lat, ((SHIP)ShipData[j+t]).lon, 0);
						double[][] pre_enu = new double[3][1];
						Matrix  enu_measured = enu.ChangeToEnu(ecef_o, ecef_next, ((SHIP)ShipData[j+t]).lat, ((SHIP)ShipData[j+t]).lon);
						pre_pos_y = ((SHIP)ShipData[j]).speed*1852/3600 * diff_time * Math.sin(Math.toRadians(((SHIP)ShipData[j]).course));
						pre_pos_x = ((SHIP)ShipData[j]).speed*1852/3600 * diff_time * Math.cos(Math.toRadians(((SHIP)ShipData[j]).course));
						pre_enu[0][0] = pre_pos_x;
						pre_enu[1][0] = pre_pos_y;
						enu_err = enu.getENU_Err(enu_measured,pre_pos_y, pre_pos_x);
						total_err[diff_time][0] += enu_err;
						total_err[diff_time][1] += 1;
						TE = TE +enu_err;
						/*
					forpaint[j][0] =  pre_WGS[0][0];
					forpaint[j][1] =  pre_WGS[1][0];
					forpaint[j][2] =  ShipData[j].lat;
					forpaint[j][3] =  ShipData[j].lon;
					if(forpaint[j][0] == 0.0){
						forpaint[j][0] = ShipData[j].lat;
						forpaint[j][1] = ShipData[j].lon;
					}
						 */
						//分散使用時解除
/*
					all_var[diff_time][0] += VA.culcVA(TAdata[diff_time ][0],enu_err);
					all_var[diff_time][1] +=1;
*/
					}
					/*
					// * enu変換の確認用
					 //http://www.enri.go.jp/~fks442/K_MUSEN/1st/1st021118.pdf
									//double[][] test_0 = enu.ChangeToEcef(38.13877338, 140.89872429, 44.512);
									double[][] test_0 = enu.ChangeToEcef(34.35265333, 139.8789083, 0);
									//double[][] test = enu.ChangeToEcef(38.14227288, 140.93265738,45.664);
									double[][] test = enu.ChangeToEcef(34.35234833, 139.8777617,0);
									Matrix test_err = enu.ChangeToEnu(test_0 , test ,34.35234833,139.8777617 );
									System.out.println(test_err.get(0, 0));
									System.out.println(test_err.get(1, 0));
					 */
					br.close();
				}


				/*ここでmmsi番号のデータを描画処理
				 * mmsi番号、緯度経度、予測緯度経度*/
				//painting(filenam[i],forpaint, title);


			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			System.out.println("total_err"+TE);
		}
/*
		for(int i = 0; i<3601 ; i++){
			writeCSV(total_err[i][0]/1000,i , total_err[i][1]);//1km 単位
		}
*/
/*
		for(int i = 0; i<3601 ; i++){
			writeVar(all_var[i][0],i , all_var[i][1]);
		}
*/

		System.out.println("fin");
	}

	public static void painting(String filenam, double[][] forpaint, String title) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		//forpain　１ファイルの長さ（列）×[予測緯度][予測経度][緯度][経度]（列）
		JFreeChart chart = ChartFactory.createScatterPlot(title, "lon", "lat", createXYDataset(forpaint));
		XYPlot plot = (XYPlot) chart.getPlot();
		chart.setTitle(filenam);
		SaveChartAsFile(filenam, chart );
		chart = null;
	}
	private static XYSeriesCollection createXYDataset(double[][] forpaint) {
		// TODO 自動生成されたメソッド・スタブ
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		XYSeries prexySeries = new XYSeries("pre");
		XYSeries realxySeries = new XYSeries("real");
		for(int i=0; i<forpaint.length; i++){
			prexySeries.add(forpaint[i][1], forpaint[i][0]);
			realxySeries.add(forpaint[i][3], forpaint[i][2]);
		}
		xySeriesCollection.addSeries(prexySeries);
		xySeriesCollection.addSeries(realxySeries);
		return xySeriesCollection;
	}

	public static void SaveChartAsFile(String csvfilename, JFreeChart chart)throws IOException{
		FileOutputStream out  = null;
		String MMSI = csvfilename.substring(0, 8);
		String outputPath = "D:\\ais_2012\\for liner\\png\\" + MMSI + ".png";
		try{
			File outFile = new File(outputPath);
			out = new FileOutputStream(outputPath,true);
			ChartPanel panel = new ChartPanel(chart);
			ChartUtilities.writeChartAsPNG(out, chart, 500 , 500);
			out.flush();
			out.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}finally{
			if(out != null){
				try{
					out.close();
				}catch(IOException e){

				}
			}
		}
	}

	private static void writeCSV(double total_err ,int i ,double count){
		// TODO 自動生成されたメソッド・スタブ
		File errfile = new File("D:\\ais_2012\\for liner\\total_err\\LinerTotalErr.csv");
		try {
			FileWriter filewriter = new FileWriter(errfile, true);
			BufferedWriter bw = new BufferedWriter(filewriter);
			PrintWriter pw = new PrintWriter(bw);
			pw.print(i );
			pw.print(",");
			pw.print(total_err);
			pw.print(",");
			pw.print(count);
			pw.println();
			pw.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private static void writeVar(double total_var,int i, double count){
		File varfile = new File("D:\\ais_2012\\for liner\\total_err\\Liner_Variance.csv");
		try{
			FileWriter filewriter = new FileWriter(varfile, true);
			BufferedWriter bw = new BufferedWriter(filewriter);
			PrintWriter pw = new PrintWriter(bw);
			pw.print(i );
			pw.print(",");
			pw.print(total_var);
			pw.print(",");
			pw.print(count);
			pw.println();
			pw.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	public Liner_pre(){
		count = 0;
		label = new JLabel("Not Start");
		JPanel labelPanel = new JPanel();
		labelPanel.add(label);
		bar = new JProgressBar(0, filenum);
		JPanel barPanel = new JPanel();
		barPanel.add(bar);
		getContentPane().add(labelPanel, BorderLayout.PAGE_START);
		getContentPane().add(barPanel, BorderLayout.CENTER);
	}
	public static void StartProbar(int i){
		label.setText(i+1 + "個目のデータを処理中");
		count++;
		bar.setValue(i);
	}
}

