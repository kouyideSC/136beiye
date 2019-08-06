package com.caipiao.admin.login.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.encrypt.RSA;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.base.UserBean;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonPageData;
import com.caipiao.domain.user.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.caipiao.admin.service.login.LoginService;
import com.caipiao.common.encrypt.MD5;

import net.sf.json.JSONObject;

@Controller
public class LoginController
{
	private static Logger logger = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private LoginService loginService;

	/**
	 * 管理后台登录
	 * @author	sjq
	 */
	@RequestMapping("/login")
	public void login(HttpServletRequest request, HttpServletResponse response)
	{
		String name = request.getParameter("name"); 		// 获取帐户名
		String password = request.getParameter("password"); // 获取密码
		JsonPageData result = new JsonPageData();
		try
		{
			if(StringUtils.isEmpty(name) || StringUtils.isEmpty(password))
			{
				result.setDcode(ConstantUtils.OPERATION_FAILURE);
				result.setDmsg("帐号或密码不能为空!");
				WebUtils.write(result.toString(),response);
				return;
			}
			//根据条件查询用户信息
			BaseDto params = new BaseDto();
			params.put("accountName", name);
			params.put("password",MD5.md5(password));
			Dto accountDto = loginService.getLoginUserInfo(params);
			if (accountDto == null || StringUtils.isEmpty(accountDto.getAsString("id")))
			{
				result.setDcode(ConstantUtils.OPERATION_FAILURE);
				result.setDmsg("帐号不存在或密码错误!");
				WebUtils.write(result.toString(),response);
				return;
			}
			// 判断用户是否被锁
			if (accountDto.getAsInteger("isLock") == 1)
			{
				result.setDcode(ConstantUtils.OPERATION_FAILURE);
				result.setDmsg("帐号已被锁定!");
				WebUtils.write(result.toString(),response);
				return;
			}
			// 查询登录用户相关的功能模块权限,并在session中保存当前登录用户对象及当前登录用户拥有权限的模块集合
			logger.info("[管理后台登录]" + (accountDto.getAsString("accountName") + "(" + accountDto.getAsString("personalName") + ")") + "登录了管理后台.");
			accountDto.put("isSale",-1);
			Map<String, BaseDto> modulemaps = loginService.getLoginUserModules(accountDto);// 获取用户有权限访问的功能模块
			SessionUtil.setSessionAttribute(request, ConstantUtils.USER_LOGGED_SESSION_SKEY, accountDto);
			SessionUtil.setSessionAttribute(request, ConstantUtils.USER_LOGGED_SESSION_FUNCSKEY,accountDto.get("moduleMap"));
			SessionUtil.setSessionAttribute(request, ConstantUtils.USER_LOGGED_SESSION_FUNCSTRSKEY,accountDto.get("modulestr"));

			//创建左侧导航菜单,并存入session中
			String realpath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
			BaseDto htmldto = createMenuHtml(modulemaps, realpath);
			SessionUtil.setSessionAttribute(request, ConstantUtils.SESSION_MENU_HTML_SKEY, htmldto.get(ConstantUtils.SESSION_MENU_HTML_SKEY));

			// 返回登录状态为成功
			result.setDcode(ConstantUtils.OPERATION_SUCCESS);
			result.setDmsg("登录成功.");
			WebUtils.write(result.toString(),response);
		}
		catch (Exception e)
		{
			logger.error("[管理后台登录]登录发生异常,帐号=" + name + ",异常信息:", e);
		}
	}

	/**
	 * 销售/代理后台登录
	 */
	@RequestMapping("/agent/login")
	public void angentLogin(HttpServletRequest request, HttpServletResponse response)
	{
		JsonPageData result = new JsonPageData();
		Dto params = WebUtils.getParamsAsDto(request);//提取参数
		try
		{
			//参数校验
			if(StringUtil.isEmpty(params.get("mobile")) || StringUtil.isEmpty(params.get("password")))
			{
				result.setDcode(ConstantUtils.OPERATION_FAILURE);
				result.setDmsg("手机号或密码不能为空!");
				WebUtils.write(result.toString(),response);
				return;
			}
			//登录验证
			if(loginService.passwordAuthLogin(params))
			{
				/**
				 * 在session中保存登录信息
				 */
				//保存当前登录用户对象及当前登录用户拥有权限的模块集合
				Dto accountDto = (Dto)params.get("accountDto");
				logger.info("[销售/代理后台登录]" + (accountDto.getAsString("accountName") + "(" + accountDto.getAsString("nickName") + ")") + "登录了销售/代理后台.");
				SessionUtil.setSessionAttribute(request, ConstantUtils.USER_LOGGED_SESSION_SKEY, accountDto);
				SessionUtil.setSessionAttribute(request, ConstantUtils.USER_LOGGED_SESSION_FUNCSKEY,accountDto.get("moduleMap"));
				SessionUtil.setSessionAttribute(request, ConstantUtils.USER_LOGGED_SESSION_FUNCSTRSKEY,accountDto.get("modulestr"));

				//创建左侧导航菜单,并存入session中
				String realpath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
				Map<String,BaseDto> modulemaps = (Map<String,BaseDto>)accountDto.get("modulemaps");
				BaseDto htmldto = createMenuHtml(modulemaps, realpath);
				SessionUtil.setSessionAttribute(request, ConstantUtils.SESSION_MENU_HTML_SKEY, htmldto.get(ConstantUtils.SESSION_MENU_HTML_SKEY));

				// 返回登录状态为成功
				result.setDcode(ConstantUtils.OPERATION_SUCCESS);
				result.setDmsg("登录成功.");
				WebUtils.write(result.toString(),response);
			}
			else
			{
				result.setDcode(ConstantUtils.OPERATION_FAILURE);
				result.setDmsg(StringUtil.isEmpty(params.get("dmsg"))? "登录失败！" : params.getAsString("dmsg"));
			}
		}
		catch (Exception e)
		{
			logger.error("[销售/代理登录]登录发生异常,接收原始参数=" + params.toString() + ",异常信息:", e);
			result.setDcode(ConstantUtils.EXCEPTION_SCODE);
			result.setDmsg(ConstantUtils.EXCEPTION_SDESC);
		}
		WebUtils.write(result.toString(),response);
	}

	/**
	 * 退出登录
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			HttpSession session = request.getSession();
			String loginurl = "redirect:/error.jsp";
			String realpath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
			if(realpath.indexOf("agent.") > -1)
			{
				loginurl = "redirect:/cpagent.jsp";
			}
			else if(realpath.indexOf("admin.") > -1)
			{
				loginurl = "redirect:/login.jsp";
			}
			session.removeAttribute(ConstantUtils.USER_LOGGED_SESSION_SKEY);
			session.removeAttribute(ConstantUtils.USER_LOGGED_SESSION_FUNCSKEY);
			session.removeAttribute(ConstantUtils.SESSION_MENU_HTML_SKEY);
			session.removeAttribute(ConstantUtils.SESSION_FAVORITE_MENU_HTML_SKEY);
			session.removeAttribute(ConstantUtils.SESSION_SETTING_MENU_HTML_SKEY);
			return loginurl;
		}
		catch(Exception e)
		{
			logger.error("用户退出发生异常,异常信息:", e);
		}
		return null;
	}

	/**
	 * 拼装左侧导航菜单
	 * @author 	sjq
	 * @param 	modulemaps	模块map
	 * @param 	realpath	根访问路径
	 * @return 	htmldto 	包含左侧导航菜单html字符串的对象
	 */
	@SuppressWarnings("unchecked")
	public static BaseDto createMenuHtml(Map<String, BaseDto> modulemaps, String realpath)
	{
		BaseDto htmldto = new BaseDto();
		htmldto.put(ConstantUtils.SESSION_MENU_HTML_SKEY, "无权限访问相关功能.");
		try
		{
            //拼装左侧导航菜单
			StringBuilder menuHtml = new StringBuilder("");
			/*menuHtml.append("<li class=\"menu-item clearfix\" mcode=\"menu_index\" mname=\"主页\" links=\"" + realpath + "/home\">");
			menuHtml.append("<span class=\"cp-icon icon-home\"></span>");
			menuHtml.append("<span class=\"menu-desc\">主页</span>");
			menuHtml.append("</li>");*/
			if (modulemaps != null)
			{
				BaseDto basedto = null;
				for (Map.Entry<String, BaseDto> entry : modulemaps.entrySet())
				{
					basedto = entry.getValue();
					if(basedto.getAsInteger("parentModuleId") == 1 && basedto.getAsInteger("moduleType") == 0)
					{
						// 拼装一级菜单
						menuHtml.append("<li class=\"menu-item clearfix\" mcode=\"" + basedto.getAsString("moduleCode") + "\" ");
						String url = basedto.getAsString("moduleUrl");
						if(StringUtils.isNotEmpty(url))
						{
							menuHtml.append("links=\"" + realpath + url + "\" ");
						}
						menuHtml.append("mname=\"" + basedto.getAsString("moduleName") + "\">");
						menuHtml.append("<span class=\"cp-icon " + basedto.getAsString("iconfont") + "\"></span>");
						menuHtml.append("<span class=\"menu-desc\">" + basedto.getAsString("moduleName") + "</span>");

						// 拼装一级菜单下的子菜单
						List<BaseDto> childList = basedto.getAsList("childList");// 一级菜单下的子菜单
						if (childList != null && childList.size() > 0)
						{
							menuHtml.append("<span class=\"plus-icon p-right\"></span>");
							menuHtml.append("<ul class=\"list-wrapper\">");
							for(BaseDto childdto : childList)
							{
								menuHtml.append("<li class=\"list-item\" mcode=\"" + childdto.getAsString("moduleCode") + "\" ");
								url = childdto.getAsString("moduleUrl");
								if(StringUtils.isNotEmpty(url))
								{
									menuHtml.append("links=\"" + realpath + url + "\" ");
								}
								menuHtml.append("mname=\"" + childdto.getAsString("moduleName") + "\">");
								menuHtml.append(childdto.getAsString("moduleName"));
								menuHtml.append("</li>");
							}
							menuHtml.append("</ul>");
						}
						menuHtml.append("</li>");
					}
				}
			}
			htmldto.put(ConstantUtils.SESSION_MENU_HTML_SKEY,menuHtml.toString());
		}
		catch (Exception e)
		{
			logger.error("[拼装左侧导航菜单]发生异常!异常信息:", e);
		}
		return htmldto;
	}
}