package com.mk.ots.bill.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillOrderDetailExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public BillOrderDetailExample() {
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

        public Criteria andHotelIdIsNull() {
            addCriterion("hotel_id is null");
            return (Criteria) this;
        }

        public Criteria andHotelIdIsNotNull() {
            addCriterion("hotel_id is not null");
            return (Criteria) this;
        }

        public Criteria andHotelIdEqualTo(Long value) {
            addCriterion("hotel_id =", value, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelIdNotEqualTo(Long value) {
            addCriterion("hotel_id <>", value, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelIdGreaterThan(Long value) {
            addCriterion("hotel_id >", value, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelIdGreaterThanOrEqualTo(Long value) {
            addCriterion("hotel_id >=", value, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelIdLessThan(Long value) {
            addCriterion("hotel_id <", value, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelIdLessThanOrEqualTo(Long value) {
            addCriterion("hotel_id <=", value, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelIdIn(List<Long> values) {
            addCriterion("hotel_id in", values, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelIdNotIn(List<Long> values) {
            addCriterion("hotel_id not in", values, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelIdBetween(Long value1, Long value2) {
            addCriterion("hotel_id between", value1, value2, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelIdNotBetween(Long value1, Long value2) {
            addCriterion("hotel_id not between", value1, value2, "hotelId");
            return (Criteria) this;
        }

        public Criteria andHotelNameIsNull() {
            addCriterion("hotel_name is null");
            return (Criteria) this;
        }

        public Criteria andHotelNameIsNotNull() {
            addCriterion("hotel_name is not null");
            return (Criteria) this;
        }

        public Criteria andHotelNameEqualTo(String value) {
            addCriterion("hotel_name =", value, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameNotEqualTo(String value) {
            addCriterion("hotel_name <>", value, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameGreaterThan(String value) {
            addCriterion("hotel_name >", value, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameGreaterThanOrEqualTo(String value) {
            addCriterion("hotel_name >=", value, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameLessThan(String value) {
            addCriterion("hotel_name <", value, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameLessThanOrEqualTo(String value) {
            addCriterion("hotel_name <=", value, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameLike(String value) {
            addCriterion("hotel_name like", value, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameNotLike(String value) {
            addCriterion("hotel_name not like", value, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameIn(List<String> values) {
            addCriterion("hotel_name in", values, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameNotIn(List<String> values) {
            addCriterion("hotel_name not in", values, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameBetween(String value1, String value2) {
            addCriterion("hotel_name between", value1, value2, "hotelName");
            return (Criteria) this;
        }

        public Criteria andHotelNameNotBetween(String value1, String value2) {
            addCriterion("hotel_name not between", value1, value2, "hotelName");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNull() {
            addCriterion("order_id is null");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNotNull() {
            addCriterion("order_id is not null");
            return (Criteria) this;
        }

        public Criteria andOrderIdEqualTo(Long value) {
            addCriterion("order_id =", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotEqualTo(Long value) {
            addCriterion("order_id <>", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThan(Long value) {
            addCriterion("order_id >", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThanOrEqualTo(Long value) {
            addCriterion("order_id >=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThan(Long value) {
            addCriterion("order_id <", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThanOrEqualTo(Long value) {
            addCriterion("order_id <=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdIn(List<Long> values) {
            addCriterion("order_id in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotIn(List<Long> values) {
            addCriterion("order_id not in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdBetween(Long value1, Long value2) {
            addCriterion("order_id between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotBetween(Long value1, Long value2) {
            addCriterion("order_id not between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderTypeIsNull() {
            addCriterion("order_type is null");
            return (Criteria) this;
        }

        public Criteria andOrderTypeIsNotNull() {
            addCriterion("order_type is not null");
            return (Criteria) this;
        }

        public Criteria andOrderTypeEqualTo(Long value) {
            addCriterion("order_type =", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotEqualTo(Long value) {
            addCriterion("order_type <>", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeGreaterThan(Long value) {
            addCriterion("order_type >", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeGreaterThanOrEqualTo(Long value) {
            addCriterion("order_type >=", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeLessThan(Long value) {
            addCriterion("order_type <", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeLessThanOrEqualTo(Long value) {
            addCriterion("order_type <=", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeIn(List<Long> values) {
            addCriterion("order_type in", values, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotIn(List<Long> values) {
            addCriterion("order_type not in", values, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeBetween(Long value1, Long value2) {
            addCriterion("order_type between", value1, value2, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotBetween(Long value1, Long value2) {
            addCriterion("order_type not between", value1, value2, "orderType");
            return (Criteria) this;
        }

        public Criteria andBeginTimeIsNull() {
            addCriterion("begin_time is null");
            return (Criteria) this;
        }

        public Criteria andBeginTimeIsNotNull() {
            addCriterion("begin_time is not null");
            return (Criteria) this;
        }

        public Criteria andBeginTimeEqualTo(Date value) {
            addCriterion("begin_time =", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotEqualTo(Date value) {
            addCriterion("begin_time <>", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeGreaterThan(Date value) {
            addCriterion("begin_time >", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("begin_time >=", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeLessThan(Date value) {
            addCriterion("begin_time <", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeLessThanOrEqualTo(Date value) {
            addCriterion("begin_time <=", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeIn(List<Date> values) {
            addCriterion("begin_time in", values, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotIn(List<Date> values) {
            addCriterion("begin_time not in", values, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeBetween(Date value1, Date value2) {
            addCriterion("begin_time between", value1, value2, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotBetween(Date value1, Date value2) {
            addCriterion("begin_time not between", value1, value2, "beginTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNull() {
            addCriterion("end_time is null");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNotNull() {
            addCriterion("end_time is not null");
            return (Criteria) this;
        }

        public Criteria andEndTimeEqualTo(Date value) {
            addCriterion("end_time =", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotEqualTo(Date value) {
            addCriterion("end_time <>", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThan(Date value) {
            addCriterion("end_time >", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("end_time >=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThan(Date value) {
            addCriterion("end_time <", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("end_time <=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIn(List<Date> values) {
            addCriterion("end_time in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotIn(List<Date> values) {
            addCriterion("end_time not in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeBetween(Date value1, Date value2) {
            addCriterion("end_time between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("end_time not between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andTotalPriceIsNull() {
            addCriterion("total_price is null");
            return (Criteria) this;
        }

        public Criteria andTotalPriceIsNotNull() {
            addCriterion("total_price is not null");
            return (Criteria) this;
        }

        public Criteria andTotalPriceEqualTo(BigDecimal value) {
            addCriterion("total_price =", value, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andTotalPriceNotEqualTo(BigDecimal value) {
            addCriterion("total_price <>", value, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andTotalPriceGreaterThan(BigDecimal value) {
            addCriterion("total_price >", value, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andTotalPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("total_price >=", value, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andTotalPriceLessThan(BigDecimal value) {
            addCriterion("total_price <", value, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andTotalPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("total_price <=", value, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andTotalPriceIn(List<BigDecimal> values) {
            addCriterion("total_price in", values, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andTotalPriceNotIn(List<BigDecimal> values) {
            addCriterion("total_price not in", values, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andTotalPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_price between", value1, value2, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andTotalPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_price not between", value1, value2, "totalPrice");
            return (Criteria) this;
        }

        public Criteria andServiceCostIsNull() {
            addCriterion("service_cost is null");
            return (Criteria) this;
        }

        public Criteria andServiceCostIsNotNull() {
            addCriterion("service_cost is not null");
            return (Criteria) this;
        }

        public Criteria andServiceCostEqualTo(BigDecimal value) {
            addCriterion("service_cost =", value, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andServiceCostNotEqualTo(BigDecimal value) {
            addCriterion("service_cost <>", value, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andServiceCostGreaterThan(BigDecimal value) {
            addCriterion("service_cost >", value, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andServiceCostGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("service_cost >=", value, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andServiceCostLessThan(BigDecimal value) {
            addCriterion("service_cost <", value, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andServiceCostLessThanOrEqualTo(BigDecimal value) {
            addCriterion("service_cost <=", value, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andServiceCostIn(List<BigDecimal> values) {
            addCriterion("service_cost in", values, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andServiceCostNotIn(List<BigDecimal> values) {
            addCriterion("service_cost not in", values, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andServiceCostBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("service_cost between", value1, value2, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andServiceCostNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("service_cost not between", value1, value2, "serviceCost");
            return (Criteria) this;
        }

        public Criteria andRuleCodeIsNull() {
            addCriterion("rule_code is null");
            return (Criteria) this;
        }

        public Criteria andRuleCodeIsNotNull() {
            addCriterion("rule_code is not null");
            return (Criteria) this;
        }

        public Criteria andRuleCodeEqualTo(Integer value) {
            addCriterion("rule_code =", value, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andRuleCodeNotEqualTo(Integer value) {
            addCriterion("rule_code <>", value, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andRuleCodeGreaterThan(Integer value) {
            addCriterion("rule_code >", value, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andRuleCodeGreaterThanOrEqualTo(Integer value) {
            addCriterion("rule_code >=", value, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andRuleCodeLessThan(Integer value) {
            addCriterion("rule_code <", value, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andRuleCodeLessThanOrEqualTo(Integer value) {
            addCriterion("rule_code <=", value, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andRuleCodeIn(List<Integer> values) {
            addCriterion("rule_code in", values, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andRuleCodeNotIn(List<Integer> values) {
            addCriterion("rule_code not in", values, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andRuleCodeBetween(Integer value1, Integer value2) {
            addCriterion("rule_code between", value1, value2, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andRuleCodeNotBetween(Integer value1, Integer value2) {
            addCriterion("rule_code not between", value1, value2, "ruleCode");
            return (Criteria) this;
        }

        public Criteria andSpreadUserIsNull() {
            addCriterion("spread_user is null");
            return (Criteria) this;
        }

        public Criteria andSpreadUserIsNotNull() {
            addCriterion("spread_user is not null");
            return (Criteria) this;
        }

        public Criteria andSpreadUserEqualTo(Long value) {
            addCriterion("spread_user =", value, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andSpreadUserNotEqualTo(Long value) {
            addCriterion("spread_user <>", value, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andSpreadUserGreaterThan(Long value) {
            addCriterion("spread_user >", value, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andSpreadUserGreaterThanOrEqualTo(Long value) {
            addCriterion("spread_user >=", value, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andSpreadUserLessThan(Long value) {
            addCriterion("spread_user <", value, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andSpreadUserLessThanOrEqualTo(Long value) {
            addCriterion("spread_user <=", value, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andSpreadUserIn(List<Long> values) {
            addCriterion("spread_user in", values, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andSpreadUserNotIn(List<Long> values) {
            addCriterion("spread_user not in", values, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andSpreadUserBetween(Long value1, Long value2) {
            addCriterion("spread_user between", value1, value2, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andSpreadUserNotBetween(Long value1, Long value2) {
            addCriterion("spread_user not between", value1, value2, "spreadUser");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonIsNull() {
            addCriterion("invalid_reason is null");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonIsNotNull() {
            addCriterion("invalid_reason is not null");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonEqualTo(Integer value) {
            addCriterion("invalid_reason =", value, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonNotEqualTo(Integer value) {
            addCriterion("invalid_reason <>", value, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonGreaterThan(Integer value) {
            addCriterion("invalid_reason >", value, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonGreaterThanOrEqualTo(Integer value) {
            addCriterion("invalid_reason >=", value, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonLessThan(Integer value) {
            addCriterion("invalid_reason <", value, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonLessThanOrEqualTo(Integer value) {
            addCriterion("invalid_reason <=", value, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonIn(List<Integer> values) {
            addCriterion("invalid_reason in", values, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonNotIn(List<Integer> values) {
            addCriterion("invalid_reason not in", values, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonBetween(Integer value1, Integer value2) {
            addCriterion("invalid_reason between", value1, value2, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andInvalidReasonNotBetween(Integer value1, Integer value2) {
            addCriterion("invalid_reason not between", value1, value2, "invalidReason");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeIsNull() {
            addCriterion("checkin_time is null");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeIsNotNull() {
            addCriterion("checkin_time is not null");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeEqualTo(Date value) {
            addCriterion("checkin_time =", value, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeNotEqualTo(Date value) {
            addCriterion("checkin_time <>", value, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeGreaterThan(Date value) {
            addCriterion("checkin_time >", value, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("checkin_time >=", value, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeLessThan(Date value) {
            addCriterion("checkin_time <", value, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeLessThanOrEqualTo(Date value) {
            addCriterion("checkin_time <=", value, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeIn(List<Date> values) {
            addCriterion("checkin_time in", values, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeNotIn(List<Date> values) {
            addCriterion("checkin_time not in", values, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeBetween(Date value1, Date value2) {
            addCriterion("checkin_time between", value1, value2, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andCheckinTimeNotBetween(Date value1, Date value2) {
            addCriterion("checkin_time not between", value1, value2, "checkinTime");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountIsNull() {
            addCriterion("prepayment_discount is null");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountIsNotNull() {
            addCriterion("prepayment_discount is not null");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountEqualTo(BigDecimal value) {
            addCriterion("prepayment_discount =", value, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountNotEqualTo(BigDecimal value) {
            addCriterion("prepayment_discount <>", value, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountGreaterThan(BigDecimal value) {
            addCriterion("prepayment_discount >", value, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("prepayment_discount >=", value, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountLessThan(BigDecimal value) {
            addCriterion("prepayment_discount <", value, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("prepayment_discount <=", value, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountIn(List<BigDecimal> values) {
            addCriterion("prepayment_discount in", values, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountNotIn(List<BigDecimal> values) {
            addCriterion("prepayment_discount not in", values, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("prepayment_discount between", value1, value2, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andPrepaymentDiscountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("prepayment_discount not between", value1, value2, "prepaymentDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountIsNull() {
            addCriterion("to_pay_discount is null");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountIsNotNull() {
            addCriterion("to_pay_discount is not null");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountEqualTo(BigDecimal value) {
            addCriterion("to_pay_discount =", value, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountNotEqualTo(BigDecimal value) {
            addCriterion("to_pay_discount <>", value, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountGreaterThan(BigDecimal value) {
            addCriterion("to_pay_discount >", value, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("to_pay_discount >=", value, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountLessThan(BigDecimal value) {
            addCriterion("to_pay_discount <", value, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("to_pay_discount <=", value, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountIn(List<BigDecimal> values) {
            addCriterion("to_pay_discount in", values, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountNotIn(List<BigDecimal> values) {
            addCriterion("to_pay_discount not in", values, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("to_pay_discount between", value1, value2, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andToPayDiscountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("to_pay_discount not between", value1, value2, "toPayDiscount");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeIsNull() {
            addCriterion("order_create_time is null");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeIsNotNull() {
            addCriterion("order_create_time is not null");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeEqualTo(Date value) {
            addCriterion("order_create_time =", value, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeNotEqualTo(Date value) {
            addCriterion("order_create_time <>", value, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeGreaterThan(Date value) {
            addCriterion("order_create_time >", value, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("order_create_time >=", value, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeLessThan(Date value) {
            addCriterion("order_create_time <", value, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("order_create_time <=", value, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeIn(List<Date> values) {
            addCriterion("order_create_time in", values, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeNotIn(List<Date> values) {
            addCriterion("order_create_time not in", values, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeBetween(Date value1, Date value2) {
            addCriterion("order_create_time between", value1, value2, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andOrderCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("order_create_time not between", value1, value2, "orderCreateTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeIsNull() {
            addCriterion("bill_time is null");
            return (Criteria) this;
        }

        public Criteria andBillTimeIsNotNull() {
            addCriterion("bill_time is not null");
            return (Criteria) this;
        }

        public Criteria andBillTimeEqualTo(Date value) {
            addCriterion("bill_time =", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeNotEqualTo(Date value) {
            addCriterion("bill_time <>", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeGreaterThan(Date value) {
            addCriterion("bill_time >", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("bill_time >=", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeLessThan(Date value) {
            addCriterion("bill_time <", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeLessThanOrEqualTo(Date value) {
            addCriterion("bill_time <=", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeIn(List<Date> values) {
            addCriterion("bill_time in", values, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeNotIn(List<Date> values) {
            addCriterion("bill_time not in", values, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeBetween(Date value1, Date value2) {
            addCriterion("bill_time between", value1, value2, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeNotBetween(Date value1, Date value2) {
            addCriterion("bill_time not between", value1, value2, "billTime");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceIsNull() {
            addCriterion("settlement_price is null");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceIsNotNull() {
            addCriterion("settlement_price is not null");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceEqualTo(BigDecimal value) {
            addCriterion("settlement_price =", value, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceNotEqualTo(BigDecimal value) {
            addCriterion("settlement_price <>", value, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceGreaterThan(BigDecimal value) {
            addCriterion("settlement_price >", value, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("settlement_price >=", value, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceLessThan(BigDecimal value) {
            addCriterion("settlement_price <", value, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("settlement_price <=", value, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceIn(List<BigDecimal> values) {
            addCriterion("settlement_price in", values, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceNotIn(List<BigDecimal> values) {
            addCriterion("settlement_price not in", values, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("settlement_price between", value1, value2, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andSettlementPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("settlement_price not between", value1, value2, "settlementPrice");
            return (Criteria) this;
        }

        public Criteria andUserCostIsNull() {
            addCriterion("user_cost is null");
            return (Criteria) this;
        }

        public Criteria andUserCostIsNotNull() {
            addCriterion("user_cost is not null");
            return (Criteria) this;
        }

        public Criteria andUserCostEqualTo(BigDecimal value) {
            addCriterion("user_cost =", value, "userCost");
            return (Criteria) this;
        }

        public Criteria andUserCostNotEqualTo(BigDecimal value) {
            addCriterion("user_cost <>", value, "userCost");
            return (Criteria) this;
        }

        public Criteria andUserCostGreaterThan(BigDecimal value) {
            addCriterion("user_cost >", value, "userCost");
            return (Criteria) this;
        }

        public Criteria andUserCostGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("user_cost >=", value, "userCost");
            return (Criteria) this;
        }

        public Criteria andUserCostLessThan(BigDecimal value) {
            addCriterion("user_cost <", value, "userCost");
            return (Criteria) this;
        }

        public Criteria andUserCostLessThanOrEqualTo(BigDecimal value) {
            addCriterion("user_cost <=", value, "userCost");
            return (Criteria) this;
        }

        public Criteria andUserCostIn(List<BigDecimal> values) {
            addCriterion("user_cost in", values, "userCost");
            return (Criteria) this;
        }

        public Criteria andUserCostNotIn(List<BigDecimal> values) {
            addCriterion("user_cost not in", values, "userCost");
            return (Criteria) this;
        }

        public Criteria andUserCostBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("user_cost between", value1, value2, "userCost");
            return (Criteria) this;
        }

        public Criteria andUserCostNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("user_cost not between", value1, value2, "userCost");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyIsNull() {
            addCriterion("available_money is null");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyIsNotNull() {
            addCriterion("available_money is not null");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyEqualTo(BigDecimal value) {
            addCriterion("available_money =", value, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyNotEqualTo(BigDecimal value) {
            addCriterion("available_money <>", value, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyGreaterThan(BigDecimal value) {
            addCriterion("available_money >", value, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("available_money >=", value, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyLessThan(BigDecimal value) {
            addCriterion("available_money <", value, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyLessThanOrEqualTo(BigDecimal value) {
            addCriterion("available_money <=", value, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyIn(List<BigDecimal> values) {
            addCriterion("available_money in", values, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyNotIn(List<BigDecimal> values) {
            addCriterion("available_money not in", values, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("available_money between", value1, value2, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andAvailableMoneyNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("available_money not between", value1, value2, "availableMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyIsNull() {
            addCriterion("ticket_money is null");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyIsNotNull() {
            addCriterion("ticket_money is not null");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyEqualTo(BigDecimal value) {
            addCriterion("ticket_money =", value, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyNotEqualTo(BigDecimal value) {
            addCriterion("ticket_money <>", value, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyGreaterThan(BigDecimal value) {
            addCriterion("ticket_money >", value, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("ticket_money >=", value, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyLessThan(BigDecimal value) {
            addCriterion("ticket_money <", value, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyLessThanOrEqualTo(BigDecimal value) {
            addCriterion("ticket_money <=", value, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyIn(List<BigDecimal> values) {
            addCriterion("ticket_money in", values, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyNotIn(List<BigDecimal> values) {
            addCriterion("ticket_money not in", values, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("ticket_money between", value1, value2, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andTicketMoneyNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("ticket_money not between", value1, value2, "ticketMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyIsNull() {
            addCriterion("wechat_pay_money is null");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyIsNotNull() {
            addCriterion("wechat_pay_money is not null");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyEqualTo(BigDecimal value) {
            addCriterion("wechat_pay_money =", value, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyNotEqualTo(BigDecimal value) {
            addCriterion("wechat_pay_money <>", value, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyGreaterThan(BigDecimal value) {
            addCriterion("wechat_pay_money >", value, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("wechat_pay_money >=", value, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyLessThan(BigDecimal value) {
            addCriterion("wechat_pay_money <", value, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyLessThanOrEqualTo(BigDecimal value) {
            addCriterion("wechat_pay_money <=", value, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyIn(List<BigDecimal> values) {
            addCriterion("wechat_pay_money in", values, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyNotIn(List<BigDecimal> values) {
            addCriterion("wechat_pay_money not in", values, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("wechat_pay_money between", value1, value2, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andWechatPayMoneyNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("wechat_pay_money not between", value1, value2, "wechatPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyIsNull() {
            addCriterion("ali_pay_money is null");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyIsNotNull() {
            addCriterion("ali_pay_money is not null");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyEqualTo(BigDecimal value) {
            addCriterion("ali_pay_money =", value, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyNotEqualTo(BigDecimal value) {
            addCriterion("ali_pay_money <>", value, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyGreaterThan(BigDecimal value) {
            addCriterion("ali_pay_money >", value, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("ali_pay_money >=", value, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyLessThan(BigDecimal value) {
            addCriterion("ali_pay_money <", value, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyLessThanOrEqualTo(BigDecimal value) {
            addCriterion("ali_pay_money <=", value, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyIn(List<BigDecimal> values) {
            addCriterion("ali_pay_money in", values, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyNotIn(List<BigDecimal> values) {
            addCriterion("ali_pay_money not in", values, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("ali_pay_money between", value1, value2, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andAliPayMoneyNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("ali_pay_money not between", value1, value2, "aliPayMoney");
            return (Criteria) this;
        }

        public Criteria andPromoIdIsNull() {
            addCriterion("promo_id is null");
            return (Criteria) this;
        }

        public Criteria andPromoIdIsNotNull() {
            addCriterion("promo_id is not null");
            return (Criteria) this;
        }

        public Criteria andPromoIdEqualTo(Integer value) {
            addCriterion("promo_id =", value, "promoId");
            return (Criteria) this;
        }

        public Criteria andPromoIdNotEqualTo(Integer value) {
            addCriterion("promo_id <>", value, "promoId");
            return (Criteria) this;
        }

        public Criteria andPromoIdGreaterThan(Integer value) {
            addCriterion("promo_id >", value, "promoId");
            return (Criteria) this;
        }

        public Criteria andPromoIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("promo_id >=", value, "promoId");
            return (Criteria) this;
        }

        public Criteria andPromoIdLessThan(Integer value) {
            addCriterion("promo_id <", value, "promoId");
            return (Criteria) this;
        }

        public Criteria andPromoIdLessThanOrEqualTo(Integer value) {
            addCriterion("promo_id <=", value, "promoId");
            return (Criteria) this;
        }

        public Criteria andPromoIdIn(List<Integer> values) {
            addCriterion("promo_id in", values, "promoId");
            return (Criteria) this;
        }

        public Criteria andPromoIdNotIn(List<Integer> values) {
            addCriterion("promo_id not in", values, "promoId");
            return (Criteria) this;
        }

        public Criteria andPromoIdBetween(Integer value1, Integer value2) {
            addCriterion("promo_id between", value1, value2, "promoId");
            return (Criteria) this;
        }

        public Criteria andPromoIdNotBetween(Integer value1, Integer value2) {
            addCriterion("promo_id not between", value1, value2, "promoId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdIsNull() {
            addCriterion("b_bill_order_week_id is null");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdIsNotNull() {
            addCriterion("b_bill_order_week_id is not null");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdEqualTo(Long value) {
            addCriterion("b_bill_order_week_id =", value, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdNotEqualTo(Long value) {
            addCriterion("b_bill_order_week_id <>", value, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdGreaterThan(Long value) {
            addCriterion("b_bill_order_week_id >", value, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdGreaterThanOrEqualTo(Long value) {
            addCriterion("b_bill_order_week_id >=", value, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdLessThan(Long value) {
            addCriterion("b_bill_order_week_id <", value, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdLessThanOrEqualTo(Long value) {
            addCriterion("b_bill_order_week_id <=", value, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdIn(List<Long> values) {
            addCriterion("b_bill_order_week_id in", values, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdNotIn(List<Long> values) {
            addCriterion("b_bill_order_week_id not in", values, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdBetween(Long value1, Long value2) {
            addCriterion("b_bill_order_week_id between", value1, value2, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andBBillOrderWeekIdNotBetween(Long value1, Long value2) {
            addCriterion("b_bill_order_week_id not between", value1, value2, "bBillOrderWeekId");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
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