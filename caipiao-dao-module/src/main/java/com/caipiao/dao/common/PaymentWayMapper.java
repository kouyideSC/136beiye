package com.caipiao.dao.common;

import com.caipiao.domain.common.PayWay;
import com.caipiao.domain.common.PaymentWay;
import com.caipiao.domain.cpadmin.Dto;

import java.util.List;

/**
 * 付款(提现)方式(渠道)数据库访问接口
 * @author  mcdog
 */
public interface PaymentWayMapper
{
    /**
     * 查询付款(提现)方式(渠道)
     * @ahthor  sjq
     */
    List<PaymentWay> queryPaymentWays(Dto params);
    /**
     * 查询提现渠道信息(管理后台)
     * @author	sjq
     */
    List<Dto> queryPaymentWayInfo(Dto params);
    /**
     * 查询提现渠道总记录条数(管理后台)
     * @author	sjq
     */
    int queryPaymentWayInfoCount(Dto params);
    /**
     * 编辑提现渠道
     * @author	sjq
     */
    int editTxqd(Dto params);
    /**
     * 查询提现渠道区域配置
     * @author	sjq
     */
    Dto queryPaymentWayAreaInfo(Dto params);
    /**
     * 查询提现渠道银行配置
     * @author	sjq
     */
    Dto queryPaymentWayBankInfo(Dto params);
}