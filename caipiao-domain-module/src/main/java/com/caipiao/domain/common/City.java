package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 城市对象
 */
public class City implements Serializable
{
    private String cityCode;//城市编号
    private String cityName;//城市名称
    private String provinceCode;//所属省份编号
    private String remark;//说明(备注)
    private Date createTime;//创建时间
    private Date updateTime;//更新时间

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}