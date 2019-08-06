package com.caipiao.admin.service.weihu.channel;

import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.AppMarketMapper;
import com.caipiao.dao.common.ChannelMapper;
import com.caipiao.domain.common.AppMarket;
import com.caipiao.domain.common.Channel;
import com.caipiao.domain.cpadmin.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 渠道相关服务
 * Created by kouyi on 2018/03/21.
 */
@Service("channelService")
public class ChannelService
{
    @Autowired
    private ChannelMapper channelMapper;

    /**
     * 发布渠道
     * @param params
     * @return
     */
    public int insertChannel(Dto params) {
        if(StringUtil.isEmpty(params.get("beginTime")))
        {
            params.remove("beginTime");
        }
        if(StringUtil.isEmpty(params.get("endTime")))
        {
            params.remove("endTime");
        }
        return channelMapper.insertChannel(params);
    }

    /**
     * 删除渠道
     * @param params
     * @return
     */
    public int deleteChannel(Dto params) {
        if(StringUtil.isEmpty(params.getAsInteger("id"))) {
            return 0;
        }
        return channelMapper.deleteChannel(params.getAsInteger("id"));
    }

    /**
     * 更新渠道
     * @param params
     * @return
     */
    public int updateChannel(Dto params) {
        if(StringUtil.isEmpty(params.getAsInteger("id"))) {
            return 0;
        }
        return channelMapper.updateChannel(params);
    }

    /**
     * 查询渠道列表
     * @return
     */
    public List<Dto> queryChannelDtoList(Dto params) {
        return channelMapper.queryChannelDtoList(params);
    }
}
