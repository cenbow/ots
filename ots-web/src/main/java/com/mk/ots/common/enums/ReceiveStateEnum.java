package com.mk.ots.common.enums;

/**
 * Created by kirinli on 15/11/12.
 */
public enum ReceiveStateEnum {
    Geted(1,"已领取"),
    Unget(2,"未领取"),
    Recover(3,"已回收"),
    binding(4,"已绑定"),
    ;

    private final Integer id;
    private final String name;

    private ReceiveStateEnum(Integer id, String name){
        this.id=id;
        this.name=name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + "";
    }
}