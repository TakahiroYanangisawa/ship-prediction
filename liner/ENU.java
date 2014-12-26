package liner;

import Jama.Matrix;

/**
 *
 */

/**
 * @author yanagisawa
 *
 */
public class ENU {
	static final double  A = 6378137.0;
	static final double ONE_F = 298.257223563; /* 1/F */
	static final double B = (A*(1.0 - 1.0/ONE_F));
	static final double E2 = ((1.0/ONE_F)*(2-(1.0/ONE_F)));
	static final double ED2 = (E2*A*A/(B*B));
	static final double f = 1 / 298.257223563;
	static final double pow_e = 2*f - (f*f);
	double[] blh = new double[3];
	final double  z = 1;
	double phi, ramda,height,p;
	double sita;
	double e,n,u,err;
	static int row;
	static int col;
	double check_e_sum=0;
	double check_n_sum=0;
	int checkcount = 0;
	double[] enu = new double[3];
	final double[][] rotX = { {		1		,		0		,		0		},
			{		0		,		Math.cos(Math.toRadians(90))			,		Math.sin(Math.toRadians(90))		},
			{		0		,		-Math.sin(Math.toRadians(90))		,		Math.cos(Math.toRadians(90))		}};
	Matrix matR_X = new Matrix(rotX);
	/*double[][] rotY = { {		Math.cos(Math.toRadians(90 - lat))		,		0		,		-Math.sin(Math.toRadians(90 - lat))		},
			{		0		,		1		,		0		},
			{Math.sin(Math.toRadians(90 - lat))		,		0		,		Math.cos(Math.toRadians(90 - lat))		}};
	 */
	final double[][] rotZ_90 = { {		Math.cos(Math.toRadians(90))		,		Math.sin(Math.toRadians(90))		,		0		},
			{		-Math.sin(Math.toRadians(90))		,		Math.cos(Math.toRadians(90))		,		0		},
			{		0		,		0		,		1		}};
	Matrix matR_Z_90 = new Matrix(rotZ_90);
	/*
	double[][] rotZ_ram = { {		Math.cos(Math.toRadians(lon))		,		Math.sin(Math.toRadians(lon))		,		0		},
			{		-Math.sin(Math.toRadians(lon))		,		Math.cos(Math.toRadians(lon))		,		0		},
			{		0		,		0		,		1		}};
	 */
	public double[] ChangeToBLH(double x , double y){
		p = Math.sqrt( Math.pow(x, 2) + Math.pow(y,2));
		sita = (180/Math.PI) * Math.atan2(z*ENU.A, p* ENU.B);
		//lat
		phi = (180/Math.PI) * Math.atan2(z + ENU.ED2 * ENU.B * Math.pow(Math.sin(Math.toRadians(sita)), 3)
				, (p - ENU.E2* ENU.A * Math.pow(Math.cos(Math.toRadians(sita)), 3)));

		//lon
		ramda = (180/Math.PI) * Math.atan2(y, x);

		//height
		height = (p / Math.cos(Math.toRadians(phi))) -
				((ENU.A/Math.sqrt(1.0- (ENU.E2) * Math.sqrt(Math.sin(Math.toRadians(phi)))))   );

		blh[0] = phi;
		blh[1] = ramda;
		blh[2] = height;

		return blh;
	}

	public static double[][] ChangeToEcef(double lat,double lon , double height){
		double[][] ecef = new double[3][1];
		double N = ENU.A / Math.sqrt( 1 - (ENU.pow_e * Math.sin(Math.toRadians(lat)) *Math.sin(Math.toRadians(lat)) ));
		ecef[0][0] = (N + height) * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(lon));//X
		ecef[1][0] = (N + height) * Math.cos(Math.toRadians(lat)) * Math.sin(Math.toRadians(lon));//Y
		ecef[2][0] = (N * (1-ENU.pow_e) + height) * Math.sin(Math.toRadians(lat));//Z
		return ecef;
	}

	public Matrix ChangeToEnu(double[][] ecef_o,double[][] ecef,double lat,double lon){//ecef_oのlat,lonを入れる
		double[][] enu =
			{{ecef[0][0] - ecef_o[0][0]},
				{ecef[1][0] - ecef_o[1][0]},
				{ecef[2][0] - ecef_o[2][0]}};
		Matrix matenu = new Matrix(enu);

		double[][] rotY =
			{ {		Math.cos(Math.toRadians(90 - lat))		,		0		,		-Math.sin(Math.toRadians(90 - lat))		},
				{		0		,		1		,		0		},
				{Math.sin(Math.toRadians(90 - lat))		,		0		,		Math.cos(Math.toRadians(90 - lat))		}};

		double[][] rotZ_ram =
			{ {		Math.cos(Math.toRadians(lon))		,		Math.sin(Math.toRadians(lon))		,		0		},
				{		-Math.sin(Math.toRadians(lon))		,		Math.cos(Math.toRadians(lon))		,		0		},
				{		0		,		0		,		1		}};

		Matrix matR_Y = new Matrix(rotY);
		Matrix matR_Zram = new Matrix(rotZ_ram);

		Matrix matA = matR_Z_90.times(matR_Y);
		Matrix matB = matA.times(matR_Zram);
		Matrix matC = matB.times(matenu);

		e = matC.get(0, 0);
		n  = matC.get(1, 0);
		u = matC.get(2, 0);

		return matC;
	}
	public void ShowSum(){
		System.out.println("e="+check_e_sum );
		System.out.println("n="+check_n_sum);
		System.out.println("e_av ="+check_e_sum/checkcount);
		System.out.println("n_av ="+check_n_sum/checkcount);
	}
	public static double[][] multi_mat(double[][] mat1, double[][] mat2) {
		// TODO 自動生成されたメソッド・スタブ
		//int row = mat1.length;
		//int col = mat2[0].length;
		row = mat1.length;
		col = mat2[0].length;
		double[][] result = new double[row][col];

		for( int i=0; i<row; i++){
			for(int  j=0; j<mat2.length; j++){
				for(int k=0; k<col; k++){
					result[i][j]+=mat1[i][k]*mat2[k][j];
				}
			}
		}
		return result;
	}

	public double getENU_Err(Matrix enu_measured, double pre_pos_y, double pre_pos_x) {
		// TODO 自動生成されたメソッド・スタブ
		/*原点ゼロ（ecef_o）のｘ、ｙ座標上（単位はメートル）で、①期先の観測位置の距離をenu_measuredとしている
		 * pre_pos_x,yはその座標上での原点からの予測位置。
		 * ここのプログラムでは、観測位置と予測位置の距離を求める。
		 * それがenu_errである。
		*/
		e = enu_measured.get(0, 0);//x軸方向
		n  = enu_measured.get(1, 0);//y軸方向
		err = Math.sqrt( Math.pow((e - pre_pos_x),2) + Math.pow((n - pre_pos_y),2));

		//System.out.println("err="+err);
		return err;
	}
	public double[][] BacktoECEF(jama.Matrix c, double[][] ECEF,double lat,double lon){
		//変換したENU座標、変換先のECEF座標、変換先のlat.lon
		double[][] enu = {{c.get(0,0)},{c.get(1,0)},{0}};
		double[][] rotY = { {		Math.cos(Math.toRadians(90 - lat))		,		0		,		-Math.sin(Math.toRadians(90 - lat))		},
				{		0		,		1		,		0		},
				{Math.sin(Math.toRadians(90 - lat))		,		0		,		Math.cos(Math.toRadians(90 - lat))		}};

		double[][] rotZ_ram = { {		Math.cos(Math.toRadians(lon))		,		Math.sin(Math.toRadians(lon))		,		0		},
				{		-Math.sin(Math.toRadians(lon))		,		Math.cos(Math.toRadians(lon))		,		0		},
				{		0		,		0		,		1		}};

		Matrix matY = new Matrix(rotY);
		Matrix matZ_ram = new Matrix(rotZ_ram);
		Matrix matZ_90 = new Matrix(rotZ_90);
		Matrix matenu = new Matrix(enu);

		matY = matY.transpose();
		matZ_ram = matZ_ram.transpose();
		matZ_90 = matZ_90.transpose();
		Matrix tempA = matZ_ram.times(matY);
		Matrix tempB = tempA.times(matZ_90);
		Matrix tempC = tempB.times(matenu);//Eの値

		double[][] temp = {{tempC.get(0,0)},{tempC.get(1,0)},{tempC.get(2,0)}};//Eをdoubleにしただけ

		double[][] ECEFpos = {{ECEF[0][0] - temp[0][0]},
											{ECEF[1][0] - temp[1][0]},
											{ECEF[2][0] - temp[2][0]}};

		return ECEFpos;
	}
	public double[][] ECEFtoWGS(double[][] ecef){
		double phi,ramda,height,p;
		double x,y,z;
		double sita;
		double[][] WGS = new double[3][1];
		x = ecef[0][0];
		y = ecef[1][0];
		z = ecef[2][0];
		p = Math.sqrt(x*x + y*y);
		sita = (180/Math.PI)*Math.atan2(z*A, p*B);

		/*lat*/
		phi = (180/Math.PI) * Math.atan2(z + ED2*B*(Math.pow(Math.sin(sita*Math.PI/180), 3)),
						(p - E2 * A * (Math.pow(Math.cos(sita * Math.PI/180), 3))));
		/*lon*/
		ramda = (180/Math.PI) * Math.atan2(y, x);
		height = 0;


		WGS[0][0] = phi;
		WGS[1][0] = ramda;
		WGS[2][0] = height;

		return WGS;
	}

}
