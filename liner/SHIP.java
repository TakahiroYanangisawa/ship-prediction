package liner;


public class SHIP {
	public String date;
	public String time;
	public double lat;
	public double lon;
	public double speed;
	public double course;
	public double[][] ECEF;
	public SHIP(String strShipData){
		/*
		ShipData[0]:日付,ShipData[1]:時刻,ShipData[7]:対地速力,ShipData[9]:緯度,ShipData[10]:経度,ShipData[11]:対地針路
		 */
		try{
			ENU enu = new ENU();
			String[] shipdata = strShipData.split("," , 13);
			date = shipdata[0];
			time = shipdata[1];
			lat = Double.parseDouble(shipdata[9]);
			lon = Double.parseDouble(shipdata[10]);

			if(Double.parseDouble(shipdata[7])>100){
				speed = Double.parseDouble(shipdata[7]);
			}
			speed = Double.parseDouble(shipdata[7]);
			if(Double.parseDouble(shipdata[11])<=90){
				course = 90 - Double.parseDouble(shipdata[11]);
			}else if(Double.parseDouble(shipdata[11]) >=90 && Double.parseDouble(shipdata[11])<180){
				course = 270 + (180 - Double.parseDouble(shipdata[11]));
			}else if(Double.parseDouble(shipdata[11]) >=180 && Double.parseDouble(shipdata[11])<270){
				course = 180 + (270 - Double.parseDouble(shipdata[11]));
			}else if(Double.parseDouble(shipdata[11]) >= 270 && Double.parseDouble(shipdata[11])<360){
				course = 90 + (360 - Double.parseDouble(shipdata[11]));
			}
			if(speed > 100){
				speed = 0;
			}
			if(course > 361){
				course = 0;
			}
			ECEF = enu.ChangeToEcef(lat, lon, 0);

		}catch(Exception e){
			e.printStackTrace();
			System.out.println(strShipData);
		}
	}

	public boolean DataCheck(String data){
		String[] shipdata = data.split(",",13);
		lat = Double.parseDouble(shipdata[9]);
		if(shipdata[10].contains("\"")){
			return false;
		}
		speed = Double.parseDouble(shipdata[7]);
		course = Double.parseDouble(shipdata[11]);
		lon = Double.parseDouble(shipdata[10]);
		if( lat >=50 || lat<=30){
			return false;
		}else if(lon>=145 ||lon<=120){
			return false;
		}else if(course > 361){
			return false;
		}else if(speed >50){
			return false;
		}
		return true;
	}

	public SHIP() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void show(){
		System.out.println("time: " + time);
		System.out.println("lat: "  + lat);
		System.out.println("lon: "  + lon);
		System.out.println("speed: "  + speed);
		System.out.println("course: "  + course);

	}
	public String getTime(){
		return time;
	}
	public static int getSec(String time) {
		// TODO 自動生成されたメソッド・スタブ
		if(time.length() < 6){
			int inttime = Integer.parseInt(time);
			time = String.format("%1$06d", inttime);
		}
		String hh = time.substring(0,2);
		String mm = time.substring(2,4);
		String ss = time.substring(4,6);
		int alltime = Integer.parseInt(hh)*3600 + Integer.parseInt(mm)*60 + Integer.parseInt(ss);
		return alltime;
	}

	public static int CheckDate(String date1, String date2) {
		int intdate1 = Integer.parseInt(date1);
		int intdate2 = Integer.parseInt(date2);
		int diff_date = intdate1 - intdate2;
		return diff_date;

	}

	public void showECEF() {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("ECEF");
		System.out.println("x="+ECEF[0][0]);
		System.out.println("y="+ECEF[1][0]);
		System.out.println("z="+ECEF[2][0]);
	}
}
