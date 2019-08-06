package com.caipiao.admin.util;

/**
 * 常量工具类
 * @author	sjq
 */
public class ConstantUtils 
{
	/**
	 * 操作成功
	 */
	public static final int OPERATION_SUCCESS = 1000;
	/**
	 * 通用操作失败状态码
	 */
	public static final int OPERATION_FAILURE = -1000;
	/**
	 * 已登录用户在session中存放的登录用户对象key
	 */
	public static final String USER_LOGGED_SESSION_SKEY = "user_logged_session_skey";
	/**
	 * 已登录用户在session中存放的拥有访问权限的功能模块key
	 */
	public static final String USER_LOGGED_SESSION_FUNCSKEY = "user_logged_session_funcskey";
	/**
	 * 已登录用户在session中存放的拥有访问权限的功能模块key(json形式的字符串)
	 */
	public static final String USER_LOGGED_SESSION_FUNCSTRSKEY = "user_logged_session_funcstrskey";
	/**
	 * 已登录用户在session中存放的拥有访问权限的数据块key
	 */
	public static final String USER_LOGGED_SESSION_DATASKEY = "user_logged_session_dataskey";
	/**
	 * 用户未登录-提示code
	 */
	public static final int USER_NOT_LOGIN_SCODE = -1001;
	/**
	 * 用户未登录-提示消息
	 */
	public static final String USER_NOT_LOGIN_SDESC = "用户尚未登录.";
	/**
	 * 用户功能模块访问-无权限-提示code
	 */
	public static final int USER_MODULE_FORBIDDEN_SCODE = -1002;
	/**
	 * 用户功能模块访问-无权限-提示消息
	 */
	public static final String USER_MODULE_FORBIDDEN_SDESC = "相关功能无访问权限.";
	/**
	 * 用户数据块访问-无权限-提示code
	 */
	public static final int USER_DATA_FORBIDDEN_SCODE = -1003;
	/**
	 * 用户数据块访问-无权限-提示消息
	 */
	public static final String USER_DATA_FORBIDDEN_SDESC = "相关数据无查看权限.";
	/**
	 * 非法访问-提示code
	 */
	public static final int WRONGFUL_VISIT_SCODE = -1004;
	/**
	 * 非法访问-提示消息
	 */
	public static final String WRONGFUL_VISIT_SDESC = "非法访问.";
	/**
	 * 通用异常-提示code
	 */
	public static final int EXCEPTION_SCODE = -1005;
	/**
	 * 通用异常-提示消息
	 */
	public static final String EXCEPTION_SDESC = "系统发生异常.";
	/**
	 * 未知错误-提示code
	 */
	public static final int UNKNOW_SCODE = -1006;
	/**
	 * 未知错误-提示消息
	 */
	public static final String UNKNOW_SDESC = "未知错误.";
	/**
	 * 相同操作过于频繁-提示code
	 */
	public static final int OPERATE_FREQUENTLY_SCODE = -1007;
	/**
	 * 相同操作过于频繁-提示消息
	 */
	public static final String OPERATE_FREQUENTLY_SDESC = "相同操作过于频繁.";
	/**
	 * 拦截器未通过-错误编码key
	 */
	public static final String INTERCEPTOR_ERRORCODE_SKEY = "interceptor_error_code_skey";
	/**
	 * 系统功能模块在session中存放的key(map形式)
	 */
	public static final String SESSION_SYSTEM_MODULESKEY = "session_system_module_skey";
	/**
	 * 系统数据块在session中存放的key(map形式)
	 */
	public static final String SESSION_SYSTEM_DATASKEY = "session_system_data_skey";
	/**
	 * 系统功能模块在session中存放的key(list形式)
	 */
	public static final String SESSION_SYSTEM_MODULELIST_SKEY = "session_system_modulelist_skey";
	/**
	 * 系统数据块在session中存放的key(list形式)
	 */
	public static final String SESSION_SYSTEM_DATALIST_SKEY = "session_system_datalist_skey";
	/**
	 * 一级菜单在session中存放的key(html节点形式)
	 */
	public static final String SESSION_TOPMENU_HTML_SKEY = "session_topmenu_html_skey";
	/**
	 * 一级菜单及左侧sidebar导航菜单对象在session中存放的key(html节点形式)
	 */
	public static final String SESSION_MENU_HTML_SKEY = "session_menu_html_skey";
	/**
	 * 常用菜单导航对象在session中存放的key(html节点形式)
	 */
	public static final String SESSION_FAVORITE_MENU_HTML_SKEY = "session_favorite_menu_html_skey";
	/**
	 * 系统设置导航对象在session中存放的key(html节点形式)
	 */
	public static final String SESSION_SETTING_MENU_HTML_SKEY = "session_setting_menu_html_skey";
}