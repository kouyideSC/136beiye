package com.caipiao.domain.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户账户流水对象
 * Created by kouyi on 2017/11/02.
 */
public class UserDetail implements Serializable {
    private static final long serialVersionUID = -2739815648927193119L;
    private Long id;
    private Integer channelCode;//业务渠道编号
    private String channelDesc;//业务渠道描述业务渠道编号(300-余额支付[购彩] 301-优惠券支付 302-盛付通提现 303~305-待后续接其他提现渠道 306-管理后台扣款  307-佣金提现 308-打赏扣除 400-中奖金额 4100~4199充值渠道 403~405待后续接其他充值渠道 406-管理后台加款 407-预约失败退款 408~411-提现失败退款[与304~307对应] 412-优惠券退回 413-佣金转入 414-收获打赏 415-注册送彩金 416-方案加奖奖金)
    private Long userId;//交易用户编号
    private Boolean inType;//资金进出类型(0-存入 1-取出)
    private Double money;//交易金额
    private Double balance;//交易后余额
    private Double lastBalance;//交易前余额
    private Double withDraw;//交易前可提现金额
    private Double lastWithDraw;//交易后可提现金额
    private Double unWithDraw;//交易前不可提现金额
    private Double lastUnWithDraw;//交易后不可提现金额
    private Integer clientFrom;//客户端来源(0-WWW 1-IOS 2-ANDROID 3-H5 4-Other)
    private String businessId;//业务关联编号(购彩时为schemeId，充值提现时为payId等）
    private String remark;//备注(说明)
    private Integer status;//流水状态(-1-无效 0-处理中 1-有效(已完成))
    private Date createTime;//入库时间

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

    public Boolean getInType() {
        return inType;
    }

    public void setInType(Boolean inType) {
        this.inType = inType;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getLastBalance() {
        return lastBalance;
    }

    public void setLastBalance(Double lastBalance) {
        this.lastBalance = lastBalance;
    }

    public Double getWithDraw() {
        return withDraw;
    }

    public void setWithDraw(Double withDraw) {
        this.withDraw = withDraw;
    }

    public Double getLastWithDraw() {
        return lastWithDraw;
    }

    public void setLastWithDraw(Double lastWithDraw) {
        this.lastWithDraw = lastWithDraw;
    }

    public Double getUnWithDraw() {
        return unWithDraw;
    }

    public void setUnWithDraw(Double unWithDraw) {
        this.unWithDraw = unWithDraw;
    }

    public Double getLastUnWithDraw() {
        return lastUnWithDraw;
    }

    public void setLastUnWithDraw(Double lastUnWithDraw) {
        this.lastUnWithDraw = lastUnWithDraw;
    }

    public Integer getClientFrom() {
        return clientFrom;
    }

    public void setClientFrom(Integer clientFrom) {
        this.clientFrom = clientFrom;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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