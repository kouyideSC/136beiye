package com.caipiao.grab.jc.handler;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.lottery.JczqUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.grab.util.GrabForJcUtil;
import com.caipiao.grab.util.JcUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 抓取竞彩足球对阵
 * Created by kouyi on 2017/11/07.
 */
@Component("grabForJczqMatch")
public class GrabForJczqMatch extends Grab<Map<String,List<MatchFootBall>>, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForJczqMatch.class);

    @Override
    public Map<String,List<MatchFootBall>> parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[竞彩足球对阵抓取] 页面数据为空!");
                return null;
            }
            Document doc = Jsoup.parse(content);
            Elements matchList = doc.getElementsByClass("match_list");
            if (StringUtil.isEmpty(matchList) || matchList.size() < 2) {
                logger.info("[竞彩足球对阵抓取] 获取match_list标签无数据!");
                return null;
            }
            Elements nodes = matchList.get(1).children();
            if(StringUtil.isEmpty(nodes)) {
                logger.info("[竞彩足球对阵抓取] 无可售比赛!");
                return null;
            }

            Map<String,List<MatchFootBall>> matchMap = new HashMap<>();
            for(int i = 0; i < nodes.size(); i++) {
                if(i % 2 == 0) {
                    continue;
                }
                Elements trs = nodes.get(i).getElementsByTag("tr");
                if(StringUtil.isEmpty(trs)) {
                    continue;
                }

                String period = "";//期次
                boolean isPeriod = true;//标记当前期次是否已经获取到
                List<MatchFootBall> matchs = new ArrayList<>();
                for(int j = 0; j < trs.size(); j++) {
                    Elements tds = trs.get(j).getElementsByTag("td");
                    if(StringUtil.isEmpty(tds)) {
                        continue;
                    }
                    //这里能抓取到的比赛都是正常销售的比赛
                    String status = tds.get(5).text();
                    if(!"已开售".equals(status)) {
                        continue;
                    }
                    MatchFootBall match = new MatchFootBall();
                    String weekStr = tds.get(0).text();
                    if(weekStr.length() == 5){
                        match.setWeekDay(weekStr.substring(0, 2));
                        match.setJcId(weekStr.substring(2,5));
                    }
                    String league = tds.get(1).text();
                    match.setLeagueName(league);
                    String color = tds.get(1).attr("bgcolor");
                    match.setLeagueColor(color);

                    Element team = tds.get(2);
                    String homeTeam = team.getElementsByClass("zhu").text();
                    String guestTeam = team.getElementsByClass("ke").text();
                    match.setHostName(GrabForJcUtil.getShortNameZq(homeTeam));
                    match.setGuestName(GrabForJcUtil.getShortNameZq(guestTeam));

                    String href = team.getElementsByTag("a").attr("href");
                    match.setJcWebId(href.split("=")[1]);

                    String time = tds.get(3).text();
                    Date matchTime = DateUtil.dateDefaultFormat(time+":00");
                    if(matchTime.getTime() < new Date().getTime()) {//比赛时间小于当前时间则不再抓取
                        continue;
                    }
                    match.setMatchTime(matchTime);
                    //根据周几00几和开赛时间计算一下期次名称
                    if(isPeriod){
                        period = JcUtil.getPeriodDay(weekStr.substring(0,2), matchTime);
                        isPeriod = false;
                    }
                    //获取销售截止时间
                    match.setEndTime(JczqUtils.getJczqSellEndTime(period, matchTime));
                    match.setMatchCode(period + match.getJcId());
                    match.setPeriod(period);
                    //这里能抓取到的比赛都是正常销售的比赛
                    String spfStatus = tds.get(6).getElementsByTag("div").attr("class");
                    String rqspfStatus = tds.get(7).getElementsByTag("div").attr("class");
                    String bfStatus = tds.get(8).getElementsByTag("div").attr("class");
                    String zjqStatus = tds.get(9).getElementsByTag("div").attr("class");
                    String bqcStatus = tds.get(10).getElementsByTag("div").attr("class");
                    setMatchStatus(match, spfStatus, rqspfStatus, bfStatus, zjqStatus, bqcStatus);
                    match.setStatus(LotteryConstants.STATUS_SELL);
                    match.setUpdateFlag(false);
                    matchs.add(match);
                }
                if(StringUtil.isNotEmpty(matchs)) {
                    matchMap.put(period, matchs);
                }
            }
            return matchMap;
        } catch (Exception e){
            logger.error("[竞彩足球对阵抓取] 解析对阵数据异常", e);
            return null;
        }
    }

    /**
     * 竞彩玩法状态初始化
     * @param match
     * @param spfStatus
     * @param rqspfStatus
     * @param bfStatus
     * @param zjqStatus
     * @param bqcStatus
     */
    private static void setMatchStatus(MatchFootBall match, String spfStatus, String rqspfStatus, String bfStatus,
         String zjqStatus, String bqcStatus) {
        //胜平负
        if(spfStatus.contains("u-cir")) {
            match.setSingleSpfStatus(LotteryConstants.STATUS_CLOSE);
            match.setSpfStatus(LotteryConstants.STATUS_SELL);
        }else if (spfStatus.contains("u-dan")) {
            match.setSingleSpfStatus(LotteryConstants.STATUS_SELL);
            match.setSpfStatus(LotteryConstants.STATUS_SELL);
        }else if (spfStatus.contains("u-kong")){
            match.setSingleSpfStatus(LotteryConstants.STATUS_CLOSE);
            match.setSpfStatus(LotteryConstants.STATUS_CLOSE);
        }

        //让球胜平负
        if(rqspfStatus.contains("u-cir")) {
            match.setSingleRqspfStatus(LotteryConstants.STATUS_CLOSE);
            match.setRqspfStatus(LotteryConstants.STATUS_SELL);
        }else if (rqspfStatus.contains("u-dan")) {
            match.setSingleRqspfStatus(LotteryConstants.STATUS_SELL);
            match.setRqspfStatus(LotteryConstants.STATUS_SELL);
        }else if (rqspfStatus.contains("u-kong")){
            match.setSingleRqspfStatus(LotteryConstants.STATUS_CLOSE);
            match.setRqspfStatus(LotteryConstants.STATUS_CLOSE);
        }

        //总进球
        if(zjqStatus.contains("u-cir")) {
            match.setSingleZjqStatus(LotteryConstants.STATUS_CLOSE);
            match.setZjqStatus(LotteryConstants.STATUS_SELL);
        }else if (zjqStatus.contains("u-dan")) {
            match.setSingleZjqStatus(LotteryConstants.STATUS_SELL);
            match.setZjqStatus(LotteryConstants.STATUS_SELL);
        }else if (zjqStatus.contains("u-kong")){
            match.setSingleZjqStatus(LotteryConstants.STATUS_CLOSE);
            match.setZjqStatus(LotteryConstants.STATUS_CLOSE);
        }

        //比分
        if(bfStatus.contains("u-cir")) {
            match.setSingleBfStatus(LotteryConstants.STATUS_CLOSE);
            match.setBfStatus(LotteryConstants.STATUS_SELL);
        }else if (bfStatus.contains("u-dan")) {
            match.setSingleBfStatus(LotteryConstants.STATUS_SELL);
            match.setBfStatus(LotteryConstants.STATUS_SELL);
        }else if (bfStatus.contains("u-kong")){
            match.setSingleBfStatus(LotteryConstants.STATUS_CLOSE);
            match.setBfStatus(LotteryConstants.STATUS_CLOSE);
        }

        //半全场
        if(bqcStatus.contains("u-cir")) {
            match.setSingleBqcStatus(LotteryConstants.STATUS_CLOSE);
            match.setBqcStatus(LotteryConstants.STATUS_SELL);
        }else if (bqcStatus.contains("u-dan")) {
            match.setSingleBqcStatus(LotteryConstants.STATUS_SELL);
            match.setBqcStatus(LotteryConstants.STATUS_SELL);
        }else if (bqcStatus.contains("u-kong")){
            match.setSingleBqcStatus(LotteryConstants.STATUS_CLOSE);
            match.setBqcStatus(LotteryConstants.STATUS_CLOSE);
        }
    }

}
