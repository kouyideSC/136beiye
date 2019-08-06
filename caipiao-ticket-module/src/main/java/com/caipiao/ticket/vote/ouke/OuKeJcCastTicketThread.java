package com.caipiao.ticket.vote.ouke;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.OuKeTicketUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 欧克提票线程执行类
 * Created by kouyi on 2018/11/03.
 */
public class OuKeJcCastTicketThread implements Runnable {
    private List<CodeInfo> tickets;
    private VoteVo vote;
    private Logger logger;
    private TicketService ticketService;
    private CountDownLatch countDownLatch;

    public OuKeJcCastTicketThread(List<CodeInfo> tickets, VoteVo vote, TicketService ticketService, Logger logger, CountDownLatch countDownLatch) {
        this.tickets = tickets;
        this.logger = logger;
        this.vote = vote;
        this.ticketService = ticketService;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            JSONArray array = new JSONArray();
            for (CodeInfo cb : tickets) {
                JSONObject body = new JSONObject();
                body.put("matchContent", cb.getArray());
                body.put("multiple", cb.getMultiple());
                body.put("orderStatus", "ING_ENTRUST");
                body.put("orderTime", DateUtil.dateDefaultFormat(new Date()));
                body.put("passItem", "");
                body.put("passMode", "");
                body.put("passType", cb.getPass());
                body.put("playType", cb.getPlayType());
                body.put("schemeCost", CalculationUtils.rdo(cb.getMoney()));
                body.put("ticketId", cb.getOrderId());
                body.put("units", cb.getZhuShu());
                array.add(body);
            }
            Map<String, String> request = OuKeTicketUtil.getRequestContent(vote, array);
            logger.info("[欧克竞彩提票]-> 请求报文体如下\n" + request.toString());
            String response = HttpClientUtil.callHttpPost_Map(vote.getApiUrl()+OuKeTicketUtil.Cast_Order, request);
            if(StringUtil.isEmpty(response)) {
                logger.error("[欧克竞彩提票]-> 返回响应为空");
            }
            JSONObject responseJson = JSONObject.fromObject(response);
            logger.info("[欧克竞彩提票]-> 返回响应体如下\n" + responseJson.toString());
            String resultCode = responseJson.getString("resultCode");
            if(resultCode.equals("ORDER_MD5_ERROR")) {
                logger.error("[欧克竞彩提票]-> 提票失败 [MD5签名验证失败]");
                return;
            }
            JSONArray arrays = responseJson.getJSONArray("message");
            for(int x = 0; x < arrays.size(); x++) {
                JSONObject rbody = arrays.getJSONObject(x);
                String code = rbody.getString("result");
                String orderId = rbody.getString("ticketId");
                if("SUCCESS".equals(code)) {
                    int row = ticketService.updateCastTicketStatus(orderId, SchemeConstants.TICKET_STATUS_CAST,
                            SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_CAST), "");
                    if(row > 0) {
                        logger.info("[欧克竞彩提票]-> 提票成功 网站票号=" + orderId);
                    } else {
                        logger.error("[欧克竞彩提票]-> 提票失败 [更新数据库失败] 网站票号=" + orderId);
                    }
                } else {
                    String desc = "错误编号[" + code + "] 错误描述[" + OuKeTicketUtil.getResultCode(code) + "]";
                    ticketService.updateCastTicketStatus(orderId, SchemeConstants.TICKET_STATUS_FAIL, desc, "");
                    logger.error("[欧克竞彩提票]-> 提票失败 " + desc + " 网站票号=" + orderId);
                }
            }
        } catch (Exception e) {
            logger.error("[欧克竞彩提票]-> 提票异常", e);
        } finally {
            if(null != countDownLatch){
                countDownLatch.countDown();
            }
        }
    }
}
