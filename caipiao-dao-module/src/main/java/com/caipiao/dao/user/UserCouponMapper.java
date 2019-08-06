package com.caipiao.dao.user;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.UserAccount;

import java.util.List;

/**
 * 用户优惠券数据库访问接口
 * @author  mcdog
 */
public interface UserCouponMapper
{
    /**
     * 查询用户优惠券信息
     * @author  mcdog
     */
    List<Dto> queryUserCoupons(Dto params);
    /**
     * 查询用户优惠券总记录数
     * @author  mcdog
     */
    long queryUserCouponsCount(Dto params);
    /**
     * 更改用户优惠券状态(后台管理)
     * @author  mcdog
     */
    int updateUserCoupon(Dto params);
    /**
     * 查询优惠券信息(后台管理)
     * @author  mcdog
     */
    List<Dto> queryCoupons(Dto params);
    /**
     * 添加用户优惠券
     * @author  mcdog
     */
    int addUserCoupon(Dto params);
}