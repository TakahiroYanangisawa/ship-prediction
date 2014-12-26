package liner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Variance {
	double TAdata[][] = new double[3601][1];
	String[] strav =new String[4];
	String strdata = null;
	double av=0;
	int count = 0;
	File TEFile = new File("D:\\ais_2012\\for liner\\total_err\\LinerTotalErr.csv");
	public double[][] getTAdata(){
		try{
			BufferedReader br = new BufferedReader(new FileReader(TEFile));
			while((strdata = br.readLine()) != null){
				strav= strdata.split(",", 4);
				av = Double.parseDouble(strav[3]);
				TAdata[count][0] = av;
				count +=1;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return TAdata;
	}
	//}
	public double culcVA(double TAdata, double enu_err) {
		if(TAdata == 0){
			return 0;
		}
		double var = Math.pow((enu_err/1000) - TAdata, 2);
		return var;
		// TODO 自動生成されたメソッド・スタブ

	}
}
