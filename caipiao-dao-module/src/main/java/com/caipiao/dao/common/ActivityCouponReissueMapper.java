package com.caipiao.dao.common;

import com.caipiao.domain.common.Activity;
import com.caipiao.domain.common.ActivityAddBonus;
import com.caipiao.domain.common.ActivityCouponReissue;
import com.caipiao.domain.common.ActivityUser;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.UserAddBonusDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动优惠券补送数据访问接口
 * @author sjq
 */
public interface ActivityCouponReissueMapper
{
    /**
     * 查询活动优惠券补送
     * @author  mcdog
     */
    List<ActivityCouponReissue> queryActivityCouponReissues(Dto params);
    /**
     * 新增活动优惠券补送
     * @author  mcdog
     */
    int insertActivityCouponReissue(ActivityCouponReissue activityCouponReissue);
    /**
     * 更新活动优惠券补送状态
     * @author  mcdog
     */
    int updateActivityCouponReissue(Dto params);
}