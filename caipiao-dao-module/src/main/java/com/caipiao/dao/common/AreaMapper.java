package com.caipiao.dao.common;

import com.caipiao.domain.common.City;
import com.caipiao.domain.common.Province;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.cpadmin.Dto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 区域(省市)数据库访问接口
 * @author  mcdog
 */
public interface AreaMapper
{
    /**
     * 查询省份
     * @author  mcdog
     */
    List<Province> queryProvinces(Dto params);
    /**
     * 查询城市
     * @author  mcdog
     */
    List<City> queryCitys(Dto params);
}