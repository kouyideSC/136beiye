package com.caipiao.ticket.vote.jimi;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.util.JiMiTicketUtil;
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
public class JiMiSzcQueryTicketThread implements Runnable {
    List<SchemeTicket> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public JiMiSzcQueryTicketThread(List<SchemeTicket> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
        this.tickets = tickets;
        this.logger = logger;
        this.vote = vote;
        this.ticketService = ticketService;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            StringBuffer body = new StringBuffer();
            for (SchemeTicket cb : tickets) {
                body.append(cb.getTicketId());
                body.append(",");
            }

            String strBody = body.toString().substring(0, body.toString().length()-1);
            logger.info("[吉米数字彩出票查询]-> 请求报文体如下\n" + strBody.toString());
            JXmlWapper xml = JiMiTicketUtil.parseUrl(JiMiTicketUtil.Cast_Order_Query, new String[]{strBody.toString()}, vote);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[吉米数字彩出票查询]-> 返回响应为空");
            }
            logger.info("[吉米数字彩出票查询]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
            int count = xml.countXmlNodes("body.tickets.ticket");
            for(int x = 0; x < count; x++) {
                String code = xml.getStringValue("body.tickets.ticket["+x+"].status");
                String orderId = xml.getStringValue("body.tickets.ticket["+x+"].ordersID");
                if("2".equals(code)) {
                    String voteTicketId = xml.getStringValue("body.tickets.ticket["+x+"].ticketId");
                    if (StringUtil.isNotEmpty(voteTicketId)) {
                        int row = ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_OUTED,
                                SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_OUTED), voteTicketId, "");
                        if (row > 0) {
                            logger.info("[吉米数字彩出票查询]-> 出票成功 网站票号=" + orderId);
                        } else {
                            logger.error("[吉米数字彩出票查询]-> 出票失败 [更新数据库失败] 网站票号=" + orderId);
                        }
                    }
                }
                else if("-2".equals(code)) {
                    String desc = "错误编号[" + code + "] 错误描述[" + JiMiTicketUtil.getResultCode(code) + "]";
                    ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, "", "");
                    logger.error("[吉米数字彩出票查询]-> 出票失败 " + desc + " 网站票号=" + orderId);
                }
                else {
                    logger.info("[吉米数字彩出票查询]-> 出票中 网站票号=" + orderId);
                }
            }
        } catch (Exception e) {
            logger.error("[吉米数字彩出票查询]-> 出票查询异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }

}
