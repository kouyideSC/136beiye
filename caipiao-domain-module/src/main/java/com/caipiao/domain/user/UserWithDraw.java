package com.caipiao.domain.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户提现对象
 * Created by kouyi on 2017/11/02.
 */
public class UserWithDraw implements Serializable {
    private static final long serialVersionUID = -2189962097862386379L;
    private Long id;
    private Long userId;//用户编号
    private String realName;//真实姓名
    private Double money;//提现金额
    private Integer status;//提现状态(-1-提现失败 0提现中 1-提现成功)
    private String payId;//平台提现流水号
    private String channelPayId;//第三方渠道提现流水号
    private Integer clientFrom;//客户端来源(0-WWW 1-IOS 2-ANDROID 3-H5 4-Other)
    private String requestIp;//请求提现的客户端IP地址
    private Date createTime;//请求提现时间
    private Date doneTime;//提现完成时间
    private String bankProvince;//银行开户省份
    private String bankCode;//银行代码
    private String bankName;//银行名称
    private String bankCard;//银行卡号
    private String subBankName;//支行名称-对应大额行号

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getChannelPayId() {
        return channelPayId;
    }

    public void setChannelPayId(String channelPayId) {
        this.channelPayId = channelPayId;
    }

    public Integer getClientFrom() {
        return clientFrom;
    }

    public void setClientFrom(Integer clientFrom) {
        this.clientFrom = clientFrom;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(Date doneTime) {
        this.doneTime = doneTime;
    }

    public String getBankProvince() {
        return bankProvince;
    }

    public void setBankProvince(String bankProvince) {
        this.bankProvince = bankProvince;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getSubBankName() {
        return subBankName;
    }

    public void setSubBankName(String subBankName) {
        this.subBankName = subBankName;
    }
}