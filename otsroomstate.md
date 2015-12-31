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


## 房态
### `「修」` 酒店房态信息查询
***
**业务说明：**
>查询酒店房型房间信息 
眯客3.0增加控制，只查询可预定的房间。同时前端展示时默认只展示5个房间。

**接口url：**
> http://ip:port/ots/roomstate/querylist

**请求参数：**

|    字段        |         名称         | 是否必须 | 说明|
--------------- | ------------------- | -------| -----------
|hotelid 	| 酒店id 	| 是 	|  	
|roomtypeid 	| 房型id 	| 否 	|    | 
|startdateday 	| 查询开始日期 	| 否 | yyyyMMdd |
|enddateday 	| 查询结束日期 	| 否 | yyyyMMdd |
|startdate 	| 入住时间 	| 否 	| yyyyMMddhhmmss |
|enddate 	| 离店时间 	| 否 	| yyyyMMddhhmmss |
|roomstatus 	| 返回房态类型	| 否 	| 0或空-全部房间<br>，1-仅返回可售房,<br>2-仅返回不可售房 |
|bednum 	| 床数量	| 否 	|  （1-1,2-2,3-其他，空-全部）|
|orderby 	| 排序项目 	| 否 	| 暂时只有roomno| 
|roomno 	| 房间号	| 否 |  |	
|isShowAllRoom 	| 是否显示所有的房间 	| 否 | 默认为F. T：显示所有 F:不显示所有（默认只显示5间）|
|callmethod 	| 调用来源 	| 否 	| 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) |
|callversion 	| 调用版本 	| 否 |  |
|callentry 	| 调用入口 	| 否 	| 1-摇一摇 <br>2-房态搜索入口<br> 3-切客 |
|ip 	| IP地址 	| 否 	|  	|
|hardwarecode 	| 硬件编码 	| 否 | | 
|otsversion 	| OTS版本 	| 否 	|  |

>API返回 json 数据说明：

```js
{
	"success":true,
	"errcode":,//错误码
	"errmsg":,//错误信息
	"hotel":[{//酒店信息
		"hotelid":,//酒店id
		"hotelname":,//酒店名称
		"hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
		"online":,//会否在线(T/F)
*「增」"repairinfo":"2014 年装修",//装修信息
		"roomtype":[{//房型
		"roomtypeid":,//房型id		
		"bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
		"roomtypename":,//房型名称
		"roomtypeprice":,//房型价格
	  	"roomtypepmsprice":,//门市价格
		"ispromoting": //1在促销期间内/0
		"promoid": "1",// 特价活动id 1-今夜特价， 2-今日特价， 3-主题酒店， 6-一元秒杀
      	"promotype": "3",// 特价活动类型
      	"isonpromo": "1", //是否特价
     	"promostarttime": "20:00", //特价开始时间
      	"promotext": "该房间正在参与今夜特价活动，预订享受超低价。", //特价文案
      	"promodustartsec": "0", //距离特价开始的秒数
      	"promoduendsec": "19039",//距离特价结束的秒数
      	"promostatus": 1,//特价活动状态： -1-特价活动结束， 0-特价活动未开始， 1-特价活动中
      	"vcroomnum":,//可售房间数
		"vctxt":"", //可售房间数小于等于3,显示“仅剩x间”；等于0显示“满房,最近预定3小时前“；大于3间不显示
		"count":0, //房间内床数
		"beds":[{
			"bedtypename":,//床型(双人床，单人床)
			"bedlength": //尺寸(1.5米，1.8米)
		}],
		"area":,//面积
		"bedtypename":,//床型
		"bedlength":,//床宽
		"bathroomtype":,//卫浴类型
		"bathmode":,//洗浴方式
		"infrastructure": [{//基础设施
			"infrastructureid":,// 基础设施id
			"infrastructurename":,// 基础设施名称
		}]
		"valueaddedfa": [{//增值设施
			"valueaddedfaid":,// 增值设施id
			"valueaddedfaname":,// 增值设施名称
		}]
		"rooms":[{//房间信息
			"roomid":,//房间id
			"roomno":,//房间号
			"isselected":  //T/F
			"roomname":,//房间类型名称（取自roomtypename）
			"roomstatus"://房间状态（vc：可售；nvc：不可售）
			"haswindow":,//是否有窗（T/F）
			"floor":,//楼层
		}]
		"cashbackcost":  //返现金额
		"iscashback":  //是否返现（T/F）

}] 

}]
}
```

>API返回json数据示例：

```js
{
	"success":true,
	"errcode":0,
	"errmsg:"",
	"hotel": [
        {
            "hotelid": 7027,
            "hotelname": "重庆鑫贵宾馆",
            "hotelrulecode": 1002,
            "visible": "T",
            "online": "T",
            "repairinfo":"2014 年装修",
            "roomtype": [
                {
                    "roomtypeid": 32582,
                    "bednum": 1,
                    "roomtypename": "特价单间",
                    "roomtypeprice": 30,
                    "roomtypepmsprice": 88,
                    "vcroomnum": 3,
                    "bed": {
                        "count": 1,
                        "beds": [
                            {
                                "bedtypename": "单床房",
                                "bedlength": "1.50米"
                            }
                        ]
                    },
                    "area": 12,
                    "bedtypename": "单床房",
                    "bedlength": "1.50米",
                    "bathroomtype": "独立卫浴",
                    "infrastructure": [
                        {
                            "infrastructureid": 15,
                            "infrastructurename": "24小时热水"
                        },
                        {
                            "infrastructureid": 18,
                            "infrastructurename": "拖鞋"
                        },
                        {
                            "infrastructureid": 19,
                            "infrastructurename": "电吹风"
                        },
                        {
                            "infrastructureid": 20,
                            "infrastructurename": "免费洗漱用品"
                        },
                        {
                            "infrastructureid": 21,
                            "infrastructurename": "免费瓶装水"
                        },
                        {
                            "infrastructureid": 39,
                            "infrastructurename": "电视"
                        },
                        {
                            "infrastructureid": 38,
                            "infrastructurename": "免费茶包"
                        },
                        {
                            "infrastructureid": 37,
                            "infrastructurename": "电热水壶"
                        },
                        {
                            "infrastructureid": 36,
                            "infrastructurename": "浴巾"
                        },
                        {
                            "infrastructureid": 35,
                            "infrastructurename": "毛巾"
                        },
                        {
                            "infrastructureid": 34,
                            "infrastructurename": "免费wifi"
                        },
                        {
                            "infrastructureid": 33,
                            "infrastructurename": "有线宽带"
                        },
                        {
                            "infrastructureid": 31,
                            "infrastructurename": "空调"
                        }
                    ],
                    "valueaddedfa": [
                        {
                            "valueaddedfaid": 28,
                            "valueaddedfaname": "茶具"
                        },
                        {
                            "valueaddedfaid": 41,
                            "valueaddedfaname": "书桌"
                        },
                        {
                            "valueaddedfaid": 42,
                            "valueaddedfaname": "插线板"
                        },
                        {
                            "valueaddedfaid": 43,
                            "valueaddedfaname": "地板"
                        },
                        {
                            "valueaddedfaid": 53,
                            "valueaddedfaname": "地砖"
                        },
                        {
                            "valueaddedfaid": 54,
                            "valueaddedfaname": "地毯"
                        }
                    ],
                    "rooms": [
                        {
                            "roomid": 179870,
                            "roomno": "106",
                            "roomname": "特价单间",
                            "roomstatus": "vc",
                            "isselected": "T",
                            "haswindow": ""
                        },
                        {
                            "roomid": 179871,
                            "roomno": "112",
                            "roomname": "特价单间",
                            "roomstatus": "vc",
                            "isselected": "F",
                            "haswindow": ""
                        },
                        {
                            "roomid": 179869,
                            "roomno": "113",
                            "roomname": "特价单间",
                            "roomstatus": "vc",
                            "isselected": "F",
                            "haswindow": ""
                        }
                    ],
                    "isfocus": "F",
                    "iscashback": "F",
                    "cashbackcost": 0,
                    "vctxt": "仅剩3间",
                    "promoid": "1",
                    "promotype": "1",
                    "isonpromo": "1",
                    "promostarttime": "20:00",
                    "promotext": "该房间正在参与今夜特价活动，预订享受超低价。",
                    "promodustartsec": "0",
                    "promoduendsec": "19039",
                    "promostatus": 1,
                    "roomtypepic": [
                        {
                            "name": "def",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FrjHUa5KrsR5_LycE6tns6DF2Okk"
                                }
                            ]
                        },
                        {
                            "name": "bed",
                            "pic": []
                        },
                        {
                            "name": "toilet",
                            "pic": []
                        }
                    ]
                },
                {
                    "roomtypeid": 32584,
                    "bednum": 2,
                    "roomtypename": "标间",
                    "roomtypeprice": 60,
                    "roomtypepmsprice": 108,
                    "vcroomnum": 2,
                    "bed": {
                        "count": 2,
                        "beds": [
                            {
                                "bedtypename": "双床房",
                                "bedlength": "1.20米"
                            },
                            {
                                "bedtypename": "双床房",
                                "bedlength": "1.20米"
                            }
                        ]
                    },
                    "area": 13,
                    "bedtypename": "双床房",
                    "bedlength": "1.20,1.20米",
                    "bathroomtype": "独立卫浴",
                    "infrastructure": [
                        {
                            "infrastructureid": 15,
                            "infrastructurename": "24小时热水"
                        },
                        {
                            "infrastructureid": 18,
                            "infrastructurename": "拖鞋"
                        },
                        {
                            "infrastructureid": 19,
                            "infrastructurename": "电吹风"
                        },
                        {
                            "infrastructureid": 20,
                            "infrastructurename": "免费洗漱用品"
                        },
                        {
                            "infrastructureid": 21,
                            "infrastructurename": "免费瓶装水"
                        },
                        {
                            "infrastructureid": 39,
                            "infrastructurename": "电视"
                        },
                        {
                            "infrastructureid": 38,
                            "infrastructurename": "免费茶包"
                        },
                        {
                            "infrastructureid": 37,
                            "infrastructurename": "电热水壶"
                        },
                        {
                            "infrastructureid": 36,
                            "infrastructurename": "浴巾"
                        },
                        {
                            "infrastructureid": 35,
                            "infrastructurename": "毛巾"
                        },
                        {
                            "infrastructureid": 34,
                            "infrastructurename": "免费wifi"
                        },
                        {
                            "infrastructureid": 33,
                            "infrastructurename": "有线宽带"
                        },
                        {
                            "infrastructureid": 31,
                            "infrastructurename": "空调"
                        }
                    ],
                    "valueaddedfa": [
                        {
                            "valueaddedfaid": 28,
                            "valueaddedfaname": "茶具"
                        },
                        {
                            "valueaddedfaid": 41,
                            "valueaddedfaname": "书桌"
                        },
                        {
                            "valueaddedfaid": 42,
                            "valueaddedfaname": "插线板"
                        },
                        {
                            "valueaddedfaid": 43,
                            "valueaddedfaname": "地板"
                        },
                        {
                            "valueaddedfaid": 53,
                            "valueaddedfaname": "地砖"
                        },
                        {
                            "valueaddedfaid": 54,
                            "valueaddedfaname": "地毯"
                        }
                    ],
                    "rooms": [
                        {
                            "roomid": 179874,
                            "roomno": "108",
                            "roomname": "标间",
                            "roomstatus": "vc",
                            "isselected": "T",
                            "haswindow": ""
                        },
                        {
                            "roomid": 179876,
                            "roomno": "110",
                            "roomname": "标间",
                            "roomstatus": "vc",
                            "isselected": "F",
                            "haswindow": ""
                        }
                    ],
                    "isfocus": "F",
                    "iscashback": "F",
                    "cashbackcost": 0,
                    "vctxt": "仅剩2间",
                    "promoid": "1",
                    "promotype": "2",
                    "isonpromo": "1",
                    "promostarttime": "20:00",
                    "promotext": "该房间正在参与今夜特价活动，预订享受超低价。",
                    "promodustartsec": "0",
                    "promoduendsec": "19039",
                    "promostatus": 1,
                    "roomtypepic": [
                        {
                            "name": "def",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/Fm5kfFUFy_vr1kNFuC83MjjqAgn8"
                                }
                            ]
                        },
                        {
                            "name": "bed",
                            "pic": []
                        },
                        {
                            "name": "toilet",
                            "pic": []
                        }
                    ]
                },
                {
                    "roomtypeid": 32591,
                    "bednum": 1,
                    "roomtypename": "豪华单间",
                    "roomtypeprice": 90,
                    "roomtypepmsprice": 158,
                    "vcroomnum": 2,
                    "bed": {
                        "count": 1,
                        "beds": [
                            {
                                "bedtypename": "单床房",
                                "bedlength": "1.50米"
                            }
                        ]
                    },
                    "area": 13,
                    "bedtypename": "单床房",
                    "bedlength": "1.50米",
                    "bathroomtype": "独立卫浴",
                    "infrastructure": [
                        {
                            "infrastructureid": 15,
                            "infrastructurename": "24小时热水"
                        },
                        {
                            "infrastructureid": 18,
                            "infrastructurename": "拖鞋"
                        },
                        {
                            "infrastructureid": 19,
                            "infrastructurename": "电吹风"
                        },
                        {
                            "infrastructureid": 20,
                            "infrastructurename": "免费洗漱用品"
                        },
                        {
                            "infrastructureid": 21,
                            "infrastructurename": "免费瓶装水"
                        },
                        {
                            "infrastructureid": 39,
                            "infrastructurename": "电视"
                        },
                        {
                            "infrastructureid": 38,
                            "infrastructurename": "免费茶包"
                        },
                        {
                            "infrastructureid": 37,
                            "infrastructurename": "电热水壶"
                        },
                        {
                            "infrastructureid": 36,
                            "infrastructurename": "浴巾"
                        },
                        {
                            "infrastructureid": 35,
                            "infrastructurename": "毛巾"
                        },
                        {
                            "infrastructureid": 34,
                            "infrastructurename": "免费wifi"
                        },
                        {
                            "infrastructureid": 33,
                            "infrastructurename": "有线宽带"
                        },
                        {
                            "infrastructureid": 31,
                            "infrastructurename": "空调"
                        }
                    ],
                    "valueaddedfa": [
                        {
                            "valueaddedfaid": 40,
                            "valueaddedfaname": "电脑"
                        },
                        {
                            "valueaddedfaid": 41,
                            "valueaddedfaname": "书桌"
                        },
                        {
                            "valueaddedfaid": 42,
                            "valueaddedfaname": "插线板"
                        },
                        {
                            "valueaddedfaid": 43,
                            "valueaddedfaname": "地板"
                        },
                        {
                            "valueaddedfaid": 53,
                            "valueaddedfaname": "地砖"
                        },
                        {
                            "valueaddedfaid": 54,
                            "valueaddedfaname": "地毯"
                        }
                    ],
                    "rooms": [
                        {
                            "roomid": 179865,
                            "roomno": "102",
                            "roomname": "豪华单间",
                            "roomstatus": "vc",
                            "isselected": "T",
                            "haswindow": ""
                        },
                        {
                            "roomid": 179867,
                            "roomno": "111",
                            "roomname": "豪华单间",
                            "roomstatus": "vc",
                            "isselected": "F",
                            "haswindow": ""
                        }
                    ],
                    "isfocus": "F",
                    "iscashback": "F",
                    "cashbackcost": 0,
                    "vctxt": "仅剩2间",
                    "promoid": "1",
                    "promotype": "3",
                    "isonpromo": "1",
                    "promostarttime": "20:00",
                    "promotext": "该房间正在参与今夜特价活动，预订享受超低价。",
                    "promodustartsec": "0",
                    "promoduendsec": "19039",
                    "promostatus": 1,
                    "roomtypepic": [
                        {
                            "name": "def",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FhpVnndlJVQh4TU9vw_DqOrW4PVv"
                                }
                            ]
                        },
                        {
                            "name": "bed",
                            "pic": []
                        },
                        {
                            "name": "toilet",
                            "pic": []
                        }
                    ]
                },
                {
                    "roomtypeid": 29324,
                    "bednum": 1,
                    "roomtypename": "豪华单间",
                    "roomtypeprice": 138,
                    "roomtypepmsprice": 158,
                    "vcroomnum": 0,
                    "bed": {
                        "count": 1,
                        "beds": [
                            {
                                "bedtypename": "单床房",
                                "bedlength": "1.50米"
                            }
                        ]
                    },
                    "area": 13,
                    "bedtypename": "单床房",
                    "bedlength": "1.50米",
                    "bathroomtype": "独立卫浴",
                    "infrastructure": [
                        {
                            "infrastructureid": 15,
                            "infrastructurename": "24小时热水"
                        },
                        {
                            "infrastructureid": 18,
                            "infrastructurename": "拖鞋"
                        },
                        {
                            "infrastructureid": 19,
                            "infrastructurename": "电吹风"
                        },
                        {
                            "infrastructureid": 20,
                            "infrastructurename": "免费洗漱用品"
                        },
                        {
                            "infrastructureid": 21,
                            "infrastructurename": "免费瓶装水"
                        },
                        {
                            "infrastructureid": 39,
                            "infrastructurename": "电视"
                        },
                        {
                            "infrastructureid": 38,
                            "infrastructurename": "免费茶包"
                        },
                        {
                            "infrastructureid": 37,
                            "infrastructurename": "电热水壶"
                        },
                        {
                            "infrastructureid": 36,
                            "infrastructurename": "浴巾"
                        },
                        {
                            "infrastructureid": 35,
                            "infrastructurename": "毛巾"
                        },
                        {
                            "infrastructureid": 34,
                            "infrastructurename": "免费wifi"
                        },
                        {
                            "infrastructureid": 33,
                            "infrastructurename": "有线宽带"
                        },
                        {
                            "infrastructureid": 31,
                            "infrastructurename": "空调"
                        }
                    ],
                    "valueaddedfa": [
                        {
                            "valueaddedfaid": 40,
                            "valueaddedfaname": "电脑"
                        },
                        {
                            "valueaddedfaid": 41,
                            "valueaddedfaname": "书桌"
                        },
                        {
                            "valueaddedfaid": 42,
                            "valueaddedfaname": "插线板"
                        },
                        {
                            "valueaddedfaid": 43,
                            "valueaddedfaname": "地板"
                        },
                        {
                            "valueaddedfaid": 53,
                            "valueaddedfaname": "地砖"
                        },
                        {
                            "valueaddedfaid": 54,
                            "valueaddedfaname": "地毯"
                        }
                    ],
                    "rooms": [],
                    "isfocus": "F",
                    "iscashback": "F",
                    "cashbackcost": 0,
                    "vctxt": "满房,最近预定3小时前",
                    "isonpromo": "0",
                    "roomtypepic": [
                        {
                            "name": "def",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FhpVnndlJVQh4TU9vw_DqOrW4PVv"
                                }
                            ]
                        },
                        {
                            "name": "bed",
                            "pic": []
                        },
                        {
                            "name": "toilet",
                            "pic": []
                        }
                    ]
                },
                {
                    "roomtypeid": 29325,
                    "bednum": 1,
                    "roomtypename": "特价单间",
                    "roomtypeprice": 68,
                    "roomtypepmsprice": 88,
                    "vcroomnum": 0,
                    "bed": {
                        "count": 1,
                        "beds": [
                            {
                                "bedtypename": "单床房",
                                "bedlength": "1.50米"
                            }
                        ]
                    },
                    "area": 12,
                    "bedtypename": "单床房",
                    "bedlength": "1.50米",
                    "bathroomtype": "独立卫浴",
                    "infrastructure": [
                        {
                            "infrastructureid": 15,
                            "infrastructurename": "24小时热水"
                        },
                        {
                            "infrastructureid": 18,
                            "infrastructurename": "拖鞋"
                        },
                        {
                            "infrastructureid": 19,
                            "infrastructurename": "电吹风"
                        },
                        {
                            "infrastructureid": 20,
                            "infrastructurename": "免费洗漱用品"
                        },
                        {
                            "infrastructureid": 21,
                            "infrastructurename": "免费瓶装水"
                        },
                        {
                            "infrastructureid": 39,
                            "infrastructurename": "电视"
                        },
                        {
                            "infrastructureid": 38,
                            "infrastructurename": "免费茶包"
                        },
                        {
                            "infrastructureid": 37,
                            "infrastructurename": "电热水壶"
                        },
                        {
                            "infrastructureid": 36,
                            "infrastructurename": "浴巾"
                        },
                        {
                            "infrastructureid": 35,
                            "infrastructurename": "毛巾"
                        },
                        {
                            "infrastructureid": 34,
                            "infrastructurename": "免费wifi"
                        },
                        {
                            "infrastructureid": 33,
                            "infrastructurename": "有线宽带"
                        },
                        {
                            "infrastructureid": 31,
                            "infrastructurename": "空调"
                        }
                    ],
                    "valueaddedfa": [
                        {
                            "valueaddedfaid": 28,
                            "valueaddedfaname": "茶具"
                        },
                        {
                            "valueaddedfaid": 41,
                            "valueaddedfaname": "书桌"
                        },
                        {
                            "valueaddedfaid": 42,
                            "valueaddedfaname": "插线板"
                        },
                        {
                            "valueaddedfaid": 43,
                            "valueaddedfaname": "地板"
                        },
                        {
                            "valueaddedfaid": 53,
                            "valueaddedfaname": "地砖"
                        },
                        {
                            "valueaddedfaid": 54,
                            "valueaddedfaname": "地毯"
                        }
                    ],
                    "rooms": [],
                    "isfocus": "F",
                    "iscashback": "F",
                    "cashbackcost": 0,
                    "vctxt": "满房,最近预定3小时前",
                    "isonpromo": "0",
                    "roomtypepic": [
                        {
                            "name": "def",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FrjHUa5KrsR5_LycE6tns6DF2Okk"
                                }
                            ]
                        },
                        {
                            "name": "bed",
                            "pic": []
                        },
                        {
                            "name": "toilet",
                            "pic": []
                        }
                    ]
                },
                {
                    "roomtypeid": 29326,
                    "bednum": 2,
                    "roomtypename": "标间",
                    "roomtypeprice": 88,
                    "roomtypepmsprice": 108,
                    "vcroomnum": 0,
                    "bed": {
                        "count": 2,
                        "beds": [
                            {
                                "bedtypename": "双床房",
                                "bedlength": "1.20米"
                            },
                            {
                                "bedtypename": "双床房",
                                "bedlength": "1.20米"
                            }
                        ]
                    },
                    "area": 13,
                    "bedtypename": "双床房",
                    "bedlength": "1.20,1.20米",
                    "bathroomtype": "独立卫浴",
                    "infrastructure": [
                        {
                            "infrastructureid": 15,
                            "infrastructurename": "24小时热水"
                        },
                        {
                            "infrastructureid": 18,
                            "infrastructurename": "拖鞋"
                        },
                        {
                            "infrastructureid": 19,
                            "infrastructurename": "电吹风"
                        },
                        {
                            "infrastructureid": 20,
                            "infrastructurename": "免费洗漱用品"
                        },
                        {
                            "infrastructureid": 21,
                            "infrastructurename": "免费瓶装水"
                        },
                        {
                            "infrastructureid": 39,
                            "infrastructurename": "电视"
                        },
                        {
                            "infrastructureid": 38,
                            "infrastructurename": "免费茶包"
                        },
                        {
                            "infrastructureid": 37,
                            "infrastructurename": "电热水壶"
                        },
                        {
                            "infrastructureid": 36,
                            "infrastructurename": "浴巾"
                        },
                        {
                            "infrastructureid": 35,
                            "infrastructurename": "毛巾"
                        },
                        {
                            "infrastructureid": 34,
                            "infrastructurename": "免费wifi"
                        },
                        {
                            "infrastructureid": 33,
                            "infrastructurename": "有线宽带"
                        },
                        {
                            "infrastructureid": 31,
                            "infrastructurename": "空调"
                        }
                    ],
                    "valueaddedfa": [
                        {
                            "valueaddedfaid": 28,
                            "valueaddedfaname": "茶具"
                        },
                        {
                            "valueaddedfaid": 41,
                            "valueaddedfaname": "书桌"
                        },
                        {
                            "valueaddedfaid": 42,
                            "valueaddedfaname": "插线板"
                        },
                        {
                            "valueaddedfaid": 43,
                            "valueaddedfaname": "地板"
                        },
                        {
                            "valueaddedfaid": 53,
                            "valueaddedfaname": "地砖"
                        },
                        {
                            "valueaddedfaid": 54,
                            "valueaddedfaname": "地毯"
                        }
                    ],
                    "rooms": [],
                    "isfocus": "F",
                    "iscashback": "F",
                    "cashbackcost": 0,
                    "vctxt": "满房,最近预定3小时前",
                    "isonpromo": "0",
                    "roomtypepic": [
                        {
                            "name": "def",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/Fm5kfFUFy_vr1kNFuC83MjjqAgn8"
                                }
                            ]
                        },
                        {
                            "name": "bed",
                            "pic": []
                        },
                        {
                            "name": "toilet",
                            "pic": []
                        }
                    ]
                }
            ]
        }
    ]
 }
```


</article>

<link href="asset/css/zTreeStyle.css" media="all" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="asset/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="asset/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="asset/js/ztree_toc.js"></script>
<script type="text/javascript" src="asset/js/mdtree.js"></script>













