package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1910;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 竞彩足球猜比分投注串转换
 * Created by kouyi on 2017/12/13.
 */
public class CodeFormat1910 extends CodeFormat1720 {

    public static void main(String[] args) throws Exception {
        String codes="CBF|20171206001=1:0/9:0,20171206002=0:0/9:9|2*1";
        Lottery1910 gcp = new Lottery1910();
        CodeFormat1910 gcc = new CodeFormat1910();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(24d);
        t.setMultiple(3);
        t.setCodes(codes);
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
        System.out.println(bean.getCodeCopy());
    }

}
