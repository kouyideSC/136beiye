package com.caipiao.domain.match;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 足球对阵赛果对象
 * Created by kouyi on 2017/11/04.
 */
public class MatchFootBallResult implements Serializable {
    private Long id;
    private String leagueName;//赛事名称
    private Long matchId;//足球对阵表id号
    private String matchCode;//竞彩场次号（如20171102001）
    private String jcId;//竞彩编号（如001）
    private String period;//期次编号
    private String hostName;//主队名称
    private String guestName;//客队名称
    private Integer lose;//让球数
    private Date matchTime;//比赛时间
    private String halfScore;//半场比分
    private String score;//全场比分
    private Integer status;//销售状态（-1:已取消 0-:已停售 1-:销售中 2-:已截止）
    private String spfResult;//胜平负赛果
    private Double spfSp;//胜平负赛果赔率
    private String rqspfResult;//让球胜平负赛果
    private Double rqspfSp;//让球胜平负赛果赔率
    private String zjqResult;//总进球赛果
    private Double zjqSp;//总进球赛果赔率
    private String bqcResult;//半全场赛果
    private Double bqcSp;//半全场赔率
    private String bfResult;//比分赛果
    private Double bfSp;//比分赛果赔率
    private Date createTime;//赛果入库时间
    private Date updateTime;//更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private List<String> pageList;//抓取数据时记录页数

    public MatchFootBallResult(){}

    public MatchFootBallResult(List<String> pageList){
        this.pageList = pageList;
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

    public Integer getLose() {
        return lose;
    }

    public void setLose(Integer lose) {
        this.lose = lose;
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

    public String getSpfResult() {
        return spfResult;
    }

    public void setSpfResult(String spfResult) {
        this.spfResult = spfResult;
    }

    public Double getSpfSp() {
        return spfSp;
    }

    public void setSpfSp(Double spfSp) {
        this.spfSp = spfSp;
    }

    public String getRqspfResult() {
        return rqspfResult;
    }

    public void setRqspfResult(String rqspfResult) {
        this.rqspfResult = rqspfResult;
    }

    public Double getRqspfSp() {
        return rqspfSp;
    }

    public void setRqspfSp(Double rqspfSp) {
        this.rqspfSp = rqspfSp;
    }

    public String getZjqResult() {
        return zjqResult;
    }

    public void setZjqResult(String zjqResult) {
        this.zjqResult = zjqResult;
    }

    public Double getZjqSp() {
        return zjqSp;
    }

    public void setZjqSp(Double zjqSp) {
        this.zjqSp = zjqSp;
    }

    public String getBqcResult() {
        return bqcResult;
    }

    public void setBqcResult(String bqcResult) {
        this.bqcResult = bqcResult;
    }

    public Double getBqcSp() {
        return bqcSp;
    }

    public void setBqcSp(Double bqcSp) {
        this.bqcSp = bqcSp;
    }

    public String getBfResult() {
        return bfResult;
    }

    public void setBfResult(String bfResult) {
        this.bfResult = bfResult;
    }

    public Double getBfSp() {
        return bfSp;
    }

    public void setBfSp(Double bfSp) {
        this.bfSp = bfSp;
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