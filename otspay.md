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



### 支付接口
***
**业务说明：**

在ota系统中为一个已经创建的订单提交支付

**接口url：**
>http://ip:port/ots/pay/create

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
		weinxinpay:[{//微信支付信息
			appid:,//以下所有返回内容意义产看微信支付文档
			appkey:,
			noncestr:,
			packagevalue:,//原demo中的package字段
			partnerid:,
			prepayid:,
			timestamp:,
			sign:
		}],
		alipay:[{ //支付宝支付信息
			alipayselleremail:,// 商家支付宝email
			alipaypartner:,// 支付宝合作者id
			alipaykey:,// 支付宝商户私钥
			alipaynotifyurl:// 支付宝异步调用url
		}] 
}

```


### 检验支付通知
***
**业务说明：**



**接口url：**
>http://ip:port/ots/pay/check

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿orderid|订单号|是|
|onlinepaytype|预付支付类型|是|1微信；2支付宝；3网银；4其他
|payno|第三方交易号|否|若预付类型是微信，则必填
|paytime|支付时间|是|
|price|支付的价格|是|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|

> API返回json数据示例：

```js
{
	success:true,
}

```

###微信支付成功后回调
***
**业务说明：**

微信公众号在接收到第三方回调后，调用pay/weixin接口，如不成功，则反复调用。

**接口url：**
>http://ip:port/ots/pay/weixin

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿orderid|订单号|是|
|onlinepaytype|预付支付类型|是|1微信；2支付宝；3网银；4其他
|payno|第三方交易号|否|若预付类型是微信，则必填
|paytime|支付时间|是|
|price|支付的价格|是|
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













