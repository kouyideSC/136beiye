package com.caipiao.admin.service.setting.role;

import com.alibaba.druid.support.logging.Log;
import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.admin.role.RoleMapper;
import com.caipiao.domain.common.Role;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色管理-服务类
 */
@Service("roleService")
public class RoleService
{
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 查询角色信息
     * @author  mcdog
     */
    public List<Dto> queryRoles(Dto params)
    {
        return roleMapper.queryRoles(params);
    }

    /**
     * 查询角色总记录条数
     * @author  mcdog
     */
    public int queryRolesCount(Dto params)
    {
        return roleMapper.queryRolesCount(params);
    }

    /**
     * 查询角色功能模块
     * @author  mcdog
     */
    public List<BaseDto> queryRoleModules(Dto params)
    {
        return roleMapper.queryRoleModules(params);
    }

    /**
     * 新增角色
     * @author  mcdog
     */
    public int addRole(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("name")))
        {
            params.put("dmsg","角色名称不能为空!");
            return 0;
        }

        /**
         * 新增角色
         */
        Role role = new Role();
        params.put("creator",params.get("opaccountId"));
        BeanUtils.copyProperties(role,params);
        int count = roleMapper.addRole(role);

        /**
         * 新增角色功能模块
         */
        if(count > 0)
        {
            logger.info("[新增角色]新增成功!操作人=" + params.getAsString("opfullName") + ",角色名=" + params.getAsString("name"));
            String moduleIds = params.getAsString("moduleIds").trim();//提取功能模块
            if(StringUtil.isNotEmpty(moduleIds))
            {
                String[] newModuleIds = moduleIds.split(",");
                Dto roleModuleDto = new BaseDto();
                roleModuleDto.put("roleId",role.getId());
                for(String moduleId : newModuleIds)
                {
                    if(StringUtil.isNotEmpty(moduleId))
                    {
                        roleModuleDto.put("moduleId",moduleId);
                        roleMapper.addRoleModule(roleModuleDto);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 更新角色
     * @author  mcdog
     */
    public int updateRole(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","角色id不能为空!");
            return 0;
        }
        if(StringUtil.isEmpty(params.get("name")))
        {
            params.put("dmsg","角色名称不能为空!");
            return 0;
        }

        /**
         * 更新角色
         */
        int count = roleMapper.updateRole(params);

        /**
         * 更新角色功能模块
         */
        if(count > 0)
        {
            roleMapper.deleteRoleModule(new BaseDto("roleId",params.get("id")));//删除该角色已有的功能模块配置
            String moduleIds = params.getAsString("moduleIds").trim();//提取功能模块
            if(StringUtil.isNotEmpty(moduleIds))
            {
                //新增角色功能模块
                String[] newModuleIds = moduleIds.split(",");
                Dto roleModuleDto = new BaseDto();
                roleModuleDto.put("roleId",params.get("id"));
                for(String moduleId : newModuleIds)
                {
                    if(StringUtil.isNotEmpty(moduleId))
                    {
                        roleModuleDto.put("moduleId",moduleId);
                        roleMapper.addRoleModule(roleModuleDto);
                    }
                }
            }
            logger.info("[更新角色]更新成功!操作人=" + params.getAsString("opfullName") + ",角色id=" + params.getAsString("id"));
        }
        return count;
    }

    /**
     * 删除角色
     * @author  mcdog
     */
    public int deleteRole(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","角色id不能为空!");
            return 0;
        }

        /**
         * 判断角色是否有引用
         */
        List<BaseDto> roleUserList = roleMapper.queryRoleUsers(new BaseDto("roleId",params.get("id")));
        if(roleUserList != null && roleUserList.size() > 0)
        {
            StringBuilder roleUserBuilder = new StringBuilder();
            for(BaseDto roleUser : roleUserList)
            {
                roleUserBuilder.append(roleUser.getAsString("accountName") + "(" + roleUser.getAsString("personalName") + ")<br>");
            }
            params.put("dmsg","该角色当前不可删除!被如下账户引用:" + roleUserBuilder.toString());
            return 0;
        }

        /**
         * 删除角色
         */
        int count = roleMapper.deleteRole(params);
        if(count > 0)
        {
            roleMapper.deleteRoleModule(new BaseDto("roleId",params.get("id")));//删除角色功能模块
            logger.info("[删除角色]删除成功!操作人=" + params.getAsString("opfullName") + ",角色id=" + params.getAsString("id"));
        }
        return count;
    }

    /**
     * 查询功能模块树
     * @author  mcdog
     */
    public List<BaseDto> queryModuleTree(Dto params)
    {
        List<BaseDto> dataList = new ArrayList<BaseDto>();
        List<BaseDto> moduleList = roleMapper.queryModules(params);
        if(moduleList != null && moduleList.size() > 0)
        {
            for(BaseDto module : moduleList)
            {
                BaseDto data = new BaseDto();
                data.put("id",module.get("id"));
                data.put("pId",module.get("parentModuleId"));
                data.put("name",module.get("moduleName"));
                dataList.add(data);
            }
            setModuleTree(dataList,dataList);
        }
        return dataList;
    }

    /**
     * 根据模块集合封装模块树
     * @author 	sjq
     * @param 	distMenuList	用来存放封装后的模块树
     * @param 	sourceMenuList	源数据
     */
    private void setModuleTree(List<BaseDto> distMenuList,List<BaseDto> sourceMenuList)
    {
        String moduleId = null;
        List<BaseDto> childList = null;
        List<BaseDto> removeList = new ArrayList<BaseDto>();
        for(BaseDto module : distMenuList)
        {
            moduleId = module.getAsString("id");	//当前模块的模块id
            childList = new ArrayList<BaseDto>();	//用来存放当前模块的子模块
            for(BaseDto childModule : sourceMenuList)
            {
                if(moduleId.equals(childModule.getAsString("pId")))
                {
                    childList.add(childModule);
                    removeList.add(childModule);
                }
            }
            if(childList.size() > 0)
            {
                setModuleTree(childList,sourceMenuList);
                module.put("children",childList);	//设置当前模块的子模块
            }
        }
        //移除不在树结构中的模块
        distMenuList.removeAll(removeList);
    }
}