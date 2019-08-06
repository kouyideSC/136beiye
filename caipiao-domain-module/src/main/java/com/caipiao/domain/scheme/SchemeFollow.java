package com.caipiao.domain.scheme;

import java.io.Serializable;
import java.util.Date;

/**
 * 跟单关系对象
 * Created by Kouyi on 2018/6/1.
 */
public class SchemeFollow implements Serializable {
    private static final long serialVersionUID = -3664405558892770553L;
    private Long id;
    private Long senderUserId;//发单人用户id
    private String senderSchemeId;//发单人订单编号-订单表schemeOrderId
    private Integer rewardProportion;//打赏比例
    private Long followUserId;//跟单人用户id
    private String followSchemeId;//跟单人订单编号-订单表schemeOrderId
    private String followNickName;//跟单人昵称
    private Date followTime;//跟单时间
    private Double followMoney;//跟单金额
    private Double followPrizeMoney;//跟单人中奖金额
    private Double rewardMoney;//打赏金额
    private Integer schemeStatus;//订单出票状态(0-出票中 1-出票成功 2-出票失败)
    private Integer awardState;//计奖状态(0-未计算打赏 1-有打赏 2-无打赏 3-已打赏)
    private Date awardTime;//计奖处理时间

    public SchemeFollow() {

    }

    public SchemeFollow(String senderSchemeId){
        this.senderSchemeId = senderSchemeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderSchemeId() {
        return senderSchemeId;
    }

    public void setSenderSchemeId(String senderSchemeId) {
        this.senderSchemeId = senderSchemeId;
    }

    public Integer getRewardProportion() {
        return rewardProportion;
    }

    public void setRewardProportion(Integer rewardProportion) {
        this.rewardProportion = rewardProportion;
    }

    public Long getFollowUserId() {
        return followUserId;
    }

    public void setFollowUserId(Long followUserId) {
        this.followUserId = followUserId;
    }

    public String getFollowSchemeId() {
        return followSchemeId;
    }

    public void setFollowSchemeId(String followSchemeId) {
        this.followSchemeId = followSchemeId;
    }

    public String getFollowNickName() {
        return followNickName;
    }

    public void setFollowNickName(String followNickName) {
        this.followNickName = followNickName;
    }

    public Date getFollowTime() {
        return followTime;
    }

    public void setFollowTime(Date followTime) {
        this.followTime = followTime;
    }

    public Double getFollowMoney() {
        return followMoney;
    }

    public void setFollowMoney(Double followMoney) {
        this.followMoney = followMoney;
    }

    public Double getFollowPrizeMoney() {
        return followPrizeMoney;
    }

    public void setFollowPrizeMoney(Double followPrizeMoney) {
        this.followPrizeMoney = followPrizeMoney;
    }

    public Double getRewardMoney() {
        return rewardMoney;
    }

    public void setRewardMoney(Double rewardMoney) {
        this.rewardMoney = rewardMoney;
    }

    public Integer getAwardState() {
        return awardState;
    }

    public void setAwardState(Integer awardState) {
        this.awardState = awardState;
    }

    public Date getAwardTime() {
        return awardTime;
    }

    public void setAwardTime(Date awardTime) {
        this.awardTime = awardTime;
    }

    public Integer getSchemeStatus() {
        return schemeStatus;
    }

    public void setSchemeStatus(Integer schemeStatus) {
        this.schemeStatus = schemeStatus;
    }
}
