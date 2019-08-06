package com.caipiao.admin.service.weihu.czqd;

import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.PayChannelMapper;
import com.caipiao.dao.common.PayWayMapper;
import com.caipiao.dao.common.PaymentWayMapper;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 充值渠道-服务类
 */
@Service("czqdService")
public class CzqdService
{
    @Autowired
    private PayChannelMapper payChannelMapper;

    /**
     * 查询充值渠道信息
     * @author	sjq
     */
    public List<Dto> queryPayChannelInfos(Dto params)
    {
        return payChannelMapper.queryPayChannelInfos(params);
    }

    /**
     * 查询充值渠道总记录条数
     * @author	sjq
     */
    public int queryPayChannelInfosCount(Dto params)
    {
        return payChannelMapper.queryPayChannelInfosCount(params);
    }

    /**
     * 启用/关闭充值渠道
     * @author	sjq
     */
    public int updatePayChannelForStatus(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","缺少必要参数id!");
            return -1;
        }
        if(StringUtil.isEmpty(params.get("status")))
        {
            params.put("dmsg","缺少必要参数status!");
            return -1;
        }
        Dto updateParams = new BaseDto("id",params.get("id"));
        updateParams.put("status",params.get("status"));
        return payChannelMapper.updatePayChannel(updateParams);
    }

    /**
     * 查询渠道充值方式信息
     * @author	sjq
     */
    public List<Dto> queryChannelPaywayInfos(Dto params)
    {
        return payChannelMapper.queryChannelPaywayInfos(params);
    }

    /**
     * 启用/关闭渠道充值方式
     * @author	sjq
     */
    public int updateChannelPaywayForStatus(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","缺少必要参数id!");
            return -1;
        }
        if(StringUtil.isEmpty(params.get("status")))
        {
            params.put("dmsg","缺少必要参数status!");
            return -1;
        }
        Dto updateParams = new BaseDto("id",params.get("id"));
        updateParams.put("status",params.get("status"));
        return payChannelMapper.updateChannelPayway(updateParams);
    }

    /**
     * 编辑渠道充值方式
     * @author	sjq
     */
    public int updateChannelPayway(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","缺少必要参数id!");
            return -1;
        }
        if("0".equals(params.getAsString("model")))
        {
            params.put("timeRangeStart",null);
            params.put("timeRangeEnd",null);
            params.put("timeCharacter",null);
        }
        else if("1".equals(params.getAsString("model")))
        {
            params.put("timeCharacter",null);
        }
        else if("2".equals(params.getAsString("model")))
        {
            params.put("timeRangeStart",null);
            params.put("timeRangeEnd",null);
        }
        params.put("updateXzMoney",1);
        params.put("minMoney",StringUtil.isEmpty(params.get("minMoney"))? null : params.get("minMoney"));
        params.put("maxMoney",StringUtil.isEmpty(params.get("maxMoney"))? null : params.get("maxMoney"));
        return payChannelMapper.updateChannelPayway(params);
    }
}