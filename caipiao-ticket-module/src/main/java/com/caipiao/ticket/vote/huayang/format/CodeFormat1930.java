package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1720;
import com.caipiao.plugin.Lottery1930;
import com.caipiao.ticket.bean.CodeInfo;

/**
 * 竞彩足球总进球投注串转换
 * Created by kouyi on 2017/12/13.
 */
public class CodeFormat1930 extends CodeFormat1720 {

    public static void main(String[] args) throws Exception {
        String codes="JQS|20171206001=0/1,20171206002=2,20171206003=6/7|3*1";
        Lottery1930 gcp = new Lottery1930();
        CodeFormat1930 gcc = new CodeFormat1930();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(24d);
        t.setMultiple(3);
        t.setCodes(codes);
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
        System.out.println(bean.getCodeCopy());
    }
}
