package com.caipiao.domain.scheme;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户购彩方案对象
 * Created by kouyi on 2017/11/04.
 */
public class Scheme implements Serializable {
    private static final long serialVersionUID = 5737462424186100954L;
    private Long id;
    private Integer clientSource;//客户端来源
    private String clientSourceName;//客户端来源名称 0-web 1-ios 2-android 3-h5 4-其它
    private Long schemeUserId;//方案发起人用户编号
    private String lotteryId;//彩种编号
    private String lotteryName;//彩种名称
    /**
     * 玩法类型id
     * 1700-竞彩足球混投 1710-竞彩篮球混投 1720-竞彩足球胜平负 1900-竞彩足球让球胜平负
     * 1910-竞彩足球猜比分 1920-竞彩足球半全场 1930-竞彩足球进球数 1940-竞彩篮球胜负
     * 1950-竞彩篮球让分胜负 1960-竞彩篮球胜分差 1970-竞彩篮球大小分
     */
    private String playTypeId;
    private String schemeOrderId;//订单编号
    private Integer schemeType;//0-普通方案 1-追号方案 2-优化方案 3-跟单方案 4-神单方案
    private Integer schemeMultiple;//方案倍数
    private Integer schemeZs;//方案注数
    private Double schemeMoney;//方案金额
    private Double schemePayMoney;//支付金额(实际支付）
    private Integer channelCode;//支付渠道编号(300-余额支付[购彩] 301-优惠券支付)
    private String channelDesc;//支付渠道描述
    private Integer schemeStatus;//-1-无效 0-待支付 1-支付成功（预约中）2-出票中 3-预约成功 4-预约失败 5-撤单成功 6-出票失败撤单 7-截止未出票撤单
    private String schemeStatusDesc;//状态描述
    private String schemePlayType;//玩法类型
    private String schemeContent;//投注内容
    private String schemeSpContent;//方案投注项赔率
    private String schemeYhContent;//优化方案投注项
    private Double prize;//税前总奖金
    private Double prizeSubjoin;//官方-税前加奖奖金
    private Double prizeSubjoinSite;//网站加奖-税前加奖奖金
    private Double prizeTax;//税后总奖金
    private Double prizeSubjoinTax;//官方-税后加奖奖金
    private Double prizeSubjoinSiteTax;//网站加奖-税后加奖奖金
    private String prizeDetail;//中奖明细
    private String prizeBarrier;//奖级
    private String drawNumber;//开奖号码
    private String period;//期次编号
    private Integer periodSum;//追期方案 总期次
    private Integer donePeriod;//已追期完成期次
    private Boolean isPrizeStop;//追期方案中奖后是否停止（0-否 1-是）
    private Date endTime;//方案截止时间
    private Boolean isShare;//是否可分享神单（0-不可分享 1-可分享）
    private Date shareTime;//分享时间
    private Date shareEndTime;//分享截止时间(此后将不能分享）
    private Date showTime;//公开时间
    private Integer hideType;//投注内容隐藏模式(0-不隐藏 1-只隐藏投注项 2-隐藏对阵和投注项)
    private Double minParticipant;//最小跟投金额
    private Integer remuneration;//提成比例
    private String userNickName;//发单人昵称
    private Long copySchemeId;//被复制的方案id(针对跟单)
    private Double rewardPrize;//打赏金额(shemeType=4-收到的总打赏金额 shemeType=3-支出的打赏）
    private Double safeGuardMoney;//跟单总额
    private Double redSafeHuardMoney;//已跟单金额
    private String schemeSource;//方案来源（商户编号）
    private String merchartOrderId;//商户订单号
    private String theoryPrize;//理论奖金范围
    private Integer profitMargin;//理论盈利率
    private Date outTicketTime;//出票时间
    private Integer openStatus;//计奖状态（0-未计奖 1-未中奖 2-中奖）
    private Date openTime;//计奖时间
    private Integer prizeStatus;//0-未派奖 1-派奖中 2-已派奖
    private Date prizeTime;//派奖时间
    private Integer backStatus;//方案返现处理状态（-1-返现失败 0-不返现 1-未返现 2-已返现）
    private Integer bigOrderStatus;//大单审核状态[距离截止1小时内](1-不是大单 2-大单未审核 3-大单已审核)
    private Integer couponId;//优惠券编号(优惠券表的编号）
    private Double offsetWithDraw;//方案抵消可提现金额
    private Double offsetUnWithDraw;//方案抵消不可提现金额
    private Integer channelNotifyNumber;//渠道出票通知次数
    private String winPopup;//中奖弹窗(0-未弹 1-已弹)
    private Date createTime;//方案下单时间
    private Date updateTime;//方案更新时间
    private Date estimateDrawTime;//方案预计开奖时间

    public String getWinPopup() {
        return winPopup;
    }

    public void setWinPopup(String winPopup) {
        this.winPopup = winPopup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getClientSource() {
        return clientSource;
    }

    public void setClientSource(Integer clientSource) {
        this.clientSource = clientSource;
    }

    public String getClientSourceName() {
        return clientSourceName;
    }

    public void setClientSourceName(String clientSourceName) {
        this.clientSourceName = clientSourceName;
    }

    public Long getSchemeUserId() {
        return schemeUserId;
    }

    public void setSchemeUserId(Long schemeUserId) {
        this.schemeUserId = schemeUserId;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }

    public String getPlayTypeId() {
        return playTypeId;
    }

    public void setPlayTypeId(String playTypeId) {
        this.playTypeId = playTypeId;
    }

    public String getSchemeOrderId() {
        return schemeOrderId;
    }

    public void setSchemeOrderId(String schemeOrderId) {
        this.schemeOrderId = schemeOrderId;
    }

    public Integer getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(Integer schemeType) {
        this.schemeType = schemeType;
    }

    public Integer getSchemeMultiple() {
        return schemeMultiple;
    }

    public void setSchemeMultiple(Integer schemeMultiple) {
        this.schemeMultiple = schemeMultiple;
    }

    public Integer getSchemeZs() {
        return schemeZs;
    }

    public void setSchemeZs(Integer schemeZs) {
        this.schemeZs = schemeZs;
    }

    public Double getSchemeMoney() {
        return schemeMoney;
    }

    public void setSchemeMoney(Double schemeMoney) {
        this.schemeMoney = schemeMoney;
    }

    public Double getSchemePayMoney() {
        return schemePayMoney;
    }

    public void setSchemePayMoney(Double schemePayMoney) {
        this.schemePayMoney = schemePayMoney;
    }

    public Integer getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(Integer channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelDesc() {
        return channelDesc;
    }

    public void setChannelDesc(String channelDesc) {
        this.channelDesc = channelDesc;
    }

    public Integer getSchemeStatus() {
        return schemeStatus;
    }

    public void setSchemeStatus(Integer schemeStatus) {
        this.schemeStatus = schemeStatus;
    }

    public String getSchemeStatusDesc() {
        return schemeStatusDesc;
    }

    public void setSchemeStatusDesc(String schemeStatusDesc) {
        this.schemeStatusDesc = schemeStatusDesc;
    }

    public String getSchemePlayType() {
        return schemePlayType;
    }

    public void setSchemePlayType(String schemePlayType) {
        this.schemePlayType = schemePlayType;
    }

    public String getSchemeContent() {
        return schemeContent;
    }

    public void setSchemeContent(String schemeContent) {
        this.schemeContent = schemeContent;
    }

    public String getSchemeSpContent() {
        return schemeSpContent;
    }

    public void setSchemeSpContent(String schemeSpContent) {
        this.schemeSpContent = schemeSpContent;
    }

    public String getSchemeYhContent() {
        return schemeYhContent;
    }

    public void setSchemeYhContent(String schemeYhContent) {
        this.schemeYhContent = schemeYhContent;
    }

    public Double getPrize() {
        return prize;
    }

    public void setPrize(Double prize) {
        this.prize = prize;
    }

    public Double getPrizeSubjoin() {
        return prizeSubjoin;
    }

    public void setPrizeSubjoin(Double prizeSubjoin) {
        this.prizeSubjoin = prizeSubjoin;
    }

    public Double getPrizeSubjoinSite() {
        return prizeSubjoinSite;
    }

    public void setPrizeSubjoinSite(Double prizeSubjoinSite) {
        this.prizeSubjoinSite = prizeSubjoinSite;
    }

    public Double getPrizeTax() {
        return prizeTax;
    }

    public void setPrizeTax(Double prizeTax) {
        this.prizeTax = prizeTax;
    }

    public Double getPrizeSubjoinTax() {
        return prizeSubjoinTax;
    }

    public void setPrizeSubjoinTax(Double prizeSubjoinTax) {
        this.prizeSubjoinTax = prizeSubjoinTax;
    }

    public Double getPrizeSubjoinSiteTax() {
        return prizeSubjoinSiteTax;
    }

    public void setPrizeSubjoinSiteTax(Double prizeSubjoinSiteTax) {
        this.prizeSubjoinSiteTax = prizeSubjoinSiteTax;
    }

    public String getPrizeDetail() {
        return prizeDetail;
    }

    public void setPrizeDetail(String prizeDetail) {
        this.prizeDetail = prizeDetail;
    }

    public String getPrizeBarrier() {
        return prizeBarrier;
    }

    public void setPrizeBarrier(String prizeBarrier) {
        this.prizeBarrier = prizeBarrier;
    }

    public String getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getPeriodSum() {
        return periodSum;
    }

    public void setPeriodSum(Integer periodSum) {
        this.periodSum = periodSum;
    }

    public Integer getDonePeriod() {
        return donePeriod;
    }

    public void setDonePeriod(Integer donePeriod) {
        this.donePeriod = donePeriod;
    }

    public Boolean getPrizeStop() {
        return isPrizeStop;
    }

    public void setPrizeStop(Boolean prizeStop) {
        isPrizeStop = prizeStop;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Boolean getShare() {
        return isShare;
    }

    public void setShare(Boolean share) {
        isShare = share;
    }

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public Date getShareEndTime() {
        return shareEndTime;
    }

    public void setShareEndTime(Date shareEndTime) {
        this.shareEndTime = shareEndTime;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

    public Integer getHideType() {
        return hideType;
    }

    public void setHideType(Integer hideType) {
        this.hideType = hideType;
    }

    public Double getMinParticipant() {
        return minParticipant;
    }

    public void setMinParticipant(Double minParticipant) {
        this.minParticipant = minParticipant;
    }

    public Integer getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(Integer remuneration) {
        this.remuneration = remuneration;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public Long getCopySchemeId() {
        return copySchemeId;
    }

    public void setCopySchemeId(Long copySchemeId) {
        this.copySchemeId = copySchemeId;
    }

    public Double getRewardPrize() {
        return rewardPrize;
    }

    public void setRewardPrize(Double rewardPrize) {
        this.rewardPrize = rewardPrize;
    }

    public Double getSafeGuardMoney() {
        return safeGuardMoney;
    }

    public void setSafeGuardMoney(Double safeGuardMoney) {
        this.safeGuardMoney = safeGuardMoney;
    }

    public Double getRedSafeHuardMoney() {
        return redSafeHuardMoney;
    }

    public void setRedSafeHuardMoney(Double redSafeHuardMoney) {
        this.redSafeHuardMoney = redSafeHuardMoney;
    }

    public String getSchemeSource() {
        return schemeSource;
    }

    public void setSchemeSource(String schemeSource) {
        this.schemeSource = schemeSource;
    }

    public String getMerchartOrderId() {
        return merchartOrderId;
    }

    public void setMerchartOrderId(String merchartOrderId) {
        this.merchartOrderId = merchartOrderId;
    }

    public String getTheoryPrize() {
        return theoryPrize;
    }

    public void setTheoryPrize(String theoryPrize) {
        this.theoryPrize = theoryPrize;
    }

    public Integer getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(Integer profitMargin) {
        this.profitMargin = profitMargin;
    }

    public Date getOutTicketTime() {
        return outTicketTime;
    }

    public void setOutTicketTime(Date outTicketTime) {
        this.outTicketTime = outTicketTime;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Integer getPrizeStatus() {
        return prizeStatus;
    }

    public void setPrizeStatus(Integer prizeStatus) {
        this.prizeStatus = prizeStatus;
    }

    public Date getPrizeTime() {
        return prizeTime;
    }

    public void setPrizeTime(Date prizeTime) {
        this.prizeTime = prizeTime;
    }

    public Integer getBackStatus() {
        return backStatus;
    }

    public void setBackStatus(Integer backStatus) {
        this.backStatus = backStatus;
    }

    public Integer getBigOrderStatus() {
        return bigOrderStatus;
    }

    public void setBigOrderStatus(Integer bigOrderStatus) {
        this.bigOrderStatus = bigOrderStatus;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Double getOffsetWithDraw() {
        return offsetWithDraw;
    }

    public void setOffsetWithDraw(Double offsetWithDraw) {
        this.offsetWithDraw = offsetWithDraw;
    }

    public Double getOffsetUnWithDraw() {
        return offsetUnWithDraw;
    }

    public void setOffsetUnWithDraw(Double offsetUnWithDraw) {
        this.offsetUnWithDraw = offsetUnWithDraw;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getChannelNotifyNumber() {
        return channelNotifyNumber;
    }

    public void setChannelNotifyNumber(Integer channelNotifyNumber) {
        this.channelNotifyNumber = channelNotifyNumber;
    }

    public Date getEstimateDrawTime() {
        return estimateDrawTime;
    }

    public void setEstimateDrawTime(Date estimateDrawTime) {
        this.estimateDrawTime = estimateDrawTime;
    }
}