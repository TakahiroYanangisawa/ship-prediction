package liner;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
public class Liner_for_compare_with_Liner extends JFrame{
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
		String[][] filenam_err = new String[filenum][2];
		String data;
		String[] strShipData;
		double[][] total_err = new double[3601][2];
		double[][] all_var = new double[3601][2];
		double enu_err;
		double TE = 0;
		Liner_for_compare_with_Liner frame = new Liner_for_compare_with_Liner();
		frame.setTitle("進捗状況 　総データ数"+filenum );
		frame.setSize(400, 150);
		frame.setVisible(true);
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
							break;
						}else if(diff_date !=0){
							break;
						}
						int diff_time = SHIP.getSec(((SHIP)ShipData[j+t]).time) -SHIP.getSec(((SHIP)ShipData[j]).time);
						if( (t == 1 && diff_time>3600) || (t ==1 &&diff_time <=0)){
							break;
						}else if(diff_time > 3600 || diff_time <=0){

							break;
						}
						double[][] ecef_o = ENU.ChangeToEcef(((SHIP)ShipData[j]).lat, ((SHIP)ShipData[j]).lon, 0);
						double[][] ecef_next = ENU.ChangeToEcef(((SHIP)ShipData[j+1]).lat, ((SHIP)ShipData[j+1]).lon, 0);
						double[][] pre_enu = new double[3][1];
						Matrix  enu_measured = enu.ChangeToEnu(ecef_o, ecef_next, ((SHIP)ShipData[j+1]).lat, ((SHIP)ShipData[j+1]).lon);

						pre_pos_y = ((SHIP)ShipData[j]).speed*1852/3600 * diff_time * Math.sin(Math.toRadians(((SHIP)ShipData[j]).course));
						pre_pos_x = ((SHIP)ShipData[j]).speed*1852/3600 * diff_time * Math.cos(Math.toRadians(((SHIP)ShipData[j]).course));
						pre_enu[0][0] = pre_pos_x;
						pre_enu[1][0] = pre_pos_y;
						enu_err = enu.getENU_Err(enu_measured,pre_pos_y, pre_pos_x);
						total_err[diff_time][0] += enu_err;
						total_err[diff_time][1] += 1;
						TE = TE +enu_err;
					}
				}
				br.close();
			}catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			filenam_err[i][0] = filenam[i];
			filenam_err[i][1] = Double.toString(TE/1000);
			TE = 0;
		}
		for(int i = 0; i < filenam_err.length ; i++){
			write(filenam_err[i][0], filenam_err[i][1]);//1km 単位
		}
		System.out.println("fin");
	}
	private static void write(String shipnum, String err) {
		// TODO 自動生成されたメソッド・スタブ
		File errfile = new File("D:\\ais_2012\\for liner\\total_err\\LinerShipErr.csv");
		try {
			FileWriter filewriter = new FileWriter(errfile, true);
			BufferedWriter bw = new BufferedWriter(filewriter);
			PrintWriter pw = new PrintWriter(bw);
			pw.print(shipnum);
			pw.print(",");
			pw.print(err);
			pw.println();
			pw.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	private static void writeCSV(double total_err ,int i ,double count){
		// TODO 自動生成されたメソッド・スタブ
		//このファイル読み込み先注意
		File errfile = new File("D:\\ais_2012\\for liner\\total_err\\LinerShipErr.csv");
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
	public Liner_for_compare_with_Liner(){
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
