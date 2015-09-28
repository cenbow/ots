package com.mk.ots.activity.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mk.framework.model.Page;
import com.mk.ots.activity.dao.IBActivityDao;
import com.mk.ots.activity.model.BActivity;
import com.mk.ots.activity.service.IBActivityService;
import com.mk.ots.ticket.model.UTicket;
import com.mk.ots.ticket.service.ITicketService;

@Service
public class BActivityServiceImpl implements IBActivityService {
	
	@Autowired
	private IBActivityDao ibActivityDao;
	
	@Autowired
	private ITicketService iTicketService;
	
	@Override
	public Page<BActivity> query(String memberid,String hotelid, String activeid, String isactivedetil,
			String isacticepic, String startdateday, String enddateday,
			String begintime, String endtime, Integer page, String limit) {
		Page<BActivity> pageEntity = new Page<BActivity>(10);
		pageEntity.setPageNo(page);
		BActivity entity = new BActivity();
		if(!Strings.isNullOrEmpty(memberid)){
			entity.setId(Long.parseLong(memberid));
		}
		pageEntity = ibActivityDao.find(pageEntity, entity);
		return pageEntity;
	}
	
	@Override
	public List<Map<String, Object>> queryinfo(Long mid, List<String> actList) {
		List<Map<String, Object>> rtnList = Lists.newArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		for(String actid : actList){
			Long activeid = Long.parseLong(actid);
			List<UTicket> ticketList =  iTicketService.queryTicketByMidAndActvie(mid, activeid);
			
			Map<String, Object> param = Maps.newHashMap();
			param.put("activeid", activeid);
			param.put("isgetticket", ticketList!=null && ticketList.size()>0 ? "T":"F");
			param.put("isused", "F");
			param.put("gettime", "");
			if(ticketList!=null && ticketList.size()>0){
				boolean isused = false;
				Date gettime = null;
				for(UTicket temp : ticketList){
					if(temp.getStatus()==1){
						isused = true;
					}
					if(gettime==null){
						gettime = temp.getPromotiontime();
					} else if(temp.getPromotiontime().before(gettime)){
						gettime = temp.getPromotiontime();
					}
				}
				param.put("isused", isused ? "T":"F");
				param.put("gettime", sdf.format(gettime));
			}
			rtnList.add(param);
		}
		return rtnList;
	}
	

	public BActivity findById(Long id) {
		return ibActivityDao.findById(id);
	}

}
