package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1920;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 竞彩足球半全场投注串转换
 * Created by kouyi on 2017/12/13.
 */
public class CodeFormat1920 extends CodeFormat1720 {

    public static void main(String[] args) throws Exception {
        String codes="BQC|20171206001=3-3/3-1,20171206002=1-3/1-1,20171206003=0-3/0-0|3*1";
        Lottery1920 gcp = new Lottery1920();
        CodeFormat1920 gcc = new CodeFormat1920();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(48d);
        t.setMultiple(3);
        t.setCodes(codes);
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
        System.out.println(bean.getCodeCopy());
    }

}
