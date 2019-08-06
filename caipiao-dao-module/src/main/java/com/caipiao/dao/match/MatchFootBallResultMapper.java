package com.caipiao.dao.match;

import com.caipiao.domain.match.MatchFootBallResult;
import com.caipiao.domain.vo.JczqResultVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 足球赛果模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface MatchFootBallResultMapper {

    /**
     * 根据期次范围查询足球赛果
     * @param begin
     * @param end
     * @return
     */
    List<JczqResultVo> queryJczqResultList(@Param("begin")String begin, @Param("end")String end);

    /**
     * 根据场次号查询足球赛果
     * @param matchCode
     * @return
     */
    MatchFootBallResult queryJczqResultInfo(String matchCode);

    /**
     * 新增足球赛果
     * @param record
     * @return
     */
    int insertFootBallResult(MatchFootBallResult record);

    /**
     * 更新足球赛果
     * @param record
     * @return
     */
    int updateJczqResult(MatchFootBallResult record);

}