package com.caipiao.dao.user;

import com.caipiao.domain.user.UserCookie;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Cookie模块功能接口定义
 * @author kouyi 2018-03-16
 */
public interface UserCookieMapper {

    /**
     * 初始化用户Cookie信息
     * @param cookie
     * @return
     */
    void insertUserCookie(UserCookie cookie) throws Exception;

    /**
     * 根据用户编号查询Cookie信息
     * @param cookie
     * @param tkey
     * @return
     */
    UserCookie queryUserCookieInfoByCookie(@Param("cookie") String cookie, @Param("tkey") String tkey) throws Exception;

    /**
     * 更新用户Cookie信息
     * @param cookie
     * @return
     */
    void updateUserCookie(UserCookie cookie) throws Exception;
}
