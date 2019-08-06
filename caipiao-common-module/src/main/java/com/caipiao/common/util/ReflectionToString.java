package com.caipiao.common.util;

import com.caipiao.domain.base.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 对象属性值打印工具类
 * Created by kouyi on 2017/10/23.
 */
public class ReflectionToString {
    private static Logger logger = LoggerFactory.getLogger(ReflectionToString.class);

    /**
     * 格式化打印对象中非空参数值
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return getFields(obj.getClass(), obj);
    }


    /**
     * 递归打印对象中非空参数值
     * @param claz
     * @return
     */
    public static String getFields(Class claz, Object obj) {
        try {
            if(StringUtil.isEmpty(claz)) {
                return null;
            }
            StringBuffer buffer = new StringBuffer("[");
            buffer.append("\n");
            Field[] fields = claz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if(StringUtil.isEmpty(field.get(obj)) || field.getName().equals("serialVersionUID") || field.getName().equals("filterColumn")) {
                    continue;
                }
                buffer.append(field.getName() + "=" +field.get(obj));
                buffer.append("\n");
            }

            //递归父类对象属性
            Class superclass = claz.getSuperclass();
            if (superclass != Object.class) {
                return getFields(superclass, obj);
            }

            buffer.append("]");
            if(buffer.toString().length() <= 2) {
                return null;
            }
            return buffer.toString();
        } catch (Exception e) {
            logger.error("递归格式化打印对象字符串异常：" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取请求全部请求参数并格式化
     * @param request
     * @return
     */
    public static String getParams(HttpServletRequest request) {
        try {
            if (request == null) {
                return null;
            }
            StringBuffer buffer = new StringBuffer("[");
            buffer.append("\n");
            Enumeration enumer = request.getParameterNames();
            while (enumer.hasMoreElements()) {
                String name = (String) enumer.nextElement();
                buffer.append(name + "=" + request.getParameter(name));
                buffer.append("\n");
            }
            buffer.append("]");
            if (buffer.toString().length() <= 2) {
                return null;
            }
            return buffer.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        UserBean bean = new UserBean();
        bean.setMobile("13636633461");
        bean.setContent("aaaaaaaaaaa");
        bean.setDevice("bbbbbbbbbb");
        bean.setKey("ccccccccc");
        bean.setPassword("dddddddddd");
        bean.setNickName("eeeeeeeeee");
        System.out.println(toString(bean));
    }
}
