package com.caipiao.taskcenter.award.util;

/**
 * 计奖业务bean
 * Created by kouyi on 2017/12/28.
 * @author kouyi
 */
public class BingoUtil {
    private String spValue;//sp值
    private String code;//投注串
    private int time;//是否命中

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getSpValue() {
        return spValue;
    }
    public void setSpValue(String spValue) {
        this.spValue = spValue;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
