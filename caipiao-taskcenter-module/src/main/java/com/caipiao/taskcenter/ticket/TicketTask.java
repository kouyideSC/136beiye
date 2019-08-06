package com.caipiao.taskcenter.ticket;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.common.util.TokenUtil;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.service.match.JczqMatchService;
import com.caipiao.service.ticket.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 票表相关任务
 * Created by Kouyi on 2018/1/3.
 */
@Component("userTicketTask")
public class TicketTask {
    private static Logger logger = LoggerFactory.getLogger(TicketTask.class);
    private final static String defaultVote = "9696";
    private final static Map<String, String> timeMap = new HashMap<>();
    @Autowired
    private TicketService ticketService;
    @Autowired
    private JczqMatchService jczqMatchService;

    static {
        //0,1$00:00-00:55|0,6$09:05-23:59|1-5$09:05-23:55 翻译：周末周一00:00-00:55|周六周末09:05-23:59|周一-周五09:05-23:55
        //0,1$00:00-00:55|1,2,5$09:05-23:55|3,4$7:35-23:55|0,6$09:05-23:59 翻译：周末周一00:00-00:55|周一周二周五09:05-23:55|周三周四7:35-23:55|周六周末09:05-23:59
        timeMap.put("1000", "*$09:05-23:59|*$00:00-01:59");
        timeMap.put("1700", "0,1$00:00-00:59|0,6$09:05-23:59|1-5$09:05-23:59");
        //timeMap.put("1700", "0,6$09:05-23:59|1-5$09:05-23:59");
        timeMap.put("1710", "0,1$00:00-00:59|1,2,5$09:05-23:59|3,4$7:35-23:59|0,6$09:05-23:59");
        //timeMap.put("1710", "1,2,5$09:05-23:59|3,4$7:35-23:59|0,6$09:05-23:59");
        timeMap.put("1560", "0-6$08:00-22:56");
        timeMap.put("1050", "0-6$08:22-21:32");
    }

    /**
     * 9696出票商票状态变更
     * @author kouyi
     */
    public void insideOutTicketStatus() {
        try {
            List<SchemeTicket> ticketList = ticketService.queryTicketList(defaultVote, SchemeConstants.TICKET_STATUS_WAITING);
            if(StringUtil.isEmpty(ticketList)) {
                return;
            }

            MatchFootBall matchFootBall = new MatchFootBall();
            matchFootBall.setLeagueName("世界杯");
            int hour = DateUtil.getCurHour();
            int minute = DateUtil.getCurMinute();
            Date beginTime = null;
            if ((hour > 11 && hour < 23) || (hour == 23 && minute < 59)) {
                beginTime = DateUtil.dateDefaultFormat(DateUtil.dateFormat(new Date(), DateUtil.DEFAULT_DATE) + " 11:00:01");
            }
            if(hour < 11) {
                beginTime = DateUtil.dateDefaultFormat(DateUtil.dateFormat(DateUtil.addDay(new Date(), -1), DateUtil.DEFAULT_DATE) + " 11:00:01");
            }
            matchFootBall.setMatchTime(beginTime);
            matchFootBall.setEndTime(DateUtil.addHour(beginTime, 24));
            List<MatchFootBall> matchFootBallList = jczqMatchService.queryMatchFootBallList(matchFootBall);
            for(SchemeTicket ticket : ticketList) {
                String[] result = worldCupTime(ticket.getLotteryId(), ticket.getCodes(), jczqMatchService);
                if(result[0].equals("1") && (StringUtil.isNotEmpty(matchFootBallList) && matchFootBallList.size() > 0)) {//世界杯赛事-出票时间范围内||根据开赛时间小于当前时间-30s
                    if(!DateUtil.isContain(timeMap.get("1000")) || DateUtil.dateDefaultFormat(result[1]).getTime() < (new Date()).getTime()) {
                        continue;
                    }
                } else {//普通赛事-根据正常出票时间范围
                    if(!DateUtil.isContain(timeMap.get(ticket.getLotteryId()))) {
                        continue;
                    }
                }
                int interval = DateUtil.secondsBetween(ticket.getCreateTime(), new Date());//当前和下单的时间差
                int random = (int)((Math.random() * 90) + 40);
                if(LotteryUtils.isKp(ticket.getLotteryId())) {
                    if(interval < 30) {
                        continue;
                    }
                    random = (int)((Math.random() * 40) + 20);
                    if(random < 30) {
                        random = 30;
                    }
                    if(random > 50) {
                        random = 50;
                    }
                } else {
                    if(interval < 40) {
                        continue;
                    }
                    if(random < 40) {
                        random = 40;
                    }
                    if(random > 120) {
                        random = 120;
                    }
                }
                if(interval < random) {
                    continue;
                }
                int row = ticketService.update9696OutTicketStatus(ticket.getId(), SchemeConstants.TICKET_STATUS_OUTED, SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_OUTED), random);
                if(row > 0) {
                    logger.info("[9696出票查询]-> 出票成功 网站票号=" + ticket.getTicketId());
                } else {
                    logger.error("[9696出票查询]-> 出票失败 [更新数据库失败] 网站票号=" + ticket.getTicketId());
                }
            }
        } catch (Exception e) {
            logger.error("[9696出票商票状态变更] 异常", e);
        }
    }

    /**
     * 世界杯比赛出票时间控制
     * @param code
     * @param jczqMatchService
     * @return
     * @throws Exception
     */
    private static String[] worldCupTime(String lotteryId, String code, JczqMatchService jczqMatchService) throws Exception {
        String[] result = new String[]{"-1", ""};
        if(!LotteryUtils.isJczq(lotteryId) || StringUtil.isEmpty(code)) {
            return result;
        }
        String[] codes = code.split("\\|");
        String[] content = codes[1].split("\\,");
        int cuplen = 0;
        Date minTime = null;
        for(String cs : content) {
            String matchCode = "";
            if (codes[0].startsWith("HH")) {
                matchCode = cs.split("\\>")[0];
            } else {
                matchCode = cs.split("\\=")[0];
            }
            MatchFootBall match = jczqMatchService.queryMatchFootBallByMatchCode(matchCode);
            if(StringUtil.isEmpty(match)) {
                return result;
            }
            if(match.getLeagueName().equals("世界杯")) {
                cuplen++;
            }
            if(StringUtil.isEmpty(minTime)) {
                minTime = match.getMatchTime();
            } else {
                if(minTime.getTime() > match.getMatchTime().getTime()) {
                    minTime = match.getMatchTime();
                }
            }
        }

        if(cuplen > 0 || cuplen == content.length) {//所有投注都是世界杯比赛,则取第一场比赛的时间
            result[0] = "1";
            result[1] = DateUtil.dateDefaultFormat(DateUtil.addSecond(minTime, 30));
        }
        return result;
    }
}
