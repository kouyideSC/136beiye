package com.caipiao.domain.base;

import java.io.Serializable;

/**
 * 票业务标准bean
 * @user kouyi
 * @date 2017-09-20
 */
public class TicketBean extends BaseBean implements Serializable {
    private static final long serialVersionUID = -5542596237569827367L;
    private String batchId;//批次编号
    private String voteId;//出票商编号

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }
}
