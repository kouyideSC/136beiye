package com.caipiao.domain.common;

import java.io.Serializable;
import java.util.Date;

/**
 * 省份对象
 */
public class Province implements Serializable
{
    private String provinceCode;//省份编号
    private String provinceName;//省份名称
    private String shortName;//省份简称
    private String remark;//说明(备注)
    private String areaName;//所属区域(比如华东/华南)
    private Date createTime;//创建时间
    private Date updateTime;//修改时间

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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