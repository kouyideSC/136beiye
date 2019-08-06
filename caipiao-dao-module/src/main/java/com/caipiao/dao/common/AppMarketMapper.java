package com.caipiao.dao.common;

import com.caipiao.domain.common.AppMarket;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

/**
 * 市场模块功能接口定义
 * @author kouyi 2017-11-03
 */
public interface AppMarketMapper {

    /**
     * 发布市场版本
     * @param record
     * @return
     */
    int insertMarketVersion(AppMarket record);

    /**
     * 删除市场版本
     * @param id
     * @return
     */
    int deleteMarketVersion(Integer id);

    /**
     * 查询市场版本列表
     * @param params
     * @return
     */
    List<AppMarket> queryMarketVersionList(Dto params);

    /**
     * 查询市场列表
     * @return
     */
    List<Dto> queryMarketList();

    /**
     * 更新市场版本
     * @param params
     * @return
     */
    int updateMarketVersion(Dto params);
}