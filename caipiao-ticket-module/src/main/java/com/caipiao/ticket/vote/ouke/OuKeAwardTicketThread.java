package com.caipiao.ticket.vote.ouke;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.encrypt.DESCoder;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.caipiao.ticket.util.OuKeTicketUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 欧克兑奖线程执行类
 * Created by kouyi on 2017/12/14.
 */
public class OuKeAwardTicketThread implements Runnable {
    List<SchemeTicket> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public OuKeAwardTicketThread(List<SchemeTicket> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
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
            for (int m = 0; m < tickets.size(); m++) {
                SchemeTicket ticket = tickets.get(m);
                ticketMap.put(ticket.getTicketId(), ticket);
                body.append(ticket.getTicketId());
                if(m != tickets.size()-1) {
                    body.append(",");
                }
            }

            Map<String, String> request = OuKeTicketUtil.getRequestContentQuery(OuKeTicketUtil.Query_Award, vote, body.toString());
            logger.info("[欧克兑奖]-> 请求报文体如下\n" + request.toString());
            String response = HttpClientUtil.callHttpPost_Map(vote.getApiUrl()+OuKeTicketUtil.Query_Award, request);
            if(StringUtil.isEmpty(response)) {
                logger.error("[欧克兑奖]-> 返回响应为空");
            }
            JSONObject responseJson = JSONObject.fromObject(response);
            logger.info("[欧克兑奖]-> 返回响应体如下\n" + responseJson.toString());
            String resultCode = responseJson.getString("resultCode");
            if(resultCode.equals("ORDER_MD5_ERROR")) {
                logger.error("[欧克兑奖]-> 查询失败 [MD5签名验证失败]");
                return;
            }

            JSONArray arrays = responseJson.getJSONArray("message");
            for(int x = 0; x < arrays.size(); x++) {
                JSONObject rbody = arrays.getJSONObject(x);
                String code = rbody.getString("result");
                String orderId = rbody.getString("ticketId");
                if("DISTRIBUTE".equals(code) || "NOT_DISTRIBUTE".equals(code)) {
                    Double prize = rbody.getDouble("bonus");
                    Double prizeTax = rbody.getDouble("afterTaxBonus");
                    int row = ticketService.updateAwardTicketStatus(orderId, SchemeConstants.TICKET_STATUS_AWARD,
                            SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_AWARD), prize, prizeTax);
                    if (row > 0) {
                        logger.info("[欧克兑奖]-> 兑奖成功 出票商返回中奖金额=" + prize + " 网站票号=" + orderId);
                    } else {
                        logger.error("[欧克兑奖]-> 兑奖失败 [更新数据库失败] 网站票号=" + orderId);
                    }
                } else if ("NOT_WON".equals(code)) {
                    logger.info("[欧克兑奖]-> 兑奖成功 未中奖 网站票号=" + orderId);
                    ticketService.updateAwardTicketStatus(orderId, SchemeConstants.TICKET_STATUS_AWARD,
                            SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_AWARD), 0D, 0D);
                } else if ("NOT_DRAW".equals(code)) {
                    logger.info("[欧克兑奖]-> 未开奖 网站票号=" + orderId);
                } else {
                    String desc = "错误编号[" + code + "] 错误描述[" + OuKeTicketUtil.getResultCode(code) + "]";
                    ticketService.updateAwardTicketStatus(orderId, SchemeConstants.TICKET_STATUS_AWARDFAIL, desc, 0D, 0D);
                    logger.error("[欧克兑奖]-> 兑奖失败 " + desc + " 网站票号=" + orderId);
                }
            }
        } catch (Exception e) {
            logger.error("[欧克兑奖]-> 兑奖异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }

}
