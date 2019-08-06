package com.caipiao.ticket.vote.huayang;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.util.HuaYangTicketUtil;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 华阳出票查询线程执行类
 * Created by kouyi on 2017/12/14.
 */
public class HuaYangJcQueryTicketThread implements Runnable {
    List<SchemeTicket> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public HuaYangJcQueryTicketThread(List<SchemeTicket> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
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
            for (SchemeTicket cb : tickets) {
                ticketMap.put(cb.getTicketId(), cb);
                body.append("<element><id>");
                body.append(cb.getTicketId());
                body.append("</id></element>");
            }

            String request = HuaYangTicketUtil.getRequestContent(HuaYangTicketUtil.Cast_Order_Query, body.toString(), vote);
            logger.info("[华阳竞彩出票查询]-> 请求报文体如下\n" + request);
            JXmlWapper xml = JXmlWapper.parseUrl(vote.getApiUrl(), request, "UTF-8", 30);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[华阳竞彩出票查询]-> 返回响应为空");
            }
            logger.info("[华阳竞彩出票查询]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
            String resultCode = xml.getStringValue("body.oelement.errorcode");
            if(resultCode.equals("0")) {
                int count = xml.countXmlNodes("body.elements.element");
                for (int x = 0; x < count; x++) {
                    String code = xml.getStringValue("body.elements.element[" + x + "].status");
                    String orderId = xml.getStringValue("body.elements.element[" + x + "].id");
                    if ("2".equals(code)) {
                        String voteTicketId = xml.getStringValue("body.elements.element[" + x + "].ticketid");
                        String codeSp = xml.getStringValue("body.elements.element[" + x + "].spvalue");
                        codeSp = formatCodeSp(codeSp, ticketMap.get(orderId));
                        if (StringUtil.isNotEmpty(codeSp)) {
                            int row = ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_OUTED,
                                    SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_OUTED), voteTicketId, codeSp);
                            if (row > 0) {
                                logger.info("[华阳竞彩出票查询]-> 出票成功 网站票号=" + orderId);
                            } else {
                                logger.error("[华阳竞彩出票查询]-> 出票失败 [更新数据库失败] 网站票号=" + orderId);
                            }
                        }
                    } else if ("6".equals(code)) {
                        String desc = "错误编号[" + code + "] 错误描述[" + JiMiTicketUtil.getResultCode(code) + "]";
                        ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, "", "");
                        logger.error("[华阳竞彩出票查询]-> 出票失败 " + desc + " 网站票号=" + orderId);
                    } else {
                        logger.info("[华阳竞彩出票查询]-> 出票中 网站票号=" + orderId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[华阳竞彩出票查询]-> 出票查询异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }

    /**
     * 格式化出票sp
     * @param codeSp
     * @return
     */
    public String formatCodeSp(String codeSp, SchemeTicket ticket) throws Exception {
        if(StringUtil.isEmpty(codeSp) || StringUtil.isEmpty(ticket)) {
            return null;
        }
        codeSp = codeSp.replaceAll("\\(", "=").replaceAll("\\)","").replaceAll("\\,", "/").replaceAll("\\;", ",").replaceAll("\\_", "@");
        if(ticket.getPlayTypeId().equals(LotteryConstants.JCLQSF)) {
            codeSp = codeSp.replaceAll("\\-", "");
        }
        if(ticket.getPlayTypeId().equals(LotteryConstants.JCLQRFSF) || ticket.getPlayTypeId().equals(LotteryConstants.JCLQDXF)) {
            StringBuffer newSp = new StringBuffer();
            String[] codes = PluginUtil.splitter(codeSp, ",");
            for(int n=0; n< codes.length; n++) {
                String[] co = PluginUtil.splitter(codes[n], "=");
                newSp.append(co[0].replaceAll("\\-", "")+"=");
                String[] cd = PluginUtil.splitter(co[1], "/");
                for(int k=0; k< cd.length; k++) {
                    String[] cbf = PluginUtil.splitter(cd[k], "@");
                    if(cbf.length != 3) {
                        continue;
                    }
                    newSp.append(cbf[0].replaceAll("1", "3").replaceAll("2", "0"));
                    newSp.append("&");
                    newSp.append(cbf[1]);
                    newSp.append("@");
                    newSp.append(cbf[2]);
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
                newSp.append(co[0].replaceAll("\\-", "")+"=");
                String[] cd = PluginUtil.splitter(co[1], "/");
                for(int k=0; k< cd.length; k++) {
                    String[] bfs = PluginUtil.splitter(cd[k], "@");
                    if(bfs.length != 2) {
                        continue;
                    }
                    newSp.append(HuaYangTicketUtil.getSfcMap(bfs[0]));
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
                String[] wf = PluginUtil.splitter(codes[n], "^");
                String playType = HuaYangTicketUtil.getVoteLotteryTypeMap(wf[0]);
                if(playType.equals(LotteryConstants.JCWF_PREFIX_RFSF) || playType.equals(LotteryConstants.JCWF_PREFIX_DXF)) {
                    String[] co = PluginUtil.splitter(codes[n], "=");
                    newSp.append(co[0].replaceAll("\\-", "") + "=");
                    String[] cd = PluginUtil.splitter(co[1], "/");
                    for (int k = 0; k < cd.length; k++) {
                        String[] cbf = PluginUtil.splitter(cd[k], "@");
                        if(cbf.length != 3) {
                            continue;
                        }
                        newSp.append(cbf[0].replaceAll("1", "3").replaceAll("2", "0"));
                        newSp.append("&");
                        newSp.append(cbf[1]);
                        newSp.append("@");
                        newSp.append(cbf[2]);
                        if(k != cd.length -1) {
                            newSp.append("/");
                        }
                    }
                } else if(playType.equals(LotteryConstants.JCWF_PREFIX_SFC)) {
                    String[] co = PluginUtil.splitter(codes[n], "=");
                    newSp.append(co[0].replaceAll("\\-", "")+"=");
                    String[] cd = PluginUtil.splitter(co[1], "/");
                    for(int k=0; k< cd.length; k++) {
                        String[] sfc = PluginUtil.splitter(cd[k], "@");
                        if(sfc.length != 2) {
                            continue;
                        }
                        newSp.append(HuaYangTicketUtil.getSfcMap(sfc[0]));
                        newSp.append("@");
                        newSp.append(sfc[1]);
                        if(k != cd.length -1) {
                            newSp.append("/");
                        }
                    }
                } else {
                    codes[n] = codes[n].replaceAll("\\-", "");
                    newSp.append(codes[n]);
                }
                if(n != codes.length -1) {
                    newSp.append(",");
                }
            }
            codeSp = newSp.toString();
            String[] result = PluginUtil.splitter(codeSp, ",");
            codeSp = "";
            for(int r=0; r < result.length; r++) {
                String[] py = PluginUtil.splitter(result[r], "=");
                String[] cs = PluginUtil.splitter(py[0], "^");
                codeSp += cs[1] + "->" + HuaYangTicketUtil.getVoteLotteryTypeMap(cs[0]) + "=" + py[1];
                if(r != result.length -1) {
                    codeSp += ",";
                }
            }
        }
        if(LotteryUtils.isJczq(ticket.getPlayTypeId())) {
            String[] sps = PluginUtil.splitter(codeSp, ",");
            codeSp = "";
            for (int m = 0; m < sps.length; m++) {
                String[] co = PluginUtil.splitter(sps[m], "=");
                if(ticket.getPlayTypeId().equals(LotteryConstants.JCZQ)) {
                    codeSp += "20" + co[0] + "=" + co[1];//混投
                } else {
                    codeSp += "20" + co[0].replaceAll("\\-", "") + "=" + co[1];
                }
                if (m != sps.length - 1) {
                    codeSp += ",";
                }
            }
        }
        return codeSp;
    }

}
