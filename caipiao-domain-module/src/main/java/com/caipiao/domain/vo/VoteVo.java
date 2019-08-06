package com.caipiao.domain.vo;

import java.awt.*;
import java.io.Serializable;

/**
 * 出票商对象
 * Created by kouyi on 2017/12/11.
 */
public class VoteVo implements Serializable {
    private static final long serialVersionUID = 2085938282084616340L;
    private String voteId;//出票商编号
    private String voteName; //出票商名称
    private String status;//出票商状态
    private String key;//商户KEY
    private String apiUrl;//出票商接口地址
    private String playType;//玩法编号
    private String playName;//玩法名称
    private String receiveTime;//提票有效时间段
    private String period;//期次-提票用

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getVoteName() {
        return voteName;
    }

    public void setVoteName(String voteName) {
        this.voteName = voteName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
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

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }
}
