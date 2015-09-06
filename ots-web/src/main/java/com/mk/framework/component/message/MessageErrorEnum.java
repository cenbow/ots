package com.mk.framework.component.message;

/**
 * 
 * @author chuaiqing.
 *
 */
public enum MessageErrorEnum {
    errorParm("-1","error.1","参数错误"),
    errorLogin("-2","error.2","用户名或者密码错误"),
    userVisibleFalse("-3","error.3","帐号被禁用"), 
    needLogin("-4","error.4","请登录后访问"),
    disableLoginByTime("-5","error.5","未在设置的时间段内登录"),
    hasLoginname("-6","error.6","已存在该用户名"),
    cannotUpdateSysUser("-7","error.7","禁止修改系统帐户"),
    withoutpermission("-8","error.8","没有权限进行该项操作"),
    changePswKeyError("-9","error.9","重置密码认证失败"),
    cannotEditSelf("-10","error.10","不能修改自己所拥有的角色、部门、权限"),
    cannotEditSelfFatherDepa("-11","error.11","不能修改自己的父部门"),
    cannotAssignSelf("-12","error.12","不能给自己(或自己所有的角色、部门)分配/解除角色、部门、权限"),
    cannotAssignSysUser("-13","error.13","不能给系统帐户分配/解除角色、部门、权限"), 
    cannotDeleteRoleByDepa("-14","error.14","该角色存在关联的部门，不能被删除"),
    cannotDeleteRoleByUser("-15","error.15","该角色存在关联的用户，不能被删除"), 
    typePathError("-16","error.16","移动后该分类无法到达"),
    hasSameApp("-17","error.17","已存在该应用"),
    sessionError("-18","error.18","未登录或session已过期"),
    unRegisterMail("-33","error.33","未注册邮箱"),
    unRegisterUser("-34","error.34","该用户名未注册"),
    savePicError("-35","error.35","存储图片出错"),
    hasSameName("-36","error.36","已存在相同名字"),
    getDataError("-37","error.37","数据为空"),
    hotelNotOnline("-38","error.38","酒店未上线"),
    hasSameDis("-39","error.39","已存在该区县"),
    versionNotNumberError("-40","error.40","版本好必须为数字"),
    versionNameNullError("-41","error.41","版本号为空"),
    urlNullError("-42","error.42","url为空"),
    deleteError("-43","error.43","删除失败"),
    changeZoneTypeError("-44","error.44","不能修改商圈类别的类型"),
    changeZoneError("-45","error.45","不能修改商圈对应类别"),
    mobileNotEmpty("-46","error.46","号码不能为空"),
    messageNotEmpty("-47","error.47","发送信息不能为空"),
    xmlParseError("-48","error.48","xml解析失败"),
    NoApp("-49","error.49","不存在该应用"),
    UnknownError("-999", "error.999", "未知错误")
    ;
    
    private final String errorCode;
    private final String errorMsg;
    private final String errorKey;
    
    private MessageErrorEnum(String errorCode,String errorkey,String errorMsg){
        this.errorCode=errorCode;
        this.errorKey=errorkey;
        this.errorMsg=errorMsg;
    }
    
    public MessageException getMyException(){
        return getMessageException(getLocalErrorMsg());
    }
    
    public MessageException getMessageException(String msg){
        return new MessageException(errorCode,errorKey, msg);
    }
    
    public static MessageErrorEnum findByCode(String code){
        for (MessageErrorEnum value : MessageErrorEnum.values()) {
            if(value.errorCode.equalsIgnoreCase(code)){
                return value;
            }
        }
        return null;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getLocalErrorMsg(){
        return errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public final String getErrorKey() {
        return errorKey;
    }
}
