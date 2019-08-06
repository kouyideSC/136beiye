package com.caipiao.dao.lottery;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.vo.LotteryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 彩种模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface LotteryMapper
{
    /**
     * 查询在售彩种列表
     * @return
     */
    List<LotteryVo> queryLotterySaleList(Dto params);
    /**
     * 根据彩种编号查询彩种信息
     * @param lotteryId
     * @return
     */
    Lottery queryLotteryInfo(String lotteryId);
    /**
     * 查询彩种信息(管理后台)
     * @author	sjq
     */
    List<Dto> queryLotterys(Dto params);
    /**
     * 查询彩种总记录条数(管理后台)
     * @author	sjq
     */
    int queryLotterysCount(Dto params);
    /**
     * 编辑彩种(管理后台)
     * @author	sjq
     */
    int editLottery(Dto params);
    /**
     * 编辑彩种销售状态(管理后台)
     * @author	sjq
     */
    int editLotterySaleStatus(Dto params);
}