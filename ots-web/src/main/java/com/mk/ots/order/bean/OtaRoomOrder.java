package com.mk.ots.order.bean;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

/**
 *
 * @author shellingford
 * @version 2014年12月22日
 */
@Component
@DbTable(name = "b_otaroomorder", pkey = "id")
public class OtaRoomOrder extends BizModel<OtaRoomOrder> {

	public static final OtaRoomOrder dao = new OtaRoomOrder();

	public OtaRoomOrder() {
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

	public OtaRoomOrder getLinkRoomOrder() {
		String sql = "select * from b_otaroomorder t where t.LinkRoomOrderId = ?";
		OtaRoomOrder order = OtaRoomOrder.dao.findFirst(sql, this.getLong("LinkRoomOrderId"));
		return order;
	}

	public long getId() {
		return this.getLong("id");
	}

	public long getHotelId() {
		return this.getLong("hotelid");
	}

	public long getRoomId() {
		return this.getLong("roomid");
	}

	private static final long serialVersionUID = -1946892437339797142L;
	private List<OtaRoomPrice> roomPrice;

	public List<OtaRoomPrice> getRoomPrice() {
		return this.roomPrice;
	}

	public void setRoomPrice(List<OtaRoomPrice> roomPrice) {
		this.roomPrice = roomPrice;
	}

	public String getRoomTypeName() {
		return this.getStr("roomTypeName");
	}

	public String getHotelName() {
		return this.getStr("hotelname");
	}

	public String getRoomNo() {
		return this.getStr("roomNo");
	}

	public Long getRoomTypeId() {
		return this.get("roomTypeId");
	}

	public BigDecimal getTotalPrice() {
		return this.getBigDecimal("totalPrice");
	}

	public String getPmsRoomOrderNo() {
		return this.getStr("pmsroomorderno");
	}

	public String getContactsPhone() {
		return this.getStr("contactsPhone");
	}

	public OtaRoomOrder saveOrUpdate() {
		if (this.get("id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}

}