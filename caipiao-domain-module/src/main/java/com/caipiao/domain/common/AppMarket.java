package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 市场版本控制对象
 * Created by kouyi on 2018/01/04.
 */
public class AppMarket implements Serializable {
    private static final long serialVersionUID = 980896366826323645L;
    private Long id;
    private String appName;//app名称
    private Integer clientType;//客户端类型(0-ios 1-安卓)
    private Integer versionType;//本类型(0-正式版 1-资讯版 2-企业版)
    private Integer marketId;//app市场编号
    private String appVersion;//app显示的小版本号
    private String buildVersion;//app开发内部大版本号(用以作为更新依据)
    private String downUrl;//下载地址
    private Integer status;//版本状态(0-无效 1-有效)
    private Integer isForceUpdate;//是否强制更新(0-否 1-是)
    private String updateInfo;//更新说明
    private String createTime;//版本数据入库时间
    private String updateTime;//更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public Integer getVersionType() {
        return versionType;
    }

    public void setVersionType(Integer versionType) {
        this.versionType = versionType;
    }

    public Integer getMarketId() {
        return marketId;
    }

    public void setMarketId(Integer marketId) {
        this.marketId = marketId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(Integer isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}