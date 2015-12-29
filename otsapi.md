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

## 酒店信息接口
### `「修」` 酒店综合信息查询 
***
**业务说明：**
> 根据参数信息，获取酒店的集合；排序规则优先级由高到低依次是：是否签约酒店（签约优先），指定排序项，距离，是否推荐，最低价格，酒店评分，增加满房标记位、月销量、可订房间数，返回一个图片，并给出总图数，返回可售房间数、文字、颜色（2.5）。增加返现标志，距离文字显示
眯客2.5新增需求，酒店离线的话则不显示该酒店

**接口url：**
> http://ip:port/ots/hotel/querylist

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------
hotelid 	| 酒店id 	| 否 	|  	
hotelname 	| 酒店名 	| 否 	| 支持模糊 
hoteladdr 	| 酒店地址 	| 否 	| 支持模糊 
keyword 	| 关键字 	| 否 	| 对于hotelname和hoteladdr做模糊查询用 
cityid 	| 城市编码 	| 是 	|  
disid 	| 区县id 	| 否 	|  	
userlongitude 	| 用户坐标(经度) | 否 | 用户的经度 
userlatitude 	| 用户坐标(纬度) | 否 | 用户的纬度 
pillowlongitude 	| 查询坐标(经度) | 否 | 非搜索条件 	
pillowlatitude 	| 查询坐标(纬度) | 否 | 非搜索条件 
range 	| 附近范围 	| 否 	| 单位米，若查询坐标的值不为空，则取查询坐标方圆XXXX米的酒店 
minprice 	| 最低价格 	| 否 	|  
maxprice 	| 最高价格 	| 否 	|  
bednum 	| 房间床数量 	| 否 	|  
startdateday 	| 查询开始日期 	| 否 | yyyyMMdd 
enddateday 	| 查询结束日期 	| 否 | yyyyMMdd 
startdate 	| 入住时间 	| 否 	| yyyyMMddhhmmss 
enddate 	| 离店时间 	| 否 	| yyyyMMddhhmmss 
page 	| 第几页 	| 是 	|  	|
limit 	| 每页多少条 	| 是 	|  
orderby 	| 排序项目 	| 否 	| 1:距离(distance)<br> 2:是否推荐(recommend)<br> 3:最低价格(minprice)<br> 4: 酒店评分权重(score)<br> 5:人气（月销量） 	
isdiscount 	| 是否考虑优惠价格 	| 否 | (T/F)，值为T，则最低价取优惠活动最低价，<br> 空或F则最低价取ota最低门市价 	
ishotelpic 	| 是否返回酒店图片 	| 否 | (T/F)，空等同于F，值为T，则返回图片信息，<br> 空或F则不返回图片信息 	
isroomtype 	| 是否返回房型信息 	| 否 | (T/F)，值为T，则返回房型信息，空或F则不返回房型信息 
isroomtypepic 	| 是否返回房型图片 | 否 | (T/F)，值为T，则返回房型图片信息，<br>空或F则不返回房型图片信息 
isroomtypefacility 	| 是否返回房型设施 | 否 | (T/F)，值为T，则返回房型设施信息，<br>空或F则不返回房型设施信息 
isfacility 	| 是否返回酒店设施 	| 否 | (T/F)，值为T，则返回酒店设施信息，<br>空或F则不返回酒店设施信息 
isbusinesszone 	| 是否返回酒店商圈信息 | 否| (T/F)，值为T，则返回酒店商圈信息，<br>空或F则不返回酒店设施信息 
isbedtype 	| 是否返回床型 	| 否 | (T/F)，值为T，则返回床型信息，<br>空或F则不返回酒店设施信息
isteambuying 	| 是否返回团购信息 | 否 | (T/F)，值为T，则返回团购信息，<br>空或F则不返回团购信息 
ispms 	| 是否签约 	| 否 	| (T/F),值为T，则只返回签约酒店，<br>值为F或空，返回所有酒店 
callmethod 	| 调用来源 	| 否 	| 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
callversion 	| 调用版本 	| 否 |  
callentry 	| 调用入口 	| 否 	| 1-摇一摇 <br>2-房态搜索入口<br> 3-切客 
ip 	| IP地址 	| 否 	|  	|
hoteltype 	| 酒店类型 	| 否 |  
hardwarecode 	| 硬件编码 	| 否 |  
otsversion 	| OTS版本 	| 否 	|  
excludehotelid 	| 排除酒店id | 否 | 用户酒店附近搜索时，过滤当前的酒店 
searchtype 	| 搜索方式 	| 否 	| 0附近；<br>1商圈；<br>2机场车站；<br>3地铁路线；<br>4行政区；<br>5景点；<br>6医院；<br>7高校；<br>8酒店；<br>9地址；<br>-1不限) 
posid 	| 位置区域id 	| 否 	|  
posname 	| 位置区域名称 	| 否 	|  
points 	| 坐标集合 	| 否 	| [[11,22], [10,12]] 
bedtype 	| 床型搜索 	| 否 	| 按指定床型搜索酒店:<br>1单床；<br>2双床；<br>-1不限。 

>API返回json数据示例：

```js
{
    "success":true,
    "errcode":,//错误码
    "errmsg":,//错误信息
    "count":,//酒店数量
    "hotel":
        [{
            "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
      *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	    *「增」 "highlights":[
                  	{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }]
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }]
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   }]

}
```

### `「修」` 特价房信息查询 『老接口』
***
**业务说明：**
> 眯客 3.1 新增需求， 搜索特价房接口
**接口url：**
> http://ip:port/ots/hotel/querypromolist

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------
cityid 	| 城市编码 	| 是 	|  
promotype 	| 特价房类型 	| 是 	| 按指定特价类型搜索酒店，>=1
hotelid 	| 酒店id 	| 否 	|  	
hotelname 	| 酒店名 	| 否 	| 支持模糊 
hoteladdr 	| 酒店地址 	| 否 	| 支持模糊 
keyword 	| 关键字 	| 否 	| 对于hotelname和hoteladdr做模糊查询用 
disid 	| 区县id 	| 否 	|  	
userlongitude 	| 用户坐标(经度) | 否 | 用户的经度 
userlatitude 	| 用户坐标(纬度) | 否 | 用户的纬度 
pillowlongitude 	| 查询坐标(经度) | 否 | 非搜索条件 	
pillowlatitude 	| 查询坐标(纬度) | 否 | 非搜索条件 
range 	| 附近范围 	| 否 	| 单位米，若查询坐标的值不为空，则取查询坐标方圆XXXX米的酒店 
minprice 	| 最低价格 	| 否 	|  
maxprice 	| 最高价格 	| 否 	|  
bednum 	| 房间床数量 	| 否 	|  
startdateday 	| 查询开始日期 	| 否 | yyyyMMdd 
enddateday 	| 查询结束日期 	| 否 | yyyyMMdd 
startdate 	| 入住时间 	| 否 	| yyyyMMddhhmmss 
enddate 	| 离店时间 	| 否 	| yyyyMMddhhmmss 
page 	| 第几页 	| 是 	|  	|
limit 	| 每页多少条 	| 是 	|  
orderby 	| 排序项目 	| 否 	| 1:距离(distance)<br> 2:是否推荐(recommend)<br> 3:最低价格(minprice)<br> 4: 酒店评分权重(score)<br> 5:人气（月销量） 	
isdiscount 	| 是否考虑优惠价格 	| 否 | (T/F)，值为T，则最低价取优惠活动最低价，<br> 空或F则最低价取ota最低门市价 	
ishotelpic 	| 是否返回酒店图片 	| 否 | (T/F)，空等同于F，值为T，则返回图片信息，<br> 空或F则不返回图片信息 	
isroomtype 	| 是否返回房型信息 	| 否 | (T/F)，值为T，则返回房型信息，空或F则不返回房型信息 
isroomtypepic 	| 是否返回房型图片 | 否 | (T/F)，值为T，则返回房型图片信息，<br>空或F则不返回房型图片信息 
isroomtypefacility 	| 是否返回房型设施 | 否 | (T/F)，值为T，则返回房型设施信息，<br>空或F则不返回房型设施信息 
isfacility 	| 是否返回酒店设施 	| 否 | (T/F)，值为T，则返回酒店设施信息，<br>空或F则不返回酒店设施信息 
isbusinesszone 	| 是否返回酒店商圈信息 | 否| (T/F)，值为T，则返回酒店商圈信息，<br>空或F则不返回酒店设施信息 
isbedtype 	| 是否返回床型 	| 否 | (T/F)，值为T，则返回床型信息，<br>空或F则不返回酒店设施信息
isteambuying 	| 是否返回团购信息 | 否 | (T/F)，值为T，则返回团购信息，<br>空或F则不返回团购信息 
ispms 	| 是否签约 	| 否 	| (T/F),值为T，则只返回签约酒店，<br>值为F或空，返回所有酒店 
callmethod 	| 调用来源 	| 否 	| 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
callversion 	| 调用版本 	| 否 |  
callentry 	| 调用入口 	| 否 	| 1-摇一摇 <br>2-房态搜索入口<br> 3-切客 
ip 	| IP地址 	| 否 	|  	|
hoteltype 	| 酒店类型 	| 否 |  
hardwarecode 	| 硬件编码 	| 否 |  
otsversion 	| OTS版本 	| 否 	|  
excludehotelid 	| 排除酒店id | 否 | 用户酒店附近搜索时，过滤当前的酒店 
searchtype 	| 搜索方式 	| 否 	| 0附近；<br>1商圈；<br>2机场车站；<br>3地铁路线；<br>4行政区；<br>5景点；<br>6医院；<br>7高校；<br>8酒店；<br>9地址；<br>-1不限) 
posid 	| 位置区域id 	| 否 	|  
posname 	| 位置区域名称 	| 否 	|  
points 	| 坐标集合 	| 否 	| [[11,22], [10,12]] 
bedtype 	| 床型搜索 	| 否 	| 按指定床型搜索酒店:<br>1单床；<br>2双床；<br>-1不限。 
 

> API返回json数据示例：

```js
{
    "success":true,
    "errcode":,//错误码
    "errmsg":,//错误信息
    "count":,//酒店数量
    "hotel":
        [{
            "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }],
      *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	    *「增」 "highlights":[
                  	{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }]
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   }],
	   "supplementhotel":[{
	         "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }],
        *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	    *「增」 "highlights":[
                  	{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }]
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   
	   }]

}
```


### 酒店详细信息查询
----
**业务说明：**
> 根据酒店ID，获取酒店的详细信息。

**接口url：**
> http://ip:port/ots/hoteldetail/queryinfo

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------
hotelid 	| 酒店id 	| 是 	|  
callmethod 	| 调用来源 	| 否 	| 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
callversion 	| 调用版本 	| 否 | 
ip 	| IP地址 	| 否 	|  	|
hardwarecode 	| 硬件编码 	| 否 |  
otsversion 	| OTS版本 	| 否 	|  

> API返回json数据示例：

```js
{
    "success":true,
    "errcode":,//错误码
    "errmsg":,//错误信息
    "hotel":{
            "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "hotelfacility":[{//酒店设施
                            "facid":,//设施id
                            "facname"://设施名称
            }]
	    "latitude":, //纬度
	    "longitude":, //经度
	    "repairinfo":, //装修信息
    "businesszone":[{//商圈
            "businesszonename"://商圈名称
    }],

    "service"[{//酒店服务
        "serviceid":,//服务id
        "servicename":,//服务名称
        }]
    "trafficdec":,//交通描述
    "viewspotdec":,//景点描述
    "ambitustrafficdec":,// 周边交通描述
    "ambituslifedec":,// 周边生活描述
    "retentiontime":  18:00//保留时间
    "defaultleavetime":  12:00// 默认离店时间
    "iscashback":  //是否返现（T/F）

    }
}
```





### 查询历史入住酒店信息
***
**业务说明：**
> 此接口用于查看某用户在某个城市最近一年内曾经预订（IN、OK、PM）过的酒店的清单，按照最后一次预订的时间排序，最近预订的酒店排在最前
异常处理：
该房型没有房间预订，则报错，跳转到酒店详情页面。

**接口url：**
> http://ip:port/ots/history/querylist

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------
| token        	| 用户令牌   | 是    |                                                    
| citycode     	| 所在城市   | 是    |                                                    
| page         	| 第几页     | 是    |                                                    
| limit        	| 每页多少条 	| 是    |                                                    
| callmethod   	| 调用来源   | 否   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 	
| callversion  	| 调用版本   | 否   |
| ip           	| IP地址  | 否     |                                                    
| hardwarecode 	| 硬件编码   | 否       |                                                    
| otsversion   	| OTS版本    | 否       |                                                    

> API返回json数据示例：

```js
{
	"success":true,
	"errcode":,//错误码
	"errmsg":,//错误信息
	"hotel" : [{
					"hotelid":,//酒店id
					"hotelame":,//酒店名字
					"roomtypeid":,// 预定历史房型
					"roomtypename":,//房型名字
					"roomno":,// 预定历史房间号
					"ordernummon":,//月销量
					"avlblroomnum":,//可订房间数
					"minprice":,//最低价格
					"minpmsprice",//最低价格对应房型的门市价 					"avlblroomnum":,//可订房间数
					"avlblroomdes":,//可订房描述 					"descolor":,//描述字体颜色 （状态: “>3间房间”    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
 					"ordernummon":,//月订单数 					"rcntordertimedes":,//最近订单时间描述 
					"pic":[{//酒店图片
							"name":,//图片名称
							"url": // url地址
					}]
				}]
}
```

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
|showblacktype 	| 显示黑名单类型的房态 	| 否 |1-一元秒杀|
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

## 特价活动
### `「增」` 获取特价活动价格区间
**业务说明：**
> 根据 promoid 获取特价活动价格区间

**接口 url：**
> http://ip:port/ots/promo/queryrange

**请求参数：**

    字段        |         名称         | 是否必须 | 说明|
--------------- | ------------------- | -------| -----------                                                 
| promoid     	| 特价活动 id   | 是    |1. 今夜特价 <br> 2.今日特价<br> 3. 主题酒店 <br> 6. 一元秒杀  |
|cityid 	| 城市编码 	| 是 	|                                                    
| callmethod   	| 调用来源   | 是   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) |
| callversion  	| 调用版本   | 是   |
| ip           	| IP地址  | 否     |                                                    
| hardwarecode 	| 硬件编码   | 否       |                                                    
| otsversion   	| OTS版本    | 否       |   


> API返回json返回说明：

```js
{
	"success": true,
	"promoid":1,		       //特价活动 id
	"step": 15,            //特价步长
	"minprice": 10,	      // 特价活动最低价
	"maxprice": 100,        // 特价活动最高价
	"promosec":0,		      //距离特价活动开始的秒数
	"promosecend":25235， //距离特价活动结束的秒数
	"nextpromosec":71975	  //距离下一次特价活动开始的秒数
	
}
``` 

> API返回json返回说明：

```js
{
	"success": true,
	"promoid":1,
	"step": 15, 
	"minprice": 10,
	"maxprice": 100,
	"promosec":0,	
	"promosecend":25235，
	"nextpromosec":71975	 
	
}
``` 
### 『增』判断是否特价城市
***
**业务说明：**
根据 cityid 获取该城市是否参与特价活动


**接口url：**
>http://ip:port/ots//promo/ispromocity

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿ cityid|城市编码|是|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|

> API返回json数据示例：

```js
{
	"result":true//该城市参与特价活动
}
或
{
	"result":false//该城市未参与特价活动
}
```

### `「增」` 特价房提醒
----
**业务说明：**
> 记录用户提醒特价房要求。

**接口url：**
> http://ip:port/ots/remind/create

**请求参数：**

|    字段        |         名称         | 是否必须  | 说明|
|--------------- | ------------------- | ----------| -----------|
|token 	| 用户令牌 	| 是 	|  
|hotelid 	| 酒店id 	| 是 	| 	
|roomtypeid 	| 房型id 	| 是 	| 	
|callmethod 	| 调用来源 	| 否 	| 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) |
|callversion 	| 调用版本 	| 否 | 	
|ip 	| IP地址 	| 否 	|  		
|hardwarecode 	| 硬件编码 	| 否 |  
|otsversion 	| OTS版本 	| 否 	|  

> API返回json数据说明：

```js
{
    "success":true,
    "errcode":,//错误码
    "errmsg"://错误信息
}
```

> API返回json数据示例：

```js
{
    "success":true,
    "errcode":0,
    "errmsg":""
}
```

### `「增」` 特价房信息查询
***
**业务说明：**
> 眯客 3.2+ 新增需求， 搜索特价房接口

**接口url：**
> http://ip:port/ots/search/querypromo

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------
cityid 	| 城市编码 	| 是 	|  
hotelid 	| 酒店id 	| 否 	|  	
hotelname 	| 酒店名 	| 否 	| 支持模糊 
hoteladdr 	| 酒店地址 	| 否 	| 支持模糊 
keyword 	| 关键字 	| 否 	| 对于hotelname和hoteladdr做模糊查询用 
disid 	| 区县id 	| 否 	|  	
promoid|特价活动 id|是|
minpromoprice 	| 价格区间-小 	| 否 	| 
maxpromoprice 	| 价格区间-大 	| 否 	| 
userlongitude 	| 用户坐标(经度) | 否 | 用户的经度 
userlatitude 	| 用户坐标(纬度) | 否 | 用户的纬度 
pillowlongitude 	| 查询坐标(经度) | 否 | 非搜索条件 
pillowlatitude 	| 查询坐标(纬度) | 否 | 非搜索条件 
range 	| 附近范围 	| 否 	| 单位米，若查询坐标的值不为空，则取查询坐标方圆XXXX米的酒店 
minprice 	| 最低价格 	| 否 	|  
maxprice 	| 最高价格 	| 否 	|  
bednum 	| 房间床数量 	| 否 	|  
startdateday 	| 查询开始日期 	| 否 | yyyyMMdd 
enddateday 	| 查询结束日期 	| 否 | yyyyMMdd 
startdate 	| 入住时间 	| 否 	| yyyyMMddhhmmss 
enddate 	| 离店时间 	| 否 	| yyyyMMddhhmmss 
page 	| 第几页 	| 是 	|  	|
limit 	| 每页多少条 	| 是 	|  
orderby 	| 排序项目 	| 否 	| 1:距离(distance)<br> 2:是否推荐(recommend)<br> 3:最低价格(minprice)<br> 4: 酒店评分权重(score)<br> 5:人气（月销量） 	
isdiscount 	| 是否考虑优惠价格 	| 否 | (T/F)，值为T，则最低价取优惠活动最低价，<br> 空或F则最低价取ota最低门市价 	
ishotelpic 	| 是否返回酒店图片 	| 否 | (T/F)，空等同于F，值为T，则返回图片信息，<br> 空或F则不返回图片信息 	
isroomtype 	| 是否返回房型信息 	| 否 | (T/F)，值为T，则返回房型信息，空或F则不返回房型信息 
isroomtypepic 	| 是否返回房型图片 | 否 | (T/F)，值为T，则返回房型图片信息，<br>空或F则不返回房型图片信息 
isroomtypefacility 	| 是否返回房型设施 | 否 | (T/F)，值为T，则返回房型设施信息，<br>空或F则不返回房型设施信息 
isfacility 	| 是否返回酒店设施 	| 否 | (T/F)，值为T，则返回酒店设施信息，<br>空或F则不返回酒店设施信息 
isbusinesszone 	| 是否返回酒店商圈信息 | 否| (T/F)，值为T，则返回酒店商圈信息，<br>空或F则不返回酒店设施信息 
isbedtype 	| 是否返回床型 	| 否 | (T/F)，值为T，则返回床型信息，<br>空或F则不返回酒店设施信息
isteambuying 	| 是否返回团购信息 | 否 | (T/F)，值为T，则返回团购信息，<br>空或F则不返回团购信息 
ispms 	| 是否签约 	| 否 	| (T/F),值为T，则只返回签约酒店，<br>值为F或空，返回所有酒店 
callmethod 	| 调用来源 	| 否 	| 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
callversion 	| 调用版本 	| 否 |  
callentry 	| 调用入口 	| 否 	| 1-摇一摇 <br>2-房态搜索入口<br> 3-切客 
ip 	| IP地址 	| 否 	|  	|
hoteltype 	| 酒店类型 	| 否 |  
hardwarecode 	| 硬件编码 	| 否 |  
otsversion 	| OTS版本 	| 否 	|  
excludehotelid 	| 排除酒店id | 否 | 用户酒店附近搜索时，过滤当前的酒店 
searchtype 	| 搜索方式 	| 否 	| 0附近；<br>1商圈；<br>2机场车站；<br>3地铁路线；<br>4行政区；<br>5景点；<br>6医院；<br>7高校；<br>8酒店；<br>9地址；<br>-1不限) 
posid 	| 位置区域id 	| 否 	|  
posname 	| 位置区域名称 	| 否 	|  
points 	| 坐标集合 	| 否 	| [[11,22], [10,12]] 
bedtype 	| 床型搜索 	| 否 	| 按指定床型搜索酒店:<br>1单床；<br>2双床；<br>-1不限。 
 

> API返回json数据示例：

```js
{
    "success":true,
    "errcode":,//错误码
    "errmsg":,//错误信息
    "count":,//酒店数量
    "hotel":
        [{
            "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
	   *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	   *「增」 "highlights":[{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }]
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }],
			     "width": //图片宽度,
			     "height": //图片高度
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }],
			     "width": //图片宽度,
			     "height": //图片高度
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   }],
	   "supplementhotel":[{
	         "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
	   *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	    *「增」 "highlights":[
                  	{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }]
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }],
			     "width": //图片宽度,
			     "height": //图片高度
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   
	   }]

}
```

### `「增」` 主题房信息查询
***
**业务说明：**
> 眯客 3.2+ 新增需求， 主题房搜索接口

**接口url：**
> http://ip:port/ots/search/querythemes

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------
cityid 	| 城市编码 	| 是 	|  
hotelid 	| 酒店id 	| 否 	|  	
hotelname 	| 酒店名 	| 否 	| 支持模糊 
hoteladdr 	| 酒店地址 	| 否 	| 支持模糊 
keyword 	| 关键字 	| 否 	| 对于hotelname和hoteladdr做模糊查询用 
disid 	| 区县id 	| 否 	|  	
userlongitude 	| 用户坐标(经度) | 否 | 用户的经度 
userlatitude 	| 用户坐标(纬度) | 否 | 用户的纬度 
pillowlongitude 	| 查询坐标(经度) | 否 | 非搜索条件 	
pillowlatitude 	| 查询坐标(纬度) | 否 | 非搜索条件 
range 	| 附近范围 	| 否 	| 单位米，若查询坐标的值不为空，则取查询坐标方圆XXXX米的酒店 
minprice 	| 最低价格 	| 否 	|  
maxprice 	| 最高价格 	| 否 	|  
bednum 	| 房间床数量 	| 否 	|  
startdateday 	| 查询开始日期 	| 否 | yyyyMMdd 
enddateday 	| 查询结束日期 	| 否 | yyyyMMdd 
startdate 	| 入住时间 	| 否 	| yyyyMMddhhmmss 
enddate 	| 离店时间 	| 否 	| yyyyMMddhhmmss 
page 	| 第几页 	| 是 	|  	|
limit 	| 每页多少条 	| 是 	|  
orderby 	| 排序项目 	| 否 	| 1:距离(distance)<br> 2:是否推荐(recommend)<br> 3:最低价格(minprice)<br> 4: 酒店评分权重(score)<br> 5:人气（月销量） 	
isdiscount 	| 是否考虑优惠价格 	| 否 | (T/F)，值为T，则最低价取优惠活动最低价，<br> 空或F则最低价取ota最低门市价 	
ishotelpic 	| 是否返回酒店图片 	| 否 | (T/F)，空等同于F，值为T，则返回图片信息，<br> 空或F则不返回图片信息 	
isroomtype 	| 是否返回房型信息 	| 否 | (T/F)，值为T，则返回房型信息，空或F则不返回房型信息 
isroomtypepic 	| 是否返回房型图片 | 否 | (T/F)，值为T，则返回房型图片信息，<br>空或F则不返回房型图片信息 
isroomtypefacility 	| 是否返回房型设施 | 否 | (T/F)，值为T，则返回房型设施信息，<br>空或F则不返回房型设施信息 
isfacility 	| 是否返回酒店设施 	| 否 | (T/F)，值为T，则返回酒店设施信息，<br>空或F则不返回酒店设施信息 
isbusinesszone 	| 是否返回酒店商圈信息 | 否| (T/F)，值为T，则返回酒店商圈信息，<br>空或F则不返回酒店设施信息 
isbedtype 	| 是否返回床型 	| 否 | (T/F)，值为T，则返回床型信息，<br>空或F则不返回酒店设施信息
isteambuying 	| 是否返回团购信息 | 否 | (T/F)，值为T，则返回团购信息，<br>空或F则不返回团购信息 
ispms 	| 是否签约 	| 否 	| (T/F),值为T，则只返回签约酒店，<br>值为F或空，返回所有酒店 
callmethod 	| 调用来源 	| 否 	| 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
callversion 	| 调用版本 	| 否 |  
callentry 	| 调用入口 	| 否 	| 1-摇一摇 <br>2-房态搜索入口<br> 3-切客 
ip 	| IP地址 	| 否 	|  	|
hoteltype 	| 酒店类型 	| 否 |  
hardwarecode 	| 硬件编码 	| 否 |  
otsversion 	| OTS版本 	| 否 	|  
excludehotelid 	| 排除酒店id | 否 | 用户酒店附近搜索时，过滤当前的酒店 
searchtype 	| 搜索方式 	| 否 	| 0附近；<br>1商圈；<br>2机场车站；<br>3地铁路线；<br>4行政区；<br>5景点；<br>6医院；<br>7高校；<br>8酒店；<br>9地址；<br>-1不限) 
posid 	| 位置区域id 	| 否 	|  
posname 	| 位置区域名称 	| 否 	|  
points 	| 坐标集合 	| 否 	| [[11,22], [10,12]] 
bedtype 	| 床型搜索 	| 否 	| 按指定床型搜索酒店:<br>1单床；<br>2双床；<br>-1不限。 
 

> API返回json数据示例：

```js
{
    "success":true,
    "errcode":,//错误码
    "errmsg":,//错误信息
    "count":,//酒店数量
    "hotel":
        [{
            "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
	   *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	     *「增」 "highlights":[
                  	{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }]
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
			"roomtypepic":[{//主题房房型图片
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }],
			     "width": //图片宽度,
			     "height": //图片高度
                          }]
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }],
			     "width": //图片宽度,
			     "height": //图片高度
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   }],
	   "supplementhotel":[{
	         "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
	  *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	     *「增」 "highlights":[
                  	{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
	     *「增」 "themetexts":[
                  	{ 
                        "text": "主题tag",//特点描述
                        "color":"66FFBB", // 如果颜色设定,则C端使用,否则C端自己控制
                       },
		       { 
                        "text": "主题tag",//特点描述
                        "color":"66FFBB", // 如果颜色设定,则C端使用,否则C端自己控制
                      }
                ],  //主题酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }]
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }],
			     "width": //图片宽度,
			     "height": //图片高度
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   
	   }]

}
```

> API 返回数据示例
```js
{
    "errcode": 0,
    "errmsg": "",
    "hotels": [
        {
            "hotel": [
                {
                    "hotelid": "4705",
                    "hotelname": "重庆帝临酒店",
                    "hotelrulecode": 1002,
                    "online": "T",
                    "hotelvc": "T",
                    "detailaddr": "重庆市沙坪坝区双巷子街6号附11号",
                    "hoteldis": "S 沙坪坝区",
                    "hotelcity": "500000",
                    "hotelprovince": "Z 重庆市",
                    "hotelphone": "023-65348952",
                    "hoteldisc": "酒店地理位置优越，交通方便，经济实惠，期待您的入驻。",
                    "longitude": 106.461941,
                    "latitude": 29.558707,
                    "distance": 2179413.1303076735,
                    "userdistance": 2179413.1303076735,
                    "isnear": "F",
                    "grade": 0,
                    "minprice": 158,
                    "minpmsprice": 488,
                    "ispms": "T",
                    "hotelpicnum": 40,
                    "avlblroomnum": 18,
                    "avlblroomdes": "",
                    "descolor": "32ab18",
                    "ordernummon": "0",
                    "rcntordertimedes": "最近预订1天前",
                    "greetscore": 1080,
                    "repairinfo": "2014年装修",
                    "highlights": [
                        {
                            "name": "wifi",
                            "icon": "http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                            "id": 44
                        }
                    ],
                    "themetexts": [
                        {
                            "text": "主题tag",
                            "color": "66FFBB"
                        },
                        {
                            "text": "主题tag",
                            "color": "66FFBB"
                        }
                    ],
                    "promoinfo": [
                        {
                            "promoprice": "60",
                            "promoid": 1,
                            "promotype": 2
                        },
                        {
                            "promoprice": "90",
                            "promoid": 1,
                            "promotype": 3
                        },
                        {
                            "promoid": 3,
                            "promotype": 21
                        }
                    ],
                    "teambuying": [
                        {
                           "teambuyingname": "",
                            "url": ""
                        }
                    ],
                    "isrecommend": "F",
                    "hotelpic": [
                        {
                            "name": "def",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FlXbAU1AZ_rFNYtdiT6j4N37BylS"
                                }
                            ]
                        },
                        {
                            "name": "lobby",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FgKBX2nbglK2U0cpMXUVGBKVFXTV"
                                }
                            ]
                        },
                        {
                            "name": "mainHousing",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FootNlBX0VNiFABYSdE7mTg-T0oq"
                                }
                            ]
                        }
                    ],
                    "themepic": [
                        {
                            "name": "sb",
			    "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FlXbAU1AZ_rFNYtdiT6j4N37BylS"
                                }
                            ]
                        }
                    ],
                    "hotelfacility": [
                        {
                            "facid": 10,
                            "facname": "叫醒服务"
                        },
                        {
                            "facid": 9,
                            "facname": "棋牌室"
                        }
                    ],
                    "businesszone": [                        
                    ]
                }
            ]
        }
    ]
   }
```


## 推荐资源配置
### 获取发现列表页和Banner页
**业务说明：**
> 根据 cityid 获取 banner 推荐信息

**接口 url：**
> http://ip:port/ots/recommend/query

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------                                                 
| citycode     	| 城市编码   | 是    |                                                    
| platform   	| 推送平台   | 是   | 1-Android；<br>2-IOS；<br>4-wechat； 
| position   	| 推荐位   | 是   | 921A：首页，<br> 921C：发现首页，<br> 921D：发现列表页，<br>921B：广告推荐位 
| callmethod   	| 调用来源   | 是   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
| callversion  	| 调用版本   | 是   |
| ip           	| IP地址  | 否     |                                                    
| hardwarecode 	| 硬件编码   | 否       |                                                    
| otsversion   	| OTS版本    | 否       |   


> API返回json返回说明：

```js
{
    "success": true,
    "banners": [
        {
            "name": "上海那些脑洞大开的主题酒店", //推荐内容名称
            "description": "上海那些脑洞大开的主题酒店",//推荐内容描述
            "imgurl": "http://7xn138.com2.z0.glb.qiniucdn.com/主题酒店滚动条.jpg", //图片地址
            "url": "www.imike.com/app", //推荐外链地址
            "detailid": 8, //推荐内链 id
            "querytype": 1, // 推荐类型
            "createtime": 1443276228000 // 创建时间
        }
    ]
}
```

> API返回json数据示例：

```js
{
    "success": true,
    "banners": [
        {
            "name": "上海那些脑洞大开的主题酒店",
            "description": "上海那些脑洞大开的主题酒店",
            "imgurl": "http://7xn138.com2.z0.glb.qiniucdn.com/主题酒店滚动条.jpg",
            "detailid": 8,
            "querytype": 1,
            "createtime": 1443276228000
        },
        {
            "name": "眯客一周年",
            "description": "眯客一周年",
            "imgurl": "http://7xn138.com2.z0.glb.qiniucdn.com/20151031imike1year.jpg",
            "url": "",
            "detailid": 11,
            "querytype": 1,
            "createtime": 1444912720000
        },
        {
            "name": "眯客3.0正式上线",
            "description": "眯客3.0正式上线",
            "imgurl": "http://7xn138.com2.z0.glb.qiniucdn.com/20151031mike3.0.png",
            "url": "",
            "detailid": 10,
            "querytype": 1,
            "createtime": 1444726043000
        }
    ]
}
```

### 「增」查询首屏推荐信息
**业务说明：**
> 根据 cityid 获取首屏推荐信息

**接口 url：**
> http://ip:port/ots/recommend/queryloading

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------                                                
| cityid     	| 城市编码   | 是    |                                                    
| callmethod   	| 调用来源   | 是   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
| callversion  	| 调用版本   | 是   |
| ip           	| IP地址  | 是     |                                                    
| hardwarecode 	| 硬件编码   | 是       |                                                    
| otsversion   	| OTS版本    | 否       |   


> API返回json返回说明：

```js
{
    "success": true,
    "loading": [ 
        {
            "name": " 新版本介绍1", // 图片名称
            "description": "新版本介绍1", //图片描述
            "imgurl": "http://7xn138.com2.z0.glb.qiniucdn.com/主题酒店滚动条.jpg", // 图片链接
            "url": "http://www.baidu.com",  //推荐跳转外链
            "detailid": 8, //推荐跳转内链 id
            "querytype": 1, // 推荐类型
            "createtime": 1443276228000 // 创建时间
        }
            ]
}
```




> API返回json数据示例：

```js
{
    "success": true,
    "loading": [
        {
            "name": " 新版本介绍1",
            "description": "新版本介绍1",
            "imgurl": "http://7xn138.com2.z0.glb.qiniucdn.com/主题酒店滚动条.jpg",
            "url": "http://www.baidu.com",
            "detailid": 8,
            "querytype": 1,
            "createtime": 1443276228000
        },
        {
            "name": "眯客一周年",
            "description": "眯客一周年",
            "imgurl": "http://7xn138.com2.z0.glb.qiniucdn.com/20151031imike1year.jpg",
            "url": "",
            "detailid": 11,
            "querytype": 1,
            "createtime": 1444912720000
        },
        {
            "name": "眯客3.0正式上线",
            "description": "眯客3.0正式上线",
            "imgurl": "http://7xn138.com2.z0.glb.qiniucdn.com/20151031mike3.0.png",
            "url": "",
            "detailid": 10,
            "querytype": 1,
            "createtime": 1444726043000
        }
    ]
}
```



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
			
				"roomscoresubject":[{//评价项目
						"subjectid":,//评价项目
						"subjectname":,//评价名称
						"grade"://分值
				}]
		}]，
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
			*『增』  "roominfo":,//房间信息
			*『增』	"hotelmark":[{//酒店评价标签
						"id":1 , //mark主键
						"mark":非常好 ,//mark变签内容
					}],
					"scorepic":[{//评价图片信息
						"url"://图片地址
					}],
	 }]
}
```


##『增』请求日志记录
***
**业务说明：**
>记录用户所有请求信息.

**接口url:**
>http://ip:port/ots/sys/viewevent

**请求参数：**

|    字段        |         名称         | 是否必须 | 说明|
--------------- | ------------------- | -------| -----------
|tourl|view的目标url/事件url|是|
|actiontype|event/view|是|
|fromurl|view的来源url/事件url|否|
|params|请求参数|是|json格式字符串[{"hotel_id":1,"city_code":123 }]
|﻿longitude|用户精度|否|
|latitude|用户纬度|否|
|cityid|用户城市id|否|
|ip|用户ip地址|否|
|callmethod|请求来源|是|1-crs；<br> 2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android)
|version|app版本|否|
|wifimacaddr|wifi mac地址|否|
|bimacaddr|蓝牙 mac地址|否|
|simsn|msn卡|否|
|token|用户token|否|能获取用户token则必填，新用户非必填
|bussinesstype|业务类型|否|类型：<br>1酒店；<br>2：订单<br>3：其它
|bussinessid|业务id|否|
|hardwarecode|硬件编码|否|
|imei|imei号|否

> API返回json数据说明：

```js
{
    "success":true,
    "errcode":,//错误码
    "errmsg":,//错误信息
}
```

> API返回json数据示例：

```js
{
    "success":true,
    "errcode":0,
    "errmsg":""
}
```

##「修」订单接口

###查询订单详情
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
|`「修」 `statetype|取值范围|否|[all、doing、commenting（查询待评价订单的状态需要加该字段且需要加isscore=F）、done]

> API返回json数据示例：

```js
{
	success:true,
errcode:,//错误码
errmsg:,//错误信息
	count:10, //总数
	order:
[{//所有订单
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
		tickets:
[{//优惠券信息，按照线上优惠额倒序排序，金额最大的为默认选中
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
		roomorder:
[{//订单下客单
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
payprice:
[{房费应支付金额
	actiondate:,// 日期
	price:,//房价金额
}]
		breakfastnum:1,   //早餐数
		contacts:’联系人姓名’,
		contactsphone:’联系人电话’,
		contactsemail:’联系人email’,
		contactsweixin:’联系人微信’,
		checkinuser:
[{//入住人信息
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
action:’’, //动作意义    cancel 取消、pay 付款、edit 修改、checkin快速入住、meet约会、refund退款、evaluation评价、continuedtolive 续住、delete删除
}]
}]
},
*「增」orderPromoPayRule:{
"id": 4,//主键
"promoType": 2,//活动的类型
"isTicketPay": 0,//是否可以使用优惠券付款：1：支持优惠券支付 0：不支持优惠券支付
"isWalletPay": 1,//是否可以使用钱包付款：1：支持钱包支付 0：不支持钱包支付
"isOnlinePay": 1,//是否可以使用在线支付付款：1：支持在线支付支付 0：不支持在线支付支付
"isRealPay": 0//是否可以使用线下付款：1：支持线下支付 0：不支持线下支付
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
|`「增」`showblacktype 	| 显示黑名单类型的房态 	| 否 |1-一元秒杀|

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
	tickets:
[{//优惠券信息，按照线上优惠额倒序排序，金额最大的为默认选中
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
	roomorder:
[{//订单下客单
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
payprice:
[{房费应支付金额
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
，orderpaydetail: [
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
},
*「增」orderPromoPayRule:{
"id": 4,//主键
"promoType": 2,//活动的类型
"isTicketPay": 0,//是否可以使用优惠券付款：1：支持优惠券支付 0：不支持优惠券支付
"isWalletPay": 1,//是否可以使用钱包付款：1：支持钱包支付 0：不支持钱包支付
"isOnlinePay": 1,//是否可以使用在线支付付款：1：支持在线支付支付 0：不支持在线支付支付
"isRealPay": 0//是否可以使用线下付款：1：支持线下支付 0：不支持线下支付
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
|`「增」`showblacktype 	| 显示黑名单类型的房态 	| 否 |1-一元秒杀|

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
	ordertype:1, //预订方式  1、预付 2、普通	pricetype:1, //价格类型 1、时租  2、日租
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
	tickets:
[{//优惠券信息，按照线上优惠额倒序排序，金额最大的为默认选中
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
	roomorder:
[{//订单下客单
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
payprice:
[{房费应支付金额
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
，orderpaydetail: [
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
},
*「增」orderPromoPayRule:{
"id": 4,//主键
"promoType": 2,//活动的类型
"isTicketPay": 0,//是否可以使用优惠券付款：1：支持优惠券支付 0：不支持优惠券支付
"isWalletPay": 1,//是否可以使用钱包付款：1：支持钱包支付 0：不支持钱包支付
"isOnlinePay": 1,//是否可以使用在线支付付款：1：支持在线支付支付 0：不支持在线支付支付
"isRealPay": 0//是否可以使用线下付款：1：支持线下支付 0：不支持线下支付
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
|`「增」`showblacktype 	| 显示黑名单类型的房态 	| 否 |1-一元秒杀|

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
	tickets:
[{//优惠券信息，按照线上优惠额倒序排序，金额最大的为默认选中
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
	roomorder:
[{//订单下客单
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
payprice:
[{房费应支付金额
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
cashbackcost:  //返现金额
，orderpaydetail: [
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
},
*「增」orderPromoPayRule:{
"id": 4,//主键
"promoType": 2,//活动的类型
"isTicketPay": 0,//是否可以使用优惠券付款：1：支持优惠券支付 0：不支持优惠券支付
"isWalletPay": 1,//是否可以使用钱包付款：1：支持钱包支付 0：不支持钱包支付
"isOnlinePay": 1,//是否可以使用在线支付付款：1：支持在线支付支付 0：不支持在线支付支付
"isRealPay": 0//是否可以使用线下付款：1：支持线下支付 0：不支持线下支付
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
### `「修」 `订单数量统计
***
**业务说明：**

该接口用于统计某些订单状态对应的订单数量

**接口url：**
>http://ip:port/ots/order/selectcount

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿token|授权token|是|必填
|﻿集合参数(status)|||
|sqnum|顺序号|是|用于返回信息排序
|orderstatus|订单类型|是|若为多个订单状态，则用“，”分隔
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android)
|callversion|调用版本|否（若预付，则必填）|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|
|`「修」 `statetype|取值范围|否|[all、doing、commenting（查询待评价订单的状态需要加该字段且需要加isscore=F）、done]
|`「增」 `isscore|是否评价|否|T/F
> API返回json数据示例：

```js
{
	success:true,
errcode:,//错误码
errmsg:,//错误信息
statuscount:
[{
	sqnum:,//顺序号
	ordercount:,//订单数量
},]
}


```

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

##首页
###『增』 首页今夜特价房
***
**业务说明：**
根据 citycode 拉取特价城市的特价活动信息


**接口url：**
>http://ip:port/ots/homepage/promolist

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿cityid|城市编码|是|
|﻿page|第几页|否|
|﻿limit|每页显示几条记录|否|
| userlatitude |用户坐标(纬度)|是|
| userlongitude |用户坐标(经度)|是|
|pillowlongitude 	| 查询坐标(经度) | 否 | 定位城市必须传, 非定位城市不能传
|pillowlatitude 	| 查询坐标(纬度) | 否 | 定位城市必须传, 非定位城市不能传
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|

> API返回json数据示例：

```js
{
    "success":true,
    "errcode":,//错误码
    "errmsg":,//错误信息
    "count":,//酒店数量
     "promonote": "每天20:00-02:00,订房30元起",
     "promoid": 1,
      "promoicon": "http://7xip11.com2.z0.glb.qiniucdn.com/frontpage_icon_jinyetejia.png",
     "normalid": -1,
    "promosec":0,             //距离特价活动开始的秒数
    "promosecend":25235， //距离特价活动结束的秒数
    "nextpromosec":71975，      //距离下一次特价活动开始的秒数
    "hotel":
        [{
            "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
	   *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	   *「增」 "highlights":[{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }]
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }],
			     "width": //图片宽度,
			     "height": //图片高度
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }],
			     "width": //图片宽度,
			     "height": //图片高度
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   }],
	   "supplementhotel":[{
	         "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
	   *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	    *「增」 "highlights":[
                  	{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }]
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }],
			     "width": //图片宽度,
			     "height": //图片高度
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   
	   }]

}
```


### 『增』首页快捷入口
***
**业务说明：**
根据 citycode 获取快捷入口信息


**接口url：**
>http://ip:port/ots/recommend/shortcut

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿ cityid|城市编码|是|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|

> API返回json数据示例：

```js
{
    "shortcut": [
        {
            "id": 1,
            "imgurl": "http://7xip11.com2.z0.glb.qiniucdn.com/frontpage_btn_jinyetejia.png",
            "name": "一元秒杀",
            "description": "HOT",
            "url": "http://www.baidu.com",
            "detailid": 8,
            "querytype": 800,
            "createtime": 1443276228000

        },
        {
            "id": 2,
             "imgurl": "http://7xip11.com2.z0.glb.qiniucdn.com/frontpage_btn_jinyetejia.png",
            "name": "评价返现",
            "description": "评价返现",
            "url": "http://www.baidu.com",
            "detailid": 8,
            "querytype": 801,
            "createtime": 1443276228000

        },
        {
           	"id": 3,
             "imgurl": "http://7xip11.com2.z0.glb.qiniucdn.com/frontpage_btn_jinyetejia.png",
            "name": "一省到底",
            "description": "一省到底",
            "url": "http://www.baidu.com",
            "detailid": 8,
            "querytype": 802,
            "createtime": 1443276228000
        },
        {
            "id": 4,
             "imgurl": "http://7xip11.com2.z0.glb.qiniucdn.com/frontpage_btn_jinyetejia.png",
            "name": "车站酒店",
            "description": "车站酒店",
            "url": "http://www.baidu.com",
            "detailid": 8,
            "querytype": 803,
            "createtime": 1443276228000
        }
    ],
    "errmsg": "",
    "errcode": 0
}

## 推荐资源配置
### 获取首页主题房推荐
**业务说明：**
> 根据 cityid 获取 主题房推荐信息

**接口 url：**
> http://ip:port/ots/homepage/themes

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------                                                 
| cityid     	| 城市编码   | 是    |
| userlatitude |用户坐标(纬度)|是|
| userlongitude |用户坐标(经度)|是|   
|pillowlongitude 	| 查询坐标(经度) | 否 | 定位城市必须传, 非定位城市不能传
|pillowlatitude 	| 查询坐标(纬度) | 否 | 定位城市必须传, 非定位城市不能传                                                 
| callmethod   	| 调用来源   | 是   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
| callversion  	| 调用版本   | 是   |
| ip           	| IP地址  | 否     |                                                    
| hardwarecode 	| 硬件编码   | 否       |                                                    
| otsversion   	| OTS版本    | 否       |   


> API返回json返回说明：

```js
{
    "errcode": 0,
    "errmsg": "",
    "promotext": "栏目大标题",
    "promonote": "栏目小标题",
    "promoicon": "栏目图标",
    "hotel":
        [{
            "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
	   *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	     *「增」 "highlights":[
                  	{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
	     *「增」 "themetexts":[
                  	{ 
                        "text": "主题tag",//特点描述
                        "color":"" // 如果颜色设定,则C端使用,否则C端自己控制
                     }
                ],  //酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }]
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
            "themepic":[{//主题酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
	    "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }]
        }
    ]
}
```

> API 数据返回示例

```js
{
    "errcode": 0,
    "errmsg": "",

    "promotext": "情侣酒店 约TA激情",
    "promonote": "冰岛之夜,地中海风情...更多主题风情,等你来挑战",
    "promoicon": "http://xxx.jpg",
            "hotel": [
                {
                    "hotelid": "4705",
                    "hotelname": "重庆帝临酒店",
                    "hotelrulecode": 1002,
                    "online": "T",
                    "hotelvc": "T",
                    "detailaddr": "重庆市沙坪坝区双巷子街6号附11号",
                    "hoteldis": "S 沙坪坝区",
                    "hotelcity": "500000",
                    "hotelprovince": "Z 重庆市",
                    "hotelphone": "023-65348952",
                    "hoteldisc": "酒店地理位置优越，交通方便，经济实惠，期待您的入驻。",
                    "longitude": 106.461941,
                    "latitude": 29.558707,
                    "distance": 2179413.1303076735,
                    "userdistance": 2179413.1303076735,
                    "isnear": "F",
                    "grade": 0,
                    "minprice": 158,
                    "minpmsprice": 488,
                    "ispms": "T",
                    "hotelpicnum": 40,
                    "avlblroomnum": 18,
                    "avlblroomdes": "",
                    "descolor": "32ab18",
                    "ordernummon": "0",
                    "rcntordertimedes": "最近预订1天前",
                    "greetscore": 1080,
                    "repairinfo": "2014年装修",
                    "highlights": [
                        {
                            "name": "wifi",
                            "icon": "http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                            "id": 44
                        }
                    ],
                    "themetexts": [
                        {
                            "text": "主题tag",
                            "color": "66FFBB"
                        },
                        {
                            "text": "主题tag",
                            "color": "66FFBB"
                        }
                    ],
                    "promoinfo": [
                        {
                            "promoprice": "60",
                            "promoid": 1,
                            "promotype": 2
                        },
                        {
                            "promoprice": "90",
                            "promoid": 1,
                            "promotype": 3
                        },
                        {
                            "promoid": 3,
                            "promotype": 21
                        }
                    ],
                    "teambuying": [
                        {
                           "teambuyingname": "",
                            "url": ""
                        }
                    ],
                    "isrecommend": "F",
                    "hotelpic": [
                        {
                            "name": "def",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FlXbAU1AZ_rFNYtdiT6j4N37BylS"
                                }
                            ]
                        },
                        {
                            "name": "lobby",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FgKBX2nbglK2U0cpMXUVGBKVFXTV"
                                }
                            ]
                        },
                        {
                            "name": "mainHousing",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FootNlBX0VNiFABYSdE7mTg-T0oq"
                                }
                            ]
                        }
                    ],
                    "themepic": [
                        {
                            "name": "sb",
			    "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FlXbAU1AZ_rFNYtdiT6j4N37BylS"
                                }
                            ]
                        }
                    ],
                    "hotelfacility": [
                        {
                            "facid": 10,
                            "facname": "叫醒服务"
                        },
                        {
                            "facid": 9,
                            "facname": "棋牌室"
                        }
                    ],
                    "businesszone": [                        
                    ]
                }
	]
   }

```
## 酒店信息接口
### `「修」` 最受欢迎查询 
***
**业务说明：**
> 

**接口url：**	
> http://ip:port/ots/search/popular

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------                                                 
| cityid     	| 城市编码   | 是    |  
| userlatitude |用户坐标(纬度)|是|
| userlongitude |用户坐标(经度)|是|   
|pillowlongitude 	| 查询坐标(经度) | 否 | 定位城市必须传, 非定位城市不能传
|pillowlatitude 	| 查询坐标(纬度) | 否 | 定位城市必须传, 非定位城市不能传                                                    
| callmethod   	| 调用来源   | 是   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
| callversion  	| 调用版本   | 是   |
| ip           	| IP地址  | 否     |                                                    
| hardwarecode 	| 硬件编码   | 否       |                                                    
| otsversion   	| OTS版本    | 否       |   

>API返回json数据示例：

```js
{
    "errcode":,//错误码
    "errmsg":,//错误信息
    "count":,//酒店数量
    "promotext": "栏目大标题",

    "promonote": "栏目小标题",
    "promoicon": "栏目图标",
    "hotel":
        [{
            "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
      *「增」"greetscore":1080,//受欢迎指数
      *「增」"repairinfo":"2014年装修",//装修信息
      *「增」"highlights":[
                  	{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价

            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
                        }]

            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }]
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }]
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   }]

}

```

> API 数据返回示例


```js
  {
		"errcode": "0", 
		"errmsg": "", 
	    "promotext": "人气之星,为你而选",
	    "promonote": "最受欢迎风向标,为你一网打尽",
            "promoicon": "http://7xip11.com2.z0.glb.qiniucdn.com/frontpage_icon_jinyetejia.png",
            "hotel": [
                {
                    "hotelid": "4705",
                    "hotelname": "重庆帝临酒店",
                    "hotelrulecode": 1002,
                    "online": "T",
                    "hotelvc": "T",
                    "detailaddr": "重庆市沙坪坝区双巷子街6号附11号",
                    "hoteldis": "S 沙坪坝区",
                    "hotelcity": "500000",
                    "hotelprovince": "Z 重庆市",
                    "hotelphone": "023-65348952",
                    "hoteldisc": "酒店地理位置优越，交通方便，经济实惠，期待您的入驻。",
                    "longitude": 106.461941,
                    "latitude": 29.558707,
                    "distance": 2179413.1303076735,
                    "userdistance": 2179413.1303076735,
                    "isnear": "F",
                    "grade": 0,
                    "minprice": 158,
                    "minpmsprice": 488,
                    "ispms": "T",
                    "hotelpicnum": 40,
                    "avlblroomnum": 18,
                    "avlblroomdes": "",
                    "descolor": "32ab18",
                    "ordernummon": "0",
                    "rcntordertimedes": "最近预订1天前",
                    "greetscore": 1080,
                    "repairinfo": "2014年装修",
                    "highlights": [
                        {
                            "name": "wifi",
                            "icon": "http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                            "id": 44
                        }
                    ],
                    "teambuying": [
                        {
                           "teambuyingname": "",
                            "url": ""
                        }
                    ],
                    "isrecommend": "F",
                    "hotelpic": [
                        {
                            "name": "def",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FlXbAU1AZ_rFNYtdiT6j4N37BylS"
                                }
                            ]
                        },
                        {
                            "name": "lobby",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FgKBX2nbglK2U0cpMXUVGBKVFXTV"
                                }
                            ]
                        },
                        {
                            "name": "mainHousing",
                            "pic": [
                                {
                                    "url": "https://dn-imke-pro.qbox.me/FootNlBX0VNiFABYSdE7mTg-T0oq"
                                }
                            ]
                        }
                    ],
                    "businesszone": [],
                    "avlblroomnum": 49,
                    "hotelcity": "500000",
                    "numroomtype1": 1,
                    "provcode": "500000",
                    "numroomtype2": 1,
                    "promoprice": "90",
                    "modifytime": 1450044239609,
                    "discode": "500107",
                    "hotelfacility": [
                        {
                            "facid": 44,
                            "facname": "公共区域wifi"
                        },
                        {
                            "facid": 9,
                            "facname": "棋牌室"
                        }
                    ],

                    "businesszone": [                        
                    ]
                    "service": [],
                    "isnewpms": "T",
                    "$mike_price_20151227": 168,
                    "minprice": 90,
                    "$mike_price_20151226": 168,
                    "$mike_price_20151225": 168,
                    "$mike_price_20151224": 168,
                    "$mike_price_20151229": 168,
                    "hoteltype": 1,
                    "$mike_price_20151228": 168
                }
	]
}

```


## 搜索
### 『增』获取用户附近车站
***
**业务说明：**
根据用户坐标获取距离用户附近的车站信息。


**接口url：**
>http://ip:port/ots/search/nearstation

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿citycode|城市编码|是|
|userlongitude 	| 用户坐标(经度) | 否 | 用户的经度 
|userlatitude 	| 用户坐标(纬度) | 否 | 用户的纬度 
|ptype| 位置类型| 是|  0附近；1商圈；2机场车站；3地铁路线；4行政区；5景点；6医院；7高校；8酒店；9地址；(本接口请传2)
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|


> API返回json数据说明：

```js
{
    "datas": [
        {
            "id": 684, //位置 id
            "type": "2",//位置类型
            "tname": "机场车站",//位置类型名称
            "name": "上海南站",//位置名称
            "coordinates": "[[121.429489,31.153303]]"//位置坐标
        }
    ],
    "$times$": "4299ms",
    "success": true
}
```

> API返回json数据示例：

```js
{
    "datas": [
        {
            "id": 684,
            "type": "2",
            "tname": "机场车站",
            "name": "上海南站",
            "coordinates": "[[121.429489,31.153303]]"
        }
    ],
    "$times$": "4299ms",
    "success": true
}
```


## 一元秒杀活动
### 参与人接口
***
**业务说明：**
根据citycode和 promoid 获取活动参与人信息。


**接口url：**
>http://ip:port/ots/promo/attend

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿citycode|城市编码|是|
|promoid|活动 id| 6-一元秒杀|
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|


> API返回json数据说明：

```
{
	totalview：4321
}
```

### 一元秒杀列表接口
***
**业务说明：**
根据citycode 和用户坐标获取一元秒杀列表。


**接口url：**
>http://ip:port/ots/promo/onedollarlist

**请求参数：**

|    字段        |         名称        | 是否必须 | 说明|
--------------- | ------------------- | -------| ----------
|﻿citycode|城市编码|是|
|userlongitude 	| 用户坐标(经度) | 否 | 用户的经度 
|userlatitude 	| 用户坐标(纬度) | 否 | 用户的纬度 
|callmethod|调用来源|否|1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) 
|callversion|调用版本|否|
|ip|IP地址|否|
|hardwarecode|硬件编码|否|
|otsversion|OTS版本|否|


> API返回json数据说明：

```js
{
    "errcode":,//错误码
    "errmsg":,//错误信息
    "count":,//酒店数量
    "promosec":,// 秒
    "promosecend":,// 距离结束时间（s）
    "nextpromosec":,// 距离下一段结束时间（s）
    "promonote":,//促销的文字,已有...人关注
    "hotel":
        [{
            "hotelid":,//酒店id
            "hotelname":,//酒店名称
            "hotelrulecode":,//酒店切客规则类型码（1001规则A；1002规则B）
            "online":,//是否在线(T/F)
            "hotelvc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
            "detailaddr":,//酒店地址
            "hoteldis":,//酒店所属区县
            "hotelcity":,//酒店所属城市
            "hotelprovince":,//酒店所属省份
            "hotelphone":,//联系电话
            "hoteldisc":,//酒店简介
            "longitude":, //酒店坐标(经度)
            "latitude":,//酒店坐标(纬度)
            "distance":,//酒店距离xxx米，该距离表示屏幕坐标到酒店坐标的距离
            "userdistance":,//距您xxx米，该距离表示用户坐标到屏幕坐标的距离
            "isnear":,//是否最近酒店，distance值最小的酒店为T,其他为F
            "grade":, //酒店评分
            "scorecount":, //评价次数
            "minprice":,//最低价格
            "minpmsprice",//最低价格对应房型的门市价
            "ispms":,//是否签约（T/F）
            "hotelpicnum":,//酒店图片总数
            "avlblroomnum":,//可订房间数
            "avlblroomdes":,//可订房描述
            "descolor":,   //描述字体颜色  （状态: ">3间房间"    绿色   32ab18 状态："<=仅剩3间"   红色   fb4b40 状态：满房   灰色    989898）
            "ordernummon":,//月订单数
            "rcntordertimedes":, //最近订单时间描述
            "distancestr": //距离描述
            "isteambuying":,//是否团购（T/F）
	   *「增」"greetscore":1080,//受欢迎指数
	   *「增」 "repairinfo":"2014年装修",//装修信息
	   *「增」 "highlights":[{ 
                        "name": "wifi",//特点描述
                        "icon":"http://7xip11.com2.z0.glb.qiniucdn.com/view_theme_roomtype_detail_icon_zhuozi.png",
                         "id":44
                     }
                ],  //酒店特点
            "isonpromo":	// 是否特价，是否特价, 0非，>=1特价
            "promoinfo":[{ // 特价活动信息
                        "promoid":// 特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）
                        "promotype": //特价活动类型 id
                        "promoprice": // 特价活动价格
            
                        }]
            "teambuying":[{//团购信息
                            "teambuyingname":,//团购名称
                            "url"://团购地址
                         }],
            "isrecommend"://是否推荐（T/F）
            "hotelpic":[{//酒店图片
                            "name":,//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                  }],
			     "width": //图片宽度,
			     "height": //图片高度
                       }],
            "hotelfacility":[{//酒店设施
                                "facid":,//设施id
                                "facname"://设施名称
                            }],
            //交通
            "businesszone":[{//商圈
                            "businesszonename"://商圈名称
                            }],
            "roomtype":[{//房型
                            "roomtypeid":,//房型id
                            "roomtypename":,//房型名称
                            "roomtypeprice":,//房型价格
                            "roomtypepmsprice":,//门市价格
                            "roomtypevc":,//是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
                            "maxarea":,//最大面积
                            "minarea":,//最小面积
                            "bednum":,//床数量（1：单床房，2：双床房，大于2：其他房）
                            "roomnum":,//房间数量
                            "count":0, //房间内床数             "beds":[{//床型信息
                        "bedtypename":,//床型(双人床，单人床)
                        "bedlength": //尺寸(1.5米，1.8米)
                    ]},
            "roomtypepic":[{//房型图片信息
		                      "name":'',//图片名称
                            "pic":[{//图片集合
                                    "url"://图片地址
                                 }],
			     "width": //图片宽度,
			     "height": //图片高度
                          }]
            "roomtypefacility":[{//房间设施
                                "roomtypefacid"://设施id
                                "roomtypefacname"://设施名称
                                }]
        
            "service":[{//酒店服务
                                "serviceid":,//服务id
                                "servicename":,//服务名称
                     }]
            "iscashback":  //是否返现（T/F）
	   }]
	}

```
### 1元特价列表



## 校园特价活动查询
### `「增」` 获取校园特价酒店列表
**业务说明：**
> 获取校园特价酒店列表

**接口 url：**
> http://ip:port/ots/promo/college

**请求参数：**

    字段        |         名称         | 是否必须 | 说明|
--------------- | ------------------- | -------| -----------                                                 
| cityid 	| 城市编码 	| 是 	|                                                    
| callmethod   	| 调用来源   | 是   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) |
| callversion  	| 调用版本   | 是   |
| ip           	| IP地址  | 否     |                                                    
| hardwarecode 	| 硬件编码   | 否       |                                                    
| otsversion   	| OTS版本    | 否       |   


> API返回json返回说明：

```js
{
    "success": true,
    "errorcode": 0,
    "errormsg": "",

    "hotel":
    [{
	"hotelid":,//酒店id [类型String]
        "promoprice": ,// 特价活动价格 [类型BigDecimal]
        "minpmsprice": //最低价格对应房型的门市价 [类型BigDecimal]
    }]
}
``` 

> API返回json返回说明：

```js
{
    "success": true,
    "errorcode": 0,
    "errormsg": "",

    "hotel":
    [{
	"hotelid": "2778",
        "promoprice": 20,
        "minpmsprice": 300
    }]
}

</article>

<link href="asset/css/zTreeStyle.css" media="all" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="asset/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="asset/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="asset/js/ztree_toc.js"></script>
<script type="text/javascript" src="asset/js/mdtree.js"></script>













