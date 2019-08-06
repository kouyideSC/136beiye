package com.caipiao.ticket.vote.huayang;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.HuaYangTicketUtil;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 华阳提票线程执行类
 * Created by kouyi on 2017/12/13.
 */
public class HuaYangSzcCastTicketThread implements Runnable {
    private List<CodeInfo> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public HuaYangSzcCastTicketThread(List<CodeInfo> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
        this.tickets = tickets;
        this.logger = logger;
        this.vote = vote;
        this.ticketService = ticketService;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            boolean isK3 = false;
            StringBuffer body = new StringBuffer();
            for (CodeInfo cb : tickets) {
                body.append("<element><username>");
                body.append(vote.getVoteId());
                body.append("</username><ticketuser>");
                body.append(vote.getVoteId());
                body.append("</ticketuser><identify>510723198709038475</identify><phone></phone><email></email><id>");
                body.append(cb.getOrderId());
                body.append("</id><lotteryid>");
                body.append(cb.getLotteryId());
                body.append("</lotteryid><issue>");
                body.append(vote.getPeriod());
                body.append("</issue><childtype>");
                body.append(cb.getPlayType());
                body.append("</childtype><saletype>");
                body.append(cb.getSaleCode());
                body.append("</saletype><lotterycode>");
                body.append(cb.getCode());
                body.append("</lotterycode><appnumbers>");
                body.append(cb.getMultiple());
                body.append("</appnumbers><lotterynumber>");
                body.append(cb.getZhuShu());
                body.append("</lotterynumber><lotteryvalue>");
                body.append(cb.getMoney()*100);
                body.append("</lotteryvalue></element>");
                if(!isK3 && (cb.getLotteryId().equals("126") || cb.getLotteryId().equals("123"))) {
                    isK3 = true;
                }
            }
            String request = HuaYangTicketUtil.getRequestContent(HuaYangTicketUtil.Cast_Number_Order, body.toString(), vote);
            logger.info("[华阳数字彩提票]-> 请求报文体如下\n" + request);

            JXmlWapper xml = HuaYangTicketUtil.parseUrl(vote.getApiUrl(), request, isK3);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[华阳数字彩提票]-> 返回响应为空");
            }
            logger.info("[华阳数字彩提票]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
            String resultCode = xml.getStringValue("body.oelement.errorcode");
            if(resultCode.equals("0")) {
                int count = xml.countXmlNodes("body.elements.element");
                for(int x = 0; x < count; x++) {
                    String code = xml.getStringValue("body.elements.element[" + x + "].errorcode");
                    String orderId = xml.getStringValue("body.elements.element[" + x + "].id");
                    String voteTicketId = xml.getStringValue("body.elements.element[" + x + "].ltappid");
                    if("0".equals(code)) {
                        int row = ticketService.updateCastTicketStatus(orderId, SchemeConstants.TICKET_STATUS_CAST,
                                SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_CAST), voteTicketId);
                        if(row > 0) {
                            logger.info("[华阳数字彩提票]-> 提票成功 网站票号=" + orderId);
                        } else {
                            logger.error("[华阳数字彩提票]-> 提票失败 [更新数据库失败] 网站票号=" + orderId);
                        }
                    } else {
                        String desc = "错误编号[" + code + "] 错误描述[" + HuaYangTicketUtil.getResultCode(code) + "]";
                        ticketService.updateCastTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, "");
                        logger.error("[华阳数字彩提票]-> 提票失败 " + desc + " 网站票号=" + orderId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[华阳数字彩提票]-> 提票异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }
}
