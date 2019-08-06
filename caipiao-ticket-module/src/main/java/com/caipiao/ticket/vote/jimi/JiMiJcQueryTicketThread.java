package com.caipiao.ticket.vote.jimi;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.encrypt.DESCoder;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 吉米出票查询线程执行类
 * Created by kouyi on 2017/12/14.
 */
public class JiMiJcQueryTicketThread implements Runnable {
    List<SchemeTicket> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public JiMiJcQueryTicketThread(List<SchemeTicket> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
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
                body.append(cb.getTicketId());
                body.append(",");
            }

            String strBody = body.toString().substring(0, body.toString().length()-1);
            logger.info("[吉米竞彩出票查询]-> 请求报文体如下\n" + strBody.toString());
            JXmlWapper xml = JiMiTicketUtil.parseUrl(JiMiTicketUtil.Cast_Order_Query, new String[]{strBody.toString()}, vote);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[吉米竞彩出票查询]-> 返回响应为空");
            }
            logger.info("[吉米竞彩出票查询]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
            int count = xml.countXmlNodes("body.tickets.ticket");
            for(int x = 0; x < count; x++) {
                String code = xml.getStringValue("body.tickets.ticket["+x+"].status");
                String orderId = xml.getStringValue("body.tickets.ticket["+x+"].ordersID");
                if("2".equals(code)) {
                    String voteTicketId = xml.getStringValue("body.tickets.ticket["+x+"].ticketId");
                    String codeSp = xml.getStringValue("body.tickets.ticket["+x+"].odds");
                    codeSp = formatCodeSp(codeSp, ticketMap.get(orderId));
                    if (StringUtil.isNotEmpty(codeSp)) {
                        int row = ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_OUTED,
                                SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_OUTED), voteTicketId, codeSp);
                        if (row > 0) {
                            logger.info("[吉米竞彩出票查询]-> 出票成功 网站票号=" + orderId);
                        } else {
                            logger.error("[吉米竞彩出票查询]-> 出票失败 [更新数据库失败] 网站票号=" + orderId);
                        }
                    }
                }
                else if("-2".equals(code)) {
                    String desc = "错误编号[" + code + "] 错误描述[" + JiMiTicketUtil.getResultCode(code) + "]";
                    ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, "", "");
                    logger.error("[吉米竞彩出票查询]-> 出票失败 " + desc + " 网站票号=" + orderId);
                }
                else {
                    logger.info("[吉米竞彩出票查询]-> 出票中 网站票号=" + orderId);
                }
            }
        } catch (Exception e) {
            logger.error("[吉米竞彩出票查询]-> 出票查询异常", e);
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

        logger.info("[吉米竞彩出票查询]-> 格式化出票商返回出票SP=" + codeSp + " 票号=" + ticket.getTicketId());
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
            newSp.append("2018001");
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
