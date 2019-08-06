package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统帐户对象
 */
public class Account implements Serializable
{
    private Long id;//主键id
    private String accountName;//帐户名
    private String password;//密码
    private String personalName;//真实姓名
    private Integer isLock;//是否锁定 0-未锁定 1-已锁定
    private Long organizationId;//组织id
    private Long jobTypeId;//岗位id
    private Integer isSuperuser;//是否为超级用户 0-非超级用户 1-超级用户
    private String idcard;//身份证号
    private String email;//邮箱
    private String weixin;//微信
    private String qq;//qq
    private String mobile;//手机号
    private String avatar;//头像
    private Integer workStatus;//工作状态 1-在职 2-离职
    private Integer receiveTodoByemail;//是否开启邮件接收待办 0-不开启 1-开启
    private Integer receiveMsgByemail;//是否开启邮件接收消息 0-不开启 1-开启
    private Integer receiveNoticeByemail;//是否开启邮件接收公告 0-不开启 1-开启
    private Long creator;//创建者
    private Date createTime;//创建时间
    private Long modifier;//修改者
    private Date modifiedTime;//修改时间
    private Integer deleteFlag;//删除标志 0-未删除 1-已删除
    private String token;//登录token
    private Date lastLoginTime;//最后一次登录时间
    private String lastLoginIp;//最后一次登录ip

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getJobTypeId() {
        return jobTypeId;
    }

    public void setJobTypeId(Long jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    public Integer getIsSuperuser() {
        return isSuperuser;
    }

    public void setIsSuperuser(Integer isSuperuser) {
        this.isSuperuser = isSuperuser;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(Integer workStatus) {
        this.workStatus = workStatus;
    }

    public Integer getReceiveTodoByemail() {
        return receiveTodoByemail;
    }

    public void setReceiveTodoByemail(Integer receiveTodoByemail) {
        this.receiveTodoByemail = receiveTodoByemail;
    }

    public Integer getReceiveMsgByemail() {
        return receiveMsgByemail;
    }

    public void setReceiveMsgByemail(Integer receiveMsgByemail) {
        this.receiveMsgByemail = receiveMsgByemail;
    }

    public Integer getReceiveNoticeByemail() {
        return receiveNoticeByemail;
    }

    public void setReceiveNoticeByemail(Integer receiveNoticeByemail) {
        this.receiveNoticeByemail = receiveNoticeByemail;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }
}