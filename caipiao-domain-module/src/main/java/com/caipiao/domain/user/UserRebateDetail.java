package com.caipiao.domain.user;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Kouyi on 2018/1/19.
 */
public class UserRebateDetail implements Serializable {
    private static final long serialVersionUID = 5066031687253122834L;
    private Long id;
    private Integer type;//类型 0-获得返点 1-提取返点
    private Long userId;//目标用户编号
    private String lotteryId;//彩种编号
    private Long schemeUserId;//订单用户编号
    private String schemeOrderId;//订单编号
    private Double schemeMoney;//订单金额
    private Double rate;//返点比例
    private Double currentRebateMoney;//该笔订单返点金额
    private Double balanceRebate;//返点总余额
    private Double lastBalanceRebate;//返点前总余额
    private Date createTime;

    public Long getSchemeUserId() {
        return schemeUserId;
    }

    public void setSchemeUserId(Long schemeUserId) {
        this.schemeUserId = schemeUserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getCurrentRebateMoney() {
        return currentRebateMoney;
    }

    public void setCurrentRebateMoney(Double currentRebateMoney) {
        this.currentRebateMoney = currentRebateMoney;
    }

    public Double getBalanceRebate() {
        return balanceRebate;
    }

    public void setBalanceRebate(Double balanceRebate) {
        this.balanceRebate = balanceRebate;
    }

    public Double getLastBalanceRebate() {
        return lastBalanceRebate;
    }

    public void setLastBalanceRebate(Double lastBalanceRebate) {
        this.lastBalanceRebate = lastBalanceRebate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
