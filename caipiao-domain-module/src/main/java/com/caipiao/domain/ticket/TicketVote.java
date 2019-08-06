package com.caipiao.domain.ticket;

import java.io.Serializable;

/**
 * 出票商对象
 * Created by kouyi on 2017/12/01.
 */
public class TicketVote implements Serializable {
    private static final long serialVersionUID = 5062526963832825198L;
    private Integer id; //主键ID
    private String voteId; //出票商id
    private String voteName; //出票商名称
    private Integer status; //启用状态, 0:未启用;1:启用
    private String apiUrl;//接口地址
    private String key;//秘钥
    private String desc; //描述信息

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
