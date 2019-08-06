package com.caipiao.taskcenter.code.mp;

import com.caipiao.common.http.Grab;
import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.taskcenter.code.util.CodeUrlUtil;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抓取双色球开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class SsqAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(SsqAwardCode.class);

    /**
     * 抓取福彩官网-开奖号码和奖级
     * @param period
     */
    public void GrabGuanWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            String JSON = cUrlApi(SSQ_FCW_PERIOD_URL, HOST_FC, REFERER_FC);
            if (StringUtil.isEmpty(JSON)) {
                logger.info("[福彩官网--双色球期次("+period.getPeriod()+")开奖号码抓取] 历史期次数据为空");
                return;
            }

            JSONObject jsonObject = JSONObject.fromObject(JSON);
            if(StringUtil.isEmpty(jsonObject)) {
                logger.info("[福彩官网--双色球期次("+period.getPeriod()+")开奖号码抓取] 历史期次数据为空");
                return;
            }
            if(jsonObject.getInt("state") != 0) {
                logger.info("[福彩官网--双色球期次("+period.getPeriod()+")开奖号码抓取] 返回状态码错误");
                return;
            }

            JSONArray array = jsonObject.getJSONArray("result");
            if(StringUtil.isEmpty(array)) {
                logger.info("[福彩官网--双色球期次("+period.getPeriod()+")开奖号码抓取] 获取result属性为空");
                return;
            }
            //循环解析期次-取参数period中对应的期次
            for(int index=0; index < array.size(); index++) {
                JSONObject periodJson = array.getJSONObject(index);
                String curPeriod = periodJson.getString("code");
                if(curPeriod.equals(period.getPeriod())) {//取对应期次
                    JSONArray gradesJson = periodJson.getJSONArray("prizegrades");
                    JSONObject grade = JSONObject.fromObject(period.getPrizeGrade());
                    for(int k=0; k < gradesJson.size(); k++) {
                        String jj = gradesJson.getJSONObject(k).getString("type").replaceAll("1", "一等奖").replaceAll("2", "二等奖").replaceAll("3", "三等奖").replaceAll("4", "四等奖").replaceAll("5", "五等奖").replaceAll("6", "六等奖");
                        if(grade.containsKey(jj)) {
                            //六等奖中奖注数为0=可以确定开奖公告没发布
                            if(jj.equals("六等奖") && gradesJson.getJSONObject(k).getInt("typenum") == 0) {
                                return;
                            }
                            JSONObject job = grade.getJSONObject(jj);
                            job.put(LotteryGrade.zjzs, getMoney(gradesJson.getJSONObject(k).getString("typenum")));
                            job.put(LotteryGrade.dzjj, getMoney(gradesJson.getJSONObject(k).getString("typemoney")));
                        }
                    }
                    grade.put(LotteryGrade.tzje, periodJson.getString("sales"));
                    grade.put(LotteryGrade.jclj, periodJson.getString("poolmoney"));
                    period.setDrawNumber(ssqCodeSort(periodJson.getString("red") + "|" + periodJson.getString("blue")));
                    period.setDrawNumberTime(new Date());
                    period.setPrizeGrade(grade.toString());
                    period.setGrabSuccess(true);
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("[福彩官网-双色球期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
        }
    }

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
        p.setPeriod("2018008");
        p.setPrizeGrade("{\"一等奖\":{\"单注奖金\":\"0\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"--\",\"中奖注数\":\"0\"},\"二等奖\":{\"单注奖金\":\"0\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"--\",\"中奖注数\":\"0\"},\"三等奖\":{\"单注奖金\":\"0\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"--\",\"中奖注数\":\"0\"},\"四等奖\":{\"单注奖金\":\"200\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"100\",\"中奖注数\":\"0\"},\"五等奖\":{\"单注奖金\":\"10\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"5\",\"中奖注数\":\"0\"},\"六等奖\":{\"单注奖金\":\"5\",\"加奖奖金\":\"0\",\"追加加奖奖金\":\"5\",\"中奖注数\":\"0\"},\"投注总金额\":\"0\",\"奖池累计金额\":\"0\"}");
        new SsqAwardCode().GrabGuanWang(p);
        System.out.println(p.getPrizeGrade());
        System.out.println(p.getGrabSuccess());
        System.out.println(p.getDrawNumber());
    }
}
