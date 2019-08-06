package com.caipiao.api.test.user;

import com.caipiao.common.util.BeanUtils;
import com.caipiao.domain.user.User;
import com.caipiao.domain.vo.UserVo;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by kouyi on 2017/9/29.
 */
public class TestUser {

    public static void main(String[] args) {
        User a = new User();
        a.setMobile("13636633461");
        a.setId(100l);
        UserVo b = new UserVo();
        try {
            BeanUtils.copyProperties(b, a);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(b.getMobile());
        System.out.println(b.getId());
        System.out.println(b.getBalance());
    }

}

