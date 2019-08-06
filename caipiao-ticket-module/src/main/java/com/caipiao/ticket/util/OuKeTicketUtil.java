package com.caipiao.ticket.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.encrypt.DESCoder;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;
import com.caipiao.ticket.vote.base.BaseCastTicket;
import net.sf.json.JSONArray;
import org.apache.commons.collections.map.HashedMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 欧克出票工具类
 * Created by Kouyi on 2018/11/03.
 */
public class OuKeTicketUtil extends BaseCastTicket {
    protected HashMap<String, GamePluginAdapter> pluginMap = new HashMap<String, GamePluginAdapter>();//共享玩法工具
    protected HashMap<String, AbstractCodeFormat> formatMap = new HashMap<String, AbstractCodeFormat>();//共享投注串转换工具
    protected static HashMap<String, String> resultCode = new HashMap<String, String>();//错误码
    protected static HashMap<String, String> lotteryPlayType = new HashMap<String, String>();//出票商玩法编码
    public static HashMap<String, String> voteLotteryPlayType = new HashMap<String, String>();//出票商玩法编码对应网站玩法编码
    public static HashMap<String, String> voteChooseType = new HashMap<String, String>();//出票商投注选项
    protected static HashMap<String, String> passPlayType = new HashMap<String, String>();//串关方式编码

    public static final String betSoruce = "100066";//系统编码100066
    public static final String publicKey = "T06109Z780T2ZAQ34S3V8CBG";//签名使用T06109Z780T2ZAQ34S3V8CBG
    protected static final String voteId = "80000066";//出票商编号

    //业务操作接口编号
    public static String Cast_Order = "pushTicketList";//投注接口
    public static String Cast_Order_Query = "queryTicketsOrderStatusList";//出票查询
    public static String Query_Award = "queryTicketsOrderBonusList";//中奖查询

    public static String getResultCode(String code) {
        return resultCode.get(code);
    }
    public static String getLotteryTypeMap(String playType) {
        return lotteryPlayType.get(playType);
    }
    public static String getVoteChooseTypeMap(String choose) {
        return voteChooseType.get(choose);
    }
    public static String getVoteLotteryTypeMap(String playType) {
        return voteLotteryPlayType.get(playType);
    }
    public static String getPassTypeMap(String passType) {
        return passPlayType.get(passType);
    }

    static {
        resultCode.put("SUCCESS", "操作成功");
        resultCode.put("ORDER_MD5_ERROR", "MD5签名验证失败");
        resultCode.put("ORDER_EXIT_ERROR", "订单重复");
        resultCode.put("ORDER_EXCEPTION", "订单异常");
        resultCode.put("TICKET_CANCLE", "打票机自动撤销票");
        resultCode.put("ORDER_NOT_EXIT_ERROR", "订单不存在");
        resultCode.put("ORDER_EXIT_ERROR", "订单已存在");
        resultCode.put("ORDER_NOT_EXIT_ERROR", "订单不存在");

        lotteryPlayType.put(LotteryConstants.JCZQ, "HHGG");
        lotteryPlayType.put(LotteryConstants.JCZQSPF, "SPF");
        lotteryPlayType.put(LotteryConstants.JCZQRQSPF, "RQSPF");
        lotteryPlayType.put(LotteryConstants.JCZQCBF, "BF");
        lotteryPlayType.put(LotteryConstants.JCZQBQC, "BQC");
        lotteryPlayType.put(LotteryConstants.JCZQJQS, "JQS");
        lotteryPlayType.put(LotteryConstants.JCLQ, "HHGG");
        lotteryPlayType.put(LotteryConstants.JCLQSF, "SF");
        lotteryPlayType.put(LotteryConstants.JCLQRFSF, "RFSF");
        lotteryPlayType.put(LotteryConstants.JCLQSFC, "SFC");
        lotteryPlayType.put(LotteryConstants.JCLQDXF, "DXF");

        voteLotteryPlayType.put("SPF", LotteryConstants.JCWF_PREFIX_SPF);
        voteLotteryPlayType.put("RQSPF", LotteryConstants.JCWF_PREFIX_RQSPF);
        voteLotteryPlayType.put("BF", LotteryConstants.JCWF_PREFIX_CBF);
        voteLotteryPlayType.put("BQC", LotteryConstants.JCWF_PREFIX_BQC);
        voteLotteryPlayType.put("JQS", LotteryConstants.JCWF_PREFIX_JQS);
        voteLotteryPlayType.put("SF", LotteryConstants.JCWF_PREFIX_SF);
        voteLotteryPlayType.put("RFSF", LotteryConstants.JCWF_PREFIX_RFSF);
        voteLotteryPlayType.put("SFC", LotteryConstants.JCWF_PREFIX_SFC);
        voteLotteryPlayType.put("DXF", LotteryConstants.JCWF_PREFIX_DXF);

        voteChooseType.put("SPF3", "WIN");
        voteChooseType.put("SPF1", "DRAW");
        voteChooseType.put("SPF0", "LOSE");

        voteChooseType.put("RQSPF3", "RQWIN");
        voteChooseType.put("RQSPF1", "RQDRAW");
        voteChooseType.put("RQSPF0", "RQLOSE");

        voteChooseType.put("JQS0", "S0");
        voteChooseType.put("JQS1", "S1");
        voteChooseType.put("JQS2", "S2");
        voteChooseType.put("JQS3", "S3");
        voteChooseType.put("JQS4", "S4");
        voteChooseType.put("JQS5", "S5");
        voteChooseType.put("JQS6", "S6");
        voteChooseType.put("JQS7", "S7");

        voteChooseType.put("BQC3-3", "WIN_WIN");
        voteChooseType.put("BQC3-1", "WIN_DRAW");
        voteChooseType.put("BQC3-0", "WIN_LOSE");
        voteChooseType.put("BQC1-3", "DRAW_WIN");
        voteChooseType.put("BQC1-1", "DRAW_DRAW");
        voteChooseType.put("BQC1-0", "DRAW_LOSE");
        voteChooseType.put("BQC0-3", "LOSE_WIN");
        voteChooseType.put("BQC0-1", "LOSE_DRAW");
        voteChooseType.put("BQC0-0", "LOSE_LOSE");

        voteChooseType.put("CBF1:0", "WIN10");
        voteChooseType.put("CBF2:0", "WIN20");
        voteChooseType.put("CBF2:1", "WIN21");
        voteChooseType.put("CBF3:0", "WIN30");
        voteChooseType.put("CBF3:1", "WIN31");
        voteChooseType.put("CBF3:2", "WIN32");
        voteChooseType.put("CBF4:0", "WIN40");
        voteChooseType.put("CBF4:1", "WIN41");
        voteChooseType.put("CBF4:2", "WIN42");
        voteChooseType.put("CBF5:0", "WIN50");
        voteChooseType.put("CBF5:1", "WIN51");
        voteChooseType.put("CBF5:2", "WIN52");
        voteChooseType.put("CBF9:0", "WIN_OTHER");
        voteChooseType.put("CBF0:0", "DRAW00");
        voteChooseType.put("CBF1:1", "DRAW11");
        voteChooseType.put("CBF2:2", "DRAW22");
        voteChooseType.put("CBF3:3", "DRAW33");
        voteChooseType.put("CBF9:9", "DRAW_OTHER");
        voteChooseType.put("CBF0:1", "LOSE01");
        voteChooseType.put("CBF0:2", "LOSE02");
        voteChooseType.put("CBF1:2", "LOSE12");
        voteChooseType.put("CBF0:3", "LOSE03");
        voteChooseType.put("CBF1:3", "LOSE13");
        voteChooseType.put("CBF2:3", "LOSE23");
        voteChooseType.put("CBF0:4", "LOSE04");
        voteChooseType.put("CBF1:4", "LOSE14");
        voteChooseType.put("CBF2:4", "LOSE24");
        voteChooseType.put("CBF0:5", "LOSE05");
        voteChooseType.put("CBF1:5", "LOSE15");
        voteChooseType.put("CBF2:5", "LOSE25");
        voteChooseType.put("CBF0:9", "LOSE_OTHER");

        voteChooseType.put("SF3", "WIN");
        voteChooseType.put("SF0", "LOSE");

        voteChooseType.put("RFSF3", "SF_WIN");
        voteChooseType.put("RFSF0", "SF_LOSE");

        voteChooseType.put("SFC11", "GUEST1_5");
        voteChooseType.put("SFC12", "GUEST6_10");
        voteChooseType.put("SFC13", "GUEST11_15");
        voteChooseType.put("SFC14", "GUEST16_20");
        voteChooseType.put("SFC15", "GUEST21_25");
        voteChooseType.put("SFC16", "GUEST26");
        voteChooseType.put("SFC01", "HOME1_5");
        voteChooseType.put("SFC02", "HOME6_10");
        voteChooseType.put("SFC03", "HOME11_15");
        voteChooseType.put("SFC04", "HOME16_20");
        voteChooseType.put("SFC05", "HOME21_25");
        voteChooseType.put("SFC06", "HOME26");

        voteChooseType.put("DXF3", "LARGE");
        voteChooseType.put("DXF0", "LITTLE");

        passPlayType.put("1*1", "P1");
        passPlayType.put("2*1", "P2_1");
        passPlayType.put("3*1", "P3_1");
        passPlayType.put("4*1", "P4_1");
        passPlayType.put("5*1", "P5_1");
        passPlayType.put("6*1", "P6_1");
        passPlayType.put("7*1", "P7_1");
        passPlayType.put("8*1", "P8_1");
        passPlayType.put("3*3", "P3_3");
        passPlayType.put("3*4", "P3_4");
        passPlayType.put("4*4", "P4_4");
        passPlayType.put("4*5", "P4_5");
        passPlayType.put("4*6", "P4_6");
        passPlayType.put("4*11", "P4_11");
        passPlayType.put("5*5", "P5_5");
        passPlayType.put("5*6", "P5_6");
        passPlayType.put("5*10", "P5_10");
        passPlayType.put("5*16", "P5_16");
        passPlayType.put("5*20", "P5_20");
        passPlayType.put("5*26", "P5_26");
        passPlayType.put("6*6", "P6_6");
        passPlayType.put("6*7", "P6_7");
        passPlayType.put("6*15", "P6_15");
        passPlayType.put("6*20", "P6_20");
        passPlayType.put("6*22", "P6_22");
        passPlayType.put("6*35", "P6_35");
        passPlayType.put("6*42", "P6_42");
        passPlayType.put("6*50", "P6_50");
        passPlayType.put("6*57", "P6_57");
        passPlayType.put("7*7", "P7_7");
        passPlayType.put("7*8", "P7_8");
        passPlayType.put("7*21", "P7_21");
        passPlayType.put("7*35", "P7_35");
        passPlayType.put("7*120", "P7_120");
        passPlayType.put("8*8", "P8_8");
        passPlayType.put("8*9", "P8_9");
        passPlayType.put("8*28", "P8_28");
        passPlayType.put("8*56", "P8_56");
        passPlayType.put("8*70", "P8_70");
        passPlayType.put("8*247", "P8_247");
    }

    /**
     * 初始化工具类
     */
    protected List<VoteVo> setInitialized() {
        List<VoteVo> voteList = initVoteLotteryConfig(voteId);
        if (StringUtil.isEmpty(voteList)) {
            return null;
        }
        for (VoteVo vo : voteList) {
            try {
                AbstractCodeFormat codeFormat = (AbstractCodeFormat) Class.forName("com.caipiao.ticket.vote.ouke.format.CodeFormat" + vo.getPlayType()).newInstance();
                formatMap.put(vo.getPlayType(), codeFormat);
                GamePluginAdapter plugin = InitPlugin.getPlugin(pluginMap, vo.getPlayType());
                pluginMap.put(vo.getPlayType(), plugin);
            } catch (Exception e) {
                logger.error("[欧克出票]-> 工具类初始化异常", e);
            }
        }
        return voteList;
    }

    /**
     * 拼接欧克请求报文-提票使用
     * @param vote
     * @param array
     * @return
     */
    public static Map<String, String> getRequestContent(VoteVo vote, JSONArray array) {
        Map<String, String> requestJson = new HashedMap();
        requestJson.put("cardCode", vote.getVoteId());
        requestJson.put("lotteryCode", getVoteLotteryId(vote.getPlayType()));
        requestJson.put("pwd", vote.getKey());
        requestJson.put("betSoruce", OuKeTicketUtil.betSoruce);
        requestJson.put("betContent", array.toString());
        requestJson.put("messageType", OuKeTicketUtil.Cast_Order);

        StringBuffer buffer = new StringBuffer();
        buffer.append("cardCode=" + vote.getVoteId() + "$");
        buffer.append("lotteryCode=" + getVoteLotteryId(vote.getPlayType()) + "$");
        buffer.append("pwd=" + vote.getKey() + "$");
        buffer.append("betSoruce=" + OuKeTicketUtil.betSoruce + "$");
        buffer.append("betContent=" + array.toString() + "$");
        buffer.append("publicKey=" + publicKey);
        String sign = MD5.md5(buffer.toString());

        requestJson.put("key", sign);
        return requestJson;
    }

    /**
     * 拼接欧克请求报文-出票查询
     * @param vote
     * @param orders
     * @param command
     * @return
     */
    public static Map<String, String> getRequestContentQuery(String command, VoteVo vote, String orders) {
        Map<String, String> requestJson = new HashedMap();
        requestJson.put("cardCode", vote.getVoteId());
        requestJson.put("lotteryCode", getVoteLotteryId(vote.getPlayType()));
        requestJson.put("messageType", command);
        requestJson.put("message", orders);

        StringBuffer buffer = new StringBuffer();
        buffer.append("cardCode=" + vote.getVoteId() + "$");
        buffer.append("lotteryCode=" + getVoteLotteryId(vote.getPlayType()) + "$");
        buffer.append("message=" + orders + "$");
        buffer.append("messageType=" + command + "$");
        buffer.append("publicKey=" + publicKey);
        String sign = MD5.md5(buffer.toString());

        requestJson.put("key", sign);
        return requestJson;
    }

    /**
     * 获取出票商彩种
     * @param playType
     * @return
     */
    public static String getVoteLotteryId(String playType) {
        if(LotteryUtils.isJczq(playType)) {
            return "JCZQ";
        } else if(LotteryUtils.isJclq(playType)){
            return "JCLQ";
        }
        return "";
    }
}
