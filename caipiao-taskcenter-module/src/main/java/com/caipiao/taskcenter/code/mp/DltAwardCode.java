package com.caipiao.taskcenter.code.mp;

import com.alibaba.fastjson.JSON;
import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 抓取大乐透开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class DltAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(DltAwardCode.class);

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
            String content = cUrl(DLT_TCW_PERIOD_URL + period.getPeriod().substring(2,period.getPeriod().length()), HOST_TC).body().text();
            if(StringUtil.isEmpty(content)){
                logger.info("[体彩网-大乐透期次("+period.getPeriod()+")开奖号码抓取] 接口无数据");
                return;
            }

            JSONObject json = JSONArray.fromObject(content).getJSONObject(0);
            String curPeriod = "20" + json.getJSONObject("lottery").getString("term");

            if(!curPeriod.equals(period.getPeriod())) {//取对应期次
                return;
            }
            String number = json.getJSONObject("lottery").getString("number").replaceAll(" ", ",").replaceAll("-", "|");
            if(StringUtil.isEmpty(number)) {
                return;
            }
            JSONArray array = json.getJSONArray("details");
            if(StringUtil.isEmpty(array)) {
                return;
            }

            JSONObject grade = JSONObject.fromObject(period.getPrizeGrade());
            for(int x=0; x<array.size(); x++) {
                if(x > 10) {//过滤追加
                    continue;
                }
                JSONObject jobj = array.getJSONObject(x);
                //六等奖中奖注数为0=可以确定开奖公告没发布
                if(jobj.getString("level").equals("九等奖") && parseInt(jobj.getString("piece").replaceAll(",", "")) == 0) {
                    return;
                }
                if(grade.containsKey(jobj.getString("level"))) {
                    JSONObject job = grade.getJSONObject(jobj.getString("level"));
                    job.put(LotteryGrade.zjzs, getMoney(jobj.getString("piece").replaceAll(",", "")));
                    job.put(LotteryGrade.dzjj, getMoney(jobj.getString("money").replaceAll(",", "")));
                }
                //追加奖级
                String lv = jobj.getString("level");
                if(lv.indexOf("追加") > 0) {
                    lv = lv.replace("追加", "");
                    if(!lv.endsWith("奖")) {
                        lv = lv + "奖";
                    }
                    JSONObject job2 = grade.getJSONObject(lv);
                    job2.put(LotteryGrade.zhjzs, getMoney(jobj.getString("piece").replaceAll(",", "")));
                    job2.put(LotteryGrade.zhjjj, getMoney(jobj.getString("money").replaceAll(",", "")));
                }
            }
            grade.put(LotteryGrade.tzje, json.getJSONObject("lottery").getString("totalSales"));
            grade.put(LotteryGrade.jclj, json.getJSONObject("lottery").getString("pool"));
            period.setDrawNumber(number);
            period.setDrawNumberTime(new Date());
            period.setPrizeGrade(grade.toString());
            period.setGrabSuccess(true);
        } catch (Exception ex) {
            logger.error("[体彩网-大乐透期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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
        p.setPeriod("2018137");
        p.setPrizeGrade("{\"一等奖\":{\"单注奖金\":\"0\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"0\",\"中奖注数\":\"0\",\"追加奖金\":\"0\",\"追加注数\":\"0\"},\"二等奖\":{\"单注奖金\":\"0\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"0\",\"中奖注数\":\"0\",\"追加奖金\":\"0\",\"追加注数\":\"0\"},\"三等奖\":{\"单注奖金\":\"0\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"0\",\"中奖注数\":\"0\",\"追加奖金\":\"0\",\"追加注数\":\"0\"},\"四等奖\":{\"单注奖金\":\"200\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"0\",\"中奖注数\":\"0\",\"追加奖金\":\"0\",\"追加注数\":\"0\"},\"五等奖\":{\"单注奖金\":\"10\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"0\",\"中奖注数\":\"0\",\"追加奖金\":\"0\",\"追加注数\":\"0\"},\"六等奖\":{\"单注奖金\":\"5\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"0\",\"中奖注数\":\"0\"},\"投注总金额\":\"0\",\"奖池累计金额\":\"0\"}");
        new DltAwardCode().GrabGuanWang(p);
        System.out.println(p.getPrizeGrade());
        System.out.println(p.getGrabSuccess());
        System.out.println(p.getDrawNumber());
    }
}
