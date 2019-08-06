package com.caipiao.domain.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 前端展示-彩种对象
 * Created by kouyi on 2017/11/13.
 */
public class LotteryVo implements Serializable {
    private static final long serialVersionUID = 4519549775940306589L;
    private String lid;//彩种编号
    private String name;//彩种名称-客户端使用shortName
    private Integer order;//排序号
    private Integer open;//彩种说明文字背景开关是否打开(0-否 1-是)
    private String desc;//描述语（app显示）
    private String icon;//图标地址
    private int maxMul;//最大销售倍数限制
    private int maxMoney;//最大投注金额限制
    private Integer maxZhNum;//单方案最多追号期数
    private String aimg;//活动图片

    public int getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(int maxMoney) {
        this.maxMoney = maxMoney;
    }

    public int getMaxMul() {
        return maxMul;
    }

    public void setMaxMul(int maxMul) {
        this.maxMul = maxMul;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getMaxZhNum() {
        return maxZhNum;
    }

    public void setMaxZhNum(Integer maxZhNum) {
        this.maxZhNum = maxZhNum;
    }

    public String getAimg() {
        return aimg;
    }

    public void setAimg(String aimg) {
        this.aimg = aimg;
    }
}
