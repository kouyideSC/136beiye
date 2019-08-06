package com.caipiao.ticket.vote.nuomi;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.encrypt.DESCoder;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 糯米兑奖线程执行类
 * Created by kouyi on 2017/12/14.
 */
public class NuoMiAwardTicketThread implements Runnable {
    List<SchemeTicket> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public NuoMiAwardTicketThread(List<SchemeTicket> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
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
            body.append("<orderlist>");
            for (SchemeTicket ticket : tickets) {
                body.append("<order><orderid>");
                body.append(ticket.getTicketId());
                body.append("</orderid></order>");
            }
            body.append("</orderlist>");

            String request = NuoMiTicketUtil.getRequestContent(vote, NuoMiTicketUtil.Query_Award, body.toString());
            logger.info("[糯米兑奖]-> 请求报文体如下\n" + body.toString());
            JXmlWapper xml = JXmlWapper.parseUrl(vote.getApiUrl(), request, "utf-8", 30);
            if (StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[糯米兑奖]-> 返回响应为空");
            }
            JXmlWapper bodyXml = JXmlWapper.parse(DESCoder.desDecrypt(xml.getStringValue("body"), vote.getKey()));
            logger.info("[糯米兑奖]-> 返回响应体如下\n" + bodyXml.toXmlString());
            int count = bodyXml.countXmlNodes("orderlist.order");
            for (int x = 0; x < count; x++) {
                String code = bodyXml.getStringValue("orderlist.order[" + x + "].errorcode");
                String orderId = bodyXml.getStringValue("orderlist.order[" + x + "].orderid");
                Double prize = bodyXml.getDoubleValue("orderlist.order[" + x + "].amount");
                if (("3".equals(code) || "4".equals(code)) && prize > 0) {
                    int row = ticketService.updateAwardTicketStatus(orderId, SchemeConstants.TICKET_STATUS_AWARD,
                            SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_AWARD), prize, prize);
                    if (row > 0) {
                        logger.info("[糯米兑奖]-> 兑奖成功 出票商返回中奖金额=" + prize + " 网站票号=" + orderId);
                    } else {
                        logger.error("[糯米兑奖]-> 兑奖失败 [更新数据库失败] 网站票号=" + orderId);
                    }
                } else if ("2".equals(code)) {
                    logger.info("[糯米兑奖]-> 兑奖成功 未中奖 网站票号=" + orderId);
                    ticketService.updateAwardTicketStatus(orderId, SchemeConstants.TICKET_STATUS_AWARD,
                            SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_AWARD), 0D, 0D);
                } else if ("0".equals(code) || "1".equals(code) || "3".equals(code) || "5".equals(code)) {
                    logger.info("[糯米兑奖]-> 未开奖 网站票号=" + orderId);
                } else {
                    String desc = "错误编号[" + code + "] 错误描述[" + NuoMiTicketUtil.getResultCode(code) + "]";
                    ticketService.updateAwardTicketStatus(orderId, SchemeConstants.TICKET_STATUS_AWARDFAIL, desc, 0D, 0D);
                    logger.error("[糯米兑奖]-> 兑奖失败 " + desc + " 网站票号=" + orderId);
                }
            }
        } catch (Exception e) {
            logger.error("[糯米兑奖]-> 出票查询异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }

}
