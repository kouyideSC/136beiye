package com.caipiao.dao.common;

import com.caipiao.domain.common.City;
import com.caipiao.domain.common.Province;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

/**
 * 优惠券-数据库访问接口
 * @author  mcdog
 */
public interface CouponMapper
{
    /**
     * 查询优惠券(管理后台)
     * @author  mcdog
     */
    List<Dto> queryCoupons(Dto params);
    /**
     * 查询优惠券总记录条数(管理后台)
     * @author  mcdog
     */
    int queryCouponsCount(Dto params);
    /**
     * 添加优惠券
     * @author  mcdog
     */
    int addCoupons(Dto params);
    /**
     * 更新优惠券
     * @author  mcdog
     */
    int updateCoupons(Dto params);
}