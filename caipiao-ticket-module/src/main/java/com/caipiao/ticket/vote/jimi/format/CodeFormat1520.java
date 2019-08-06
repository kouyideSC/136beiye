package com.caipiao.ticket.vote.jimi.format;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1030;
import com.caipiao.plugin.Lottery1520;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 排列5注串转换
 * Created by kouyi on 2017/12/19.
 */
public class CodeFormat1520 extends AbstractCodeFormat {
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
                    saleType = "2";
                    String[] cs = PluginUtil.splitter(code, ",");
                    StringBuffer buffer = new StringBuffer();
                    for (int n = 0; n < cs.length; n++) {
                        if (cs[n].length() > 1) {
                            for (int y = 0; y < cs[n].length(); y++) {
                                buffer.append(cs[n].charAt(y));
                                if (y != cs[n].length() - 1) {
                                    buffer.append("/");
                                }
                            }
                        } else {
                            buffer.append(cs[n]);
                        }
                        if (n != cs.length - 1) {
                            buffer.append("//");
                        }
                    }
                    code = buffer.toString();
                    sb.append(code);
                    compound = true;
                } else {
                    saleType = "1";
                    sb.append(code.replaceAll(",", "/"));
                }
                if (compound && i > 0) {
                    cb.setErrorCode(3);//复杂票注数不能超过1注
                    return cb;
                }
                if (i != gccs.length - 1) {
                    sb.append(DH_SPLIT);
                }
                sumMoney += gcc.getCastMoney();
            }

            if (sumMoney * ticket.getMultiple() != ticket.getMoney().intValue()) {//判断金额是否一致，防止票表被修改
                cb.setErrorCode(3);
            } else {
                cb.setCode(saleType + JH_SPLIT + sb.toString());
                cb.setOrderId(ticket.getTicketId());
                cb.setMoney(ticket.getMoney().intValue());
                cb.setMultiple(ticket.getMultiple());
                cb.setZhuShu(cb.getMoney() / cb.getMultiple() / 2);
            }
        } catch (Exception e) {
            logger.error("[吉米慢频提票]-> 格式化票信息异常 票号=" + ticket.getTicketId(), e);
            cb.setErrorCode(3);
        }
        return cb;
    }

    public static void main(String[] args) throws Exception {
        String codes="1,2,0,1,2:1:1;1,2,1,2,4:1:1";
        Lottery1520 gcp = new Lottery1520();
        CodeFormat1520 gcc = new CodeFormat1520();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(4d);
        t.setMultiple(1);
        t.setCodes(codes);
        t.setLotteryId("1520");
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }
}
