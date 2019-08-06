package com.caipiao.taskcenter.code.kp;

import com.caipiao.common.http.Grab;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抓取广东11选5开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class X511GdAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(X511GdAwardCode.class);

    /**
     * 抓取广东体彩网-开奖号码
     * @param period
     */
    @Override
    public void GrabGuanWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            Document doc = cUrl(X115GD_GDTC_PERIOD_URL, HOST_GD);
            Elements tables = doc.getElementsByAttribute("bordercolorlight").attr("bgcolor","#006599");
            if (StringUtil.isEmpty(tables)) {
                logger.info("[广东体彩网-广东11选5期次("+period.getPeriod()+")开奖号码抓取] 获取bordercolorlight标签无数据");
                return;
            }

            //循环解析期次-取参数period中对应的期次
            Elements trs = tables.get(0).getElementsByTag("tr");
            if(StringUtil.isEmpty(trs) || trs.size() < 3) {
                logger.info("[广东体彩网-广东11选5期次("+period.getPeriod()+")开奖号码抓取] 无开奖数据");
                return;
            }
            for(int index=trs.size()-3; index>0; index--) {
                Elements tds = trs.get(index).getElementsByTag("td");
                if(StringUtil.isEmpty(tds)) {
                    continue;
                }
                String curPeriod = "20" + tds.get(0).text();
                if(curPeriod.equals(period.getPeriod())) {//取对应期次
                    String drawNumber = tds.get(1).text().replaceAll("，", ",");
                    if(StringUtil.isNotEmpty(drawNumber)) {
                        period.setDrawNumber(drawNumber);
                        period.setDrawNumberTime(new Date());
                        period.setGrabSuccess(true);
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("[广东体彩网-广东11选5期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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
        p.setPeriod("20171118059");
        new X511GdAwardCode().GrabGuanWang(p);
        System.out.println(p.getDrawNumber());
        System.out.println(p.getGrabSuccess());
    }
}
