package com.caipiao.common.lottery;

import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.DateUtil;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * 初始化生成各彩种奖级
 * Created by Kouyi on 2017/11/4.
 */
public class LotteryGrade {
    public static final String dzjj = "单注奖金";
    public static final String jjjj = "加奖奖金";
    public static final String zhjjj = "追加奖金";
    public static final String zjjj = "追加加奖奖金";
    public static final String zhjzs = "追加注数";
    public static final String zjzs = "中奖注数";
    public static final String tzje = "投注总金额";
    public static final String jclj = "奖池累计金额";
    private static Map<String,Object> grade = new LinkedHashMap<String,Object>();
    //双色球奖级初始化
    private static void getSsqGrade() {
        grade.clear();
        Map<String,Integer> gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("一等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("二等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,3000);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("三等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,200);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("四等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,10);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("五等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,5);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("六等奖",gradeData);

        grade.put(tzje,0);
        grade.put(jclj, 0);
    }

    //大乐透奖级初始化
    private static void getDltGrade() {
        grade.clear();
        Map<String,String> gradeData = new LinkedHashMap<String, String>();
        gradeData.put(dzjj,"0");//单注奖金
        gradeData.put(jjjj,"0");//加奖奖金
        gradeData.put(zjjj, "0");//追加加奖奖金
        gradeData.put(zjzs,"0");//中奖注数
        //gradeData.put(zhjjj, "0");//追加奖金
        //gradeData.put(zhjzs,"0");//追加注数
        grade.put("一等奖",gradeData);

        gradeData = new LinkedHashMap<String, String>();
        gradeData.put(dzjj,"0");
        gradeData.put(jjjj,"0");
        gradeData.put(zjjj, "0");
        gradeData.put(zjzs,"0");
        //gradeData.put(zhjjj, "0");
        //gradeData.put(zhjzs,"0");
        grade.put("二等奖",gradeData);

        gradeData = new LinkedHashMap<String, String>();
        gradeData.put(dzjj,"10000");
        gradeData.put(jjjj,"0");
        gradeData.put(zjjj, "0");
        gradeData.put(zjzs,"0");
        //gradeData.put(zhjjj, "0");
        //gradeData.put(zhjzs,"0");
        grade.put("三等奖",gradeData);

        gradeData = new LinkedHashMap<String, String>();
        gradeData.put(dzjj,"3000");
        gradeData.put(jjjj,"0");
        gradeData.put(zjjj, "0");
        gradeData.put(zjzs,"0");
        //gradeData.put(zhjjj, "0");
        //gradeData.put(zhjzs,"0");
        grade.put("四等奖",gradeData);

        gradeData = new LinkedHashMap<String, String>();
        gradeData.put(dzjj,"300");
        gradeData.put(jjjj,"0");
        gradeData.put(zjjj, "0");
        gradeData.put(zjzs,"0");
        //gradeData.put(zhjjj, "0");
        //gradeData.put(zhjzs,"0");
        grade.put("五等奖",gradeData);

        gradeData = new LinkedHashMap<String, String>();
        gradeData.put(dzjj,"200");
        gradeData.put(jjjj,"0");
        gradeData.put(zjjj, "0");
        gradeData.put(zjzs,"0");
        grade.put("六等奖",gradeData);

        gradeData = new LinkedHashMap<String, String>();
        gradeData.put(dzjj,"100");
        gradeData.put(jjjj,"0");
        gradeData.put(zjjj, "0");
        gradeData.put(zjzs,"0");
        grade.put("七等奖",gradeData);

        gradeData = new LinkedHashMap<String, String>();
        gradeData.put(dzjj,"15");
        gradeData.put(jjjj,"0");
        gradeData.put(zjjj, "0");
        gradeData.put(zjzs,"0");
        grade.put("八等奖",gradeData);

        gradeData = new LinkedHashMap<String, String>();
        gradeData.put(dzjj,"5");
        gradeData.put(jjjj,"0");
        gradeData.put(zjjj, "0");
        gradeData.put(zjzs,"0");
        grade.put("九等奖",gradeData);
        grade.put(tzje,0);
        grade.put(jclj, 0);

    }

    //七乐彩奖级初始化
    private static void getQlcGrade() {
        grade.clear();
        Map<String,Integer> gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("一等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("二等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("三等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,200);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("四等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,50);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("五等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,10);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("六等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,5);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("七等奖",gradeData);

        grade.put(tzje, 0);
        grade.put(jclj, 0);
    }

    //七星彩奖级初始化
    private static void getQxcGrade() {
        grade.clear();
        Map<String,Integer> gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("一等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("二等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,1800);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("三等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,300);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("四等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,20);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("五等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,5);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("六等奖",gradeData);

        grade.put(tzje, 0);
        grade.put(jclj, 0);
    }

    //11选5奖级初始化
    private static void get11x5Grade() {
        grade.clear();
        Map<String,Integer> gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,13);
        gradeData.put(jjjj,0);
        grade.put("前一直选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,6);
        gradeData.put(jjjj,0);
        grade.put("任选二",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,19);
        gradeData.put(jjjj,0);
        grade.put("任选三",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,78);
        gradeData.put(jjjj,0);
        grade.put("任选四",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,540);
        gradeData.put(jjjj,0);
        grade.put("任选五",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,90);
        gradeData.put(jjjj,0);
        grade.put("任选六",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,26);
        gradeData.put(jjjj,0);
        grade.put("任选七",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,9);
        gradeData.put(jjjj,0);
        grade.put("任选八",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,130);
        gradeData.put(jjjj,0);
        grade.put("前二直选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,65);
        gradeData.put(jjjj,0);
        grade.put("前二组选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,1170);
        gradeData.put(jjjj,0);
        grade.put("前三直选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,195);
        gradeData.put(jjjj,0);
        grade.put("前三组选",gradeData);

        grade.put(tzje, 0);
        grade.put(jclj, 0);
    }

    //快3奖级初始化
    private static void getK3Grade() {
        grade.clear();
        Map<String,Integer> gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,80);
        gradeData.put(jjjj,0);
        grade.put("和值4",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,40);
        gradeData.put(jjjj,0);
        grade.put("和值5",gradeData);


        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,25);
        gradeData.put(jjjj,0);
        grade.put("和值6",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,16);
        gradeData.put(jjjj,0);
        grade.put("和值7",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,12);
        gradeData.put(jjjj,0);
        grade.put("和值8",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,10);
        gradeData.put(jjjj,0);
        grade.put("和值9",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,9);
        gradeData.put(jjjj,0);
        grade.put("和值10",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,9);
        gradeData.put(jjjj,0);
        grade.put("和值11",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,10);
        gradeData.put(jjjj,0);
        grade.put("和值12",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,12);
        gradeData.put(jjjj,0);
        grade.put("和值13",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,16);
        gradeData.put(jjjj,0);
        grade.put("和值14",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,25);
        gradeData.put(jjjj,0);
        grade.put("和值15",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,40);
        gradeData.put(jjjj,0);
        grade.put("和值16",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,80);
        gradeData.put(jjjj,0);
        grade.put("和值17",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,40);
        gradeData.put(jjjj,0);
        grade.put("三同号通选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,240);
        gradeData.put(jjjj,0);
        grade.put("三同号单选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,40);
        gradeData.put(jjjj,0);
        grade.put("三不同号",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,10);
        gradeData.put(jjjj,0);
        grade.put("三连号通选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,15);
        gradeData.put(jjjj,0);
        grade.put("二同号复选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,80);
        gradeData.put(jjjj,0);
        grade.put("二同号单选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,8);
        gradeData.put(jjjj,0);
        grade.put("二不同号",gradeData);

        grade.put(tzje, 0);
        grade.put(jclj, 0);
    }

    //福彩3D、排列3
    private static void get3dGrade() {
        grade.clear();
        Map<String,Integer> gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,1040);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("直选",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,346);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("组三",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,173);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("组六",gradeData);

        grade.put(tzje, 0);
        grade.put(jclj, 0);
    }

    //排列5
    private static void getPl5Grade() {
        grade.clear();
        Map<String,Integer> gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,100000);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("一等奖",gradeData);

        grade.put(tzje, 0);
        grade.put(jclj, 0);
    }


    //胜负彩
    private static void getSfcGrade() {
        grade.clear();
        Map<String,Integer> gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("一等奖",gradeData);

        gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("二等奖",gradeData);

        grade.put(tzje, 0);
        grade.put(jclj, 0);
    }

    //任九和进球彩与半全场
    private static void getRjGrade() {
        grade.clear();
        Map<String,Integer> gradeData = new LinkedHashMap<String, Integer>();
        gradeData.put(dzjj,0);
        gradeData.put(jjjj,0);
        gradeData.put(zjzs,0);
        grade.put("一等奖",gradeData);

        grade.put(tzje, 0);
        grade.put(jclj, 0);
    }

    //胜负彩任九对阵模板
    private static void getSfcRjMatch() {
        grade.clear();
        Map<String,String> matchData = new LinkedHashMap<String, String>();
        matchData.put("序号","1");
        matchData.put("赛事","英超");
        matchData.put("主队","利物浦");
        matchData.put("客队","南安普顿");
        matchData.put("比赛时间", DateUtil.dateDefaultFormat(new Date()));
        matchData.put("比分","1:0");
        grade.put("1", matchData);
        Map<String,String> matchData2 = new LinkedHashMap<String, String>();
        matchData2.put("序号","2");
        matchData2.put("赛事","英超");
        matchData2.put("主队","利物浦");
        matchData2.put("客队","南安普顿");
        matchData2.put("比赛时间", DateUtil.dateDefaultFormat(new Date()));
        matchData2.put("比分","2:0");
        grade.put("2", matchData);
    }

    public static void main(String[] args) {
        getSsqGrade();
        System.out.println("创建双色球奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        get3dGrade();
        System.out.println("创建福彩3D和排列3奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        getPl5Grade();
        System.out.println("创建排列5奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        getDltGrade();
        System.out.println("创建大乐透奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        getK3Grade();
        System.out.println("创建快3奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        get11x5Grade();
        System.out.println("创建11选5奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        getQxcGrade();
        System.out.println("创建七星彩奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        getQlcGrade();
        System.out.println("创建七乐彩奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        getSfcGrade();
        System.out.println("创建胜负彩奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        getRjGrade();
        System.out.println("创建任九和进球彩与半全场奖级json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        getSfcRjMatch();
        System.out.println("创建胜负彩任九对阵json字符串如下");
        System.out.println(JsonUtil.JsonObject(grade));
        //Map<String, Object> map = JsonUtil.jsonToMap(JsonUtil.JsonObject(grade));
        //System.out.println(map.size());

        JSONObject jsonObject = JSONObject.fromObject("{\"一等奖\":{\"单注奖金\":\"10000000\",\"加奖奖金\":0,\"中奖注数\":\"4\"},\"二等奖\":{\"单注奖金\":\"190901\",\"加奖奖金\":0,\"中奖注数\":\"165\"},\"三等奖\":{\"单注奖金\":\"3000\",\"加奖奖金\":0,\"中奖注数\":\"989\"},\"四等奖\":{\"单注奖金\":\"200\",\"加奖奖金\":0,\"中奖注数\":\"61094\"},\"五等奖\":{\"单注奖金\":\"10\",\"加奖奖金\":0,\"中奖注数\":\"1333557\"},\"六等奖\":{\"单注奖金\":\"5\",\"加奖奖金\":0,\"中奖注数\":\"5586895\"},\"投注总金额\":\"372,349,292元\",\"奖池累计金额\":\"324,719,716元\"}");
        System.out.println(jsonObject.getJSONObject("一等奖").get(dzjj));
    }
}
