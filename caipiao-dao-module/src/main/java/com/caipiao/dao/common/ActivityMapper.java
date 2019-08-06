package com.caipiao.dao.common;

import com.caipiao.domain.common.Activity;
import com.caipiao.domain.common.ActivityAddBonus;
import com.caipiao.domain.common.ActivityUser;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.user.UserAddBonusDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动模块功能接口定义
 * @author kouyi 2017-11-03
 */
public interface ActivityMapper {

    /**
     * 根据条件查询活动列表
     * @param activity
     * @return
     */
    List<Activity> queryActivityList(Activity activity);

    /**
     * 保存活动记录
     * @param activity
     * @return
     */
    int insertActivity(Activity activity);
    /**
     * 查询活动
     * @author  mcdog
     */
    List<Activity> queryActivitys(Dto params);
    /**
     * 查询活动总记录数(管理后台)
     * @author  mcdog
     */
    int queryActivitysCount(Dto params);
    /**
     * 新增活动
     * @author  mcdog
     */
    int addActivity(Dto params);
    /**
     * 更新活动
     * @author  mcdog
     */
    int updateActivity(Dto params);
    /**
     * 删除活动
     * @author  mcdog
     */
    int deleteActivity(Dto params);

    /**
     * 根据彩种查询加奖活动列表
     * @param lotteryId
     * @param passType
     * @param userId
     * @return
     */
    List<ActivityAddBonus> queryAddPrizeActivityList(@Param("lotteryId") String lotteryId,
                                                     @Param("passType") String passType, @Param("userId") Long userId);

    /**
     * 根据彩种查询加奖活动列表
     * @param lotteryId
     * @return
     */
    List<ActivityAddBonus> queryLotteryAddActivityList(@Param("lotteryId") String lotteryId);

    /**
     * 查询活动参与的所有用户
     * @return
     */
    List<Long> queryActivityJoinUser(@Param("activityId") Integer activityId);

    /**
     * 根据彩种查询加奖活动列表(后台管理)
     * @return
     */
    List<Dto> queryAddBounsList(Dto params);

    /**
     * 删除加奖活动(后台管理)
     * @param params
     * @return
     */
    int deleteAddBouns(Dto params);

    /**
     * 更新加奖活动（后台管理）
     * @param params
     * @return
     */
    int updateAddBouns(Dto params);

    /**
     * 新增加奖活动（后台管理）
     * @param params
     * @return
     */
    int addAddBouns(Dto params);

    /**
     * 查询用户当日已经加奖金额
     * @author kouyi
     */
    double queryUserDayAddprizeSum(@Param("userId") Long userId, @Param("activityId") Integer activityId,
                                   @Param("matchCode") String matchCode);

    /**
     * 新增用户加奖流水
     * @param detail
     * @return
     */
    int insertUserAddBounsDetail(UserAddBonusDetail detail);

    /**
     * 更新活动已使用额度
     * @param balance
     * @param id
     * @return
     */
    int updateAddBounsBalance(@Param("balance") Double balance, @Param("id") Integer id);

    /**
     * 查询用户加奖流水
     * @param params
     * @return
     */
    Dto queryUserAddBonusInfo(Dto params);

    /**
     * 用户领取活动
     * @param activityUser
     * @return
     */
    int insertActivityUser(ActivityUser activityUser);

    /**
     * 查询活动参与的用户列表（后台管理）
     * @param params
     * @return
     */
    List<Dto> queryActivityUserList(Dto params);

    /**
     * 查询用户是否参与活动
     * @param activityUser
     * @return
     */
    int isUserJoinActivity(ActivityUser activityUser);

}