package com.caipiao.domain.ticket;

import java.io.Serializable;

/**
 * 出票参数配置对象
 * Created by kouyi on 2017/12/01.
 */
public class TicketConfig implements Serializable {
    private static final long serialVersionUID = 8454374486088000035L;
    private Integer id; //主键ID
    private Integer lotteryId; //彩种编号
    private String lotteryName; //彩种名称
    private String playType; //玩法类型
    private String playName;//玩法名称
    private Integer maxMultiple;//方案低于倍数
    private Double maxMoney; //方案低于金额
    private Double maxPrize;//方案最高金额
    private String maxPassType;//最大过关方式

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

    public Integer getMaxMultiple() {
        return maxMultiple;
    }

    public void setMaxMultiple(Integer maxMultiple) {
        this.maxMultiple = maxMultiple;
    }

    public Double getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(Double maxMoney) {
        this.maxMoney = maxMoney;
    }

    public Double getMaxPrize() {
        return maxPrize;
    }

    public void setMaxPrize(Double maxPrize) {
        this.maxPrize = maxPrize;
    }

    public String getMaxPassType() {
        return maxPassType;
    }

    public void setMaxPassType(String maxPassType) {
        this.maxPassType = maxPassType;
    }
}
