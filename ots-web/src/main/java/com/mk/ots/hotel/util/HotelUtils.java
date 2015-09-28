package com.mk.ots.hotel.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;


/**
 * 酒店接口工具类.
 * @author chuaiqing.
 *
 */
public final class HotelUtils {
    
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(HotelUtils.class);
    
    /**
     * 返回酒店是否有可售房间
     * @param hotelid
     * @return
     */
    public static String getHotelvc(String hotelid, String startDate, String endDate) {
        String result = Constant.STR_TRUE;
        if (StringUtils.isBlank(hotelid)) {
            return Constant.STR_FALSE;
        }
        if (1==1) {
            return Constant.STR_TRUE;
        }
        String cityid = Constant.STR_CITYID_SHANGHAI;
        try {
            String strCurDay = DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME);
            String whereStarttime = "";
            if (!StringUtils.isBlank(startDate)) {
                whereStarttime = " time >= '" + startDate + "'";
            } else {
                whereStarttime = " time >= '" + strCurDay + "'";
            }
            
            String whereEndtime = "";
            if (!StringUtils.isBlank(endDate)) {
                whereEndtime = " time <= '" + endDate + "'";
            } else {
                whereEndtime = " time <= '" + strCurDay + "'";
            }
            String sql = "select count(id) as counts from b_roomtemp_" + cityid + " where hotelid='" + hotelid + "'";
            
            String whereTime = "";
            if (!StringUtils.isBlank(whereStarttime)) {
                whereTime = whereStarttime;
            }
            if (!StringUtils.isBlank(whereEndtime)) {
                if (StringUtils.isBlank(whereTime)) {
                    whereTime = whereEndtime;
                } else {
                    whereTime += " and " + whereEndtime;
                }
            }
            if (!StringUtils.isBlank(whereTime)) {
                sql += " and (" + whereTime + ")";
            }
            logger.info("getHotelvc method sql is: \n {} ", sql);
            long counts = Db.findFirst(sql).getLong("counts");
            logger.info("酒店占用记录数：{} ", counts);
            // counts > 0，说明已被占用，不可用
            result = counts > 0 ? Constant.STR_FALSE : Constant.STR_TRUE;
            logger.info("--================================== 酒店是否有可售房查询结果：{} ==================================-- ", result);
        } catch (Exception e) {
            result = Constant.STR_FALSE;
            logger.error("getHotelvc method error:\n" + e.getMessage());
        }
        return result;
    }
}
