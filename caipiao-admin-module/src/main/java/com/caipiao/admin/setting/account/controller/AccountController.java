package com.caipiao.admin.setting.account.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.setting.account.AccountService;
import com.caipiao.admin.service.setting.role.RoleService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
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
 * 设置-帐户管理控制类
 */
@Controller
@RequestMapping("/setting/account")
public class AccountController
{
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private static final String defaultPassword = "123456";//默认帐户密码

    @Autowired
    private AccountService accountService;

    /**
     * 显示帐户管理首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_setting_account")
    public String index(HttpServletRequest request, HttpServletResponse response)
    {
        return "setting/account/index";
    }

    /**
     * 显示帐户编辑页面
     * @author  mcdog
     */
    @RequestMapping("/initEdit")
    @ModuleAuthorityRequired(mcode = "btn_setting_account_edit")
    public String initEdit(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> accountList = accountService.queryAccounts(params);
        if(accountList != null && accountList.size() > 0)
        {
            Dto accountdto = accountList.get(0);

            //设置帐户所属角色信息
            String roleIds = "";
            List<BaseDto> userRoleList = accountService.queryAccountRoles(new BaseDto("accountId",params.get("id")));//查询帐户所属角色信息
            if(userRoleList != null && userRoleList.size() > 0)
            {
                for(BaseDto userRole : userRoleList)
                {
                    roleIds += "," + userRole.getAsString("id");
                }
                roleIds = roleIds.substring(1);
            }
            accountdto.put("roleIds",roleIds);//设置帐户所属角色id
            map.put("params",accountdto);
        }
        return "setting/account/edit";
    }

    /**
     * 显示帐户新增页面
     * @author  mcdog
     */
    @RequestMapping("/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_setting_account_add")
    public String initAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "setting/account/add";
    }

    /**
     * 查询帐户信息
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_setting_account")
    public void queryRoles(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",accountService.queryAccounts(params));//查询记录
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",accountService.queryAccountsCount(params));//查询总记录数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询账户信息]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto, DateUtil.DEFAULT_DATE_TIME).toString(),response);
    }

    /**
     * 新增帐户
     * @author  mcdog
     */
    @RequestMapping("/add")
    @ModuleAuthorityRequired(mcode = "btn_setting_account_add")
    public void addRole(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            logger.info("[新增帐户]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("password",MD5.md5(defaultPassword));//设置默认密码
            if(accountService.addAccount(params) > 0)
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
            logger.error("[新增帐户]发生异常!操作帐户=" + params.getAsString("opfullName") + ",异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 更新帐户
     * @author  mcdog
     */
    @RequestMapping("/edit")
    @ModuleAuthorityRequired(mcode = "btn_setting_account_edit")
    public void updateRole(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            logger.info("[更新帐户]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(accountService.updateAccount(params) > 0)
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
            logger.error("[更新帐户]发生异常!操作帐户=" + params.getAsString("opfullName") + ",异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 重置密码
     * @author  mcdog
     */
    @RequestMapping("/resetpwd")
    @ModuleAuthorityRequired(mcode = "btn_setting_account_resetpwd")
    public void resetPassword(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            logger.info("[重置密码]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("password",MD5.md5(defaultPassword));//设置默认密码
            if(accountService.updateAccountForPwd(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","重置成功!新密码为" + defaultPassword);
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[重置密码]发生异常!操作帐户=" + params.getAsString("opfullName") + ",异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 锁定/解锁帐户
     * @author  mcdog
     */
    @RequestMapping("/lock")
    @ModuleAuthorityRequired(mcode = "btn_setting_account_lock")
    public void lockAccount(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            logger.info("[锁定/解锁帐户]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(accountService.updateAccountForLock(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","操作成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[锁定/解锁帐户]发生异常!操作帐户=" + params.getAsString("opfullName") + ",异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 离职/复职帐户
     * @author  mcdog
     */
    @RequestMapping("/setworkstatus")
    @ModuleAuthorityRequired(mcode = "btn_setting_account_workstatus")
    public void setAccountWorkStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            logger.info("[离职/复职帐户]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(accountService.updateAccountForWorkStatus(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","操作成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[离职/复职帐户]发生异常!操作帐户=" + params.getAsString("opfullName") + ",异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除帐户
     * @author  mcdog
     */
    @RequestMapping("/delete")
    @ModuleAuthorityRequired(mcode = "btn_setting_account_delete")
    public void deleteRole(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[删除帐户]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(accountService.deleteAccount(params) > 0)
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
            logger.error("[删除帐户]发生异常!操作帐户=" + params.getAsString("opfullName") + ",异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取组织树
     * @author  mcdog
     */
    @RequestMapping("/otree/get")
    public void getModuleTree(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",accountService.queryOrganizationTree(params));//查询记录
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[获取组织树]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto, DateUtil.DEFAULT_DATE_TIME).toString(),response);
    }

    /**
     * 获取岗位
     * @author  mcdog
     */
    @RequestMapping("/jobtype/get")
    public void getJobTypes(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",accountService.queryJobtypes(params));//查询记录
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[获取岗位]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto, DateUtil.DEFAULT_DATE_TIME).toString(),response);
    }
}