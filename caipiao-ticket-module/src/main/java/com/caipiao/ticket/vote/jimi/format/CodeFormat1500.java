package com.caipiao.ticket.vote.jimi.format;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1010;
import com.caipiao.plugin.Lottery1500;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 大乐透注串转换
 * Created by kouyi on 2017/12/18.
 */
public class CodeFormat1500 extends AbstractCodeFormat {
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

            int money = 2;//单价
            int sumMoney = 0;
            String saleType = "";
            String zhuiJia = "0";
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
                    if (StringUtil.isNotEmpty(redTuo)) {
                        sb.append(redDan).append(SG_SPLIT).append(redTuo);
                    } else {
                        sb.append(redDan);
                    }
                    sb.append(DH_SPLIT);
                    String blueTuo = getSource(gcc.getFourth(), 0, DH_SPLIT).replaceAll(",","/");
                    if (StringUtil.isNotEmpty(blueTuo)) {
                        sb.append(blueDan).append(SG_SPLIT).append(blueTuo);
                    } else {
                        sb.append(blueDan);
                    }
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
                if (gcc.getPlayMethod() == Lottery1500.PM_ZHUIJIA) {
                    zhuiJia = "1";
                    money= 3;
                }
                if(i != gccs.length - 1) {
                    sb.append(DH_SPLIT);
                }
                sumMoney += gcc.getCastMoney();
            }

            if(sumMoney * ticket.getMultiple() != ticket.getMoney().intValue()) {//判断金额是否一致，防止票表被修改
                cb.setErrorCode(3);
            } else {
                cb.setCode(saleType + JH_SPLIT + sb.toString() + JH_SPLIT + zhuiJia);
                cb.setOrderId(ticket.getTicketId());
                cb.setMoney(ticket.getMoney().intValue());
                cb.setMultiple(ticket.getMultiple());
                cb.setZhuShu(cb.getMoney() / cb.getMultiple() / money);
            }
        } catch (Exception e) {
            logger.error("[吉米慢频提票]-> 格式化票信息异常 票号=" + ticket.getTicketId(), e);
            cb.setErrorCode(3);
        }
        return cb;
    }

    public static void main(String[] args) throws Exception {
        String codes="29,11,30,31,15|07,06:1:1;03,18,05,10,26|11,04:1:1;28,19,35,01,32|02,09:1:1;03,04,18,27,32|10,12:1:1;35,18,26,12,14|07,04:1:1";
        Lottery1500 gcp = new Lottery1500();
        CodeFormat1500 gcc = new CodeFormat1500();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(10d);
        t.setMultiple(1);
        t.setCodes(codes);
        t.setLotteryId("1500");
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }

}
