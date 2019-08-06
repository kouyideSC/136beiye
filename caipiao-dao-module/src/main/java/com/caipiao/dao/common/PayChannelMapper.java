package com.caipiao.dao.common;

import com.caipiao.domain.common.PayChannel;
import com.caipiao.domain.common.PayWay;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

/**
 * 充值渠道数据访问接口
 * @author  mcdog
 */
public interface PayChannelMapper
{
    /**
     * 查询充值渠道
     * @author  mcdog
     */
    List<PayChannel> queryPayChannels(Dto params);
    /**
     * 查询充值渠道总记录数
     * @ahthor  sjq
     */
    int queryPayChannelsCount(Dto params);
    /**
     * 查询充值渠道(管理后台)
     * @author  mcdog
     */
    List<Dto> queryPayChannelInfos(Dto params);
    /**
     * 查询充值渠道总记录数(管理后台)
     * @ahthor  sjq
     */
    int queryPayChannelInfosCount(Dto params);
    /**
     * 更新充值渠道
     * @ahthor  sjq
     */
    int updatePayChannel(Dto params);
    /**
     * 查询渠道充值方式信息(管理后台)
     * @author  mcdog
     */
    List<Dto> queryChannelPaywayInfos(Dto params);
    /**
     * 更新渠道充值方式
     * @ahthor  sjq
     */
    int updateChannelPayway(Dto params);
}