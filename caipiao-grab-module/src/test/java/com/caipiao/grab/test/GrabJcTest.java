package com.caipiao.grab.test;

import com.caipiao.grab.jc.task.JclqMatchTask;
import com.caipiao.grab.jc.task.JczqMatchTask;
import com.caipiao.grab.jc.task.gyjMatchTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 抓取竞彩数据测试类
 * Created by Kouyi on 2017/11/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class GrabJcTest {
    @Autowired
    private JczqMatchTask jczqMatchTask;
    @Autowired
    private JclqMatchTask jclqMatchTask;
    @Autowired
    private gyjMatchTask gyjMatchTask;

    @Test
    public void testGrabJczqData(){
        //jczqMatchTask.grabGwJczqMatch();//对阵
        jczqMatchTask.grabGwJczqMatchSp();//赔率
        //jczqMatchTask.grabGwJczqMatchResult();//赛果
        //gyjMatchTask.grabGwGjMatch();
        //gyjMatchTask.grabGwGyjMatch();
    }

    @Test
    public void testGrabJclqData() {
        //jclqMatchTask.grabGwJclqMatch();//对阵
        //jclqMatchTask.grabGwJclqMatchSp();//赔率
        //jclqMatchTask.grabGwJclqMatchResult();//赛果
    }
}
