package com.caipiao.domain.user;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户账户对象
 * Created by kouyi on 2017/9/28.
 */
public class UserAccount implements Serializable {
    private static final long serialVersionUID = -3937295507595744283L;
    private Long id;//编号
    private Long userId;//用户ID
    private double balance;//账户总余额
    private double withDraw;//可提现金额
    private double unWithDraw;//不可提现金额
    private double frozen;//冻结金额
    private double balanceBack;//返利余额|总提成
    private double totalRecharge;//累计充值金额
    private double totalWithDraw;//累计提现金额
    private double totalConsume;//累计消费金额
    private double totalAward;//累计中奖金额
    private double totalBack;//累计返现金额
    private Date updateTime;//更新时间

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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getWithDraw() {
        return withDraw;
    }

    public void setWithDraw(double withDraw) {
        this.withDraw = withDraw;
    }

    public double getUnWithDraw() {
        return unWithDraw;
    }

    public void setUnWithDraw(double unWithDraw) {
        this.unWithDraw = unWithDraw;
    }

    public double getFrozen() {
        return frozen;
    }

    public void setFrozen(double frozen) {
        this.frozen = frozen;
    }

    public double getTotalRecharge() {
        return totalRecharge;
    }

    public void setTotalRecharge(double totalRecharge) {
        this.totalRecharge = totalRecharge;
    }

    public double getTotalWithDraw() {
        return totalWithDraw;
    }

    public void setTotalWithDraw(double totalWithDraw) {
        this.totalWithDraw = totalWithDraw;
    }

    public double getTotalConsume() {
        return totalConsume;
    }

    public void setTotalConsume(double totalConsume) {
        this.totalConsume = totalConsume;
    }

    public double getTotalAward() {
        return totalAward;
    }

    public void setTotalAward(double totalAward) {
        this.totalAward = totalAward;
    }

    public double getTotalBack() {
        return totalBack;
    }

    public void setTotalBack(double totalBack) {
        this.totalBack = totalBack;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public double getBalanceBack() {
        return balanceBack;
    }

    public void setBalanceBack(double balanceBack) {
        this.balanceBack = balanceBack;
    }
}