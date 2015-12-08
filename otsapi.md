<div style='width:25%; magin-top:30px'>
	<ul id="tree" class="ztree" style='width:100%;float:right'>
	</ul>
</div>
<article class='md-body' style="margin: 20px;">
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
### 酒店综合信息查询
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

### 特价房信息查询
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



## 特价活动
### 获取特价活动价格区间
**业务说明：**
> 根据 promoid 获取特价活动价格区间

**接口 url：**
> http://ip:port/ots/promo/queryrange

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------                                                 
| promoid     	| 特价活动 id   | 是    |1. 今夜特价 <br> 2.今日特价<br> 3. 主题酒店 <br> 6. 一元秒杀                                                    
| callmethod   	| 调用来源   | 是   | 1-crs；<br>2-web；<br>3-wechat；<br>4-app(ios)；<br>5-app(Android) 
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
	"maxprice": 100        // 特价活动最高价
	"promosec":0,		      //距离特价活动开始的秒数
	"promosecend":25235， //距离特价活动结束的秒数
	"nextpromosec":71975	  //距离下一次特价活动开始的秒数
	
}
``` 

### 特价房信息查询
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
	    "greetscore":,//受欢迎指数
	    "highlights":,[{ 
                        "text": 特点描述
                        }]  //酒店特点
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
	    "greetscore":,//受欢迎指数
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

### 主题房信息查询
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
	    "greetscore":,//受欢迎指数
	    "highlights":,[{ 
                        "text": 特点描述
                        }]  //酒店特点
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
	    "greetscore":,//受欢迎指数
	    "highlights":,[{ 
                        "text": 特点描述
                        }]  //酒店特点
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

### 查询首屏推荐信息
**业务说明：**
> 根据 cityid 获取首屏推荐信息

**接口 url：**
> http://ip:port/ots/recommend/queryloadinglist

**请求参数：**

    字段        |         名称         | 是否必须 | 说明
--------------- | ------------------- | -------| -----------                                                
| citycode     	| 城市编码   | 是    |                                                    
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




</article>

<link href="asset/css/zTreeStyle.css" media="all" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="asset/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="asset/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="asset/js/ztree_toc.js"></script>
<script type="text/javascript" src="asset/js/mdtree.js"></script>













