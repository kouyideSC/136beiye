package com.caipiao.grab.zucai.handler;

import com.alibaba.fastjson.JSON;
import com.caipiao.common.http.Grab;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.grab.vo.SfcMatchVo;
import com.caipiao.grab.vo.SfcOkoMatchInfoVO;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import net.sf.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抓取澳客网胜负彩任九比赛对阵
 * Created by kouyi on 2017/11/10.
 */
@Component("grabForOkoSfcMatch")
public class GrabForOkoSfcMatch extends Grab<Map<Integer, SfcOkoMatchInfoVO>, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForOkoSfcMatch.class);

    @Override
    public Map<Integer, SfcOkoMatchInfoVO> parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[澳客网胜负彩任九比赛对阵数据抓取] 页面数据为空!");
                return null;
            }
            Document doc = Jsoup.parse(content);
            Elements elements = doc.select("table#table4");
            Map<Integer, SfcOkoMatchInfoVO> sf14OkWMatchInfoVOList = null;
            for(Element element : elements) {
                Elements trs = element.getElementsByTag("tr");
                if (null == trs || trs.size() < 14) {
                    continue;
                }
                for(Element tr : trs){
                    Elements tds = tr.getElementsByTag("td");
                    if (null == tds || tds.size() < 4) {
                        continue;
                    }
                    if(null == sf14OkWMatchInfoVOList){
                        sf14OkWMatchInfoVOList = new HashMap<>();
                    }
                    SfcOkoMatchInfoVO okoMatchInfo = new SfcOkoMatchInfoVO();
                    okoMatchInfo.setIndex(StringUtil.isNotEmpty(tds.get(0).text()) ? Integer.parseInt(tds.get(0).text()):0);
                    okoMatchInfo.setLeagueName(tds.get(1).text().trim());
                    okoMatchInfo.setHomeTeamView(tds.get(3).select(".homenameobj.homename").text().trim());
                    okoMatchInfo.setAwayTeamView(tds.get(3).select(".awaynameobj.awayname").text().trim());
                    okoMatchInfo.setMatchTime(DateUtil.dateFormat(new Date(), DateUtil.DEFAULT_DATE0)+"-"+tds.get(2).text()+":00");
                    okoMatchInfo.setSheng(tds.get(3).select(".sbg .pltxt").text().trim());
                    okoMatchInfo.setPing(tds.get(3).select(".pbg .pltxt").text().trim());
                    okoMatchInfo.setFu(tds.get(3).select(".fbg .pltxt").text().trim());
                    okoMatchInfo.setScore(tds.get(6).text().trim().replaceAll(" : ", ":"));
                    sf14OkWMatchInfoVOList.put(okoMatchInfo.getIndex(), okoMatchInfo);
                }
            }
            return sf14OkWMatchInfoVOList;
        } catch (Exception ex) {
            logger.error("[澳客网胜负彩任九比赛对阵数据解析异常] 数据转换出错!");
            return null;
        }
    }

    public static void main(String[] args) {
        new GrabForOkoSfcMatch().parse("ew","");
    }
}
