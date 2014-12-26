package kalman;

import jama.Matrix;

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

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import jkalman.JKalman;
import liner.ENU;
import liner.Liner_pre;
import liner.SHIP;
import liner.Variance;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Kalman_only_pos {

	static JProgressBar bar;
	static JLabel label;
	static int filenum;
	static String title;
	public static void main(String[] args) throws Exception{
		File temp = new File("D:\\ais_2012\\data\\");
		ENU enu = new ENU();
		SHIP S = new SHIP();
		Liner_pre liner = new Liner_pre();
		File temps[] = temp.listFiles();
		filenum= temps.length;
		title = "kalman prediction";
		int ttt =0;
		String filenam[] = new String[filenum];
		String data;
		String[] strShipData = null;
		double[][] total_err = new double[3601][2];
		double[][] all_var = new double[3601][2];
		double sum_total_err = 0;
		double enu_err;
		Matrix ProsessNoiseCov = new Matrix(
				new double[][] {{ 0.5540341524701068, 0.03958970771784662},
						{ 0.039589692819525964, 0.039589706953638226}});
				//optimal cov
				/*new double[][] {{93.40606375778589 ,0.03944045965615798},
						{0.03944045965615798 , 7.862788414096715}});*/
		Matrix MeasurementNoiseCov = new Matrix(
				new double[][]{{ 100.05074019856619,0.050740198730252886},
						{ 0.050740198730252886, 100.05074019866437}});
				//optimal cov
				/*new double[][]{{100.00044045965647 ,0.05044045965914967},
						{0.05044045965914967 ,100.0504404596561 }});*/
		Liner_pre frame = new Liner_pre();
		frame.setTitle("進捗状況 　総データ数"+filenum );
		frame.setSize(400, 150);
		frame.setVisible(true);
		Variance VA = new Variance();
		double[][] TAdata = VA.getTAdata();
		double var;

		for(int i=0; i<filenum;i++){
			filenam[i] = temps[i].getName();
			if(filenam[i].length()<13){
				continue;
			}
			Liner_pre.StartProbar(i+1);
			System.out.println("読み込みファイル::"+filenam[i]);
			File newfile = new File("D:\\ais_2012\\data\\"+ filenam[i]);
			try {
				BufferedReader br = new BufferedReader(new FileReader(newfile));
				int DataCount=0;
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
				JKalman kalman = new JKalman(4 ,2);//状態値４種、観測値2種のベクトル作成,状態値、観測値の位置は誤差距離x,y（m）とする。
				double x = 0;
				double y = 0;
				double dx =  ((SHIP)ShipData[0]).speed * 1852 / 3600 *  Math.cos(Math.toRadians(((SHIP)ShipData[0]).course));
				double dy = ((SHIP)ShipData[0]).speed  * 1852 / 3600 *  Math.sin(Math.toRadians(((SHIP)ShipData[0]).course));
				Matrix s = new Matrix(4, 1); // state [x, y, dx, dy, dxy],事前推定値
				s.set(0, 0, x);s.set(1, 0, y);
				s.set(2, 0, dx);s.set(3, 0, dy);
				Matrix c = new Matrix(2, 1); // corrected state [x, y, dx, dy]、事後推定値
				c.set(0, 0, 0);c.set(1, 0, 0);
				Matrix m = new Matrix(2, 1); // measurement [x]
				m.set(0, 0, 0);m.set(1, 0, 0);
				double[][] tr =
					{ {1, 0, 1, 0},
						{0, 1, 0, 1},
						{0, 0, 1, 0},
						{0, 0, 0, 1} };
				kalman.setTransition_matrix(new Matrix(tr));
				kalman.setError_cov_post(kalman.getError_cov_post().identity());//ここで推定値の誤差の共分散初期化
				JKalman.setError_cov_post_specifye
				(100.05062268979759,	0.05079349919424203,	0.05070760779465722,	0.05069847202614419,
						0.050793062639684194,	100.05066125865505,	0.03955614454827022,	0.050713331832254904,
						0.050707607579539626,	0.03955614466073598,	0.05341404531524545,	0.05067569632477639,
						0.050698471868315664,	0.050713331748653445,	0.050675696324700495,	0.05341389956877747);
				kalman.setProcess_noise_cov_origin(kalman.getProcess_noise_cov(), ProsessNoiseCov);
				kalman.setMeasurement_noise_cov_origin2(kalman.getMeasurement_noise_cov(),MeasurementNoiseCov);
				kalman.setState_post(s);
				double[][] ECEF_O= enu.BacktoECEF(c, ((SHIP)ShipData[0]).ECEF, ((SHIP)ShipData[0]).lat,((SHIP)ShipData[0]).lon);
				double[][] O_WGS = new double[2][1];
				O_WGS[0][0] = ShipData[0].lat;
				O_WGS[1][0] = ShipData[0].lon;
				Matrix pos_o = new Matrix(2,1);
				int diff_time = 0;
				int diff_date = 0;
				for(int j = 0; j<DataCount -1801 ; j++){
					boolean check_break = true;
					for(int t = 1;t<1801;t++){
						diff_date = SHIP.CheckDate(((SHIP)ShipData[j+t]).date, ((SHIP)ShipData[j]).date);
						if(t ==1 &&diff_date !=0){
							check_break =false;
							break;
						}else if(diff_date !=0){
							break;
						}
						diff_time = SHIP.getSec(((SHIP)ShipData[j+t]).time) -SHIP.getSec(((SHIP)ShipData[j]).time);
						if( (t == 1 && diff_time>3600) || (t ==1 &&diff_time <=0)){
							check_break = false;
							break;
						}else if(diff_time > 3600 || diff_time <=0){
							break;
						}
						double pre_pos_y,pre_pos_x;
						double[][] ecef_next = {{ShipData[j+t].ECEF[0][0]},{ShipData[j+t].ECEF[1][0]},{ShipData[j+t].ECEF[2][0]}};
						Jama.Matrix  enu_measured = enu.ChangeToEnu(ECEF_O, ecef_next, O_WGS[0][0],O_WGS[1][0]);
						pre_pos_x = ((SHIP)ShipData[j+1]).speed * 1852 / 3600 *  Math.cos(Math.toRadians(((SHIP)ShipData[j+1]).course)) * diff_time ;
						pre_pos_y = ((SHIP)ShipData[j+1]).speed * 1852 / 3600 *  Math.sin(Math.toRadians(((SHIP)ShipData[j+1]).course))* diff_time ;
						enu_err = enu.getENU_Err(enu_measured,pre_pos_y, pre_pos_x);
						total_err[diff_time][0] += enu_err;
						total_err[diff_time][1] += 1;
						sum_total_err +=enu_err;
						//分散取得時解除
						all_var[diff_time][0] += VA.culcVA(TAdata[diff_time ][0],enu_err);
						all_var[diff_time][1] +=1;
					}
					kalman.getState_post().set(0, 0, 0);
					kalman.getState_post().set(1, 0, 0);
					kalman.getState_post().set(2, 0, ((SHIP)ShipData[j]).speed * 1852 / 3600 *  Math.cos(Math.toRadians(((SHIP)ShipData[j]).course)));
					kalman.getState_post().set(3, 0, ((SHIP)ShipData[j]).speed * 1852 / 3600 *  Math.sin(Math.toRadians(((SHIP)ShipData[j]).course)));
					if(check_break == false){
						ECEF_O[0][0]= ShipData[j+1].ECEF[0][0];
						ECEF_O[1][0]= ShipData[j+1].ECEF[1][0];
						ECEF_O[2][0]= ShipData[j+1].ECEF[2][0];
						O_WGS = enu.ECEFtoWGS(ECEF_O);
						kalman.getState_post().set(2, 0,((SHIP)ShipData[j+1]).speed * 1852 / 3600 *  Math.cos(Math.toRadians(((SHIP)ShipData[j+1]).course)));
						kalman.getState_post().set(3, 0,((SHIP)ShipData[j+1]).speed * 1852 / 3600 *  Math.sin(Math.toRadians(((SHIP)ShipData[j+1]).course)));
						continue;
					}
					int diff_time_for_kal = SHIP.getSec(((SHIP)ShipData[j+1]).time) -SHIP.getSec(((SHIP)ShipData[j]).time);
					for(int k = 0 ;k < diff_time_for_kal ; k ++){
						s=kalman.Predict();
					}

					double[][] ecef_next = ((SHIP)ShipData[j+1]).ECEF;//check here
					O_WGS = enu.ECEFtoWGS(ECEF_O);
					Jama.Matrix real_enu_measured = enu.ChangeToEnu(ECEF_O, ecef_next,O_WGS[0][0], O_WGS[1][0]);//lat.lonはECEF_OのWGS値
					m.set(0,0,real_enu_measured.get(0, 0) );
					m.set(1,0,real_enu_measured.get(1, 0) );
					c = kalman.Correct(m);
					pos_o.set(0, 0, -(c.get(0, 0)));
					pos_o.set(1, 0, -(c.get(1, 0)));
					ECEF_O = enu.BacktoECEF(pos_o, ENU.ChangeToEcef(O_WGS[0][0], O_WGS[1][0], 0), O_WGS[0][0],O_WGS[1][0]);
					O_WGS = enu.ECEFtoWGS(ECEF_O);
				}
				/*ここでmmsi番号のデータを描画処理
				 * mmsi番号、緯度経度、予測緯度経度*/
				//painting(filenam[i],forpaint,title);
				System.out.println("【事前共分散】");
				System.out.println(kalman.getError_cov_pre());
				System.out.println("");
				System.out.println("【事後共分散】");
				System.out.println(kalman.getError_cov_post());
			}catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			System.out.println("【誤差合計】");
			System.out.println("total_err="+sum_total_err);
			System.out.println(ttt);
		}
/*
		for(int i = 0; i<3601 ; i++){
			writeCSV(total_err[i][0]/1000,i , total_err[i][1]);//1km 単位
		}
*/

		for(int i = 0; i<3601 ; i++){
			writeVar(all_var[i][0],i , all_var[i][1]);
		}


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
		String outputPath = "D:\\ais_2012\\for kalman\\png\\" + MMSI + ".png";
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

	private static void show_ecef_o(double[][] ecef_o) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("ECEF_O");
		System.out.println("x="+ecef_o[0][0]);
		System.out.println("y="+ecef_o[1][0]);
		System.out.println("z="+ecef_o[2][0]);
	}

	private static void writeCSV(double total_err ,int i ,double count){
		// TODO 自動生成されたメソッド・スタブ
		File errfile = new File("D:\\ais_2012\\for kalman\\total_err\\KalmanTotalErrUsePos.csv");
		//File errfile = new File("D:\\ais_2012\\for kalman\\total_err\\KalmanTotalErrUsePosOptimalCovFor431000437.csv");
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
		File varfile = new File("D:\\ais_2012\\for kalman\\total_err\\KalmanTotalErrUsePosOptimalCovFor431000437.csv");
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

}
