package com.caipiao.grab.jc.handler;

import com.caipiao.common.http.Grab;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchBasketBallResult;
import com.caipiao.grab.util.JcUtil;
import org.apache.commons.collections.list.TreeList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抓取竞彩篮球赛果
 * Created by kouyi on 2017/11/08.
 */
@Component("grabForJclqMatchResult")
public class GrabForJclqMatchResult extends Grab<Map<String, MatchBasketBallResult>, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForJclqMatchResult.class);

    public static void main(String[] args) {
        new GrabForJclqMatchResult().parse("rrrr","");
    }

    @Override
    public Map<String, MatchBasketBallResult> parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                return null;
            }
            Document doc = Jsoup.parse(content);
            Elements mtabList = doc.getElementsByClass("m-tab");
            if (StringUtil.isEmpty(mtabList)) {
                logger.info("[竞彩篮球赛果抓取] 获取m-tab标签无数据!");
                return null;
            }

            Map<String, MatchBasketBallResult> matchResult = new HashMap<>();
            Elements trs = mtabList.get(0).getElementsByTag("tr");
            if(StringUtil.isEmpty(trs)) {
                logger.info("[竞彩篮球赛果抓取] 无赛果数据!");
                return null;
            }
            for(int j = 0; j < trs.size(); j++) {
                Elements tds = trs.get(j).getElementsByTag("td");
                if(StringUtil.isEmpty(tds) || tds.size() < 14) {
                    continue;
                }
                String status = tds.get(11).text();
                if(!"已完成".equals(status)) {
                    continue;
                }

                MatchBasketBallResult result = new MatchBasketBallResult();
                String halfScore = "0:0";
                try {
                    String[] halfScoreStr = tds.get(4).getElementsByTag("span").text().split(" ");
                    halfScore = (parseInt(halfScoreStr[0].split("\\:")[0]) + parseInt(halfScoreStr[1].split("\\:")[0]))
                            + ":"
                            + (parseInt(halfScoreStr[0].split("\\:")[1]) + parseInt(halfScoreStr[1].split("\\:")[1]));
                } catch (Exception e) {
                    logger.info("[竞彩篮球赛果抓取] 无法解析半场比分");
                }
                String score = tds.get(7).getElementsByTag("span").text();
                if(StringUtil.isEmpty(score) || score.split("\\:").length != 2) {
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
                        pages.add("http://info.sporttery.cn/basketball/" + ele.attr("href").toString());
                    }
                }
                if(pages.size() > 0) {
                    matchResult.put("pageSum", new MatchBasketBallResult(pages));
                }
            }
            return matchResult;
        } catch (Exception e){
            logger.error("[竞彩篮球赛果抓取] 解析赛果数据异常", e);
            return null;
        }
    }

}
