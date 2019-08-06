package com.caipiao.ticket.vote.huayang;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
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
public class HuaYangSzcQueryTicketThread implements Runnable {
    List<SchemeTicket> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public HuaYangSzcQueryTicketThread(List<SchemeTicket> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
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
                body.append("<element><id>");
                body.append(cb.getTicketId());
                body.append("</id></element>");
            }

            String request = HuaYangTicketUtil.getRequestContent(HuaYangTicketUtil.Cast_Order_Query, body.toString(), vote);
            logger.info("[华阳数字彩出票查询]-> 请求报文体如下\n" + request);
            JXmlWapper xml = JXmlWapper.parseUrl(vote.getApiUrl(), request, "UTF-8", 30);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[华阳数字彩出票查询]-> 返回响应为空");
            }
            logger.info("[华阳数字彩出票查询]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
            String resultCode = xml.getStringValue("body.oelement.errorcode");
            if(resultCode.equals("0")) {
                int count = xml.countXmlNodes("body.elements.element");
                for (int x = 0; x < count; x++) {
                    String code = xml.getStringValue("body.elements.element[" + x + "].status");
                    String orderId = xml.getStringValue("body.elements.element[" + x + "].id");
                    if ("2".equals(code)) {
                        String voteTicketId = xml.getStringValue("body.elements.element[" + x + "].ticketid");
                        if (StringUtil.isNotEmpty(voteTicketId)) {
                            int row = ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_OUTED,
                                    SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_OUTED), voteTicketId, "");
                            if (row > 0) {
                                String drawNumber = xml.getStringValue("body.elements.element[" + x + "].lscode");
                                if(StringUtil.isNotEmpty(drawNumber)) {//大乐透乐善号码加奖
                                    ticketService.updateOutTicketDrawNumber(orderId, drawNumber);
                                }
                                logger.info("[华阳数字彩出票查询]-> 出票成功 网站票号=" + orderId);
                            } else {
                                logger.error("[华阳数字彩出票查询]-> 出票失败 [更新数据库失败] 网站票号=" + orderId);
                            }
                        }
                    } else if ("6".equals(code)) {
                        String desc = "错误编号[" + code + "] 错误描述[" + JiMiTicketUtil.getResultCode(code) + "]";
                        ticketService.updateOutTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, "", "");
                        logger.error("[华阳数字彩出票查询]-> 出票失败 " + desc + " 网站票号=" + orderId);
                    } else {
                        logger.info("[华阳数字彩出票查询]-> 出票中 网站票号=" + orderId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[华阳数字彩出票查询]-> 出票查询异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }

}
