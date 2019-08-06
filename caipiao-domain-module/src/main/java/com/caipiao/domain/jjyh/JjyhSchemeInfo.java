package com.caipiao.domain.jjyh;

import java.io.Serializable;

/**
 * 奖金优化对应方案结果对象-奖金优化使用
 * Created by Kouyi on 2018/5/7.
 */
public class JjyhSchemeInfo implements Serializable {
    private static final long serialVersionUID = 3397482982730149474L;
    private String lotteryId;//彩种编号
    private String desc;//描述-页面使用
    private String passType;//串关方式
    private String money;//总金额
    private String schemeContent;//原始投注串
    private JjyhOne avgYh;//平均优化
    private JjyhOne coldYh;//搏冷优化
    private JjyhOne hotYh;//博热优化

    public String getSchemeContent() {
        return schemeContent;
    }

    public void setSchemeContent(String schemeContent) {
        this.schemeContent = schemeContent;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public JjyhOne getAvgYh() {
        return avgYh;
    }

    public void setAvgYh(JjyhOne avgYh) {
        this.avgYh = avgYh;
    }

    public JjyhOne getColdYh() {
        return coldYh;
    }

    public void setColdYh(JjyhOne coldYh) {
        this.coldYh = coldYh;
    }

    public JjyhOne getHotYh() {
        return hotYh;
    }

    public void setHotYh(JjyhOne hotYh) {
        this.hotYh = hotYh;
    }
}
