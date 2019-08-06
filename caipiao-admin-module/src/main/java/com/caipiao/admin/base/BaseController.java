package com.caipiao.admin.base;

import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

public class BaseController
{
    /**
     * 将数据源中值为null替换为""
     * @author  mcdog
     * @param   dataList    数据源
     */
    public static List<Dto> replaceNullToEmpty(List<Dto> dataList)
    {
        if(dataList != null && dataList.size() > 0)
        {
            for(Dto dto : dataList)
            {
                for(Dto.Entry<String,Object> entry : dto.entrySet())
                {
                    if(entry.getValue() == null)
                    {
                        entry.setValue("");
                    }
                }
            }
        }
        return dataList;
    }
}
