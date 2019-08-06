package com.caipiao.domain.jjyh;

import com.caipiao.domain.cpadmin.Dto;

import java.io.Serializable;

/**
 * 场次信息对象-奖金优化使用
 * Created by Kouyi on 2018/5/4.
 */
public class MatchInfo implements Serializable {
    private static final long serialVersionUID = 990431560630548415L;
    private String matchCode;//场次号
    private String hostName;//主队名
    private String leagueName;//赛事名
    private String sp;//赔率sp
    private String jcId;//竞彩编号
    private String choose;//玩法选项
    private String chooseDesc;//玩法选项描述
    private double rqf;//让球/分数
    private String filterFlag;//选项过滤标记
    private Dto matchSp;//场次赔率对象

    public String getFilterFlag() {
        return filterFlag;
    }

    public void setFilterFlag(String filterFlag) {
        this.filterFlag = filterFlag;
    }

    public double getRqf() {
        return rqf;
    }

    public void setRqf(double rqf) {
        this.rqf = rqf;
    }

    public Dto getMatchSp() {
        return matchSp;
    }

    public void setMatchSp(Dto matchSp) {
        this.matchSp = matchSp;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getJcId() {
        return jcId;
    }

    public void setJcId(String jcId) {
        this.jcId = jcId;
    }

    public String getChoose() {
        return choose;
    }

    public void setChoose(String choose) {
        this.choose = choose;
    }

    public String getChooseDesc() {
        return chooseDesc;
    }

    public void setChooseDesc(String chooseDesc) {
        this.chooseDesc = chooseDesc;
    }

}