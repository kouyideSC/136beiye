package com.caipiao.dao.user;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.UserDetail;

import java.util.List;

/**
 * 用户账户交易流水模块功能接口定义
 * @author kouyi 2017-11-03
 */
public interface UserDetailMapper {

    /**
     * 保存账户交易流水信息
     * @param record
     * @return
     */
    int insertUserDetail(UserDetail record);

    /**
     * 查询用户账户流水
     * @author  mcdog
     */
    List<UserDetail> queryUserDetail(Dto params);
    /**
     * 查询用户账户流水总记录条数
     * @author  mcdog
     */
    int queryUserDetailCount(Dto params);
    /**
     * 更新账户流水状态(提现)
     * @author  mcdog
     */
    int updateUserDetailOfTx(Dto params);
    /**
     * 查询用户账户流水(管理后台)
     * @author  mcdog
     */
    List<Dto> queryUserAccountDetailInfo(Dto params);
    /**
     * 查询用户账户流水总记录条数(管理后台)
     * @author  mcdog
     */
    int queryUserAccountDetailCount(Dto params);
}