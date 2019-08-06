package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 充值方式对象
 * Created by kouyi on 2017/11/04.
 */
public class PayWay implements Serializable {
    private static final long serialVersionUID = -2309629138097597856L;
    private Long id;
    private String payName;//充值方式名称
    private String payShort;//充值方式英文简称
    private Integer payCode;//充值方式业务编号(如4100-微信充值)
    private String payDesc;//充值方式业务描述
    private String showDesc;//显示描述
    private String clientTypes;//所属/开放客户端(-1-所有 0-web 1-ios 2-android 3-h5)，多个客户端用","连接
    private String payThumbUrl;//支付图标地址
    private Integer status;//启用状态(0-未启用 1-启用)
    private Integer orderValue;//排序顺序
    private Date createTime;//入库时间
    private Date updateTime;//更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getPayShort() {
        return payShort;
    }

    public void setPayShort(String payShort) {
        this.payShort = payShort;
    }

    public Integer getPayCode() {
        return payCode;
    }

    public void setPayCode(Integer payCode) {
        this.payCode = payCode;
    }

    public String getPayDesc() {
        return payDesc;
    }

    public void setPayDesc(String payDesc) {
        this.payDesc = payDesc;
    }

    public String getShowDesc() {
        return showDesc;
    }

    public void setShowDesc(String showDesc) {
        this.showDesc = showDesc;
    }

    public String getClientTypes() {
        return clientTypes;
    }

    public void setClientTypes(String clientTypes) {
        this.clientTypes = clientTypes;
    }

    public String getPayThumbUrl() {
        return payThumbUrl;
    }

    public void setPayThumbUrl(String payThumbUrl) {
        this.payThumbUrl = payThumbUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(Integer orderValue) {
        this.orderValue = orderValue;
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