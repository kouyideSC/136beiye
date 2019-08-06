package com.caipiao.common.encrypt;

import com.caipiao.common.constants.KeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by pc on 2017/10/26.
 */
public class AuthSign {
    private static Logger logger = LoggerFactory.getLogger(AuthSign.class);

    /**
     * 平台普通用户登录验证签名
     * 签名规则：
     * a_str=所有的参数名排序后 按照key=value方式 使用&符号拼接
     * str=a_str&key=xxxx
     * sign=md5(str)
     * @param request
     * @return
     */
    public static boolean checkSign(HttpServletRequest request) {
        try {
            String appId = request.getParameter(KeyConstants.APPID);
            if (!KeyConstants.loginKeys.containsKey(appId)) {
                logger.error("[签名验证] appid验证不通过!");
                return false;
            }

            String clientSign = "";//客户端传递的签名串
            List<String> params = new ArrayList<String>();
            Enumeration em = request.getParameterNames();
            while (em.hasMoreElements()) {
                String name = em.nextElement().toString();
                if ("sign".equals(name)) {
                    clientSign = request.getParameter(name);
                    continue;
                }
                params.add(name);
            }
            Collections.sort(params, Collections.<String>reverseOrder());

            StringBuffer buffer = new StringBuffer();
            for (String pm : params) {
                buffer.append(pm);
                buffer.append("=");
                buffer.append(request.getParameter(pm));
                buffer.append("&");
            }
            buffer.append(KeyConstants.APPKEY);
            buffer.append("=");
            buffer.append(KeyConstants.loginKeys.get(appId));
            String serverSign = MD5.md5(buffer.toString());
            if (clientSign.equalsIgnoreCase(serverSign)) {
                logger.debug("[签名验证] 验证通过!");
                return true;
            }
        } catch (Exception e) {
            logger.error("[签名验证] 发生异常", e);
        }
        return false;
    }

    /**
     * 渠道用户登录验证签名
     * 签名规则：
     * a_str=所有的参数名排序后 按照key=value方式 使用&符号拼接
     * str=a_str&key=xxxx
     * sign=md5(str)
     * @param request
     * @return
     */
    public static boolean checkSignChannel(HttpServletRequest request, String authKey) {
        try {
            String clientSign = "";//客户端传递的签名串
            List<String> params = new ArrayList<String>();
            Enumeration em = request.getParameterNames();
            while (em.hasMoreElements()) {
                String name = em.nextElement().toString();
                if ("sign".equals(name)) {
                    clientSign = request.getParameter(name);
                    continue;
                }
                params.add(name);
            }
            Collections.sort(params, Collections.<String>reverseOrder());

            StringBuffer buffer = new StringBuffer();
            for (String pm : params) {
                buffer.append(pm);
                buffer.append("=");
                buffer.append(request.getParameter(pm));
                buffer.append("&");
            }
            buffer.append(KeyConstants.APPKEY);
            buffer.append("=");
            buffer.append(authKey);
            String serverSign = MD5.md5(buffer.toString());
            if (clientSign.equalsIgnoreCase(serverSign)) {
                logger.debug("[签名验证] 验证通过!");
                return true;
            }
        } catch (Exception e) {
            logger.error("[签名验证] 发生异常", e);
        }
        return false;
    }
}
