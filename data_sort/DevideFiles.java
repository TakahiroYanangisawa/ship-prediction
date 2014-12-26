package data_sort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DevideFiles {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		File file = new File("C:\\Users\\yanagisawa\\Desktop\\201202_1.csv");
		divideFile(file,1048570,"UTF-8", "UTF-8");
	}
	public static boolean divideFile(File fi, int row, String iCode, String oCode) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		InputStreamReader isr = null;
		OutputStreamWriter osw = null;
		String line = null;
		int count = 0;
		int fileNo = 1;
		try {
			fis = new FileInputStream(fi);
			isr = new InputStreamReader(fis, iCode);
			br = new BufferedReader(isr);
			fos = new FileOutputStream(fi.getPath() + String.valueOf(fileNo)+".csv");
			osw = new OutputStreamWriter(fos, oCode);
			bw = new BufferedWriter(osw);
			while ((line = br.readLine()) != null) {
				bw.write(line);
				bw.newLine();
				if (count == row) {
					bw.close();
					fileNo++;
					fos = new FileOutputStream(fi.getPath() + String.valueOf(fileNo));
					osw = new OutputStreamWriter(fos, oCode);
					bw = new BufferedWriter(osw);
					count = 0;
				}
				count++;
				System.out.println(count);
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
