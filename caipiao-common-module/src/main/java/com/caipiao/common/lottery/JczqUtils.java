package com.caipiao.common.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.match.MatchFootBallResult;
import com.caipiao.domain.match.MatchFootBallSp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 竞彩足球工具类
 * Created by Kouyi on 2017/11/7.
 */
public class JczqUtils {
    public final static String JCZQ_MATCH_KEY = "JCZQ_MATCH_KEY_70_";//竞彩足球单个场次对阵缓存
    public final static String JCZQ_MATCH_KEY_LIST = "JCZQ_MATCH_KEY_LIST_70";//竞彩足球抓取对阵列表缓存
    public final static String JCZQ_MATCH_KEY_SP = "JCZQ_MATCHSP_KEY_70_";//竞彩足球单个场次赔率缓存
    public final static String JCZQ_MATCH_KEY_LOSE = "JCZQ_MATCHSP_KEY_LOSE_70_";//竞彩足球单个场次赔率缓存

    /**
     * 竞彩足球网站销售截止时间
     * 周一到周五,开赛时间0:00-9:30的场次 截止时间=23:50
     * 周一到周五,开赛时间9:30-23:59的场次 截止时间=比赛开始前10分钟
     * 周六到周日,开赛时间1:00-9:30的场次 截止时间=00:50
     * 周六到周日,开赛时间9:30-00:59的场次 截止时间=比赛开始前10分钟
     * @param period
     * @param matchTime
     * @return
     */
    public static Date getJczqSellEndTime(String period, Date matchTime) {
        int week = DateUtil.getWeekInt(DateUtil.dateFormat(period, DateUtil.DEFAULT_DATE1));
        int hour = DateUtil.getHour(matchTime);
        int minute = DateUtil.getMinute(matchTime);

        //固定时间内
        Date today = DateUtil.parseDate(period+"000001", DateUtil.LOG_DATE_TIME2);
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
                if (hour < 9 || (hour == 9 && minute < 30)) {//截止时间=23:50
                    endTime.append(DateUtil.dateFormat(DateUtil.addDay(matchTime, -1), DateUtil.DEFAULT_DATE));
                    endTime.append(" 23:40:00");
                } else {//比赛开始前10分钟
                    endTime.append(DateUtil.dateFormat(DateUtil.addMinute(matchTime, -20), DateUtil.DEFAULT_DATE_TIME));
                }
            } else {//周六到周末
                if ((hour >= 1 && hour < 9) || (hour == 9 && minute < 30)) {//截止时间=00:50
                    endTime.append(DateUtil.dateFormat(matchTime, DateUtil.DEFAULT_DATE));
                    endTime.append(" 00:40:00");
                    //endTime.append(DateUtil.dateFormat(DateUtil.addDay(matchTime, -1), DateUtil.DEFAULT_DATE));
                    //endTime.append(" 23:40:00");//春节临时处理
                } else {//比赛开始前10分钟
                    endTime.append(DateUtil.dateFormat(DateUtil.addMinute(matchTime, -20), DateUtil.DEFAULT_DATE_TIME));
                }
            }
            return DateUtil.dateDefaultFormat(endTime.toString());
        }
    }

    /**
     * 竞彩足球网站销售截止时间-含世界杯赛事
     * 周一到周五,开赛时间0:00-9:30的场次 截止时间=23:50
     * 周一到周五,开赛时间9:30-23:59的场次 截止时间=比赛开始前10分钟
     * 周六到周日,开赛时间1:00-9:30的场次 截止时间=00:50
     * 周六到周日,开赛时间9:30-00:59的场次 截止时间=比赛开始前10分钟
     * @param period
     * @param leagueName
     * @param matchTime
     * @return
     */
    public static Date getJczqSellEndTime(String period, String leagueName, Date matchTime) {
        int week = DateUtil.getWeekInt(DateUtil.dateFormat(period, DateUtil.DEFAULT_DATE1));
        int hour = DateUtil.getHour(matchTime);
        int minute = DateUtil.getMinute(matchTime);

        //网站普通规则
        StringBuffer endTime = new StringBuffer();
        if(!"世界杯".equals(leagueName)) {//世界杯以外的赛事照旧
            if (week >= 1 && week <= 5) {//周一到周五
                if (hour < 9 || (hour == 9 && minute < 30)) {//截止时间=23:50
                    endTime.append(DateUtil.dateFormat(DateUtil.addDay(matchTime, -1), DateUtil.DEFAULT_DATE));
                    endTime.append(" 23:40:00");
                } else {//比赛开始前10分钟
                    endTime.append(DateUtil.dateFormat(DateUtil.addMinute(matchTime, -20), DateUtil.DEFAULT_DATE_TIME));
                }
            } else {//周六到周末
                //if ((hour >= 1 && hour < 9) || (hour == 9 && minute < 30)) {//截止时间=00:50
                if (hour < 9 || (hour == 9 && minute < 30)) {//截止时间=00:50
                    //endTime.append(DateUtil.dateFormat(matchTime, DateUtil.DEFAULT_DATE));
                    //endTime.append(" 00:50:00");
                    endTime.append(DateUtil.dateFormat(DateUtil.addDay(matchTime, -1), DateUtil.DEFAULT_DATE));
                    endTime.append(" 23:40:00");
                } else {//比赛开始前10分钟
                    endTime.append(DateUtil.dateFormat(DateUtil.addMinute(matchTime, -20), DateUtil.DEFAULT_DATE_TIME));
                }
            }
        }
        else //世界杯赛事特殊处理截止时间
        {
            endTime.append(DateUtil.dateFormat(DateUtil.addMinute(matchTime, -10), DateUtil.DEFAULT_DATE_TIME));
        }
        return DateUtil.dateDefaultFormat(endTime.toString());
    }

    /**
     * 竞彩足球根据半全场比分计算赛果
     * @param matchResult
     * @param matchSp
     * @throws Exception
     */
    public static void getFootballResult(MatchFootBallResult matchResult, MatchFootBallSp matchSp, MatchFootBall match) throws Exception {
        String[] halfScore = matchResult.getHalfScore().split(":");
        String[] score = matchResult.getScore().split(":");
        int homeHalfScore = Integer.parseInt(halfScore[0]);
        int guestHalfScore = Integer.parseInt(halfScore[1]);
        int homeScore = Integer.parseInt(score[0]);
        int guestScore = Integer.parseInt(score[1]);

        //胜平负玩法-排除未开售
        if(match.getSpfStatus() != LotteryConstants.STATUS_CLOSE) {
            if (homeScore > guestScore) {
                matchResult.setSpfResult("主胜");
                matchResult.setSpfSp(matchSp.getSheng());
            }
            else if (homeScore == guestScore) {
                matchResult.setSpfResult("平");
                matchResult.setSpfSp(matchSp.getPing());
            }
            else {
                matchResult.setSpfResult("主负");
                matchResult.setSpfSp(matchSp.getFu());
            }
        }

        //让球胜平负玩法-排除未开售
        if(match.getRqspfStatus() != LotteryConstants.STATUS_CLOSE) {
            if (homeScore + match.getLose() > guestScore) {
                matchResult.setRqspfResult("("+match.getLose()+")主胜");
                matchResult.setRqspfSp(matchSp.getrSheng());
            }
            else if (homeScore + match.getLose() == guestScore) {
                matchResult.setRqspfResult("("+match.getLose()+")平");
                matchResult.setRqspfSp(matchSp.getrPing());
            }
            else {
                matchResult.setRqspfResult("("+match.getLose()+")主负");
                matchResult.setRqspfSp(matchSp.getRfu());
            }
        }

        //总进球玩法-排除未开售
        if(match.getZjqStatus() != LotteryConstants.STATUS_CLOSE) {
            int zjq = homeScore + guestScore;
            if(zjq > 7) { zjq = 7; }
            matchResult.setZjqResult(zjq + (zjq > 6 ? "+":""));
            if (zjq == 0) {
                matchResult.setZjqSp(matchSp.getT0());
            }
            else if (zjq == 1) {
                matchResult.setZjqSp(matchSp.getT1());
            }
            else if (zjq == 2) {
                matchResult.setZjqSp(matchSp.getT2());
            }
            else if (zjq == 3) {
                matchResult.setZjqSp(matchSp.getT3());
            }
            else if (zjq == 4) {
                matchResult.setZjqSp(matchSp.getT4());
            }
            else if (zjq == 5) {
                matchResult.setZjqSp(matchSp.getT5());
            }
            else if (zjq == 6) {
                matchResult.setZjqSp(matchSp.getT6());
            }
            else {
                matchResult.setZjqSp(matchSp.getT7());
            }
        }

        //半全场玩法-排除未开售
        if(match.getBqcStatus() != LotteryConstants.STATUS_CLOSE) {
            if (homeHalfScore > guestHalfScore) {//胜
                if (homeScore > guestScore) {
                    matchResult.setBqcResult("胜胜");
                    matchResult.setBqcSp(matchSp.getSs());
                } else if (homeScore == guestScore) {
                    matchResult.setBqcResult("胜平");
                    matchResult.setBqcSp(matchSp.getSp());
                } else {
                    matchResult.setBqcResult("胜负");
                    matchResult.setBqcSp(matchSp.getSf());
                }
            } else if (homeHalfScore == guestHalfScore) {//平
                if (homeScore > guestScore) {
                    matchResult.setBqcResult("平胜");
                    matchResult.setBqcSp(matchSp.getPs());
                } else if (homeScore == guestScore) {
                    matchResult.setBqcResult("平平");
                    matchResult.setBqcSp(matchSp.getPp());
                } else {
                    matchResult.setBqcResult("平负");
                    matchResult.setBqcSp(matchSp.getPf());
                }
            } else {
                if (homeScore > guestScore) {
                    matchResult.setBqcResult("负胜");
                    matchResult.setBqcSp(matchSp.getFs());
                } else if (homeScore == guestScore) {
                    matchResult.setBqcResult("负平");
                    matchResult.setBqcSp(matchSp.getFp());
                } else {
                    matchResult.setBqcResult("负负");
                    matchResult.setBqcSp(matchSp.getFf());
                }
            }
        }

        //比分玩法-排除未开售
        if(match.getBfStatus() != LotteryConstants.STATUS_CLOSE) {
            String result = getBfValue(homeScore, guestScore);
            matchResult.setBfResult(result);
            if (result.equals("1:0")) {
                matchResult.setBfSp(matchSp.getS10());
            }
            if (result.equals("2:0")) {
                matchResult.setBfSp(matchSp.getS20());
            }
            if (result.equals("3:0")) {
                matchResult.setBfSp(matchSp.getS30());
            }
            if (result.equals("4:0")) {
                matchResult.setBfSp(matchSp.getS40());
            }
            if (result.equals("5:0")) {
                matchResult.setBfSp(matchSp.getS50());
            }
            if (result.equals("2:1")) {
                matchResult.setBfSp(matchSp.getS21());
            }
            if (result.equals("3:1")) {
                matchResult.setBfSp(matchSp.getS31());
            }
            if (result.equals("4:1")) {
                matchResult.setBfSp(matchSp.getS41());
            }
            if (result.equals("5:1")) {
                matchResult.setBfSp(matchSp.getS51());
            }
            if (result.equals("3:2")) {
                matchResult.setBfSp(matchSp.getS32());
            }
            if (result.equals("4:2")) {
                matchResult.setBfSp(matchSp.getS42());
            }
            if (result.equals("5:2")) {
                matchResult.setBfSp(matchSp.getS52());
            }
            if (result.equals("9:0")) {
                matchResult.setBfResult("胜其他");
                matchResult.setBfSp(matchSp.getsOther());
            }
            if (result.equals("0:0")) {
                matchResult.setBfSp(matchSp.getP00());
            }
            if (result.equals("1:1")) {
                matchResult.setBfSp(matchSp.getP11());
            }
            if (result.equals("2:2")) {
                matchResult.setBfSp(matchSp.getP22());
            }
            if (result.equals("3:3")) {
                matchResult.setBfSp(matchSp.getP33());
            }
            if (result.equals("9:9")) {
                matchResult.setBfResult("平其他");
                matchResult.setBfSp(matchSp.getpOther());
            }
            if (result.equals("0:1")) {
                matchResult.setBfSp(matchSp.getF01());
            }
            if (result.equals("0:2")) {
                matchResult.setBfSp(matchSp.getF02());
            }
            if (result.equals("0:3")) {
                matchResult.setBfSp(matchSp.getF03());
            }
            if (result.equals("0:4")) {
                matchResult.setBfSp(matchSp.getF04());
            }
            if (result.equals("0:5")) {
                matchResult.setBfSp(matchSp.getF05());
            }
            if (result.equals("1:2")) {
                matchResult.setBfSp(matchSp.getF12());
            }
            if (result.equals("1:3")) {
                matchResult.setBfSp(matchSp.getF13());
            }
            if (result.equals("1:4")) {
                matchResult.setBfSp(matchSp.getF14());
            }
            if (result.equals("1:5")) {
                matchResult.setBfSp(matchSp.getF15());
            }
            if (result.equals("2:3")) {
                matchResult.setBfSp(matchSp.getF23());
            }
            if (result.equals("2:4")) {
                matchResult.setBfSp(matchSp.getF24());
            }
            if (result.equals("2:5")) {
                matchResult.setBfSp(matchSp.getF25());
            }
            if (result.equals("0:9")) {
                matchResult.setBfResult("负其他");
                matchResult.setBfSp(matchSp.getfOther());
            }
        }
    }

    //比分格式化
    private static String getBfValue(int homeScore, int guestScore){
        String key = homeScore + ":" + guestScore;
        if (homeScore > guestScore) {
            if (homeScore == guestScore && homeScore > 3) {
                key = "9:9";
            }
            if (homeScore == 4 && guestScore == 3) {
                key = "9:0";
            }
            if (homeScore == 5 && guestScore > 2) {
                key = "9:0";
            }
            if (homeScore > 5) {
                key = "9:0";
            }
        } else if (guestScore == homeScore) {
            if (guestScore > 3) {
                key = "9:9";
            }
        } else {
            if (guestScore == 4 && homeScore == 3) {
                key = "0:9";
            }
            if (guestScore == 5 && homeScore > 2) {
                key = "0:9";
            }
            if (guestScore > 5) {
                key = "0:9";
            }
        }
        return key;
    }

}
