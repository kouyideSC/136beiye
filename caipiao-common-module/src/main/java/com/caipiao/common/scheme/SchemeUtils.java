package com.caipiao.common.scheme;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方案工具类
 * @author  mcdog
 */
public class SchemeUtils
{
    private static final Logger logger = LoggerFactory.getLogger(SchemeUtils.class);

    /**
     * 获取方案类型列表
     * @author  mcdog
     * @return  List<Map>   方案类型集合(单个对象为Map,包含:type-类型 description-类型描述)
     */
    public static List<Map<String,String>> getSchemeTypes()
    {
        List<Map<String,String>> dataList = new ArrayList<Map<String, String>>();
        for(Map.Entry<Integer,String> entry : SchemeConstants.schemeTypesMap.entrySet())
        {
            Map<String,String> matchStateMap = new HashMap<String, String>();
            matchStateMap.put("value",entry.getKey() + "");
            matchStateMap.put("desc",entry.getValue());
            dataList.add(matchStateMap);
        }
        return dataList;
    }

    /**
     * 获取方案状态列表
     * @author  mcdog
     * @return  List<Map>   方案状态集合(单个对象为Map,包含:status-状态 description-状态描述)
     */
    public static List<Map<String,String>> getSchemeStatus()
    {
        List<Map<String,String>> dataList = new ArrayList<Map<String, String>>();
        for(Map.Entry<Integer,String> entry : SchemeConstants.schemeStatusMap.entrySet())
        {
            Map<String,String> matchStateMap = new HashMap<String, String>();
            matchStateMap.put("value",entry.getKey() + "");
            matchStateMap.put("desc",entry.getValue());
            dataList.add(matchStateMap);
        }
        return dataList;
    }

    /**
     * 获取客户端来源列表
     * @author  mcdog
     * @return  List<Map>   方案状态集合(单个对象为Map,包含:status-状态 description-状态描述)
     */
    public static List<Map<String,String>> getClientSources()
    {
        List<Map<String,String>> dataList = new ArrayList<Map<String, String>>();
        for(Map.Entry<Integer,String> entry : UserConstants.userSourceMap.entrySet())
        {
            Map<String,String> matchStateMap = new HashMap<String, String>();
            matchStateMap.put("value",entry.getKey() + "");
            matchStateMap.put("desc",entry.getValue());
            dataList.add(matchStateMap);
        }
        return dataList;
    }

    /**
     * 获取方案对阵Map(以竞彩场次号为key)
     * @author  mcdog
     * @param   schemeMatchList     方案对阵List
     */
    public static Map<String,Dto> getSchemeMatchMaps(List<Dto> schemeMatchList)
    {
        Map<String,Dto> schemeMatchMaps = new HashMap<String,Dto>();
        if(schemeMatchList != null && schemeMatchList.size() > 0)
        {
            for(Dto schemeMatch : schemeMatchList)
            {
                schemeMatchMaps.put(schemeMatch.getAsString("matchCode"),schemeMatch);
            }
        }
        return schemeMatchMaps;
    }
}
