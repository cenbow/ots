package com.mk.ots.bill.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillOrderWeekExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public BillOrderWeekExample() {
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

        public Criteria andCheckStatusIsNull() {
            addCriterion("check_status is null");
            return (Criteria) this;
        }

        public Criteria andCheckStatusIsNotNull() {
            addCriterion("check_status is not null");
            return (Criteria) this;
        }

        public Criteria andCheckStatusEqualTo(Integer value) {
            addCriterion("check_status =", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusNotEqualTo(Integer value) {
            addCriterion("check_status <>", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusGreaterThan(Integer value) {
            addCriterion("check_status >", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("check_status >=", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusLessThan(Integer value) {
            addCriterion("check_status <", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusLessThanOrEqualTo(Integer value) {
            addCriterion("check_status <=", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusIn(List<Integer> values) {
            addCriterion("check_status in", values, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusNotIn(List<Integer> values) {
            addCriterion("check_status not in", values, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusBetween(Integer value1, Integer value2) {
            addCriterion("check_status between", value1, value2, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("check_status not between", value1, value2, "checkStatus");
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

        public Criteria andOrderNumIsNull() {
            addCriterion("order_num is null");
            return (Criteria) this;
        }

        public Criteria andOrderNumIsNotNull() {
            addCriterion("order_num is not null");
            return (Criteria) this;
        }

        public Criteria andOrderNumEqualTo(Integer value) {
            addCriterion("order_num =", value, "orderNum");
            return (Criteria) this;
        }

        public Criteria andOrderNumNotEqualTo(Integer value) {
            addCriterion("order_num <>", value, "orderNum");
            return (Criteria) this;
        }

        public Criteria andOrderNumGreaterThan(Integer value) {
            addCriterion("order_num >", value, "orderNum");
            return (Criteria) this;
        }

        public Criteria andOrderNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("order_num >=", value, "orderNum");
            return (Criteria) this;
        }

        public Criteria andOrderNumLessThan(Integer value) {
            addCriterion("order_num <", value, "orderNum");
            return (Criteria) this;
        }

        public Criteria andOrderNumLessThanOrEqualTo(Integer value) {
            addCriterion("order_num <=", value, "orderNum");
            return (Criteria) this;
        }

        public Criteria andOrderNumIn(List<Integer> values) {
            addCriterion("order_num in", values, "orderNum");
            return (Criteria) this;
        }

        public Criteria andOrderNumNotIn(List<Integer> values) {
            addCriterion("order_num not in", values, "orderNum");
            return (Criteria) this;
        }

        public Criteria andOrderNumBetween(Integer value1, Integer value2) {
            addCriterion("order_num between", value1, value2, "orderNum");
            return (Criteria) this;
        }

        public Criteria andOrderNumNotBetween(Integer value1, Integer value2) {
            addCriterion("order_num not between", value1, value2, "orderNum");
            return (Criteria) this;
        }

        public Criteria andBillCostIsNull() {
            addCriterion("bill_cost is null");
            return (Criteria) this;
        }

        public Criteria andBillCostIsNotNull() {
            addCriterion("bill_cost is not null");
            return (Criteria) this;
        }

        public Criteria andBillCostEqualTo(BigDecimal value) {
            addCriterion("bill_cost =", value, "billCost");
            return (Criteria) this;
        }

        public Criteria andBillCostNotEqualTo(BigDecimal value) {
            addCriterion("bill_cost <>", value, "billCost");
            return (Criteria) this;
        }

        public Criteria andBillCostGreaterThan(BigDecimal value) {
            addCriterion("bill_cost >", value, "billCost");
            return (Criteria) this;
        }

        public Criteria andBillCostGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("bill_cost >=", value, "billCost");
            return (Criteria) this;
        }

        public Criteria andBillCostLessThan(BigDecimal value) {
            addCriterion("bill_cost <", value, "billCost");
            return (Criteria) this;
        }

        public Criteria andBillCostLessThanOrEqualTo(BigDecimal value) {
            addCriterion("bill_cost <=", value, "billCost");
            return (Criteria) this;
        }

        public Criteria andBillCostIn(List<BigDecimal> values) {
            addCriterion("bill_cost in", values, "billCost");
            return (Criteria) this;
        }

        public Criteria andBillCostNotIn(List<BigDecimal> values) {
            addCriterion("bill_cost not in", values, "billCost");
            return (Criteria) this;
        }

        public Criteria andBillCostBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("bill_cost between", value1, value2, "billCost");
            return (Criteria) this;
        }

        public Criteria andBillCostNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("bill_cost not between", value1, value2, "billCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostIsNull() {
            addCriterion("hotel_cost is null");
            return (Criteria) this;
        }

        public Criteria andHotelCostIsNotNull() {
            addCriterion("hotel_cost is not null");
            return (Criteria) this;
        }

        public Criteria andHotelCostEqualTo(BigDecimal value) {
            addCriterion("hotel_cost =", value, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostNotEqualTo(BigDecimal value) {
            addCriterion("hotel_cost <>", value, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostGreaterThan(BigDecimal value) {
            addCriterion("hotel_cost >", value, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("hotel_cost >=", value, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostLessThan(BigDecimal value) {
            addCriterion("hotel_cost <", value, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostLessThanOrEqualTo(BigDecimal value) {
            addCriterion("hotel_cost <=", value, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostIn(List<BigDecimal> values) {
            addCriterion("hotel_cost in", values, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostNotIn(List<BigDecimal> values) {
            addCriterion("hotel_cost not in", values, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("hotel_cost between", value1, value2, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andHotelCostNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("hotel_cost not between", value1, value2, "hotelCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostIsNull() {
            addCriterion("change_cost is null");
            return (Criteria) this;
        }

        public Criteria andChangeCostIsNotNull() {
            addCriterion("change_cost is not null");
            return (Criteria) this;
        }

        public Criteria andChangeCostEqualTo(BigDecimal value) {
            addCriterion("change_cost =", value, "changeCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostNotEqualTo(BigDecimal value) {
            addCriterion("change_cost <>", value, "changeCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostGreaterThan(BigDecimal value) {
            addCriterion("change_cost >", value, "changeCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("change_cost >=", value, "changeCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostLessThan(BigDecimal value) {
            addCriterion("change_cost <", value, "changeCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostLessThanOrEqualTo(BigDecimal value) {
            addCriterion("change_cost <=", value, "changeCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostIn(List<BigDecimal> values) {
            addCriterion("change_cost in", values, "changeCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostNotIn(List<BigDecimal> values) {
            addCriterion("change_cost not in", values, "changeCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("change_cost between", value1, value2, "changeCost");
            return (Criteria) this;
        }

        public Criteria andChangeCostNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("change_cost not between", value1, value2, "changeCost");
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

        public Criteria andIsFreezeIsNull() {
            addCriterion("is_freeze is null");
            return (Criteria) this;
        }

        public Criteria andIsFreezeIsNotNull() {
            addCriterion("is_freeze is not null");
            return (Criteria) this;
        }

        public Criteria andIsFreezeEqualTo(String value) {
            addCriterion("is_freeze =", value, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeNotEqualTo(String value) {
            addCriterion("is_freeze <>", value, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeGreaterThan(String value) {
            addCriterion("is_freeze >", value, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeGreaterThanOrEqualTo(String value) {
            addCriterion("is_freeze >=", value, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeLessThan(String value) {
            addCriterion("is_freeze <", value, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeLessThanOrEqualTo(String value) {
            addCriterion("is_freeze <=", value, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeLike(String value) {
            addCriterion("is_freeze like", value, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeNotLike(String value) {
            addCriterion("is_freeze not like", value, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeIn(List<String> values) {
            addCriterion("is_freeze in", values, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeNotIn(List<String> values) {
            addCriterion("is_freeze not in", values, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeBetween(String value1, String value2) {
            addCriterion("is_freeze between", value1, value2, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andIsFreezeNotBetween(String value1, String value2) {
            addCriterion("is_freeze not between", value1, value2, "isFreeze");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdIsNull() {
            addCriterion("check_user_id is null");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdIsNotNull() {
            addCriterion("check_user_id is not null");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdEqualTo(Long value) {
            addCriterion("check_user_id =", value, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdNotEqualTo(Long value) {
            addCriterion("check_user_id <>", value, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdGreaterThan(Long value) {
            addCriterion("check_user_id >", value, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("check_user_id >=", value, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdLessThan(Long value) {
            addCriterion("check_user_id <", value, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdLessThanOrEqualTo(Long value) {
            addCriterion("check_user_id <=", value, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdIn(List<Long> values) {
            addCriterion("check_user_id in", values, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdNotIn(List<Long> values) {
            addCriterion("check_user_id not in", values, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdBetween(Long value1, Long value2) {
            addCriterion("check_user_id between", value1, value2, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckUserIdNotBetween(Long value1, Long value2) {
            addCriterion("check_user_id not between", value1, value2, "checkUserId");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameIsNull() {
            addCriterion("checku_ser_name is null");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameIsNotNull() {
            addCriterion("checku_ser_name is not null");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameEqualTo(String value) {
            addCriterion("checku_ser_name =", value, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameNotEqualTo(String value) {
            addCriterion("checku_ser_name <>", value, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameGreaterThan(String value) {
            addCriterion("checku_ser_name >", value, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameGreaterThanOrEqualTo(String value) {
            addCriterion("checku_ser_name >=", value, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameLessThan(String value) {
            addCriterion("checku_ser_name <", value, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameLessThanOrEqualTo(String value) {
            addCriterion("checku_ser_name <=", value, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameLike(String value) {
            addCriterion("checku_ser_name like", value, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameNotLike(String value) {
            addCriterion("checku_ser_name not like", value, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameIn(List<String> values) {
            addCriterion("checku_ser_name in", values, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameNotIn(List<String> values) {
            addCriterion("checku_ser_name not in", values, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameBetween(String value1, String value2) {
            addCriterion("checku_ser_name between", value1, value2, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckuSerNameNotBetween(String value1, String value2) {
            addCriterion("checku_ser_name not between", value1, value2, "checkuSerName");
            return (Criteria) this;
        }

        public Criteria andCheckTimeIsNull() {
            addCriterion("check_time is null");
            return (Criteria) this;
        }

        public Criteria andCheckTimeIsNotNull() {
            addCriterion("check_time is not null");
            return (Criteria) this;
        }

        public Criteria andCheckTimeEqualTo(Date value) {
            addCriterion("check_time =", value, "checkTime");
            return (Criteria) this;
        }

        public Criteria andCheckTimeNotEqualTo(Date value) {
            addCriterion("check_time <>", value, "checkTime");
            return (Criteria) this;
        }

        public Criteria andCheckTimeGreaterThan(Date value) {
            addCriterion("check_time >", value, "checkTime");
            return (Criteria) this;
        }

        public Criteria andCheckTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("check_time >=", value, "checkTime");
            return (Criteria) this;
        }

        public Criteria andCheckTimeLessThan(Date value) {
            addCriterion("check_time <", value, "checkTime");
            return (Criteria) this;
        }

        public Criteria andCheckTimeLessThanOrEqualTo(Date value) {
            addCriterion("check_time <=", value, "checkTime");
            return (Criteria) this;
        }

        public Criteria andCheckTimeIn(List<Date> values) {
            addCriterion("check_time in", values, "checkTime");
            return (Criteria) this;
        }

        public Criteria andCheckTimeNotIn(List<Date> values) {
            addCriterion("check_time not in", values, "checkTime");
            return (Criteria) this;
        }

        public Criteria andCheckTimeBetween(Date value1, Date value2) {
            addCriterion("check_time between", value1, value2, "checkTime");
            return (Criteria) this;
        }

        public Criteria andCheckTimeNotBetween(Date value1, Date value2) {
            addCriterion("check_time not between", value1, value2, "checkTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIsNull() {
            addCriterion("confirm_time is null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIsNotNull() {
            addCriterion("confirm_time is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeEqualTo(Date value) {
            addCriterion("confirm_time =", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotEqualTo(Date value) {
            addCriterion("confirm_time <>", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThan(Date value) {
            addCriterion("confirm_time >", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("confirm_time >=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThan(Date value) {
            addCriterion("confirm_time <", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThanOrEqualTo(Date value) {
            addCriterion("confirm_time <=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIn(List<Date> values) {
            addCriterion("confirm_time in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotIn(List<Date> values) {
            addCriterion("confirm_time not in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeBetween(Date value1, Date value2) {
            addCriterion("confirm_time between", value1, value2, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotBetween(Date value1, Date value2) {
            addCriterion("confirm_time not between", value1, value2, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdIsNull() {
            addCriterion("confirm_user_id is null");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdIsNotNull() {
            addCriterion("confirm_user_id is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdEqualTo(Long value) {
            addCriterion("confirm_user_id =", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdNotEqualTo(Long value) {
            addCriterion("confirm_user_id <>", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdGreaterThan(Long value) {
            addCriterion("confirm_user_id >", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("confirm_user_id >=", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdLessThan(Long value) {
            addCriterion("confirm_user_id <", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdLessThanOrEqualTo(Long value) {
            addCriterion("confirm_user_id <=", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdIn(List<Long> values) {
            addCriterion("confirm_user_id in", values, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdNotIn(List<Long> values) {
            addCriterion("confirm_user_id not in", values, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdBetween(Long value1, Long value2) {
            addCriterion("confirm_user_id between", value1, value2, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdNotBetween(Long value1, Long value2) {
            addCriterion("confirm_user_id not between", value1, value2, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameIsNull() {
            addCriterion("confirm_user_name is null");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameIsNotNull() {
            addCriterion("confirm_user_name is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameEqualTo(String value) {
            addCriterion("confirm_user_name =", value, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameNotEqualTo(String value) {
            addCriterion("confirm_user_name <>", value, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameGreaterThan(String value) {
            addCriterion("confirm_user_name >", value, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameGreaterThanOrEqualTo(String value) {
            addCriterion("confirm_user_name >=", value, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameLessThan(String value) {
            addCriterion("confirm_user_name <", value, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameLessThanOrEqualTo(String value) {
            addCriterion("confirm_user_name <=", value, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameLike(String value) {
            addCriterion("confirm_user_name like", value, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameNotLike(String value) {
            addCriterion("confirm_user_name not like", value, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameIn(List<String> values) {
            addCriterion("confirm_user_name in", values, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameNotIn(List<String> values) {
            addCriterion("confirm_user_name not in", values, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameBetween(String value1, String value2) {
            addCriterion("confirm_user_name between", value1, value2, "confirmUserName");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNameNotBetween(String value1, String value2) {
            addCriterion("confirm_user_name not between", value1, value2, "confirmUserName");
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