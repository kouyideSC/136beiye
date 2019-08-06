package com.caipiao.ticket.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeZhuiHao;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.ticket.TicketConfig;
import com.caipiao.domain.ticket.TicketVoteRule;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.ticket.split.SplitTicket;
import com.caipiao.ticket.vo.SplitTicketVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 出票规则
 * Created by Kouyi on 2017/12/04.
 */
public class OutTicketUtil {
    /**
     * 分票规则实现类[按订单分票=即每个订单产生的N个小票将只会被分配到同一个出票商 保证其完整性]
     * 分票实现原理 1.各玩法类型对应的分票规则汇总在一起 计算各条规则中分票比例占有的区间段
     *           2.生成一个随机数，匹配到某个区间的索引值 即为分配到的出票商规则
     *           3.取出该规则对应的出票商编号 设置到订单对应的所有小票中
     * @param ticketVo
     * @param mapRule
     */
    public static synchronized void randomSplitTicket(SplitTicketVo ticketVo, Map<String, List<TicketVoteRule>> mapRule, Logger logger) throws Exception {
        if(StringUtil.isEmpty(mapRule) || StringUtil.isEmpty(ticketVo) || StringUtil.isEmpty(ticketVo.getMapOrder())) {
            return;
        }

        //将控制参数以内的或混投非单一玩法的票进行分票处理
        if(StringUtil.isNotEmpty(ticketVo.getMapTicket())) {
            for (Map.Entry<String, List<String>> orderEntry : ticketVo.getMapOrder().entrySet()) {
                String playType = orderEntry.getKey();
                List<String> orderList = orderEntry.getValue();
                for (String orderId : orderList) {
                    if (!ticketVo.getMapTicket().containsKey(orderId)) {
                        continue;
                    }
                    TicketVoteRule curRule = getVoteRule(playType, mapRule);
                    if(StringUtil.isNotEmpty(curRule)) {
                        List<SchemeTicket> schemeTickets = ticketVo.getMapTicket().get(orderId);
                        for (SchemeTicket ticket : schemeTickets) {
                            if(ticket.getVoteId().equals(SplitTicket.defaultVote)) {
                                continue;
                            }
                            if (StringUtil.isEmpty(ticket.getVoteId())) {
                                ticket.setVoteId(curRule.getVoteId());//分配出票商
                                logger.info("[方案分票任务] 玩法=" + playType + " 方案号=" + orderId + " 票号=" + ticket.getTicketId() + " 分票成功 被分配给出票商" + curRule.getVoteId());
                            }
                        }
                    }
                }
            }
        }

        //将控制参数以外的单一玩法的票进行分票处理
        if(StringUtil.isNotEmpty(ticketVo.getTicketList())) {
            for (SchemeTicket singleTicket : ticketVo.getTicketList()) {
                if(StringUtil.isNotEmpty(singleTicket.getVoteId()) || singleTicket.getVoteId().equals(SplitTicket.defaultVote)) {
                    continue;
                }
                String playType = singleTicket.getPlayTypeId();
                TicketVoteRule curRule = getVoteRule(playType, mapRule);
                if (StringUtil.isNotEmpty(curRule)) {
                    singleTicket.setVoteId(curRule.getVoteId());//分配出票商
                    logger.info("[方案分票任务] 玩法=" + playType + " 方案号=" + singleTicket.getSchemeId() + " 票号=" + singleTicket.getTicketId() + " 分票成功 被分配给出票商" + curRule.getVoteId());
                }
            }
        }
    }

    /**
     * 分票规则核心实现-从N个出票商的同一玩法中 按照比例取出某一个出票商的规则
     * @param playType
     * @param mapRule
     * @return
     */
    private static TicketVoteRule getVoteRule(String playType, Map<String, List<TicketVoteRule>> mapRule) {
        TicketVoteRule curRule = null;
        List<TicketVoteRule> rules = mapRule.get(playType);
        if(StringUtil.isEmpty(rules)) {//管理员未给该玩法配置分票规则 则不出票
            return curRule;
        }
        if(rules.size() > 1) {//进行分票匹配
            double sumRate = 0d;//累加总概率值-可超过100%
            for (TicketVoteRule rule : rules) {
                sumRate += StringUtil.parseDouble(rule.getRate());
            }

            int size = rules.size();//总规则数
            List<Double> rateList = new ArrayList<>(size);//计算单个规则的概率占总概率区间 并放入集合
            Double tempRate = 0d;
            for (TicketVoteRule vr : rules) {
                tempRate += StringUtil.parseDouble(vr.getRate());
                rateList.add(tempRate / sumRate);
            }

            double nextDouble = Math.random();//随机取出一个数
            rateList.add(nextDouble);
            Collections.sort(rateList);
            int index = rateList.indexOf(nextDouble);//规则索引
            curRule = rules.get(index);//获取分配规则
        } else {
            curRule = rules.get(0);//只有一个出票商规则时 没必要再随机分票
        }
        return curRule;
    }

    /**
     * 是否设置默认出票商-追号订单
     * @param configMap
     * @param scheme
     * @return
     */
    public static boolean checkOutTicketConfig(Map<String, TicketConfig> configMap, SchemeZhuiHao scheme) {
        return checkConfig(configMap, scheme.getLotteryId(), scheme.getLotteryId(), scheme.getSchemeContent(),
                scheme.getSchemeMultiple(), scheme.getSchemeMoney());
    }

    /**
     * 是否设置默认出票商-普通订单
     * @param configMap
     * @param scheme
     * @return
     */
    public static boolean checkOutTicketConfig(Map<String, TicketConfig> configMap, Scheme scheme) {
        return checkConfig(configMap, scheme.getLotteryId(), scheme.getPlayTypeId(), scheme.getSchemeContent(),
                scheme.getSchemeMultiple(), scheme.getSchemeMoney());
    }

    /**
     * 检查是否触发系统控制参数规则
     * @param configMap
     * @param lottId
     * @param content
     * @param mul
     * @param money
     * @return true-触发 false-不触发
     */
    private static boolean checkConfig(Map<String, TicketConfig> configMap, String lottId, String playTypeId, String content, int mul, double money) {
        try {
            if (StringUtil.isEmpty(configMap)) {
                return false;
            }
            //投注竞彩半全场、猜比分、进球数、胜分差玩法以及慢频数字彩和老足彩不触发规则
            if((LotteryUtils.isJc(lottId) && (playTypeId.equals(LotteryConstants.JCZQBQC)
                    || playTypeId.equals(LotteryConstants.JCZQCBF)
                    || playTypeId.equals(LotteryConstants.JCZQJQS)
                    || playTypeId.equals(LotteryConstants.JCLQSFC)
                    || playTypeId.equals(LotteryConstants.GYJ))
                    || playTypeId.equals(LotteryConstants.GJ))
                || LotteryUtils.isZC(lottId) || LotteryUtils.isMp(lottId)) {
                return false;
            }
            String conKey = "";
            if (!LotteryUtils.isJc(lottId)) {
                String[] contents = PluginUtil.splitter(content, ";");
                for(String con : contents) {
                    String[] cs = PluginUtil.splitter(con, ":");
                    conKey = lottId + cs[1] + ":" + cs[2];
                }
            } else {
                conKey = lottId + playTypeId;
            }
            TicketConfig config = configMap.get(conKey);
            if (StringUtil.isNotEmpty(config)) {
                if(StringUtil.isEmpty(config.getMaxMultiple())) {
                    config.setMaxMultiple(0);
                }
                if(StringUtil.isEmpty(config.getMaxMoney())) {
                    config.setMaxMoney(0d);
                }
                //是否超过最低倍数&&是否超过最低金额并低于最高金额
                if (mul >= config.getMaxMultiple() && (money >= config.getMaxMoney() && money < config.getMaxPrize())) {
                    return true;
                }
                /*//是否超过最大串关方式
                if (LotteryUtils.isJc(lottId) && StringUtil.isNotEmpty(config.getMaxPassType())
                        && minPassType(contents[2]) > minPassType(config.getMaxPassType())) {
                    return true;
                }*/
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 计算最小串关方式
     * @param pass
     * @return
     */
    private static int minPassType(String pass) {
        if(StringUtil.isEmpty(pass)) {
            return 0;
        }
        int min = 0;
        String[] cps = PluginUtil.splitter(pass,",");
        for(String ps : cps) {
            int p = StringUtil.parseInt(PluginUtil.splitter(ps, "*"));
            if(p > 0) {
                if(min == 0) {
                    min = p;
                } else {
                    if(min < p) {
                        min = p;
                    }
                }
            }
        }
        return min;
    }
}
