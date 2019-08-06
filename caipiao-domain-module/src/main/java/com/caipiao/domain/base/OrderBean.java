package com.caipiao.domain.base;

import java.io.Serializable;

/**
 * 订单业务标准bean
 * @user kouyi
 * @date 2017-09-20
 */
public class OrderBean extends BaseBean implements Serializable {
    private static final long serialVersionUID = 6816876924024744512L;
    private int schemeId;//方案编号
    private int lotteryId;//彩种编号
    private String issue;//期次编号

    public int getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(int schemeId) {
        this.schemeId = schemeId;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
