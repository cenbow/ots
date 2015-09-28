package com.mk.ots.common.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author shellingford
 * @version 创建时间：2012-3-29 下午1:57:41
 * @param <W>
 * 
 */
public class PageObject<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long count;
	private List<T> list;
	private String content;
	
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public PageObject(List<T> list,Long count) {
		super();
		this.count = count;
		this.list = list;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
