package com.caipiao.domain.match;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 冠亚军对阵对象
 * Created by kouyi on 2017/11/04.
 */
public class GyjMatch implements Serializable {
    private static final long serialVersionUID = 3829493996195256011L;
    private Long id;
    private String leagueName;//赛事名称
    private String lotteryId;//彩种编号
    private String period;//期次号
    private String matchCode;//场次号
    private String teamImg;//主队图片地址
    private Long teamId;//主队编号
    private String teamName;//主队名称
    private String guestTeamImg;//客队图片地址
    private Long guestTeamId;//客队编号
    private String guestTeamName;//客队名称
    private Double sp;//赔率SP
    private String probability;//概率
    private Integer status;//销售状态（-1:已取消 0-:已停售 1-:销售中 2-:已截止）
    private Boolean updateFlag;//手动更新标记(0-未手动更新 1-已手动更新)
    private Date createTime;//场次入库时间
    private Date updateTime;//场次入库时间

    public GyjMatch() {}
    public GyjMatch(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getGuestTeamId() {
        return guestTeamId;
    }

    public void setGuestTeamId(Long guestTeamId) {
        this.guestTeamId = guestTeamId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getTeamImg() {
        return teamImg;
    }

    public void setTeamImg(String teamImg) {
        this.teamImg = teamImg;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getGuestTeamImg() {
        return guestTeamImg;
    }

    public void setGuestTeamImg(String guestTeamImg) {
        this.guestTeamImg = guestTeamImg;
    }

    public String getGuestTeamName() {
        return guestTeamName;
    }

    public void setGuestTeamName(String guestTeamName) {
        this.guestTeamName = guestTeamName;
    }

    public Double getSp()
    {
        if(sp == null)
        {
            return sp;
        }
        return Double.valueOf(String.format("%.2f",sp));
    }

    public void setSp(Double sp) {
        this.sp = sp;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(Boolean updateFlag) {
        this.updateFlag = updateFlag;
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

    /**
     * 对阵重要属性字符串 用来判断对阵是否更新过
     * @return
     */
    public String getMatchInfo(){
        return matchCode+ "_" + teamImg + "_" + teamName + "_" + guestTeamImg + "_" + guestTeamName + "_" + sp
                + "_" + probability + "_" + status + "_" + teamId + "_" + guestTeamId;
    }
}