# **ship-prediction**  
船舶動向予測技術評価のためのプログラム  
**data_sort**:AISデータの分割・ソート  
**Liner**:船舶のオブジェクト化・線形予測時の平均誤差距離・分散計算・座標変換  
**Kalman**:カルマンフィルタを用いた予測時の平均誤差距離・分散計算  
**steepest descent method**:最急降下法利用時のプログラム  
**Jkalman**:カルマンフィルタのライブラリ  
**jama**:行列計算のためのライブラリ

===============
## data_sort  
船舶動向予測技術評価のための一ヶ月分の実データを用意する。  

===============  
### DevidedFiles.java  
デコードされたAIS type1 メッセージをDevideFiles.javaにて一ヶ月毎のcsvファイルに分割する。  
csv形式のAISデータを一行毎に読み込みAISデータのタイムスタンプで１２ヵ月分に分割する。   最終行まで読み込むとプログラムは終了する。  
出力には"年月_メッセージタイプ"をファイル名とするcsvファイルを得る。  
### mmsi_sort.java  
DevidedFiles.javaによって分割されたcsvファイルをさらにMMSI番号毎にソートし新たなcsvファイルに保存する。  
DecidedFiles.javaによって分割されたcsvファイルを１行毎に読み込み、MMSI番号とそれに付随する船舶データを  
配列に保持する。最終行まで読み込むとプログラムは終了する。  
配列に保持されたMMSI番号のcsvファイルが存在すれば、配列に保存された船舶データを当該csvファイルに追加書き込みを  
行う。  
配列に保持されたMMSI番号のcsvファイルが存在しない場合はそのMMSI番号をファイル名とするcsvファイルを新たに作成し  
配列に保存された船舶データを当該csvファイルに書き込みを行う。

===============
## Liner  
座標変換
船舶のオブジェクト化
線形予測を行う際の平均誤差距離・分散計算  

===============
### ENU.java  
測地座標系で表現されている船舶GPS位置情報をECEF座標系及びENU座標系に相互変換する。
[理解するためのGPS測位計算プログラム入門](http://www.enri.go.jp/~fks442/K_MUSEN/1st/1st021118.pdf)  
を参考にJavaで作成した。  
#### ChangToECEF(double, double)
測地座標で表される船舶位置をECEF座標に変換する。
2種類のdouble型引数（緯度・経度）をとり、戻り値にdouble型配列(X,Y,Z)を得る。  
#### ChangeToENU(double[][], double[][],double, double)
ECEF座標で表される2点を、一方を原点とするENU座標に変換する。  
double型配列2種類(原点とするECEF座標ともう一方のECEF座標)及びdouble型引数2種類（原点の緯度・経度）を引数にとり  
3行1列のMatrix型(e,n,u)の戻り値を得る。  
""""
#### BacktoECEF(matrix, double[][], double, double)
ENU座標で表される位置をECEF座標に変換する。  
ENU座標が保持される3行1列のMatrix型、ENU座標の原点となるECEF座標が保持されるdouble型配列(X,Y,Z),  
ENU座標の原点となる測地座標のdouble型2種(lat, lon)を引数にとり、  
変換されたECEF座標がdouble型配列で戻り値として得られる。
#### ECEFtoWGS(double[][])
ECEF座標を測地座標に変換する。  
ECEF座標が保持されたdouble型配列(X,Y,Z)を引数にとり、  
変換された測地座標がdouble型配列(lat,lon,height)で戻り値として得られる。  

