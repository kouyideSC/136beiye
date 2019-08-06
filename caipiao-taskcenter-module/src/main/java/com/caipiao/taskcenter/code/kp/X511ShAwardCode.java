package com.caipiao.taskcenter.code.kp;

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

/**
 * 抓取上海11选5开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class X511ShAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(X511ShAwardCode.class);

    /**
     * 抓取上海体彩-开奖号码
     * @param period
     */
    @Override
    public void GrabGuanWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            Document doc = cUrl(X115SH_SHTC_PERIOD_URL, HOST_SH);
            Elements trs = doc.getElementsByTag("tr");
            if(StringUtil.isEmpty(trs) || trs.size() < 2) {
                logger.info("[上海体彩-上海11选5期次("+period.getPeriod()+")开奖号码抓取] 获取tr标签无数据");
                return;
            }
            for (int i = 1; i < trs.size(); i++) {
                Elements tds = trs.get(i).getElementsByTag("td");
                if(StringUtil.isEmpty(tds)) {
                    continue;
                }
                String curPeriod = tds.get(0).text();
                if(curPeriod.equals(period.getPeriod())) {
                    String drawNumber = tds.get(1).text();
                    if(StringUtil.isNotEmpty(drawNumber)) {
                        period.setDrawNumber(drawNumber);
                        period.setDrawNumberTime(new Date());
                        period.setGrabSuccess(true);
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("[上海体彩-上海11选5期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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
        p.setPeriod("2017111744");
        new X511ShAwardCode().GrabGuanWang(p);
        System.out.println(p.getDrawNumber());
        System.out.println(p.getGrabSuccess());
    }
}
