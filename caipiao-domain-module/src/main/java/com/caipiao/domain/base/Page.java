package com.caipiao.domain.base;

import java.io.Serializable;

/**
 * 分页对象
 */
public class Page implements Serializable {
    private static final long serialVersionUID = -6502252345938090105L;
    private int pageStart = 0; //开始位置
	private int pageSize = 15; //单页条数
	private int pageSum = 1; //总页数
	private int pageNo = 1; //当前页码

    public int getPageSum() {
        return pageSum;
    }
    public void setPageSum(int pageSum) {
        this.pageSum = pageSum;
    }

    public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
		this.pageStart = (pageNo-1)*pageSize;
	}

	public int getPageStart() {
		return pageStart;
	}
	public void setPageStart(int pageStart) {
		this.pageStart = pageStart;
	}

	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		this.pageStart = (pageNo-1)*pageSize;
	}
	
}
