package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 加奖活动对象
 * Created by kouyi on 2018/03/23.
 */
public class ActivityAddBonus implements Serializable {
    private static final long serialVersionUID = -7566745770440634986L;
    private Integer id;
    private String activityName;//活动名称
    private Double maxMoney;//最大额度
    private Double balance;//累计已加奖额度
    private Double userDayLimit;//用户单日加奖限制
    private String lotteryLimit;//加奖彩种编号，多个以逗号隔开
    private String leagueNameLimit;//加奖赛事限制
    private String matchCode;//加奖场次(仅单关活动时有效）
    private String passType;//支持加奖的串关方式
    private Double schemeMoneyLimit;//方案金额限制
    private Integer isWithDraw;//是否可提现(0-不能 1-能)
    private Integer outAccountUserId;//出款账户ID
    private Integer status;//活动状态(0-停用 1-启用)
    private String weekLimit;//限制周几享受加奖
    private Date beginTime;//生效时间
    private Date endTime;//结束时间
    private String addBonusRate;//加奖区间比例

    public Double getSchemeMoneyLimit() {
        return schemeMoneyLimit;
    }

    public void setSchemeMoneyLimit(Double schemeMoneyLimit) {
        this.schemeMoneyLimit = schemeMoneyLimit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Double getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(Double maxMoney) {
        this.maxMoney = maxMoney;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getUserDayLimit() {
        return userDayLimit;
    }

    public void setUserDayLimit(Double userDayLimit) {
        this.userDayLimit = userDayLimit;
    }

    public String getLotteryLimit() {
        return lotteryLimit;
    }

    public void setLotteryLimit(String lotteryLimit) {
        this.lotteryLimit = lotteryLimit;
    }

    public String getLeagueNameLimit() {
        return leagueNameLimit;
    }

    public void setLeagueNameLimit(String leagueNameLimit) {
        this.leagueNameLimit = leagueNameLimit;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    public Integer getIsWithDraw() {
        return isWithDraw;
    }

    public void setIsWithDraw(Integer isWithDraw) {
        this.isWithDraw = isWithDraw;
    }

    public Integer getOutAccountUserId() {
        return outAccountUserId;
    }

    public void setOutAccountUserId(Integer outAccountUserId) {
        this.outAccountUserId = outAccountUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWeekLimit() {
        return weekLimit;
    }

    public void setWeekLimit(String weekLimit) {
        this.weekLimit = weekLimit;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAddBonusRate() {
        return addBonusRate;
    }

    public void setAddBonusRate(String addBonusRate) {
        this.addBonusRate = addBonusRate;
    }
}