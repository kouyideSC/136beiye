package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1810;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 任九注串转换
 * Created by kouyi on 2017/12/19.
 */
public class CodeFormat1810 extends AbstractCodeFormat {
    @Override
    public CodeInfo getCodeBean(SchemeTicket ticket, GamePluginAdapter plugin) {
        CodeInfo cb = new CodeInfo();
        try {
            GameCastCode[] gccs = plugin.parseGameCastCodes(ticket.getCodes());
            if (gccs.length > 5) {
                cb.setErrorCode(3);//单张票不能超过5注单式票
                return cb;
            }

            int sumMoney = 0;
            String saleType = "";
            boolean compound = false;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < gccs.length; i++) {
                GameCastCode gcc = gccs[i];
                String code = gcc.getSourceCode().split(MH_SPLIT)[0];
                if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_MULTI) {
                    saleType = "1";
                    compound = true;
                    sb.append(code.replaceAll(",", XH_SPLIT));
                } else {
                    saleType = "0";
                    sb.append(code.replaceAll(",", ""));
                }
                if (compound && i > 0) {
                    cb.setErrorCode(3);//复杂票注数不能超过1注
                    return cb;
                }
                if (i != gccs.length - 1) {
                    sb.append(JH_SPLIT);
                }
                sumMoney += gcc.getCastMoney();
            }

            if (sumMoney * ticket.getMultiple() != ticket.getMoney().intValue()) {//判断金额是否一致，防止票表被修改
                cb.setErrorCode(3);
            } else {
                cb.setCode(sb.toString());
                cb.setOrderId(ticket.getTicketId());
                cb.setMoney(ticket.getMoney().intValue());
                cb.setMultiple(ticket.getMultiple());
                cb.setZhuShu(cb.getMoney() / cb.getMultiple() / 2);
                cb.setPlayType("0");
                cb.setSaleCode(saleType);
            }
        } catch (Exception e) {
            logger.error("[华阳慢频提票]-> 格式化票信息异常 票号=" + ticket.getTicketId(), e);
            cb.setErrorCode(3);
        }
        return cb;
    }

    public static void main(String[] args) throws Exception {
        String codes="3,#,3,1,3,3,#,0,1,3,1,#,#,#:1:1;3,0,3,1,3,3,1,#,#,3,1,#,#,#:1:1;3,0,3,1,#,3,1,0,#,3,1,#,#,#:1:1;3,0,3,1,3,3,1,0,#,3,#,#,#,#:1:1;3,0,3,1,3,#,1,#,1,3,1,#,#,#:1:1";
        Lottery1810 gcp = new Lottery1810();
        CodeFormat1810 gcc = new CodeFormat1810();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(10d);
        t.setMultiple(1);
        t.setCodes(codes);
        t.setLotteryId("1810");
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }
}
