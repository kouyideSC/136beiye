package com.caipiao.taskcenter.award.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeZhuiHao;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameAwardCode;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.service.config.SysConfig;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 冠亚军计奖业务类
 * Created by kouyi on 2018/04/05.
 */
public class GyjAwardUtil {
    private static Logger logger = LoggerFactory.getLogger(GyjAwardUtil.class);

    /**
     * 冠亚军-已经截止未出票的方案自动撤单
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
                            ticketService.updateOutTicketStatusForCancel(ticket.getTicketId(), -2, "冠亚军截止-系统自动出票失败");
                        }
                    }
                    if (success == ticketList.size()) {//出票成功
                        continue;
                    }
                }
                scheme.setSchemeStatus(SchemeConstants.SCHEME_STATUS_ETF);
                scheme.setSchemeStatusDesc(SchemeConstants.schemeStatusMap.get(scheme.getSchemeStatus()));
                schemeService.updateSchemeTicketStatus(scheme);
                logger.info("冠亚军截止-方案状态处理成功 方案号=" + scheme.getSchemeOrderId());
            }
            return true;
        } catch (Exception e) {
            logger.error("冠亚军截止-方案状态处理异常", e);
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
            LoggerUtil.printError("冠亚军-抓取开奖号任务", period, "抓取开奖号码异常", e, logger);
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
            LoggerUtil.printError("冠亚军-开奖号同步订单任务", period, "开奖号同步订单异常", e, logger);
            return false;
        }
    }

    /**
     * 冠亚军-中奖匹配
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
            //查询彩种冠亚军需要过关的票
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
                        LoggerUtil.printInfo("冠亚军-中奖匹配任务", period, "方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + "[中奖匹配]成功 中奖["+outDel+"]", logger);
                    } else {
                        isWin = 1;
                        LoggerUtil.printInfo("冠亚军-中奖匹配任务", period, "方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + "[中奖匹配]成功 未中奖["+outDel+"] 开奖号=" + period.getDrawNumber() + " 投注号码=" + ticket.getCodes(), logger);
                    }

                    ticket.setBonusInfo(infos);
                    ticket.setIsWin((int) isWin);
                    ticket.setBonusState(1);
                    int row = ticketService.updateTicketPrizeMoney(ticket);
                    if (row == 0) {
                        LoggerUtil.printError("冠亚军-中奖匹配任务", period, "票号(" + ticket.getTicketId() + ") 更新数据库失败", null, logger);
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
            LoggerUtil.printError("冠亚军-中奖匹配任务", period, "中奖匹配异常", e, logger);
            return false;
        }
    }

    /**
     * 冠亚军-计算奖金
     * @param ticketService
     * @param period
     * @return
     */
    public static boolean numberCalculatePrizeMoney(TicketService ticketService, Period period) {
        try{
            if(StringUtil.isEmpty(period)){
                return false;
            }
            //查询彩种冠亚军需要计算奖金的票
            List<SchemeTicket> tickets = ticketService.queryGuoGuanTicketListByPeriod(period, 1);
            if(StringUtil.isEmpty(tickets)){//没有需要计算奖金的票 返回成功
                return true;
            }

            for(SchemeTicket ticket : tickets) {
                double totalmoney = 0,taxmoney = 0,addmoney = 0,taxaddmoney = 0;//总奖金、税后奖金、加奖奖金、加奖税后奖金
                String wInfo = ticket.getBonusInfo();
                if(ticket.getIsWin().intValue() == 2) {//已中奖
                    Map<String, String> spMap = getSchemeCodeSp(ticket.getCodesSp());
                    int[] wInfos = PluginUtil.SplitterInt(wInfo, ",");//中奖奖级
                    for(int n = 0; n < wInfos.length; n++) {
                        if(wInfos[n] > 0) {//中奖
                            Double spValue = StringUtil.parseDouble(spMap.get((n+1)+""));
                            totalmoney = spValue * ticket.getMultiple() * 2;
                            taxmoney = (totalmoney > 10000) ? CalculationUtils.spValue(CalculationUtils.muld(totalmoney, 0.8)) : totalmoney;
                            break;
                        }
                    }
                    LoggerUtil.printInfo("冠亚军-计算奖金任务", period, "方案号=" + ticket.getSchemeId() + " 票号=" + ticket.getTicketId() + "[计算奖金]成功 中奖[税前:"+(totalmoney+addmoney)+",税后:"+(taxmoney+taxaddmoney)+"]", logger);
                }

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
                    LoggerUtil.printError("冠亚军-计算奖金任务", period, "票号(" + ticket.getTicketId() + ") 更新数据库失败", null, logger);
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
            LoggerUtil.printError("冠亚军-计算奖金任务", period, "计算奖金异常", e, logger);
            return false;
        }
    }

    /**
     * 冠亚军-奖金汇总
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
                if (StringUtil.isEmpty(list)) {
                    continue;
                }
                //总奖金、税后奖金、加奖奖金、加奖税后奖金、网站加奖奖金、网站加奖税后奖金
                double totalmoney = 0, taxmoney = 0, addmoney = 0, taxaddmoney = 0, addmoneysite = 0, taxaddmoneysite = 0;
                String info = "", tcode = "";
                for (SchemeTicket ticket : list) {//汇总金额及奖级[由于奖级不能在SQL中汇总，故使用程序汇总]
                    if (ticket.getIsWin().intValue() == 2) {//已中奖
                        totalmoney += ticket.getTicketPrize();
                        taxmoney += ticket.getTicketPrizeTax();
                        addmoney += ticket.getTicketSubjoinPrize();
                        taxaddmoney += ticket.getTicketSubjoinPrizeTax();
                        if (!tcode.equals(ticket.getCodes())) {
                            info = addInfo(info, ticket.getBonusInfo());
                        }
                        tcode = ticket.getCodes();
                    }
                }

                //设置中奖信息
                Scheme scheme = schemeService.querySchemeInfoBySchemeOrderId(schemeId);//方案
                if (scheme == null) {
                    LoggerUtil.printError("冠亚军-奖金汇总任务", period, "找不到方案数据(方案号=" + schemeId + ")", null, logger);
                    continue;
                }
                //只有代办成功的方案才更新中奖状态
                if (scheme.getSchemeStatus().intValue() == SchemeConstants.SCHEME_STATUS_CPCG) {
                    if (totalmoney > 0) {//中奖
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

                ticketService.updateTicketBonusState(schemeId, 3);//更新票过关状态
                LoggerUtil.printInfo("冠亚军-奖金汇总任务", period, "方案号=" + schemeId + " 奖金汇总成功", logger);
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
            LoggerUtil.printError("冠亚军-奖金汇总任务", period, "奖金汇总异常", e, logger);
            return false;
        }
    }

    /**
     * 冠亚军-自动派奖任务
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
            Dto params = new BaseDto();
            for(Scheme scheme : schemeList) {
                Dto param = new BaseDto();
                param.put("id", scheme.getId());
                param.put("iszh", scheme.getSchemeType());
                int success = userService.updateSchemeForQrPj(param);
                if(success == 1){
                    LoggerUtil.printInfo("冠亚军-自动派奖任务", period, "方案号=" + scheme.getSchemeOrderId() + " (奖金小于"+maxPrize+")自动派奖成功【派送奖金:"+scheme.getPrizeTax()+",税前奖金:"+scheme.getPrize()+"】", logger);
                } else {
                    LoggerUtil.printInfo("冠亚军-自动派奖任务", period, "方案号=" + scheme.getSchemeOrderId() + " (奖金小于"+maxPrize+")自动派奖失败【税后奖金:"+scheme.getPrizeTax()+"】", logger);
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
            LoggerUtil.printError("冠亚军自动派奖任务", period, "自动派奖异常", e, logger);
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
                if(money < 0) {//如果浮动奖金则取冠亚军奖级奖金
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

                //加奖奖金-取冠亚军奖级奖金
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

    /**
     * 将出票sp串格式化为map-计奖使用
     * @param codeSp
     * @return
     */
    public static Map<String, String> getSchemeCodeSp(String codeSp) {
        Map<String, String> spMap = new HashMap<>();
        //18001=1@3.00/2@3.40/32@1000
        String[] sps = PluginUtil.splitter(PluginUtil.splitter(codeSp, "=")[1], "/");
        for (String sp : sps) {
            String[] ms = PluginUtil.splitter(sp, "@");
            spMap.put(ms[0], ms[1]);
        }
        return spMap;
    }
}
