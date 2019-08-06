package com.caipiao.admin.setting.role.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.setting.role.RoleService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import com.caipiao.domain.cpadmin.JsonPageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 设置-角色管理-控制类
 */
@Controller
@RequestMapping("/setting/role")
public class RoleController
{
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    /**
     * 显示角色管理首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_setting_role")
    public String index(HttpServletRequest request, HttpServletResponse response)
    {
        return "setting/role/index";
    }

    /**
     * 显示角色编辑页面
     * @author  mcdog
     */
    @RequestMapping("/initEdit")
    @ModuleAuthorityRequired(mcode = "btn_setting_role_edit")
    public String initEdit(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> roleList = roleService.queryRoles(params);
        if(roleList != null && roleList.size() > 0)
        {
            Dto roleDto = roleList.get(0);

            //设置角色功能模块信息
            String moduleIds = "";
            String moduleNames = "";
            List<BaseDto> roleModuleList = roleService.queryRoleModules(new BaseDto("roleId",roleDto.get("id")));//查询角色功能模块
            if(roleModuleList != null && roleModuleList.size() > 0)
            {
                for(Dto roleModule : roleModuleList)
                {
                    moduleIds += "," + roleModule.getAsString("id");
                    moduleNames += "," + roleModule.getAsString("moduleName");
                }
                moduleIds = moduleIds.substring(1);
                moduleNames = moduleNames.substring(1);
            }
            roleDto.put("moduleIds",moduleIds);//设置角色功能模块id
            roleDto.put("moduleNames",moduleNames);//设置角色功能模块名称
            map.put("params",roleDto);
        }
        return "setting/role/edit";
    }

    /**
     * 显示角色新增页面
     * @author  mcdog
     */
    @RequestMapping("/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_setting_role_add")
    public String initAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "setting/role/add";
    }

    /**
     * 查询角色信息
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_setting_role")
    public void queryRoles(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",roleService.queryRoles(params));//查询记录
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",roleService.queryRolesCount(params));//查询总记录数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询角色信息]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto, DateUtil.DEFAULT_DATE_TIME).toString(),response);
    }

    /**
     * 更新角色
     * @author  mcdog
     */
    @RequestMapping("/edit")
    @ModuleAuthorityRequired(mcode = "btn_setting_role_edit")
    public void updateRole(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            logger.info("[更新角色]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(roleService.updateRole(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","更新成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[更新角色]发生异常!操作帐户=" + params.getAsString("opfullName") + ",异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 新增角色
     * @author  mcdog
     */
    @RequestMapping("/add")
    @ModuleAuthorityRequired(mcode = "btn_setting_role_add")
    public void addRole(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            logger.info("[新增角色]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(roleService.addRole(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","新增成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[新增角色]发生异常!操作帐户=" + params.getAsString("opfullName") + ",异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除角色
     * @author  mcdog
     */
    @RequestMapping("/delete")
    @ModuleAuthorityRequired(mcode = "btn_setting_role_delete")
    public void deleteRole(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[删除角色]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(roleService.deleteRole(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","删除成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[删除角色]发生异常!操作帐户=" + params.getAsString("opfullName") + ",异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取功能模块树
     * @author  mcdog
     */
    @RequestMapping("/mtree/get")
    public void getModuleTree(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",roleService.queryModuleTree(params));//查询记录
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[获取功能模块树]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto, DateUtil.DEFAULT_DATE_TIME).toString(),response);
    }
}