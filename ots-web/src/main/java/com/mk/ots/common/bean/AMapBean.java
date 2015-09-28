package com.mk.ots.common.bean;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mk.ots.common.enums.AMapEnum;
import com.mk.ots.common.utils.CalculateMd5;

/**
 *
 * @author shellingford
 * @version 2015年2月16日
 */
public class AMapBean {
	private final double minLon;
	private final double maxLon;
	private final double minLat;
	private final double maxLat;
	private final AMapEnum level;
	private final int no;
	private final int x;
	private final int y;
	private final double left;
	private final double top;
	private final double right;
	private final double bottom;
	private final double addlon;
	private final double addlat;
	
	
	public static AMapBean findAll(List<AMapBean> list){
//		AMapBean returnbean=new AMapBean();
		if(list==null || list.size()==0){
			return null;
		}
		double minLon=AMapEnum.MAXLON;
		double maxLon=AMapEnum.MINLON;
		double minLat=AMapEnum.MAXLAT;
		double maxLat=AMapEnum.MINLAT;
		AMapEnum level=list.get(0).getLevel();
		//当x区块存在不连续时，表示跨越了180°
		Set<Integer> xlist=new TreeSet<Integer>();
		for (AMapBean aMapBean : list) {
			if(aMapBean.getMinLon()<minLon){
				minLon=aMapBean.getMinLon();
			}
			if(aMapBean.getMaxLon()>maxLon){
				maxLon=aMapBean.getMaxLon();
			}
			if(aMapBean.getMinLat()<minLat){
				minLat=aMapBean.getMinLat();
			}
			if(aMapBean.getMaxLat()>maxLat){
				maxLat=aMapBean.getMaxLat();
			}
			xlist.add(aMapBean.getX());
		}
		boolean over=false;
		Iterator<Integer> iter=xlist.iterator();
		int base=iter.next();
		while(iter.hasNext()){
			base++;
			if(iter.next().intValue()!=base){
				over=true;
			}
		}
		AMapBean bean=new AMapBean(minLon, maxLon, minLat, maxLat, level, over);
		return bean;
	}
	
	public AMapBean(double minLon, double maxLon, double minLat, double maxLat,
			AMapEnum level,boolean over) {
		super();
		this.minLon = minLon;
		this.maxLon = maxLon;
		this.minLat = minLat;
		this.maxLat = maxLat;
		this.level = level;
		this.no = 0;
		this.x = 0;
		this.y = 0;
		this.left = minLon;
		this.top = maxLat;
		this.right = maxLon;
		this.bottom = minLat;
		this.addlon=0;
		this.addlat=0;
	}
	
	public AMapBean(double minLon, double maxLon, double minLat, double maxLat,
			AMapEnum level, int no, int x, int y) {
		super();
		this.minLon = minLon;
		this.maxLon = maxLon;
		this.minLat = minLat;
		this.maxLat = maxLat;
		this.level = level;
		this.no = no;
		this.x = x;
		this.y = y;
		this.left = minLon;
		this.top = maxLat;
		this.right = maxLon;
		this.bottom = minLat;
		int tx=Math.abs(CalculateMd5.caculateCF(new Integer(x).toString(),"UTF-8").hashCode());
		addlon=(tx%60+20)*0.01*level.getLonr();
		int ty=Math.abs(CalculateMd5.caculateCF(new Integer(y).toString(),"UTF-8").hashCode());
		addlat=(ty%60+20)*0.01*level.getLatr();
		
	}

	public double getMinLon() {
		return minLon;
	}

	public double getMaxLon() {
		return maxLon;
	}

	public double getMinLat() {
		return minLat;
	}

	public double getMaxLat() {
		return maxLat;
	}

	public AMapEnum getLevel() {
		return level;
	}

	public int getNo() {
		return no;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public double getLeft() {
		return left;
	}

	public double getTop() {
		return top;
	}

	public double getRight() {
		return right;
	}

	public double getBottom() {
		return bottom;
	}

	public double getAddlon() {
		return addlon;
	}

	public double getAddlat() {
		return addlat;
	}

	@Override
	public String toString() {
		return "AMapBean [minLon=" + minLon + ", maxLon=" + maxLon
				+ ", minLat=" + minLat + ", maxLat=" + maxLat + ", level="
				+ level + ", no=" + no + ", x=" + x + ", y=" + y + ", left="
				+ left + ", top=" + top + ", right=" + right + ", bottom="
				+ bottom + ", addlon=" + addlon + ", addlat=" + addlat + "]";
	}
	
}
