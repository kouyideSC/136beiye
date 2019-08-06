package com.caipiao.common.util;

import com.caipiao.domain.cpadmin.Dto;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.net.URLEncoder;
import java.util.*;

/**
 * 排序工具类
 * @author  mcdog
 */
public class SortUtils
{
    /**
     * 根据Map参数对象获取按参数名ASCII码从小到大排序的字符串(键值对形式的字符串(key=value),多个键值对用&连接)
     * @author  mcdog
     * @param   paraMap     原始参数对象
     * @param   urlEncode   是否需要进行URL编码(true-需要 false-不需要,默认不编码)
     */
    public static String getOrderByAsciiAscFromMap(Map<String,String> paraMap,boolean urlEncode)
    {
        StringBuilder keyValueBuilder = new StringBuilder();
        if(paraMap != null && paraMap.size() > 0)
        {
            try
            {
                //对key进行ASCII码从小到大排序(字典序)
                Map<String, String> tmpMap = paraMap;
                List<Map.Entry<String, String>> keyList = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());//获取key集合
                Collections.sort(keyList,new Comparator<Map.Entry<String,String>>()
                {
                    @Override
                    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)
                    {
                        return (o1.getKey()).toString().compareTo(o2.getKey());
                    }
                });
                // 构造键值对的格式
                for(Map.Entry<String, String> entry : keyList)
                {
                    if(StringUtils.isNotBlank(entry.getKey()))
                    {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if(urlEncode)
                        {
                            value = URLEncoder.encode(value,"utf-8");
                        }
                        keyValueBuilder.append("&" + key + "=" + value);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                keyValueBuilder = new StringBuilder();
            }
        }
        return  keyValueBuilder.length() > 0? keyValueBuilder.toString().substring(1) : "";
    }

    /**
     * 根据Map参数对象获取按参数名ASCII码从小到大排序的字符串(键值对形式的字符串(key=value),多个键值对用&连接)
     * @author  mcdog
     * @param   paraMap     原始参数对象
     * @param   urlEncode   是否需要进行URL编码(true-需要 false-不需要,默认不编码)
     */
    public static String getOrderByAsciiDescFromMap(Map<String,String> paraMap,boolean urlEncode)
    {
        StringBuilder keyValueBuilder = new StringBuilder();
        if(paraMap != null && paraMap.size() > 0)
        {
            try
            {
                //对key进行ASCII码从小到大排序(字典序)
                Map<String, String> tmpMap = paraMap;
                List<Map.Entry<String, String>> keyList = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());//获取key集合
                Collections.sort(keyList,new Comparator<Map.Entry<String,String>>()
                {
                    @Override
                    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)
                    {
                        return o2.getKey().compareTo((o1.getKey()).toString());
                    }
                });
                // 构造键值对的格式
                for(Map.Entry<String, String> entry : keyList)
                {
                    if(StringUtils.isNotBlank(entry.getKey()))
                    {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if(urlEncode)
                        {
                            value = URLEncoder.encode(value,"utf-8");
                        }
                        keyValueBuilder.append("&" + key + "=" + value);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                keyValueBuilder = new StringBuilder();
            }
        }
        return  keyValueBuilder.length() > 0? keyValueBuilder.toString().substring(1) : "";
    }

    /**
     * 根据Dto参数对象获取按参数名ASCII码升序的字符串(键值对形式的字符串(key=value),多个键值对用&连接)
     * @author  mcdog
     * @param   params      原始参数对象
     * @param   urlEncode   是否需要进行URL编码(true-需要 false-不需要,默认不编码)
     */
    public static String getOrderByAsciiAscFromDto(Dto params, boolean urlEncode)
    {
        StringBuilder keyValueBuilder = new StringBuilder();
        if(params != null && params.size() > 0)
        {
            try
            {
                //对key进行ASCII码从小到大排序(字典序)
                Dto tmpParams = params;
                List<Dto.Entry<String,Object>> keyList = new ArrayList<Dto.Entry<String,Object>>(tmpParams.entrySet());//获取key集合
                Collections.sort(keyList,new Comparator<Dto.Entry<String,Object>>()
                {
                    @Override
                    public int compare(Map.Entry<String,Object> o1, Map.Entry<String,Object> o2)
                    {
                        return (o1.getKey()).toString().compareTo(o2.getKey());
                    }
                });
                // 构造键值对的格式
                for(Map.Entry<String,Object> entry : keyList)
                {
                    if(StringUtil.isNotEmpty(entry.getKey()) && StringUtil.isNotEmpty(entry.getValue()))
                    {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        if(urlEncode)
                        {
                            value = URLEncoder.encode(value,"utf-8");
                        }
                        keyValueBuilder.append("&" + key + "=" + value);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                keyValueBuilder = new StringBuilder();
            }
        }
        return  keyValueBuilder.length() > 0? keyValueBuilder.toString().substring(1) : "";
    }

    /**
     * 根据Dto参数对象获取按参数名ASCII码升序的字符串(键值对形式的字符串(key=value),多个键值对用&连接)
     * @author  mcdog
     * @param   params      原始参数对象
     * @param   filterValue 过滤掉指定值的参数
     * @param   urlEncode   是否需要进行URL编码(true-需要 false-不需要,默认不编码)
     */
    public static String getOrderByAsciiAscFromDto(Dto params, String filterValue,boolean urlEncode)
    {
        StringBuilder keyValueBuilder = new StringBuilder();
        if(params != null && params.size() > 0)
        {
            try
            {
                //对key进行ASCII码从小到大排序(字典序)
                Dto tmpParams = params;
                List<Dto.Entry<String,Object>> keyList = new ArrayList<Dto.Entry<String,Object>>(tmpParams.entrySet());//获取key集合
                Collections.sort(keyList,new Comparator<Dto.Entry<String,Object>>()
                {
                    @Override
                    public int compare(Map.Entry<String,Object> o1, Map.Entry<String,Object> o2)
                    {
                        return (o1.getKey()).toString().compareTo(o2.getKey());
                    }
                });
                // 构造键值对的格式
                for(Map.Entry<String,Object> entry : keyList)
                {
                    if(StringUtil.isNotEmpty(entry.getKey())
                            && StringUtil.isNotEmpty(entry.getValue())
                            && !entry.getValue().equals(filterValue))
                    {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        if(urlEncode)
                        {
                            value = URLEncoder.encode(value,"utf-8");
                        }
                        keyValueBuilder.append("&" + key + "=" + value);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                keyValueBuilder = new StringBuilder();
            }
        }
        return  keyValueBuilder.length() > 0? keyValueBuilder.toString().substring(1) : "";
    }

    /**
     * 根据Dto参数对象获取按参数名ASCII码降序的字符串(键值对形式的字符串(key=value),多个键值对用&连接)
     * @author  mcdog
     * @param   params      原始参数对象
     * @param   urlEncode   是否需要进行URL编码(true-需要 false-不需要,默认不编码)
     */
    public static String getOrderByAsciiDescFromDto(Dto params, boolean urlEncode)
    {
        StringBuilder keyValueBuilder = new StringBuilder();
        if(params != null && params.size() > 0)
        {
            try
            {
                //对key进行ASCII码从小到大排序(字典序)
                Dto tmpParams = params;
                List<Dto.Entry<String,Object>> keyList = new ArrayList<Dto.Entry<String,Object>>(tmpParams.entrySet());//获取key集合
                Collections.sort(keyList,new Comparator<Dto.Entry<String,Object>>()
                {
                    @Override
                    public int compare(Map.Entry<String,Object> o1, Map.Entry<String,Object> o2)
                    {
                        return o2.getKey().compareTo((o1.getKey()).toString());
                    }
                });
                // 构造键值对的格式
                for(Map.Entry<String,Object> entry : keyList)
                {
                    if(StringUtil.isNotEmpty(entry.getKey()) && StringUtil.isNotEmpty(entry.getValue()))
                    {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        if(urlEncode)
                        {
                            value = URLEncoder.encode(value,"utf-8");
                        }
                        keyValueBuilder.append("&" + key + "=" + value);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                keyValueBuilder = new StringBuilder();
            }
        }
        return  keyValueBuilder.length() > 0? keyValueBuilder.toString().substring(1) : "";
    }
}
