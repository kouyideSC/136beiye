package com.caipiao.domain.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户令牌对象
 * Created by kouyi on 2017/9/28.
 */
public class UserToken implements Serializable {
    private static final long serialVersionUID = 7214326464166666817L;
    private Long id;//编号
    private Long userId;//用户ID
    private String mobile;//手机号
    private String password;//用户密码
    private String token;//用户token
    private String tkey;//秘钥
    private Integer expiresin;//有效期（秒）
    private String device;//设备号imei
    private Date lastTime;//上次验证时间

    /**
     * 用户token发送到前端时过滤不展示的属性
     * @return
     */
    public static Map<String, String> filterColumn = new HashMap<String, String>();
    static{
        filterColumn.put("id", "id");
        filterColumn.put("userId", "userId");
        filterColumn.put("password", "password");
        filterColumn.put("expiresin", "expiresin");
        filterColumn.put("device", "device");
        filterColumn.put("lastTime", "lastTime");
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTkey() {
        return tkey;
    }

    public void setTkey(String tkey) {
        this.tkey = tkey;
    }

    public Integer getExpiresin() {
        return expiresin;
    }

    public void setExpiresin(Integer expiresin) {
        this.expiresin = expiresin;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
