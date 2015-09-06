package com.mk.framework.component.message;

/**
 * message constant.
 * @author chuaiqing.
 *
 */
public class Constant {
    //session中的绑定值
    public static final String MySessionName="mysession_object";
    //系统配置service
    public static final String sysConfigService="sysConfigService";
    public static final String userService="userService";
    public static final String roleService="roleService";
    public static final String depaService="depaService";
    public static final String tableService="tableService";
    public static final String assignService="assignService";
    public static final String functionService="functionService";
    public static final String authService="authService";
    public static final String logService="logService";
    public static final String cityService = "cityService";
    public static final String hotelService = "hotelService";
    public static final String qrcodeService = "qrcodeService";
    public static final String appService = "appService";
    public static final String businessZoneService = "businessZoneService";
    public static final String memberService = "memberService";
    public static final String userCtrl="userCtrl";
    public static final String roleCtrl="roleCtrl";
    public static final String depaCtrl="depaCtrl";
    public static final String assignCtrl="assignCtrl";
    public static final String functionCtrl="functionCtrl";
    public static final String authCtrl="authCtrl";
    public static final String logCtrl="logCtrl";
    public static final String cityCtrl = "cityCtrl";
    public static final String hotelCtrl = "hotelCtrl";
    public static final String businessZoneCtrl = "businessZoneCtrl";
    public static final String memberCtrl = "memberCtrl";
    public static final String aboutCtrl = "aboutCtrl";
    public static final String aboutService = "aboutService";
    //系统配置type
    public static final String systype="sys";
    //默认编码
    public static final String defaultcharset="UTF-8";
    //14位时间
    public static final String time14="yyyyMMddHHmmss";
    //8位时间
    public static final String time8="yyyyMMdd";
    //数据库表名
    public static final String ID="id";
    
    public static final int depaPathLength=8;
    public static final int apptypePathLength=8;
    public static final int filePathLength=10;
    
    //错误密码登录次数
    public static final String loginErrorByPswTime="loginErrorByPswTime";
    //禁止登录时间（分钟）
    public static final String disableLoginTime="disableLoginTime";
    //新建帐户是否需要修改密码
    public static final String newUserNeedUpdatePsw="newUserNeedUpdatePsw";
    //重置密码有效期
    public static final String changePswTime="changePswTime";
    //系统邮箱配置
    public static final String statusMailServer="statusMailServer";
    public static final String systemMail="systemMail";
    public static final String statusMailPassword="statusMailPassword";
    public static final String systemPort="systemPort";
    
    //系统管理员帐号id
    public static final Long sysadminid = 1L;
    
//  public static Object dbname="hotel_manager";
    
    public static final String pmsRoleId = "pmsroleid";
    public static final String messagesn = "messagesn";
    public static final String messagepsw = "messagepsw";
    public static final String messageUrl = "messageurl";
    public static final String messageAudioUrl = "messageaudiourl";
    public static final String mikewebtype = "mikeweb";
    public static final String mikewebWebUrl = "mikewebHessianUrl";
    
    public static final String qieketicketno = "qieketicketno";
    public static final String yijiaticketno = "yijiaticketno";
    public static final String qiekeUrl = "qiekeUrl";
    
    public static final String QIKE_TODAY_LIMITNUM = "QIKE_TODAY_LIMITNUM";
    public static final String QIKE_MONTH_LIMITNUM = "QIKE_MONTH_LIMITNUM";
    
    public static final String WX_TEST_DEBUG = "WX_TEST_DEBUG";
}
