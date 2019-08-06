package com.caipiao.dao.common;

import com.caipiao.domain.common.Parameter;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

/**
 * 系统参数模块功能接口定义
 * @author kouyi 2017-09-21
 */
public interface ParameterMapper {

    /**
     * 查询所有配置参数
     * @return
     */
    List<Parameter> queryParameterList();

    /**
     * 更新配置参数
     * @return
     */
    int updateParameter(Dto params);

    /**
     * 新增配置参数
     * @param params
     * @return
     */
    int saveParams(Dto params);

    /**
     * 删除配置参数
     * @param params
     * @return
     */
    int deleteParams(Dto params);

    /**
     * 根据条件查询参数配置
     * @author	sjq
     */
    BaseDto queryParameter(Dto params);

    /**
     * 根据条件查询参数配置信息
     * @author	sjq
     */
    List<BaseDto> queryParameters(Dto params);
}
