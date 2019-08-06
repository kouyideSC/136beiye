package com.caipiao.dao.ticket;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.ticket.TicketConfig;
import com.caipiao.domain.ticket.TicketVote;
import com.caipiao.domain.ticket.TicketVoteRule;
import com.caipiao.domain.vo.VoteVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出票商及规则管理功能接口定义
 * @author kouyi 2017-11-04
 */
public interface TicketVoteMapper {

    /**
     * 查询出票商信息
     * @param voteId
     * @return
     */
    TicketVote queryTicketVoteInfo(String voteId);

    /**
     * 查询分票规则列表
     * @return
     */
    List<TicketVoteRule> queryTicketRuleAll();

    /**
     * 查询控制参数列表
     * @return
     */
    List<TicketConfig> queryTicketConfigAll();


    /**
     * 查询出票商列表（后台管理）
     * @param params
     * @return
     */
    List<Dto> queryTicketVoteList(Dto params);

    /**
     * 新增出票商（后台管理）
     * @param params
     * @return
     */
    int saveTicketVote(Dto params);

    /**
     * 删除出票商(管理后台)
     * @param params
     * @return
     */
    int deleteTicketVote(Dto params);

    /**
     * 修改出票商(管理后台)
     * @param params
     * @return
     */
    int updateTicketVote(Dto params);

    /**
     * 查询出票商分票规则列表（后台管理）
     * @param params
     * @return
     */
    List<Dto> queryTicketRuleList(Dto params);

    /**
     * 新增出票商分票规则（后台管理）
     * @param params
     * @return
     */
    int saveTicketRule(Dto params);

    /**
     * 删除出票商分票规则(管理后台)
     * @param params
     * @return
     */
    int deleteTicketRule(Dto params);

    /**
     * 修改出票商分票规则(管理后台)
     * @param params
     * @return
     */
    int updateTicketRule(Dto params);

    /**
     * 查询出票控制参数列表（后台管理）
     * @param params
     * @return
     */
    List<Dto> queryTicketConfigList(Dto params);

    /**
     * 修改出票控制参数(管理后台)
     * @param params
     * @return
     */
    int updateTicketConfig(Dto params);

    /**
     * 查询出票商支持彩种编号(出票)
     * @return
     */
    List<VoteVo> queryTicketVoteLotteryList(String voteId);

    /**
     * 更新出票商余额
     * @param money
     * @param voteId
     * @return
     */
    int updateVoteBalance(@Param("money") String money, @Param("voteId") String voteId);
}