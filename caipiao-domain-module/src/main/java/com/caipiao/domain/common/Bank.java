package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 银行对象
 */
public class Bank implements Serializable
{
    private String bankCode;//银行编号
    private String bankName;//银行名称
    private String abbreviation;//银行简称
    private String logo;//银行logo图片路径
    private String remark;//备注(说明)
    private int status;//状态(0-废弃 1-正常)
    private int needSub;//转账是否需要支行(0-不需要 1-需要)

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNeedSub() {
        return needSub;
    }

    public void setNeedSub(int needSub) {
        this.needSub = needSub;
    }
}