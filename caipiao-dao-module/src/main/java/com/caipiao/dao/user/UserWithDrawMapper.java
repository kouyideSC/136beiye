package com.caipiao.dao.user;

import com.caipiao.domain.user.UserWithDraw;

/**
 * 提现交易模块功能接口定义
 * @author kouyi 2017-11-03
 */
public interface UserWithDrawMapper {

    /**
     * 保存提现交易信息
     * @param record
     * @return
     */
    int insertUserWith(UserWithDraw record);

}