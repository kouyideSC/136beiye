package com.caipiao.domain.base;

import com.caipiao.domain.jjyh.MatchInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 方案业务处理对象
 * @author  mcdog
 */
public class SchemeBean extends BaseBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long sid;//方案id(主要用在追号方案或查询用)
    private Integer clientSource;//客户端来源
    private String clientSourceName;//客户端来源名称 0-web 1-ios 2-android 3-h5 4-其它
    private String lid;//彩种编号
    private String lname;//彩种名称
    /**
     * 玩法类型id
     * 1700-竞彩足球混投 1710-竞彩篮球混投 1720-竞彩足球胜平负 1900-竞彩足球让球胜平负
     * 1910-竞彩足球猜比分 1920-竞彩足球半全场 1930-竞彩足球进球数 1940-竞彩篮球胜负
     * 1950-竞彩篮球让分胜负 1960-竞彩篮球胜分差 1970-竞彩篮球大小分
     */
    private String playTypeId;
    private String scode;//订单编号
    private Integer stype;//0-普通方案 1-追号方案 2-优化方案 3-跟单方案 4-神单方案
    private Integer smultiple;//方案倍数
    private Integer szs;//方案注数
    private Double money;//方案金额
    private Double ymoney;//应付金额
    private Double smoney;//支付金额(实际支付）
    private Integer channelCode;//支付渠道编号(300-余额支付[购彩] 301-优惠券支付)
    private String channelDesc;//支付渠道描述
    private Integer status;//-1-无效 0-待支付 1-支付成功（预约中） 2-出票中 3-预约成功 4-预约失败
    private String sdesc;//状态描述
    private String wtype;//玩法类型
    private String tzcontent;//投注内容
    private String tzspcontent;//投注内容赔率
    private Double prize;//税前总奖金
    private Double sprize;//税后总奖金
    private String prizeDetail;//中奖明细
    private String prizeBarrier;//奖级
    private String kcode;//开奖号码
    private String period;//期次编号
    private Integer pSum;//追期方案 总期次数
    private Integer ywcSum;//已追期完成期次数
    private Integer prizeStop;//追期方案中奖后是否停止（0-否 1-是）
    private Date etime;//方案截止时间
    private Integer isShare;//是否可分享神单（0-不可分享 1-可分享）
    private Date ftime;//分享时间
    private Date fetime;//分享截止时间(此后将不能分享）
    private Date gtime;//公开时间
    private Integer hideType;//投注内容隐藏模式(0-不隐藏 1-只隐藏投注项 2-隐藏对阵和投注项)
    private Double zxgtMoney;//最小跟投金额
    private Integer tcbl;//提成比例
    private String unick;//发单人昵称
    private Long copySchemeId;//被复制的方案id(针对跟单)
    private Double rprize;//打赏金额(shemeType=4-收到的总打赏金额 shemeType=3-支出的打赏）
    private Double gdzmoney;//跟单总额
    private Double ygdmoney;//已跟单金额
    private String source;//方案来源（商户编号）
    private String morderId;//商户订单号
    private String lprize;//理论奖金范围
    private Integer profitMargin;//理论盈利率
    private Integer jstatus;//计奖状态（0-未计奖 1-已计奖）
    private Date jtime;//计奖时间
    private Integer pstatus;//0-未派奖 1-派奖中 2-已派奖
    private Date ptime;//派奖时间
    private Integer bigOrderStatus;//大单审核状态[距离截止1小时内](1-不是大单 2-大单未审核 3-大单已审核)
    private Integer bstatus;//方案返现处理状态（-1-返现失败 0-不返现 1-未返现 2-已返现）
    private Integer cid;//优惠券编号(优惠券表的编号）
    private Double offsetWithDraw;//方案抵消可提现金额
    private Double offsetUnWithDraw;//方案抵消不可提现金额
    private Date ctime;//方案下单时间
    private Date utime;//方案更新时间
    private List<Object> matchList;//场次竞彩编号集合
    private Map<String,Map<String,String>> pankouMap;//场次盘口集合(以场次竞彩编号为key,单个盘口对象:竞彩足球包括让球(lose),竞彩篮球包括让分(lose),大小分(dxf))
    private Map<String, MatchInfo> matchInfoList;//投注串包含的场次信息
    private String zhinfos;//智能追号信息
    private String yhinfos;//优化方案信息
    private Date yjkjTime;//预计开奖时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
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

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPlayTypeId() {
        return playTypeId;
    }

    public void setPlayTypeId(String playTypeId) {
        this.playTypeId = playTypeId;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public Integer getStype() {
        return stype;
    }

    public void setStype(Integer stype) {
        this.stype = stype;
    }

    public Integer getSmultiple() {
        return smultiple;
    }

    public void setSmultiple(Integer smultiple) {
        this.smultiple = smultiple;
    }

    public Integer getSzs() {
        return szs;
    }

    public void setSzs(Integer szs) {
        this.szs = szs;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getYmoney() {
        return ymoney;
    }

    public void setYmoney(Double ymoney) {
        this.ymoney = ymoney;
    }

    public Double getSmoney() {
        return smoney;
    }

    public void setSmoney(Double smoney) {
        this.smoney = smoney;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSdesc() {
        return sdesc;
    }

    public void setSdesc(String sdesc) {
        this.sdesc = sdesc;
    }

    public String getWtype() {
        return wtype;
    }

    public void setWtype(String wtype) {
        this.wtype = wtype;
    }

    public String getTzcontent() {
        return tzcontent;
    }

    public void setTzcontent(String tzcontent) {
        this.tzcontent = tzcontent;
    }

    public String getTzspcontent() {
        return tzspcontent;
    }

    public void setTzspcontent(String tzspcontent) {
        this.tzspcontent = tzspcontent;
    }

    public Double getPrize() {
        return prize;
    }

    public void setPrize(Double prize) {
        this.prize = prize;
    }

    public Double getSprize() {
        return sprize;
    }

    public void setSprize(Double sprize) {
        this.sprize = sprize;
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

    public String getKcode() {
        return kcode;
    }

    public void setKcode(String kcode) {
        this.kcode = kcode;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getpSum() {
        return pSum;
    }

    public void setpSum(Integer pSum) {
        this.pSum = pSum;
    }

    public Integer getYwcSum() {
        return ywcSum;
    }

    public void setYwcSum(Integer ywcSum) {
        this.ywcSum = ywcSum;
    }

    public Integer getPrizeStop() {
        return prizeStop;
    }

    public void setPrizeStop(Integer prizeStop) {
        this.prizeStop = prizeStop;
    }

    public Date getEtime() {
        return etime;
    }

    public void setEtime(Date etime) {
        this.etime = etime;
    }

    public Integer getIsShare() {
        return isShare;
    }

    public void setIsShare(Integer isShare) {
        this.isShare = isShare;
    }

    public Date getFtime() {
        return ftime;
    }

    public void setFtime(Date ftime) {
        this.ftime = ftime;
    }

    public Date getFetime() {
        return fetime;
    }

    public void setFetime(Date fetime) {
        this.fetime = fetime;
    }

    public Date getGtime() {
        return gtime;
    }

    public void setGtime(Date gtime) {
        this.gtime = gtime;
    }

    public Integer getHideType() {
        return hideType;
    }

    public void setHideType(Integer hideType) {
        this.hideType = hideType;
    }

    public Double getZxgtMoney() {
        return zxgtMoney;
    }

    public void setZxgtMoney(Double zxgtMoney) {
        this.zxgtMoney = zxgtMoney;
    }

    public Integer getTcbl() {
        return tcbl;
    }

    public void setTcbl(Integer tcbl) {
        this.tcbl = tcbl;
    }

    public String getUnick() {
        return unick;
    }

    public void setUnick(String unick) {
        this.unick = unick;
    }

    public Long getCopySchemeId() {
        return copySchemeId;
    }

    public void setCopySchemeId(Long copySchemeId) {
        this.copySchemeId = copySchemeId;
    }

    public Double getRprize() {
        return rprize;
    }

    public void setRprize(Double rprize) {
        this.rprize = rprize;
    }

    public Double getGdzmoney() {
        return gdzmoney;
    }

    public void setGdzmoney(Double gdzmoney) {
        this.gdzmoney = gdzmoney;
    }

    public Double getYgdmoney() {
        return ygdmoney;
    }

    public void setYgdmoney(Double ygdmoney) {
        this.ygdmoney = ygdmoney;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMorderId() {
        return morderId;
    }

    public void setMorderId(String morderId) {
        this.morderId = morderId;
    }

    public String getLprize() {
        return lprize;
    }

    public void setLprize(String lprize) {
        this.lprize = lprize;
    }

    public Integer getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(Integer profitMargin) {
        this.profitMargin = profitMargin;
    }

    public Integer getJstatus() {
        return jstatus;
    }

    public void setJstatus(Integer jstatus) {
        this.jstatus = jstatus;
    }

    public Date getJtime() {
        return jtime;
    }

    public void setJtime(Date jtime) {
        this.jtime = jtime;
    }

    public Integer getPstatus() {
        return pstatus;
    }

    public void setPstatus(Integer pstatus) {
        this.pstatus = pstatus;
    }

    public Date getPtime() {
        return ptime;
    }

    public void setPtime(Date ptime) {
        this.ptime = ptime;
    }

    public Integer getBigOrderStatus() {
        return bigOrderStatus;
    }

    public void setBigOrderStatus(Integer bigOrderStatus) {
        this.bigOrderStatus = bigOrderStatus;
    }

    public Integer getBstatus() {
        return bstatus;
    }

    public void setBstatus(Integer bstatus) {
        this.bstatus = bstatus;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
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

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    public List<Object> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<Object> matchList) {
        this.matchList = matchList;
    }

    public Map<String, Map<String, String>> getPankouMap() {
        return pankouMap;
    }

    public void setPankouMap(Map<String, Map<String, String>> pankouMap) {
        this.pankouMap = pankouMap;
    }
    public Map<String, MatchInfo> getMatchInfoList() {
        return matchInfoList;
    }

    public void setMatchInfoList(Map<String, MatchInfo> matchInfoList) {
        this.matchInfoList = matchInfoList;
    }
    public String getZhinfos() {
        return zhinfos;
    }

    public void setZhinfos(String zhinfos) {
        this.zhinfos = zhinfos;
    }

    public String getYhinfos() {
        return yhinfos;
    }

    public void setYhinfos(String yhinfos) {
        this.yhinfos = yhinfos;
    }

    public Date getYjkjTime() {
        return yjkjTime;
    }

    public void setYjkjTime(Date yjkjTime) {
        this.yjkjTime = yjkjTime;
    }
}