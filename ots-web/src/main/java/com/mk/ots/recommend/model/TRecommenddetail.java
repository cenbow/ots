package com.mk.ots.recommend.model;

import java.util.Date;

public class TRecommenddetail {
    private Long id;

    private String title;

    private String subtitle;

    private String author;

    private String topimage;

    private Date createtime;

    private Date pubtime;

    private Long recommendid;

    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    public String getTopimage() {
        return topimage;
    }

    public void setTopimage(String topimage) {
        this.topimage = topimage == null ? null : topimage.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getPubtime() {
        return pubtime;
    }

    public void setPubtime(Date pubtime) {
        this.pubtime = pubtime;
    }

    public Long getRecommendid() {
        return recommendid;
    }

    public void setRecommendid(Long recommendid) {
        this.recommendid = recommendid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}