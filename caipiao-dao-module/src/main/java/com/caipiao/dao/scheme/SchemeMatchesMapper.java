package com.caipiao.dao.scheme;

import com.caipiao.domain.scheme.SchemeMatches;

/**
 * 方案对阵模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface SchemeMatchesMapper {

    /**
     * 插入方案对应的场次信息
     * @param record
     * @return
     */
    int insertSchemeMatches(SchemeMatches record);

}