package com.caipiao.common.util;

import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 重写BeanUtils的copyProperties方法支持对象拷贝时时间属性空值转换
 * Created by kouyi on 2017/10/23.
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
    private BeanUtils() {
    }

    static {
        //注册sql|util.date|sql.timestamp的转换器，即允许BeanUtils.copyProperties时的源目标的util类型的值允许为空
        ConvertUtils.register(new org.apache.commons.beanutils.converters.SqlDateConverter(null), java.sql.Date.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.SqlDateConverter(null), java.util.Date.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.SqlTimestampConverter(null), java.sql.Timestamp.class);
    }

    public static void copyProperties(Object target, Object source)
            throws InvocationTargetException, IllegalAccessException {
        org.apache.commons.beanutils.BeanUtils.copyProperties(target, source);
    }
}
