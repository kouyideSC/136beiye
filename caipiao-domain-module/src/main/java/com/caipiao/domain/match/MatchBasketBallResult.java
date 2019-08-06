package com.caipiao.domain.match;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 篮球对阵赛果对象
 * Created by kouyi on 2017/11/04.
 */
public class MatchBasketBallResult implements Serializable {
    private static final long serialVersionUID = 5108915663860775933L;
    private Long id;
    private String leagueName;//赛事名称
    private Long matchId;//足球对阵表id号
    private String matchCode;//竞彩场次号（如20171102001）
    private String jcId;//竞彩编号（如001）
    private String period;//期次编号
    private String hostName;//主队名称
    private String guestName;//客队名称
    private Date matchTime;//比赛时间
    private String halfScore;//半场比分
    private String score;//全场比分
    private Integer status;//销售状态（-1:已取消 0-:已停售 1-:销售中 2-:已截止）
    private String sfResult;//胜负赛果
    private Double sfSp;//胜负赛果赔率
    private String rfsfResult;//让分胜负赛果
    private Double rfsfSp;//让分胜负赛果赔率
    private Double lose;//让分值
    private String sfcResult;//胜分差赛果
    private Double sfcSp;//胜分差赛果赔率
    private String dxfResult;//大小分赛果
    private Double dxfSp;//大小分赛果赔率
    private Double dxf;//大小分值
    private Date createTime;//赛果入库时间
    private Date updateTime;//更新时间

    private List<String> pageList;//抓取数据时记录页数

    public MatchBasketBallResult(){}

    public MatchBasketBallResult(List<String> pageList) {
        this.pageList = pageList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getJcId() {
        return jcId;
    }

    public void setJcId(String jcId) {
        this.jcId = jcId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public Date getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(Date matchTime) {
        this.matchTime = matchTime;
    }

    public String getHalfScore() {
        return halfScore;
    }

    public void setHalfScore(String halfScore) {
        this.halfScore = halfScore;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSfResult() {
        return sfResult;
    }

    public void setSfResult(String sfResult) {
        this.sfResult = sfResult;
    }

    public Double getSfSp() {
        return sfSp;
    }

    public void setSfSp(Double sfSp) {
        this.sfSp = sfSp;
    }

    public String getRfsfResult() {
        return rfsfResult;
    }

    public void setRfsfResult(String rfsfResult) {
        this.rfsfResult = rfsfResult;
    }

    public Double getRfsfSp() {
        return rfsfSp;
    }

    public void setRfsfSp(Double rfsfSp) {
        this.rfsfSp = rfsfSp;
    }

    public Double getLose() {
        return lose;
    }

    public void setLose(Double lose) {
        this.lose = lose;
    }

    public String getSfcResult() {
        return sfcResult;
    }

    public void setSfcResult(String sfcResult) {
        this.sfcResult = sfcResult;
    }

    public Double getSfcSp() {
        return sfcSp;
    }

    public void setSfcSp(Double sfcSp) {
        this.sfcSp = sfcSp;
    }

    public String getDxfResult() {
        return dxfResult;
    }

    public void setDxfResult(String dxfResult) {
        this.dxfResult = dxfResult;
    }

    public Double getDxfSp() {
        return dxfSp;
    }

    public void setDxfSp(Double dxfSp) {
        this.dxfSp = dxfSp;
    }

    public Double getDxf() {
        return dxf;
    }

    public void setDxf(Double dxf) {
        this.dxf = dxf;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<String> getPageList() {
        return pageList;
    }

    public void setPageList(List<String> pageList) {
        this.pageList = pageList;
    }
}