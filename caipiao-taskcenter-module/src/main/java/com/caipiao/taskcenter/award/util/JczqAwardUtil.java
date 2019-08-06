package com.caipiao.taskcenter.award.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.DoubleUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.ActivityAddBonus;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeFollow;
import com.caipiao.domain.scheme.SchemeMatches;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAddBonusDetail;
import com.caipiao.domain.vo.JczqAwardInfo;
import com.caipiao.domain.vo.PrizeMoneyVO;
import com.caipiao.memcache.MemCached;
import com.caipiao.plugin.Lottery1720;
import com.caipiao.plugin.bjutil.CombineUtil;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.jcutil.JcCastCode;
import com.caipiao.plugin.jcutil.JcItemBean;
import com.caipiao.plugin.jcutil.JcItemCodeUtil;
import com.caipiao.plugin.jcutil.JcPassTypeUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.service.user.UserService;
import org.fusesource.mqtt.codec.CONNACK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mina.rbc.util.StringUtil.splitter;



/**
 * 竞彩足球场次维护工具类
 * Created by kouyi on 2017/12/19.
 */
public class JczqAwardUtil {
    private static Logger logger = LoggerFactory.getLogger(JczqAwardUtil.class);

    /**
     * 竞彩足球-已经截止未出票的方案状态处理
     * @param schemeService
     * @param ticketService
     * @param awardInfo
     * @return true-撤单完成 false-撤单异常
     */
    public static boolean cancelJczqScheme(SchemeService schemeService, TicketService ticketService, JczqAwardInfo awardInfo) {
        try {
            List<Scheme> schemeList = schemeService.queryJcNoSuccessSchemeForEndTime(LotteryConstants.JCZQ, awardInfo.getMatchCode());
            if (StringUtil.isEmpty(schemeList)) {
                return true;
            }
            //查询出票系统的票
            for (Scheme scheme : schemeList) {
                List<SchemeTicket> ticketList = ticketService.queryTicketListBySchemeId(scheme.getSchemeOrderId());
                if (StringUtil.isEmpty(ticketList)) {
                    continue;
                }
                int success = 0;
                for (SchemeTicket ticket : ticketList) {
                    if (ticket.getTicketStatus().intValue() > 0) {//等待出票或出票成功
                        success++;
                    } else {
                        //单张票出票失败
                        ticketService.updateOutTicketStatusForCancel(ticket.getTicketId(), -2, "场次截止-系统自动出票失败");
                    }
                }
                if (success == ticketList.size()) {//出票成功
                    continue;
                }

                scheme.setSchemeStatus(SchemeConstants.SCHEME_STATUS_ETF);
                scheme.setSchemeStatusDesc(SchemeConstants.schemeStatusMap.get(scheme.getSchemeStatus()));
                schemeService.updateSchemeTicketStatus(scheme);
                logger.info("场次截止方案状态处理成功 方案号=" + scheme.getSchemeOrderId());
            }
            return true;
        } catch (Exception e) {
            logger.error("场次截止方案状态处理异常", e);
            return false;
        }
    }

    /**
     * 竞彩足球-过关任务
     * @param schemeService
     * @param ticketService
     * @param match
     * @param pluginMap
     * @return
     */
    public static boolean guoGuanJczqMatch(SchemeService schemeService, TicketService ticketService, JczqAwardInfo match, HashMap<String, GamePluginAdapter> pluginMap){
        try{
            if(StringUtil.isEmpty(match)){
                return false;
            }

            //场次相关所有方案
            List<SchemeMatches> schemeMatchesList = schemeService.querySchemeForMatch(LotteryConstants.JCZQ, match.getMatchCode());
            if (StringUtil.isEmpty(schemeMatchesList)) {//该场无购彩方案
                LoggerUtil.printJcInfo("竞彩足球-过关任务", match.getMatchCode(), "场次没有过关方案", logger);
                return true;
            }

            //当前场次关联的所有方案包含的场次
            Map<String, JczqAwardInfo> orderMatchs = schemeService.querySchemeForJczqMatch(LotteryConstants.JCZQ, match.getMatchCode());
            if(StringUtil.isEmpty(orderMatchs)) {
                LoggerUtil.printJcError("竞彩足球-过关任务", match.getMatchCode(), "未找到当前场次相关的数据", logger);
                return false;
            }

            JczqAwardInfo currAwardInfo = orderMatchs.get(match.getMatchCode());
            if(StringUtil.isEmpty(currAwardInfo)) {
                LoggerUtil.printJcError("竞彩足球-过关任务", match.getMatchCode(), "数据丢失", logger);
                return false;
            }
            if(currAwardInfo.getState() != LotteryConstants.MATCHJJ_STATE_FILE) {
                LoggerUtil.printJcError("竞彩足球-过关任务", match.getMatchCode(), "未审核", logger);
                return false;
            }

            for (SchemeMatches schemeMatches : schemeMatchesList) {
                if(!schemeService.isAuditJczqSchemeForMatch(schemeMatches.getLotteryId(), schemeMatches.getSchemeId())) {
                    continue;//该方案包含的所有场次未全部审核 不计奖
                }
                List<SchemeTicket> tickets = ticketService.queryGuoGuanTicketListBySchemeId(schemeMatches.getSchemeOrderId(), 0);
                if(StringUtil.isEmpty(tickets)){
                    LoggerUtil.printJcInfo("竞彩足球-过关任务", match.getMatchCode(), "方案[" + schemeMatches.getSchemeOrderId() + "]没有待计奖的票", logger);
                    continue;//该方案没有需要计奖的票
                }
                for(SchemeTicket ticket : tickets) {
                    if (StringUtil.isEmpty(ticket.getCodes())) {
                        break;
                    }
                    GamePluginAdapter plugin = InitPlugin.getPlugin(pluginMap, ticket.getPlayTypeId());
                    if (plugin == null) {
                        LoggerUtil.printJcInfo("竞彩足球-过关任务", match.getMatchCode(), "无法获取彩种插件" + ticket.getPlayTypeId(), logger);
                        break;
                    }
                    try {
                        double [] results = new double[3];
                        Map<String, String> detail = new TreeMap<>();//中奖明细
                        //方案过关
                        boolean finish = processJcGuoGuanCodes(ticket, orderMatchs, detail, results, plugin);
                        if(!finish){
                            break;//该票包含的比赛未全部完场
                        }

                        double isWin = results[0];//中奖总注数
                        String infos = "";//串关方式|中奖注数|税前奖金|税后奖金
                        for (Map.Entry<String, String> entry : detail.entrySet()) {
                            String[] value = splitter(entry.getValue(), "|");
                            infos += entry.getKey() + "|" + value[0] + "|" + CalculationUtils.spValue(Double.parseDouble(value[1])*ticket.getMultiple()) + "|" + CalculationUtils.spValue(Double.parseDouble(value[2])*ticket.getMultiple()) + ";";
                        }
                        if(infos.endsWith(";")) {
                            infos = infos.substring(0, infos.length()-1);
                        }

                        String outDel = ticket.getTicketStatus().intValue() > 1 ? "已出票" : "未出票";
                        if (isWin >= 1) {
                            isWin = 2;
                            LoggerUtil.printJcInfo("竞彩足球-过关任务", match.getMatchCode(), "方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + "[过关]成功 中奖[" + outDel + "] 投注串=" + ticket.getCodes(), logger);
                        } else {
                            isWin = 1;
                            LoggerUtil.printJcInfo("竞彩足球-过关任务", match.getMatchCode(), "方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + "[过关]成功 未中奖[" + outDel + "] 投注串=" + ticket.getCodes(), logger);
                        }

                        ticket.setBonusInfo(infos);
                        ticket.setIsWin((int) isWin);
                        ticket.setBonusState(2);
                        ticket.setTicketPrize(results[1]);
                        ticket.setTicketSubjoinPrize(0d);
                        ticket.setTicketPrizeTax(results[2]);
                        ticket.setTicketSubjoinPrizeTax(0d);
                        if(StringUtil.isNotEmpty(ticket.getVoteId()) && ticket.getVoteId().equals(PrizesUtil.defaultVote)) {
                            ticket.setTicketStatus(99);//票状态变更
                        }
                        int row = ticketService.updateTicketPrizeMoney(ticket);
                        if (row == 0) {
                            LoggerUtil.printJcError("竞彩足球-过关任务", match.getMatchCode(), "票号(" + ticket.getTicketId() + ") 更新数据库失败", null, logger);
                        }
                    } catch (Exception e) {
                        //未成功出票的票可能格式原因导致匹配异常,当以成功处理并继续修改过关状态
                        if (ticket.getTicketStatus() < SchemeConstants.TICKET_STATUS_OUTED) {
                            ticket.setBonusInfo("过关失败-问题票");
                            ticket.setIsWin(1);
                            ticket.setBonusState(2);
                            ticket.setTicketPrize(0d);
                            ticket.setTicketSubjoinPrize(0d);
                            ticket.setTicketPrizeTax(0d);
                            ticket.setTicketSubjoinPrizeTax(0d);
                            ticketService.updateTicketPrizeMoney(ticket);
                        } else {
                            //打印异常日志
                            LoggerUtil.printJcError("竞彩足球-过关任务", match.getMatchCode(), "票号(" + ticket.getTicketId() + ") 过关异常", e, logger);
                        }
                    }
                }
            }
            return true;
        } catch (Exception e){
            LoggerUtil.printJcError("竞彩足球-过关任务", match.getMatchCode(), "过关异常", e, logger);
            return false;
        }
    }

    /**
     * 竞彩足球-奖金汇总
     * @param schemeService
     * @param ticketService
     * @param activityService
     * @param userService
     * @param match
     * @return
     */
    public static boolean prizeMoneySummary(SchemeService schemeService, TicketService ticketService, ActivityService activityService, UserService userService, MemCached memCached, JczqAwardInfo match) {
        try{
            if(StringUtil.isEmpty(match)){
                return false;
            }
            //需要进行金额汇总的票
            List<String> tickets = ticketService.queryTicketPrizeSummaryForMatch(LotteryConstants.JCZQ, match.getMatchCode());
            if(StringUtil.isEmpty(tickets)){//没有需要汇总奖金的票
                return true;
            }

            for(String schemeId : tickets) {
                List<SchemeTicket> list = ticketService.queryTicketListBySchemeId(schemeId);
                if(StringUtil.isEmpty(list)){
                    continue;
                }
                //总奖金、税后奖金、官方加奖奖金、官方加奖税后奖金、网站加奖奖金、网站加奖税后奖金
                double totalmoney = 0,taxmoney = 0,addmoney = 0,taxaddmoney = 0, addmoneysite = 0, taxaddmoneysite = 0;
                Map<String, String> winInfo = new TreeMap<>();
                for(SchemeTicket ticket : list) {//汇总金额及奖级[由于奖级不能在SQL中汇总，故使用程序汇总]
                    if(ticket.getIsWin().intValue() == 2) {//已中奖
                        totalmoney += ticket.getTicketPrize();
                        taxmoney += ticket.getTicketPrizeTax();
                        addmoney += ticket.getTicketSubjoinPrize();
                        taxaddmoney += ticket.getTicketSubjoinPrizeTax();
                        addInfo(winInfo, ticket.getBonusInfo());
                    }
                }

                totalmoney = CalculationUtils.spValue(totalmoney);
                taxmoney = CalculationUtils.spValue(taxmoney);
                addmoney = CalculationUtils.spValue(addmoney);
                taxaddmoney = CalculationUtils.spValue(taxaddmoney);

                //设置中奖信息
                Scheme scheme = schemeService.querySchemeInfoBySchemeOrderId(schemeId);//方案
                if(scheme == null) {
                    LoggerUtil.printJcError("竞彩足球-奖金汇总任务", match.getMatchCode(), "找不到方案数据(方案号="+schemeId+")", null, logger);
                    continue;
                }
                //只有代办成功的方案才更新中奖状态
                double dsmoney = 0;//打赏金额
                if(scheme.getSchemeStatus().intValue() == SchemeConstants.SCHEME_STATUS_CPCG) {
                    if ((totalmoney + addmoney + addmoneysite) > 0) {//中奖
                        scheme.setPrizeTax(taxmoney);
                        addmoneysite = addPrizeSummary(schemeService, activityService, userService, scheme, match.getMatchCode(), memCached);
                        taxaddmoneysite = addmoneysite;
                        if(scheme.getSchemeType().intValue() == SchemeConstants.SCHEME_TYPE_GD) {//跟单方案
                            SchemeFollow follow = schemeService.querySchemeFollowInfo(scheme.getSchemeOrderId());
                            if(StringUtil.isEmpty(follow)) {
                                LoggerUtil.printJcError("竞彩足球-跟单方案打赏任务", match.getMatchCode(), "找不到跟单关联表数据", logger);
                                continue;
                            }
                            //打赏比例大于0&&方案中奖后有盈利 则计算打赏
                            if(follow.getRewardProportion() > 0 && taxmoney > scheme.getSchemeMoney()) {
                                dsmoney = CalculationUtils.bankerAlgoNum((taxmoney - scheme.getSchemeMoney())*(follow.getRewardProportion()/100.00d));
                                follow.setAwardState(1);
                                scheme.setRewardPrize(dsmoney);//记录订单表
                            } else {
                                follow.setAwardState(2);
                            }
                            follow.setFollowPrizeMoney(taxmoney);
                            follow.setRewardMoney(dsmoney);
                            schemeService.updateSchemeFollow(follow);//更新跟单计奖信息
                            totalmoney -= dsmoney;
                            taxmoney -= dsmoney;
                        }
                        else if(scheme.getSchemeType().intValue() == SchemeConstants.SCHEME_TYPE_SD) {//神单方案
                            List<SchemeFollow> follows = schemeService.querySchemeFollowList(new SchemeFollow(scheme.getSchemeOrderId()));
                            if(StringUtil.isNotEmpty(follows)) {//有人跟单-计算打赏
                                scheme.setPrizeStatus(1);
                            }
                        }
                        scheme.setOpenStatus(2);
                    } else {//未中
                        scheme.setOpenStatus(1);
                    }
                    scheme.setOpenTime(new Date());
                }
                scheme.setPrize(CalculationUtils.spValue(totalmoney+addmoney+addmoneysite));
                scheme.setPrizeSubjoin(addmoney);
                scheme.setPrizeSubjoinSite(addmoneysite);
                scheme.setPrizeTax(CalculationUtils.spValue(taxmoney+taxaddmoney+taxaddmoneysite));
                scheme.setPrizeSubjoinTax(taxaddmoney);
                scheme.setPrizeSubjoinSiteTax(taxaddmoneysite);
                scheme.setPrizeDetail(getInfoDetail(winInfo, totalmoney, addmoney+addmoneysite, taxmoney, taxaddmoney+taxaddmoneysite, dsmoney));
                schemeService.updateSchemeStatusPrize(scheme);

                ticketService.updateTicketBonusState(schemeId, 3);//更新票过关状态
                LoggerUtil.printJcInfo("竞彩足球-奖金汇总任务", match.getMatchCode(), "方案号=" + schemeId + " 奖金汇总成功", logger);
            }
            tickets.clear();
            tickets = ticketService.queryTicketPrizeSummaryForMatch(LotteryConstants.JCZQ, match.getMatchCode());
            if(StringUtil.isEmpty(tickets)){//为空表示全部完成汇总,否则返回false，流程继续执行
                return true;
            } else {
                tickets.clear();
                return false;
            }
        } catch (Exception e){
            LoggerUtil.printJcError("竞彩足球-奖金汇总任务", match.getMatchCode(), "奖金汇总异常", e, logger);
            return false;
        }
    }

    /**
     * 竞彩足球-计算加奖任务
     * @param schemeService
     * @param activityService
     * @param userService
     * @param scheme
     * @param matchCode
     * @return
     */
    public static synchronized double addPrizeSummary(SchemeService schemeService, ActivityService activityService, UserService userService, Scheme scheme, String matchCode, MemCached memCached) {
        try{
            double addPrize = 0;
            User user = null;
            String usKey = "award_us_" + scheme.getSchemeUserId();
            //出款账户不加奖
            if(memCached.contains(usKey)) {
                user = (User) memCached.get(usKey);
            } else {
                user = userService.queryUserInfoByAward(scheme.getSchemeUserId());
                if(StringUtil.isEmpty(user)){
                    return addPrize;
                }
                memCached.set(usKey, user, 12 * 60 * 60);//缓存12小时
            }
            if(user.getUserType() == UserConstants.USER_TYPE_OUTMONEY) {
                return addPrize;
            }
            List<SchemeMatches> matchesList = schemeService.querySchemeInfoByMatches(LotteryConstants.JCZQ, scheme.getId());
            if(StringUtil.isEmpty(matchesList)) {
                return addPrize;
            }

            //查询彩种相关的加奖活动
            List<ActivityAddBonus> activityAddBonusList = null;
            String acKey = "addbonus_ac_" + LotteryConstants.JCZQ;
            if(memCached.contains(acKey)) {
                activityAddBonusList = (List<ActivityAddBonus>) memCached.get(acKey);
            } else {
                activityAddBonusList = activityService.queryLotteryAddActivityList(LotteryConstants.JCZQ);
                if(StringUtil.isEmpty(activityAddBonusList)){
                    return addPrize;
                }
                memCached.set(acKey, activityAddBonusList, 10 * 60);//缓存10分钟
            }

            for(ActivityAddBonus activityAddBonus : activityAddBonusList) {
                if(!activityAddBonus.getPassType().equalsIgnoreCase(matchesList.size()+"*1")) {//串关方式匹配
                    continue;
                }
                if (StringUtil.isEmpty(activityAddBonus.getAddBonusRate())) {
                    LoggerUtil.printJcError("竞彩足球-计算加奖任务", "未配置正确加奖比例[加奖活动名称=" + activityAddBonus.getActivityName() + "]", logger);
                    continue;//未正确配置加奖比例
                }
                //查询参与活动的所有用户
                List<Long> joinUserList = null;
                String acJoinKey = "userid_join_" + activityAddBonus.getId();
                if(memCached.contains(acJoinKey)) {
                    joinUserList = (List<Long>) memCached.get(acJoinKey);
                } else {
                    joinUserList = activityService.queryActivityJoinUser(activityAddBonus.getId());
                    if(StringUtil.isEmpty(joinUserList)){
                        continue;
                    }
                    memCached.set(acJoinKey, joinUserList, 10 * 60);//缓存10分钟
                }
                if(!joinUserList.contains(scheme.getSchemeUserId())) {
                    continue;//用户未参与该加奖活动
                }
                String[] contents = scheme.getSchemeContent().split("\\|");
                if (contents.length != 3) {
                    LoggerUtil.printJcError("竞彩足球-计算加奖任务", "投注串格式错误[方案号=" + scheme.getSchemeOrderId() + "]", logger);
                    continue;
                }

                //方案金额最低限制
                if (scheme.getSchemeMoney() < activityAddBonus.getSchemeMoneyLimit()) {
                    LoggerUtil.printJcError("竞彩足球-计算加奖任务", "方案金额"+scheme.getSchemeMoney()+"元,达不到最低限制"+activityAddBonus.getSchemeMoneyLimit()+"元,[方案号=" + scheme.getSchemeOrderId() + "]", logger);
                    continue;
                }

                String maxMatchCode = matchesList.get(0).getMatchCode();//取出最大场次(数据库查询倒序)确定订单归属日期
                //单关加奖方案验证购买场次必须一致
                if (matchesList.size() == 1 && contents[2].equals("1*1")) {
                    if (!activityAddBonus.getMatchCode().equals(maxMatchCode)) {
                        continue;
                    }
                }
                //串关加奖验证
                else if (matchesList.size() > 1 && contents[2].equals((matchesList.size() + "*1"))) {
                    maxMatchCode = maxMatchCode.substring(0, 8);//串关加奖订单归属必须使用日期
                    Date schemeAddTime = DateUtil.dateFormat(maxMatchCode + "235959", DateUtil.LOG_DATE_TIME2);
                    //不再有效时间内<开始时间
                    if (StringUtil.isNotEmpty(activityAddBonus.getBeginTime()) && activityAddBonus.getBeginTime().getTime() > schemeAddTime.getTime()) {
                        continue;
                    }
                    //不再有效时间内>结束时间
                    if (StringUtil.isNotEmpty(activityAddBonus.getEndTime()) && activityAddBonus.getEndTime().getTime() < schemeAddTime.getTime()) {
                        continue;
                    }
                    //星期限制
                    if (StringUtil.isNotEmpty(activityAddBonus.getWeekLimit()) && activityAddBonus.getWeekLimit().indexOf(DateUtil.getWeekInt(schemeAddTime) + "") == -1) {
                        continue;
                    }
                    //赛事限制
                    if (StringUtil.isNotEmpty(activityAddBonus.getLeagueNameLimit())) {
                        boolean isLimit = false;
                        for (SchemeMatches match : matchesList) {
                            if (activityAddBonus.getLeagueNameLimit().indexOf(match.getLotteryId()) == -1) {
                                isLimit = true;
                                break;
                            }
                        }
                        if(isLimit) {
                            continue;
                        }
                    }
                } else {
                    continue;//其他订单不满足加奖条件
                }

                if (activityAddBonus.getBalance() >= activityAddBonus.getMaxMoney()) {
                    LoggerUtil.printJcError("竞彩足球-计算加奖任务", "活动剩余额度不足[加奖活动名称=" + activityAddBonus.getActivityName() + " 方案编号=" + scheme.getSchemeOrderId() + "]", logger);
                    continue;//可用额度不足
                }
                Map<String, Double> rateMap = PrizesUtil.initRateMap(activityAddBonus.getAddBonusRate());//格式化加奖比例
                //查询用户当前期已经加奖金额
                double userDayAddPrizeSum = activityService.queryUserDayAddprizeSum(scheme.getSchemeUserId(), activityAddBonus.getId(), maxMatchCode);
                if (userDayAddPrizeSum >= activityAddBonus.getUserDayLimit()) {
                    LoggerUtil.printJcInfo("竞彩足球-计算加奖任务", "方案号=" + scheme.getSchemeOrderId() + "不享受加奖(单日加奖[金额" + userDayAddPrizeSum + "元]已达到上限)", logger);
                    continue;
                }
                //计算加奖奖金
                double rate = PrizesUtil.getAddBonusRate(rateMap, scheme.getPrizeTax());
                addPrize = CalculationUtils.muld(rate, scheme.getPrizeTax());
                //当前加奖金额+用户单日已加奖金额>用户单日加奖限制，则计算差值
                if (CalculationUtils.add(addPrize, userDayAddPrizeSum) > activityAddBonus.getUserDayLimit()) {
                    addPrize = CalculationUtils.sub(activityAddBonus.getUserDayLimit(), userDayAddPrizeSum);
                }
                //当前加奖金额+活动已使用额度>活动总额度，则计算差值
                if (CalculationUtils.add(addPrize, activityAddBonus.getBalance()) > activityAddBonus.getMaxMoney()) {
                    addPrize = CalculationUtils.sub(activityAddBonus.getMaxMoney(), activityAddBonus.getBalance());
                }
                //满足加奖条件
                if (addPrize > 0) {
                    addPrize = CalculationUtils.spValue(addPrize);
                    //记录订单加奖流水
                    UserAddBonusDetail detail = new UserAddBonusDetail();
                    detail.setUserId(scheme.getSchemeUserId());
                    detail.setSchemeOrderId(scheme.getSchemeOrderId());
                    detail.setSchemeMoney(scheme.getSchemeMoney());
                    detail.setActivityId(activityAddBonus.getId());
                    detail.setLotteryId(scheme.getLotteryId());
                    detail.setRateRange(rate + "");
                    detail.setAddPrizeTax(addPrize);
                    detail.setPrizeTax(scheme.getPrizeTax());
                    detail.setAddPrizeDateStr(maxMatchCode);
                    detail.setLastBalance(activityAddBonus.getBalance());
                    detail.setCurrBalance(CalculationUtils.add(activityAddBonus.getBalance(), addPrize));
                    int row = activityService.insertUserAddBounsDetail(detail);
                    if (row > 0) {//流水添加成功
                        if(user.getUserType() != UserConstants.USER_TYPE_VIRTUAL) {
                            activityService.updateAddBounsBalance(addPrize, activityAddBonus.getId());//更新使用额度
                        }
                        LoggerUtil.printJcInfo("竞彩足球-计算加奖任务", matchCode, "方案号=" + scheme.getSchemeOrderId() + ",中奖金额=" + scheme.getPrizeTax() + ",加奖活动名称=" + activityAddBonus.getActivityName() + ",加奖比例=" + rate + ",加奖金额=" + addPrize + " 计算加奖金额成功", logger);
                    }
                    break;//同一订单不能参与多个加奖活动
                }
            }
            return addPrize;
        } catch (Exception e){
            LoggerUtil.printJcError("竞彩足球-计算加奖任务", matchCode, "方案号="+scheme.getSchemeOrderId()+" 计算加奖异常", e, logger);
            return 0;
        }
    }

    /**
     * 竞彩足球-汇总神单打赏
     * @param schemeService
     * @param match
     * @return
     */
    public static boolean followSchemeRewardMoney(SchemeService schemeService, JczqAwardInfo match) {
        try{
            if(StringUtil.isEmpty(match)){
                return false;
            }
            //查询中奖的神单发单人收获打赏金额
            List<SchemeFollow> followList = schemeService.querySchemeFollowListByRewards(LotteryConstants.JCZQ, match.getMatchCode());
            if(StringUtil.isEmpty(followList)){
                return true;
            }
            for(SchemeFollow follow : followList) {
                Double prize = CalculationUtils.spValue(follow.getFollowMoney() + follow.getRewardMoney());
                Double prizeTax = CalculationUtils.spValue(follow.getFollowPrizeMoney() + follow.getRewardMoney());
                String prizeDetail = getInfoDetail(follow.getFollowNickName(), follow.getRewardMoney());
                Scheme scheme = new Scheme();
                scheme.setSchemeOrderId(follow.getSenderSchemeId());
                scheme.setPrize(prize);
                scheme.setPrizeTax(prizeTax);
                scheme.setPrizeDetail(prizeDetail);
                scheme.setRewardPrize(follow.getRewardMoney());
                schemeService.updateSchemeFollowPrize(scheme);

                //更新跟单表计奖状态
                schemeService.updateSchemeFollowBySendSchemeOrderId(follow.getSenderSchemeId());
                LoggerUtil.printJcInfo("竞彩足球-汇总神单打赏任务", match.getMatchCode(), "方案号=" + follow.getSenderSchemeId() + " 神单打赏金汇总成功", logger);
            }
            followList.clear();
            return true;
        } catch (Exception e){
            LoggerUtil.printJcError("竞彩足球-汇总神单打赏任务", match.getMatchCode(), "神单打赏金汇总异常", e, logger);
            return false;
        }
    }

    /**
     * 竞彩足球-自动派奖任务
     * @param schemeService
     * @param userService
     * @param match
     * @return
     */
    public static boolean authSendSmallMoney(SchemeService schemeService, UserService userService, JczqAwardInfo match){
        try{
            if(StringUtil.isEmpty(match)){
                return false;
            }

            int maxPrize = SysConfig.getInt("AUTO_SEND_PRIZEMONEY");
            if(maxPrize == 0) {
                maxPrize = 2000;//默认
            }
            //满足自动派奖要求的方案
            List<Scheme> schemeList = schemeService.queryAutoSendMoneySchemeList(LotteryConstants.JCZQ, maxPrize);
            if(StringUtil.isEmpty(schemeList)){
                return true;
            }

            for(Scheme scheme : schemeList) {
                Dto param = new BaseDto();
                param.put("id", scheme.getId());
                param.put("iszh", 0);
                int success = userService.updateSchemeForQrPj(param);
                if(success == 1){
                    LoggerUtil.printJcInfo("竞彩足球-自动派奖任务", "方案号=" + scheme.getSchemeOrderId() + " (奖金小于"+maxPrize+")自动派奖成功【派送奖金:"+scheme.getPrizeTax()+",税前奖金:"+scheme.getPrize()+"】", logger);
                } else {
                    LoggerUtil.printJcInfo("竞彩足球-自动派奖任务", "方案号=" + scheme.getSchemeOrderId() + " (奖金小于"+maxPrize+")自动派奖失败【税后奖金:"+scheme.getPrizeTax()+"】", logger);
                }
            }
            schemeList.clear();
            schemeList = schemeService.queryAutoSendMoneySchemeList(LotteryConstants.JCZQ, maxPrize);
            if(StringUtil.isEmpty(schemeList)){//为空表示全部处理完成,否则返回false，流程继续执行
                return true;
            } else {
                schemeList.clear();
                return false;
            }
        } catch (Exception e){
            LoggerUtil.printJcError("竞彩足球-自动派奖任务", match.getMatchCode(), "自动派奖异常", e, logger);
            return false;
        }
    }

    /**
     * 竞彩足球-神单数据统计
     * @param schemeService
     * @param match
     * @return
     */
    public static boolean followSchemeUserDataStatis(SchemeService schemeService, JczqAwardInfo match){
        try{
            if(StringUtil.isEmpty(match)){
                return false;
            }
            //查询某场比赛相关已结算的神单用户编号
            List<Long> userList = schemeService.queryFollowUserIdForMatch(LotteryConstants.JCZQ, match.getMatchCode());
            if(StringUtil.isEmpty(userList)){
                return true;
            }

            double tempValue = 0.0;
            for(Long userId : userList) {
                Dto followStatis = new BaseDto();
                //***************************近一周数据统计********************************
                //查询最近一周用户神单数据
                int hitSum = 0;
                List<Dto> schemeWeekList = schemeService.queryUserFollowForWeek(LotteryConstants.JCZQ, userId);
                if(StringUtil.isNotEmpty(schemeWeekList)) {
                    boolean isRed = true;//是否命中标记-标识连红
                    int conRed = 0;
                    double money = 0.0, prize = 0.0;
                    for(Dto scheme : schemeWeekList) {
                        if(scheme.getAsInteger("openStatus") == 2) {
                            hitSum++;
                            if(isRed) {conRed++;}//连红统计
                        } else {
                            if(isRed) {isRed = false;}
                        }
                        money += scheme.getAsDoubleValue("schemeMoney");
                        prize += scheme.getAsDoubleValue("prizeTax");
                    }
                    followStatis.put("weekOrderSums", schemeWeekList.size());
                    followStatis.put("weekHitSums", hitSum);
                    tempValue = CalculationUtils.bankerAlgoNum((double)hitSum/schemeWeekList.size())*100;
                    followStatis.put("weekHitRate", tempValue);
                    followStatis.put("weekBuyMoney", money);
                    followStatis.put("weekHitMoney", prize);
                    tempValue = CalculationUtils.bankerAlgoNum(prize/money)*100;
                    followStatis.put("weekWinRate", tempValue);
                    //followStatis.put("weekHitDescribe", schemeWeekList.size() + "中" + hitSum);
                    followStatis.put("weekRunRedSums", conRed);
                }

                //***************************近一月数据统计********************************
                //查询最近一月用户神单数据
                Dto schemeMonth = schemeService.queryUserFollowStatisForMonth(LotteryConstants.JCZQ, userId);
                if(StringUtil.isNotEmpty(schemeMonth)) {
                    int mSum = schemeMonth.getAsInteger("orderSums");
                    int mHitSum = schemeMonth.getAsInteger("hitSums");
                    double mMoney = schemeMonth.getAsDoubleValue("buyMoney");
                    double mPrize = schemeMonth.getAsDoubleValue("hitMoney");
                    followStatis.put("monthOrderSums", mSum);
                    followStatis.put("monthHitSums", mHitSum);
                    tempValue = CalculationUtils.bankerAlgoNum((double)mHitSum/mSum)*100;
                    followStatis.put("monthHitRate", tempValue);
                    followStatis.put("monthBuyMoney", mMoney);
                    followStatis.put("monthHitMoney", mPrize);
                    tempValue = CalculationUtils.bankerAlgoNum(mPrize/mMoney)*100;
                    followStatis.put("monthWinRate", tempValue);
                    followStatis.put("monthHitDescribe", mSum + "中" + mHitSum);
                    followStatis.put("monthRunRedSums", 0);//月连红不统计
                }

                //近10场走势
                String tenOrderTrend = "";
                int hitCount = 0;
                List<Long> tenRedList = schemeService.queryUserNearTenFollowScheme(LotteryConstants.JCZQ, userId);
                if(StringUtil.isNotEmpty(tenRedList)) {
                    for (int index = tenRedList.size() - 1; index >= 0; index--) {
                        if(tenRedList.get(index).intValue() == 2) {
                            tenOrderTrend += "1";
                            hitCount++;
                        } else {
                            tenOrderTrend += "0";
                        }
                        if(index != 0) {
                            tenOrderTrend += "-";
                        }
                    }
                    followStatis.put("tenOrderTrend", tenOrderTrend);
                }
                followStatis.put("weekHitDescribe", tenRedList.size() + "中" + hitCount);//只统计近10场战绩
                followStatis.put("userId", userId);
                followStatis.put("lotteryId", LotteryConstants.JCZQ);
                //最近一周有分享神单 才统计收到打赏总金额
                if(schemeWeekList.size() > 0) {
                    Dto statis = schemeService.queryUserFollowRewardMoneyStatis(LotteryConstants.JCZQ, userId);
                    int sum = statis.getAsInteger("orderSums");
                    int hit = statis.getAsInteger("hitSums");
                    double money = statis.getAsDoubleValue("buyMoney");
                    double prize = statis.getAsDoubleValue("hitMoney");
                    followStatis.put("orderSums", sum);
                    followStatis.put("hitSums", hit);
                    tempValue = CalculationUtils.bankerAlgoNum((double)hit/sum)*100;
                    followStatis.put("hitRate", tempValue);
                    followStatis.put("buyMoney", money);
                    followStatis.put("hitMoney", prize);
                    tempValue = CalculationUtils.bankerAlgoNum((double)prize/money)*100;
                    followStatis.put("winRate", tempValue);
                    followStatis.put("hitDescribe", sum + "中" + hit);
                    followStatis.put("rewardMoney", statis.getAsDoubleValue("redwardSum"));
                    followStatis.put("followSums", statis.getAsInteger("followNum"));
                    followStatis.put("followMoneySums", statis.getAsDoubleValue("followSum"));
                }
                //更新数据
                schemeService.updateUserFollowStatisInfo(followStatis);
                LoggerUtil.printJcInfo("竞彩足球-用户神单数据任务", "用户编号=" + userId + " 数据处理成功", logger);
                tempValue = 0.0;
            }
            userList.clear();
            return true;
        } catch (Exception e){
            LoggerUtil.printJcError("竞彩足球-用户神单数据任务", match.getMatchCode(), "用户神单数据处理异常", e, logger);
            return false;
        }
    }

    /**
     * 竞彩过关业务实现类
     * @param ticket
     * @param orderMatchs
     * @param detail
     * @param results
     * @param plugin
     * @return
     * @throws Exception
     */
    private static boolean processJcGuoGuanCodes(SchemeTicket ticket, Map<String, JczqAwardInfo> orderMatchs, Map<String, String> detail, double[] results, GamePluginAdapter plugin) throws Exception {
        String [] tcodes = splitter(ticket.getCodes(), ";");
        Map<String, String> mapTicketSp = getSchemeCodeSp(ticket.getCodesSp(), ticket.getPlayTypeId());//初始化转换出票sp
        for (int index = 0; index < tcodes.length; index++) {
            if(StringUtil.isEmpty(tcodes[index])){
                continue;
            }
            GameCastCode gcc = plugin.parseGameCastCode(tcodes[index]);
            List<Object> lst = gcc.getCast();
            for (int jt = 0; jt < lst.size(); jt++) {
                JcCastCode ccode = (JcCastCode) lst.get(jt);
                String billCode = ccode.toBillCode();
                String [] bcs = splitter(billCode, ";");
                for(int t = 0; t < bcs.length; t++){
                    if(StringUtil.isEmpty(bcs[t])){
                        continue;
                    }
                    GameCastCode _gcc = plugin.parseGameCastCode(bcs[t]);
                    List<Object> _lst = _gcc.getCast();
                    for (int j = 0; j <_lst.size(); j++) {
                        JcCastCode _bccode = (JcCastCode) _lst.get(j);
                        int num = 0;
                        double total = 0, tax = 0;
                        List<BingoUtil> bingoList = new ArrayList<BingoUtil>();
                        List<JcItemBean> lstItem = _bccode.getJcItemList();
                        for (int k = 0; k < lstItem.size(); k++) {
                            JcItemBean item = lstItem.get(k);
                            String itemId = item.getItemid();
                            JczqAwardInfo award = orderMatchs.get(itemId);
                            if (award == null) {
                                error(ticket, itemId, "数据丢失", logger);
                                return false;
                            }
                            if (award.getState() < LotteryConstants.MATCHJJ_STATE_FILE) {
                                info(ticket, itemId, "未审核", logger);
                                return false;
                            }

                            BingoUtil bingo = new BingoUtil();
                            int rel = Long.bitCount(item.getCode() & award.getBingoCode());
                            bingo.setTime(rel);
                            if (rel > 1) {
                                bingo.setSpValue("1.0");
                                bingoList.add(bingo);
                                info(ticket, itemId, "已取消 开奖SP=1.0", logger);
                            } else if (rel == 1) {
                                bingo.setSpValue(award.getSpValue(mapTicketSp, item));
                                bingoList.add(bingo);
                                info(ticket, itemId, "命中 开奖SP=" + bingo.getSpValue(), logger);
                            } else {
                                info(ticket, itemId, "未命中", logger);
                            }
                        }

                        int start = JcPassTypeUtil.getMinRangePassType(ccode.getPassType());
                        int end = JcPassTypeUtil.getPassType(ccode.getPassType());
                        for (int n = start; n <= end; n++) {
                            List<int[]> comList = CombineUtil.combine(bingoList.size(), n);
                            if (comList != null) {//结果和串关数得出是否中奖
                                for (int k = 0; k < comList.size(); k++) {
                                    int[] cml = comList.get(k);
                                    double d1 = -1.0;
                                    int tt = 1;
                                    for (int m = 0; m < cml.length; m++) {
                                        BingoUtil bv = bingoList.get(m);
                                        if (cml[m] > 0) {//中奖
                                            if (d1 < 0) {
                                                d1 = 1.0;
                                            }
                                            d1 = CalculationUtils.muld(d1, CalculationUtils.muld(Double.parseDouble(bv.getSpValue()), 1));
                                            tt *= bv.getTime();
                                        }
                                    }
                                    if (d1 > 0) {
                                        double dt = CalculationUtils.spValue(CalculationUtils.bankerAlgoNum(CalculationUtils.muld(d1, 2)));
                                        if(tt > 1){
                                            dt *= tt;
                                        }
                                        double da = (dt/tt > 10000) ? CalculationUtils.spValue(CalculationUtils.muld(dt, 0.8)) : dt;
                                        logger.info("竞彩足球-过关中... 方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + " 串关方式=" + getPassType(n) + " 单注奖金=" + dt + " 税后奖金=" + da);
                                        if(detail.containsKey(getPassType(n))) {
                                            String[] values = splitter(detail.get(getPassType(n)), "|");
                                            detail.put(getPassType(n), (Integer.parseInt(values[0]) + tt) + "|" + CalculationUtils.spValue(Double.parseDouble(values[1]) + dt) + "|" + CalculationUtils.spValue(Double.parseDouble(values[1]) + da));
                                        } else {
                                            detail.put(getPassType(n), tt + "|" + dt + "|" + da);
                                        }
                                        num += tt;
                                        total += dt;
                                        tax += da;
                                    }
                                }
                                comList.clear();
                            }
                        }
                        bingoList.clear();

                        num = num * ticket.getMultiple();
                        total = com.util.math.MathUtil.round(com.util.math.MathUtil.round(total,2)*ticket.getMultiple(), 2);
                        tax = com.util.math.MathUtil.round(com.util.math.MathUtil.round(tax,2)*ticket.getMultiple(), 2);
                        logger.info("竞彩足球-过关中... 方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + " 投注倍数=" + ticket.getMultiple() + " 中奖注数=" + num + " 税前奖金=" + total + " 税后奖金=" + tax);

                        results[0] += num;//中奖注数
                        results[1] += total;//中奖税前奖金
                        results[2] += tax;//中奖税后奖金
                    }
                }
            }
        }
        return true;
    }

    /**
     * 中奖明细汇总
     * @param winInfo 总奖级
     * @param curInfo 当前奖级
     * @return
     */
    private static void addInfo(Map<String, String> winInfo, String curInfo) {
        if (StringUtil.isEmpty(curInfo)) {
            return;
        }

        String[] winfos = PluginUtil.splitter(curInfo, ";");
        for(String wf : winfos) {
            String[] fo = PluginUtil.splitter(wf, "|");
            if(fo.length != 4) {
                continue;
            }
            String chuan = fo[0];
            if(winInfo.containsKey(chuan)) {
                String[] vles = splitter(winInfo.get(chuan), "|");
                winInfo.put(chuan, (Integer.parseInt(vles[0]) + Integer.parseInt(fo[1])) + "|" + CalculationUtils.spValue(Double.parseDouble(vles[1]) + Double.parseDouble(fo[2]))+ "|" + CalculationUtils.spValue(Double.parseDouble(vles[2]) + Double.parseDouble(fo[3])));
            } else {
                winInfo.put(chuan, fo[1] + "|" + fo[2] + "|" + fo[3]);
            }
        }
    }

    /**
     * 中奖明细格式化为字符串
     * @param winInfo
     * @param amoney
     * @param addmoney
     * @param taxmoney
     * @param taxaddmoney
     * @param dsmoney
     * @return
     */
    private static String getInfoDetail(Map<String, String> winInfo, double amoney, double addmoney, double taxmoney,
                                        double taxaddmoney, double dsmoney) {
        StringBuffer info = new StringBuffer();
        if(StringUtil.isEmpty(winInfo)) {
            return info.toString();
        }
        for (Map.Entry<String, String> entry : winInfo.entrySet()) {
            String[] value = splitter(entry.getValue(), "|");
            info.append(entry.getKey() + "中" + value[0] + "注 实际<b>中奖</b>:税前" + value[1] + "元 税后" + value[2] + "元<br/>");
        }

        info.append("派奖小结：税前<b>派奖总奖金</b>" + CalculationUtils.spValue(amoney + addmoney) + "元");
        if(addmoney > 0 || dsmoney > 0) {
            info.append("(");
            if(addmoney > 0) {
                info.append("含加奖").append(addmoney).append("元");
            }
            if(dsmoney > 0) {
                if(addmoney > 0) {
                    info.append(",");
                }
                info.append("支付赏金").append(dsmoney).append("元");
            }
            info.append(")");
        }
        info.append(" 税后<b>派奖总奖金</b>" + CalculationUtils.spValue(taxmoney + taxaddmoney) + "元");
        if(taxaddmoney > 0 || dsmoney > 0) {
            info.append("(");
            if(taxaddmoney > 0) {
                info.append("含加奖").append(taxaddmoney).append("元");
            }
            if(dsmoney > 0) {
                if(taxaddmoney > 0) {
                    info.append(",");
                }
                info.append("支付赏金").append(dsmoney).append("元");
            }
            info.append(")");
        }
        return info.toString();
    }

    /**
     * 神单发单人中奖详情-增加打赏说明
     * @param detail
     * @param reward
     * @return
     */
    private static String getInfoDetail(String detail, double reward) {
        if(StringUtil.isEmpty(detail) || reward <= 0) {
            return detail;
        }
        int begin = detail.indexOf("税前<b>派奖总奖金</b>");
        //税前和税后加上收获打赏金额
        String info = detail.substring(0, begin);
        String regEx = "[^0-9.]";
        String[] des = detail.substring(begin, detail.length()).split("\\s+");
        for(int index=0; index<des.length; index++) {
            int pos = des[index].length() - 1;
            int len = des[index].indexOf("(");
            if(len > -1) {
                pos = len;
            }
            Pattern pattern = Pattern.compile(regEx);
            Matcher m = pattern.matcher(des[index].substring(0, pos));
            if(m.find()) {
                String money = m.replaceAll("").trim();
                des[index] = des[index].replaceAll(money, CalculationUtils.spValue(Double.parseDouble(money)+reward)+"");
            }
            if(des[index].indexOf("(") > 0 && des[index].indexOf(")") > 0) {
                des[index] = des[index].substring(0, des[index].indexOf(")"));
                des[index] += ",";
            } else {
                des[index] += "(";
            }
            des[index] += "收取赏金" + reward + "元)";
            info += des[index];
            info += " ";
        }

        if(info.endsWith(" ")) {
            info = info.substring(0, info.length()-1);
        }
        return info;
    }

    /**
     * 根据int获取串关方式
     * @param c
     * @return
     */
    private static String getPassType(int c) {
        if(c < 1 || c > 8) {
            return "未知串关";
        }
        String pass = "";
        switch (c) {
            case 1:
                pass = "单关";
                break;
            case 2:
                pass = "2串1";
                break;
            case 3:
                pass = "3串1";
                break;
            case 4:
                pass = "4串1";
                break;
            case 5:
                pass = "5串1";
                break;
            case 6:
                pass = "6串1";
                break;
            case 7:
                pass = "7串1";
                break;
            default:
                pass = "8串1";
                break;
        }
        return pass;
    }

    /**
     * 将出票sp串格式化为map-计奖使用
     * @param codeSp
     * @return
     */
    public static Map<String, String> getSchemeCodeSp(String codeSp, String playType) {
        Map<String, String> spMap = new HashMap<>();
        codeSp = codeSp.replaceAll("\\->", "#");
        String[] sps = PluginUtil.splitter(codeSp, ",");
        if (codeSp.indexOf("#") > -1) {//混投
            for (String sp : sps) {
                String[] ms = PluginUtil.splitter(sp, "#");
                String[] alx = PluginUtil.splitter(ms[1], "=");
                int type = getPlayType(alx[0]);
                String[] gs = PluginUtil.splitter(alx[1], "/");
                for (String g : gs) {
                    String[] s = PluginUtil.splitter(g, "@");
                    spMap.put(ms[0] + "_" + type + "_" + s[0], s[1]);
                }
            }
        } else {
            int type = getPlayType(playType);
            for (String sp : sps) {
                String[] ms = PluginUtil.splitter(sp, "=");
                String[] xs = PluginUtil.splitter(ms[1], "/");
                for (String ch : xs) {
                    String[] s = PluginUtil.splitter(ch, "@");
                    spMap.put(ms[0] + "_" + type + "_" + s[0], s[1]);
                }
            }
        }
        return spMap;
    }

    /**
     * 根据玩法字符串表示 返回对应int类型定义
     * @param playType
     * @return
     */
    private static int getPlayType(String playType) {
        int r = 0;
        if(playType.equals("SPF")){
            r = JcItemCodeUtil.SPF;
        } else if (playType.equals("CBF")) {
            r = JcItemCodeUtil.CBF;
        } else if (playType.equals("BQC")) {
            r = JcItemCodeUtil.BQC;
        } else if (playType.equals("JQS")) {
            r = JcItemCodeUtil.JQS;
        } else if (playType.equals("RQSPF")) {
            r = JcItemCodeUtil.RQSPF;
        } else if(playType.equals("1720")){
            r = JcItemCodeUtil.SPF;
        } else if (playType.equals("1900")) {
            r = JcItemCodeUtil.RQSPF;
        } else if (playType.equals("1910")) {
            r = JcItemCodeUtil.CBF;
        } else if (playType.equals("1920")) {
            r = JcItemCodeUtil.BQC;
        } else if (playType.equals("1930")) {
            r = JcItemCodeUtil.JQS;
        }
        return r;
    }

    /**
     * 日志输出-info
     * @param ticket
     * @param matchID
     * @param info
     * @param logger
     */
    public static void info(SchemeTicket ticket, String matchID, String info, Logger logger) {
        logger.info("竞彩足球-过关中... 方案号=" + ticket.getSchemeId() + ",票号=" + ticket.getTicketId() + " 场次=" + matchID + " " + info);
    }

    /**
     * 日志输出-error
     * @param ticket
     * @param matchID
     * @param info
     * @param logger
     */
    public static void error(SchemeTicket ticket, String matchID, String info, Logger logger) {
        logger.error("竞彩足球-过关中... 方案号=" + ticket.getSchemeId() + ",票号=" + ticket.getTicketId() + " 场次=" + matchID + " " + info);
    }

    public static void main(String[] args) {
        try {
            //System.out.println(getInfoDetail("2串1中1注 实际中奖:税前189.76元 税后189.76元<br/>税前派奖总奖金199.25元(含加奖9.49元) 税后派奖总奖金199.25元(含加奖9.49元)", 88.38));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
