package com.mk.ots.rule.dao;

import java.util.Map;
import org.springframework.stereotype.Component;
import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.rule.model.BAreaRuleDetail;

@Component
public class BAreaRuleDetailDAO extends MyBatisDaoImpl<BAreaRuleDetail, Long> {

	/**
	 * 获取规则value
	 * @param key
	 * @param rulecode
	 * @return
	 */
	public BAreaRuleDetail queryRuleValue(String key,Integer rulecode) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("rulecode", rulecode);
		param.put("rulekey", key);
		return this.findOne("queryRuleValue", param); 
	}
}
