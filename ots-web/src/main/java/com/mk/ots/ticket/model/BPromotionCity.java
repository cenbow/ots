package com.mk.ots.ticket.model;

import java.util.Date;

/**
 * Created by ljx on 15/10/13.
 */
public class BPromotionCity {

    /**
     * 自增主键id
     */
    private Long id;

    /**
     * 批次号
     */
    private Long  promotionId;

    /**
     * 城市编码
     */
    private  String  cityCode;

    /**
     *  城市名称
     */
    private  String  cityName;

    /**
     * 活动Id
     */
    private  Long   activityId;

    /**
     * 状态 是否删除
     */
    private  boolean    delete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
