package com.caipiao.ticket.vote.jimi.format;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1710;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.caipiao.ticket.util.NuoMiTicketUtil;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 竞彩篮球混投投注串转换
 * Created by kouyi on 2017/12/13.
 */
public class CodeFormat1710 extends AbstractCodeFormat {
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
                    String[] n = PluginUtil.splitter(m[0], DF_SPLIT);
                    voteCodes.append("B");
                    voteCodes.append(n[0]);
                    voteCodes.append(DH_SPLIT);
                    if(n[1].equals(LotteryConstants.JCWF_PREFIX_SF) || n[1].equals(LotteryConstants.JCWF_PREFIX_RFSF)) {//胜负和让分胜负
                        voteCodes.append(m[1].replaceAll("3","1"));
                    }
                    else if(n[1].equals(LotteryConstants.JCWF_PREFIX_SFC)) {//胜分差
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
                    }
                    else {
                        voteCodes.append(m[1].replaceAll("3", "1").replaceAll("0", "2"));
                    }
                    voteCodes.append(HG_SPLIT);
                    voteCodes.append(JiMiTicketUtil.getLotteryTypeMap(LotteryConstants.jcWfPrefixPlayIdMaps.get(ticket.getLotteryId()+n[1])));
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
        String codes="HH|20171206302>SFC=11/16,20171206303>DXF=3,20171206304>SF=3|3*1";
        Lottery1710 gcp = new Lottery1710();
        CodeFormat1710 gcc = new CodeFormat1710();
        SchemeTicket t = new SchemeTicket();
        t.setMoney(4d);
        t.setMultiple(1);
        t.setCodes(codes);
        t.setLotteryId("1710");
        CodeInfo bean=gcc.getCodeBean(t, gcp);
        System.out.println(bean.getCode());
    }

}
