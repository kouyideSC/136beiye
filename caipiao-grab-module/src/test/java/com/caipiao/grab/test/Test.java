package com.caipiao.grab.test;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.plugin.helper.PluginUtil;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kouyi on 2018/4/4.
 */
public class Test {
    public static void main(String[] args) {
        String content = "HH|20181101310>SFC=05(18.50)/04(9.60)|1*1";
        Map<String, String> mapSp = getSchemeCodeSp(content);
        System.out.println(mapSp.toString());
    }

    /**
     * 将用户订单sp串格式化为map用来生成票对应的sp串-算奖使用
     * @param schemeCodeSp
     * @return
     */
    private static Map<String, String> getSchemeCodeSp(String schemeCodeSp) {
        Map<String, String> spMap = new HashMap<>();
        if(StringUtil.isEmpty(schemeCodeSp)) {
            return spMap;
        }
        schemeCodeSp = schemeCodeSp.replaceAll("\\(", "&").replaceAll("\\)", "");
        String[] cs = PluginUtil.splitter(schemeCodeSp, "|");
        if(cs.length != 3) {
            return null;
        }

        String[] tdan = PluginUtil.splitter(cs[1], "$");
        for(String dan : tdan) {
            String[] sps = PluginUtil.splitter(dan, ",");
            if (schemeCodeSp.indexOf(">") > -1) {//混投
                for (String sp : sps) {
                    String[] ms = PluginUtil.splitter(sp, ">");
                    String[] xs = PluginUtil.splitter(ms[1], "+");
                    for (String ch : xs) {
                        String[] alx = PluginUtil.splitter(ch, "=");
                        String[] fs = PluginUtil.splitter(alx[0], "&");//针对让球胜平负、让分胜负、大小分处理分值
                        if (alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RQSPF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                            alx[0] = fs[0];
                        }
                        String[] gs = PluginUtil.splitter(alx[1], "/");
                        for (String g : gs) {
                            String[] s = PluginUtil.splitter(g, "&");
                            if (alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                                spMap.put(ms[0] + alx[0] + s[0], "(" + fs[1] + ")|(" + s[1] + ")");
                            } else {
                                spMap.put(ms[0] + alx[0] + s[0], "|(" + s[1] + ")");
                            }
                        }
                    }
                }
            } else {
                for (String sp : sps) {
                    String[] ms = PluginUtil.splitter(sp, "=");
                    String[] fs = PluginUtil.splitter(ms[0], "&");//针对让球胜平负、让分胜负、大小分处理分值
                    if (cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RQSPF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                        ms[0] = fs[0];
                    }
                    String[] xs = PluginUtil.splitter(ms[1], "/");
                    for (String ch : xs) {
                        String[] alx = PluginUtil.splitter(ch, "&");
                        if (cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                            spMap.put(ms[0] + cs[0] + alx[0], "(" + fs[1] + ")|(" + alx[1] + ")");
                        } else {
                            spMap.put(ms[0] + cs[0]  + alx[0], "|(" + alx[1] + ")");
                        }
                    }
                }
            }
        }
        return spMap;
    }
}
