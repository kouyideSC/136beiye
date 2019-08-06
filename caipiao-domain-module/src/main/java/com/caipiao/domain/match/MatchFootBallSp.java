package com.caipiao.domain.match;

import java.io.Serializable;
import java.util.Date;

/**
 * 足球对阵赔率对象
 * Created by kouyi on 2017/11/04.
 */
public class MatchFootBallSp implements Serializable {
    private static final long serialVersionUID = -8660747452976641216L;
    private Long id;
    private Long matchId;//足球对阵表id号
    private String matchCode;//竞彩场次号（如20171102001）
    private String jcId;//竞彩编号（如001）
    private String period;//期次编号
    private Double f01;//比分0:1的sp值
    private Double f02;//比分0:2的sp值
    private Double f03;//比分0:3的sp值
    private Double f04;//比分0:4的sp值
    private Double f05;//比分0:5的sp值
    private Double f12;//比分1:2的sp值
    private Double f13;//比分1:3的sp值
    private Double f14;//比分1:4的sp值
    private Double f15;//比分1:5的sp值
    private Double f23;//比分2:3的sp值
    private Double f24;//比分2:4的sp值
    private Double f25;//比分2:5的sp值
    private Double fOther;//比分负其他的sp值
    private Double p00;//比分0:0的sp值
    private Double p11;//比分1:1的sp值
    private Double p22;//比分2:2的sp值
    private Double p33;//比分3:3的sp值
    private Double pOther;//比分平其他的sp值
    private Double s10;//比分1:0的sp值
    private Double s20;//比分2:0的sp值
    private Double s30;//比分3:0的sp值
    private Double s40;//比分4:0的sp值
    private Double s50;//比分5:0的sp值
    private Double s21;//比分2:1的sp值
    private Double s31;//比分3:1的sp值
    private Double s41;//比分4:1的sp值
    private Double s51;//比分5:1的sp值
    private Double s32;//比分3:2的sp值
    private Double s42;//比分4:2的sp值
    private Double s52;//比分5:2的sp值
    private Double sOther;//比分胜其他的sp值
    private Double ff;//半全场负负的sp值
    private Double fp;//半全场负平的sp值
    private Double fs;//半全场负胜的sp值
    private Double pf;//半全场平负的sp值
    private Double pp;//半全场平平的sp值
    private Double ps;//半全场平胜的sp值
    private Double sf;//半全场胜负的sp值
    private Double sp;//半全场胜平的sp值
    private Double ss;//半全场胜胜的sp值
    private Double sheng;//胜的sp值
    private Double ping;//平的sp值
    private Double fu;//负的sp值
    private Double rSheng;//让球胜的sp值
    private Double rPing;//让球平的sp值
    private Double rfu;//让球负的sp值
    private Double t0;//总进球0的sp值
    private Double t1;//总进球1的sp值
    private Double t2;//总进球2的sp值
    private Double t3;//总进球3的sp值
    private Double t4;//总进球4的sp值
    private Double t5;//总进球5的sp值
    private Double t6;//总进球6的sp值
    private Double t7;//总进球7+的sp值
    private Date createTime;//赔率入库时间
    private Date updateTime;//更新时间

    /**
     * 对阵重要属性字符串 用来判断对阵是否更新过
     * @return
     */
    public String getMatchSpInfo(){
        return matchCode+ "_" + period + "_" + jcId + "_" + f01 + "_" + f02 + "_" + f03 + "_" + f04 + "_" + f05 + "_" +
               f12 + "_" + f13 + "_" + f14 + "_" + f15 + "_" + f23 + "_" + f24 + "_" + f25 + "_" + fOther + "_" +
               p00 + "_" + p11 + "_" + p22 + "_" + p33 + "_" + pOther + "_" + s10 + "_" + s20 + "_" + s30 + "_" +
               s40 + "_" + s50 + "_" + s21 + "_" + s31 + "_" + s41 + "_" + s51 + "_" + s32 + "_" + s42 + "_" +
               s52 + "_" + sOther + "_" + ff + "_" + fp + "_" + fs + "_" + pf + "_" + pp + "_" + ps + "_" + sf + "_" +
               sp + "_" + ss + "_" + sheng + "_" + ping + "_" + fu + "_" + rSheng + "_" + rPing + "_" + rfu + "_" +
               t0 + "_" + t1 + "_" + t2 + "_" + t3 + "_" + t4 + "_" + t5 + "_" + t6 + "_" + t7;
    }

    private Integer lose;//让球数

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getJcId() {
        return jcId;
    }

    public void setJcId(String jcId) {
        this.jcId = jcId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getF01() {
        return f01;
    }

    public void setF01(Double f01) {
        this.f01 = f01;
    }

    public Double getF02() {
        return f02;
    }

    public void setF02(Double f02) {
        this.f02 = f02;
    }

    public Double getF03() {
        return f03;
    }

    public void setF03(Double f03) {
        this.f03 = f03;
    }

    public Double getF04() {
        return f04;
    }

    public void setF04(Double f04) {
        this.f04 = f04;
    }

    public Double getF05() {
        return f05;
    }

    public void setF05(Double f05) {
        this.f05 = f05;
    }

    public Double getF12() {
        return f12;
    }

    public void setF12(Double f12) {
        this.f12 = f12;
    }

    public Double getF13() {
        return f13;
    }

    public void setF13(Double f13) {
        this.f13 = f13;
    }

    public Double getF14() {
        return f14;
    }

    public void setF14(Double f14) {
        this.f14 = f14;
    }

    public Double getF15() {
        return f15;
    }

    public void setF15(Double f15) {
        this.f15 = f15;
    }

    public Double getF23() {
        return f23;
    }

    public void setF23(Double f23) {
        this.f23 = f23;
    }

    public Double getF24() {
        return f24;
    }

    public void setF24(Double f24) {
        this.f24 = f24;
    }

    public Double getF25() {
        return f25;
    }

    public void setF25(Double f25) {
        this.f25 = f25;
    }

    public Double getfOther() {
        return fOther;
    }

    public void setfOther(Double fOther) {
        this.fOther = fOther;
    }

    public Double getP00() {
        return p00;
    }

    public void setP00(Double p00) {
        this.p00 = p00;
    }

    public Double getP11() {
        return p11;
    }

    public void setP11(Double p11) {
        this.p11 = p11;
    }

    public Double getP22() {
        return p22;
    }

    public void setP22(Double p22) {
        this.p22 = p22;
    }

    public Double getP33() {
        return p33;
    }

    public void setP33(Double p33) {
        this.p33 = p33;
    }

    public Double getpOther() {
        return pOther;
    }

    public void setpOther(Double pOther) {
        this.pOther = pOther;
    }

    public Double getS10() {
        return s10;
    }

    public void setS10(Double s10) {
        this.s10 = s10;
    }

    public Double getS20() {
        return s20;
    }

    public void setS20(Double s20) {
        this.s20 = s20;
    }

    public Double getS30() {
        return s30;
    }

    public void setS30(Double s30) {
        this.s30 = s30;
    }

    public Double getS40() {
        return s40;
    }

    public void setS40(Double s40) {
        this.s40 = s40;
    }

    public Double getS50() {
        return s50;
    }

    public void setS50(Double s50) {
        this.s50 = s50;
    }

    public Double getS21() {
        return s21;
    }

    public void setS21(Double s21) {
        this.s21 = s21;
    }

    public Double getS31() {
        return s31;
    }

    public void setS31(Double s31) {
        this.s31 = s31;
    }

    public Double getS41() {
        return s41;
    }

    public void setS41(Double s41) {
        this.s41 = s41;
    }

    public Double getS51() {
        return s51;
    }

    public void setS51(Double s51) {
        this.s51 = s51;
    }

    public Double getS32() {
        return s32;
    }

    public void setS32(Double s32) {
        this.s32 = s32;
    }

    public Double getS42() {
        return s42;
    }

    public void setS42(Double s42) {
        this.s42 = s42;
    }

    public Double getS52() {
        return s52;
    }

    public void setS52(Double s52) {
        this.s52 = s52;
    }

    public Double getsOther() {
        return sOther;
    }

    public void setsOther(Double sOther) {
        this.sOther = sOther;
    }

    public Double getFf() {
        return ff;
    }

    public void setFf(Double ff) {
        this.ff = ff;
    }

    public Double getFp() {
        return fp;
    }

    public void setFp(Double fp) {
        this.fp = fp;
    }

    public Double getFs() {
        return fs;
    }

    public void setFs(Double fs) {
        this.fs = fs;
    }

    public Double getPf() {
        return pf;
    }

    public void setPf(Double pf) {
        this.pf = pf;
    }

    public Double getPp() {
        return pp;
    }

    public void setPp(Double pp) {
        this.pp = pp;
    }

    public Double getPs() {
        return ps;
    }

    public void setPs(Double ps) {
        this.ps = ps;
    }

    public Double getSf() {
        return sf;
    }

    public void setSf(Double sf) {
        this.sf = sf;
    }

    public Double getSp() {
        return sp;
    }

    public void setSp(Double sp) {
        this.sp = sp;
    }

    public Double getSs() {
        return ss;
    }

    public void setSs(Double ss) {
        this.ss = ss;
    }

    public Double getSheng() {
        return sheng;
    }

    public void setSheng(Double sheng) {
        this.sheng = sheng;
    }

    public Double getPing() {
        return ping;
    }

    public void setPing(Double ping) {
        this.ping = ping;
    }

    public Double getFu() {
        return fu;
    }

    public void setFu(Double fu) {
        this.fu = fu;
    }

    public Double getrSheng() {
        return rSheng;
    }

    public void setrSheng(Double rSheng) {
        this.rSheng = rSheng;
    }

    public Double getrPing() {
        return rPing;
    }

    public void setrPing(Double rPing) {
        this.rPing = rPing;
    }

    public Double getRfu() {
        return rfu;
    }

    public void setRfu(Double rfu) {
        this.rfu = rfu;
    }

    public Double getT0() {
        return t0;
    }

    public void setT0(Double t0) {
        this.t0 = t0;
    }

    public Double getT1() {
        return t1;
    }

    public void setT1(Double t1) {
        this.t1 = t1;
    }

    public Double getT2() {
        return t2;
    }

    public void setT2(Double t2) {
        this.t2 = t2;
    }

    public Double getT3() {
        return t3;
    }

    public void setT3(Double t3) {
        this.t3 = t3;
    }

    public Double getT4() {
        return t4;
    }

    public void setT4(Double t4) {
        this.t4 = t4;
    }

    public Double getT5() {
        return t5;
    }

    public void setT5(Double t5) {
        this.t5 = t5;
    }

    public Double getT6() {
        return t6;
    }

    public void setT6(Double t6) {
        this.t6 = t6;
    }

    public Double getT7() {
        return t7;
    }

    public void setT7(Double t7) {
        this.t7 = t7;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getLose() {
        return lose;
    }

    public void setLose(Integer lose) {
        this.lose = lose;
    }
}