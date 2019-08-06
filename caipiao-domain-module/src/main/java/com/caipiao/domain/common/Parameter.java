package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统配置参数对象
 * Created by kouyi on 2017/9/21.
 */
public class Parameter implements Serializable {
    private static final long serialVersionUID = -6274271631284106012L;
    private Long id;//编号
    private String pmKey;//键
    private String pmValue;//值
    private String pmDescribe;//描述
    private Date updateTime;//创建或更新时间-变化的

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPmKey() {
        return pmKey;
    }

    public void setPmKey(String pmKey) {
        this.pmKey = pmKey;
    }

    public String getPmValue() {
        return pmValue;
    }

    public void setPmValue(String pmValue) {
        this.pmValue = pmValue;
    }

    public String getPmDescribe() {
        return pmDescribe;
    }

    public void setPmDescribe(String pmDescribe) {
        this.pmDescribe = pmDescribe;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
