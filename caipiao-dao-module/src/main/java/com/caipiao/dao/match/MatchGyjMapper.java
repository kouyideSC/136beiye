package com.caipiao.dao.match;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.match.GyjMatch;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.vo.JczqAwardInfo;
import com.caipiao.domain.vo.JczqMatchVo;
import com.caipiao.domain.vo.KaiJiangVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 冠亚军对阵模块功能接口定义
 * @author kouyi 2018-04-04
 */
public interface MatchGyjMapper {

    /**
     * 根据场次号-查询冠亚军对阵信息
     * @param lotteryId
     * @param matchCode
     * @return
     */
    GyjMatch queryGyjMatchInfo(@Param("lotteryId") String lotteryId, @Param("matchCode") String matchCode);

    /**
     * 根据传入参数-查询冠亚军对阵列表
     * @param match
     * @return
     */
    List<GyjMatch> queryGyjMatchList(GyjMatch match);

    /**
     * 查询冠亚军对阵
     * @author  mcdog
     */
    List<Dto> queryGyjMatchInfos(Dto params);

    /**
     * 新增冠亚军对阵场次
     * @param match
     * @return
     */
    int insertGyjMatch(GyjMatch match);

    /**
     * 通用更新冠亚军场次数据
     * @param match
     * @return
     */
    int updateGyjMatch(GyjMatch match);
}