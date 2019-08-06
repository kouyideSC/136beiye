package com.caipiao.taskcenter.code.mp;

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
 * 抓取福彩3D开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class Fc3dAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(Fc3dAwardCode.class);

    /**
     * 抓取中彩网-开奖号码和奖级
     * @param period
     */
    @Override
    public void GrabGuanWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            Document doc = cUrl(FC3D_ZCW_PERIOD_URL, HOST_ZC);
            Elements tables = doc.getElementsByClass("wqhgt");
            if (StringUtil.isEmpty(tables)) {
                logger.info("[中彩网-福彩3D期次("+period.getPeriod()+")开奖号码抓取] 获取wqhgt标签无数据");
                return;
            }

            Elements trs = tables.get(0).getElementsByTag("tr");
            if (StringUtil.isEmpty(trs) || trs.size() < 3) {
                logger.info("[中彩网-福彩3D期次("+period.getPeriod()+")开奖号码抓取] 历史期次数据为空");
                return;
            }

            //循环解析期次-取参数period中对应的期次
            for(int index=2; index < trs.size(); index++) {
                Elements tds = trs.get(index).getElementsByTag("td");
                if(StringUtil.isEmpty(tds) || tds.size() < 9) {
                    continue;
                }
                String curPeriod = tds.get(1).text();
                if(curPeriod.equals(period.getPeriod())) {//取对应期次
                    if(parseInt(tds.get(3).text()) + parseInt(tds.get(4).text()) + parseInt(tds.get(5).text()) == 0) {
                        break;
                    }
                    JSONObject grade = JSONObject.fromObject(period.getPrizeGrade());
                    JSONObject zx = grade.getJSONObject("直选");
                    zx.put(LotteryGrade.zjzs, getMoney(tds.get(3).text()));
                    JSONObject zs = grade.getJSONObject("组三");
                    zs.put(LotteryGrade.zjzs, getMoney(tds.get(4).text()));
                    JSONObject zl = grade.getJSONObject("组六");
                    zl.put(LotteryGrade.zjzs, getMoney(tds.get(5).text()));

                    grade.put(LotteryGrade.tzje, tds.get(6).text());
                    period.setDrawNumber(tds.get(2).text().replaceAll(" ", ","));
                    period.setDrawNumberTime(new Date());
                    period.setPrizeGrade(grade.toString());
                    period.setGrabSuccess(true);
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("[中彩网-福彩3D期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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
        p.setPeriod("2017313");
        p.setPrizeGrade("{\"直选\":{\"单注奖金\":1040,\"加奖奖金\":0,\"中奖注数\":0},\"组三\":{\"单注奖金\":346,\"加奖奖金\":0,\"中奖注数\":0},\"组六\":{\"单注奖金\":173,\"加奖奖金\":0,\"中奖注数\":0},\"投注总金额\":0,\"奖池累计金额\":0}");
        new Fc3dAwardCode().GrabGuanWang(p);
        System.out.println(p.getPrizeGrade());
        System.out.println(p.getGrabSuccess());
    }
}
