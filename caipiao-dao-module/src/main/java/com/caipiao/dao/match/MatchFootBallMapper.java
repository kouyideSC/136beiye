package com.caipiao.dao.match;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.vo.JczqAwardInfo;
import com.caipiao.domain.vo.JczqMatchVo;
import com.caipiao.domain.vo.KaiJiangVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 足球对阵模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface MatchFootBallMapper {

    /**
     * 查询计奖状态为未处理的比赛-截止前5分钟
     * @return
     */
    List<MatchFootBall> queryJczqStatusNoHandlerList();

    /**
     * 根据竞彩场次号-查询竞彩足球对阵信息
     * @param matchCode
     * @return
     */
    MatchFootBall queryMatchFootBallByMatchCode(String matchCode);

    /**
     * 根据传入参数-查询竞彩足球对阵列表
     * @param match
     * @return
     */
    List<MatchFootBall> queryMatchFootBallList(MatchFootBall match);

    /**
     * 根据参数-查询竞彩足球可售对阵列表-前端接口展示使用
     * @return
     */
    List<JczqMatchVo> queryJczqSaleMatchList();

    /**
     * 查询开赛时间在3天内且至少105分钟前，无赛果和等待抓取赛果的-竞彩足球对阵列表
     * @return
     */
    List<MatchFootBall> queryMatchFootBallNoResultList();

    /**
     * 新增足球对阵场次
     * @param match
     * @return
     */
    int insertMatchFootBall(MatchFootBall match);

    /**
     * 通用更新竞彩场次数据
     * @param match
     * @return
     */
    int updateMatchFootBall(MatchFootBall match);

    /**
     * 根据比赛唯一编号-更新比赛状态
     * @param award
     */
    int updateMatchStatusById(JczqAwardInfo award);

    /**
     * 抓取任务-更新竞彩足球让球数
     * @param lose
     * @param id
     * @return
     */
    int updateMatchFootBallLose(@Param("lose")Integer lose, @Param("id")Long id);

    /**
     * 抓取任务-更新竞彩足球赛果
     * @param match
     * @return
     */
    int updateMatchFootBallResult(MatchFootBall match);

    /**
     * 查询最新开奖的对阵信息
     * @author  mcdog
     * @param   params  查询参数(lotteryId-彩种id appStatus-app销售状态)
     */
    KaiJiangVo queryLatestKjMatch(Dto params);

    /**
     * 查询足球对阵信息(管理后台)
     * @author  mcdog
     */
    List<Dto> queryFootBallMatchs(Dto params);
    /**
     * 查询足球对阵总记录条数(管理后台)
     * @author  mcdog
     */
    int queryFootBallMatchsCount(Dto params);
    /**
     * 查询足球赛事审核信息(管理后台)
     * @author  mcdog
     */
    List<Dto> queryFootBallAudit(Dto params);
    /**
     * 查询足球赛事审核总记录条数(管理后台)
     * @author  mcdog
     */
    int queryFootBallAuditCount(Dto params);
    /**
     * 编辑比赛热门状态(管理后台)
     * @author  mcdog
     */
    int editHot(Dto params);
    /**
     * 编辑销售状态(管理后台)
     * @author  mcdog
     */
    int editSellStatus(Dto params);
    /**
     * 编编玩法销售状态(管理后台)
     * @author  mcdog
     */
    int editPlaySellStatus(Dto params);
    /**
     * 编辑赛果(管理后台)
     * @author  mcdog
     */
    int editMatchResult(Dto params);
    /**
     * 编辑sp(管理后台)
     * @author  mcdog
     */
    int editSp(Dto params);

    /**
     * 充值比赛计奖状态-回退至赛果获取中
     * @author mcdog
     */
    int updateMatchRebackState(Dto params);
}