package com.mk.ots.common.enums;

/**
 * Created by Thinkpad on 2015/12/21.
 */
public enum AppUrlEnum {
    orderList("www.imikeshareMessage-url-scheme://orderlist","APP跳转到订单列表页面"),
    map("www.imikeshareMessage-url-scheme://map","APP跳转地图页"),
    coupon("www.imikeshareMessage-url-scheme://coupon","APP跳转到优惠券"),
    redirectUrl("www.imikeshareMessage-url-scheme://innerwap/url","url地址(app跳转到内置网页)"),
    activityUrl("www.imikeshareMessage-url-scheme://inneractivityurl/activityurl","活动url地址（app跳转到活动页面）"),
    orderDetail("www.imikeshareMessage-url-scheme://orderdetail/orderid/token","订单详情orderid替换成订单id token替换成token");
    private String url;
    private String text;

    AppUrlEnum(String url, String text) {
        this.url = url;
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }
}
