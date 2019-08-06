package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1720;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 竞彩足球胜平负投注串转换
 * Created by kouyi on 2017/12/21.
 */
public class CodeFormat1720 extends AbstractCodeFormat {
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
                long min = Long.MAX_VALUE;
                long max = Long.MIN_VALUE;
                StringBuffer voteCodes = new StringBuffer();
                String[] codes = PluginUtil.splitter(ticket.getCodes(), SX_SPLIT);
                String[] matchs = PluginUtil.splitter(codes[1], DH_SPLIT);
                for(int x = 0; x < matchs.length; x++) {
                    String[] m = PluginUtil.splitter(matchs[x], DY_SPLIT);
                    if (Long.valueOf(m[0]) < min) {
                        min = Long.valueOf(m[0]);
                    }
                    if (Long.valueOf(m[0]) > max) {
                        max = Long.valueOf(m[0]);
                    }
                    voteCodes.append(m[0].substring(2, m[0].length()-3));
                    voteCodes.append(HG_SPLIT);
                    voteCodes.append(m[0].substring(8));
                    voteCodes.append("(");
                    voteCodes.append(m[1].replaceAll("\\/",DH_SPLIT).replaceAll("\\:","").replaceAll("\\-",""));
                    voteCodes.append(")");
                    if(x != matchs.length -1) {
                        voteCodes.append(FH_SPLIT);
                    }
                }
                cb.setCode(voteCodes.toString());
                cb.setCodeCopy(String.valueOf(min).substring(2, String.valueOf(min).length()-3) + HG_SPLIT + String.valueOf(min).substring(8) + JH_SPLIT + String.valueOf(max).substring(2, String.valueOf(max).length()-3) + HG_SPLIT + String.valueOf(max).substring(8));
                cb.setOrderId(ticket.getTicketId());
                cb.setMoney(ticket.getMoney().intValue());
                cb.setMultiple(ticket.getMultiple());
                cb.setZhuShu(cb.getMoney()/cb.getMultiple()/2);
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
        String codes="SPF|20171206002=3/0,20171206003=3/1/0,20171206004=3|1*1";
        Lottery1720 gcp = new Lottery1720();
        CodeFormat1720 gcc = new CodeFormat1720();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(24d);
        t.setMultiple(2);
        t.setCodes(codes);
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
        System.out.println(bean.getCodeCopy());
    }

}
