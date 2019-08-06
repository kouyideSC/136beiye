package com.caipiao.taskcenter.code.kp;

import com.caipiao.common.http.Grab;
import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抓取重庆时时彩开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class SscCqAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(SscCqAwardCode.class);

    /**
     * 抓取重庆福彩官网-开奖号码
     * @param period
     */
    @Override
    public void GrabGuanWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            Document doc = cUrl(SSC_CQGW_PERIOD_URL, HOST_CQ);
            Elements uls = doc.getElementById("openlist").getElementsByTag("ul");
            if (StringUtil.isEmpty(uls) || uls.size() < 2) {
                logger.info("[重庆福彩官网-重庆时时彩期次("+period.getPeriod()+")开奖号码抓取] 获取openlist.ul标签无数据");
                return;
            }

            //循环解析期次-取参数period中对应的期次
            for(int index=1; index < uls.size(); index++) {
                Elements lis = uls.get(index).getElementsByTag("li");
                if(StringUtil.isEmpty(lis) || lis.size() < 9) {
                    continue;
                }
                String curPeriod = "20" + lis.get(0).text();
                if(curPeriod.equals(period.getPeriod())) {//取对应期次
                    String drawNumber = lis.get(1).text().replaceAll("-", ",");
                    if(StringUtil.isNotEmpty(drawNumber)) {
                        period.setDrawNumber(drawNumber);
                        period.setDrawNumberTime(new Date());
                        period.setGrabSuccess(true);
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("[重庆福彩官网-重庆时时彩期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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
        p.setPeriod("20171117035");
        new SscCqAwardCode().GrabGuanWang(p);
        System.out.println(p.getDrawNumber());
        System.out.println(p.getGrabSuccess());
    }
}
