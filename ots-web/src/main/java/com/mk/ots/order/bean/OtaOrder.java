package com.mk.ots.order.bean;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mk.framework.AppUtils;
import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.ots.hotel.bean.TCity;
import com.mk.ots.hotel.bean.TDistrict;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.order.model.OtaOrderMac;
import com.mk.ots.manager.OtsCacheManager;

/**
 * b_otaorder
 */
@Component
@DbTable(name = "b_otaorder", pkey = "id")
public class OtaOrder extends BizModel<OtaOrder> {

	private static final long serialVersionUID = -434027864719974886L;
	public static final OtaOrder dao = new OtaOrder();

	private List<OtaRoomOrder> roomOrderList;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private com.mk.ots.order.model.OtaOrderMac otaOrderMac;

	public OtaOrderMac getOtaOrderMac() {
		return otaOrderMac;
	}

	public void setOtaOrderMac(OtaOrderMac otaOrderMac) {
		this.otaOrderMac = otaOrderMac;
	}

	public OtaOrder() {
		super();
		try {
			this.set("receipt", "F");
			this.set("promotion", "F");
			this.set("coupon", "F");
			this.set("canshow", "T");
			this.set("hiddenOrder", "F");
		} catch (Exception e) {
		}
	}
	
	public OtaOrder(Long id,String key,String value) {
		this.setId(id);
		this.set(key, value);
	}
	
	public OtaOrder saveOrUpdate() {
		if (this.get("id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}

	public String getCityCode(){
		return this.get("citycode","");
	}
	
	public void setCityCode(String cityCode){
		this.set("citycode",cityCode);
	}
	
	/**
	 * 此订单使用的返现
	 * @return
	 */
	public BigDecimal getAvailableMoney(){
		return this.get("availablemoney");
	}
	
	/**
	 * 得到，返现
	 * @return
	 */
	public BigDecimal getCashBack(){
		return this.get("cashback");
	}
	/**
	 * 得到结算类型
	 * @return
	 */
	public int getClearingType(){
		return this.get("clearingtype");
	}
	/**
	 * 设置结算类型
	 * @param clearingtype
	 */
	public void setClearingType(int clearingtype){
		this.set("clearingtype", clearingtype);
		
	}
	/**
	 * 设置结算金额
	 * @param clearingMoney
	 */
	public void setClearingMoney(BigDecimal clearingMoney){
		this.set("clearingmoney", clearingMoney);
	}
	/**
	 * 得到结算金额
	 * @return
	 */
	public BigDecimal getClearingMoney(){
		return this.get("clearingmoney");
	}
	/**
	 * 设置，订单能用的返现
	 * @param availableMoney
	 */
	public void setAvailableMoney(BigDecimal availableMoney){
		this.set("availablemoney", availableMoney);
	}
	/**
	 * 设置，返现
	 * @param cashBack
	 */
	public void setCashBack(BigDecimal cashBack){
		this.set("cashback", cashBack);
	}
	
	/**
	 *  0，无需领取
    * 1，还未领取
    * 2，已经领取
	 * @param isReceiveCashBack
	 */
	public void setIsReceiveCashBack(int isReceiveCashBack){
		this.set("IsReceiveCashBack", isReceiveCashBack);
	}
	
	/**
	 * 得到返现是否已经领取
	 * @return
	 */
	public int getReceiveCashBack(){
		return this.get("isreceivecashback",0);
	}
	
	public String getOrderId() {
		return this.get("orderid", "");
	}

	public void setOrderId(String id) {
		this.set("orderid", id);
	}

	public Long getId() {
		return this.get("id");
	}

	public void setId(Long id) {
		this.set("id", id);
	}

	public Long getHotelId() {
		return this.get("hotelid");
	}

	public void setHotelId(Long hotelId) {
		this.set("hotelid", hotelId);
	}

	public String getHotelName() {
		return this.get("hotelname");
	}

	public void setHotelName(String hotelName) {
		this.set("hotelname", hotelName);
	}

	public String getHotelPms() {
		return this.get("hotelpms");
	}

	public void setHotelPms(String hotelPms) {
		this.set("hotelpms", hotelPms);
	}

	public Integer getOrderMethod() {
		return this.get("ordermethod");
	}

	public void setOrderMethod(int orderMethod) {
		this.set("ordermethod", orderMethod);
	}

	public int getOrderType() {
		return this.get("ordertype");
	}

	public void setOrderType(int orderType) {
		this.set("ordertype", orderType);
	}

	public int getPriceType() {
		return this.get("pricetype");
	}

	public void setPriceType(int priceType) {
		this.set("pricetype", priceType);
	}

	public Date getBeginTime() {
		return this.get("begintime");
	}

	public void setBeginTime(Date beginTime) {
		this.set("begintime", beginTime);
	}

	public Date getEndTime() {
		return this.get("endtime");
	}

	public void setEndTime(Date endTime) {
		this.set("endtime", endTime);
	}

	public Long getMid() {
		return this.get("mid");
	}

	public void setMid(Long mid) {
		this.set("mid", mid);
	}

	public int getMlevel() {
		return this.get("mlevel");
	}

	public void setMlevel(int mlevel) {
		this.set("mlevel", mlevel);
	}

	public Date getCreateTime() {
		return this.getDate("createtime");
	}

	public void setCreateTime(Date createTime) {
		this.set("createtime", createTime);
	}

	public String getPromotion() {
		return this.get("promotion");
	}

	public void setPromotion(String promotion) {
		this.set("promotion", promotion);
	}

	public String getCoupon() {
		return this.get("coupon");
	}

	public void setCoupon(String coupon) {
		this.set("coupon", coupon);
	}

	public BigDecimal getTotalPrice() {
		return this.get("totalprice");
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.set("totalprice", totalPrice);
	}

	public BigDecimal getPrice() {
		return this.get("price");
	}

	public void setPrice(BigDecimal price) {
		this.set("price", price);
	}

	public Integer getBreakfastNum() {
		return this.get("breakfastnum");
	}

	public void setBreakfastNum(Integer breakfastNum) {
		this.set("breakfastnum", breakfastNum);
	}

	public String getContacts() {
		return this.get("contacts", "");
	}

	public void setContacts(String contacts) {
		this.set("contacts", contacts);
	}

	public String getContactsPhone() {
		return this.get("contactsphone", "");
	}

	public void setContactsPhone(String contactsPhone) {
		this.set("contactsphone", contactsPhone);
	}

	public String getContactsEmail() {
		return this.get("contactsemail", "");
	}

	public void setContactsEmail(String contactsEmail) {
		this.set("contactsemail", contactsEmail);
	}

	public String getContactsWeiXin() {
		return this.get("contactsweixin", "");
	}

	public void setContactsWeiXin(String contactsWeiXin) {
		this.set("contactsweixin", contactsWeiXin);
	}

	public String getNote() {
		return this.get("note", "");
	}

	public void setNote(String note) {
		this.set("note", note);
	}

	public int getOrderStatus() {
		return this.get("orderstatus");
	}

	public void setOrderStatus(int orderStatus) {
		this.set("orderstatus", orderStatus);
	}

	public int getPayStatus() {
		return this.get("paystatus");
	}

	public void setPayStatus(int payStatus) {
		this.set("paystatus", payStatus);
	}

	public String getReceipt() {
		return this.get("receipt");
	}

	public void setReceipt(String receipt) {
		this.set("receipt", receipt);
	}

	public Date getUpdateTime() {
		return this.get("updatetime");
	}

	public void setUpdateTime(Date updateTime) {
		this.set("updatetime", updateTime);
	}

	// public List<OtaRoomOrder> getRoomOrderList() {
	// return roomOrderList;
	// }
	// public void setRoomOrderList(List<OtaRoomOrder> roomOrderList) {
	// this.roomOrderList = roomOrderList;
	// }
	public String getCanshow() {
		return this.get("canshow");
	}

	public void setCanshow(String canshow) {
		this.set("canshow", canshow);
	}

	/**
	 * 取规则码
	 * @return
	 */
	public Integer getRuleCode(){
		return this.get("rulecode");
	}
	/**
	 * 设置规则码
	 * @param ruleCode
	 */
	public void setRuleCode(Integer ruleCode){
		this.set("rulecode", ruleCode);
	}
	
	public String getHiddenOrder() {
		return this.get("hiddenorder");
	}

	public void setHiddenOrder(String hiddenOrder) {
		this.set("hiddenOrder", hiddenOrder);
	}

	public Integer getVersion() {
		return this.get("version");
	}

	public void setVersion(Integer version) {
		this.set("version", version);
	}

	public String getOstype() {
		return this.get("ostype");
	}

	public void setOstype(String ostype) {
		this.set("ostype", ostype);
	}

	public String getHotelAddress() {
		return this.get("hoteladdress", "");
	}

	public void setHotelAddress(String hotelAddress) {
		this.put("hoteladdress", hotelAddress);
	}

	public Date getRetentionTime() {
		return this.get("retentiontime");
	}

	public void setRetentionTime(Date retentionTime) {
		this.put("retentiontime", retentionTime);
	}

	public Date getDefaultLeaveTime() {
		return this.get("defaultleavetime");
	}

	public void setDefaultLeaveTime(Date defaultLeaveTime) {
		this.put("defaultleavetime", defaultLeaveTime);
	}

	public String getHotelPic() {
		return this.get("hotelpic");
	}

	public void setHotelPic(String hotelPic) {
		this.put("hotelpic", hotelPic);
	}

	public Long getSpreadUser() {
		return this.get("spreaduser");
	}

	public void setSpreadUser(Long spreadUser) {
		this.set("spreaduser", spreadUser);
	}

	public String getPromotionNo() {
		return this.get("promotionno");
	}

	public void setPromotionNo(String promotionNo) {
		this.put("promotionno", promotionNo);
	}

	public String getCouponNo() {
		return this.get("couponno");
	}

	public void setCouponNo(String couponNo) {
		this.put("couponno", couponNo);
	}

	public Long getDaynumber() {
		return Long.valueOf(String.valueOf(this.get("daynumber")));
	}

	public void setDaynumber(Long daynumber) {
		this.set("daynumber", daynumber);
	}


	public String getPromoType() {
		return this.get("promotype");
	}

	public void setPromoType(String promoType) {
		this.set("promotype", promoType);
	}

	public String getRoomTicket() {
		return this.get("roomticket");
	}

	public void setRoomTicket(String roomTicket) {
		this.set("roomticket", roomTicket);
	}

	public void setRoomOrderList(List<OtaRoomOrder> roomOrderList) {
		this.roomOrderList = roomOrderList;
	}

	TCity city;

	public TCity getTCity() {
		if (this.city != null) {
			return this.city;
		}
		THotel hotel = THotel.dao.findById(this.getHotelId());
		TDistrict dis = TDistrict.dao.findById(hotel.get("disId"));
		this.city = TCity.dao.findById(dis.getLong("CityID"));
		return this.city;
	}

	public List<OtaRoomOrder> getRoomOrderList() {
		
		OtsCacheManager manager = AppUtils.getBean(OtsCacheManager.class);
		if(get("RoomOrderList") == null){
			//List<Long> ids = Db.query("select id from b_otaroomorder where OtaOrderId=?", getLong("id"));
			put("RoomOrderList", OtaRoomOrder.dao.find("select * from b_otaroomorder where OtaOrderId=?", getLong("id")));
			// comment by chuaiqing at 2015-05-17 11:34:00
			////manager.put(cacheSchema + this.getTable().getName(), get("id"), this);
		}
		return get("RoomOrderList");
	}
	
	public void setActiveid(Long activeid){
		this.set("activeid", activeid);
	}

	public Long getActiveid(){
		return this.get("activeid");
	}

}
