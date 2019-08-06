package com.caipiao.ticket.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.ticket.bean.PeriodInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;
import com.caipiao.ticket.vote.base.BaseCastTicket;
import com.mina.rbc.util.xml.JXmlWapper;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 吉米出票工具类
 * Created by Kouyi on 2017/12/11.
 */
public class JiMiTicketUtil extends BaseCastTicket {
    protected HashMap<String, GamePluginAdapter> pluginMap = new HashMap<String, GamePluginAdapter>();//共享玩法工具
    protected HashMap<String, AbstractCodeFormat> formatMap = new HashMap<String, AbstractCodeFormat>();//共享投注串转换工具
    protected HashMap<String, PeriodInfo> periodMap = new HashMap<String, PeriodInfo>();//数字彩期次
    protected static HashMap<String, String> resultCode = new HashMap<String, String>();//错误码
    protected static HashMap<String, String> lotteryPlayType = new HashMap<String, String>();//出票商玩法编码
    public static HashMap<String, String> voteLotteryPlayType = new HashMap<>();//出票商玩法编码对应网站玩法编码

    protected static final String voteId = "800126";//出票商编号

    //业务操作接口编号
    public static String Cast_Order = "BetTicket";//投注接口
    public static String Cast_Order_Query = "QueryTicket";//出票查询
    public static String Query_Award = "QueryAwardTicket";//中奖查询
    public static String Query_Current_Period = "QueryIssueSell";//当前期查询

    public static String getResultCode(String code) {
        return resultCode.get(code);
    }
    public static String getLotteryTypeMap(String playType) {
        return lotteryPlayType.get(playType);
    }
    public static String getVoteLotteryTypeMap(String playType) {
        return voteLotteryPlayType.get(playType);
    }

    static {
        resultCode.put("0", "操作成功");
        resultCode.put("1001", "参数为空");
        resultCode.put("1002", "签名错误");
        resultCode.put("1003", "商户不存在");
        resultCode.put("1004", "商户已冻洁");
        resultCode.put("1005", "提交数据过多");
        resultCode.put("1006", "消息体格式错误");
        resultCode.put("1007", "彩种错误");
        resultCode.put("1008", "彩种已停售");
        resultCode.put("1009", "无比赛信息");
        resultCode.put("1010", "无在售期号");
        resultCode.put("1011", "比赛已截止");
        resultCode.put("1012", "有重复的比赛");
        resultCode.put("1013", "超过最大倍数");
        resultCode.put("1014", "超过最大金额");
        resultCode.put("1015", "注数错误");
        resultCode.put("1016", "金额错误");
        resultCode.put("1017", "过关方式错误");
        resultCode.put("1018", "余额不足");
        resultCode.put("1019", "订单号重复");
        resultCode.put("2001", "系统异常");

        lotteryPlayType.put(LotteryConstants.JCZQ, "16");
        lotteryPlayType.put(LotteryConstants.JCZQSPF, "11");
        lotteryPlayType.put(LotteryConstants.JCZQRQSPF, "15");
        lotteryPlayType.put(LotteryConstants.JCZQCBF, "12");
        lotteryPlayType.put(LotteryConstants.JCZQBQC, "14");
        lotteryPlayType.put(LotteryConstants.JCZQJQS, "13");
        lotteryPlayType.put(LotteryConstants.JCLQ, "25");
        lotteryPlayType.put(LotteryConstants.JCLQSF, "21");
        lotteryPlayType.put(LotteryConstants.JCLQRFSF, "22");
        lotteryPlayType.put(LotteryConstants.JCLQSFC, "23");
        lotteryPlayType.put(LotteryConstants.JCLQDXF, "24");
        lotteryPlayType.put(LotteryConstants.SFC, "41");
        lotteryPlayType.put(LotteryConstants.RXJ, "42");
        lotteryPlayType.put(LotteryConstants.DLT, "51");
        lotteryPlayType.put(LotteryConstants.QXC, "52");
        lotteryPlayType.put(LotteryConstants.PL3, "54");
        lotteryPlayType.put(LotteryConstants.PL5, "53");
        lotteryPlayType.put(LotteryConstants.SSQ, "61");
        lotteryPlayType.put(LotteryConstants.QLC, "62");
        lotteryPlayType.put(LotteryConstants.FC3D, "63");
        lotteryPlayType.put(LotteryConstants.X511_GD, "71");
        lotteryPlayType.put(LotteryConstants.X511_SD, "72");
        lotteryPlayType.put(LotteryConstants.GJ, "17");
        lotteryPlayType.put(LotteryConstants.GYJ, "18");

        voteLotteryPlayType.put("11", LotteryConstants.JCWF_PREFIX_SPF);
        voteLotteryPlayType.put("15", LotteryConstants.JCWF_PREFIX_RQSPF);
        voteLotteryPlayType.put("12", LotteryConstants.JCWF_PREFIX_CBF);
        voteLotteryPlayType.put("14", LotteryConstants.JCWF_PREFIX_BQC);
        voteLotteryPlayType.put("13", LotteryConstants.JCWF_PREFIX_JQS);
        voteLotteryPlayType.put("21", LotteryConstants.JCWF_PREFIX_SF);
        voteLotteryPlayType.put("22", LotteryConstants.JCWF_PREFIX_RFSF);
        voteLotteryPlayType.put("23", LotteryConstants.JCWF_PREFIX_SFC);
        voteLotteryPlayType.put("24", LotteryConstants.JCWF_PREFIX_DXF);

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
                AbstractCodeFormat codeFormat = (AbstractCodeFormat) Class.forName("com.caipiao.ticket.vote.jimi.format.CodeFormat" + vo.getPlayType()).newInstance();
                formatMap.put(vo.getPlayType(), codeFormat);
                GamePluginAdapter plugin = InitPlugin.getPlugin(pluginMap, vo.getPlayType());
                pluginMap.put(vo.getPlayType(), plugin);
            } catch (Exception e) {
                logger.error("[吉米出票]-> 工具类初始化异常", e);
            }
        }
        return voteList;
    }

    /**
     * 出票webservice接口调用
     * @param method
     * @param params
     * @param vo
     * @return
     */
    public static JXmlWapper parseUrl(String method, String[] params, VoteVo vo) {
        try {
            StringBuffer body = new StringBuffer();
            String[] parameters = null;
            if(StringUtil.isNotEmpty(params)) {
                parameters = new String[2+params.length];
                for(int x = 0; x < params.length; x++) {
                    body.append(params[x]);
                    parameters[x+1] = params[x];
                }
            } else {
                parameters = new String[2];
            }
            String sign = MD5.md5(vo.getVoteId() + body.toString() + vo.getKey());
            parameters[0] = vo.getVoteId();
            parameters[parameters.length-1] = sign;

            Service service = new Service();
            Call call = (Call)service.createCall();
            call.setUseSOAPAction(true);
            call.setTargetEndpointAddress(new URL(vo.getApiUrl()));
            call.setEncodingStyle("http://schemas.xmlsoap.org/wsdl/soap/");
            call.setOperationName(new QName("http://service.xixi.com/", method));
            call.setTimeout(5000);
            return JXmlWapper.parse((String) call.invoke(parameters));
        } catch (Exception ex) {
            logger.error("[调用吉米WebService接口异常]", ex);
        }
        return new JXmlWapper("");
    }

}
