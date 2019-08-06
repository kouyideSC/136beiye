package com.caipiao.dao.ticket;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.user.User;
import com.caipiao.domain.vo.PrizeMoneyVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 票模块功能接口定义
 * @author kouyi 2017-12-5
 */
public interface TicketMapper {

    /**
     * 查询出票列表（后台管理）
     * @param params
     * @return
     */
    List<Dto> queryTicketList(Dto params) throws Exception;

    /**
     * 实体店查询出票列表（后台管理）
     * @param params
     * @return
     */
    List<Dto> queryTicketListShop(Dto params) throws Exception;

    /**
     * 查询出票列表-总条数（后台管理）
     * @author kouyi
     */
    int queryTicketListCount(Dto params) throws Exception;

    /**
     * 根据编号查询票信息
     * @param id
     * @return
     */
    Dto queryTicketInfoById(Long id) throws Exception;

    /**
     * 新票入库
     * @param ticket
     * @return
     */
    int saveTicket(SchemeTicket ticket) throws Exception;

    /**
     * 手动批量切票(管理后台)
     * @param params
     * @return
     */
    int changeTicket(Dto params) throws Exception;

    /**
     * 根据出票商查询票列表
     * @param voteId
     * @param ticketStatus
     * @return
     * @throws Exception
     */
    List<SchemeTicket> queryTicketListForVote(@Param("voteId") String voteId, @Param("ticketStatus") Integer ticketStatus) throws Exception;

    /**
     * 更新出票商9696出票状态(出票)
     * @param ticketStatus
     * @param ticketDesc
     * @param voteTicketId
     * @param id
     * @return
     * @throws Exception
     */
    int update9696OutTicketStatus(@Param("ticketStatus") Integer ticketStatus, @Param("ticketDesc") String ticketDesc,
                              @Param("voteTicketId") String voteTicketId, @Param("random") Integer random, @Param("id") Long id) throws Exception;


    /**
     * 查询票列表(出票)-条件无期号
     * @param voteId
     * @param playTypeId
     * @param ticketStatus
     * @return
     * @throws Exception
     */
    List<SchemeTicket> queryNoPeriodTicketList(@Param("voteId") String voteId, @Param("playTypeId") String playTypeId,
                                         @Param("ticketStatus") Integer ticketStatus) throws Exception;

    /**
     * 查询票列表(出票)-条件包括期号
     * @param voteId
     * @param playTypeId
     * @param ticketStatus
     * @return
     * @throws Exception
     */
    List<SchemeTicket> queryPeriodTicketList(@Param("voteId") String voteId, @Param("playTypeId") String playTypeId,
                                             @Param("period") String period, @Param("ticketStatus") Integer ticketStatus) throws Exception;

    /**
     * 更新提票状态(出票)
     * @param ticketStatus
     * @param ticketDesc
     * @param voteTicketId
     * @param ticketId
     * @return
     * @throws Exception
     */
    int updateCastTicketStatus(@Param("ticketStatus") Integer ticketStatus, @Param("ticketDesc") String ticketDesc,
                               @Param("voteTicketId") String voteTicketId, @Param("ticketId") String ticketId) throws Exception;

    /**
     * 更新出票状态(出票)
     * @param ticketStatus
     * @param ticketDesc
     * @param voteTicketId
     * @param codesSP
     * @param ticketId
     * @return
     * @throws Exception
     */
    int updateOutTicketStatus(@Param("ticketStatus") Integer ticketStatus, @Param("ticketDesc") String ticketDesc,
                              @Param("voteTicketId") String voteTicketId,
                              @Param("codesSP") String codesSP, @Param("ticketId") String ticketId) throws Exception;

    /**
     * 实体店打票更新出票状态(出票)
     * @param ticketStatus
     * @param ticketDesc
     * @param voteTicketId
     * @param random
     * @param codesSP
     * @param id
     * @return
     * @throws Exception
     */
    int updateShopOutTicketStatus(@Param("ticketStatus") Integer ticketStatus, @Param("ticketDesc") String ticketDesc,
                                  @Param("voteTicketId") String voteTicketId, @Param("random") Integer random,
                                  @Param("codesSP") String codesSP, @Param("id") Long id) throws Exception;


    /**
     * 更新兑奖状态(出票)
     * @param ticketStatus
     * @param ticketDesc
     * @param votePrize
     * @param votePrizeTax
     * @param ticketId
     * @return
     * @throws Exception
     */
    int updateAwardTicketStatus(@Param("ticketStatus") Integer ticketStatus, @Param("ticketDesc") String ticketDesc,
                               @Param("votePrize") Double votePrize, @Param("votePrizeTax") Double votePrizeTax,
                               @Param("ticketId") String ticketId) throws Exception;

    /**
     * 更新大乐透乐善号码(出票)
     * @param drawNumber
     * @param ticketId
     * @return
     * @throws Exception
     */
    int updateOutTicketDrawNumber(@Param("drawNumber") String drawNumber, @Param("ticketId") String ticketId) throws Exception;

    /**
     * 更新大乐透乐善玩法过关状态
     * @param info
     * @param ticketId
     * @return
     * @throws Exception
     */
    int updateOutTicketNumberBonusInfo(@Param("info") String info, @Param("ticketId") String ticketId, @Param("lsWin") Integer lsWin) throws Exception;

    /**
     * 根据网站票号查询票信息(接收出票通知)
     * @param ticketId
     * @return
     * @throws Exception
     */
     SchemeTicket queryNoticeTicketInfo(String ticketId) throws Exception;

    /**
     * 根据方案编号查询票列表
     * @param schemeId
     * @return
     * @throws Exception
     */
     List<SchemeTicket> queryTicketListBySchemeId(String schemeId) throws Exception;

    /**
     * 根据方案编号查询需要过关的票列表
     * @param schemeId
     * @return
     * @throws Exception
     */
    List<SchemeTicket> queryGuoGuanTicketListBySchemeId(@Param("schemeId") String schemeId, @Param("bonusState") Integer bonusState) throws Exception;

    /**
     * 根据期次号查询需要过关的票列表
     * @param lotteryId
     * @param period
     * @param bonusState
     * @return
     * @throws Exception
     */
    List<SchemeTicket> queryGuoGuanTicketListByPeriod(@Param("lotteryId") String lotteryId, @Param("period") String period, @Param("bonusState") Integer bonusState) throws Exception;

    /**
     * 更新票状态及中奖奖金
     * @param ticket
     * @return
     * @throws Exception
     */
    int updateTicketPrizeMoney(SchemeTicket ticket) throws Exception;

    /**
     * 根据场次查询需要汇总奖金的票-竞彩
     * @param lotteryId
     * @param matchCode
     * @return
     * @throws Exception
     */
    List<String> queryTicketPrizeSummaryForMatch(@Param("lotteryId") String lotteryId, @Param("matchCode") String matchCode) throws Exception;

    /**
     * 根据期次查询需要汇总奖金的票-数字彩
     * @param lotteryId
     * @param period
     * @return
     * @throws Exception
     */
    List<String> queryTicketPrizeSummaryForPeriod(@Param("lotteryId") String lotteryId, @Param("period") String period) throws Exception;


    /**
     * 更新票过关状态
     * @param schemeId
     * @param bonusState
     * @return
     * @throws Exception
     */
    int updateTicketBonusState(@Param("schemeId") String schemeId, @Param("bonusState") Integer bonusState) throws Exception;

    /**
     * 系统自动撤单中-根据票号更新票状态
     * @param ticketStatus
     * @param ticketDesc
     * @param ticketId
     * @return
     * @throws Exception
     */
    int updateOutTicketStatusForCancel(@Param("ticketStatus") Integer ticketStatus, @Param("ticketDesc") String ticketDesc, @Param("ticketId") String ticketId) throws Exception;

    /**
     * 根据方案号更新票状态
     * @param ticketStatus
     * @param ticketDesc
     * @param schemeId
     * @return
     * @throws Exception
     */
    int updateOutTicketStatusForSchemeId(@Param("ticketStatus") Integer ticketStatus, @Param("ticketDesc") String ticketDesc, @Param("schemeId") String schemeId) throws Exception;

}