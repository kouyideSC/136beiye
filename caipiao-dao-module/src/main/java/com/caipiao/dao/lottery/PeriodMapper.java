package com.caipiao.dao.lottery;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.vo.KaiJiangVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 期次模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface PeriodMapper {

    /**
     * 查询待处理期次列表-计奖程序
     * @return
     */
    List<Period> queryDefaultStatusPeriods(String lotteryId);

    /**
     * 查询未截止的最近一期期次
     * @param lotteryId
     * @return
     */
    Period queryCurrentFirstPeriod(String lotteryId);

    /**
     * 查询彩种当前期次
     * @param lotteryId
     * @return
     */
    List<Period> queryCurrentPeriodByLottery(String lotteryId);


    /**
     * 根据期次范围查询期次列表
     * @param params
     * @return
     */
    List<Period> queryPeriodsByRange(Dto params);

    /**
     * 保存期次信息
     * @param record
     * @return
     */
    int insertPeriod(Period record);

    /**
     * 根据彩种期号查询期次信息
     * @param lotteryId
     * @param period
     * @return
     */
    Period queryPeriodByPerod(@Param("lotteryId")String lotteryId, @Param("period")String period);

    /**
     * 根据唯一编号-修改期次状态
     * @param period
     * @return
     */
    int updatePeriodStatusById(Period period);

    /**
     * 根据id更新期次数据
     * @param period
     * @return
     */
    int updatePeriodById(Period period);

    /**
     * 根据期次编号-修改期次时间
     * @param period
     * @return
     */
    int updatePeriodStatusByPeriodId(Period period);

    /**
     * 根据彩种id查询最新一期的开奖信息
     * @author  mcdog
     * @param   params   查询参数(lotteryId-彩种id appStatus-app销售状态)
     */
    KaiJiangVo queryLatestKjByLotteryId(Dto params);

    /**
     * 查询期次信息(管理后台)
     * @author	mcdog
     */
    List<Dto> queryPeriods(Dto params);
    /**
     * 查询期次总记录条数(管理后台)
     * @author	mcdog
     */
    int queryPeriodsCount(Dto params);
    /**
     * 修改期次(管理后台)
     * @author	mcdog
     */
    int editPeriod(Dto params);
    /**
     * 删除期次(管理后台)
     * @author	mcdog
     */
    int deletePeriod(Dto params);
    /**
     * 审核期次(管理后台)
     * @author	mcdog
     */
    int auditPeriod(Dto params);
    /**
     * 新增期次(管理后台)
     * @author	mcdog
     */
    int addPeriod(Map params);
    /**
     * 查询彩种最大期次(管理后台)
     * @author	mcdog
     */
    Dto queryMaxPeriod(Dto params);
    /**
     * 查询范围期次(管理后台)
     * @author	mcdog
     */
    List<Dto> queryRangePeriods(Dto params);
    /**
     * 根据起始期次等信息查询包含起始期次的在售期次信息
     * @author	mcdog
     * @param   params  参数对象(lotteryId-彩种id startPeriod-起始期次 psum-要查询的总期次数)
     */
    List<Period> queryPeriodInfoByStartPeriod(Map<String,Object> params);
    /**
     * 查询当前在售期次
     * @author  mcdog
     * @param   lotteryId   彩种id
     */
    Period queryCurrentSellPeriod(String lotteryId);

    /**
     * 查询已经开奖但无开奖号码的期次
     * @param lotteryId
     * @return
     */
    List<Period> queryPeriodAlreadyDrawList(String lotteryId);

    /**
     * 重置期次计奖状态-回退至开奖号码待审核
     * @param params
     * @return
     */
    int updatePeriodRebackState(Dto params);
}