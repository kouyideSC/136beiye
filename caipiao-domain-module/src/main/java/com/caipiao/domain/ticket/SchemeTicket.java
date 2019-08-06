package com.caipiao.domain.ticket;

import java.io.Serializable;
import java.util.Date;

/**
 * 方案对应票对象
 * Created by kouyi on 2017/11/04.
 */
public class SchemeTicket implements Serializable {
    private static final long serialVersionUID = -1004546560690724360L;
    private Long id;
    private String lotteryId;//彩种编号
    /**
     * 玩法类型id
     * 1700-竞彩足球混投 1710-竞彩篮球混投 1720-竞彩足球胜平负 1900-竞彩足球让球胜平负
     * 1910-竞彩足球猜比分 1920-竞彩足球半全场 1930-竞彩足球进球数 1940-竞彩篮球胜负
     * 1950-竞彩篮球让分胜负 1960-竞彩篮球胜分差 1970-竞彩篮球大小分
     */
    private String playTypeId;
    private String schemeId;//方案编号
    private String period;//期次编号
    private Double money;//金额
    private Integer multiple;//倍数
    private String codes;//投注串
    private String codesSp;//出票SP
    private Integer ticketStatus;//出票状态(-4-废弃票-已重新出票 -3-奖金核对失败 -2-系统撤单(任务不用处理该状态) -1-出票失败 0-待提票 1-提票成功未出票 2-出票成功 3-奖金核对成功)
    private String ticketDesc;//票状态描述
    private String ticketId;//网站票号
    private Date sendTicketTime;//提票时间
    private Date outTicketTime;//出票时间
    private Date awardTime;//奖金核对时间
    private String voteTicketId;//出票商票号
    private String voteId;//出票商编号
    private Double votePrize;//出票商返回奖金
    private Double votePrizeTax;//出票商税后奖金
    private Boolean isBig;//是否大奖（0-是 1-否）
    private Boolean isZhuiHao;//是否追号方案（0-否 1-是）
    private Integer bonusState;//网站计奖状态(0-未计奖 1-中奖匹配完成 2-计算奖金 3-汇总批次奖金)
    private Date bonusStateTime;//对应bonusStateTime状态处理时间
    private String bonusInfo;//中奖奖级描述
    private Integer isWin;//中奖状态(0-待计奖 1-未中奖 2-已中奖)
    private Date winTime;//中奖时间
    private Double ticketPrize;//票税前奖金
    private Double ticketSubjoinPrize;//票税前加奖奖金
    private Double ticketPrizeTax;//票税后奖金
    private Double ticketSubjoinPrizeTax;//票税后加奖奖金
    private String drawNumber;//大乐透乐善号码
    private String numberBonusInfo;//大乐透乐善号码中奖奖级描述
    private Date endTime;//票截止时间
    private Date createTime;//拆票入库时间
    private Date updateTime;//更新时间

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getNumberBonusInfo() {
        return numberBonusInfo;
    }

    public void setNumberBonusInfo(String numberBonusInfo) {
        this.numberBonusInfo = numberBonusInfo;
    }

    public String getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    public String getPlayTypeId() {
        return playTypeId;
    }

    public void setPlayTypeId(String playTypeId) {
        this.playTypeId = playTypeId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getMultiple() {
        return multiple;
    }

    public void setMultiple(Integer multiple) {
        this.multiple = multiple;
    }

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    public String getCodesSp() {
        return codesSp;
    }

    public void setCodesSp(String codesSp) {
        this.codesSp = codesSp;
    }

    public Integer getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(Integer ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getTicketDesc() {
        return ticketDesc;
    }

    public void setTicketDesc(String ticketDesc) {
        this.ticketDesc = ticketDesc;
    }

    public Date getSendTicketTime() {
        return sendTicketTime;
    }

    public void setSendTicketTime(Date sendTicketTime) {
        this.sendTicketTime = sendTicketTime;
    }

    public Date getOutTicketTime() {
        return outTicketTime;
    }

    public void setOutTicketTime(Date outTicketTime) {
        this.outTicketTime = outTicketTime;
    }

    public Date getAwardTime() {
        return awardTime;
    }

    public void setAwardTime(Date awardTime) {
        this.awardTime = awardTime;
    }

    public String getVoteTicketId() {
        return voteTicketId;
    }

    public void setVoteTicketId(String voteTicketId) {
        this.voteTicketId = voteTicketId;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public Double getVotePrize() {
        return votePrize;
    }

    public void setVotePrize(Double votePrize) {
        this.votePrize = votePrize;
    }

    public Double getVotePrizeTax() {
        return votePrizeTax;
    }

    public void setVotePrizeTax(Double votePrizeTax) {
        this.votePrizeTax = votePrizeTax;
    }

    public Boolean getBig() {
        return isBig;
    }

    public void setBig(Boolean big) {
        isBig = big;
    }

    public Boolean getZhuiHao() {
        return isZhuiHao;
    }

    public void setZhuiHao(Boolean zhuiHao) {
        isZhuiHao = zhuiHao;
    }

    public Integer getBonusState() {
        return bonusState;
    }

    public void setBonusState(Integer bonusState) {
        this.bonusState = bonusState;
    }

    public Date getBonusStateTime() {
        return bonusStateTime;
    }

    public void setBonusStateTime(Date bonusStateTime) {
        this.bonusStateTime = bonusStateTime;
    }

    public String getBonusInfo() {
        return bonusInfo;
    }

    public void setBonusInfo(String bonusInfo) {
        this.bonusInfo = bonusInfo;
    }

    public Integer getIsWin() {
        return isWin;
    }

    public void setIsWin(Integer isWin) {
        this.isWin = isWin;
    }

    public Date getWinTime() {
        return winTime;
    }

    public void setWinTime(Date winTime) {
        this.winTime = winTime;
    }

    public Double getTicketPrize() {
        return ticketPrize;
    }

    public void setTicketPrize(Double ticketPrize) {
        this.ticketPrize = ticketPrize;
    }

    public Double getTicketSubjoinPrize() {
        return ticketSubjoinPrize;
    }

    public void setTicketSubjoinPrize(Double ticketSubjoinPrize) {
        this.ticketSubjoinPrize = ticketSubjoinPrize;
    }

    public Double getTicketPrizeTax() {
        return ticketPrizeTax;
    }

    public void setTicketPrizeTax(Double ticketPrizeTax) {
        this.ticketPrizeTax = ticketPrizeTax;
    }

    public Double getTicketSubjoinPrizeTax() {
        return ticketSubjoinPrizeTax;
    }

    public void setTicketSubjoinPrizeTax(Double ticketSubjoinPrizeTax) {
        this.ticketSubjoinPrizeTax = ticketSubjoinPrizeTax;
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