package com.mk.pms.bean;

import java.util.Date;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.ots.promo.model.BPromotion;

//@Component
//@DbTable(name="p_log", pkey="id")
public class PmsLog extends BizModel<BPromotion> {
    
    public static final PmsLog dao = new PmsLog();

	private static final long serialVersionUID = 7269124790914566631L;

//	@DbFieldMapping(dbColName = "id", isPrimaryKey = true, isAutoPrimaryKey=false)
//	private Long id;
//	@DbFieldMapping(dbColName = "pmsno")
//	private String pmsno;
//	@DbFieldMapping(dbColName = "hotelId")
//	private Long hotelId;
//	@DbFieldMapping(dbColName = "type")
//	private PmsLogTypeEnum type;
//	@DbFieldMapping(dbColName = "reslut")
//	private PmsResultEnum reslut;
//	@DbFieldMapping(dbColName = "content")
//	private String content;
//	@DbFieldMapping(dbColName = "time",saveBigintByDate=false)
//	private Date time;
//	@DbFieldMapping(dbColName = "trytime")
//	private Integer trytime;
//	@DbFieldMapping(dbColName = "level")
//	private Integer level;
//	@DbFieldMapping(dbColName = "note")
//	private String note;
//	@DbFieldMapping(dbColName = "errorMsg")
	private String errorMsg;
	
	private Document doc;
	public Long getId() {
		return get("id");
	}
	public void setId(Long id) {
		set("id", id);
	}
	public String getPmsno() {
		return get("pmsno");
	}
	public void setPmsno(String pmsno) {
		
		set("pmsno", pmsno);
	}
	public Long getHotelId() {
		return get("hotelId");
	}
	public void setHotelId(Long hotelId) {
		set("hotelId", hotelId);
	}
	public String getType() {
		return get("type");
	}
	public void setType(String type) {
		set("type", type);
	}
	public String getReslut() {
		return get("reslut");
	}
	public void setReslut(String reslut) {
		set("reslut", reslut);
	}
	public String getContent() {
		return get("content");
	}
	public void setContent(String content) {
		set("content", content);
	}
	public Date getTime() {
		return get("time");
	}
	public void setTime(Date time) {
		set("time", time);
	}
	public Integer getTrytime() {
		return get("trytime");
	}
	public void setTrytime(Integer trytime) {
		set("trytime", trytime);
	}
	public Integer getLevel() {
		return get("level");
	}
	public void setLevel(Integer level) {
		set("level", level);
	}
	
	public String getNote() {
		return get("note");
	}
	public void setNote(String note) {
		set("note", note);
	}
	public String getErrorMsg() {
		return get("errorMsg");
	}
	public void setErrorMsg(String errorMsg) {
		set("errorMsg", errorMsg);
	}
	public Document getDoc() {
		return get("doc");
	}
	public void setDoc(Document doc) {
		set("doc", doc);
	}

}
