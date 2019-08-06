package com.caipiao.ticket.vote.ouke;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.OuKeTicketUtil;
import com.caipiao.ticket.util.OuKeTicketUtil;
import com.caipiao.ticket.vote.base.InterfaceCastTicket;
import com.caipiao.ticket.vote.ouke.OuKeAwardTicketThread;
import com.caipiao.ticket.vote.ouke.OuKeJcCastTicketThread;
import com.caipiao.ticket.vote.ouke.OuKeJcQueryTicketThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 欧克出票
 * Created by kouyi on 2018/11/03.
 */
@Component("ouKeCastTicketTask")
public class OuKeCastTicketTask extends OuKeTicketUtil implements InterfaceCastTicket {
    protected static Logger logger = LoggerFactory.getLogger(OuKeCastTicketTask.class);
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
            logger.error("[欧克竞彩提票]-> 提票异常", e);
        }
    }

    private void jcCast(VoteVo vote) {
        try {
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), SchemeConstants.TICKET_STATUS_WAITING);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 15;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[欧克竞彩提票]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<CodeInfo> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    SchemeTicket ticket = ticketList.get(n);
                    if(ticket.getMultiple() > maxMultiple){
                        logger.info("[欧克竞彩提票]-> 超过单票最大倍数" + maxMultiple + "倍 票号=" + ticket.getTicketId());
                        continue;
                    }
                    if(ticket.getMoney() > maxMoney){
                        logger.info("[欧克竞彩提票]-> 超过单票最大金额" + maxMoney + "元 票号=" + ticket.getTicketId());
                        continue;
                    }
                    CodeInfo cb = formatMap.get(ticket.getPlayTypeId()).getCodeBean(ticket, pluginMap.get(ticket.getPlayTypeId()));
                    if (cb.getErrorCode() == 3) {
                        logger.info("[欧克竞彩提票]-> 格式化出票商异常 票号=" + ticket.getTicketId());
                        continue;
                    }
                    tickets.add(cb);
                }
                executor.execute(new OuKeJcCastTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[欧克竞彩提票]-> 线程运行结束,启动" + tp + "线程,提交" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[欧克竞彩提票]-> 提票异常", e);
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
            logger.error("[欧克竞彩出票查询]-> 出票查询异常", e);
        }
    }

    private void jcQuery(VoteVo vote) {
        try {
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), SchemeConstants.TICKET_STATUS_CAST);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 15;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[欧克竞彩出票查询]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new OuKeJcQueryTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[欧克竞彩出票查询]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[欧克竞彩出票查询]-> 出票查询异常", e);
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
            logger.error("[欧克竞彩兑奖]-> 兑奖异常", e);
        }
    }

    @Override
    public void queryPeriodDrawCode() {

    }

    @Override
    public void queryAccountBalance() {

    }

    public void award(VoteVo vote) throws Exception {
        try {
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), SchemeConstants.TICKET_STATUS_OUTED);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 15;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[欧克兑奖]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new OuKeAwardTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[欧克兑奖]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[欧克兑奖]-> 兑奖异常", e);
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
