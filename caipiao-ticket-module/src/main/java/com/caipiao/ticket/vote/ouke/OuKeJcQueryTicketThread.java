package com.caipiao.ticket.vote.ouke;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.encrypt.DESCoder;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.caipiao.ticket.util.OuKeTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 欧克出票查询线程执行类
 * Created by kouyi on 2017/12/14.
 */
public class OuKeJcQueryTicketThread implements Runnable {
    List<SchemeTicket> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public OuKeJcQueryTicketThread(List<SchemeTicket> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
        this.tickets = tickets;
        this.logger = logger;
        this.vote = vote;
        this.ticketService = ticketService;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            Map<String, SchemeTicket> ticketMap = new HashMap<>();
            StringBuffer body = new StringBuffer();
            for (int m = 0; m < tickets.size(); m++) {
                SchemeTicket ticket = tickets.get(m);
                ticketMap.put(ticket.getTicketId(), ticket);
                body.append(ticket.getTicketId());
                if(m != tickets.size()-1) {
                    body.append(",");
                }
            }

            Map<String, String> request = OuKeTicketUtil.getRequestContentQuery(OuKeTicketUtil.Cast_Order_Query, vote, body.toString());
            logger.info("[欧克竞彩出票查询]-> 请求报文体如下\n" + request.toString());
            String response = HttpClientUtil.callHttpPost_Map(vote.getApiUrl()+OuKeTicketUtil.Cast_Order_Query, request);
            if(StringUtil.isEmpty(response)) {
                logger.error("[欧克竞彩出票查询]-> 返回响应为空");
            }
            JSONObject responseJson = JSONObject.fromObject(response);
            logger.info("[欧克竞彩出票查询]-> 返回响应体如下\n" + responseJson.toString());
            String resultCode = responseJson.getString("resultCode");
            if(resultCode.equals("ORDER_MD5_ERROR")) {
                logger.error("[欧克竞彩出票查询]-> 查询失败 [MD5签名验证失败]");
                return;
            }

            JSONArray arrays = responseJson.getJSONArray("message");
            for(int x = 0; x < arrays.size(); x++) {
                JSONObject rbody = arrays.getJSONObject(x);
                String code = rbody.getString("result");
                String orderId = rbody.getString("ticketId");
                if ("SUC_TICKET".equals(code)) {
                    String voteTicketId = rbody.getString("orderNumber");
                    JSONArray spArray = rbody.getJSONObject("odds").getJSONObject("spMap").getJSONArray("matchNumber");
                    if(StringUtil.isEmpty(spArray)) {
                        logger.error("[欧克竞彩出票查询]-> 赔率参数[matchNumber]为空 网站票号=" + orderId);
                        continue;
                    }
                    String codeSp = formatCodeSp(spArray, ticketMap.get(orderId));
                    if(StringUtil.isNotEmpty(codeSp)) {
                        int row = ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_OUTED,
                                SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_OUTED), voteTicketId, codeSp);
                        if (row > 0) {
                            logger.info("[欧克竞彩出票查询]-> 出票成功 网站票号=" + orderId);
                        } else {
                            logger.error("[欧克竞彩出票查询]-> 出票失败 [更新数据库失败] 网站票号=" + orderId);
                        }
                    }
                }
                else if("SUC_ENTRUST".equals(code) || "ING_ENTRUST".equals(code)) {
                    logger.info("[欧克竞彩出票查询]-> 出票中 网站票号=" + orderId);
                }
                else {
                    String desc = "错误编号[" + code + "] 错误描述[" + OuKeTicketUtil.getResultCode(code) + "]";
                    ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, "", "");
                    logger.error("[欧克竞彩出票查询]-> 出票失败 " + desc + " 网站票号=" + orderId);
                }
            }
        } catch (Exception e) {
            logger.error("[欧克竞彩出票查询]-> 出票查询异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }

    /**
     * 出票SP转换
     * @param codeSp
     * @param ticket
     * @return
     */
    private static String formatCodeSp(JSONArray codeSp, SchemeTicket ticket) {
        Map<String, Map<String, String>> infoMap = getResponseSpValue(codeSp);
        if(StringUtil.isEmpty(ticket) || StringUtil.isEmpty(infoMap)) {
            return null;
        }
        String codes = ticket.getCodes().replaceAll("\\-", "").replaceAll("\\:", "").replaceAll("\\>", "->");
        StringBuffer buffer = new StringBuffer();
        String[] splitCodes = codes.split("\\|");
        if(ticket.getPlayTypeId().equals(LotteryConstants.JCZQ))
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
        else if(ticket.getPlayTypeId().equals(LotteryConstants.JCLQ))
        {
            //20181103301->SF=0/3,20181103302->RFSF=0,20181103303->SF=0
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
        else if(ticket.getPlayTypeId().equals(LotteryConstants.JCZQSPF) || ticket.getPlayTypeId().equals(LotteryConstants.JCZQRQSPF)
                || ticket.getPlayTypeId().equals(LotteryConstants.JCZQCBF) || ticket.getPlayTypeId().equals(LotteryConstants.JCZQBQC)
                || ticket.getPlayTypeId().equals(LotteryConstants.JCZQJQS)) { //足球其他玩法
            //20181103023=3/1,20181103024=0
            String[] content = splitCodes[1].split("\\,");
            for (int n = 0; n < content.length; n++) {
                String[] xcn = content[n].split("\\=");
                if (infoMap.containsKey(xcn[0])) {
                    Map<String, String> choose = infoMap.get(xcn[0]);
                    buffer.append(xcn[0]).append("=");
                    String[] cn = xcn[1].split("\\/");
                    for (int p = 0; p < cn.length; p++) {
                        String sp = choose.get(cn[p]);
                        if (StringUtil.isEmpty(sp)) {
                            return null;
                        }
                        buffer.append(cn[p] + "@" + sp);
                        if (p != cn.length - 1) {
                            buffer.append("/");
                        }
                    }
                } else {
                    return null;
                }
                if (n != content.length - 1) {
                    buffer.append(",");
                }
            }
        } else if(ticket.getPlayTypeId().equals(LotteryConstants.JCLQSF) || ticket.getPlayTypeId().equals(LotteryConstants.JCLQSFC)
                || ticket.getPlayTypeId().equals(LotteryConstants.JCLQDXF) || ticket.getPlayTypeId().equals(LotteryConstants.JCLQRFSF))
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
        } else {

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
                if(valueJson.get(key) != null)
                {
                    String value = valueJson.get(key).toString();
                    valueMap.put(key, value);
                }
            }
            infoMap.put(matchCode, valueMap);
        }
        return infoMap;
    }
}
