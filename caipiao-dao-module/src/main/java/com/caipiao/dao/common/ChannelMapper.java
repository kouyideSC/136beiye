package com.caipiao.dao.common;

import com.caipiao.domain.common.Channel;
import com.caipiao.domain.common.MessageCode;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

public interface ChannelMapper {

	/**
	 * 保存渠道
	 * @param params
	 * @return
	 */
	Integer insertChannel(Dto params);
	
	/**
	 * 更新渠道
	 * @param params
	 * @return
	 */
	Integer updateChannel(Dto params);

	/**
	 * 删除渠道
	 * @param id
	 * @return
	 */
	Integer deleteChannel(Integer id);

	/**
	 * 根据渠道编号查询渠道信息
	 * @parm channelCode
	 * @return
	 */
	Channel queryChannelInfo(String channelCode);

	/**
	 * 查询渠道列表
	 * @return
	 */
	List<Channel> queryChannelList(Channel channel);

	/**
	 * 后台查询渠道列表
	 * @return
	 */
	List<Dto> queryChannelDtoList(Dto params);
}