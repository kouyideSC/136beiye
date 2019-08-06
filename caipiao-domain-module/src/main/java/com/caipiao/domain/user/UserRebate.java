package com.caipiao.domain.user;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Kouyi on 2018/1/16.
 */
public class UserRebate implements Serializable {
    private static final long serialVersionUID = 2463366862134427823L;
    private Long id;
    private Long userId;//用户编号
    private String lotteryId;//彩种编号
    private Double rate;//返点比例
    private Date createTime;
    private Date updateTime;

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

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
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
