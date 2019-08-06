package com.caipiao.common.json;

import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.springframework.util.StringUtils;

import java.util.*;

public class JsonUtil {

    /**
     * JSON时间属性转换
     * @param pattern
     * @return
     */
	public static JsonConfig formatDate(String pattern) {
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(pattern));
		return config;
	}

    /**
     * LIST转换为JSON字符串
     * @param list
     * @return
     */
	public static String JsonArray(List<?> list) {
		return JSONArray.fromObject(list, format()).toString();
	}

    /**
     * 对象转换为JSON字符串
     * @param obj
     * @return
     */
	public static String JsonObject(Object obj) {
		return JSONObject.fromObject(obj, format()).toString();
	}

    /**
     * 对象转换为JSON字符串
     * @param map
     * @return
     */
    public static String JsonObject(Map map) {
        return JSONArray.fromObject(map, format()).toString();
    }

    /**
     * LIST转换为JSON字符串-并过滤掉对象属性(showColumns)
     * @param list
     * @param showColumns
     * @return
     */
	public static String JsonArray(List<?> list, Map<String, String> showColumns) {
		return JSONArray.fromObject(list, formatFilterProperty(showColumns)).toString();
	}

    /**
     * LIST转换为JSON字符串-并过滤掉对象属性(showColumns)
     * @param list
     * @param showColumns
     * @return
     */
    public static String JsonArray(List<?> list, List<String> showColumns) {
        return JSONArray.fromObject(list, formatFilterProperty(showColumns)).toString();
    }

    /**
     * 对象转换为JSON字符串-并过滤掉对象属性(showColumns)
     * @param obj
     * @param showColumns
     * @return
     */
	public static String JsonObject(Object obj, Map<String, String> showColumns) {
		return JSONObject.fromObject(obj, formatFilterProperty(showColumns)).toString();
	}

    /**
     * 对象转换为JSON字符串-并过滤掉对象属性(showColumns)
     * @param obj
     * @param showColumns
     * @return
     */
    public static String JsonObject(Object obj, List<String> showColumns) {
        return JSONObject.fromObject(obj, formatFilterProperty(showColumns)).toString();
    }

	/** 
     * JSON字符串转换成Object对象
     *  
     * @param jsonString 
     *            需要转换的字符串 
     * @return 对象 
     */  
    public static Object jsonToObject(String jsonString) {  
        JSONObject jsonObject = JSONObject.fromObject(jsonString);  
        return JSONObject.toBean(jsonObject);  
    }

    /**
     * JSON字符串转换为指定对象
     * @param jsonString
     * @param type
     * @param map
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String jsonString, Class<T> type, Map<String,T> map) {  
        JSONObject jsonObject = JSONObject.fromObject(jsonString); 
        return (T) JSONObject.toBean(jsonObject, type, map);
    } 
	
	/** 
     * JSON字符串转换成对象 
     * @param jsonString
     * @param type
     * @return 对象
     */  
    @SuppressWarnings("unchecked")  
    public static <T> T jsonToObject(String jsonString, Class<T> type) {  
        JSONObject jsonObject = JSONObject.fromObject(jsonString); 
        return (T) JSONObject.toBean(jsonObject, type);  
    }  
    
    /***
     * 将JSON对象数组转换为传入类型的List
     * @param 
     * @param jsonArray
     * @param objectClass
     * @return
     */
    public static <T> List<T> toList(JSONArray jsonArray, Class<T> objectClass)
    {
        return JSONArray.toList(jsonArray, objectClass);
    }
    
    /***
     * 将对象转换为传入类型的List
     * @param 
     * @param object
     * @param objectClass
     * @return
     */
    public static <T> List<T> toList(Object object, Class<T> objectClass)
    {
        JSONArray jsonArray = JSONArray.fromObject(object);
        return JSONArray.toList(jsonArray, objectClass);
    }

    /***
     * 将JSON对象转换为传入类型的对象
     * @param 
     * @param jsonObject
     * @param beanClass
     * @return
     */
    public static <T> T toBean(JSONObject jsonObject, Class<T> beanClass)
    {
        return (T) JSONObject.toBean(jsonObject, beanClass);
    }

    /***
     * 将将对象转换为传入类型的对象
     * @param 
     * @param object
     * @param beanClass
     * @return
     */
    public static <T> T toBean(Object object, Class<T> beanClass)
    {
        JSONObject jsonObject = JSONObject.fromObject(object);
        return (T) JSONObject.toBean(jsonObject, beanClass);
    }
  
    /** 
     * 将JSONArray对象转换成list集合 
     *  
     * @param jsonArr 
     * @return 
     */  
    public static List<Object> jsonToList(JSONArray jsonArr) {  
        List<Object> list = new ArrayList<Object>();  
        for (Object obj : jsonArr) {  
            if (obj instanceof JSONArray) {  
                list.add(jsonToList((JSONArray) obj));  
            } else if (obj instanceof JSONObject) {  
                list.add(jsonToMap((JSONObject) obj));  
            } else {  
                list.add(obj);  
            }  
        }  
        return list;  
    }  
  
    /** 
     * 将json字符串转换成map对象 
     *  
     * @param json 
     * @return 
     */  
    public static Map<String, Object> jsonToMap(String json) {  
        JSONObject obj = JSONObject.fromObject(json);  
        return jsonToMap(obj);  
    }

    /**
     * 将json字符串转换成Dto对象
     * @param   json    待转换的json字符串
     */
    public static Dto jsonToDto(String json)
    {
        JSONObject obj = JSONObject.fromObject(json);
        return jsonToDto(obj);
    }
  
    /** 
     * 将JSONObject转换成map对象 
     *  
     * @param obj
     * @return 
     */  
    public static Map<String, Object> jsonToMap(JSONObject obj) {  
        Set<?> set = obj.keySet();  
        Map<String, Object> map = new LinkedHashMap<String, Object>(set.size());
        for (Object key : obj.keySet()) {  
            Object value = obj.get(key);  
            if (value instanceof JSONArray) {  
                map.put(key.toString(), jsonToList((JSONArray) value));  
            } else if (value instanceof JSONObject) {  
                map.put(key.toString(), jsonToMap((JSONObject) value));  
            } else {  
                map.put(key.toString(), obj.get(key));  
            }  
  
        }  
        return map;  
    }

    /**
     * 将JSONObject转换成Dto对象
     * @param   obj     待转换的对象
     */
    public static Dto jsonToDto(JSONObject obj)
    {
        Set<?> set = obj.keySet();
        Dto dto = new BaseDto();
        for (Object key : obj.keySet())
        {
            Object value = obj.get(key);
            if (value instanceof JSONArray)
            {
                dto.put(key.toString(),jsonToList((JSONArray) value));
            }
            else if (value instanceof JSONObject)
            {
                dto.put(key.toString(),jsonToDto((JSONObject) value));
            }
            else
            {
                dto.put(key.toString(),obj.get(key));
            }
        }
        return dto;
    }

    /**
	 * json时间类型转换
	 * 
	 * @return
	 */
	public static JsonConfig format() {
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
		return config;
	}

	/**
	 * json时间类型转换并过滤属性
	 * 
	 * @return
	 */
	public static JsonConfig formatFilterProperty(final Map<String, String> showColumns) {
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
		config.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object source, String name, Object value) {
				if (!StringUtils.isEmpty(showColumns) && showColumns.containsKey(name)) {
					return true;
				} else {
					return false;
				}
			}
		});
		return config;
	}

    /**
     * json时间类型转换并过滤属性
     *
     * @return
     */
    public static JsonConfig formatFilterProperty(final List showColumns) {
        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
        config.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                if (!StringUtils.isEmpty(showColumns) && showColumns.contains(name)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        return config;
    }

    /**
     * json时间类型转换并只显示给出的属性
     *
     * @return
     */
    public static JsonConfig formatShowProperty(final Map<String, String> showColumns) {
        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
        config.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                if (!StringUtils.isEmpty(showColumns) && showColumns.containsKey(name)) {
                    return false;
                } else {
                    return true;
                }
            }
        });
        return config;
    }

}
