package com.caipiao.taskcenter.award;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Period;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.lottery.PeriodService;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.service.user.UserService;
import com.caipiao.taskcenter.award.util.GyjAwardUtil;
import com.caipiao.taskcenter.award.util.LoggerUtil;
import com.caipiao.taskcenter.award.util.PrizesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 冠亚军期次任务
 * Created by kouyi on 2018/04/05.
 */
@Component("gyjMatchTask")
public class GyjMatchTask extends GyjAwardUtil {
    private static Logger logger = LoggerFactory.getLogger(GyjMatchTask.class);
    private HashMap<String, GamePluginAdapter> mapPlugin = new HashMap<String, GamePluginAdapter>();
    private HashMap<String, Period> mapPeriods = new HashMap<String, Period>();
    private static List<String> lottery = new ArrayList<String>();
    private long lastLoad = 0;

    @Autowired
    private PeriodService periodService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private SchemeService schemeService;
    @Autowired
    private UserService userService;

    static {
        lottery.add(LotteryConstants.GJ);//猜冠军
        lottery.add(LotteryConstants.GYJ);//冠亚军
    }


    /**
     * 状态处理
     */
    public void gyjProcessTask() {
        try {
            // 3分钟增量加载新期次
            if (lastLoad < System.currentTimeMillis() - 1000 * 60 * 3 || mapPeriods.isEmpty()) {
                for (String lotId : lottery) {
                    loadDataBasePeriod(lotId);
                }
                lastLoad = System.currentTimeMillis();
            }

            List<String> endPeriod = new ArrayList<String>();
            Iterator<String> iterator = mapPeriods.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Period period = mapPeriods.get(key);
                try {
                    int state = processPeriod(period);
                    if (state == 99) {
                        LoggerUtil.printInfo("冠亚军-期次任务", period, "卸载完成", logger);
                        endPeriod.add(key);
                    }
                } catch (Exception e) {
                    LoggerUtil.printError("冠亚军-期次任务", period, "处理异常", e, logger);
                }
            }

            for (String key : endPeriod) {
                Period iss = mapPeriods.remove(key);
                loadDataBasePeriod(iss.getLotteryId());
                iss = null;
            }
            endPeriod.clear();
        } catch (Exception ex) {
            logger.error("冠亚军-期次任务处理异常", ex);
        }
    }

    /**
     * 冠亚军计奖流程
     * @param period
     * @return
     */
    public int processPeriod(Period period) throws Exception {
        int state = period.getState();
        switch (state) {
            case LotteryConstants.STATE_DEFAULT : {//期次截止:投注截止
                Date endTime = period.getSellEndTime();
                if (endTime.getTime() < System.currentTimeMillis()) {
                    period.setState(LotteryConstants.STATE_ONE);
                    period.setSellStatus(LotteryConstants.STATUS_EXPIRE);
                    periodService.updatePeriodStatusById(period);
                    LoggerUtil.printInfo("冠亚军-期次截止任务", period, "截止销售", logger);
                }
                break;
            }
            case LotteryConstants.STATE_ONE: {//方案撤单:冠亚军期次官方截止前1分钟还没出票
                Date endTime = period.getAuthorityEndTime();
                if (endTime.getTime() < (System.currentTimeMillis() + 60000)) {
                    boolean flag = cancelSzcScheme(schemeService, ticketService, period);
                    if(flag) {
                        period.setState(LotteryConstants.STATE_THREE);
                        periodService.updatePeriodStatusById(period);
                        LoggerUtil.printInfo("冠亚军-自动撤单任务", period, "撤单完成", logger);
                    }
                }
                break;
            }
            case LotteryConstants.STATE_THREE: {//冠亚军开奖号码运营审核
                break;
            }
            case LotteryConstants.STATE_FOUR: {//开奖号同步到用户数字彩订单
                if(isCancel(period)) {
                    LoggerUtil.printError("冠亚军-系统审核开奖号任务", period, "运营审核开奖号不正确 回退状态重新审核 开奖号=" + period.getDrawNumber(), logger);
                    period.setState(LotteryConstants.STATE_THREE);
                    periodService.updatePeriodStatusById(period);
                    break;
                }

                boolean flag = drawNumberSynchronous(schemeService, period);
                if(flag) {
                    period.setState(LotteryConstants.STATE_FILE);
                    periodService.updatePeriodStatusById(period);
                    LoggerUtil.printInfo("冠亚军-开奖号同步订单任务", period, "开奖号同步完成 开奖号=" + period.getDrawNumber(), logger);
                }
                break;
            }
            case LotteryConstants.STATE_FILE: {//中奖匹配:计奖票表每张票中奖注数和奖级
                GamePluginAdapter plugin = InitPlugin.getPlugin(mapPlugin, period.getLotteryId());
                if(plugin == null) {
                    LoggerUtil.printInfo("冠亚军-中奖匹配任务", period, "无法获取彩种插件" + period.getLotteryId(), logger);
                    break;
                }
                boolean flag = numberBingoDrawingMatch(ticketService, period, plugin);
                if(flag) {//全部完成
                    period.setState(LotteryConstants.STATE_SIX);
                    periodService.updatePeriodStatusById(period);
                    LoggerUtil.printInfo("冠亚军-中奖匹配任务", period, "中奖匹配完成", logger);
                }
                break;
            }
            case LotteryConstants.STATE_SIX: {//计算奖金:计算票表每张票的中奖奖金
                boolean flag = numberCalculatePrizeMoney(ticketService, period);
                if(flag) {//全部完成
                    period.setState(LotteryConstants.STATE_SEVEN);
                    periodService.updatePeriodStatusById(period);
                    LoggerUtil.printInfo("冠亚军-计算奖金任务", period, "计算奖金完成", logger);
                }
                break;
            }
            case LotteryConstants.STATE_SEVEN: {//奖金汇总:票表奖金汇总到订单表中的用户订单
                boolean flag = processCountMoney(schemeService, ticketService, period);
                if(flag) {//全部完成
                    period.setState(LotteryConstants.STATE_EIGHT);
                    periodService.updatePeriodStatusById(period);
                    LoggerUtil.printInfo("冠亚军-奖金汇总任务", period, "奖金汇总完成", logger);
                }
                break;
            }
            case LotteryConstants.STATE_EIGHT: {//核对奖金:核对奖金是否正确
                period.setState(LotteryConstants.STATE_NINE);
                periodService.updatePeriodStatusById(period);
                LoggerUtil.printInfo("冠亚军-奖金核对任务", period, "奖金核对完成", logger);
                break;
            }
            case LotteryConstants.STATE_NINE: {//自动派奖:系统自动派奖
                boolean flag = authSendSmallMoney(schemeService, userService, period);
                if(flag) {//全部完成
                    period.setState(LotteryConstants.STATE_TEN);
                    periodService.updatePeriodStatusById(period);
                    LoggerUtil.printInfo("冠亚军-自动派奖任务", period, "自动派奖完成", logger);
                }
                break;
            }
            case LotteryConstants.STATE_TEN: {//过关统计-冠亚军暂不做任何逻辑
                period.setState(LotteryConstants.STATE_ELEVEN);
                periodService.updatePeriodStatusById(period);
                LoggerUtil.printInfo("冠亚军-过关统计任务", period, "过关统计完成", logger);
                break;
            }
            case LotteryConstants.STATE_ELEVEN: {//用户战绩统计-冠亚军暂不做任何逻辑
                period.setState(LotteryConstants.STATE_TWELVE);
                periodService.updatePeriodStatusById(period);
                LoggerUtil.printInfo("冠亚军-战绩统计任务", period, "战绩统计完成", logger);
                break;
            }
            case LotteryConstants.STATE_TWELVE: {//派送返点
                period.setState(LotteryConstants.STATE_THIRTEEN);
                periodService.updatePeriodStatusById(period);
                LoggerUtil.printInfo("冠亚军-派送返点任务", period, "派送返点完成", logger);
                break;
            }
            case LotteryConstants.STATE_THIRTEEN: {//期次处理结束
                period.setState(LotteryConstants.STATE_END);
                periodService.updatePeriodStatusById(period);
                LoggerUtil.printInfo("冠亚军-任务结束", period, "期次处理结束", logger);
                break;
            }
            case LotteryConstants.STATE_END: {
                break;
            }
        }
        return state;
    }

    //开奖号码验证-流程是否取消
    private boolean isCancel(Period period) {
        if(StringUtil.isEmpty(period)) {
            return true;
        }

        //期次未截止
        if(period.getSellStatus() != LotteryConstants.STATUS_EXPIRE) {
            return true;
        }
        if(StringUtil.isEmpty(period.getDrawNumber())) {
            return true;
        }
        //号码不合法
        if(!PrizesUtil.isNumber(period.getDrawNumber(), period.getLotteryId())) {
            return true;
        }

        return false;
    }

    /**
     * 加载同步数据库待处理期次
     * @param lottery
     */
    public void loadDataBasePeriod(String lottery) throws Exception {
        List<Period> list = periodService.queryDefaultStatusPeriods(lottery);
        if(StringUtil.isEmpty(list)) {
            return;
        }

        for(Period period : list) {
            String key = lottery + "_" + period.getId();
            Period last = mapPeriods.get(key);
            if(StringUtil.isEmpty(last)) {
                mapPeriods.put(key, period);
            } else {
                last.setSellStartTime(period.getSellStartTime());
                last.setSellEndTime(period.getSellEndTime());
                last.setAuthorityEndTime(period.getAuthorityEndTime());
                last.setUpdateFlag(period.getUpdateFlag());
                last.setPrizeGrade(period.getPrizeGrade());
                last.setState(period.getState());//同步人工处理状态
                last.setSellStatus(period.getSellStatus());
                last.setDrawNumber(period.getDrawNumber());
                last.setDrawNumberTime(period.getDrawNumberTime());
            }
        }
    }
}