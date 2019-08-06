package com.caipiao.taskcenter.code.kp;

import com.caipiao.common.util.DateUtil;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 抓取吉林快3开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class K3JlAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(K3JlAwardCode.class);

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
            Document doc = cUrl(K3JL_CLL_PERIOD_URL, HOST_CLL);
            String date = doc.getElementsByClass("underthe_box").get(0).getElementsByTag("span").text();
            if(StringUtil.isEmpty(date)) {
                logger.info("[彩乐乐-吉林快三期次("+period.getPeriod()+")开奖号码抓取] 获取underthe_box Pool标签无数据");
                return;
            }

            String periodPrefix = DateUtil.dateFormat(DateUtil.dateFormat(date, DateUtil.DEFAULT_DATE_TIME_SECOND),
                    DateUtil.DEFAULT_DATE1);

            Elements tables = doc.getElementsByClass("stripe");
            if (StringUtil.isEmpty(tables) && tables.size() < 4) {
                logger.info("[彩乐乐-吉林快三期次("+period.getPeriod()+")开奖号码抓取] 获取stripe标签无数据");
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
                    String curPeriod = periodPrefix + "0" + tds.get(0).text();
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
            logger.error("[彩乐乐-吉林快三期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
        }
    }

    /**
     * 抓取彩经网-开奖号码
     * @param period
     */
    @Override
    public void GrabCaiJingWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            Document doc = cUrl(K3JL_CJW_PERIOD_URL, HOST_CJW);
            Elements tables = doc.getElementsByClass("kjjg_table");
            if(StringUtil.isEmpty(tables)) {
                logger.info("[彩经网-吉林快3期次("+period.getPeriod()+")开奖号码抓取] 获取kjjg_table标签无数据");
                return;
            }
            Elements trs = tables.get(0).getElementsByTag("tr");
            for (int i = 1; i < trs.size(); i++) {
                Elements tds = trs.get(i).getElementsByTag("td");
                String curPeriod = tds.get(0).text().replaceAll("期","");
                if(curPeriod.equals(period.getPeriod())) {
                    /*Elements spans = tds.get(2).getElementsByClass("hm_bg");
                    if(StringUtil.isNotEmpty(spans)) {
                        String drawNumber = "";
                        for(Element el : spans) {
                            drawNumber = drawNumber + el.text() + ",";
                        }
                        drawNumber = drawNumber.substring(0, drawNumber.length()-1);
                        if(StringUtil.isNotEmpty(drawNumber)) {
                            period.setDrawNumber(drawNumber);
                            period.setDrawNumberTime(new Date());
                            period.setGrabSuccess(true);
                        }
                    }*/
                    Elements spans = tds.get(2).getElementsByTag("img");
                    if(StringUtil.isNotEmpty(spans)) {
                        String drawNumber = "";
                        for(Element el : spans) {
                            String[] nm = el.attr("src").toString().split("\\/");
                            if(codeMaps.containsKey(nm[nm.length-1])) {
                                drawNumber += codeMaps.get(nm[nm.length-1]) + ",";
                            } else {
                                break;
                            }
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
            logger.error("[彩经网-吉林快3期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
        }
    }

    @Override
    public void Grab360(Period period) {

    }

    @Override
    public void GrabWangYi(Period period) {

    }

    private final static Map<String, String> codeMaps = new HashMap<>();
    static {
        codeMaps.put("20180628032901601404.png", "1");
        codeMaps.put("20180628032901602893.png", "2");
        codeMaps.put("20180628032901603462.png", "3");
        codeMaps.put("20180628032901604949.png", "4");
        codeMaps.put("20180628032901605235.png", "5");
        codeMaps.put("20180628032901606536.png", "6");
    }

    public static void main(String[] args) {
        Period p = new Period();
        p.setPeriod("20180628041");
        new K3JlAwardCode().GrabCaiJingWang(p);
        System.out.println(p.getDrawNumber());
        System.out.println(p.getGrabSuccess());
    }
}
