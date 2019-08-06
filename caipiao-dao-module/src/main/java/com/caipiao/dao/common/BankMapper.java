package com.caipiao.dao.common;

import com.caipiao.domain.common.Bank;
import com.caipiao.domain.common.BankSub;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

/**
 * 银行数据库访问接口
 * @author  mcdog
 */
public interface BankMapper
{
    /**
     * 查询银行
     * @author  mcdog
     */
    List<Bank> queryBanks(Dto params);
    /**
     * 查询银行支行
     * @author  mcdog
     */
    List<BankSub> queryBankSubs(Dto params);
}