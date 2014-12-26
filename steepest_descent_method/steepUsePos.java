package steepest_descent_method;

import jama.Matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jkalman.JKalman;
import liner.ENU;
import liner.Liner_pre;
import liner.SHIP;

public class steepUsePos {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		// TODO 自動生成されたメソッド・スタブ

		File temp = new File("D:\\ais_2012\\data\\431000437.csv");
		ENU enu = new ENU();
		SHIP S = new SHIP();
		Liner_pre liner = new Liner_pre();
		String data;
		String[] strShipData = null;
		double[][] total_err = new double[3601][2];
		double bf_total_err =		4.725791418583589E9;
		double partial_h = 0.000000001;//
		double 				α = 0.0000000000000035; 				//最急降下法パラメータ
		System.out.println("α = 0.00000000001");
		double enu_err;
		JKalman kalman = new JKalman(4 ,2);//状態値４種、観測値4種のベクトル作成,状態値、観測値の位置は誤差距離x,y（m）とする。
		double sum_total_err = 0;
		double[][] partial  = new double[6][1];
		double[][] COV = new double[6][1];
		/*COV[0][0] = 0.5539804248131315;
		COV[1][0] = 0.03953598037067568;	COV[2][0] = 0.5539804247998019;*/
		COV[0][0] =  7.862788413956523;

		/*COV[3][0] = 100.05068647106742;
		COV[4][0] = 0.05068647111619983;	COV[5][0] = 100.05068647111928;*/



		double[][] aftERR = new double[4][1];
		jama.Matrix aftmatERR =new jama.Matrix(aftERR);
		double[][] befERR = new double[4][1];
		jama.Matrix ProsessNoiseCov = new jama.Matrix(new double[][]
				{{93.40606375778589 ,0.03944045965615798},
				{0.03944045965615798 , 7.862788414096715}});
		jama.Matrix MeasuremetNoiseCov = new jama.Matrix(new double[][]
				{{100.00044045965647 ,0.05044045965914967},
				{0.05044045965914967 ,100.0504404596561 }});

		File newfile = new File("D:\\ais_2012\\data\\431000437.csv");
		try {
			BufferedReader br = new BufferedReader(new FileReader(newfile));
			int DataCount=0;
			List<String> list = new ArrayList<String>();
			while((data = br.readLine()) != null){
				if(false == S.DataCheck(data)){
					break;
				}
				DataCount +=1;
				list.add(data);
			}
			strShipData = list.toArray(new String[0]);
			SHIP[] ShipData = new SHIP[strShipData.length];
			for(int j = 0;j<strShipData.length ; j++){
				ShipData[j] = new SHIP(strShipData[j]);
			}
			for(int i = 0; i<1 ; i++){
				aftERR[i ][0] = COV[i][0];
			}

			while(200000.000<bf_total_err){
				for(int i = 0; i<1; i++){
					COV[i][0] = aftERR[i][0];
					befERR[i][0] = aftERR[i][0];
				}

				int k =0;
				for(int i = 0;i<2 ;i++){
					for(int j = 0; j<2;j++){
						System.out.print("COV[][0] = " +ProsessNoiseCov.get(i, j) + ";" + "\t");
						k++;
					}
					System.out.println("");
				}
				System.out.println("MeasurementNoise");
				//System.out.println(kalman.getMeasurement_noise_cov());
				for(int i = 0;i<2 ;i++){
					for(int j = 0; j<2;j++){
						System.out.print("COV[][0] = " +MeasuremetNoiseCov.get(i, j) + ";" + "\t");
					}
					System.out.println("");
				}
				//System.out.println("ノルム"+aftmatERR.normF());

				for(int step =0; step < 2; step++){
					sum_total_err = 0;
					if(step <1){
						COV[step][0] += partial_h;
					}else{
						System.out.println("");
					}

					ProsessNoiseCov = new jama.Matrix(new double[][]
							{{93.40606375778589  ,0.03944045965615798},
							{0.03944045965615798 ,7.862788414096715 }});
					MeasuremetNoiseCov = new jama.Matrix(new double[][]
							{{100.00044045965647 ,0.05044045965914967},
							{0.05044045965914967 ,100.0504404596561 }});
					//prepare kalman
					double x = 0;
					double y = 0;
					double dx =  ((SHIP)ShipData[0]).speed * 1852 / 3600 *  Math.cos(Math.toRadians(((SHIP)ShipData[0]).course));
					double dy = ((SHIP)ShipData[0]).speed  * 1852 / 3600 *  Math.sin(Math.toRadians(((SHIP)ShipData[0]).course));
					jama.Matrix s = new jama.Matrix(4, 1); // state [x, y, dx, dy, dxy],事前推定値
					s.set(0, 0, x);s.set(1, 0, y);
					s.set(2, 0, dx);s.set(3, 0, dy);
					jama.Matrix c = new jama.Matrix(2, 1); // corrected state [x, y, dx, dy]、事後推定値
					c.set(0, 0, 0);
					c.set(1, 0, 0);
					jama.Matrix m = new jama.Matrix(2, 1); // measurement [x]
					m.set(0, 0, 0);
					m.set(1, 0, 0);
					// transitions for x, y, dx, dy
					double[][] tr = { {1, 0, 1, 0},
							{0, 1, 0, 1},
							{0, 0, 1, 0},
							{0, 0, 0, 1} };
					kalman.setTransition_matrix(new jama.Matrix(tr));

					// 1s somewhere?
					kalman.setError_cov_post(kalman.getError_cov_post().identity());//ここで推定値の誤差の共分散初期化
					//JKalman.setError_cov_post_specifye()

					JKalman.setError_cov_post_specifye
					(100.05062268979759,	0.05079349919424203,
							0.050793062639684194,	100.05066125865505);
					kalman.setProcess_noise_cov_origin(kalman.getProcess_noise_cov(),ProsessNoiseCov);
					kalman.setMeasurement_noise_cov_origin2(kalman.getMeasurement_noise_cov(),MeasuremetNoiseCov);
					kalman.setState_post(s);
					double[][] ECEF_O = enu.BacktoECEF(c, ((SHIP)ShipData[0]).ECEF, ((SHIP)ShipData[0]).lat,((SHIP)ShipData[0]).lon);
					double[][] O_WGS = new double[2][1];
					O_WGS[0][0] = ShipData[0].lat;
					O_WGS[1][0] = ShipData[0].lon;
					Matrix pos_o = new Matrix(2,1);
					int diff_time = 0;
					int diff_date = 0;
					for(int j = 0; j<DataCount -1801 ; j++){
						boolean check_break = true;
						for(int t = 1;t<1801;t++){
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
						for(int z = 0 ;z < diff_time_for_kal ; z ++){
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
					if(step < 1){
						COV[step][0] -=partial_h;
						partial[step ][0] = ( sum_total_err - bf_total_err  )/partial_h;//微分値が入ってる

						aftERR[step  ][0] = befERR[step][0] -   (α* partial[step][0]);//aftERR
					}else{
						for(int i = 0;i<1 ; i++){
							aftmatERR.set(i, 0, aftERR[i][0]);
							COV[i][0] = aftERR[i][0];
						}
						if(sum_total_err>bf_total_err){
							System.out.println("最急降下法かけても減少しない場合に出力");
							System.out.println("bf_total_err = "+bf_total_err);
							System.out.println("tota_err = "+sum_total_err);
							bf_total_err = sum_total_err;
							break;
						}
						bf_total_err = sum_total_err;
						System.out.println("total_err  ="+sum_total_err);
					}
				}//stepのforはここまで
				System.out.println("ProcessNoise");
				//System.out.println(kalman.getProcess_noise_cov());
				/*
				int k =0;
				for(int i = 0;i<2 ;i++){
					for(int j = 0; j<2;j++){
						System.out.print("COV[][0] = " +ProsessNoiseCov.get(i, j) + ";" + "\t");
						k++;
					}
					System.out.println("");
				}
				System.out.println("MeasurementNoise");
				//System.out.println(kalman.getMeasurement_noise_cov());
				for(int i = 0;i<2 ;i++){
					for(int j = 0; j<2;j++){
						System.out.print("COV[][0] = " +MeasuremetNoiseCov.get(i, j) + ";" + "\t");
					}
					System.out.println("");
				}
				 */
				double norm=0;
				for(int i =0;i<1;i++){
					norm = norm + Math.pow(partial[i][0],2);
					System.out.println("partial["+i+"]" + partial[i][0]);
				}
				System.out.println("ノルム"+Math.sqrt(norm));
				System.out.println("【事前共分散】");
				System.out.println(kalman.getError_cov_pre());
				System.out.println("【事後共分散】");
				System.out.println(kalman.getError_cov_post());


				if(Math.sqrt(norm) <10){
					System.out.println("fin");
					break;
				}
				System.out.println("");


			}
		}catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		//System.out.println("total_err="+sum_total_err);

		System.out.println("fin");



	}

}
