package com.mk.ots.home.util;

import java.math.BigDecimal;
import java.util.Map;

public class ValueUtil {

	public static int getInt(Map data, String key){
		if (data.containsKey(key)) {
			try {
				return (int)data.get(key);
			} catch (Exception e) {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public static long getLong(Map data, String key){
		if (data.containsKey(key)) {
			try {
				return (long) data.get(key);
			} catch (Exception e) {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public static float getFloat(Map data, String key){
		if (data.containsKey(key)) {
			try {
				return (float) data.get(key);
			} catch (Exception e) {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public static BigDecimal getBigDecimal(Map data, String key){
		if (data.containsKey(key)) {
			try {
				BigDecimal bd = (BigDecimal) data.get(key);
				bd = bd == null ? BigDecimal.ZERO : bd;
				return bd;
			} catch (Exception e) {
				return BigDecimal.ZERO;
			}
		} else {
			return BigDecimal.ZERO;
		}
	}

}
