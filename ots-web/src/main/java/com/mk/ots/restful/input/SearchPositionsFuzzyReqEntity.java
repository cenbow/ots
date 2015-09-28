package com.mk.ots.restful.input;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.mk.ots.common.bean.ParamBaseBean;

/**
 * 
 * @author chuaiqing.
 *
 */
public class SearchPositionsFuzzyReqEntity extends ParamBaseBean {

    /**
     * 序列化UID
     */
    private static final long serialVersionUID = 1L;
    
    
    /** 入参: 搜索城市编码,必填 */
    @NotNull(message="缺少参数-搜索城市编码: citycode.")
    @NotEmpty(message="接口参数citycode不能为空.")
    private String citycode;
    
    /** 入参: 本地城市编码,必填 */
    @NotNull(message="缺少参数-城市编码: word.")
    @NotEmpty(message="接口参数word不能为空.")
    private String word;
    
    public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getCitycode() {
        return citycode;
    }
    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
}
