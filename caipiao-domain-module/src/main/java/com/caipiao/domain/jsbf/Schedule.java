package com.caipiao.domain.jsbf;

import java.io.Serializable;
import java.util.Date;

/**
 * 即时比分对阵表
 * @user kouyi
 * @date 2017-11-20
 */
public class Schedule implements Serializable, Comparable<Schedule> {
	private static final long serialVersionUID = -5228446511537214896L;
	private Long id;
	private String scheduleId;//场次id
    private String league;//联赛名称
	private String period;//期次编号
	private String speriod;//期次编号简称(期次的简化,例如期次20140416)
	private String jcId;//竞彩编号
	private String week;//周几
    private Long homeTeamId; //主队id
    private Long guestTeamId; //客队id
    private String homeTeam; //主队名称
    private String guestTeam; //客队名称
    private Boolean neutrality; //中立场默认否
    private Date matchTime; //开赛时间
    private Date beginTime; //上下半场开赛时间
    private String homeOrder; //主队排名
    private String guestOrder; //客队排名
    private Integer matchState; //比赛状态 0:未开,1:上半场,2:中场,3:下半场,4,加时，-11:待定,-12:腰斩,-13:中断,-14:推迟,-1:完场，-10取消
    private Integer homeScore; //主队比分
    private Integer guestScore; //客队比分
    private Integer homeHalfScore; //主队半场比分
    private Integer guestHalfScore; //客队半场比分
    private Integer homeRed; //主队红牌数
    private Integer guestRed; //客队红牌数
    private Integer homeYellow; //主队黄牌数
    private Integer guestYellow; //客队黄牌数
    private String remark; //比分说明
    private Date updateTime;
    private Integer flag; //标识位

	public Schedule(){}
	public Schedule(String scheduleId){
		this.scheduleId = scheduleId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getLeague() {
		return league;
	}

	public void setLeague(String league) {
		this.league = league;
	}

	public Long getHomeTeamId() {
		return homeTeamId;
	}

	public void setHomeTeamId(Long homeTeamId) {
		this.homeTeamId = homeTeamId;
	}

	public Long getGuestTeamId() {
		return guestTeamId;
	}

	public void setGuestTeamId(Long guestTeamId) {
		this.guestTeamId = guestTeamId;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getGuestTeam() {
		return guestTeam;
	}

	public void setGuestTeam(String guestTeam) {
		this.guestTeam = guestTeam;
	}

	public Boolean getNeutrality() {
		return neutrality;
	}

	public void setNeutrality(Boolean neutrality) {
		this.neutrality = neutrality;
	}

	public Date getMatchTime() {
		return matchTime;
	}

	public void setMatchTime(Date matchTime) {
		this.matchTime = matchTime;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public String getHomeOrder() {
		return homeOrder;
	}

	public void setHomeOrder(String homeOrder) {
		this.homeOrder = homeOrder;
	}

	public String getGuestOrder() {
		return guestOrder;
	}

	public void setGuestOrder(String guestOrder) {
		this.guestOrder = guestOrder;
	}

	public Integer getMatchState() {
		return matchState;
	}

	public void setMatchState(Integer matchState) {
		this.matchState = matchState;
	}

	public Integer getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	public Integer getGuestScore() {
		return guestScore;
	}

	public void setGuestScore(Integer guestScore) {
		this.guestScore = guestScore;
	}

	public Integer getHomeHalfScore() {
		return homeHalfScore;
	}

	public void setHomeHalfScore(Integer homeHalfScore) {
		this.homeHalfScore = homeHalfScore;
	}

	public Integer getGuestHalfScore() {
		return guestHalfScore;
	}

	public void setGuestHalfScore(Integer guestHalfScore) {
		this.guestHalfScore = guestHalfScore;
	}

	public Integer getHomeRed() {
		return homeRed;
	}

	public void setHomeRed(Integer homeRed) {
		this.homeRed = homeRed;
	}

	public Integer getGuestRed() {
		return guestRed;
	}

	public void setGuestRed(Integer guestRed) {
		this.guestRed = guestRed;
	}

	public Integer getHomeYellow() {
		return homeYellow;
	}

	public void setHomeYellow(Integer homeYellow) {
		this.homeYellow = homeYellow;
	}

	public Integer getGuestYellow() {
		return guestYellow;
	}

	public void setGuestYellow(Integer guestYellow) {
		this.guestYellow = guestYellow;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getSperiod() {
		return speriod;
	}

	public void setSperiod(String speriod) {
		this.speriod = speriod;
	}

	public String getJcId() {
		return jcId;
	}

	public void setJcId(String jcId) {
		this.jcId = jcId;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public int compareTo(Schedule o) {
		int m = 0;
		if(o.getMatchState() < 0) {
			m = Integer.valueOf(Math.abs(this.matchState.intValue())).compareTo(Integer.valueOf(Math.abs(o.getMatchState().intValue())));
			if(m == 0) {
				m = this.jcId.compareTo(o.getJcId());
			}
		} else {
			m = this.jcId.compareTo(o.getJcId());
		}
		return m;
	}
	
}