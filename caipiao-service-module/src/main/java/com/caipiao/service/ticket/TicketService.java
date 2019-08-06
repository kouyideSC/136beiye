package com.caipiao.service.ticket;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.JclqUtils;
import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.PushMsgUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.ticket.TicketMapper;
import com.caipiao.dao.ticket.TicketVoteMapper;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.ticket.TicketConfig;
import com.caipiao.domain.ticket.TicketVote;
import com.caipiao.domain.ticket.TicketVoteRule;
import com.caipiao.domain.vo.PrizeMoneyVO;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.service.exception.ServiceException;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 出票相关服务
 * Created by kouyi on 2017/12/04.
 */
@Service("ticketService")
public class TicketService
{
    private static Logger logger = LoggerFactory.getLogger(TicketService.class);
    @Autowired
    private TicketVoteMapper ticketVoteMapper;
    @Autowired
    private TicketMapper ticketMapper;

    private static Map<String,String> voteAccountMaps = new HashMap<String,String>();//实体店对应的帐号编号

    static
    {
        voteAccountMaps.put("11111","9");
        voteAccountMaps.put("22222","27");
    }

    /**
     * 根据参数-查询分票规则列表
     * @return
     */
    public Map<String, List<TicketVoteRule>> queryTicketRuleAll() throws Exception {
        try {
            List<TicketVoteRule> listRule = ticketVoteMapper.queryTicketRuleAll();
            if(StringUtil.isEmpty(listRule)) {
                return null;
            }
            Map<String, List<TicketVoteRule>> mapRule = new HashMap<>();
            for(TicketVoteRule rule : listRule) {
                if(StringUtil.isEmpty(rule.getReceiveTime())) {
                    continue;
                }
                if(DateUtil.isContain(rule.getReceiveTime())) {//规则在时间段内
                    if (mapRule.containsKey(rule.getPlayType())) {
                        List<TicketVoteRule> list = mapRule.get(rule.getPlayType());
                        list.add(rule);
                    } else {
                        List<TicketVoteRule> list = new ArrayList<>();
                        list.add(rule);
                        mapRule.put(rule.getPlayType(), list);
                    }
                }
            }
            return mapRule;
        } catch (Exception e) {
            logger.error("[查询分票规则列表异常] errorDesc=" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据参数-查询控制参数列表
     * @return
     */
    public List<TicketConfig> queryTicketConfigAll() throws Exception {
        try {
            return ticketVoteMapper.queryTicketConfigAll();
        } catch (Exception e) {
            logger.error("[查询控制参数列表异常] errorDesc=" + e.getMessage());
            return null;
        }
    }

    /**
     * 新票入库-批量操作-保证单方案拆票完整性
     * @param tickets
     * @return 未分配出票商的订单状态回退列表
     * @throws ServiceException
     * @throws Exception
     */
    public void saveTicket(List<SchemeTicket> tickets, Map<String, Boolean> listSchemes, Logger logger) throws ServiceException {
        try {
            if(StringUtil.isEmpty(tickets)) {
                return;
            }
            Map<String,Integer> msgMaps = new HashMap<String,Integer>();//实体店出票商对应的出票数
            for(SchemeTicket ticket : tickets) {
                if(StringUtil.isEmpty(ticket.getVoteId())) {
                    logger.info("[新票入库终止] 方案号=" + tickets.get(0).getSchemeId() + "未分配出票商");
                    listSchemes.put(ticket.getSchemeId(), ticket.getZhuiHao());
                    break;
                }
                ticketMapper.saveTicket(ticket);
                if("11111".equals(ticket.getVoteId()) || "22222".equals(ticket.getVoteId()) || "33333".equals(ticket.getVoteId()))
                {
                    msgMaps.put(ticket.getVoteId(),StringUtil.isEmpty(msgMaps.get(ticket.getVoteId()))? 1 : (msgMaps.get(ticket.getVoteId()) + 1));
                }
            }
            //如果有实体店出票,则给实体店相对应的后台帐号推送消息
            try
            {
                if(msgMaps.size() > 0)
                {
                    Dto dataDto = new BaseDto("pushAddress","http://172.19.175.65:9999/manager/sendmsg");//消息推送地址
                    for(String key : msgMaps.keySet())
                    {
                        if(StringUtil.isNotEmpty(msgMaps.get(key)) && msgMaps.get(key) > 0)
                        {
                            dataDto.put("aid",voteAccountMaps.get(key));//消息接收帐户
                            dataDto.put("cptotal",msgMaps.get(key));//消息条数
                            PushMsgUtil.pushMsgToClient(dataDto);//推送消息
                        }
                    }
                }
            }
            catch(Exception e)
            {
                logger.error("[新票入库]给实体店对应的后台帐号推送消息发生异常!异常信息:",e);
            }
        } catch (Exception e){
            logger.error("[新票入库异常] schemeId=" + tickets.get(0).getSchemeId(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询出票商信息
     * @return
     */
    public TicketVote queryTicketVoteInfo(String voteId) throws ServiceException {
        try {
            return ticketVoteMapper.queryTicketVoteInfo(voteId);
        } catch (Exception e){
            logger.error("[查询出票商信息异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询出票商支持彩种编号(出票)
     * @return
     */
    public List<VoteVo> queryTicketVoteLotteryList(String voteId) throws ServiceException {
        try {
            List<VoteVo> vo = ticketVoteMapper.queryTicketVoteLotteryList(voteId);
            if(StringUtil.isNotEmpty(vo)) {
                Iterator<VoteVo> it = vo.iterator();
                while(it.hasNext()){
                    VoteVo v = it.next();
                    if(StringUtil.isEmpty(v.getReceiveTime()) || !DateUtil.isContain(v.getReceiveTime())) {//规则在时间段内
                        it.remove();
                    }
                }
            }
            return vo;
        } catch (Exception e){
            logger.error("[查询出票商支持彩种编号异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据出票商查询票列表(出票)
     * @param voteId
     * @param ticketStatus
     * @return
     * @throws ServiceException
     */
    public List<SchemeTicket> queryTicketList(String voteId, Integer ticketStatus) throws ServiceException {
        try {
            return ticketMapper.queryTicketListForVote(voteId, ticketStatus);
        } catch (Exception e){
            logger.error("[根据出票商查询票列表异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询票列表(出票)-条件无期号
     * @param voteId
     * @param playType
     * @param ticketStatus
     * @return
     * @throws ServiceException
     */
    public List<SchemeTicket> queryTicketList(String voteId, String playType, Integer ticketStatus) throws ServiceException {
        try {
            return ticketMapper.queryNoPeriodTicketList(voteId, playType, ticketStatus);
        } catch (Exception e){
            logger.error("[查询票列表(条件无期号)异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询票列表(出票)-条件包括期号
     * @param voteId
     * @param playType
     * @param period
     * @param ticketStatus
     * @return
     * @throws ServiceException
     */
    public List<SchemeTicket> queryTicketList(String voteId, String playType, String period, Integer ticketStatus) throws ServiceException {
        try {
            return ticketMapper.queryPeriodTicketList(voteId, playType, period, ticketStatus);
        } catch (Exception e){
            logger.error("[查询票列表(条件包括期号)异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新提票状态(出票)
     * @param ticketId
     * @param ticketStatus
     * @param ticketDesc
     * @param voteTicketId
     * @return
     * @throws ServiceException
     */
    public int updateCastTicketStatus(String ticketId, Integer ticketStatus, String ticketDesc, String voteTicketId) throws ServiceException {
        try {
            return ticketMapper.updateCastTicketStatus(ticketStatus, ticketDesc, voteTicketId, ticketId);
        } catch (Exception e){
            logger.error("[更新提票状态异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新出票商9696出票状态(出票)
     * @param id
     * @param ticketStatus
     * @param ticketDesc
     * @return
     * @throws ServiceException
     */
    public int update9696OutTicketStatus(Long id, Integer ticketStatus, String ticketDesc, int random) throws ServiceException {
        try {
            if(random > 70) {
                random = 70;
            }
            Random rd = new Random();
            String result = "";
            for (int i=0; i < 18; i++)
            {
                result += rd.nextInt(10);
            }
            return ticketMapper.update9696OutTicketStatus(ticketStatus, ticketDesc, result, random, id);
        } catch (Exception e){
            logger.error("[更新出票商9696出票状态异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新出票状态(出票)
     * @param ticketId
     * @param ticketStatus
     * @param ticketDesc
     * @param voteTicketId
     * @param codesSP
     * @return
     * @throws ServiceException
     */
    public int updateOutTicketStatus(String ticketId, Integer ticketStatus, String ticketDesc, String voteTicketId, String codesSP) throws ServiceException {
        try {
            return ticketMapper.updateOutTicketStatus(ticketStatus, ticketDesc, voteTicketId, codesSP, ticketId);
        } catch (Exception e){
            logger.error("[更新出票状态异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新兑奖状态(出票)
     * @param ticketId
     * @param ticketStatus
     * @param ticketDesc
     * @param votePrize
     * @param votePrizeTax
     * @return
     * @throws ServiceException
     */
    public int updateAwardTicketStatus(String ticketId, Integer ticketStatus, String ticketDesc, Double votePrize,
                                       Double votePrizeTax) throws ServiceException {
        try {
            return ticketMapper.updateAwardTicketStatus(ticketStatus, ticketDesc, votePrize, votePrizeTax, ticketId);
        } catch (Exception e){
            logger.error("[更新兑奖状态异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新大乐透乐善号码(出票)
     * @param ticketId
     * @param drawNumber
     * @return
     * @throws ServiceException
     */
    public int updateOutTicketDrawNumber(String ticketId, String drawNumber) throws ServiceException {
        try {
            return ticketMapper.updateOutTicketDrawNumber(drawNumber, ticketId);
        } catch (Exception e){
            logger.error("[更新大乐透乐善号码异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据网站票号查询票信息(接收出票通知)
     * @param ticketId
     * @return
     * @throws ServiceException
     */
    public SchemeTicket queryNoticeTicketInfo(String ticketId) throws ServiceException {
        try {
            return ticketMapper.queryNoticeTicketInfo(ticketId);
        } catch (Exception e){
            logger.error("[根据网站票号查询票信息异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据方案编号查询票列表
     * @param schemeId
     * @return
     * @throws ServiceException
     */
    public List<SchemeTicket> queryTicketListBySchemeId(String schemeId) throws ServiceException {
        try {
            return ticketMapper.queryTicketListBySchemeId(schemeId);
        } catch (Exception e){
            logger.error("[根据方案编号查询票列表异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据方案编号查询需要过关的票列表
     * @param schemeId
     * @return
     * @throws ServiceException
     */
    public List<SchemeTicket> queryGuoGuanTicketListBySchemeId(String schemeId, Integer bonusState) throws ServiceException {
        try {
            return ticketMapper.queryGuoGuanTicketListBySchemeId(schemeId, bonusState);
        } catch (Exception e){
            logger.error("[根据方案编号查询需要过关的票列表异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据期次号查询需要过关的票列表
     * @param period
     * @param bonusState
     * @return
     * @throws ServiceException
     */
    public List<SchemeTicket> queryGuoGuanTicketListByPeriod(Period period, Integer bonusState) throws ServiceException {
        try {
            return ticketMapper.queryGuoGuanTicketListByPeriod(period.getLotteryId(), period.getPeriod(), bonusState);
        } catch (Exception e){
            logger.error("[根据期次号查询需要过关的票列表异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新票状态及中奖奖金
     * @param ticket
     * @return
     * @throws ServiceException
     */
    public int updateTicketPrizeMoney(SchemeTicket ticket) throws ServiceException {
        try {
            return ticketMapper.updateTicketPrizeMoney(ticket);
        } catch (Exception e){
            logger.error("[更新票状态及中奖奖金异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据场次查询需要汇总奖金的票-竞彩
     * @param lotteryId
     * @param matchCode
     * @return
     * @throws ServiceException
     */
    public List<String> queryTicketPrizeSummaryForMatch(String lotteryId, String matchCode) throws ServiceException {
        try {
            return ticketMapper.queryTicketPrizeSummaryForMatch(lotteryId, matchCode);
        } catch (Exception e){
            logger.error("[根据场次查询需要汇总奖金的票列表异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据期次查询需要汇总奖金的票-数字彩
     * @param period
     * @return
     * @throws ServiceException
     */
    public List<String> queryTicketPrizeSummaryForPeriod(Period period) throws ServiceException {
        try {
            return ticketMapper.queryTicketPrizeSummaryForPeriod(period.getLotteryId(), period.getPeriod());
        } catch (Exception e){
            logger.error("[根据期次查询需要汇总奖金的票列表异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新票过关状态
     * @param schemeId
     * @param bonusState
     * @return
     * @throws ServiceException
     */
    public int updateTicketBonusState(String schemeId, Integer bonusState) throws ServiceException {
        try {
            return ticketMapper.updateTicketBonusState(schemeId, bonusState);
        } catch (Exception e){
            logger.error("[更新票过关状态异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新大乐透乐善玩法过关状态
     * @param ticketId
     * @param info
     * @param lsWin
     * @return
     * @throws ServiceException
     */
    public int updateOutTicketNumberBonusInfo(String ticketId, String info, Integer lsWin) throws ServiceException {
        try {
            return ticketMapper.updateOutTicketNumberBonusInfo(info, ticketId, lsWin);
        } catch (Exception e){
            logger.error("[更新大乐透乐善玩法过关状态异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 系统自动撤单中-根据票号更新票状态
     * @param ticketId
     * @param ticketStatus
     * @param ticketDesc
     * @return
     * @throws Exception
     */
    public int updateOutTicketStatusForCancel(String ticketId, Integer ticketStatus, String ticketDesc) throws ServiceException {
        try {
            return ticketMapper.updateOutTicketStatusForCancel(ticketStatus, ticketDesc, ticketId);
        } catch (Exception e){
            logger.error("[系统自动撤单中-根据票号更新票状态异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新出票商余额
     * @param money
     * @param voteId
     * @return
     */
    public void updateVoteBalance(String money, String voteId) {
        try {
            ticketVoteMapper.updateVoteBalance(money, voteId);
        } catch (Exception e){
            logger.error("[更新出票商余额异常]", e);
        }
    }
}
