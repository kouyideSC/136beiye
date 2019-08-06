package com.caipiao.service.check;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.check.CheckMapper;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 对账相关业务处理服务
 * Created by kouyi on 2018/03/09.
 */
@Service("checkService")
public class CheckService {
    @Autowired
    private CheckMapper checkMapper;

    /**
     * 定时更新平台账户资金对账数据
     */
    public void timeUpdatePlatFormCapitalData(Logger logger) throws ServiceException {
        if(logger == null) {
            return;
        }
        try {
            logger.info("[任务-定时更新-彩票系统账户资金对账数据] 当前时间:" + DateUtil.dateDefaultFormat(new Date()));

            //统计平台当前账户总余额
            Double curCapital = checkMapper.sumPlatFormCapital();
            if(StringUtil.isEmpty(curCapital)) {
                curCapital = new Double(0);
            }
            logger.info("[任务-定时更新-彩票系统账户资金对账数据] 平台账户当前资金总余额:" + curCapital.doubleValue()+"元");

            //查询当天平台资金报表期末数据
            Dto data = checkMapper.queryPlatFormCapitalStatisData();
            data.put("endCapital", curCapital);
            //更新上一天平台资金对账数据
            checkMapper.updatePlatFormCapital(data);
            logger.info("[任务-定时更新-彩票系统账户资金对账数据] 更新上一天账户资金对账数据完毕");

            //新建当天平台资金初始数据
            checkMapper.savePlatFormCapital(curCapital);
            logger.info("[任务-定时更新-彩票系统账户资金对账数据] 新建当天账户资金初始数据完毕");

        } catch (Exception e){
            logger.error("[任务-定时更新-彩票系统账户资金对账数据] 异常 时间=" + new Date(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 定时初始订单和票对账数据入库
     */
    public void timeSaveOrderAndTicket(Logger logger) throws ServiceException {
        if(logger == null) {
            return;
        }
        try {
            logger.info("[任务-定时统计-彩票系统订单和出票对账数据] 当前时间:" + DateUtil.dateDefaultFormat(new Date()));
            //统计系统订单和出票对账数据
            checkMapper.saveOrderAndTicket();
            logger.info("[任务-定时更新-彩票系统订单和出票对账数据] 新建上一天订单和出票数据完毕");

        } catch (Exception e){
            logger.error("[任务-定时统计-彩票系统订单和出票对账数据] 异常 时间=" + new Date(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 定时初始出票商兑奖奖金与系统计奖奖金对账数据入库
     */
    public void timeSaveVoteAndSitePrize(Logger logger) throws ServiceException {
        if(logger == null) {
            return;
        }
        try {
            logger.info("[任务-定时统计-彩票系统计奖与出票商兑奖奖金对账数据] 当前时间:" + DateUtil.dateDefaultFormat(new Date()));
            //彩票系统计奖与出票商兑奖奖金对账数据
            checkMapper.saveVoteAndSitePrize();
            logger.info("[任务-定时更新-彩票系统计奖与出票商兑奖奖金对账数据] 新建上一天出票商兑奖奖金与系统计奖奖金数据完毕");

        } catch (Exception e){
            logger.error("[任务-定时统计-彩票系统计奖与出票商兑奖奖金对账数据] 异常 时间=" + new Date(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 定时更新平台用户返利对账数据
     */
    public void timeUpdateUserRebateData(Logger logger) throws ServiceException {
        if(logger == null) {
            return;
        }
        try {
            logger.info("[任务-定时更新-彩票系统用户返利对账数据] 当前时间:" + DateUtil.dateDefaultFormat(new Date()));

            //统计平台当前用户返利总余额
            Double curRebate = checkMapper.sumUserRebate();
            if(StringUtil.isEmpty(curRebate)) {
                curRebate = new Double(0);
            }
            logger.info("[任务-定时更新-彩票系统用户返利对账数据] 平台返利账户当前总余额:" + curRebate.doubleValue()+"元");

            //查询当天平台资金报表期末数据
            Dto data = checkMapper.queryUserRebateStatisData();
            data.put("endRebate", curRebate);

            //更新上一天平台用户返利资金对账数据
            checkMapper.updateUserRebate(data);
            logger.info("[任务-定时更新-彩票系统用户返利对账数据] 更新上一天用户返利资金对账数据完毕");

            //新建当天平台用户返利资金初始数据
            checkMapper.saveUserRebate(curRebate);
            logger.info("[任务-定时更新-彩票系统用户返利对账数据] 新建当天返利资金初始数据完毕");

        } catch (Exception e){
            logger.error("[任务-定时更新-彩票系统用户返利对账数据] 异常 时间=" + new Date(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 定时更新彩票系统上一天测试票中奖奖金统计数据
     */
    public void timeUpdateYesterdayTestTicketPrizeMoneyData(Logger logger) throws ServiceException {
        if(logger == null) {
            return;
        }
        try {
            logger.info("[任务-定时更新-彩票系统上一天测试票中奖奖金统计数据] 当前时间:" + DateUtil.dateDefaultFormat(new Date()));
            //统计彩票系统上一天测试票中奖奖金
            Double prizeMoney = checkMapper.sumYesterdayTestTicketPrizeMoney();
            if(StringUtil.isEmpty(prizeMoney)) {
                prizeMoney = new Double(0);
            }
            logger.info("[任务-定时更新-彩票系统上一天测试票中奖奖金统计数据] 平台上一天测试票中奖奖金:" + prizeMoney.doubleValue()+"元");

            //更新彩票系统上一天测试票中奖奖金统计数据
            checkMapper.updateYesterdayTestTicketPrizeMoney(prizeMoney);
            logger.info("[任务-定时更新-彩票系统上一天测试票中奖奖金统计数据] 更新彩票系统上一天测试票中奖奖金统计数据完毕");

        } catch (Exception e){
            logger.error("[任务-定时更新-彩票系统上一天测试票中奖奖金统计数据] 异常 时间=" + new Date(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 定时统计-世界杯期间-用户购彩金额前10名
     */
    public void timeSaveWorldCupGcMoneyStatic(Logger logger) throws ServiceException {
        if(logger == null) {
            return;
        }
        try {
            logger.info("[任务-定时统计-世界杯期间-用户购彩金额前10名数据] 当前时间:" + DateUtil.dateDefaultFormat(new Date()));
            checkMapper.saveWorldCupGcMoneyStatic();
            logger.info("[任务-定时统计-世界杯期间-用户购彩金额前10名数据数据] 统计数据完毕");
        } catch (Exception e){
            logger.error("[任务-定时统计-世界杯期间-用户购彩金额前10名数据数据] 异常 时间=" + new Date(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }
}