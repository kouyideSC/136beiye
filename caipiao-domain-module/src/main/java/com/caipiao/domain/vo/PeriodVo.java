package com.caipiao.domain.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 前端展示-历史期次对象
 * @author  mcdog
 */
public class PeriodVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String lid;//彩种id(编号)
    private String lname;//彩种名称
    private String pid;//期次(期次全称)
    private String spid;//期次(期次简称)
    private String pname;//期次名称
    private String stime;//开售时间
    private String etime;//销售截止时间
    private String setime;//销售截止时间简称
    private String status;//销售状态 -1-已截止 0-未开售 1-销售中
    private String ktime;//开奖时间
    private String sktime;//开奖时间简称
    private String kcode;//开奖号码
    private String kstatus;//开奖状态 0-未开奖 1-已开奖
    private String dxScale;//大小比例
    private String joScale;//奇偶比例
    private Object matches;//对阵信息
    private List<Map<String,Object>> pclist;//奖池信息
    private List<Map<String,Object>> pglist;//奖级信息
    private String gc;//滚存

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSpid() {
        return spid;
    }

    public void setSpid(String spid) {
        this.spid = spid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public String getSetime() {
        return setime;
    }

    public void setSetime(String setime) {
        this.setime = setime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKtime() {
        return ktime;
    }

    public void setKtime(String ktime) {
        this.ktime = ktime;
    }

    public String getSktime() {
        return sktime;
    }

    public void setSktime(String sktime) {
        this.sktime = sktime;
    }

    public String getKcode() {
        return kcode;
    }

    public void setKcode(String kcode) {
        this.kcode = kcode;
    }

    public String getKstatus() {
        return kstatus;
    }

    public void setKstatus(String kstatus) {
        this.kstatus = kstatus;
    }

    public String getDxScale() {
        return dxScale;
    }

    public void setDxScale(String dxScale) {
        this.dxScale = dxScale;
    }

    public String getJoScale() {
        return joScale;
    }

    public void setJoScale(String joScale) {
        this.joScale = joScale;
    }

    public Object getMatches() {
        return matches;
    }

    public void setMatches(Object matches) {
        this.matches = matches;
    }

    public List<Map<String, Object>> getPclist() {
        return pclist;
    }

    public void setPclist(List<Map<String, Object>> pclist) {
        this.pclist = pclist;
    }

    public List<Map<String, Object>> getPglist() {
        return pglist;
    }

    public void setPglist(List<Map<String, Object>> pglist) {
        this.pglist = pglist;
    }

    public String getGc() {
        return gc;
    }

    public void setGc(String gc) {
        this.gc = gc;
    }
}