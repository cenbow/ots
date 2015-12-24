<div style='width:300px; magin-top:20px'>
	<ul id="tree" class="ztree" style='width:100%;float:right'>
	</ul>
</div>
<article class='md-body' style="margin-left: 15%;">
# OTS Api 文档
## 版本

版本 |  修改日期  | 修改人 | 备注
--- | --------- | ----- | -----------
1.0 | 2015-4-18 | 谷佳良 | 创建
1.1 | 2015-04-21 | 谷佳良 |1.增加系统接口 <br> 2.修改1.0各接口 <br>3.合并支付接口 <br> 4. 合并支付确认接口


## 通用定义
### 时间定义
* 除特殊说明之外，所有时间通信使用北京时间通信。格式为 `14` 位 `yyyyMMddhhmmss`  ,例如`20141225140000` ，如果业务无需精确到秒，根据业务需要后面填充最小/最大时间。
* 注: 客户端需要自行处理时区问题，与服务器的通信均认为是北京时间。

### 提交方式
> 所有提交一律使用post提交

### session管理
客户端自身需要维护 session 管理，即使用同一个 session 的客户端进行 http 链接。

### 错误信息
除下载文件外，所有返回信息均为 `json`，必定包含 `success` 属性，有错误时必定包含 `errorcode`属性

```js
{
"success":false,
"errcode":'-1',
"errmsg":'错误信息'
}
```
errorcode 表示:
	
	1. `-701`  session 超时或者不存在该 session
	2. `-702`  该 session 因为密码被修改而失效
	3. `-703`  该 session 因用户被禁用而失效

### 下载文件错误信息
下载文件时，所有错误信息使用 http 错误状态码提示:

* `404` 表示未找到文件
* `403` 表示无权限下载 

### 请求报文Url及参数格式
> http://ip:port/ots/业务类别/业务场景（动词:查询-query或querylist；创建-create）


```
所有 url 全部使用小写英文字母
所有参数全部小写英文字母
```
事例：

```
http://ip:port/ots/hotel/querylist
http://ip:port/ots/score/modify
http://ip:port/ots/order/create
http://ip:port/ots/order/cancel
```

### 报文中判断 true 和 false 的判断
> 报文中判断是否的字段，均使用（T/F），T 代表 true，F 代表 false

##订单接口

### `「修」` 查询订单详情
***
**业务说明：**

查询订单信息，若订单有删除标记，则不返回该笔订单信息
眯客3.0增加 订单根据业务要求，显示需要给前端显示的状态（3.0暂时不做）


**接口url：**
> http://ip:port/ots/order/querylist

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿token|授权token|是|
|orderid|订单id|否|
|startdateday|查询开始日期|否|yyyyMMdd
|enddateday|查询结束日期|否|yyyyMMdd
|begintime|查询时间段的开始时|否|yyyyMMddhhmmss
|endtime|查询时间段的结束时|否|yyyyMMddhhmmss
|ordertype|订单状态|否|多状态用”，”隔开
|isscore|是否评价|否|T/F
|page|页数|是|第一页为1
|limit|每页显示记录条数|是|必须为正数
|hotelid|酒店id|否|只查询该酒店订单
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|
|statetype|取值范围|否|[all、doing、done]                                          

> API返回json数据示例：

```js
{
	success:true,
	errcode:,//错误码
	errmsg:,//错误信息
	count:10, //总数
	order:[{//所有订单
		orderid:1,//订单id
		hotelid:1,//酒店id
		hotelname:,//酒店名称
		hotelphone:,//联系电话
		hoteladdress:,//酒店地址
		hoteldis:,//酒店所属区县
		hotelcity:,//酒店所属城市
		longitude:, //酒店坐标(经度)
		latitude:,//酒店坐标(纬度)
		retentiontime:, //最晚保留时间 6位字符串 hhmmss,
		defaultleavetime:, //默认离店时间  6位字符串 hhmmss,
		ordermethod:1, //预订方式 1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
		ordertype:1, //预订方式  1、预付 2、普通        
		pricetype:1, //价格类型 1、时租  2、日租
		begintime:‘20141225171400’, //预抵时间
		endtime:‘20141225171400’,  //预离时间
		createtime:‘20141225171400’, //订单创建时间
		timeouttime:,//订单失效时间（创建订单时，默认是预付，订单失效时间为订单创建后15分钟）
		promotion:F, //是否促销(T/F)
		coupon:F, //是否使用了优惠券(T/F)
		isonpromo:// 是否特价, 0非，1特价
		promotype:	// 是否特价, 0非，1特价
		roomticket：房券代码
		checkcnt:,//可使用优惠券张数
*「增」 paytip:支付提示(当有值的时候需要在支付的时候进行提示，没有或者值为""则不提示)
		tickets:[{//优惠券信息，按照线上优惠额倒序排序，金额最大的为默认选中
			id:1,//优惠券唯一id
			name:’优惠券名’,
			select:T, //选中,
			check:T, //可以使用
			subprice:12, //线上优惠多少钱
			offlinesubprice:12, //线下优惠多少钱
			type:1, //优惠券类型 1、普通立减 2、切客优惠码 3、议价优惠码
			isticket:T,//  是否是优惠券（T优惠券，F优惠码）
			uselimit://使用限制(1—线上；2—线下；空—全部)
		}],
		totalprice:150.00, //总房价
		price:50,  //房价
		breakfastnum:1,   //早餐数
		contacts:’联系人姓名’,
		contactsphone:’联系人电话’,
		contactsemail:’联系人email’,
		contactsweixin:’联系人微信’,
		note:’备注’,
		orderstatus:100,  //订单状态
		pay:F,  //是否需要支付(T/F)
		payid:1, //需要支付的订单id
		orderretentiontime:’’  订单保留时间  14位
		onlinepay:100.00, //线上客单价
		offlinepay:100.00// 线下客单价
		receipt:F,   //是否需要发票(T/F)
		spreaduser:1,  //切客用户id
		roomorder:[{//订单下客单
			orderroomid:1,  //客单id
			hotelid:1   //酒店id
			hotelname:’酒店名称’,
			roomtypeid:1, //房型id
			roomtypename:’房型名称’,
			roomid:1,
			roomno:’房间号’,
			ordermethod:1,  //预订方式 1、微信、web3、app
			ordertype:1,  //预订方式  1、预付 2、普通
			pricetype:1,  //价格类型 1、时租  2、日租
			begintime:‘20141225171400’,  //预抵时间
			endtime:‘20141225171400’,  //预离时间
			orderday:1, //1天
			createtime:‘20141225171400’, //订单创建时间
			promotion:F, //是否促销(T/F)
			coupon:F, //是否使用了优惠券(T/F)
			totalprice:150.00, //总房价
		payprice:[{房费应支付金额
			actiondate:,// 日期
			price:,//房价金额
		}] 	
		breakfastnum:1,   //早餐数
		contacts:’联系人姓名’,
		contactsphone:’联系人电话’,
		contactsemail:’联系人email’,
		contactsweixin:’联系人微信’,
		checkinuser:[{//入住人信息
			cpname:,//入住人姓名
			cpsex:,//入住人性别
			birthday:,//生日
			cardtype:,//证件类型
			cardid:,//证件号
			ethnic:,//民族
			fromaddress:,//户籍地址
			address://联系地址
			phone:‘’手机号
		}]
		note:’备注’,
		orderstatus:100,  //订单状态
		pay:F,  //是否需要支付(T/F)
		receipt:F,   //是否需要发票(T/F)
		promotionno:’促销代码’,
		reeceipttitle:’发票抬头’,续住时需要第三方支付金额
		onlinepay:100.00, //线上客单价
		offlinepay:100.00// 线下客单价
		showtitle:’已取消’, //显示状态

		citycode：城市编码
		walletcost ： //钱包金额
		cashbackcost ： //返还金额
		orderpaydetail: [
			{name:房款 ，cost ： 100},
			{name:优惠券 ，cost ： -10},
			{name:乐住币 ，cost ：-20},
			{name:房券 ，cost ：-20}
		],//订单明细费用
		orderstatusname：订单状态的汉字描述
		orderstatusid：订单显示状态id
		receivecashback: 1, //（0:无需领取；1:还未领取;  2:已经领取) 是否返现

确认状态显示规则和前端按钮id
		button:[{
			name:’’, //按钮名称
			action:’’, //动作意义    cancel 取消、pay 付款、edit 修改、checkin快速入住、meet约会、refund退款、			evaluation评价、continuedtolive 续住、delete删除
		}]
}]
}


```

###「修」  创建订单
***
**业务说明：**


**接口url：**
> http://ip:port/ots/order/create

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿token|授权token|是|
|activeid|活动ID|否|
|hotelid|所选酒店id|是|
|roomtypeid|所选房型id|是|
|pricetype|日租/时租 |是|1时租；2日租
|startdateday|查询开始日期|是|yyyyMMdd
|enddateday|查询结束日期|是|yyyyMMdd
|begintime|开始时间|否|yyyyMMddhhmmss
|endtime|结束时间|否|yyyyMMddhhmmss
|userlongitude|用户坐标(经度)|否|用户的经度
|userlatitude|用户坐标(纬度)|否|用户的纬度
|roomid|所选房间id|是|
|ordertype|预付类型|是|1预付；2到店支付
|hideorder|是否无痕入住|否|T/F
|breakfastnum|早餐数|否|
|contacts|联系人|否|
|contactsphone|联系电话|否|
|contactsemail|联系邮箱|否|
|contactsweixin|联系微信|否|
|note|备注|否|
|spreaduser|切客用户id|否|
|ordermethod|订单方式|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|promotion|促销代码|否|
|couponno|优惠券代码|否|
|quickuserid|常住人主键id|否|可多个，多个使用过 英文逗号分隔
|checkinuser|入住人信息|否|除去常住人之外的入住人信息，格式为json格式[{name:’姓名’,sex:’性别’, ethnic,’民族’,birthday:’生日’,cardtype:’证件类型’,cardid:’证件号’,disid:’身份证地址区县id’,address:’身份证具体地址’,img:’’},{…}]其中性别、名族参照代码表，区县地址可从服务器端获取全国三级省市县id照片使用base64编码具体地址最多100汉字/字符，电话最多25字符，证件号50字符
|sysno|系统号|否|
|uuid|用户注册应用信息|否|
|deviceimei|手机唯一识别码imei|否|
|simsn|sim卡串号|否|
|wifimacaddr|wifi的mac地址|否|
|blmacaddr|蓝牙的mac地址|否|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|
|cashbackcost|返现金额              |否|                                                

> API返回json数据示例：

```js
{
		success:true,
		errcode:,//错误码
		errmsg:,//错误信息
		orderid:1,//订单id
		hotelid:1,//酒店id
		hotelname:,//酒店名称
		hotelphone:,//联系电话
		hoteladdress:,//酒店地址
		hoteldis:,//酒店所属区县
		hotelcity:,//酒店所属城市
		longitude:, //酒店坐标(经度)
		latitude:,//酒店坐标(纬度)
		retentiontime:, //最晚保留时间 6位字符串 hhmmss,
		defaultleavetime:, //默认离店时间  6位字符串 hhmmss,
		ordermethod:1, //预订方式 1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
		ordertype:1, //预订方式  1、预付 2、普通
		pricetype:1, //价格类型 1、时租  2、日租
		begintime:‘20141225180000’, //预抵时间
		endtime:‘20141226120000’,  //预离时间
		createtime:‘20141225171400’, //订单创建时间
		timeouttime:,//订单失效时间（创建订单时，默认是预付，订单失效时间为订单创建后15分钟）
		promotion:F, //是否促销(T/F)
		coupon:F, //是否使用了优惠券(T/F)
		isonpromo:// 是否特价, 0非，1特价
		promotype:	// 是否特价, 0非，1特价
		roomticket：房券代码
		checkcnt:,//可使用优惠券张数
		uselimit://选定的优惠券的使用限制(1—线上；2—线下；空—全部)
 *「增」paytip:支付提示(当有值的时候需要在支付的时候进行提示，没有或者值为""则不提示)
		tickets:[{//优惠券信息，按照线上优惠额倒序排序，金额最大的为默认选中
			id:1,//优惠券唯一id
			name:’优惠券名’,
			select:T, //选中,
			check:T, //可以使用
			subprice:12, //线上优惠多少钱
			offlinesubprice:12, //线下优惠多少钱
			type:1, //优惠券类型 1、普通立减 2、切客优惠码 3、议价优惠码
			isticket:T,//  是否是优惠券（T优惠券，F优惠码）
			uselimit://使用限制(1—线上；2—线下；空—全部)
		}],
		totalprice:150.00, //总房价
		price:50,  //房价
		breakfastnum:1,   //早餐数
		contacts:’联系人姓名’,
		contactsphone:’联系人电话’,
		contactsemail:’联系人email’,
		contactsweixin:’联系人微信’,
		note:’备注’,
		orderstatus:100,  //订单状态
		pay:F,  //是否需要支付(T/F)
		payid:1, //需要支付的订单id
		orderretentiontime:’’  订单保留时间  14位
		onlinepay:100.00, //线上客单价
		offlinepay:100.00// 线下客单价
		receipt:F,   //是否需要发票(T/F)
		spreaduser:1,  //切客用户id
		roomorder:[{//订单下客单
			orderroomid:1,  //客单id
			hotelid:1   //酒店id
			hotelname:’酒店名称’,
			roomtypeid:1, //房型id
			roomtypename:’房型名称’,
			roomid:1,
			roomno:’房间号’,
			ordermethod:1,  //预订方式 1、微信、web3、app
			ordertype:1,  //预订方式  1、预付 2、普通
			pricetype:1,  //价格类型 1、时租  2、日租
			begintime:‘20141225171400’,  //预抵时间
			endtime:‘20141225171400’,  //预离时间
			orderday:1, //1天
			createtime:‘20141225171400’, //订单创建时间
			promotion:F, //是否促销(T/F)
			coupon:F, //是否使用了优惠券(T/F)
			totalprice:150.00, //总房价
			payprice:[{房费应支付金额
				actiondate:,// 日期
				price:,//房价金额
			}] 	
			breakfastnum:1,   //早餐数
			contacts:’联系人姓名’,
			contactsphone:’联系人电话’,
			contactsemail:’联系人email’,
			contactsweixin:’联系人微信’,
			note:’备注’,
			orderstatus:100,  //订单状态
			pay:F,  //是否需要支付(T/F)
			receipt:F,   //是否需要发票(T/F)
			promotionno:’促销代码’,
			reeceipttitle:’发票抬头’,续住时需要第三方支付金额
			onlinepay:100.00, //线上客单价
			offlinepay:100.00// 线下客单价
		}]
		walletcost ： //钱包金额
		citycode：城市编码
		cashbackcost:  //返现金额
		orderpaydetail: [
			{name:房款 ，cost ： 100},
			{name:优惠券 ，cost ： -10},
			{name:乐住币 ，cost ：-20},
			{name:房券 ，cost ：-20}
		],
		orderstatusname：订单状态的汉字描述
		orderstatusid：订单显示状态id

		usermessage：提示信息
内容要求：
（1）您预订的酒店，在入住日期前一天18:00前可进行退款操作；预订今日酒店，付款完成后就不可以修改订单或退款咯
（2）凌晨23:56-2:00下单，可当天办理入住，提示“您最晚可在xxxx年xx月xx日12：00办理退房哦”；
（3）凌晨2:00后下单，必须在12：00后办理入住，提示“您在xxxx年xx月xx日12:00后可办理入住哦”；
}

```

### 「修」 修改订单
***
**业务说明：**


**接口url：**
> http://ip:port/ots/order/modify

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿token|授权token|是|
|orderid|订单ID|是|
|hotelid|所选酒店id|否|
|roomtypeid|所选房型id|否|
|pricetype|日租/时租 |否|1时租；2日租
|startdateday|查询开始日期|否|yyyyMMdd
|enddateday|查询结束日期|否|yyyyMMdd
|begintime|开始时间|否|yyyyMMddhhmmss
|endtime|结束时间|否|yyyyMMddhhmmss
|roomid|所选房间id|否|
|ordertype|预付类型|否|1预付；2到店支付
|hideorder|是否无痕入住|否|T/F
|breakfastnum|早餐数|否|
|contacts|联系人|否|
|contactsphone|联系电话|否|
|contactsemail|联系邮箱|否|
|contactsweixin|联系微信|否|
|note|备注|否|
|spreaduser|切客用户id|否|
|ordermethod|订单方式|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|promotion|促销代码|否|
|couponno|优惠券代码|否|
|quickuserid|常住人主键id|否|可多个，多个使用过 英文逗号分隔
|checkinuser|入住人信息|否|除去常住人之外的入住人信息，格式为json格式
|sysno|系统号|否|
|uuid|用户注册应用信息|否|
|deviceimei|手机唯一识别码imei|否|
|simsn|sim卡串号|否|
|wifimacaddr|wifi的mac地址|否|
|blmacaddr|蓝牙的mac地址|否|
|walletcost|钱包费用|否|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|
|isuselewallet|是否使用钱包|否|T/F
|roomticket|房卷代码|否|                                 

> API返回json数据示例：

```js
{
		success:true,
		errcode:,//错误码
		errmsg:,//错误信息
		orderid:1,//订单id
		hotelid:1,//酒店id
		hotelname:,//酒店名称
		hotelphone:,//联系电话
		hoteladdress:,//酒店地址
		hoteldis:,//酒店所属区县
		hotelcity:,//酒店所属城市
		longitude:, //酒店坐标(经度)
		latitude:,//酒店坐标(纬度)
		retentiontime:, //最晚保留时间 6位字符串 hhmmss,
		defaultleavetime:, //默认离店时间  6位字符串 hhmmss,
		ordermethod:1, //预订方式 1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
		ordertype:1, //预订方式  1、预付 2、普通		pricetype:1, //价格类型 1、时租  2、日租
		begintime:‘20141225171400’, //预抵时间
		endtime:‘20141225171400’,  //预离时间
		createtime:‘20141225171400’, //订单创建时间
		timeouttime:,//订单失效时间（创建订单时，默认是预付，订单失效时间为订单创建后15分钟）
		promotion:F, //是否促销(T/F)
		coupon:F, //是否使用了优惠券(T/F)
		isonpromo:// 是否特价, 0非，1特价
		promotype:	// 是否特价, 0非，1特价
		roomticket：房券代码
		checkcnt:,//可使用优惠券张数
		uselimit:// 选定的优惠券的使用限制(1—线上；2—线下；空—全部)
*「增」 paytip:支付提示(当有值的时候需要在支付的时候进行提示，没有或者值为""则不提示)
		tickets:[{//优惠券信息，按照线上优惠额倒序排序，金额最大的为默认选中
			id:1,//优惠券唯一id
			name:’优惠券名’,
			select:T, //选中,
			check:T, //可以使用
			subprice:12, //线上优惠多少钱
			offlinesubprice:12, //线下优惠多少钱
			type:1, //优惠券类型 1、普通立减 2、切客优惠码 3、议价优惠码
			isticket:T,//  是否是优惠券（T优惠券，F优惠码）
			uselimit://使用限制(1—线上；2—线下；空—全部)
		}],
		totalprice:150.00, //总房价
		price:50,  //房价
		breakfastnum:1,   //早餐数
		contacts:’联系人姓名’,
		contactsphone:’联系人电话’,
		contactsemail:’联系人email’,
		contactsweixin:’联系人微信’,
		note:’备注’,
		orderstatus:100,  //订单状态
		pay:F,  //是否需要支付(T/F)
		payid:1, //需要支付的订单id
		orderretentiontime:’’  订单保留时间  14位
		onlinepay:100.00, //线上客单价
		offlinepay:100.00// 线下客单价
		receipt:F,   //是否需要发票(T/F)
		spreaduser:1,  //切客用户id
		roomorder:[{//订单下客单
			orderroomid:1,  //客单id
			hotelid:1   //酒店id
			hotelname:’酒店名称’,
			roomtypeid:1, //房型id
			roomtypename:’房型名称’,
			roomid:1,
			roomno:’房间号’,
			ordermethod:1,  //预订方式 1、微信、web3、app
			ordertype:1,  //预订方式  1、预付 2、普通3、房券支付
			pricetype:1,  //价格类型 1、时租  2、日租
			begintime:‘20141225171400’,  //预抵时间
			endtime:‘20141225171400’,  //预离时间
			orderday:1, //1天
			createtime:‘20141225171400’, //订单创建时间
			promotion:F, //是否促销(T/F)
			coupon:F, //是否使用了优惠券(T/F)
			totalprice:150.00, //总房价
			payprice:[{房费应支付金额
				actiondate:,// 日期
				price:,//房价金额
			}] 	
			breakfastnum:1,   //早餐数
			contacts:’联系人姓名’,
			contactsphone:’联系人电话’,
			contactsemail:’联系人email’,
			contactsweixin:’联系人微信’,
			note:’备注’,
			orderstatus:100,  //订单状态
			pay:F,  //是否需要支付(T/F)
			receipt:F,   //是否需要发票(T/F)
			promotionno:’促销代码’,
			reeceipttitle:’发票抬头’,续住时需要第三方支付金额
			onlinepay:100.00, //线上客单价
			offlinepay:100.00// 线下客单价
		}]
		walletcost ： //钱包金额
		citycode：城市编码
		cashbackcost: ， //返现金额
		orderpaydetail: [
			{name:房款 ，cost ： 100},
			{name:优惠券 ，cost ： -10},
			{name:乐住币 ，cost ：-20},
			{name:房券 ，cost ：-20}
		],

		orderstatusname：订单状态的汉字描述
		orderstatusid：订单显示状态id
		usermessage：提示信息
内容要求：
（1）您预订的酒店，在入住日期前一天18:00前可进行退款操作；预订今日酒店，付款完成后就不可以修改订单或退款咯
（2）凌晨23:56-2:00下单，可当天办理入住，提示“您最晚可在xxxx年xx月xx日12：00办理退房哦”；
（3）凌晨2:00后下单，必须在12：00后办理入住，提示“您在xxxx年xx月xx日12:00后可办理入住哦”；
}


```

### 「修」 房型预订创建订单
***
**业务说明：**
根据酒店房型进行预定，系统自动根据房型下能够预定的房间自动分配一个房间进行展示。

**接口url：**
>http://ip:port/ots/order/createByRoomType

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿token|授权token|是|
|activeid|活动ID|否|
|hotelid|所选酒店id|是|
|roomtypeid|所选房型id|是|
|pricetype|日租/时租 |是|1时租；2日租
|startdateday|查询开始日期|是|yyyyMMdd
|enddateday|查询结束日期|是|yyyyMMdd
|begintime|开始时间|否|yyyyMMddhhmmss
|endtime|结束时间|否|yyyyMMddhhmmss
|userlongitude|用户坐标(经度)|否|用户的经度
|userlatitude|用户坐标(纬度)|否|用户的纬度
|ordertype|预付类型|是|1预付；2到店支付
|hideorder|是否无痕入住|否|T/F
|breakfastnum|早餐数|否|
|contacts|联系人|否|
|contactsphone|联系电话|否|
|contactsemail|联系邮箱|否|
|contactsweixin|联系微信|否|
|note|备注|否|
|spreaduser|切客用户id|否|
|ordermethod|订单方式|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|promotion|促销代码|否|
|couponno|优惠券代码|否|
|quickuserid|常住人主键id|否|可多个，多个使用过 英文逗号分隔
|checkinuser|入住人信息|否|除去常住人之外的入住人信息，格式为json格式
|sysno|系统号|否|
|uuid|用户注册应用信息|否|
|deviceimei|手机唯一识别码imei|否|
|simsn|sim卡串号|否|
|wifimacaddr|wifi的mac地址|否|
|blmacaddr|蓝牙的mac地址|否|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|
|cashbackcost|返现金额|否|                           

> API返回json数据示例：

```js
{
		success:true,
		errcode:,//错误码
		errmsg:,//错误信息
		orderid:1,//订单id
		hotelid:1,//酒店id
		hotelname:,//酒店名称
		hotelphone:,//联系电话
		hoteladdress:,//酒店地址
		hoteldis:,//酒店所属区县
		hotelcity:,//酒店所属城市
		longitude:, //酒店坐标(经度)
		latitude:,//酒店坐标(纬度)
		retentiontime:, //最晚保留时间 6位字符串 hhmmss,
		defaultleavetime:, //默认离店时间  6位字符串 hhmmss,
		ordermethod:1, //预订方式 1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
		ordertype:1, //预订方式  1、预付 2、普通
		pricetype:1, //价格类型 1、时租  2、日租
		begintime:‘20141225171400’, //预抵时间
		endtime:‘20141225171400’,  //预离时间
		createtime:‘20141225171400’, //订单创建时间
		timeouttime:,//订单失效时间（创建订单时，默认是预付，订单失效时间为订单创建后15分钟）
		promotion:F, //是否促销(T/F)
		coupon:F, //是否使用了优惠券(T/F)
		isonpromo:// 是否特价, 0非，1特价
		promotype:	// 是否特价, 0非，1特价
		roomticket：房券代码
		checkcnt:,//可使用优惠券张数
*「增」 paytip,:支付提示(当有值的时候需要在支付的时候进行提示，没有或者值为""则不提示)
		tickets:[{//优惠券信息，按照线上优惠额倒序排序，金额最大的为默认选中
			id:1,//优惠券唯一id
			name:’优惠券名’,
			select:T, //选中,
			check:T, //可以使用
			subprice:12, //线上优惠多少钱
			offlinesubprice:12, //线下优惠多少钱
			type:1, //优惠券类型 1、普通立减 2、切客优惠码 3、议价优惠码
			isticket:T,//  是否是优惠券（T优惠券，F优惠码）
			uselimit://使用限制(1—线上；2—线下；空—全部)
		}],
		totalprice:150.00, //总房价
		price:50,  //房价
		breakfastnum:1,   //早餐数
		contacts:’联系人姓名’,
		contactsphone:’联系人电话’,
		contactsemail:’联系人email’,
		contactsweixin:’联系人微信’,
		note:’备注’,
		orderstatus:100,  //订单状态
		pay:F,  //是否需要支付(T/F)
		payid:1, //需要支付的订单id
		orderretentiontime:’’  订单保留时间  14位
		onlinepay:100.00, //线上客单价
		offlinepay:100.00// 线下客单价
		receipt:F,   //是否需要发票(T/F)
		spreaduser:1,  //切客用户id
		roomorder:[{//订单下客单
			orderroomid:1,  //客单id
			hotelid:1   //酒店id
			hotelname:’酒店名称’,
			roomtypeid:1, //房型id
			roomtypename:’房型名称’,
			roomid:1,
			roomno:’房间号’,
			ordermethod:1,  //预订方式 1、微信、web3、app
			ordertype:1,  //预订方式  1、预付 2、普通
			pricetype:1,  //价格类型 1、时租  2、日租
			begintime:‘20141225171400’,  //预抵时间
			endtime:‘20141225171400’,  //预离时间
			orderday:1, //1天
			createtime:‘20141225171400’, //订单创建时间
			promotion:F, //是否促销(T/F)
			coupon:F, //是否使用了优惠券(T/F)
			hasroomticket：F, //是否可以使用房券（T/F）
			roomticket：房券代码
			totalprice:150.00, //总房价
			payprice:[{房费应支付金额
				actiondate:,// 日期
				price:,//房价金额
			}] 	
			breakfastnum:1,   //早餐数
			contacts:’联系人姓名’,
			contactsphone:’联系人电话’,
			contactsemail:’联系人email’,
			contactsweixin:’联系人微信’,
			note:’备注’,
			orderstatus:100,  //订单状态
			pay:F,  //是否需要支付(T/F)
			receipt:F,   //是否需要发票(T/F)
			promotionno:’促销代码’,
			reeceipttitle:’发票抬头’,续住时需要第三方支付金额
			onlinepay:100.00, //线上客单价
			offlinepay:100.00// 线下客单价
		}]
		cashbackcost:，  //返现金额
		orderpaydetail: [
			{name:房款 ，cost ： 100},
			{name:优惠券 ，cost ： -10},
			{name:乐住币 ，cost ：-20},
			{name:房券 ，cost ：-20}
		],

		orderstatusname：订单状态的汉字描述
		usermessage：提示信息
内容要求：
（1）您预订的酒店，在入住日期前一天18:00前可进行退款操作；预订今日酒店，付款完成后就不可以修改订单或退款咯
（2）凌晨23:56-2:00下单，可当天办理入住，提示“您最晚可在xxxx年xx月xx日12：00办理退房哦”；
（3）凌晨2:00后下单，必须在12：00后办理入住，提示“您在xxxx年xx月xx日12:00后可办理入住哦”；
}

```

### 取消订单
***
**业务说明：**


**接口url：**
>http://ip:port/ots/order/cancel

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿token|授权token|是|
|orderid|订单ID|是|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|
|type|取消类型|否|1 用户取消 2 回退取消.为了版本兼容，该参数不传，默认值为1
                        

> API返回json数据示例：

```js
{
	success:true,
	errcode:,//错误码
	errmsg:,//错误信息
	cancelpay://待退款金额
}
```

### 删除订单
***
**业务说明：**

该接口用于将订单隐藏，不在C端订单列表中显示,OTS订单表需要增加一个标记位，用于判断是否返回订单给C端查询接口

**接口url：**
>http://ip:port/ots/order/disable

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿token|授权token|是|
|orderid|订单ID|是|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|

> API返回json数据示例：

```js
{
	success:true,
	errcode:,//错误码
	errmsg:,//错误信息
}

```
### 订单数量统计
***
**业务说明：**

该接口用于统计某些订单状态对应的订单数量

**接口url：**
>http://ip:port/ots/order/selectcount

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿orderid|订单id|是|必填
|promotionno|促销代码|否|若多个，则用“，”分割
|couponno|优惠券代码|否|若多个，则用“，”分割
|paytype|支付类型|是|1预付；2到付
|onlinepaytype|预付支付类型|否（若预付，则必填）|1微信；2支付宝3网银4其他；5房卷支付
|房卷号|否|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|

> API返回json数据示例：

```js
{
	success:true,
	errcode:,//错误码
	errmsg:,//错误信息
	statuscount:[{
		sqnum:,//顺序号
		ordercount:,//订单数量
	}]
}


```


###入住人查询接口
***
**业务说明：**

查询用户历史入住人信息

**接口url：**
>http://ip:port/ots/order/selectcheckinuser

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿token|授权token|是|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|

> API返回json数据示例：

```js
{
	success:true,
	errcode:,//错误码
	errmsg:,//错误信息
	datas: [{
		username: ‘’//入住人姓名
		phone ： ‘’ //入住人手机号
	}]
}


```

###修改入住人接口
***
**业务说明：**

当订单预定后，用户需要修改入住人信息时，请调用该接口进行处理。
注意：如果订单已经入住，则用户修改入住人信息时，系统报“订单入住后，不能进行入住人信息修改”


**接口url：**
>http://ip:port/ots/order/modifycheckinuser

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿token|授权token|是|
|orderid|订单id|是|
|checkinuser|入住人信息|是|除去常住人之外的入住人信息，格式为json格式
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|

> API返回json数据示例：

```js
{
	success:true,
	errcode:,//错误码
	errmsg:,//错误信息
}

```



</article>

<link href="asset/css/zTreeStyle.css" media="all" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="asset/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="asset/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="asset/js/ztree_toc.js"></script>
<script type="text/javascript" src="asset/js/mdtree.js"></script>













