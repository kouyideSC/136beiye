package com.caipiao.admin.service.weihu.coupon;

import com.caipiao.admin.service.config.SysConfig;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ActivityMapper;
import com.caipiao.dao.common.CouponMapper;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.cpadmin.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 优惠券-服务类
 */
@Service("couponyService")
public class CouponyService
{
    @Autowired
    private CouponMapper couponMapper;

    /**
     * 查询优惠券信息
     * @author	sjq
     */
    public List<Dto> queryCoupons(Dto params)
    {
        return couponMapper.queryCoupons(params);
    }

    /**
     * 查询优惠券总记录条数
     * @author	sjq
     */
    public int queryCouponsCount(Dto params)
    {
        return couponMapper.queryCouponsCount(params);
    }

    /**
     * 新增优惠券
     * @author	sjq
     */
    public int addCoupon(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.get("lotteryId")))
        {
            params.remove("lotteryId");
        }
        if(StringUtil.isEmpty(params.get("limitMoney")))
        {
            params.remove("limitMoney");
        }
        return couponMapper.addCoupons(params);
    }

    /**
     * 编辑优惠券
     * @author	sjq
     */
    public int editCoupon(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.get("lotteryId")))
        {
            params.remove("lotteryId");
        }
        return couponMapper.updateCoupons(params);
    }
}