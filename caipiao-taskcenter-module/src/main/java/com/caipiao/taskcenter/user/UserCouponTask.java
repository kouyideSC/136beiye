package com.caipiao.taskcenter.user;

import com.caipiao.common.util.DateUtil;
import com.caipiao.dao.user.UserCouponMapper;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用户优惠券统计任务
 * @author sjq
 */
@Component("userCouponTask")
public class UserCouponTask
{
    private static Logger logger = LoggerFactory.getLogger(UserCouponTask.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserCouponMapper userCouponMapper;

    /**
     * 用户优惠券过期维护
     * @author  mcdog
     */
    public void doUserCouponExpire()
    {
        try
        {
            logger.info("[用户优惠券过期统计]统计开始.");
            List<Dto> userCouponList = new ArrayList<Dto>();//用来保存已到期且未使用的用户优惠券
            Calendar current = Calendar.getInstance();
            Dto params = new BaseDto();
            params.put("type","0");//设置类型为发行限制期限
            params.put("useStatus","1");//设置使用状态为未使用
            params.put("fxxzqxgqtime",DateUtil.formatDate(current.getTime(),DateUtil.DEFAULT_DATE_TIME));//过期时间(针对优惠券类型为发行限制期限)
            userCouponList.addAll(userCouponMapper.queryUserCoupons(params));//追加类型为发行限制期限的过期优惠券
            params.put("type","1");//设置类型为使用期限
            params.put("fxsyqxgqtime",params.get("fxxzqxgqtime"));
            params.remove("fxxzqxgqtime");
            userCouponList.addAll(userCouponMapper.queryUserCoupons(params));//追加类型为使用期限的过期优惠券
            if(userCouponList != null && userCouponList.size() > 0)
            {
                //循环用户优惠券,将用户优惠状态设置为已过期
                logger.info("[用户优惠券过期统计]本次共统计到" + userCouponList.size() + "条需要设置为过期的用户优惠券.");
                Dto updateDto = new BaseDto();
                for(Dto userCoupon : userCouponList)
                {
                    try
                    {
                        updateDto.put("id",userCoupon.get("cuid"));
                        updateDto.put("status","0");//设置优惠券使用状态为已过期
                        userCouponMapper.updateUserCoupon(updateDto);
                    }
                   catch(Exception e)
                   {
                       logger.error("[用户优惠券过期统计]更新用户优惠券使用状态发生异常!用户优惠券id=" + userCoupon.getAsString("cuid") + ",异常信息:", e);
                   }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("[用户优惠券过期统计]发生异常!异常信息:", e);
        }
        logger.info("[用户优惠券过期统计]统计结束.");
    }
}