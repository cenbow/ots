package com.mk.pms.manager;

import com.mk.framework.AppUtils;
import com.mk.ots.hotel.bean.EHotel;
import com.mk.ots.hotel.dao.EHotelDAO;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.pms.myenum.PmsErrorEnum;

/**
 *
 * @author shellingford
 * @version 2015年1月13日
 */
public class CacheManager {

	private final static CacheManager instance = new CacheManager();
	private final String base = "pms_";

	public String hotelIDToPms(Long hotelId) {
		EHotelDAO eHotelDAO = AppUtils.getBean(EHotelDAO.class);
		// String key = base + "hotelToPms_" + hotelId;
		String pms = (String) CacheManager.getManager().get(this.base + "hotelToPms_", hotelId);
		if (pms == null) {
			EHotel hotel = eHotelDAO.findEHotelByid(hotelId);
			pms = hotel.get("Pms");
			if (pms == null) {
				throw PmsErrorEnum.noPmsHotel.getException();
			}
			CacheManager.getManager().setExpires(this.base + "hotelToPms_", hotelId.toString(), pms, 30);
		}
		return pms;
	}

	private CacheManager() {
	}

	public static CacheManager getInstance() {
		return CacheManager.instance;
	}

	private static OtsCacheManager getManager() {
		return AppUtils.getBean(OtsCacheManager.class);
	}
}
