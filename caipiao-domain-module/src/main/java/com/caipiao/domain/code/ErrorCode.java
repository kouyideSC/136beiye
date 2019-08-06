package com.caipiao.domain.code;

import java.io.Serializable;

public abstract class ErrorCode implements Serializable {
	private static final long serialVersionUID = 3867285300059808138L;

	// ===================系统级提示状态码定义===================
	public static final int SUCCESS = 200;
	public static final String SUCCESS_MSG = "处理成功";
	public static final int NETWORK_ERROR = 201;
	public static final String NETWORK_ERROR_MSG = "您的网络不稳定,请稍后重试";
	public static final int SERVER_ERROR = 202;
	public static final String SERVER_ERROR_MSG = "系统服务不稳定,请稍后重试";

	public static final int ERROR_100001 = 100001;
	public static final String ERROR_100001_MSG = "请先登录再操作";
	public static final int ERROR_100002 = 100002;
	public static final String ERROR_100002_MSG = "非法渠道来源,请联系客服";
	public static final int ERROR_100003 = 100003;
	public static final String ERROR_100003_MSG = "签名验证不通过";
	public static final int ERROR_100004 = 100004;
	public static final String ERROR_100004_MSG = "限制访问,未开通IP白名单";
	public static final int ERROR_100005 = 100005;
	public static final String ERROR_100005_MSG = "未配置渠道出款账户,请联系客服";
	public static final int ERROR_100006 = 100006;
	public static final String ERROR_100006_MSG = "渠道出款账户余额不足,请先充值";
	public static final int ERROR_100007 = 100007;
	public static final String ERROR_100007_MSG = "超出查询支持的最大订单数";
	public static final int ERROR_100008 = 100008;
	public static final String ERROR_100008_MSG = "尚未绑定用户信息";

	/**
	 * 根据异常码返回异常描述
	 * @param code
	 * @return 
	 */
	public abstract String getCodeMsg(int code);
}
