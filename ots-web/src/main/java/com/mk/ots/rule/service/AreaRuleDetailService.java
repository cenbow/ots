package com.mk.ots.rule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.common.enums.RuleEnum;
import com.mk.ots.rule.dao.BAreaRuleDetailDAO;
import com.mk.ots.rule.model.BAreaRuleDetail;

@Service
public class AreaRuleDetailService {
	final Logger logger = LoggerFactory.getLogger(AreaRuleDetailService.class);

	@Autowired
	private BAreaRuleDetailDAO bAreaRuleDetailDAO;
	
	/**
	 * 根据key和规则code查询对应的value
	 * @param key 目前对应预付到付等
	 * @param rulecode 从枚举中得到
	 * @return
	 */
	public BAreaRuleDetail queryRuleValue(String key,RuleEnum rule) {
		return bAreaRuleDetailDAO.queryRuleValue(key, rule.getId());
	}
}
