package com.caipiao.grab.test;

import com.caipiao.grab.jsbf.task.JsbfTask;
import com.caipiao.grab.task.CreateUpdateDataTask;
import com.caipiao.grab.util.JsbfUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 即时比分任务生成文件测试类
 * Created by Kouyi on 2017/11/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class JsbfTest {
    @Autowired
    private JsbfTask jsbfTask;

    @Test
    public void testJsbfData(){
        jsbfTask.grabHaoCaiMatch();
        //jsbfTask.createJsbfMatch();
        //jsbfTask.grabHaoCaiNextMatch();
        //jsbfTask.createDistoryPeriod();
        //jsbfTask.createCurrentJsbfMatch();
    }

}
