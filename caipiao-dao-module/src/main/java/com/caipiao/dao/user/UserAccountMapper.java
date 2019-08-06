package com.caipiao.dao.user;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.UserAccount;

/**
 * 用户账户模块功能接口定义
 * @author kouyi 2017-09-28
 */
public interface UserAccountMapper {
    /**
     * 初始化用户账户信息
     * @param userId
     * @return
     */
    void insertUserAccount(Long userId) throws Exception;

    /**
     * 根据用户编号查询账户信息
     * @param userId
     * @return
     */
    UserAccount queryUserAccountInfoByUserId(Long userId) throws Exception;

    /**
     * 更新用户账户余额等信息
     * @author  mcdog
     */
    int updateUserAccount(Dto params) throws Exception;
}
