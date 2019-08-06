package com.caipiao.grab.vo;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

/**
 * 中国体彩网-胜负彩期次对象
 * Created by Kouyi on 2017/11/10.
 */
public class SfcPeriodVO implements Serializable {
    private static final long serialVersionUID = -6126752385941264051L;
    //奖期数组
    private String[] tremList;
    //当前期的前一期
    private String ctrem;

    public String[] getTremList() {
        return tremList;
    }

    public void setTremList(String[] tremList) {
        this.tremList = tremList;
    }

    public String getCtrem() {
        return ctrem;
    }

    public void setCtrem(String ctrem) {
        this.ctrem = ctrem;
    }
}
