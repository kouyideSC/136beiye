package com.caipiao.domain.vo;

import java.io.Serializable;

/**
 * 前端展示-竞彩篮球赛果对象
 * Created by kouyi on 2017/10/23.
 */
public class JclqResultVo implements Serializable {
    private static final long serialVersionUID = 4694857848150839212L;
    private String name;//赛事名称
    private String period;//期次号（不返回前端）
    private String mid;//周几+竞彩编号
    private String hname;//主队名称
    private String gname;//客队名称
    private String mtime;//比赛时间
    private String score;//全场比分
    private String sfr;//胜负赛果
    private String sfs;//胜负赛果赔率
    private String rfsfr;//让分胜负赛果
    private String rfsfs;//让分胜负赛果赔率
    private String sfcr;//胜分差赛果
    private String sfcs;//胜分差赛果赔率
    private String dxfr;//大小分赛果
    private String dxfs;//大小分赛果赔率

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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSfr() {
        return sfr;
    }

    public void setSfr(String sfr) {
        this.sfr = sfr;
    }

    public String getSfs() {
        return sfs;
    }

    public void setSfs(String sfs) {
        this.sfs = sfs;
    }

    public String getRfsfr() {
        return rfsfr;
    }

    public void setRfsfr(String rfsfr) {
        this.rfsfr = rfsfr;
    }

    public String getRfsfs() {
        return rfsfs;
    }

    public void setRfsfs(String rfsfs) {
        this.rfsfs = rfsfs;
    }

    public String getSfcr() {
        return sfcr;
    }

    public void setSfcr(String sfcr) {
        this.sfcr = sfcr;
    }

    public String getSfcs() {
        return sfcs;
    }

    public void setSfcs(String sfcs) {
        this.sfcs = sfcs;
    }

    public String getDxfr() {
        return dxfr;
    }

    public void setDxfr(String dxfr) {
        this.dxfr = dxfr;
    }

    public String getDxfs() {
        return dxfs;
    }

    public void setDxfs(String dxfs) {
        this.dxfs = dxfs;
    }
}
