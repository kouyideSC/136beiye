package com.caipiao.admin.service.setting.account;

import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.admin.account.AccountMapper;
import com.caipiao.domain.common.Account;
import com.caipiao.domain.common.Role;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统帐户-服务类
 * @author  mcdog
 */
@Service("accountService")
public class AccountService
{
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 查询帐户所属角色信息
     * @author	sjq
     */
    public List<BaseDto> queryAccountRoles(Dto params)
    {
        return accountMapper.queryAccountRoles(params);
    }

    /**
     * 查询帐户信息
     * @author  mcdog
     */
    public List<Dto> queryAccounts(Dto params)
    {
        return accountMapper.queryAccounts(params);
    }

    /**
     * 查询帐户信息总记录条数
     * @author  mcdog
     */
    public int queryAccountsCount(Dto params)
    {
        return accountMapper.queryAccountsCount(params);
    }

    /**
     * 新增帐户
     * @author	sjq
     */
    public int addAccount(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("accountName")))
        {
            params.put("dmsg","帐户名不能为空!");
            return 0;
        }
        if(StringUtil.isEmpty(params.get("password")))
        {
            params.put("dmsg","帐户密码不能为空!");
            return 0;
        }

        /**
         * 新增帐户
         */
        Account account = new Account();
        BeanUtils.copyProperties(account,params);
        account.setCreator(params.getAsLong("opaccountId"));
        int count = accountMapper.addAccount(account);
        if(count > 0)
        {
            //如果有选择角色,则添加帐户角色信息
            if(StringUtil.isNotEmpty(params.getAsString("roleIds")))
            {
                Dto accountRoleDto = new BaseDto();
                accountRoleDto.put("accountId",account.getId());
                String[] roleIds = params.getAsString("roleIds").split(",");
                for(String roleId : roleIds)
                {
                    accountRoleDto.put("roleId",roleId);
                    accountMapper.addAccountRole(accountRoleDto);
                }
            }
            logger.info("[新增帐户]新增成功!操作人=" + params.getAsString("opfullName") + ",帐户名=" + params.getAsString("accountName"));
        }
        return count;
    }

    /**
     * 更新帐户
     * @author	sjq
     */
    public int updateAccount(Dto params) throws Exception
    {
        //参数校验
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","帐户id不能为空!");
            return 0;
        }

        /**
         * 更新帐户
         */
        int count = accountMapper.updateAccount(params);//更新帐户
        if(count > 0)
        {
            //删除已有的帐户角色信息,如果有选择角色,则添加帐户角色信息
            accountMapper.deleteAccountRoles(new BaseDto("accountId",params.getAsString("id")));//删除帐户角色信息
            if(StringUtil.isNotEmpty(params.getAsString("roleIds")))
            {
                Dto accountRoleDto = new BaseDto();
                accountRoleDto.put("accountId",params.get("id"));
                String[] roleIds = params.getAsString("roleIds").split(",");
                for(String roleId : roleIds)
                {
                    accountRoleDto.put("roleId",roleId);
                    accountMapper.addAccountRole(accountRoleDto);
                }
            }
            logger.info("[更新帐户]更新成功!操作人=" + params.getAsString("opfullName") + ",帐户id=" + params.getAsString("id"));
        }
        return count;
    }

    /**
     * 修改帐户密码
     * @author	sjq
     */
    public int updateAccountForPwd(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","帐户id不能为空!");
            return 0;
        }
        if(StringUtil.isEmpty(params.get("password")))
        {
            params.put("dmsg","帐户密码不能为空!");
            return 0;
        }

        /**
         * 修改密码
         */
        int count = accountMapper.updateAccountForPwd(params);//修改密码
        if(count > 0)
        {
            logger.info("[修改帐户密码]修改成功!操作人=" + params.getAsString("opfullName") + ",帐户id=" + params.getAsString("id"));
        }
        return count;
    }

    /**
     * 锁定/解锁帐户
     * @author	sjq
     */
    public int updateAccountForLock(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","帐户id不能为空!");
            return 0;
        }
        if(StringUtil.isEmpty(params.get("isLock")))
        {
            params.put("dmsg","帐户锁定状态不能为空!");
            return 0;
        }

        /**
         * 锁定/解锁帐户
         */
        int count = accountMapper.updateAccountForLock(params);//锁定/解锁帐户
        if(count > 0)
        {
            logger.info("[锁定/解锁帐户]" + (0 == params.getAsInteger("isLock")? "解锁" : "锁定") + "成功!操作人=" + params.getAsString("opfullName") + ",帐户id=" + params.getAsString("id"));
        }
        return count;
    }

    /**
     * 离职/复职帐户
     * @author	sjq
     */
    public int updateAccountForWorkStatus(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","帐户id不能为空!");
            return 0;
        }
        if(StringUtil.isEmpty(params.get("workStatus")))
        {
            params.put("dmsg","帐户工作状态不能为空!");
            return 0;
        }

        /**
         * 离职/复职帐户
         */
        int count = accountMapper.updateAccountForWorkStatus(params);//离职/复职帐户
        if(count > 0)
        {
            logger.info("[离职/复职帐户]" + (1 == params.getAsInteger("workStatus")? "复职" : "离职") + "成功!操作人=" + params.getAsString("opfullName") + ",帐户id=" + params.getAsString("id"));
        }
        return count;
    }

    /**
     * 删除帐户
     * @author	sjq
     */
    public int deleteAccount(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","帐户id不能为空!");
            return 0;
        }

        /**
         * 删除帐户信息
         */
        int count = accountMapper.deleteAccount(params);//删除帐户
        if(count > 0)
        {
            accountMapper.deleteAccountRoles(new BaseDto("accountId",params.getAsString("id")));//删除帐户角色信息
            logger.info("[删除帐户]删除成功!操作人=" + params.getAsString("opfullName") + ",帐户id=" + params.getAsString("id"));
        }
        return count;
    }

    /**
     * 查询岗位信息
     * @author	sjq
     */
    public List<Dto> queryJobtypes(Dto params)
    {
        return accountMapper.queryJobtypes(params);
    }

    /**
     * 查询组织树
     * @author  mcdog
     */
    public List<Dto> queryOrganizationTree(Dto params)
    {
        List<Dto> dataList = new ArrayList<Dto>();
        List<Dto> organizationList = accountMapper.queryOrganizations(params);
        if(organizationList != null && organizationList.size() > 0)
        {
            for(Dto organization : organizationList)
            {
                BaseDto data = new BaseDto();
                data.put("id",organization.get("id"));
                data.put("pId",organization.get("parentId"));
                data.put("name",organization.get("organizationName"));
                data.put("code",organization.get("organizationCode"));
                dataList.add(data);
            }
            setTree(dataList,dataList);
        }
        return dataList;
    }

    /**
     * 封装树状数据
     * @author 	sjq
     * @param 	distList	用来存放封装后的数据
     * @param 	sourceList	源数据
     */
    private void setTree(List<Dto> distList,List<Dto> sourceList)
    {
        String id = null;
        List<Dto> childList = null;
        List<Dto> removeList = new ArrayList<Dto>();
        for(Dto node : distList)
        {
            id = node.getAsString("id");	//当前节点id
            childList = new ArrayList<Dto>();//用来存放当节点的子节点
            for(Dto childNode : sourceList)
            {
                if(id.equals(childNode.getAsString("pId")))
                {
                    childList.add(childNode);
                    removeList.add(childNode);
                }
            }
            if(childList.size() > 0)
            {
                setTree(childList,sourceList);
                node.put("children",childList);	//设置当前节点的子节点
            }
        }
        //移除不在树结构中的数据
        distList.removeAll(removeList);
    }
}