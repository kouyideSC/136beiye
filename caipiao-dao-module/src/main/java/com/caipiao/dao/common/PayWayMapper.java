package com.caipiao.dao.common;

import com.caipiao.domain.common.PayWay;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.UserPay;

import java.util.List;

/**
 * 充值方式模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface PayWayMapper {

    /**
     * 新增充值方式
     * @param record
     * @return
     */
    int insertPayWay(PayWay record);
    /**
     * 查询充值方式
     * @ahthor  sjq
     */
    List<PayWay> queryPayWays(Dto params);
    /**
     * 查询充值方式(管理后台)
     * @ahthor  sjq
     */
    List<Dto> queryPayWayInfos(Dto params);
    /**
     * 查询充值方式总记录数(管理后台)
     * @ahthor  sjq
     */
    int queryPayWayInfosCount(Dto params);
    /**
     * 编辑充值方式
     * @ahthor  sjq
     */
    int editPayway(Dto params);
}