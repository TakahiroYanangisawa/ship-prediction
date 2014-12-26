package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import liner.ENU;
import liner.SHIP;

import Jama.Matrix;

public class Pos_Speed_Cov {

	/**
	 * @param args
	 */
	static double[][] pos_cov = new double[2][2];
	static double[][] pos_var = new double[2][2];
	static double[][] pos_sp_cov = new double[4][4];
	final static double pos_x_av=0.18446166501177172;
	final static double pos_y_av=0.18482365944704945;
	static double total_pos_x =0;
	static double total_pos_y =0;
	static double  pos_xx_var =0;
	static double  pos_xy_var =0;
	static double  pos_yy_var =0;
	static double  pos_yx_var =0;

	static double total_diff_time =0;

	final static double sp_x_av = 0.0032956808165979456;
	final static double sp_y_av = 0.0030386630871916532;
	static double[][] sp_cov = new double[2][2];
	static double[][] sp_var = new double[2][2];
	static double total_sp_x = 0;
	static double total_sp_y = 0;
	static double  sp_xx_var= 0;
	static double  sp_xy_var= 0;
	static double  sp_yy_var= 0;
	static double  sp_yx_var= 0;

	public static int TotalDataCount = 0;
	public static void main(String[] args) {
		//File temp = new File("D:\\ais_2012\\for liner\\temp");
		File temp = new File("D:\\ais_2012\\for liner\\temp all data\\240190000.csv");
		File temps[] = temp.listFiles();
		ENU enu = new ENU();
		SHIP S = new SHIP();
		String data;
		String[] strShipData;
		//int filenum = temps.length;
		int filenum = 1;
		//String filenam[] = new String[filenum];
		double[][] enu_err = null;
		double pos_x_err = 0;
		double pos_y_err = 0;
		double liner_err = 0;
		double liner_av;

		double sp_x_err = 0;
		double sp_y_err = 0;

		int DataCount;
		for(int i = 0; i<filenum; i++){/*
			filenam[i] = temps[i].getName();
			if(filenam[i].length()<13){
				continue;
			}*/
			//File newfile = new File("D:\\ais_2012\\for liner\\temp\\"+filenam[i]);
			File newfile = new File("D:\\ais_2012\\for liner\\temp all data\\240190000.csv");
			//System.out.println("読み込みファイル::"+filenam[i]);
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

				double pre_pos_x ,pre_pos_y;
				for(int j = 0;j <DataCount-1; j++ ){
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
					total_diff_time = total_diff_time +diff_time;
					TotalDataCount += 1;
					double[][] ecef_o = enu.ChangeToEcef(((SHIP)ShipData[j]).lat, ((SHIP)ShipData[j]).lon, 0);
					double[][] ecef_next = enu.ChangeToEcef(((SHIP)ShipData[j+1]).lat, ((SHIP)ShipData[j+1]).lon, 0);
					Matrix  real_enu_measured = enu.ChangeToEnu(ecef_o, ecef_next, ((SHIP)ShipData[j+1]).lat, ((SHIP)ShipData[j+1]).lon);
					pre_pos_y = ((SHIP)ShipData[j]).speed*1852/3600 * diff_time * Math.sin(Math.toRadians(((SHIP)ShipData[j]).course));
					pre_pos_x = ((SHIP)ShipData[j]).speed*1852/3600 * diff_time * Math.cos(Math.toRadians(((SHIP)ShipData[j]).course));
					enu_err = getERR(real_enu_measured,pre_pos_x, pre_pos_y);
					total_pos_x = total_pos_x +enu_err[0][0];
					total_pos_y = total_pos_x +enu_err[1][0];

					double x_PS = ((SHIP)ShipData[j]).speed * 1852/3600 * Math.cos(Math.toRadians(((SHIP)ShipData[j]).course));
					double y_PS = ((SHIP)ShipData[j]).speed * 1852/3600 * Math.sin(Math.toRadians(((SHIP)ShipData[j]).course));
					double x_NS = ((SHIP)ShipData[j+1]).speed * 1852/3600 * Math.cos(Math.toRadians(((SHIP)ShipData[j+1]).course));
					double y_NS = ((SHIP)ShipData[j+1]).speed * 1852/3600 * Math.sin(Math.toRadians(((SHIP)ShipData[j+1]).course));

					sp_x_err = Math.abs(x_PS - x_NS);
					sp_y_err = Math.abs(y_PS - y_NS);

					total_sp_x = total_sp_x + sp_x_err;
					total_sp_y = total_sp_y + sp_y_err;
					//sp_cov = Speed_var.getVar(x_PS,x_NS, y_PS,y_NS);

					pos_sp_cov = pos_sp_getCov(enu_err , sp_x_err,sp_y_err);

				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		showCov(pos_sp_cov);
		showAverage(TotalDataCount,total_pos_x,total_pos_y,total_sp_x,total_sp_y);

	}

	private static void showAverage(int totalDataCount2, double pos_x, double pos_y,double sp_x_err, double sp_y_err) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("total diff time = "+ total_diff_time);
		System.out.println("total data count = "+ totalDataCount2);
		System.out.println("x err total = "+ pos_x);
		System.out.println("y err total = "+ pos_y);
		System.out.println("total data count = "+ totalDataCount2);
		System.out.println("err of pos");
		System.out.println("x err av = " + pos_x/total_diff_time);
		System.out.println("y err  av= " + pos_y/total_diff_time);
		System.out.println("err of speed");
		System.out.println("x err av = " + sp_x_err/total_diff_time);
		System.out.println("y err av = " + sp_y_err/total_diff_time);
	}

	private static double[][] pos_sp_getCov(double[][] enu_err, double sp_x_err, double sp_y_err) {
		// TODO 自動生成されたメソッド・スタブ

		double pos_x_err = enu_err[0][0];
		double pos_y_err = enu_err[1][0];

		pos_sp_cov[0][0] = pos_sp_cov[0][0] + Math.pow( (pos_x_err - pos_x_av) ,2);
		pos_sp_cov[0][1] = pos_sp_cov[0][1] + ((pos_x_err - pos_x_av) * (pos_y_err - pos_y_av));
		pos_sp_cov[0][2] = pos_sp_cov[0][2] + ((pos_x_err - pos_x_av) * (sp_x_err - sp_x_av));
		pos_sp_cov[0][3] = pos_sp_cov[0][3] + ((pos_x_err - pos_x_av) * (sp_y_err - sp_y_av));

		pos_sp_cov[1][0] = pos_sp_cov[1][0] + ((pos_y_err - pos_y_av) * (pos_x_err - pos_x_av));
		pos_sp_cov[1][1] = pos_sp_cov[1][1] + Math.pow((pos_y_err - pos_y_av) ,2);
		pos_sp_cov[1][2] = pos_sp_cov[1][2] + ((pos_y_err - pos_y_av) * (sp_x_err - sp_x_av));
		pos_sp_cov[1][3] = pos_sp_cov[1][3] + ((pos_y_err - pos_y_av) * (sp_y_err - sp_y_av));

		pos_sp_cov[2][0] = pos_sp_cov[2][0] + ((sp_x_err - sp_x_av) * (pos_x_err - pos_x_av));
		pos_sp_cov[2][1] = pos_sp_cov[2][1] + ((sp_x_err - sp_x_av) * (pos_y_err - pos_y_av));
		pos_sp_cov[2][2] = pos_sp_cov[2][2] + Math.pow((sp_x_err - sp_x_av) ,2);
		pos_sp_cov[2][3] = pos_sp_cov[2][3] + ((sp_x_err - sp_x_av) * (sp_y_err - sp_y_av));

		pos_sp_cov[3][0] = pos_sp_cov[3][0] + ((sp_y_err - sp_y_av) * (pos_x_err - pos_x_av));
		pos_sp_cov[3][1] = pos_sp_cov[3][1] + ((sp_y_err - sp_y_av) * (pos_y_err - pos_y_av));
		pos_sp_cov[3][2] = pos_sp_cov[3][2] + ((sp_y_err - sp_y_av) * (sp_x_err - sp_x_av));
		pos_sp_cov[3][3] = pos_sp_cov[3][3] + Math.pow((sp_y_err - sp_y_av), 2);

		return pos_sp_cov;
	}

	private static void showCov(double[][] cov2) {
		// TODO 自動生成されたメソッド・スタブ\
		System.out.println("共分散");
		for(int i =0; i<cov2.length; i++){
			for(int j =0; j<cov2.length; j++){
				System.out.print(cov2[i][j] / (total_diff_time -1) + "\t");
			}
			System.out.println("");
		}

		System.out.println("相関行列");
		for(int i =0; i<cov2.length; i++){
			for(int j =0; j<cov2.length; j++){
				System.out.print( (cov2[i][j] / TotalDataCount) / ((Math.sqrt(cov2[i][i] / TotalDataCount)) * ((Math.sqrt(cov2[j][j] / TotalDataCount)))) + "\t");
			}
			System.out.println("");
		}
	}
	private static double[][] getERR(Matrix real_enu_measured, double pre_pos_x, double pre_pos_y) {
		// TODO 自動生成されたメソッド・スタブ

		double real_pos_x = real_enu_measured.get(0, 0);
		double real_pos_y = real_enu_measured.get(1, 0);
		double[][] err = new double[3][1];//[err_of_x, err_of_y]

		err[0][0] = Math.abs(real_pos_x - pre_pos_x);
		err[1][0] = Math.abs(real_pos_y - pre_pos_y);
		//err[2][0] = Math.sqrt( (err[0][0] * err[0][0]) + ( err[1][0] * err[1][0] ));
		return err;
	}
}
