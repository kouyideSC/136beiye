package com.caipiao.dao.common;

import com.caipiao.domain.common.SensitiveWord;

import java.util.List;

/**
 * 敏感词模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface SensitiveWordMapper {

    /**
     * 新增敏感词
     * @param record
     * @return
     */
    int insertSensitiveWord(SensitiveWord record);

    /**
     * 查询敏感词列表
     * @param record
     * @return
     */
    List<SensitiveWord> querySensitiveWordList(SensitiveWord record);
}