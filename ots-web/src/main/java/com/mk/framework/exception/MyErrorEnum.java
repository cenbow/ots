package com.mk.framework.exception;


/**
 * @author shellingford
 * @version 创建时间：2012-2-2 下午01:22:47
 * 
 */
public enum MyErrorEnum {
	errorParm("-1","参数错误。"),
	errorLogin("-2","用户名或者密码错误。"),
	needLogin("-3","请登录后访问。"),
	memberExist("-4","会员已经存在。"),
	memberNotExist("-5","会员不存在。"),
	systemBusy("-6","系统忙碌，稍后再试。"),
	savePicError("-9","图片存储错误。"),
	findHotelinfo("-40","酒店信息错误。"),
	notfindHotel("-41","酒店不存在。"),
	notfindUser("-42","会员不存在。"), 
	PhoneNumFormatError("-43","手机号码格式错误。"), 
	PasswordFormatError("-44","密码格式错误。"),
	PhoneVerifyCodeError("-45","验证码错误。"),
	PhoneVerifyError("-46","手机验证错误。"), 
	SessionTimeout("-47","会话超时."),
	VerifyQuestionError("-48","验证问题错误."),
	PhoneNotVerify("-49","该手机号与验证号不一致."),
	PasswordError("-50","密码错误."),
	HotCityConfigError("-51","热门城市配置错误."),
	MutilToneCityError("-52","多音字城市读取错误."),
	appversionError("-53","版本号错误."),
	tokenError("-54","自动登录令牌错误."),
	OldPasswordError("-55","旧密码错误."),
	appversionNotExist("-56","版本号不存在."),
	prePayIDNULL("-57","微信预付ID为空."),
	openidByPhone("-58","已被其他手机号绑定."), 
	phoneByOpenid("-59","手机号已被其他openid绑定."), 
	orderCanceled("-60","该订单已取消,无法支付."), 
	exclusiveRequest("-61","该订单正在进行其他操作,请稍候重试."),
	orderConfirm("-62","该订单已确认过,不能重复创建支付."),
	
	saveOrder("-100","保存订单失败."),
	saveOrderPms("-101","保存订单失败:PMS错误."),
	saveOrderCost("-102","保存订单失败:价格计算错误."),
	saveOrderRoomConflict("-103","保存订单失败:住房时间冲突."),
	saveOrderInTimeByTime("-104","入住时间必须大于现在时间!"),
//	saveOrderInTimeByYF("-105","(未预付)入住时间不能大于酒店房间保留时间!"),
	saveOrderInTimeByYF("-105","(未预付)18点至次日6点不能选择到付!"),
	Pms("-106","PMS错误."),
	delOrderError("-121","取消订单错误."),
	delOrderErrorByOrderIn("-122","只能取消未入住的订单."),
	delOrderErrorByRoomOrderIn("-123","只能取消未入住的客单."),
	cancelTime("-124","预付后12小时内无法取消订单,如有特殊情况请联系客服."),
	updateOrder("-131","修改订单失败."),
	updateOrderbyKeyInfo("-132","修改关键信息失败.关键信息:入住、离店时间、房间类型、房间号、以及优惠券使用。"),
	updateOrderbyPay("-133","支付状态下不能修改订单:入住、离店时间、房间类型、房间号、以及优惠券使用。"),
	updateOrderbyBeforePay("-133","支付后不能修改订单:入住、离店时间、房间类型、房间号、以及优惠券使用。"),
	updateOrderbyIn("-135","入住后都不可以修改订单."),
	findOrder("-170","订单不存在."),
	delOrder("-180","删除订单失败."),
	notExistOrderType("-181","不存在此订单类型."),
	orderStsNotWait("-190","订单非前台等待状态."),
	roomNUll("-199","酒店房间已满,客官请下次再来."),
    walletError("-140","钱包错误"),

	
	noJifenError("-201","没有足够的积分."),
	noChuZhiError("-202","没有足够的储值."),
	noXiaoZhenTouError("-203","没有足够的小枕头."),
	exisitOrderPay("-204","该订单已经存在相应支付单."),
	payToPmsError("-205","通知pms支付失败."),
	checkPmsOnlineError("-206","检测pms是否在线出错."),
	pmsOffline("-207","pms离线."),
	cancelPayToPmsError("-208","取消pms支付失败."),
	changePmsPriceError("-209","通知pms改价失败."),
	payPrice0("-210","用优惠券后支付金额为0的处理失败."),
	kefuRefundOrderError("-211","客服byError退款失败."), 
	spreadUserNofind("-301","二维码信息错误."),
	
	sessionError("-701","session失效."),
	sessionErrorByPassword("-702","session因为密码被修改而失效."),
	sessionErroByStatus("-703","session因账户被禁用而失效."), 
	accesstokenTimeOut("-704","accesstoken过期."),
	nullError("-705","系统正忙,请稍后再试."),
	serviceRefundError("-706","退款失败."),
	
	orderIdCastError("-801","支付宝返回订单号有误."),
	totalFeeCastError("-802","支付宝返回订单金额有误."),
	cronExpressionError("-803","日期cron表达式有误."),
	xmlParseError("-804","优惠券信息解析错误."),
	requestAliError("-805","请求支付宝失败."),
	requestWXError("-806","请求微信失败."),
	requestWXNoPay("-807","无支付记录."),
	selselectCustomerpayError("-808","PMS2.0 客单支付情况查询失败."),
	cancelpaybyerrorError("-809","异常情况下支付取消失败."),
	OrderCancelBySystem("-810","订单在支付过程被系统取消."),
	alreadyPay("-811","订单已经支付成功."),
	modifypaystatusbyerrorError("-812","修改订单支付状态失败."),
	
	ticketTimeUseless("-901","优惠券未在指定的有效时间内使用."),
	ticketHotelUseless("-902","优惠券未在指定酒店内使用."),
	ticketRoomTypeUseless("-903","优惠券未在指定的房型内使用."),
	ticketDisUseless("-904","优惠券未在指定的区县使用."),
	ticketCityUseless("-905","优惠券未在指定的城市使用."),
	ticketMinDayUseless("-906","订单的天数未达到优惠券指定天数."),
	ticketMinPayUseless("-907","订单金额未达到优惠券指定金额."),
	ticketUseless("-908","优惠券无法使用."),
	noUsableTicket("-909","无有效的优惠券."),
	promotaionUseless("-910","优惠码无法使用."),
	noUsablePromotaion("-911","无有效的优惠码."),
	yijiaOnlinePriceError("-912","无效的线上优惠券价格."),
	yijiaOfflinePriceError("-913","无效的线下优惠券价格."),
	notMemberTicket("-914","非本人优惠券."),
	useSameTicket("-915","订单重复使用优惠码."),
	UnknownError("-999", "未知错误."),
	
	
	//--------优惠券活动-------------
	notStartActive("-916", "活动未开始."),
	alreadyEndActive("-917", "活动已结束."),
	notAllowGet("-918", "此活动暂不可领取优惠券."),
	notAllowMultiGet("-919", "此活动优惠券不可重复领取."),
	alreadyEndGet("－920", "此活动优惠券已被领完."),
	IllegalActive("－921","无效活动."),
	
	
	//-----------------------------
	promoPswError("－1001","卷密码不正确."),
	promoNotActivte("－1002","卷没有被激活."),
	promoAreaError("－1003","该卷不能在此区域使用."),
	promoDayError("－1004","该卷不在有效期内."),
	promoTimeError("－1005","该卷使用时间不对."),
	promoTypeError("－1006","特价房才能使用优惠劵."),
	promoCityCodeError("－1007","酒店信息不正确."),

	promotionError("-1008","很抱歉，今夜特价房不能与其他促销一起使用"),
	couponNoError("-1009","很抱歉，今夜特价房不能使用优惠券"),
	OrderTypeError("-1010","很抱歉，今夜特价房只能使用在线支付"),

	// -----------------------------
	customError("0000","");
	;
	
	private final String errorCode;
	private final String errorMsg;
	
	private MyErrorEnum(String errorCode,String errorMsg){
		this.errorCode=errorCode;
		this.errorMsg=errorMsg;
	}
	
	public MyException getMyException(){
		return getMyException(errorMsg);
	}
	
	public MyException getMyException(String msg){
		return new MyException(errorCode, "", msg);//  返回输入的错误信息
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	 
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public static MyErrorEnum findByCode(String code){
		for (MyErrorEnum value : MyErrorEnum.values()) {
			if(value.errorCode.equalsIgnoreCase(code)){
				return value;
			}
		}
		return null;
	}
}
