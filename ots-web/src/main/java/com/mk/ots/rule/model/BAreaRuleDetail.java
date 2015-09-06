package com.mk.ots.rule.model;

import java.util.Date;

public class BAreaRuleDetail {
    private Long id;

    private String rulekey;

    private String rulevalue;

    private Integer createby;

    private Integer updateby;

    private Date createtime;

    private Date updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRulekey() {
        return rulekey;
    }

    public void setRulekey(String rulekey) {
        this.rulekey = rulekey == null ? null : rulekey.trim();
    }

    public String getRulevalue() {
        return rulevalue;
    }

    public void setRulevalue(String rulevalue) {
        this.rulevalue = rulevalue == null ? null : rulevalue.trim();
    }

    public Integer getCreateby() {
        return createby;
    }

    public void setCreateby(Integer createby) {
        this.createby = createby;
    }

    public Integer getUpdateby() {
        return updateby;
    }

    public void setUpdateby(Integer updateby) {
        this.updateby = updateby;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}