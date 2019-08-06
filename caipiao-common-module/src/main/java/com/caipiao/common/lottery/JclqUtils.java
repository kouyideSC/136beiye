package com.caipiao.common.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.match.MatchBasketBallResult;
import com.caipiao.domain.match.MatchBasketBallSp;
import com.caipiao.domain.vo.JczqMatchVo;

import java.util.Date;

/**
 * 竞彩篮球工具类
 * Created by Kouyi on 2017/11/7.
 */
public class JclqUtils {
    public final static String JCLQ_MATCH_KEY = "JCLQ_MATCH_KEY_71_";//竞彩篮球单个场次对阵缓存
    public final static String JCLQ_MATCH_KEY_LIST = "JCLQ_MATCH_KEY_LIST_71";//竞彩篮球抓取对阵列表缓存
    public final static String JCLQ_MATCH_KEY_SP = "JCLQ_MATCHSP_KEY_71_";//竞彩篮球单个场次赔率缓存
    public final static String JCLQ_MATCH_KEY_LOSE = "JCLQ_MATCHSP_KEY_LOSE_71_";//竞彩篮球单个场次赔率缓存

    /**
     * 竞彩篮球网站销售截止时间
     * 周一到周五,开赛时间0:00-9:30的场次 截止时间=23:45
     * 周一到周五,开赛时间9:30-23:59的场次 截止时间=比赛开始前15分钟
     * 周六到周日,开赛时间1:00-9:30的场次 截止时间=00:45
     * 周六到周日,开赛时间9:30-00:59的场次 截止时间=比赛开始前15分钟
     * @param period
     * @param matchTime
     * @return
     */
    public static Date getJclqSellEndTime(String period, Date matchTime) {
        int week = DateUtil.getWeekInt(DateUtil.dateFormat(period, DateUtil.DEFAULT_DATE1));
        int hour = DateUtil.getHour(matchTime);
        int minute = DateUtil.getMinute(matchTime);

        //固定时间内
        Date today = DateUtil.parseDate(period, DateUtil.DEFAULT_DATE1);
        Date sixDate = DateUtil.parseDate("20190601", DateUtil.DEFAULT_DATE1);
        Date eightDate = DateUtil.parseDate("20200101", DateUtil.DEFAULT_DATE1);
        if(today.getTime() > sixDate.getTime() && today.getTime() < eightDate.getTime()) {
            //2019年特殊杯赛
            StringBuffer endTime = new StringBuffer();
            if (week >= 1 && week <= 5) {//周一到周五
                if (hour < 9 || (hour == 9 && minute < 30)) {//截止时间=23:50
                    endTime.append(DateUtil.dateFormat(DateUtil.addDay(matchTime, -1), DateUtil.DEFAULT_DATE));
                    endTime.append(" 21:40:00");
                } else if(hour > 21) {
                    endTime.append(DateUtil.dateFormat(matchTime, DateUtil.DEFAULT_DATE));
                    endTime.append(" 21:40:00");
                } else {//比赛开始前10分钟
                    endTime.append(DateUtil.dateFormat(DateUtil.addMinute(matchTime, -20), DateUtil.DEFAULT_DATE_TIME));
                }
            } else {//周六到周末
                if (hour < 9 || (hour == 9 && minute < 30)) {//截止时间=00:50
                    endTime.append(DateUtil.dateFormat(DateUtil.addDay(matchTime, -1), DateUtil.DEFAULT_DATE));
                    endTime.append(" 22:40:00");
                } else if(hour > 22) {
                    endTime.append(DateUtil.dateFormat(matchTime, DateUtil.DEFAULT_DATE));
                    endTime.append(" 22:40:00");
                } else {//比赛开始前10分钟
                    endTime.append(DateUtil.dateFormat(DateUtil.addMinute(matchTime, -20), DateUtil.DEFAULT_DATE_TIME));
                }
            }
            return DateUtil.dateDefaultFormat(endTime.toString());
        } else {
            //网站普通规则
            StringBuffer endTime = new StringBuffer();
            if (week >= 1 && week <= 5) {//周一到周五
                if (hour < 9 || (hour == 9 && minute < 30)) {//截止时间=23:45
                    endTime.append(DateUtil.dateFormat(DateUtil.addDay(matchTime, -1), DateUtil.DEFAULT_DATE));
                    endTime.append(" 23:40:00");
                } else {//比赛开始前10分钟
                    endTime.append(DateUtil.dateFormat(DateUtil.addMinute(matchTime, -20), DateUtil.DEFAULT_DATE_TIME));
                }
            } else {//周六到周末
                if ((hour >= 1 && hour < 9) || (hour == 9 && minute < 30)) {//截止时间=00:45
                    endTime.append(DateUtil.dateFormat(matchTime, DateUtil.DEFAULT_DATE));
                    endTime.append(" 00:40:00");
                    //endTime.append(DateUtil.dateFormat(DateUtil.addDay(matchTime, -1), DateUtil.DEFAULT_DATE));
                    //endTime.append(" 23:40:00");
                } else {//比赛开始前10分钟
                    endTime.append(DateUtil.dateFormat(DateUtil.addMinute(matchTime, -20), DateUtil.DEFAULT_DATE_TIME));
                }
            }
            return DateUtil.dateDefaultFormat(endTime.toString());
        }
    }

    /**
     * 竞彩篮球根据全场比分计算赛果
     * @param matchResult
     * @param matchSp
     * @throws Exception
     */
    public static void getBasketballResult(MatchBasketBallResult matchResult, MatchBasketBallSp matchSp, MatchBasketBall match) throws Exception {
        String[] score = matchResult.getScore().split(":");
        int homeScore = Integer.parseInt(score[1]);
        int guestScore = Integer.parseInt(score[0]);

        //胜负玩法-排除未开售
        if(match.getSfStatus() != LotteryConstants.STATUS_CLOSE) {
            if (homeScore > guestScore) {
                matchResult.setSfResult("主胜");
                matchResult.setSfSp(matchSp.getSheng());
            }
            else {
                matchResult.setSfResult("主负");
                matchResult.setSfSp(matchSp.getFu());
            }
        }

        //让分胜负玩法-排除未开售
        if(match.getRfsfStatus() != LotteryConstants.STATUS_CLOSE) {
            if (homeScore + matchSp.getLose() > guestScore) {
                matchResult.setRfsfResult("("+matchSp.getLose()+")主胜");
                matchResult.setRfsfSp(matchSp.getrSheng());
            }
            else {
                matchResult.setRfsfResult("("+matchSp.getLose()+")主负");
                matchResult.setRfsfSp(matchSp.getRfu());
            }
        }

        //大小分玩法-排除未开售
        if(match.getDxfStatus() != LotteryConstants.STATUS_CLOSE) {
            if (homeScore + guestScore > matchSp.getDxf()) {
                matchResult.setDxfResult("("+matchSp.getDxf()+")大分");
                matchResult.setDxf(matchSp.getDxf());
                matchResult.setDxfSp(matchSp.getDf());
            }
            else {
                matchResult.setDxfResult("("+matchSp.getDxf()+")小分");
                matchResult.setDxf(matchSp.getDxf());
                matchResult.setDxfSp(matchSp.getXf());
            }
        }

        //胜分差玩法-排除未开售
        if(match.getSfcStatus() != LotteryConstants.STATUS_CLOSE) {
            int val = homeScore - guestScore;
            if (val > 0) {
                if(val > 0 & val <= 5){
                    matchResult.setSfcResult("主胜1-5");
                    matchResult.setSfcSp(matchSp.getZs15());
                } else if (val > 5 & val <= 10){
                    matchResult.setSfcResult("主胜6-10");
                    matchResult.setSfcSp(matchSp.getZs610());
                } else if (val > 10 & val <= 15){
                    matchResult.setSfcResult("主胜11-15");
                    matchResult.setSfcSp(matchSp.getZs1115());
                } else if (val > 15 & val <= 20){
                    matchResult.setSfcResult("主胜16-20");
                    matchResult.setSfcSp(matchSp.getZs1620());
                } else if (val > 20 & val <= 25){
                    matchResult.setSfcResult("主胜21-25");
                    matchResult.setSfcSp(matchSp.getZs2125());
                } else {
                    matchResult.setSfcResult("主胜26+");
                    matchResult.setSfcSp(matchSp.getZs26());
                }
            } else {
                val = Math.abs(val);
                if(val > 0 & val <= 5){
                    matchResult.setSfcResult("客胜1-5");
                    matchResult.setSfcSp(matchSp.getKs15());
                } else if (val > 5 & val <= 10){
                    matchResult.setSfcResult("客胜6-10");
                    matchResult.setSfcSp(matchSp.getKs610());
                } else if (val > 10 & val <= 15){
                    matchResult.setSfcResult("客胜11-15");
                    matchResult.setSfcSp(matchSp.getKs1115());
                } else if (val > 15 & val <= 20){
                    matchResult.setSfcResult("客胜16-20");
                    matchResult.setSfcSp(matchSp.getKs1620());
                } else if (val > 20 & val <= 25){
                    matchResult.setSfcResult("客胜21-25");
                    matchResult.setSfcSp(matchSp.getKs2125());
                } else {
                    matchResult.setSfcResult("客胜26+");
                    matchResult.setSfcSp(matchSp.getKs26());
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(getJclqSellEndTime("20171107",DateUtil.dateDefaultFormat("2017-11-08 09:30:00")));
    }

}
