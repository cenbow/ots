package com.mk.ots.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.mk.framework.util.CommonUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mk.framework.AppUtils;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.UrlUtils;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.manager.HotelPMSManager;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.utils.Tools;
import com.mk.pms.order.bean.SynedCustomerBean;
import com.mk.pms.order.service.NewPmsOrderService;
import com.mk.pms.order.service.PmsOrderService;

import cn.com.winhoo.pms.webout.service.bean.CustomerResult;
import cn.com.winhoo.pms.webout.service.bean.ReturnObject;

/**
 * 每2分钟执行一次，从redis队列中取出已处理客单数据，再查出未处理客单，然后进行反查
 * @author jianghe
 *
 */
public class DealUnsynedOrderJob  extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(DealUnsynedOrderJob.class);
	private PmsOrderService pmsOrderService = AppUtils.getBean(PmsOrderService.class);
	private OtsCacheManager cacheManager = AppUtils.getBean(OtsCacheManager.class);
	private NewPmsOrderService newPmsOrderService = AppUtils.getBean(NewPmsOrderService.class);
	private HotelService hotelService = AppUtils.getBean(HotelService.class);
	private Gson gson = new Gson();
	@Autowired
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
        if(cacheManager.isExistKey("dealOrderJobKey")){
        	logger.info("存在dealOrderJobKey锁");
        	return;
        }
		logger.info("DealUnsynedOrderJob::start");
		boolean runflag = true;
		while(runflag){
			try {
				String strJson = null;
				if((strJson = popFromRedis())==null){
					//runflag = false;
					break;
				}
				SynedCustomerBean synedCustomerBean = gson.fromJson(strJson, SynedCustomerBean.class);
				Long hotelid = synedCustomerBean.getHotelId();
				String customernos = synedCustomerBean.getCustomernos();
				List<PmsRoomOrder> oldRoomOrderlist = pmsOrderService.findUnSynedPmsRoomOrder(hotelid,customernos);
				if(oldRoomOrderlist.size()>0){
					List<String> pmsCustomNos=new ArrayList<>();
					for (PmsRoomOrder temp : oldRoomOrderlist) {
						if(pmsCustomNos.contains(temp.get("PmsRoomOrderNo"))==false){
							pmsCustomNos.add(String.valueOf(temp.get("PmsRoomOrderNo")));
						}
					}
					THotel hotel = hotelService.readonlyTHotel(hotelid);
					// pms2.0
					if ("T".equals(hotel.getStr("isNewPms"))) {
						JSONObject pmsCustomNosJson = new JSONObject();
						pmsCustomNosJson.put("hotelid", hotel.getStr("pms"));
						pmsCustomNosJson.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
						pmsCustomNosJson.put("function", "selectcustomerno");
						pmsCustomNosJson.put("timestamp ", DateUtils.formatDateTime(new Date()));
						StringBuffer ids = new StringBuffer();
						for (String id : pmsCustomNos) {
							ids.append(id).append(",");
						}
						ids.setLength(ids.length() - 1);
						pmsCustomNosJson.put("customerid", ids.toString());

						String result = null;
						Transaction t = Cat.newTransaction("PmsHttpsPost", UrlUtils.getUrl("newpms.url") + "/selectcustomerno");
						try {
							logger.info("Pms2.0DealUnsynedOrderJob::处理全量更新未同步客单传参{}{}", hotelid,pmsCustomNosJson.toString());
							result = PayTools.dopostjson(UrlUtils.getUrl("newpms.url") + "/selectcustomerno", pmsCustomNosJson.toJSONString());
							logger.info("Pms2.0DealUnsynedOrderJob::处理全量更新未同步客单数据结果{}{}", hotelid,result);
							Cat.logEvent("Pms/selectcustomerno", CommonUtils.toStr(hotelid), Event.SUCCESS, pmsCustomNosJson.toJSONString());
							t.setStatus(Transaction.SUCCESS);
						} catch (Exception e) {
							t.setStatus(e);
							this.logger.error("Pms/selectcustomerno error.", e);
							throw MyErrorEnum.errorParm.getMyException(e.getMessage());
						}finally {
							t.complete();
						}

						JSONObject returnObject = JSONObject.parseObject(result);
						
						if (returnObject.getBooleanValue("success")) {
							JSONArray customerno = returnObject.getJSONArray("customerno");
							JSONObject param = new JSONObject();
							param.put("hotelid", hotelid);
							param.put("customerno", customerno);
							Map map = newPmsOrderService.saveCustomerNo(param);
						} else {
							logger.error("Pms2.0DealUnsynedOrderJob::err::{}", returnObject.getString("errormsg"));
						}
					} else {
						// pms1.0
						logger.info("DealUnsynedOrderJob::处理全量更新未同步客单传参{}{}", hotelid,pmsCustomNos.toString());
						ReturnObject<List<CustomerResult>> ro = HotelPMSManager.getInstance().getService().selectCustomerno(hotelid, pmsCustomNos);
						if (ro != null && ro.getIsError() == false) {
							logger.info("DealUnsynedOrderJob::处理全量更新未同步客单,返回size{}", ro.getValue().size());
							if(pmsCustomNos.size()>1 && ro.getValue().size()<=1){//判断是否升级ota
								logger.error("ota为旧版本:{}",hotelid);
							}
							String theResult = pmsOrderService.saveResult(hotelid, ro.getValue());
							logger.info("DealUnsynedOrderJob::处理全量更新未同步客单数据结果{}{}", hotelid,theResult);
						} else {
							logger.error("DealUnsynedOrderJob::处理全量更新未同步客单数据err::{}", ro.getErrorMessage());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("DealUnsynedOrderJob::error{}",e.getMessage());
				//runflag = false;
				//break;
			}
		}
		logger.info("DealUnsynedOrderJob::end");
	}
	
	

    public String popFromRedis() {
    	String strJson = cacheManager.rpop("synedCustomersQuene");
    	if(strJson==null){
    		return null;
    	}
    	return strJson;
	}
}
