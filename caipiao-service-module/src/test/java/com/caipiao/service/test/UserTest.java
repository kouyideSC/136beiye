package com.caipiao.service.test;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.SchemeBean;
import com.caipiao.domain.base.UserBean;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.jjyh.JjyhSchemeInfo;
import com.caipiao.domain.jjyh.JjyhTwo;
import com.caipiao.domain.jjyh.MatchInfo;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kouyi on 2017/11/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class UserTest {
    @Autowired
    private UserService userService;
    @Autowired
    private SchemeService schemeService;
    @Autowired
    private ActivityService activityService;

    @Test
    public void testCreateFileData() throws Exception{
        //List<Long> list = activityService.queryActivityJoinUser(4);
        //System.out.println(list.size());
        /*UserBean bean = new UserBean();
        bean.setPassword("55555rrrr");
        bean.setMobile("13636633461");
        bean.setContent("978062");
        ResultBean result = new ResultBean();
        try {
            userService.resetUserPasswd(bean, result);
            System.out.println(result.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void testSchemeJJYH() {
        try {
            SchemeBean bean = new SchemeBean();
            bean.setLid("1700");
            bean.setMoney(50d);
            //HH|20180517001>SPF=3/1+RQSPF=1,20180517002>SPF=1+RQSPF=1/0|2*1
            bean.setTzcontent("HH|20180619015>BQC=3-0/1-1/1-0/0-3/0-1|1*1");
            ResultBean result = new ResultBean();
            schemeService.jcJJYHCalculate(bean, result);
            System.out.println(JsonUtil.JsonObject(result, JjyhTwo.filter));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
