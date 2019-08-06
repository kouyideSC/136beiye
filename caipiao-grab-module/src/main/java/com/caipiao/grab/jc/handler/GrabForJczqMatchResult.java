package com.caipiao.grab.jc.handler;

import com.caipiao.common.http.Grab;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchFootBallResult;
import com.caipiao.grab.util.JcUtil;
import org.apache.commons.collections.list.TreeList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 抓取竞彩足球赛果
 * Created by kouyi on 2017/11/08.
 */
@Component("grabForJczqMatchResult")
public class GrabForJczqMatchResult extends Grab<Map<String, MatchFootBallResult>, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForJczqMatchResult.class);

    @Override
    public Map<String, MatchFootBallResult> parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                return null;
            }
            Document doc = Jsoup.parse(content);
            Elements mtabList = doc.getElementsByClass("m-tab");
            if (StringUtil.isEmpty(mtabList)) {
                logger.info("[竞彩足球赛果抓取] 获取m-tab标签无数据!");
                return null;
            }

            Map<String, MatchFootBallResult> matchResult = new HashMap<>();
            Elements trs = mtabList.get(0).getElementsByTag("tr");
            if(StringUtil.isEmpty(trs)) {
                logger.info("[竞彩足球赛果抓取] 无赛果数据!");
                return null;
            }
            for(int j = 0; j < trs.size(); j++) {
                Elements tds = trs.get(j).getElementsByTag("td");
                if(StringUtil.isEmpty(tds) || tds.size() < 12) {
                    continue;
                }
                String status = tds.get(9).text();
                if(!"已完成".equals(status)) {
                    continue;
                }

                MatchFootBallResult result = new MatchFootBallResult();
                String halfScore = tds.get(4).getElementsByTag("span").text();
                String score = tds.get(5).getElementsByTag("span").text();
                if(StringUtil.isEmpty(halfScore) || StringUtil.isEmpty(score) || halfScore.split("\\:").length != 2
                        || score.split("\\:").length != 2) {
                    continue;
                }
                result.setHalfScore(halfScore);
                result.setScore(score);

                String weekStr = tds.get(1).text();
                String period = tds.get(0).text();
                period = JcUtil.getPeriodDay(weekStr.substring(0,2), DateUtil.dateFormat(period, DateUtil.DEFAULT_DATE));
                result.setMatchCode(period + weekStr.substring(2,5));
                matchResult.put(result.getMatchCode(), result);
            }

            //解析页数
            Elements mpageList = doc.getElementsByClass("m-page").get(0).getElementsByTag("a");
            if(StringUtil.isNotEmpty(mpageList)) {
                List<String> pages = new TreeList();
                for(Element ele : mpageList) {
                    if(NumberUtil.isNumber(ele.text()) && Integer.parseInt(ele.text()) > 1) {
                        pages.add("http://info.sporttery.cn/football/" + ele.attr("href").toString());
                    }
                }
                if(pages.size() > 0) {
                    matchResult.put("pageSum", new MatchFootBallResult(pages));
                }
            }
            return matchResult;
        } catch (Exception e){
            logger.error("[竞彩足球赛果抓取] 解析赛果数据异常", e);
            return null;
        }
    }

}
