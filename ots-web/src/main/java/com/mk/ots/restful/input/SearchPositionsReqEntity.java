package com.mk.ots.restful.input;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.mk.ots.common.bean.ParamBaseBean;

/**
 * 
 * @author chuaiqing.
 *
 */
public class SearchPositionsReqEntity extends ParamBaseBean {

    /**
     * 序列化UID
     */
    private static final long serialVersionUID = 7344747371481325049L;
    
    /** 入参: 本地城市编码,必填 */
    @NotNull(message="缺少参数-城市编码: localcitycode.")
    @NotEmpty(message="接口参数localcitycode不能为空.")
    private String localcitycode;
    
    /** 入参: 搜索城市编码,必填 */
    @NotNull(message="缺少参数-搜索城市编码: citycode.")
    @NotEmpty(message="接口参数citycode不能为空.")
    private String citycode;
    
    private String ptype;
    
    
    public String getLocalcitycode() {
        return localcitycode;
    }
    public void setLocalcitycode(String localcitycode) {
        this.localcitycode = localcitycode;
    }
    
    public String getCitycode() {
        return citycode;
    }
    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
    
    
}
