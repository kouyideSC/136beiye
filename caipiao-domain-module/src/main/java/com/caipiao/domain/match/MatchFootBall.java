package com.caipiao.domain.match;

import java.io.Serializable;
import java.util.Date;

/**
 * 足球对阵对象
 * Created by kouyi on 2017/11/04.
 */
public class MatchFootBall implements Serializable {
    private static final long serialVersionUID = -4839951548849739120L;
    private Long id;
    private String leagueName;//赛事名称
    private Integer leagueId;//赛事编号
    private String leagueColor;//赛事颜色
    private String jcId;//竞彩编号（如001）
    private String jcWebId;//竞彩官网唯一编号
    private String matchCode;//竞彩场次号（如20171102001）
    private String period;//期次编号
    private String weekDay;//周几
    private Integer hostTeamId;//主队编号
    private String hostName;//主队名称
    private Integer guestTeamId;//客队编号
    private String guestName;//客队名称
    private Integer lose;//让球数
    private Date matchTime;//比赛时间
    private String halfScore;//半场比分
    private String score;//全场比分
    private Integer status;//销售状态（-1:已取消 0-:已停售 1-:销售中 2-:已截止）
    private Boolean updateFlag;//手动更新标记(0-未手动更新 1-已手动更新)
    private Integer singleSpfStatus;//单关-胜平负玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer singleRqspfStatus;//单关-让球胜平负玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer singleZjqStatus;//单关-总进球玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer singleBfStatus;//单关-比分玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer singleBqcStatus;//单关-半全场玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer spfStatus;//胜平负玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer rqspfStatus;//让球胜平负玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer zjqStatus;//总进球玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer bfStatus;//比分玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer bqcStatus;//半全场玩法状态（-1-未开玩法 0-已停售 1-销售中）
    private Integer outMatchId;//外部关联比赛ID
    private Boolean isHot;//是否热门赛事(0-否 1-是)
    private Date endTime;//竞彩截止销售时间
    //对阵处理状态（0-待处理 1-自动撤单中 2-赛果获取中 3-已有赛果待审核 4-赛果人工审核成功 5-系统审核成功
    // 6-计算奖金成功 7-奖金汇总成功 8-奖金核对成功 9-自动派奖成功 10-过关统计完成 11-战绩统计完成
    // 12-派送返点成功 99-场次处理结束）
    private Integer state;
    private Date stateTime;//对阵状态处理时间
    private Date createTime;//场次入库时间
    private Date updateTime;//场次入库时间

    public MatchFootBall(){}

    public MatchFootBall(String matchCode){
        this.matchCode = matchCode;
    }

    public MatchFootBall(Date matchTime){
        this.matchTime = matchTime;
    }

    public MatchFootBall(Integer status, Integer state){
        this.status = status;
        this.state = state;
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

    public Integer getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public String getLeagueColor() {
        return leagueColor;
    }

    public void setLeagueColor(String leagueColor) {
        this.leagueColor = leagueColor;
    }

    public String getJcId() {
        return jcId;
    }

    public void setJcId(String jcId) {
        this.jcId = jcId;
    }

    public String getJcWebId() {
        return jcWebId;
    }

    public void setJcWebId(String jcWebId) {
        this.jcWebId = jcWebId;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getHostTeamId() {
        return hostTeamId;
    }

    public void setHostTeamId(Integer hostTeamId) {
        this.hostTeamId = hostTeamId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getGuestTeamId() {
        return guestTeamId;
    }

    public void setGuestTeamId(Integer guestTeamId) {
        this.guestTeamId = guestTeamId;
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

    public Boolean getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(Boolean updateFlag) {
        this.updateFlag = updateFlag;
    }

    public Integer getSingleSpfStatus() {
        return singleSpfStatus;
    }

    public void setSingleSpfStatus(Integer singleSpfStatus) {
        this.singleSpfStatus = singleSpfStatus;
    }

    public Integer getSingleRqspfStatus() {
        return singleRqspfStatus;
    }

    public void setSingleRqspfStatus(Integer singleRqspfStatus) {
        this.singleRqspfStatus = singleRqspfStatus;
    }

    public Integer getSingleZjqStatus() {
        return singleZjqStatus;
    }

    public void setSingleZjqStatus(Integer singleZjqStatus) {
        this.singleZjqStatus = singleZjqStatus;
    }

    public Integer getSingleBfStatus() {
        return singleBfStatus;
    }

    public void setSingleBfStatus(Integer singleBfStatus) {
        this.singleBfStatus = singleBfStatus;
    }

    public Integer getSingleBqcStatus() {
        return singleBqcStatus;
    }

    public void setSingleBqcStatus(Integer singleBqcStatus) {
        this.singleBqcStatus = singleBqcStatus;
    }

    public Integer getSpfStatus() {
        return spfStatus;
    }

    public void setSpfStatus(Integer spfStatus) {
        this.spfStatus = spfStatus;
    }

    public Integer getRqspfStatus() {
        return rqspfStatus;
    }

    public void setRqspfStatus(Integer rqspfStatus) {
        this.rqspfStatus = rqspfStatus;
    }

    public Integer getZjqStatus() {
        return zjqStatus;
    }

    public void setZjqStatus(Integer zjqStatus) {
        this.zjqStatus = zjqStatus;
    }

    public Integer getBfStatus() {
        return bfStatus;
    }

    public void setBfStatus(Integer bfStatus) {
        this.bfStatus = bfStatus;
    }

    public Integer getBqcStatus() {
        return bqcStatus;
    }

    public void setBqcStatus(Integer bqcStatus) {
        this.bqcStatus = bqcStatus;
    }

    public Integer getOutMatchId() {
        return outMatchId;
    }

    public void setOutMatchId(Integer outMatchId) {
        this.outMatchId = outMatchId;
    }

    public Boolean getHot() {
        return isHot;
    }

    public void setHot(Boolean hot) {
        isHot = hot;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getStateTime() {
        return stateTime;
    }

    public void setStateTime(Date stateTime) {
        this.stateTime = stateTime;
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

    /**
     * 对阵重要属性字符串 用来判断对阵是否更新过
     * @return
     */
    public String getMatchInfo(){
        return matchCode+ "_" + period + "_" + jcId + "_" + weekDay + "_" + jcWebId + "_" + hostName
                + "_" + guestName + "_" + leagueName + "_" + matchTime + "_" + endTime + "_" + status
                + "_" + singleSpfStatus + "_" + singleRqspfStatus + "_" + singleZjqStatus + "_" + singleBfStatus
                + "_" + singleBqcStatus + "_" + spfStatus + "_" + rqspfStatus + "_" + zjqStatus + "_" + bfStatus
                + "_" + bqcStatus;
    }
}