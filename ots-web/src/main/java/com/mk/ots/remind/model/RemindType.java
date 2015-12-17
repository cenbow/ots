package com.mk.ots.remind.model;

import java.util.Date;

public class RemindType {
   private Long id;
   private String code;
   private String name;
   private String content;
   private String url;
   private String title;
   private Date remindTime;
   private Integer weixin;
   private Integer sms;
   private Integer push;
   private Integer valid;
   private Date createTime;
   private String createBy;
   private Date updateTime;
   private String updateBy;

   public Date getRemindTime() {
      return remindTime;
   }

   public void setRemindTime(Date remindTime) {
      this.remindTime = remindTime;
   }
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getCode() {
      return code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Integer getWeixin() {
      return weixin;
   }

   public void setWeixin(Integer weixin) {
      this.weixin = weixin;
   }

   public Integer getSms() {
      return sms;
   }

   public void setSms(Integer sms) {
      this.sms = sms;
   }

   public Integer getPush() {
      return push;
   }

   public void setPush(Integer push) {
      this.push = push;
   }

   public Integer getValid() {
      return valid;
   }

   public void setValid(Integer valid) {
      this.valid = valid;
   }

   public Date getCreateTime() {
      return createTime;
   }

   public void setCreateTime(Date createTime) {
      this.createTime = createTime;
   }

   public String getCreateBy() {
      return createBy;
   }

   public void setCreateBy(String createBy) {
      this.createBy = createBy;
   }

   public Date getUpdateTime() {
      return updateTime;
   }

   public void setUpdateTime(Date updateTime) {
      this.updateTime = updateTime;
   }

   public String getUpdateBy() {
      return updateBy;
   }

   public void setUpdateBy(String updateBy) {
      this.updateBy = updateBy;
   }
}
