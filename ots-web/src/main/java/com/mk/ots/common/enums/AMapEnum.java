package com.mk.ots.common.enums;

import java.util.ArrayList;
import java.util.List;

import com.mk.ots.common.bean.AMapBean;

/**
 *
 * @author shellingford
 * @version 2015年2月16日
 */
public enum AMapEnum {
	L18(18,121.618576,31.265619,121.622436,31.25975),
	L17(17,121.616645,31.26803,121.624366,31.25629),
	L16(16,121.612777,31.272842,121.628227,31.249364),
	L15(15,121.605056,31.282468,121.635955,31.235511),
	L14(14,121.589607,31.301725,121.651405,31.207805),
	L13(13,121.558708,31.340217,121.682304,31.152362),
	L12(12,121.49691,31.41716,121.744102,31.041383),
	L11(11,121.373313,31.570852,121.867698,30.819036),
	L10(10,121.126121,31.877479,122.114891,30.372797),
	L9(9,120.631736,32.487678,122.609275,29.474195),
	L8(8,119.642967,33.695705,123.598045,27.653039),
	L7(7,117.731346,36.030147,125.641502,23.884498),
	L6(6,113.776268,40.554435,129.596572,16.09719),
	L5(5,105.866111,48.740734,137.506729,-0.276122),
	L4(4,90.045799,61.746437,153.327041,-31.701375),
	L3(3,58.405174,77.164161,-175.032318,-70.129661),
	;
	
	public static final double MINLON=-180D;
	public static final double MAXLON=180D;
	public static final double MINLAT=-90D;
	public static final double MAXLAT=90D;
	
	private final int level;
	private final double lonr;
	private final double latr;
	private final int maxX;
	private final int maxY;
	
	/**
	 * 根据地图等级获得相应的枚举类
	 * @param level
	 * @return
	 */
	public static AMapEnum findAMapByLevel(int level){
		for (AMapEnum one : AMapEnum.values()) {
			if(one.level==level){
				return one;
			}
		}
		return null;
	}
	
	/**
	 * 在该等级下，通过传入对角线坐标创建区块list
	 * 容错处理：只要是对角线坐标即可，无视左上和右下还是左下和右上，无视lon1与lat1为一个坐标点还是lon1与lat2为一个坐标点
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public List<AMapBean> createZone(double lon1,double lat1,double lon2,double lat2){
		List<AMapBean> returnlist=new ArrayList<AMapBean>();
		//容错处理，全部从小至大
		double minlon=lon1>lon2?lon2:lon1;
		double maxlon=lon1>lon2?lon1:lon2;
		double minlat=lat1>lat2?lat2:lat1;
		double maxlat=lat1>lat2?lat1:lat2;
		boolean over=false;
		if((maxlon-minlon)>180D){
			//跨越180°
			over=true;
		}
		//在定位边界区块时跨越180°并不影响
		int $minx=(int) Math.floor((minlon-MINLON)/lonr)+1;
		if($minx>maxX){
			//这种情况表示左侧坐标正好落在最大的经度线上，该区块包含左右两侧的线
			$minx=maxX;
		}
		int $maxx=(int) Math.floor((maxlon-MINLON)/lonr)+1;
		if($maxx>maxX){
			$maxx=maxX;
		}
		int $miny=(int) Math.floor((minlat-MINLAT)/latr)+1;
		if($miny>maxY){
			$miny=maxY;
		}
		int $maxy=(int) Math.floor((maxlat-MINLAT)/latr)+1;
		if($maxy>maxY){
			$maxy=maxY;
		}
		if(over){
			//跨越180°的情况,仅影响经度,先从最小区块开始递减
			int i=$minx;
			boolean check=false; //检测循环是否已经越过180°
			while(true){
				for (int j = $miny; j <= $maxy; j++) {
					double tempMinLon=(i-1)*lonr+MINLON;
					double tempMaxLon=i*lonr+MINLON;
					if(tempMaxLon>MAXLON){
						tempMaxLon=MAXLON;
					}
					double tempMinLat=(j-1)*latr+MINLAT;
					double tempMaxLat=j*latr+MINLAT;
					if(tempMaxLat>MAXLAT){
						tempMaxLat=MAXLAT;
					}
					int no=(j-1)*maxX+i;
					AMapBean bean=new AMapBean(tempMinLon,tempMaxLon,tempMinLat,tempMaxLat,this,no,i,j);
					returnlist.add(bean);
				}
				i--;
				if(check && i<$maxx){
					break;
				}
				if(i<1){
					i=maxX;
					check=true;
				}
			}
		}else{
			for (int i = $minx; i <= $maxx; i++) {
				for (int j = $miny; j <= $maxy; j++) {
					double tempMinLon=(i-1)*lonr+MINLON;
					double tempMaxLon=i*lonr+MINLON;
					if(tempMaxLon>MAXLON){
						tempMaxLon=MAXLON;
					}
					double tempMinLat=(j-1)*latr+MINLAT;
					double tempMaxLat=j*latr+MINLAT;
					if(tempMaxLat>MAXLAT){
						tempMaxLat=MAXLAT;
					}
					int no=(j-1)*maxX+i;
					AMapBean bean=new AMapBean(tempMinLon,tempMaxLon,tempMinLat,tempMaxLat,this,no,i,j);
					returnlist.add(bean);
				}
			}
			
		}
		return returnlist;
	}
	
	private AMapEnum(int level,double lon1,double lat1,double lon2,double lat2){
		this.level=level;
		lonr=Math.abs((lon2-lon1)/5);
		latr=Math.abs((lat1-lat2)/3);
//		System.out.println("每个经度"+lonr);
//		System.out.println("每个经度"+latr);
		maxX=(int) Math.ceil(Math.abs(MAXLON-MINLON)/lonr);
		maxY=(int)Math.ceil(Math.abs(MAXLAT-MINLAT)/latr);
//		System.out.println("整个地图分为"+maxX+"*"+maxY+"块区域");
	}
	
	public double getLonr() {
		return lonr;
	}

	public double getLatr() {
		return latr;
	}

}
