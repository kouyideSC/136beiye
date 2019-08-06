package com.caipiao.grab.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 即时比分-比赛信息对象
 * Created by Kouyi on 2017/11/21.
 */
public class JsbfScheduleVO implements Serializable, Comparable<JsbfScheduleVO> {
    private static final long serialVersionUID = -8810763674842842470L;
    private String sid;//场次id
    private String league;//联赛名称
    private String period;//期次编号
    private String mid;//竞彩编号
    private String week;//周几
    private String hname;//主队名称
    private String gname;//客队名称
    private Boolean haddle=false;//中立场默认否
    private String stime;//开赛时间
    private Date btime;//上下半场开赛时间
    private Integer state;//比赛状态 0:未开,1:上半场,2:中场,3:下半场,4,加时，-11:待定,-12:腰斩,-13:中断,-14:推迟,-1:完场，-10取消
    private String score;//全场比分
    private String hscore;//半场比分
    private String sdesc;//描述

    public String getSdesc() {
        return sdesc;
    }

    public void setSdesc(String sdesc) {
        this.sdesc = sdesc;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public Boolean getHaddle() {
        return haddle;
    }

    public void setHaddle(Boolean haddle) {
        this.haddle = haddle;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public Date getBtime() {
        return btime;
    }

    public void setBtime(Date btime) {
        this.btime = btime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getHscore() {
        return hscore;
    }

    public void setHscore(String hscore) {
        this.hscore = hscore;
    }

    public int compareTo(JsbfScheduleVO o) {
        int m = 0;
        if(o.getState() < 0) {
            m = Integer.valueOf(Math.abs(this.state.intValue())).compareTo(Integer.valueOf(Math.abs(o.getState().intValue())));
            if(m == 0) {
                m = this.mid.compareTo(o.getMid());
            }
        } else {
            m = this.mid.compareTo(o.getMid());
        }
        return m;
    }
}
