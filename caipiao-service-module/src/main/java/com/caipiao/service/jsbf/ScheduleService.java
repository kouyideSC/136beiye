package com.caipiao.service.jsbf;

import com.caipiao.dao.jsbf.ScheduleMapper;
import com.caipiao.dao.lottery.LotteryMapper;
import com.caipiao.domain.jsbf.Schedule;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.vo.LotteryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 即时比分相关业务处理服务
 * Created by kouyi on 2017/11/10.
 */
@Service("scheduleService")
public class ScheduleService {
    private static Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    @Autowired
    private ScheduleMapper scheduleMapper;

    /**
     * 根据期次区间统计查询场次数
     * @param s
     * @param e
     * @return
     */
    public List<Schedule> queryPeriodNumList(String s, String e) {
        return scheduleMapper.queryPeriodNumList(s, e);
    }

    /**
     * 根据期次查询当前期及上期未打完的比赛对阵
     * @param period
     * @param lastPeriod 上期期号
     * @return
     */
    public List<Schedule> queryScheduleAndLastNoEndList(String period, String lastPeriod) {
        return scheduleMapper.queryScheduleAndLastNoEndList(period, lastPeriod);
    }

    /**
     * 根据期次查询即时比分对阵
     * @param period
     * @return
     */
    public List<Schedule> queryScheduleList(String period) {
        return scheduleMapper.queryScheduleList(period);
    }

    /**
     * 插入即时比分对阵信息
     * @param schedule
     * @return
     */
    public int insertSchedule(Schedule schedule) {
        try {
            scheduleMapper.insertSchedule(schedule);
            return schedule.getId().intValue();
        } catch(DuplicateKeyException e) {//已经存在
            return 0;
        } catch(Exception e) {
            logger.error("插入即时比分对阵信息异常 ScheduleId=" + schedule.getScheduleId(), e);
            return -1;
        }
    }

    /**
     * 更新对阵数据
     * @param sche
     * @return int
     */
    public int update(Schedule sche) throws Exception {
        return scheduleMapper.updateSchedule(sche);
    }

}
