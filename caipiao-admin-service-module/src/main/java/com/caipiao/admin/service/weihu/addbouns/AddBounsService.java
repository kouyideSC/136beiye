package com.caipiao.admin.service.weihu.addbouns;

import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ActivityMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.ActivityUser;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.vo.UserVo;
import com.caipiao.memcache.MemCached;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * 加奖活动相关服务
 * Created by kouyi on 2018/03/26.
 */
@Service("addBounsService")
public class AddBounsService
{
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private MemCached memCached;
    @Autowired
    private UserMapper userMapper;

    /**
     * 发布加奖活动
     * @param params
     * @return
     */
    public int insertAddBonus(Dto params) {
        if(StringUtil.isEmpty(params.get("beginTime")))
        {
            params.remove("beginTime");
        }
        if(StringUtil.isEmpty(params.get("endTime")))
        {
            params.remove("endTime");
        }
        return activityMapper.addAddBouns(params);
    }

    /**
     * 删除加奖活动
     * @param params
     * @return
     */
    public int deleteAddBouns(Dto params) {
        if(StringUtil.isEmpty(params.getAsInteger("id"))) {
            return 0;
        }
        return activityMapper.deleteAddBouns(params);
    }

    /**
     * 更新加奖活动
     * @param params
     * @return
     */
    public int updateAddBonus(Dto params) {
        if(StringUtil.isEmpty(params.getAsInteger("id"))) {
            return 0;
        }
        return activityMapper.updateAddBouns(params);
    }

    /**
     * 查询加奖活动列表
     * @return
     */
    public List<Dto> queryAddBonusDtoList(Dto params) {
        return activityMapper.queryAddBounsList(params);
    }

    /**
     * 用户领取活动
     * @author kouyi
     */
    public int insertActivityUser(ActivityUser activityUser) {
        try {
            activityUser.setActivityType(1);//默认加奖活动
            if(StringUtil.isEmpty(activityUser.getActivityId()) || StringUtil.isEmpty(activityUser.getUserId())) {
                return 0;
            }
            String mkey = "USER_APPLY_ADDACTIVITY_"+ activityUser.getUserId() + "_" + activityUser.getActivityId();
            if(memCached.contains(mkey)) {
                return -1;//重复领取活动资格
            }
            UserVo userInfo = userMapper.queryUserInfoBalaceById(activityUser.getUserId());
            if (StringUtil.isEmpty(userInfo)) {
                return 0;//用户信息不完整
            }
            if (userInfo.getStatus().intValue() != 1) {//用户状态
                return -2;//用户已被冻结或注销,请联系客服
            }
            List<Dto> dtoList = activityMapper.queryAddBounsList(new BaseDto("id", activityUser.getActivityId()));
            if(StringUtil.isEmpty(dtoList)) {
                return -3;//活动已过期或无效
            }
            Dto activity = dtoList.get(0);
            if(activity.getAsInteger("status")==0) {
                return -3;//活动已过期或无效
            }
            //不再有效时间内<开始时间
            Date begin = activity.getAsDate("beginTime", DateUtil.DEFAULT_DATE_TIME);
            if(StringUtil.isNotEmpty(begin) && begin.getTime() > new Date().getTime()) {
                return -3;//活动已过期或无效
            }
            //不再有效时间内>结束时间
            Date end = activity.getAsDate("endTime", DateUtil.DEFAULT_DATE_TIME);
            if(StringUtil.isNotEmpty(end) && end.getTime() < new Date().getTime()) {
                return -3;//活动已过期或无效
            }
            if(activityMapper.isUserJoinActivity(activityUser) > 0) {
                return -1;//重复领取活动资格
            }
            activityMapper.insertActivityUser(activityUser);
            memCached.set(mkey, 1, 24*60*60);//领取成功用户 缓存一天
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
