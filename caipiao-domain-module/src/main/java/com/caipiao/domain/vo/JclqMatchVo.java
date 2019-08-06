package com.caipiao.domain.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 前端展示-竞彩篮球比赛对象
 * Created by kouyi on 2017/10/23.
 */
public class JclqMatchVo implements Serializable {
    private static final long serialVersionUID = 8057122883590076769L;
    private String name;//赛事名称
    private String color;//赛事颜色
    private String period;//期次号
    private String mid;//周几+竞彩编号
    private String mcode;//竞彩场次号（如20171102001）
    private String hname;//主队名称
    private String gname;//客队名称
    private String rf;//让分值
    private String dx;//大小分
    private String etime;//截止时间
    private Integer s1;//单关-胜负过关玩法状态（-1-未开玩法 1-销售中）
    private Integer s2;//单关-让分胜负玩法状态（-1-未开玩法 1-销售中）
    private Integer s3;//单关-大小分玩法状态（-1-未开玩法 1-销售中）
    private Integer s4;//单关-胜分差玩法状态（-1-未开玩法 1-销售中）
    private Integer oid;//外部关联比赛ID
    private Boolean hot;//是否热门赛事(0-否 1-是)
    private String sf;//胜负赔率
    private String rfsf;//让分胜负赔率
    private String dxf;//大小分赔率
    private String sfc;//胜分差赔率
    private Date matchTime;//比赛时间

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public String getMcode() {
        return mcode;
    }

    public void setMcode(String mcode) {
        this.mcode = mcode;
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

    public String getRf() {
        return rf;
    }

    public void setRf(String rf) {
        this.rf = rf;
    }

    public String getDx() {
        return dx;
    }

    public void setDx(String dx) {
        this.dx = dx;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public Integer getS1() {
        return s1;
    }

    public void setS1(Integer s1) {
        this.s1 = s1;
    }

    public Integer getS2() {
        return s2;
    }

    public void setS2(Integer s2) {
        this.s2 = s2;
    }

    public Integer getS3() {
        return s3;
    }

    public void setS3(Integer s3) {
        this.s3 = s3;
    }

    public Integer getS4() {
        return s4;
    }

    public void setS4(Integer s4) {
        this.s4 = s4;
    }

    public Integer getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public Boolean getHot() {
        return hot;
    }

    public void setHot(Boolean hot) {
        this.hot = hot;
    }

    public String getSf() {
        return sf;
    }

    public void setSf(String sf) {
        this.sf = sf;
    }

    public String getRfsf() {
        return rfsf;
    }

    public void setRfsf(String rfsf) {
        this.rfsf = rfsf;
    }

    public String getDxf() {
        return dxf;
    }

    public void setDxf(String dxf) {
        this.dxf = dxf;
    }

    public String getSfc() {
        return sfc;
    }

    public void setSfc(String sfc) {
        this.sfc = sfc;
    }

    public Date getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(Date matchTime) {
        this.matchTime = matchTime;
    }
}
