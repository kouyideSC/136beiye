package com.caipiao.dao.user;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserRebate;
import com.caipiao.domain.user.UserRebateDetail;
import com.caipiao.domain.vo.UserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户神单模块功能接口定义
 * @author kouyi 2018-08-31
 */
public interface UserFollowMapper {
    /**
     * 查询某场比赛相关已结算的神单用户编号
     * @param lotteryId
     * @param matchCode
     * @return
     * @throws Exception
     */
    List<Long> queryFollowUserIdForMatch(@Param("lotteryId") String lotteryId, @Param("matchCode") String matchCode) throws Exception;

    /**
     * 初始化用户神单统计信息
     * @param lotteryId
     * @param userId
     * @throws Exception
     */
    void insertUserFollowStatis(@Param("lotteryId") String lotteryId, @Param("userId") Long userId) throws Exception;

    /**
     * 根据条件查询用户神单统计信息列表
     * @param dto
     * @return
     */
    List<Dto> queryUserFollowStatisList(Dto dto) throws Exception;

    /**
     * 查询用户神单统计信息是否存在
     * @param lotteryId
     * @param userId
     * @return
     * @throws Exception
     */
    int queryUserFollowIsExists(@Param("lotteryId") String lotteryId, @Param("userId") Long userId) throws Exception;

    /**
     * 更新用户神单统计信息
     * @param dto
     * @return
     */
    int updateUserFollowStatisInfo(Dto dto) throws Exception;

    /**
     * 查询某个用户最近一周的神单数据
     * @param lotteryId
     * @param userId
     * @return
     * @throws Exception
     */
    List<Dto> queryUserFollowForWeek(@Param("lotteryId") String lotteryId, @Param("userId") Long userId) throws Exception;

    /**
     * 统计某个用户最近一月的神单数据
     * @param lotteryId
     * @param userId
     * @return
     * @throws Exception
     */
    Dto queryUserFollowStatisForMonth(@Param("lotteryId") String lotteryId, @Param("userId") Long userId) throws Exception;

    /**
     * 查询某个用户最近10个已结算的神单数据
     * @param lotteryId
     * @param userId
     * @return
     * @throws Exception
     */
    List<Long> queryUserNearTenFollowScheme(@Param("lotteryId") String lotteryId, @Param("userId") Long userId) throws Exception;

    /**
     * 统计用户获取神单打赏总金额
     * @param lotteryId
     * @param userId
     * @return
     * @throws Exception
     */
    Dto queryUserFollowRewardMoneyStatis(@Param("lotteryId") String lotteryId, @Param("userId") Long userId) throws Exception;

    /**
     * 查询有周榜数据但已经一周没有发神单的用户列表
     * @param lotteryId
     * @return
     * @throws Exception
     */
    List<Long> queryWeekNoFollowUserList(@Param("lotteryId") String lotteryId) throws Exception;

    /**
     * 查询有月榜数据但已经一月没有发神单的用户列表
     * @param lotteryId
     * @return
     * @throws Exception
     */
    List<Long> queryMonthNoFollowUserList(@Param("lotteryId") String lotteryId) throws Exception;

}
