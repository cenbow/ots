package com.mk.ots.wallet.model;

public class UWalletCashFlowExtend extends UWalletCashFlow{
    /**
     * cashflowtype描述
     */
    private String cashflowtypestr;
    /**
     * 支出 1  收入 2
     */
    private int isgetin;

    public String getCashflowtypestr() {
        return cashflowtypestr;
    }

    public void setCashflowtypestr(String cashflowtypestr) {
        this.cashflowtypestr = cashflowtypestr;
    }

    public int getIsgetin() {
        return isgetin;
    }

    public void setIsgetin(int isgetin) {
        this.isgetin = isgetin;
    }
}