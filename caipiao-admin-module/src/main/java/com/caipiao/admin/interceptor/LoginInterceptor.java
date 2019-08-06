package com.caipiao.admin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonPageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 登录拦截器
 * @author	sjq
 */
public class LoginInterceptor extends HandlerInterceptorAdapter 
{
	private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
	private static String[] methodFilters = new String[]{"insert","add","save","update","edit","remove","delete"};//操作方法前缀(涉及到数据库改动的操作)
	private static String suffix = "__user_operation";
	
	/**
	 * 调用前
	 */
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception 
	{
		try
		{
			HttpSession session = request.getSession();
			if(session.getAttribute(ConstantUtils.USER_LOGGED_SESSION_SKEY) != null)
			{
				Dto account = (Dto)session.getAttribute(ConstantUtils.USER_LOGGED_SESSION_SKEY);
				request.setAttribute("opaccountId",account.getAsString("id"));
				request.setAttribute("opaccountName",account.getAsString("accountName"));
				request.setAttribute("oppersonalName",account.getAsString("personalName"));
				request.setAttribute("opfullName",account.getAsString("accountName") + "(" + account.getAsString("personalName") + ")");
				
				//判断当前操作是否过于频繁
				HandlerMethod method = (HandlerMethod) handler;
				String methodName = method.getMethod().getName();
				if(methodName.startsWith(methodFilters[0]) 
					|| methodName.startsWith(methodFilters[1]) 
					|| methodName.startsWith(methodFilters[2]) 
					|| methodName.startsWith(methodFilters[3]))
				{
					if(session.getAttribute(methodName + suffix) != null)
					{
						long current = System.currentTimeMillis();
						long time = Long.parseLong(session.getAttribute(methodName + suffix).toString());
						if(current - time > 2 * 1000)
						{
							session.removeAttribute(methodName + suffix);
						}
						else
						{
							JsonPageData pageData = new JsonPageData(ConstantUtils.OPERATE_FREQUENTLY_SCODE,ConstantUtils.OPERATE_FREQUENTLY_SDESC);
							WebUtils.write(pageData.toString(),response);
							return false;
						}
					}
					session.setAttribute(methodName + suffix,System.currentTimeMillis());
				}
				return true;
			}
			else
			{
				//ajax请求,返回未登录状态及消息
				if(request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest"))
				{
					request.setAttribute(ConstantUtils.INTERCEPTOR_ERRORCODE_SKEY,ConstantUtils.USER_NOT_LOGIN_SCODE);
					JsonPageData pageData = new JsonPageData(ConstantUtils.USER_NOT_LOGIN_SCODE,ConstantUtils.USER_NOT_LOGIN_SDESC);
					WebUtils.write(pageData.toString(),response);
					return false;
				}
				else
				{
					//非ajax请求,直接跳转至登录页
					String realpath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
					if(realpath.indexOf("agent.") > -1)
					{
						response.sendRedirect(realpath + "/cpagent.jsp");
					}
					else if(realpath.indexOf("admin.") > -1)
					{
						response.sendRedirect(realpath + "/login.jsp");
					}
					else
					{
						response.sendRedirect(realpath + "/error.jsp");
					}
					return false;
				}
			}
		}
		catch(Exception e)
		{
			logger.error("[登录拦截][调用前]发生异常,异常信息：",e);
			JsonPageData pageData = new JsonPageData(ConstantUtils.EXCEPTION_SCODE,e.getMessage());
			WebUtils.write(pageData.toString(),response);
		}
		return false;
	}
	
	/**
	 * 调用后(调用方法完毕且开始渲染视图前)
	 * @author	sjq
	 */
	public void postHandle(HttpServletRequest request,HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception 
	{
		try
		{
			//移除正在操作的标识
			HttpSession session = request.getSession();
			HandlerMethod method = (HandlerMethod) handler;
			String methodName = method.getMethod().getName();
			if(methodName.startsWith(methodFilters[0]) 
				|| methodName.startsWith(methodFilters[1]) 
				|| methodName.startsWith(methodFilters[2]) 
				|| methodName.startsWith(methodFilters[3]))
			{
				session.removeAttribute(methodName + suffix);
			}
		}
		catch(Exception e)
		{
			logger.error("[登录拦截][调用后]发生异常,异常信息：",e);
		}
	}
}