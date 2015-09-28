package com.mk.ots.restful.input;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.mk.ots.common.bean.ParamBaseBean;

/**
 * search 联想搜索API接口入参实体类.
 * @author chuaiqing.
 *
 */
public class SearchReqEntity extends ParamBaseBean {

    /**
     * 序列化UID
     */
    private static final long serialVersionUID = 4143656557417464829L;
    
    /** 入参: 授权码,必填 */
    @NotNull(message="缺少参数-授权码: token.")
    @NotEmpty(message="接口参数token不能为空.")
    private String token;
    
    /** 入参: 搜索城市编码,必填 */
    @NotNull(message="缺少参数-搜索城市编码: citycode.")
    @NotEmpty(message="接口参数citycode不能为空.")
    private String citycode;
    
    /** 入参: 搜索关键字,必填 */
    @NotNull(message="缺少参数-搜索关键字: word.")
    @NotEmpty(message="接口参数word不能为空.")
    private String word;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getCitycode() {
        return citycode;
    }
    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }

}
