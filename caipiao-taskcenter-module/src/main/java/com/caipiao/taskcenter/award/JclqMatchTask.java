package com.caipiao.taskcenter.award;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.vo.JclqAwardInfo;
import com.caipiao.memcache.MemCached;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.match.JclqMatchService;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.service.user.UserService;
import com.caipiao.taskcenter.award.util.JclqAwardUtil;
import com.caipiao.taskcenter.award.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 竞彩篮球场次任务
 * Created by kouyi on 2017/11/22.
 */
@Component("jclqMatchTask")
public class JclqMatchTask extends JclqAwardUtil {
    private static Logger logger = LoggerFactory.getLogger(JclqMatchTask.class);
    private HashMap<String, JclqAwardInfo> mapMatchs = new HashMap<>();
    private HashMap<String, GamePluginAdapter> pluginMap = new HashMap<>();
    private long lastLoad = 0;

    @Autowired
    private JclqMatchService jclqMatchService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private SchemeService schemeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private MemCached memCached;

    /**
     * 场次状态处理
     */
    public void jinCaiLqMatchTask() {
        try {
            // 180s增量加载新期次
            if (lastLoad < System.currentTimeMillis() - 1000 * 180 || mapMatchs.isEmpty()) {
                loadDataBaseMatch();
                lastLoad = System.currentTimeMillis();
            }

            List<String> endMatch = new ArrayList<String>();
            Iterator<String> iterator = mapMatchs.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JclqAwardInfo match = mapMatchs.get(key);
                try {
                    int state = processJclqMatch(match);
                    if (state == 99) {
                        LoggerUtil.printJcInfo("竞彩篮球-场次任务", match.getMatchCode(), "卸载完成", logger);
                        endMatch.add(key);
                    }
                } catch (Exception e) {
                    LoggerUtil.printJcError("竞彩篮球-场次任务", match.getMatchCode(), "处理异常", e, logger);
                }
            }

            for (String key : endMatch) {
                mapMatchs.remove(key);
                loadDataBaseMatch();
            }
            endMatch.clear();
        } catch (Exception ex) {
            logger.error("竞彩篮球-场次任务处理异常", ex);
        }
    }

    /**
     * 处理场次场次
     * @param match
     * @return
     * @throws Exception
     */
    public int processJclqMatch(JclqAwardInfo match) throws Exception {
        int state = match.getState();
        switch (state) {
            case LotteryConstants.MATCHJJ_STATE_DEFAULT: {//场次截止:投注截止
                Date endTime = match.getEndTime();
                if (endTime.getTime() < System.currentTimeMillis()) {
                    match.setState(LotteryConstants.MATCHJJ_STATE_ONE);
                    if(match.getStatus() != LotteryConstants.STATUS_CANCEL) {//如果比赛未取消,则设置为截止，取消的比赛不设置截止
                        match.setStatus(LotteryConstants.STATUS_EXPIRE);
                    }
                    jclqMatchService.updateMatchStatusById(match);
                    LoggerUtil.printJcInfo("竞彩篮球-场次截止任务", match.getMatchCode(), "截止销售", logger);
                    //刷新对阵文件任务
                    taskService.saveTask(new Task(Constants.JCLQ_MATCH_UPDATE_TASK));
                }
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_ONE: {//方案撤单:竞彩篮球开赛前30秒还没出票
                Date endTime = match.getMatchTime();
                if (endTime.getTime() < (System.currentTimeMillis() + 30000)) {
                    boolean flag = cancelJclqScheme(schemeService, ticketService, match);
                    if(flag) {
                        match.setState(LotteryConstants.MATCHJJ_STATE_TWO);
                        if(match.getStatus() == LotteryConstants.STATUS_CANCEL) {
                            match.setState(LotteryConstants.MATCHJJ_STATE_THREE);//取消的比赛跳过赛果获取、直接可审核
                        }
                        jclqMatchService.updateMatchStatusById(match);
                        LoggerUtil.printJcInfo("竞彩篮球-自动撤单任务", match.getMatchCode(), "撤单完成", logger);
                    }
                }
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_TWO: {//等待抓取赛果自动改为3
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_THREE: {//竞彩篮球赛果审核
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_FOUR: {//系统审核赛果[确保运营填写格式正确]
                if(!isCancel(match)) {
                    match.setState(LotteryConstants.MATCHJJ_STATE_FILE);
                    LoggerUtil.printJcInfo("竞彩篮球-系统审核赛果任务", match.getMatchCode(), "系统审核赛果完成 比分=" + (match.getStatus()==1 ? "已取消" : match.getScore()), logger);
                } else {
                    LoggerUtil.printJcError("竞彩篮球-系统审核赛果任务", match.getMatchCode(), "运营审核赛果不正确 流程回退重新审核", logger);
                    match.setState(LotteryConstants.MATCHJJ_STATE_THREE);
                }
                jclqMatchService.updateMatchStatusById(match);
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_FILE: {//方案过关
                if(match.getStatus() == LotteryConstants.STATUS_SELL) {
                    LoggerUtil.printJcError("竞彩篮球-过关任务", match.getMatchCode(), "暂停过关-场次状态:销售中", null, logger);
                    break;
                }
                boolean flag = guoGuanJclqMatch(schemeService, ticketService, match, pluginMap);
                if(flag) {//全部完成
                    match.setState(LotteryConstants.MATCHJJ_STATE_SIX);
                    jclqMatchService.updateMatchStatusById(match);
                    LoggerUtil.printJcInfo("竞彩篮球-过关任务", match.getMatchCode(), "过关完成", logger);
                }
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_SIX: {//奖金汇总
                boolean flag = prizeMoneySummary(schemeService, ticketService, activityService, userService, memCached, match);
                if(flag) {//全部完成
                    match.setState(LotteryConstants.MATCHJJ_STATE_SEVEN);
                    jclqMatchService.updateMatchStatusById(match);
                    LoggerUtil.printJcInfo("竞彩篮球-奖金汇总任务", match.getMatchCode(), "奖金汇总完成", logger);
                }
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_SEVEN: {//核对奖金
                boolean flag = followSchemeRewardMoney(schemeService, match);//汇总神单打赏
                if(flag) {//全部完成
                    match.setState(LotteryConstants.MATCHJJ_STATE_EIGHT);
                    jclqMatchService.updateMatchStatusById(match);
                    LoggerUtil.printJcInfo("竞彩篮球-核对奖金任务", match.getMatchCode(), "奖金核对完成", logger);
                }
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_EIGHT: {//自动派奖
                boolean flag = authSendSmallMoney(schemeService, userService, match);
                if(flag) {//全部完成
                    match.setState(LotteryConstants.MATCHJJ_STATE_NINE);
                    jclqMatchService.updateMatchStatusById(match);
                    LoggerUtil.printJcInfo("竞彩篮球-自动派奖任务", match.getMatchCode(), "自动派奖完成", logger);
                }
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_NINE: {//过关统计
                match.setState(LotteryConstants.MATCHJJ_STATE_TEN);
                jclqMatchService.updateMatchStatusById(match);
                LoggerUtil.printJcInfo("竞彩篮球-过关统计任务", match.getMatchCode(), "过关统计完成", logger);
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_TEN: {//用户战绩统计
                //boolean flag = followSchemeUserDataStatis(schemeService, match);
                //if(flag) {//全部完成
                    match.setState(LotteryConstants.MATCHJJ_STATE_ELEVEN);
                    jclqMatchService.updateMatchStatusById(match);
                    LoggerUtil.printJcInfo("竞彩篮球-战绩统计任务", match.getMatchCode(), "战绩统计完成", logger);
                //}
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_ELEVEN: {//派送返点
                match.setState(LotteryConstants.MATCHJJ_STATE_TWELVE);
                jclqMatchService.updateMatchStatusById(match);
                LoggerUtil.printJcInfo("竞彩篮球-派送返点任务", match.getMatchCode(), "派送返点完成", logger);
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_TWELVE: {//场次处理结束
                match.setState(LotteryConstants.MATCHJJ_STATE_END);
                jclqMatchService.updateMatchStatusById(match);
                LoggerUtil.printJcInfo("竞彩篮球-任务结束", match.getMatchCode(), "场次处理结束", logger);
                break;
            }
            case LotteryConstants.MATCHJJ_STATE_END: {
                break;
            }
        }
        return state;
    }

    //比分验证-流程是否取消
    private boolean isCancel(JclqAwardInfo match) {
        if(StringUtil.isEmpty(match)) {
            return true;
        }
        //比赛取消
        if(match.getStatus() == LotteryConstants.STATUS_CANCEL) {
            return false;
        }
        //比赛销售中或停售
        if(match.getStatus() == LotteryConstants.STATUS_SELL || match.getStatus() == LotteryConstants.STATUS_STOP) {
            return true;
        }
        if(StringUtil.isEmpty(match.getScore())) {
            return true;
        }
        if(PluginUtil.splitter(match.getScore(),":").length != 2 || match.getScore().length() < 3 || !NumberUtil.isNumber(match.getScore().replaceAll(":",""))) {
            return true;
        }
        return false;
    }

    /**
     * 加载同步数据库中的比赛数据
     * @throws ServiceException
     * @throws Exception
     */
    public void loadDataBaseMatch() throws Exception {
        List<MatchBasketBall> list = jclqMatchService.queryJclqStatusNoHandlerList();
        if(StringUtil.isEmpty(list)) {
            return;
        }

        for(MatchBasketBall match : list) {
            String key = LotteryConstants.JCZQ + "_" + match.getId();
            JclqAwardInfo last = mapMatchs.get(key);
            if(StringUtil.isEmpty(last)) {
                JclqAwardInfo award = new JclqAwardInfo();
                BeanUtils.copyProperties(award, match);
                setMatch(award, match);
                mapMatchs.put(key, award);
            } else {
                last.setStatus(match.getStatus());
                last.setState(match.getState());
                last.setEndTime(match.getEndTime());
                last.setMatchTime(match.getMatchTime());
                setMatch(last, match);
            }
        }
    }

    /**
     * 比分同步
     * @param award
     * @param match
     */
    private void setMatch(JclqAwardInfo award, MatchBasketBall match) {
        if(match.getStatus() == LotteryConstants.STATUS_EXPIRE && match.getState() >= LotteryConstants.MATCHJJ_STATE_THREE) {
            if(StringUtil.isNotEmpty(match.getScore())) {
                match.setScore(match.getScore().trim());
            }
            if(StringUtil.isNotEmpty(match.getScore()) && PluginUtil.splitter(match.getScore(),":").length == 2) {
                award.setScore(match.getScore());
                String[] score = PluginUtil.splitter(match.getScore(),":");
                award.sethScore(Integer.parseInt(score[1]));
                award.setgScore(Integer.parseInt(score[0]));
            }
        }
    }
}