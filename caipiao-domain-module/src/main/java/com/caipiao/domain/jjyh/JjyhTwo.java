package com.caipiao.domain.jjyh;

import com.util.comparable.ComparableBean;
import com.util.string.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 奖金优化类型对象
 * Created by Kouyi on 2018/5/7.
 */
public class JjyhTwo implements Serializable, Comparable<JjyhTwo> {
    private static final long serialVersionUID = -5863157182328069297L;
    private int index;//索引下标
    private Double jsprize;//奖金
    private String prize;//返回给前端
    private double jssp;//赔率
    private String sp;//返回给前端
    private int mul;//倍数
    private String content;//投注串
    private boolean isCalBonuse;//是否计算奖金
    private List<MatchInfo> matchInfos;//比赛列表

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Double getJsprize() {
        return jsprize;
    }

    public void setJsprize(Double jsprize) {
        this.jsprize = jsprize;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public double getJssp() {
        return jssp;
    }

    public void setJssp(double jssp) {
        this.jssp = jssp;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public int getMul() {
        return mul;
    }

    public void setMul(int mul) {
        this.mul = mul;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCalBonuse() {
        return isCalBonuse;
    }

    public void setCalBonuse(boolean calBonuse) {
        isCalBonuse = calBonuse;
    }

    public List<MatchInfo> getMatchInfos() {
        return matchInfos;
    }

    public void setMatchInfos(List<MatchInfo> matchInfos) {
        this.matchInfos = matchInfos;
    }

    @Override
    public int compareTo(JjyhTwo o) {
        return this.jsprize.compareTo(o.getJsprize());
    }

    public static List<String> filter = new ArrayList<>();
    static {
        filter.add("matchSp");
        filter.add("index");
        filter.add("filterFlag");
        filter.add("choose");
        filter.add("leagueName");
        filter.add("jcId");
        filter.add("rqf");
        filter.add("jssp");
        filter.add("jsprize");
    }
}
