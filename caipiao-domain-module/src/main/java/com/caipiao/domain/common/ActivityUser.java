package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户关联活动对象
 * Created by Kouyi on 2018/3/28.
 */
public class ActivityUser implements Serializable {
    private static final long serialVersionUID = -6919344348660637195L;
    private Long id;
    private Long userId;
    private Integer activityType;
    private Integer activityId;
    private Date createTime;

    public ActivityUser(){}

    public ActivityUser(Long userId) {
        this.userId = userId;
    }

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

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
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
