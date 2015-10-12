package com.mybatis.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BStrategyPriceExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public BStrategyPriceExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Long value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Long value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Long value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Long value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Long value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Long value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Long> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Long> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Long value1, Long value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Long value1, Long value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andValueIsNull() {
            addCriterion("value is null");
            return (Criteria) this;
        }

        public Criteria andValueIsNotNull() {
            addCriterion("value is not null");
            return (Criteria) this;
        }

        public Criteria andValueEqualTo(BigDecimal value) {
            addCriterion("value =", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotEqualTo(BigDecimal value) {
            addCriterion("value <>", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueGreaterThan(BigDecimal value) {
            addCriterion("value >", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("value >=", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueLessThan(BigDecimal value) {
            addCriterion("value <", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueLessThanOrEqualTo(BigDecimal value) {
            addCriterion("value <=", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueIn(List<BigDecimal> values) {
            addCriterion("value in", values, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotIn(List<BigDecimal> values) {
            addCriterion("value not in", values, "value");
            return (Criteria) this;
        }

        public Criteria andValueBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("value between", value1, value2, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("value not between", value1, value2, "value");
            return (Criteria) this;
        }

        public Criteria andStpriceIsNull() {
            addCriterion("stprice is null");
            return (Criteria) this;
        }

        public Criteria andStpriceIsNotNull() {
            addCriterion("stprice is not null");
            return (Criteria) this;
        }

        public Criteria andStpriceEqualTo(BigDecimal value) {
            addCriterion("stprice =", value, "stprice");
            return (Criteria) this;
        }

        public Criteria andStpriceNotEqualTo(BigDecimal value) {
            addCriterion("stprice <>", value, "stprice");
            return (Criteria) this;
        }

        public Criteria andStpriceGreaterThan(BigDecimal value) {
            addCriterion("stprice >", value, "stprice");
            return (Criteria) this;
        }

        public Criteria andStpriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("stprice >=", value, "stprice");
            return (Criteria) this;
        }

        public Criteria andStpriceLessThan(BigDecimal value) {
            addCriterion("stprice <", value, "stprice");
            return (Criteria) this;
        }

        public Criteria andStpriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("stprice <=", value, "stprice");
            return (Criteria) this;
        }

        public Criteria andStpriceIn(List<BigDecimal> values) {
            addCriterion("stprice in", values, "stprice");
            return (Criteria) this;
        }

        public Criteria andStpriceNotIn(List<BigDecimal> values) {
            addCriterion("stprice not in", values, "stprice");
            return (Criteria) this;
        }

        public Criteria andStpriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stprice between", value1, value2, "stprice");
            return (Criteria) this;
        }

        public Criteria andStpriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stprice not between", value1, value2, "stprice");
            return (Criteria) this;
        }

        public Criteria andRuleareaIsNull() {
            addCriterion("rulearea is null");
            return (Criteria) this;
        }

        public Criteria andRuleareaIsNotNull() {
            addCriterion("rulearea is not null");
            return (Criteria) this;
        }

        public Criteria andRuleareaEqualTo(Long value) {
            addCriterion("rulearea =", value, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRuleareaNotEqualTo(Long value) {
            addCriterion("rulearea <>", value, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRuleareaGreaterThan(Long value) {
            addCriterion("rulearea >", value, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRuleareaGreaterThanOrEqualTo(Long value) {
            addCriterion("rulearea >=", value, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRuleareaLessThan(Long value) {
            addCriterion("rulearea <", value, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRuleareaLessThanOrEqualTo(Long value) {
            addCriterion("rulearea <=", value, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRuleareaIn(List<Long> values) {
            addCriterion("rulearea in", values, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRuleareaNotIn(List<Long> values) {
            addCriterion("rulearea not in", values, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRuleareaBetween(Long value1, Long value2) {
            addCriterion("rulearea between", value1, value2, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRuleareaNotBetween(Long value1, Long value2) {
            addCriterion("rulearea not between", value1, value2, "rulearea");
            return (Criteria) this;
        }

        public Criteria andRulehotelIsNull() {
            addCriterion("rulehotel is null");
            return (Criteria) this;
        }

        public Criteria andRulehotelIsNotNull() {
            addCriterion("rulehotel is not null");
            return (Criteria) this;
        }

        public Criteria andRulehotelEqualTo(Long value) {
            addCriterion("rulehotel =", value, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRulehotelNotEqualTo(Long value) {
            addCriterion("rulehotel <>", value, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRulehotelGreaterThan(Long value) {
            addCriterion("rulehotel >", value, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRulehotelGreaterThanOrEqualTo(Long value) {
            addCriterion("rulehotel >=", value, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRulehotelLessThan(Long value) {
            addCriterion("rulehotel <", value, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRulehotelLessThanOrEqualTo(Long value) {
            addCriterion("rulehotel <=", value, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRulehotelIn(List<Long> values) {
            addCriterion("rulehotel in", values, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRulehotelNotIn(List<Long> values) {
            addCriterion("rulehotel not in", values, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRulehotelBetween(Long value1, Long value2) {
            addCriterion("rulehotel between", value1, value2, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRulehotelNotBetween(Long value1, Long value2) {
            addCriterion("rulehotel not between", value1, value2, "rulehotel");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeIsNull() {
            addCriterion("ruleroomtype is null");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeIsNotNull() {
            addCriterion("ruleroomtype is not null");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeEqualTo(Long value) {
            addCriterion("ruleroomtype =", value, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeNotEqualTo(Long value) {
            addCriterion("ruleroomtype <>", value, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeGreaterThan(Long value) {
            addCriterion("ruleroomtype >", value, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeGreaterThanOrEqualTo(Long value) {
            addCriterion("ruleroomtype >=", value, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeLessThan(Long value) {
            addCriterion("ruleroomtype <", value, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeLessThanOrEqualTo(Long value) {
            addCriterion("ruleroomtype <=", value, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeIn(List<Long> values) {
            addCriterion("ruleroomtype in", values, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeNotIn(List<Long> values) {
            addCriterion("ruleroomtype not in", values, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeBetween(Long value1, Long value2) {
            addCriterion("ruleroomtype between", value1, value2, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRuleroomtypeNotBetween(Long value1, Long value2) {
            addCriterion("ruleroomtype not between", value1, value2, "ruleroomtype");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeIsNull() {
            addCriterion("rulebegintime is null");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeIsNotNull() {
            addCriterion("rulebegintime is not null");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeEqualTo(Date value) {
            addCriterion("rulebegintime =", value, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeNotEqualTo(Date value) {
            addCriterion("rulebegintime <>", value, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeGreaterThan(Date value) {
            addCriterion("rulebegintime >", value, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeGreaterThanOrEqualTo(Date value) {
            addCriterion("rulebegintime >=", value, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeLessThan(Date value) {
            addCriterion("rulebegintime <", value, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeLessThanOrEqualTo(Date value) {
            addCriterion("rulebegintime <=", value, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeIn(List<Date> values) {
            addCriterion("rulebegintime in", values, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeNotIn(List<Date> values) {
            addCriterion("rulebegintime not in", values, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeBetween(Date value1, Date value2) {
            addCriterion("rulebegintime between", value1, value2, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRulebegintimeNotBetween(Date value1, Date value2) {
            addCriterion("rulebegintime not between", value1, value2, "rulebegintime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeIsNull() {
            addCriterion("ruleendtime is null");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeIsNotNull() {
            addCriterion("ruleendtime is not null");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeEqualTo(Date value) {
            addCriterion("ruleendtime =", value, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeNotEqualTo(Date value) {
            addCriterion("ruleendtime <>", value, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeGreaterThan(Date value) {
            addCriterion("ruleendtime >", value, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeGreaterThanOrEqualTo(Date value) {
            addCriterion("ruleendtime >=", value, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeLessThan(Date value) {
            addCriterion("ruleendtime <", value, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeLessThanOrEqualTo(Date value) {
            addCriterion("ruleendtime <=", value, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeIn(List<Date> values) {
            addCriterion("ruleendtime in", values, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeNotIn(List<Date> values) {
            addCriterion("ruleendtime not in", values, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeBetween(Date value1, Date value2) {
            addCriterion("ruleendtime between", value1, value2, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleendtimeNotBetween(Date value1, Date value2) {
            addCriterion("ruleendtime not between", value1, value2, "ruleendtime");
            return (Criteria) this;
        }

        public Criteria andRuleroomIsNull() {
            addCriterion("ruleroom is null");
            return (Criteria) this;
        }

        public Criteria andRuleroomIsNotNull() {
            addCriterion("ruleroom is not null");
            return (Criteria) this;
        }

        public Criteria andRuleroomEqualTo(String value) {
            addCriterion("ruleroom =", value, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomNotEqualTo(String value) {
            addCriterion("ruleroom <>", value, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomGreaterThan(String value) {
            addCriterion("ruleroom >", value, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomGreaterThanOrEqualTo(String value) {
            addCriterion("ruleroom >=", value, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomLessThan(String value) {
            addCriterion("ruleroom <", value, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomLessThanOrEqualTo(String value) {
            addCriterion("ruleroom <=", value, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomLike(String value) {
            addCriterion("ruleroom like", value, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomNotLike(String value) {
            addCriterion("ruleroom not like", value, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomIn(List<String> values) {
            addCriterion("ruleroom in", values, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomNotIn(List<String> values) {
            addCriterion("ruleroom not in", values, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomBetween(String value1, String value2) {
            addCriterion("ruleroom between", value1, value2, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andRuleroomNotBetween(String value1, String value2) {
            addCriterion("ruleroom not between", value1, value2, "ruleroom");
            return (Criteria) this;
        }

        public Criteria andEnableIsNull() {
            addCriterion("enable is null");
            return (Criteria) this;
        }

        public Criteria andEnableIsNotNull() {
            addCriterion("enable is not null");
            return (Criteria) this;
        }

        public Criteria andEnableEqualTo(String value) {
            addCriterion("enable =", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotEqualTo(String value) {
            addCriterion("enable <>", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThan(String value) {
            addCriterion("enable >", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThanOrEqualTo(String value) {
            addCriterion("enable >=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThan(String value) {
            addCriterion("enable <", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThanOrEqualTo(String value) {
            addCriterion("enable <=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLike(String value) {
            addCriterion("enable like", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotLike(String value) {
            addCriterion("enable not like", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableIn(List<String> values) {
            addCriterion("enable in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotIn(List<String> values) {
            addCriterion("enable not in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableBetween(String value1, String value2) {
            addCriterion("enable between", value1, value2, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotBetween(String value1, String value2) {
            addCriterion("enable not between", value1, value2, "enable");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}