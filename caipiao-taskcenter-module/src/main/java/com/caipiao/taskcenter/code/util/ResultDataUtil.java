package com.caipiao.taskcenter.code.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.taskcenter.code.kp.*;
import com.caipiao.taskcenter.code.mp.*;
import com.caipiao.taskcenter.code.zc.SfcAwardCode;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抓取开奖号码工具类
 * Created by kouyi on 2017/11/18.
 */
public class ResultDataUtil extends CodeUrlUtil implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(ResultDataUtil.class);

    /**
     * 开奖号码抓取工具
     * @param url
     * @param host
     * @return
     */
    public static Document cUrl(String url, String host) throws Exception {
        return Jsoup.connect(url).timeout(DEFAULT_TIMEOUT).header("User-Agent", AGENT).header("Host", host).get();
    }

    /**
     * 开奖号码抓取工具-抓取api接口
     * @param url
     * @param host
     * @return
     */
    public static String cUrlApi(String url, String host, String referer) throws Exception {
        return Jsoup.connect(url).timeout(DEFAULT_TIMEOUT).header("User-Agent", AGENT).header("Host", host).header("Referer", referer).ignoreContentType(true).get().text();
    }


    /**
     * 根据彩种获取实现类名
     * @param lotId
     * @return
     */
    public static String getClassName(String lotId){
        switch (lotId){
            //快频
            case LotteryConstants.X511_SD://十一运夺金
                return X511SdAwardCode.class.getName();
            case LotteryConstants.X511_SH://上海11选5
                return X511ShAwardCode.class.getName();
            case LotteryConstants.X511_GD://广东11选5
                return X511GdAwardCode.class.getName();
            case LotteryConstants.K3_AH://安徽快3
                return K3AhAwardCode.class.getName();
            case LotteryConstants.K3_JS://江苏快3
                return K3JsAwardCode.class.getName();
            case LotteryConstants.K3_JL://吉林快3
                return K3JlAwardCode.class.getName();
            case LotteryConstants.SSC_CQ://重庆时时彩
                return SscCqAwardCode.class.getName();
            //慢频数字彩
            case LotteryConstants.PL3://排列3
                return Pl3AwardCode.class.getName();
            case LotteryConstants.PL5://排列5
                return Pl5AwardCode.class.getName();
            case LotteryConstants.FC3D://福彩3D
                return Fc3dAwardCode.class.getName();
            case LotteryConstants.DLT://大乐透
                return DltAwardCode.class.getName();
            case LotteryConstants.QXC://七星彩
                return QxcAwardCode.class.getName();
            case LotteryConstants.SSQ://双色球
                return SsqAwardCode.class.getName();
            case LotteryConstants.QLC://七乐彩
                return QlcAwardCode.class.getName();
            //老足彩
            case LotteryConstants.SFC:
            case LotteryConstants.RXJ://胜负彩任九
                return SfcAwardCode.class.getName();
            default:
                return null;
        }
    }

    /**
     * 排列五投注总额
     * @param url
     * @param grade
     * @throws Exception
     */
    protected void pl5TouzhuMoney(String url, JSONObject grade) throws Exception {
        try {
            if (StringUtil.isEmpty(url)) {
                return;
            }
            Elements spans = cUrl(url, HOST_TC).select(".k_04").get(0).getElementsByTag("span");
            if (null != spans && spans.size() >= 5) {
                String text1= spans.get(1).text();
                if(StringUtil.isNotEmpty(text1) ){
                    String regEx="[^0-9.]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(text1);
                    if(m.find()){
                        String tz = m.replaceAll("").trim();
                        grade.put(LotteryGrade.tzje, CalculationUtils.getMoneyDouHao(tz));
                    }
                }
                String text2= spans.get(4).text();
                if(StringUtil.isNotEmpty(text2) ){
                    String regEx="[^0-9.]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(text2);
                    if(m.find()){
                        grade.put(LotteryGrade.jclj, CalculationUtils.getMoneyDouHao(m.replaceAll("").trim()));
                    }
                }
            }
        } catch (IOException e) {
            logger.error("[体彩网-排列5投注和奖池抓取异常", e);
        }
    }

    /**
     * 任选九投注总额
     * @param url
     * @param grade
     * @throws Exception
     */
    public void rx9TouzhuMoney(String url, JSONObject grade) throws Exception {
        try {
            if (StringUtil.isEmpty(url)) {
                return;
            }
            Elements elements = cUrl(url, HOST_TC).getElementsByTag("span");
            int index =0;
            for(Element element : elements){
                String text = element.text();
                if(text.indexOf("销售金额") != -1){
                    if (index > 0) {
                        String regEx="[^0-9.]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(text);
                        if(m.find()){
                            String je = m.replaceAll("").trim();
                            grade.put(LotteryGrade.tzje, CalculationUtils.getMoneyDouHao(je));
                        }
                    }
                    index++;
                }
                if(text.indexOf("奖金滚入下期奖池") != -1){
                    if (index > 0) {
                        String regEx="[^0-9.]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(text);
                        if(m.find()){
                            grade.put(LotteryGrade.jclj, CalculationUtils.getMoneyDouHao(m.replaceAll("").trim()));
                        }
                    }
                    index++;
                }
            }
        } catch (IOException e) {
            logger.error("[体彩网-任选九投注和奖池抓取异常", e);
        }
    }

    /**
     * 双色球数字排序
     * @param code
     */
    public static String ssqCodeSort(String code) {
        if(StringUtil.isEmpty(code)) {
            return "";
        }

        String[] codes = code.split("\\|");
        String[] cs = codes[0].split("\\,");
        String temp = "";
        int size = cs.length;
        for (int i = 0; i < size-1; i++) {
            for (int j = i; j < size; j++) {
                if (StringUtil.parseInt(cs[i]) > StringUtil.parseInt(cs[j])) {
                    temp = cs[i];
                    cs[i] = cs[j];
                    cs[j] = temp;
                }
            }
        }

        StringBuffer buffer = new StringBuffer();
        for(int m = 0; m < cs.length; m++) {
            buffer.append(cs[m]);
            if(m != cs.length - 1) {
                buffer.append(",");
            }
        }
        buffer.append("|");
        buffer.append(codes[1]);
        return buffer.toString();
    }

    public static void main(String[] args) {
        System.out.println(ssqCodeSort("20,23,19,14,21,06|08"));
    }
}
