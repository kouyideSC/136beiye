package com.caipiao.domain.user;

import com.caipiao.domain.vo.BankInfoVo;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户充值提现订单对象
 * Created by kouyi on 2017/11/02.
 */
public class UserPay implements Serializable {
    private static final long serialVersionUID = 3566832003427638888L;
    private Long id;
    private Integer channelCode;//支付渠道编号(302-盛付通提现 100-微信官方支付 101-快接支付 102-贝付宝支付 103-威富通支付 104-豆豆平台 105-迅游通)
    private String channelDesc;//支付渠道描述
    private Integer payCode;//支付方式业务编号,针对业务类型为充值(4100-微信 4101-微信h5 4102-支付宝 4103-支付宝h5 4104-QQ钱包 4105-京东钱包 4106-银联)
    private String payDesc;//支付方式业务描述,针对业务类型为充值
    private Long userId;//交易用户编号
    private Integer payType;//业务类型(0-充值 1-提现)
    private Double money;//交易金额
    private Double omoney;//订单原始金额(主要针对一些充值渠道,订单原始金额与最终的实际的交易金额不一致的情况)
    private Integer status;//处理状态(-1-处理失败 0-待处理 1-等待重新处理中 2-处理中 3-处理成功)
    private String remark;//处理结果标记(备注)
    private String payId;//平台支付流水号
    private String channelPayId;//第三方渠道支付流水号
    private String bankType;//用户付款银行标识
    private String bankTypeDesc;//用户付款银行标识描述
    private BankInfoVo bankInfo;//收款银行信息(针对提现)
    private Integer clientFrom;//客户端来源(0-WWW 1-IOS 2-ANDROID 3-H5 4-Other)
    private String requestIp;//请求支付的客户端IP地址
    private Date createTime;//订单创建时间
    private Date doneTime;//处理完成时间
    private Date updateTime;//订单更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(Integer channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelDesc() {
        return channelDesc;
    }

    public void setChannelDesc(String channelDesc) {
        this.channelDesc = channelDesc;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getOmoney() {
        return omoney;
    }

    public void setOmoney(Double omoney) {
        this.omoney = omoney;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankTypeDesc() {
        return bankTypeDesc;
    }

    public void setBankTypeDesc(String bankTypeDesc) {
        this.bankTypeDesc = bankTypeDesc;
    }

    public BankInfoVo getBankInfo() {
        return bankInfo;
    }

    public void setBankInfo(String bankInfo) {
        JSONObject jsonObject = JSONObject.fromObject(bankInfo);
        this.bankInfo = (BankInfoVo)JSONObject.toBean(jsonObject,BankInfoVo.class);
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}