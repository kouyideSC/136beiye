package com.caipiao.dao.jsbf;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.jsbf.Schedule;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.vo.LotteryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 即时比分竞彩对阵模块功能接口定义
 * @author kouyi 2017-11-20
 */
public interface ScheduleMapper {

    /**
     * 根据期次区间统计查询场次数
     * @param speid
     * @param epeid
     * @return
     */
    List<Schedule> queryPeriodNumList(@Param("speid") String speid, @Param("epeid") String epeid);

    /**
     * 根据期次查询当前期及上期未打完的比赛对阵
     * @param period
     * @param lastPeriod 上期期号
     * @return
     */
    List<Schedule> queryScheduleAndLastNoEndList(@Param("period") String period, @Param("lastPeriod") String lastPeriod);

    /**
     * 根据期次查询即时比分对阵
     * @param period
     * @return
     */
    List<Schedule> queryScheduleList(String period);

    /**
     * 查询即时比分对阵信息
     * @param record
     * @return
     */
    int queryScheduleInfo(Schedule record);


    /**
     * 更新即时比分对阵信息
     * @param record
     * @return
     */
    int updateSchedule(Schedule record);

    /**
     * 插入即时比分对阵信息
     * @param record
     * @return
     */
    int insertSchedule(Schedule record);

}