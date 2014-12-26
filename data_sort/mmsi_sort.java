package data_sort;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class mmsi_sort {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO 自動生成されたメソッド・スタブ
		File file = new File("c:\\ais_2012\\201201_1.csv");//12月までsortした。
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			File newfile = new File("D:\\ais_2012\\data\\test.csv");
			FileWriter filewriter = new FileWriter(newfile, true);
			BufferedWriter bw = new BufferedWriter(filewriter);
			PrintWriter pw = new PrintWriter(bw);
			String str;
			String[] MMSI;
			int count =0;
			while((str = br.readLine()) != null){
				//System.out.println(str);
				MMSI = str.split(",",3);
				//System.out.println(MMSI[1]);
				if(MMSI[1].equals("999999999") == true){
					continue;
				}
				if(count == 0){
					count =1;
					continue;
				}
				newfile = new File("D:\\ais_2012\\data\\"+MMSI[1]+".csv");
				if(newfile.exists()){
					filewriter = new FileWriter(newfile, true);
					bw = new BufferedWriter(filewriter);
					pw = new PrintWriter(bw);
					pw.println(MMSI[2]);
					pw.close();
					//追加書き込み
				}else{
					newfile = new File("D:\\ais_2012\\data\\"+MMSI[1]+".csv");
					try {
						newfile.createNewFile();
						 filewriter = new FileWriter(newfile,true);
						 bw = new BufferedWriter(filewriter);
						 pw = new PrintWriter(bw);
						pw.println(MMSI[2]);
						pw.close();
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
			}
			pw.close();
			System.out.println("fin");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
