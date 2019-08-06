package com.caipiao.ticket.vote.jimi.format;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1940;
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
    public CodeInfo getCodeBean(SchemeTicket ticket, GamePluginAdapter plugin){
        CodeInfo cb = new CodeInfo();
        try {
            GameCastCode gcc = plugin.parseGameCastCode(ticket.getCodes());
            if(gcc.getCastMoney() * ticket.getMultiple() != ticket.getMoney().intValue()) {//判断金额是否一致，防止票表被修改
                cb.setErrorCode(3);
            } else {
                StringBuffer voteCodes = new StringBuffer();
                String[] codes = PluginUtil.splitter(ticket.getCodes(), SX_SPLIT);
                voteCodes.append(codes[2].equals("1*1")?"0":codes[2]);
                voteCodes.append(JH_SPLIT);
                String[] matchs = PluginUtil.splitter(codes[1], DH_SPLIT);
                for(int x = 0; x < matchs.length; x++) {
                    String[] m = PluginUtil.splitter(matchs[x], DY_SPLIT);
                    voteCodes.append("B");
                    voteCodes.append(m[0]);
                    voteCodes.append(DH_SPLIT);
                    voteCodes.append(m[1].replaceAll("01", "51")
                            .replaceAll("02", "52")
                            .replaceAll("03", "53")
                            .replaceAll("04", "54")
                            .replaceAll("05", "55")
                            .replaceAll("06", "56")
                            .replaceAll("11", "01")
                            .replaceAll("12", "02")
                            .replaceAll("13", "03")
                            .replaceAll("14", "04")
                            .replaceAll("15", "05")
                            .replaceAll("16", "06"));
                    if(x != matchs.length -1) {
                        voteCodes.append(SG_SPLIT);
                    }
                }
                cb.setCode(voteCodes.toString());
                cb.setOrderId(ticket.getTicketId());
                cb.setMoney(ticket.getMoney().intValue());
                cb.setMultiple(ticket.getMultiple());
                cb.setZhuShu(cb.getMoney()/cb.getMultiple()/2);
                cb.setPass(codes[2]);
            }
        } catch (Exception e) {
            logger.error("[吉米竞彩提票]-> 格式化票信息异常 票号=" + ticket.getTicketId(), e);
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
    }
}
