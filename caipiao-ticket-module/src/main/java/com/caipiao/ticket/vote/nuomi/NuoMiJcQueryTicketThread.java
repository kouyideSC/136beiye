package com.caipiao.ticket.vote.nuomi;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.encrypt.DESCoder;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.bean.SpInfo;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 糯米出票查询线程执行类
 * Created by kouyi on 2017/12/14.
 */
public class NuoMiJcQueryTicketThread implements Runnable {
    List<SchemeTicket> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public NuoMiJcQueryTicketThread(List<SchemeTicket> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
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
            body.append("<orderlist>");
            for (SchemeTicket ticket : tickets) {
                ticketMap.put(ticket.getTicketId(), ticket);
                body.append("<order><orderid>");
                body.append(ticket.getTicketId());
                body.append("</orderid></order>");
            }
            body.append("</orderlist>");

            String request = NuoMiTicketUtil.getRequestContent(vote, NuoMiTicketUtil.Cast_Order_Query, body.toString());
            logger.info("[糯米竞彩出票查询]-> 请求报文体如下\n" + body.toString());
            JXmlWapper xml = JXmlWapper.parseUrl(vote.getApiUrl(), request, "utf-8", 30);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[糯米竞彩出票查询]-> 返回响应为空");
            }
            JXmlWapper bodyXml = JXmlWapper.parse(DESCoder.desDecrypt(xml.getStringValue("body"), vote.getKey()));
            logger.info("[糯米竞彩出票查询]-> 返回响应体如下\n" + bodyXml.toXmlString());
            int count = bodyXml.countXmlNodes("orderlist.order");
            for(int x = 0; x < count; x++) {
                String code = bodyXml.getStringValue("orderlist.order["+x+"].errorcode");
                String orderId = bodyXml.getStringValue("orderlist.order["+x+"].orderid");
                if("0".equals(code)) {
                    int ticketCount = bodyXml.countXmlNodes("orderlist.order["+x+"].ticketlist");
                    for(int c = 0; c < ticketCount; c++) {
                        String voteTicketId = bodyXml.getStringValue("orderlist.order["+x+"].ticketlist.ticket["+c+"].@ticketid");
                        String codeSp = bodyXml.getStringValue("orderlist.order["+x+"].ticketlist.ticket["+c+"].@sp");
                        codeSp = formatCodeSp(codeSp, ticketMap.get(orderId));
                        if(StringUtil.isNotEmpty(codeSp)) {
                            int row = ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_OUTED,
                                    SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_OUTED), voteTicketId, codeSp);
                            if (row > 0) {
                                logger.info("[糯米竞彩出票查询]-> 出票成功 网站票号=" + orderId);
                            } else {
                                logger.error("[糯米竞彩出票查询]-> 出票失败 [更新数据库失败] 网站票号=" + orderId);
                            }
                        }
                    }
                }
                else if("1".equals(code)) {
                    logger.info("[糯米竞彩出票查询]-> 出票中 网站票号=" + orderId);
                }
                else {
                    String desc = "错误编号[" + code + "] 错误描述[" + NuoMiTicketUtil.getResultCode(code) + "]";
                    ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, "", "");
                    logger.error("[糯米竞彩出票查询]-> 出票失败 " + desc + " 网站票号=" + orderId);
                }
            }
        } catch (Exception e) {
            logger.error("[糯米竞彩出票查询]-> 出票查询异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }

    /**
     * 格式化出票sp
     * @param codeSp
     * @param ticket
     * @return
     */
    public String formatCodeSp(String codeSp, SchemeTicket ticket) throws Exception {
        if(StringUtil.isEmpty(codeSp) || StringUtil.isEmpty(ticket)) {
            return null;
        }

        logger.info("[糯米竞彩出票查询]-> 格式化出票商返回出票SP=" + codeSp + " 票号=" + ticket.getTicketId());
        codeSp = codeSp.replaceAll("\\(", "=").replaceAll("\\)", "").replaceAll("\\,", "/").replaceAll("\\_", "@")
                .replaceAll("\\*", "->").replaceAll("\\|", ",").replaceAll("\\+","");
        if(ticket.getPlayTypeId().equals(LotteryConstants.JCLQRFSF) || ticket.getPlayTypeId().equals(LotteryConstants.JCLQDXF)) {//让分胜负|大小分
            StringBuffer newSp = new StringBuffer();
            String[] codes = PluginUtil.splitter(codeSp, ",");
            for(int n=0; n< codes.length; n++) {
                String[] co = PluginUtil.splitter(codes[n], "=");
                newSp.append(co[0]+"=");
                String[] cd = PluginUtil.splitter(co[1], ":");
                String[] chs = PluginUtil.splitter(cd[1], "/");
                for(int c = 0; c < chs.length; c++) {
                    if (ticket.getPlayTypeId().equals(LotteryConstants.JCLQDXF)) {
                        newSp.append(chs[c].substring(0, 1).replaceAll("1", "3").replaceAll("2", "0"));
                    } else {
                        newSp.append(chs[c].substring(0, 1));
                    }
                    newSp.append("&");
                    newSp.append(cd[0]);
                    newSp.append(chs[c].substring(1));
                    if (c != chs.length - 1) {
                        newSp.append("/");
                    }
                }
                if(n != codes.length - 1) {
                    newSp.append(",");
                }
            }
            codeSp = newSp.toString();
        } else if(ticket.getPlayTypeId().equals(LotteryConstants.JCZQ) || ticket.getPlayTypeId().equals(LotteryConstants.JCLQ)) {
            for(Map.Entry<String, String> voteLotterys : NuoMiTicketUtil.voteLotteryPlayType.entrySet()) {
                codeSp = codeSp.replaceAll("->"+voteLotterys.getKey(), "->"+voteLotterys.getValue());
            }
            if(ticket.getPlayTypeId().equals(LotteryConstants.JCLQ)) {//混投
                StringBuffer newSp = new StringBuffer();
                String[] codes = PluginUtil.splitter(codeSp, ",");
                for(int n=0; n< codes.length; n++) {
                    String[] co = PluginUtil.splitter(codes[n], "=");
                    if (co[0].split("\\->")[1].equals(LotteryConstants.JCWF_PREFIX_DXF) || co[0].split("\\->")[1].equals(LotteryConstants.JCWF_PREFIX_RFSF)) {
                        newSp.append(co[0]+"=");
                        String[] cd = PluginUtil.splitter(co[1], ":");
                        String[] chs = PluginUtil.splitter(cd[1], "/");
                        for(int c = 0; c < chs.length; c++) {
                            if (co[0].split("\\->")[1].equals(LotteryConstants.JCWF_PREFIX_DXF)) {
                                newSp.append(chs[c].substring(0, 1).replaceAll("1", "3").replaceAll("2", "0"));
                            } else {
                                newSp.append(chs[c].substring(0, 1));
                            }
                            newSp.append("&");
                            newSp.append(cd[0]);
                            newSp.append(chs[c].substring(1));
                            if (c != chs.length - 1) {
                                newSp.append("/");
                            }
                        }
                    } else {
                        newSp.append(codes[n]);
                    }

                    if(n != codes.length - 1) {
                        newSp.append(",");
                    }
                }
                codeSp = newSp.toString();
            }
        }
        return codeSp;
    }
}
