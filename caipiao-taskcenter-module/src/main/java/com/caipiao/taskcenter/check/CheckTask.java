package com.caipiao.taskcenter.check;

import com.caipiao.service.check.CheckService;
import com.caipiao.service.common.MessageCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 彩票系统对账任务
 * Created by Kouyi on 2018/03/09
 */
@Component("checkTask")
public class CheckTask {
    private static Logger logger = LoggerFactory.getLogger(CheckTask.class);
    @Autowired
    private CheckService checkService;

    /**
     * 定时更新-彩票系统账户资金对账数据
     * @author kouyi
     */
    public void autoPlatFormCapitalData() {
        try {
            checkService.timeUpdatePlatFormCapitalData(logger);
        } catch (Exception e) {
            logger.error("[定时更新-彩票系统账户资金对账数据] 异常", e);
        }
    }

    /**
     * 定时更新-彩票系统订单和出票对账数据
     * @author kouyi
     */
    public void autoOrderAndTicketData() {
        try {
            checkService.timeSaveOrderAndTicket(logger);
        } catch (Exception e) {
            logger.error("[定时统计-彩票系统订单和出票对账数据] 异常", e);
        }
    }

    /**
     * 定时更新-彩票系统计奖与出票商兑奖奖金对账数据
     * @author kouyi
     */
    public void autoVoteAndSitePrizeData() {
        try {
            checkService.timeSaveVoteAndSitePrize(logger);
        } catch (Exception e) {
            logger.error("[定时统计-彩票系统计奖与出票商兑奖奖金对账数据] 异常", e);
        }
    }

    /**
     * 定时更新-彩票系统用户返利资金对账数据
     * @author kouyi
     */
    public void autoUserRebateData() {
        try {
            checkService.timeUpdateUserRebateData(logger);
        } catch (Exception e) {
            logger.error("[定时统计-彩票系统用户返利资金对账数据] 异常", e);
        }
    }

    /**
     * 定时更新-彩票系统上一天测试票中奖奖金统计数据
     * @author kouyi
     */
    public void autoYesterdayTestTicketPrizeMoneyData() {
        try {
            checkService.timeUpdateYesterdayTestTicketPrizeMoneyData(logger);
        } catch (Exception e) {
            logger.error("[定时统计-彩票系统上一天测试票中奖奖金统计数据] 异常", e);
        }
    }

    /**
     * 定时统计-世界杯期间-用户购彩金额前10名
     * @author kouyi
     */
    public void autoYesterdayWorldCupGcMoneyData() {
        try {
            checkService.timeSaveWorldCupGcMoneyStatic(logger);
        } catch (Exception e) {
            logger.error("[定时统计-世界杯期间-用户购彩金额前10名数据] 异常", e);
        }
    }
}
