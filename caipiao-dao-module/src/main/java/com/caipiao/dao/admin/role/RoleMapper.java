package com.caipiao.dao.admin.role;

import com.caipiao.domain.common.Role;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

/**
 * 角色管理-数据访问接口
 * @author	sjq
 */
public interface RoleMapper
{
	/**
	 * 查询角色信息
	 * @author	sjq
	 */
	List<Dto> queryRoles(Dto params);
	/**
	 * 查询角色总记录条数
	 * @author	sjq
	 */
	int queryRolesCount(Dto params);
	/**
	 * 查询角色模块信息
	 * @author	sjq
	 */
	List<BaseDto> queryRoleModules(Dto params);
	/**
	 * 新增角色
	 * @author	sjq
	 */
	int addRole(Role role);
	/**
	 * 更新角色
	 * @author	sjq
	 */
	int updateRole(Dto params);
    /**
     * 删除角色
     * @author	sjq
     */
    int deleteRole(Dto params);
	/**
	 * 新增角色功能模块
	 * @author	sjq
	 */
	int addRoleModule(Dto params);
    /**
     * 删除角色功能模块
     * @author	sjq
     */
    int deleteRoleModule(Dto params);
	/**
	 * 查询功能模块信息
	 * @author	sjq
	 */
	List<BaseDto> queryModules(Dto params);
	/**
	 * 查询角色用户信息
	 * @author	sjq
	 */
	List<BaseDto> queryRoleUsers(Dto params);
}