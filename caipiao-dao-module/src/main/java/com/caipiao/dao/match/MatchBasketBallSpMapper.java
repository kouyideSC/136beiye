package com.caipiao.dao.match;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.match.MatchBasketBallSp;

import java.util.List;

/**
 * 篮球对阵赔率模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface MatchBasketBallSpMapper {

    /**
     * 根据竞彩场次号-查询竞彩篮球赔率信息
     * @param matchCode
     * @return
     */
    MatchBasketBallSp queryMatchBasketBallSpByMatchCode(String matchCode);


    /**
     * 新增篮球对阵sp记录
     * @param record
     * @return
     */
    int insertMatchBasketBallSp(MatchBasketBallSp record);

    /**
     * 更新竞彩赔率数据
     * @param match
     * @return
     */
    int updateMatchBasketBallSp(MatchBasketBallSp match);

    /**
     * 根据竞彩场次号查询篮球赔率信息
     * @author  mcdog
     * @param   mathchCode  竞彩场次号
     */
    Dto queryBasketBallSp(String mathchCode);
}