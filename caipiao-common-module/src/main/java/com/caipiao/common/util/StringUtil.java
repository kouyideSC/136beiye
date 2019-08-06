package com.caipiao.common.util;


import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.UniqueTag;

import java.io.*;
import java.util.*;

/**
 * String工具类
 * Created by kouyi on 2017-09-22
 */
public class StringUtil {

	/**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static byte[] getBytes(String str){
    	if (str != null){
    		try {
				return str.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
    	}else{
    		return null;
    	}
    }
    
    /**
     * 字节数组转换为字符串
     * @param bytes
     * @return
     */
    public static String toString(byte[] bytes){
    	try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
    }


	public static boolean isEmpty(Object value) {
		return (value == null || "".equals(value));
	}
	
	public static boolean isEmpty(Object[] values) {
		return (values == null || values.length == 0);
	}

	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str.trim()));
	}
	
	public static boolean isEmpty(Collection<?> value) {
		return (value == null || value.size() == 0);
	}
	
	public static boolean isEmpty(Map<?,?> value) {
		return (value == null || value.size() == 0);
	}
	
	public static boolean isEmpty(String[] value) {
		return (value == null || value.length == 0);
	}

	public static boolean isNotEmpty(Object value) {
		return (!isEmpty(value));
	}

	public static boolean isNotEmpty(Collection<?> value) {
		return (!isEmpty(value));
	}

	public static boolean isNotEmpty(Map<?,?> value) {
		return (!isEmpty(value));
	}

	public static boolean isNotEmpty(String[] value) {
		return (!isEmpty(value));
	}

	public static boolean isNotEmpty(String value) {
		return (!isEmpty(value));
	}

	/**
	 * 获取UUID
	 * @return
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.toUpperCase().replace("-", "");
	}

	/**
	 * 对象转换为double-不四舍五入取两位小数
	 * @param obj
	 * @return
	 */
	public static double parseDoubleNoRound(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return 0.0;
			}
			return DoubleUtil.roundNoDouble(Double.parseDouble(obj.toString()), 2);
		}catch (Exception e) {
			return 0.0;
		}
	}

	/**
	 * 对象转换为double
	 * @param obj
	 * @return
	 */
	public static double parseDouble(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return 0.0;
			}
			return Double.parseDouble(obj.toString());
		}catch (Exception e) {
			return 0.0;
		}
	}

	/**
	 * 对象转换为Long
	 * @param obj
	 * @return
	 */
	public static Long parseLong(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return null;
			}
			return Long.parseLong(obj.toString());
		}catch (Exception e) {
			return 0l;
		}
	}

	/**
	 * 对象转换为Int
	 * @param obj
	 * @return
	 */
	public static Integer parseInt(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return null;
			}
			return Integer.parseInt(obj.toString());
		}catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 对象转换boolean
	 * @param obj
	 * @return
	 */
	public static Boolean parseBoolean(Object obj) {
		if (UniqueTag.NOT_FOUND == obj) {
			return null;
		}
		return Boolean.parseBoolean(obj.toString());
	}

	/**
	 * 将xml字符串转换为Map
	 * @author	sjq
	 * @param 	xmlStr	xml格式的字符串
	 */
	public static Map<String,Object> parseMapFromXmlStr(String xmlStr) throws Exception
	{
		Map<String,Object> map = new HashMap<String,Object>();
		if(StringUtil.isNotEmpty(xmlStr))
		{
			Document doc = null;
			doc = DocumentHelper.parseText(xmlStr);
			Element root = doc.getRootElement();
			List children = root.elements();
			if(children != null && children.size() > 0)
			{
				for(int i = 0; i < children.size(); i ++)
				{
					Element child = (Element)children.get(i);
					map.put(child.getName(),child.getTextTrim());
				}
			}
		}
		return map;
	}

	/**
	 * 将xml字符串转换为Dto
	 * @author	sjq
	 * @param 	xmlStr	xml格式的字符串
	 */
	public static Dto parseDtoFromXmlStr(String xmlStr) throws Exception
	{
		Dto dto = new BaseDto();
		if(StringUtil.isNotEmpty(xmlStr))
		{
			Document doc = DocumentHelper.parseText(xmlStr);
			Element root = doc.getRootElement();
			List children = root.elements();
			if(children != null && children.size() > 0)
			{
				for(int i = 0; i < children.size(); i ++)
				{
					Element child = (Element)children.get(i);
					dto.put(child.getName(),child.getTextTrim());
				}
			}
		}
		return dto;
	}

	//运行序列化和反序列化-进行深度拷贝
	public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		List<T> dest = (List<T>) in.readObject();
		return dest;
	}
}
