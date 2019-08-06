package com.caipiao.taskcenter.award.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeZhuiHao;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1500;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameAwardCode;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.lottery.PeriodService;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.service.user.UserService;
import com.caipiao.taskcenter.code.util.GrabDrawCode;
import com.caipiao.taskcenter.code.util.ResultDataUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

/**
 * 数字彩计奖业务类
 * Created by kouyi on 2017/11/20.
 */
public class PeriodAwardUtil {
    private static Logger logger = LoggerFactory.getLogger(PeriodAwardUtil.class);

    /**
     * 数字彩-已经截止未出票的方案自动撤单
     * @param schemeService
     * @param ticketService
     * @param period
     * @return true-撤单完成 false-撤单异常
     */
    public static boolean cancelSzcScheme(SchemeService schemeService, TicketService ticketService, Period period) {
        try {
            List<Scheme> schemeList = schemeService.querySzcNoSuccessSchemeForEndTime(period.getLotteryId(), period.getPeriod());
            if (StringUtil.isEmpty(schemeList)) {
                return true;
            }

            //查询出票系统的票
            for (Scheme scheme : schemeList) {
                List<SchemeTicket> ticketList = ticketService.queryTicketListBySchemeId(scheme.getSchemeOrderId());
                if (StringUtil.isNotEmpty(ticketList)) {
                    int success = 0;
                    for (SchemeTicket ticket : ticketList) {
                        if (ticket.getTicketStatus().intValue() > 0) {//等待出票或出票成功
                            success++;
                        } else if(ticket.getTicketStatus().intValue() == 0){//出票失败
                            ticketService.updateOutTicketStatusForCancel(ticket.getTicketId(), -2, "期次截止-系统自动出票失败");
                        }
                    }
                    if (success == ticketList.size()) {//出票成功
                        continue;
                    }
                }
                scheme.setSchemeStatus(SchemeConstants.SCHEME_STATUS_ETF);
                scheme.setSchemeStatusDesc(SchemeConstants.schemeStatusMap.get(scheme.getSchemeStatus()));
                schemeService.updateSchemeTicketStatus(scheme);
                logger.info("期次截止-方案状态处理成功 方案号=" + scheme.getSchemeOrderId());
            }
            return true;
        } catch (Exception e) {
            logger.error("期次截止-方案状态处理异常", e);
            return false;
        }
    }

    /**
     * 开奖号码抓取
     * @param period
     * @return
     */
    public static void processAwardCode(Period period) {
        try {
            if(StringUtil.isEmpty(period)) {
                return;
            }

            GrabDrawCode grabResult = (GrabDrawCode) Thread.currentThread().getContextClassLoader().loadClass(ResultDataUtil.getClassName(period.getLotteryId())).newInstance();
            grabResult.GrabGuanWang(period);//官网
            if(StringUtil.isEmpty(period.getDrawNumber()) || !period.getGrabSuccess()){//彩经网
                grabResult.GrabCaiJingWang(period);
            }
        } catch (Exception e) {
            LoggerUtil.printError("期次-抓取开奖号任务", period, "抓取开奖号码异常", e, logger);
        }
    }

    /**
     * 同步方案开奖号码
     * @param schemeService
     * @param period
     * @return
     */
    public static boolean drawNumberSynchronous(SchemeService schemeService, Period period){
        try{
            if(StringUtil.isEmpty(period) || StringUtil.isEmpty(period.getDrawNumber())){
                return false;
            }

            //更新方案开奖号码
            schemeService.updateSchemeDrawNumber(period.getLotteryId(), period.getPeriod(), period.getDrawNumber());
            return true;
        }catch (Exception e){
            LoggerUtil.printError("期次-开奖号同步订单任务", period, "开奖号同步订单异常", e, logger);
            return false;
        }
    }

    /**
     * 数字彩-中奖匹配
     * @param ticketService
     * @param period
     * @param plugin
     * @return
     */
    public static boolean numberBingoDrawingMatch(TicketService ticketService, Period period, GamePluginAdapter plugin) {
        try{
            if(StringUtil.isEmpty(period)){
                return false;
            }
            //查询彩种期次需要过关的票
            List<SchemeTicket> tickets = ticketService.queryGuoGuanTicketListByPeriod(period, 0);
            if(StringUtil.isEmpty(tickets)){//当前期无购彩记录，返回成功
                return true;
            }

            GameAwardCode gac = plugin.buildAwardCode(period.getDrawNumber());
            for(SchemeTicket ticket : tickets) {
                try {
                    if(StringUtil.isEmpty(ticket.getCodes())) {
                        continue;
                    }
                    String infos = "";
                    double isWin = 1.0;//默认未中奖
                    GameCastCode[] gcc = plugin.parseGameCastCodes(ticket.getCodes());
                    int[] level = plugin.bingoMatch(gcc, gac, plugin.getGradeNum());
                    if (StringUtil.isNotEmpty(level) && level.length > 0) {
                        for (int lv : level) {
                            infos += lv + ",";
                            isWin += lv;
                        }
                        infos = infos.substring(0, infos.length() - 1);
                    }

                    String outDel = ticket.getTicketStatus().intValue() > 1 ? "已出票" : "未出票";
                    if (isWin > 1) {
                        isWin = 2;
                        LoggerUtil.printInfo("期次-中奖匹配任务", period, "方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + "[中奖匹配]成功 中奖["+outDel+"]", logger);
                    } else {
                        isWin = 1;
                        LoggerUtil.printInfo("期次-中奖匹配任务", period, "方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + "[中奖匹配]成功 未中奖["+outDel+"] 开奖号=" + period.getDrawNumber() + " 投注号码=" + ticket.getCodes(), logger);
                    }

                    ticket.setBonusInfo(infos);
                    ticket.setIsWin((int) isWin);
                    ticket.setBonusState(1);
                    int row = ticketService.updateTicketPrizeMoney(ticket);
                    if (row > 0) {
                        //大乐透乐善玩法算奖
                        if(ticket.getLotteryId().equals(LotteryConstants.DLT) && StringUtil.isNotEmpty(ticket.getDrawNumber())) {
                            String[] numbers = ticket.getDrawNumber().split("\\,");
                            if(numbers.length == 7) {//乐善号码格式处理
                                StringBuffer buffer = new StringBuffer();
                                for(int x = 0; x < numbers.length; x++) {
                                    buffer.append(numbers[x]);
                                    if(x == 4) {
                                        buffer.append("|");
                                    } else if(x != numbers.length-1){
                                        buffer.append(",");
                                    }
                                }
                                GameAwardCode lsGac = plugin.buildAwardCode(buffer.toString());
                                GameCastCode[] lsGcc = plugin.parseGameCastCodes(ticket.getCodes());
                                int[] lsLevel = plugin.bingoMatch(lsGcc, lsGac, plugin.getGradeNum());
                                infos = "";
                                int lsWin = 0;
                                if (StringUtil.isNotEmpty(lsLevel) && lsLevel.length > 0) {
                                    for (int lv : lsLevel) {
                                        infos += lv + ",";
                                        lsWin += lv;
                                    }
                                    infos = infos.substring(0, infos.length() - 1);
                                }
                                if(isWin == 1 && lsWin > 0) {//常规方案没中奖，但乐善号码中奖则修改票状态为中奖
                                    lsWin = 2;
                                }
                                ticketService.updateOutTicketNumberBonusInfo(ticket.getTicketId(), infos, lsWin);
                                LoggerUtil.printInfo("大乐透乐善玩法-中奖匹配任务", period, "方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + " 计算处理成功", logger);
                            }
                        }
                    } else {
                        LoggerUtil.printError("期次-中奖匹配任务", period, "票号(" + ticket.getTicketId() + ") 更新数据库失败", null, logger);
                    }
                } catch (Exception e) {
                    //未成功出票的票可能格式原因导致匹配异常,当以成功处理并继续修改过关状态
                    if (ticket.getTicketStatus() < SchemeConstants.TICKET_STATUS_OUTED) {
                        ticket.setBonusInfo("中奖匹配失败-问题票");
                        ticket.setIsWin(1);
                        ticket.setBonusState(1);
                        ticket.setTicketPrize(0d);
                        ticket.setTicketSubjoinPrize(0d);
                        ticket.setTicketPrizeTax(0d);
                        ticket.setTicketSubjoinPrizeTax(0d);
                        ticketService.updateTicketPrizeMoney(ticket);
                    }
                }
            }

            tickets.clear();
            tickets = ticketService.queryGuoGuanTicketListByPeriod(period, 0);
            if(StringUtil.isEmpty(tickets)){//为空表示当前期全部完成中奖匹配,否则返回false，流程继续执行
                return true;
            } else {
                tickets.clear();
                return false;
            }
        } catch (Exception e){
            LoggerUtil.printError("期次-中奖匹配任务", period, "中奖匹配异常", e, logger);
            return false;
        }
    }

    /**
     * 数字彩-计算奖金
     * @param ticketService
     * @param period
     * @return
     */
    public static boolean numberCalculatePrizeMoney(TicketService ticketService, Period period) {
        try{
            if(StringUtil.isEmpty(period)){
                return false;
            }
            //查询彩种期次需要计算奖金的票
            List<SchemeTicket> tickets = ticketService.queryGuoGuanTicketListByPeriod(period, 1);
            if(StringUtil.isEmpty(tickets)){//没有需要计算奖金的票 返回成功
                return true;
            }

            for(SchemeTicket ticket : tickets) {
                double totalmoney = 0,taxmoney = 0,addmoney = 0,taxaddmoney = 0;//总奖金、税后奖金、加奖奖金、加奖税后奖金
                String wInfo = ticket.getBonusInfo();
                boolean nomoney = false;//奖级奖金没设置
                if(StringUtil.isNotEmpty(wInfo) && ticket.getIsWin().intValue() == 2) {//已中奖
                    int[] wInfos = PluginUtil.SplitterInt(wInfo, ",");//中奖奖级对应注数
                    int gradeNum = PrizesUtil.gradeNumber(period.getLotteryId());//彩种奖级
                    for(int n = 0; n < gradeNum; n++) {
                        //普通奖金
                        double money = PrizesUtil.fixedMoney(period.getLotteryId(), n);//首先取固定奖金
                        if(money < 0) {//如果浮动奖金则取期次奖级奖金
                            money = CalculationUtils.parseMoney(JSONObject.fromObject(period.getPrizeGrade()).getJSONObject(PrizesUtil.getPrizeName(period.getLotteryId(), n)).getString(LotteryGrade.dzjj)).doubleValue();
                        }
                        if(wInfos[n] > 0 && money <= 0) {
                            nomoney = true;
                            break;
                        }
                        totalmoney += money * wInfos[n] * ticket.getMultiple();
                        if(money > 10000) {
                            taxmoney += money * wInfos[n] * ticket.getMultiple() * 0.8;
                        } else {
                            taxmoney += money * wInfos[n] * ticket.getMultiple();
                        }

                        /*//大乐透追加-六等奖无追加
                        if(ticket.getLotteryId().equals(LotteryConstants.DLT) && n < 5) {
                            double zmoney = Math.floor(money * (n < 3 ? 0.6 : 0.5));//浮动奖追加奖金为基本投注对应单注奖金的60%，固定奖为50%
                            totalmoney += zmoney * wInfos[6+n] * ticket.getMultiple();
                            if(zmoney > 10000) {
                                taxmoney += zmoney * wInfos[6+n] * ticket.getMultiple() * 0.8;
                            } else {
                                taxmoney += zmoney * wInfos[6+n] * ticket.getMultiple();
                            }
                        }*/
                        //大乐透追加-二等奖以后无追加-新规则
                        if(ticket.getLotteryId().equals(LotteryConstants.DLT) && n < 2) {
                            double zmoney = Math.floor(money * 0.8);//浮动奖追加奖金为基本投注对应单注奖金的80%
                            totalmoney += zmoney * wInfos[9+n] * ticket.getMultiple();
                            if(zmoney > 10000) {
                                taxmoney += zmoney * wInfos[9+n] * ticket.getMultiple() * 0.8;
                            } else {
                                taxmoney += zmoney * wInfos[9+n] * ticket.getMultiple();
                            }
                        }

                        //加奖奖金-取期次奖级奖金
                        double amoney = CalculationUtils.parseMoney(JSONObject.fromObject(period.getPrizeGrade()).getJSONObject(PrizesUtil.getPrizeName(period.getLotteryId(), n)).getString(LotteryGrade.jjjj)).doubleValue();//奖级加奖奖金
                        addmoney += amoney * wInfos[n] * ticket.getMultiple();
                        if(amoney > 10000) {
                            taxaddmoney += amoney * wInfos[n] * ticket.getMultiple() * 0.8;
                        } else {
                            taxaddmoney += amoney * wInfos[n] * ticket.getMultiple();
                        }

                        //大乐透官方活动-追加玩法加奖
                        String[] tmpCode = PluginUtil.splitter(ticket.getCodes(), ":");
                        if(ticket.getLotteryId().equals(LotteryConstants.DLT) && PluginUtil.toByte(tmpCode[1]) == Lottery1500.PM_ZHUIJIA) {
                            //追加加奖奖金-取期次奖级奖金
                            double zamoney = CalculationUtils.parseMoney(JSONObject.fromObject(period.getPrizeGrade()).getJSONObject(PrizesUtil.getPrizeName(period.getLotteryId(), n)).getString(LotteryGrade.zjjj)).doubleValue();//奖级加奖奖金
                            addmoney += zamoney * wInfos[n] * ticket.getMultiple();
                            if(zamoney > 10000) {
                                taxaddmoney += zamoney * wInfos[n] * ticket.getMultiple() * 0.8;
                            } else {
                                taxaddmoney += zamoney * wInfos[n] * ticket.getMultiple();
                            }
                        }
                    }
                    LoggerUtil.printInfo("期次-计算奖金任务", period, "方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + "[计算奖金]成功 中奖[税前:"+(totalmoney+addmoney)+",税后:"+(taxmoney+taxaddmoney)+"]", logger);
                }

                //大乐透官方活动乐善玩法计算奖金
                if(ticket.getLotteryId().equals(LotteryConstants.DLT) && StringUtil.isNotEmpty(ticket.getDrawNumber()) && StringUtil.isNotEmpty(ticket.getNumberBonusInfo())) {
                    wInfo = ticket.getNumberBonusInfo();
                    //乐善玩法加奖奖金-取固定奖级奖金
                    int[] wInfos = PluginUtil.SplitterInt(wInfo, ",");
                    int gradeNum = PrizesUtil.gradeNumber(ticket.getLotteryId());
                    for(int n = 0; n < gradeNum; n++) {
                        double money = PrizesUtil.fixedMoney(LotteryConstants.DLT_LS, n);
                        addmoney += money * wInfos[n];
                        taxaddmoney += money * wInfos[n];
                    }
                }

                if(!nomoney) {
                    ticket.setBonusState(2);
                    ticket.setTicketPrize(totalmoney);
                    ticket.setTicketSubjoinPrize(addmoney);
                    ticket.setTicketPrizeTax(taxmoney);
                    ticket.setTicketSubjoinPrizeTax(taxaddmoney);
                    if(StringUtil.isNotEmpty(ticket.getVoteId()) && ticket.getVoteId().equals(PrizesUtil.defaultVote)) {
                        ticket.setTicketStatus(99);//票状态变更
                    }
                    int row = ticketService.updateTicketPrizeMoney(ticket);
                    if(row == 0) {
                        LoggerUtil.printError("期次-计算奖金任务", period, "票号(" + ticket.getTicketId() + ") 更新数据库失败", null, logger);
                    }
                }
            }
            tickets.clear();
            tickets = ticketService.queryGuoGuanTicketListByPeriod(period, 1);
            if(StringUtil.isEmpty(tickets)){//为空表示当前期全部完成奖金计算,否则返回false，流程继续执行
                return true;
            } else {
                tickets.clear();
                return false;
            }
        } catch (Exception e){
            LoggerUtil.printError("期次-计算奖金任务", period, "计算奖金异常", e, logger);
            return false;
        }
    }

    /**
     * 数字彩-奖金汇总
     * @param schemeService
     * @param ticketService
     * @param period
     * @return
     */
    public static boolean processCountMoney(SchemeService schemeService, TicketService ticketService, Period period) {
        try{
            if(StringUtil.isEmpty(period)){
                return false;
            }
            //需要进行金额汇总的票
            List<String> tickets = ticketService.queryTicketPrizeSummaryForPeriod(period);
            if(StringUtil.isEmpty(tickets)){//没有需要汇总奖金的票 返回成功
                return true;
            }

            for(String schemeId : tickets) {
                List<SchemeTicket> list = ticketService.queryTicketListBySchemeId(schemeId);
                if(StringUtil.isEmpty(list)){
                    continue;
                }
                //总奖金、税后奖金、加奖奖金、加奖税后奖金、网站加奖奖金、网站加奖税后奖金
                double totalmoney = 0,taxmoney = 0,addmoney = 0,taxaddmoney = 0, addmoneysite = 0, taxaddmoneysite = 0;
                String info = "", tcode = "";
                for(SchemeTicket ticket : list) {//汇总金额及奖级[由于奖级不能在SQL中汇总，故使用程序汇总]
                    if(ticket.getIsWin().intValue() == 2) {//已中奖
                        totalmoney += ticket.getTicketPrize();
                        taxmoney += ticket.getTicketPrizeTax();
                        addmoney += ticket.getTicketSubjoinPrize();
                        taxaddmoney += ticket.getTicketSubjoinPrizeTax();
                        if(!tcode.equals(ticket.getCodes())) {
                            info = addInfo(info, ticket.getBonusInfo());
                        }
                        tcode = ticket.getCodes();
                    }
                }

                boolean isZhuiHao = list.get(0).getZhuiHao();//是否追号方案
                if(!isZhuiHao) {
                    //设置中奖信息
                    Scheme scheme = schemeService.querySchemeInfoBySchemeOrderId(schemeId);//方案
                    if (scheme == null) {
                        LoggerUtil.printError("期次-奖金汇总任务", period, "找不到方案数据(方案号=" + schemeId + ")", null, logger);
                        continue;
                    }
                    //只有代办成功的方案才更新中奖状态
                    if (scheme.getSchemeStatus().intValue() == SchemeConstants.SCHEME_STATUS_CPCG) {
                        if ((totalmoney + addmoney + addmoneysite) > 0) {//中奖
                            //是否有加奖活动 待实现
                            scheme.setOpenStatus(2);
                        } else {//未中
                            scheme.setOpenStatus(1);
                        }
                        scheme.setOpenTime(new Date());
                    }

                    scheme.setPrize(spValue(totalmoney + addmoney + addmoneysite));
                    scheme.setPrizeSubjoin(addmoney);
                    scheme.setPrizeSubjoinSite(addmoneysite);//网站加奖暂未开通 默认0
                    scheme.setPrizeTax(spValue(taxmoney + taxaddmoney + taxaddmoneysite));
                    scheme.setPrizeSubjoinTax(taxaddmoney);
                    scheme.setPrizeSubjoinSiteTax(taxaddmoneysite);//网站加奖暂未开通 默认0
                    scheme.setPrizeBarrier(info);
                    scheme.setPrizeDetail(getPrizeDetail(period, scheme.getPrizeBarrier(), scheme.getSchemeMultiple(), scheme.getPrize(), scheme.getPrizeTax()));
                    schemeService.updateSchemeStatusPrize(scheme);
                } else {//追号订单
                    SchemeZhuiHao scheme = schemeService.queryZhuihaoSchemeInfoBySchemeOrderId(schemeId);//方案
                    if (scheme == null) {
                        LoggerUtil.printError("期次-奖金汇总任务", period, "找不到追号方案数据(方案号=" + schemeId + ")", null, logger);
                        continue;
                    }
                    //只有预约成功的方案才更新中奖状态
                    if (scheme.getSchemeStatus().intValue() == SchemeConstants.SCHEME_STATUS_CPCG) {
                        if ((totalmoney + addmoney + addmoneysite) > 0) {//中奖
                            //是否有加奖活动 待实现
                            //.................
                            scheme.setOpenStatus(2);
                        } else {//未中
                            scheme.setOpenStatus(1);
                        }
                        scheme.setOpenTime(new Date());
                    }
                    scheme.setPrize(spValue(totalmoney + addmoney + addmoneysite));
                    scheme.setPrizeSubjoin(addmoney);
                    scheme.setPrizeSubjoinSite(addmoneysite);//网站加奖暂未开通 默认0
                    scheme.setPrizeTax(spValue(taxmoney + taxaddmoney + taxaddmoneysite));
                    scheme.setPrizeSubjoinTax(taxaddmoney);
                    scheme.setPrizeSubjoinSiteTax(taxaddmoneysite);//网站加奖暂未开通 默认0
                    scheme.setPrizeBarrier(info);
                    scheme.setPrizeDetail(getPrizeDetail(period, scheme.getPrizeBarrier(), scheme.getSchemeMultiple(), scheme.getPrize(), scheme.getPrizeTax()));
                    schemeService.updateZhuihaoSchemeStatusPrize(scheme);

                    //更新主方案
                    Scheme mainScheme = schemeService.querySchemeInfoById(scheme.getSchemeId());
                    if(StringUtil.isEmpty(mainScheme)) {
                        LoggerUtil.printError("期次-奖金汇总任务", period, "找不到追号主方案数据(主键编号=" + scheme.getSchemeId() + ")", null, logger);
                        continue;
                    }
                    mainScheme.setPrize(spValue(mainScheme.getPrize() + totalmoney + addmoney + addmoneysite));
                    mainScheme.setPrizeSubjoin(mainScheme.getPrizeSubjoin() + addmoney);
                    mainScheme.setPrizeSubjoinSite(mainScheme.getPrizeSubjoinSite() + addmoneysite);
                    mainScheme.setPrizeTax(spValue(mainScheme.getPrizeTax() + taxmoney + taxaddmoney + taxaddmoneysite));
                    mainScheme.setPrizeSubjoinTax(mainScheme.getPrizeSubjoinTax() + taxaddmoney);
                    mainScheme.setPrizeSubjoinSiteTax(mainScheme.getPrizeSubjoinSiteTax() + taxaddmoneysite);
                    mainScheme.setPrizeBarrier(addInfo(mainScheme.getPrizeBarrier(), info));
                    mainScheme.setPrizeDetail(getPrizeDetail(period, mainScheme.getPrizeBarrier(), mainScheme.getSchemeMultiple(), mainScheme.getPrize(), mainScheme.getPrizeTax()));
                    //追期完成以后更新总方案状态（撤单总方案不更新）
                    if(StringUtil.isEmpty(mainScheme.getDonePeriod())) {
                        mainScheme.setDonePeriod(0);
                    }
                    int done = mainScheme.getDonePeriod() + 1;//完成期数
                    if(done > mainScheme.getPeriodSum()) {
                        done = mainScheme.getPeriodSum();
                    }
                    mainScheme.setDonePeriod(done);
                    boolean stopZhuiHao = false;//中奖后停止追号
                    if (StringUtil.isNotEmpty(mainScheme.getPrizeStop()) && mainScheme.getPrizeStop()) {
                        stopZhuiHao = true;
                    }
                    //中奖后停止
                    if(stopZhuiHao && mainScheme.getPrize() > 0 && mainScheme.getSchemeStatus() != SchemeConstants.SCHEME_STATUS_CDCG) {
                        if (done < mainScheme.getPeriodSum()) {//如果完成期数小于追期总数 则未完成期次全部撤单
                            //查出追号批次中所有还没出票的方案
                            List<SchemeZhuiHao> schemeZhuiHaoList = schemeService.queryZhuihaoSchemeInfoById(scheme.getSchemeId(), SchemeConstants.SCHEME_STATUS_ZFCG);
                            if (StringUtil.isNotEmpty(schemeZhuiHaoList)) {
                                for (SchemeZhuiHao zhuiHao : schemeZhuiHaoList) {
                                    Dto param = new BaseDto();
                                    param.put("id", zhuiHao.getId());
                                    param.put("iszh", "1");
                                    param.put("statusDesc", "中奖后停止追号");
                                    schemeService.updateSchemeForCancel(param);
                                }
                            }
                        }
                        done = mainScheme.getPeriodSum();
                    }
                    if(done == mainScheme.getPeriodSum()) {//追号完成
                        if(mainScheme.getPrize() > 0) {
                            mainScheme.setOpenStatus(2);
                        } else {
                            mainScheme.setOpenStatus(1);
                        }
                        mainScheme.setOpenTime(new Date());
                    }
                    schemeService.updateSchemeStatusPrize(mainScheme);//更新总方案
                }
                ticketService.updateTicketBonusState(schemeId, 3);//更新票过关状态
                LoggerUtil.printInfo("期次-奖金汇总任务", period, "方案号=" + schemeId + " 奖金汇总成功", logger);
            }
            tickets.clear();
            tickets = ticketService.queryTicketPrizeSummaryForPeriod(period);
            if(StringUtil.isEmpty(tickets)){//为空表示当前期全部完成汇总,否则返回false，流程继续执行
                return true;
            } else {
                tickets.clear();
                return false;
            }
        } catch (Exception e){
            LoggerUtil.printError("期次-奖金汇总任务", period, "奖金汇总异常", e, logger);
            return false;
        }
    }

    /**
     * 数字彩-自动派奖任务
     * @param schemeService
     * @param userService
     * @param period
     * @return
     */
    public static boolean authSendSmallMoney(SchemeService schemeService, UserService userService, Period period){
        try{
            if(StringUtil.isEmpty(period)){
                return false;
            }

            int maxPrize = SysConfig.getInt("AUTO_SEND_PRIZEMONEY");
            if(maxPrize == 0) {
                maxPrize = 2000;//默认
            }
            //满足自动派奖要求的方案
            List<Scheme> schemeList = schemeService.queryAutoSendMoneySzcSchemeList(period.getPeriod(), maxPrize);
            if(StringUtil.isEmpty(schemeList)){
                return true;
            }
            for(Scheme scheme : schemeList) {
                Dto param = new BaseDto();
                param.put("id", scheme.getId());
                param.put("iszh", scheme.getSchemeType());
                String zhText = scheme.getSchemeType() == 1? "追号" : "";
                int success = userService.updateSchemeForQrPj(param);
                if(success == 1){
                    LoggerUtil.printInfo("期次-自动派奖任务", period, zhText + "方案号=" + scheme.getSchemeOrderId() + " (奖金小于"+maxPrize+")自动派奖成功【派送奖金:"+scheme.getPrizeTax()+",税前奖金:"+scheme.getPrize()+"】", logger);
                } else {
                    LoggerUtil.printInfo("期次-自动派奖任务", period, zhText + "方案号=" + scheme.getSchemeOrderId() + " (奖金小于"+maxPrize+")自动派奖失败【税后奖金:"+scheme.getPrizeTax()+"】", logger);
                }
            }
            schemeList.clear();
            schemeList = schemeService.queryAutoSendMoneySzcSchemeList(period.getPeriod(), maxPrize);
            if(StringUtil.isEmpty(schemeList)){//为空表示全部处理完成,否则返回false，流程继续执行
                return true;
            } else {
                schemeList.clear();
                return false;
            }
        } catch (Exception e){
            LoggerUtil.printError("期次自动派奖任务", period, "自动派奖异常", e, logger);
            return false;
        }
    }

    /**
     * 奖金明细
     * @param period
     * @param prizeBarrier
     * @param mul
     * @param prize
     * @param prizeTax
     * @return
     */
    public static String getPrizeDetail(Period period, String prizeBarrier, Integer mul, double prize, double prizeTax) {
        if(StringUtil.isEmpty(prizeBarrier)) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        NumberFormat fm = new DecimalFormat("0.00");
        int[] wInfos = PluginUtil.SplitterInt(prizeBarrier, ",");//当前奖级注数
        int gradeNum = PrizesUtil.gradeNumber(period.getLotteryId());//彩种奖级
        for(int n=0; n<gradeNum; n++) {
            if(StringUtil.isNotEmpty(wInfos[n]) && wInfos[n] > 0) {//中奖注数
                if(buffer.length() > 0) {
                    buffer.append("&nbsp;");
                }
                //总奖金、税后奖金、（加奖追加）奖金、（加奖追加）税后奖金
                double totalmoney = 0, taxmoney = 0, zhuimoney = 0, taxzhuimoney = 0, addmoney = 0, taxaddmoney = 0;
                buffer.append(PrizesUtil.getPrizeName(period.getLotteryId(), n));
                buffer.append(wInfos[n]).append("注");
                double money = PrizesUtil.fixedMoney(period.getLotteryId(), n);//首先取固定奖金
                if(money < 0) {//如果浮动奖金则取期次奖级奖金
                    money = CalculationUtils.parseMoney(JSONObject.fromObject(period.getPrizeGrade()).getJSONObject(PrizesUtil.getPrizeName(period.getLotteryId(), n)).getString(LotteryGrade.dzjj)).doubleValue();
                }
                buffer.append("(单注" + fm.format(money)).append(")元,");
                totalmoney += money * wInfos[n];
                if(money > 10000) {
                    taxmoney += money * wInfos[n] * 0.8;
                } else {
                    taxmoney += money * wInfos[n];
                }

                //大乐透追加-六等奖无追加
                if(period.getLotteryId().equals(LotteryConstants.DLT) && n < 5) {
                    double zmoney = Math.floor(money * (n < 3 ? 0.6 : 0.5));//浮动奖追加奖金为基本投注对应单注奖金的60%，固定奖为50%
                    zhuimoney += zmoney * wInfos[6+n];
                    taxzhuimoney = zhuimoney;
                    if(zmoney > 10000) {
                        taxzhuimoney = zhuimoney * 0.8;
                    }
                    totalmoney += zhuimoney;
                    taxmoney += taxzhuimoney;
                }

                //加奖奖金-取期次奖级奖金
                double amoney = CalculationUtils.parseMoney(JSONObject.fromObject(period.getPrizeGrade()).getJSONObject(PrizesUtil.getPrizeName(period.getLotteryId(), n)).getString(LotteryGrade.jjjj)).doubleValue();//奖级加奖奖金
                addmoney += amoney * wInfos[n];
                taxaddmoney = addmoney;
                if(amoney > 10000) {
                    taxaddmoney = addmoney * 0.8;
                }
                totalmoney += addmoney;
                taxmoney += taxaddmoney;

                buffer.append("税前").append(fm.format(totalmoney)).append("元");
                if(zhuimoney > 0 || addmoney > 0) {
                    buffer.append("(");
                    if(zhuimoney > 0) {
                        buffer.append("含追加").append(fm.format(zhuimoney)).append("元");
                    }
                    if(addmoney > 0) {
                        if(zhuimoney > 0) {
                            buffer.append(",");
                        }
                        buffer.append("含加奖").append(fm.format(addmoney)).append("元");
                    }
                    buffer.append(")");
                }

                buffer.append(",税后").append(fm.format(taxmoney)).append("元");
                if(taxzhuimoney > 0 || taxaddmoney > 0) {
                    buffer.append("(");
                    if(taxzhuimoney > 0) {
                        buffer.append("含追加").append(fm.format(taxzhuimoney)).append("元");
                    }
                    if(taxaddmoney > 0) {
                        if(taxzhuimoney > 0) {
                            buffer.append(",");
                        }
                        buffer.append("含加奖").append(fm.format(taxaddmoney)).append("元");
                    }
                    buffer.append(")");
                }
            }
        }
        buffer.append("<br>派奖小结：方案倍数").append(mul).append("倍 ");
        buffer.append("税前<b>派奖总奖金</b>").append(fm.format(prize)).append("元,");
        buffer.append("税后<b>派奖总奖金</b>").append(fm.format(prizeTax)).append("元");
        return buffer.toString();
    }

    /**
     * 奖级累加
     * @param allInfo 总奖级
     * @param curInfo 当前奖级
     * @return
     */
    public static String addInfo(String allInfo, String curInfo) {
        if (StringUtil.isEmpty(allInfo)) {
            return curInfo;
        }
        if(StringUtil.isEmpty(curInfo)) {
            return allInfo;
        }

        if(allInfo.endsWith(",")) {
            allInfo = allInfo.substring(0, allInfo.length()-1);
        }
        if(curInfo.endsWith(",")) {
            curInfo = curInfo.substring(0, curInfo.length()-1);
        }

        int[] lsInfos = PluginUtil.SplitterInt(allInfo, ",");//累计奖级注数
        int[] wInfos = PluginUtil.SplitterInt(curInfo, ",");//当前奖级注数
        for (int i = 0; i < lsInfos.length; i++) {
            lsInfos[i] += wInfos[i];
        }
        String info = "";
        for(int a : lsInfos) {
            info = info + a + ",";
        }
        info = info.substring(0, info.length()-1);
        return info;
    }

    public static double spValue(double d) {
        return com.util.math.MathUtil.round(d, 2);
    }

    public static void main(String[] args) {
        //try {
            /*Integer lotteryId=10042;
            GamePlugin_82 obj = new GamePlugin_82();
            String cc = "0,123,123,123,12,123,12,12:1:2";
            GameCastCode[] codes = obj.parseGameCastCodes(cc);
            String code = "0,3,2,1,1,2,2,1";
            GameAwardCode gac = obj.buildAwardCode(code);
            int[] levels = obj.bingoMatch(codes, gac, obj.getGradeNum());
            if (levels != null) {
                for (int i = 0; i < levels.length; i++) {
                    System.out.println(levels[i]);
                }
            } else {
                System.out.println("未中");
            }
            String infos = "";
            if (StringUtil.isNotEmpty(levels) && levels.length > 0) {
                for (int lv : levels) {
                    infos += lv + ",";
                }
                infos = infos.substring(0, infos.length() - 1);
            }
            if(StringUtil.isEmpty(infos)) {
                return;
            }
            int[] winfos = SplitterInt(infos, ",");//中奖奖级对应注数
            int gradeNum = PrizesUtil.gradeNum(lotteryId);//彩种奖级
            double totalmoney = 0, taxmoney = 0, addmoney = 0, taxaddmoney = 0;
            String prize = "<PA:7SRL7SV948IUQ7N1><KV:YZZWY0F63WZJAWFS><NV:89YXWKQ7BB3TSASM>一等奖<KV:YZZWY0F63WZJAWFS>448193<PA:7SRL7SV948IUQ7N1>一等奖加奖<KV:YZZWY0F63WZJAWFS>-<PA:7SRL7SV948IUQ7N1>一等奖中奖注数<KV:YZZWY0F63WZJAWFS>2<PA:7SRL7SV948IUQ7N1>一等奖追加奖金<KV:YZZWY0F63WZJAWFS>-<PA:7SRL7SV948IUQ7N1>一等奖追加中奖注数<KV:YZZWY0F63WZJAWFS>-<PA:7SRL7SV948IUQ7N1>";
            for(int n=0; n<gradeNum; n++) {
                //普通奖金
                double money = PrizesUtil.fixedMoney(lotteryId, n);//首先取固定奖金
                if (money < 0) {//如果浮动奖金则取期次奖级奖金
                    money = CalculationUtils.parseMoney(new SchemeParameter(prize).getParameter(PrizesUtil.getPrizeName(10026, n))).doubleValue();
                }
                if (money <= 0) {
                    break;
                }
                totalmoney += money * winfos[n];
                if (money > 10000) {
                    taxmoney += money * winfos[n] * 0.8;
                } else {
                    taxmoney += money * winfos[n];
                }

                //大乐透追加-六等奖无追加
                if (lotteryId == LotteryContants.LOTTO && n < 5) {
                    double zmoney = Math.floor(money * (n < 3 ? 0.6 : 0.5));//浮动奖追加奖金为基本投注对应单注奖金的60%，固定奖为50%
                    totalmoney += zmoney * winfos[6 + n];
                    if (zmoney > 10000) {
                        taxmoney += zmoney * winfos[6 + n] * 0.8;
                    } else {
                        taxmoney += zmoney * winfos[6 + n];
                    }
                }

                //加奖奖金-取期次奖级奖金
                double amoney = CalculationUtils.parseMoney(new SchemeParameter(prize).getParameter(
                        PrizesUtil.getAddPrizeName(lotteryId, n))).doubleValue();//奖级加奖奖金
                addmoney += amoney * winfos[n];
                if (amoney > 10000) {
                    taxaddmoney += amoney * winfos[n] * 0.8;
                } else {
                    taxaddmoney += amoney * winfos[n];
                }
            }
            NumberFormat format=new DecimalFormat("0.00");
            System.out.println("税前totalmoney=" + format.format(totalmoney) + ",addmoney=" + format.format(addmoney) + ",税后taxmoney=" + format.format(taxmoney) + ",taxaddmoney=" +format.format(taxaddmoney));
            System.out.println(xxxx(infos, lotteryId, prize));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    /**
     * 奖金明细
     * @return
     */
    /*public static String xxxx(String info, int lotteryid, String prize) {
        if(StringUtil.isEmpty(info)) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        int[] winfos = SplitterInt(info, ",");//当前奖级注数
        int gradeNum = PrizesUtil.gradeNum(lotteryid);//彩种奖级
        for(int n=0; n<gradeNum; n++) {
            if(StringUtil.isNotEmpty(winfos[n]) && winfos[n] > 0) {//中奖注数
                if(buffer.length() > 0) {
                    buffer.append("<br>");
                }
                double totalmoney = 0,taxmoney = 0, zhuimoney = 0, taxzhuimoney = 0, addmoney = 0, taxaddmoney = 0;//总奖金、税后奖金、（加奖追加）奖金、（加奖追加）税后奖金
                buffer.append(PrizesUtil.getPrizeName(lotteryid, n));
                buffer.append(winfos[n]).append("注");
                double money = PrizesUtil.fixedMoney(lotteryid, n);//首先取固定奖金
                if(money < 0) {//如果浮动奖金则取期次奖级奖金
                    money = CalculationUtils.parseMoney(new SchemeParameter(prize).getParameter(PrizesUtil.getPrizeName(lotteryid, n))).doubleValue();
                }
                buffer.append("(单注" + money).append(")元，");
                totalmoney += money * winfos[n];
                if(money > 10000) {
                    taxmoney += money * winfos[n] * 0.8;
                } else {
                    taxmoney += money * winfos[n];
                }

                //大乐透追加-六等奖无追加
                if(lotteryid == LotteryContants.LOTTO && n < 5) {
                    double zmoney = Math.floor(money * (n < 3 ? 0.6 : 0.5));//浮动奖追加奖金为基本投注对应单注奖金的60%，固定奖为50%
                    zhuimoney = zmoney * winfos[6+n];
                    taxzhuimoney = zhuimoney;
                    if(zmoney > 10000) {
                        taxzhuimoney = zhuimoney * 0.8;
                    }
                    totalmoney += zhuimoney;
                    taxmoney += taxzhuimoney;
                }

                //加奖奖金-取期次奖级奖金
                double amoney = CalculationUtils.parseMoney(new SchemeParameter(prize).getParameter(
                        PrizesUtil.getAddPrizeName(lotteryid, n))).doubleValue();//奖级加奖奖金
                addmoney = amoney * winfos[n];
                taxaddmoney = addmoney;
                if(amoney > 10000) {
                    taxaddmoney = addmoney * 0.8;
                }
                totalmoney += addmoney;
                taxmoney += taxaddmoney;

                buffer.append("税前").append(totalmoney).append("元");
                if(zhuimoney > 0 || addmoney > 0) {
                    buffer.append("(");
                    if(zhuimoney > 0) {
                        buffer.append("含追加").append(zhuimoney).append("元");
                    }
                    if(addmoney > 0) {
                        if(zhuimoney > 0) {
                            buffer.append(",");
                        }
                        buffer.append("含加奖").append(addmoney).append("元");
                    }
                    buffer.append(")");
                }

                buffer.append("，税后").append(taxmoney).append("元");
                if(taxzhuimoney > 0 || taxaddmoney > 0) {
                    buffer.append("(");
                    if(taxzhuimoney > 0) {
                        buffer.append("含追加").append(taxzhuimoney).append("元");
                    }
                    if(taxaddmoney > 0) {
                        if(taxzhuimoney > 0) {
                            buffer.append(",");
                        }
                        buffer.append("含加奖").append(taxaddmoney).append("元");
                    }
                    buffer.append(")");
                }
            }
        }
        return buffer.toString();
    }*/
}
