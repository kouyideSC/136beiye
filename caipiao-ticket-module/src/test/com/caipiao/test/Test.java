package com.caipiao.test;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.*;
import com.caipiao.common.lottery.CombineUtil;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.LotteryVo;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.config.SysConfig;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.caipiao.ticket.util.OuKeTicketUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by Kouyi on 2018/1/25.
 */
public class Test {
    public static HashMap<String, String> voteLotteryPlayType = new HashMap<String, String>();//出票商玩法编码对应网站玩法编码
    static {
        voteLotteryPlayType.put("3010", LotteryConstants.JCWF_PREFIX_SPF);
        voteLotteryPlayType.put("3006", LotteryConstants.JCWF_PREFIX_RQSPF);
        voteLotteryPlayType.put("3007", LotteryConstants.JCWF_PREFIX_CBF);
        voteLotteryPlayType.put("3009", LotteryConstants.JCWF_PREFIX_BQC);
        voteLotteryPlayType.put("3008", LotteryConstants.JCWF_PREFIX_JQS);
        voteLotteryPlayType.put("3001", LotteryConstants.JCWF_PREFIX_SF);
        voteLotteryPlayType.put("3002", LotteryConstants.JCWF_PREFIX_RFSF);
        voteLotteryPlayType.put("3003", LotteryConstants.JCWF_PREFIX_SFC);
        voteLotteryPlayType.put("3004", LotteryConstants.JCWF_PREFIX_DXF);
    }

    public static void main(String[] args) {
        try {
            /*String codeSp = "01,01:3.00/02:3.40/32:1000";
            SchemeTicket ticket = new SchemeTicket();
            ticket.setPlayTypeId("1980");
            String sl = formatCodeSp(codeSp, ticket);
            System.out.println(sl);

            List<int[]> resultList = new ArrayList<>();
            Map<String, List<Integer>> hashMap = new HashMap<>();
            String[] str = "0-3,0-30,0-30,30-3,30-30,30-30,30-3,30-30,30-30".split(",");
            for(int i=0; i<str.length; i++) {
                List<int[]> listInt = new ArrayList<>();
                String[] xuan = str[i].split("\\-");
                for(String xn : xuan) {
                    int[] vl = new int[xn.length()];
                    for(int k=0; k<xn.length(); k++) {
                        vl[k] = StringUtil.parseInt(xn.charAt(k));
                    }
                    listInt.add(vl);
                }
                CombineUtil.combineMulPlay(listInt, new int[listInt.size()], 0, resultList);
            }
            System.out.println(resultList.size());*/
            //System.out.println(getTicketSp("GYJ|2018001=41/44",getSchemeCodeSp("GYJ|2018001=41(8.0)/44(6.5)")));
            String xm = "{\"cardCode\":80000066,\"lotteryCode\":\"JCLQ\",\"pwd\":null,\"betSoruce\":0,\"key\":null,\"resultCode\":\"SUCCESS\",\"message\":[{\"odds\":{\"spMap\":{\"matchKey\":null,\"matchNumber\":[{\"matchNumber\":\"20181106302\",\"valueMap\":{\"1\":\"1.87\"},\"vMap\":{\"WIN\":\"1.87\"},\"value\":\"{\\\"1\\\":\\\"1.87\\\"}\",\"playType\":\"SF\",\"handicap\":0.0},{\"matchNumber\":\"20181106303\",\"valueMap\":{\"2\":\"1.63\"},\"vMap\":{\"LOSE\":\"1.63\"},\"value\":\"{\\\"2\\\":\\\"1.63\\\"}\",\"playType\":\"SF\",\"handicap\":0.0}]}},\"orderNumber\":\"\",\"result\":\"SUC_TICKET\",\"successTime\":\"2018-11-05 12:48:48\",\"ticketId\":\"TK2VHW01M8ND103T1TW20TX1K5F57084\",\"playType\":null}]}";
            JSONObject responseJson = JSONObject.fromObject(xm);
            String resultCode = responseJson.getString("resultCode");
            if(resultCode.equals("ORDER_MD5_ERROR")) {
                return;
            }

            JSONArray arrays = responseJson.getJSONArray("message");
            for(int x = 0; x < arrays.size(); x++) {
                JSONObject rbody = arrays.getJSONObject(x);
                String code = rbody.getString("result");
                String orderId = rbody.getString("ticketId");
                if ("SUC_TICKET".equals(code)) {
                    String voteTicketId = rbody.getString("orderNumber");
                    JSONArray codeSp = rbody.getJSONObject("odds").getJSONObject("spMap").getJSONArray("matchNumber");
                    Map<String, Map<String, String>> infoMap = getResponseSpValue(codeSp);
                    System.out.println(formatCodeSp("1940", "SF|20181106302=3,20181106303=0|2*1", infoMap));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String formatCodeSp(String sid, String codes, Map<String, Map<String, String>> infoMap) {
        if(StringUtil.isEmpty(codes) || StringUtil.isEmpty(infoMap)) {
            return null;
        }
        codes = codes.replaceAll("\\-", "").replaceAll("\\:", "").replaceAll("\\>", "->");
        StringBuffer buffer = new StringBuffer();
        String[] splitCodes = codes.split("\\|");
        if(sid.equals("1700"))
        {
            //20180202012->RQSPF=1,20180202013->RQSPF=1,20180202021->SPF=1,20180202027->RQSPF=1
            String[] content = splitCodes[1].split("\\,");
            for(int n=0; n<content.length; n++) {
                String[] xcn = content[n].split("\\->");
                if(infoMap.containsKey(xcn[0])) {
                    Map<String, String> choose = infoMap.get(xcn[0]);
                    String[] cn = content[n].split("\\=");
                    buffer.append(cn[0]).append("=");
                    String[] c = cn[1].split("\\/");
                    for(int o=0 ; o<c.length; o++) {
                        String sp = choose.get(c[o]);
                        if(StringUtil.isEmpty(sp)) {
                            return null;
                        }
                        buffer.append(c[o] + "@" + sp);
                        if(o != c.length - 1) {
                            buffer.append("/");
                        }
                    }
                } else {
                    return null;
                }
                if(n != content.length -1) {
                    buffer.append(",");
                }
            }
        }
        else if(sid.equals("1710"))
        {
            //20181106301->DXF=3,20181106302->RFSF=3
            String[] content = splitCodes[1].split("\\,");
            for(int n=0; n<content.length; n++) {
                String[] xcn = content[n].split("\\->");
                if(infoMap.containsKey(xcn[0])) {
                    Map<String, String> choose = infoMap.get(xcn[0]);
                    String[] cn = xcn[1].split("\\=");
                    buffer.append(xcn[0]).append("->").append(cn[0]).append("=");
                    String[] c = cn[1].split("\\/");
                    for(int o=0 ; o<c.length; o++) {
                        if(cn[0].equals(LotteryConstants.JCWF_PREFIX_DXF) || cn[0].equals(LotteryConstants.JCWF_PREFIX_RFSF)) {
                            String temp = c[o].replace("3", "1").replace("0", "2");
                            String sp = choose.get(temp);
                            String pan = choose.get("p");
                            if(StringUtil.isEmpty(sp) || StringUtil.isEmpty(pan)) {
                                return null;
                            }
                            buffer.append(c[o] + "&").append(pan).append("@").append(sp);
                        } else if(cn[0].equals(LotteryConstants.JCWF_PREFIX_SF)) {
                            String temp = c[o].replace("3", "1").replace("0", "2");
                            String sp = choose.get(temp);
                            if(StringUtil.isEmpty(sp)) {
                                return null;
                            }
                            buffer.append(c[o] + "@").append(sp);
                        } else {
                            String sp = choose.get(c[o]);
                            if(StringUtil.isEmpty(sp)) {
                                return null;
                            }
                            buffer.append(c[o] + "@" + sp);
                        }
                        if(o != c.length - 1) {
                            buffer.append("/");
                        }
                    }
                } else {
                    return null;
                }
                if(n != content.length -1) {
                    buffer.append(",");
                }
            }
        }
        else if(sid.equals("1900"))
        {//足球其他玩法
            //20181103023=3/1,20181103024=0
            String[] content = splitCodes[1].split("\\,");
            for(int n=0; n<content.length; n++) {
                String[] xcn = content[n].split("\\=");
                if(infoMap.containsKey(xcn[0])) {
                    Map<String, String> choose = infoMap.get(xcn[0]);
                    buffer.append(xcn[0]).append("=");
                    String[] cn = xcn[1].split("\\/");
                    for(int p=0 ; p<cn.length; p++) {
                        String sp = choose.get(cn[p]);
                        if(StringUtil.isEmpty(sp)) {
                            return null;
                        }
                        buffer.append(cn[p] + "@" + sp);
                        if(p != cn.length - 1) {
                            buffer.append("/");
                        }
                    }
                } else {
                    return null;
                }
                if(n != content.length -1) {
                    buffer.append(",");
                }
            }
        }
        else if(sid.equals("1940"))
        {//篮球其他玩法
            //20181106301=0,20181106302=3
            String[] content = splitCodes[1].split("\\,");
            for(int n=0; n<content.length; n++) {
                String[] xcn = content[n].split("\\=");
                if(infoMap.containsKey(xcn[0])) {
                    Map<String, String> choose = infoMap.get(xcn[0]);
                    buffer.append(xcn[0]).append("=");
                    String[] c = xcn[1].split("\\/");
                    for(int o=0 ; o<c.length; o++) {
                        if(splitCodes[0].equals(LotteryConstants.JCWF_PREFIX_DXF) || splitCodes[0].equals(LotteryConstants.JCWF_PREFIX_RFSF)) {
                            String temp = c[o].replace("3", "1").replace("0", "2");
                            String sp = choose.get(temp);
                            String pan = choose.get("p");
                            if(StringUtil.isEmpty(sp) || StringUtil.isEmpty(pan)) {
                                return null;
                            }
                            buffer.append(c[o] + "&").append(pan).append("@").append(sp);
                        } else if(splitCodes[0].equals(LotteryConstants.JCWF_PREFIX_SF)) {
                            String temp = c[o].replace("3", "1").replace("0", "2");
                            String sp = choose.get(temp);
                            if(StringUtil.isEmpty(sp)) {
                                return null;
                            }
                            buffer.append(c[o] + "@").append(sp);
                        } else {
                            String sp = choose.get(c[o]);
                            if(StringUtil.isEmpty(sp)) {
                                return null;
                            }
                            buffer.append(c[o] + "@" + sp);
                        }
                        if(o != c.length - 1) {
                            buffer.append("/");
                        }
                    }
                } else {
                    return null;
                }
                if(n != content.length -1) {
                    buffer.append(",");
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 将出票商返回的赔率数据格式化为MAP结构
     * @param codeSp
     * @return
     */
    private static Map<String, Map<String, String>> getResponseSpValue(JSONArray codeSp) {
        if(StringUtil.isEmpty(codeSp)) {
            return null;
        }
        Map<String, Map<String, String>> infoMap = new HashMap<>();
        for(int m = 0; m < codeSp.size(); m++) {
            JSONObject spInfo = codeSp.getJSONObject(m);
            String matchCode = spInfo.getString("matchNumber");
            JSONObject valueJson = spInfo.getJSONObject("value");

            if(StringUtil.isNotEmpty(spInfo.get("handicap"))) {
                valueJson.put("p", spInfo.getString("handicap"));
            }
            Map<String, String> valueMap = new HashMap<>();
            Iterator it = valueJson.keys();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                String value = (String) valueJson.get(key);
                valueMap.put(key, value);
            }
            infoMap.put(matchCode, valueMap);
        }
        return infoMap;
    }


    /**
     * 将用户订单sp串格式化为map用来生成票对应的sp串-算奖使用
     * @param schemeCodeSp
     * @return
     */
    public static Map<String, String> getSchemeCodeSp(String schemeCodeSp) {
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
                                spMap.put(ms[0] + "->" + alx[0] + "->" + s[0], "&" + fs[1] + "@" + s[1]);
                            } else {
                                spMap.put(ms[0] + "->" + alx[0] + "->" + s[0], "@" + s[1]);
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
                            spMap.put(ms[0] + "->" + cs[0] + "->" + alx[0], "&" + fs[1] + "@" + alx[1]);
                        } else {
                            spMap.put(ms[0] + "->" + cs[0] + "->" + alx[0], "@" + alx[1]);
                        }
                    }
                }
            }
        }
        return spMap;
    }

    /**
     * 拆票时默认写入用户下单时的sp
     * @param codes
     * @param spMap
     * @return
     */
    public static String getTicketSp(String codes, Map<String, String> spMap) {
        if(StringUtil.isEmpty(codes) || StringUtil.isEmpty(spMap)) {
            return null;
        }
        String[] cs = PluginUtil.splitter(codes, "|");
        if(cs.length != 3) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        String[] sps = PluginUtil.splitter(cs[1], ",");
        if (codes.indexOf(">") > -1) {//混投
            for (int k=0; k<sps.length; k++) {
                String[] ms = PluginUtil.splitter(sps[k], ">");
                buffer.append(ms[0]);
                buffer.append("->");
                String[] xs = PluginUtil.splitter(ms[1], "=");
                buffer.append(xs[0]);
                buffer.append("=");
                String[] ss = PluginUtil.splitter(xs[1], "/");
                for (int n=0; n<ss.length; n++) {
                    buffer.append(ss[n].replaceAll("\\:","").replaceAll("\\-",""));
                    String key = ms[0] + "->" + xs[0] + "->" + ss[n];
                    if(!spMap.containsKey(key)) {
                        return null;
                    }
                    buffer.append(spMap.get(key));
                    if(n != ss.length - 1) {
                        buffer.append("/");
                    }
                }
                if(k != sps.length - 1) {
                    buffer.append(",");
                }
            }
        } else {
            for (int k=0; k<sps.length; k++) {
                String[] ms = PluginUtil.splitter(sps[k], "=");
                buffer.append(ms[0]);
                buffer.append("=");
                String[] xs = PluginUtil.splitter(ms[1], "/");
                for (int n=0; n<xs.length; n++) {
                    buffer.append(xs[n].replaceAll("\\:","").replaceAll("\\-",""));
                    String key = ms[0] + "->" + cs[0] + "->" + xs[n];
                    if(!spMap.containsKey(key)) {
                        return null;
                    }
                    buffer.append(spMap.get(key));
                    if(n != xs.length - 1) {
                        buffer.append("/");
                    }
                }
                if(k != sps.length - 1) {
                    buffer.append(",");
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 格式化出票sp
     * @param codeSp
     * @return
     */
    public static String formatCodeSp(String codeSp, SchemeTicket ticket) throws Exception {
        if(StringUtil.isEmpty(codeSp) || StringUtil.isEmpty(ticket)) {
            return null;
        }

        codeSp = codeSp.replaceAll("F", "").replaceAll("B", "").replaceAll("\\(", "&").replaceAll("\\)","")
                .replaceAll("\\,", "=").replaceAll("\\//", ",").replaceAll("\\_", "").replaceAll("\\+","");
        if(!ticket.getPlayTypeId().equals(LotteryConstants.JCZQCBF)
                && !ticket.getPlayTypeId().equals(LotteryConstants.JCZQ)
                && !ticket.getPlayTypeId().equals(LotteryConstants.JCLQ)) {
            codeSp = codeSp.replaceAll("\\:", "@");
        }
        if(ticket.getPlayTypeId().equals(LotteryConstants.JCZQCBF)) {
            StringBuffer newSp = new StringBuffer();
            String[] codes = PluginUtil.splitter(codeSp, ",");
            for(int n=0; n< codes.length; n++) {
                String[] co = PluginUtil.splitter(codes[n], "=");
                newSp.append(co[0]+"=");
                String[] cd = PluginUtil.splitter(co[1], "/");
                for(int k=0; k< cd.length; k++) {
                    String[] bfs = PluginUtil.splitter(cd[k], ":");
                    if(bfs.length != 3) {
                        continue;
                    }
                    newSp.append((bfs[0]+bfs[1]).replaceAll("43", "90").replaceAll("44", "99").replaceAll("34", "09"));
                    newSp.append("@");
                    newSp.append(bfs[2]);
                    if(k != cd.length -1) {
                        newSp.append("/");
                    }
                }
                if(n != codes.length -1) {
                    newSp.append(",");
                }
            }
            codeSp = newSp.toString();
        }
        if(ticket.getPlayTypeId().equals(LotteryConstants.JCLQSF)) {
            StringBuffer newSp = new StringBuffer();
            String[] codes = PluginUtil.splitter(codeSp, ",");
            for(int n=0; n< codes.length; n++) {
                String[] co = PluginUtil.splitter(codes[n], "=");
                newSp.append(co[0]+"=");
                String[] cd = PluginUtil.splitter(co[1], "/");
                for(int k=0; k< cd.length; k++) {
                    String[] bfs = PluginUtil.splitter(cd[k], "@");
                    if(bfs.length != 2) {
                        continue;
                    }
                    newSp.append((bfs[0]).replaceAll("1", "3"));
                    newSp.append("@");
                    newSp.append(bfs[1]);
                    if(k != cd.length -1) {
                        newSp.append("/");
                    }
                }
                if(n != codes.length -1) {
                    newSp.append(",");
                }
            }
            codeSp = newSp.toString();
        }
        if(ticket.getPlayTypeId().equals(LotteryConstants.JCLQRFSF) || ticket.getPlayTypeId().equals(LotteryConstants.JCLQDXF)) {
            StringBuffer newSp = new StringBuffer();
            String[] codes = PluginUtil.splitter(codeSp, ",");
            for(int n=0; n< codes.length; n++) {
                String[] co = PluginUtil.splitter(codes[n], "=");
                newSp.append(co[0]+"=");
                String[] cd = PluginUtil.splitter(co[1], "/");
                for(int k=0; k< cd.length; k++) {
                    String[] cbf = PluginUtil.splitter(cd[k], "@");
                    if(cbf.length != 2) {
                        continue;
                    }
                    newSp.append(cbf[0].substring(0,1).replaceAll("1","3").replaceAll("2","0"));
                    newSp.append("&");
                    newSp.append(cbf[0].substring(2));
                    newSp.append("@");
                    newSp.append(cbf[1]);
                    if(k != cd.length -1) {
                        newSp.append("/");
                    }
                }
                if(n != codes.length -1) {
                    newSp.append(",");
                }
            }
            codeSp = newSp.toString();
        }
        if(ticket.getPlayTypeId().equals(LotteryConstants.JCLQSFC)) {
            StringBuffer newSp = new StringBuffer();
            String[] codes = PluginUtil.splitter(codeSp, ",");
            for(int n=0; n< codes.length; n++) {
                String[] co = PluginUtil.splitter(codes[n], "=");
                newSp.append(co[0]+"=");
                String[] cd = PluginUtil.splitter(co[1], "/");
                for(int k=0; k< cd.length; k++) {
                    String[] bfs = PluginUtil.splitter(cd[k], "@");
                    if(bfs.length != 2) {
                        continue;
                    }
                    newSp.append((bfs[0]).replaceAll("01", "11")
                            .replaceAll("02", "12")
                            .replaceAll("03", "13")
                            .replaceAll("04", "14")
                            .replaceAll("05", "15")
                            .replaceAll("06", "16")
                            .replaceAll("51", "01")
                            .replaceAll("52", "02")
                            .replaceAll("53", "03")
                            .replaceAll("54", "04")
                            .replaceAll("55", "05")
                            .replaceAll("56", "06"));
                    newSp.append("@");
                    newSp.append(bfs[1]);
                    if(k != cd.length -1) {
                        newSp.append("/");
                    }
                }
                if(n != codes.length -1) {
                    newSp.append(",");
                }
            }
            codeSp = newSp.toString();
        }
        if(ticket.getPlayTypeId().equals(LotteryConstants.JCZQ) || ticket.getPlayTypeId().equals(LotteryConstants.JCLQ)) {
            StringBuffer newSp = new StringBuffer();
            String[] codes = PluginUtil.splitter(codeSp, ",");
            for(int n=0; n< codes.length; n++) {
                codes[n] = new StringBuffer(codes[n]).reverse().toString().replaceFirst("-", "#");
                codes[n] = new StringBuffer(codes[n]).reverse().toString();
                String[] wf = PluginUtil.splitter(codes[n], "#");
                String playType = JiMiTicketUtil.getVoteLotteryTypeMap(wf[1]);
                if(playType.equals(LotteryConstants.JCWF_PREFIX_CBF)) {
                    String[] co = PluginUtil.splitter(codes[n], "=");
                    newSp.append(co[0]+"=");
                    String[] cd = PluginUtil.splitter(co[1], "/");
                    for(int k=0; k< cd.length; k++) {
                        String[] bf = PluginUtil.splitter(cd[k], ":");
                        if(bf.length != 3) {
                            continue;
                        }
                        newSp.append((bf[0]+bf[1]).replaceAll("43", "90").replaceAll("44", "99").replaceAll("34", "09"));
                        newSp.append("@");
                        newSp.append(bf[2]);
                        if(k != cd.length -1) {
                            newSp.append("/");
                        }
                    }
                } else {
                    codes[n] = codes[n].replaceAll("\\:", "@");
                    if(playType.equals(LotteryConstants.JCWF_PREFIX_RFSF) || playType.equals(LotteryConstants.JCWF_PREFIX_DXF)) {
                        String[] co = PluginUtil.splitter(codes[n], "=");
                        newSp.append(co[0] + "=");
                        String[] cd = PluginUtil.splitter(co[1], "/");
                        for (int k = 0; k < cd.length; k++) {
                            String[] bfs = PluginUtil.splitter(cd[k], "@");
                            if (bfs.length != 2) {
                                continue;
                            }
                            newSp.append(bfs[0].substring(0, 1).replaceAll("1", "3").replaceAll("2", "0"));
                            newSp.append("&");
                            newSp.append(bfs[0].substring(2));
                            newSp.append("@");
                            newSp.append(bfs[1]);
                            if (k != cd.length - 1) {
                                newSp.append("/");
                            }
                        }
                    } else if(playType.equals(LotteryConstants.JCWF_PREFIX_SFC)) {
                        String[] co = PluginUtil.splitter(codes[n], "=");
                        newSp.append(co[0]+"=");
                        String[] cd = PluginUtil.splitter(co[1], "/");
                        for(int k=0; k< cd.length; k++) {
                            String[] sfc = PluginUtil.splitter(cd[k], "@");
                            if(sfc.length != 2) {
                                continue;
                            }
                            newSp.append((sfc[0]).replaceAll("01", "11")
                                    .replaceAll("02", "12")
                                    .replaceAll("03", "13")
                                    .replaceAll("04", "14")
                                    .replaceAll("05", "15")
                                    .replaceAll("06", "16")
                                    .replaceAll("51", "01")
                                    .replaceAll("52", "02")
                                    .replaceAll("53", "03")
                                    .replaceAll("54", "04")
                                    .replaceAll("55", "05")
                                    .replaceAll("56", "06"));
                            newSp.append("@");
                            newSp.append(sfc[1]);
                            if(k != cd.length -1) {
                                newSp.append("/");
                            }
                        }
                    } else if(playType.equals(LotteryConstants.JCWF_PREFIX_SF)) {
                        String[] co = PluginUtil.splitter(codes[n], "=");
                        newSp.append(co[0]+"=");
                        String[] cd = PluginUtil.splitter(co[1], "/");
                        for(int k=0; k< cd.length; k++) {
                            String[] bfs = PluginUtil.splitter(cd[k], "@");
                            if(bfs.length != 2) {
                                continue;
                            }
                            newSp.append((bfs[0]).replaceAll("1", "3"));
                            newSp.append("@");
                            newSp.append(bfs[1]);
                            if(k != cd.length -1) {
                                newSp.append("/");
                            }
                        }
                    } else {
                        newSp.append(codes[n]);
                    }
                }
                if(n != codes.length -1) {
                    newSp.append(",");
                }
            }
            codeSp = newSp.toString();
            String[] result = PluginUtil.splitter(codeSp, ",");
            codeSp = "";
            for(int r=0; r < result.length; r++) {
                String[] py = PluginUtil.splitter(result[r], "#");
                String[] cs = PluginUtil.splitter(py[0], "=");
                codeSp += cs[0] + "->" + JiMiTicketUtil.getVoteLotteryTypeMap(py[1]) + "=" + cs[1];
                if(r != result.length -1) {
                    codeSp += ",";
                }
            }
        }
        if(ticket.getPlayTypeId().equals(LotteryConstants.GJ) || ticket.getPlayTypeId().equals(LotteryConstants.GYJ)) {
            StringBuffer newSp = new StringBuffer();
            newSp.append("18001");
            newSp.append("=");
            String[] codes = PluginUtil.splitter(PluginUtil.splitter(codeSp, "=")[1], "/");
            for(int n=0; n< codes.length; n++) {
                String[] co = PluginUtil.splitter(codes[n], "@");
                newSp.append(StringUtil.parseInt(co[0]));
                newSp.append("@");
                newSp.append(co[1]);
                if(n != codes.length -1) {
                    newSp.append("/");
                }
            }
            codeSp = newSp.toString();
        }
        return codeSp;
    }
}
