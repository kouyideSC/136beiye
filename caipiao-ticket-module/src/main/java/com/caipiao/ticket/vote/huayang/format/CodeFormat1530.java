package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1030;
import com.caipiao.plugin.Lottery1530;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 排列3注串转换
 * Created by kouyi on 2017/12/18.
 */
public class CodeFormat1530 extends AbstractCodeFormat {
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
                String code = PluginUtil.splitter(gcc.getSourceCode(), MH_SPLIT)[0];
                if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_BAOHAO) {//包号
                    if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION3) {
                        saleType = "5";
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION6) {
                        saleType = "4";
                    }
                    code = code.replaceAll(",", "");
                    sb.append(code);
                } else if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_HESHU) {//和值
                    if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_SINGLE3) {//直选
                        saleType = "2";
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION3) {//组三
                        saleType = "6";
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION6) {//组六
                        saleType = "6";
                    }
                    sb.append(code);
                } else if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_SINGLE || gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_MULTI) {// 单复式
                    if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_SINGLE3) {//直选
                        if (gcc.getCastMoney() > 2) {
                            saleType = "1";
                        } else {
                            saleType = "0";
                        }
                        code = code.replaceAll(",", XH_SPLIT);
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION3) {//组三
                        if (gcc.getCastMoney() == 2) {
                            saleType = "3";
                            code = code.replaceAll(",", "");
                        }
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION6) {//组六
                        if (gcc.getCastMoney() == 2) {
                            saleType = "3";
                            code = code.replaceAll(",", "");
                        }
                    }
                    sb.append(code);
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
        String codes="1,234,678:1:2";
        Lottery1530 gcp = new Lottery1530();
        CodeFormat1530 gcc = new CodeFormat1530();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(18d);
        t.setMultiple(1);
        t.setCodes(codes);
        t.setLotteryId("1053");
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }

}
