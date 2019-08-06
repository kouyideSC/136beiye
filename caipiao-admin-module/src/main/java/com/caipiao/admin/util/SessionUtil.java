package com.caipiao.admin.util;

import com.caipiao.domain.cpadmin.Dto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtil
{

    public static void put(String key,Object value)
    {
        HttpSession session = WebUtils.getRequest().getSession(true);
        session.setAttribute(key,value);
    }

    public static Object get(String key,Object defaultValue)
    {
        HttpSession session = WebUtils.getRequest().getSession(true);
        Object value = session.getAttribute(key);
        if (value==null)
        {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 获取当前登录帐户
     * @return
     */
    public static Dto getCurrentAccount(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        Dto accountDto = (Dto) session.getAttribute(ConstantUtils.USER_LOGGED_SESSION_SKEY);
        return accountDto;
    }
    
    /**
     * 设置session属性
     * @author 	sjq
     * @param 	request	HttpServletRequest对象
     * @param	key		在session中的键
     * @param	value	在session中的值
     */
    public static void setSessionAttribute(HttpServletRequest request,String key,Object value)
    {
    	try
    	{
    		HttpSession session = request.getSession();
    		session.setAttribute(key,value);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}