package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 充值渠道配置对象
 */
public class PayWayChannel implements Serializable
{
    private Long id;//主键id
    private Long paywayId;//充值方式id
    private Long paychannelId;//充值渠道id
    private String apiUrl;//接口地址
    private String notifyUrl;//支付结果通知地址(异步)
    private String returnUrl;//页面通知地址
    private String merchantNo;//商户号
    private String appNo;//应用编号/产品编号
    private String appName;//应用名称/app名称/商品名称/商品描述
    private Integer signType;//签名方式(1-MD5 2-RSA 3-RES) 默认为1
    private String secretKey;//签名密钥
    private String rsaPublicKey;//RSA公钥
    private String rsaPrivateKey;//RSA私钥
    private String deviceInfo;//终端设备信息
    private String webAddress;//应用官网地址
    private String clientTypes;//所属/开放客户端(-1-所有 0-web 1-ios 2-android 3-h5)，多个客户端用","连接
    private Integer model;//启用模式(0-默认模式 1-时间段 2-时间特征),默认为0
    private Date timeRangeStart;//时间段-开始时间(针对启用模式为某个时间段)
    private Date timeRangeEnd;//时间段-结束时间(针对启用模式为某个时间段)
    private String timeCharacter;//时间特征(针对启用模式为某个时间特征，时分模式，比如08:00~12:00，多个用";"连接)
    private Double weight;//使用权重(比例，优先级要大于所属渠道配置的权重)
    private Double maxMoney;//单笔最大金额
    private Double minMoney;//单笔最小金额
    private String fixedMoney;//单笔充值固定金额,多个用;隔开
    private Integer status;//启用状态(0-未启用 1-已启用)
    private Date createTime;//创建时间
    private Date updateTime;//更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaywayId() {
        return paywayId;
    }

    public void setPaywayId(Long paywayId) {
        this.paywayId = paywayId;
    }

    public Long getPaychannelId() {
        return paychannelId;
    }

    public void setPaychannelId(Long paychannelId) {
        this.paychannelId = paychannelId;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getSignType() {
        return signType;
    }

    public void setSignType(Integer signType) {
        this.signType = signType;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRsaPublicKey() {
        return rsaPublicKey;
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    public String getRsaPrivateKey() {
        return rsaPrivateKey;
    }

    public void setRsaPrivateKey(String rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getClientTypes() {
        return clientTypes;
    }

    public void setClientTypes(String clientTypes) {
        this.clientTypes = clientTypes;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(Double maxMoney) {
        this.maxMoney = maxMoney;
    }

    public Double getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(Double minMoney) {
        this.minMoney = minMoney;
    }

    public String getFixedMoney() {
        return fixedMoney;
    }

    public void setFixedMoney(String fixedMoney) {
        this.fixedMoney = fixedMoney;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}