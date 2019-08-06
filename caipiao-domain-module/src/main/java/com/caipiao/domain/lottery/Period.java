package com.caipiao.domain.lottery;

import java.io.Serializable;
import java.util.Date;

/**
 * 期次对象
 * Created by kouyi on 2017/11/04.
 */
public class Period implements Serializable {
    private static final long serialVersionUID = 69138400925335968L;
    private Long id;
    private String lotteryId;//彩种编号
    private String period;//期次
    private String drawNumber;//开奖号码
    private Date drawNumberTime;//获取到开奖号码时间
    private Integer sellStatus;//-1-已截止 0-未开售 1-销售中
    private Date sellStartTime;//开始销售时间
    private Date sellEndTime;//截止销售时间
    private Date authorityEndTime;//官方截止时间
    private String prizeGrade;//彩种期次对应的奖级
    private String matches;//老足彩对阵数据
    private Boolean updateFlag;//手动更新标记（0-未手动更新过 1-已手动更新）
    //期次处理逻辑状态码(0-初始默认(待处理) 1-系统自动撤单 2-抓取开奖号 3-待审核开奖号 4-开奖号审核完成
    // 5-开奖号同步订单 6-中奖匹配完成 7-奖金计算完成 8-奖金汇总完成 9-奖金核对完成
    // 10-自动派奖完成 11-过关统计完成 12-战绩统计完成 13-派送返点完成 99-期次处理完成)
    private Integer state;
    private Date stateTime;//状态变更时间
    private Date createTime;//期次生成时间
    private Date updateTime;//期次更新时间

    private Boolean isGrabSuccess=false;//开奖号码是否抓取成功,默认否

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    public Date getDrawNumberTime() {
        return drawNumberTime;
    }

    public void setDrawNumberTime(Date drawNumberTime) {
        this.drawNumberTime = drawNumberTime;
    }

    public Integer getSellStatus() {
        return sellStatus;
    }

    public void setSellStatus(Integer sellStatus) {
        this.sellStatus = sellStatus;
    }

    public Date getSellStartTime() {
        return sellStartTime;
    }

    public void setSellStartTime(Date sellStartTime) {
        this.sellStartTime = sellStartTime;
    }

    public Date getSellEndTime() {
        return sellEndTime;
    }

    public void setSellEndTime(Date sellEndTime) {
        this.sellEndTime = sellEndTime;
    }

    public Date getAuthorityEndTime() {
        return authorityEndTime;
    }

    public void setAuthorityEndTime(Date authorityEndTime) {
        this.authorityEndTime = authorityEndTime;
    }

    public String getPrizeGrade() {
        return prizeGrade;
    }

    public void setPrizeGrade(String prizeGrade) {
        this.prizeGrade = prizeGrade;
    }

    public String getMatches() {
        return matches;
    }

    public void setMatches(String matches) {
        this.matches = matches;
    }

    public Boolean getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(Boolean updateFlag) {
        this.updateFlag = updateFlag;
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

    public Boolean getGrabSuccess() {
        return isGrabSuccess;
    }

    public void setGrabSuccess(Boolean grabSuccess) {
        isGrabSuccess = grabSuccess;
    }
}