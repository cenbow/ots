package com.mk.ots.ticket.service.parse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.elasticsearch.common.base.Strings;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import cn.com.winhoo.mikeweb.bean.TicketXmlDto;

import com.mk.framework.AppUtils;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.XMLUtils;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.ticket.dao.UTicketDao;
import com.mk.ots.ticket.model.UTicket;

/**
 * 
 * 切客优惠码
 * 
 * <root> 
 * <!-- 减免费用--> 
 * <subprice value="100.00"/>
 * <offlinesubprice value="100.0"/>
 *  </root>
 * 
 * @author shellingford
 * @version 2015年1月29日
 */
public class QieKeTicket extends ITicketParse{
}
