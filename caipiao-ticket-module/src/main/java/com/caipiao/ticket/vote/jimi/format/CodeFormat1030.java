package com.caipiao.ticket.vote.jimi.format;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1010;
import com.caipiao.plugin.Lottery1030;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 福彩3D注串转换
 * Created by kouyi on 2017/12/18.
 */
public class CodeFormat1030 extends AbstractCodeFormat {
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
                        saleType = "22";
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION6) {
                        saleType = "23";
                    }
                    code = code.replaceAll(",", "/");
                    sb.append(code);
                } else if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_HESHU) {//和值
                    if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_SINGLE3) {//直选
                        saleType = "31";
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION3) {//组三
                        saleType = "32";
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION6) {//组六
                        saleType = "33";
                    }
                    sb.append(code.length()==1 ? ("0"+code) : code);
                } else if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_SINGLE || gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_MULTI) {// 单复式
                    if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_SINGLE3) {//直选
                        if (gcc.getCastMoney() > 2) {
                            saleType = "21";
                            String[] cs = PluginUtil.splitter(code, ",");
                            StringBuffer buffer = new StringBuffer();
                            for(int x = 0; x < cs.length; x++) {
                                if(cs[x].length() > 1) {
                                    for(int y = 0; y < cs[x].length(); y++) {
                                        buffer.append(cs[x].charAt(y));
                                        if(y != cs[x].length()-1) {
                                            buffer.append("/");
                                        }
                                    }
                                } else {
                                    buffer.append(cs[x]);
                                }
                                if(x != cs.length - 1) {
                                    buffer.append("//");
                                }
                            }
                            code = buffer.toString();
                        } else {
                            saleType = "11";
                            code = code.replaceAll(",", "/");
                        }
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION3) {//组三
                        if (gcc.getCastMoney() == 2) {
                            saleType = "12";
                            code = code.replaceAll(",", "/");
                        }
                    } else if (gcc.getPlayMethod() == Lottery1030.TDPLAYTYPE_COMBINATION6) {//组六
                        if (gcc.getCastMoney() == 2) {
                            saleType = "13";
                            code = code.replaceAll(",", "/");
                        }
                    }
                    sb.append(code);
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
        String codes="2,5,7:3:1;1,4,7:3:1;2,4,6:3:1;1,5,6:3:1;2,4,7:3:1";
        Lottery1030 gcp = new Lottery1030();
        CodeFormat1030 gcc = new CodeFormat1030();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(10d);
        t.setMultiple(1);
        t.setCodes(codes);
        t.setLotteryId("1030");
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }

}
