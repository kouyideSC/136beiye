package com.caipiao.taskcenter.code.kp;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抓取江苏快3开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class K3JsAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(K3JsAwardCode.class);

    /**
     * 抓取彩经网-开奖号码
     * @param period
     */
    @Override
    public void GrabGuanWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            Document doc = cUrl(K3JS_CJW_PERIOD_URL, HOST_CJW);
            Elements tables = doc.getElementsByClass("kjjg_table");
            if(StringUtil.isEmpty(tables)) {
                logger.info("[彩经网-江苏快3期次("+period.getPeriod()+")开奖号码抓取] 获取kjjg_table标签无数据");
                return;
            }
            Elements trs = tables.get(0).getElementsByTag("tr");
            for (int i = 1; i < trs.size(); i++) {
                Elements tds = trs.get(i).getElementsByTag("td");
                String curPeriod = tds.get(0).text().replaceAll("期","");
                if(curPeriod.equals(period.getPeriod())) {
                    Elements spans = tds.get(2).getElementsByTag("span");
                    if(StringUtil.isNotEmpty(spans)) {
                        String drawNumber = "";
                        for(Element el : spans) {
                            drawNumber += el.attr("class").toString().replaceAll("kjjg_hm_", "") + ",";
                        }
                        drawNumber = drawNumber.substring(0, drawNumber.length()-1);
                        if(StringUtil.isNotEmpty(drawNumber)) {
                            period.setDrawNumber(drawNumber);
                            period.setDrawNumberTime(new Date());
                            period.setGrabSuccess(true);
                        }
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("[彩经网-江苏快3期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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
        p.setPeriod("20171118036");
        new K3JsAwardCode().GrabGuanWang(p);
        System.out.println(p.getDrawNumber());
        System.out.println(p.getGrabSuccess());
    }
}
