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

public class steepUsePos_Sp {
	public static void main(String[] args)throws Exception{
		File temp = new File("D:\\ais_2012\\for liner\\temp all data\\371012000.csv");
		ENU enu = new ENU();
		SHIP S = new SHIP();
		Liner_pre liner = new Liner_pre();
		String data;
		String[] strShipData = null;
		double[][] total_err = new double[3601][2];

		double bf_total_err =1.310201141983219E8;
			double partial_h = 0.000000001;//
						double α = 0.0000000000001; 				//最急降下法パラメータ
	System.out.println("α = 0.0000000000001");
		double enu_err;
		JKalman kalman = new JKalman(4 ,4);//状態値４種、観測値4種のベクトル作成,状態値、観測値の位置は誤差距離x,y（m）とする。
		double sum_total_err = 0;
		double[][] partial  = new double[20][1];
		double[][] COV = new double[20][1];
		COV[0][0] = 0.5539804248131315;
		COV[1][0] = 0.03953598037067568;	COV[4][0] = 0.5539804247998019;
		COV[2][0] = 0.03953597202181118;	COV[5][0] = 0.03953597799906274;	COV[7][0] = 0.05342515067476168;
		COV[3][0] = 0.03953597347817849;	COV[6][0] = 0.03953596575910924;	COV[8][0] = 0.039535864187602424;	COV[9][0] = 0.053425033101328595;

		COV[10][0] = 100.05068647106742;
		COV[11][0] = 0.05068647111619983;	COV[14][0] = 100.05068647111928;
		COV[12][0] = 0.05068647053583892;	COV[15][0] = 0.05068647100962197;	COV[17][0] = 1.0506864666800746;
		COV[13][0] = 0.0506864711018689;	COV[16][0] = 0.05068647067792463;	COV[18][0] = 0.05068646943361896;	COV[19][0] = 1.0506864651927714;

		double[][] aftERR = new double[20][1];
		jama.Matrix aftmatERR =new jama.Matrix(aftERR);
		double[][] befERR = new double[20][1];



		jama.Matrix ProsessNoiseCov = new jama.Matrix(new double[][]
				{{COV[0][0] ,COV[1][0],COV[2][0],COV[3][0]},
				{COV[1][0] ,COV[4][0],COV[5][0],COV[6][0]},
				{COV[2][0] ,COV[5][0],COV[7][0],COV[8][0]},
				{COV[3][0] ,COV[6][0],COV[8][0],COV[9][0] }});

		jama.Matrix MeasuremetNoiseCov = new jama.Matrix(new double[][]
				{{COV[10][0] ,COV[11][0],COV[12][0],COV[13][0]},
				{COV[11][0] ,COV[14][0],COV[15][0],COV[16][0]},
				{COV[12][0] ,COV[15][0],COV[17][0],COV[18][0]},
				{COV[13][0] ,COV[16][0],COV[18][0],COV[19][0] }});

		File newfile = new File("D:\\ais_2012\\for liner\\temp all data\\371012000.csv");
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
			for(int i = 0; i<20 ; i++){
				aftERR[i ][0] = COV[i][0];
			}

			while(200000.000<bf_total_err){
				for(int i = 0; i<20; i++){
					COV[i][0] = aftERR[i][0];
					befERR[i][0] = aftERR[i][0];
				}
				for(int step =0; step < 21; step++){
					sum_total_err = 0;
					if(step <20){
						COV[step][0] += partial_h;
					}else{
						System.out.println("");
					}
					ProsessNoiseCov = new jama.Matrix(new double[][]
							{{COV[0][0] ,COV[1][0],COV[2][0],COV[3][0]},
							{COV[1][0] ,COV[4][0],COV[5][0],COV[6][0]},
							{COV[2][0] ,COV[5][0],COV[7][0],COV[8][0]},
							{COV[3][0] ,COV[6][0],COV[8][0],COV[9][0] }});

					MeasuremetNoiseCov = new jama.Matrix(new double[][]
							{{COV[10][0] ,COV[11][0],COV[12][0],COV[13][0]},
							{COV[11][0] ,COV[14][0],COV[15][0],COV[16][0]},
							{COV[12][0] ,COV[15][0],COV[17][0],COV[18][0]},
							{COV[13][0] ,COV[16][0],COV[18][0],COV[19][0] }});

					//prepare kalman
					double x = 0;
					double y = 0;
					double dx =  ((SHIP)ShipData[0]).speed * 1852 / 3600 *  Math.cos(Math.toRadians(((SHIP)ShipData[0]).course));
					double dy = ((SHIP)ShipData[0]).speed  * 1852 / 3600 *  Math.sin(Math.toRadians(((SHIP)ShipData[0]).course));
					jama.Matrix s = new jama.Matrix(4, 1); // state [x, y, dx, dy, dxy],事前推定値
					//
					s.set(0, 0, x);
					s.set(1, 0, y);
					s.set(2, 0, dx);
					s.set(3, 0, dy);
					//
					jama.Matrix c = new jama.Matrix(4, 1); // corrected state [x, y, dx, dy]、事後推定値
					c.set(0, 0, 0);
					c.set(1, 0, 0);
					jama.Matrix m = new jama.Matrix(4, 1); // measurement [x]
					m.set(0, 0, 0);
					m.set(1, 0, 0);
					m.set(2, 0, dx);
					m.set(3, 0, dy);
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
					(100.05062268979759,	0.05079349919424203,	0.05070760779465722,	0.05069847202614419,
							0.050793062639684194,	100.05066125865505,	0.03955614454827022,	0.050713331832254904,
							0.050707607579539626,	0.03955614466073598,	0.05341404531524545,	0.05067569632477639,
							0.050698471868315664,	0.050713331748653445,	0.050675696324700495,	0.05341389956877747);
					kalman.setProcess_noise_cov_origin(kalman.getProcess_noise_cov(),ProsessNoiseCov);
					kalman.setMeasurement_noise_cov_origin(kalman.getMeasurement_noise_cov(),MeasuremetNoiseCov);
					kalman.setState_post(s);
					double[][] ECEF_O = enu.BacktoECEF(c, ((SHIP)ShipData[0]).ECEF, ((SHIP)ShipData[0]).lat,((SHIP)ShipData[0]).lon);
					for(int j = 0; j<DataCount -1801 ; j++){
						for(int t = 1;t<1801;t++){
							int diff_date = SHIP.CheckDate(((SHIP)ShipData[j+t]).date, ((SHIP)ShipData[j]).date);
							if(diff_date != 0){
								kalman.getState_post().set(2, 0,((SHIP)ShipData[j]).speed * 1852 / 3600 *  Math.cos(Math.toRadians(((SHIP)ShipData[j]).course)));
								kalman.getState_post().set(3, 0,((SHIP)ShipData[j]).speed * 1852 / 3600 *  Math.sin(Math.toRadians(((SHIP)ShipData[j]).course)));
								ECEF_O = ENU.ChangeToEcef(((SHIP)ShipData[j]).lat, ((SHIP)ShipData[j]).lon, 0);
								continue;
							}
							int diff_time = SHIP.getSec(((SHIP)ShipData[j+t]).time) -SHIP.getSec(((SHIP)ShipData[j]).time);
							if(diff_time <0 || diff_time >3600){
								kalman.getState_post().set(2, 0,((SHIP)ShipData[j]).speed * 1852 / 3600 *  Math.cos(Math.toRadians(((SHIP)ShipData[j]).course)));
								kalman.getState_post().set(3, 0,((SHIP)ShipData[j]).speed * 1852 / 3600 *  Math.sin(Math.toRadians(((SHIP)ShipData[j]).course)));
								ECEF_O = ENU.ChangeToEcef(((SHIP)ShipData[j]).lat, ((SHIP)ShipData[j]).lon, 0);
								continue;
							}
							kalman.getState_post().set(0, 0, 0);
							kalman.getState_post().set(1, 0, 0);

							for(int k = 0 ;k < diff_time ; k ++){
								s=kalman.Predict();
							}
							double[][] ecef_next = ((SHIP)ShipData[j+t]).ECEF;
							double[][] O_WGS = new double[3][1];
							O_WGS = enu.ECEFtoWGS(ECEF_O);
							Jama.Matrix real_enu_measured = enu.ChangeToEnu(ECEF_O, ecef_next,O_WGS[0][0], O_WGS[1][0]);//lat.lonはECEF_OのWGS値

							m.set(0,0,real_enu_measured.get(0, 0) );
							m.set(1,0,real_enu_measured.get(1, 0) );
							m.set(2, 0, ((SHIP)ShipData[j+t]).speed * 1852/3600 *  Math.cos(Math.toRadians(((SHIP)ShipData[j+t]).course)));
							m.set(3, 0, ((SHIP)ShipData[j+t]).speed * 1852/3600 *  Math.sin(Math.toRadians(((SHIP)ShipData[j+t]).course)));

							enu_err = enu.getENU_Err(real_enu_measured,s.get(1, 0), s.get(0, 0));
							total_err[diff_time][0] += enu_err;
							total_err[diff_time][1] += 1;
							sum_total_err +=enu_err;

							c = kalman.Correct(m);
							Matrix pos_o = new Matrix(2,1);
							pos_o.set(0, 0, -(c.get(0, 0)));
							pos_o.set(1, 0, -(c.get(1, 0)));
							ECEF_O = enu.BacktoECEF(pos_o, ENU.ChangeToEcef(O_WGS[0][0], O_WGS[1][0], 0), O_WGS[0][0],O_WGS[1][0]);
						}
					}
					if(step < 20){
						COV[step][0] -=partial_h;
						partial[step ][0] = ( sum_total_err - bf_total_err  )/partial_h;//微分値が入ってる

						aftERR[step  ][0] = befERR[step][0] -   (α* partial[step][0]);//aftERR
					}else{
						for(int i = 0;i<20 ; i++){
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

				int k =0;
				for(int i = 0;i<4 ;i++){
					for(int j = 0; j<4;j++){
						System.out.print("COV[][0] = " +ProsessNoiseCov.get(i, j) + ";" + "\t");
						k++;
					}
					System.out.println("");
				}
				System.out.println("MeasurementNoise");
				//System.out.println(kalman.getMeasurement_noise_cov());
				for(int i = 0;i<4 ;i++){
					for(int j = 0; j<4;j++){
						System.out.print("COV[][0] = " +MeasuremetNoiseCov.get(i, j) + ";" + "\t");
					}
					System.out.println("");
				}
				//System.out.println("ノルム"+aftmatERR.normF());*/
				double norm=0;
				for(int i =0;i<16;i++){
					norm = norm + Math.pow(partial[i][0],2);
				}
				System.out.println("ノルム"+Math.sqrt(norm));
				System.out.println("【事前共分散】");
				System.out.println(kalman.getError_cov_pre());
				System.out.println("【事後共分散】");
				System.out.println(kalman.getError_cov_post());


				if(Math.sqrt(norm) <1000){
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


