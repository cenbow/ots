package com.mk.framework.schedule.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mk.framework.util.TFBooleanSerializer;

public class SchedulePlan {
	
	@JsonProperty("id")
    private Long id;

	@JsonProperty("name")
    private String name;

	@JsonFormat(pattern = "yyyyMMddHHmmss",timezone="GMT+8")
	@JsonProperty("startdate")
    private Date startdate;

	@JsonFormat(pattern = "yyyyMMddHHmmss",timezone="GMT+8")
	@JsonProperty("enddate")
    private Date enddate;

	@JsonProperty("script")
    private String script;

	@JsonProperty("expression")
    private String expression;

	@JsonProperty("type")
    private String type;

	@JsonSerialize(using=TFBooleanSerializer.class)
	@JsonProperty("active")
    private Boolean active;

	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script == null ? null : script.trim();
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression == null ? null : expression.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "SchedulePlan [id=" + id + ", name=" + name + ", startdate="
				+ startdate + ", enddate=" + enddate + ", script=" + script
				+ ", expression=" + expression + ", type=" + type + ", active="
				+ active + "]";
	}
}