package com.caipiao.dao.user;

import com.caipiao.domain.user.UserToken;
import org.apache.ibatis.annotations.Param;

/**
 * 用户模块功能接口定义
 * @author kouyi 2017-09-21
 */
public interface UserTokenMapper {

    /**
     * 初始化用户token信息
     * @param token
     * @return
     */
    void insertUserToken(UserToken token) throws Exception;

    /**
     * 根据用户编号查询token信息
     * @param token
     * @param tkey
     * @return
     */
    UserToken queryUserTokenInfoByToken(@Param("token")String token, @Param("tkey")String tkey) throws Exception;

    /**
     * 更新用户token信息
     * @param token
     * @return
     */
    void updateUserToken(UserToken token) throws Exception;
}
