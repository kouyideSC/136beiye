package com.caipiao.ticket.vote.base;

import com.caipiao.domain.vo.VoteVo;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.lottery.PeriodService;
import com.caipiao.service.ticket.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by kouyi on 2016/12/12.
 */
public class BaseCastTicket {
    protected static Logger logger = LoggerFactory.getLogger(BaseCastTicket.class);
    private static final String VOTE_KEY = "TICKET_VOTES_KEY";
    protected static final int maxMoney = 20000;//单票最大金额
    protected static final int maxMultiple = 99;//单票最大倍数

    @Autowired
    protected TicketService ticketService;
    @Autowired
    protected PeriodService periodService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected MemCached memcache;

    /**
     * 初始化出票商和彩种配置
     * @return
     */
    protected List<VoteVo> initVoteLotteryConfig(String voteId) {
        List<VoteVo> voteList = null;
        try {
            if(memcache.contains(VOTE_KEY + voteId)) {
                voteList = (List<VoteVo>) memcache.get(VOTE_KEY + voteId);
            } else {
                voteList = ticketService.queryTicketVoteLotteryList(voteId);
                memcache.set(VOTE_KEY + voteId, voteList, 600);//10分钟(没数据也设置缓存 避免每次查库)
            }
        } catch (Exception ex) {
            logger.error("[查询出票商相关彩种异常]", ex);
        }
        return voteList;
    }
}
