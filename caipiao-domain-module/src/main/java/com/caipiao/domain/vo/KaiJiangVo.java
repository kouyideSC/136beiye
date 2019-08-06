package com.caipiao.domain.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 开奖对象
 * @author  mcdog
 */
public class KaiJiangVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String lid;//彩种id(编号)
    private String lname;//彩种名称
    private String pid;//期次号
    private String pname;//期次名称
    private String ktime;//开奖时间
    private String kcode;//开奖号码
    private String hname;//主队名称
    private String gname;//客队名称
    private String bf;//比分
    private int xh;//显示顺序号

    public KaiJiangVo(){}

    public KaiJiangVo(String lid)
    {
        this.lid = lid;
    }

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

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getKtime() {
        return ktime;
    }

    public void setKtime(String ktime) {
        this.ktime = ktime;
    }

    public String getKcode() {
        return kcode;
    }

    public void setKcode(String kcode) {
        this.kcode = kcode;
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

    public String getBf() {
        return bf;
    }

    public void setBf(String bf) {
        this.bf = bf;
    }

    public int getXh() {
        return xh;
    }

    public void setXh(int xh) {
        this.xh = xh;
    }
}