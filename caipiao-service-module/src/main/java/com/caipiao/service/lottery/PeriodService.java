package com.caipiao.service.lottery;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.JczqUtils;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.domain.base.MatchBean;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 期次相关业务处理服务
 * Created by kouyi on 2017/11/07.
 */
@Service("periodService")
public class PeriodService {
    private static Logger logger = LoggerFactory.getLogger(PeriodService.class);
    @Autowired
    private PeriodMapper periodMapper;
    @Autowired
    private TaskService taskService;

    /**
     * 查询待处理期次列表-计奖程序
     * @return
     */
    public List<Period> queryDefaultStatusPeriods(String lotteryId) throws Exception {
        return periodMapper.queryDefaultStatusPeriods(lotteryId);
    }

    /**
     * 查询未截止-最近一期期次
     * @param lotteryId
     * @return
     */
    public Period queryCurrentFirstPeriod(String lotteryId) throws Exception {
        return periodMapper.queryCurrentFirstPeriod(lotteryId);
    }

    /**
     * 查询彩种当前期次
     * @param lotteryId
     * @return
     */
    public List<Period> queryCurrentPeriodByLottery(String lotteryId) throws Exception {
        return periodMapper.queryCurrentPeriodByLottery(lotteryId);
    }


    /**
     * 根据彩种期号查询期次信息
     * @param lotteryId
     * @return
     */
    public Period queryPeriodByPerod(String lotteryId, String period) throws Exception {
        return periodMapper.queryPeriodByPerod(lotteryId, period);
    }

    /**
     * 保存或更新竞彩期次信息-胜负彩期次处理
     * @param period
     */
    public void saveOrUpdateSfcPeriod(Period period) throws ServiceException, Exception {
        try {
            if(!LotteryConstants.lotteryMap.containsKey(period.getLotteryId())) {
                return;
            }
            Period pe = periodMapper.queryPeriodByPerod(period.getLotteryId(), period.getPeriod());
            if(StringUtil.isNotEmpty(pe)) {
                periodMapper.updatePeriodById(period);
                taskService.saveTask(new Task(Constants.periodUpdateTaskMaps.get(period.getLotteryId())));//在售期次文件更新任务
            } else {
                periodMapper.insertPeriod(period);
            }
        } catch (Exception e){
            logger.error("[保存或更新竞彩期次异常] period=" + period.getPeriod(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 保存或更新竞彩期次信息-竞彩期次处理
     * @param period
     */
    public void saveOrUpdateJcPeriod(String lotteryId, String period, Date endTime) throws ServiceException, Exception {
        try {
            if(!LotteryConstants.lotteryMap.containsKey(lotteryId)) {
                return;
            }
            Period pe = periodMapper.queryPeriodByPerod(lotteryId, period);
            if(StringUtil.isNotEmpty(pe)) {
                if(endTime.getTime() > pe.getSellEndTime().getTime()) {
                    pe.setSellEndTime(endTime);
                    pe.setAuthorityEndTime(DateUtil.addMinute(endTime, 10));
                    pe.setDrawNumberTime(DateUtil.addHour(endTime, 2));
                    periodMapper.updatePeriodById(pe);
                }
            } else {
                Period pd = new Period();
                pd.setLotteryId(lotteryId);
                pd.setPeriod(period);
                pd.setSellStatus(1);
                pd.setSellStartTime(new Date());
                pd.setSellEndTime(endTime);
                pd.setAuthorityEndTime(DateUtil.addMinute(endTime, 10));
                pd.setDrawNumberTime(DateUtil.addHour(endTime, 2));
                periodMapper.insertPeriod(pd);
            }
        } catch (Exception e){
            logger.error("[保存或更新竞彩期次异常] period=" + period, e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据期次唯一编号-更新期次状态
     * @param period
     */
    public void updatePeriodStatusById(Period period) throws ServiceException, Exception {
        try {
            if(!LotteryConstants.lotteryMap.containsKey(period.getLotteryId())) {
                return;
            }
            periodMapper.updatePeriodStatusById(period);
        } catch (Exception e){
            logger.error("[更新期次状态异常] period=" + period.getPeriod(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询期次信息
     * @author	sjq
     */
    public List<Dto> queryPeriods(Dto params)
    {
        return periodMapper.queryPeriods(params);
    }

    /**
     * 根据期次编号-更新期次数据
     * @param period
     */
    public void updatePeriodStatusByPeriodId(Period period) throws ServiceException, Exception {
        try {
            if(!LotteryConstants.lotteryMap.containsKey(period.getLotteryId())) {
                return;
            }
            periodMapper.updatePeriodStatusByPeriodId(period);
        } catch (Exception e){
            logger.error("[更新期次数据异常] period=" + period.getPeriod(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询已经开奖但无开奖号码的期次
     * @param lotteryId
     * @return
     */
    public List<Period> queryPeriodAlreadyDrawList(String lotteryId) {
        try {
            return periodMapper.queryPeriodAlreadyDrawList(lotteryId);
        } catch (Exception e){
            logger.error("[查询已经开奖但无开奖号码的期次异常] lotteryId=" + lotteryId, e);
            return null;
        }
    }
}