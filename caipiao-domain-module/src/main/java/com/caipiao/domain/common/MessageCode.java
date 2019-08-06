package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 消息对象
 * Created by kouyi on 2017/10/25.
 */
public class MessageCode implements Serializable{
	private static final long serialVersionUID = -6061136248458872486L;
	private Long id;//唯一标识
	private Long userId;//用户编号
	private String mobile;//手机号
	private Integer type;//类型，1-注册验证码,2-更换手机号,3-找回密码 4-实名认证 5-微信/QQ绑定用户信息
	private String content;//发送内容
	private Integer state;//发送状态 0-未发送 1-已发送
	private Integer sendCode;//发送结果状态
	private String sendInfo;//发送结果描述
	private Integer isAuth;//验证状态 0-未验证 1-已验证
	private Date beginTime;//开始发送时间（定时发送场景）
	private Integer tryNumber=0;//尝试发送次数
	private Date createTime;//创建时间
	private Date expireTime;//过期时间

	/**
	 * 消息对象发送到前端时过滤不展示的属性
	 * @return
	 */
	public static Map<String, String> filterColumn = new HashMap<String, String>();
	static{
		filterColumn.put("id", "id");
		filterColumn.put("userId", "userId");
		filterColumn.put("type", "type");
		filterColumn.put("state", "state");
		filterColumn.put("sendCode", "sendCode");
		filterColumn.put("sendInfo", "sendInfo");
		filterColumn.put("isAuth", "isAuth");
		filterColumn.put("beginTime", "beginTime");
		filterColumn.put("tryNumber", "tryNumber");
		filterColumn.put("createTime", "createTime");
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getSendCode() {
		return sendCode;
	}

	public void setSendCode(Integer sendCode) {
		this.sendCode = sendCode;
	}

	public String getSendInfo() {
		return sendInfo;
	}

	public void setSendInfo(String sendInfo) {
		this.sendInfo = sendInfo;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Integer getTryNumber() {
		return tryNumber;
	}

	public void setTryNumber(Integer tryNumber) {
		this.tryNumber = tryNumber;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Integer getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(Integer isAuth) {
		this.isAuth = isAuth;
	}
}
