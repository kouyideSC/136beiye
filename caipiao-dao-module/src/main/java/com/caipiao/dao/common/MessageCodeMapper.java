package com.caipiao.dao.common;

import com.caipiao.domain.common.MessageCode;

import java.util.List;

public interface MessageCodeMapper {

	/**
	 * 保存消息
	 * @param code
	 * @return
	 */
	void insertMessageCode(MessageCode code);
	
	/**
	 * 更新消息
	 * @param code
	 * @return
	 */
	Integer updateMessageCode(MessageCode code);

	/**
	 * 查询消息
	 * @parm code
	 * @return
	 */
	MessageCode queryMessageCode(MessageCode code);

	/**
	 * 查询未发送的短信列表
	 * @return
	 */
	List<MessageCode> queryNoSendMessageCode();
	
	/**
	 * 消息正确性验证
	 * @param code
	 * @return
	 */
	Long checkMessageCode(MessageCode code);
	
}