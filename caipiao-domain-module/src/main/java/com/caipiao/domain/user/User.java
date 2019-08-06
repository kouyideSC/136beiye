package com.caipiao.domain.user;

import com.caipiao.domain.vo.BankInfoVo;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.*;

/**
 * 用户对象
 * Created by kouyi on 2017/9/21.
 */
public class User implements Serializable {
    private static final long serialVersionUID = -8834344864212105954L;
    private Long id;//编号
    private String nickName;//昵称
    private Integer updateUserNameNum;//更新昵称次数
    private String password;//密码
    private String payPassword;//支付密码
    private String realName;//真实姓名
    private String idCard;//身份证号
    private String avatar;//头像
    private Integer sex;//性别 0-男 1-女
    private String address;//联系地址
    private String mobile;//手机号
    private String email;//邮件地址
    private String qq;//邀请码(销售和代理才有)
    private String question;//验证问题
    private String answer;//答案
    private Integer status;//状态 -1-注销 0-冻结 1-正常
    private String device;//移动设备号imei
    private Integer vipLevel;//VIP等级(1-普通 2-...待扩展)
    private Integer loginDegree;//登录次数
    private Integer loginType;//登录类型 0-普通密码登录 1-TOKEN登录 2-微信联合登录 3-QQ联合登录 4-支付宝联合登录 5-验证码登录 6-渠道免密登录
    private String openId;//第三方登录openId
    private String weixinOpenId;//微信openId
    private String QqOpenId;//QQ openId
    private Date lastLoginTime;//上次登录时间
    private String lastLoginIp;//上次登录IP
    private Date registerTime;//注册时间
    private String registerIp;//注册IP
    private String province;//用户所在省份
    private BankInfoVo bankInfo;//银行卡信息
    private Integer bankIsBind;//是否绑定银行卡 0-未绑定 1-绑定 2-更新绑定卡（待处理-审核成功后=1）
    private String bankCardPositiveUrl;//银行卡正面上传图片URL
    private String bankCardReverseUrl;//银行卡反面上传图片URL
    private Integer userType;//用户类型 0-普通用户 1-渠道合作用户 2-..可扩展.. 8888-内部虚拟用户 9999-内部出款用户
    private Integer registerFrom;//注册来源 0-WWW 1-IOS 2-ANDROID 3-H5 4-Other
    private String marketFrom;//安装包市场来源
    private Integer followNum;//关注数
    private Integer fansNum;//粉丝数
    private Integer isWhite;//是否开通购彩白名单 0-未开通 1-开通
    private Integer whiteApplyStatus;//申请白名单状态 0-申请中 1-已处理
    private Date openWhiteTime;//白名单时间(申请时-表示申请时间 通过后-表示开通时间)
    private Long higherUid;//上级用户编号
    private Date higherTime;//绑定到上级的时间
    private Integer isSale;//用户头衔类型 0-普通用户 1-销售员 2-代理员
    private Date isSaleTime;//头衔变更时间
    private Integer isAdmin;//是否为管理员,0-不是 1-是
    private Integer score;//积分
    private Integer continuitySignDay;//连续签到天数
    private Date updateTime;//更新时间

    /**
     * 用户基本信息发送到前端时过滤不展示的属性
     * @return
     */
    public static Map<String, String> filterColumn = new HashMap<String, String>();
    public static List<String> h5column = new ArrayList<>();
    static{
        filterColumn.put("password", "password");
        filterColumn.put("payPassword", "payPassword");
        filterColumn.put("sex", "sex");
        filterColumn.put("address", "address");
        filterColumn.put("qq", "qq");
        filterColumn.put("question", "question");
        filterColumn.put("answer", "answer");
        filterColumn.put("device", "device");
        filterColumn.put("loginDegree", "loginDegree");
        filterColumn.put("loginType", "loginType");
        filterColumn.put("openId", "openId");
        filterColumn.put("weixinOpenId", "weixinOpenId");
        filterColumn.put("QqOpenId", "QqOpenId");
        filterColumn.put("registerTime", "registerTime");
        filterColumn.put("registerIp", "registerIp");
        filterColumn.put("province", "province");
        filterColumn.put("bankCardPositiveUrl", "bankCardPositiveUrl");
        filterColumn.put("bankCardReverseUrl", "bankCardReverseUrl");
        filterColumn.put("registerForm", "registerForm");
        filterColumn.put("higherUid", "higherUid");
        filterColumn.put("higherTime", "higherTime");

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
        h5column.add("lastLoginTime");
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public Integer getLoginDegree() {
        return loginDegree;
    }

    public void setLoginDegree(Integer loginDegree) {
        this.loginDegree = loginDegree;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getWeixinOpenId() {
        return weixinOpenId;
    }

    public void setWeixinOpenId(String weixinOpenId) {
        this.weixinOpenId = weixinOpenId;
    }

    public String getQqOpenId() {
        return QqOpenId;
    }

    public void setQqOpenId(String qqOpenId) {
        QqOpenId = qqOpenId;
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

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public BankInfoVo getBankInfo() {
        return bankInfo;
    }

    public void setBankInfo(String bankInfo) {
        JSONObject jsonObject = JSONObject.fromObject(bankInfo);
        this.bankInfo = (BankInfoVo)JSONObject.toBean(jsonObject,BankInfoVo.class);
    }

    public Integer getBankIsBind() {
        return bankIsBind;
    }

    public void setBankIsBind(Integer bankIsBind) {
        this.bankIsBind = bankIsBind;
    }

    public String getBankCardPositiveUrl() {
        return bankCardPositiveUrl;
    }

    public void setBankCardPositiveUrl(String bankCardPositiveUrl) {
        this.bankCardPositiveUrl = bankCardPositiveUrl;
    }

    public String getBankCardReverseUrl() {
        return bankCardReverseUrl;
    }

    public void setBankCardReverseUrl(String bankCardReverseUrl) {
        this.bankCardReverseUrl = bankCardReverseUrl;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getRegisterFrom() {
        return registerFrom;
    }

    public void setRegisterFrom(Integer registerFrom) {
        this.registerFrom = registerFrom;
    }

    public String getMarketFrom() {
        return marketFrom;
    }

    public void setMarketFrom(String marketFrom) {
        this.marketFrom = marketFrom;
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

    public Integer getWhiteApplyStatus() {
        return whiteApplyStatus;
    }

    public void setWhiteApplyStatus(Integer whiteApplyStatus) {
        this.whiteApplyStatus = whiteApplyStatus;
    }

    public Date getOpenWhiteTime() {
        return openWhiteTime;
    }

    public void setOpenWhiteTime(Date openWhiteTime) {
        this.openWhiteTime = openWhiteTime;
    }

    public Long getHigherUid() {
        return higherUid;
    }

    public void setHigherUid(Long higherUid) {
        this.higherUid = higherUid;
    }

    public Date getHigherTime() {
        return higherTime;
    }

    public void setHigherTime(Date higherTime) {
        this.higherTime = higherTime;
    }

    public Integer getIsSale() {
        return isSale;
    }

    public void setIsSale(Integer isSale) {
        this.isSale = isSale;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getIsSaleTime() {
        return isSaleTime;
    }

    public void setIsSaleTime(Date isSaleTime) {
        this.isSaleTime = isSaleTime;
    }
}
