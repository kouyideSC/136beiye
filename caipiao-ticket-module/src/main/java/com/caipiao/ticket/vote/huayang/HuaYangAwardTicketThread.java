package com.caipiao.ticket.vote.huayang;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.util.HuaYangTicketUtil;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 华阳兑奖线程执行类
 * Created by kouyi on 2017/12/20.
 */
public class HuaYangAwardTicketThread implements Runnable {
    List<SchemeTicket> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public HuaYangAwardTicketThread(List<SchemeTicket> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
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

            String request = HuaYangTicketUtil.getRequestContent(HuaYangTicketUtil.Query_Award, body.toString(), vote);
            logger.info("[华阳兑奖]-> 请求报文体如下\n" + request);
            JXmlWapper xml = JXmlWapper.parseUrl(vote.getApiUrl(), request, "UTF-8", 30);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[华阳兑奖]-> 返回响应为空");
            }
            logger.info("[华阳兑奖]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
            String resultCode = xml.getStringValue("body.oelement.errorcode");
            if(resultCode.equals("0")) {
                int count = xml.countXmlNodes("body.elements.element");
                for (int x = 0; x < count; x++) {
                    String code = xml.getStringValue("body.elements.element[" + x + "].status");
                    String orderId = xml.getStringValue("body.elements.element[" + x + "].id");
                    Double prize = xml.getDoubleValue("body.elements.element[" + x + "].prebonusvalue")/100;
                    Double taxPrize = xml.getDoubleValue("body.elements.element[" + x + "].bonusvalue")/100;
                    if ("2".equals(code) && taxPrize > 0) {
                        int row = ticketService.updateAwardTicketStatus(orderId, SchemeConstants.TICKET_STATUS_AWARD,
                                SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_AWARD), prize, taxPrize);
                        if (row > 0) {
                            logger.info("[华阳兑奖]-> 兑奖成功 出票商返回中奖金额=" + prize + " 网站票号=" + orderId);
                        } else {
                            logger.error("[华阳兑奖]-> 兑奖失败 [更新数据库失败] 网站票号=" + orderId);
                        }
                    } else if ("1".equals(code)) {
                        logger.info("[华阳兑奖]-> 兑奖成功 未中奖 网站票号=" + orderId);
                        ticketService.updateAwardTicketStatus(orderId, SchemeConstants.TICKET_STATUS_AWARD,
                                SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_AWARD), 0D, 0D);
                    } else if ("0".equals(code) || "5".equals(code)) {
                        logger.info("[华阳兑奖]-> 未开奖 网站票号=" + orderId);
                    } else {
                        String desc = "错误编号[" + code + "] 错误描述[" + NuoMiTicketUtil.getResultCode(code) + "]";
                        ticketService.updateAwardTicketStatus(orderId, SchemeConstants.TICKET_STATUS_AWARDFAIL, desc, 0D, 0D);
                        logger.error("[华阳兑奖]-> 兑奖失败 " + desc + " 网站票号=" + orderId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[华阳兑奖]-> 出票查询异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }
}
