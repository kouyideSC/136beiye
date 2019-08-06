package com.caipiao.admin.service.check;

import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.check.CheckMapper;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 对账相关业务处理服务
 * Created by kouyi on 2018/03/09.
 */
@Service("checkService")
public class CheckService {
    @Autowired
    private CheckMapper checkMapper;

    /**
     * 查询平台资金对账列表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryPlatFormCapital(Dto params) throws Exception
    {
        return checkMapper.queryPlatFormCapital(params);
    }

    /**
     * 查询平台资金对账列表-总条数（后台管理）
     * @author kouyi
     */
    public int queryPlatFormCapitalCount(Dto params) throws Exception
    {
        return checkMapper.queryPlatFormCapitalCount(params);
    }

    /**
     * 查询平台方案和票报表（后台管理）
     * @author kouyi
     */
    public List<Dto> querySchemeTicket(Dto params) throws Exception
    {
        return checkMapper.querySchemeTicket(params);
    }

    /**
     * 查询平台方案和票报表-总条数（后台管理）
     * @author kouyi
     */
    public int querySchemeTicketCount(Dto params) throws Exception
    {
        return checkMapper.querySchemeTicketCount(params);
    }

    /**
     * 查询平台兑奖计奖报表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryVoteSitePrize(Dto params) throws Exception
    {
        return checkMapper.queryVoteSitePrize(params);
    }

    /**
     * 查询平台兑奖计奖报表-总条数（后台管理）
     * @author kouyi
     */
    public int queryVoteSitePrizeCount(Dto params) throws Exception
    {
        return checkMapper.queryVoteSitePrizeCount(params);
    }

    /**
     * 查询平台兑奖报表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryVoteSiteAward(Dto params) throws Exception
    {
        return checkMapper.queryVoteSiteAward(params);
    }

    /**
     * 汇总某天平台总加奖金额
     * @return
     */
    public Double queryDateSchemeSubjoin(Dto params) throws Exception
    {
        return checkMapper.queryDateSchemeSubjoin(params);
    }

    /**
     * 查询平台用户返利报表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryUserRebate(Dto params) throws Exception
    {
        return checkMapper.queryUserRebate(params);
    }

    /**
     * 查询平台用户返利报表-总条数（后台管理）
     * @author kouyi
     */
    public int queryUserRebateCount(Dto params) throws Exception
    {
        return checkMapper.queryUserRebateCount(params);
    }
}