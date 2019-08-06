package com.caipiao.admin.service.user;

import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台用户账户相关服务
 * Created by kouyi on 2017/11/25.
 */
@Service("userAccountService")
public class UserAccountService
{
    @Autowired
    private UserAccountMapper userAccountMapper;

    /**
     * 查询用户账户信息（后台管理）
     * @author kouyi
     */
    public UserAccount queryUserAccountInfoById(Long id) throws Exception
    {
        return userAccountMapper.queryUserAccountInfoByUserId(id);
    }

}
