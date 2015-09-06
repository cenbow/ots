package com.mk.pms.room.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomRepairPoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public RoomRepairPoExample() {
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

        public Criteria andHotelidIsNull() {
            addCriterion("hotelId is null");
            return (Criteria) this;
        }

        public Criteria andHotelidIsNotNull() {
            addCriterion("hotelId is not null");
            return (Criteria) this;
        }

        public Criteria andHotelidEqualTo(Long value) {
            addCriterion("hotelId =", value, "hotelid");
            return (Criteria) this;
        }

        public Criteria andHotelidNotEqualTo(Long value) {
            addCriterion("hotelId <>", value, "hotelid");
            return (Criteria) this;
        }

        public Criteria andHotelidGreaterThan(Long value) {
            addCriterion("hotelId >", value, "hotelid");
            return (Criteria) this;
        }

        public Criteria andHotelidGreaterThanOrEqualTo(Long value) {
            addCriterion("hotelId >=", value, "hotelid");
            return (Criteria) this;
        }

        public Criteria andHotelidLessThan(Long value) {
            addCriterion("hotelId <", value, "hotelid");
            return (Criteria) this;
        }

        public Criteria andHotelidLessThanOrEqualTo(Long value) {
            addCriterion("hotelId <=", value, "hotelid");
            return (Criteria) this;
        }

        public Criteria andHotelidIn(List<Long> values) {
            addCriterion("hotelId in", values, "hotelid");
            return (Criteria) this;
        }

        public Criteria andHotelidNotIn(List<Long> values) {
            addCriterion("hotelId not in", values, "hotelid");
            return (Criteria) this;
        }

        public Criteria andHotelidBetween(Long value1, Long value2) {
            addCriterion("hotelId between", value1, value2, "hotelid");
            return (Criteria) this;
        }

        public Criteria andHotelidNotBetween(Long value1, Long value2) {
            addCriterion("hotelId not between", value1, value2, "hotelid");
            return (Criteria) this;
        }

        public Criteria andRepairidIsNull() {
            addCriterion("repairId is null");
            return (Criteria) this;
        }

        public Criteria andRepairidIsNotNull() {
            addCriterion("repairId is not null");
            return (Criteria) this;
        }

        public Criteria andRepairidEqualTo(String value) {
            addCriterion("repairId =", value, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidNotEqualTo(String value) {
            addCriterion("repairId <>", value, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidGreaterThan(String value) {
            addCriterion("repairId >", value, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidGreaterThanOrEqualTo(String value) {
            addCriterion("repairId >=", value, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidLessThan(String value) {
            addCriterion("repairId <", value, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidLessThanOrEqualTo(String value) {
            addCriterion("repairId <=", value, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidLike(String value) {
            addCriterion("repairId like", value, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidNotLike(String value) {
            addCriterion("repairId not like", value, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidIn(List<String> values) {
            addCriterion("repairId in", values, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidNotIn(List<String> values) {
            addCriterion("repairId not in", values, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidBetween(String value1, String value2) {
            addCriterion("repairId between", value1, value2, "repairid");
            return (Criteria) this;
        }

        public Criteria andRepairidNotBetween(String value1, String value2) {
            addCriterion("repairId not between", value1, value2, "repairid");
            return (Criteria) this;
        }

        public Criteria andRoomidIsNull() {
            addCriterion("roomid is null");
            return (Criteria) this;
        }

        public Criteria andRoomidIsNotNull() {
            addCriterion("roomid is not null");
            return (Criteria) this;
        }

        public Criteria andRoomidEqualTo(Long value) {
            addCriterion("roomid =", value, "roomid");
            return (Criteria) this;
        }

        public Criteria andRoomidNotEqualTo(Long value) {
            addCriterion("roomid <>", value, "roomid");
            return (Criteria) this;
        }

        public Criteria andRoomidGreaterThan(Long value) {
            addCriterion("roomid >", value, "roomid");
            return (Criteria) this;
        }

        public Criteria andRoomidGreaterThanOrEqualTo(Long value) {
            addCriterion("roomid >=", value, "roomid");
            return (Criteria) this;
        }

        public Criteria andRoomidLessThan(Long value) {
            addCriterion("roomid <", value, "roomid");
            return (Criteria) this;
        }

        public Criteria andRoomidLessThanOrEqualTo(Long value) {
            addCriterion("roomid <=", value, "roomid");
            return (Criteria) this;
        }

        public Criteria andRoomidIn(List<Long> values) {
            addCriterion("roomid in", values, "roomid");
            return (Criteria) this;
        }

        public Criteria andRoomidNotIn(List<Long> values) {
            addCriterion("roomid not in", values, "roomid");
            return (Criteria) this;
        }

        public Criteria andRoomidBetween(Long value1, Long value2) {
            addCriterion("roomid between", value1, value2, "roomid");
            return (Criteria) this;
        }

        public Criteria andRoomidNotBetween(Long value1, Long value2) {
            addCriterion("roomid not between", value1, value2, "roomid");
            return (Criteria) this;
        }

        public Criteria andBegintimeIsNull() {
            addCriterion("Begintime is null");
            return (Criteria) this;
        }

        public Criteria andBegintimeIsNotNull() {
            addCriterion("Begintime is not null");
            return (Criteria) this;
        }

        public Criteria andBegintimeEqualTo(Date value) {
            addCriterion("Begintime =", value, "begintime");
            return (Criteria) this;
        }

        public Criteria andBegintimeNotEqualTo(Date value) {
            addCriterion("Begintime <>", value, "begintime");
            return (Criteria) this;
        }

        public Criteria andBegintimeGreaterThan(Date value) {
            addCriterion("Begintime >", value, "begintime");
            return (Criteria) this;
        }

        public Criteria andBegintimeGreaterThanOrEqualTo(Date value) {
            addCriterion("Begintime >=", value, "begintime");
            return (Criteria) this;
        }

        public Criteria andBegintimeLessThan(Date value) {
            addCriterion("Begintime <", value, "begintime");
            return (Criteria) this;
        }

        public Criteria andBegintimeLessThanOrEqualTo(Date value) {
            addCriterion("Begintime <=", value, "begintime");
            return (Criteria) this;
        }

        public Criteria andBegintimeIn(List<Date> values) {
            addCriterion("Begintime in", values, "begintime");
            return (Criteria) this;
        }

        public Criteria andBegintimeNotIn(List<Date> values) {
            addCriterion("Begintime not in", values, "begintime");
            return (Criteria) this;
        }

        public Criteria andBegintimeBetween(Date value1, Date value2) {
            addCriterion("Begintime between", value1, value2, "begintime");
            return (Criteria) this;
        }

        public Criteria andBegintimeNotBetween(Date value1, Date value2) {
            addCriterion("Begintime not between", value1, value2, "begintime");
            return (Criteria) this;
        }

        public Criteria andEndtimeIsNull() {
            addCriterion("Endtime is null");
            return (Criteria) this;
        }

        public Criteria andEndtimeIsNotNull() {
            addCriterion("Endtime is not null");
            return (Criteria) this;
        }

        public Criteria andEndtimeEqualTo(Date value) {
            addCriterion("Endtime =", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeNotEqualTo(Date value) {
            addCriterion("Endtime <>", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeGreaterThan(Date value) {
            addCriterion("Endtime >", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeGreaterThanOrEqualTo(Date value) {
            addCriterion("Endtime >=", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeLessThan(Date value) {
            addCriterion("Endtime <", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeLessThanOrEqualTo(Date value) {
            addCriterion("Endtime <=", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeIn(List<Date> values) {
            addCriterion("Endtime in", values, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeNotIn(List<Date> values) {
            addCriterion("Endtime not in", values, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeBetween(Date value1, Date value2) {
            addCriterion("Endtime between", value1, value2, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeNotBetween(Date value1, Date value2) {
            addCriterion("Endtime not between", value1, value2, "endtime");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidIsNull() {
            addCriterion("roomTypeId is null");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidIsNotNull() {
            addCriterion("roomTypeId is not null");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidEqualTo(Long value) {
            addCriterion("roomTypeId =", value, "roomtypeid");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidNotEqualTo(Long value) {
            addCriterion("roomTypeId <>", value, "roomtypeid");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidGreaterThan(Long value) {
            addCriterion("roomTypeId >", value, "roomtypeid");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidGreaterThanOrEqualTo(Long value) {
            addCriterion("roomTypeId >=", value, "roomtypeid");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidLessThan(Long value) {
            addCriterion("roomTypeId <", value, "roomtypeid");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidLessThanOrEqualTo(Long value) {
            addCriterion("roomTypeId <=", value, "roomtypeid");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidIn(List<Long> values) {
            addCriterion("roomTypeId in", values, "roomtypeid");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidNotIn(List<Long> values) {
            addCriterion("roomTypeId not in", values, "roomtypeid");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidBetween(Long value1, Long value2) {
            addCriterion("roomTypeId between", value1, value2, "roomtypeid");
            return (Criteria) this;
        }

        public Criteria andRoomtypeidNotBetween(Long value1, Long value2) {
            addCriterion("roomTypeId not between", value1, value2, "roomtypeid");
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