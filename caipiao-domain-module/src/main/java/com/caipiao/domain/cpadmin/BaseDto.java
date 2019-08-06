package com.caipiao.domain.cpadmin;

import sun.swing.StringUIClientPropertyKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;


public class BaseDto extends HashMap<String,Object> implements Dto, Serializable
{
    public BaseDto(){}

	public BaseDto(String key, Object value)
	{
		if(value != null)
		{
			//List/ArrayList/LinkedList类型
			if(value instanceof List || value instanceof ArrayList || value instanceof LinkedList)
			{
				List<Object> valueList = (List<Object>)value;
				List<Object> dataList = new ArrayList<Object>();
				for(Object object : valueList)
				{
					if(object != null && (object instanceof  Dto || object instanceof BaseDto))
					{
						//遍历Dto中的key,如果对应的值为null,则改为""
						Dto data = (Dto)object;
						for(Dto.Entry<String,Object> entry : data.entrySet())
						{
							if(entry.getValue() == null)
							{
								entry.setValue("");
							}
						}
						dataList.add(data);
					}
					else
					{
						dataList.add(object);
					}
				}
				value = dataList;
			}
		}
		put(key, value);
	}

	public BaseDto(Boolean success){
		setSuccess(success);
	}

	public BaseDto(Boolean success, String msg){
		setSuccess(success);
		setMsg(msg);
	}

	public BigDecimal getAsBigDecimal(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "BigDecimal", null);
		if (obj != null)
			return (BigDecimal) obj;
		else
			return null;
	}

	public Date getAsDate(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "Date", "yyyy-MM-dd");
		if (obj != null)
			return (Date) obj;
		else
			return null;
	}

    public Date getAsDate(String key, String dateFormat) {
        Object obj = TypeCaseHelper.convert(get(key), "Date", dateFormat);
        if (obj != null)
            return (Date) obj;
        else
            return null;
    }


    public Integer getAsInteger(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "Integer", null);
		if (obj != null)
			return (Integer) obj;
		else
			return null;
	}

	public Long getAsLong(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "Long", null);
		if (obj != null)
			return (Long) obj;
		else
			return null;
	}

    public Float getAsFloat(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "Float", null);
        if (obj != null)
            return (Float) obj;
        else
            return null;
    }

    public Double getAsDouble(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "Double", null);
        if (obj != null)
            return (Double) obj;
        else
            return null;
    }

	public Double getAsDoubleValue(String key)
	{
		Object obj = TypeCaseHelper.convert(get(key), "Double", null);
		if (obj != null)
			return (Double) obj;
		else
			return 0d;
	}

	public String getAsString(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "String", null);
		if (obj != null) {
            if (obj instanceof Date || obj instanceof Timestamp) {
                String time = String.valueOf(obj);
                if(time.length() > 18){
                    return time.substring(0, 19);
                }else{
                    return time.substring(0, 10);
                }
            }
            return (String) obj;
        }else {
            return "";
        }
	}

	public List getAsList(String key){
		return (List)get(key);
	}

	public Timestamp getAsTimestamp(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "Timestamp", "yyyy-MM-dd HH:mm:ss");
		if (obj != null)
			return (Timestamp) obj;
		else
			return null;
	}

	public Boolean getAsBoolean(String key){
		Object obj = TypeCaseHelper.convert(get(key), "Boolean", null);
		if (obj != null)
			return (Boolean) obj;
		else
			return null;
	}

	public void setDefaultAList(List pList) {
		put("defaultAList", pList);
	}

	public void setDefaultBList(List pList) {
		put("defaultBList", pList);
	}

	public List getDefaultAList() {
		return (List) get("defaultAList");
	}

	public List getDefaultBList() {
		return (List) get("defaultBList");
	}

	public void setDefaultJson(String jsonString){
    	put("defaultJsonString", jsonString);
    }

    public String getDefaultJson(){
    	return getAsString("defaultJsonString");
    }

	public String toXml(String pStyle) {
        System.out.println("XML 暂不支持");
		return "";
	}

	public String toXml()
	{
		StringBuilder xmlBuilder = new StringBuilder("<xml>");
		for(Map.Entry<String,Object> entry : this.entrySet())
		{
			xmlBuilder.append("<" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">");
		}
		xmlBuilder.append("</xml>");
        return xmlBuilder.toString();
	}

	public String toJson() {
		String strJson = null;
		strJson = JsonHelper.encodeObject2Json(this);
		return strJson;
	}

	public String toJson(String pFormat){
		String strJson = null;
		strJson = JsonHelper.encodeObject2Json(this, pFormat);
		return strJson;
	}

	public void setSuccess(Boolean pSuccess){
		put("success", pSuccess);
		if (pSuccess) {
			put("bflag", "1");
		}else {
			put("bflag", "0");
		}

	}

	/**
	 * 重写toString
	 * @author 	sjq
	 */
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for(Map.Entry<String,Object> entry : this.entrySet())
		{
			stringBuilder.append("," + entry.getKey() + "=" + entry.getValue());
		}
		return stringBuilder.length() > 0? stringBuilder.toString().substring(1) : stringBuilder.toString();
	}

	/**
	 * 将对象拼接为按指定分隔符连接的字符串
	 * @author 	sjq
	 */
	@Override
	public String toSeparatorString(String separator)
	{
		int length = separator == null? 0 : separator.length();
		StringBuilder stringBuilder = new StringBuilder();
		for(Map.Entry<String,Object> entry : this.entrySet())
		{
			stringBuilder.append(separator + entry.getKey() + "=" + entry.getValue());
		}
		return stringBuilder.length() > 0? stringBuilder.toString().substring(length) : stringBuilder.toString();
	}

	public Boolean getSuccess(){
		return getAsBoolean("success");
	}

	public void setMsg(String pMsg){
		put("msg", pMsg);
	}

	public String getMsg(){
		return getAsString("msg");
	}
}
