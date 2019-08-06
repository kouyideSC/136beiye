package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1010;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.HuaYangTicketUtil;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 双色球注串转换
 * Created by kouyi on 2017/12/20.
 */
public class CodeFormat1010 extends AbstractCodeFormat {
    /**
     * 格式化投注信息为出票商格式
     * @param ticket
     * @param plugin
     * @return
     */
    public CodeInfo getCodeBean(SchemeTicket ticket, GamePluginAdapter plugin){
        CodeInfo cb = new CodeInfo();
        try {
            GameCastCode[] gccs = plugin.parseGameCastCodes(ticket.getCodes());
            if(gccs.length > 5) {
                cb.setErrorCode(3);//单张票不能超过5注单式票
                return cb;
            }

            int sumMoney = 0;
            String saleType = "";
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < gccs.length; i++) {
                GameCastCode gcc = gccs[i];
                String redDan = getSource(gcc.getFirst(), 0, DH_SPLIT).replaceAll(",","");
                String blueDan = getSource(gcc.getThird(), 0, DH_SPLIT).replaceAll(",","");
                if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_DANTUO) {//胆拖
                    if (gccs.length > 1) {
                        cb.setErrorCode(3);//复杂票注数不能超过1注
                        return cb;
                    }
                    saleType = "2";
                    String redTuo = getSource(gcc.getSecond(), 0, DH_SPLIT).replaceAll(",","");
                    if (StringUtil.isNotEmpty(redDan)) {
                        sb.append(redDan).append(XH_SPLIT).append(redTuo);
                    } else {
                        sb.append(redTuo);
                    }
                    sb.append(SX_SPLIT);
                    sb.append(blueDan);
                } else if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_SINGLE) {//单式
                    saleType = "0";
                    sb.append(redDan).append(SX_SPLIT).append(blueDan);
                } else if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_MULTI) {//复式
                    if (gccs.length > 1) {
                        cb.setErrorCode(3);//复杂票注数不能超过1注
                        return cb;
                    }
                    saleType = "1";
                    sb.append(redDan).append(SX_SPLIT).append(blueDan);
                }
                if(i != gccs.length - 1) {
                    sb.append(JH_SPLIT);
                }
                sumMoney += gcc.getCastMoney();
            }

            if(sumMoney * ticket.getMultiple() != ticket.getMoney().intValue()) {//判断金额是否一致，防止票表被修改
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
        String codes="02,03,06,14,13,15|03,11:1:2";
        Lottery1010 gcp = new Lottery1010();
        CodeFormat1010 gcc = new CodeFormat1010();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(4d);
        t.setMultiple(1);
        t.setCodes(codes);
        t.setLotteryId("1010");
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }

}
