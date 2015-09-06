package com.mk.ots.ticket.service.parse;


/**
 * 
 * 立减优惠券保存格式
 * 
 * <root> 
 * <!-- 使用时间策略 主策略--> 
 * <times> 
 * <time crontab="">
 *  <!-- 额外添加 --> 
 *  <add crontab=""/> 
 * <!-- 排除时间 --> 
 * <del crontab=""/> 
 * </time> 
 * </times> 
 * <!-- 包含酒店排除酒店--> 
 * <hotel included="1,2,3" excluded="1,2,3" /> 
 * <!-- 包含房型 排除房型 -->
 * <roomType included="1,2,3" excluded="1,2,3" />
 *  <!-- 包含区县 排除区县 --> 
 *  <dis included="1,2,3" excluded="1,2,3" /> 
 * <!-- 包含城市 排除城市 --> 
 * <city included="1,2,3" excluded="1,2,3" />
 *  <!-- 最小间夜--> 
 *  <minDay value=""/>
 *   <!--最少价格--> 
 *   <minPrice value=""/> 
 * <!-- 减免费用--> 
 * <subprice value="100.00"/>
 *  </root>
 * 
 * @author shellingford
 * @version 2015年1月29日
 */
public class MinusTicket extends ITicketParse {
}
