package com.caipiao.service.config;

import com.caipiao.dao.common.ParameterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 初始化系统参数守护线程-2秒reload一次
 * Created by kouyi on 2017/9/25.
 */
public class InitParameterThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(InitParameterThread.class);

    private ParameterMapper paramDao;
    public InitParameterThread(ParameterMapper paramDao) {
        this.paramDao = paramDao;
    }

    public void run() {
        while (true) {
            try {
                this.waitTime(2);
                new SysConfig().initialize(paramDao);
            } catch (Exception e) {
                logger.error("加载系统参数线程异常", e);
            }
        }
    }

    public synchronized void waitTime(int minute) {
        try {
            wait(minute * 60 * 1000);
        } catch (Exception e) {
            logger.error("系统参数线程wait异常", e);
        }
    }
}
