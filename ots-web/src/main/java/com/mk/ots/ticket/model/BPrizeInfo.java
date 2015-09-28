package com.mk.ots.ticket.model;


public class BPrizeInfo {
    private Long id;
    private Integer type;
    private String  name;
    private String code;
    private Long price;
    private String begintime;
    private String endtime;
    private String createtime;
    private String url;
    private Long merchantid;
    private Long prizeRecordId;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public Long getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(Long merchantid) {
		this.merchantid = merchantid;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code == null ? "" : code.trim();
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price == null ? 0L : price;
	}

	
	public String getBegintime() {
		return begintime;
	}

	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getPrizeRecordId() {
		return prizeRecordId;
	}

	public void setPrizeRecordId(Long prizeRecordId) {
		this.prizeRecordId = prizeRecordId;
	}

	@Override
	public String toString() {
		return "BPrizeInfo [id=" + id + ", type=" + type + ", name=" + name
				+ ", code=" + code + ", price=" + price + ", begintime="
				+ begintime + ", endtime=" + endtime + ", createtime="
				+ createtime + ", url=" + url + ", merchantid=" + merchantid
				+ ", prizeRecordId=" + prizeRecordId + "]";
	}

	

	
}
