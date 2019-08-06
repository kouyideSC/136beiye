package com.caipiao.common.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * spring实例工具类
 * @author  mcdog
 */
public class SpringBeanUtil implements ApplicationContextAware
{
    private static ApplicationContext applicationContext = null;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        SpringBeanUtil.applicationContext = applicationContext;
    }

    public static Object getBeanByName(String beanName)
    {
        if (applicationContext == null)
        {
            return null;
        }
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> type)
    {
        return applicationContext.getBean(type);
    }
}