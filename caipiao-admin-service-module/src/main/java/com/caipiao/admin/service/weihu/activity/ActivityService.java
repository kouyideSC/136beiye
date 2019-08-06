package com.caipiao.admin.service.weihu.activity;

import com.caipiao.admin.service.config.SysConfig;
import com.caipiao.admin.service.task.TaskService;
import com.caipiao.common.constants.Constants;
import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ActivityMapper;
import com.caipiao.dao.lottery.LotteryMapper;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动-服务类
 */
@Service("activityService")
public class ActivityService
{
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private TaskService taskService;

    /**
     * 查询活动信息
     * @author	sjq
     */
    public List<Activity> queryActivitys(Dto params)
    {
        List<Activity> activityList = activityMapper.queryActivitys(params);
        if(activityList != null && activityList.size() > 0)
        {
            for(Activity activity : activityList)
            {
                if(StringUtil.isNotEmpty(activity.getPictureUrl()))
                {
                    activity.setPictureLink(SysConfig.getHostStatic() + activity.getPictureUrl());
                }
            }
        }
        return activityList;
    }

    /**
     * 查询活动信息总记录条数
     * @author	sjq
     */
    public int queryActivitysCount(Dto params)
    {
        return activityMapper.queryActivitysCount(params);
    }

    /**
     * 新增活动
     * @author	sjq
     */
    public int addActivity(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.get("couponExpireTime")))
        {
            params.remove("couponExpireTime");
        }
        if(StringUtil.isEmpty(params.get("beginTime")))
        {
            params.remove("beginTime");
        }
        if(StringUtil.isEmpty(params.get("expireTime")))
        {
            params.remove("expireTime");
        }
        //如果活动类型为特定活动,且有选择优惠券,则设置活动优惠券信息
        if("2".equals(params.getAsString("activityType"))
                && (StringUtil.isNotEmpty(params.get("couponIds")) || StringUtil.isNotEmpty(params.get("czsCouponIds"))))
        {
            //如果优惠券赠送类型为充值送,且优惠券赠送模式为自定义模式,则活动优惠券赠送信息取czsCouponIds字段
            if("1".equals(params.getAsString("couponType")) && "1".equals(params.getAsString("couponMode")))
            {
                params.put("couponIds",params.get("czsCouponIds"));
            }
        }
        int result = activityMapper.addActivity(params);
        if(result > 0)
        {
            try
            {
                if("5".equals(params.getAsString("activityType")))
                {
                    Task task = new Task();
                    task.setTaskName(Constants.BANNER_APPSTART_UPDATE_TASK);
                    taskService.saveTask(task);
                }
                else
                {
                    Task task = new Task();
                    task.setTaskName(Constants.BANNER_HOME_UPDATE_TASK);
                    taskService.saveTask(task);
                }
            }
            catch(Exception e)
            {
                result = 0;
            }
        }
        return result;
    }

    /**
     * 编辑活动
     * @author	sjq
     */
    public int editActivity(Dto params) throws Exception
    {
        //非特定活动类型,则移除特定活动相关的属性
        if(!"2".equals(params.getAsString("activityType")))
        {
            params.remove("couponType");
            params.remove("couponMode");
            params.remove("couponIds");
            params.remove("couponExpireTime");
        }
        if(StringUtil.isEmpty(params.get("couponExpireTime")))
        {
            params.remove("couponExpireTime");
        }
        if(StringUtil.isEmpty(params.get("beginTime")))
        {
            params.remove("beginTime");
        }
        if(StringUtil.isEmpty(params.get("expireTime")))
        {
            params.remove("expireTime");
        }
        //如果活动类型为特定活动,且有选择优惠券,则设置活动优惠券信息
        if("2".equals(params.getAsString("activityType"))
                && (StringUtil.isNotEmpty(params.get("couponIds")) || StringUtil.isNotEmpty(params.get("czsCouponIds"))))
        {
            //如果优惠券赠送类型为充值送,且优惠券赠送模式为自定义模式,则活动优惠券赠送信息取czsCouponIds字段
            if("1".equals(params.getAsString("couponType")) && "1".equals(params.getAsString("couponMode")))
            {
                params.put("couponIds",params.get("czsCouponIds"));
            }
        }
        int result = activityMapper.updateActivity(params);
        if(result > 0)
        {
            try
            {
                if("5".equals(params.getAsString("activityType")))
                {
                    Task task = new Task();
                    task.setTaskName(Constants.BANNER_APPSTART_UPDATE_TASK);
                    taskService.saveTask(task);
                }
                else
                {
                    Task task = new Task();
                    task.setTaskName(Constants.BANNER_HOME_UPDATE_TASK);
                    taskService.saveTask(task);
                }
            }
            catch(Exception e)
            {
                result = 0;
            }
        }
        return result;
    }

    /**
     * 删除活动
     * @author	sjq
     */
    public int deleteActivity(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","缺少必要参数!");
            return 0;
        }
        int result = activityMapper.deleteActivity(params);
        if(result > 0)
        {
            try
            {
                Task task = new Task();
                task.setTaskName(Constants.BANNER_HOME_UPDATE_TASK);
                taskService.saveTask(task);
            }
            catch(Exception e)
            {
                result = 0;
            }
        }
        return result;
    }

    /**
     * 查询活动参与的用户列表
     * @author kouyi
     */
    public List<Dto> queryActivityUserList(Dto params) {
        return activityMapper.queryActivityUserList(params);
    }
}