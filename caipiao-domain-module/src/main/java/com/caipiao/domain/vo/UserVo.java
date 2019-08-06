package com.caipiao.domain.vo;

import com.caipiao.domain.cpadmin.Dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 前端展示-用户数据对象
 * Created by kouyi on 2017/10/23.
 */
public class UserVo implements Serializable {
    private static final long serialVersionUID = 3101957974572880560L;
    private Long id;//编号
    private String nickName;//昵称
    private Integer updateUserNameNum;//更新昵称次数
    private String realName;//真实姓名
    private Integer isPasswordSafe;//是否密码保护 0-未保护 1-已保护
    private String idCard;//身份证号
    private Integer idCardIsBind;//身份证是否绑定 0-未绑定 1-绑定
    private String avatar;//头像
    private String mobile;//手机号
    private Integer status;//状态 -1-注销 0-冻结 1-正常
    private Integer vipLevel;//VIP等级(1-普通 2-...待扩展)
    private Date lastLoginTime;//上次登录时间
    private String lastLoginIp;//上次登录IP
    private String bankName;//银行名称
    private String bankCard;//银行卡号
    private Integer bankIsBind;//是否绑定银行卡 0-未绑定 1-绑定 2-更新绑定卡（待处理-审核成功后=1）
    private Integer followNum;//关注数
    private Integer fansNum;//粉丝数
    private Integer isWhite;//是否开通购彩白名单 0-未开通 1-开通
    private Integer isSale;//用户头衔类型 0-普通用户 1-销售员 2-代理员
    private Integer score;//积分
    private Integer continuitySignDay;//连续签到天数
    private Integer securityLevel;//用户安全级别
    private String code;//邀请码
    private Integer showRebate = 0;//是否显示返利菜单(0-不显示 1-显示)
    private String token;//用户当前token
    private String key;//用户token对应秘钥
    private String balance;//账户总余额
    private String withDraw;//可提现金额
    private Dto winPopup;//中奖信息弹窗
    private Integer isAdmin;//是否为管理员,0-不是 1-是

    public static List<String> h5column = new ArrayList<>();
    static{
        h5column.add("code");
        h5column.add("continuitySignDay");
        h5column.add("fansNum");
        h5column.add("followNum");
        h5column.add("isPasswordSafe");
        h5column.add("idCard");
        h5column.add("idCardIsBind");
        h5column.add("isSale");
        h5column.add("isWhite");
        h5column.add("key");
        h5column.add("lastLoginIp");
        h5column.add("realName");
        h5column.add("score");
        h5column.add("securityLevel");
        h5column.add("token");
        h5column.add("updateUserNameNum");
        h5column.add("vipLevel");
        h5column.add("withDraw");
        h5column.add("bankCard");
        h5column.add("bankIsBind");
        h5column.add("bankName");
        h5column.add("isAdmin");
    }

    public Dto getWinPopup() {
        return winPopup;
    }

    public void setWinPopup(Dto winPopup) {
        this.winPopup = winPopup;
    }

    public Integer getShowRebate() {
        return showRebate;
    }

    public void setShowRebate(Integer showRebate) {
        this.showRebate = showRebate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getUpdateUserNameNum() {
        return updateUserNameNum;
    }

    public void setUpdateUserNameNum(Integer updateUserNameNum) {
        this.updateUserNameNum = updateUserNameNum;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public Integer getBankIsBind() {
        return bankIsBind;
    }

    public void setBankIsBind(Integer bankIsBind) {
        this.bankIsBind = bankIsBind;
    }

    public Integer getFollowNum() {
        return followNum;
    }

    public void setFollowNum(Integer followNum) {
        this.followNum = followNum;
    }

    public Integer getFansNum() {
        return fansNum;
    }

    public void setFansNum(Integer fansNum) {
        this.fansNum = fansNum;
    }

    public Integer getIsWhite() {
        return isWhite;
    }

    public void setIsWhite(Integer isWhite) {
        this.isWhite = isWhite;
    }

    public Integer getIsSale() {
        return isSale;
    }

    public void setIsSale(Integer isSale) {
        this.isSale = isSale;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getContinuitySignDay() {
        return continuitySignDay;
    }

    public void setContinuitySignDay(Integer continuitySignDay) {
        this.continuitySignDay = continuitySignDay;
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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getWithDraw() {
        return withDraw;
    }

    public void setWithDraw(String withDraw) {
        this.withDraw = withDraw;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public Integer getIsPasswordSafe() {
        return isPasswordSafe;
    }

    public void setIsPasswordSafe(Integer isPasswordSafe) {
        this.isPasswordSafe = isPasswordSafe;
    }

    public Integer getIdCardIsBind() {
        return idCardIsBind;
    }

    public void setIdCardIsBind(Integer idCardIsBind) {
        this.idCardIsBind = idCardIsBind;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }
}
