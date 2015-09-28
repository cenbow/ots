package com.mk.pms.bean;

import java.io.Serializable;
import java.util.List;

import cn.com.winhoo.mikeweb.hotelpojo.TRoomRepair;

import com.mk.ots.order.bean.PmsOrder;
import com.mk.ots.order.bean.PmsRoomOrder;

/**
 *
 * @author shellingford
 * @version 2015年1月12日
 */
public class ChangeSynOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1544699698119792991L;

	private List<PmsOrder> changeOrder;
	private List<PmsRoomOrder> changeRoomOrder;
	private List<TRoomRepair> changeRepair;
	
	public List<PmsOrder> getChangeOrder() {
		return changeOrder;
	}
	public void setChangeOrder(List<PmsOrder> changeOrder) {
		this.changeOrder = changeOrder;
	}
	public List<PmsRoomOrder> getChangeRoomOrder() {
		return changeRoomOrder;
	}
	public void setChangeRoomOrder(List<PmsRoomOrder> changeRoomOrder) {
		this.changeRoomOrder = changeRoomOrder;
	}
	public List<TRoomRepair> getChangeRepair() {
		return changeRepair;
	}
	public void setChangeRepair(List<TRoomRepair> changeRepair) {
		this.changeRepair = changeRepair;
	}
}
