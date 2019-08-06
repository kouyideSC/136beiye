package com.caipiao.dao.admin.login;

import java.util.List;

import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;

/**
 * 登录/退出登录 - 数据访问接口
 * @author	sjq
 */
public interface LoginMapper
{
	/**
	 * 查询帐户信息
	 * @author	sjq
	 */
	BaseDto queryLoginUsers(Dto params);
	/**
	 * 查询模块信息
	 * @author	sjq
	 */
	List<BaseDto> queryModules(Dto params);
}