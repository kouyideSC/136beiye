package com.caipiao.dao.common;

import com.caipiao.domain.common.PayWay;
import com.caipiao.domain.common.PayWayChannel;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

/**
 * 充值渠道配置数据访问接口
 * @author  mcdog
 */
public interface PayWayChannelMapper
{
    /**
     * 查询充值渠道配置
     * @author  mcdog
     */
    List<PayWayChannel> queryPayWayChannels(Dto params);
    /**
     * 查询充值渠道配置总记录数
     * @author  mcdog
     */
    int queryPayWayChannelsCount(Dto params);
    /**
     * 更新充值渠道配置
     * @author  mcdog
     */
    int updatePayWayChannel(Dto params);
}