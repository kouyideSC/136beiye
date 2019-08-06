package com.caipiao.domain.scheme;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户购彩追号方案对象
 * Created by kouyi on 2017/11/04.
 */
public class SchemeZhuiHao implements Serializable {
    private static final long serialVersionUID = 2660594316478185192L;
    private Long id;
    private Long schemeId;//方案表ID
    private Long schemeUserId;//方案发起人用户编号
    private String lotteryId;//彩种编号
    private String lotteryName;//彩种名称
    private String schemeOrderId;//订单编号
    private Integer schemeMultiple;//方案倍数
    private Double schemeMoney;//方案金额
    private Integer schemeStatus;//-1-无效 0-待支付 1-支付成功（预约中） 2-预约成功 3-预约失败
    private String schemeStatusDesc;//状态描述
    private String schemePlayType;//玩法类型
    private String schemeContent;//投注内容
    private Double prize;//税前总奖金
    private Double prizeSubjoin;//官方-税前加奖奖金
    private Double prizeSubjoinSite;//网站加奖-税前加奖奖金
    private Double prizeTax;//税后总奖金
    private Double prizeSubjoinTax;//官方-税后加奖奖金
    private Double prizeSubjoinSiteTax;//网站加奖-税后加奖奖金
    private String prizeDetail;//中奖明细
    private String prizeBarrier;//奖级
    private String drawNumber;//开奖号码
    private String period;//期次编号
    private Date endTime;//方案截止时间
    private Date outTicketTime;//出票时间
    private Integer openStatus;//计奖状态（0-未计奖 1-未中奖 2-中奖）
    private Date openTime;//计奖时间
    private Integer prizeStatus;//0-未派奖 1-派奖中 2-已派奖
    private Date prizeTime;//派奖时间
    private Integer backStatus;//方案返现处理状态（-1-返现失败 0-不返现 1-未返现 2-已返现）
    private Integer channelNotifyNumber;//渠道出票通知次数
    private Date createTime;//方案下单时间

    public void setSchemeMoney(Double schemeMoney) {
        this.schemeMoney = schemeMoney;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getSchemeUserId() {
        return schemeUserId;
    }

    public void setSchemeUserId(Long schemeUserId) {
        this.schemeUserId = schemeUserId;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }

    public String getSchemeOrderId() {
        return schemeOrderId;
    }

    public void setSchemeOrderId(String schemeOrderId) {
        this.schemeOrderId = schemeOrderId;
    }

    public Integer getSchemeMultiple() {
        return schemeMultiple;
    }

    public void setSchemeMultiple(Integer schemeMultiple) {
        this.schemeMultiple = schemeMultiple;
    }

    public Double getSchemeMoney() {
        return schemeMoney;
    }

    public Integer getSchemeStatus() {
        return schemeStatus;
    }

    public void setSchemeStatus(Integer schemeStatus) {
        this.schemeStatus = schemeStatus;
    }

    public String getSchemeStatusDesc() {
        return schemeStatusDesc;
    }

    public void setSchemeStatusDesc(String schemeStatusDesc) {
        this.schemeStatusDesc = schemeStatusDesc;
    }

    public String getSchemePlayType() {
        return schemePlayType;
    }

    public void setSchemePlayType(String schemePlayType) {
        this.schemePlayType = schemePlayType;
    }

    public String getSchemeContent() {
        return schemeContent;
    }

    public void setSchemeContent(String schemeContent) {
        this.schemeContent = schemeContent;
    }

    public Double getPrize() {
        return prize;
    }

    public void setPrize(Double prize) {
        this.prize = prize;
    }

    public Double getPrizeSubjoin() {
        return prizeSubjoin;
    }

    public void setPrizeSubjoin(Double prizeSubjoin) {
        this.prizeSubjoin = prizeSubjoin;
    }

    public Double getPrizeSubjoinSite() {
        return prizeSubjoinSite;
    }

    public void setPrizeSubjoinSite(Double prizeSubjoinSite) {
        this.prizeSubjoinSite = prizeSubjoinSite;
    }

    public Double getPrizeTax() {
        return prizeTax;
    }

    public void setPrizeTax(Double prizeTax) {
        this.prizeTax = prizeTax;
    }

    public Double getPrizeSubjoinTax() {
        return prizeSubjoinTax;
    }

    public void setPrizeSubjoinTax(Double prizeSubjoinTax) {
        this.prizeSubjoinTax = prizeSubjoinTax;
    }

    public Double getPrizeSubjoinSiteTax() {
        return prizeSubjoinSiteTax;
    }

    public void setPrizeSubjoinSiteTax(Double prizeSubjoinSiteTax) {
        this.prizeSubjoinSiteTax = prizeSubjoinSiteTax;
    }

    public String getPrizeDetail() {
        return prizeDetail;
    }

    public void setPrizeDetail(String prizeDetail) {
        this.prizeDetail = prizeDetail;
    }

    public String getPrizeBarrier() {
        return prizeBarrier;
    }

    public void setPrizeBarrier(String prizeBarrier) {
        this.prizeBarrier = prizeBarrier;
    }

    public String getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getOutTicketTime() {
        return outTicketTime;
    }

    public void setOutTicketTime(Date outTicketTime) {
        this.outTicketTime = outTicketTime;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Integer getPrizeStatus() {
        return prizeStatus;
    }

    public void setPrizeStatus(Integer prizeStatus) {
        this.prizeStatus = prizeStatus;
    }

    public Date getPrizeTime() {
        return prizeTime;
    }

    public void setPrizeTime(Date prizeTime) {
        this.prizeTime = prizeTime;
    }

    public Integer getBackStatus() {
        return backStatus;
    }

    public void setBackStatus(Integer backStatus) {
        this.backStatus = backStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getChannelNotifyNumber() {
        return channelNotifyNumber;
    }

    public void setChannelNotifyNumber(Integer channelNotifyNumber) {
        this.channelNotifyNumber = channelNotifyNumber;
    }
}