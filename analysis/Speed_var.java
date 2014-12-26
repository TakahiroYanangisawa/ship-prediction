package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import liner.SHIP;
public class Speed_var {

	/**
	 * @param args
	 */
	static int TotalDataCount = 0;
	final static double x_av = 3.3131971534191375E-4;
	final static double y_av = 9.000367384435387E-4;
	static double[][] cov = new double[2][2];
	static double[][] var = new double[2][2];
	static double  xx_var= 0;
	static double  xy_var= 0;
	static double  yy_var= 0;
	static double  yx_var= 0;

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		File temp = new File("D:\\ais_2012\\for liner\\temp all data\\240190000.csv");
		File temps[] = temp.listFiles();
		SHIP S = new SHIP();
		String data;
		String[] strShipData;
		//int filenum = temps.length;
		//String filenam[] = new String[filenum];
		double x_err = 0;
		double y_err = 0;


		int DataCount ;
		for(int i=0; i<1;i++){

			File newfile = new File("D:\\ais_2012\\for liner\\temp all data\\240190000.csv");

			try{
				BufferedReader br = new BufferedReader(new FileReader(newfile));
				DataCount = 0;
				List<String> list = new ArrayList<String>();
				while((data = br.readLine()) != null){
					if(false == S.DataCheck(data)){
						break;
					}
					DataCount +=1;

					list.add(data);
				}
				strShipData = list.toArray(new String[0]);
				if(strShipData.length ==1 || strShipData.length ==0){
					continue;
				}
				SHIP[] ShipData = new SHIP[strShipData.length];

				for(int j = 0;j<strShipData.length ; j++){
					ShipData[j] = new SHIP(strShipData[j]);
				}
				for(int j = 0; j<DataCount-1; j++){
					int diff_date = SHIP.CheckDate(((SHIP)ShipData[j+1]).date, ((SHIP)ShipData[j]).date);
					int diff_time = SHIP.getSec(((SHIP)ShipData[j+1]).time) -SHIP.getSec(((SHIP)ShipData[j]).time);
					if(diff_date <0){
						continue;
					}
					if(diff_date >1){
						continue;
					}
					if(-86399 <= diff_time && diff_time<0){
						if(diff_date == 0 ){
							continue;
						}
						diff_time = diff_time + 86400;
						if(diff_time > 3600){
							continue;
						}
					} if(diff_time > 3600){
						continue;
					}
					if(diff_time == 0){
						continue;
					}
					TotalDataCount += 1;
					double x_PS = ((SHIP)ShipData[j]).speed * 1852/3600 * Math.cos(Math.toRadians(((SHIP)ShipData[j]).course));
					double y_PS = ((SHIP)ShipData[j]).speed * 1852/3600 * Math.sin(Math.toRadians(((SHIP)ShipData[j]).course));
					double x_NS = ((SHIP)ShipData[j+1]).speed * 1852/3600 * Math.cos(Math.toRadians(((SHIP)ShipData[j+1]).course));
					double y_NS = ((SHIP)ShipData[j+1]).speed * 1852/3600 * Math.sin(Math.toRadians(((SHIP)ShipData[j+1]).course));

					x_err = x_err + (x_PS - x_NS);
					y_err = y_err + (y_PS - y_NS);

					cov = getVar(x_PS,x_NS, y_PS,y_NS);
				}
			}catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		get_av_ERR(x_err , y_err);
		showCov(cov);
	}

	static double[][] getVar(double x_PS, double x_NS, double y_PS, double y_NS) {
		// TODO 自動生成されたメソッド・スタブ

		xx_var =  xx_var + Math.pow((x_PS - x_NS) - x_av , 2);
		xy_var =  xy_var + (((x_PS - x_NS) - x_av)  * ((y_PS - y_NS) - y_av));
		yy_var =  yy_var + Math.pow((y_PS - y_NS) - y_av , 2);
		//yx_var =  yx_var + Math.pow((y_PS - y_NS) - x_av , 2);

		var[0][0] = xx_var;
		var[0][1] = xy_var;
		var[1][0] = xy_var;
		var[1][1] = yy_var;

		return var;
	}
	static void showCov(double[][] cov2) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("共分散");
		System.out.print(cov[0][0]/ TotalDataCount);
		System.out.print("\t");
		System.out.println(cov[0][1]/TotalDataCount);
		System.out.print(cov[1][0]/ TotalDataCount);
		System.out.print("\t");
		System.out.println(cov[1][1]/TotalDataCount);

		System.out.println("相関行列");
		System.out.print((cov[0][0]/ TotalDataCount)/(cov[0][0]/ TotalDataCount));
		System.out.print("\t");
		System.out.println((cov[0][1]/TotalDataCount) / ((Math.sqrt (cov[0][0]/ TotalDataCount)) * (Math.sqrt(cov[1][1]/TotalDataCount))));
		System.out.print((cov[1][0]/ TotalDataCount) / ((Math.sqrt (cov[0][0]/ TotalDataCount)) * (Math.sqrt(cov[1][1]/TotalDataCount))));
		System.out.print("\t");
		System.out.println((cov[1][1]/TotalDataCount) / (cov[1][1]/TotalDataCount));	}
	static void get_av_ERR(double x_err, double y_err) {
		// TODO 自動生成されたメソッド・スタブ
		double x_err_av = x_err / TotalDataCount;
		double y_err_av = y_err / TotalDataCount;

		System.out.println("x_av = "+x_err_av);
		System.out.println("y_av = "+y_err_av);

	}
}
