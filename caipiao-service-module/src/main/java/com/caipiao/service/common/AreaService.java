package com.caipiao.service.common;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.user.UserUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.AreaMapper;
import com.caipiao.dao.common.MessageCodeMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.Bank;
import com.caipiao.domain.common.City;
import com.caipiao.domain.common.MessageCode;
import com.caipiao.domain.common.Province;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.User;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 地区(省市)服务类
 * @author  mcdog
 */
@Service("areaService")
public class AreaService
{
    private static Logger logger = LoggerFactory.getLogger(AreaService.class);

    @Autowired
    private AreaMapper areaMapper;

    /**
     * 获取省份
     * @author  mcdog
     * @param   params  查询参数对象
     * @param   result  处理结果对象
     */
    public void getProvinces(Dto params,ResultBean result) throws ServiceException,Exception
    {
        List<Province> dataList = areaMapper.queryProvinces(params);//查询省份信息
        List<Dto> provinceList = new ArrayList<Dto>();
        if(dataList != null && dataList.size() > 0)
        {
            //重新封装省份信息
            Dto provinceDto = null;
            for(Province province : dataList)
            {
                provinceDto = new BaseDto();
                provinceDto.put("pcode",province.getProvinceCode());//设置省份编号
                provinceDto.put("name",province.getProvinceName());//设置省份名称
                provinceList.add(provinceDto);
            }
        }
        result.setErrorCode(ErrorCode_API.SUCCESS);
        result.setData(provinceList);
    }

    /**
     * 获取城市
     * @author  mcdog
     * @param   params  查询参数对象
     * @param   result  处理结果对象
     *
     */
    public void getCitys(Dto params,ResultBean result) throws ServiceException,Exception
    {
        /**
         * 校验参数
         */
        if(StringUtil.isEmpty(params.get("pcode")))
        {
            logger.error("[获取城市]参数校验不通过!确实必要参数,接收原始参数:" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        List<City> dataList = areaMapper.queryCitys(params);//查询城市信息
        List<Dto> cityList = new ArrayList<Dto>();
        if(dataList != null && dataList.size() > 0)
        {
            //重新封装省份信息
            Dto cityDto = null;
            for(City city : dataList)
            {
                cityDto = new BaseDto();
                cityDto.put("pcode",city.getProvinceCode());//设置城市所属省份编号
                cityDto.put("acode",city.getCityCode());//设置城市编号
                cityDto.put("name",city.getCityName());//设置城市名称
                cityList.add(cityDto);
            }
        }
        result.setErrorCode(ErrorCode_API.SUCCESS);
        result.setData(cityList);
    }
}