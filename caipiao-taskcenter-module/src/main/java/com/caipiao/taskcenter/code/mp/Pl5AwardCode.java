package com.caipiao.taskcenter.code.mp;

import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;

/**
 * 抓取排列5开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class Pl5AwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(Pl5AwardCode.class);

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
                logger.info("[体彩网-排列5期次("+period.getPeriod()+")开奖号码抓取] 接口无数据");
                return;
            }

            JSONObject json = JSONArray.fromObject(content).getJSONObject(0);
            String curPeriod = "20" + json.getJSONObject("lottery").getString("term");

            if(!curPeriod.equals(period.getPeriod())) {//取对应期次
                return;
            }

            String number = getPl5Number(json.getJSONArray("codeNumberPlw"));
            if(StringUtil.isEmpty(number)) {
                return;
            }

            JSONArray array = json.getJSONArray("detailsPlw");
            if(StringUtil.isEmpty(array)) {
                return;
            }
            JSONObject grade = JSONObject.fromObject(period.getPrizeGrade());
            for(int x=0; x<array.size(); x++) {
                JSONObject jobj = array.getJSONObject(x);
                String name = jobj.getString("level").replaceAll("直选","一等奖");
                if(grade.containsKey(name)) {
                    JSONObject job = grade.getJSONObject(name);
                    job.put(LotteryGrade.zjzs, getMoney(jobj.getString("piece").replaceAll(",", "")));
                    job.put(LotteryGrade.dzjj, getMoney(jobj.getString("money").replaceAll(",", "")));
                }
            }

            //String plwDetailId = json.getString("drawNewsPlw");
            //if(StringUtil.isNotEmpty(plwDetailId)) {
            //    pl5TouzhuMoney(MessageFormat.format(PL5_TCW_PERIOD_URL, new Object[] {plwDetailId}), grade);
            //}
            //String je = grade.getString(LotteryGrade.tzje);
            //if(StringUtil.isEmpty(je) || je.equals("0") || je.length() < 3) {
            //    return;
            //}
            period.setDrawNumber(number);
            period.setDrawNumberTime(new Date());
            period.setPrizeGrade(grade.toString());
            period.setGrabSuccess(true);
        } catch (Exception ex) {
            logger.error("[体彩网-排列5期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
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

    /**
     * 格式化排列5开奖号码
     * @param array
     * @return
     */
    public String getPl5Number(JSONArray array){
        if(StringUtil.isEmpty(array)) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for(int k=0; k<array.size(); k++) {
            buffer.append(array.get(k).toString());
            if(k != array.size()-1) {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        Period p = new Period();
        p.setPeriod("2018304");
        p.setPrizeGrade("{\"一等奖\":{\"单注奖金\":100000,\"加奖奖金\":0,\"中奖注数\":0},\"投注总金额\":0,\"奖池累计金额\":0}");
        new Pl5AwardCode().GrabGuanWang(p);
        System.out.println(p.getDrawNumber());
        System.out.println(p.getPrizeGrade());
        System.out.println(p.getGrabSuccess());
    }
}
