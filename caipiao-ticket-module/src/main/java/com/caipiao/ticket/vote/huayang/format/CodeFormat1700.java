package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1700;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.HuaYangTicketUtil;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 竞彩足球混投投注串转换
 * Created by kouyi on 2017/12/13.
 */
public class CodeFormat1700 extends AbstractCodeFormat {
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
                    String[] n = PluginUtil.splitter(m[0], DF_SPLIT);
                    if (Long.valueOf(n[0]) < min) {
                        min = Long.valueOf(n[0]);
                    }
                    if (Long.valueOf(n[0]) > max) {
                        max = Long.valueOf(n[0]);
                    }
                    voteCodes.append(HuaYangTicketUtil.getLotteryTypeMap(LotteryConstants.jcWfPrefixPlayIdMaps.get(ticket.getLotteryId()+n[1])));
                    voteCodes.append(JH_SPLIT);
                    voteCodes.append(n[0].substring(2, n[0].length()-3));
                    voteCodes.append(HG_SPLIT);
                    voteCodes.append(n[0].substring(8));
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
        String codes="HH|20171206001>JQS=2/4,20171206002>RQSPF=3/1,20171206003>BQC=3-1,20171206006>CBF=3:1/9:0|4*1";
        Lottery1700 gcp = new Lottery1700();
        CodeFormat1700 gcc = new CodeFormat1700();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(16d);
        t.setMultiple(1);
        t.setCodes(codes);
        t.setLotteryId("1700");
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }

}
