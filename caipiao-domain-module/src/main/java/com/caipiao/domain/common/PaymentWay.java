package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 付款(提现)方式(渠道)对象
 */
public class PaymentWay implements Serializable
{
    private Long id;
    private String name;//渠道名称
    private String logo;//logo
    private Integer channelCode;//渠道编号(302-盛付通提现)
    private String channelDesc;//渠道描述
    private Integer status;//启用状态(0-未启用 1-启用)
    private Integer model;//启用模式(0-默认模式 1-时间段 2-时间特征)
    private Date timeRangeStart;//时间段-开始时间(针对启用模式为时间段)
    private Date timeRangeEnd;//时间段-结束时间(针对启用模式为时间段)
    private String timeCharacter;//时间特征(针对启用模式为某个时间特征，时分模式，比如08:00~12:00，多个用";"连接)
    private Double rate;//使用权重(比例)
    private Double minMoney;//单笔最小金额
    private Double maxMoney;//单笔最大金额
    private Date createTime;//创建时间
    private Date updateTime;//更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(Integer channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelDesc() {
        return channelDesc;
    }

    public void setChannelDesc(String channelDesc) {
        this.channelDesc = channelDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

    public Date getTimeRangeStart() {
        return timeRangeStart;
    }

    public void setTimeRangeStart(Date timeRangeStart) {
        this.timeRangeStart = timeRangeStart;
    }

    public Date getTimeRangeEnd() {
        return timeRangeEnd;
    }

    public void setTimeRangeEnd(Date timeRangeEnd) {
        this.timeRangeEnd = timeRangeEnd;
    }

    public String getTimeCharacter() {
        return timeCharacter;
    }

    public void setTimeCharacter(String timeCharacter) {
        this.timeCharacter = timeCharacter;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(Double minMoney) {
        this.minMoney = minMoney;
    }

    public Double getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(Double maxMoney) {
        this.maxMoney = maxMoney;
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
}