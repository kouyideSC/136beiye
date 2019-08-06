package com.caipiao.ticket.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.vo.VoteVo;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.ticket.bean.PeriodInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;
import com.caipiao.ticket.vote.base.BaseCastTicket;
import com.mina.rbc.util.xml.JXmlWapper;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 华阳出票工具类
 * Created by Kouyi on 2017/12/20.
 */
public class HuaYangTicketUtil extends BaseCastTicket {
    protected HashMap<String, GamePluginAdapter> pluginMap = new HashMap<String, GamePluginAdapter>();//共享玩法工具
    protected HashMap<String, AbstractCodeFormat> formatMap = new HashMap<String, AbstractCodeFormat>();//共享投注串转换工具
    protected HashMap<String, PeriodInfo> periodMap = new HashMap<String, PeriodInfo>();//数字彩期次
    protected static HashMap<String, String> resultCode = new HashMap<String, String>();//错误码
    protected static HashMap<String, String> lotteryPlayType = new HashMap<String, String>();//出票商玩法编码
    public static HashMap<String, String> voteLotteryPlayType = new HashMap<>();//出票商玩法编码对应网站玩法编码
    protected static HashMap<String, String> passPlayType = new HashMap<String, String>();//串关方式编码
    protected static HashMap<String, String> lqPassPlayType = new HashMap<String, String>();//篮球串关方式编码
    protected static HashMap<String, String> sfcType = new HashMap<String, String>();//网站胜分差
    protected static HashMap<String, String> voteSfcType = new HashMap<String, String>();//出票商胜分差

    protected static final String voteId = "10002204";//出票商编号

    //业务操作接口编号
    public static String Cast_Number_Order = "13005";//数字彩投注接口
    public static String Cast_Jc_Order = "13010";//竞彩投注接口
    public static String Cast_Order_Query = "13004";//出票查询
    public static String Query_Award = "13011";//中奖查询
    public static String Query_Current_Period = "13007";//当前期查询

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
    public static String getLqPassTypeMap(String passType) {
        return lqPassPlayType.get(passType);
    }
    public static String getSfcMap(String type) {
        return sfcType.get(type);
    }
    public static String getVoteSfcMap(String type) {
        return voteSfcType.get(type);
    }

    static {
        resultCode.put("0", "操作成功");
        resultCode.put("1", "超时,请重新投注");
        resultCode.put("10000", "代理权限不足,不能执行此操作");
        resultCode.put("10003", "用户名已经存在");
        resultCode.put("10010", "账户金额不足,无法完成此操作");
        resultCode.put("10018", "交易通讯超时");
        resultCode.put("10019", "参数过长");
        resultCode.put("10032", "交易已存在");
        resultCode.put("12901", "无效或者不完整XML数据格式");
        resultCode.put("12902", "要查询的数据还未开奖,请等开奖后再查");
        resultCode.put("12903", "传入参数信息错误");
        resultCode.put("12904", "一次提交数据过多");
        resultCode.put("14000", "系统目前不支持此玩法");
        resultCode.put("14001", "操作代码格式错误");
        resultCode.put("14002", "注数计算错误");
        resultCode.put("14003", "投注金额计算错误");
        resultCode.put("14004", "玩法已期结,无法完成本次投注");
        resultCode.put("14005", "一票超过20000元限");
        resultCode.put("12900", "数据库编译错误");
        resultCode.put("17001", "彩种为空或者不支持彩种");
        resultCode.put("17002", "串关方式错误");
        resultCode.put("17007", "最大最小场验证错误");
        resultCode.put("17006", "选择的赛事场数大于过关数");
        resultCode.put("17003", "投注格式错误");
        resultCode.put("17004", "注数格式错误:不为整数");
        resultCode.put("17005", "注数格式错误:不为整数");
        resultCode.put("14016", "倍数过大(体彩最高99倍，福彩最高50倍)");
        resultCode.put("1001", "加密方式不对");
        resultCode.put("1000", "渠道号验证失败");
        resultCode.put("90000", "系统维护中|无法找到拆单文件|串关方式与投注内容不符|最大最小场次编号获取失败|方案超过限制");
        resultCode.put("90001", "该彩种单关已停售");
        resultCode.put("14005", "投注不能超过100票");
        resultCode.put("14005", "投注金额错误");
        resultCode.put("90080", "投注内容错误");
        resultCode.put("90010", "奖金优化时,优化记录错误");

        lotteryPlayType.put(LotteryConstants.JCZQ, "208");
        lotteryPlayType.put(LotteryConstants.JCZQSPF, "209");
        lotteryPlayType.put(LotteryConstants.JCZQRQSPF, "210");
        lotteryPlayType.put(LotteryConstants.JCZQCBF, "211");
        lotteryPlayType.put(LotteryConstants.JCZQBQC, "213");
        lotteryPlayType.put(LotteryConstants.JCZQJQS, "212");
        lotteryPlayType.put(LotteryConstants.JCLQ, "218");
        lotteryPlayType.put(LotteryConstants.JCLQSF, "216");
        lotteryPlayType.put(LotteryConstants.JCLQRFSF, "214");
        lotteryPlayType.put(LotteryConstants.JCLQSFC, "217");
        lotteryPlayType.put(LotteryConstants.JCLQDXF, "215");
        lotteryPlayType.put(LotteryConstants.SFC, "108");
        lotteryPlayType.put(LotteryConstants.RXJ, "109");
        lotteryPlayType.put(LotteryConstants.DLT, "106");
        lotteryPlayType.put(LotteryConstants.QXC, "103");
        lotteryPlayType.put(LotteryConstants.PL3, "100");
        lotteryPlayType.put(LotteryConstants.PL5, "102");
        lotteryPlayType.put(LotteryConstants.SSQ, "118");
        lotteryPlayType.put(LotteryConstants.QLC, "117");
        lotteryPlayType.put(LotteryConstants.FC3D, "116");
        lotteryPlayType.put(LotteryConstants.K3_JL, "126");
        lotteryPlayType.put(LotteryConstants.X511_GD, "129");
        lotteryPlayType.put(LotteryConstants.X511_SD, "112");

        voteLotteryPlayType.put("209", LotteryConstants.JCWF_PREFIX_SPF);
        voteLotteryPlayType.put("210", LotteryConstants.JCWF_PREFIX_RQSPF);
        voteLotteryPlayType.put("211", LotteryConstants.JCWF_PREFIX_CBF);
        voteLotteryPlayType.put("213", LotteryConstants.JCWF_PREFIX_BQC);
        voteLotteryPlayType.put("212", LotteryConstants.JCWF_PREFIX_JQS);
        voteLotteryPlayType.put("216", LotteryConstants.JCWF_PREFIX_SF);
        voteLotteryPlayType.put("214", LotteryConstants.JCWF_PREFIX_RFSF);
        voteLotteryPlayType.put("217", LotteryConstants.JCWF_PREFIX_SFC);
        voteLotteryPlayType.put("215", LotteryConstants.JCWF_PREFIX_DXF);

        passPlayType.put("1*1", "101");
        passPlayType.put("2*1", "102");
        passPlayType.put("3*1", "103");
        passPlayType.put("4*1", "104");
        passPlayType.put("5*1", "105");
        passPlayType.put("6*1", "106");
        passPlayType.put("7*1", "107");
        passPlayType.put("8*1", "108");
        passPlayType.put("3*3", "603");
        passPlayType.put("3*4", "118");
        passPlayType.put("4*4", "604");
        passPlayType.put("4*5", "120");
        passPlayType.put("4*6", "605");
        passPlayType.put("4*11", "121");
        passPlayType.put("5*5", "606");
        passPlayType.put("5*6", "123");
        passPlayType.put("5*10", "607");
        passPlayType.put("5*16", "124");
        passPlayType.put("5*20", "608");
        passPlayType.put("5*26", "125");
        passPlayType.put("6*6", "609");
        passPlayType.put("6*7", "127");
        passPlayType.put("6*15", "610");
        passPlayType.put("6*20", "611");
        passPlayType.put("6*22", "128");
        passPlayType.put("6*35", "612");
        passPlayType.put("6*42", "129");
        passPlayType.put("6*50", "613");
        passPlayType.put("6*57", "602");
        passPlayType.put("7*7", "702");
        passPlayType.put("7*8", "703");
        passPlayType.put("7*21", "704");
        passPlayType.put("7*35", "705");
        passPlayType.put("7*120", "706");
        passPlayType.put("8*8", "802");
        passPlayType.put("8*9", "803");
        passPlayType.put("8*28", "804");
        passPlayType.put("8*56", "805");
        passPlayType.put("8*70", "806");
        passPlayType.put("8*247", "807");

        lqPassPlayType.put("1*1", "02");
        lqPassPlayType.put("2*1", "03");
        lqPassPlayType.put("3*1", "04");
        lqPassPlayType.put("3*3", "41");
        lqPassPlayType.put("3*4", "42");
        lqPassPlayType.put("4*1", "05");
        lqPassPlayType.put("4*4", "51");
        lqPassPlayType.put("4*5", "52");
        lqPassPlayType.put("4*6", "53");
        lqPassPlayType.put("4*11", "54");
        lqPassPlayType.put("5*1", "06");
        lqPassPlayType.put("5*5", "61");
        lqPassPlayType.put("5*6", "62");
        lqPassPlayType.put("5*10", "63");
        lqPassPlayType.put("5*16", "64");
        lqPassPlayType.put("5*20", "65");
        lqPassPlayType.put("5*26", "66");
        lqPassPlayType.put("6*1", "07");
        lqPassPlayType.put("6*6", "71");
        lqPassPlayType.put("6*7", "72");
        lqPassPlayType.put("6*15", "73");
        lqPassPlayType.put("6*20", "74");
        lqPassPlayType.put("6*22", "75");
        lqPassPlayType.put("6*35", "76");
        lqPassPlayType.put("6*42", "77");
        lqPassPlayType.put("6*50", "78");
        lqPassPlayType.put("6*57", "79");
        lqPassPlayType.put("7*1", "08");
        lqPassPlayType.put("7*7", "81");
        lqPassPlayType.put("7*8", "82");
        lqPassPlayType.put("7*21", "83");
        lqPassPlayType.put("7*35", "84");
        lqPassPlayType.put("7*120", "85");
        lqPassPlayType.put("8*1", "09");
        lqPassPlayType.put("8*8", "91");
        lqPassPlayType.put("8*9", "92");
        lqPassPlayType.put("8*28", "93");
        lqPassPlayType.put("8*56", "94");
        lqPassPlayType.put("8*70", "95");
        lqPassPlayType.put("8*247", "96");

        sfcType.put("1", "01");//主1-5
        sfcType.put("2", "02");//主6-10
        sfcType.put("3", "03");//主11-15
        sfcType.put("4", "04");//主16-20
        sfcType.put("5", "05");//主21-25
        sfcType.put("6", "06");//主26+
        sfcType.put("7", "11");//客1-5
        sfcType.put("8", "12");//客6-10
        sfcType.put("9", "13");//客11-15
        sfcType.put("10", "14");//客16-20
        sfcType.put("11", "15");//客21-25
        sfcType.put("12", "16");//客26+
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
                AbstractCodeFormat codeFormat = (AbstractCodeFormat) Class.forName("com.caipiao.ticket.vote.huayang.format.CodeFormat" + vo.getPlayType()).newInstance();
                formatMap.put(vo.getPlayType(), codeFormat);
                GamePluginAdapter plugin = InitPlugin.getPlugin(pluginMap, vo.getPlayType());
                pluginMap.put(vo.getPlayType(), plugin);
            } catch (Exception e) {
                logger.error("[华阳出票]-> 工具类初始化异常", e);
            }
        }
        return voteList;
    }

    /**
     * 华阳出票接口调用
     * @param command
     * @param body
     * @param vo
     * @return
     */
    public static String getRequestContent(String command, String body, VoteVo vo) {
        try {
            String timestamp = DateUtil.formatDate(new Date(), DateUtil.LOG_DATE_TIME2);
            String messageId = DateUtil.formatDate(new Date(), DateUtil.LOG_DATE_TIME) + (int) (Math.random() * 1000);
            String sign = MD5.md5(timestamp + vo.getKey() + "<body><elements>" + body + "</elements></body>");
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            sb.append("<message version=\"1.0\"><header>");
            sb.append("<messengerid>").append(messageId).append("</messengerid>");
            sb.append("<timestamp>").append(timestamp).append("</timestamp>");
            sb.append("<transactiontype>").append(command).append("</transactiontype>");
            sb.append("<digest>").append(sign).append("</digest>");
            sb.append("<agenterid>").append(vo.getVoteId()).append("</agenterid>");
            sb.append("<username>").append(vo.getVoteId()).append("</username>");
            sb.append("</header><body><elements>");
            sb.append(body);
            sb.append("</elements></body></message>");
            return sb.toString();
        } catch (Exception ex) {
            logger.error("[调用华阳接口异常]", ex);
        }
        return null;
    }

    /**
     * 出票接口调用-适配BOM头
     * @param url
     * @param body
     * @param isK3
     * @return
     */
    public static JXmlWapper parseUrl(String url, String body, boolean isK3) {
        try {
            JXmlWapper xml = null;
            if(!isK3) {
                xml = JXmlWapper.parseUrl(url, body, "UTF-8", 30);
            } else {
                String response = HttpClientUtil.callHttpPost_String(url, body);
                if(StringUtils.isNotEmpty(response)) {
                    byte[] bytes = response.getBytes("UTF-8");
                    response = new String(bytes, 3, bytes.length-3, "UTF-8"); //解决BOM头解析
                    xml = JXmlWapper.parse(response);
                }
            }
            return xml;
        } catch (Exception ex) {
            logger.error("[调用华阳接口异常]", ex);
            return new JXmlWapper("");
        }
    }

}
