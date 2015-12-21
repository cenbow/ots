package com.mk.ots.recommend.model;

import java.util.Date;

public class RecommendList {
	private String name;//‘功能名称’, 
	private String description;//  banner内容描述, //描述
	private String imgurl;//http://imgurl, //图片地址
    private String url;//,//功能跳转url
    private Long detailid;//：//发现页唯一id
    private Integer querytype;//是否访问url  1:详情 ,2 走Link
    private Date createtime;
	private Integer sort;
    
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getDetailid() {
		return detailid;
	}
	public void setDetailid(Long detailid) {
		this.detailid = detailid;
	}
	public Integer getQuerytype() {
		return querytype;
	}
	public void setQuerytype(Integer querytype) {
		this.querytype = querytype;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
}
