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



## 评分

### `「修」`  获取评分项信息
**业务说明：**
> 查询系统的已有的酒店评分信息和评价标签

**接口 url：**
> http://ip:port/ots/score/subject/querylist

**请求参数：**

|    字段        |         名称         | 是否必须 | 说明|
--------------- | ------------------- | -------| -----------                                                
| subjectid     	| 评分项id   | 否    |         |                                           
| callmethod   	| 调用来源   | 是   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) |
| callversion  	| 调用版本   | 是   |
| ip           	| IP地址  | 是     |                                                    
| hardwarecode 	| 硬件编码   | 是       |                                                    
| otsversion   	| OTS版本    | 否       |   


> API返回json返回说明：

```js
{
	"success":true,
	"errcode":,//错误码
	"errmsg":,//错误信息
	"subjects": [{//评分项
		"subjectid":,//评分项id
		"subjectname"://评分项名称
		"minno":,//最小分值
		"maxno":,//最大分值
		"dno":,//允许最小值差（每个分值之间的差值，例如：最低分是1分，最高分是5分，值差是1分，则分数分别是1,2,3,4,5分共5个分级）
		"wfun"://权重系数
	}],
*『增』 "hotelmark":[{
 		"id":1,  //mark主键
 		"mark":"非常好" //mark变签内容
}]

}
```




> API返回json数据示例：

```js
{
    "subjects": [
        {
            "wfun": 4,
            "maxno": 5,
            "minno": 1,
            "dno": 1,
            "mno": 1,
            "subjectname": "卫生",
            "subjectid": 23981766981189704
        },
        {
            "wfun": 3,
            "maxno": 5,
            "minno": 1,
            "dno": 1,
            "mno": 1,
            "subjectname": "交通",
            "subjectid": 23981766981189704
        },
        {
            "wfun": 3,
            "maxno": 5,
            "minno": 1,
            "dno": 1,
            "mno": 1,
            "subjectname": "性价比",
            "subjectid": 23981766981189704
        }
    ],
    "hotelmark":[{
 		"id":1,
 		"mark":"非常好"
	}],
    "success": true
}
```


### `「修」` 酒店评价信息维护
**业务说明：**
> 查询系统的已有的酒店评分信息和评价标签

**接口 url：**
> http://ip:port/ots/score/modify

**请求参数：**

|    字段        |         名称         | 是否必须 | 说明|
--------------- | ------------------- | -------| -----------                                                
| orderid     	| 订单id   | 是   |         |   
| token     	| 授权token   | 是   |         |   
| actiontype     	| 操作类型   | 是   |  i添加<br>m修改<br>d删除      |   
| score     	| 评价内容  | 否   |         |   
| phone     	| 评价人手机号  | 否   |         |          
| isdefault     	| 默认评价  | 否   |   判断是否系统默认评价(T/F)  |   
| *『增』 markids     	|评论标签id  | 否   |   多个标签用逗号”,”隔开| 
|集合参数(scorepics):                                        
| scorepictitle     	| 图片标题  | 否   |         |                                           
| scorepicurl     	| 评价图片url  | 否   |         |                                           
|集合参数(grades)：
| subjectid     	| 评分项id | 否   |  集合内必填       |                                           
| grade     	| 分数 | 否   |  集合内必填       |                                    
| callmethod   	| 调用来源   | 是   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) |
| callversion  	| 调用版本   | 是   |
| ip           	| IP地址  | 是     |                                                    
| hardwarecode 	| 硬件编码   | 是       |                                                    
| otsversion   	| OTS版本    | 否       |   


> API返回json返回说明：

```js
{
	"success":true,
	"errcode":,//错误码
	"errmsg"
```

> API返回json返回示例：

```js
{
	"success":true,
	"errcode":0,
	"errmsg":""
```

##『修』查询酒店评价信息
***
**业务说明：**
> 2分归入差评，>=2且<3分归入一般，>3分归入好评.

**接口url:**
>http://ip:port/ots/score/querylist

**请求参数：**

|    字段        |         名称         | 是否必须 | 说明|
--------------- | ------------------- | -------| -----------
|﻿hotelid|酒店id|是|
|roomtypeid|房型id|否|若不为空，则返回数据仅包含该房型的对应信息
|roomid|房间id|否|若不为空，则返回数据仅包含该房间的对应信息
|isdetail|是否返回评价明细|否|(T/F)，若为空或F，则不返回评价明细，若为T则返回评价明细
|maxgrade|最高分值|否|(0-5)取小于等于该分值的评价信息
|mingrade|最低分值|否|(0-5)取大于等于该分值的评价信息
|gradetype|评价分类|否|好：G，中：M,  差：B， 全部：A
|subjectid|评价类型|否|结合t_subject表的评分项目类型
|orderby|排序项|否|
|startdateday|查询开始日期|否|yyyyMMdd
|enddateday|查询结束日期|否|yyyyMMdd
|starttime|开始时间|否|开始时间，若不为空则明细评价取该时间以后的评价信息；格式yyyyMMddhhmmss
|endtime|结束时间|否|结束时间，若不为空，则明细评价取该时间以前的评价信息；格式yyyyMMddhhmmss
|page|第几页|否|若isdetail是T，则此项必填
|limit|每页多少条|否|若isdetail是T，则此项必填
|callmethod|调用来源|否|1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否


> API返回json数据示例：

```js 
{
     "success":true,
      "errcode":,//错误码
      "errmsg":,//错误信息
      "hotel":[{//酒店信息
				"hotelid":,//酒店id
				"hotelgrade":,//酒店总分
				"scorecount":,//评价次数
		*『增』	"hotelmark":[{//酒店评价标签
						"id":1 , //mark主键
						"mark":非常好 ,//mark变签内容
				}],
				"hotelscoresubject":[{//酒店单项总分
							"subjectid":,//评价项目
							"subjectname":,//评价名称
							"grade":4.5//分值，浮点型，1位小数（4舍5入）
				}]
				"roomtype":[{//房型评价信息
							"hotelid":,//酒店id
							"roomtypeid":,//房型id
							"roomtypegrade":,//房型总分
							"scorecount":,//评价次数
							"roomtypescoresubject":[{//房型单项分数
									"subjectid":,//评价项目
									"subjectname":,//评价名称
									"grade"://单项总分
							}],
				}],
				"room":[{//房间评价信息
							"roomtypeid"://房型id
							"roomid":,//房间id
							"roomgrade":,//房间总分
							"roomscoresubject":[{//房型单项分数
								"subjectid":,//评价项目
								"subjectname":,//评价名称
								"grade"://单项总分
							}]
				}],
				"scoremxcount":,//评价明细条数
				"scoremx":[{ //评价明细
								"orderid":,//订单号
								"phone":,//评价人手机号
								"roomtypeid"://房型id
								"roomid":,//房间id
								"score":,//评价内容
								"allgrade":,//综合评分
								"createtime":,//评价时间
								"hotelreply":,//客服回复
								"hotelreplytime":,//客服回复时间
								"hotelgeireply":,//酒店回复
								"hotelgeireplytime":,//客服回复时间
								"scorepic":[{//评价图片信息
									"url"://图片地址
								}],
				"roomscoresubject":[{//评价项目
						"subjectid":,//评价项目
						"subjectname":,//评价名称
						"grade"://分值
				}]
		}]
	}]
}
```



</article>

<link href="asset/css/zTreeStyle.css" media="all" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="asset/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="asset/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="asset/js/ztree_toc.js"></script>
<script type="text/javascript" src="asset/js/mdtree.js"></script>













