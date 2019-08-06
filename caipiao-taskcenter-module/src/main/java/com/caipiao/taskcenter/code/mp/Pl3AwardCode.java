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
 * 抓取排列3开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class Pl3AwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(Pl3AwardCode.class);

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
            String content = cUrl(PL3_TCW_PERIOD_URL + period.getPeriod().substring(2,period.getPeriod().length()), HOST_TC).body().text();
            if(StringUtil.isEmpty(content)){
                logger.info("[体彩网-排列3期次("+period.getPeriod()+")开奖号码抓取] 接口无数据");
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

            int zhushu = 0;
            JSONObject grade = JSONObject.fromObject(period.getPrizeGrade());
            for(int x=0; x<array.size(); x++) {
                JSONObject jobj = array.getJSONObject(x);
                //三种玩法总注数为0=可以确定开奖公告没发布
                zhushu += parseInt(jobj.getString("piece").replaceAll(",", ""));
                if(grade.containsKey(jobj.getString("level"))) {
                    JSONObject job = grade.getJSONObject(jobj.getString("level"));
                    job.put(LotteryGrade.zjzs, getMoney(jobj.getString("piece").replaceAll(",", "")));
                    job.put(LotteryGrade.dzjj, getMoney(jobj.getString("money").replaceAll(",", "")));
                }
            }
            if(zhushu == 0) {//未开奖
                return;
            }
            grade.put(LotteryGrade.tzje, json.getJSONObject("lottery").getString("totalSales"));
            grade.put(LotteryGrade.jclj, json.getJSONObject("lottery").getString("pool"));
            period.setDrawNumber(number.replaceAll(" ", ","));
            period.setDrawNumberTime(new Date());
            period.setPrizeGrade(grade.toString());
            period.setGrabSuccess(true);
        } catch (Exception ex) {
            logger.error("[体彩网-排列3期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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
        p.setPeriod("2018304");
        p.setPrizeGrade("{\"直选\":{\"单注奖金\":1040,\"加奖奖金\":0,\"中奖注数\":9747},\"组三\":{\"单注奖金\":346,\"加奖奖金\":0,\"中奖注数\":4214},\"组六\":{\"单注奖金\":173,\"加奖奖金\":0,\"中奖注数\":0},\"投注总金额\":null,\"奖池累计金额\":null}");
        new Pl3AwardCode().GrabGuanWang(p);
        System.out.println(p.getPrizeGrade());
        System.out.println(p.getGrabSuccess());
    }
}
