package com.caipiao.grab.vo;

import java.io.Serializable;

/**
 * 澳客网-胜负彩比赛信息对象
 * Created by Kouyi on 2017/11/10.
 */
public class SfcOkoMatchInfoVO implements Serializable {
    private static final long serialVersionUID = 6835170041103537516L;
    private int index;
    private String awayTeamView;
    private String homeTeamView;
    private String matchTime;
    private String leagueName;
    private String halfScore;   //半场比分
    private String score;   //全场比分
    private String result;//赛果 默认为- 取消为*
    private String sheng;//胜赔
    private String ping;//平赔
    private String fu;//负赔

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAwayTeamView() {
        return awayTeamView;
    }

    public void setAwayTeamView(String awayTeamView) {
        this.awayTeamView = awayTeamView;
    }

    public String getHomeTeamView() {
        return homeTeamView;
    }

    public void setHomeTeamView(String homeTeamView) {
        this.homeTeamView = homeTeamView;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getHalfScore() {
        return halfScore;
    }

    public void setHalfScore(String halfScore) {
        this.halfScore = halfScore;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSheng() {
        return sheng;
    }

    public void setSheng(String sheng) {
        this.sheng = sheng;
    }

    public String getPing() {
        return ping;
    }

    public void setPing(String ping) {
        this.ping = ping;
    }

    public String getFu() {
        return fu;
    }

    public void setFu(String fu) {
        this.fu = fu;
    }
}
