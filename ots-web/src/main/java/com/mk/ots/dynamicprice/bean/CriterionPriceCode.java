package com.mk.ots.dynamicprice.bean;

/**
 * Created by kirinli on 16/1/12.
 */
public class CriterionPriceCode {
    private  Integer id;
    private  Integer oClock;
    private  Double dropRatio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getoClock() {
        return oClock;
    }

    public void setoClock(Integer oClock) {
        this.oClock = oClock;
    }

    public Double getDropRatio() {
        return dropRatio;
    }

    public void setDropRatio(Double dropRatio) {
        this.dropRatio = dropRatio;
    }
}
