package com.caipiao.domain.base;

import java.io.Serializable;

/**
 * 处理用户业务对象
 * Created by kouyi on 2017/9/20.
 */
public class UserBean extends BaseBean implements Serializable {
    private static final long serialVersionUID = 6846015131559917426L;
    private String mobile;//手机号
    private String nickName;//用户昵称
    private String avatar;//头像
    private Integer sex;//性别
    private String province;//省份
    private Long higherUid;//上级用户编号
    private String code;//邀请码
    private String openId;//QQ用户的openid
    private String accessToken;//调用接口凭证
    private String wxcode;//获取微信accessToken的票据
    private int validFlag;//校验标识(1-尚未绑定用户信息(针对第三方联合登录))

    private Object obj;//对象参数

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Long getHigherUid() {
        return higherUid;
    }

    public void setHigherUid(Long higherUid) {
        this.higherUid = higherUid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getWxcode() {
        return wxcode;
    }

    public void setWxcode(String wxcode) {
        this.wxcode = wxcode;
    }

    public int getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(int validFlag) {
        this.validFlag = validFlag;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}