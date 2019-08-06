package com.caipiao.taskcenter.code.mp;

import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抓取七星彩开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class QxcAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(QxcAwardCode.class);

    /**
     * 抓取体彩网-开奖号码和奖级
     * @param period
     */
    @Override
    public void GrabGuanWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            String content = cUrl(QXC_TCW_PERIOD_URL + period.getPeriod().substring(2,period.getPeriod().length()), HOST_TC).body().text();
            if(StringUtil.isEmpty(content)){
                logger.info("[体彩网-七星彩期次("+period.getPeriod()+")开奖号码抓取] 接口无数据");
                return;
            }

            JSONObject json = JSONArray.fromObject(content).getJSONObject(0);
            String curPeriod = "20" + json.getJSONObject("lottery").getString("term");

            if(!curPeriod.equals(period.getPeriod())) {//取对应期次
                return;
            }
            String number = json.getJSONObject("lottery").getString("number");
            if(StringUtil.isEmpty(number)) {
                return;
            }
            JSONArray array = json.getJSONArray("details");
            if(StringUtil.isEmpty(array)) {
                return;
            }

            JSONObject grade = JSONObject.fromObject(period.getPrizeGrade());
            for(int x=0; x<array.size(); x++) {
                JSONObject jobj = array.getJSONObject(x);
                //六等奖中奖注数为0=可以确定开奖公告没发布
                if(jobj.getString("level").equals("六等奖") && parseInt(jobj.getString("piece").replaceAll(",", "")) == 0) {
                    return;
                }
                if(grade.containsKey(jobj.getString("level"))) {
                    JSONObject job = grade.getJSONObject(jobj.getString("level"));
                    job.put(LotteryGrade.zjzs, getMoney(jobj.getString("piece").replaceAll(",", "")));
                    job.put(LotteryGrade.dzjj, getMoney(jobj.getString("money").replaceAll(",", "")));
                }
            }
            grade.put(LotteryGrade.tzje, json.getJSONObject("lottery").getString("totalSales"));
            grade.put(LotteryGrade.jclj, json.getJSONObject("lottery").getString("pool"));
            period.setDrawNumber(number.replaceAll(" ", ","));
            period.setDrawNumberTime(new Date());
            period.setPrizeGrade(grade.toString());
            period.setGrabSuccess(true);
        } catch (Exception ex) {
            logger.error("[体彩网-七星彩期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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
        p.setPeriod("2018138");
        p.setPrizeGrade("{\"一等奖\":{\"单注奖金\":0,\"加奖奖金\":0,\"中奖注数\":0},\"二等奖\":{\"单注奖金\":0,\"加奖奖金\":0,\"中奖注数\":0},\"三等奖\":{\"单注奖金\":1800,\"加奖奖金\":0,\"中奖注数\":0},\"四等奖\":{\"单注奖金\":300,\"加奖奖金\":0,\"中奖注数\":0},\"五等奖\":{\"单注奖金\":20,\"加奖奖金\":0,\"中奖注数\":0},\"六等奖\":{\"单注奖金\":5,\"加奖奖金\":0,\"中奖注数\":0},\"投注总金额\":0,\"奖池累计金额\":0}");
        new QxcAwardCode().GrabGuanWang(p);
        System.out.println(p.getPrizeGrade());
        System.out.println(p.getGrabSuccess());
    }
}
