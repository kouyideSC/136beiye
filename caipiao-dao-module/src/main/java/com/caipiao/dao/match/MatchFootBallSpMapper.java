package com.caipiao.dao.match;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.match.MatchFootBallSp;

import java.util.List;

/**
 * 足球对阵赔率模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface MatchFootBallSpMapper {

    /**
     * 根据竞彩场次号-查询竞彩足球赔率信息
     * @param matchCode
     * @return
     */
    MatchFootBallSp queryMatchFootBallSpByMatchCode(String matchCode);


    /**
     * 新增足球对阵sp记录
     * @param record
     * @return
     */
    int insertMatchFootBallSp(MatchFootBallSp record);

    /**
     * 更新竞彩赔率数据
     * @param match
     * @return
     */
    int updateMatchFootBallSp(MatchFootBallSp match);

    /**
     * 根据竞彩场次号查询足球赔率信息
     * @author  mcdog
     * @param   mathchCode  竞彩场次号
     */
    Dto queryFootBallSp(String mathchCode);
}