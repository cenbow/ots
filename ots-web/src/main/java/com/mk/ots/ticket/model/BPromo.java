package com.mk.ots.ticket.model;

import java.util.Date;

/**
 * Created by ljx on 15/10/13.
 */
public class BPromo {

    /**
     * 自增主键id
     */
    private Long id;

    /**
     * 批次号
     */
    private String  batchNo;

    /**
     * 名称
     */
    private  String  promoName;

    /**
     * 劵号
     */
    private  String  promoNo;

    /**
     * 卷码
     */
    private  String   promoPwd;

    /**
     * 状态 1:生成;2:入库;3:激活；4：使用；5：注销
     */
    private  Integer   promoStatus;

    /**
     * 使用城市Id
     */
    private  long  promoCityId;

    /**
     * 类型  0:其他；1：今夜特价
     */
    private  int  promoType;

    /**
     * 开始时间（HH:mm）
     */
    private  String  beginTime;

    /**
     * 结束时间（HH:mm）
     */
    private   String  endTime;

    /**
     * 有效期开始日期（yyyy-MM-dd）
     */
    private Date beginDate;

    /**
     * 有效期结束日期（yyyy-MM-dd）
     */
    private Date endDate;


    /**
     * 描述
     */
    private  String  description;

    /**
     * 创建时间
     */
    private  Date   createTime;

    /**
     * 更新时间
     */
    private  Date   updateTime;

    /**
     * 创建人员
     */
    private  String   createBy;

    /**
     * 创建人员
     */
    private  String   updateBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public String getPromoNo() {
        return promoNo;
    }

    public void setPromoNo(String promoNo) {
        this.promoNo = promoNo;
    }

    public String getPromoPwd() {
        return promoPwd;
    }

    public void setPromoPwd(String promoPwd) {
        this.promoPwd = promoPwd;
    }

    public Integer getPromoStatus() {
        return promoStatus;
    }

    public void setPromoStatus(Integer promoStatus) {
        this.promoStatus = promoStatus;
    }

    public long getPromoCityId() {
        return promoCityId;
    }

    public void setPromoCityId(long promoCityId) {
        this.promoCityId = promoCityId;
    }

    public int getPromoType() {
        return promoType;
    }

    public void setPromoType(int promoType) {
        this.promoType = promoType;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
}
