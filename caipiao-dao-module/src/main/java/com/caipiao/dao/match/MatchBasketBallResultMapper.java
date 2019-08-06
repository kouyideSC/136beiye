package com.caipiao.dao.match;

import com.caipiao.domain.match.MatchBasketBallResult;
import com.caipiao.domain.vo.JclqResultVo;
import com.caipiao.domain.vo.JczqResultVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 篮球赛果模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface MatchBasketBallResultMapper {

    /**
     * 根据期次范围查询篮球赛果
     * @param begin
     * @param end
     * @return
     */
    List<JclqResultVo> queryJclqResultList(@Param("begin")String begin, @Param("end")String end);

    /**
     * 根据场次号查询篮球赛果
     * @param matchCode
     * @return
     */
    MatchBasketBallResult queryJclqResultInfo(String matchCode);

    /**
     * 新增篮球赛果
     * @param record
     * @return
     */
    int insertBasketBallResult(MatchBasketBallResult record);

    /**
     * 更新篮球赛果
     * @param record
     * @return
     */
    int updateJclqResult(MatchBasketBallResult record);

}