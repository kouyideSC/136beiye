package com.caipiao.grab.vo;

import java.io.Serializable;

/**
 * 中国体彩网-胜负彩比赛信息对象
 * Created by Kouyi on 2017/11/10.
 */
public class SfcMatchInfoVO implements Serializable {
    private static final long serialVersionUID = -6710312075301066349L;
    private String awayTeamView;
    private String homeTeamView;
    private String matchTime;
    private String matchname;
    private String rb;
    private String results;
    private String rq;
    private String teamView;

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

    public String getMatchname() {
        return matchname;
    }

    public void setMatchname(String matchname) {
        this.matchname = matchname;
    }

    public String getRb() {
        return rb;
    }

    public void setRb(String rb) {
        this.rb = rb;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getRq() {
        return rq;
    }

    public void setRq(String rq) {
        this.rq = rq;
    }

    public String getTeamView() {
        return teamView;
    }

    public void setTeamView(String teamView) {
        this.teamView = teamView;
    }
}
