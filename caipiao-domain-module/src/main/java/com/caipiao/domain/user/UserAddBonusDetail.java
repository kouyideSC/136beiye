package com.caipiao.domain.user;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Kouyi on 2018/03/27.
 */
public class UserAddBonusDetail implements Serializable {
    private static final long serialVersionUID = -184981056853486995L;
    private Long id;
    private Long userId;//用户编号
    private String lotteryId;//彩种编号
    private String schemeOrderId;//订单编号
    private Double schemeMoney;//订单金额
    private Double prizeTax;//税后中奖金额
    private String rateRange;//加奖比例范围
    private Double addPrizeTax;//当前加奖金额
    private Double lastBalance;//加奖前已使用额度
    private Double currBalance;//加奖后已使用额度
    private String addPrizeDateStr;//加奖日期
    private Integer activityId;//加奖活动编号
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getSchemeOrderId() {
        return schemeOrderId;
    }

    public void setSchemeOrderId(String schemeOrderId) {
        this.schemeOrderId = schemeOrderId;
    }

    public Double getSchemeMoney() {
        return schemeMoney;
    }

    public void setSchemeMoney(Double schemeMoney) {
        this.schemeMoney = schemeMoney;
    }

    public Double getPrizeTax() {
        return prizeTax;
    }

    public void setPrizeTax(Double prizeTax) {
        this.prizeTax = prizeTax;
    }

    public String getRateRange() {
        return rateRange;
    }

    public void setRateRange(String rateRange) {
        this.rateRange = rateRange;
    }

    public Double getAddPrizeTax() {
        return addPrizeTax;
    }

    public void setAddPrizeTax(Double addPrizeTax) {
        this.addPrizeTax = addPrizeTax;
    }

    public Double getLastBalance() {
        return lastBalance;
    }

    public void setLastBalance(Double lastBalance) {
        this.lastBalance = lastBalance;
    }

    public Double getCurrBalance() {
        return currBalance;
    }

    public void setCurrBalance(Double currBalance) {
        this.currBalance = currBalance;
    }

    public String getAddPrizeDateStr() {
        return addPrizeDateStr;
    }

    public void setAddPrizeDateStr(String addPrizeDateStr) {
        this.addPrizeDateStr = addPrizeDateStr;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
