/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Page is the result of Model.paginate(......) or Db.paginate(......)
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class Page<T> implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5395997221963176643L;
    private List<T> list;               // list result of this page
    private int pageNumber;             // page number
    private int pageSize;               // result amount of this page
    private int totalPage;              // total page
    private int totalRow;               // total row
    
    /**
     * Constructor.
     * @param list the list of paginate result
     * @param pageNumber the page number
     * @param pageSize the page size
     * @param totalPage the total page of paginate
     * @param totalRow the total row of paginate
     */
    public Page(List<T> list, int pageNumber, int pageSize, int totalPage, int totalRow) {
        this.list = list;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalRow = totalRow;
    }
    
    /**
     * Return list of this page.
     */
    public List<T> getList() {
        return list;
    }
    
    /**
     * Return page number.
     */
    public int getPageNumber() {
        return pageNumber;
    }
    
    /**
     * Return page size.
     */
    public int getPageSize() {
        return pageSize;
    }
    
    /**
     * Return total page.
     */
    public int getTotalPage() {
        return totalPage;
    }
    
    /**
     * Return total row.
     */
    public int getTotalRow() {
        return totalRow;
    }
}
