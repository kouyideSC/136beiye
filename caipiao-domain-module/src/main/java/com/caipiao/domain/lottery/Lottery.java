package com.caipiao.domain.lottery;

import java.io.Serializable;
import java.util.Date;

/**
 * 彩种对象
 * Created by kouyi on 2017/11/04.
 */
public class Lottery implements Serializable {
    private static final long serialVersionUID = -1570554803090710568L;
    private String id;//彩种编号
    private String name;//彩种名称
    private String shortName;//客户端显示彩种简称
    private String prizeGrade;//奖级定义
    private Integer appStatus;//app销售状态(0-停售 1-销售)
    private Integer webStatus;//网站销售状态(0-停售 1-销售)
    private Integer h5Status;//H5销售状态(0-停售 1-销售)
    private Integer consoleStatus;//后台销售状态(0-停售 1-销售)
    private Integer orderValue;//排序号
    private Integer backGround;//彩种说明文字背景是否打开(0-否 1-是)
    private Integer showInHome;//是否在首页展示(0-不显示 1-显示)
    private String message;//彩种说明（app显示）
    private Integer maxSellMoney;//单方案最大金额
    private Integer maxSellMultiple;//单方案最大倍数
    private Integer minSellMoney;//单方案最小金额
    private Integer minSellMultiple;//单方案最小倍数
    private Integer xzMaxSellMoney;//是否限制单方案最大金额,0-不限制 1-限制
    private Integer xzMaxSellMultiple;//是否限制单方案最大倍数,0-不限制 1-限制
    private Integer xzMinSellMoney;//是否限制单方案最小金额,0-不限制 1-限制
    private Integer xzMinSellMultiple;//是否限制单方案最小倍数,0-不限制 1-限制
    private Integer ggfsFlag;//支持的过关模式(针对竞彩),0-单个过关方式 1-不限
    private Integer maxZhNum = 99;//单方案最多追号期数,默认为99
    private String activityImg;//活动图片
    private Date updateTime;//更新时间

    public Integer getMaxSellMoney() {
        return maxSellMoney;
    }

    public void setMaxSellMoney(Integer maxSellMoney) {
        this.maxSellMoney = maxSellMoney;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPrizeGrade() {
        return prizeGrade;
    }

    public void setPrizeGrade(String prizeGrade) {
        this.prizeGrade = prizeGrade;
    }

    public Integer getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(Integer appStatus) {
        this.appStatus = appStatus;
    }

    public Integer getWebStatus() {
        return webStatus;
    }

    public void setWebStatus(Integer webStatus) {
        this.webStatus = webStatus;
    }

    public Integer getH5Status() {
        return h5Status;
    }

    public void setH5Status(Integer h5Status) {
        this.h5Status = h5Status;
    }

    public Integer getConsoleStatus() {
        return consoleStatus;
    }

    public void setConsoleStatus(Integer consoleStatus) {
        this.consoleStatus = consoleStatus;
    }

    public Integer getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(Integer orderValue) {
        this.orderValue = orderValue;
    }

    public Integer getBackGround() {
        return backGround;
    }

    public void setBackGround(Integer backGround) {
        this.backGround = backGround;
    }

    public Integer getShowInHome() {
        return showInHome;
    }

    public void setShowInHome(Integer showInHome) {
        this.showInHome = showInHome;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMaxSellMultiple() {
        return maxSellMultiple;
    }

    public void setMaxSellMultiple(Integer maxSellMultiple) {
        this.maxSellMultiple = maxSellMultiple;
    }

    public Integer getMinSellMoney() {
        return minSellMoney;
    }

    public void setMinSellMoney(Integer minSellMoney) {
        this.minSellMoney = minSellMoney;
    }

    public Integer getMinSellMultiple() {
        return minSellMultiple;
    }

    public void setMinSellMultiple(Integer minSellMultiple) {
        this.minSellMultiple = minSellMultiple;
    }

    public Integer getXzMaxSellMoney() {
        return xzMaxSellMoney;
    }

    public void setXzMaxSellMoney(Integer xzMaxSellMoney) {
        this.xzMaxSellMoney = xzMaxSellMoney;
    }

    public Integer getXzMaxSellMultiple() {
        return xzMaxSellMultiple;
    }

    public void setXzMaxSellMultiple(Integer xzMaxSellMultiple) {
        this.xzMaxSellMultiple = xzMaxSellMultiple;
    }

    public Integer getXzMinSellMoney() {
        return xzMinSellMoney;
    }

    public void setXzMinSellMoney(Integer xzMinSellMoney) {
        this.xzMinSellMoney = xzMinSellMoney;
    }

    public Integer getXzMinSellMultiple() {
        return xzMinSellMultiple;
    }

    public void setXzMinSellMultiple(Integer xzMinSellMultiple) {
        this.xzMinSellMultiple = xzMinSellMultiple;
    }

    public Integer getGgfsFlag() {
        return ggfsFlag;
    }

    public void setGgfsFlag(Integer ggfsFlag) {
        this.ggfsFlag = ggfsFlag;
    }

    public Integer getMaxZhNum() {
        return maxZhNum;
    }

    public void setMaxZhNum(Integer maxZhNum) {
        this.maxZhNum = maxZhNum;
    }

    public String getActivityImg() {
        return activityImg;
    }

    public void setActivityImg(String activityImg) {
        this.activityImg = activityImg;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}