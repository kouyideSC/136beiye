package com.caipiao.ticket.vote.huaying;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.encrypt.DESCoder;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.HuaYingTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 华盈提票线程执行类
 * Created by kouyi on 2017/12/13.
 */
public class HuaYingJcCastTicketThread implements Runnable {
    private List<CodeInfo> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public HuaYingJcCastTicketThread(List<CodeInfo> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
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
            for (CodeInfo cb : tickets) {
                body.append("<order><lotterytype>");
                body.append(cb.getSaleCode());
                body.append("</lotterytype><phase>");
                body.append(DateUtil.dateFormat(new Date(), DateUtil.DEFAULT_DATE1));
                body.append("</phase><orderid>");
                body.append(cb.getOrderId());
                body.append("</orderid><playtype>");
                body.append(cb.getPlayType());
                body.append("</playtype><betcode>");
                body.append(cb.getCode());
                body.append("</betcode><multiple>");
                body.append(cb.getMultiple());
                body.append("</multiple><amount>");
                body.append(cb.getMoney());
                body.append("</amount><add>0</add><endtime></endtime></order>");
            }
            body.append("</orderlist>");

            String request = HuaYingTicketUtil.getRequestContent(vote, HuaYingTicketUtil.Cast_Order, body.toString());
            logger.info("[华盈竞彩提票]-> 请求报文体如下\n" + body.toString());
            JXmlWapper xml = JXmlWapper.parseUrl(vote.getApiUrl(), request, "utf-8", 30);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[华盈竞彩提票]-> 返回响应为空");
            }
            JXmlWapper bodyXml = JXmlWapper.parse(DESCoder.desDecrypt(xml.getStringValue("body"), vote.getKey()));
            logger.info("[华盈竞彩提票]-> 返回响应体如下\n" + bodyXml.toXmlString());
            int count = bodyXml.countXmlNodes("orderlist.order");
            for(int x = 0; x < count; x++) {
                String code = bodyXml.getStringValue("orderlist.order[" + x + "].errorcode");
                String orderId = bodyXml.getStringValue("orderlist.order[" + x + "].orderid");
                String voteTicketId = bodyXml.getStringValue("orderlist.order[" + x + "].sysid");
                if("0".equals(code)) {
                    int row = ticketService.updateCastTicketStatus(orderId, SchemeConstants.TICKET_STATUS_CAST,
                            SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_CAST), voteTicketId);
                    if(row > 0) {
                        logger.info("[华盈竞彩提票]-> 提票成功 网站票号=" + orderId);
                    } else {
                        logger.error("[华盈竞彩提票]-> 提票失败 [更新数据库失败] 网站票号=" + orderId);
                    }
                } else {
                    String desc = "错误编号[" + code + "] 错误描述[" + HuaYingTicketUtil.getResultCode(code) + "]";
                    ticketService.updateCastTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, voteTicketId);
                    logger.error("[华盈竞彩提票]-> 提票失败 " + desc + " 网站票号=" + orderId);
                }
            }
        } catch (Exception e) {
            logger.error("[华盈竞彩提票]-> 提票异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }
}
