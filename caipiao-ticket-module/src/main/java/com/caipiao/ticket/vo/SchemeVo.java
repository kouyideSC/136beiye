package com.caipiao.ticket.vo;

import org.joda.time.DateTime;

import java.awt.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 普通和追号方案合并属性
 * Created by Kouyi on 2017/11/30.
 */
public class SchemeVo implements Serializable {
    private static final long serialVersionUID = -8543661374180750349L;
    private String schemeOrderId;//方案表ID
    private String lotteryId;//彩种编号
    private String playTypeId;//玩法编号
    private Integer schemeMultiple;//方案倍数
    private Double schemeMoney;//方案金额
    private Integer schemeStatus;//-1-无效 0-待支付 1-支付成功（预约中） 2-预约成功 3-预约失败
    private String schemeContent;//投注内容
    private String period;//期次编号
    private boolean isZhuiHao;//是否追号方案
    private String voteId;//出票商编号
    private Map<String, String> schemeSp;//方案下单sp
    private Long userId;//方案用户
    private Date endTime;//票截止时间

    public SchemeVo(){}
    public SchemeVo(String schemeOrderId, String lotteryId, String playTypeId,
                    Integer schemeMultiple, Double schemeMoney,
                    Integer schemeStatus, String schemeContent,
                    String period, String voteId, Map<String, String> schemeSp,
                    Long userId, Date endTime, boolean isZhuiHao){
        this.schemeOrderId = schemeOrderId;
        this.lotteryId = lotteryId;
        this.playTypeId = playTypeId;
        this.schemeMultiple = schemeMultiple;
        this.schemeMoney = schemeMoney;
        this.schemeStatus = schemeStatus;
        this.schemeContent = schemeContent;
        this.period = period;
        this.voteId = voteId;
        this.schemeSp = schemeSp;
        this.userId = userId;
        this.isZhuiHao = isZhuiHao;
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Map<String, String> getSchemeSp() {
        return schemeSp;
    }

    public void setSchemeSp(Map<String, String> schemeSp) {
        this.schemeSp = schemeSp;
    }

    public String getPlayTypeId() {
        return playTypeId;
    }

    public void setPlayTypeId(String playTypeId) {
        this.playTypeId = playTypeId;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public boolean isZhuiHao() {
        return isZhuiHao;
    }

    public void setZhuiHao(boolean zhuiHao) {
        isZhuiHao = zhuiHao;
    }

    public String getSchemeOrderId() {
        return schemeOrderId;
    }

    public void setSchemeOrderId(String schemeOrderId) {
        this.schemeOrderId = schemeOrderId;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
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

    public void setSchemeMoney(Double schemeMoney) {
        this.schemeMoney = schemeMoney;
    }

    public Integer getSchemeStatus() {
        return schemeStatus;
    }

    public void setSchemeStatus(Integer schemeStatus) {
        this.schemeStatus = schemeStatus;
    }

    public String getSchemeContent() {
        return schemeContent;
    }

    public void setSchemeContent(String schemeContent) {
        this.schemeContent = schemeContent;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
