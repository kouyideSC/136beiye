package com.caipiao.ticket.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.encrypt.DESCoder;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;
import com.caipiao.ticket.vote.base.BaseCastTicket;
import com.mina.rbc.util.xml.JXmlWapper;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 华盈出票工具类
 * Created by Kouyi on 2017/12/11.
 */
public class HuaYingTicketUtil extends BaseCastTicket {
    protected HashMap<String, GamePluginAdapter> pluginMap = new HashMap<String, GamePluginAdapter>();//共享玩法工具
    protected HashMap<String, AbstractCodeFormat> formatMap = new HashMap<String, AbstractCodeFormat>();//共享投注串转换工具
    protected static HashMap<String, String> resultCode = new HashMap<String, String>();//错误码
    protected static HashMap<String, String> lotteryPlayType = new HashMap<String, String>();//出票商玩法编码
    public static HashMap<String, String> voteLotteryPlayType = new HashMap<String, String>();//出票商玩法编码对应网站玩法编码
    protected static HashMap<String, String> passPlayType = new HashMap<String, String>();//串关方式编码

    protected static final String voteId = "80002";//出票商编号

    //业务操作接口编号
    public static String Cast_Order = "801";//投注接口
    public static String Cast_Order_Query = "802";//出票查询
    public static String Query_Award = "803";//中奖查询
    public static String Cast_Balance_Query = "806";//余额查询接口

    public static String getResultCode(String code) {
        return resultCode.get(code);
    }
    public static String getLotteryTypeMap(String playType) {
        return lotteryPlayType.get(playType);
    }
    public static String getVoteLotteryTypeMap(String playType) {
        return voteLotteryPlayType.get(playType);
    }
    public static String getPassTypeMap(String passType) {
        return passPlayType.get(passType);
    }

    static {
        resultCode.put("0", "操作成功");
        resultCode.put("1", "出票中");
        resultCode.put("2", "操作失败");
        resultCode.put("3", "暂停销售");
        resultCode.put("5", "查询结果不存在");
        resultCode.put("6", "订单已存在");
        resultCode.put("10001", "彩期不存在");
        resultCode.put("10002", "当前期不存在");
        resultCode.put("20001", "注码金额错误");
        resultCode.put("20004", "对阵不存在");
        resultCode.put("20005", "对阵未开启");
        resultCode.put("20006", "对阵已经过期");
        resultCode.put("20007", "对阵不支持该玩法");
        resultCode.put("20023", "投注限号或不允许投注");
        resultCode.put("9999", "未知错误");
        resultCode.put("30002", "开奖号码不存在");
        resultCode.put("40002", "用户不存在");
        resultCode.put("40003", "Md5加密错误");
        resultCode.put("40007", "彩期不存在");
        resultCode.put("40006", "彩种不存在");
        resultCode.put("40013", "彩期不是当前期");
        resultCode.put("40004", "用户账户不存在");
        resultCode.put("40005", "用户可用余额不足");
        resultCode.put("40008", "彩期已过期");
        resultCode.put("70001", "ip错误");
        resultCode.put("70002", "解密错误");
        resultCode.put("70003", "解析错误");
        resultCode.put("70004", "请求命令不存在");
        resultCode.put("70005", "用户停用");

        lotteryPlayType.put(LotteryConstants.JCZQ, "3011");
        lotteryPlayType.put(LotteryConstants.JCZQSPF, "3010");
        lotteryPlayType.put(LotteryConstants.JCZQRQSPF, "3006");
        lotteryPlayType.put(LotteryConstants.JCZQCBF, "3007");
        lotteryPlayType.put(LotteryConstants.JCZQBQC, "3009");
        lotteryPlayType.put(LotteryConstants.JCZQJQS, "3008");
        lotteryPlayType.put(LotteryConstants.JCLQ, "3005");
        lotteryPlayType.put(LotteryConstants.JCLQSF, "3001");
        lotteryPlayType.put(LotteryConstants.JCLQRFSF, "3002");
        lotteryPlayType.put(LotteryConstants.JCLQSFC, "3003");
        lotteryPlayType.put(LotteryConstants.JCLQDXF, "3004");

        voteLotteryPlayType.put("3010", LotteryConstants.JCWF_PREFIX_SPF);
        voteLotteryPlayType.put("3006", LotteryConstants.JCWF_PREFIX_RQSPF);
        voteLotteryPlayType.put("3007", LotteryConstants.JCWF_PREFIX_CBF);
        voteLotteryPlayType.put("3009", LotteryConstants.JCWF_PREFIX_BQC);
        voteLotteryPlayType.put("3008", LotteryConstants.JCWF_PREFIX_JQS);
        voteLotteryPlayType.put("3001", LotteryConstants.JCWF_PREFIX_SF);
        voteLotteryPlayType.put("3002", LotteryConstants.JCWF_PREFIX_RFSF);
        voteLotteryPlayType.put("3003", LotteryConstants.JCWF_PREFIX_SFC);
        voteLotteryPlayType.put("3004", LotteryConstants.JCWF_PREFIX_DXF);

        passPlayType.put("1*1", "11001");
        passPlayType.put("2*1", "12001");
        passPlayType.put("3*1", "13001");
        passPlayType.put("4*1", "14001");
        passPlayType.put("5*1", "15001");
        passPlayType.put("6*1", "16001");
        passPlayType.put("7*1", "17001");
        passPlayType.put("8*1", "18001");
        passPlayType.put("3*3", "13003");
        passPlayType.put("3*4", "13004");
        passPlayType.put("4*4", "14004");
        passPlayType.put("4*5", "14005");
        passPlayType.put("4*6", "14006");
        passPlayType.put("4*11", "14011");
        passPlayType.put("5*5", "15005");
        passPlayType.put("5*6", "15006");
        passPlayType.put("5*10", "15010");
        passPlayType.put("5*16", "15016");
        passPlayType.put("5*20", "15020");
        passPlayType.put("5*26", "15026");
        passPlayType.put("6*6", "16006");
        passPlayType.put("6*7", "16007");
        passPlayType.put("6*15", "16015");
        passPlayType.put("6*20", "16020");
        passPlayType.put("6*22", "16022");
        passPlayType.put("6*35", "16035");
        passPlayType.put("6*42", "16042");
        passPlayType.put("6*50", "16050");
        passPlayType.put("6*57", "16057");
        passPlayType.put("7*7", "17007");
        passPlayType.put("7*8", "17008");
        passPlayType.put("7*21", "17021");
        passPlayType.put("7*35", "17035");
        passPlayType.put("7*120", "17120");
        passPlayType.put("8*8", "18008");
        passPlayType.put("8*9", "18009");
        passPlayType.put("8*28", "18028");
        passPlayType.put("8*56", "18056");
        passPlayType.put("8*70", "18070");
        passPlayType.put("8*247", "18247");
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
                AbstractCodeFormat codeFormat = (AbstractCodeFormat) Class.forName("com.caipiao.ticket.vote.nuomi.format.CodeFormat" + vo.getPlayType()).newInstance();
                formatMap.put(vo.getPlayType(), codeFormat);
                GamePluginAdapter plugin = InitPlugin.getPlugin(pluginMap, vo.getPlayType());
                pluginMap.put(vo.getPlayType(), plugin);
            } catch (Exception e) {
                logger.error("[华盈出票]-> 工具类初始化异常", e);
            }
        }
        return voteList;
    }

    /**
     * 拼接华盈请求报文
     * @param vote
     * @param body
     * @return
     */
    public static String getRequestContent(VoteVo vote, String command, String body) {
        String timestamp = DateUtil.formatDate(new Date(), DateUtil.LOG_DATE_TIME2);
        String messageId = vote.getVoteId() + command + timestamp;
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<content><head><version>1.0</version><merchant>");
        sb.append(vote.getVoteId());
        sb.append("</merchant><command>");
        sb.append(command);
        sb.append("</command><messageid>");
        sb.append(messageId);
        sb.append("</messageid><timestamp>");
        sb.append(timestamp);
        sb.append("</timestamp></head>");
        sb.append("<body>");
        sb.append(DESCoder.desEncrypt("<message>" + body + "</message>", vote.getKey()));
        sb.append("</body><signature>");
        sb.append(MD5.md5(command+timestamp+vote.getVoteId()+vote.getKey()));
        sb.append("</signature></content>");
        return sb.toString();
    }

    /**
     * 出票接口调用-适配BOM头
     * @param url
     * @param body
     * @return
     */
    public static JXmlWapper parseUrl(String url, String body) {
        try {
            JXmlWapper xml = null;
            String response = HttpClientUtil.callHttpPost_String(url, body);
            if(StringUtils.isNotEmpty(response)) {
                byte[] bytes = response.getBytes("UTF-8");
                response = new String(bytes, 3, bytes.length-3, "UTF-8"); //解决BOM头解析
                xml = JXmlWapper.parse(response);
            }
            return xml;
        } catch (Exception ex) {
            logger.error("[调用华盈接口异常]", ex);
            return new JXmlWapper("");
        }
    }
}
