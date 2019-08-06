package com.caipiao.grab.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 中国体彩网-胜负彩期次信息对象
 * Created by Kouyi on 2017/11/10.
 */
public class SfcPeriodInfoVO implements Serializable {
    private static final long serialVersionUID = -8616719177094792479L;
    private String drawNews;
    @JSONField(name = "fTime")
    private String saleEndTime;
    private int isAP;
    @JSONField(name = "ispool")
    private int isPool;
    private int lType;
    private String numSequence;
    @JSONField(name = "numSequence_pool")
    private String numSequencePool;
    private String number;
    @JSONField(name = "number_pool")
    private String numberPool;
    private String openTime;
    @JSONField(name = "openTime_fmt")
    private String openTimeFmt;
    @JSONField(name = "openTime_fmt1")
    private String openTimeFmt1;
    private String pool;
    @JSONField(name = "sTime")
    private String saleStartTime;
    private String status;
    private String term;
    private String totalSales;
    private String totalSales2;
    @JSONField(name = "totlSaleNews")
    private String totalSaleNews;
    private int verify;

    public String getDrawNews() {
        return drawNews;
    }

    public void setDrawNews(String drawNews) {
        this.drawNews = drawNews;
    }

    public String getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(String saleEndTime) {
        this.saleEndTime = saleEndTime;
    }

    public int getIsAP() {
        return isAP;
    }

    public void setIsAP(int isAP) {
        this.isAP = isAP;
    }

    public int getIsPool() {
        return isPool;
    }

    public void setIsPool(int isPool) {
        this.isPool = isPool;
    }

    public int getlType() {
        return lType;
    }

    public void setlType(int lType) {
        this.lType = lType;
    }

    public String getNumSequence() {
        return numSequence;
    }

    public void setNumSequence(String numSequence) {
        this.numSequence = numSequence;
    }

    public String getNumSequencePool() {
        return numSequencePool;
    }

    public void setNumSequencePool(String numSequencePool) {
        this.numSequencePool = numSequencePool;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumberPool() {
        return numberPool;
    }

    public void setNumberPool(String numberPool) {
        this.numberPool = numberPool;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getOpenTimeFmt() {
        return openTimeFmt;
    }

    public void setOpenTimeFmt(String openTimeFmt) {
        this.openTimeFmt = openTimeFmt;
    }

    public String getOpenTimeFmt1() {
        return openTimeFmt1;
    }

    public void setOpenTimeFmt1(String openTimeFmt1) {
        this.openTimeFmt1 = openTimeFmt1;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    public String getSaleStartTime() {
        return saleStartTime;
    }

    public void setSaleStartTime(String saleStartTime) {
        this.saleStartTime = saleStartTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(String totalSales) {
        this.totalSales = totalSales;
    }

    public String getTotalSales2() {
        return totalSales2;
    }

    public void setTotalSales2(String totalSales2) {
        this.totalSales2 = totalSales2;
    }

    public String getTotalSaleNews() {
        return totalSaleNews;
    }

    public void setTotalSaleNews(String totalSaleNews) {
        this.totalSaleNews = totalSaleNews;
    }

    public int getVerify() {
        return verify;
    }

    public void setVerify(int verify) {
        this.verify = verify;
    }
}
