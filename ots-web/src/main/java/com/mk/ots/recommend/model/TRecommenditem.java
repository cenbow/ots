package com.mk.ots.recommend.model;

import java.util.Date;

public class TRecommenditem {
    private Long id;

    private Long positionid;

    private String title;

    private String subtitle;

    private String imageurl;

    private String link;

    private Integer viewtype;

    private Date createtime;

    private Integer sort;

    private String deleted;

    private Integer status;

    private Date starttime;

    private Date endtime;

    private String description;
    
    private Long detailid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPositionid() {
        return positionid;
    }

    public void setPositionid(Long positionid) {
        this.positionid = positionid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle == null ? null : subtitle.trim();
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl == null ? null : imageurl.trim();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link == null ? null : link.trim();
    }

    public Integer getViewtype() {
        return viewtype;
    }

    public void setViewtype(Integer viewtype) {
        this.viewtype = viewtype;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted == null ? null : deleted.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

	public Long getDetailid() {
		return detailid;
	}

	public void setDetailid(Long detailid) {
		this.detailid = detailid;
	}
    
    
}