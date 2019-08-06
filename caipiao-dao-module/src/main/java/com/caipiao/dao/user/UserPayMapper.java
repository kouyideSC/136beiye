package com.caipiao.dao.user;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.UserPay;

import java.util.List;

/**
 * 充值/提现模块功能接口定义
 * @author kouyi 2017-11-03
 */
public interface UserPayMapper {

    /**
     * 保存订单
     * @param record
     * @return
     */
    int insertUserPay(UserPay record);
    /**
     * 查询订单
     * @author  mcdog
     */
    List<UserPay> queryUserPays(Dto params);
    /**
     * 查询订单总记录条数
     * @author  mcdog
     */
    int queryUserPaysCount(Dto params);
    /**
     * 查询待发送支付结果通知的充值订单记录
     * @author  mcdog
     */
    List<Dto> queryRechargeForNeedNotify(Dto params);
    /**
     * 更新订单
     * @author  mcdog
     */
    int updateUserPay(Dto params);
    /**
     * 充值
     * @author  mcdog
     */
    void doRecharge(Dto params);
    /**
     * 扣款
     * @author  mcdog
     */
    void doDeduct(Dto params);
    /**
     * 查询充值/提现流水(管理后台)
     * @author  mcdog
     */
    List<Dto> queryUserPayInfos(Dto params);
    /**
     * 查询充值/提现流水总计(管理后台)
     * @author  mcdog
     */
    Dto queryUserPayInfosCount(Dto params);
}