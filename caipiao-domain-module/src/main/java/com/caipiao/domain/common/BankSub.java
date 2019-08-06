package com.caipiao.domain.common;

import java.io.Serializable;

/**
 * 银行(支行)对象
 */
public class BankSub implements Serializable
{
    private String subBankName;//支行名称
    private String bankCode;//银行名称
    private String cityCode;//支行所在城市编号
    private String address;//支行所在详细地址
    private String remark;//备注(说明)
    private int status;//状态(0-废弃 1-正常)

    public String getSubBankName() {
        return subBankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getAddress() {
        return address;
    }

    public String getRemark() {
        return remark;
    }

    public int getStatus() {
        return status;
    }
}