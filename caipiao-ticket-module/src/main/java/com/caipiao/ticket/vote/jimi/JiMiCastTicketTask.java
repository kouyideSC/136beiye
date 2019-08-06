package com.caipiao.ticket.vote.jimi;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.bean.PeriodInfo;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.caipiao.ticket.vote.base.InterfaceCastTicket;
import com.caipiao.ticket.vote.huayang.HuaYangCastTicketTask;
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
 * 吉米出票
 * Created by kouyi on 2016/12/12.
 */
@Component("jiMiCastTicketTask")
public class JiMiCastTicketTask extends JiMiTicketUtil implements InterfaceCastTicket {
    protected static Logger logger = LoggerFactory.getLogger(JiMiCastTicketTask.class);
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
            logger.error("[吉米竞彩提票]-> 提票异常", e);
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
            logger.info("[吉米竞彩提票]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<CodeInfo> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    SchemeTicket ticket = ticketList.get(n);
                    if(ticket.getMultiple() > maxMultiple){
                        logger.info("[吉米竞彩提票]-> 超过单票最大倍数" + maxMultiple + "倍 票号=" + ticket.getTicketId());
                        continue;
                    }
                    if(ticket.getMoney() > maxMoney){
                        logger.info("[吉米竞彩提票]-> 超过单票最大金额" + maxMoney + "元 票号=" + ticket.getTicketId());
                        continue;
                    }
                    CodeInfo cb = formatMap.get(ticket.getPlayTypeId()).getCodeBean(ticket, pluginMap.get(ticket.getPlayTypeId()));
                    if (cb.getErrorCode() == 3) {
                        logger.info("[吉米竞彩提票]-> 格式化出票商异常 票号=" + ticket.getTicketId());
                        continue;
                    }
                    tickets.add(cb);
                }
                executor.execute(new JiMiJcCastTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[吉米竞彩提票]-> 线程运行结束,启动" + tp + "线程,提交" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[吉米竞彩提票]-> 提票异常", e);
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
            logger.error("[吉米竞彩出票查询]-> 出票查询异常", e);
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
            logger.info("[吉米竞彩出票查询]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new JiMiJcQueryTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[吉米竞彩出票查询]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[吉米竞彩出票查询]-> 出票查询异常", e);
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
            logger.error("[吉米竞彩兑奖]-> 兑奖异常", e);
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
            int size = 45;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[吉米兑奖]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new JiMiAwardTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[吉米兑奖]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
                logger.error("[吉米兑奖]-> 兑奖异常", e);
        }
    }

    @Override
    public void kpCastTicketTask() {

    }

    @Override
    public void kpQueryTicketTask() {

    }

    @Override
    public void mpCastTicketTask() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.isMp(vo.getPlayType()) && !LotteryUtils.isZC(vo.getPlayType())) {
                    continue;
                }
                getCurrentPeriod(vo);
                if(StringUtil.isNotEmpty(vo.getPeriod())) {
                    szcCast(vo);
                }
            }
        } catch (Exception e) {
            logger.error("[吉米数字彩提票]-> 提票异常", e);
        }
    }

    private void szcCast(VoteVo vote) {
        try {
            String period = "20" + vote.getPeriod();
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), period, SchemeConstants.TICKET_STATUS_WAITING);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 40;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[吉米数字彩提票]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<CodeInfo> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    SchemeTicket ticket = ticketList.get(n);
                    if(ticket.getMultiple() > maxMultiple){
                        logger.info("[吉米数字彩提票]-> 超过单票最大倍数" + maxMultiple + "倍 票号=" + ticket.getTicketId());
                        continue;
                    }
                    if(ticket.getMoney() > maxMoney){
                        logger.info("[吉米数字彩提票]-> 超过单票最大金额" + maxMoney + "元 票号=" + ticket.getTicketId());
                        continue;
                    }
                    CodeInfo cb = formatMap.get(ticket.getPlayTypeId()).getCodeBean(ticket, pluginMap.get(ticket.getPlayTypeId()));
                    if (cb.getErrorCode() == 3) {
                        logger.info("[吉米数字彩提票]-> 格式化出票商异常 票号=" + ticket.getTicketId());
                        continue;
                    }
                    tickets.add(cb);
                }
                executor.execute(new JiMiSzcCastTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[吉米数字彩提票]-> 线程运行结束,启动" + tp + "线程,提交" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[吉米数字彩提票]-> 提票异常", e);
        }
    }

    @Override
    public void mpQueryTicketTask() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.isMp(vo.getPlayType()) && !LotteryUtils.isZC(vo.getPlayType())) {
                    continue;
                }
                szcQuery(vo);
            }
        } catch (Exception e) {
            logger.error("[吉米数字彩出票查询]-> 出票查询异常", e);
        }
    }

    private void szcQuery(VoteVo vote) {
        try {
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), SchemeConstants.TICKET_STATUS_CAST);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 40;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[吉米数字彩出票查询]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new JiMiSzcQueryTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[吉米数字彩出票查询]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[吉米数字彩出票查询]-> 出票查询异常", e);
        }
    }

    @Override
    public void kpAwardTicketTask() {

    }

    @Override
    public void mpAwardTicketTask() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.isMp(vo.getPlayType()) && !LotteryUtils.isZC(vo.getPlayType())) {
                    continue;
                }
                award(vo);
            }
        } catch (Exception e) {
            logger.error("[吉米数字彩兑奖]-> 兑奖异常", e);
        }
    }

    /**
     * 获取彩种当前期次
     * @param vote
     * @return
     * @throws Exception
     */
    private void getCurrentPeriod(VoteVo vote) throws Exception {
        PeriodInfo period = periodMap.get(vote.getPlayType());
        if (StringUtil.isEmpty(period)) {
            JXmlWapper xml = getPeriodInfo(vote);
            if(StringUtil.isEmpty(xml)) {
                return;
            }
            String pid = xml.getStringValue("issue");
            if (StringUtil.isNotEmpty(pid)) {
                String endTime = xml.getStringValue("endTime");
                period = new PeriodInfo();
                period.setGid(vote.getPlayType());
                period.setPeriodID(pid);
                period.setEndTime(DateUtil.parseDate(endTime, DateUtil.LOG_DATE_TIME2));
                periodMap.put(vote.getPlayType(), period);
                logger.info("[吉米当前期缓存]-> 将彩种[" + vote.getPlayName() + "]当前期[" + pid + "]加入缓存 " +
                        "其截止时间=" + DateUtil.dateDefaultFormat(period.getEndTime()));
                vote.setPeriod(pid);
            } else {
                logger.info("[吉米当前期查询]-> 未查到当前期号");
            }
        } else {
            if (period.getEndTime().getTime() < System.currentTimeMillis()) {//过期移除
                periodMap.remove(vote.getPlayType());
            } else {
                vote.setPeriod(period.getPeriodID());
            }
        }
    }

    /**
     * 根据彩种获取当前期次
     * @param vote
     * @return
     */
    private JXmlWapper getPeriodInfo(VoteVo vote) {
        try {
            logger.info("[吉米当前期查询]-> 请求报文体如下\n" + vote.getPlayType());
            JXmlWapper xml = parseUrl(Query_Current_Period, new String[] {getLotteryTypeMap(vote.getPlayType())}, vote);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[吉米当前期查询]-> 返回响应为空");
                return null;
            }
            logger.info("[吉米当前期查询]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
            int count = xml.countXmlNodes("body.issues.issue");
            int temp = 0, index = 0;
            for(int x = 0; x < count; x++) {
                JXmlWapper xmlBody = xml.getXmlNode("body.issues.issue[" + x + "]");
                int pid = xmlBody.getIntValue("issue");
                if(x == 0) {
                    temp = pid;
                } else {
                    if(temp > pid) {
                        temp = pid;
                        index = x;
                    }
                }
            }
            return xml.getXmlNode("body.issues.issue[" + index + "]");
        } catch (Exception e) {
            logger.error("[吉米当前期查询]-> 查询异常", e);
        }
        return null;
    }
}
