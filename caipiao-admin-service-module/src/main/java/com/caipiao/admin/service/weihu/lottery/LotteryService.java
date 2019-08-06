package com.caipiao.admin.service.weihu.lottery;

import com.caipiao.admin.service.config.SysConfig;
import com.caipiao.admin.service.task.TaskService;
import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.lottery.LotteryMapper;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.memcache.MemCached;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 彩种-服务类
 */
@Service("lotteryService")
public class LotteryService
{
    @Autowired
    private LotteryMapper lotteryMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MemCached memcached;

    /**
     * 查询彩种信息
     * @author	sjq
     */
    public List<Dto> queryLotterys(Dto params)
    {
        List<Dto> lotteryList = lotteryMapper.queryLotterys(params);
        if(lotteryList != null && lotteryList.size() >0)
        {
            for(Dto lottery : lotteryList)
            {
                if(StringUtil.isNotEmpty(lottery.get("activityImg")))
                {
                    lottery.put("activityImgLink",(SysConfig.getHostStatic() + lottery.getAsString("activityImg")));
                }
            }
        }
        return lotteryList;
    }

    /**
     * 查询彩种总记录条数
     * @author	sjq
     */
    public int queryLotterysCount(Dto params)
    {
        return lotteryMapper.queryLotterysCount(params);
    }

    /**
     * 修改彩种
     * @author	sjq
     */
    public int editLottery(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","缺少必要参数id");
            return 0;
        }
        //初始化参数
        if("0".equals(params.getAsString("xzMaxSellMultiple")))
        {
            params.put("maxSellMultiple",null);
        }
        if("0".equals(params.getAsString("xzMaxSellMoney")))
        {
            params.put("maxSellMoney",null);
        }
        if("0".equals(params.getAsString("xzMinSellMultiple")))
        {
            params.put("minSellMultiple",null);
        }
        if("0".equals(params.getAsString("xzMinSellMoney")))
        {
            params.put("minSellMoney",null);
        }

        /**
         * 修改彩种
         */
        int result = lotteryMapper.editLottery(params);
        if(result > 0)
        {
            memcached.delete(LotteryConstants.lotteryPrefix + params.getAsString("id"));//移除彩种缓存
            try
            {
                Task task = new Task();
                task.setTaskName(Constants.LOTTERY_HOME_UPDATE_TASK);
                taskService.saveTask(task);
            }
            catch(Exception e)
            {
                result = 0;
                params.put("dmsg","修改彩种成功!插入首页文件更新任务发生异常!异常信息:" + e);
                throw  e;
            }
        }
        return result;
    }

    /**
     * 修改彩种销售状态
     * @author	sjq
     */
    public int editLotterySaleStatus(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","缺少必要参数id");
            return 0;
        }

        /**
         * 修改彩种销售状态
         */
        int result = lotteryMapper.editLotterySaleStatus(params);
        if(result > 0)
        {
            memcached.delete(LotteryConstants.lotteryPrefix + params.getAsString("id"));//移除彩种缓存
            try
            {
                Task task = new Task();
                task.setTaskName(Constants.LOTTERY_HOME_UPDATE_TASK);
                taskService.saveTask(task);
            }
            catch(Exception e)
            {
                result = 0;
                params.put("dmsg","审核失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
                throw  e;
            }
        }
        return result;
    }
}