package com.caipiao.grab.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 中国体彩网-胜负彩比赛对象
 * Created by Kouyi on 2017/11/10.
 */
public class SfcMatchVo implements Serializable {
    private static final long serialVersionUID = -5798926200616959715L;
    private SfcPeriodInfoVO lterm;//期次对象
    private List<SfcMatchInfoVO> match_vs;//场次数据

    public SfcPeriodInfoVO getLterm() {
        return lterm;
    }

    public void setLterm(SfcPeriodInfoVO lterm) {
        this.lterm = lterm;
    }

    public List<SfcMatchInfoVO> getMatch_vs() {
        return match_vs;
    }

    public void setMatch_vs(List<SfcMatchInfoVO> match_vs) {
        this.match_vs = match_vs;
    }
}
