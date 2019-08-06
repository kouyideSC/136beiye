package com.caipiao.ticket.vote.jimi.format;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1010;
import com.caipiao.plugin.Lottery1700;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 双色球注串转换
 * Created by kouyi on 2017/12/18.
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
                String redDan = getSource(gcc.getFirst(), 0, DH_SPLIT).replaceAll(",","/");
                String blueDan = getSource(gcc.getThird(), 0, DH_SPLIT).replaceAll(",","/");
                if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_DANTUO) {//胆拖
                    if (gccs.length > 1) {
                        cb.setErrorCode(3);//复杂票注数不能超过1注
                        return cb;
                    }
                    saleType = "3";
                    String redTuo = getSource(gcc.getSecond(), 0, DH_SPLIT).replaceAll(",","/");
                    if (StringUtil.isNotEmpty(redDan)) {
                        sb.append(redDan).append(SG_SPLIT).append(redTuo);
                    } else {
                        sb.append(redTuo);
                    }
                    sb.append(DH_SPLIT);
                    sb.append(blueDan);
                } else if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_SINGLE) {//单式
                    saleType = "1";
                    sb.append(redDan).append(SG_SPLIT).append(blueDan);
                } else if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_MULTI) {//复式
                    if (gccs.length > 1) {
                        cb.setErrorCode(3);//复杂票注数不能超过1注
                        return cb;
                    }
                    saleType = "2";
                    sb.append(redDan).append(SG_SPLIT).append(blueDan);
                }
                if(i != gccs.length - 1) {
                    sb.append(DH_SPLIT);
                }
                sumMoney += gcc.getCastMoney();
            }

            if(sumMoney * ticket.getMultiple() != ticket.getMoney().intValue()) {//判断金额是否一致，防止票表被修改
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
        String codes="06,09,10,13,22,24|02:1:1;06,10,12,13,23,24|03:1:1;09,11,15,16,27,29|04:1:1";
        Lottery1010 gcp = new Lottery1010();
        CodeFormat1010 gcc = new CodeFormat1010();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(6d);
        t.setMultiple(1);
        t.setCodes(codes);
        t.setLotteryId("1010");
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }

}
