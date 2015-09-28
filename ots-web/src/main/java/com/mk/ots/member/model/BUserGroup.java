package com.mk.ots.member.model;

import java.util.Date;

public class BUserGroup {
    private String id;

    private String groupCode;

    private Date groupName;

    private Integer sFlag;

    private String userCode;

    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode == null ? null : groupCode.trim();
    }

    public Date getGroupName() {
        return groupName;
    }

    public void setGroupName(Date groupName) {
        this.groupName = groupName;
    }

    public Integer getsFlag() {
        return sFlag;
    }

    public void setsFlag(Integer sFlag) {
        this.sFlag = sFlag;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode == null ? null : userCode.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}