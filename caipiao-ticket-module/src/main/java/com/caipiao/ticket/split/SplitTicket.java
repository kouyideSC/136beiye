package com.caipiao.ticket.split;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.util.*;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.scheme.SchemeZhuiHao;
import com.caipiao.domain.ticket.TicketConfig;
import com.caipiao.domain.ticket.TicketVote;
import com.caipiao.domain.ticket.TicketVoteRule;
import com.caipiao.memcache.MemCached;
import com.caipiao.plugin.Lottery1030;
import com.caipiao.plugin.Lottery1050;
import com.caipiao.plugin.Lottery1500;
import com.caipiao.plugin.Lottery1560;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GameContains;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.lottery.PeriodService;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.service.user.UserService;
import com.caipiao.split.GameSplit;
import com.caipiao.ticket.util.LoggerUtil;
import com.caipiao.ticket.util.OutTicketUtil;
import com.caipiao.ticket.vo.SchemeVo;
import com.caipiao.ticket.vo.SplitTicketVo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 拆票工具类
 * Created by Kouyi on 2017/11/27.
 */
@Component("splitTicket")
public class SplitTicket {
    private static Logger logger = LoggerFactory.getLogger(SplitTicket.class);
    private HashMap<String, GamePluginAdapter> mapPlugin = new HashMap<String, GamePluginAdapter>();
    private final static int maxMoney = 20000;//拆票支持最大金额[包含倍数在内]
    public final static String defaultVote = "9696";
    public final static String defaultTempVote = "19696";//临时过渡出票上编号
    public final static String defaultTempVoteTwo = "29696";//临时过渡出票上编号
    private final static String huaYangVote = "9696";//10002204

    @Autowired
    private MemCached memcache;
    @Autowired
    private SchemeService schemeService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;
    @Autowired
    private PeriodService periodService;

    /**
     * 拆票与分票业务主类
     * @return
     */
    public void orderSplitTicket() {
        try {
            //排除控制参数
            Map<String, TicketConfig> configMap = getTicketConfig(defaultVote);
            //查询分票规则
            Map<String, List<TicketVoteRule>> mapRule = ticketService.queryTicketRuleAll();
            if(StringUtil.isEmpty(mapRule)) {//无规则不拆票
                return;
            }
            //排除虚拟用户
            List<Integer> userList = userService.queryUserListByUserType();
            //排除黑名单用户
            List<Integer> blackList = userService.queryUserListByBlackType();
            //排除测试用户
            List<Integer> testList = SysConfig.getFilterUser();
            //拆票后集合
            SplitTicketVo ticketVo = new SplitTicketVo();
            ordinaryOrderSplitTicket(ticketVo, configMap, userList, testList, blackList);//普通方案拆票
            zhuiHaoOrderSplitTicket(ticketVo, configMap, userList, testList, blackList);//追号方案拆票
            jjyhOrderSplitTicket(ticketVo, configMap, userList, testList, blackList);//优化方案拆票
            if(StringUtil.isEmpty(ticketVo.getMapTicket()) && StringUtil.isEmpty(ticketVo.getTicketList())) {
                return;//无可处理的票 终止
            }

            //执行分票
            OutTicketUtil.randomSplitTicket(ticketVo, mapRule, logger);

            Map<String, Boolean> statusBack = new HashMap();
            //将控制参数以内的或混投非单一玩法的票入库
            if(StringUtil.isNotEmpty(ticketVo.getMapTicket())) {
                for (Map.Entry<String, List<SchemeTicket>> tickets : ticketVo.getMapTicket().entrySet()) {
                    try {//保证单方案拆票完整性
                        ticketService.saveTicket(tickets.getValue(), statusBack, logger);
                    } catch (ServiceException ex) {
                        logger.error("[新票入库异常] 方案号=" + tickets.getValue(), ex);
                    }
                }
            }
            //将控制参数以外的单一玩法的票入库
            ticketService.saveTicket(ticketVo.getTicketList(), statusBack,  logger);

            //订单未分配出票商，拆票状态回退
            if(StringUtil.isNotEmpty(statusBack)) {
                Scheme scheme = new Scheme();
                scheme.setSchemeStatus(SchemeConstants.SCHEME_STATUS_ZFCG);
                scheme.setSchemeStatusDesc(SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_ZFCG));
                for (Map.Entry<String, Boolean> orderEntry : statusBack.entrySet()) {
                    String schemeId = orderEntry.getKey();
                    scheme.setSchemeOrderId(schemeId);
                    if(!orderEntry.getValue()) {
                        scheme.setSchemeType(0);
                    } else {
                        scheme.setSchemeType(1);
                    }
                    schemeService.updateSchemeTicketStatusBySchemeOrderId(scheme);
                }
            }
        } catch (Exception ex) {
            logger.error("方案拆票和分票任务-异常", ex);
        }
    }

    /**
     * 普通订单-拆票业务方法
     * @param ticketVo
     * @param configMap
     * @param userList
     * @param testList
     * @param blackList
     */
    private void ordinaryOrderSplitTicket(SplitTicketVo ticketVo, Map<String, TicketConfig> configMap, List<Integer> userList,
                                          List<Integer> testList, List<Integer> blackList) {
        try {
            List<Scheme> list = schemeService.querySchemePaySuccess(0);
            if(StringUtil.isNotEmpty(list)) {
                for (Scheme sch : list) {
                    try {
                        if(LotteryUtils.isSzc(sch.getLotteryId())) {//普通方案如果是数字彩 只拆当前期
                            List<Period> currentPeriods = periodService.queryCurrentPeriodByLottery(sch.getLotteryId());
                            if(StringUtil.isEmpty(currentPeriods)) {
                                continue;//找不到当前期 不拆票
                            }
                            boolean isCurrent = false;
                            for(Period period : currentPeriods) {
                                if(period.getPeriod().equals(sch.getPeriod())) {
                                    isCurrent = true;
                                    break;
                                }
                            }
                            if(!isCurrent) {//不是当前期 不拆票
                                logger.error("[方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " 已截止-未拆票");
                                continue;
                            }
                        }
                        //默认不是大方案
                        if (StringUtil.isEmpty(sch.getBigOrderStatus())) {
                            sch.setBigOrderStatus(1);
                        }
                        int zs = (int) (sch.getSchemeMoney() / sch.getSchemeMultiple());
                        //大方案必须审核后再拆票
                        if (SysConfig.isBigTicket(zs, sch.getEndTime()) && sch.getBigOrderStatus() != 3) {
                            continue;
                        }

                        sch.setSchemeStatus(SchemeConstants.SCHEME_STATUS_CPZ);
                        sch.setSchemeStatusDesc(SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_CPZ));
                        schemeService.updateSchemeTicketStatus(sch);

                        String defaultVoteId = "";//默认出票商
                        if(StringUtil.isNotEmpty(userList) && userList.contains(sch.getSchemeUserId().intValue())) {//虚拟用户
                            defaultVoteId = defaultVote;
                            logger.info("[方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 虚拟用户投注-被分配给默认出票商" + defaultVoteId);
                        }
                        if(StringUtil.isNotEmpty(testList) && testList.contains(sch.getSchemeUserId().intValue())
                                && (LotteryUtils.isJc(sch.getLotteryId()) || LotteryUtils.isKp(sch.getLotteryId()))) {//测试用户
                            defaultVoteId = defaultTempVoteTwo;
                            logger.info("[方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 测试用户投注-被分配给默认出票商" + defaultVoteId);
                        }

                        if(SysConfig.isOpenOutTicket() && StringUtil.isEmpty(defaultVoteId) && !blackList.contains(sch.getSchemeUserId().intValue())
                                && OutTicketUtil.checkOutTicketConfig(configMap, sch)) {//控制参数模块
                            defaultVoteId = defaultTempVote;
                            logger.info("[方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 触发控制参数-被分配给默认出票商" + defaultVoteId);
                        }

                        //竞彩多串过关(其他出票商不支持)-固定分配到华阳
                        if(isMulPass(sch.getLotteryId(), sch.getSchemeContent()) && StringUtil.isEmpty(defaultVoteId)) {
                            defaultVoteId = huaYangVote;
                            logger.info("[方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 多串过关固定-被分配给华阳出票" + defaultVoteId);
                        }

                        Map<Integer, List<SchemeTicket>> ticketMap = orderTransForTicket(new SchemeVo(sch.getSchemeOrderId(),
                                sch.getLotteryId(), sch.getPlayTypeId(), sch.getSchemeMultiple(), sch.getSchemeMoney(),
                                sch.getSchemeStatus(), sch.getSchemeContent(), sch.getPeriod(), defaultVoteId,
                                getSchemeCodeSp(sch.getSchemeSpContent()), sch.getSchemeUserId(), sch.getEndTime(), false));
                        ticketVo.putTicket(sch.getSchemeOrderId(), ticketMap.get(1));
                        ticketVo.addAllTicket(ticketMap.get(2));
                        if(StringUtil.isEmpty(defaultVoteId) || defaultVoteId.equals(defaultTempVote) || defaultVoteId.equals(defaultTempVoteTwo)) {//存入分票对象后续进行分票
                            ticketVo.putOrder(sch.getPlayTypeId(), sch.getSchemeOrderId());
                        }
                    } catch (Exception e) {
                        logger.error("普通方案拆票任务-拆票异常 方案号=" + sch.getSchemeOrderId(), e);
                        sch.setSchemeStatus(SchemeConstants.SCHEME_STATUS_CPSB);
                        sch.setSchemeStatusDesc("拆票异常");
                        schemeService.updateSchemeTicketStatus(sch);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("普通方案拆票任务-查询待拆方案异常", ex);
        }
    }

    /**
     * 追号订单-拆票业务方法
     * @param ticketVo
     * @param configMap
     * @param userList
     * @param testList
     * @param blackList
     */
    private void zhuiHaoOrderSplitTicket(SplitTicketVo ticketVo, Map<String, TicketConfig> configMap, List<Integer> userList,
                                         List<Integer> testList, List<Integer> blackList) {
        try {
            List<SchemeZhuiHao> list = schemeService.queryZhuiHaoSchemePaySuccess();
            if(StringUtil.isNotEmpty(list)) {
                for (SchemeZhuiHao sch : list) {
                    try {
                        //快频追期-截止前N分钟进行拆票[为了实现中奖后停止追期功能-因为上一期开奖时间比下一期开始销售时间晚]
                        //11选5和快3-截止前三分钟
                        if((LotteryUtils.is11x5(sch.getLotteryId()) || LotteryUtils.isK3(sch.getLotteryId()))
                                && new Date().getTime() < DateUtil.addSecond(sch.getOpenTime(), -180).getTime()) {
                            continue;
                        }
                        sch.setSchemeStatus(SchemeConstants.SCHEME_STATUS_CPZ);
                        sch.setSchemeStatusDesc(SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_CPZ));
                        schemeService.updateZhuihaoSchemeTicketStatus(sch);

                        String defaultVoteId = "";//默认出票商
                        if(StringUtil.isNotEmpty(userList) && userList.contains(sch.getSchemeUserId().intValue())) {//虚拟用户
                            defaultVoteId = defaultVote;
                            logger.info("[追号方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 测试用户投注-被分配给默认出票商" + defaultVoteId);
                        }
                        if(StringUtil.isNotEmpty(testList) && testList.contains(sch.getSchemeUserId().intValue())
                                && (LotteryUtils.isJc(sch.getLotteryId()) || LotteryUtils.isKp(sch.getLotteryId()))) {//测试用户
                            defaultVoteId = defaultTempVoteTwo;
                            logger.info("[追号方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 测试用户投注-被分配给默认出票商" + defaultVoteId);
                        }

                        if(SysConfig.isOpenOutTicket() && StringUtil.isEmpty(defaultVoteId) && !blackList.contains(sch.getSchemeUserId().intValue())
                                && OutTicketUtil.checkOutTicketConfig(configMap, sch)) {//控制参数模块
                            defaultVoteId = defaultTempVote;
                            logger.info("[追号方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 触发控制参数-被分配给默认出票商" + defaultVoteId);
                        }

                        Map<Integer, List<SchemeTicket>> ticketMap = orderTransForTicket(new SchemeVo(sch.getSchemeOrderId(),
                                sch.getLotteryId(), sch.getLotteryId(), sch.getSchemeMultiple(), sch.getSchemeMoney(),
                                sch.getSchemeStatus(), sch.getSchemeContent(), sch.getPeriod(), defaultVoteId, null,
                                sch.getSchemeUserId(), sch.getEndTime(), true));
                        ticketVo.putTicket(sch.getSchemeOrderId(), ticketMap.get(1));
                        ticketVo.addAllTicket(ticketMap.get(2));
                        if(StringUtil.isEmpty(defaultVoteId) || defaultVoteId.equals(defaultTempVote) || defaultVoteId.equals(defaultTempVoteTwo)) {//存入分票对象后续进行分票
                            ticketVo.putOrder(sch.getLotteryId(), sch.getSchemeOrderId());
                        }
                    } catch (Exception e) {
                        logger.error("追号方案拆票任务-拆票异常 方案号=" + sch.getSchemeOrderId(), e);
                        sch.setSchemeStatus(SchemeConstants.SCHEME_STATUS_CPSB);
                        sch.setSchemeStatusDesc("拆票异常");
                        schemeService.updateZhuihaoSchemeTicketStatus(sch);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("追号方案拆票任务-查询待拆方案异常", ex);
        }
    }

    /**
     * 优化订单-拆票业务方法
     * @param ticketVo
     * @param configMap
     * @param userList
     * @param testList
     * @param blackList
     */
    private void jjyhOrderSplitTicket(SplitTicketVo ticketVo, Map<String, TicketConfig> configMap, List<Integer> userList,
                                      List<Integer> testList, List<Integer> blackList) {
        try {
            List<Scheme> list = schemeService.querySchemePaySuccess(2);
            if(StringUtil.isNotEmpty(list)) {
                for (Scheme sch : list) {
                    try {
                        if(!LotteryUtils.isJc(sch.getLotteryId())) {//优化订单只针对竞彩
                            continue;
                        }
                        //默认不是大方案
                        if (StringUtil.isEmpty(sch.getBigOrderStatus())) {
                            sch.setBigOrderStatus(1);
                        }

                        JSONArray array = JSONArray.fromObject(sch.getSchemeYhContent());//解析优化投注串
                        if(StringUtil.isEmpty(array)) {
                            continue;
                        }
                        int zs = array.size();//注数
                        //大方案必须审核后再拆票
                        if (SysConfig.isBigTicket(zs, sch.getEndTime()) && sch.getBigOrderStatus() != 3) {
                            continue;
                        }

                        sch.setSchemeStatus(SchemeConstants.SCHEME_STATUS_CPZ);
                        sch.setSchemeStatusDesc(SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_CPZ));
                        schemeService.updateSchemeTicketStatus(sch);

                        String defaultVoteId = "";//默认出票商
                        if(StringUtil.isNotEmpty(userList) && userList.contains(sch.getSchemeUserId().intValue())) {//虚拟用户
                            defaultVoteId = defaultVote;
                            logger.info("[优化方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 虚拟用户投注-被分配给默认出票商" + defaultVoteId);
                        }
                        if(StringUtil.isNotEmpty(testList) && testList.contains(sch.getSchemeUserId().intValue())) {//测试用户
                            defaultVoteId = defaultTempVoteTwo;
                            logger.info("[优化方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 测试用户投注-被分配给默认出票商" + defaultVoteId);
                        }

                        List<SchemeTicket> ticketOne = new ArrayList<>();
                        List<SchemeTicket> ticketTwo = new ArrayList<>();
                        for(int x = 0; x < array.size(); x++) {
                            String tempDefaultVoteId = defaultVoteId;//临时出票商
                            JSONObject content = array.getJSONObject(x);
                            sch.setSchemeContent(content.getString("tzcontent"));
                            sch.setSchemeMoney(content.getDouble("money"));
                            sch.setSchemeMultiple(content.getInt("smultiple"));
                            if(SysConfig.isOpenOutTicket() && StringUtil.isEmpty(tempDefaultVoteId) && !blackList.contains(sch.getSchemeUserId().intValue())
                                    && OutTicketUtil.checkOutTicketConfig(configMap, sch)) {//控制参数模块
                                tempDefaultVoteId = defaultTempVote;
                                logger.info("[优化方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + "分票成功 触发控制参数-被分配给默认出票商" + defaultVoteId);
                            }
                            //竞彩奖金优化不支持多串过关
                            if(isMulPass(sch.getLotteryId(), sch.getSchemeContent())) {
                                logger.info("[优化方案拆票任务] 彩种=" + sch.getLotteryId() + " 方案号=" + sch.getSchemeOrderId() + " " + " 优化方案不支持多串过关");
                                throw new Exception();
                            }
                            Map<Integer, List<SchemeTicket>> ticketMap = orderTransForTicket(new SchemeVo(sch.getSchemeOrderId(),
                                    sch.getLotteryId(), sch.getPlayTypeId(), sch.getSchemeMultiple(), sch.getSchemeMoney(),
                                    sch.getSchemeStatus(), sch.getSchemeContent(), sch.getPeriod(), tempDefaultVoteId,
                                    getSchemeCodeSp(sch.getSchemeSpContent()), sch.getSchemeUserId(), sch.getEndTime(), false));
                            ticketOne.addAll(ticketMap.get(1));
                            ticketTwo.addAll(ticketMap.get(2));
                            if(StringUtil.isEmpty(defaultVoteId) || tempDefaultVoteId.equals(defaultTempVote) || tempDefaultVoteId.equals(defaultTempVoteTwo)) {//存入分票对象后续进行分票
                                ticketVo.putOrder(sch.getPlayTypeId(), sch.getSchemeOrderId());
                            }
                        }
                        ticketVo.putTicket(sch.getSchemeOrderId(), ticketOne);
                        ticketVo.addAllTicket(ticketTwo);
                    } catch (Exception e) {
                        logger.error("优化方案拆票任务-拆票异常 方案号=" + sch.getSchemeOrderId(), e);
                        sch.setSchemeStatus(SchemeConstants.SCHEME_STATUS_CPSB);
                        sch.setSchemeStatusDesc("拆票异常");
                        schemeService.updateSchemeTicketStatus(sch);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("优化方案拆票任务-查询待拆方案异常", ex);
        }
    }

    /**
     * 方案拆票方法实现类
     * @param scheme
     * @throws Exception
     */
    private Map<Integer, List<SchemeTicket>> orderTransForTicket(SchemeVo scheme) throws Exception {
        if (StringUtil.isEmpty(scheme.getSchemeContent())) {
            LoggerUtil.printError("方案拆票任务", scheme, "投注内容为空", logger);
            throw new Exception("投注内容为空");
        }

        int singleMoney = 2;//单注基本金额
        GameSplit split = GameSplit.getGameSplit(scheme.getLotteryId());
        StringBuffer content = new StringBuffer();
        String[] cs = PluginUtil.splitter(scheme.getSchemeContent(), ";");
        for (int i = 0; i < cs.length; i++) {
            if (StringUtil.isEmpty(cs[i])) {
                continue;
            }
            String playType = scheme.getPlayTypeId();
            if(LotteryUtils.isJc(scheme.getLotteryId())) {
                playType = LotteryConstants.jcWfPrefixPlayIdMaps.get(scheme.getLotteryId() + PluginUtil.splitter(cs[i], "|")[0]);
            }
            GamePluginAdapter plugin = InitPlugin.getPlugin(mapPlugin, playType);
            if(plugin == null) {
                throw new Exception("无法获取拆票插件");
            }
            GameCastCode gcc = plugin.parseGameCastCode(cs[i]);
            if (scheme.getLotteryId().equals(LotteryConstants.DLT) && gcc.getPlayMethod() == Lottery1500.PM_ZHUIJIA) {
                singleMoney = 3;
            }

            if (StringUtil.isNotEmpty(split)) {
                content.append(split.getSplitCode(cs[i]));//拆小票
            } else {
                content.append(cs[i]);
            }
            if(i != cs.length -1) {
                content.append(";");
            }
        }

        scheme.setSchemeContent(content.toString());
        List<String> tickets = splitMultipleMaxMoney(scheme.getLotteryId(), scheme.getSchemeContent(), scheme.getSchemeMultiple(), singleMoney);//拆倍数和金额
        int splitMoney = splitTicketMoney(tickets);//拆票后金额
        if(splitMoney == scheme.getSchemeMoney()) {
            LoggerUtil.printInfo("方案拆票任务", scheme, "拆票成功 拆票后金额=" + splitMoney + " 订单金额=" + scheme.getSchemeMoney(), logger);
            List<SchemeTicket> ticketList = new ArrayList<>();
            List<SchemeTicket> singleTicketList = new ArrayList<>();
            Map<Integer, List<SchemeTicket>> ticketMap = new HashMap<>();
            for(String codes : tickets) {
                String[] co = PluginUtil.splitter(codes, "_");
                SchemeTicket tk = new SchemeTicket();
                tk.setLotteryId(scheme.getLotteryId());
                tk.setSchemeId(scheme.getSchemeOrderId());
                tk.setPeriod(scheme.getPeriod());
                tk.setMoney(StringUtil.parseDouble(co[1]));
                tk.setMultiple(StringUtil.parseInt(co[2]));
                tk.setCodes(co[0]);
                tk.setTicketStatus(SchemeConstants.TICKET_STATUS_WAITING);
                tk.setTicketDesc(SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_WAITING));
                tk.setTicketId(TokenUtil.generateRandomKey("TK"));
                tk.setZhuiHao(scheme.isZhuiHao());
                tk.setEndTime(scheme.getEndTime());
                if(LotteryUtils.isJc(scheme.getLotteryId())) {//默认下单sp
                    tk.setCodesSp(getTicketSp(co[0], scheme.getSchemeSp()));
                }
                tk.setVoteId(scheme.getVoteId());
                //重新分配出票商
                getRateOfReturn(tk, scheme.getUserId());
                //混投单一玩法-转换玩法编号
                if(LotteryUtils.isJcht(scheme.getLotteryId()) && (tk.getCodes().startsWith(LotteryConstants.JCWF_PREFIX_JQS)
                        || tk.getCodes().startsWith(LotteryConstants.JCWF_PREFIX_BQC)
                        || tk.getCodes().startsWith(LotteryConstants.JCWF_PREFIX_CBF)
                        || tk.getCodes().startsWith(LotteryConstants.JCWF_PREFIX_SPF)
                        || tk.getCodes().startsWith(LotteryConstants.JCWF_PREFIX_RQSPF)
                        || tk.getCodes().startsWith(LotteryConstants.JCWF_PREFIX_SF)
                        || tk.getCodes().startsWith(LotteryConstants.JCWF_PREFIX_RFSF)
                        || tk.getCodes().startsWith(LotteryConstants.JCWF_PREFIX_SFC)
                        || tk.getCodes().startsWith(LotteryConstants.JCWF_PREFIX_DXF))) {
                    tk.setPlayTypeId(LotteryConstants.jcWfPrefixPlayIdMaps.get(scheme.getLotteryId() + PluginUtil.splitter(tk.getCodes(), "|")[0]));
                    if(!tk.getVoteId().equals(defaultVote)) {
                        singleTicketList.add(tk);//控制参数以外的且混投单一玩法的票 需要参与分票
                    } else {
                        ticketList.add(tk);
                    }
                } else {
                    tk.setPlayTypeId(scheme.getPlayTypeId());
                    ticketList.add(tk);
                }
            }
            ticketMap.put(1, ticketList);
            ticketMap.put(2, singleTicketList);
            return ticketMap;
        } else {//金额不一致
            LoggerUtil.printError("方案拆票任务", scheme, "金额不一致 拆票后金额=" + splitMoney + " 订单金额=" + scheme.getSchemeMoney(), logger);
            throw new Exception("金额不一致 拆票后金额=" + splitMoney + " 订单金额=" + scheme.getSchemeMoney());
        }
    }


    /**
     * 拆分最大倍数和最大金额
     * @param lotId
     * @param codes
     * @param multiple
     * @param singleMoney
     * @return
     * @throws Exception
     */
    private List<String> splitMultipleMaxMoney(String lotId, String codes, int multiple, int singleMoney) throws Exception {
        List<String> tickets = new ArrayList<>();
        HashMap<String, Integer> singleMap = new HashMap<>();//单式组合
        HashMap<String, Integer> multipleMap = new HashMap<>();//复式组合
        String[] cs = PluginUtil.splitter(codes, ";");
        for (int i = 0; i < cs.length; i++) {
            if(StringUtil.isEmpty(cs[i])) {
                continue;
            }
            String playType = lotId;
            if(LotteryUtils.isJc(lotId)) {
                playType = LotteryConstants.jcWfPrefixPlayIdMaps.get(playType + PluginUtil.splitter(cs[i], "|")[0]);
            }
            GamePluginAdapter plugin = InitPlugin.getPlugin(mapPlugin, playType);
            if(plugin == null) {
                throw new Exception("无法获取拆票插件");
            }
            GameCastCode gcc = plugin.parseGameCastCode(cs[i]);
            String key = cs[i] + "|" + gcc.getCastMoney();
            //除去和值的单式玩法
            if (gcc.getCastMoney() == singleMoney && gcc.getCastMethod() != GameCastMethodDef.CASTTYPE_HESHU) {
                Integer dsZs = singleMap.get(key);
                if (StringUtil.isEmpty(dsZs)) {
                    singleMap.put(key, Integer.valueOf(1));
                } else {
                    singleMap.put(key, Integer.valueOf(dsZs + 1));
                }
            }
            //复式玩法
            else {
                Integer fsZs = multipleMap.get(key);
                if (StringUtil.isEmpty(fsZs)) {
                    multipleMap.put(key, Integer.valueOf(1));
                } else {
                    multipleMap.put(key, Integer.valueOf(fsZs + 1));
                }
            }
        }

        int max = maxNumber(lotId);//彩种单式组合最大注数
        if(singleMap.size() > 0) {//处理单式组合
            Set<Integer> mulSet = new HashSet<Integer>();//存放分组以后的倍数
            Iterator<String> singleKey = singleMap.keySet().iterator();
            while (singleKey.hasNext()) {
                String key = singleKey.next();
                mulSet.add(singleMap.get(key));
            }
            specialLotteryTicket(lotId, multiple, mulSet, singleMap, max, tickets);
        }
        singleMap.clear();
        if(multipleMap.size() > 0) {//处理复式组合
            Iterator<String> mlKeys = multipleMap.keySet().iterator();
            while (mlKeys.hasNext()) {
                List<String> mulList = new ArrayList<>();
                String key = mlKeys.next();
                mulList.add(key);
                List<String> mList = splitTicket(mulList, multiple * multipleMap.get(key), max, getMaxMultiple(lotId));
                tickets.addAll(mList);
            }
        }
        multipleMap.clear();
        return tickets;
    }

    /**
     * 特殊彩种处理单式注数合并
     * @param lotId 彩种
     * @param multiple 购买倍数
     * @param mulSet 单注包含所有倍数
     * @param singleMap 单式组合
     * @param max 单式最大注数
     * @param tickets 返回票集合
     * @throws Exception
     */
    private static void specialLotteryTicket(String lotId, int multiple, Set<Integer> mulSet, HashMap<String, Integer> singleMap, int max, List<String> tickets) throws Exception {
        if(StringUtil.isEmpty(mulSet)) {
            return;
        }
        int maxMultiple = getMaxMultiple(lotId);
        Iterator<Integer> bsIterator = mulSet.iterator();
        while (bsIterator.hasNext()) {//处理单式组合
            Integer mulValue = bsIterator.next();
            Iterator<String> keys = singleMap.keySet().iterator();
            //福彩3D、排列3
            if (LotteryConstants.FC3D.equals(lotId) || LotteryConstants.PL3.equals(lotId)) {
                List<String> array1 = null, array2 = null, array3 = null;
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (singleMap.get(key).intValue() == mulValue.intValue()) {
                        String[] tmpCode = PluginUtil.splitter(key, ":");
                        byte playType = PluginUtil.toByte(tmpCode[1]);
                        if (playType == Lottery1030.TDPLAYTYPE_SINGLE3) { //直选
                            if (array1 == null) {
                                array1 = new ArrayList<>();
                            }
                            array1.add(key);
                        } else if (playType == Lottery1030.TDPLAYTYPE_COMBINATION3) { //组三
                            if (array2 == null) {
                                array2 = new ArrayList<>();
                            }
                            array2.add(key);
                        } else { //组六
                            if (array3 == null) {
                                array3 = new ArrayList<>();
                            }
                            array3.add(key);
                        }
                    }
                }
                tickets.addAll(splitTicket(array1, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array2, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array3, multiple * mulValue, max, maxMultiple));
            }
            //11选5
            else if (GameContains.is11x5(lotId)) {
                List<String> array1 = null, array2 = null, array3 = null, array4 = null, array5 = null, array6 = null,
                        array7 = null, array8 = null, array9 = null, array10 = null, array11 = null, array12 = null;
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (singleMap.get(key).intValue() == mulValue.intValue()) {
                        String[] tmpCode = PluginUtil.splitter(key, ":");
                        byte playType = PluginUtil.toByte(tmpCode[1]);
                        if (playType == Lottery1560.R1) {//任1
                            if (array1 == null) {
                                array1 = new ArrayList<>();
                            }
                            array1.add(key);
                        } else if (playType == Lottery1560.R2) {//任2
                            if (array2 == null) {
                                array2 = new ArrayList<>();
                            }
                            array2.add(key);
                        } else if (playType == Lottery1560.R3) {//任3
                            if (array3 == null) {
                                array3 = new ArrayList<>();
                            }
                            array3.add(key);
                        } else if (playType == Lottery1560.R4) {//任4
                            if (array4 == null) {
                                array4 = new ArrayList<>();
                            }
                            array4.add(key);
                        } else if (playType == Lottery1560.R5) {//任5
                            if (array5 == null) {
                                array5 = new ArrayList<>();
                            }
                            array5.add(key);
                        } else if (playType == Lottery1560.R6) {//任6
                            if (array6 == null) {
                                array6 = new ArrayList<>();
                            }
                            array6.add(key);
                        } else if (playType == Lottery1560.R7) {//任7
                            if (array7 == null) {
                                array7 = new ArrayList<>();
                            }
                            array7.add(key);
                        } else if (playType == Lottery1560.R8) {//任8
                            if (array8 == null) {
                                array8 = new ArrayList<>();
                            }
                            array8.add(key);
                        } else if (playType == Lottery1560.Q2) {//前二直
                            if (array9 == null) {
                                array9 = new ArrayList<>();
                            }
                            array9.add(key);
                        } else if (playType == Lottery1560.Q3) {//前三直
                            if (array10 == null) {
                                array10 = new ArrayList<>();
                            }
                            array10.add(key);
                        } else if (playType == Lottery1560.Z2) {//前二组
                            if (array11 == null) {
                                array11 = new ArrayList<>();
                            }
                            array11.add(key);
                        } else {//前三组
                            if (array12 == null) {
                                array12 = new ArrayList<>();
                            }
                            array12.add(key);
                        }
                    }
                }
                tickets.addAll(splitTicket(array1, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array2, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array3, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array4, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array5, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array6, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array7, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array8, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array9, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array10, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array11, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array12, multiple * mulValue, max, maxMultiple));
            //快3
            } else if (GameContains.isK3(lotId)) {
                List<String> array1 = null, array2 = null, array3 = null, array4 = null, array5 = null, array6 = null, array7 = null, array8 = null;
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (singleMap.get(key).intValue() == mulValue.intValue()) {
                        String[] tmpCode = PluginUtil.splitter(key, ":");
                        byte playType = PluginUtil.toByte(tmpCode[1]);
                        if (playType == Lottery1050.PM_SUM) {//和值
                            if (array1 == null) {
                                array1 = new ArrayList<>();
                            }
                            array1.add(key);
                        } else if (playType == Lottery1050.PM_THREE_SINGLE) {//三同号单选
                            if (array2 == null) {
                                array2 = new ArrayList<>();
                            }
                            array2.add(key);
                        } else if (playType == Lottery1050.PM_THREEALL) {//三同号通选
                            if (array3 == null) {
                                array3 = new ArrayList<>();
                            }
                            array3.add(key);
                        } else if (playType == Lottery1050.PM_THREE_NOSAME) {//三不同号
                            if (array4 == null) {
                                array4 = new ArrayList<>();
                            }
                            array4.add(key);
                        } else if (playType == Lottery1050.PM_THREE) {//三连号通选
                            if (array5 == null) {
                                array5 = new ArrayList<>();
                            }
                            array5.add(key);
                        } else if (playType == Lottery1050.PM_TWO_SINGLE) {//二同号单选
                            if (array6 == null) {
                                array6 = new ArrayList<>();
                            }
                            array6.add(key);
                        } else if (playType == Lottery1050.PM_TWO_MUL) {//二同号复选
                            if (array7 == null) {
                                array7 = new ArrayList<>();
                            }
                            array7.add(key);
                        } else if (playType == Lottery1050.PM_TWO_NOSAME) {//二不同号
                            if (array8 == null) {
                                array8 = new ArrayList<>();
                            }
                            array8.add(key);
                        }
                    }
                }
                tickets.addAll(splitTicket(array1, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array2, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array3, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array4, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array5, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array6, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array7, multiple * mulValue, max, maxMultiple));
                tickets.addAll(splitTicket(array8, multiple * mulValue, max, maxMultiple));
            } else {
                List<String> list = new ArrayList<>();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (singleMap.get(key).intValue() == mulValue.intValue()) {
                        list.add(key);
                    }
                }
                tickets.addAll(splitTicket(list, multiple * mulValue, max, maxMultiple));
                list.clear();
            }
        }
    }

    /**
     * 拆最大倍数和最大金额
     * @param list 号码组合
     * @param multiple 倍数
     * @param maxNum 单张票最大单式注数
     * @return
     */
    private static List<String> splitTicket(List<String> list, int multiple, int maxNum, int maxMultiple) throws Exception {
        if(StringUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        int mc = 0;
        if (multiple % maxMultiple == 0) {
            mc = multiple / maxMultiple;
        } else {
            mc = multiple / maxMultiple + 1;
        }

        List<String> lstTicket = new ArrayList<>();
        int money = 0;
        for (int i = 1; i <= mc; i++) {
            int mul = (i == mc) ? multiple - (i - 1) * maxMultiple : maxMultiple;
            String tempCode = "";
            int count = 0;
            for (int j = 0; j < list.size(); j++) {
                String[] cos = PluginUtil.splitter(list.get(j), "|");
                String temp = "";
                for (int k = 0; k < cos.length - 1; k++) {
                    if (k != cos.length - 2) {
                        temp += cos[k] + "|";
                    } else {
                        temp += cos[k];
                    }
                }

                int sumMoney = Integer.parseInt(cos[cos.length - 1]);
                if (sumMoney * mul >= maxMoney) {//拆倍数
                    int maxMul = maxMoney / sumMoney;
                    int num = 0;
                    if (mul % maxMul == 0) {
                        num = mul / maxMul;
                    } else {
                        num = mul / maxMul + 1;
                    }
                    for (int k = 0; k < num; k++) {
                        int m = (k == (num - 1)) ? (mul - maxMul * (num - 1)) : maxMul;
                        lstTicket.add(temp + "_" + m * sumMoney + "_" + m);
                    }
                } else {
                    if (money + sumMoney * mul >= maxMoney) {
                        tempCode = tempCode.substring(0, tempCode.length() - 1);
                        lstTicket.add(tempCode + "_" + money + "_" + mul);

                        tempCode = temp + ";";
                        count = 1;
                        money = sumMoney * mul;
                    } else {
                        money += sumMoney * mul;
                        tempCode += temp + ";";
                        count++;

                        if (tempCode.length() >= 1500 || count >= maxNum || money >= maxMoney) {
                            tempCode = tempCode.substring(0, tempCode.length() - 1);
                            lstTicket.add(tempCode + "_" + money + "_" + mul);
                            tempCode = "";
                            count = 0;
                            money = 0;
                        }
                    }
                }
            }

            if (tempCode.length() > 0) {
                tempCode = tempCode.substring(0, tempCode.length() - 1);
                lstTicket.add(tempCode + "_" + money + "_" + mul);
                money = 0;
            }
        }
        return lstTicket;
    }

    /**
     * 获取彩种单张票支持的最大单式注数
     * @param lotId
     * @return
     */
    private static int maxNumber(String lotId) throws Exception {
        int max;
        if (GameContains.isBjdc(lotId)) {
            max = 1;
        } else if (GameContains.isJczq(lotId)) {
            max = 1;
        } else if (GameContains.isJclq(lotId)) {
            max = 1;
        } else if (GameContains.isGyj(lotId)) {
            max = 1;
        } else if (GameContains.isK3(lotId)) {
            max = 1;
        } else if (GameContains.isZc(lotId)) {
            max = 3;
        } else {
            max = 5;
        }
        return max;
    }

    /**
     * 获取单个彩种支持的最大倍数
     * @param lotId
     * @return
     */
    private static int getMaxMultiple(String lotId) throws Exception {
        int max;
        if (GameContains.isFc(lotId)) {
            max = 50;
        } else {
            max = 50;
        }
        return max;
    }

    /**
     * 汇总拆票后总金额
     * @return
     */
    private int splitTicketMoney(List<String> tickets) throws Exception {
        int splitMoney = 0;
        if(StringUtil.isEmpty(tickets)) {
            return 0;

        }
        for (String tk : tickets) {
            String[] ks = PluginUtil.splitter(tk, "_");
            if (StringUtil.isNotEmpty(ks) && ks.length == 3){
                splitMoney += StringUtil.parseInt(ks[1]) ;
            }
        }
        return splitMoney;
    }

    /**
     * 获取系统控制参数列表
     * @return
     * @throws Exception
     */
    private Map<String, TicketConfig> getTicketConfig(String voteId) throws Exception {
        Map<String, TicketConfig> configMap = null;//系统控制参数
        TicketVote vote = ticketService.queryTicketVoteInfo(voteId);
        if(StringUtil.isNotEmpty(vote) && ((!voteId.equals(defaultVote) && vote.getStatus() == 1) || voteId.equals(defaultVote))) {
            if (memcache.contains(Constants.TICKET_CONFIG)) {
                configMap = (Map<String, TicketConfig>) memcache.get(Constants.TICKET_CONFIG);
            } else {
                List<TicketConfig> configList = ticketService.queryTicketConfigAll();
                configMap = new HashMap<>();
                for (TicketConfig tc : configList) {
                    configMap.put(tc.getLotteryId() + tc.getPlayType(), tc);
                }
                memcache.set(Constants.TICKET_CONFIG, configMap, 60 * 60 * 24);//24小时
            }
        }
        return configMap;
    }

    /**
     * 拆票时默认写入用户下单时的sp
     * @param codes
     * @param spMap
     * @return
     */
    public static String getTicketSp(String codes, Map<String, String> spMap) {
        if(StringUtil.isEmpty(codes) || StringUtil.isEmpty(spMap)) {
            return null;
        }
        String[] cs = PluginUtil.splitter(codes, "|");
        if(cs.length != 2 && cs.length != 3) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        String[] sps = PluginUtil.splitter(cs[1], ",");
        if (codes.indexOf(">") > -1) {//混投
            for (int k=0; k<sps.length; k++) {
                String[] ms = PluginUtil.splitter(sps[k], ">");
                buffer.append(ms[0]);
                buffer.append("->");
                String[] xs = PluginUtil.splitter(ms[1], "=");
                buffer.append(xs[0]);
                buffer.append("=");
                String[] ss = PluginUtil.splitter(xs[1], "/");
                for (int n=0; n<ss.length; n++) {
                    buffer.append(ss[n].replaceAll("\\:","").replaceAll("\\-",""));
                    String key = ms[0] + "->" + xs[0] + "->" + ss[n];
                    if(!spMap.containsKey(key)) {
                        return null;
                    }
                    buffer.append(spMap.get(key));
                    if(n != ss.length - 1) {
                        buffer.append("/");
                    }
                }
                if(k != sps.length - 1) {
                    buffer.append(",");
                }
            }
        } else {
            for (int k=0; k<sps.length; k++) {
                String[] ms = PluginUtil.splitter(sps[k], "=");
                buffer.append(ms[0]);
                buffer.append("=");
                String[] xs = PluginUtil.splitter(ms[1], "/");
                for (int n=0; n<xs.length; n++) {
                    buffer.append(xs[n].replaceAll("\\:","").replaceAll("\\-",""));
                    String key = ms[0] + "->" + cs[0] + "->" + xs[n];
                    if(!spMap.containsKey(key)) {
                        return null;
                    }
                    buffer.append(spMap.get(key));
                    if(n != xs.length - 1) {
                        buffer.append("/");
                    }
                }
                if(k != sps.length - 1) {
                    buffer.append(",");
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 将用户订单sp串格式化为map用来生成票对应的sp串-算奖使用
     * @param schemeCodeSp
     * @return
     */
    public static Map<String, String> getSchemeCodeSp(String schemeCodeSp) {
        Map<String, String> spMap = new HashMap<>();
        if(StringUtil.isEmpty(schemeCodeSp)) {
            return spMap;
        }
        schemeCodeSp = schemeCodeSp.replaceAll("\\(", "&").replaceAll("\\)", "");
        String[] cs = PluginUtil.splitter(schemeCodeSp, "|");
        if(cs.length != 2 && cs.length != 3) {
            return null;
        }

        String[] tdan = PluginUtil.splitter(cs[1], "$");
        for(String dan : tdan) {
            String[] sps = PluginUtil.splitter(dan, ",");
            if (schemeCodeSp.indexOf(">") > -1) {//混投
                for (String sp : sps) {
                    String[] ms = PluginUtil.splitter(sp, ">");
                    String[] xs = PluginUtil.splitter(ms[1], "+");
                    for (String ch : xs) {
                        String[] alx = PluginUtil.splitter(ch, "=");
                        String[] fs = PluginUtil.splitter(alx[0], "&");//针对让球胜平负、让分胜负、大小分处理分值
                        if (alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RQSPF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                            alx[0] = fs[0];
                        }
                        String[] gs = PluginUtil.splitter(alx[1], "/");
                        for (String g : gs) {
                            String[] s = PluginUtil.splitter(g, "&");
                            if (alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                                spMap.put(ms[0] + "->" + alx[0] + "->" + s[0], "&" + fs[1] + "@" + s[1]);
                            } else {
                                spMap.put(ms[0] + "->" + alx[0] + "->" + s[0], "@" + s[1]);
                            }
                        }
                    }
                }
            } else {
                for (String sp : sps) {
                    String[] ms = PluginUtil.splitter(sp, "=");
                    String[] fs = PluginUtil.splitter(ms[0], "&");//针对让球胜平负、让分胜负、大小分处理分值
                    if (cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RQSPF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                        ms[0] = fs[0];
                    }
                    String[] xs = PluginUtil.splitter(ms[1], "/");
                    for (String ch : xs) {
                        String[] alx = PluginUtil.splitter(ch, "&");
                        if (cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
                            spMap.put(ms[0] + "->" + cs[0] + "->" + alx[0], "&" + fs[1] + "@" + alx[1]);
                        } else {
                            spMap.put(ms[0] + "->" + cs[0] + "->" + alx[0], "@" + alx[1]);
                        }
                    }
                }
            }
        }
        return spMap;
    }

    /**
     * 计算竞彩回报率重新分配出票商
     * @param ticket
     */
    public static void getRateOfReturn(SchemeTicket ticket, Long userId) {
        if(StringUtil.isNotEmpty(ticket.getVoteId())) {
            String tempVote = ticket.getVoteId();
            if(tempVote.equals(defaultTempVote) || tempVote.equals(defaultTempVoteTwo)) {//过渡出票商
                if(LotteryUtils.isKp(ticket.getLotteryId())) {
                    ticket.setVoteId(defaultVote);//重置出票商
                }
                else if(LotteryUtils.isJcht(ticket.getLotteryId())) {
                    ticket.setVoteId("");
                    if(StringUtil.isEmpty(ticket.getCodes()) || StringUtil.isEmpty(ticket.getCodesSp())) {
                        return;
                    }
                    //足球codes: HH|20180423001>SPF=0,20180423002>SPF=1,20180423009>CBF=2:0/1:1,20180423010>RQSPF=1|4*1
                    //篮球codes: HH|20180423301>SFC=13/01,20180423302>RFSF=0,20180424303>SFC=02/03|3*1
                    //单一玩法codes: SFC|20180423301=13,20180423302=02|2*1|DXF|20180423301=3,20180423302=3|2*1
                    String[] cs = PluginUtil.splitter(ticket.getCodes(), "|");
                    if(cs.length != 3) {
                        return;
                    }
                    int passNumber = isMulPass(cs[2]);
                    if(passNumber == 0) {
                        return;
                    }
                    //混投玩法
                    String[] codesps = PluginUtil.splitter(ticket.getCodesSp(), ",");
                    if(passNumber != codesps.length) {//串关数和场次数相等
                        return;
                    }
                    //schemeCodeSp: 20180423001->SPF=0@2.200,20180423002->SPF=1@4.300,20180423009->CBF=11@6.500/20@9.000,20180423010->RQSPF=1@3.300
                    //schemeCodeSp: 20180423301->SFC=01@5.250/13@5.150,20180423302->RFSF=0&-5.5@1.750,20180424303->SFC=03@4.000/02@3.250
                    //schemeCodeSp: 20180423301=3&216.5@1.750,20180423302=3&209.5@1.690|20180423301=13@4.900,20180423302=02@3.650
                    double prizeSp = 1.0;
                    for(String csp : codesps) {
                        double tempSp = 10000.0;
                        String[] sph = PluginUtil.splitter(csp, "=")[1].split("\\/");
                        for(String sh : sph) {
                            double sp = StringUtil.parseDouble(PluginUtil.splitter(sh, "@")[1]);
                            if(sp < 1) {
                                sp = 1.0;
                            }
                            if(tempSp > sp) {
                                tempSp = sp;
                            }
                        }
                        prizeSp *= tempSp;
                    }

                    double rateReturn = CalculationUtils.spValue(prizeSp*100);//回报率
                    if(tempVote.equals(defaultTempVote)) {//控制参数内的票根据配置回报率
                        //回报率在某个范围内
                        String range = SysConfig.getTicketMinRateReturn();
                        if (StringUtil.isNotEmpty(range) && range.split("\\|").length == 3) {
                            String[] ranges = range.split("\\|");
                            if (ranges[2].equals("open")) {
                                if (rateReturn >= StringUtil.parseDouble(ranges[0]) && rateReturn <= StringUtil.parseDouble(ranges[1])) {
                                    ticket.setVoteId(defaultVote);//重置出票商
                                    return;
                                }
                            }
                        }
                        //回报率在某个范围外
                        String overStemp = SysConfig.getTicketRateReturn();
                        if (StringUtil.isNotEmpty(overStemp) && overStemp.split("\\|").length == 4) {
                            String[] overStemps = overStemp.split("\\|");
                            if (overStemps[3].equals("open")) {
                                if (rateReturn <= StringUtil.parseDouble(overStemps[0]) || (rateReturn >= StringUtil.parseDouble(overStemps[1]) && rateReturn <= StringUtil.parseDouble(overStemps[2]))) {
                                    ticket.setVoteId(defaultVote);//重置出票商
                                }
                            }
                        }
                    }
                    else //测试用户的票根据固定回报率
                    {
                        if(ticket.getMoney() <= 100 && rateReturn < 3000) {
                            ticket.setVoteId(defaultVote);//重置出票商
                        }
                        if(ticket.getMoney() > 100 && rateReturn < 2000) {
                            ticket.setVoteId(defaultVote);//重置出票商
                        }
                    }
                }
            }
        }
        //固定金额的票范围指定出票商
        String fixedMoney = SysConfig.getTicketFixedMoneyRange();
        if (LotteryUtils.isJcht(ticket.getLotteryId()) && StringUtil.isNotEmpty(fixedMoney) && fixedMoney.split("\\|").length == 5) {
            String[] fixedStemps = fixedMoney.split("\\|");
            if (fixedStemps[4].equals("open")) {
                boolean isPass = false;
                String[] pass = fixedStemps[2].split("\\,");
                for(int m=0; m<pass.length; m++) {
                    if(pass[m].equals(ticket.getCodes().split("\\|")[2])) {
                        isPass = true;
                        break;
                    }
                }
                if(isPass && StringUtil.isEmpty(ticket.getVoteId()) && ticket.getMoney() > StringUtil.parseDouble(fixedStemps[0]) && ticket.getMoney() < StringUtil.parseDouble(fixedStemps[1])) {
                    ticket.setVoteId(fixedStemps[3]);//重置出票商
                }
            }
        }
        //某些用户的票固定到某个出票商
        String fixedUser = SysConfig.getTicketFixedUser();
        if (LotteryUtils.isJcht(ticket.getLotteryId()) && StringUtil.isNotEmpty(fixedUser) && fixedUser.split("\\|").length == 3) {
            String[] fixedStemps = fixedUser.split("\\|");
            if (fixedStemps[2].equals("open")) {
                boolean isInjo = false;
                String[] users = fixedStemps[0].split("\\,");
                for(int m=0; m<users.length; m++) {
                    if(users[m].equals(userId+"")) {
                        isInjo = true;
                        break;
                    }
                }
                if(isInjo) {
                    ticket.setVoteId(fixedStemps[1]);//重置出票商
                }
            }
        }
        //某些用户的票固定到某个出票商2
        String fixedUserTwo = SysConfig.getTicketFixedUserTwo();
        if (LotteryUtils.isJcht(ticket.getLotteryId()) && StringUtil.isNotEmpty(fixedUserTwo) && fixedUserTwo.split("\\|").length == 3) {
            String[] fixedStemps = fixedUserTwo.split("\\|");
            if (fixedStemps[2].equals("open")) {
                boolean isInjo = false;
                String[] users = fixedStemps[0].split("\\,");
                for(int m=0; m<users.length; m++) {
                    if(users[m].equals(userId+"")) {
                        isInjo = true;
                        break;
                    }
                }
                if(isInjo) {
                    ticket.setVoteId(fixedStemps[1]);//重置出票商
                }
            }
        }
    }

    /**
     * 获取串关方式是否为多串过关或多个过关
     * @param pass
     * @return
     *      0-true 大于0-返回串关数
     */
    private static int isMulPass(String pass) {
        String[] ps = PluginUtil.splitter(pass, ",");
        if(ps.length > 1) {
            return 0;
        }

        int passNum = 1;
        for(String s : ps) {
            passNum = Integer.parseInt(s.substring(2));
            if(passNum > 1) {
                return 0;
            } else {
                passNum = Integer.parseInt(s.substring(0,1));
            }
        }
        return passNum;
    }

    /**
     * 是否为多串过关
     * @param codes
     * @return
     */
    private boolean isMulPass(String lotteryId, String codes) {
        if(!LotteryUtils.isJcht(lotteryId)) {
            return false;
        }
        if(StringUtil.isEmpty(codes)) {
            return false;
        }
        String[] cs = codes.split("\\|");
        if(cs.length != 3) {
            return false;
        }
        String[] ps = cs[2].split("\\,");
        for(String s : ps) {
            if(Integer.parseInt(s.substring(2)) > 1) {
                return true;
            }
        }
        return false;
    }
}
