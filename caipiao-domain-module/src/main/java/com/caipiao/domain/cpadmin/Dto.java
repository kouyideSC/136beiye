package com.caipiao.domain.cpadmin;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface Dto extends Map<String,Object> 
{

    public Integer getAsInteger(String key);

    public Long getAsLong(String key);

    public String getAsString(String key);

    public BigDecimal getAsBigDecimal(String pStr);

    public Date getAsDate(String pStr);

    public Date getAsDate(String pStr, String dateFormat);

    public List getAsList(String key);

    public Float getAsFloat(String key);

    public Double getAsDouble(String key);

    public Double getAsDoubleValue(String key);

    public Timestamp getAsTimestamp(String key);

    public Boolean getAsBoolean(String key);

    public void setDefaultAList(List pList);

    public void setDefaultBList(List pList);

    public List getDefaultAList();

    public List getDefaultBList();

    public void setDefaultJson(String jsonString);

    public String getDefaultJson();

    public String toXml(String pStyle);

    public String toXml();

    public String toJson();

    public String toJson(String pFormat);

    public String toSeparatorString(String separator);

    public void setSuccess(Boolean pSuccess);

    public Boolean getSuccess();

    public void setMsg(String pMsg);

    public String getMsg();

}
