package com.caipiao.domain.ticket;

import java.io.Serializable;

/**
 * 出票商规则对象
 * Created by kouyi on 2017/12/01.
 */
public class TicketVoteRule implements Serializable {
    private static final long serialVersionUID = -7263058945213646734L;
    private Integer id; //主键ID
    private Integer lotteryId; //彩种编号
    private String lotteryName; //彩种名称
    private String playType; //玩法类型
    private String playName;//玩法名称
    private String voteId;//出票商编号
    private String rate; //百分比
    private String receiveTime;//接收票时间范围

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(Integer lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String playType) {
        this.playType = playType;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

}
