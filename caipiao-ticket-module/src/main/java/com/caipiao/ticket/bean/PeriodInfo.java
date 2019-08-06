package com.caipiao.ticket.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 期次对象
 * Created by kouyi on 2017/12/11
 */
public class PeriodInfo implements Serializable {
	private static final long serialVersionUID = 8380835172619349434L;
	private String gid ;//彩种
	private String periodID;//期次编号
	private Date beginTime;//期次开始时间
	private Date endTime ;//期次结束时间
	private Date awardTime;//期次开奖时间

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getPeriodID() {
		return periodID;
	}

	public void setPeriodID(String periodID) {
		this.periodID = periodID;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getAwardTime() {
		return awardTime;
	}

	public void setAwardTime(Date awardTime) {
		this.awardTime = awardTime;
	}

	public String toString(){
		return "彩种=" + gid + ";期次=" + periodID + ";开始时间=" + beginTime +";截止时间=" + endTime + "开奖时间=" + awardTime;
	}
}
