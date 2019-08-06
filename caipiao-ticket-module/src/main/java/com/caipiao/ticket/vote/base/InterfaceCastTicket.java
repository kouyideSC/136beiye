package com.caipiao.ticket.vote.base;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.ticket.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 对接出票商接口
 * Created by kouyi on 2016/12/12.
 */
public interface InterfaceCastTicket {
    /**
     * 快频提票任务
     */
    void kpCastTicketTask();
    /**
     * 慢频提票任务
     */
    void mpCastTicketTask();
    /**
     * 竞彩提票任务
     */
    void jcCastTicketTask();

    /**
     * 快频出票查询任务
     */
    void kpQueryTicketTask();
    /**
     * 慢频出票查询任务
     */
    void mpQueryTicketTask();
    /**
     * 竞彩出票查询任务
     */
    void jcQueryTicketTask();

    /**
     * 快频兑奖任务
     */
    void kpAwardTicketTask();
    /**
     * 快频兑奖任务
     */
    void mpAwardTicketTask();
    /**
     * 快频兑奖任务
     */
    void jcAwardTicketTask();

    /**
     * 查询期次开奖号码
     */
    void queryPeriodDrawCode();

    /**
     * 查询账户余额
     */
    void queryAccountBalance();
}
