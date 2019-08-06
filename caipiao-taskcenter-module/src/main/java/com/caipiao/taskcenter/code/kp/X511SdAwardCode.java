package com.caipiao.taskcenter.code.kp;

import com.caipiao.common.http.Grab;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 抓取十一运夺金开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class X511SdAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(X511SdAwardCode.class);

    /**
     * 抓取彩乐乐-开奖号码
     * @param period
     */
    @Override
    public void GrabGuanWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            Document doc = cUrl(X115SD_CLL_PERIOD_URL, HOST_CLL);
            String date = doc.getElementsByClass("underthe_box").get(0).getElementsByTag("span").text();
            if(StringUtil.isEmpty(date)) {
                logger.info("[彩乐乐-十一运夺金期次("+period.getPeriod()+")开奖号码抓取] 获取underthe_box Pool标签无数据");
                return;
            }

            String periodPrefix = DateUtil.dateFormat(DateUtil.dateFormat(date, DateUtil.DEFAULT_DATE_TIME_SECOND),
                    DateUtil.DEFAULT_DATE1);

            Elements tables = doc.getElementsByClass("stripe");
            if (StringUtil.isEmpty(tables) && tables.size() < 4) {
                logger.info("[彩乐乐-十一运夺金期次("+period.getPeriod()+")开奖号码抓取] 获取stripe标签无数据");
                return;
            }

            //循环解析期次-取参数period中对应的期次
            for(Element ele : tables) {
                Elements trs = ele.getElementsByTag("tr");
                if(StringUtil.isEmpty(trs)) {
                    continue;
                }
                for(int index=1; index<trs.size(); index++) {
                    Elements tds = trs.get(index).getElementsByTag("td");
                    if(StringUtil.isEmpty(tds)) {
                        continue;
                    }
                    String curPeriod = periodPrefix + tds.get(0).text();
                    if(curPeriod.equals(period.getPeriod())) {//取对应期次
                        String drawNumber = tds.get(1).text();
                        if(StringUtil.isNotEmpty(drawNumber)) {
                            period.setDrawNumber(drawNumber);
                            period.setDrawNumberTime(new Date());
                            period.setGrabSuccess(true);
                        }
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("[彩乐乐-十一运夺金期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
        }
    }

    @Override
    public void GrabCaiJingWang(Period period) {

    }

    @Override
    public void Grab360(Period period) {

    }

    @Override
    public void GrabWangYi(Period period) {

    }

    public static void main(String[] args) {
        Period p = new Period();
        p.setPeriod("2018062116");
        new X511SdAwardCode().GrabCaiJingWang(p);
        System.out.println(p.getDrawNumber());
        System.out.println(p.getGrabSuccess());
    }
}
