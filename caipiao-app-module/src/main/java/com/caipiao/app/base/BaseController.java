package com.caipiao.app.base;

import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.UserBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 控制器基类
 * Created by kouyi on 2017/9/20.
 */
public class BaseController {
	protected static final String START_TIME = "startTime";//传递接收请求的时间
	protected static final String USER_LOGIN_OBJ = "loginUser";//传递登录用户对象键值
	protected static final String LOGIN_URI = "/user/login";

	/**
	 * 获取登录用户编号
	 * @param bean
	 * @param request
	 * @return
	 */
	protected void getLoginUserId(UserBean bean, HttpServletRequest request) {
		try {
			bean.setUserId((Long) request.getAttribute(USER_LOGIN_OBJ));
			if(StringUtil.isNotEmpty(request.getAttribute(START_TIME))) {
				bean.setStartTime(Long.parseLong(request.getAttribute(START_TIME).toString()));
			}
		} catch (Exception e) {
			bean.setUserId(0l);
		}
	}

	/**
	 * 获取登录用户编号
	 * @param 	request
	 * @return
	 */
	protected Long getLoginUserId(HttpServletRequest request)
	{
		try
		{
			return (Long)request.getAttribute(USER_LOGIN_OBJ);
		}
		catch (Exception e) {}
		return 0l;
	}

	/**
	 * 获取参数-设置到对象中进行传递
	 * @param bean
	 * @param request
	 * @return
	 */
	protected void getLoginUserInfo(UserBean bean, HttpServletRequest request) {
		//标记(true:调用login单纯登录操作 false:涉及用户业务操作中需要的登录权限验证)
		if(request.getRequestURI().indexOf(LOGIN_URI) > -1) {
			bean.setFlag(true);
		}
		if(StringUtil.isNotEmpty(request.getAttribute(START_TIME))) {
			bean.setStartTime(Long.parseLong(request.getAttribute(START_TIME).toString()));
		}
		bean.setIpAddress(getRequestIP(request));//设置IP地址
		bean.setObj(request.getAttribute(USER_LOGIN_OBJ));
	}

	/**
	 * 将处理后的结果返回给客户端-只返回状态码信息
	 * @author kouyi
	 * @param 
	 */
	protected void writeResponse(int code, HttpServletResponse response) {
		ResultBean result = new ResultBean();
		result.setErrorCode(code);
		List<String> columns = new ArrayList<>();
		columns.add("data");
		columns.add("pageNo");
		writeResponse(result, columns, response);
	}

	/**
	 * 将处理后的结果返回给客户端-不过滤属性
	 * @author kouyi
	 * @param
	 */
	protected void writeResponse(ResultBean bean, HttpServletResponse response) {
		DataOutputStream out = null;
		try {
			//设置页面不缓存
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			//response.addHeader("Access-Control-Allow-Origin", "http://www.tuiqiuxiong.com");
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
			response.setStatus(200);
			response.setContentType("text/json; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			out = new DataOutputStream(response.getOutputStream());
			StringBuffer buffer = new StringBuffer();
			buffer.append(JsonUtil.JsonObject(bean));
			out.write((new String(buffer)).getBytes("UTF-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 将处理后的结果返回给客户端-过滤filterColumns
	 * @author kouyi
	 * @param 
	 */
	protected void writeResponse(ResultBean bean, Map<String, String> filterColumns, HttpServletResponse response) {
		DataOutputStream out = null;
		try {
			//设置页面不缓存
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			//response.addHeader("Access-Control-Allow-Origin", "http://www.tuiqiuxiong.com");
			response.setHeader("Access-Control-Allow-Origin", "*");
		    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		    response.setHeader("Access-Control-Max-Age", "3600");
		    response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
			response.setStatus(200);
			response.setContentType("text/json; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			out = new DataOutputStream(response.getOutputStream());
			StringBuffer buffer = new StringBuffer();
			buffer.append(JsonUtil.JsonObject(bean, filterColumns));
			out.write((new String(buffer)).getBytes("UTF-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将处理后的结果返回给客户端-过滤filterColumns
	 * @author kouyi
	 * @param
	 */
	protected void writeResponse(ResultBean bean, List<String> filterColumns, HttpServletResponse response) {
		DataOutputStream out = null;
		try {
			//设置页面不缓存
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			//response.addHeader("Access-Control-Allow-Origin", "http://www.tuiqiuxiong.com");
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
			response.setStatus(200);
			response.setContentType("text/json; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			out = new DataOutputStream(response.getOutputStream());
			StringBuffer buffer = new StringBuffer();
			buffer.append(JsonUtil.JsonObject(bean, filterColumns));
			out.write((new String(buffer)).getBytes("UTF-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将处理后的结果返回
	 * @author	sjq
	 */
	protected void writeResponse(String code, HttpServletResponse response)
	{
		DataOutputStream out = null;
		try
		{
			//设置页面不缓存
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
			response.setStatus(200);
			response.setCharacterEncoding("UTF-8");
			out = new DataOutputStream(response.getOutputStream());
			out.write(code.getBytes("UTF-8"));
			out.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}


	/** 
     * 获取客户端ip 
     * @param request 
     * @return 
     */  
    public String getRequestIP(HttpServletRequest request) {  
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 多个代理时第一个IP为客户端真实IP
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}
}