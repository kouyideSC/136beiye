package com.caipiao.ticket.vote.jimi;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 吉米提票线程执行类
 * Created by kouyi on 2017/12/13.
 */
public class JiMiSzcCastTicketThread implements Runnable {
    private List<CodeInfo> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public JiMiSzcCastTicketThread(List<CodeInfo> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
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
            for (CodeInfo cb : tickets) {
                body.append(cb.getOrderId());
                body.append("#");
                body.append(cb.getCode());
                body.append("#");
                body.append(cb.getZhuShu());
                body.append("#");
                body.append(cb.getMultiple());
                body.append("#");
                body.append(cb.getMoney());
                body.append("|");
            }
            String strBody = body.toString().substring(0, body.toString().length()-1);
            logger.info("[吉米数字彩提票]-> 请求报文体如下\n" + strBody);
            JXmlWapper xml = JiMiTicketUtil.parseUrl(JiMiTicketUtil.Cast_Order, new String[]{
                    JiMiTicketUtil.getLotteryTypeMap(vote.getPlayType()), vote.getPeriod(), strBody}, vote);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[吉米数字彩提票]-> 返回响应为空");
            }
            logger.info("[吉米数字彩提票]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
            int count = xml.countXmlNodes("body.tickets.ticket");
            for(int x = 0; x < count; x++) {
                String code = xml.getStringValue("body.tickets.ticket[" + x + "].errorCode");
                String orderId = xml.getStringValue("body.tickets.ticket[" + x + "].ordersID");
                if("0".equals(code)) {
                    int row = ticketService.updateCastTicketStatus(orderId, SchemeConstants.TICKET_STATUS_CAST,
                            SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_CAST), "");
                    if(row > 0) {
                        logger.info("[吉米数字彩提票]-> 提票成功 网站票号=" + orderId);
                    } else {
                        logger.error("[吉米数字彩提票]-> 提票失败 [更新数据库失败] 网站票号=" + orderId);
                    }
                } else {
                    String desc = "错误编号[" + code + "] 错误描述[" + JiMiTicketUtil.getResultCode(code) + "]";
                    ticketService.updateCastTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, "");
                    logger.error("[吉米数字彩提票]-> 提票失败 " + desc + " 网站票号=" + orderId);
                }
            }
        } catch (Exception e) {
            logger.error("[吉米数字彩提票]-> 提票异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }
}
