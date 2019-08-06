package com.caipiao.domain.scheme;

import java.io.Serializable;
import java.util.Date;

/**
 * 方案对应比赛对象
 * Created by kouyi on 2017/11/04.
 */
public class SchemeMatches implements Serializable {
    private static final long serialVersionUID = -6425944350274881972L;
    private Long id;
    private Long schemeId;//方案id
    private String schemeOrderId;//订单编号
    private String lotteryId;//彩种编号
    private String matchCode;//场次竞彩编号
    private Date createTime;//创建时间

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

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}