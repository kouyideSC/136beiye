package com.caipiao.dao.check;

import com.caipiao.domain.cpadmin.Dto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台对账模块功能接口定义
 * @author kouyi 2018-03-09
 */
public interface CheckMapper
{
    /**
     * 查询平台资金报表
     * @return
     */
    List<Dto> queryPlatFormCapital(Dto params) throws Exception;

    /**
     * 查询平台资金报表-总条数（后台管理）
     * @author kouyi
     */
    int queryPlatFormCapitalCount(Dto params) throws Exception;

    /**
     * 查询平台方案和票报表
     * @return
     */
    List<Dto> querySchemeTicket(Dto params) throws Exception;

    /**
     * 查询平台方案和票报表-总条数（后台管理）
     * @author kouyi
     */
    int querySchemeTicketCount(Dto params) throws Exception;

    /**
     * 查询平台兑奖计奖报表
     * @return
     */
    List<Dto> queryVoteSitePrize(Dto params) throws Exception;

    /**
     * 查询平台兑奖计奖报表-总条数（后台管理）
     * @author kouyi
     */
    int queryVoteSitePrizeCount(Dto params) throws Exception;

    /**
     * 查询平台兑奖报表
     * @return
     */
    List<Dto> queryVoteSiteAward(Dto params) throws Exception;

    /**
     * 汇总某天平台总加奖金额
     * @return
     */
    Double queryDateSchemeSubjoin(Dto params) throws Exception;

    /**
     * 查询平台用户返利报表
     * @return
     */
    List<Dto> queryUserRebate(Dto params) throws Exception;

    /**
     * 查询平台用户返利报表-总条数（后台管理）
     * @author kouyi
     */
    int queryUserRebateCount(Dto params) throws Exception;

    /**
     * 汇总当前平台账户总资金
     * @return
     */
    Double sumPlatFormCapital() throws Exception;

    /**
     * 新建当天平台总资金数据
     * @param beginCapital
     *          当天开始数据
     * @return
     */
    int savePlatFormCapital(Double beginCapital) throws Exception;

    /**
     * 查询当天平台资金报表期末数据
     * @return
     * @throws Exception
     */
    Dto queryPlatFormCapitalStatisData() throws Exception;

    /**
     * 更新上一天平台资金对账数据
     * @param data
     *          上一天截止数据
     * @return
     */
    int updatePlatFormCapital(Dto data) throws Exception;

    /**
     * 初始订单和票对账数据入库
     * @return
     */
    int saveOrderAndTicket() throws Exception;

    /**
     * 初始出票商兑奖奖金与系统计奖奖金对账数据入库
     * @return
     */
    int saveVoteAndSitePrize() throws Exception;

    /**
     * 查询平台返利余额报表
     * @return
     */
    Double sumUserRebate() throws Exception;

    /**
     * 初始期初返利数据入库
     * @param beginRebate
     *          当天开始数据
     * @return
     */
    int saveUserRebate(Double beginRebate) throws Exception;

    /**
     * 查询当天用户返利报表数据
     * @return
     * @throws Exception
     */
    Dto queryUserRebateStatisData() throws Exception;

    /**
     * 更新上一天平台用户返利对账数据
     * @param data
     *          上一天截止数据
     * @return
     */
    int updateUserRebate(Dto data) throws Exception;

    /**
     * 统计彩票系统上一天测试票中奖奖金总额
     * @return
     */
    Double sumYesterdayTestTicketPrizeMoney() throws Exception;

    /**
     * 更新彩票系统上一天测试票中奖奖金统计数据
     * @param endCapital
     *          上一天截止数据
     * @return
     */
    int updateYesterdayTestTicketPrizeMoney(Double endCapital) throws Exception;

    /**
     * 统计世界杯期间-用户购彩金额前10名
     * @return
     * @throws Exception
     */
    int saveWorldCupGcMoneyStatic() throws Exception;

    /**
     * 查询世界杯期间某天的用户购彩榜单
     * @param dateStr
     * @return
     * @throws Exception
     */
    List<Dto> getWorldCupRankList(String dateStr) throws Exception;
}