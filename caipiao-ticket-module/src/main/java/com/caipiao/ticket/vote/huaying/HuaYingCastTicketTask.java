package com.caipiao.ticket.vote.huaying;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.encrypt.DESCoder;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.HuaYingTicketUtil;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.caipiao.ticket.vote.base.InterfaceCastTicket;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 华盈出票
 * Created by kouyi on 2016/12/12.
 */
@Component("huaYingCastTicketTask")
public class HuaYingCastTicketTask extends HuaYingTicketUtil implements InterfaceCastTicket {
    protected static Logger logger = LoggerFactory.getLogger(HuaYingCastTicketTask.class);
    @Override
    public void jcCastTicketTask() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.isJc(vo.getPlayType())) {
                    continue;
                }
                jcCast(vo);
            }
        } catch (Exception e) {
            logger.error("[华盈竞彩提票]-> 提票异常", e);
        }
    }

    private void jcCast(VoteVo vote) {
        try {
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), SchemeConstants.TICKET_STATUS_WAITING);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 45;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[华盈竞彩提票]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<CodeInfo> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    SchemeTicket ticket = ticketList.get(n);
                    if(ticket.getMultiple() > maxMultiple){
                        logger.info("[华盈竞彩提票]-> 超过单票最大倍数" + maxMultiple + "倍 票号=" + ticket.getTicketId());
                        continue;
                    }
                    if(ticket.getMoney() > maxMoney){
                        logger.info("[华盈竞彩提票]-> 超过单票最大金额" + maxMoney + "元 票号=" + ticket.getTicketId());
                        continue;
                    }
                    CodeInfo cb = formatMap.get(ticket.getPlayTypeId()).getCodeBean(ticket, pluginMap.get(ticket.getPlayTypeId()));
                    if (cb.getErrorCode() == 3) {
                        logger.info("[华盈竞彩提票]-> 格式化出票商异常 票号=" + ticket.getTicketId());
                        continue;
                    }
                    cb.setPass(getPassTypeMap(cb.getPass()));
                    cb.setPlayType(getLotteryTypeMap(ticket.getPlayTypeId()) + cb.getPass());
                    cb.setSaleCode(getLotteryTypeMap(ticket.getPlayTypeId()));
                    tickets.add(cb);
                }
                executor.execute(new HuaYingJcCastTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[华盈竞彩提票]-> 线程运行结束,启动" + tp + "线程,提交" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[华盈竞彩提票]-> 提票异常", e);
        }
    }

    @Override
    public void jcQueryTicketTask() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.isJc(vo.getPlayType())) {
                    continue;
                }
                jcQuery(vo);
            }
        } catch (Exception e) {
            logger.error("[华盈竞彩出票查询]-> 出票查询异常", e);
        }
    }

    private void jcQuery(VoteVo vote) {
        try {
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), SchemeConstants.TICKET_STATUS_CAST);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 45;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[华盈竞彩出票查询]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new HuaYingJcQueryTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[华盈竞彩出票查询]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[华盈竞彩出票查询]-> 出票查询异常", e);
        }
    }

    @Override
    public void jcAwardTicketTask() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.isJc(vo.getPlayType())) {
                    continue;
                }
                award(vo);
            }
        } catch (Exception e) {
            logger.error("[华盈竞彩兑奖]-> 兑奖异常", e);
        }
    }

    @Override
    public void queryPeriodDrawCode() {

    }

    @Override
    public void queryAccountBalance() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            VoteVo vote = voteList.get(0);
            String body = "<content><merchant>" + vote.getVoteId() + "</merchant></content>";
            String request = HuaYingTicketUtil.getRequestContent(vote, HuaYingTicketUtil.Cast_Balance_Query, body);
            logger.info("[华盈余额查询]-> 请求报文体如下\n" + body.toString());
            JXmlWapper xml = JXmlWapper.parseUrl(vote.getApiUrl(), request);
            if (StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[华盈余额查询]-> 返回响应为空");
            }
            JXmlWapper bodyXml = JXmlWapper.parse(DESCoder.desDecrypt(xml.getStringValue("body"), vote.getKey()));
            logger.info("[华盈余额查询]-> 返回响应体如下\n" + bodyXml.toXmlString());
            Double balance = bodyXml.getDoubleValue("balance");
            if (StringUtil.isNotEmpty(balance)) {
                ticketService.updateVoteBalance("出票余额 " + CalculationUtils.rd(balance/100) + "元[60秒刷新]", vote.getVoteId());
            }
        } catch (Exception e) {
            logger.error("[华盈余额查询]-> 查询异常", e);
        }
    }

    public void award(VoteVo vote) throws Exception {
        try {
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), SchemeConstants.TICKET_STATUS_OUTED);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 45;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[华盈兑奖]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new HuaYingAwardTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[华盈兑奖]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[华盈兑奖]-> 兑奖异常", e);
        }
    }

    @Override
    public void kpQueryTicketTask() {

    }

    @Override
    public void mpQueryTicketTask() {

    }

    @Override
    public void kpCastTicketTask() {

    }

    @Override
    public void mpCastTicketTask() {

    }

    @Override
    public void kpAwardTicketTask() {

    }

    @Override
    public void mpAwardTicketTask() {

    }

}
