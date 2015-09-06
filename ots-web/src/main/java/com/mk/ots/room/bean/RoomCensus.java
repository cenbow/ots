package com.mk.ots.room.bean;

import java.util.Date;

public class RoomCensus {
    private Long id;

    private Long hotelid;

    private String hotelname;

    private String visible;

    private String online;

    private Integer roomcount;

    private Integer freeroomcount;

    private String year;

    private String month;

    private String day;

    private String date;

    private Date createtime;

    private Integer other1;

    private Integer other2;

    private String other3;

    private String other4;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelid() {
        return hotelid;
    }

    public void setHotelid(Long hotelid) {
        this.hotelid = hotelid;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname == null ? null : hotelname.trim();
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible == null ? null : visible.trim();
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online == null ? null : online.trim();
    }

    public Integer getRoomcount() {
        return roomcount;
    }

    public void setRoomcount(Integer roomcount) {
        this.roomcount = roomcount;
    }

    public Integer getFreeroomcount() {
        return freeroomcount;
    }

    public void setFreeroomcount(Integer freeroomcount) {
        this.freeroomcount = freeroomcount;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year == null ? null : year.trim();
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month == null ? null : month.trim();
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day == null ? null : day.trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getOther1() {
        return other1;
    }

    public void setOther1(Integer other1) {
        this.other1 = other1;
    }

    public Integer getOther2() {
        return other2;
    }

    public void setOther2(Integer other2) {
        this.other2 = other2;
    }

    public String getOther3() {
        return other3;
    }

    public void setOther3(String other3) {
        this.other3 = other3 == null ? null : other3.trim();
    }

    public String getOther4() {
        return other4;
    }

    public void setOther4(String other4) {
        this.other4 = other4 == null ? null : other4.trim();
    }

	@Override
	public String toString() {
		return "RoomCensus [id=" + id + ", hotelid=" + hotelid + ", hotelname="
				+ hotelname + ", visible=" + visible + ", online=" + online
				+ ", roomcount=" + roomcount + ", freeroomcount="
				+ freeroomcount + ", year=" + year + ", month=" + month
				+ ", day=" + day + ", date=" + date + ", createtime="
				+ createtime + "]";
	}
}