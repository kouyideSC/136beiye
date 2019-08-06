package com.caipiao.service.test.user;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.domain.base.SchemeBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.jjyh.MatchInfo;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.exception.ServiceException;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kouyi on 2018/5/16.
 */
public class Test {
    public static void main(String[] args) {
        String codes = "或鸡蛋和高科技";
        System.out.println(codes.substring(0,4));
        System.out.println("1*1,2*1".replace("1*1", "单关").replace("*", "串"));
        System.out.println("20189203".substring(2));

        List<Long> dl = new ArrayList<>();
        dl.add(18l);
        Long sl = 18l;
        if(dl.contains(sl)) {
            System.out.println(11111);
        }
    }

}
