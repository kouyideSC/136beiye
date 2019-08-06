package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.*;

/**
 * 活动对象
 * Created by kouyi on 2017/9/21.
 */
public class Activity implements Serializable {
    private static final long serialVersionUID = -3137345528851763716L;
    private Long id;
    private String activityName;//活动名称
    private Integer activityType;//活动类型(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告 4-资讯/优惠信息 5-app启动页文件)
    private Integer isbanner;//是否设置为焦点图，针对非焦点图类型的活动 0-不设置 1-设置
    private Integer couponType;//优惠券赠送类型(0-注册送 1-充值送)
    private Integer couponMode;//优惠券赠送模式(0-固定模式 1-自定义模式)
    private String couponIds;//活动优惠券赠送信息
    private Date couponExpireTime;//优惠券过期时间
    private String title;//活动显示小标题
    private String build;//活动详情显示模式(0-活动模式 1-公告模式 2-资讯模式 1010-双色球购买页 其它彩种号表示跳转到响应的彩种购买页)
    private Integer clientType;//显示客户端(1-app 2-h5 3-web)
    private String linkUrl;//点击活动图片链接到url
    private String pictureUrl;//显示的图片地址
    private Integer isShow;//是否显示(0-否 1-是)
    private String content;//公告内容
    private Date beginTime;//活动开始时间
    private Date expireTime;//活动过期时间
    private Date createTime;//活动入库时间

    private String pictureLink;//显示图片的完整链接地址(非数据库字段)

    /**
     * 首页banner发送到前端时过滤不展示的属性
     * @return
     */
    public static List<String> column_banner = new ArrayList<>();
    static{
        column_banner.add("activityName");
        column_banner.add("activityType");
        column_banner.add("clientType");
        column_banner.add("isShow");
        column_banner.add("beginTime");
        column_banner.add("expireTime");
        column_banner.add("createTime");
    }

    /**
     * 公告发送到前端时过滤不展示的属性
     * @return
     */
    public static List<String> column_notice = new ArrayList<>();
    static{
        column_notice.add("activityName");
        column_notice.add("activityType");
        column_notice.add("clientType");
        //column_notice.add("build");
        column_notice.add("pictureUrl");
        //column_notice.add("linkUrl");
        column_notice.add("isShow");
        column_notice.add("beginTime");
        column_notice.add("expireTime");
        column_notice.add("createTime");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    public Integer getIsbanner() {
        return isbanner;
    }

    public void setIsbanner(Integer isbanner) {
        this.isbanner = isbanner;
    }

    public Integer getCouponType() {
        return couponType;
    }

    public void setCouponType(Integer couponType) {
        this.couponType = couponType;
    }

    public Integer getCouponMode() {
        return couponMode;
    }

    public void setCouponMode(Integer couponMode) {
        this.couponMode = couponMode;
    }

    public String getCouponIds() {
        return couponIds;
    }

    public void setCouponIds(String couponIds) {
        this.couponIds = couponIds;
    }

    public Date getCouponExpireTime() {
        return couponExpireTime;
    }

    public void setCouponExpireTime(Date couponExpireTime) {
        this.couponExpireTime = couponExpireTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }
}