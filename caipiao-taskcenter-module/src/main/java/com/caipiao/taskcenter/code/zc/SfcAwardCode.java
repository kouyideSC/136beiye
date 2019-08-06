package com.caipiao.taskcenter.code.zc;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;

/**
 * 抓取胜负彩开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class SfcAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(SfcAwardCode.class);
    private static String name = "任九";

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
            String content = cUrl(SFC_TCW_PERIOD_URL + period.getPeriod().substring(2,period.getPeriod().length()), HOST_TC).body().text();
            if(StringUtil.isEmpty(content)){
                logger.info("[体彩网-胜负彩期次("+period.getPeriod()+")开奖号码抓取] 接口无数据");
                return;
            }

            JSONObject json = JSONArray.fromObject(content).getJSONObject(0);
            String curPeriod = "20" + json.getJSONObject("lottery").getString("term");

            if(!curPeriod.equals(period.getPeriod())) {//取对应期次
                return;
            }
            String number = json.getJSONObject("lottery").getString("number").trim().replaceAll("-","");
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
                if(period.getLotteryId().equals(LotteryConstants.SFC)) {
                    if(grade.containsKey(jobj.getString("level"))) {
                        JSONObject job = grade.getJSONObject(jobj.getString("level"));
                        job.put(LotteryGrade.zjzs, getMoney(jobj.getString("piece").replaceAll(",", "")));
                        job.put(LotteryGrade.dzjj, getMoney(jobj.getString("money").replaceAll(",", "")));
                    }
                } else if(period.getLotteryId().equals(LotteryConstants.RXJ)){
                    if(jobj.getString("level").equals(name)) {
                        JSONObject job = grade.getJSONObject(jobj.getString("level").replaceAll(name,"一等奖"));
                        job.put(LotteryGrade.zjzs, getMoney(jobj.getString("piece").replaceAll(",", "")));
                        job.put(LotteryGrade.dzjj, getMoney(jobj.getString("money").replaceAll(",", "")));
                    }
                }
            }
            if(period.getLotteryId().equals(LotteryConstants.SFC)) {
                String je = json.getJSONObject("lottery").getString("totalSales");
                if(StringUtil.isEmpty(je) || je.equals("0") || je.length() < 4) {
                    return;
                }
                grade.put(LotteryGrade.tzje, CalculationUtils.getMoneyFormat(je));
                grade.put(LotteryGrade.jclj, CalculationUtils.getMoneyFormat(json.getJSONObject("lottery").getString("pool")));
            } else {
                String rjDetailId = json.getJSONObject("lottery").getString("drawNews");
                if(StringUtil.isNotEmpty(rjDetailId)) {
                    rx9TouzhuMoney(MessageFormat.format(RXJ_TCW_PERIOD_URL, new Object[] {rjDetailId}), grade);
                }
                /*String je = grade.getString(LotteryGrade.tzje);
                if(StringUtil.isEmpty(je) || je.equals("0") || je.length() < 4) {
                    return;
                }*/
            }
            period.setDrawNumber(number.replaceAll(" ",","));
            period.setDrawNumberTime(new Date());
            period.setPrizeGrade(grade.toString());
            period.setGrabSuccess(true);
        } catch (Exception ex) {
            logger.error("[体彩网-胜负彩期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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
        p.setPeriod("2018053");
        p.setLotteryId(LotteryConstants.SFC);
        p.setPrizeGrade("{\"一等奖\":{\"单注奖金\":\"10345293\",\"加奖奖金\":\"0\",\"中奖注数\":\"3\"},\"二等奖\":{\"单注奖金\":\"4433662\",\"加奖奖金\":\"0\",\"中奖注数\":\"151\"},\"投注总金额\":\"23,092,176\",\"奖池累计金额\":\"0\"}");
        new SfcAwardCode().GrabGuanWang(p);
        System.out.println(p.getPrizeGrade());
        System.out.println(p.getGrabSuccess());
        System.out.println(p.getDrawNumber());
    }
}
