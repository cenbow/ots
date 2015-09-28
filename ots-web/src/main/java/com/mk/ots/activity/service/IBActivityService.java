package com.mk.ots.activity.service;

import java.util.List;
import java.util.Map;

import com.mk.framework.model.Page;
import com.mk.ots.activity.model.BActivity;

/**
 * @author nolan
 *
 */
public interface IBActivityService {

	/**
	 * @param hotelid
	 * @param activeid
	 * @param isactivedetil
	 * @param isacticepic
	 * @param startdateday
	 * @param enddateday
	 * @param begintime
	 * @param endtime
	 * @param page
	 * @param limit
	 * @return
	 */
	public abstract Page<BActivity> query(String memberid,String hotelid, String activeid, String isactivedetil,
			String isacticepic, String startdateday, String enddateday, String begintime, String endtime,
			Integer page, String limit);
	
	/**
	 * @param mid
	 * @param actList
	 * @return
	 */
	public List<Map<String, Object>> queryinfo(Long mid, List<String> actList);
	
	
	public BActivity findById(Long id) ;
}
