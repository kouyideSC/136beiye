package com.caipiao.admin.service.login;

import java.util.*;

import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.encrypt.RSA;
import com.caipiao.common.user.UserUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.admin.account.AccountMapper;
import com.caipiao.dao.admin.login.LoginMapper;
import com.caipiao.dao.admin.role.RoleMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.caipiao.common.encrypt.RSA.decryptByPrivateKey;

/**
 * 登录/退出登录 - 服务类
 * @author	sjq
 */
@Service("loginService")
public class LoginService
{
	Logger logger = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private LoginMapper loginMapper;

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private UserMapper userMapper;

	private static Map<String,String> sellerModuleMaps = new HashMap<String,String>();//销售功能模块集合

	private static Map<String,String> agentModuleMaps = new HashMap<String,String>();//代理功能模块集合

	static
	{
		sellerModuleMaps.put("menu_xiaoshou","menu_xiaoshou");//销售管理
		sellerModuleMaps.put("menu_xiaoshou_lower","menu_xiaoshou_lower");//下级用户
		sellerModuleMaps.put("menu_xiaoshou_proxy","menu_xiaoshou_proxy");//下级代理
		sellerModuleMaps.put("menu_xiaoshou_commiss","menu_xiaoshou_commiss");//我的销量
		sellerModuleMaps.put("menu_xiaoshou_userrecharge","menu_xiaoshou_userrecharge");//用户充值流水
		sellerModuleMaps.put("menu_xiaoshou_usertx","menu_xiaoshou_usertx");//用户提现流水
		sellerModuleMaps.put("menu_xiaoshou_userscheme","menu_xiaoshou_userscheme");//用户方案
		sellerModuleMaps.put("menu_xiaoshou_accountdetail","menu_xiaoshou_accountdetail");//用户账户流水
		sellerModuleMaps.put("menu_xiaoshou_wdytc","menu_xiaoshou_wdytc");//我的月提成

		agentModuleMaps.put("menu_xiaoshou","menu_xiaoshou");//销售管理
		agentModuleMaps.put("menu_xiaoshou_lower","menu_xiaoshou_lower");//下级用户
		agentModuleMaps.put("menu_xiaoshou_proxymoney","menu_xiaoshou_proxymoney");//我的返利
		agentModuleMaps.put("menu_xiaoshou_userrecharge","menu_xiaoshou_userrecharge");//用户充值流水
		agentModuleMaps.put("menu_xiaoshou_usertx","menu_xiaoshou_usertx");//用户提现流水
		agentModuleMaps.put("menu_xiaoshou_userscheme","menu_xiaoshou_userscheme");//用户方案
		agentModuleMaps.put("menu_xiaoshou_accountdetail","menu_xiaoshou_accountdetail");//用户账户流水
		agentModuleMaps.put("menu_xiaoshou_fanlidetail","menu_xiaoshou_fanlidetail");//返利明细
	}

	/**
	 * 获取登录帐户信息
	 * @author	sjq
	 */
	public Dto getLoginUserInfo(Dto params)
	{
		return loginMapper.queryLoginUsers(params);
	}

	/**
	 * 获取销售/代理登录帐户信息
	 * @author	sjq
	 */
	public Dto getAgentLoginUserInfo(Dto params)
	{
		return loginMapper.queryLoginUsers(params);
	}

	/**
	 * 销售/代理-手机号密码登录验证
	 * @author	sjq
	 */
	public boolean passwordAuthLogin(Dto params) throws Exception
	{
		/**
		 * 校验参数
		 */
		//手机号验证
		String mobile = params.getAsString("mobile");//提取手机号
		if(!UserUtils.checkMobile(mobile))
		{
			params.put("dmsg","手机号不合法");
			return false;
		}
		//验证登录密码
		String password = params.getAsString("password");//提取密码
		password = RSA.decryptByPrivateKey(password);
		if(!UserUtils.checkPassword(password))
		{
			params.put("dmsg","密码不合法");
			return false;
		}

		/**
		 * 根据手机号查询帐户信息
		 */
		User userInfo = userMapper.queryUserInfoByMobile(mobile);
		if(StringUtil.isEmpty(userInfo) || !MD5.verify(password,userInfo.getPassword()))
		{
			params.put("dmsg","用户不存在或密码错误");
			return false;
		}

		/**
		 * 校验用户合法性
		 */
		//校验帐户类型是否允许登录
		if(userInfo.getIsSale() != UserConstants.USER_PROXY_SALE
				&& userInfo.getIsSale() != UserConstants.USER_STATUS_AGENT)
		{
			params.put("dmsg","用户不存在或密码错误");
			return false;
		}
		//校验用户状态是否允许登录
		if(userInfo.getStatus().intValue() != 1)
		{
			params.put("dmsg","用户已被冻结或注销,请联系客服");
			return false;
		}

		/**
		 * 设置登录信息
		 */
		//设置帐户基本信息
		Dto accountDto = new BaseDto();
		accountDto.put("id",userInfo.getId());
		accountDto.put("accountName",userInfo.getMobile());//设置帐户id
		accountDto.put("personalName",userInfo.getRealName());//设置帐户真实姓名
		accountDto.put("isSale",userInfo.getIsSale());//设置帐户头衔
        accountDto.put("mobile",userInfo.getMobile());//设置帐户手机号

		//设置帐户功能模块信息
		List<BaseDto> moduleList = loginMapper.queryModules(new BaseDto("queryAll",1));
		StringBuilder mcodebuilder = new StringBuilder("{");//用来存储功能权限code的json形式字符串
		Map<String,BaseDto> smodulemaps = new HashMap<String, BaseDto>();
		List<BaseDto> newlist = new ArrayList<BaseDto>();
		if(userInfo.getIsSale() == UserConstants.USER_PROXY_SALE)
		{
			for(BaseDto module : moduleList)
			{
				if(sellerModuleMaps.containsKey(module.getAsString("moduleCode")))
				{
					mcodebuilder.append(module.get("moduleCode") + "=\"" + module.get("moduleCode") + "\",");
					smodulemaps.put(module.getAsString("moduleCode"),module);
					newlist.add((BaseDto)module.clone());
				}
			}
		}
		else if(userInfo.getIsSale() == UserConstants.USER_STATUS_AGENT)
		{
			for(BaseDto module : moduleList)
			{
				if(agentModuleMaps.containsKey(module.getAsString("moduleCode")))
				{
					mcodebuilder.append(module.get("moduleCode") + "=\"" + module.get("moduleCode") + "\",");
					smodulemaps.put(module.getAsString("moduleCode"),module);
					newlist.add((BaseDto)module.clone());
				}
			}
		}
		mcodebuilder = new StringBuilder(mcodebuilder.substring(0,mcodebuilder.length() - 1));
		mcodebuilder.append("}");
		accountDto.put("modulestr",mcodebuilder.toString());
		accountDto.put("moduleList",moduleList);
		accountDto.put("moduleMap",smodulemaps);

		//封装菜单树,并存入modulemaps中(模块code为键,模块对象为值)
		setModuleTree(newlist,newlist);
		Map<String,BaseDto> modulemaps = new LinkedHashMap<String,BaseDto>();
		for(BaseDto module : newlist)
		{
			modulemaps.put(module.getAsString("moduleCode"),module);
		}
		accountDto.put("modulemaps",modulemaps);
		params.put("accountDto",accountDto);
		return true;
	}

	/**
     * 根据账户信息获取该账户所拥有权限的功能模块信息
     * @author	sjq
     * @param	account	查询参数对象(id:用户id)
     * @return 	modules	功能模块信息集合(以模块code为键,模块对象为值)
     */
	public Map<String,BaseDto> getLoginUserModules(Dto account)
	{
		Map<String,BaseDto> modulemaps = new LinkedHashMap<String,BaseDto>();
		List<BaseDto> moduleList = new ArrayList<BaseDto>();
		try
		{
			//超级用户直接获取系统所有的模块
			if("1".equals(account.getAsString("isSuperuser")))
			{
				moduleList = loginMapper.queryModules(new BaseDto());
            }
        	else
        	{
        		//普通用户先获取该用户所属的角色信息
        		List<BaseDto> accountRoleList = accountMapper.queryAccountRoles(new BaseDto("accountId",account.get("id")));

        		//根据角色信息获取角色所拥有的功能模块
        		if(accountRoleList != null && accountRoleList.size() > 0 && accountRoleList.get(0) != null)
        		{
        			BaseDto basedto = new BaseDto();
        			List<BaseDto> roleModuleList = null;
        			for(BaseDto roleDto : accountRoleList)
        			{
						roleModuleList = roleMapper.queryRoleModules(new BaseDto("roleId",roleDto.get("id")));
        				if(roleModuleList != null && roleModuleList.size() > 0)
        				{
        					for(BaseDto module : roleModuleList)
        					{
        						if(!moduleList.contains(module))
        						{
        							moduleList.add(module);
        						}
        					}
        				}
        			}
        			//手动排序,按sort从小到大排序
                    Collections.sort(moduleList, new Comparator<BaseDto>()
                    {
                        @Override
                        public int compare(BaseDto o1, BaseDto o2)
                        {
                            Integer sort1 = o1.getAsInteger("sort");
                            Integer sort2 = o2.getAsInteger("sort");
                            return ((sort1 != null && sort2 == null) || (sort1 != null && sort2 != null && sort1 > sort2))? 1 : -1;
                        }
                    });
        		}
        	}
            //保存当前用户的所有模块信息
			StringBuilder mcodebuilder = new StringBuilder("{");//用来存储功能权限code的json形式字符串
			Map<String,BaseDto> smodulemaps = new HashMap<String, BaseDto>();
			List<BaseDto> newlist = new ArrayList<BaseDto>();
			for(BaseDto module : moduleList)
			{
                mcodebuilder.append(module.get("moduleCode") + "=\"" + module.get("moduleCode") + "\",");
                smodulemaps.put(module.getAsString("moduleCode"),module);
                newlist.add((BaseDto)module.clone());
			}
			mcodebuilder = new StringBuilder(mcodebuilder.substring(0,mcodebuilder.length() - 1));
			mcodebuilder.append("}");
			account.put("modulestr",mcodebuilder.toString());
			account.put("moduleList",moduleList);
			account.put("moduleMap",smodulemaps); 

			//封装菜单树,并存入modulemaps中(模块code为键,模块对象为值)
			setModuleTree(newlist,newlist);
			for(BaseDto module : newlist)
			{
				modulemaps.put(module.getAsString("moduleCode"),module);
			}
		}
		catch (Exception e)
		{
			logger.error("查询用户功能模块信息发生异常,异常消息:" + e);
		}
		return modulemaps;
	}

	/**
	 * 根据模块集合封装模块树
	 * @author 	sjq
	 * @param 	distMenuList	菜单集合
	 * @param 	sourceMenuList	模块集合
	 */
	private void setModuleTree(List<BaseDto> distMenuList,List<BaseDto> sourceMenuList)
	{
		String moduleId = null;
		List<BaseDto> childList = null;
		List<BaseDto> removeList = new ArrayList<BaseDto>();
		for(BaseDto module : distMenuList)
		{
			if(module.getAsInteger("moduleType") == 0 && module.getAsInteger("parentModuleId") == 1)
			{
				moduleId = module.getAsString("id");	//当前模块的模块id
				childList = new ArrayList<BaseDto>();	//用来存放当前模块的子模块
				for(BaseDto childModule : sourceMenuList)
				{
					if(childModule.getAsInteger("moduleType") == 0 && moduleId.equals(childModule.getAsString("parentModuleId")))
					{
						childList.add(childModule);
						removeList.add(childModule);
					}
				}
				if(childList.size() > 0)
				{
					setModuleTree(childList,sourceMenuList);
					module.put("childList",childList);	//设置当前模块的子模块
				}
			}
		}
		//移除不在树结构中的模块
		distMenuList.removeAll(removeList);
	}
}