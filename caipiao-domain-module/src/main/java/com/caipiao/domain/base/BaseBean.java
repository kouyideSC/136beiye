package com.caipiao.domain.base;

import java.io.Serializable;

/**
 * 基类bean-包含各模块公共属性
 */
public class BaseBean implements Serializable {
	private static final long serialVersionUID = -7272182833911222440L;
	protected Long userId;//用户编号
	protected String password;//用户密码
	protected String oldPassword;//旧密码
	protected Integer userType = 0;//用户类型 0-普通用户 1-渠道合作用户 2-第三方联合登录用户 3-..可扩展.. 8888-内部虚拟用户 9999-内部出款用户
	protected String ipAddress = "";//IP地址
	protected String appId;//为接入端分配的应用编号参数 用来签名
	protected String marketFrom;//市场来源
	protected String token;//token令牌字符串
	protected String key;//token对应秘钥
	protected String device;//设备号
	protected String content;//验证码
	protected Integer loginType;//用户登录类型 0-普通密码登录 1-TOKEN登录 2-微信联合登录 3-QQ联合登录 4-支付宝联合登录 5-验证码登录 6-...
	protected boolean isFlag;//通用标记
	protected String version;//版本号
	protected Integer pageNo; //当前页码
	protected long startTime=0;//接收请求时间

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMarketFrom() {
		return marketFrom;
	}

	public void setMarketFrom(String marketFrom) {
		this.marketFrom = marketFrom;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public boolean isFlag() {
		return isFlag;
	}

	public void setFlag(boolean flag) {
		isFlag = flag;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}
