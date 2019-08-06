package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 活动优惠券补送
 */
public class ActivityCouponReissue implements Serializable
{
    private Long id;
    private Long activityId;//活动编号
    private Long userId;//用户编号
    private Double smoney;//金额
    private Integer couponType;//优惠券赠送类型(0-注册送 1-充值送)
    private Integer couponMode;//优惠券赠送模式(0-固定模式 1-自定义模式)
    private String couponIds;//活动优惠券赠送信息
    private Date couponExpireTime;//优惠券过期时间
    private Integer status;//补送状态(0-待补送 1-已补送)
    private Date createTime;//创建时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getSmoney() {
        return smoney;
    }

    public void setSmoney(Double smoney) {
        this.smoney = smoney;
    }

    public Integer getCouponType() {
        return couponType;
    }

    public void setCouponType(Integer couponType) {
        this.couponType = couponType;
    }

    public Integer getCouponMode() {
        return couponMode;
    }

    public void setCouponMode(Integer couponMode) {
        this.couponMode = couponMode;
    }

    public String getCouponIds() {
        return couponIds;
    }

    public void setCouponIds(String couponIds) {
        this.couponIds = couponIds;
    }

    public Date getCouponExpireTime() {
        return couponExpireTime;
    }

    public void setCouponExpireTime(Date couponExpireTime) {
        this.couponExpireTime = couponExpireTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}