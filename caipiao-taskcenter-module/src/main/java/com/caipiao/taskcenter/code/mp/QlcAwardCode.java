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
 * 抓取七乐彩开奖信息
 * Created by kouyi on 2017/11/16.
 */
public class QlcAwardCode extends ResultDataUtil implements GrabDrawCode {
    private static Logger logger = LoggerFactory.getLogger(QlcAwardCode.class);

    /**
     * 抓取福彩官网-开奖号码和奖级
     * @param period
     */
    public void GrabGuanWang(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }
            Document doc = cUrl(QLC_FCW_PERIOD_URL, HOST_FC);
            Elements tables = doc.getElementsByClass("hz");
            if (StringUtil.isEmpty(tables)) {
                logger.info("[福彩官网-七乐彩期次("+period.getPeriod()+")开奖号码抓取] 获取hz标签无数据");
                return;
            }

            Elements trs = tables.get(0).getElementsByTag("tr");
            if (StringUtil.isEmpty(trs) || trs.size() < 3) {
                logger.info("[福彩官网-七乐彩期次("+period.getPeriod()+")开奖号码抓取] 历史期次数据为空");
                return;
            }

            //循环解析期次-取参数period中对应的期次
            for(int index=2; index < trs.size(); index++) {
                Elements tds = trs.get(index).getElementsByTag("td");
                if(StringUtil.isEmpty(tds) || tds.size() < 7) {
                    continue;
                }
                String curPeriod = tds.get(0).text();
                if(curPeriod.equals(period.getPeriod())) {//取对应期次
                    String detailUrl = tds.get(6).getElementsByTag("a").attr("href").replaceAll("../../..", "http://" + HOST_FC);
                    Document detail_doc = cUrl(detailUrl, HOST_FC);
                    Elements detailTables = detail_doc.getElementsByClass("mt17");
                    if (StringUtil.isEmpty(detailTables)) {
                        logger.info("[福彩官网-七乐彩期次("+period.getPeriod()+")开奖公告页抓取] 获取mt17标签无数据");
                        return;
                    }

                    Elements detailTrs = detailTables.get(0).getElementsByTag("tr");
                    if (StringUtil.isEmpty(detailTrs) || detailTrs.size() < 7) {
                        logger.info("[福彩官网-七乐彩期次("+period.getPeriod()+")开奖公告页抓取] 开奖公告详情数据为空");
                        return;
                    }

                    JSONObject grade = JSONObject.fromObject(period.getPrizeGrade());
                    for(int k=1; k < detailTrs.size(); k++) {
                        Elements d = detailTrs.get(k).getElementsByTag("td");
                        if(StringUtil.isEmpty(d) || d.size() < 3) {
                            continue;
                        }
                        if(grade.containsKey(d.get(0).text())) {
                            if(d.get(0).text().equals("七等奖") && parseInt(d.get(1).text()) == 0) {
                                break;
                            }
                            JSONObject job = grade.getJSONObject(d.get(0).text());
                            job.put(LotteryGrade.zjzs, getMoney(d.get(1).text()));
                            job.put(LotteryGrade.dzjj, getMoney(d.get(2).text()));
                        }
                    }
                    grade.put(LotteryGrade.tzje, tds.get(4).text());
                    grade.put(LotteryGrade.jclj, tds.get(5).text());
                    period.setDrawNumber(tds.get(1).text().replaceAll(" ", ",") + "|" + tds.get(2).text().replaceAll(" ", ","));
                    period.setDrawNumberTime(new Date());
                    period.setPrizeGrade(grade.toString());
                    period.setGrabSuccess(true);
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("[福彩官网-七乐彩期次("+period.getPeriod()+")开奖号码抓取异常,原因=" + ex.getMessage());
        }
    }

    /**
     * 抓取彩经网开奖号码和奖级
     * @param period
     */
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
        p.setPeriod("2017134");
        p.setPrizeGrade("{\"一等奖\":{\"单注奖金\":0,\"加奖奖金\":466,\"中奖注数\":0},\"二等奖\":{\"单注奖金\":0,\"加奖奖金\":2333,\"中奖注数\":0},\"三等奖\":{\"单注奖金\":0,\"加奖奖金\":0,\"中奖注数\":0},\"四等奖\":{\"单注奖金\":200,\"加奖奖金\":0,\"中奖注数\":0},\"五等奖\":{\"单注奖金\":50,\"加奖奖金\":0,\"中奖注数\":0},\"六等奖\":{\"单注奖金\":10,\"加奖奖金\":0,\"中奖注数\":0},\"七等奖\":{\"单注奖金\":5,\"加奖奖金\":0,\"中奖注数\":0},\"投注总金额\":0,\"奖池累计金额\":0}");
        new QlcAwardCode().GrabGuanWang(p);
        System.out.println(p.getPrizeGrade());
        System.out.println(p.getGrabSuccess());
    }
}
