package com.caipiao.taskcenter.Test;

import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kouyi on 2018/6/4.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(CalculationUtils.bankerAlgoNum(7.62/5600)*100);
        for(int a=0;a<100;a++){
            System.out.println((int)(Math.random() * 40+20));
        }
        System.out.println(getInfoDetail("3串1中1注 实际<b>中奖</b>:税前176.4元 税后176.4元<br/>派奖小结：税前<b>派奖总奖金</b>176.4元 税后<b>派奖总奖金</b>176.4元", 100));
    }

    /**
     * 神单发单人中奖详情-增加打赏说明
     * @param detail
     * @param reward
     * @return
     */
    private static String getInfoDetail(String detail, double reward) {
        if(StringUtil.isEmpty(detail) || reward <= 0) {
            return detail;
        }

        int begin = detail.indexOf("税前<b>派奖总奖金</b>");
        //税前和税后加上收获打赏金额
        String info = detail.substring(0, begin);
        String regEx = "[^0-9.]";
        String[] des = detail.substring(begin, detail.length()).split("\\s+");
        for(int index=0; index<des.length; index++) {
            int pos = des[index].length() - 1;
            int len = des[index].indexOf("(");
            if(len > -1) {
                pos = len;
            }
            Pattern pattern = Pattern.compile(regEx);
            Matcher m = pattern.matcher(des[index].substring(0, pos));
            if(m.find()) {
                String money = m.replaceAll("").trim();
                des[index] = des[index].replaceAll(money, CalculationUtils.spValue(Double.parseDouble(money) + reward)+"");
            }
            if(des[index].indexOf("(") > 0 && des[index].indexOf(")") > 0) {
                des[index] = des[index].substring(0, des[index].indexOf(")"));
                des[index] += ",";
            } else {
                des[index] += "(";
            }
            des[index] += "获得打赏" + reward + "元)";
            info += des[index];
            info += " ";
        }

        if(info.endsWith(" ")) {
            info = info.substring(0, info.length()-1);
        }
        return info;
    }
}
