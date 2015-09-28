package com.mk.ots.restful.output;

import java.io.Serializable;

/**
 * search/positions 查询位置区域返回实体类.
 * 
 * @author LYN.
 *
 */
public class SearchPositionsDistanceRespEntity implements Serializable {
	
		private String name;
		private Long value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getValue() {
			return value;
		}

		public void setValue(Long value) {
			this.value = value;
		}


}
