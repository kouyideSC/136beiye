package com.caipiao.grab.jc.handler;

import com.caipiao.common.http.Grab;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchBasketBallSp;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 抓取竞彩篮球对阵赔率
 * Created by kouyi on 2017/11/08.
 */
@Component("grabForJclqMatchSp")
public class GrabForJclqMatchSp extends Grab<MatchBasketBallSp, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForJclqMatchSp.class);

    @Override
    public MatchBasketBallSp parse(String content, String matchCode) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[竞彩篮球赔率抓取] 场次号="+matchCode+"页面数据为空!");
                return null;
            }
            Document doc = Jsoup.parse(content);
            Elements tables = doc.getElementsByClass("kj-table");
            if (StringUtil.isEmpty(tables) || tables.size() < 4) {
                logger.info("[竞彩篮球赔率抓取] 场次号="+matchCode+"获取kj-table标签无数据!!");
                return null;
            }

            int number = 0;
            //胜负过关
            MatchBasketBallSp matchSp = new MatchBasketBallSp();
            Elements trsSf = tables.get(0).getElementsByTag("tr");
            if (StringUtil.isNotEmpty(trsSf) && trsSf.size() > 2) {
                Elements tdsSf = trsSf.get(trsSf.size()-1).getElementsByTag("td");
                if(StringUtil.isNotEmpty(tdsSf) && tdsSf.size() > 2) {
                    matchSp.setSheng(parseDouble(tdsSf.get(2).text()));
                    matchSp.setFu(parseDouble(tdsSf.get(1).text()));
                } else {
                    matchSp.setSheng(0d);
                    matchSp.setFu(0d);
                    logger.info("[竞彩篮球赔率抓取] 场次号="+matchCode+"胜负过关玩法未开售!");
                    number++;
                }
            }

            //让分胜负
            Elements trsRfsf = tables.get(1).getElementsByTag("tr");
            if (StringUtil.isNotEmpty(trsRfsf) && trsRfsf.size() > 1) {
                Elements tdsRfsf = trsRfsf.get(trsRfsf.size()-1).getElementsByTag("td");
                if(StringUtil.isNotEmpty(tdsRfsf) && tdsRfsf.size() > 4) {
                    matchSp.setRfu(parseDouble(tdsRfsf.get(1).text()));
                    matchSp.setLose(parseDouble(tdsRfsf.get(2).text()));
                    matchSp.setrSheng(parseDouble(tdsRfsf.get(3).text()));
                } else {
                    matchSp.setRfu(0d);
                    matchSp.setLose(0d);
                    matchSp.setrSheng(0d);
                    logger.info("[竞彩篮球赔率抓取] 场次号="+matchCode+"让分胜负玩法未开售!");
                    number++;
                }
            }

            //大小分
            Elements trsDxf = tables.get(2).getElementsByTag("tr");
            if (StringUtil.isNotEmpty(trsDxf) && trsDxf.size() > 1) {
                Elements tdsDxf = trsDxf.get(trsDxf.size()-1).getElementsByTag("td");
                if(StringUtil.isNotEmpty(tdsDxf) && tdsDxf.size() > 4) {
                    matchSp.setDf(parseDouble(tdsDxf.get(1).text()));
                    matchSp.setDxf(parseDouble(tdsDxf.get(2).text()));
                    matchSp.setXf(parseDouble(tdsDxf.get(3).text()));
                } else {
                    matchSp.setDf(0d);
                    matchSp.setDxf(0d);
                    matchSp.setXf(0d);
                    logger.info("[竞彩篮球赔率抓取] 场次号="+matchCode+"大小分玩法未开售!");
                    number++;
                }
            }

            //胜分差
            Elements trsSfc = tables.get(3).getElementsByTag("tr");
            if (StringUtil.isNotEmpty(trsSfc) && trsSfc.size() > 3) {
                Elements tdsSfc = trsSfc.get(trsSfc.size()-1).getElementsByTag("td");
                if(StringUtil.isNotEmpty(tdsSfc) && tdsSfc.size() > 12) {
                    matchSp.setKs15(parseDouble(tdsSfc.get(1).text()));
                    matchSp.setKs610(parseDouble(tdsSfc.get(2).text()));
                    matchSp.setKs1115(parseDouble(tdsSfc.get(3).text()));
                    matchSp.setKs1620(parseDouble(tdsSfc.get(4).text()));
                    matchSp.setKs2125(parseDouble(tdsSfc.get(5).text()));
                    matchSp.setKs26(parseDouble(tdsSfc.get(6).text()));
                    matchSp.setZs15(parseDouble(tdsSfc.get(7).text()));
                    matchSp.setZs610(parseDouble(tdsSfc.get(8).text()));
                    matchSp.setZs1115(parseDouble(tdsSfc.get(9).text()));
                    matchSp.setZs1620(parseDouble(tdsSfc.get(10).text()));
                    matchSp.setZs2125(parseDouble(tdsSfc.get(11).text()));
                    matchSp.setZs26(parseDouble(tdsSfc.get(12).text()));
                } else {
                    matchSp.setKs15(0d);
                    matchSp.setKs610(0d);
                    matchSp.setKs1115(0d);
                    matchSp.setKs1620(0d);
                    matchSp.setKs2125(0d);
                    matchSp.setKs26(0d);
                    matchSp.setZs15(0d);
                    matchSp.setZs610(0d);
                    matchSp.setZs1115(0d);
                    matchSp.setZs1620(0d);
                    matchSp.setZs2125(0d);
                    matchSp.setZs26(0d);
                    logger.info("[竞彩篮球赔率抓取] 场次号="+matchCode+"胜分差玩法未开售!");
                    number++;
                }
            }
            if(number == 4) {
                matchSp = null;
            }
            return matchSp;
        } catch (Exception e){
            logger.error("[竞彩篮球赔率抓取] 解析对阵(matchCode=" + matchCode + ")数据异常[content=" + content + "]", e);
            return null;
        }
    }

}
