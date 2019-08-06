package com.caipiao.ticket.vote.huayang;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.bean.PeriodInfo;
import com.caipiao.ticket.util.HuaYangTicketUtil;
import com.caipiao.ticket.util.PrizesUtil;
import com.caipiao.ticket.vote.base.InterfaceCastTicket;
import com.mina.rbc.util.xml.JXmlWapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 华阳出票
 * Created by kouyi on 2016/12/12.
 */
@Component("huaYangCastTicketTask")
public class HuaYangCastTicketTask extends HuaYangTicketUtil implements InterfaceCastTicket {
    protected static Logger logger = LoggerFactory.getLogger(HuaYangCastTicketTask.class);
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
            logger.error("[华阳竞彩提票]-> 提票异常", e);
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
            logger.info("[华阳竞彩提票]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<CodeInfo> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    SchemeTicket ticket = ticketList.get(n);
                    if(ticket.getMultiple() > maxMultiple){
                        logger.info("[华阳竞彩提票]-> 超过单票最大倍数" + maxMultiple + "倍 票号=" + ticket.getTicketId());
                        continue;
                    }
                    if(ticket.getMoney() > maxMoney){
                        logger.info("[华阳竞彩提票]-> 超过单票最大金额" + maxMoney + "元 票号=" + ticket.getTicketId());
                        continue;
                    }
                    CodeInfo cb = formatMap.get(ticket.getPlayTypeId()).getCodeBean(ticket, pluginMap.get(ticket.getPlayTypeId()));
                    if (cb.getErrorCode() == 3) {
                        logger.info("[华阳竞彩提票]-> 格式化出票商异常 票号=" + ticket.getTicketId());
                        continue;
                    }
                    if(LotteryUtils.isJclq(ticket.getPlayTypeId())) {
                        cb.setPass(getLqPassTypeMap(cb.getPass()));
                    } else {
                        cb.setPass(getPassTypeMap(cb.getPass()));
                    }
                    cb.setLotteryId(getLotteryTypeMap(ticket.getPlayTypeId()));
                    tickets.add(cb);
                }
                executor.execute(new HuaYangJcCastTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[华阳竞彩提票]-> 线程运行结束,启动" + tp + "线程,提交" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[华阳竞彩提票]-> 提票异常", e);
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
            logger.error("[华阳竞彩出票查询]-> 出票查询异常", e);
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
            logger.info("[华阳竞彩出票查询]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new HuaYangJcQueryTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[华阳竞彩出票查询]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[华阳竞彩出票查询]-> 出票查询异常", e);
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
            logger.error("[华阳竞彩兑奖]-> 兑奖异常", e);
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
            logger.info("[华阳兑奖]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new HuaYangAwardTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[华阳兑奖]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[华阳兑奖]-> 兑奖异常", e);
        }
    }

    @Override
    public void kpCastTicketTask() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.isKp(vo.getPlayType())) {
                    continue;
                }
                getCurrentPeriod(vo);
                if(StringUtil.isNotEmpty(vo.getPeriod())) {
                    szcCast(vo);
                }
            }
        } catch (Exception e) {
            logger.error("[华阳数字彩提票]-> 提票异常", e);
        }
    }

    @Override
    public void kpQueryTicketTask() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.isKp(vo.getPlayType())) {
                    continue;
                }
                szcQuery(vo);
            }
        } catch (Exception e) {
            logger.error("[华阳数字彩出票查询]-> 出票查询异常", e);
        }
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
            logger.error("[华阳数字彩提票]-> 提票异常", e);
        }
    }

    /**
     * 数字彩-多线程提票
     * @param vote
     */
    private void szcCast(VoteVo vote) {
        try {
            String period = vote.getPeriod();
            if(LotteryUtils.isTC(vote.getPlayType()) || LotteryUtils.isKp(vote.getPlayType())) {
                period = "20" + vote.getPeriod();
            }
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), period, SchemeConstants.TICKET_STATUS_WAITING);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 45;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[华阳数字彩提票]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<CodeInfo> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    SchemeTicket ticket = ticketList.get(n);
                    if(ticket.getMultiple() > maxMultiple){
                        logger.info("[华阳数字彩提票]-> 超过单票最大倍数" + maxMultiple + "倍 票号=" + ticket.getTicketId());
                        continue;
                    }
                    if(ticket.getMoney() > maxMoney){
                        logger.info("[华阳数字彩提票]-> 超过单票最大金额" + maxMoney + "元 票号=" + ticket.getTicketId());
                        continue;
                    }
                    CodeInfo cb = formatMap.get(ticket.getPlayTypeId()).getCodeBean(ticket, pluginMap.get(ticket.getPlayTypeId()));
                    if (cb.getErrorCode() == 3) {
                        logger.info("[华阳数字彩提票]-> 格式化出票商异常 票号=" + ticket.getTicketId());
                        continue;
                    }
                    cb.setLotteryId(getLotteryTypeMap(ticket.getPlayTypeId()));
                    tickets.add(cb);
                }
                executor.execute(new HuaYangSzcCastTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[华阳数字彩提票]-> 线程运行结束,启动" + tp + "线程,提交" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[华阳数字彩提票]-> 提票异常", e);
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
            logger.error("[华阳数字彩出票查询]-> 出票查询异常", e);
        }
    }

    /**
     * 数字彩-多线程出票查询
     * @param vote
     */
    private void szcQuery(VoteVo vote) {
        try {
            List<SchemeTicket> ticketList = ticketService.queryTicketList(vote.getVoteId(), vote.getPlayType(), SchemeConstants.TICKET_STATUS_CAST);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }
            int len = ticketList.size();
            int size = 45;
            int tp = len % size == 0 ? len / size : len / size + 1;//实际线程个数
            logger.info("[华阳数字彩出票查询]-> 启动线程个数:" + tp);

            long start = System.currentTimeMillis();
            CountDownLatch countDownLatch = new CountDownLatch(tp);
            ExecutorService executor = Executors.newFixedThreadPool(tp);
            for (int m = 0; m < tp; m++) {
                List<SchemeTicket> tickets = new ArrayList<>();
                for (int n = size * m; n < size * (m + 1) && n < len; n++) {
                    tickets.add(ticketList.get(n));
                }
                executor.execute(new HuaYangSzcQueryTicketThread(tickets, vote, ticketService, logger, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
            long end = System.currentTimeMillis();
            logger.info("[华阳数字彩出票查询]-> 线程运行结束,启动" + tp + "线程,处理" + len + "张票,花费" + ((end - start)/1000) + "秒");
        } catch (Exception e) {
            logger.error("[华阳数字彩出票查询]-> 出票查询异常", e);
        }
    }

    @Override
    public void kpAwardTicketTask() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.isKp(vo.getPlayType())) {
                    continue;
                }
                award(vo);
            }
        } catch (Exception e) {
            logger.error("[华阳数字彩兑奖]-> 兑奖异常", e);
        }
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
            logger.error("[华阳数字彩兑奖]-> 兑奖异常", e);
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
            if(StringUtil.isNotEmpty(xml)) {
                String status = xml.getStringValue("status");
                if ("0".equals(status)) {
                    String issue = xml.getStringValue("issue");
                    String endTime = xml.getStringValue("endtime");
                    String startTime = xml.getStringValue("starttime");
                    period = new PeriodInfo();
                    period.setGid(vote.getPlayType());
                    period.setPeriodID(issue);
                    period.setEndTime(DateUtil.dateDefaultFormat(endTime));
                    periodMap.put(vote.getPlayType(), period);
                    logger.info("[华阳当前期缓存]-> 将彩种[" + vote.getPlayName() + "]当前期[" + issue + "]加入缓存 开始时间=" + startTime + " 截止时间=" + endTime);
                    vote.setPeriod(issue);
                    //吉林快三当前期次从华阳抓取并更新到期次中
                    if(vote.getPlayType().equals(LotteryConstants.K3_JL)) {
                        Period k3CurPeriod = new Period();
                        k3CurPeriod.setPeriod("20"+issue);
                        k3CurPeriod.setLotteryId(vote.getPlayType());
                        k3CurPeriod.setAuthorityEndTime(DateUtil.dateDefaultFormat(endTime));
                        k3CurPeriod.setDrawNumberTime(DateUtil.addSecond(k3CurPeriod.getAuthorityEndTime(), 240));
                        periodService.updatePeriodStatusByPeriodId(k3CurPeriod);//更新当前期的官方截止时间
                        Period k3NextPeriod = new Period();
                        k3NextPeriod.setPeriod(getNextPeriod("20"+issue));
                        k3NextPeriod.setLotteryId(vote.getPlayType());
                        k3NextPeriod.setSellStartTime(DateUtil.addSecond(DateUtil.dateDefaultFormat(endTime), -120));
                        //更新下一期为跨天001期时截止时间先固定为早上08:25:00,等抓到001期正确时间后再进行更新校正
                        if(k3NextPeriod.getPeriod().substring(8, 11).equals("001")) {
                            k3NextPeriod.setSellEndTime(DateUtil.dateFormat(k3NextPeriod.getPeriod().substring(0, 8) + "082500", DateUtil.LOG_DATE_TIME2));
                        } else {
                            k3NextPeriod.setSellEndTime(DateUtil.addSecond(k3NextPeriod.getSellStartTime(), 540));
                        }
                        k3NextPeriod.setAuthorityEndTime(DateUtil.addSecond(k3NextPeriod.getSellEndTime(), 120));
                        k3NextPeriod.setDrawNumberTime(DateUtil.addSecond(k3NextPeriod.getAuthorityEndTime(), 240));
                        periodService.updatePeriodStatusByPeriodId(k3NextPeriod);//更新下一期的开始和截止时间
                        //当前期次文件任务
                        taskService.saveTask(new Task(Constants.periodUpdateTaskMaps.get(vote.getPlayType())));
                    }
                }
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
            String body = "<element><lotteryid>" + getLotteryTypeMap(vote.getPlayType()) + "</lotteryid><issue></issue></element>";
            String request = getRequestContent(Query_Current_Period, body, vote);
            logger.info("[华阳当前期查询]-> 请求报文体如下\n" + request);
            JXmlWapper xml = JXmlWapper.parseUrl(vote.getApiUrl(), request, "UTF-8", 30);
            if(StringUtil.isEmpty(xml.toXmlString())) {
                logger.error("[华阳当前期查询]-> 返回响应为空");
                return null;
            }
            logger.info("[华阳当前期查询]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
            String code = xml.getStringValue("body.oelement.errorcode");
            if(code.equals("0")) {
                return xml.getXmlNode("body.elements.element[0]");
            }
        } catch (Exception e) {
            logger.error("[华阳当前期查询]-> 查询异常", e);
        }
        return null;
    }

    /**
     * 期次自动加1期
     * @param period
     * @return
     */
    private String getNextPeriod(String period) {
        String s = period.substring(0, 8);
        int e = StringUtil.parseInt(period.substring(8, 11)) + 1;
        if(e > 87) {
            s = DateUtil.dateFormat(DateUtil.addDay(DateUtil.dateFormat(s, DateUtil.DEFAULT_DATE1),1), DateUtil.DEFAULT_DATE1);
            e = 1;
        }
        if(e < 10) {
            return s + "00" + e;
        } else if(e >= 10 && e < 100) {
            return s + "0" + e;
        } else {
            return s + e;
        }
    }

    @Override
    public void queryPeriodDrawCode() {
        try {
            List<VoteVo> voteList = setInitialized();
            if (StringUtil.isEmpty(voteList)) {
                return;
            }
            for (VoteVo vo : voteList) {
                if (!LotteryUtils.is11x5(vo.getPlayType())) {
                    continue;
                }
                getPeriodDrawCodeInfo(vo);
            }
        } catch (Exception e) {
            logger.error("[华阳快频开奖号码查询]-> 查询异常", e);
        }
    }

    @Override
    public void queryAccountBalance() {

    }

    /**
     * 根据彩种获取开奖号码
     * @param vote
     * @return
     */
    private void getPeriodDrawCodeInfo(VoteVo vote) {
        try {
            List<Period> periodList = periodService.queryPeriodAlreadyDrawList(vote.getPlayType());
            if(StringUtil.isEmpty(periodList)) {
                return;
            }
            for(Period pd : periodList) {
                logger.info("[华阳开奖号码查询]-> 彩种=" + vote.getPlayType());
                String body = "<element><lotteryid>" + getLotteryTypeMap(vote.getPlayType()) + "</lotteryid><issue>"+ pd.getPeriod().substring(2) +"</issue></element>";
                String request = getRequestContent(Query_Current_Period, body, vote);
                logger.info("[华阳开奖号码查询]-> 请求报文体如下\n" + request);
                JXmlWapper xml = JXmlWapper.parseUrl(vote.getApiUrl(), request, "UTF-8", 30);
                if (StringUtil.isEmpty(xml.toXmlString())) {
                    logger.error("[华阳开奖号码查询]-> 返回响应为空");
                    return;
                }
                logger.info("[华阳开奖号码查询]-> 返回响应体如下\n" + xml.toXmlString("UTF-8"));
                String code = xml.getStringValue("body.oelement.errorcode");
                if (!code.equals("0")) {
                    logger.error("[华阳开奖号码查询]-> 返回状态码不正确 code=" + code);
                    return;
                }
                JXmlWapper result = xml.getXmlNode("body.elements.element[0]");
                if(StringUtil.isEmpty(result)) {
                    logger.error("[华阳开奖号码查询]-> 无节点数据");
                    return;
                }
                int status = result.getIntValue("status");
                if (status < 3) {
                    logger.error("[华阳开奖号码查询]-> 开奖号码为空 等待开奖 status=" + status);
                    return;
                }
                String issue = result.getStringValue("issue");
                String drawCode = result.getStringValue("bonuscode");
                if(StringUtil.isEmpty(drawCode)) {
                    logger.error("[华阳开奖号码查询]-> 开奖号码为空 等待开奖");
                    return;
                }
                logger.info("[华阳开奖号码查询]-> 查询成功 期次=" + issue + " 开奖号码=" + drawCode);
                if(!PrizesUtil.isNumber(drawCode, vote.getPlayType())) {
                    logger.error("[华阳开奖号码查询]-> 开奖号格式不正确");
                    return;
                }
                pd.setDrawNumber(drawCode);
                pd.setDrawNumberTime(new Date());
                pd.setState(LotteryConstants.STATE_THREE);
                periodService.updatePeriodStatusById(pd);
                logger.info("[华阳开奖号码查询]-> 期次["+ issue +"] 开奖号更新成功");
            }
        } catch (Exception e) {
            logger.error("[华阳开奖号码查询]-> 查询异常", e);
        }
    }
}
