package com.caipiao.domain.match;

import java.io.Serializable;
import java.util.Date;

/**
 * 篮球对阵赔率对象
 * Created by kouyi on 2017/11/04.
 */
public class MatchBasketBallSp implements Serializable {
    private static final long serialVersionUID = 5529278768223008394L;
    private Long id;
    private Long matchId;//篮球对阵表id号
    private String matchCode;//竞彩场次号（如20171102001）
    private String jcId;//竞彩编号（如001）
    private String period;//期次编号
    private Double sheng;//胜的sp值
    private Double fu;//负的sp值
    private Double lose;//让分值
    private Double rSheng;//让分胜的sp值
    private Double rfu;//让分负的sp值
    private Double dxf;//大小分值
    private Double df;//大分的sp值
    private Double xf;//小分的sp值
    private Double zs15;//主胜分差1-5的sp值
    private Double zs610;//主胜分差6-10的sp值
    private Double zs1115;//主胜分差11-15的sp值
    private Double zs1620;//主胜分差16-20的sp值
    private Double zs2125;//主胜分差21-25的sp值
    private Double zs26;//主胜分差26+的sp值
    private Double ks15;//客胜分差1-5的sp值
    private Double ks610;//客胜分差6-10的sp值
    private Double ks1115;//客胜分差11-15的sp值
    private Double ks1620;//客胜分差16-20的sp值
    private Double ks2125;//客胜分差21-25的sp值
    private Double ks26;//客胜分差26+的sp值
    private Date createTime;//赔率入库时间
    private Date updateTime;//更新时间

    /**
     * 对阵重要属性字符串 用来判断对阵是否更新过
     * @return
     */
    public String getMatchSpInfo(){
        return matchCode+ "_" + period + "_" + jcId + "_" + zs15 + "_" + zs610 + "_" + zs1115 + "_" + zs1620 + "_" +
                zs2125 + "_" + zs26 + "_" + ks15 + "_" + ks610 + "_" + ks1115 + "_" + ks1620 + "_" + ks2125 + "_" +
                ks26 + "_" + df + "_" + xf + "_" + dxf + "_" + rfu + "_" + rSheng + "_" + lose + "_" + fu + "_" + sheng;
    }

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

    public Double getSheng() {
        return sheng;
    }

    public void setSheng(Double sheng) {
        this.sheng = sheng;
    }

    public Double getFu() {
        return fu;
    }

    public void setFu(Double fu) {
        this.fu = fu;
    }

    public Double getLose() {
        return lose;
    }

    public void setLose(Double lose) {
        this.lose = lose;
    }

    public Double getrSheng() {
        return rSheng;
    }

    public void setrSheng(Double rSheng) {
        this.rSheng = rSheng;
    }

    public Double getRfu() {
        return rfu;
    }

    public void setRfu(Double rfu) {
        this.rfu = rfu;
    }

    public Double getDxf() {
        return dxf;
    }

    public void setDxf(Double dxf) {
        this.dxf = dxf;
    }

    public Double getDf() {
        return df;
    }

    public void setDf(Double df) {
        this.df = df;
    }

    public Double getXf() {
        return xf;
    }

    public void setXf(Double xf) {
        this.xf = xf;
    }

    public Double getZs15() {
        return zs15;
    }

    public void setZs15(Double zs15) {
        this.zs15 = zs15;
    }

    public Double getZs610() {
        return zs610;
    }

    public void setZs610(Double zs610) {
        this.zs610 = zs610;
    }

    public Double getZs1115() {
        return zs1115;
    }

    public void setZs1115(Double zs1115) {
        this.zs1115 = zs1115;
    }

    public Double getZs1620() {
        return zs1620;
    }

    public void setZs1620(Double zs1620) {
        this.zs1620 = zs1620;
    }

    public Double getZs2125() {
        return zs2125;
    }

    public void setZs2125(Double zs2125) {
        this.zs2125 = zs2125;
    }

    public Double getZs26() {
        return zs26;
    }

    public void setZs26(Double zs26) {
        this.zs26 = zs26;
    }

    public Double getKs15() {
        return ks15;
    }

    public void setKs15(Double ks15) {
        this.ks15 = ks15;
    }

    public Double getKs610() {
        return ks610;
    }

    public void setKs610(Double ks610) {
        this.ks610 = ks610;
    }

    public Double getKs1115() {
        return ks1115;
    }

    public void setKs1115(Double ks1115) {
        this.ks1115 = ks1115;
    }

    public Double getKs1620() {
        return ks1620;
    }

    public void setKs1620(Double ks1620) {
        this.ks1620 = ks1620;
    }

    public Double getKs2125() {
        return ks2125;
    }

    public void setKs2125(Double ks2125) {
        this.ks2125 = ks2125;
    }

    public Double getKs26() {
        return ks26;
    }

    public void setKs26(Double ks26) {
        this.ks26 = ks26;
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
}