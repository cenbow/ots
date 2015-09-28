package com.mk.ots.room.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomCensusExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public RoomCensusExample() {
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

        public Criteria andHotelnameIsNull() {
            addCriterion("hotelName is null");
            return (Criteria) this;
        }

        public Criteria andHotelnameIsNotNull() {
            addCriterion("hotelName is not null");
            return (Criteria) this;
        }

        public Criteria andHotelnameEqualTo(String value) {
            addCriterion("hotelName =", value, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameNotEqualTo(String value) {
            addCriterion("hotelName <>", value, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameGreaterThan(String value) {
            addCriterion("hotelName >", value, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameGreaterThanOrEqualTo(String value) {
            addCriterion("hotelName >=", value, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameLessThan(String value) {
            addCriterion("hotelName <", value, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameLessThanOrEqualTo(String value) {
            addCriterion("hotelName <=", value, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameLike(String value) {
            addCriterion("hotelName like", value, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameNotLike(String value) {
            addCriterion("hotelName not like", value, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameIn(List<String> values) {
            addCriterion("hotelName in", values, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameNotIn(List<String> values) {
            addCriterion("hotelName not in", values, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameBetween(String value1, String value2) {
            addCriterion("hotelName between", value1, value2, "hotelname");
            return (Criteria) this;
        }

        public Criteria andHotelnameNotBetween(String value1, String value2) {
            addCriterion("hotelName not between", value1, value2, "hotelname");
            return (Criteria) this;
        }

        public Criteria andVisibleIsNull() {
            addCriterion("visible is null");
            return (Criteria) this;
        }

        public Criteria andVisibleIsNotNull() {
            addCriterion("visible is not null");
            return (Criteria) this;
        }

        public Criteria andVisibleEqualTo(String value) {
            addCriterion("visible =", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotEqualTo(String value) {
            addCriterion("visible <>", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThan(String value) {
            addCriterion("visible >", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThanOrEqualTo(String value) {
            addCriterion("visible >=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThan(String value) {
            addCriterion("visible <", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThanOrEqualTo(String value) {
            addCriterion("visible <=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLike(String value) {
            addCriterion("visible like", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotLike(String value) {
            addCriterion("visible not like", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleIn(List<String> values) {
            addCriterion("visible in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotIn(List<String> values) {
            addCriterion("visible not in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleBetween(String value1, String value2) {
            addCriterion("visible between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotBetween(String value1, String value2) {
            addCriterion("visible not between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andOnlineIsNull() {
            addCriterion("online is null");
            return (Criteria) this;
        }

        public Criteria andOnlineIsNotNull() {
            addCriterion("online is not null");
            return (Criteria) this;
        }

        public Criteria andOnlineEqualTo(String value) {
            addCriterion("online =", value, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineNotEqualTo(String value) {
            addCriterion("online <>", value, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineGreaterThan(String value) {
            addCriterion("online >", value, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineGreaterThanOrEqualTo(String value) {
            addCriterion("online >=", value, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineLessThan(String value) {
            addCriterion("online <", value, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineLessThanOrEqualTo(String value) {
            addCriterion("online <=", value, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineLike(String value) {
            addCriterion("online like", value, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineNotLike(String value) {
            addCriterion("online not like", value, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineIn(List<String> values) {
            addCriterion("online in", values, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineNotIn(List<String> values) {
            addCriterion("online not in", values, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineBetween(String value1, String value2) {
            addCriterion("online between", value1, value2, "online");
            return (Criteria) this;
        }

        public Criteria andOnlineNotBetween(String value1, String value2) {
            addCriterion("online not between", value1, value2, "online");
            return (Criteria) this;
        }

        public Criteria andRoomcountIsNull() {
            addCriterion("roomCount is null");
            return (Criteria) this;
        }

        public Criteria andRoomcountIsNotNull() {
            addCriterion("roomCount is not null");
            return (Criteria) this;
        }

        public Criteria andRoomcountEqualTo(Integer value) {
            addCriterion("roomCount =", value, "roomcount");
            return (Criteria) this;
        }

        public Criteria andRoomcountNotEqualTo(Integer value) {
            addCriterion("roomCount <>", value, "roomcount");
            return (Criteria) this;
        }

        public Criteria andRoomcountGreaterThan(Integer value) {
            addCriterion("roomCount >", value, "roomcount");
            return (Criteria) this;
        }

        public Criteria andRoomcountGreaterThanOrEqualTo(Integer value) {
            addCriterion("roomCount >=", value, "roomcount");
            return (Criteria) this;
        }

        public Criteria andRoomcountLessThan(Integer value) {
            addCriterion("roomCount <", value, "roomcount");
            return (Criteria) this;
        }

        public Criteria andRoomcountLessThanOrEqualTo(Integer value) {
            addCriterion("roomCount <=", value, "roomcount");
            return (Criteria) this;
        }

        public Criteria andRoomcountIn(List<Integer> values) {
            addCriterion("roomCount in", values, "roomcount");
            return (Criteria) this;
        }

        public Criteria andRoomcountNotIn(List<Integer> values) {
            addCriterion("roomCount not in", values, "roomcount");
            return (Criteria) this;
        }

        public Criteria andRoomcountBetween(Integer value1, Integer value2) {
            addCriterion("roomCount between", value1, value2, "roomcount");
            return (Criteria) this;
        }

        public Criteria andRoomcountNotBetween(Integer value1, Integer value2) {
            addCriterion("roomCount not between", value1, value2, "roomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountIsNull() {
            addCriterion("freeRoomCount is null");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountIsNotNull() {
            addCriterion("freeRoomCount is not null");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountEqualTo(Integer value) {
            addCriterion("freeRoomCount =", value, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountNotEqualTo(Integer value) {
            addCriterion("freeRoomCount <>", value, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountGreaterThan(Integer value) {
            addCriterion("freeRoomCount >", value, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountGreaterThanOrEqualTo(Integer value) {
            addCriterion("freeRoomCount >=", value, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountLessThan(Integer value) {
            addCriterion("freeRoomCount <", value, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountLessThanOrEqualTo(Integer value) {
            addCriterion("freeRoomCount <=", value, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountIn(List<Integer> values) {
            addCriterion("freeRoomCount in", values, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountNotIn(List<Integer> values) {
            addCriterion("freeRoomCount not in", values, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountBetween(Integer value1, Integer value2) {
            addCriterion("freeRoomCount between", value1, value2, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andFreeroomcountNotBetween(Integer value1, Integer value2) {
            addCriterion("freeRoomCount not between", value1, value2, "freeroomcount");
            return (Criteria) this;
        }

        public Criteria andYearIsNull() {
            addCriterion("year is null");
            return (Criteria) this;
        }

        public Criteria andYearIsNotNull() {
            addCriterion("year is not null");
            return (Criteria) this;
        }

        public Criteria andYearEqualTo(String value) {
            addCriterion("year =", value, "year");
            return (Criteria) this;
        }

        public Criteria andYearNotEqualTo(String value) {
            addCriterion("year <>", value, "year");
            return (Criteria) this;
        }

        public Criteria andYearGreaterThan(String value) {
            addCriterion("year >", value, "year");
            return (Criteria) this;
        }

        public Criteria andYearGreaterThanOrEqualTo(String value) {
            addCriterion("year >=", value, "year");
            return (Criteria) this;
        }

        public Criteria andYearLessThan(String value) {
            addCriterion("year <", value, "year");
            return (Criteria) this;
        }

        public Criteria andYearLessThanOrEqualTo(String value) {
            addCriterion("year <=", value, "year");
            return (Criteria) this;
        }

        public Criteria andYearLike(String value) {
            addCriterion("year like", value, "year");
            return (Criteria) this;
        }

        public Criteria andYearNotLike(String value) {
            addCriterion("year not like", value, "year");
            return (Criteria) this;
        }

        public Criteria andYearIn(List<String> values) {
            addCriterion("year in", values, "year");
            return (Criteria) this;
        }

        public Criteria andYearNotIn(List<String> values) {
            addCriterion("year not in", values, "year");
            return (Criteria) this;
        }

        public Criteria andYearBetween(String value1, String value2) {
            addCriterion("year between", value1, value2, "year");
            return (Criteria) this;
        }

        public Criteria andYearNotBetween(String value1, String value2) {
            addCriterion("year not between", value1, value2, "year");
            return (Criteria) this;
        }

        public Criteria andMonthIsNull() {
            addCriterion("month is null");
            return (Criteria) this;
        }

        public Criteria andMonthIsNotNull() {
            addCriterion("month is not null");
            return (Criteria) this;
        }

        public Criteria andMonthEqualTo(String value) {
            addCriterion("month =", value, "month");
            return (Criteria) this;
        }

        public Criteria andMonthNotEqualTo(String value) {
            addCriterion("month <>", value, "month");
            return (Criteria) this;
        }

        public Criteria andMonthGreaterThan(String value) {
            addCriterion("month >", value, "month");
            return (Criteria) this;
        }

        public Criteria andMonthGreaterThanOrEqualTo(String value) {
            addCriterion("month >=", value, "month");
            return (Criteria) this;
        }

        public Criteria andMonthLessThan(String value) {
            addCriterion("month <", value, "month");
            return (Criteria) this;
        }

        public Criteria andMonthLessThanOrEqualTo(String value) {
            addCriterion("month <=", value, "month");
            return (Criteria) this;
        }

        public Criteria andMonthLike(String value) {
            addCriterion("month like", value, "month");
            return (Criteria) this;
        }

        public Criteria andMonthNotLike(String value) {
            addCriterion("month not like", value, "month");
            return (Criteria) this;
        }

        public Criteria andMonthIn(List<String> values) {
            addCriterion("month in", values, "month");
            return (Criteria) this;
        }

        public Criteria andMonthNotIn(List<String> values) {
            addCriterion("month not in", values, "month");
            return (Criteria) this;
        }

        public Criteria andMonthBetween(String value1, String value2) {
            addCriterion("month between", value1, value2, "month");
            return (Criteria) this;
        }

        public Criteria andMonthNotBetween(String value1, String value2) {
            addCriterion("month not between", value1, value2, "month");
            return (Criteria) this;
        }

        public Criteria andDayIsNull() {
            addCriterion("day is null");
            return (Criteria) this;
        }

        public Criteria andDayIsNotNull() {
            addCriterion("day is not null");
            return (Criteria) this;
        }

        public Criteria andDayEqualTo(String value) {
            addCriterion("day =", value, "day");
            return (Criteria) this;
        }

        public Criteria andDayNotEqualTo(String value) {
            addCriterion("day <>", value, "day");
            return (Criteria) this;
        }

        public Criteria andDayGreaterThan(String value) {
            addCriterion("day >", value, "day");
            return (Criteria) this;
        }

        public Criteria andDayGreaterThanOrEqualTo(String value) {
            addCriterion("day >=", value, "day");
            return (Criteria) this;
        }

        public Criteria andDayLessThan(String value) {
            addCriterion("day <", value, "day");
            return (Criteria) this;
        }

        public Criteria andDayLessThanOrEqualTo(String value) {
            addCriterion("day <=", value, "day");
            return (Criteria) this;
        }

        public Criteria andDayLike(String value) {
            addCriterion("day like", value, "day");
            return (Criteria) this;
        }

        public Criteria andDayNotLike(String value) {
            addCriterion("day not like", value, "day");
            return (Criteria) this;
        }

        public Criteria andDayIn(List<String> values) {
            addCriterion("day in", values, "day");
            return (Criteria) this;
        }

        public Criteria andDayNotIn(List<String> values) {
            addCriterion("day not in", values, "day");
            return (Criteria) this;
        }

        public Criteria andDayBetween(String value1, String value2) {
            addCriterion("day between", value1, value2, "day");
            return (Criteria) this;
        }

        public Criteria andDayNotBetween(String value1, String value2) {
            addCriterion("day not between", value1, value2, "day");
            return (Criteria) this;
        }

        public Criteria andDateIsNull() {
            addCriterion("date is null");
            return (Criteria) this;
        }

        public Criteria andDateIsNotNull() {
            addCriterion("date is not null");
            return (Criteria) this;
        }

        public Criteria andDateEqualTo(String value) {
            addCriterion("date =", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateNotEqualTo(String value) {
            addCriterion("date <>", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateGreaterThan(String value) {
            addCriterion("date >", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateGreaterThanOrEqualTo(String value) {
            addCriterion("date >=", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateLessThan(String value) {
            addCriterion("date <", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateLessThanOrEqualTo(String value) {
            addCriterion("date <=", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateLike(String value) {
            addCriterion("date like", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateNotLike(String value) {
            addCriterion("date not like", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateIn(List<String> values) {
            addCriterion("date in", values, "date");
            return (Criteria) this;
        }

        public Criteria andDateNotIn(List<String> values) {
            addCriterion("date not in", values, "date");
            return (Criteria) this;
        }

        public Criteria andDateBetween(String value1, String value2) {
            addCriterion("date between", value1, value2, "date");
            return (Criteria) this;
        }

        public Criteria andDateNotBetween(String value1, String value2) {
            addCriterion("date not between", value1, value2, "date");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIsNull() {
            addCriterion("createTime is null");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIsNotNull() {
            addCriterion("createTime is not null");
            return (Criteria) this;
        }

        public Criteria andCreatetimeEqualTo(Date value) {
            addCriterion("createTime =", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotEqualTo(Date value) {
            addCriterion("createTime <>", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThan(Date value) {
            addCriterion("createTime >", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("createTime >=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThan(Date value) {
            addCriterion("createTime <", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThanOrEqualTo(Date value) {
            addCriterion("createTime <=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIn(List<Date> values) {
            addCriterion("createTime in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotIn(List<Date> values) {
            addCriterion("createTime not in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeBetween(Date value1, Date value2) {
            addCriterion("createTime between", value1, value2, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotBetween(Date value1, Date value2) {
            addCriterion("createTime not between", value1, value2, "createtime");
            return (Criteria) this;
        }

        public Criteria andOther1IsNull() {
            addCriterion("other1 is null");
            return (Criteria) this;
        }

        public Criteria andOther1IsNotNull() {
            addCriterion("other1 is not null");
            return (Criteria) this;
        }

        public Criteria andOther1EqualTo(Integer value) {
            addCriterion("other1 =", value, "other1");
            return (Criteria) this;
        }

        public Criteria andOther1NotEqualTo(Integer value) {
            addCriterion("other1 <>", value, "other1");
            return (Criteria) this;
        }

        public Criteria andOther1GreaterThan(Integer value) {
            addCriterion("other1 >", value, "other1");
            return (Criteria) this;
        }

        public Criteria andOther1GreaterThanOrEqualTo(Integer value) {
            addCriterion("other1 >=", value, "other1");
            return (Criteria) this;
        }

        public Criteria andOther1LessThan(Integer value) {
            addCriterion("other1 <", value, "other1");
            return (Criteria) this;
        }

        public Criteria andOther1LessThanOrEqualTo(Integer value) {
            addCriterion("other1 <=", value, "other1");
            return (Criteria) this;
        }

        public Criteria andOther1In(List<Integer> values) {
            addCriterion("other1 in", values, "other1");
            return (Criteria) this;
        }

        public Criteria andOther1NotIn(List<Integer> values) {
            addCriterion("other1 not in", values, "other1");
            return (Criteria) this;
        }

        public Criteria andOther1Between(Integer value1, Integer value2) {
            addCriterion("other1 between", value1, value2, "other1");
            return (Criteria) this;
        }

        public Criteria andOther1NotBetween(Integer value1, Integer value2) {
            addCriterion("other1 not between", value1, value2, "other1");
            return (Criteria) this;
        }

        public Criteria andOther2IsNull() {
            addCriterion("other2 is null");
            return (Criteria) this;
        }

        public Criteria andOther2IsNotNull() {
            addCriterion("other2 is not null");
            return (Criteria) this;
        }

        public Criteria andOther2EqualTo(Integer value) {
            addCriterion("other2 =", value, "other2");
            return (Criteria) this;
        }

        public Criteria andOther2NotEqualTo(Integer value) {
            addCriterion("other2 <>", value, "other2");
            return (Criteria) this;
        }

        public Criteria andOther2GreaterThan(Integer value) {
            addCriterion("other2 >", value, "other2");
            return (Criteria) this;
        }

        public Criteria andOther2GreaterThanOrEqualTo(Integer value) {
            addCriterion("other2 >=", value, "other2");
            return (Criteria) this;
        }

        public Criteria andOther2LessThan(Integer value) {
            addCriterion("other2 <", value, "other2");
            return (Criteria) this;
        }

        public Criteria andOther2LessThanOrEqualTo(Integer value) {
            addCriterion("other2 <=", value, "other2");
            return (Criteria) this;
        }

        public Criteria andOther2In(List<Integer> values) {
            addCriterion("other2 in", values, "other2");
            return (Criteria) this;
        }

        public Criteria andOther2NotIn(List<Integer> values) {
            addCriterion("other2 not in", values, "other2");
            return (Criteria) this;
        }

        public Criteria andOther2Between(Integer value1, Integer value2) {
            addCriterion("other2 between", value1, value2, "other2");
            return (Criteria) this;
        }

        public Criteria andOther2NotBetween(Integer value1, Integer value2) {
            addCriterion("other2 not between", value1, value2, "other2");
            return (Criteria) this;
        }

        public Criteria andOther3IsNull() {
            addCriterion("other3 is null");
            return (Criteria) this;
        }

        public Criteria andOther3IsNotNull() {
            addCriterion("other3 is not null");
            return (Criteria) this;
        }

        public Criteria andOther3EqualTo(String value) {
            addCriterion("other3 =", value, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3NotEqualTo(String value) {
            addCriterion("other3 <>", value, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3GreaterThan(String value) {
            addCriterion("other3 >", value, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3GreaterThanOrEqualTo(String value) {
            addCriterion("other3 >=", value, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3LessThan(String value) {
            addCriterion("other3 <", value, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3LessThanOrEqualTo(String value) {
            addCriterion("other3 <=", value, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3Like(String value) {
            addCriterion("other3 like", value, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3NotLike(String value) {
            addCriterion("other3 not like", value, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3In(List<String> values) {
            addCriterion("other3 in", values, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3NotIn(List<String> values) {
            addCriterion("other3 not in", values, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3Between(String value1, String value2) {
            addCriterion("other3 between", value1, value2, "other3");
            return (Criteria) this;
        }

        public Criteria andOther3NotBetween(String value1, String value2) {
            addCriterion("other3 not between", value1, value2, "other3");
            return (Criteria) this;
        }

        public Criteria andOther4IsNull() {
            addCriterion("other4 is null");
            return (Criteria) this;
        }

        public Criteria andOther4IsNotNull() {
            addCriterion("other4 is not null");
            return (Criteria) this;
        }

        public Criteria andOther4EqualTo(String value) {
            addCriterion("other4 =", value, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4NotEqualTo(String value) {
            addCriterion("other4 <>", value, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4GreaterThan(String value) {
            addCriterion("other4 >", value, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4GreaterThanOrEqualTo(String value) {
            addCriterion("other4 >=", value, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4LessThan(String value) {
            addCriterion("other4 <", value, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4LessThanOrEqualTo(String value) {
            addCriterion("other4 <=", value, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4Like(String value) {
            addCriterion("other4 like", value, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4NotLike(String value) {
            addCriterion("other4 not like", value, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4In(List<String> values) {
            addCriterion("other4 in", values, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4NotIn(List<String> values) {
            addCriterion("other4 not in", values, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4Between(String value1, String value2) {
            addCriterion("other4 between", value1, value2, "other4");
            return (Criteria) this;
        }

        public Criteria andOther4NotBetween(String value1, String value2) {
            addCriterion("other4 not between", value1, value2, "other4");
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