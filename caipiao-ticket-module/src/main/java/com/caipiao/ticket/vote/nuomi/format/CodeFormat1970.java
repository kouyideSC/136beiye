package com.caipiao.ticket.vote.nuomi.format;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1970;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 竞彩大小分投注串转换
 * Created by kouyi on 2017/12/13.
 */
public class CodeFormat1970 extends AbstractCodeFormat {

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
                String[] matchs = PluginUtil.splitter(codes[1], DH_SPLIT);
                for(int x = 0; x < matchs.length; x++) {
                    String[] m = PluginUtil.splitter(matchs[x], DY_SPLIT);
                    voteCodes.append(m[0]);
                    voteCodes.append("(");
                    voteCodes.append(m[1].replaceAll("\\/", DH_SPLIT).replaceAll("3","1").replaceAll("0","2"));
                    voteCodes.append(")");
                    if(x != matchs.length -1) {
                        voteCodes.append(SX_SPLIT);
                    } else {
                        voteCodes.append(JH_SPLIT);
                    }
                }
                cb.setCode(voteCodes.toString());
                cb.setOrderId(ticket.getTicketId());
                cb.setMoney(ticket.getMoney().intValue());
                cb.setMultiple(ticket.getMultiple());
                cb.setPass(codes[2]);
            }
        } catch (Exception e) {
            logger.error("[糯米竞彩提票]-> 格式化票信息异常 票号=" + ticket.getTicketId(), e);
            cb.setErrorCode(3);
        }
        return cb;
    }

    public static void main(String[] args) throws Exception {
        String codes="DXF|20171206302=3/0,20171206303=3/0|2*1";
        Lottery1970 gcp = new Lottery1970();
        CodeFormat1970 gcc = new CodeFormat1970();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(8d);
        t.setMultiple(1);
        t.setCodes(codes);
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }

}
