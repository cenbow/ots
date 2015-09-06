package com.mk.ots.comp;

import org.springframework.stereotype.Component;

@Component
public class SynOrderConf implements SynOrderConfMBean {

	private Long hotelId = null;

	private Boolean enable = null;

	public SynOrderConf() {
		this.hotelId = 0L;
		this.enable = Boolean.FALSE;
	}

	@Override
	public Long getHotelId() {
		return this.hotelId;
	}

	@Override
	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}

	@Override
	public Boolean getEnable() {
		return this.enable;
	}

	@Override
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}
