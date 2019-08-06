package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 渠道对象
 * Created by kouyi on 2018/03/14.
 */
public class Channel implements Serializable{
	private static final long serialVersionUID = 1944957883759852158L;
	private Long id;//唯一标识
	private String channelName;//渠道名称
	private String channelCode;//渠道编号
	private String secret;//秘钥
	private String authKey;//验证key
	private String contactMobile;//联系人手机号
	private Integer status;//渠道状态(0-关闭 1-开启)
	private Integer notifyStatus;//出票通知状态(0-关闭 1-开启)
	private String notifyUrl;//商户接收通知地址
	private Long outAccountUserId;//出款账户编号
	private Double overstepAccount;//渠道透支金额
	private Date beginTime;//生效开始时间
	private Date endTime;//生效结束时间
	private String ipLimit;//ip地址访问限制
	private String remark;//描述
	private Date createTime;//创建时间
	private Date expireTime;//过期时间

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public Double getOverstepAccount() {
		return overstepAccount;
	}

	public void setOverstepAccount(Double overstepAccount) {
		this.overstepAccount = overstepAccount;
	}

	public String getIpLimit() {
		return ipLimit;
	}

	public void setIpLimit(String ipLimit) {
		this.ipLimit = ipLimit;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getNotifyStatus() {
		return notifyStatus;
	}

	public void setNotifyStatus(Integer notifyStatus) {
		this.notifyStatus = notifyStatus;
	}

	public Long getOutAccountUserId() {
		return outAccountUserId;
	}

	public void setOutAccountUserId(Long outAccountUserId) {
		this.outAccountUserId = outAccountUserId;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
}
