package com.caipiao.admin.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.util.ConstantUtils;

import com.caipiao.admin.util.WebUtils;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.JsonPageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 功能模块/功能菜单 权限拦截器
 * @author	sjq
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter 
{
	private static final Logger logger = LoggerFactory.getLogger(AuthorityInterceptor.class);

	@SuppressWarnings({"unchecked"})
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception 
	{
		//当前登录用户如果是超级用户,则不做任何校验
		HttpSession session = request.getSession();
		BaseDto userdto = new BaseDto();
		if(session.getAttribute(ConstantUtils.USER_LOGGED_SESSION_SKEY) != null)
		{
			userdto = (BaseDto)session.getAttribute(ConstantUtils.USER_LOGGED_SESSION_SKEY);
		}
		if(userdto != null && userdto.size() > 0 && userdto.get("isSuperuser") != null && userdto.getAsInteger("isSuperuser") == 1)
		{
			return true;
		}
		//功能模块权限拦截,验证是否有权限访问相应的功能模块
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		ModuleAuthorityRequired moduleRequired = method.getAnnotation(ModuleAuthorityRequired.class);
		if(moduleRequired != null)
		{
			try
			{
				String[] mcodes = moduleRequired.mcode();	//提取当前访问的功能模块编码
				if(mcodes == null || mcodes.length == 0)
				{
					JsonPageData pageData = new JsonPageData(ConstantUtils.WRONGFUL_VISIT_SCODE,ConstantUtils.WRONGFUL_VISIT_SDESC);
					WebUtils.write(pageData.toString(),response);
					return false;
				}
				//从session中获取当前登录用户有权限的模块集合
				boolean checkflag = true;
				Map<String,BaseDto> modulemaps = (Map<String,BaseDto>)session.getAttribute(ConstantUtils.USER_LOGGED_SESSION_FUNCSKEY);
				if(modulemaps != null)
				{
					//如果多个code之间的关系为且,则任何一个code匹配不到功能块就返回false
					if(moduleRequired.mflag() == 0)
					{
						for(String mcode : mcodes)
						{
							if(modulemaps.get(mcode) == null)
							{
								checkflag = false;
								break;
							}
						}
					}
					//如果多个code之间的关系为或,则任意一个code匹配到功能模块就返回true
					else if(moduleRequired.mflag() == 1)
					{
						for(String mcode : mcodes)
						{
							if(modulemaps.get(mcode) != null)
							{
								checkflag = true;
								break;
							}
						}
					}
					else
					{
						checkflag = false;
					}
				}
				else
				{
					checkflag = false;
				}
				if(checkflag == false)
				{
					JsonPageData pageData = new JsonPageData(ConstantUtils.USER_MODULE_FORBIDDEN_SCODE,ConstantUtils.USER_MODULE_FORBIDDEN_SDESC);
					WebUtils.write(pageData.toString(),response);
				}
				return checkflag;
			}
			catch(Exception e)
			{
				logger.error("功能权限验证发生异常!" + "，登录帐户="
						+ (userdto == null? "未知" : userdto.getAsString("accountName"))
						+ ",mcode=" + moduleRequired.mcode()
						+ ",异常信息:" + e);
				return false;
			}
		}
		return true;
	}
}