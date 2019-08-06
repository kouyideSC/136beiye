package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务对象
 * Created by kouyi on 2017/11/07.
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 893719588875652517L;
    private Long id;
    private String taskName;//任务名称
    private Integer status;//任务状态(0-未执行 1-已执行)
    private Integer spendTime;//任务耗时(秒)
    private Date beginTime;//定时任务的开始执行时间
    private Date executeTime;//任务实际执行时间
    private Date createTime;//任务创建时间

    public Task() {}

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public Task(String taskName, Date beginTime) {
        this.taskName = taskName;
        this.beginTime = beginTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}