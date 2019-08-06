package com.caipiao.domain.vo;

import java.io.Serializable;

/**
 * 前端展示-竞彩足球赛果对象
 * Created by kouyi on 2017/10/23.
 */
public class JczqResultVo implements Serializable {
    private static final long serialVersionUID = 4694857848150839212L;
    private String name;//赛事名称
    private String period;//期次号（不返回前端）
    private String mid;//周几+竞彩编号
    private String hname;//主队名称
    private String gname;//客队名称
    private String mtime;//比赛时间
    private String hscore;//半场比分
    private String score;//全场比分
    private String spfr;//胜平负赛果
    private String spfs;//胜平负赛果赔率
    private String rqspfr;//让球胜平负赛果
    private String rqspfs;//让球胜平负赛果赔率
    private String zjqr;//总进球赛果
    private String zjqs;//总进球赛果赔率
    private String bqcr;//半全场赛果
    private String bqcs;//半全场赔率
    private String bfr;//比分赛果
    private String bfs;//比分赛果赔率

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getHscore() {
        return hscore;
    }

    public void setHscore(String hscore) {
        this.hscore = hscore;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSpfr() {
        return spfr;
    }

    public void setSpfr(String spfr) {
        this.spfr = spfr;
    }

    public String getSpfs() {
        return spfs;
    }

    public void setSpfs(String spfs) {
        this.spfs = spfs;
    }

    public String getRqspfr() {
        return rqspfr;
    }

    public void setRqspfr(String rqspfr) {
        this.rqspfr = rqspfr;
    }

    public String getRqspfs() {
        return rqspfs;
    }

    public void setRqspfs(String rqspfs) {
        this.rqspfs = rqspfs;
    }

    public String getZjqr() {
        return zjqr;
    }

    public void setZjqr(String zjqr) {
        this.zjqr = zjqr;
    }

    public String getZjqs() {
        return zjqs;
    }

    public void setZjqs(String zjqs) {
        this.zjqs = zjqs;
    }

    public String getBqcr() {
        return bqcr;
    }

    public void setBqcr(String bqcr) {
        this.bqcr = bqcr;
    }

    public String getBqcs() {
        return bqcs;
    }

    public void setBqcs(String bqcs) {
        this.bqcs = bqcs;
    }

    public String getBfr() {
        return bfr;
    }

    public void setBfr(String bfr) {
        this.bfr = bfr;
    }

    public String getBfs() {
        return bfs;
    }

    public void setBfs(String bfs) {
        this.bfs = bfs;
    }
}
