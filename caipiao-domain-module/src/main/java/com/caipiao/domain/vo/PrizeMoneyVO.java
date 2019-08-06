package com.caipiao.domain.vo;

/**
 * Created by kouyi on 2017/12/28.
 */
public class PrizeMoneyVO {
    private Integer id;
    private String info;
    private Integer isWin;
    private double amoney;
    private double taxMoney;
    private double addMoney;
    private double taxAddmoney;
    private Integer bonusState;

    public PrizeMoneyVO(){}
    public PrizeMoneyVO(Integer id, String info, Integer isWin) {
        this.id = id;
        this.info = info;
        this.isWin = isWin;
    }
    public PrizeMoneyVO(Integer id, double amoney, double taxMoney, double addMoney, double taxAddmoney){
        this.id = id;
        this.amoney = amoney;
        this.taxMoney = taxMoney;
        this.addMoney = addMoney;
        this.taxAddmoney = taxAddmoney;
    }
    //计奖结果更新
    public PrizeMoneyVO(Integer id, String info, Integer isWin, Integer bonusState, double amoney, double taxMoney,
                        double addMoney, double taxAddmoney){
        this.id = id;
        this.info = info;
        this.isWin = isWin;
        this.bonusState = bonusState;
        this.amoney = amoney;
        this.taxMoney = taxMoney;
        this.addMoney = addMoney;
        this.taxAddmoney = taxAddmoney;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getIsWin() {
        return isWin;
    }

    public void setIsWin(Integer isWin) {
        this.isWin = isWin;
    }

    public double getAmoney() {
        return amoney;
    }

    public void setAmoney(double amoney) {
        this.amoney = amoney;
    }

    public double getTaxMoney() {
        return taxMoney;
    }

    public void setTaxMoney(double taxMoney) {
        this.taxMoney = taxMoney;
    }

    public double getAddMoney() {
        return addMoney;
    }

    public void setAddMoney(double addMoney) {
        this.addMoney = addMoney;
    }

    public double getTaxAddmoney() {
        return taxAddmoney;
    }

    public void setTaxAddmoney(double taxAddmoney) {
        this.taxAddmoney = taxAddmoney;
    }

    public Integer getBonusState() {
        return bonusState;
    }

    public void setBonusState(Integer bonusState) {
        this.bonusState = bonusState;
    }
}
