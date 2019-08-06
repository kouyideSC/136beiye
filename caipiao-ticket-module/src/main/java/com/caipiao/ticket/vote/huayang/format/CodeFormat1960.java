package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1960;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 竞彩胜分差投注串转换
 * Created by kouyi on 2017/12/14.
 */
public class CodeFormat1960 extends AbstractCodeFormat {
    /**
     * 格式化投注信息为出票商格式
     * @param ticket
     * @param plugin
     * @return
     */
    public CodeInfo getCodeBean(SchemeTicket ticket, GamePluginAdapter plugin) {
        CodeInfo cb = new CodeInfo();
        try {
            GameCastCode gcc = plugin.parseGameCastCode(ticket.getCodes());
            if (gcc.getCastMoney() * ticket.getMultiple() != ticket.getMoney().intValue()) {//判断金额是否一致，防止票表被修改
                cb.setErrorCode(3);
            } else {
                long min = Long.MAX_VALUE;
                long max = Long.MIN_VALUE;
                StringBuffer voteCodes = new StringBuffer();
                String[] codes = PluginUtil.splitter(ticket.getCodes(), SX_SPLIT);
                String[] matchs = PluginUtil.splitter(codes[1], DH_SPLIT);
                for (int x = 0; x < matchs.length; x++) {
                    String[] m = PluginUtil.splitter(matchs[x], DY_SPLIT);
                    if (Long.valueOf(m[0]) < min) {
                        min = Long.valueOf(m[0]);
                    }
                    if (Long.valueOf(m[0]) > max) {
                        max = Long.valueOf(m[0]);
                    }
                    voteCodes.append(m[0].substring(0, m[0].length() - 3));
                    voteCodes.append(HG_SPLIT);
                    voteCodes.append(m[0].substring(8));
                    voteCodes.append("(");
                    voteCodes.append(m[1].replaceAll("\\/", DH_SPLIT)
                            .replaceAll("01", "1")
                            .replaceAll("02", "2")
                            .replaceAll("03", "3")
                            .replaceAll("04", "4")
                            .replaceAll("05", "5")
                            .replaceAll("06", "6")
                            .replaceAll("11", "7")
                            .replaceAll("12", "8")
                            .replaceAll("13", "9")
                            .replaceAll("14", "10")
                            .replaceAll("15", "11")
                            .replaceAll("16", "12"));
                    voteCodes.append(")");
                    if (x != matchs.length - 1) {
                        voteCodes.append(FH_SPLIT);
                    }
                }
                cb.setCode(voteCodes.toString());
                cb.setCodeCopy(String.valueOf(min).substring(0, String.valueOf(min).length() - 3) + HG_SPLIT + String.valueOf(min).substring(8) + JH_SPLIT + String.valueOf(max).substring(0, String.valueOf(max).length() - 3) + HG_SPLIT + String.valueOf(max).substring(8));
                cb.setOrderId(ticket.getTicketId());
                cb.setMoney(ticket.getMoney().intValue());
                cb.setMultiple(ticket.getMultiple());
                cb.setZhuShu(cb.getMoney() / cb.getMultiple() / 2);
                cb.setSaleCode("0");
                cb.setPass(codes[2]);
            }
        } catch (Exception e) {
            logger.error("[华阳竞彩提票]-> 格式化票信息异常 票号=" + ticket.getTicketId(), e);
            cb.setErrorCode(3);
        }
        return cb;
    }

    public static void main(String[] args) throws Exception {
        String codes="SFC|20171206302=11/16,20171206304=11/01|2*1";
        Lottery1960 gcp = new Lottery1960();
        CodeFormat1960 gcc = new CodeFormat1960();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(24d);
        t.setMultiple(3);
        t.setCodes(codes);
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
        System.out.println(bean.getCodeCopy());
    }
}
