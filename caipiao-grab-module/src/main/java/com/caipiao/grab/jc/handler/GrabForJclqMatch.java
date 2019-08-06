package com.caipiao.grab.jc.handler;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.lottery.JclqUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchBasketBall;
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
 * 抓取竞彩篮球对阵
 * Created by kouyi on 2017/11/07.
 */
@Component("grabForJclqMatch")
public class GrabForJclqMatch extends Grab<Map<String,List<MatchBasketBall>>, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForJclqMatch.class);

    @Override
    public Map<String,List<MatchBasketBall>> parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[竞彩篮球对阵抓取] 页面数据为空!");
                return null;
            }
            Document doc = Jsoup.parse(content);
            Elements matchList = doc.getElementsByClass("match_list");
            if (StringUtil.isEmpty(matchList) || matchList.size() < 2) {
                logger.info("[竞彩篮球对阵抓取] 获取match_list标签无数据!");
                return null;
            }
            Elements nodes = matchList.get(1).children();
            if(StringUtil.isEmpty(nodes)) {
                logger.info("[竞彩篮球对阵抓取] 无可售比赛!");
                return null;
            }

            Map<String,List<MatchBasketBall>> matchMap = new HashMap<>();
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
                List<MatchBasketBall> matchs = new ArrayList<>();
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
                    MatchBasketBall match = new MatchBasketBall();
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
                    String homeTeam = team.getElementsByClass("ke").text();
                    String guestTeam = team.getElementsByClass("zhu").text();
                    match.setHostName(GrabForJcUtil.getShortName(homeTeam));
                    match.setGuestName(GrabForJcUtil.getShortName(guestTeam));

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
                    match.setEndTime(JclqUtils.getJclqSellEndTime(period, matchTime));
                    match.setMatchCode(period + match.getJcId());
                    match.setPeriod(period);
                    //这里能抓取到的比赛都是正常销售的比赛
                    String sfStatus = tds.get(6).getElementsByTag("div").attr("class");
                    String rfsfStatus = tds.get(7).getElementsByTag("div").attr("class");
                    String dxStatus = tds.get(8).getElementsByTag("div").attr("class");
                    String sfcStatus = tds.get(9).getElementsByTag("div").attr("class");
                    setMatchStatus(match, sfStatus, rfsfStatus, dxStatus, sfcStatus);
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
            logger.error("[竞彩篮球对阵抓取] 解析对阵数据异常", e);
            return null;
        }
    }

    /**
     * 竞彩玩法状态初始化
     * @param match
     * @param sfStatus
     * @param rfsfStatus
     * @param dxfStatus
     * @param sfcStatus
     */
    private static void setMatchStatus(MatchBasketBall match, String sfStatus, String rfsfStatus, String dxfStatus,
         String sfcStatus) {
        //胜负
        if(sfStatus.contains("u-cir")) {
            match.setSingleSfStatus(LotteryConstants.STATUS_CLOSE);
            match.setSfStatus(LotteryConstants.STATUS_SELL);
        }else if (sfStatus.contains("u-dan")) {
            match.setSingleSfStatus(LotteryConstants.STATUS_SELL);
            match.setSfStatus(LotteryConstants.STATUS_SELL);
        }else if (sfStatus.contains("u-kong")){
            match.setSingleSfStatus(LotteryConstants.STATUS_CLOSE);
            match.setSfStatus(LotteryConstants.STATUS_CLOSE);
        }

        //让分胜负
        if(rfsfStatus.contains("u-cir")) {
            match.setSingleRfsfStatus(LotteryConstants.STATUS_CLOSE);
            match.setRfsfStatus(LotteryConstants.STATUS_SELL);
        }else if (rfsfStatus.contains("u-dan")) {
            match.setSingleRfsfStatus(LotteryConstants.STATUS_SELL);
            match.setRfsfStatus(LotteryConstants.STATUS_SELL);
        }else if (rfsfStatus.contains("u-kong")){
            match.setSingleRfsfStatus(LotteryConstants.STATUS_CLOSE);
            match.setRfsfStatus(LotteryConstants.STATUS_CLOSE);
        }

        //大小分
        if(dxfStatus.contains("u-cir")) {
            match.setSingleDxfStatus(LotteryConstants.STATUS_CLOSE);
            match.setDxfStatus(LotteryConstants.STATUS_SELL);
        }else if (dxfStatus.contains("u-dan")) {
            match.setSingleDxfStatus(LotteryConstants.STATUS_SELL);
            match.setDxfStatus(LotteryConstants.STATUS_SELL);
        }else if (dxfStatus.contains("u-kong")){
            match.setSingleDxfStatus(LotteryConstants.STATUS_CLOSE);
            match.setDxfStatus(LotteryConstants.STATUS_CLOSE);
        }

        //胜分差
        if(sfcStatus.contains("u-cir")) {
            match.setSingleSfcStatus(LotteryConstants.STATUS_CLOSE);
            match.setSfcStatus(LotteryConstants.STATUS_SELL);
        }else if (sfcStatus.contains("u-dan") ) {
            match.setSingleSfcStatus(LotteryConstants.STATUS_SELL);
            match.setSfcStatus(LotteryConstants.STATUS_SELL);
        }else if ( sfcStatus.contains("u-kong") ){
            match.setSingleSfcStatus(LotteryConstants.STATUS_CLOSE);
            match.setSfcStatus(LotteryConstants.STATUS_CLOSE);
        }
    }

}
