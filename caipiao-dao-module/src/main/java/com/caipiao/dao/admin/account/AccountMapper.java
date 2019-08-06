package com.caipiao.dao.admin.account;

import com.caipiao.domain.common.Account;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

/**
 * 帐户管理-数据访问接口
 * @author	sjq
 */
public interface AccountMapper
{
	/**
	 * 查询帐户所属角色信息
	 * @author	sjq
	 */
	List<BaseDto> queryAccountRoles(Dto params);
	/**
	 * 查询帐户信息
	 * @author	sjq
	 */
	List<Dto> queryAccounts(Dto params);
	/**
	 * 查询帐户信息总记录数
	 * @author	sjq
	 */
	int queryAccountsCount(Dto params);
	/**
	 * 新增帐户信息
	 * @author	sjq
	 */
	int addAccount(Account account);
	/**
	 * 新增帐户角色信息
	 * @author	sjq
	 */
	int addAccountRole(Dto params);
	/**
	 * 更新帐户
	 * @author	sjq
	 */
	int updateAccount(Dto params);
	/**
	 * 修改帐户密码
	 * @author	sjq
	 */
	int updateAccountForPwd(Dto params);
	/**
	 * 锁定/解锁帐户
	 * @author	sjq
	 */
	int updateAccountForLock(Dto params);
	/**
	 * 离职/复职帐户
	 * @author	sjq
	 */
	int updateAccountForWorkStatus(Dto params);
	/**
	 * 删除帐户
	 * @author	sjq
	 */
	int deleteAccount(Dto params);
	/**
	 * 删除帐户角色信息
	 * @author	sjq
	 */
	int deleteAccountRoles(Dto params);
	/**
	 * 查询组织信息
	 * @author	sjq
	 */
	List<Dto> queryOrganizations(Dto params);
	/**
	 * 查询岗位信息
	 * @author	sjq
	 */
	List<Dto> queryJobtypes(Dto params);
}