package com.caipiao.service.scheme;

import com.caipiao.common.constants.*;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.CombineUtil;
import com.caipiao.common.lottery.FilterPlayTypeUtil;
import com.caipiao.common.lottery.Lottery1700Utils;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.scheme.SchemeUtils;
import com.caipiao.common.util.*;
import com.caipiao.dao.common.ActivityMapper;
import com.caipiao.dao.lottery.LotteryMapper;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.match.*;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.dao.ticket.TicketMapper;
import com.caipiao.dao.user.*;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.SchemeBean;
import com.caipiao.domain.base.TicketBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.jjyh.JjyhOne;
import com.caipiao.domain.jjyh.JjyhTwo;
import com.caipiao.domain.jjyh.JjyhSchemeInfo;
import com.caipiao.domain.jjyh.MatchInfo;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.match.GyjMatch;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeFollow;
import com.caipiao.domain.scheme.SchemeMatches;
import com.caipiao.domain.scheme.SchemeZhuiHao;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserDetail;
import com.caipiao.domain.vo.JclqAwardInfo;
import com.caipiao.domain.vo.JczqAwardInfo;
import com.caipiao.memcache.MemCached;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.jcutil.JcCastCode;
import com.caipiao.plugin.lqutil.LqCastCode;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.redis.RedisSingle;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import com.util.combine.CombineSplit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * 方案业务处理类
 * @author  mcdog
 */
@Service("schemeService")
public class SchemeService
{
    private static Logger logger = LoggerFactory.getLogger(SchemeService.class);

    private static final String schemeSavePrefix = "scheme_save_";//保存方案-前缀(缓存key)

    private HashMap<String, GamePluginAdapter> pluginMaps = new HashMap<String, GamePluginAdapter>();//彩种插件集合

    private HashMap<String,LotteryUtils> lotteryUtilsMap = new HashMap<String, LotteryUtils>();//彩种工具类集合

    @Autowired
    private RedisSingle redis;

    @Autowired
    private MemCached memcached;

    @Autowired
    private PeriodMapper periodMapper;

    @Autowired
    private MatchFootBallMapper footBallMapper;

    @Autowired
    private MatchBasketBallMapper basketBallMapper;

    @Autowired
    private MatchFootBallSpMapper footBallSpMapper;

    @Autowired
    private MatchBasketBallSpMapper basketBallSpMapper;

    @Autowired
    private LotteryMapper lotteryMapper;

    @Autowired
    private SchemeMapper schemeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Autowired
    private MatchGyjMapper matchGyjMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 保存方案(投注)
     * @author  mcdog
     * @param   schemeBean  方案业务处理对象
     * @param   result      处理结果对象
     */
    public synchronized void saveScheme(SchemeBean schemeBean, ResultBean result) throws ServiceException
    {
        //校验app系统是否开放
        String openStatus = SysConfig.getString("SYSTEM_OPEN_STATUS");
        if(StringUtil.isEmpty(openStatus) || "0".equals(openStatus))
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"系统升级维护中!暂停销售。");
        }
        //校验投注系统是否开放
        openStatus = SysConfig.getString("TRADE_OPEN_STATUS");
        if(StringUtil.isEmpty(openStatus) || "0".equals(openStatus))
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"系统升级维护中!暂停销售。");
        }
        //校验是否启用指定彩种开放投注
        openStatus = SysConfig.getString("TRADE_LOTTERY_OPEN_STATUS");
        if(StringUtil.isNotEmpty(openStatus) && !"0".equals(openStatus) && openStatus.indexOf(schemeBean.getLid()) < 0)
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"系统升级维护中!暂停销售。");
        }
        //3秒内只允许投注一次
        if(redis.containsKey(schemeSavePrefix + schemeBean.getUserId()))
        {
            logger.error("[保存方案]投注过于频繁!用户编号=" + schemeBean.getUserId());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120011,ErrorCode_API.ERROR_SCHEM_120011_MSG);
        }
        redis.setNx((schemeSavePrefix + schemeBean.getUserId()),"1",3);//在缓存中存放发起方案的标识,设置有效期为3秒

        //校验投注项是否为空
        if(StringUtil.isEmpty(schemeBean.getTzcontent()))
        {
            logger.error("[保存方案]投注项不能为空!用户编号=" + schemeBean.getUserId());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 校验用户是否开通白名单
         */
        User user = null;
        try
        {
            user = userMapper.queryUserInfoById(schemeBean.getUserId());
            if(user == null || user.getIsWhite() != 1)
            {
                logger.error("[保存方案]用户白名单校验未通过!用户编号=" + schemeBean.getUserId());
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120000,ErrorCode_API.ERROR_SCHEM_XTSJWFYY_MSG);
            }
        }
        catch (Exception e)
        {
            logger.error("[保存方案]查询用户白名单发生异常!用户编号=" + schemeBean.getUserId() + ",异常信息:" + e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }

        /**
         * 校验彩种是否支持
         */
        //判断彩种插件是否支持
        String playTypeId = schemeBean.getLid();//玩法类型id,默认与彩种id一样
        if(LotteryUtils.isJc(schemeBean.getLid()))
        {
            //竞彩,根据投注串前缀确定玩法类型id
            String[] tzcontents = schemeBean.getTzcontent().split("\\|");
            playTypeId = LotteryConstants.jcWfPrefixPlayIdMaps.get(schemeBean.getLid() + tzcontents[0]);//设置玩法类型

            //校验过关方式是否重复
            if(!LotteryUtils.isGyj(schemeBean.getLid()))
            {
                String[] ggfs = tzcontents[2].split(",");
                List<String> ggfsList = new ArrayList<String>();
                for(String gg : ggfs)
                {
                    if(ggfsList.contains(gg))
                    {
                        logger.error("[保存方案]包含重复的过关方式" + gg + ",用户编号=" + schemeBean.getUserId() + ",投注选项=:" + schemeBean.getTzcontent());
                        throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                    }
                    ggfsList.add(gg);
                }
            }
        }
        //初始化彩种插件
        schemeBean.setPlayTypeId(playTypeId);
        GamePluginAdapter plugin = InitPlugin.getPlugin(pluginMaps,schemeBean.getPlayTypeId());
        if(plugin == null)
        {
            logger.error("[保存方案]找不到相关彩种插件!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120001);
        }
        //校验彩种合法性
        schemeBean.setLid(LotteryConstants.lotteryMap.get(schemeBean.getLid()));
        if(StringUtil.isEmpty(schemeBean.getLid()))
        {
            logger.error("[保存方案]彩种不合法!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120001);
        }
        //校验彩种销售状态
        Lottery lottery = (Lottery) memcached.get(LotteryConstants.lotteryPrefix + schemeBean.getLid());
        if(lottery == null)
        {
            lottery = lotteryMapper.queryLotteryInfo(schemeBean.getLid());
            memcached.set((LotteryConstants.lotteryPrefix + schemeBean.getLid()),lottery,5 * 60);//在缓存中保存5分钟
        }
        if(lottery == null || !checkLotterySellStatus(schemeBean,lottery))
        {
            logger.error("[保存方案]查询不到相关彩种信息!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120001);
        }

        /**
         * 数字彩,则校验号码组数
         */
        if(LotteryUtils.isSzc(schemeBean.getLid()))
        {
            String xztzzs = SysConfig.getString("TRADE_SZC_XZTZZS");
            if(StringUtil.isNotEmpty(xztzzs) && !"-1".equals(xztzzs))
            {
                int tzzs = Integer.parseInt(xztzzs);
                if(schemeBean.getTzcontent().split(";").length > tzzs)
                {
                    logger.error("[保存方案]号码组数超过系统设置的最大组数!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId() + ",投注项=" + schemeBean.getTzcontent());
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120001,"投注号码组数不能超过" + tzzs + "组");
                }
            }
        }

        /**
         * 根据彩种和玩法拆分投注串
         */
        //如果彩种为福彩3D/排列三且玩法为组三单式/组六单式,则拆分投注串
        if(LotteryConstants.FC3D.equals(schemeBean.getLid()) || LotteryConstants.PL3.equals(schemeBean.getLid()))
        {
            splitTzcontentForFc3dAndPl3(schemeBean,lottery);//拆分福彩3D/排列三投注串
        }
        //如果彩种为快三,则根据方案类型拆分智能追号信息,根据玩法拆分投注格式
        List<JSONObject> znzhinfoList = new ArrayList<JSONObject>();//智能追号信息
        int znzhSmultiple = 0;//智能追号总倍数
        if(LotteryUtils.isK3(schemeBean.getLid()))
        {
            //如果方案类型为智能追号,则解析智能追号信息
            if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZNZH)
            {
                JSONArray znzhinfoArray = JSONArray.fromObject(schemeBean.getZhinfos());
                if(znzhinfoArray == null || znzhinfoArray.size() == 0)
                {
                    logger.error("[保存方案]解析不出任何有效的追号期次信息!原始追号信息=" + schemeBean.getZhinfos() + ",用户编号=" + schemeBean.getUserId() + ",彩种=" + lottery.getName());
                    throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                }
                for(Object object : znzhinfoArray)
                {
                    JSONObject jsonObject = JSONObject.fromObject(object);

                    //校验倍数
                    if(lottery.getXzMaxSellMultiple() == 1 && jsonObject.getInt("smultiple") > lottery.getMaxSellMultiple())
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120005,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120005_MSG,new Object[]{lottery.getMaxSellMultiple()}));
                    }
                    else if(lottery.getXzMinSellMultiple() == 1 && jsonObject.getInt("smultiple") < lottery.getMinSellMultiple())
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120031,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120031_MSG,new Object[]{lottery.getMinSellMultiple()}));
                    }
                    //校验金额
                    if(lottery.getXzMaxSellMoney() == 1 && jsonObject.getDouble("money") > lottery.getMaxSellMoney())
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120020,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120020_MSG,new Object[]{lottery.getMaxSellMoney()}));
                    }
                    else if(lottery.getXzMinSellMoney() == 1 && jsonObject.getDouble("money") < lottery.getMinSellMoney())
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120032,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120032_MSG,new Object[]{lottery.getMinSellMoney()}));
                    }
                    znzhinfoList.add(jsonObject);
                    znzhSmultiple += jsonObject.getInt("smultiple");
                }
                schemeBean.setPeriod(znzhinfoList.get(0).getString("period"));//取智能追号的第一期(当前期)作为大方案的期次
            }
            //重新拆分投注串
            splitTzcontentForK3(schemeBean,lottery);//拆分快三投注串
        }
        //如果方案类型为奖金优化,则解析投注串
        List<JSONObject> yhinfosList = new ArrayList<JSONObject>();
        int yhzs = 0;//优化方案总注数
        int yhbs = 0;//优化方案总倍数
        double yhmoney = 0d;//优化方案总金额
        if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_YH)
        {
            //校验过关方式(多串过关不支持奖金优化)
            String[] sggfs = schemeBean.getTzcontent().split("\\|")[2].split(",");
            for(String ggfs : sggfs)
            {
                if(Integer.parseInt(ggfs.substring(ggfs.indexOf("*") + 1)) > 1)
                {
                    logger.error("[保存方案][奖金优化]奖金优化方案不支持过关方式" + ggfs + "!用户编号=" + schemeBean.getUserId() + ",彩种=" + lottery.getName());
                    throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                }
            }
            //提取奖金优化方案投注信息
            JSONArray yhinfosArray = JSONArray.fromObject(schemeBean.getYhinfos());
            if(yhinfosArray == null || yhinfosArray.size() == 0)
            {
                logger.error("[保存方案][奖金优化]解析不出任何有效的优化方案信息!原始优化方案信息=" + schemeBean.getYhinfos() + ",用户编号=" + schemeBean.getUserId() + ",彩种=" + lottery.getName());
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            //解析奖金优化方案投注信息
            for(Object object : yhinfosArray)
            {
                /**
                 * 校验投注串格式/倍数/金额
                 */
                //初始化彩种插件
                JSONObject jsonObject = JSONObject.fromObject(object);
                String yhtzcontent = jsonObject.getString("tzcontent");
                GamePluginAdapter yhplugin = InitPlugin.getPlugin(pluginMaps,LotteryConstants.jcWfPrefixPlayIdMaps.get(schemeBean.getLid() + yhtzcontent.split("\\|")[0]));
                if(plugin == null)
                {
                    logger.error("[保存方案][奖金优化]找不到相关彩种插件!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId());
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120001);
                }
                GameCastCode[] gameCastCodes = null;
                try
                {
                    gameCastCodes = yhplugin.parseGameCastCodes(jsonObject.getString("tzcontent"));
                }
                catch(Exception e)
                {
                    logger.error("[保存方案][奖金优化]单组投注项格式错误!单组投注项=" + jsonObject.getString("tzcontent")
                            + ",用户编号=" + schemeBean.getUserId()
                            + ",彩种=" + lottery.getName());
                    throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                }
                //读取投注串信息
                double dzmoney = 0d;//单组投注金额
                for(GameCastCode castCode : gameCastCodes)
                {
                    dzmoney += castCode.getCastMoney() * jsonObject.getInt("smultiple");//单组投注串的总金额需要 x 倍数
                    yhzs += castCode.getCastMoney() / 2;
                }
                //校验倍数
                if(lottery.getXzMaxSellMultiple() == 1 && jsonObject.getInt("smultiple") > lottery.getMaxSellMultiple())
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120005,
                            MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120005_MSG,new Object[]{lottery.getMaxSellMultiple()}));
                }
                else if(lottery.getXzMinSellMultiple() == 1 && jsonObject.getInt("smultiple") < lottery.getMinSellMultiple())
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120031,
                            MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120031_MSG,new Object[]{lottery.getMinSellMultiple()}));
                }
                //校验金额
                if(dzmoney <= 0 || dzmoney != jsonObject.getDouble("money"))
                {
                    logger.error("[保存方案][奖金优化]单组方案金额错误!单组方案金额=" + jsonObject.getDouble("money")
                            + ",单组方案实际金额=" + dzmoney
                            + ",单组投注项=" + jsonObject.getString("tzcontent")
                            + ",用户编号=" + schemeBean.getUserId()
                            + ",彩种=" + lottery.getName());
                    throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                }
                else if(lottery.getXzMaxSellMoney() == 1 && dzmoney > lottery.getMaxSellMoney())
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120020,
                            MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120020_MSG,new Object[]{lottery.getMaxSellMoney()}));
                }
                else if(lottery.getXzMinSellMoney() == 1 && dzmoney < lottery.getMinSellMoney())
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120032,
                            MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120032_MSG,new Object[]{lottery.getMinSellMoney()}));
                }
                //校验过关方式
                if(lottery.getGgfsFlag() == 0 && yhtzcontent.split("\\|")[2].split(",").length > 1)
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120033,ErrorCode_API.ERROR_SCHEM_120033_MSG);
                }
                yhbs += jsonObject.getInt("smultiple");
                yhmoney += dzmoney;
                yhinfosList.add(jsonObject);
            }
        }

        /**
         * 校验投注格式/金额/注数/倍数/理论奖金
         */
        //智能追号/奖金优化方案,方案倍数设置为1
        if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZNZH
                || schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_YH)
        {
            schemeBean.setSmultiple(1);
        }
        else
        {
            //判断方案最大倍数
            if(lottery.getXzMaxSellMoney() == 1)
            {
                if(schemeBean.getSmultiple() > lottery.getMaxSellMultiple())
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120005,
                            MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120005_MSG,new Object[]{lottery.getMaxSellMultiple()}));
                }
            }
            //判断方案最小倍数
            if(lottery.getXzMinSellMultiple() == 1)
            {
                //非奖金优化且包含多个过关方式的竞彩方案,则根据系统参数配置中的最小倍数限制来判断
                if(LotteryUtils.isJc(schemeBean.getLid())
                        && schemeBean.getStype() != SchemeConstants.SCHEME_TYPE_YH
                        && schemeBean.getTzcontent().split("\\|")[2].split(",").length > 1)
                {
                    openStatus = SysConfig.getString("TRADE_JC_MULTI_GGFS_XZMINBS");
                    if(StringUtil.isNotEmpty(openStatus) && !"-1".equals(openStatus))
                    {
                        int xzbs = Integer.parseInt(openStatus);
                        if(schemeBean.getSmultiple() < xzbs)
                        {
                            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120031,
                                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120031_MSG,new Object[]{xzbs}));
                        }
                    }
                }
                else
                {
                    if(schemeBean.getSmultiple() < lottery.getMinSellMultiple())
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120031,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120031_MSG,new Object[]{lottery.getMinSellMultiple()}));
                    }
                }
            }
        }
        //判断单方案最多追号期数
        if((schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZH && schemeBean.getpSum() > lottery.getMaxZhNum())
                || (schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZNZH && znzhinfoList.size() > lottery.getMaxZhNum()))
        {
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120008,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120008_MSG,new Object[]{lottery.getMaxZhNum()}));
        }
        //校验投注串格式(非奖金优化方案)
        GameCastCode[] gameCastCodes = null;
        double money = 0d;//方案金额
        double dbmoney = 0d;//单倍金额(针对追号/智能追号)
        int zs = 0;//方案注数
        Map<String,String> wfMap = new HashMap<String,String>();//玩法集合
        try
        {
            gameCastCodes = plugin.parseGameCastCodes(schemeBean.getTzcontent());
        }
        catch(Exception e)
        {
            logger.error("[保存方案]投注项格式错误!投注项=" + schemeBean.getTzcontent() + ",用户编号=" + schemeBean.getUserId() + ",彩种=" + lottery.getName());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        if(gameCastCodes == null)
        {
            logger.error("[保存方案]投注项格式错误!投注项=" + schemeBean.getTzcontent() + ",用户编号=" + schemeBean.getUserId() + ",彩种=" + lottery.getName());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //读取投注串信息
        for(GameCastCode castCode : gameCastCodes)
        {
            money += castCode.getCastMoney() * schemeBean.getSmultiple();//单组投注串的总金额需要 x 倍数
            if(castCode.getPlayMethod() == 2 && LotteryConstants.DLT.equals(schemeBean.getLid()))
            {
                zs += castCode.getCastMoney() / 3;//大乐透追加(每注3元)
            }
            else
            {
                zs += castCode.getCastMoney() / 2;
            }
            String wf = castCode.getPlayMethod() + ":" + castCode.getCastMethod();
            wfMap.put(wf,wf);
        }
        //如果是追号投注,则方案总金额还需 x 追号期数
        if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZH)
        {
            money = money * schemeBean.getpSum();
            schemeBean.setYwcSum(0);//设置默认已完成追期数为0
        }
        //如果是智能追号,则方案总金额等于各追期方案金额的总和
        else if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZNZH)
        {
            dbmoney = money;
            money = money * znzhSmultiple;
            schemeBean.setYwcSum(0);//设置默认已完成追期数为0
            schemeBean.setpSum(znzhinfoList.size());
        }
        //如果是奖金优化方案,则方案金额等于各组优化方案金额的总和,方案注数等于各组优化方案注数的总和
        else if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_YH)
        {
            //校验奖金优化总金额是否>=原始投注项金额 x 2
            if(yhmoney < money * 2)
            {
                logger.error("[保存方案][奖金优化]优化后的方案总金额不能小于方案优化前总金额的2倍!投注项=" + schemeBean.getTzcontent()
                        + ",投注金额=" + schemeBean.getMoney()
                        + ",实际所需金额=" + money
                        + ",用户编号=" + schemeBean.getUserId()
                        + ",彩种=" + lottery.getName());
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120022,
                        MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120022_MSG,new Object[]{money}));
            }
            //校验奖金优化总注数(不能超过500注)
            if(zs > 500)
            {
                logger.error("[保存方案][奖金优化]优化方案总注数不能超过500注!投注项=" + schemeBean.getTzcontent()
                        + ",方案实际注数=" + zs
                        + ",用户编号=" + schemeBean.getUserId()
                        + ",彩种=" + lottery.getName());
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120023,ErrorCode_API.ERROR_SCHEM_120023_MSG);
            }
            money = yhmoney;
            zs = yhzs;
            schemeBean.setpSum(null);//设置追期总数为空
            schemeBean.setYwcSum(null);//设置已完成期数为空
        }
        else
        {
            schemeBean.setpSum(null);//设置追期总数为空
            schemeBean.setYwcSum(null);//设置已完成期数为空
        }
        //判断实际方案金额和传入的方案金额是否符合
        if(money <= 0 || money != schemeBean.getMoney())
        {
            logger.error("[保存方案]投注金额错误!投注项:" + schemeBean.getTzcontent()
                    + ",投注金额=" + schemeBean.getMoney()
                    + ",实际所需金额=" + money
                    + ",用户编号=" + schemeBean.getUserId()
                    + ",彩种=" + lottery.getName());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120021,ErrorCode_API.ERROR_SCHEM_120021_MSG);
        }
        //判断方案最大金额
        if(lottery.getXzMaxSellMoney() == 1 && money > lottery.getMaxSellMoney())
        {
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120006,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120006_MSG,new Object[]{lottery.getMaxSellMoney()}));
        }
        else if(lottery.getXzMinSellMoney() == 1 && money < lottery.getMinSellMoney())
        {
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120032,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120032_MSG,new Object[]{lottery.getMinSellMoney()}));
        }
        //判断方案最大注数
        if(zs == 0 || zs > LotteryConstants.maxSchemeZs)
        {
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120004,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120004_MSG,new Object[]{LotteryConstants.maxSchemeZs}));
        }
        //任九/竞彩,则判断系统是否有限制最多选择场次数
        if(LotteryConstants.RXJ.equals(schemeBean.getLid()) || LotteryUtils.isJc(schemeBean.getLid()))
        {
            String maxccs = SysConfig.getString("TRADE_LOTTERY_MAXCC");
            if(StringUtil.isNotEmpty(maxccs) && maxccs.indexOf(schemeBean.getLid()) > -1)
            {
                String[] maxccArray = maxccs.split(";");
                for(String maxcc : maxccArray)
                {
                    if(maxcc.indexOf(schemeBean.getLid()) > -1)
                    {
                        maxccArray = maxcc.split("\\=");
                        int xzcc = Integer.parseInt(maxccArray[1]);
                        int realcc = 0;
                        if(LotteryConstants.RXJ.equals(schemeBean.getLid()))
                        {
                            String[] rxjtzxxs = schemeBean.getTzcontent().split(":")[0].split(",");
                            for(String rxjtzxx : rxjtzxxs)
                            {
                                if(!"#".equals(rxjtzxx))
                                {
                                    realcc ++;
                                }
                            }
                        }
                        else if(LotteryUtils.isJc(schemeBean.getLid()))
                        {
                            realcc = schemeBean.getTzcontent().split("\\|")[1].split(",").length;
                        }
                        if(realcc > xzcc)
                        {
                            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120034,
                                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120034_MSG,new Object[]{xzcc}));
                        }
                        break;
                    }
                }
            }
        }
        /**
         * 校验期次/场次销售状态
         */
        //猜冠军/冠亚军,判断期次的销售状态
        if(LotteryUtils.isGyj(schemeBean.getLid()))
        {
            checkGyjMatchSellStatus(schemeBean);
        }
        //竞彩足球/竞彩篮球,判断投注项中的场次的销售状态
        else if(LotteryUtils.isJc(schemeBean.getLid()))
        {
            //判断过关方式系统是否支持
            if(lottery.getGgfsFlag() == 0 && schemeBean.getTzcontent().split("\\|")[2].split(",").length > 1)
            {
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120033,ErrorCode_API.ERROR_SCHEM_120033_MSG);
            }
            checkJcMatchSellStatus(schemeBean);
        }
        //慢频/快频/足彩,判断期次的销售状态
        else if(LotteryUtils.isMp(schemeBean.getLid())
                || LotteryUtils.isKp(schemeBean.getLid())
                || LotteryUtils.isZC(schemeBean.getLid()))
        {
            try
            {
                checkSzcSellStatus(schemeBean);
            }
            catch(ServiceException e)
            {
                //拦截异常,如果抛出期次截止的异常,则查询当前实际在售期次信息
                if(e.getErrorCode() == ErrorCode_API.ERROR_SCHEM_120009)
                {
                    //查询彩种当前的在售期次
                    Period period = periodMapper.queryCurrentSellPeriod(schemeBean.getLid());
                    if(period == null)
                    {
                        logger.error("[保存方案]查询不到当前在售期次!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId());
                        throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                    }
                    Map<String,Object> periodMap = new HashMap<String,Object>();
                    periodMap.put("cperiod",period.getPeriod());//当前在售期次
                    periodMap.put("csetime",DateUtil.formatDate(period.getSellEndTime(),DateUtil.DEFAULT_DATE_TIME));//当前在售期次截止时间
                    result.setData(periodMap);
                }
                throw e;
            }
        }

        /**
         * 如果是竞彩,则判断系统是否有配置临近截止时间的投注倍数和注数的限制
         */
        if(LotteryUtils.isJc(schemeBean.getLid()))
        {
            String zsbsxzstr = SysConfig.getString("TRADE_JC_ENDTIME_XZZSBS");
            if(StringUtil.isNotEmpty(zsbsxzstr) && !"-1".equals(zsbsxzstr))
            {
                String[] zsbsxzs = zsbsxzstr.split(";");
                Calendar current = Calendar.getInstance();
                if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_YH)
                {
                    for(String zsbsxz : zsbsxzs)
                    {
                        String[] xzs = zsbsxz.split("\\|");
                        if(DateUtil.minutesBetween(current.getTime(),schemeBean.getEtime()) <= Integer.parseInt(xzs[0]))
                        {
                            if(StringUtil.isNotEmpty(xzs[1]) && yhzs > Integer.parseInt(xzs[1]))
                            {
                                logger.error("[保存方案]方案注数不能大于系统配置的临近截止时间的投注注数的限制!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId() + ",优化方案总注数=" + yhzs);
                                throw new ServiceException(ErrorCode_API.SERVER_ERROR,"当前时间段不能超过" + xzs[1] + "注");
                            }
                            else if(StringUtil.isNotEmpty(xzs[2]) && schemeBean.getSmultiple() > Integer.parseInt(xzs[2]))
                            {
                                logger.error("[保存方案]方案倍数不能大于系统配置的临近截止时间的投注倍数的限制!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId() + ",优化方案总倍数=" + yhbs);
                                throw new ServiceException(ErrorCode_API.SERVER_ERROR,"当前时间段不能超过" + xzs[2] + "倍");
                            }
                        }
                    }
                }
                else
                {
                    for(String zsbsxz : zsbsxzs)
                    {
                        String[] xzs = zsbsxz.split("\\|");
                        if(DateUtil.minutesBetween(current.getTime(),schemeBean.getEtime()) <= Integer.parseInt(xzs[0]))
                        {
                            if(StringUtil.isNotEmpty(xzs[1]) && zs > Integer.parseInt(xzs[1]))
                            {
                                logger.error("[保存方案]方案注数不能大于系统配置的临近截止时间的投注注数的限制!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId() + ",方案注数=" + zs);
                                throw new ServiceException(ErrorCode_API.SERVER_ERROR,"当前时间段不能超过" + xzs[1] + "注");
                            }
                            else if(StringUtil.isNotEmpty(xzs[2]) && schemeBean.getSmultiple() > Integer.parseInt(xzs[2]))
                            {
                                logger.error("[保存方案]方案倍数不能大于系统配置的临近截止时间的投注倍数的限制!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId() + ",方案倍数=" + schemeBean.getSmultiple());
                                throw new ServiceException(ErrorCode_API.SERVER_ERROR,"当前时间段不能超过" + xzs[2] + "倍");
                            }
                        }
                    }
                }
            }
        }

        /**
         * 设置方案参数,保存方案
         */
        //设置方案参数
        boolean isznzh = schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZNZH? true : false;//是否为智能追号
        schemeBean.setStatus(SchemeConstants.SCHEME_STATUS_DZF);//设置方案状态为待支付
        schemeBean.setSdesc(SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_DZF));//设置方案状态描述
        schemeBean.setMoney(money);//设置方案金额
        schemeBean.setSzs(zs);//设置方案注数
        schemeBean.setLname(lottery.getShortName());//设置方案所属彩种名称
        if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZNZH)
        {
            schemeBean.setStype(SchemeConstants.SCHEME_TYPE_ZH);//智能追号方案类型设置为追号
        }
        //猜冠亚军,设置期次号/投注项赔率
        if(LotteryUtils.isGyj(schemeBean.getLid()))
        {
            schemeBean.setPeriod(schemeBean.getTzcontent().split("\\|")[1].split("\\=")[0]);//设置期次号
            schemeBean.setWtype(LotteryConstants.playMethodMaps.get(schemeBean.getTzcontent().split("\\|")[0]));//根据投注串的前缀标识去匹配玩法名称
            settingGyjTzContentSp(schemeBean);//设置投注项赔率
        }
        //竞彩,则设置过关方式/期次号/玩法名称/投注项赔率/让球/让分/大小分盘口
        else if(LotteryUtils.isJc(schemeBean.getLid()))
        {
            //设置过关方式
            String[] tzcontents = schemeBean.getTzcontent().split("\\|");
            String[] tzggfs = tzcontents[2].split(",");
            LotteryUtils.sortArrayWithGgfsByAsc(tzggfs);//过关方式升序排列
            String ggfs = "";
            for(String gg : tzggfs)
            {
                ggfs += "," + gg;
            }
            schemeBean.setTzcontent(tzcontents[0] + "|" + tzcontents[1] + "|" + ggfs.substring(1));//重新设置投注内容及过关方式
            schemeBean.setPeriod(schemeBean.getMatchList().get(0).toString().substring(0,8));//设置期次号(截取第一场的场次号)
            schemeBean.setWtype(LotteryConstants.playMethodMaps.get(schemeBean.getTzcontent().split("\\|")[0]));//根据投注串的前缀标识去匹配玩法名称
            settingTzContentSpAndLose(schemeBean);//设置投注项赔率/让球/让分/大小分盘口

            //设置方案盈利率
            if(StringUtil.isNotEmpty(schemeBean.getLprize()))
            {
                double lprize = 0d;
                if(schemeBean.getLprize().indexOf("-") > -1)
                {
                    lprize = Double.parseDouble(schemeBean.getLprize().substring(schemeBean.getLprize().indexOf("-") + 1).replace("元",""));
                }
                else if(schemeBean.getLprize().indexOf("~") > -1)
                {
                    lprize = Double.parseDouble(schemeBean.getLprize().substring(schemeBean.getLprize().indexOf("~") + 1).replace("元",""));
                }
                else if(schemeBean.getLprize().indexOf("～") > -1)
                {
                    lprize = Double.parseDouble(schemeBean.getLprize().substring(schemeBean.getLprize().indexOf("～") + 1).replace("元",""));
                }
                else
                {
                    lprize = Double.parseDouble(schemeBean.getLprize().replace("元",""));
                }
                schemeBean.setProfitMargin((int)((lprize / schemeBean.getMoney()) * 100));//设置理论盈利率
            }

            /**
             * 设置该方案是否可以分享
             */
            schemeBean.setIsShare(0);//默认设置为不可分享
            if(LotteryUtils.isJczq(schemeBean.getLid()) && schemeBean.getStype() != SchemeConstants.SCHEME_TYPE_YH)
            {
                //判断过关方式是否符合神单分享要求
                boolean shareFlag = true;
                boolean ishh = schemeBean.getTzcontent().startsWith(LotteryConstants.JCWF_PREFIX_HH);
                String[] tzcodes = schemeBean.getTzcontent().split("\\|")[1].split("\\$");
                for(String codes : tzcodes)
                {
                    String[] tzxxcodes = codes.split(",");
                    for(String tzxxcode : tzxxcodes)
                    {
                        int xxcount = 0;
                        if(ishh)
                        {
                            String[] xxcodes = tzxxcode.split("\\>")[1].split("\\+");
                            for(String xxcode : xxcodes)
                            {
                                xxcount += xxcode.split("\\=")[1].split("\\/").length;
                            }
                        }
                        else
                        {
                            xxcount += tzxxcode.split("\\=")[1].split("\\/").length;
                        }
                        if(xxcount > 5)
                        {
                            shareFlag = false;
                            break;
                        }
                    }
                    if(!shareFlag)
                    {
                        break;
                    }
                }
                if(shareFlag)
                {
                    //查询用户当天已发神单数,已发神单数<系统设置的用户每天发起神单的最大个数时,该方案才允许分享神单
                    Dto queryDto = new BaseDto("userId",schemeBean.getUserId());
                    queryDto.put("schemeType",SchemeConstants.SCHEME_TYPE_SD);
                    queryDto.put("createDate",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE));
                    int count = schemeMapper.queryUserSdCountOfDay(queryDto);
                    String maxSdcountOfday = SysConfig.getString("MAX_SDCOUNT_OFDAY");//从系统中获取用户每天发起神单的最大个数
                    int maxsdcount = StringUtil.isEmpty(maxSdcountOfday)? 3 : Integer.parseInt(maxSdcountOfday);//默认为3个
                    if(count < maxsdcount)
                    {
                        //查询该用户是否有与本方案投注内容相同的神单,如果有则本方案不允许神单分享
                        queryDto.put("lotteryId",schemeBean.getLid());
                        queryDto.put("schemeContent",schemeBean.getTzcontent());
                        count = schemeMapper.queryUserSdCountOfSameCotent(queryDto);
                        if(count <= 0)
                        {
                            schemeBean.setIsShare(1);//设置方案
                            schemeBean.setFetime(schemeBean.getEtime());//设置神单分享截止时间
                        }
                    }
                }
            }
        }
        //非竞彩,如果有多个玩法,则设置玩法为组合,否则,根据彩种id和玩法去匹配玩法名称
        else
        {
            if(wfMap.size() > 1)
            {
                schemeBean.setWtype("组合");
            }
            else
            {
                for(Map.Entry<String,String> entry : wfMap.entrySet())
                {
                    schemeBean.setWtype(LotteryConstants.playMethodMaps.get(schemeBean.getLid() + "-" + entry.getValue()));
                }
            }
        }
        //如果方案类型非追号类型,则判断是否为大单,如果为大单则设置大单状态为大单未审核(截止时间在一个小时内且注数满足系统配置的大单注数)
        if(schemeBean.getStype() != SchemeConstants.SCHEME_TYPE_ZH && SysConfig.isBigTicket(zs,schemeBean.getEtime()))
        {
            schemeBean.setBigOrderStatus(2);//设置大单状态为大单未审核
        }
        else
        {
            schemeBean.setBigOrderStatus(1);//设置大单状态为不是大单
        }
        //非追号方案则设置中奖是否停止为否(0-否 1-是)
        if(schemeBean.getStype() != SchemeConstants.SCHEME_TYPE_ZH)
        {
            schemeBean.setPrizeStop(0);
        }
        else
        {
            schemeBean.setPrizeStop(schemeBean.getPrizeStop() != null? schemeBean.getPrizeStop() : 0);
        }
        //设置方案编号
        String scodePrefix = "";//方案编号前缀
        if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZH)
        {
            scodePrefix = isznzh? SchemeConstants.SCHEME_SCODE_ZHZN : SchemeConstants.SCHEME_SCODE_ZH;
        }
        else if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_YH)
        {
            scodePrefix = SchemeConstants.SCHEME_SCODE_JJYH;
        }
        else
        {
            scodePrefix = getSchemeCodePrefix(schemeBean.getLid());
        }
        String randdom = "" + new Random().nextInt(10) + new Random().nextInt(10) + new Random().nextInt(10) + new Random().nextInt(10);//生成4位的随机数
        schemeBean.setScode(scodePrefix + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME) + randdom);

        //设置客户端来源及名称
        schemeBean.setClientSource(KeyConstants.loginUserMap.get(schemeBean.getAppId()));//设置客户端来源
        schemeBean.setClientSourceName(UserConstants.userSourceMap.get(schemeBean.getClientSource()));//设置客户端来源名称

        //商户渠道-默认00000为官方
        if(StringUtil.isEmpty(schemeBean.getSource()))
        {
            schemeBean.setSource("00000");//默认官方
        }
        //保存方案
        int count = schemeMapper.saveScheme(schemeBean);

        /**
         * 如果为追号,则保存追号详细
         */
        //追号方案,保存追号详细
        if(schemeBean.getStype() == SchemeConstants.SCHEME_TYPE_ZH && !isznzh)
        {
            if(count > 0 && schemeBean.getId() != null && schemeBean.getpSum() >= 1)
            {
                //根据起始期次查询包含起始期次的在售期信息
                Map<String,Object> params = new HashMap<String,Object>();
                params.put("lotteryId",schemeBean.getLid());
                params.put("startPeriod",schemeBean.getPeriod());
                params.put("psum",schemeBean.getpSum());
                List<Period> periodList = periodMapper.queryPeriodInfoByStartPeriod(params);

                //如果在售期次总数<追号总期数则不允许追号成功
                if(periodList == null || periodList.size() < schemeBean.getpSum())
                {
                    logger.error("[保存方案]当前在售总期次数不足以完成追号!在售期次数=" + (periodList == null? 0 : periodList.size())
                            + ",追号总期数=" + schemeBean.getpSum() + ",用户编号=" + schemeBean.getUserId());
                    throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                }
                //将查询到的在售期次信息依次取出作为追号期次保存到追号表中
                if(periodList != null && periodList.size() > 0)
                {
                    SchemeBean zhSchemeBean = new SchemeBean();
                    zhSchemeBean.setClientSource(schemeBean.getClientSource());//设置客户端来源
                    zhSchemeBean.setClientSourceName(schemeBean.getClientSourceName());//设置客户端来源名称
                    zhSchemeBean.setSid(schemeBean.getId());//设置追号方案所属方案id
                    zhSchemeBean.setUserId(schemeBean.getUserId());//设置追号方案发起人用户编号
                    zhSchemeBean.setLid(schemeBean.getLid());//设置追号方案彩种id
                    zhSchemeBean.setLname(schemeBean.getLname());//设置追号方案彩种名称
                    zhSchemeBean.setSmultiple(schemeBean.getSmultiple());//设置追号方案倍数
                    zhSchemeBean.setSzs(schemeBean.getSzs());//设置追号方案注数
                    zhSchemeBean.setMoney(schemeBean.getMoney() / schemeBean.getpSum());//设置追号方案金额(总方案金额/追号总期数)
                    zhSchemeBean.setStatus(SchemeConstants.SCHEME_STATUS_DZF);//设置追号方案状态为待支付
                    zhSchemeBean.setSdesc(SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_DZF));//设置追号方案状态描述
                    zhSchemeBean.setWtype(schemeBean.getWtype());//设置追号方案玩法
                    zhSchemeBean.setTzcontent(schemeBean.getTzcontent());//设置追号方案投注内容
                    int index = 1;
                    for(Period period : periodList)
                    {
                        zhSchemeBean.setScode(schemeBean.getScode() + "-"+ index);//设置追号方案编号(大方案编号 + "-" + 追期序号)
                        zhSchemeBean.setPeriod(period.getPeriod());//设置追号方案期次
                        zhSchemeBean.setEtime(period.getAuthorityEndTime());//设置追号方案截止时间
                        schemeMapper.saveZhScheme(zhSchemeBean);
                        index ++;
                    }
                }
            }
        }
        //智能追号,保存智能追期信息
        else if(isznzh)
        {
            if(count > 0 && schemeBean.getId() != null && znzhinfoList.size() > 0)
            {
                SchemeBean zhSchemeBean = new SchemeBean();
                zhSchemeBean.setClientSource(schemeBean.getClientSource());//设置客户端来源
                zhSchemeBean.setClientSourceName(schemeBean.getClientSourceName());//设置客户端来源名称
                zhSchemeBean.setSid(schemeBean.getId());//设置追号方案所属方案id
                zhSchemeBean.setUserId(schemeBean.getUserId());//设置追号方案发起人用户编号
                zhSchemeBean.setLid(schemeBean.getLid());//设置追号方案彩种id
                zhSchemeBean.setLname(schemeBean.getLname());//设置追号方案彩种名称
                zhSchemeBean.setStatus(SchemeConstants.SCHEME_STATUS_DZF);//设置追号方案状态为待支付
                zhSchemeBean.setSdesc(SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_DZF));//设置追号方案状态描述
                zhSchemeBean.setWtype(schemeBean.getWtype());//设置追号方案玩法
                zhSchemeBean.setTzcontent(schemeBean.getTzcontent());//设置追号方案投注内容
                int index = 1;
                for(JSONObject znzhinfo : znzhinfoList)
                {
                    String cperiod = znzhinfo.getString("period");
                    Period period = periodMapper.queryPeriodByPerod(schemeBean.getLid(),cperiod);//查询期次信息
                    if(period == null)
                    {
                        logger.error("[保存方案][保存智能追号]期次号不存在!期次号=" + cperiod + ",用户编号=" + schemeBean.getUserId() + ",彩种=" + lottery.getName());
                        throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                    }
                    zhSchemeBean.setSmultiple(znzhinfo.getInt("smultiple"));//设置追号方案倍数
                    zhSchemeBean.setSzs(schemeBean.getSzs());//设置追号方案注数
                    zhSchemeBean.setMoney(znzhinfo.getInt("smultiple") * dbmoney);//设置追号方案金额
                    zhSchemeBean.setScode(schemeBean.getScode() + "-"+ index);//设置追号方案编号(大方案编号 + "-" + 追期序号)
                    zhSchemeBean.setPeriod(cperiod);//设置追号方案期次
                    zhSchemeBean.setEtime(period.getAuthorityEndTime());//设置追号方案截止时间
                    schemeMapper.saveZhScheme(zhSchemeBean);
                    index ++;
                }
            }
        }

        /**
         * 如果为竞技彩,则保存对阵场次信息
         */
        if(LotteryUtils.isJc(schemeBean.getLid()) && !LotteryUtils.isGyj(schemeBean.getLid()))
        {
            if(count > 0 && schemeBean.getId() != null)
            {
                Map<String,Object> matchMap = new HashMap<String,Object>();
                for(Object matchCode : schemeBean.getMatchList())
                {
                    matchMap.put("schemeId",schemeBean.getId());//设置方案id
                    matchMap.put("schemeOrderId",schemeBean.getScode());//设置方案编号
                    matchMap.put("lotteryId",schemeBean.getLid());//设置彩种id
                    matchMap.put("matchCode",matchCode);//设置场次竞彩编号
                    schemeMapper.saveSchemeMatches(matchMap);//保存方案对阵信息
                }
            }
        }

        /**
         * 设置返回数据
         */
        //如果彩种为竞彩,则设置注数和倍数信息
        Map<String,Object> dataMap = new HashMap<String,Object>();
        if(LotteryUtils.isJc(schemeBean.getLid()))
        {
            dataMap.put("zs",zs);//注数
            dataMap.put("bs",schemeBean.getSmultiple());//倍数
        }
        //非竞彩,则设置期次信息
        else
        {
            dataMap.put("pid",schemeBean.getPeriod());//期次
        }
        try
        {
            //查询用户当前余额
            UserAccount userAccount = userAccountMapper.queryUserAccountInfoByUserId(schemeBean.getUserId());
            if(userAccount == null)
            {
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            dataMap.put("balance",String.format("%.2f",userAccount.getBalance()));
            dataMap.put("dbalance",(userAccount.getBalance() >= schemeBean.getMoney()? 0 : (String.format("%.2f",schemeBean.getMoney() - userAccount.getBalance()))));
        }
        catch (Exception e)
        {
            logger.error("[保存方案]查询用户当前余额发生异常!用户编号=" + schemeBean.getUserId() + lottery.getName() + ",异常信息=" + e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        try
        {
            //查询用户优惠券信息,判断方案类型,只有方案不是追号方案,才能使用优惠券
            List<Dto> couponList = new ArrayList<Dto>();
            if(schemeBean.getStype() != 1)
            {
                Dto couponQueryDto = new BaseDto("userId",schemeBean.getUserId());
                couponQueryDto.put("useStatus",1);
                couponQueryDto.put("useLotteryId",schemeBean.getLid());
                List<Dto> couponDataList = userCouponMapper.queryUserCoupons(couponQueryDto);
                couponList.addAll(filterCoupon(couponDataList,schemeBean));
            }
            dataMap.put("coupons",couponList);
        }
        catch (Exception e)
        {
            logger.error("[保存方案]查询用户优惠券发生异常!用户编号=" + schemeBean.getUserId() + lottery.getName() + ",异常信息=" + e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //设置其它返回数据
        dataMap.put("lname",schemeBean.getLname());//彩种名称
        dataMap.put("money",money);//方案金额
        dataMap.put("ymoney",money);//应付金额
        dataMap.put("sid",schemeBean.getId());//方案id
        dataMap.put("scode",schemeBean.getScode());//方案编号
        String rechargeDesc = SysConfig.getString("PAY_RECHARGE_DESC");//获取系统设置的充值描述
        dataMap.put("czdesc",StringUtil.isEmpty(rechargeDesc)? "" : rechargeDesc);//设置充值描述
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
        logger.info("[保存方案]方案保存成功!用户编号=" + schemeBean.getUserId()
                + ",方案id=" + schemeBean.getId()
                + ",方案编号=" + schemeBean.getScode()
                + ",方案金额=" + schemeBean.getMoney()
                + ",彩种=" + schemeBean.getLname());
    }

    /**
     * 方案确认购买(付款)
     * @author  mcdog
     * @param   params     业务参数对象
     * @param   result     处理结果对象
     */
    public synchronized void schemeConfirm(Dto params, ResultBean result) throws ServiceException
    {
        /**
         * 校验方案id/应付金额/实付金额是否为空,为空则返回系统异常
         */
        if(StringUtil.isEmpty(params.get("sid"))
                || StringUtil.isEmpty(params.get("ymoney"))
                || StringUtil.isEmpty(params.get("smoney")))
        {
            logger.error("[方案确认购买]参数校验不通过!用户编号=" + params.getAsString("userId") + ",请求参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 调用方案确认购买(付款)存储过程
         * 处理状态:1000-支付成功 -1000-支付失败 1001-金额错误 1002-方案不存在 1003-方案已截止 1004-方案已失效 1005-方案重复支付
         *        2000-追号方案不能使用优惠券 2001-优惠券无效 2002-优惠券不满足使用条件 3001-用户不存在 3002-用户余额不足
         */
        params.put("dcode",-1000);//设置默认处理状态
        params.put("dmsg","支付失败");//设置默认处理状态描述
        params.put("channelCode", PayConstants.CHANNEL_CODE_OUT_DRAWING);//设置支付渠道
        params.put("channelDesc",PayConstants.channelCodeMap.get(PayConstants.CHANNEL_CODE_OUT_DRAWING));//设置支付渠道描述
        params.put("clientFrom",KeyConstants.loginUserMap.get(params.getAsString("appId")));//设置客户端来源 0-WWW 1-IOS 2-ANDROID 3-H5 4-Other
        if(StringUtil.isEmpty(params.get("channelId"))) {
            schemeMapper.schemePay(params);//非渠道合作调用方案确认购买(付款)存储过程
        } else {
            params.put("cuid", "");//渠道不支持优惠券支付
            schemeMapper.schemePayChannel(params);//渠道调用方案确认购买(付款)存储过程
        }

        //提取处理状态码,dcode=1000表示付款成功
        logger.info("[方案确认购买]处理结果:dcode=" + params.getAsString("dcode") + ",dmsg=" + params.getAsString("dmsg") + ",用户编号=" + params.getAsString("userId"));
        int dcode = params.getAsInteger("dcode");
        if(dcode == 1000)
        {
            result.setErrorCode(ErrorCode.SUCCESS);
            Scheme scheme = schemeMapper.querySchemeInfoById(params.getAsLong("sid"));//获取方案
            if(scheme == null)
            {
                result.setData(new BaseDto());
                return;
            }
            //如果方案类型为跟单,则更新神单方案及神单跟单
            if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD)
            {
                try
                {
                    //查询神单方案
                    Scheme sdscheme = schemeMapper.querySchemeInfoById(scheme.getCopySchemeId());//查询神单方案
                    if(sdscheme == null)
                    {
                        logger.error("[方案确认购买]查询不到相关的神单方案!用户编号=" + params.getAsString("userId") + ",方案id=" + scheme.getId() + ",神单方案id=" + scheme.getCopySchemeId());
                    }
                    else
                    {
                        //保存神单跟单
                        Dto sdgdDto = new BaseDto();
                        sdgdDto.put("senderUserId",sdscheme.getSchemeUserId());//设置神单发起人用户编号
                        sdgdDto.put("senderSchemeId",sdscheme.getSchemeOrderId());//设置神单订单编号
                        sdgdDto.put("rewardProportion",sdscheme.getRemuneration());//设置神单提成比例
                        sdgdDto.put("followUserId",scheme.getSchemeUserId());//设置跟单人用户编号
                        sdgdDto.put("followSchemeId",scheme.getSchemeOrderId());//设置跟单订单编号
                        sdgdDto.put("followTime",DateUtil.formatDate(scheme.getCreateTime(),DateUtil.DEFAULT_DATE_TIME));//设置跟单时间
                        sdgdDto.put("followMoney",scheme.getSchemeMoney());//设置跟单金额
                        User gduser = userMapper.queryUserInfoById(scheme.getSchemeUserId());//查询跟单人
                        sdgdDto.put("followNickName",gduser.getNickName());//设置跟单人昵称
                        int count = schemeMapper.saveSdFollow(sdgdDto);
                        logger.info("[方案确认购买][保存神单跟单]保存" + (count > 0? "成功" : "失败") + ",用户编号=" + params.getAsString("userId") + ",方案id=" + scheme.getId());
                        if(count > 0)
                        {
                            //更新神单方案
                            Dto updateDto = new BaseDto("id",sdscheme.getId());
                            updateDto.put("redSafeHuardMoney",scheme.getSchemeMoney());
                            schemeMapper.updateSdScheme(updateDto);
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("[方案确认购买]更新神单方案及神单跟单记录发生异常!用户编号=" + params.getAsString("userId") + ",方案id=" + scheme.getId() + ",所属神单方案id=" + scheme.getCopySchemeId() + ",异常信息=" + e);
                }
            }
            //设置返回数据
            Dto dataDto = new BaseDto();
            dataDto.put("lid",scheme.getLotteryId());//设置方案所属彩种id
            dataDto.put("sid",scheme.getId());//设置方案id
            dataDto.put("zflag",scheme.getSchemeType() == 1? 1 : 0);//设置是否追号(0-非追号 1-追号)
            dataDto.put("stype",scheme.getSchemeType());//设置方案类型(0-普通方案 1-追号方案 2-优化方案 3-跟单方案 4-神单方案 5-智能追号)
            if(LotteryUtils.isGyj(scheme.getLotteryId()))
            {
                dataDto.put("ltype",3);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-冠亚军)
                dataDto.put("isgj",LotteryConstants.GJ.equals(scheme.getLotteryId())? 1 : 0);//设置是否为冠军(0-不是 1-是)
            }
            else if(LotteryUtils.isJc(scheme.getLotteryId()))
            {
                dataDto.put("ltype",0);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-冠亚军)
                dataDto.put("islq",LotteryUtils.isJczq(scheme.getLotteryId())? 0 : 1);//设置是否为篮球(0-不是 1-是)
            }
            else if(LotteryUtils.isZC(scheme.getLotteryId()))
            {
                dataDto.put("ltype",1);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-冠亚军)
            }
            else if(LotteryUtils.isSzc(scheme.getLotteryId()))
            {
                dataDto.put("ltype",2);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-冠亚军)
            }
            result.setData(dataDto);
            return;
        }
        //金额错误/方案不存在/方案已失效/方案重复支付/优惠券无效/优惠券不满足使用条件/用户不存在,返回系统异常
        else if(dcode == 1001 || dcode == 1002 || dcode == 1004 || dcode == 1005 || dcode == 2000 || dcode == 2002)
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        else if(dcode == 1003)
        {
            logger.error("[方案确认购买]方案已截止!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120015);
        }
        else if(dcode == 2001)
        {
            logger.error("[方案确认购买]优惠券已过期!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120016);
        }
        else if(dcode == 3001)
        {
            logger.error("[方案确认购买]用户不存在!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_USERNOTEXISTS);
        }
        //用户余额不足,则提示充值
        else if(dcode == 3002)
        {
            try
            {
                //查询用户当前余额并计算差额
                UserAccount userAccount = userAccountMapper.queryUserAccountInfoByUserId(params.getAsLong("userId"));
                if(userAccount == null)
                {
                    throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                }
                Map<String,Object> dataMap = new HashMap<String,Object>();
                dataMap.put("balance",userAccount.getBalance());
                dataMap.put("dbalance",params.getAsDouble("ymoney") - userAccount.getBalance());
                result.setData(dataMap);
            }
            catch (Exception e)
            {
                logger.error("[方案确认购买]查询用户当前余额发生异常!用户编号=" + params.getAsString("userId") + ",异常信息=" + e);
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120007);
        }
        else if(dcode == 3003)
        {
            logger.error("[方案确认购买]非法渠道来源!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.ERROR_100002);
        }
        else if(dcode == 3004)
        {
            logger.error("[方案确认购买]未配置渠道出款账户!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.ERROR_100005);
        }
        else
        {
            throw new ServiceException(ErrorCode_API.NETWORK_ERROR);
        }
    }

    /**
     * 查询用户方案
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getScheme(Dto params, ResultBean result) throws ServiceException
    {
        /**
         * 设置查询参数
         */
        settingPageParams(params);//设置分页查询参数
        //最多只查询最近3个月的方案
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-(SchemeConstants.SCHEME_QUERY_DATERANGE));
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("stime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置方案起始时间

        //判断查询类型(qtype,0-待开奖 1-已开奖 2-已中奖 3-追号 为空时查询全部)
        if(StringUtil.isNotEmpty(params.get("qtype")))
        {
            //待开奖
            int qtype = params.getAsInteger("qtype");
            if(qtype == 0)
            {
                //params.put("openStatus","0");//设置中奖状态为0(0-未计奖 1-未中奖 2-已中奖)
                //params.put("maxSchemeStatus",SchemeConstants.SCHEME_STATUS_CPCG);
                params.put("dkjflag","1");
            }
            //已开奖
            else if(qtype == 1)
            {
                params.put("minOpenStatus","1");//设置中奖状态为>=1
            }
            //已中奖
            else if(qtype == 2)
            {
                params.put("openStatus","2");//设置中奖状态为2(0-未计奖 1-未中奖 2-已中奖
            }
            //追号
            else if(qtype == 3)
            {
                params.put("schemeType",SchemeConstants.SCHEME_TYPE_ZH);
            }
            //无效订单
            else if(qtype == 4)
            {
                params.put("wxflag","1");
            }
        }

        /**
         * 查询方案并设置返回数据
         */
        //查询方案
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("list",new ArrayList<SchemeBean>());
        params.put("minSchemeStatus",SchemeConstants.SCHEME_STATUS_ZFCG);//只查询支付成功的方案
        List<Scheme> schemeList = schemeMapper.querySchemeInfo(params);
        if(schemeList != null && schemeList.size() > 0)
        {
            //拼装方案信息
            List<Dto> dataList = new ArrayList<Dto>();
            Dto dataDto = null;
            calendar.setTime(new Date());//设置为当前时间
            Calendar gcalendar = Calendar.getInstance();//用来设置方案购买时间
            for(Scheme scheme : schemeList)
            {
                dataDto = new BaseDto("sid",scheme.getId());//设置方案id
                dataDto.put("lname",scheme.getLotteryName());//设置彩种名称
                dataDto.put("money",scheme.getSchemeMoney());//设置方案金额
                dataDto.put("logo", SysConfig.getLotteryLogo(scheme.getLotteryId()) + "?v=" + KeyConstants.FIXED_VERSION);//设置彩种logo地址
                dataDto.put("stype",scheme.getSchemeType());//方案类型(0-普通 1-追号 2-优化 3-跟单 4-神单 5-智能追号)

                /**
                 * 设置彩种类型
                 */
                //如果方案所属彩种为冠亚军
                if(LotteryUtils.isGyj(scheme.getLotteryId()))
                {
                    dataDto.put("ltype",3);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-猜冠亚军)
                    dataDto.put("isgj",LotteryConstants.GJ.equals(scheme.getLotteryId())? 1 : 0);//设置是否为猜冠军(0-不是 1-是)
                }
                //如果方案所属彩种为竞彩
                else if(LotteryUtils.isJc(scheme.getLotteryId()))
                {
                    dataDto.put("ltype",0);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-猜冠亚军)
                    dataDto.put("islq",LotteryUtils.isJczq(scheme.getLotteryId())? 0 : 1);//设置是否为竞彩篮球(0-不是 1-是)
                }
                //如果方案所属彩种为足彩
                else if(LotteryUtils.isZC(scheme.getLotteryId()))
                {
                    dataDto.put("ltype",1);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-猜冠亚军)
                }
                //如果方案所属彩种为数字彩
                else if(LotteryUtils.isSzc(scheme.getLotteryId()))
                {
                    dataDto.put("ltype",2);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-猜冠亚军)
                }

                /**
                 * 设置方案购买时间
                 */
                gcalendar.setTime(scheme.getCreateTime());
                if(calendar.get(Calendar.DAY_OF_MONTH) == gcalendar.get(Calendar.DAY_OF_MONTH))
                {
                    dataDto.put("gtime","今天 " + DateUtil.formatDate(gcalendar.getTime(),DateUtil.DEFAULT_TIME));
                }
                else if(DateUtil.daysBetween(calendar.getTime(),gcalendar.getTime()) == 1)
                {
                    dataDto.put("gtime","昨天 " + DateUtil.formatDate(gcalendar.getTime(),DateUtil.DEFAULT_TIME));
                }
                else
                {
                    dataDto.put("gtime",DateUtil.formatDate(gcalendar.getTime(),DateUtil.MDHMS_FORMAT));
                }

                /**
                 * 设置方案状态描述
                 */
                //追号方案,则根据追期完成状况设置相应的状态描述
                if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_ZH)
                {
                    //如果该方案为中奖停止,且方案中奖奖金>0(中奖奖金>0说明有中过奖了),则设置方案追号状态为已完成
                    dataDto.put("zflag",1);//设置追号标识为追号
                    if(scheme.getPrizeStop())
                    {
                        if(scheme.getPrizeTax() > 0)
                        {
                            dataDto.put("sdesc","已完成");
                        }
                        else
                        {
                            dataDto.put("sdesc",(scheme.getDonePeriod() + "/" + scheme.getPeriodSum()));
                        }
                    }
                    //非中奖停止的追号方案,则根据当前已追期数和总期数判断追号完成状态
                    else
                    {
                        if(scheme.getDonePeriod() == scheme.getPeriodSum())
                        {
                            dataDto.put("sdesc","已完成");
                        }
                        else
                        {
                            dataDto.put("sdesc",(scheme.getDonePeriod() + "/" + scheme.getPeriodSum()));
                        }
                    }
                }
                //非追号方案,则根据派奖/中奖状态设置相应的状态描述
                else
                {
                    dataDto.put("zflag",0);//设置追号标识为非追号
                    if(scheme.getSchemeStatus() <= SchemeConstants.SCHEME_STATUS_CPZ
                            || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPSB
                            || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_TKF
                            || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_ETF)
                    {
                        dataDto.put("sdesc","正在预约");
                    }
                    else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
                    {
                        dataDto.put("sdesc","已撤单");
                    }
                    else
                    {
                        if(scheme.getOpenStatus() == 0)
                        {
                            dataDto.put("sdesc","等待开奖");
                        }
                        if(scheme.getOpenStatus() == 1)
                        {
                            dataDto.put("sdesc","未中奖");
                        }
                        else if(scheme.getOpenStatus() == 2)
                        {
                            if(scheme.getPrizeStatus() <= 1)
                            {
                                dataDto.put("sdesc","等待派奖");
                            }
                            else
                            {
                                dataDto.put("sdesc","中奖");
                            }
                        }
                    }
                }
                //设置中奖金额
                dataDto.put("amoney",scheme.getOpenStatus() == 2? (DoubleUtil.roundDouble(scheme.getPrizeTax()) + "元") : (""));
                dataList.add(dataDto);
            }
            dataMap.put("list",dataList);//设置方案记录信息
        }
        //如果有分页标识,则查询方案总记录条数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = schemeMapper.querySchemeInfoCount(params);
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置总条数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }
        //设置返回数据
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 查询方案详情
     * @author  mcdog
     * @param   params  查询参数对象
     * @param   result  处理结果对象
     */
    public void getSchemeDetail(Dto params, ResultBean result) throws ServiceException,Exception
    {
        //校验查询参数
        if(StringUtil.isEmpty(params.get("sid")))
        {
            logger.error("[保存方案]用户编号=" + params.getAsString("userId") + "参数校验不通过,请求参数:" + params.toString());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }

        /**
         * 查询并设置方案信息/出票信息
         */
        //根据方案id查询方案信息
        List<Scheme> schemeList = schemeMapper.querySchemeInfo(params);
        if(schemeList != null && schemeList.size() > 0)
        {
            /**
             * 设置方案信息/出票信息
             */
            Calendar currentCalendar = Calendar.getInstance();
            Scheme scheme = schemeList.get(0);
            Dto schemeDto = new BaseDto();//用来封装返回给前端的方案详情数据对象
            if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD)
            {
                /**
                 * 设置跟单方案详情
                 */
                //设置公共属性
                schemeDto.put("lid",scheme.getLotteryId());//设置彩种编号
                schemeDto.put("lname",scheme.getLotteryName());//设置彩种名称
                schemeDto.put("logo",SysConfig.getLotteryLogo(scheme.getLotteryId()) + "?v=" + KeyConstants.FIXED_VERSION);//设置彩种logo
                schemeDto.put("sid",scheme.getId());//设置方案id
                schemeDto.put("smoney", CalculationUtils.formatToThousandsStr(scheme.getSchemeMoney()) + "元");//设置方案金额/出票金额
                schemeDto.put("scode",scheme.getSchemeOrderId());//设置方案编号
                schemeDto.put("ctime",DateUtil.formatDate(scheme.getCreateTime(),DateUtil.MDHM_FORMAT));//设置方案发起时间/跟单时间

                //设置注数/倍数/过关方式
                schemeDto.put("zs",scheme.getSchemeZs());//设置方案注数
                schemeDto.put("bs",scheme.getSchemeMultiple());//设置方案倍数
                String[] tzspContent = scheme.getSchemeSpContent().split("\\|");//提取带sp的投注内容
                schemeDto.put("ggfs",tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," "));//设置过关方式
                schemeDto.put("sdesc","跟单");//设置方案类型描述

                //设置方案奖金/跟单所属神单的抽佣等信息
                Scheme sdscheme = schemeMapper.querySchemeInfoById(scheme.getCopySchemeId());//查询所属神单
                schemeDto.put("sdsid",sdscheme.getId());//设置神单方案id
                schemeDto.put("remuneration",sdscheme.getRemuneration() + "%");//设置提成比例/抽佣比例
                if(scheme.getOpenStatus() == 2)
                {
                    schemeDto.put("sprize",(DoubleUtil.roundDouble(scheme.getPrizeTax() + scheme.getRewardPrize(),2)) + "元");//设置方案奖金/中奖金额
                    schemeDto.put("rprize",scheme.getPrizeTax());//设置方案到账奖金
                    schemeDto.put("rewardprize",DoubleUtil.roundDouble(scheme.getRewardPrize(),2) + "元");//设置提成金额/抽佣金额
                }
                else
                {
                    schemeDto.put("sprize","--");//设置方案奖金/中奖金额
                    schemeDto.put("rprize","--");//设置方案到账奖金
                    schemeDto.put("rewardprize","--");//设置提成金额/抽佣金额
                }
                //设置神单发起人昵称/头像
                User user = userMapper.queryUserInfoById(sdscheme.getSchemeUserId());
                schemeDto.put("uid",user.getId());//神单发起人用户编号
                schemeDto.put("unick",user.getNickName());//设置神单发起人昵称
                schemeDto.put("avatar",SysConfig.getHostStatic() + user.getAvatar());//设置发单人头像

                //设置跟单状态
                if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPCG)
                {
                    if(scheme.getOpenStatus() == 1)
                    {
                        schemeDto.put("zdesc","未中奖");
                    }
                    else if(scheme.getOpenStatus() == 2)
                    {
                        if(scheme.getPrizeStatus() == 0)
                        {
                            schemeDto.put("zdesc","等待派奖");
                        }
                        else if(scheme.getPrizeStatus() == 1)
                        {
                            schemeDto.put("zdesc","派奖中");
                        }
                        else
                        {
                            schemeDto.put("zdesc","已派奖");
                        }
                    }
                    else
                    {
                        schemeDto.put("zdesc","等待开奖");
                    }
                }
                else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
                {
                    schemeDto.put("zdesc","出票失败已退款");
                }
                else
                {
                    schemeDto.put("zdesc","出票中");
                }
                //设置对阵信息/投注选项
                List<Dto> tzxxList = new ArrayList<Dto>();
                Dto tempDto = new BaseDto("id",scheme.getId());
                tempDto.put("lotteryId",scheme.getLotteryId());
                tempDto.put("hideType",scheme.getHideType());
                tempDto.put("schemeContent",scheme.getSchemeContent());
                tempDto.put("schemeSpContent",scheme.getSchemeSpContent());
                if(currentCalendar.getTime().after(scheme.getEndTime()))
                {
                    //方案已截止
                    tempDto.put("needSg",1);
                    tzxxList.addAll(getYjzSchemeTzxxList(tempDto));
                }
                else
                {
                    //方案未截止
                    tzxxList.addAll(getWjzSchemeTzxxList(tempDto));
                }
                schemeDto.put("tzxxs",tzxxList);
            }
            else
            {
                //设置公共属性
                schemeDto.put("lid",scheme.getLotteryId());//设置彩种编号
                schemeDto.put("lname",scheme.getLotteryName());//设置彩种名称
                schemeDto.put("logo",SysConfig.getLotteryLogo(scheme.getLotteryId()) + "?v=" + KeyConstants.FIXED_VERSION);//设置彩种logo
                schemeDto.put("pid",scheme.getPeriod());//设置期次号
                schemeDto.put("pname",scheme.getPeriod() + "期");//设置期次名称
                schemeDto.put("scode",scheme.getSchemeOrderId());//设置方案编号
                schemeDto.put("stype",scheme.getSchemeType());//设置方案类型(0-普通方案 1-追号方案 2-优化方案 3-跟单方案 4-神单方案 5-智能追号)
                schemeDto.put("sdesc",(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_SD)? "晒单" : "自购");//设置方案类型描述
                schemeDto.put("tzcontent",scheme.getSchemeContent());//设置投注内容
                schemeDto.put("money", CalculationUtils.formatToThousandsStr(scheme.getSchemeMoney()) + "元");//设置方案金额
                schemeDto.put("yjpjtime",DateUtil.formatDate(scheme.getEstimateDrawTime(),DateUtil.DEFAULT_DATE_TIME_SECOND));//设置方案预计派奖时间

                //非追号,则设置方案进度
                if(scheme.getSchemeType() != SchemeConstants.SCHEME_TYPE_ZH)
                {
                    schemeDto.put("zflag",0);//设置追号标识(0-非追号 1-追号)
                    //settingSchemeStatusJindu(scheme,schemeDto);//设置方案进度
                    //设置方案进度描述
                    if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_ZFCG
                            || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPZ
                            || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPSB
                            || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_TKF
                            || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_ETF)
                    {
                        schemeDto.put("jddesc",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_CPZ));//预约中
                    }
                    else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPCG)
                    {
                        schemeDto.put("jddesc",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_CPCG));//预约成功
                        if(scheme.getOpenStatus() == 2)
                        {
                            if(scheme.getPrizeStatus() == 0)
                            {
                                schemeDto.put("jddesc","等待派奖");
                            }
                            else if(scheme.getPrizeStatus() == 1)
                            {
                                schemeDto.put("jddesc","派奖中");
                            }
                            else if(scheme.getPrizeStatus() == 2)
                            {
                                schemeDto.put("jddesc","已派奖");
                            }
                        }
                    }
                    else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
                    {
                        //schemeDto.put("jddesc",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_CDCG));//撤单成功
                        schemeDto.put("jddesc","预约失败");//撤单成功
                    }
                }
                else
                {
                    schemeDto.put("zflag",scheme.getSchemeOrderId().startsWith(SchemeConstants.SCHEME_SCODE_ZHZN)? 2 : 1);//设置追号标识(0-非追号 1-追号 2-智能追号)
                    schemeDto.put("jindu",new ArrayList<Dto>());
                }
                //如果方案所属彩种为冠亚军
                if(LotteryUtils.isGyj(scheme.getLotteryId()))
                {
                    schemeDto.put("ltype",3);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-猜冠亚军)
                    settingGyjSchemeDetail(scheme,schemeDto);//设置冠亚军方案详情
                }
                //如果方案所属彩种为竞彩
                else if(LotteryUtils.isJc(scheme.getLotteryId()))
                {
                    //设置预计奖金
                    schemeDto.put("yjjj","--");
                    if(StringUtil.isNotEmpty(scheme.getTheoryPrize()))
                    {
                        schemeDto.put("yjjj","若中奖,奖金" + scheme.getTheoryPrize().replace("元","") + "元");
                    }
                    //设置神单分享状态
                    if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_SD)
                    {
                        schemeDto.put("share",2);//方案是否可分享(0-不可分享 1-可分享 2-已分享)
                    }
                    else if(scheme.getShare()
                            && scheme.getEndTime().after(new Date())
                            && scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPCG)
                    {
                        schemeDto.put("share",1);//方案是否可分享(0-不可分享 1-可分享 2-已分享)
                    }
                    else
                    {
                        schemeDto.put("share",0);//方案是否可分享(0-不可分享 1-可分享 2-已分享)
                    }
                    schemeDto.put("ltype",0);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-猜冠亚军)
                    settingJcSchemeDetail(scheme,schemeDto);//设置竞彩方案详情
                }
                //如果方案所属彩种为足彩
                else if(LotteryUtils.isZC(scheme.getLotteryId()))
                {
                    schemeDto.put("ltype",1);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-猜冠亚军)
                    settingZcSchemeDetail(scheme,schemeDto);//设置足彩方案详情
                }
                //如果方案所属彩种为数字彩
                else if(LotteryUtils.isSzc(scheme.getLotteryId()))
                {
                    schemeDto.put("ltype",2);//设置彩种类型(0-竞彩 1-足彩 2-数字彩 3-猜冠亚军)
                    settingSzcSchemeDetail(scheme,schemeDto);//设置数字彩方案详情
                }
            }
            //设置返回结果和状态
            result.setData(schemeDto);
            result.setErrorCode(ErrorCode.SUCCESS);
        }
        else
        {
            logger.error("[查询方案详情]查询不到方案信息!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
    }

    /**
     * 获取方案信息
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getSchemeInfo(Dto params, ResultBean result) throws ServiceException
    {
        //校验查询参数
        if(StringUtil.isEmpty(params.get("sid")))
        {
            logger.error("[获取方案信息]用户编号=" + params.getAsString("userId") + "参数校验不通过,请求参数:" + params.toString());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //根据方案id查询方案信息
        List<Scheme> schemeList = schemeMapper.querySchemeInfo(params);
        if(schemeList != null && schemeList.size() > 0)
        {
            //如果彩种为竞彩,则设置注数和倍数信息
            Scheme scheme = schemeList.get(0);
            Map<String,Object> dataMap = new HashMap<String,Object>();
            if(LotteryUtils.isJc(scheme.getLotteryId()))
            {
                dataMap.put("zs",scheme.getSchemeZs());//注数
                dataMap.put("bs",scheme.getSchemeMultiple());//倍数
            }
            //非竞彩,则设置期次信息
            else
            {
                dataMap.put("pid",scheme.getPeriod());//期次
            }
            //查询用户当前余额
            try
            {
                UserAccount userAccount = userAccountMapper.queryUserAccountInfoByUserId(scheme.getSchemeUserId());
                if(userAccount == null)
                {
                    throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                }
                dataMap.put("balance",userAccount.getBalance());
                dataMap.put("dbalance",userAccount.getBalance() >= scheme.getSchemeMoney()? 0 : (scheme.getSchemeMoney() - userAccount.getBalance()));
            }
            catch (Exception e)
            {
                logger.error("[获取方案信息]查询用户当前余额发生异常!用户编号=" + scheme.getSchemeUserId() + ",异常信息:" + e);
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            //查询用户优惠券信息
            try
            {
                //判断方案类型,只有方案不是追号方案,才能使用优惠券
                List<Dto> couponList = new ArrayList<Dto>();
                if(scheme.getSchemeType() != 1)
                {
                    Dto couponQueryDto = new BaseDto("userId",scheme.getSchemeUserId());
                    couponQueryDto.put("useStatus",1);
                    couponQueryDto.put("useLotteryId",scheme.getLotteryId());
                    List<Dto> couponDataList = userCouponMapper.queryUserCoupons(couponQueryDto);
                    if(couponDataList != null && couponDataList.size() > 0)
                    {
                        Calendar current = Calendar.getInstance();
                        Dto coupon = null;
                        for(Dto couponData : couponDataList)
                        {
                            /**
                             * 判断优惠券是否满足使用条件
                             */
                            //如果优惠券类型为发行限制期限,则只有当前时间在生效时间和过期时间范围内才能使用
                            String cutype = couponData.getAsString("cuType");
                            if("0".equals(cutype))
                            {
                                if(DateUtil.parseCalendar(couponData.getAsString("cuBeginTime"),DateUtil.DEFAULT_DATE_TIME).after(current)
                                        || DateUtil.parseCalendar(couponData.getAsString("cuEndTime"),DateUtil.DEFAULT_DATE_TIME).before(current))
                                {
                                    continue;
                                }
                            }
                            //如果优惠券类型为为使用期限,则判断优惠券针对用户的过期时间
                            else if("1".equals(cutype))
                            {
                                if(DateUtil.parseCalendar(couponData.getAsString("cuExpireTime"),DateUtil.DEFAULT_DATE_TIME).before(current))
                                {
                                    continue;
                                }
                            }
                            //如果优惠券的使用类型为满减,则判断方案金额是否满足限制金额
                            if("1".equals(couponData.getAsString("cUseType")))
                            {
                                if(scheme.getSchemeMoney() < couponData.getAsDouble("cLimitMoney"))
                                {
                                    continue;
                                }
                            }
                            //封装用户优惠券前端展示对象
                            coupon = new BaseDto();
                            coupon.put("cuid",couponData.get("cuid"));//用户优惠券id
                            coupon.put("name",couponData.getAsString("cName"));//优惠券名称
                            coupon.put("faceValue",couponData.getAsString("cMoney"));//优惠券面值
                            couponList.add(coupon);
                        }
                    }
                }
                dataMap.put("coupons",couponList);
            }
            catch (Exception e)
            {
                logger.error("[获取方案信息]查询用户优惠券发生异常!" + ",用户编号=" + scheme.getSchemeUserId() + ",异常信息:" + e);
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            //设置其它返回数据
            dataMap.put("lname",scheme.getLotteryName());//彩种名称
            dataMap.put("money",scheme.getSchemeMoney());//方案金额
            dataMap.put("ymoney",scheme.getSchemeMoney());//应付金额
            dataMap.put("sid",scheme.getId());//方案id
            dataMap.put("scode",scheme.getSchemeOrderId());//方案编号
            dataMap.put("stype",scheme.getSchemeType());//方案类型(0-普通方案 1-追号方案 2-优化方案 3-跟单方案 4-神单方案 5-智能追号)
            String rechargeDesc = SysConfig.getString("PAY_RECHARGE_DESC");//获取系统设置的充值描述
            dataMap.put("czdesc",StringUtil.isEmpty(rechargeDesc)? "" : rechargeDesc);//设置充值描述
            result.setData(dataMap);
            result.setErrorCode(ErrorCode.SUCCESS);
        }
    }

    /**
     * 验证所选择竞彩场次的销售状态(竞彩)
     * @author  mcdog
     * @param   schemeBean   方案业务对象
     */
    private void checkJcMatchSellStatus(SchemeBean schemeBean) throws ServiceException
    {
        String lotteryId = schemeBean.getLid();
        String playTypeId = schemeBean.getPlayTypeId();
        String tzContent = schemeBean.getTzcontent();
        boolean containsDg = tzContent.indexOf("1*1") > -1? true : false;//是否包含单关,true表示包含,false表示不包含
        boolean ishh = schemeBean.getTzcontent().startsWith(LotteryConstants.JCWF_PREFIX_HH)? true : false;;//是否为混投 true-是

        /**
         * 解析投注串中包含的场次/玩法
         */
        List<Object> matchList = new ArrayList<Object>();//用来保存场次竞彩编号
        List<Date> dateList = new ArrayList<Date>();//用来保存场次的截止时间
        List<Date> matchTimeList = new ArrayList<Date>();//用来保存场次的比赛时间
        Map<String, MatchInfo> matchInfoList = new HashMap<>();//投注串包含的场次信息
        Map<String,Map<String,String>> pankouMaps = new HashMap<String,Map<String,String>>();//用来保存场次盘口信息
        Map<String,String> pankouMap = null;//用来保存单场次盘口
        String[] realTzContent = tzContent.split("\\|")[1].split("\\$");
        for(int i = 0; i < realTzContent.length; i ++)
        {
            String[] tzcodes = realTzContent[i].split(",");//截取投注串
            for(String tzcode : tzcodes)
            {
                pankouMap = new HashMap<String,String>();
                MatchInfo matchInfo = new MatchInfo();
                String[] tempCodes = tzcode.split("=");//默认按单个玩法解析

                //解析玩法前缀
                Map<String,String> wfMaps = new HashMap<String,String>();//用来保存玩法前缀
                if(ishh)
                {
                    tempCodes = tzcode.split(">");//竞彩混投解析
                    String[] wfs = tempCodes[1].split("\\+");
                    for(String wf : wfs)
                    {
                        wfMaps.put(wf.split("=")[0],"1");
                    }
                }
                else
                {
                    wfMaps.put(LotteryConstants.jcWfPrefixMaps.get(playTypeId),"1");//根据彩种获取玩法名称
                }
                //查询足球对阵场次信息
                if(LotteryUtils.isJczq(lotteryId))
                {
                    MatchFootBall match = (MatchFootBall) memcached.get(LotteryConstants.jczqMatchPrefix + tempCodes[0]);//从缓存中获取场次信息
                    if(match == null)
                    {
                        match = footBallMapper.queryMatchFootBallByMatchCode(tempCodes[0]);//从数据库中查询场次信息
                        memcached.set(LotteryConstants.jczqMatchPrefix + tempCodes[0],match,30);//在缓存中保存30秒
                    }
                    //判断场次是否存在或场次是否开售
                    if(match == null
                            || match.getStatus() == LotteryConstants.STATUS_CANCEL
                            || match.getStatus() == LotteryConstants.STATUS_STOP
                            || match.getStatus() == LotteryConstants.STATUS_CLOSE)
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,tempCodes[0]));
                    }
                    //判断场次是否截止
                    else if(match.getStatus() == LotteryConstants.STATUS_EXPIRE)
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120013,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120013_MSG,tempCodes[0]));
                    }
                    //判断胜平负玩法是否开售
                    if((wfMaps.containsKey(LotteryConstants.JCWF_PREFIX_SPF)
                            && ((!containsDg && match.getSpfStatus() != 1) || (containsDg && match.getSingleSpfStatus() != 1))))
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,
                                        tempCodes[0] + LotteryConstants.playMethodMaps.get(LotteryConstants.JCWF_PREFIX_SPF)));
                    }
                    //判断让球胜平负玩法是否开售
                    if((wfMaps.containsKey(LotteryConstants.JCWF_PREFIX_RQSPF)
                            && ((!containsDg && match.getRqspfStatus() != 1) || (containsDg && match.getSingleRqspfStatus() != 1))))
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,
                                        tempCodes[0] + LotteryConstants.playMethodMaps.get(LotteryConstants.JCWF_PREFIX_RQSPF)));
                    }
                    //判断总进球玩法是否开售
                    if((wfMaps.containsKey(LotteryConstants.JCWF_PREFIX_JQS)
                            && ((!containsDg && match.getZjqStatus() != 1) || (containsDg && match.getSingleZjqStatus() != 1))))
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,
                                        tempCodes[0] + LotteryConstants.playMethodMaps.get(LotteryConstants.JCWF_PREFIX_JQS)));
                    }
                    //判断半全场玩法是否开售
                    if((wfMaps.containsKey(LotteryConstants.JCWF_PREFIX_BQC)
                            && ((!containsDg && match.getBqcStatus() != 1) || (containsDg && match.getSingleBqcStatus() != 1))))
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,
                                        tempCodes[0] + LotteryConstants.playMethodMaps.get(LotteryConstants.JCWF_PREFIX_BQC)));
                    }
                    //判断比分玩法是否开售
                    if((wfMaps.containsKey(LotteryConstants.JCWF_PREFIX_CBF)
                            && ((!containsDg && match.getBfStatus() != 1) || (containsDg && match.getSingleBfStatus() != 1))))
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,
                                        tempCodes[0] + LotteryConstants.playMethodMaps.get(LotteryConstants.JCWF_PREFIX_CBF)));
                    }
                    matchList.add(tempCodes[0]);
                    dateList.add(match.getEndTime());
                    matchTimeList.add(match.getMatchTime());
                    pankouMap.put("lose",match.getLose() != null? match.getLose().toString() : "");
                    matchInfo.setMatchCode(tempCodes[0]);
                    matchInfo.setLeagueName(match.getLeagueName());
                    matchInfo.setHostName(match.getHostName().length() > 4 ? match.getHostName().substring(0,4) : match.getHostName());
                    matchInfo.setJcId(match.getJcId());
                    matchInfo.setRqf(match.getLose());
                }
                //查询篮球对阵场次信息
                else if(LotteryUtils.isJclq(lotteryId))
                {
                    MatchBasketBall match = (MatchBasketBall) memcached.get(LotteryConstants.jclqMatchPrefix + tempCodes[0]);//从缓存中获取场次信息
                    if(match == null)
                    {
                        match = basketBallMapper.queryMatchBasketBallByMatchCode(tempCodes[0]);//从数据库中查询场次信息
                        memcached.set(LotteryConstants.jclqMatchPrefix + tempCodes[0],match,30);//在缓存中保存30秒
                    }
                    //判断场次是否存在或场次是否开售
                    if(match == null
                            || match.getStatus() == LotteryConstants.STATUS_CANCEL
                            || match.getStatus() == LotteryConstants.STATUS_STOP
                            || match.getStatus() == LotteryConstants.STATUS_CLOSE)
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,tempCodes[0]));
                    }
                    //判断场次是否截止
                    else if(match.getStatus() == LotteryConstants.STATUS_EXPIRE)
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120013,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120013_MSG,tempCodes[0]));
                    }
                    //判断胜负玩法是否开售
                    if((wfMaps.containsKey(LotteryConstants.JCWF_PREFIX_SF)
                            && ((!containsDg && match.getSfStatus() != 1) || (containsDg && match.getSingleSfStatus() != 1))))
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,
                                        tempCodes[0] + LotteryConstants.playMethodMaps.get(LotteryConstants.JCWF_PREFIX_SF)));
                    }
                    //判断让分胜负玩法是否开售
                    if((wfMaps.containsKey(LotteryConstants.JCWF_PREFIX_RFSF)
                            && ((!containsDg && match.getRfsfStatus() != 1) || (containsDg && match.getSingleRfsfStatus() != 1))))
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,
                                        tempCodes[0] + LotteryConstants.playMethodMaps.get(LotteryConstants.JCWF_PREFIX_RFSF)));
                    }
                    //判断大小分玩法是否开售
                    if((wfMaps.containsKey(LotteryConstants.JCWF_PREFIX_DXF)
                            && ((!containsDg && match.getDxfStatus() != 1) || (containsDg && match.getSingleDxfStatus() != 1))))
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,
                                        tempCodes[0] + LotteryConstants.playMethodMaps.get(LotteryConstants.JCWF_PREFIX_DXF)));
                    }
                    //判断胜分差玩法是否开售
                    if((wfMaps.containsKey(LotteryConstants.JCWF_PREFIX_SFC)
                            && ((!containsDg && match.getSfcStatus() != 1) || (containsDg && match.getSingleSfcStatus() != 1))))
                    {
                        throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                                MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,
                                        tempCodes[0] + LotteryConstants.playMethodMaps.get(LotteryConstants.JCWF_PREFIX_SFC)));
                    }
                    matchList.add(tempCodes[0]);
                    dateList.add(match.getEndTime());
                    matchTimeList.add(match.getMatchTime());
                    pankouMap.put("lose",match.getLose() != null? match.getLose().toString() : "");
                    pankouMap.put("dxf",match.getDxf() != null? match.getDxf().toString() : "");
                    matchInfo.setMatchCode(tempCodes[0]);
                    matchInfo.setLeagueName(match.getLeagueName());
                    matchInfo.setHostName(match.getHostName());
                    matchInfo.setJcId(match.getJcId());
                    matchInfo.setRqf(match.getLose());
                }
                pankouMaps.put(tempCodes[0],pankouMap);
                matchInfoList.put(matchInfo.getMatchCode(), matchInfo);
            }
        }
        //对截止时间从小到大排序
        Collections.sort(dateList, new Comparator<Date>()
        {
            @Override
            public int compare(Date date1, Date date2)
            {
                return date1.getTime() > date2.getTime()? 1 : -1;
            }
        });
        //对比赛时间从小到大排序
        Collections.sort(matchTimeList, new Comparator<Date>()
        {
            @Override
            public int compare(Date date1, Date date2)
            {
                return date1.getTime() > date2.getTime()? 1 : -1;
            }
        });
        schemeBean.setEtime(dateList.get(0));//取最早的场次截止时间作为方案的截止时间
        schemeBean.setMatchList(matchList);//设置场次竞彩编号
        schemeBean.setPankouMap(pankouMaps);//设置场次盘口
        schemeBean.setMatchInfoList(matchInfoList);//设置场次信息

        //设置方案预计开奖时间(取最晚开赛场次的比赛开始时间 + 120分钟(足球)/150分钟(篮球))
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(matchTimeList.get(matchTimeList.size() - 1));
        calendar.add(Calendar.MINUTE,(LotteryUtils.isJczq(lotteryId)? 120 : 150));
        schemeBean.setYjkjTime(calendar.getTime());//设置方案的预计开奖时间
    }

    /**
     * 验证所选择猜冠军/冠亚军场次的销售状态(猜冠亚军)
     * @author  mcdog
     * @param   schemeBean   方案业务对象
     */
    private void checkGyjMatchSellStatus(SchemeBean schemeBean) throws ServiceException
    {
        /**
         * 根据当前时间,校验冠亚军是否在在售期时间段
         */
        Dto params = new BaseDto("leagueName","世界杯");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("minMatchTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
        params.put("orderBySorts","matchTime asc");
        List<Dto> jcmatchList = footBallMapper.queryFootBallMatchs(params);
        if(jcmatchList != null && jcmatchList.size() > 0)
        {
            Dto jcmatch = jcmatchList.get(0);
            calendar = Calendar.getInstance();
            Calendar jcmatchCalendar = Calendar.getInstance();
            jcmatchCalendar.setTime(DateUtil.parseDate(jcmatch.getAsString("matchTime"),DateUtil.DEFAULT_DATE_TIME));
            if(calendar.get(Calendar.HOUR_OF_DAY) < 9)
            {
                if(jcmatchCalendar.get(Calendar.HOUR_OF_DAY) < 9)
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120012,"当前玩法暂停销售");
                }
            }
            else
            {
                if(jcmatchCalendar.get(Calendar.HOUR_OF_DAY) > 9
                        && DateUtil.minutesBetween(calendar.getTime(),jcmatchCalendar.getTime()) < 25)
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120012,"当前玩法暂停销售");
                }
            }
        }

        /**
         * 校验期次
         */
        String lotteryId = schemeBean.getLid();
        String periodStr = schemeBean.getTzcontent().split("\\|")[1].split("\\=")[0];
        Period period = periodMapper.queryPeriodByPerod(lotteryId,periodStr);
        if(period == null
                || period.getSellStatus() == LotteryConstants.STATUS_CANCEL
                || period.getSellStatus() == LotteryConstants.STATUS_STOP
                || period.getSellStatus() == LotteryConstants.STATUS_CLOSE)
        {
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120012,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120012_MSG,new Object[]{period.getPeriod()}));
        }
        //判断期次是否截止
        else if(period.getSellStatus() == -1 || period.getSellEndTime().getTime() < System.currentTimeMillis())
        {
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120009,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120009_MSG,new Object[]{period.getPeriod()}));
        }

        /**
         * 校验场次
         */
        //查询场次信息
        params = new BaseDto("lotteryId",lotteryId);
        params.put("period",periodStr);
        List<Dto> matchList = matchGyjMapper.queryGyjMatchInfos(params);//查询对阵信息
        Map<String,Dto> matchMaps = new HashMap<String,Dto>();
        if(matchList != null && matchList.size() > 0)
        {
            //封装对阵信息
            for(Dto matchDto : matchList)
            {
                matchMaps.put(matchDto.getAsString("matchCode"),matchDto);
            }
        }
        //校验投注选项中的场次信息
        String[] tzcodes = schemeBean.getTzcontent().split("\\|")[1].split("\\=")[1].split("/");
        for(String tzcode : tzcodes)
        {
            Dto matchDto = matchMaps.get(tzcode);
            String status = matchDto.getAsString("status");//提取场次销售状态
            if(matchDto == null)
            {
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            //校验场次是否停售/截止
            else if(!"1".equals(status))
            {
                String vsname = matchDto.getAsString("teamName");
                if(LotteryConstants.GYJ.equals(lotteryId))
                {
                    vsname += matchDto.getAsString("guestTeamName");
                }
                if("-1".equals(status) || "0".equals(status))
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120014,
                            MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120014_MSG,new Object[]{vsname}));
                }
                else if("2".equals(status))
                {
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120013,
                            MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120013_MSG,new Object[]{vsname}));
                }
            }
        }
        schemeBean.setEtime(period.getSellEndTime());//取期次的截止时间作为方案的截止时间
    }

    /**
     * 验证所选择期次的销售状态(数字彩)
     * @author  mcdog
     * @param   schemeBean   方案业务对象
     */
    private void checkSzcSellStatus(SchemeBean schemeBean) throws ServiceException
    {
        //判断期次是否存在或者期次状态为取消/停售/未开售
        String lotteryId = schemeBean.getLid();
        String periodStr = schemeBean.getPeriod();
        Period period = periodMapper.queryPeriodByPerod(lotteryId,periodStr);
        if(period == null
                || period.getSellStatus() == LotteryConstants.STATUS_CANCEL
                || period.getSellStatus() == LotteryConstants.STATUS_STOP
                || period.getSellStatus() == LotteryConstants.STATUS_CLOSE)
        {
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120012,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120012_MSG,new Object[]{period.getPeriod()}));
        }
        //判断期次是否截止
        else if(period.getSellStatus() == -1 || period.getSellEndTime().getTime() < System.currentTimeMillis())
        {
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120009,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120009_MSG,new Object[]{period.getPeriod()}));
        }
        schemeBean.setEtime(period.getSellEndTime());//取期次的截止时间作为方案的截止时间

        //设置预计开奖时间
        Calendar kjcalendar = Calendar.getInstance();
        kjcalendar.setTime(period.getDrawNumberTime());
        if(!LotteryUtils.isZC(schemeBean.getLid()))
        {
            kjcalendar.set(Calendar.HOUR_OF_DAY,21);
            kjcalendar.set(Calendar.MINUTE,30);
        }
        schemeBean.setYjkjTime(kjcalendar.getTime());
    }

    /**
     * 验证彩种销售状态
     * @author  mcdog
     * @param   schemeBean  方案业务对象
     * @param   lottery     彩种对象
     */
    public static boolean checkLotterySellStatus(SchemeBean schemeBean,Lottery lottery)
    {
        boolean result = false;
        String appId = schemeBean.getAppId();
        if(lottery != null)
        {
            //app
            if(UserConstants.USER_SOURCE_ANDROID == KeyConstants.loginUserMap.get(appId)
                    || UserConstants.USER_SOURCE_IOS == KeyConstants.loginUserMap.get(appId))
            {
                if(lottery.getAppStatus() == LotteryConstants.STATUS_SELL)
                {
                    result = true;
                }
            }
            //h5
            else if(UserConstants.USER_SOURCE_H5 == KeyConstants.loginUserMap.get(appId))
            {
                if(lottery.getAppStatus() == LotteryConstants.STATUS_SELL)
                {
                    result = true;
                }
            }
            //web
            else if(UserConstants.USER_SOURCE_WEB == KeyConstants.loginUserMap.get(appId))
            {
                if(lottery.getAppStatus() == LotteryConstants.STATUS_SELL)
                {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 设置投注项赔率/让球/让分/大小分盘口(竞彩)
     * @author  mcdog
     * @param   schemeBean  方案业务对象
     */
    public void settingTzContentSpAndLose(SchemeBean schemeBean)
    {
        Map<String, Map<String, String>> pankouMap = schemeBean.getPankouMap();//提取场次盘口信息
        String[] splitTzcodes = schemeBean.getTzcontent().split("\\|");//提取投注内容
        boolean ishh = schemeBean.getTzcontent().startsWith(LotteryConstants.JCWF_PREFIX_HH)? true : false;//是否为混投 true-是
        boolean hasD = splitTzcodes[1].indexOf("$") > -1? true : false;//是否有胆,true表示有
        StringBuilder spTzContentBuilder = new StringBuilder();//用来存放带赔率的投注串
        Dto spDto = null;

        //解析投注内容
        String[] realTzcodes = splitTzcodes[1].split("\\$");
        for(int i = 0; i < realTzcodes.length; i ++)
        {
            String[] tzcodes = realTzcodes[i].split(",");
            StringBuilder tempBuilder = new StringBuilder();
            for(String tzcode : tzcodes)
            {
                //解析单场次投注内容
                String[] tempCodes = tzcode.split("=");//默认按单个玩法解析
                if(ishh)
                {
                    tempCodes = tzcode.split(">");//竞彩混投解析
                }
                //查询竞彩足球赔率
                if(LotteryUtils.isJczq(schemeBean.getLid()))
                {
                    spDto = (Dto)memcached.get(LotteryConstants.jczqSpPrefix + tempCodes[0]);//从缓存中获取赔率信息
                    if(spDto == null)
                    {
                        spDto = footBallSpMapper.queryFootBallSp(tempCodes[0]);//从数据库中查询赔率信息
                        memcached.set(LotteryConstants.jczqSpPrefix + tempCodes[0],spDto,60);//在缓存中保留1分钟
                    }
                }
                //查询竞彩篮球赔率
                else if(LotteryUtils.isJclq(schemeBean.getLid()))
                {
                    spDto = (Dto)memcached.get(LotteryConstants.jclqSpPrefix + tempCodes[0]);//从缓存中获取赔率信息
                    if(spDto == null)
                    {
                        spDto = basketBallSpMapper.queryBasketBallSp(tempCodes[0]);//从数据库中查询赔率信息
                        memcached.set(LotteryConstants.jclqSpPrefix + tempCodes[0],spDto,60);//在缓存中保留1分钟
                    }
                }
                if(spDto != null)
                {
                    //根据投注项去匹配盘口和赔率信息
                    StringBuilder tempBuilder2 = new StringBuilder();
                    if(ishh)
                    {
                        tempBuilder.append("," + tempCodes[0] +  ">");//拼装场次信息
                        String[] xxCodes = tempCodes[1].split("\\+");//提取场次投注项
                        for(String xxCode : xxCodes)
                        {
                            //判断玩法类型,如果是让球胜平负/让分胜负,则设置让球/让分盘口
                            String[] xxs = xxCode.split("=");
                            if(LotteryConstants.JCWF_PREFIX_RQSPF.equals(xxs[0]) || LotteryConstants.JCWF_PREFIX_RFSF.equals(xxs[0]))
                            {
                                String lose = pankouMap.get(tempCodes[0]).get("lose");
                                tempBuilder2.append("+" + xxs[0] + (StringUtil.isEmpty(lose)? "" : ("(" + lose + ")=")));
                            }
                            //如果玩法类型为大小分,则设置大小分盘口
                            else if(LotteryConstants.JCWF_PREFIX_DXF.equals(xxs[0]))
                            {
                                String dxf = pankouMap.get(tempCodes[0]).get("dxf");
                                tempBuilder2.append("+" + xxs[0] + (StringUtil.isEmpty(dxf)? "" : ("(" + dxf + ")")) + "=");
                            }
                            else
                            {
                                tempBuilder2.append("+" + xxs[0] + "=");
                            }
                            //设置选项赔率
                            StringBuilder tempBuilder3 = new StringBuilder();
                            String[] xs = xxs[1].split("/");
                            for(String x : xs)
                            {
                                tempBuilder3.append("/" + x + "(" + spDto.get(xxs[0] + x) + ")");
                            }
                            tempBuilder2.append(tempBuilder3.toString().substring(1));
                        }
                    }
                    else
                    {
                        //判断玩法类型,如果是让球胜平负/让分胜负,则设置让球/让分盘口
                        tempBuilder.append("," + tempCodes[0]);
                        if(LotteryConstants.JCWF_PREFIX_RQSPF.equals(splitTzcodes[0]) || LotteryConstants.JCWF_PREFIX_RFSF.equals(splitTzcodes[0]))
                        {
                            String lose = pankouMap.get(tempCodes[0]).get("lose");
                            tempBuilder.append(StringUtil.isEmpty(lose)? "" : ("(" + lose + ")"));
                        }
                        //如果玩法类型为大小分,则设置大小分盘口
                        else if(LotteryConstants.JCWF_PREFIX_DXF.equals(splitTzcodes[0]))
                        {
                            String dxf = pankouMap.get(tempCodes[0]).get("dxf");
                            tempBuilder.append(StringUtil.isEmpty(dxf)? "" : ("(" + dxf + ")"));
                        }
                        tempBuilder.append("=");
                        String[] xxs = tempCodes[1].split("/");
                        for(String xx : xxs)
                        {
                            tempBuilder2.append("/" + xx + "(" + spDto.get(splitTzcodes[0] + xx) + ")");
                        }
                    }
                    tempBuilder.append(tempBuilder2.toString().substring(1));
                }
            }
            spTzContentBuilder.append((hasD && i == 0)? tempBuilder.toString() : tempBuilder.toString().substring(1));
            spTzContentBuilder.append((hasD && i == 0)? "$" : "");
        }
        //设置带赔率的投注串
        schemeBean.setTzspcontent(splitTzcodes[0] + "|" + (hasD? spTzContentBuilder.toString().substring(1) : spTzContentBuilder.toString()) + "|" + splitTzcodes[2]);
    }

    /**
     * 设置投注项赔率(猜冠亚军)
     * @author  mcdog
     * @param   schemeBean  方案业务对象
     */
    public void settingGyjTzContentSp(SchemeBean schemeBean) throws ServiceException
    {
        /**
         * 封装对阵信息
         */
        //查询对阵信息
        Map<String,GyjMatch> gyjMatchMap = new HashMap<String,GyjMatch>();//用来存放冠亚军对阵
        String[] tzcontents = schemeBean.getTzcontent().split("\\|");//提取投注内容
        String[] tzcodes = tzcontents[1].split("\\=");//提取投注选项
        String period = tzcodes[0];
        GyjMatch queryGyjMatch = new GyjMatch();
        queryGyjMatch.setPeriod(period);
        queryGyjMatch.setLotteryId(schemeBean.getLid());
        List<GyjMatch> gyjMatchList = matchGyjMapper.queryGyjMatchList(queryGyjMatch);//查询对阵场次
        if(gyjMatchList != null && gyjMatchList.size() > 0)
        {
            //封装对阵信息
            for(GyjMatch gyjMatch : gyjMatchList)
            {
                gyjMatchMap.put(gyjMatch.getMatchCode(),gyjMatch);
            }
        }

        /**
         * 解析投注串,并拼接带赔率的投注串
         */
        StringBuilder spTzContentBuilder = new StringBuilder();//用来存放带赔率的投注串
        Dto spDto = null;
        tzcodes = tzcodes[1].split("\\/");
        for(String tzcode : tzcodes)
        {
            GyjMatch gyjMatch = gyjMatchMap.get(tzcode);//匹配场次信息
            if(gyjMatch == null)
            {
                logger.error("[设置投注项赔率(猜冠亚军)]匹配不到冠亚军场次信息!期次号=" + period + ",场次号=" + tzcode);
                throw new ServiceException(ErrorCode.SERVER_ERROR);
            }
            spTzContentBuilder.append("/" + tzcode + "(" + gyjMatch.getSp() + ")");
        }
        String sptzcontent = tzcontents[0] + "|" + period + "=" + spTzContentBuilder.toString().substring(1);
        schemeBean.setTzspcontent(sptzcontent);//设置待赔率的投注串
    }

    /**
     * 拆分福彩3D/排列3投注串
     * @author  mcdog
     * @param   schemeBean  方案业务对象
     * @param   lottery     彩种对象
     */
    public void splitTzcontentForFc3dAndPl3(SchemeBean schemeBean,Lottery lottery) throws ServiceException
    {
        //截取投注串,判断玩法和投注方式
        StringBuilder tzcontentBuilder = new StringBuilder();
        String[] tempTzcodes = schemeBean.getTzcontent().split(";");//提取投注串
        for(String tempTzcode : tempTzcodes)
        {
            //判断是否为组三单式/组六单式
            if(tempTzcode.endsWith("2:1") || tempTzcode.endsWith("3:1"))
            {
                String[] tzcodes = tempTzcode.split(":");
                try
                {
                    //组三单式
                    if ("2".equals(tzcodes[1]) && "1".equals(tzcodes[2]))
                    {
                        if (tzcodes[0].indexOf("|") < 0)
                        {
                            logger.error("[保存方案]组三单式投注项格式错误!投注项=" + tzcodes[0] + ",用户编号=" + schemeBean.getUserId() + ",彩种=" + lottery.getName());
                            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                        }
                        splitZ3Simple(schemeBean,tempTzcode);//拆分组三单式投注串
                    }
                    //组六单式
                    else if ("3".equals(tzcodes[1]) && "1".equals(tzcodes[2]))
                    {
                        if (tzcodes[0].indexOf("|") < 0)
                        {
                            logger.error("[保存方案]组六单式投注项格式错误!投注项=" + tzcodes[0] + ",用户编号=" + schemeBean.getUserId() + ",彩种=" + lottery.getName() + ",");
                            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                        }
                        splitZ6Simple(schemeBean,tempTzcode);//拆分组六单式投注串
                    }
                    tzcontentBuilder.append(";" + schemeBean.getTzcontent());
                }
                catch(ServiceException e)
                {
                    logger.error("[保存方案]投注项格式错误!投注项=" + schemeBean.getTzcontent() + ",用户编号=" + schemeBean.getUserId() + ",彩种=" + lottery.getName());
                    throw e;
                }
            }
            //如果是排列3/福彩3D的组六复式玩法,且注数只有1注,则将复式变成单式
            else if(tempTzcode.endsWith("3:3") && tempTzcode.split(",").length == 3)
            {
                tzcontentBuilder.append(";" + (tempTzcode.substring(0,tempTzcode.indexOf(":") + 1) + "3:1"));
            }
            else
            {
                tzcontentBuilder.append(";" + tempTzcode);
            }
        }
        schemeBean.setTzcontent(tzcontentBuilder.toString().substring(1));
    }

    /**
     * 拆分组三单式投注串
     * @author  mcdog
     * @param   schemeBean  方案业务对象
     * @param   tzcontent   待拆分的投注串
     */
    public void splitZ3Simple(SchemeBean schemeBean,String tzcontent) throws ServiceException
    {
        /**
         * 校验格式
         */
        String[] tzcodes = tzcontent.split(":")[0].split("\\|");
        if(tzcodes.length < 2)
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        String[] bthCodes = tzcodes[0].split(",");//不同号选项
        String[] thCodes = tzcodes[1].split(",");//同号选项
        if(bthCodes.length == 0 || thCodes.length == 0)
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 拆分号码
         */
        StringBuilder tzContentBuilder = new StringBuilder();
        for(String bthCode : bthCodes)
        {
            if(tzcodes[1].indexOf(bthCode) > -1)
            {
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            for(String thCode : thCodes)
            {
                tzContentBuilder.append(";" + bthCode + "," + thCode + "," + thCode + ":2:1");
            }
        }
        if(tzContentBuilder.length() > 0)
        {
            schemeBean.setTzcontent(tzContentBuilder.toString().substring(1));
        }
        else
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
    }

    /**
     * 拆分组六单式投注串
     * @author  mcdog
     * @param   schemeBean  方案业务对象
     * @param   tzcontent   待拆分的投注串
     */
    public void splitZ6Simple(SchemeBean schemeBean,String tzcontent) throws ServiceException
    {
        /**
         * 校验格式
         */
        String[] tzcodes = tzcontent.split(":")[0].split("\\|");
        if(tzcodes.length < 3)
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        String[] firstCodes = tzcodes[0].split(",");//第一位置选项
        String[] secondCodes = tzcodes[1].split(",");//第二位置选项
        String[] thirdCodes = tzcodes[2].split(",");//第三位置选项
        if(firstCodes.length == 0 || secondCodes.length == 0 || thirdCodes.length == 0)
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 拆分号码
         */
        StringBuilder tzContentBuilder = new StringBuilder();
        for(String firstCode : firstCodes)
        {
            if(tzcodes[1].indexOf(firstCode) > -1 || tzcodes[2].indexOf(firstCode) > -1)
            {
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            for(String secondCode : secondCodes)
            {
                if(tzcodes[2].indexOf(secondCode) > -1)
                {
                    throw new ServiceException(ErrorCode_API.SERVER_ERROR);
                }
                for(String thirdCode : thirdCodes)
                {
                    tzContentBuilder.append(";" + firstCode + "," + secondCode + "," + thirdCode + ":3:1");
                }
            }
        }
        if(tzContentBuilder.length() > 0)
        {
            schemeBean.setTzcontent(tzContentBuilder.toString().substring(1));
        }
        else
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
    }

    /**
     * 拆分快三投注串
     * @author  mcdog
     * @param   schemeBean  方案业务对象
     * @param   lottery     彩种对象
     */
    public void splitTzcontentForK3(SchemeBean schemeBean,Lottery lottery) throws ServiceException
    {
        StringBuilder newTzcontentsBuilder = new StringBuilder();
        String[] tzcotents = schemeBean.getTzcontent().split(";");
        Map<String,List> tzcodeMap = new HashMap<String,List>();//用来保存单组投注项
        for(String tzcontent : tzcotents)
        {
            //如果包含两同号单选玩法,则重新拼接投注串
            if(tzcontent.endsWith("7:1"))
            {
                String[] tzcodes = tzcontent.split(":")[0].split(",");
                String thcodes = tzcodes[0];//同号选项
                List bthcodes = tzcodeMap.get(thcodes);//不同号选项
                if(bthcodes == null)
                {
                    bthcodes = new ArrayList();
                }
                if(!bthcodes.contains(tzcodes[2]))
                {
                    bthcodes.add(tzcodes[2]);
                }
                tzcodeMap.put(thcodes,bthcodes);
            }
            else
            {
                newTzcontentsBuilder.append(";" + tzcontent);
            }
        }
        //循环读取两同号选项
        for(Map.Entry<String,List> entry : tzcodeMap.entrySet())
        {
            List<String> bthcodeList = entry.getValue();
            Collections.sort(bthcodeList);
            String bthcodes = "";
            for(String bthcode : bthcodeList)
            {
                bthcodes += "," + bthcode;
            }
            bthcodes = bthcodes.substring(1);
            newTzcontentsBuilder.append(";" + (entry.getKey() + "|" + bthcodes + ":7:1"));
        }
        schemeBean.setTzcontent(newTzcontentsBuilder.toString().substring(1));
    }

    /**
     * 根据彩种获取方案编号前缀
     * @author  mcdog
     * @param   lotteryId   彩种id
     */
    public synchronized static String getSchemeCodePrefix(String lotteryId)
    {
        String prefix = "";
        if(LotteryUtils.isJc(lotteryId))
        {
            prefix = SchemeConstants.SCHEME_SCODE_JC;
        }
        else if(LotteryUtils.isKp(lotteryId))
        {
            prefix = SchemeConstants.SCHEME_SCODE_KP;
        }
        else if(LotteryUtils.isMp(lotteryId))
        {
            prefix = SchemeConstants.SCHEME_SCODE_MP;
        }
        else if(LotteryUtils.isZC(lotteryId))
        {
            prefix = SchemeConstants.SCHEME_SCODE_ZC;
        }
        return prefix;
    }

    /**
     * 设置方案状态的进度
     * @author  mcdog
     * @param   scheme      数据源(方案对象)
     * @param   schemeData  结果对象
     */
    public void settingSchemeStatusJindu(Scheme scheme,Dto schemeData)
    {
        //如果方案状态为支付成功/出票中/出票失败待撤单/截止未出票待撤单,则进度依次为:发起预约-预约成功-方案开奖-方案派奖
        List<Dto> jinduList = new ArrayList<Dto>();
        if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_ZFCG
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPZ
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPSB
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_TKF
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_ETF)
        {
            Dto jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_ZFCG));//节点名称-发起预约
            jinduDto.put("time",DateUtil.formatDate(scheme.getCreateTime(),DateUtil.MDHMS_FORMAT));//节点时间为方案发起时间
            jinduDto.put("status",1);//节点状态为已完成 0-等待完成 1-已完成
            jinduDto.put("cflag",1);//节点为最新的已完成的节点 0-否 1-是
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_CPCG));//节点名称-预约成功
            jinduDto.put("time","");//节点时间为""
            jinduDto.put("status",0);//节点状态为等待完成 0-等待完成 1-已完成
            jinduDto.put("cflag",0);//节点非最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.SCHEME_STATUS_FAKJ_DESC);//节点名称-方案开奖
            jinduDto.put("time","");//节点时间为""
            jinduDto.put("status",0);//节点状态为等待完成
            jinduDto.put("cflag",0);//节点非最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.SCHEME_STATUS_FAPJ_DESC);//节点名称-方案派奖
            jinduDto.put("time","");//节点时间为""
            jinduDto.put("status",0);//节点状态为等待完成
            jinduDto.put("cflag",0);//节点非最新的已完成的节点
            jinduList.add(jinduDto);
        }
        //如果方案状态为出票成功
        else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPCG)
        {
            Dto jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_ZFCG));//节点名称-发起预约
            jinduDto.put("time",DateUtil.formatDate(scheme.getCreateTime(),DateUtil.MDHMS_FORMAT));//节点时间为方案发起时间
            jinduDto.put("status",1);//节点状态为已完成
            jinduDto.put("cflag",0);//节点非最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_CPCG));//节点名称-预约成功
            jinduDto.put("time",DateUtil.formatDate(scheme.getOutTicketTime(),DateUtil.MDHMS_FORMAT));//节点时间为方案出票时间
            jinduDto.put("status",1);//节点状态为已完成
            jinduDto.put("cflag",scheme.getOpenStatus() == 0? 1 : 0);//根据方案计奖状态,设置节点是否为最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.SCHEME_STATUS_FAKJ_DESC);//节点名称-方案开奖

            //根据方案计奖状态设置节点时间和节点状态
            if(scheme.getOpenStatus() == 0)
            {
                jinduDto.put("time","");//节点时间为""
                jinduDto.put("status",0);//节点状态为等待完成
                jinduDto.put("cflag",0);//节点非最新的已完成的节点
            }
            else
            {
                jinduDto.put("time",DateUtil.formatDate(scheme.getOpenTime(),DateUtil.MDHMS_FORMAT));//节点时间为计奖时间
                jinduDto.put("status",1);//节点状态为已完成
                jinduDto.put("cflag",scheme.getPrizeStatus() < 2? 1 : 0);//根据派奖状态设置节点是否为最新的已完成的节点
            }
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.SCHEME_STATUS_FAPJ_DESC);//节点名称-方案派奖
            if(scheme.getPrizeStatus() == 2)
            {
                jinduDto.put("time",DateUtil.formatDate(scheme.getPrizeTime(),DateUtil.MDHMS_FORMAT));//节点时间为派奖时间
                jinduDto.put("status",1);//节点状态为已完成
                jinduDto.put("cflag",1);//节点为最新的已完成的节点
            }
            else
            {
                jinduDto.put("time","");//节点时间时间为""
                jinduDto.put("status",0);//节点状态为等待完成
                jinduDto.put("cflag",0);//节点非最新的已完成的节点
            }
            jinduList.add(jinduDto);
        }
        //如果方案状态为出票失败
        /*else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPSB)
        {
            Dto jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_ZFCG));//节点名称-发起预约
            jinduDto.put("time",DateUtil.formatDate(scheme.getCreateTime(),DateUtil.MDHMS_FORMAT));//节点时间为方案发起时间
            jinduDto.put("status",1);//节点状态为已完成
            jinduDto.put("cflag",0);//节点非最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_CPSB));//节点名称-预约失败
            jinduDto.put("time",DateUtil.formatDate(scheme.getOutTicketTime(),DateUtil.MDHMS_FORMAT));//节点时间
            jinduDto.put("status",1);//节点状态为已完成
            jinduDto.put("cflag",0);//节点为最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.SCHEME_STATUS_FATK_DESC);//节点名称-方案退款
            jinduDto.put("time",DateUtil.formatDate(scheme.getOutTicketTime(),DateUtil.MDHMS_FORMAT));//节点时间为出票时间
            jinduDto.put("status",1);//节点状态为等待完成
            jinduDto.put("cflag",1);//节点为最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.SCHEME_STATUS_FAPJ_DESC);//节点名称-已退款
            jinduDto.put("time",DateUtil.formatDate(scheme.getOutTicketTime(),DateUtil.MDHMS_FORMAT));//节点时间出票时间
            jinduDto.put("status",0);//节点状态为等待完成
            jinduDto.put("cflag",0);//节点非最新的已完成的节点
            jinduList.add(jinduDto);
        }*/
        //如果方案状态为撤单成功
        else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
        {
            Dto jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_ZFCG));//节点名称-发起预约
            jinduDto.put("time",DateUtil.formatDate(scheme.getCreateTime(),DateUtil.MDHMS_FORMAT));//节点时间为方案发起时间
            jinduDto.put("status",1);//节点状态为已完成
            jinduDto.put("cflag",0);//节点非最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.schemeStatusClientMap.get(SchemeConstants.SCHEME_STATUS_CPSB));//节点名称-预约失败
            jinduDto.put("time",DateUtil.formatDate(scheme.getOutTicketTime(),DateUtil.MDHMS_FORMAT));//节点时间为出票时间
            jinduDto.put("status",1);//节点状态为已完成
            jinduDto.put("cflag",0);//节点非最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.SCHEME_STATUS_FATK_DESC);//节点名称-方案退款
            jinduDto.put("time",DateUtil.formatDate(scheme.getOutTicketTime(),DateUtil.MDHMS_FORMAT));//节点时间为出票时间
            jinduDto.put("status",1);//节点状态为已完成
            jinduDto.put("cflag",0);//节点非最新的已完成的节点
            jinduList.add(jinduDto);

            jinduDto = new BaseDto();
            jinduDto.put("name",SchemeConstants.SCHEME_STATUS_YTK_DESC);//节点名称-已退款
            jinduDto.put("time",DateUtil.formatDate(scheme.getPrizeTime(),DateUtil.MDHMS_FORMAT));//节点时间为派奖时间
            jinduDto.put("status",1);//节点状态为已完成
            jinduDto.put("cflag",1);//节点为最新的已完成的节点
            jinduList.add(jinduDto);
        }
        schemeData.put("jindu",jinduList);
    }

    /**
     * 查询待拆票方案列表
     * @return
     * @throws ServiceException
     */
    public List<Scheme> querySchemePaySuccess(Integer schemeType) throws Exception {
        return schemeMapper.querySchemePaySuccess(schemeType);
    }

    /**
     * 查询待拆票追号方案列表
     * @return
     * @throws ServiceException
     */
    public List<SchemeZhuiHao> queryZhuiHaoSchemePaySuccess() throws Exception {
        return schemeMapper.queryZhuiHaoSchemePaySuccess();
    }

    /**
     * 更新方案出票状态
     * @param scheme
     * @return
     * @throws ServiceException
     * @throws Exception
     */
    public int updateSchemeTicketStatus(Scheme scheme) throws Exception {
        if(StringUtil.isEmpty(scheme)) {
            return 0;
        }
        if(scheme.getSchemeType() == 1) {//追号方案
            return schemeMapper.updateZhuiHaoSchemeTicketStatus(scheme);
        } else {
            return schemeMapper.updateSchemeTicketStatus(scheme);
        }
    }

    /**
     * 更新跟单方案出票状态
     * @param followSchemeId
     * @param schemeStatus
     * @return
     * @throws Exception
     */
    public int updateSchemeStatusFollow(String followSchemeId, Integer schemeStatus) throws Exception {
        return schemeMapper.updateSchemeStatusFollow(followSchemeId, schemeStatus);
    }

    /**
     * 根据订单号更新方案出票状态-拆票
     * @param scheme
     * @return
     * @throws ServiceException
     * @throws Exception
     */
    public int updateSchemeTicketStatusBySchemeOrderId(Scheme scheme) throws Exception {
        if(StringUtil.isEmpty(scheme)) {
            return 0;
        }
        if(scheme.getSchemeType() == 1) {//追号方案
            return schemeMapper.updateZhuiHaoSchemeStatusBySchemeOrderId(scheme);
        } else {
            return schemeMapper.updateSchemeStatusBySchemeOrderId(scheme);
        }
    }

    /**
     * 更新追号方案出票状态-拆票
     * @param scheme
     * @return
     * @throws ServiceException
     * @throws Exception
     */
    public int updateZhuihaoSchemeTicketStatus(SchemeZhuiHao scheme) throws Exception {
        Scheme scheme1 = new Scheme();
        scheme1.setSchemeStatus(scheme.getSchemeStatus());
        scheme1.setSchemeStatusDesc(scheme.getSchemeStatusDesc());
        scheme1.setId(scheme.getId());
        return schemeMapper.updateZhuiHaoSchemeTicketStatus(scheme1);
    }

    /**
     * 查询场次截止后还是出票中的方案列表-竞彩
     * @param lottId
     * @param matchCode
     * @return
     * @throws Exception
     */
    public List<Scheme> queryJcNoSuccessSchemeForEndTime(String lottId, String matchCode) throws Exception {
        return schemeMapper.queryJcNoSuccessSchemeForEndTime(lottId, matchCode);
    }

    /**
     * 查询场次截止后还是出票中的方案列表-数字彩
     * @param lottId
     * @param period
     * @return
     * @throws Exception
     */
    public List<Scheme> querySzcNoSuccessSchemeForEndTime(String lottId, String period) throws Exception {
        return schemeMapper.querySzcNoSuccessSchemeForEndTime(lottId, period);
    }

    /**
     * 查询场次相关的所有方案
     * @param lottId
     * @param matchCode
     * @return
     * @throws Exception
     */
    public List<SchemeMatches> querySchemeForMatch(String lottId, String matchCode) throws Exception {
        return schemeMapper.querySchemeForMatch(lottId, matchCode);
    }

    /**
     * 根据方案编号查询方案信息
     * @param schemeOrderId
     * @return
     * @throws Exception
     */
    public Scheme querySchemeInfoBySchemeOrderId(String schemeOrderId) throws Exception {
        return schemeMapper.querySchemeInfoBySchemeOrderId(schemeOrderId);
    }

    /**
     * 根据方案编号查询追号方案信息
     * @param schemeOrderId
     * @return
     * @throws Exception
     */
    public SchemeZhuiHao queryZhuihaoSchemeInfoBySchemeOrderId(String schemeOrderId) throws Exception {
        return schemeMapper.queryZhuihaoSchemeInfoBySchemeOrderId(schemeOrderId);
    }

    /**
     * 更新方案计奖状态与奖金
     * @param scheme
     * @return
     */
    public int updateSchemeStatusPrize(Scheme scheme) {
        return schemeMapper.updateSchemeStatusPrize(scheme);
    }

    /**
     * 更新追号方案计奖状态与奖金
     * @param scheme
     * @return
     */
    public int updateZhuihaoSchemeStatusPrize(SchemeZhuiHao scheme) {
        return schemeMapper.updateZhuihaoSchemeStatusPrize(scheme);
    }

    /**
     * 根据主键编号查询方案信息
     * @param id
     * @return
     * @throws Exception
     */
    public Scheme querySchemeInfoById(Long id) throws Exception {
        return schemeMapper.querySchemeInfoById(id);
    }

    /**
     * 查询奖金低于maxPrize的待派奖方案列表-竞彩
     * @param lottId
     * @param maxPrize
     * @return
     * @throws Exception
     */
    public List<Scheme> queryAutoSendMoneySchemeList(String lottId, int maxPrize) throws Exception {
        return schemeMapper.queryAutoSendMoneySchemeList(lottId, maxPrize);
    }

    /**
     * 查询奖金低于maxPrize的待派奖方案列表-数字彩
     * @param period
     * @param maxPrize
     * @return
     * @throws Exception
     */
    public List<Scheme> queryAutoSendMoneySzcSchemeList(String period, int maxPrize) throws Exception {
        return schemeMapper.queryAutoSendMoneySzcSchemeList(period, maxPrize);
    }

    /**
     * 根据主键编号查询未预约成功的追号方案列表
     * @param schemeId
     * @param schemeStatus
     * @return
     * @throws Exception
     */
    public List<SchemeZhuiHao> queryZhuihaoSchemeInfoById(Long schemeId, Integer schemeStatus) {
        return schemeMapper.queryZhuihaoSchemeInfoById(schemeId, schemeStatus);
    }

    /**
     * 查询出票中的方案列表
     * @return
     * @throws Exception
     */
    public List<Scheme> queryOutTicketingSchemeList() throws Exception {
        return schemeMapper.queryOutTicketingSchemeList();
    }

    /**
     * 查询需要返利的方案列表
     * @param backStatus
     * @return
     * @throws Exception
     */
    public List<Scheme> queryRebateSchemeList(Integer backStatus) throws Exception {
        return schemeMapper.queryRebateSchemeList(backStatus);
    }

    /**
     * 查询场次包含的所有订单对应的全部场次-竞彩足球
     * @param lottId
     * @param matchCode
     * @return
     * @throws Exception
     */
    public Map<String, JczqAwardInfo> querySchemeForJczqMatch(String lottId, String matchCode) throws Exception {
        List<MatchFootBall> list = schemeMapper.querySchemeForJczqMatch(lottId, matchCode);
        if(StringUtil.isEmpty(list)) {
            return null;
        }
        Map<String, JczqAwardInfo> jczqMap = new HashMap<>();
        for(MatchFootBall match : list) {
            if(jczqMap.containsKey(match.getMatchCode())) {
                continue;
            }
            JczqAwardInfo award = convertJczqAwardMatchInfo(match);
            award.init();//初始化赛果
            jczqMap.put(match.getMatchCode(), award);
        }
        list.clear();
        return jczqMap;
    }

    private JczqAwardInfo convertJczqAwardMatchInfo(MatchFootBall match) {
        if(StringUtil.isEmpty(match)) {
            return null;
        }
        JczqAwardInfo awd = new JczqAwardInfo();
        awd.setId(match.getId());
        awd.setMatchCode(match.getMatchCode());
        awd.setMatchTime(match.getMatchTime());
        awd.setEndTime(match.getEndTime());
        awd.setState(match.getState());
        awd.setLose(match.getLose().intValue());
        awd.setStatus(match.getStatus());
        //未取消|截止销售|已有赛果
        if(awd.getStatus() != LotteryConstants.STATUS_CANCEL && awd.getStatus() != LotteryConstants.STATUS_SELL && match.getState() >= LotteryConstants.MATCHJJ_STATE_THREE) {
            if(StringUtil.isNotEmpty(match.getHalfScore())) {
                String[] halfScore = match.getHalfScore().split("\\:");
                awd.setHhScore(Integer.parseInt(halfScore[0]));
                awd.setHgScore(Integer.parseInt(halfScore[1]));
            }
            if(StringUtil.isNotEmpty(match.getScore())) {
                awd.setScore(match.getScore());
                String[] score = match.getScore().split("\\:");
                awd.sethScore(Integer.parseInt(score[0]));
                awd.setgScore(Integer.parseInt(score[1]));
            }
        }
        return awd;
    }

    /**
     * 查询场次包含的所有订单对应的全部场次-竞彩篮球
     * @param lottId
     * @param matchCode
     * @return
     * @throws Exception
     */
    public Map<String, JclqAwardInfo> querySchemeForJclqMatch(String lottId, String matchCode) throws Exception {
        List<MatchBasketBall> list = schemeMapper.querySchemeForJclqMatch(lottId, matchCode);
        if(StringUtil.isEmpty(list)) {
            return null;
        }
        Map<String, JclqAwardInfo> jczqMap = new HashMap<>();
        for(MatchBasketBall match : list) {
            if(jczqMap.containsKey(match.getMatchCode())) {
                continue;
            }
            JclqAwardInfo award = convertJclqAwardMatchInfo(match);
            award.init();//初始化赛果
            jczqMap.put(match.getMatchCode(), award);
        }
        list.clear();
        return jczqMap;
    }

    private JclqAwardInfo convertJclqAwardMatchInfo(MatchBasketBall match) {
        if(StringUtil.isEmpty(match)) {
            return null;
        }
        JclqAwardInfo award = new JclqAwardInfo();
        award.setId(match.getId());
        award.setMatchCode(match.getMatchCode());
        award.setMatchTime(match.getMatchTime());
        award.setEndTime(match.getEndTime());
        award.setState(match.getState());
        award.setStatus(match.getStatus());
        //未取消|截止销售|已有赛果
        if(award.getStatus() != LotteryConstants.STATUS_CANCEL && award.getStatus() != LotteryConstants.STATUS_SELL && match.getState() >= LotteryConstants.MATCHJJ_STATE_THREE) {
            if(StringUtil.isNotEmpty(match.getScore())) {
                award.setScore(match.getScore());
                String[] score = match.getScore().split("\\:");
                award.sethScore(Integer.parseInt(score[1]));
                award.setgScore(Integer.parseInt(score[0]));
            }
        }
        return award;
    }

    /**
     * 查询方案包含的所有场次是否全部审核通过-竞彩足球
     * @param lottId
     * @param schemeId
     * @return
     * @throws Exception
     */
    public boolean isAuditJczqSchemeForMatch(String lottId, Long schemeId) throws Exception {
        Integer count = schemeMapper.queryNoAuditMatchCountForJczqScheme(lottId, schemeId, LotteryConstants.MATCHJJ_STATE_FILE);
        if(StringUtil.isNotEmpty(count) && count.intValue() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 查询方案包含的所有场次是否全部审核通过-竞彩篮球
     * @param lottId
     * @param schemeId
     * @return
     * @throws Exception
     */
    public boolean isAuditJclqSchemeForMatch(String lottId, Long schemeId) throws Exception {
        Integer count = schemeMapper.queryNoAuditMatchCountForJclqScheme(lottId, schemeId, LotteryConstants.MATCHJJ_STATE_FILE);
        if(StringUtil.isNotEmpty(count) && count.intValue() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 更新数字彩开奖号码至用户订单号
     * @param period
     * @param drawNumber
     * @return
     * @throws Exception
     */
    public void updateSchemeDrawNumber(String lotteryId, String period, String drawNumber) throws Exception {
        //更新非追号订单
        schemeMapper.updateSchemeDrawNumber(drawNumber, period, lotteryId);
        //更新追号订单
        schemeMapper.updateZhuiHaoSchemeDrawNumber(drawNumber, period, lotteryId);
    }

    /**
     * 设置竞彩方案详情
     * @author  mcdog
     * @param   scheme      方案对象(源数据)
     * @param   schemeDto   方案详情对象(详情保存在该对象中)
     */
    public void settingJcSchemeDetail(Scheme scheme,Dto schemeDto) throws ServiceException,Exception
    {
        //获取彩种工具类
        LotteryUtils lotteryUtils = InitPlugin.getLotteryUtils(lotteryUtilsMap,scheme.getLotteryId());
        if(lotteryUtils == null)
        {
            logger.error("[设置竞彩方案详情]获取不到彩种工具类!彩种编号=" + scheme.getLotteryId());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        boolean islq = LotteryUtils.isJclq(scheme.getLotteryId());
        schemeDto.put("islq",islq? 1 : 0);//设置方案是否为竞彩篮球(0-不是 1-是)
        if(islq)
        {
            schemeDto.put("spdesc","竞猜结果包含常规时间和加时后的结果。" + SchemeConstants.SCHEME_CKSP_DESC);//设置方案投注项参考sp描述
        }
        else
        {
            schemeDto.put("spdesc","竞猜结果只含90分钟内（含伤停补时）的结果，不含加时和点球大战。" + SchemeConstants.SCHEME_CKSP_DESC);//设置方案投注项参考sp描述
        }
        //设置过关方式
        String[] tzspContent = scheme.getSchemeSpContent().split("\\|");//提取带sp的投注内容
        boolean iszh = scheme.getSchemeContent().indexOf(";") > -1? true : false;//是否为组合投注
        schemeDto.put("ggfs",iszh? "组合投注" : (tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," ")));//设置过关方式

        //设置是否显示方案中奖情况(方案状态为预约成功才显示中奖情况)
        if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPCG)
        {
            schemeDto.put("showzj",1);//设置是否显示中奖情况 0-不显示 1-显示
        }
        else
        {
            schemeDto.put("showzj",0);
        }

        /**
         * 设置方案中奖状态/中奖状态描述
         */
        //设置方案中奖状态
        schemeDto.put("zstatus",scheme.getOpenStatus());//中奖状态 0-等待开奖 1-未中奖 2-已中奖 3-已撤单
        if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
        {
            schemeDto.put("zstatus",3);
        }
        //设置中奖描述
        if(scheme.getSchemeStatus() <= SchemeConstants.SCHEME_STATUS_CPZ
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPSB
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_TKF
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_ETF)
        {
            schemeDto.put("zdesc","正在预约");
        }
        else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
        {
            schemeDto.put("zdesc","已撤单");
        }
        else
        {
            //方案未中奖
            if(scheme.getOpenStatus() == 1)
            {
                schemeDto.put("zdesc","未中奖");
            }
            //方案中奖,则显示已中奖+中奖金额
            else if(scheme.getOpenStatus() == 2)
            {
                String zdesc = "<font color='#FF0000'>已中奖，奖金" + CalculationUtils.formatToThousandsStr(scheme.getPrizeTax()) + "元";
                int jjcs = 0;//加奖次数
                double tjj = 0;//加奖
                if(scheme.getPrizeSubjoinTax() != null && scheme.getPrizeSubjoinTax() > 0)
                {
                    jjcs ++;
                    tjj += scheme.getPrizeSubjoinTax();
                }
                if(scheme.getPrizeSubjoinSiteTax() != null && scheme.getPrizeSubjoinSiteTax() > 0)
                {
                    jjcs ++;
                    tjj += scheme.getPrizeSubjoinSiteTax();
                }
                if(scheme.getRewardPrize() != null && scheme.getRewardPrize() > 0)
                {
                    jjcs ++;
                }
                zdesc += jjcs > 0? "(" : "";
                if(tjj > 0)
                {
                    zdesc += "加奖" + tjj + "元";
                }
                if(scheme.getRewardPrize() != null && scheme.getRewardPrize() > 0
                        && (scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD
                        || scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_SD))
                {
                    zdesc += tjj > 0? "+" : "";
                    if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD)
                    {
                        zdesc += "支付赏金" + scheme.getRewardPrize() + "元";
                    }
                    else
                    {
                        zdesc += "含打赏" + scheme.getRewardPrize() + "元";
                    }
                }
                zdesc += jjcs > 0? ")" : "";
                zdesc += "</font>";
                schemeDto.put("zdesc",zdesc);
            }
            //默认显示等待开奖
            else
            {
                schemeDto.put("zdesc","等待开奖");
            }
        }

        /**
         * 设置方案场次信息
         */
        //查询方案场次信息
        Map<String,Dto> schemeMatchMaps = new HashMap<String,Dto>();
        List<SchemeMatches> chemeMatchList = schemeMapper.querySchemeMatches(new BaseDto("schemeId",scheme.getId()));
        if(chemeMatchList != null && chemeMatchList.size() > 0)
        {
            if(LotteryUtils.isJczq(scheme.getLotteryId()))
            {
                for(SchemeMatches schemeMatches : chemeMatchList)
                {
                    Dto matchDto = (Dto)memcached.get(LotteryConstants.jczqSchemeMatchPrefix + schemeMatches.getMatchCode());
                    if(matchDto == null)
                    {
                        //查询对阵信息
                        matchDto = new BaseDto();
                        MatchFootBall match = (MatchFootBall) memcached.get(LotteryConstants.jczqMatchPrefix + schemeMatches.getMatchCode());//从缓存中获取场次信息
                        if(match == null)
                        {
                            match = footBallMapper.queryMatchFootBallByMatchCode(schemeMatches.getMatchCode());//从数据库中查询场次信息
                            memcached.set(LotteryConstants.jczqMatchPrefix + schemeMatches.getMatchCode(),match,30);//在缓存中保存30秒
                        }
                        matchDto.put("matchCode",match.getMatchCode());
                        matchDto.put("weekday",match.getWeekDay());
                        matchDto.put("jcId",match.getJcId());
                        matchDto.put("hostName",match.getHostName());
                        matchDto.put("guestName",match.getGuestName());
                        matchDto.put("leagueName",match.getLeagueName());
                        matchDto.put("halfScore",match.getHalfScore());
                        matchDto.put("score",match.getScore());
                        matchDto.put("matchTime",DateUtil.formatDate(match.getMatchTime(),DateUtil.DEFAULT_DATE_TIME));
                        matchDto.put("state",match.getState());

                        //查询即时比分信息
                        if(StringUtil.isEmpty(match.getScore()))
                        {
                            Dto matchJsbfDto = (Dto)memcached.get(LotteryConstants.jczqMatchJsbfPrefix + schemeMatches.getMatchCode());
                            if(matchJsbfDto == null)
                            {
                                Dto queryJsbfDto = new BaseDto("speriod",match.getPeriod());
                                queryJsbfDto.put("jcId",match.getJcId());
                                matchJsbfDto = schemeMapper.querySchemeMatchBfInfo(queryJsbfDto);
                                if(matchJsbfDto != null)
                                {
                                    memcached.set(LotteryConstants.jczqMatchJsbfPrefix + schemeMatches.getMatchCode(),match,30);//在缓存中保存30秒
                                }
                            }
                            if(matchJsbfDto != null)
                            {
                                matchDto.putAll(matchJsbfDto);
                            }
                        }
                        memcached.set(LotteryConstants.jczqSchemeMatchPrefix + schemeMatches.getMatchCode(),matchDto,30);//在缓存中保存30秒
                    }
                    schemeMatchMaps.put(schemeMatches.getMatchCode(),matchDto);
                }
            }
            else
            {
                for(SchemeMatches schemeMatches : chemeMatchList)
                {
                    Dto matchDto = (Dto)memcached.get(LotteryConstants.jclqSchemeMatchPrefix + schemeMatches.getMatchCode());
                    if(matchDto == null)
                    {
                        //查询对阵信息
                        matchDto = new BaseDto();
                        MatchBasketBall match = (MatchBasketBall) memcached.get(LotteryConstants.jclqMatchPrefix + schemeMatches.getMatchCode());//从缓存中获取场次信息
                        if(match == null)
                        {
                            match = basketBallMapper.queryMatchBasketBallByMatchCode(schemeMatches.getMatchCode());//从数据库中查询场次信息
                            memcached.set(LotteryConstants.jclqMatchPrefix + schemeMatches.getMatchCode(),match,30);//在缓存中保存30秒
                        }
                        matchDto.put("matchCode",match.getMatchCode());
                        matchDto.put("weekday",match.getWeekDay());
                        matchDto.put("jcId",match.getJcId());
                        matchDto.put("hostName",match.getHostName());
                        matchDto.put("guestName",match.getGuestName());
                        matchDto.put("leagueName",match.getLeagueName());
                        matchDto.put("halfScore",match.getHalfScore());
                        matchDto.put("score",match.getScore());
                        matchDto.put("matchTime",DateUtil.formatDate(match.getMatchTime(),DateUtil.DEFAULT_DATE_TIME));
                        matchDto.put("state",match.getState());

                        //查询即时比分信息
                        if(StringUtil.isEmpty(match.getScore()))
                        {
                            Dto matchJsbfDto = (Dto)memcached.get(LotteryConstants.jclqMatchJsbfPrefix + schemeMatches.getMatchCode());
                            if(matchJsbfDto == null)
                            {
                                Dto queryJsbfDto = new BaseDto("speriod",match.getPeriod());
                                queryJsbfDto.put("jcId",match.getJcId());
                                matchJsbfDto = schemeMapper.querySchemeMatchBfInfo(queryJsbfDto);
                                if(matchJsbfDto != null)
                                {
                                    memcached.set(LotteryConstants.jclqMatchJsbfPrefix + schemeMatches.getMatchCode(),match,30);//在缓存中保存30秒
                                }
                            }
                            if(matchJsbfDto != null)
                            {
                                matchDto.putAll(matchJsbfDto);
                            }
                        }
                        memcached.set(LotteryConstants.jclqSchemeMatchPrefix + schemeMatches.getMatchCode(),matchDto,30);//在缓存中保存30秒
                    }
                    schemeMatchMaps.put(schemeMatches.getMatchCode(),matchDto);
                }
            }
        }
        //设置投注信息
        schemeDto.put("tzinfos",schemeMatchMaps.size() + "场，"
                + schemeDto.getAsString("ggfs") + "，"
                + scheme.getSchemeZs() + "注" + scheme.getSchemeMultiple() + "倍");

        //设置投注选项
        Dto params = new BaseDto();
        schemeDto.put("tzxxs",lotteryUtils.getJcTzxxList(scheme,schemeMatchMaps,params));//设置投注选项信息

        //设置优化明细
        if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_YH)
        {
            schemeDto.put("ggfs",schemeDto.getAsString("ggfs") + "<font color='#FF0000'>（奖金优化）</font>");
            schemeDto.put("yhinfos",lotteryUtils.getYhinfos(scheme,schemeMatchMaps,params));//设置设置优化明细信息
        }

        //设置出票信息
        List<SchemeTicket> schemeTicketList = ticketMapper.queryTicketListBySchemeId(scheme.getSchemeOrderId());//查询方案出票信息
        schemeDto.put("tkinfos",lotteryUtils.getTicketList(scheme,schemeMatchMaps,schemeTicketList));//设置方案出票详细信息
    }

    /**
     * 设置足彩方案详情
     * @author  mcdog
     * @param   scheme      方案对象(源数据)
     * @param   schemeDto   方案详情对象(详情保存在该对象中)
     */
    public void settingZcSchemeDetail(Scheme scheme,Dto schemeDto) throws ServiceException,Exception
    {
        //获取彩种工具类
        LotteryUtils lotteryUtils = InitPlugin.getLotteryUtils(lotteryUtilsMap,scheme.getLotteryId());
        if(lotteryUtils == null)
        {
            logger.error("[设置竞彩方案详情]获取不到彩种工具类!彩种编号=" + scheme.getLotteryId());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //设置是否显示方案中奖情况(方案状态为预约成功才显示中奖情况)
        if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPCG)
        {
            schemeDto.put("showzj",1);//设置是否显示中奖情况 0-不显示 1-显示
        }
        else
        {
            schemeDto.put("showzj",0);
        }
        /**
         * 设置方案中奖状态/中奖状态描述
         */
        //设置方案中奖状态
        schemeDto.put("zstatus",scheme.getOpenStatus());//中奖状态 0-等待开奖 1-未中奖 2-已中奖 3-已撤单
        if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
        {
            schemeDto.put("zstatus",3);
        }
        //设置方案中奖描述
        if(scheme.getSchemeStatus() <= SchemeConstants.SCHEME_STATUS_CPZ
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPSB
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_TKF
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_ETF)
        {
            schemeDto.put("zdesc","正在预约");
        }
        else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
        {
            schemeDto.put("zdesc","已撤单");
        }
        else
        {
            //方案未中奖,则显示未中奖
            if(scheme.getOpenStatus() == 1)
            {
                schemeDto.put("zdesc","未中奖");
            }
            //方案中奖,则显示已中奖+中奖金额
            else if(scheme.getOpenStatus() == 2)
            {
                String zdesc = "<font color='#FF0000'>已中奖，奖金" + CalculationUtils.formatToThousandsStr(scheme.getPrizeTax()) + "元";
                int jjcs = 0;//加奖次数
                double tjj = 0;//加奖
                if(scheme.getPrizeSubjoinTax() != null && scheme.getPrizeSubjoinTax() > 0)
                {
                    jjcs ++;
                    tjj += scheme.getPrizeSubjoinTax();
                }
                if(scheme.getPrizeSubjoinSiteTax() != null && scheme.getPrizeSubjoinSiteTax() > 0)
                {
                    jjcs ++;
                    tjj += scheme.getPrizeSubjoinSiteTax();
                }
                if(scheme.getRewardPrize() != null && scheme.getRewardPrize() > 0)
                {
                    jjcs ++;
                }
                zdesc += jjcs > 0? "(" : "";
                if(tjj > 0)
                {
                    zdesc += "加奖" + tjj + "元";
                }
                if(scheme.getRewardPrize() != null && scheme.getRewardPrize() > 0
                        && (scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD
                        || scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_SD))
                {
                    zdesc += tjj > 0? "+" : "";
                    if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD)
                    {
                        zdesc += PayConstants.CHANNEL_CODE_IN_SQDASHANG + scheme.getRewardPrize() + "元";
                    }
                    else
                    {
                        zdesc += PayConstants.CHANNEL_CODE_IN_ZFDASHANG + scheme.getRewardPrize() + "元";
                    }
                }
                zdesc += jjcs > 0? ")" : "";
                zdesc += "</font>";
                schemeDto.put("zdesc",zdesc);
            }
            //默认显示等待开奖
            else
            {
                schemeDto.put("zdesc","等待开奖");
            }
        }

        //查询并设置期次的对阵信息
        Map<String,Object> matchMaps = new HashMap<String,Object>();
        if(memcached.get(LotteryConstants.zcPeriodPrefix + scheme.getPeriod()) != null)
        {
            matchMaps.putAll((Map<String,Object>)memcached.get(LotteryConstants.zcPeriodPrefix + scheme.getPeriod()));
        }
        else
        {
            Period period = periodMapper.queryPeriodByPerod(scheme.getLotteryId(),scheme.getPeriod());//查询期次
            matchMaps.putAll(JsonUtil.jsonToMap(period.getMatches()));//提取场次信息
            memcached.add((LotteryConstants.zcPeriodPrefix + scheme.getPeriod()),matchMaps,60);//在缓存中保存足彩期次对阵信息(60秒有效期)
        }

        //设置投注选项信息
        schemeDto.put("tzxxs",lotteryUtils.getZcTzxxList(scheme,matchMaps));

        //设置出票信息
        List<SchemeTicket> schemeTicketList = ticketMapper.queryTicketListBySchemeId(scheme.getSchemeOrderId());//查询方案出票信息
        schemeDto.put("tkinfos",lotteryUtils.getTicketList(scheme,schemeTicketList));//设置方案出票详细信息
    }

    /**
     * 设置数字彩方案详情
     * @author  mcdog
     * @param   scheme      方案对象(源数据)
     * @param   schemeDto   方案详情对象(详情保存在该对象中)
     */
    public void settingSzcSchemeDetail(Scheme scheme,Dto schemeDto) throws ServiceException,Exception
    {
        //获取彩种工具类
        LotteryUtils lotteryUtils = InitPlugin.getLotteryUtils(lotteryUtilsMap,scheme.getLotteryId());
        if(lotteryUtils == null)
        {
            logger.error("[设置竞彩方案详情]获取不到彩种工具类!彩种编号=" + scheme.getLotteryId());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 非追号方案,设置开奖/中奖/派奖等信息
         */
        Period period = periodMapper.queryPeriodByPerod(scheme.getLotteryId(),scheme.getPeriod());//查询期次信息
        if(scheme.getSchemeType() != SchemeConstants.SCHEME_TYPE_ZH)
        {
            //设置开奖状态/开奖号码/开奖时间
            schemeDto.put("kstatus",scheme.getOpenStatus() == 0? 0 : 1);//设置开奖状态 0-未开奖 1-已开奖
            //schemeDto.put("kcode",StringUtil.isNotEmpty(scheme.getDrawNumber())? scheme.getDrawNumber() : lotteryUtils.getDefaultKcodes());//设置开奖号
            schemeDto.put("kcode",StringUtil.isNotEmpty(scheme.getDrawNumber())? scheme.getDrawNumber() : "--");//设置开奖号
            /**
             * 设置开奖时间
             */
            schemeDto.put("ktime","");//默认为空
            if(LotteryUtils.isMp(scheme.getLotteryId()))
            {
                if(scheme.getOpenStatus() == 0)
                {
                    schemeDto.put("ktime","预计" + DateUtil.formatDate(period.getDrawNumberTime(),DateUtil.DEFAULT_DATE_TIME_SECOND) + "开奖");
                }
                else
                {
                    schemeDto.put("ktime","开奖时间：" + DateUtil.formatDate(period.getDrawNumberTime(),DateUtil.DEFAULT_DATE_TIME_SECOND));
                }
            }
            //设置是否显示方案中奖情况(方案状态为预约成功才显示中奖情况)
            if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPCG)
            {
                schemeDto.put("showzj",1);//设置是否显示中奖情况 0-不显示 1-显示
            }
            else
            {
                schemeDto.put("showzj",0);
            }
            /**
             * 设置中奖状态/中奖状态描述
             */
            //设置方案中奖状态
            schemeDto.put("zstatus",scheme.getOpenStatus());//设置中奖状态 0-等待开奖 1-未中奖 2-已中奖 3-已撤单
            if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
            {
                schemeDto.put("zstatus",3);
            }
            //设置方案中奖描述
            if(scheme.getSchemeStatus() <= SchemeConstants.SCHEME_STATUS_CPZ
                    || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPSB
                    || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_TKF
                    || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_ETF)
            {
                schemeDto.put("zdesc","正在预约");
            }
            else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
            {
                schemeDto.put("zdesc","已撤单");
            }
            else
            {
                if(scheme.getOpenStatus() == 1)
                {
                    schemeDto.put("zdesc","未中奖");
                }
                else if (scheme.getOpenStatus() == 2)
                {
                    String zdesc = "<font color='#FF0000'>已中奖，奖金" + CalculationUtils.formatToThousandsStr(scheme.getPrizeTax()) + "元";
                    int jjcs = 0;//加奖次数
                    double tjj = 0;//加奖
                    if(scheme.getPrizeSubjoinTax() != null && scheme.getPrizeSubjoinTax() > 0)
                    {
                        jjcs ++;
                        tjj += scheme.getPrizeSubjoinTax();
                    }
                    if(scheme.getPrizeSubjoinSiteTax() != null && scheme.getPrizeSubjoinSiteTax() > 0)
                    {
                        jjcs ++;
                        tjj += scheme.getPrizeSubjoinSiteTax();
                    }
                    if(scheme.getRewardPrize() != null && scheme.getRewardPrize() > 0)
                    {
                        jjcs ++;
                    }
                    zdesc += jjcs > 0? "(" : "";
                    if(tjj > 0)
                    {
                        zdesc += "加奖" + tjj + "元";
                    }
                    if(scheme.getRewardPrize() != null && scheme.getRewardPrize() > 0
                            && (scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD
                            || scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_SD))
                    {
                        zdesc += tjj > 0? "+" : "";
                        if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD)
                        {
                            zdesc += PayConstants.CHANNEL_CODE_IN_SQDASHANG + scheme.getRewardPrize() + "元";
                        }
                        else
                        {
                            zdesc += PayConstants.CHANNEL_CODE_IN_ZFDASHANG + scheme.getRewardPrize() + "元";
                        }
                    }
                    zdesc += jjcs > 0? ")" : "";
                    zdesc += "</font>";
                    schemeDto.put("zdesc",zdesc);
                }
                else
                {
                    schemeDto.put("zdesc","等待开奖");
                }
            }
            /**
             * 设置派奖状态描述
             */
            //已中奖,根据派奖状态设置描述
            if(scheme.getOpenStatus() == 2)
            {
                schemeDto.put("pdesc",scheme.getPrizeStatus() == 2? "已派奖" : "派奖中");
            }
            //未开奖或未中奖等情况
            else
            {
                schemeDto.put("pdesc","");
            }
        }

        //设置方案投注信息
        schemeDto.put("tzinfos",scheme.getSchemeZs() + "注，" + scheme.getSchemeMultiple() + "倍");

        /**
         * 设置投注选项
         */
        //设置选号是否有前后区 true-有 false-无
        boolean hasQhq = (LotteryConstants.SSQ.equals(scheme.getLotteryId()) || LotteryConstants.DLT.equals(scheme.getLotteryId()))? true : false;
        schemeDto.put("xhxt",hasQhq? 1 : 0);//设置选号形态(0-无 1-前后分区 2-位置顺序)
        schemeDto.put("tzxxs",lotteryUtils.getSzcTzxxList(scheme));//设置投注选项

        /**
         * 设置方案追号期次/开奖/中奖/派奖/方案出票详细等信息
         */
        //追号方案,则设置方案追号期次/开奖/中奖/派奖/方案出票详细等信息
        if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_ZH)
        {
            //查询并设置已追方案信息
            Dto zhQueryDto = new BaseDto("schemeId",scheme.getId());
            zhQueryDto.put("minSchemeStatus",3);//只查询预约成功/预约失败的追号方案
            List<SchemeZhuiHao> zhuiHaoList = schemeMapper.querySchemeZhuihaoInfo(zhQueryDto);
            schemeDto.put("zhinfos",getZhDetail(scheme,zhuiHaoList,period,0,lotteryUtils));//设置已追方案信息

            //查询并设置待追方案信息
            zhQueryDto = new BaseDto("schemeId",scheme.getId());
            zhQueryDto.put("maxSchemeStatus",2);//只查询尚未预约成功/失败的追号方案
            zhuiHaoList = schemeMapper.querySchemeZhuihaoInfo(zhQueryDto);
            schemeDto.put("dzhinfos",getZhDetail(scheme,zhuiHaoList,period,1,lotteryUtils));//设置待追方案信息
        }
        //非追号,则设置方案出票详细信息
        else
        {
            List<SchemeTicket> schemeTicketList = ticketMapper.queryTicketListBySchemeId(scheme.getSchemeOrderId());//查询方案出票信息
            schemeDto.put("tkinfos",lotteryUtils.getTicketList(scheme,schemeTicketList));//设置方案出票详细信息
        }
    }

    /**
     * 设置冠亚军方案详情
     * @author  mcdog
     * @param   scheme      方案对象(源数据)
     * @param   schemeDto   方案详情对象(详情保存在该对象中)
     */
    public void settingGyjSchemeDetail(Scheme scheme,Dto schemeDto) throws ServiceException,Exception
    {
        //获取彩种工具类
        LotteryUtils lotteryUtils = InitPlugin.getLotteryUtils(lotteryUtilsMap,scheme.getLotteryId());
        if(lotteryUtils == null)
        {
            logger.error("[设置猜冠亚军方案详情]获取不到彩种工具类!彩种编号=" + scheme.getLotteryId());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 设置方案详情
         */
        schemeDto.put("spdesc",SchemeConstants.SCHEME_CKSP_DESC);//设置方案投注项参考sp描述
        schemeDto.put("ggfs","单关");//设置过关方式为单关

        //设置是否显示方案中奖情况(方案状态为预约成功才显示中奖情况)
        if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPCG)
        {
            schemeDto.put("showzj",1);//设置是否显示中奖情况 0-不显示 1-显示
        }
        else
        {
            schemeDto.put("showzj",0);
        }
        /**
         * 设置方案中奖状态/中奖状态描述
         */
        //设置方案中奖状态
        schemeDto.put("zstatus",scheme.getOpenStatus());//中奖状态 0-等待开奖 1-未中奖 2-已中奖 3-已撤单
        if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
        {
            schemeDto.put("zstatus",3);
        }
        //设置方案中奖描述
        if(scheme.getSchemeStatus() <= SchemeConstants.SCHEME_STATUS_CPZ
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPSB
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_TKF
                || scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_ETF)
        {
            schemeDto.put("zdesc","正在预约");
        }
        else if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
        {
            schemeDto.put("zdesc","已撤单");
        }
        else
        {
            //方案未中奖
            if(scheme.getOpenStatus() == 1)
            {
                schemeDto.put("zdesc","未中奖");
            }
            //方案中奖,则显示已中奖+中奖金额
            else if(scheme.getOpenStatus() == 2)
            {
                String zdesc = "<font color='#FF0000'>已中奖，奖金" + CalculationUtils.formatToThousandsStr(scheme.getPrizeTax()) + "元";
                int jjcs = 0;//加奖次数
                double tjj = 0;//加奖
                if(scheme.getPrizeSubjoinTax() != null && scheme.getPrizeSubjoinTax() > 0)
                {
                    jjcs ++;
                    tjj += scheme.getPrizeSubjoinTax();
                }
                if(scheme.getPrizeSubjoinSiteTax() != null && scheme.getPrizeSubjoinSiteTax() > 0)
                {
                    jjcs ++;
                    tjj += scheme.getPrizeSubjoinSiteTax();
                }
                if(scheme.getRewardPrize() != null && scheme.getRewardPrize() > 0)
                {
                    jjcs ++;
                }
                zdesc += jjcs > 0? "(" : "";
                if(tjj > 0)
                {
                    zdesc += "加奖" + tjj + "元";
                }
                if(scheme.getRewardPrize() != null && scheme.getRewardPrize() > 0
                        && (scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD
                        || scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_SD))
                {
                    zdesc += tjj > 0? "+" : "";
                    if(scheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_GD)
                    {
                        zdesc += PayConstants.CHANNEL_CODE_IN_SQDASHANG + scheme.getRewardPrize() + "元";
                    }
                    else
                    {
                        zdesc += PayConstants.CHANNEL_CODE_IN_ZFDASHANG + scheme.getRewardPrize() + "元";
                    }
                }
                zdesc += jjcs > 0? ")" : "";
                zdesc += "</font>";
                schemeDto.put("zdesc",zdesc);
            }
            //默认显示等待开奖
            else
            {
                schemeDto.put("zdesc","等待开奖");
            }
        }
        //设置方案场次信息
        Dto queryDto = new BaseDto();
        queryDto.put("period",scheme.getPeriod());
        queryDto.put("lotteryId",scheme.getLotteryId());
        List<Dto> gyjMatchList = matchGyjMapper.queryGyjMatchInfos(queryDto);//查询对阵信息
        Map<String,Dto> matchMaps = new HashMap<String,Dto>();
        if(gyjMatchList != null && gyjMatchList.size() > 0)
        {
            //封装对阵信息
            for(Dto gyjMatchDto : gyjMatchList)
            {
                matchMaps.put(gyjMatchDto.getAsString("matchCode"),gyjMatchDto);
            }
        }
        //设置投注选项
        schemeDto.put("tzxxs",lotteryUtils.getJcTzxxList(scheme,matchMaps,new BaseDto()));//设置投注选项信息

        //设置出票信息
        List<SchemeTicket> schemeTicketList = ticketMapper.queryTicketListBySchemeId(scheme.getSchemeOrderId());//查询方案出票信息
        schemeDto.put("tkinfos",lotteryUtils.getTicketList(scheme,matchMaps,schemeTicketList));//设置方案出票详细信息
    }

    /**
     * 方案撤单
     * @author kouyi
     */
    public int updateSchemeForCancel(Dto params) throws Exception
    {
        int count = 0;
        //校验参数
        if(StringUtil.isEmpty(params.get("id")))
        {
            logger.error("[方案撤单] 撤单失败,找不到方案号!");
            return 0;
        }
        //方案是否存在
        boolean iszh = "1".equals(params.getAsString("iszh"));//是否为追号
        List<Dto> schemeList = new ArrayList<Dto>();
        if(iszh)
        {
            schemeList.addAll(schemeMapper.querySchemeZhs(params));//查询追号方案
        }
        else
        {
            schemeList.addAll(schemeMapper.queryUserSchemes(new BaseDto("id",params.get("id"))));//查询普通方案
        }
        if(schemeList == null || schemeList.size() == 0)
        {
            logger.error("[方案撤单] 找不到方案信息 方案号=" + params.get("id"));
            return 0;
        }
        /**
         * 判断方案是否允许撤单(方案状态为支付成功/出票中/出票失败,且方案类型不能为追号)
         */
        //根据方案状态判断是否允许撤单
        Dto schemeDto = schemeList.get(0);
        long userId = schemeDto.getAsLong("schemeUserId");//提取用户编号
        int schemeStatus = schemeDto.getAsInteger("schemeStatus");//提取方案状态
        Integer schemeType = schemeDto.getAsInteger("schemeType");//提起方案类型
        if(!(schemeStatus == SchemeConstants.SCHEME_STATUS_ZFCG
                || schemeStatus == SchemeConstants.SCHEME_STATUS_CPZ
                || schemeStatus == SchemeConstants.SCHEME_STATUS_CPSB
                || schemeStatus == SchemeConstants.SCHEME_STATUS_TKF
                || schemeStatus == SchemeConstants.SCHEME_STATUS_ETF))
        {
            logger.error("[方案撤单] 方案未满足撤单条件 方案号=" + params.get("id"));
            return 0;
        }
        //如果方案状态为出票中,则需要根据方案编号去查询该方案的所有出票情况,只有所有票张的状态都为系统撤单/出票失败/待提票时,才允许撤单
        if(schemeStatus == 2)
        {
            List<Dto> ticketList = ticketMapper.queryTicketList(new BaseDto("schemeId",schemeDto.getAsString("schemeOrderId")));//查询出票信息
            if(ticketList != null && ticketList.size() > 0)
            {
                for(Dto ticketDto : ticketList)
                {
                    int ticketStatus = ticketDto.getAsInteger("ticketStatus");
                    if(ticketStatus != SchemeConstants.TICKET_STATUS_CANCEL
                            && ticketStatus != SchemeConstants.TICKET_STATUS_FAIL
                            && ticketStatus != SchemeConstants.TICKET_STATUS_WAITING)
                    {
                        logger.error("[方案撤单] 方案部分票已不允许撤单! 票单id:" + ticketDto.getAsString("id") + ",票单状态描述:" + ticketDto.getAsString("ticketDesc"));
                        return 0;
                    }
                }
            }
        }
        //撤单
        params.put("schemeStatus",SchemeConstants.SCHEME_STATUS_CDCG);
        params.put("schemeStatusDesc",StringUtil.isEmpty(params.getAsString("statusDesc"))?SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_CDCG):params.getAsString("statusDesc"));
        if(iszh)
        {
            count = schemeMapper.cancelSchemeZh(params);//追号方案撤单
        }
        else
        {
            count = schemeMapper.cancelScheme(params);//普通方案撤单
        }
        if(count > 0)
        {
            //更新用户账户余额等信息
            double schemeMoney = iszh? schemeDto.getAsDouble("schemeMoney") : schemeDto.getAsDouble("schemePayMoney");
            UserAccount beforeUserAccount = userAccountMapper.queryUserAccountInfoByUserId(userId);//查询用户账户更新前信息
            UserAccount afterUserAccount = beforeUserAccount;//用户账户更新后信息
            Calendar current = Calendar.getInstance();
            if(schemeMoney > 0)
            {
                Dto userAccountDto = new BaseDto("userId",userId);
                userAccountDto.put("tbalance",schemeMoney);//方案所减少的余额
                userAccountDto.put("tconsume",-schemeMoney);//方案所增加的累计消费金额
                userAccountDto.put("offsetWithDraw",schemeDto.getAsDouble("offsetWithDraw"));//方案抵消的可提现金额
                userAccountDto.put("offsetUnWithDraw",schemeDto.getAsDouble("offsetUnWithDraw"));//方案抵消的不可提现金额
                userAccountMapper.updateUserAccount(userAccountDto);

                //添加账户现金流水
                afterUserAccount = userAccountMapper.queryUserAccountInfoByUserId(schemeDto.getAsLong("schemeUserId"));//查询用户账户更新后信息
                UserDetail userDetail = new UserDetail();
                userDetail.setUserId(userId);//账户id
                userDetail.setInType(false);//流水类型为存入
                userDetail.setChannelCode(PayConstants.CHANNEL_CODE_IN_YYFAIL);//业务渠道为407(预约失败退款)
                userDetail.setChannelDesc(StringUtil.isEmpty(params.getAsString("statusDesc"))?PayConstants.channelCodeMap.get(userDetail.getChannelCode()):params.getAsString("statusDesc"));//业务渠道描述
                userDetail.setMoney(schemeMoney);//交易金额
                userDetail.setLastBalance(beforeUserAccount.getBalance());//交易前账户余额
                userDetail.setBalance(afterUserAccount.getBalance());//交易后账户余额
                userDetail.setLastWithDraw(beforeUserAccount.getWithDraw());//交易前账户可提现金额
                userDetail.setWithDraw(afterUserAccount.getWithDraw());//交易后账户可提现金额
                userDetail.setLastUnWithDraw(beforeUserAccount.getUnWithDraw());//交易前账户不可提现金额
                userDetail.setUnWithDraw(afterUserAccount.getUnWithDraw());//交易后账户不可提现金额
                userDetail.setClientFrom(schemeDto.getAsInteger("clientSource"));//客户端来源
                userDetail.setBusinessId(schemeDto.getAsString("id"));//业务关联编号
                userDetail.setCreateTime(current.getTime());//流水时间
                userDetail.setRemark(schemeDto.getAsString("lotteryName") + "退款[" + schemeDto.getAsString("schemeOrderId") + "]");//设置备注
                userDetailMapper.insertUserDetail(userDetail);//添加流水
            }

            /**
             * 如果该方案(非追号)有用优惠券支付,则退还用户优惠券并添加优惠券退还流水
             */
            if(!(schemeType == null || schemeType == SchemeConstants.SCHEME_TYPE_ZH))
            {
                Dto couponQueryDto = new BaseDto("userId",userId);
                couponQueryDto.put("useStatus",2);
                couponQueryDto.put("schemeId",schemeDto.get("id"));
                List<Dto> couponDataList = userCouponMapper.queryUserCoupons(couponQueryDto);
                if(couponDataList != null && couponDataList.size() > 0)
                {
                    Dto couponData = couponDataList.get(0);
                    Dto couponUpdateDto = new BaseDto("id",couponData.get("id"));
                    couponUpdateDto.put("status",1);

                    //判断优惠券类型,将优惠券的使用时间或过期时间延长
                    Calendar couponCalendar = Calendar.getInstance();//优惠券使用时间
                    couponCalendar.setTime(DateUtil.parseDate(schemeDto.getAsString("createTime"),DateUtil.DEFAULT_DATE_TIME));
                    int millis = (int)(current.getTimeInMillis() - couponCalendar.getTimeInMillis());//当前时间与优惠券使用时间之间相差的毫秒
                    couponCalendar.add(Calendar.MILLISECOND,millis);
                    String cutype = couponData.getAsString("cuType");
                    if("0".equals(cutype))
                    {
                        //将发行限制期限类型的优惠券的过期时间延长
                        couponUpdateDto.put("endTime",DateUtil.formatDate(couponCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
                    }
                    //如果优惠券类型为为使用期限,则判断优惠券针对用户的过期时间
                    else if("1".equals(cutype))
                    {
                        //将使用期限类型的优惠券的过期时间延长
                        couponUpdateDto.put("expireTime",DateUtil.formatDate(couponCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
                    }
                    //更新用户优惠券的使用状态
                    userCouponMapper.updateUserCoupon(couponUpdateDto);

                    //添加用户优惠券流水
                    UserDetail userDetail = new UserDetail();
                    userDetail.setUserId(userId);//账户id
                    userDetail.setInType(false);//流水类型为存入
                    userDetail.setChannelCode(PayConstants.CHANNEL_CODE_IN_YHJBACK);//业务渠道为412(优惠券退回)
                    userDetail.setChannelDesc(StringUtil.isEmpty(params.getAsString("statusDesc"))?PayConstants.channelCodeMap.get(userDetail.getChannelCode()):params.getAsString("statusDesc"));//业务渠道描述
                    userDetail.setMoney(schemeDto.getAsDouble("schemePayMoney"));//交易金额
                    userDetail.setLastBalance(beforeUserAccount.getBalance());//交易前账户余额
                    userDetail.setBalance(afterUserAccount.getBalance());//交易后账户余额
                    userDetail.setLastWithDraw(beforeUserAccount.getWithDraw());//交易前账户可提现金额
                    userDetail.setWithDraw(afterUserAccount.getWithDraw());//交易后账户可提现金额
                    userDetail.setLastUnWithDraw(beforeUserAccount.getUnWithDraw());//交易前账户不可提现金额
                    userDetail.setUnWithDraw(afterUserAccount.getUnWithDraw());//交易后账户不可提现金额
                    userDetail.setClientFrom(schemeDto.getAsInteger("clientSource"));//客户端来源
                    userDetail.setBusinessId(schemeDto.getAsString("id"));//业务关联编号
                    userDetail.setCreateTime(current.getTime());//流水时间
                    userDetailMapper.insertUserDetail(userDetail);//添加流水
                }
            }
            //跟单方案撤单-更新发单人信息
            if(!iszh && schemeType == SchemeConstants.SCHEME_TYPE_GD) {
                //更新跟单表-订单状态=出票失败
                schemeMapper.updateSchemeStatusFollow(schemeDto.getAsString("schemeOrderId"), 2);
                //更新发单人-已跟单金额
                schemeMapper.updateSchemeFollowMoney(schemeDto.getAsString("schemeOrderId"));
                logger.info("[方案撤单] 更新跟单表订单状态以及发单人已跟单金额成功.");
            }
        }
        return count;
    }

    /**
     * 设置追号详情
     * @author  mcdog
     * @param   scheme          方案对象
     * @param   zhuiHaoList     追号方案集合
     * @param   period          方案所属期次对象
     * @param   type            类型(0-已追号 1-待追号)
     * @param   lotteryUtils    彩种工具类
     */
    public List<Dto> getZhDetail(Scheme scheme,List<SchemeZhuiHao> zhuiHaoList,Period period,int type,LotteryUtils lotteryUtils)
    {
        //设置方案追号信息
        List<Dto> zhList = new ArrayList<Dto>();
        if(zhuiHaoList != null && zhuiHaoList.size() > 0)
        {
            SchemeZhuiHao zhuiHaoScheme = null;
            Dto zhuiHaoDto = null;
            for(int i = 0; i < zhuiHaoList.size(); i ++)
            {
                zhuiHaoScheme = zhuiHaoList.get(i);
                zhuiHaoDto = new BaseDto();
                zhuiHaoDto.put("pid",zhuiHaoScheme.getPeriod());//设置期次号
                zhuiHaoDto.put("pname",zhuiHaoScheme.getPeriod() + "期");//设置期次名称
                zhuiHaoDto.put("smultiple",zhuiHaoScheme.getSchemeMultiple());//设置倍数
                zhuiHaoDto.put("money",zhuiHaoScheme.getSchemeMoney());//设置金额
                zhuiHaoDto.put("jddesc",type == 1? ("") : (("已追" + (zhuiHaoList.size() - i)) + "/" + scheme.getPeriodSum() + "期"));//设置追期进度描述
                zhuiHaoDto.put("kstatus",zhuiHaoScheme.getOpenStatus() == 0? 0 : 1);//设置开奖状态,0-未开奖,1-已开奖
                zhuiHaoDto.put("uitype",1);//设置开奖号显示形式

                //撤单方案
                if(zhuiHaoScheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CDCG)
                {
                    if(type == 1)
                    {
                        zhuiHaoDto.put("kcode","已撤单");
                    }
                    else
                    {
                        if(StringUtil.isEmpty(zhuiHaoScheme.getDrawNumber()))
                        {
                            zhuiHaoDto.put("kcode","--");
                        }
                        else
                        {
                            zhuiHaoDto.put("kcode",zhuiHaoScheme.getDrawNumber());
                        }
                    }
                    zhuiHaoDto.put("kdesc","--");
                    zhuiHaoDto.put("pdesc","已撤单");
                    zhuiHaoDto.put("zdesc","已撤单");//设置中奖状态描述为未中奖
                }
                //非撤单方案
                else
                {
                    //设置开奖号和开奖号的显示模式
                    if(type == 1)
                    {
                        zhuiHaoDto.put("kcode","待追号");
                        zhuiHaoDto.put("kdesc","待追号");
                        zhuiHaoDto.put("pdesc","待追号");
                        zhuiHaoDto.put("zdesc","待追号");
                    }
                    else
                    {
                        if(StringUtil.isEmpty(zhuiHaoScheme.getDrawNumber()))
                        {
                            zhuiHaoDto.put("kcode","--");
                        }
                        else
                        {
                            zhuiHaoDto.put("kcode",zhuiHaoScheme.getDrawNumber());
                        }
                        /**
                         * 设置派奖状态描述
                         */
                        //未开奖,慢频设置开奖描述为预计开奖时间
                        if(zhuiHaoScheme.getOpenStatus() == 0)
                        {
                            if(LotteryUtils.isMp(scheme.getLotteryId()))
                            {
                                //period = periodMapper.queryPeriodByPerod(scheme.getLotteryId(),zhuiHaoScheme.getPeriod());//查询期次信息
                                //zhuiHaoDto.put("pdesc","预计" + DateUtil.formatDate(period.getDrawNumberTime(),DateUtil.DEFAULT_DATE_TIME_SECOND) + "开奖");
                                zhuiHaoDto.put("pdesc","等待开奖");
                            }
                            else
                            {
                                zhuiHaoDto.put("pdesc","等待开奖");
                            }
                        }
                        //已中奖
                        else if(zhuiHaoScheme.getOpenStatus() == 2)
                        {
                            //根据派奖状态显示开奖描述
                            if(zhuiHaoScheme.getPrizeStatus() == 2)
                            {
                                zhuiHaoDto.put("pdesc","已派奖");
                            }
                            else
                            {
                                zhuiHaoDto.put("pdesc","等待派奖");
                            }
                        }
                        else
                        {
                            zhuiHaoDto.put("pdesc","");
                        }
                        /**
                         * 设置中奖状态/中奖状态描述
                         */
                        zhuiHaoDto.put("zstatus",zhuiHaoScheme.getOpenStatus() == 2? 1 : 0);//设置中奖状态 0-未中奖 1-已中奖
                        if(zhuiHaoScheme.getOpenStatus() == 1)
                        {
                            zhuiHaoDto.put("zdesc","未中奖");//设置中奖状态描述为未中奖
                        }
                        else if (zhuiHaoScheme.getOpenStatus() == 2)
                        {
                            zhuiHaoDto.put("zdesc",(zhuiHaoScheme.getPrizeTax() + "元"));//设置中奖描述为中奖金额
                        }
                        else
                        {
                            zhuiHaoDto.put("zdesc","");//设置默认中奖状态描述为空
                        }
                    }
                }
                zhList.add(zhuiHaoDto);
            }
        }
        return zhList;
    }

    /**
     * 渠道查询订单出票状态
     * @param params
     * @return
     */
    public List<Dto> queryChannelSchemeStatus(Dto params) throws ServiceException, Exception {
        try {
            if(StringUtil.isEmpty(params) || StringUtil.isEmpty(params.get("appId")) || StringUtil.isEmpty(params.get("orderId"))) {
                return null;
            }
            List<Dto> schemes = schemeMapper.queryChannelSchemeList(params);
            if(StringUtil.isNotEmpty(schemes)) {
                for(Dto d : schemes) {
                    Integer status = d.getAsInteger("schemeStatus");
                    if(status == 1 || status == 6 || status == 7) {
                        status = 2;
                    }
                    if(status == 4) {
                        status = -1;
                    }
                    d.put("schemeStatus", status);
                }
            }
            return schemes;
        } catch (Exception e) {
            logger.error("[渠道查询订单出票状态] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 渠道查询订单中奖状态
     * @param params
     * @return
     */
    public List<Dto> queryChannelSchemeAwardStatus(Dto params) throws ServiceException, Exception {
        try {
            if(StringUtil.isEmpty(params) || StringUtil.isEmpty(params.get("appId")) || StringUtil.isEmpty(params.get("orderId"))) {
                return null;
            }
            return schemeMapper.queryChannelAwardSchemeList(params);
        } catch (Exception e) {
            logger.error("[渠道查询订单中奖状态异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询需要进行出票通知的渠道订单
     * @param appId
     * @return
     */
    public List<Dto> queryChannelSchemeToNotify(String appId) throws ServiceException, Exception {
        try {
            return schemeMapper.queryChannelSchemeToNotify(appId);
        } catch (Exception e) {
            logger.error("[查询需要进行出票通知的渠道订单异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新渠道订单出票通知次数
     * @param id
     * @param number
     * @return
     * @throws Exception
     */
    public void updateSchemeNotifyNumber(Long id, Integer number) {
        try {
            schemeMapper.updateSchemeNotifyNumber(id, number);
        } catch (Exception e) {
            logger.error("[更新渠道订单出票通知次数异常] errorDesc=" + e.getMessage());
        }
    }

    /**
     * 查询订单相关的场次数据
     * @return
     */
    public List<SchemeMatches> querySchemeInfoByMatches(String lotteryId, Long schemeId) throws ServiceException {
        try {
            if(lotteryId.equals(LotteryConstants.JCZQ)) {
                return schemeMapper.querySchemeInfoByZqMatches(schemeId);
            }
            if(lotteryId.equals(LotteryConstants.JCLQ)) {
                return schemeMapper.querySchemeInfoByLqMatches(schemeId);
            }
            return null;
        } catch (Exception e) {
            logger.error("[查询竞彩足球订单相关的场次数据异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 竞彩奖金优化计算方法
     * @param schemeBean
     * @param result
     * @return
     * @throws ServiceException
     */
    public void jcJJYHCalculate(SchemeBean schemeBean, ResultBean result) throws ServiceException, Exception {
        if (!LotteryUtils.isJc(schemeBean.getLid())) {
            logger.error("[竞彩奖金优化] 只能针对竞彩进行奖金优化!彩种编号=" + schemeBean.getLid());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120017);
        }
        if (StringUtil.isEmpty(schemeBean.getTzcontent()) || StringUtil.isEmpty(schemeBean.getMoney())) {
            logger.error("[竞彩奖金优化] 投注内容不正确!彩种编号=" + schemeBean.getLid());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120002);
        }
        if (schemeBean.getMoney() < 4 || schemeBean.getMoney() % 2 != 0) {
            logger.error("[竞彩奖金优化] 投注金额不正确!彩种编号=" + schemeBean.getLid());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120021);
        }

        //判断彩种插件是否支持
        String playTypeId = schemeBean.getLid();//玩法类型id,默认与彩种id一样
        playTypeId = LotteryConstants.jcWfPrefixPlayIdMaps.get(schemeBean.getLid() + schemeBean.getTzcontent().split("\\|")[0]);
        GamePluginAdapter plugin = InitPlugin.getPlugin(pluginMaps, playTypeId);//初始化彩种插件
        if (plugin == null) {
            logger.error("[竞彩奖金优化] 找不到相关彩种插件!彩种编号=" + schemeBean.getLid());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120001);
        }

        //验证投注内容
        GameCastCode castCode = plugin.parseGameCastCode(schemeBean.getTzcontent());
        String[] schemeContent = PluginUtil.splitter(schemeBean.getTzcontent(), "|");
        String[] ps = PluginUtil.splitter(schemeContent[2], ",");
        for (String p : ps) {
            if (!p.endsWith("*1")) {
                logger.error("[竞彩奖金优化] 多串过关不支持奖金优化!");
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120018);
            }
            if (StringUtil.parseInt(p.substring(0, 1)) > 6) {
                logger.error("[竞彩奖金优化] 奖金优化最多支持6场比赛!");
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120019);
            }
        }

        checkJcMatchSellStatus(schemeBean);//比赛数据
        if (schemeBean.getMatchList().size() > 6) {
            logger.error("[竞彩奖金优化] 奖金优化最多支持6场比赛!");
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120019);
        }

        getMatchSp(schemeBean);//赔率数据

        int minMoney = 0;//最低优化金额
        ArrayList<JjyhTwo> jjyhTwoList = new ArrayList<>();
        List<Object> list = castCode.getCast();
        for(Object obj : list) {
            String content = "";
            if(schemeBean.getLid().equals(LotteryConstants.JCZQ)) {
                JcCastCode zqCast = (JcCastCode) obj;
                content = zqCast.toBillCode();
                minMoney += zqCast.getBettingnum();
            } else {
                LqCastCode lqCast = (LqCastCode) obj;
                content = lqCast.toBillCode();
                minMoney += lqCast.getBettingnum();
            }
            if(StringUtil.isEmpty(content)) {
                continue;
            }
            String [] contents = content.split(";");
            for (int i = 0; i < contents.length; i++) {
                String[] codes = PluginUtil.splitter(contents[i], "|");
                String[] code = PluginUtil.splitter(codes[1], ",");
                List<String[]> listCode = new ArrayList<>();
                for (int j = 0; j < code.length; j++) {
                    String wf = code[j].substring(0, code[j].indexOf("="));
                    String[] cTemp = code[j].split("=")[1].split("/");
                    for (int k = 0; k < cTemp.length; k++) {
                        cTemp[k] = wf + "=" + cTemp[k];
                    }
                    listCode.add(cTemp);
                }

                final List<String> split = new ArrayList<String>();
                new CombineSplit<String>(listCode) {
                    public void sequence(List<String> list) {
                        String temp = "";
                        for(int t = 0; t < list.size(); t++) {
                            temp += list.get(t);
                            if(t != list.size() - 1) {
                                temp += "|";
                            }
                        }
                        split.add(temp);
                    }
                };

                for(int l = 0; l < split.size();l++) {
                    JjyhTwo two = new JjyhTwo();
                    String [] a = PluginUtil.splitter(split.get(l), "|");
                    double sumSp = 1.0;
                    List<MatchInfo> matchInfos = new ArrayList<>();
                    for(int m = 0; m < a.length; m++) {
                        String key = "", val = "", playType = codes[0];
                        if(codes[0].equals(LotteryConstants.JCWF_PREFIX_HH)) {
                            key = PluginUtil.splitter(a[m], ">")[0];
                            val = PluginUtil.splitter(a[m], "=")[1];
                            playType = PluginUtil.splitter(PluginUtil.splitter(a[m], ">")[1], "=")[0];
                        } else {
                            key = PluginUtil.splitter(a[m], "=")[0];
                            val = PluginUtil.splitter(a[m], "=")[1];
                        }
                        String playChoose = playType + val;
                        MatchInfo matchInfo = new MatchInfo();
                        MatchInfo tempInfo = schemeBean.getMatchInfoList().get(key);
                        double sp = tempInfo.getMatchSp().getAsDoubleValue(playChoose);
                        sumSp = sumSp * sp;
                        BeanUtils.copyProperties(matchInfo, tempInfo);
                        matchInfo.setSp(CalculationUtils.rd(sp));
                        matchInfo.setChoose(val);
                        matchInfo.setChooseDesc(LotteryConstants.playTypeName.get(playChoose));
                        matchInfo.setFilterFlag(playType + "=" + val);
                        matchInfos.add(matchInfo);
                    }
                    two.setContent(codes[0] + "|" + split.get(l).replaceAll("\\|", ",") + "|" + codes[2]);
                    two.setJssp(CalculationUtils.spValue(sumSp*2));
                    two.setJsprize(two.getJssp());
                    two.setMatchInfos(matchInfos);
                    jjyhTwoList.add(two);
                }
            }
        }

        if(minMoney < 2) {
            logger.error("[竞彩奖金优化] 优化方案最小支持2注!彩种编号=" + schemeBean.getLid());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120024);
        }
        if(minMoney > 500) {
            logger.error("[竞彩奖金优化] 优化方案最大支持500注!彩种编号=" + schemeBean.getLid());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120023);
        }
        minMoney = minMoney * 4;
        if(schemeBean.getMoney() < minMoney) {
            logger.error("[竞彩奖金优化] 未达到优化最低起投金额" + minMoney + "元!彩种编号=" + schemeBean.getLid());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120022,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120022_MSG, new Object[]{minMoney}));
        }

        Collections.sort(jjyhTwoList);
        JjyhSchemeInfo schemeInfo = new JjyhSchemeInfo();
        schemeInfo.setLotteryId(schemeBean.getLid());
        schemeInfo.setPassType(schemeContent[2].replace("1*1", "单关").replace("*", "串"));
        schemeInfo.setMoney(schemeBean.getMoney() + "");
        schemeInfo.setDesc("已选" + schemeBean.getMatchList().size() + "场比赛");
        schemeInfo.setSchemeContent(schemeBean.getTzcontent());

        Map<String, List<String>> filter = getFilter(schemeBean);//计算最高奖金前选项过滤优化
        //循环三种优化方式
        int maxMul = 0;
        for(int otype = 1; otype < 4; otype++) {
            List<JjyhTwo> resultJjyh = getOptimizeBs(otype, schemeBean.getMoney().intValue(), StringUtil.deepCopy(jjyhTwoList));
            JjyhTwo two = resultJjyh.get(resultJjyh.size()-1);
            resultJjyh.remove(resultJjyh.size()-1);//移除最后一条
            if(two.getIndex() != 1 && otype == 3) {//博热优化最高奖金特殊计算
                filter = getFilterSame(schemeBean, resultJjyh.get(0).getMatchInfos(), filter);
            }
            int index = 0;
            double maxPrize = 0.0, minPrize = 0.0, maxPrizeTemp = 0.0;
            for(int jj = 0; jj < resultJjyh.size(); jj++) {
                List<MatchInfo> matchInfoList = resultJjyh.get(jj).getMatchInfos();
                boolean isAddPrize = true;//是否累加该组合奖金
                for(MatchInfo match : matchInfoList) {
                    List<String> filterStr = filter.get(match.getMatchCode());
                    if(filterStr.contains(match.getFilterFlag())) {
                        isAddPrize = false;
                        break;
                    }
                }
                //是否累加理论奖金
                if(isAddPrize) {
                    maxPrize += resultJjyh.get(jj).getJsprize();
                    resultJjyh.get(jj).setCalBonuse(true);//给前端标记
                }
                //最小金额
                if(minPrize == 0 || minPrize > resultJjyh.get(jj).getJsprize()) {
                    minPrize = resultJjyh.get(jj).getJsprize();
                }
                //最大金额
                if(maxPrizeTemp == 0 || maxPrizeTemp < resultJjyh.get(jj).getJsprize()) {
                    maxPrizeTemp = resultJjyh.get(jj).getJsprize();
                    index = jj;
                }
                resultJjyh.get(jj).setSp(CalculationUtils.rd(resultJjyh.get(jj).getJssp()));
                resultJjyh.get(jj).setPrize(CalculationUtils.rd(resultJjyh.get(jj).getJsprize()));
                if(otype == 1) {
                    maxMul += resultJjyh.get(jj).getMul();
                }
            }

            schemeInfo.setMoney((maxMul*2) + "");
            if(maxPrize < maxPrizeTemp) {
                maxPrize = maxPrizeTemp;
                for(int xx = 0; xx < resultJjyh.size(); xx++) {
                    if(xx == index) {
                        resultJjyh.get(xx).setCalBonuse(true);
                    } else {
                        resultJjyh.get(xx).setCalBonuse(false);
                    }
                }
            }
            JjyhOne one = new JjyhOne();
            one.setMinPrize(CalculationUtils.spValue(minPrize) + "");
            one.setMaxPrize(CalculationUtils.spValue(maxPrize) + "");
            one.setTwoList(resultJjyh);

            //平均优化
            if(otype == 1) {
                one.setDesc("各单注奖金比较平均,避免中奖后不盈利");
                schemeInfo.setAvgYh(one);
            }
            //搏冷优化
            else if(otype == 2) {
                one.setDesc("中奖回报最高的单注奖金最大化,其余趋于保本");
                schemeInfo.setColdYh(one);
            }
            //博热优化
            else if(otype == 3) {
                one.setDesc("中奖概率最高的单注奖金最大化,其余趋于保本");
                schemeInfo.setHotYh(one);
            }
            else {
                //无
            }
        }
        result.setData(schemeInfo);
    }

    /**
     * 计算奖金优化倍数
     * @param type
     * @param money
     * @param list
     * @return
     * @throws Exception
     */
    private List<JjyhTwo> getOptimizeBs(int type, int money, List<JjyhTwo> list) {
        List<JjyhTwo> jjyhList = new ArrayList<>();
        try {
            double[] rate = new double[list.size()];
            double sum = 0;
            //权重计算方式
            int sumbs = money / 2;
            for (int k = 0; k < list.size(); k++) {
                rate[k] = 1 / list.get(k).getJssp();
                sum += 1 / list.get(k).getJssp();
            }

            //计算权重对应倍数
            double t = sumbs / sum;
            int bsSum = 0;
            int[] bs = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                int bi = (int) (t * rate[i]);
                if (bi == 0) {
                    bi += 1;
                }
                bs[i] = bi;
                bsSum += bi;
            }

            //将倍数和奖金算入对象中
            List<JjyhTwo> tempList = new ArrayList<>();
            for (int w = 0; w < list.size(); w++) {
                JjyhTwo jjyhTwo = new JjyhTwo();
                jjyhTwo.setIndex(w);
                jjyhTwo.setJsprize(bs[w] / rate[w]);
                tempList.add(jjyhTwo);
            }
            Collections.sort(tempList);//按奖金升序
            //将剩余倍数分配到对应索引中
            for (int x = 0; x < sumbs - bsSum; x++) {
                bs[tempList.get(x).getIndex()] += 1;
            }
            tempList.clear();
            //再次将最新倍数和奖金算入对象中
            for (int y = 0; y < list.size(); y++) {
                list.get(y).setMul(bs[y]);
                list.get(y).setJsprize(CalculationUtils.spValue(bs[y] / rate[y]));
            }
            //如果不能保本 则不进行搏冷和博热优化
            for (int i = 0; i < list.size(); i++) {
                if (money > list.get(i).getJsprize()) {
                    type = 1;
                    break;
                }
            }
            //博冷优化
            if (type == 2) {
                int tempSum = 0;
                bs = new int[list.size()];
                for (int c = 0; c < list.size() - 1; c++) {
                    int bi = (int) Math.ceil(money * rate[c]);
                    bs[c] = bi;
                    tempSum += bi;
                }
                bs[list.size() - 1] = sumbs - tempSum;
            }
            //搏热优化
            if (type == 3) {
                int tempSum = 0;
                bs = new int[list.size()];
                for (int b = list.size() - 1; b > 0; b--) {
                    int bi = (int) Math.ceil(money * rate[b]);
                    bs[b] = bi;
                    tempSum += bi;
                }
                bs[0] = sumbs - tempSum;
            }
            //搏冷和博热优化后 将最新倍数和奖金算入对象中
            if (type != 1) {
                for (int y = 0; y < list.size(); y++) {
                    list.get(y).setMul(bs[y]);
                    list.get(y).setJsprize(CalculationUtils.spValue(bs[y] / rate[y]));
                }
            }
        } catch (Exception e) {
            for(int i = 0; i < list.size(); i++){
                int bs = money / 2 / list.size();
                if(i == list.size() - 1){
                    bs = bs + ((money / 2) % list.size());
                }
                list.get(i).setMul(bs);
                list.get(i).setJsprize(CalculationUtils.spValue(bs*list.get(i).getJssp()));
            }
        }
        for(JjyhTwo jjyh : list) {
            jjyhList.add(jjyh);
        }
        JjyhTwo twoType = new JjyhTwo();
        twoType.setIndex(type);//返回当前优化类型
        jjyhList.add(twoType);
        return jjyhList;
    }

    /**
     * 设置场次赔率数据
     * @param bean
     */
    private void getMatchSp(SchemeBean bean) {
        if(StringUtil.isEmpty(bean) || StringUtil.isEmpty(bean.getMatchInfoList())) {
            return;
        }
        for(Map.Entry<String, MatchInfo> entry : bean.getMatchInfoList().entrySet()) {
            Dto spDto = null;
            MatchInfo matchInfo = entry.getValue();
            //查询竞彩足球赔率
            if (LotteryUtils.isJczq(bean.getLid())) {
                spDto = (Dto) memcached.get(LotteryConstants.jczqSpPrefix + matchInfo.getMatchCode());//从缓存中获取赔率信息
                if (spDto == null) {
                    spDto = footBallSpMapper.queryFootBallSp(matchInfo.getMatchCode());//从数据库中查询赔率信息
                    memcached.set(LotteryConstants.jczqSpPrefix + matchInfo.getMatchCode(), spDto, 60 * 5);//在缓存中保留5分钟
                }
            }
            //查询竞彩篮球赔率
            else if (LotteryUtils.isJclq(bean.getLid())) {
                spDto = (Dto) memcached.get(LotteryConstants.jclqSpPrefix + matchInfo.getMatchCode());//从缓存中获取赔率信息
                if (spDto == null) {
                    spDto = basketBallSpMapper.queryBasketBallSp(matchInfo.getMatchCode());//从数据库中查询赔率信息
                    memcached.set(LotteryConstants.jclqSpPrefix + matchInfo.getMatchCode(), spDto, 60 * 5);//在缓存中保留5分钟
                }
            }
            matchInfo.setMatchSp(spDto);
        }
    }

    /**
     * 计算最高奖金前选项过滤优化
     * @param schemeBean
     */
    private Map<String, List<String>> getFilter(SchemeBean schemeBean) throws Exception {
        String[] schemeContent = PluginUtil.splitter(schemeBean.getTzcontent(), "|");
        Map<String, List<String>> filter = new HashMap<>();
        String[] cds = schemeContent[1].split(",");
        for (String cs : cds) {
            List<String> listSps = new ArrayList<>();
            getListSps(schemeContent, cs, schemeBean, listSps);
            String matchCode = listSps.get(listSps.size()-1);
            listSps.remove(listSps.size()-1);
            filter.put(matchCode, FilterPlayTypeUtil.getFilterPlay(listSps.toArray(new String[0])));
        }
        return filter;
    }

    /**
     * 博热优化最高奖金特殊计算
     * @param schemeBean
     * @param matchInfo
     * @param filterOne
     * @return
     * @throws Exception
     */
    private Map<String, List<String>> getFilterSame(SchemeBean schemeBean, List<MatchInfo> matchInfo, Map<String, List<String>> filterOne) throws Exception {
        String[] schemeContent = PluginUtil.splitter(schemeBean.getTzcontent(), "|");
        Map<String, List<String>> filter = new HashMap<>();
        String[] cds = schemeContent[1].split(",");
        for (String cs : cds) {
            List<String> listSps = new ArrayList<>();
            getListSps(schemeContent, cs, schemeBean, listSps);
            String matchCode = listSps.get(listSps.size()-1);
            listSps.remove(listSps.size()-1);
            String temp = "";
            boolean isTrue = false;//标记过滤选项中是否包含该场比赛
            for(MatchInfo info : matchInfo) {
                if(!matchCode.equals(info.getMatchCode())) {
                    continue;
                }
                temp = info.getFilterFlag() + "|" + info.getSp();
                if (info.getFilterFlag().startsWith(LotteryConstants.JCWF_PREFIX_RQSPF) || info.getFilterFlag().startsWith(LotteryConstants.JCWF_PREFIX_RFSF)) {
                    temp += "|" + info.getRqf();
                }
                if(listSps.contains(temp)) {
                    listSps.remove(temp);
                }
                isTrue = true;
            }
            if(isTrue) {//包含
                filter.put(matchCode, FilterPlayTypeUtil.getFilterPlaySame(listSps.toArray(new String[0]), temp));
            } else {//不包含则从原始过滤中获取
                filter.put(matchCode, filterOne.get(matchCode));
            }
        }
        return filter;
    }

    /**
     * 格式化选项赔率
     * @param schemeContent
     * @param cs
     * @param schemeBean
     */
    private static void getListSps(String[] schemeContent, String cs, SchemeBean schemeBean, List<String> listSps) throws Exception {
        String matchCode = "";
        if (schemeContent[0].equals(LotteryConstants.JCWF_PREFIX_HH)) {
            String[] temp0 = cs.split("\\>");
            matchCode = temp0[0];
            MatchInfo infoHH = schemeBean.getMatchInfoList().get(matchCode);
            String[] xc = temp0[1].split("\\+");
            for (String x : xc) {
                String[] temp2 = x.split("\\=");
                String[] temp3 = temp2[1].split("\\/");
                for (String t : temp3) {
                    String lsps = temp2[0] + "=" + t + "|" + infoHH.getMatchSp().getAsDouble(temp2[0] + t);
                    if (temp2[0].equals(LotteryConstants.JCWF_PREFIX_RQSPF) || temp2[0].equals(LotteryConstants.JCWF_PREFIX_RFSF)) {
                        lsps += "|" + infoHH.getRqf();
                    }
                    listSps.add(lsps);
                }
            }
        } else {
            String[] temp1 = cs.split("\\=");
            matchCode = temp1[0];
            MatchInfo infoFH = schemeBean.getMatchInfoList().get(matchCode);
            String[] temp4 = temp1[1].split("\\/");
            for (String t : temp4) {
                String lsps = schemeContent[0] + "=" + t + "|" + infoFH.getMatchSp().getAsDouble(schemeContent[0] + t);
                if (schemeContent[0].equals(LotteryConstants.JCWF_PREFIX_RQSPF) || schemeContent[0].equals(LotteryConstants.JCWF_PREFIX_RFSF)) {
                    lsps += "|" + infoFH.getRqf();
                }
                listSps.add(lsps);
            }
        }
        listSps.add(matchCode);//最后一个为场次编号
    }

    /**
     * 根据ID-更新跟单方案关联信息
     * @param follow
     */
    public void updateSchemeFollow(SchemeFollow follow) throws Exception {
        try {
            schemeMapper.updateSchemeFollow(follow);
        } catch (Exception e) {
            logger.error("[更新跟单方案关联信息异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据发单人订单编号-更新跟单方案计奖状态
     * @param senderSchemeId
     */
    public void updateSchemeFollowBySendSchemeOrderId(String senderSchemeId) throws Exception {
        try {
            schemeMapper.updateSchemeFollowBySendSchemeOrderId(senderSchemeId);
        } catch (Exception e) {
            logger.error("[根据发单人订单编号-更新跟单方案关联信息异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询跟单关联列表
     * @return
     */
    public List<SchemeFollow> querySchemeFollowList(SchemeFollow follow) throws ServiceException {
        try {
            return schemeMapper.querySchemeFollowList(follow);
        } catch (Exception e) {
            logger.error("[查询跟单关联列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据跟单方案编号查询跟单关联记录
     * @param schemeId
     * @return
     */
    public SchemeFollow querySchemeFollowInfo(String schemeId) throws ServiceException {
        try {
            return schemeMapper.querySchemeFollowInfo(schemeId);
        } catch (Exception e) {
            logger.error("[根据跟单方案编号查询跟单关联记录异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 保存跟单方案关联信息
     * @return
     */
    public void saveSchemeFollow(SchemeFollow follow) throws ServiceException {
        try {
            schemeMapper.saveSchemeFollow(follow);
        } catch (Exception e) {
            logger.error("[保存跟单方案关联信息异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据比赛场次查询中奖神单收获打赏列表
     * @return
     */
    public List<SchemeFollow> querySchemeFollowListByRewards(String lotteryId, String matchCode) throws ServiceException {
        try {
            return schemeMapper.querySchemeFollowListByRewards(lotteryId, matchCode);
        } catch (Exception e) {
            logger.error("[根据比赛场次查询神单收获打赏列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新神单方案中奖金额和描述
     * @param scheme
     */
    public int updateSchemeFollowPrize(Scheme scheme) throws ServiceException {
        try {
            return schemeMapper.updateSchemeFollowPrize(scheme);
        } catch (Exception e) {
            logger.error("[更新神单方案中奖金额和描述异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 神单分享(晒单)
     * @author  mcdog
     * @param   params  业务参数
     * @param   result  处理结果对象
     */
    public synchronized void shareScheme(Dto params, ResultBean result) throws ServiceException,Exception
    {
        //校验方案id
        if(StringUtil.isEmpty(params.get("sid")))
        {
            logger.error("[神单分享(晒单)]方案id不能为空!接收参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //校验抽佣比例
        if(StringUtil.isEmpty(params.get("remuneration")))
        {
            logger.error("[神单分享(晒单)]抽佣比例不能为空!接收参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        int remuneration = params.getAsInteger("remuneration");
        if(remuneration < 1 || remuneration > 10)
        {
            logger.error("[神单分享(晒单)]抽佣比例取值错误!接收参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //校验投注内容隐藏模式
        if(StringUtil.isEmpty(params.get("hideType")))
        {
            logger.error("[神单分享(晒单)]投注内容隐藏模式不能为空!接收参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        int hideType = params.getAsInteger("hideType");
        if(hideType != 0 && hideType != 1 && hideType != 2)
        {
            logger.error("[神单分享(晒单)]投注内容隐藏模式取值错误!接收参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //校验方案是否存在
        List<Scheme> schemeList = schemeMapper.querySchemeInfo(params);
        if(schemeList == null && schemeList.size() == 0)
        {
            logger.error("[神单分享(晒单)]查询不到相关的方案信息!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //校验方案是否已出票成功
        Scheme scheme = schemeList.get(0);
        if(scheme.getSchemeStatus() != SchemeConstants.SCHEME_STATUS_CPCG)
        {
            logger.error("[神单分享(晒单)]方案尚未出票成功!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //校验方案所属彩种类型
        if(!LotteryUtils.isJc(scheme.getLotteryId()))
        {
            logger.error("[神单分享(晒单)]非竞彩方案不能分享!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //校验方案类型
        if(scheme.getSchemeType() != SchemeConstants.SCHEME_TYPE_PT
                && scheme.getSchemeType() != SchemeConstants.SCHEME_TYPE_YH)
        {
            logger.error("[神单分享(晒单)]非普通/优化方案不能晒单!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 校验神单是否可以分享
         */
        //查询用户当天已发神单数,已发神单数<系统设置的用户每天发起神单的最大个数时,该方案才允许分享神单
        Dto queryDto = new BaseDto("userId",params.get("userId"));
        queryDto.put("schemeType",SchemeConstants.SCHEME_TYPE_SD);
        queryDto.put("createDate",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE));
        int count = schemeMapper.queryUserSdCountOfDay(queryDto);
        String maxSdcountOfday = SysConfig.getString("MAX_SDCOUNT_OFDAY");//从系统中获取用户每天发起神单的最大个数
        int maxsdcount = StringUtil.isEmpty(maxSdcountOfday)? 3 : Integer.parseInt(maxSdcountOfday);//默认为3个
        if(count >= maxsdcount)
        {
            logger.error("[神单分享(晒单)]用户本日晒单已经达到" + count + "个!本日无法继续晒单!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120026,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120026_MSG,new Object[]{maxsdcount}));
        }
        else
        {
            //判断该方案是否可以进行神单分享
            if(!scheme.getShare())
            {
                logger.error("[神单分享(晒单)]方案不具备晒单条件!用户编号=" + params.getAsString("userId") + ",方案id=" + scheme.getId());
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120027,ErrorCode_API.ERROR_SCHEM_120027_MSG);
            }
            else
            {
                //判断该方案是否已过晒单截止时间
                if(scheme.getShareEndTime().before(new Date()))
                {
                    logger.error("[神单分享(晒单)]方案晒单时间已截止!用户编号=" + params.getAsString("userId") + ",方案id=" + scheme.getId());
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120028,ErrorCode_API.ERROR_SCHEM_120028_MSG);
                }
            }
        }

        /**
         * 更改方案类型,将方案类型变更为神单
         */
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//获取用户信息
        Dto updateDto = new BaseDto("sid",params.get("sid"));//设置方案id
        updateDto.put("userId",params.get("userId"));//设置方案所属用户编号
        updateDto.put("userNickName",user.getNickName());//设置发单人昵称
        updateDto.put("remuneration",params.get("remuneration"));//设置抽佣比例
        updateDto.put("minParticipant",scheme.getSchemeMoney() / scheme.getSchemeMultiple());//设置最小跟投金额
        updateDto.put("safeguardMoney",scheme.getSchemeMoney() * 10);//设置跟单总额(方案金额的10倍)
        updateDto.put("hideType",hideType);//设置投注内容隐藏模式(0-不隐藏 1-隐藏投注选项 2-隐藏对阵和投注选项)
        schemeMapper.updateSchemeForSd(updateDto);//更新订单为神单

        /**
         * 设置业务结果
         */
        result.setErrorCode(ErrorCode_API.SUCCESS);
        logger.info("[神单分享(晒单)]晒单成功!用户编号=" + params.getAsString("userId") + ",方案id=" + params.getAsString("sid"));
    }

    /**
     * 查询某场比赛已结算的神单用户编号
     * @param lotteryId
     * @param matchCode
     * @return
     * @throws ServiceException
     */
    public List<Long> queryFollowUserIdForMatch(String lotteryId, String matchCode) throws ServiceException {
        try {
            return userFollowMapper.queryFollowUserIdForMatch(lotteryId, matchCode);
        } catch (Exception e) {
            logger.error("[查询某场比赛相关已结算的神单用户编号列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询某个用户最近一周的神单数据
     * @param lotteryId
     * @param userId
     * @return
     * @throws ServiceException
     */
    public List<Dto> queryUserFollowForWeek(String lotteryId, Long userId) throws ServiceException {
        try {
            return userFollowMapper.queryUserFollowForWeek(lotteryId, userId);
        } catch (Exception e) {
            logger.error("[查询某个用户最近一周的神单数据列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 统计某个用户最近一月的神单数据
     * @param lotteryId
     * @param userId
     * @return
     * @throws ServiceException
     */
    public Dto queryUserFollowStatisForMonth(String lotteryId, Long userId) throws ServiceException {
        try {
            return userFollowMapper.queryUserFollowStatisForMonth(lotteryId, userId);
        } catch (Exception e) {
            logger.error("[统计某个用户最近一月的神单数据异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询某个用户最近10个已结算的神单数据
     * @param lotteryId
     * @param userId
     * @return
     * @throws ServiceException
     */
    public List<Long> queryUserNearTenFollowScheme(String lotteryId, Long userId) throws ServiceException {
        try {
            return userFollowMapper.queryUserNearTenFollowScheme(lotteryId, userId);
        } catch (Exception e) {
            logger.error("[查询某个用户最近10个已结算的神单数据异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询有周榜数据但已经一周没有发神单的用户列表
     * @param lotteryId
     * @return
     * @throws ServiceException
     */
    public List<Long> queryWeekNoFollowUserList(String lotteryId) throws ServiceException {
        try {
            return userFollowMapper.queryWeekNoFollowUserList(lotteryId);
        } catch (Exception e) {
            logger.error("[查询有周榜数据但已经一周没有发神单的用户列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询有月榜数据但已经一月没有发神单的用户列表
     * @param lotteryId
     * @return
     * @throws ServiceException
     */
    public List<Long> queryMonthNoFollowUserList(String lotteryId) throws ServiceException {
        try {
            return userFollowMapper.queryMonthNoFollowUserList(lotteryId);
        } catch (Exception e) {
            logger.error("[查询有月榜数据但已经一月没有发神单的用户列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新用户神单统计信息
     * @param dto
     * @return
     * @throws ServiceException
     */
    public int updateUserFollowStatisInfo(Dto dto) throws ServiceException {
        try {
            return userFollowMapper.updateUserFollowStatisInfo(dto);
        } catch (Exception e) {
            logger.error("[更新用户神单统计数据异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 统计用户获取神单打赏总金额
     * @param lotteryId
     * @param userId
     * @return
     * @throws ServiceException
     */
    public Dto queryUserFollowRewardMoneyStatis(String lotteryId, Long userId) throws ServiceException {
        try {
            return userFollowMapper.queryUserFollowRewardMoneyStatis(lotteryId, userId);
        } catch (Exception e) {
            logger.error("[统计用户获取神单打赏总金额异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询神单方案(晒单达人)
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getSdSchemes(Dto params, ResultBean result) throws ServiceException
    {
        /**
         * 设置查询参数
         */
        Calendar calendar = Calendar.getInstance();
        params.put("minEndTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置日期查询条件,只查询未截止的神单方案
        params.put("completeFlag",1);//只查询跟单金额尚未满额的神单
        settingPageParams(params);//设置分页查询参数

        //设置排序参数
        String psorts = params.getAsString("psorts");//提取排序方式(1-金额最高 2-跟买最多 3-近期战绩 4-回报最高 5-截止时间,默认取1)
        if("2".equals(psorts))
        {
            params.put("psorts","redSafeHuardMoney desc");
        }
        else if("3".equals(psorts))
        {
            params.put("psorts","weekHitRate desc");
        }
        else if("4".equals(psorts))
        {
            params.put("psorts","profitMargin desc");
        }
        else if("5".equals(psorts))
        {
            params.put("psorts","endTime asc");
        }
        else
        {
            params.put("psorts","schemeMoney desc");
        }

        /**
         * 查询并设置返回数据
         */
        //查询神单方案记录
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("list",new ArrayList<SchemeBean>());
        List<Dto> schemeList = schemeMapper.querySdSchemeInfo(params);
        if(schemeList != null && schemeList.size() > 0)
        {
            //拼装神单方案信息
            List<Dto> dataList = new ArrayList<Dto>();
            Dto dataDto = null;
            Calendar currentCalendar = Calendar.getInstance();
            for(Dto schemeDto : schemeList)
            {
                //拼装基本信息
                dataDto = new BaseDto("sid",schemeDto.get("id"));//设置方案id
                dataDto.put("lname",schemeDto.get("lotteryName"));//设置彩种名称
                dataDto.put("uid",schemeDto.get("schemeUserId"));//设置发单人编号
                dataDto.put("nickName",schemeDto.get("nickName"));//设置发单人昵称

                //设置发单人头像
                if(StringUtil.isEmpty(schemeDto.get("avatar")))
                {
                    dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                }
                else
                {
                    if(schemeDto.getAsString("avatar").startsWith("http"))
                    {
                        dataDto.put("avatar",schemeDto.getAsString("avatar"));
                    }
                    else
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + schemeDto.getAsString("avatar"));
                    }
                }
                dataDto.put("islq",LotteryUtils.isJczq(schemeDto.getAsString("lotteryId"))? 0 : 1);//设置是否为竞彩篮球(0-不是 1-是)
                dataDto.put("zgmoney",schemeDto.get("schemeMoney") + "元");//设置自购金额
                dataDto.put("yjyll",schemeDto.getAsString("profitMargin") + "%");//设置预计盈利率
                dataDto.put("jqzj",StringUtil.isEmpty(schemeDto.get("weekHitDescribe"))? "--" : schemeDto.get("weekHitDescribe"));//设置近期战绩(取近一周的命中情况描述)
                dataDto.put("qtmoney",schemeDto.getAsInteger("minParticipant") + "元");//设置起投金额
                dataDto.put("remuneration",(schemeDto.getAsString("remuneration") + "%"));//设置提成比例
                dataDto.put("ygmoney",schemeDto.getAsInteger("redSafeHuardMoney") + "元");//设置已跟投金额
                dataDto.put("symoney",schemeDto.getAsInteger("safeguardMoney") - schemeDto.getAsInteger("redSafeHuardMoney"));//设置剩余可跟金额

                //拼装截止时间
                calendar.setTime(DateUtil.parseDate(schemeDto.getAsString("endTime"),DateUtil.DEFAULT_DATE_TIME));
                dataDto.put("etime",getDateTimeDetail(calendar,currentCalendar));

                //拼装过关方式
                String[] tzspContent = schemeDto.getAsString("schemeContent").split("\\|");//提取带sp的投注内容
                dataDto.put("ggfs",tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," "));//设置过关方式

                //设置对阵信息/投注选项
                if("0".equals(schemeDto.getAsString("hideType"))
                    || (StringUtil.isNotEmpty(params.get("userId")) && params.getAsLong("userId").longValue() == schemeDto.getAsLong("schemeUserId").longValue()))
                {
                    schemeDto.put("hideType",0);//设置对阵选项隐藏模式为不隐藏
                }
                dataDto.put("tzxxs",getWjzSchemeTzxxList(schemeDto));
                dataList.add(dataDto);
            }
            dataMap.put("list",dataList);//设置方案记录信息
        }
        //如果有分页标识,则查询总记录条数及总页数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = schemeMapper.querySdSchemeInfoCount(params);
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置总条数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }
        //设置返回数据
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 查询神单命中榜
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getSdTjForMz(Dto params, ResultBean result) throws ServiceException
    {
        settingPageParams(params);//设置分页查询参数
        if("2".equals(params.getAsString("qtype")))
        {
            params.put("minMonthOrderSums",2);
            params.put("minMonthHitSums",1);
            params.put("psorts","monthHitRate desc,monthOrderSums desc");
        }
        else
        {
            params.put("minWeekOrderSums",2);
            params.put("minWeekHitSums",1);
            params.put("psorts","weekHitRate desc,weekOrderSums desc");
        }

        /**
         * 查询榜单数据
         */
        List<Dto> dataList = new ArrayList<Dto>();
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("list",new ArrayList<SchemeBean>());
        List<Dto> sdtjList = schemeMapper.querySdTj(params);
        if(sdtjList != null && sdtjList.size() > 0)
        {
            //根据查询类型qtype参数封装榜单数据(qtype,1-近一周榜单 2-近一个月榜单,默认取1)
            Dto dataDto = null;
            if("2".equals(params.getAsString("qtype")))
            {
                for(Dto sdtjDto : sdtjList)
                {
                    dataDto = new BaseDto();
                    dataDto.put("uid",sdtjDto.get("userId"));//设置用户编号
                    dataDto.put("nickName",sdtjDto.get("nickName"));//设置用户昵称

                    //设置用户头像
                    if(StringUtil.isEmpty(sdtjDto.get("avatar")))
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                    }
                    else
                    {
                        if(sdtjDto.getAsString("avatar").startsWith("http"))
                        {
                            dataDto.put("avatar",sdtjDto.getAsString("avatar"));
                        }
                        else
                        {
                            dataDto.put("avatar",SysConfig.getHostStatic() + sdtjDto.getAsString("avatar"));
                        }
                    }
                    dataDto.put("mzl",sdtjDto.get("monthHitRate") + "%");//设置命中率
                    dataDto.put("yll",sdtjDto.get("monthWinRate") + "%");//设置盈利率
                    dataDto.put("zjdesc",sdtjDto.get("monthHitDescribe"));//设置战绩描述
                    dataDto.put("tzcount",sdtjDto.get("monthOrderSums"));//设置神单发起数
                    dataDto.put("mzcount",sdtjDto.get("monthHitSums"));//设置神单命中数
                    dataDto.put("sdcount",StringUtil.isEmpty(sdtjDto.get("sdcount"))? 0 : sdtjDto.get("sdcount"));//当前可跟神单数
                    dataList.add(dataDto);
                }
            }
            else
            {
                for(Dto sdtjDto : sdtjList)
                {
                    dataDto = new BaseDto();
                    dataDto.put("uid",sdtjDto.get("userId"));//设置用户编号
                    dataDto.put("nickName",sdtjDto.get("nickName"));//设置用户昵称

                    //设置用户头像
                    if(StringUtil.isEmpty(sdtjDto.get("avatar")))
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                    }
                    else
                    {
                        if(sdtjDto.getAsString("avatar").startsWith("http"))
                        {
                            dataDto.put("avatar",sdtjDto.getAsString("avatar"));
                        }
                        else
                        {
                            dataDto.put("avatar",SysConfig.getHostStatic() + sdtjDto.getAsString("avatar"));
                        }
                    }
                    dataDto.put("mzl",sdtjDto.get("weekHitRate") + "%");//设置命中率
                    dataDto.put("yll",sdtjDto.get("weekWinRate") + "%");//设置盈利率
                    dataDto.put("zjdesc",sdtjDto.get("weekHitDescribe"));//设置战绩描述
                    dataDto.put("tzcount",sdtjDto.get("weekOrderSums"));//设置神单发起数
                    dataDto.put("mzcount",sdtjDto.get("weekHitSums"));//设置神单命中数
                    dataDto.put("sdcount",StringUtil.isEmpty(sdtjDto.get("sdcount"))? 0 : sdtjDto.get("sdcount"));//当前可跟神单数
                    dataList.add(dataDto);
                }
            }
            dataMap.put("list",dataList);//设置统计数据
        }
        //如果有分页标识,则查询总记录条数及总页数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = schemeMapper.querySdTjCount(params);
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置总条数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }

        /**
         * 设置返回数据
         */
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 查询神单盈利榜
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getSdTjForYl(Dto params, ResultBean result) throws ServiceException
    {
        settingPageParams(params);//设置分页查询参数
        if("2".equals(params.getAsString("qtype")))
        {
            params.put("minMonthWinRate",1);
            params.put("psorts","monthWinRate desc,monthOrderSums desc");
        }
        else
        {
            params.put("minWeekWinRate",1);
            params.put("psorts","weekWinRate desc,weekOrderSums desc");
        }

        /**
         * 查询榜单数据
         */
        List<Dto> dataList = new ArrayList<Dto>();
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("list",new ArrayList<SchemeBean>());
        List<Dto> sdtjList = schemeMapper.querySdTj(params);
        if(sdtjList != null && sdtjList.size() > 0)
        {
            //根据查询类型qtype参数封装榜单数据(qtype,1-近一周榜单 2-近一个月榜单,默认取1)
            Dto dataDto = null;
            if("2".equals(params.getAsString("qtype")))
            {
                for(Dto sdtjDto : sdtjList)
                {
                    dataDto = new BaseDto();
                    dataDto.put("uid",sdtjDto.get("userId"));//设置用户编号
                    dataDto.put("nickName",sdtjDto.get("nickName"));//设置用户昵称

                    //设置用户头像
                    if(StringUtil.isEmpty(sdtjDto.get("avatar")))
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                    }
                    else
                    {
                        if(sdtjDto.getAsString("avatar").startsWith("http"))
                        {
                            dataDto.put("avatar",sdtjDto.getAsString("avatar"));
                        }
                        else
                        {
                            dataDto.put("avatar",SysConfig.getHostStatic() + sdtjDto.getAsString("avatar"));
                        }
                    }
                    dataDto.put("yll",sdtjDto.get("monthWinRate"));//设置盈利率
                    dataDto.put("mzl",sdtjDto.get("monthHitRate") + "%");//设置命中率
                    dataDto.put("sdcount",StringUtil.isEmpty(sdtjDto.get("sdcount"))? 0 : sdtjDto.get("sdcount"));//当前可跟神单数
                    dataList.add(dataDto);
                }
            }
            else
            {
                for(Dto sdtjDto : sdtjList)
                {
                    dataDto = new BaseDto();
                    dataDto.put("uid",sdtjDto.get("userId"));//设置用户编号
                    dataDto.put("nickName",sdtjDto.get("nickName"));//设置用户昵称
                    //设置用户头像
                    if(StringUtil.isEmpty(sdtjDto.get("avatar")))
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                    }
                    else
                    {
                        if(sdtjDto.getAsString("avatar").startsWith("http"))
                        {
                            dataDto.put("avatar",sdtjDto.getAsString("avatar"));
                        }
                        else
                        {
                            dataDto.put("avatar",SysConfig.getHostStatic() + sdtjDto.getAsString("avatar"));
                        }
                    }
                    dataDto.put("yll",sdtjDto.get("weekWinRate"));//设置盈利率
                    dataDto.put("mzl",sdtjDto.get("weekHitRate") + "%");//设置命中率
                    dataDto.put("sdcount",StringUtil.isEmpty(sdtjDto.get("sdcount"))? 0 : sdtjDto.get("sdcount"));//当前可跟神单数
                    dataList.add(dataDto);
                }
            }
            dataMap.put("list",dataList);//设置统计数据
        }
        //如果有分页标识,则查询总记录条数及总页数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = schemeMapper.querySdTjCount(params);
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置总条数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }

        /**
         * 设置返回数据
         */
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 查询神单连红榜
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getSdTjForLh(Dto params, ResultBean result) throws ServiceException
    {
        settingPageParams(params);//设置分页查询参数
        if("2".equals(params.getAsString("qtype")))
        {
            params.put("minMonthRunRedSums",1);
            params.put("psorts","monthRunRedSums desc,monthOrderSums desc");
        }
        else
        {
            params.put("minWeekRunRedSums",1);
            params.put("psorts","weekRunRedSums desc,weekOrderSums desc");
        }

        /**
         * 查询榜单数据
         */
        List<Dto> dataList = new ArrayList<Dto>();
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("list",new ArrayList<SchemeBean>());
        List<Dto> sdtjList = schemeMapper.querySdTj(params);
        if(sdtjList != null && sdtjList.size() > 0)
        {
            //根据查询类型qtype参数封装榜单数据(qtype,1-近一周榜单 2-近一个月榜单,默认取1)
            Dto dataDto = null;
            if("2".equals(params.getAsString("qtype")))
            {
                for(Dto sdtjDto : sdtjList)
                {
                    dataDto = new BaseDto();
                    dataDto.put("uid",sdtjDto.get("userId"));//设置用户编号
                    dataDto.put("nickName",sdtjDto.get("nickName"));//设置用户昵称

                    //设置用户头像
                    if(StringUtil.isEmpty(sdtjDto.get("avatar")))
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                    }
                    else
                    {
                        if(sdtjDto.getAsString("avatar").startsWith("http"))
                        {
                            dataDto.put("avatar",sdtjDto.getAsString("avatar"));
                        }
                        else
                        {
                            dataDto.put("avatar",SysConfig.getHostStatic() + sdtjDto.getAsString("avatar"));
                        }
                    }
                    dataDto.put("mzl",sdtjDto.get("monthHitRate") + "%");//设置命中率
                    dataDto.put("yll",sdtjDto.get("monthWinRate") + "%");//设置盈利率
                    dataDto.put("lh",sdtjDto.get("monthRunRedSums"));//设置连红数
                    dataDto.put("sdcount",StringUtil.isEmpty(sdtjDto.get("sdcount"))? 0 : sdtjDto.get("sdcount"));//当前可跟神单数
                    dataList.add(dataDto);
                }
            }
            else
            {
                for(Dto sdtjDto : sdtjList)
                {
                    dataDto = new BaseDto();
                    dataDto.put("uid",sdtjDto.get("userId"));//设置用户编号
                    dataDto.put("nickName",sdtjDto.get("nickName"));//设置用户昵称

                    //设置用户头像
                    if(StringUtil.isEmpty(sdtjDto.get("avatar")))
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                    }
                    else
                    {
                        if(sdtjDto.getAsString("avatar").startsWith("http"))
                        {
                            dataDto.put("avatar",sdtjDto.getAsString("avatar"));
                        }
                        else
                        {
                            dataDto.put("avatar",SysConfig.getHostStatic() + sdtjDto.getAsString("avatar"));
                        }
                    }
                    dataDto.put("mzl",sdtjDto.get("weekHitRate") + "%");//设置命中率
                    dataDto.put("yll",sdtjDto.get("weekWinRate") + "%");//设置盈利率
                    dataDto.put("lh",sdtjDto.get("weekRunRedSums"));//设置连红数
                    dataDto.put("sdcount",StringUtil.isEmpty(sdtjDto.get("sdcount"))? 0 : sdtjDto.get("sdcount"));//当前可跟神单数
                    dataList.add(dataDto);
                }
            }
            dataMap.put("list",dataList);//设置统计数据
        }
        //如果有分页标识,则查询总记录条数及总页数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = schemeMapper.querySdTjCount(params);
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置总条数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }

        /**
         * 设置返回数据
         */
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 跟单
     * @author  mcdog
     * @param   schemeBean  方案业务处理对象
     * @param   result      处理结果对象
     */
    public synchronized void schemeFollow(SchemeBean schemeBean, ResultBean result) throws ServiceException,Exception
    {
        //校验系统是否开放
        String openStatus = SysConfig.getString("SYSTEM_OPEN_STATUS");
        if(StringUtil.isEmpty(openStatus) || "0".equals(openStatus))
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"系统升级维护中!暂停销售。");
        }
        //校验投注系统是否开放
        openStatus = SysConfig.getString("TRADE_OPEN_STATUS");
        if(StringUtil.isEmpty(openStatus) || "0".equals(openStatus))
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"系统升级维护中!暂停销售。");
        }
        //4秒内只允许投注一次
        if(redis.containsKey(schemeSavePrefix + schemeBean.getUserId()))
        {
            logger.error("[跟单]投注过于频繁!用户编号=" + schemeBean.getUserId());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120011,ErrorCode_API.ERROR_SCHEM_120011_MSG);
        }
        //在缓存中存放发起方案的标识
        redis.setNx((schemeSavePrefix + schemeBean.getUserId()),"1",3);//设置有效期为3秒

        /**
         * 校验用户
         */
        User user = null;
        try
        {
            user = userMapper.queryUserInfoById(schemeBean.getUserId());//查询用户
            if(user == null)
            {
                logger.error("[跟单]查询不到相关的用户!用户编号=" + schemeBean.getUserId());
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120000,ErrorCode_API.ERROR_SCHEM_XTSJWFYY_MSG);
            }
            else
            {
                //校验用户是否开通白名单
                if(user.getIsWhite() != 1)
                {
                    logger.error("[跟单]用户白名单校验未通过!用户编号=" + schemeBean.getUserId());
                    throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120000,ErrorCode_API.ERROR_SCHEM_XTSJWFYY_MSG);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("[跟单]查询用户白名单发生异常!用户编号=" + schemeBean.getUserId() + ",异常信息:" + e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
        //校验神单方案id/跟单倍数
        if(StringUtil.isEmpty(schemeBean.getSid()) || StringUtil.isEmpty(schemeBean.getSmultiple()))
        {
            logger.error("[跟单]神单方案id或跟单倍数不能为空!用户编号=" + schemeBean.getUserId()
                    + ",神单方案id=" + schemeBean.getSid() + ",跟单倍数=" + schemeBean.getSmultiple());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //校验神单是否存在
        Scheme sdscheme = schemeMapper.querySchemeInfoById(schemeBean.getSid());//查询神单
        if(sdscheme == null)
        {
            logger.error("[跟单]神单不存在!用户编号=" + schemeBean.getUserId() + ",神单方案id=" + schemeBean.getSid());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //自己发起的神单自己不能再跟单
        if(sdscheme.getSchemeUserId() == schemeBean.getUserId())
        {
            logger.error("[跟单]不能跟单自己发起的神单!用户编号=" + schemeBean.getUserId() + ",神单方案id=" + schemeBean.getSid());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"无法跟投自己发起的神单");
        }
        //判断神单发起人帐户类型,虚拟用户只允许跟单虚拟用户发起的神单
        if(user.getUserType() == UserConstants.USER_TYPE_VIRTUAL
                || user.getUserType() == UserConstants.USER_TYPE_OUTMONEY)
        {
            User sduser = userMapper.queryUserInfoById(sdscheme.getSchemeUserId());
            if(sduser.getUserType() != UserConstants.USER_TYPE_VIRTUAL
                    && sduser.getUserType() != UserConstants.USER_TYPE_OUTMONEY)
            {
                logger.error("[跟单]虚拟用户/出款账户不允许跟单!用户编号=" + schemeBean.getUserId());
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120000,ErrorCode_API.ERROR_SCHEM_XTSJWFYY_MSG);
            }
        }
        //校验神单是否满额
        if(sdscheme.getRedSafeHuardMoney() >= sdscheme.getSafeGuardMoney())
        {
            logger.error("[跟单]神单跟投金额已满额!无法继续跟单!用户编号=" + schemeBean.getUserId() + ",神单方案id=" + schemeBean.getSid());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120029,ErrorCode_API.ERROR_SCHEM_120029_MSG);
        }
        //校验剩余可跟投金额
        double sygtmoney = sdscheme.getSafeGuardMoney() - sdscheme.getRedSafeHuardMoney();//剩余可跟单金额
        double dbmoney = sdscheme.getSchemeMoney() / sdscheme.getSchemeMultiple();//提取方案单倍金额
        double gtmoney = schemeBean.getSmultiple() * dbmoney;//跟投金额
        if(gtmoney > sygtmoney)
        {
            logger.error("[跟单]跟单金额不能超过剩余可跟投金额!用户编号=" + schemeBean.getUserId() + ",神单方案id=" + schemeBean.getSid() + ",剩余可跟金额=" + sygtmoney + ",跟投金额=" + gtmoney);
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120030,
                    MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120030_MSG,new Object[]{sygtmoney}));
        }
        //校验彩种合法性/销售状态
        schemeBean.setLid(sdscheme.getLotteryId());
        Lottery lottery = (Lottery) memcached.get(LotteryConstants.lotteryPrefix + schemeBean.getLid());
        if(lottery == null)
        {
            lottery = lotteryMapper.queryLotteryInfo(schemeBean.getLid());
            memcached.set((LotteryConstants.lotteryPrefix + schemeBean.getLid()),lottery,5 * 60);//在缓存中保存5分钟
        }
        if(lottery == null)
        {
            logger.error("[跟单]查询不到相关彩种信息!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120001);
        }
        if(!checkLotterySellStatus(schemeBean,lottery))
        {
            logger.error("[跟单]彩种暂未对客户端开放!彩种编号=" + schemeBean.getLid() + ",用户编号=" + schemeBean.getUserId());
            throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120001);
        }
        //校验方案最大倍数
        if(lottery.getXzMaxSellMultiple() == 1)
        {
            if(schemeBean.getSmultiple() > lottery.getMaxSellMultiple())
            {
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120005,
                        MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120005_MSG,new Object[]{lottery.getMaxSellMultiple()}));
            }
        }
        //校验方案最小倍数
        if(lottery.getXzMinSellMultiple() == 1)
        {
            if(schemeBean.getSmultiple() < lottery.getMinSellMultiple())
            {
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120031,
                        MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120031_MSG,new Object[]{lottery.getMinSellMultiple()}));
            }
        }
        //校验方案最大金额
        if(lottery.getXzMaxSellMoney() == 1)
        {
            if(gtmoney > lottery.getMaxSellMoney())
            {
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120006,
                        MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120006_MSG,new Object[]{lottery.getMaxSellMoney()}));
            }
        }
        //校验方案最小金额
        if(lottery.getXzMinSellMoney() == 1)
        {
            if(gtmoney < lottery.getMinSellMoney())
            {
                throw new ServiceException(ErrorCode_API.ERROR_SCHEM_120032,
                        MessageFormat.format(ErrorCode_API.ERROR_SCHEM_120032_MSG,new Object[]{lottery.getMinSellMoney()}));
            }
        }
        //校验期次/场次销售状态
        schemeBean.setTzcontent(sdscheme.getSchemeContent());//设置方案投注选项
        schemeBean.setPlayTypeId(sdscheme.getPlayTypeId());//设置方案玩法类型
        checkJcMatchSellStatus(schemeBean);

        /**
         * 判断系统是否有配置临近截止时间的投注倍数和注数的限制
         */
        String zsbsxzstr = SysConfig.getString("TRADE_JC_ENDTIME_XZZSBS");
        if(StringUtil.isNotEmpty(zsbsxzstr) && !"-1".equals(zsbsxzstr))
        {
            String[] zsbsxzs = zsbsxzstr.split(";");
            Calendar current = Calendar.getInstance();
            if(sdscheme.getSchemeType() == SchemeConstants.SCHEME_TYPE_YH)
            {
            }
            else
            {
                for(String zsbsxz : zsbsxzs)
                {
                    String[] xzs = zsbsxz.split("\\|");
                    if(DateUtil.minutesBetween(current.getTime(),schemeBean.getEtime()) <= Integer.parseInt(xzs[0]))
                    {
                        if(StringUtil.isNotEmpty(xzs[1]) && sdscheme.getSchemeZs() > Integer.parseInt(xzs[1]))
                        {
                            logger.error("[跟单]跟投方案注数不能大于系统配置的临近截止时间的投注注数的限制!神单方案id=" + sdscheme.getId() + ",用户编号=" + schemeBean.getUserId() + ",跟投方案注数=" + sdscheme.getSchemeZs());
                            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"当前时间段不能跟投超过" + xzs[1] + "注的神单");
                        }
                        else if(StringUtil.isNotEmpty(xzs[2]) && schemeBean.getSmultiple() > Integer.parseInt(xzs[2]))
                        {
                            logger.error("[跟单]跟投倍数不能大于系统配置的临近截止时间的投注倍数的限制!神单方案id=" + sdscheme.getId() + ",用户编号=" + schemeBean.getUserId() + ",跟投倍数=" + schemeBean.getSmultiple());
                            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"当前时间段跟投不能超过" + xzs[2] + "倍");
                        }
                    }
                }
            }
        }

        /**
         * 设置方案参数,保存方案
         */
        //设置方案参数
        schemeBean.setStype(SchemeConstants.SCHEME_TYPE_GD);//设置方案类型为跟单
        schemeBean.setStatus(SchemeConstants.SCHEME_STATUS_DZF);//设置方案状态为待支付
        schemeBean.setSdesc(SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_DZF));//设置方案状态描述
        schemeBean.setMoney(schemeBean.getSmultiple() * dbmoney);//设置方案金额
        schemeBean.setSzs(sdscheme.getSchemeZs());//设置方案注数
        schemeBean.setLname(sdscheme.getLotteryName());//设置方案所属彩种名称
        schemeBean.setPeriod(sdscheme.getPeriod());//设置期次号
        schemeBean.setWtype(sdscheme.getSchemePlayType());//设置玩法名称
        settingTzContentSpAndLose(schemeBean);//设置投注项赔率/让球/让分/大小分盘口
        schemeBean.setBigOrderStatus(sdscheme.getBigOrderStatus());//设置大单状态
        schemeBean.setCopySchemeId(sdscheme.getId());//设置被复制的方案id

        //设置方案编号
        String scodePrefix = getSchemeCodePrefix(schemeBean.getLid());;//方案编号前缀
        if(sdscheme.getSchemeOrderId().startsWith(SchemeConstants.SCHEME_SCODE_JJYH))
        {
            scodePrefix = SchemeConstants.SCHEME_SCODE_JJYH;
        }
        String randdom = "" + new Random().nextInt(10) + new Random().nextInt(10) + new Random().nextInt(10) + new Random().nextInt(10);//生成4位的随机数
        schemeBean.setScode(scodePrefix + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME) + randdom);

        //设置客户端来源及名称
        schemeBean.setClientSource(KeyConstants.loginUserMap.get(schemeBean.getAppId()));//设置客户端来源
        schemeBean.setClientSourceName(UserConstants.userSourceMap.get(schemeBean.getClientSource()));//设置客户端来源名称

        //商户渠道-默认00000为官方
        if(StringUtil.isEmpty(schemeBean.getSource()))
        {
            schemeBean.setSource("00000");//默认官方
        }

        //设置发单人昵称
        User sduser = userMapper.queryUserInfoById(sdscheme.getSchemeUserId());
        schemeBean.setUnick(sduser.getNickName());

        //设置投注内容隐藏模式/理论盈利率/理论奖金范围
        schemeBean.setHideType(sdscheme.getHideType());
        schemeBean.setProfitMargin(sdscheme.getProfitMargin());
        String theoryPrize = sdscheme.getTheoryPrize();
        if(StringUtil.isNotEmpty(theoryPrize))
        {
            try
            {
                if(theoryPrize.indexOf("-") > -1)
                {
                    String[] theoryPrizes = theoryPrize.split("-");
                    double minTheoryPrizes = (schemeBean.getSmultiple() / sdscheme.getSchemeMultiple()) * DoubleUtil.roundDouble(theoryPrizes[0].replace("元",""),2);//最小理论奖金
                    double maxTheoryPrizes = ((schemeBean.getSmultiple() / sdscheme.getSchemeMultiple())) * DoubleUtil.roundDouble(theoryPrizes[1].replace("元",""),2);//最大理论奖金
                    schemeBean.setLprize(minTheoryPrizes + "-" + maxTheoryPrizes + "元");
                }
                else if(theoryPrize.indexOf("~") > -1)
                {
                    String[] theoryPrizes = theoryPrize.split("~");
                    double minTheoryPrizes = (schemeBean.getSmultiple() / sdscheme.getSchemeMultiple()) * DoubleUtil.roundDouble(theoryPrizes[0].replace("元",""),2);//最小理论奖金
                    double maxTheoryPrizes = ((schemeBean.getSmultiple() / sdscheme.getSchemeMultiple())) * DoubleUtil.roundDouble(theoryPrizes[1].replace("元",""),2);//最大理论奖金
                    schemeBean.setLprize(minTheoryPrizes + "~" + maxTheoryPrizes + "元");
                }
                else
                {
                    double actualTheoryPrize = DoubleUtil.roundDouble(theoryPrize.replace("元",""),2);//神单实际理论奖金
                    schemeBean.setLprize((schemeBean.getSmultiple() / sdscheme.getSchemeMultiple()) * actualTheoryPrize + "元");//跟单方案理论奖金
                }
            }
            catch(Exception e)
            {
                logger.error("[跟单]提取神单理论奖金范围发生异常!神单方案id=" + sdscheme.getId() + ",神单理论奖金范围=" + theoryPrize + ",用户编号=" + schemeBean.getUserId() + ",异常信息=" + e);
            }
        }

        //保存方案
        int count = schemeMapper.saveFollowScheme(schemeBean);
        if(count > 0 && schemeBean.getId() != null)
        {
            //保存对阵场次信息
            Map<String,Object> matchMap = new HashMap<String,Object>();
            for(Object matchCode : schemeBean.getMatchList())
            {
                matchMap.put("schemeId",schemeBean.getId());//设置方案id
                matchMap.put("schemeOrderId",schemeBean.getScode());//设置方案编号
                matchMap.put("lotteryId",schemeBean.getLid());//设置彩种id
                matchMap.put("matchCode",matchCode);//设置场次竞彩编号
                schemeMapper.saveSchemeMatches(matchMap);//保存方案对阵信息
            }
        }

        /**
         * 设置返回数据
         */
        //设置注数和倍数信息
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("zs",schemeBean.getSzs());//注数
        dataMap.put("bs",schemeBean.getSmultiple());//倍数
        try
        {
            //查询用户当前余额
            UserAccount userAccount = userAccountMapper.queryUserAccountInfoByUserId(schemeBean.getUserId());
            if(userAccount == null)
            {
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            dataMap.put("balance",String.format("%.2f",userAccount.getBalance()));
            dataMap.put("dbalance",(userAccount.getBalance() >= schemeBean.getMoney()? 0 : (String.format("%.2f",schemeBean.getMoney() - userAccount.getBalance()))));
        }
        catch (Exception e)
        {
            logger.error("[跟单]查询用户当前余额发生异常!用户编号=" + schemeBean.getUserId() + lottery.getName() + ",异常信息=" + e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        try
        {
            //查询用户优惠券信息
            Dto couponQueryDto = new BaseDto("userId",schemeBean.getUserId());
            couponQueryDto.put("useStatus",1);
            couponQueryDto.put("useLotteryId",schemeBean.getLid());
            List<Dto> couponDataList = userCouponMapper.queryUserCoupons(couponQueryDto);
            dataMap.put("coupons",filterCoupon(couponDataList,schemeBean));
        }
        catch (Exception e)
        {
            logger.error("[跟单]查询用户优惠券发生异常!用户编号=" + schemeBean.getUserId() + lottery.getName() + ",异常信息=" + e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //设置其它返回数据
        dataMap.put("lname",schemeBean.getLname());//彩种名称
        dataMap.put("money",schemeBean.getMoney());//方案金额
        dataMap.put("ymoney",schemeBean.getMoney());//应付金额
        dataMap.put("sid",schemeBean.getId());//方案id
        dataMap.put("scode",schemeBean.getScode());//方案编号
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
        logger.info("[跟单]跟单成功!用户编号=" + schemeBean.getUserId()
                + ",方案id=" + schemeBean.getId()
                + ",方案编号=" + schemeBean.getScode()
                + ",方案金额=" + schemeBean.getMoney()
                + ",彩种=" + schemeBean.getLname());

    }

    /**
     * 获取神单用户
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getSdUser(Dto params, ResultBean result) throws ServiceException
    {
        //查询并封装数据
        List<Dto> dataList = new ArrayList<Dto>();
        params.put("lotteryId",LotteryConstants.JCZQ);
        List<Dto> userList = schemeMapper.querySdUser(params);//查询神单用户
        if(userList != null && userList.size() > 0)
        {
            Dto dataDto = null;
            for(Dto userDto : userList)
            {
                dataDto = new BaseDto("uid",userDto.get("id"));//设置用户编号
                dataDto.put("unick",userDto.get("nickName"));//设置用户昵称

                //设置用户头像
                if(StringUtil.isEmpty(userDto.get("avatar")))
                {
                    dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                }
                else
                {
                    if(userDto.getAsString("avatar").startsWith("http"))
                    {
                        dataDto.put("avatar",userDto.getAsString("avatar"));
                    }
                    else
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + userDto.getAsString("avatar"));
                    }
                }
                dataList.add(dataDto);
            }
        }
        //设置返回数据
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("list",dataList);
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 获取神单详情
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getSdSchemeDetail(Dto params, ResultBean result) throws ServiceException
    {
        //校验参数
        if(StringUtil.isEmpty(params.get("sid")))
        {
            logger.error("[获取神单详情]神单id不能为空!");
            return;
        }
        //查询并封装数据
        params.put("lid",LotteryConstants.JCZQ);
        List<Dto> schemeList = schemeMapper.querySdSchemeInfo(params);//查询神单
        Dto dataDto = new BaseDto();
        Calendar currentCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        if(schemeList != null && schemeList.size() > 0)
        {
            //拼装神单方案信息
            Dto schemeDto = schemeList.get(0);
            dataDto.put("sid",schemeDto.get("id"));//设置方案id
            dataDto.put("lname",schemeDto.get("lotteryName"));//设置彩种名称
            dataDto.put("logo",SysConfig.getLotteryLogo(schemeDto.getAsString("lotteryId")) + "?v=" + KeyConstants.FIXED_VERSION);//设置彩种logo
            dataDto.put("uid",schemeDto.get("schemeUserId"));//设置发单人编号
            dataDto.put("nickName",schemeDto.get("nickName"));//设置发单人昵称

            //设置用户头像
            if(StringUtil.isEmpty(schemeDto.get("avatar")))
            {
                dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
            }
            else
            {
                if(schemeDto.getAsString("avatar").startsWith("http"))
                {
                    dataDto.put("avatar",schemeDto.getAsString("avatar"));
                }
                else
                {
                    dataDto.put("avatar",SysConfig.getHostStatic() + schemeDto.getAsString("avatar"));
                }
            }
            dataDto.put("islq",LotteryUtils.isJczq(schemeDto.getAsString("lotteryId"))? 0 : 1);//设置是否为竞彩篮球(0-不是 1-是)
            dataDto.put("remuneration",(schemeDto.getAsString("remuneration") + "%"));//设置提成比例
            dataDto.put("zgmoney",schemeDto.getAsInteger("schemeMoney") + "元");//设置自购金额/方案金额
            dataDto.put("xgmoney",schemeDto.getAsInteger("safeguardMoney") + "元");//设置限购总额
            dataDto.put("qtmoney",schemeDto.getAsInteger("minParticipant") + "元");//设置起投金额
            dataDto.put("ygmoney",schemeDto.getAsInteger("redSafeHuardMoney") + "元");//设置已跟投金额
            dataDto.put("symoney",schemeDto.getAsInteger("safeguardMoney") - schemeDto.getAsInteger("redSafeHuardMoney"));//设置剩余可跟投金额
            dataDto.put("gmjd",((int)((schemeDto.getAsDoubleValue("redSafeHuardMoney") / schemeDto.getAsDoubleValue("safeguardMoney")) * 100)));//设置跟买进度

            //拼装发单人战绩信息
            dataDto.put("weekHitRate",schemeDto.getAsInteger("weekHitRate") + "%");//近一周命中率
            dataDto.put("monthHitRate",schemeDto.getAsInteger("monthHitRate") + "%");//近一月命中率
            dataDto.put("weekyll",schemeDto.getAsInteger("weekWinRate") + "%");//近一周盈利率
            dataDto.put("monthyll",schemeDto.getAsInteger("monthWinRate") + "%");//近一月盈利率
            dataDto.put("weekhit",schemeDto.getAsDoubleValue("weekHitMoney") + "元");//近一周中奖总金额
            dataDto.put("monthhit",schemeDto.getAsDoubleValue("monthHitMoney") + "元");//近一月中奖总金额
            dataDto.put("tgdrc",schemeDto.getAsInteger("followSums"));//累计跟单人次
            dataDto.put("tgdmoney",schemeDto.getAsInteger("followMoneySums"));//累计跟买金额
            dataDto.put("thitmoney",schemeDto.getAsDoubleValue("hitMoney") + "元");//设置累计中奖金额

            // 拼装截止时间
            calendar.setTime(DateUtil.parseDate(schemeDto.getAsString("endTime"),DateUtil.DEFAULT_DATE_TIME));
            dataDto.put("etime",getDateTimeDetail(calendar,currentCalendar));

            //拼装过关方式
            String[] tzspContent = schemeDto.getAsString("schemeContent").split("\\|");//提取带sp的投注内容
            dataDto.put("ggfs",tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," "));//设置过关方式

            //设置方案开奖状态及中奖金额
            int openStatus = schemeDto.getAsInteger("openStatus");//提取方案计奖状态
            dataDto.put("zstatus",openStatus);//设置中奖状态(0-待开奖 1-未中奖 2-已中奖)
            if(2 == openStatus)
            {
                dataDto.put("zdesc","已中奖");
                dataDto.put("zjmoney",String.format("%.2f",(schemeDto.getAsDoubleValue("prizeTax") - schemeDto.getAsDoubleValue("rewardPrize"))));
            }
            else if(1 == openStatus)
            {
                dataDto.put("zdesc","未中奖");
                dataDto.put("zjmoney",String.format("%.2f",0d) + "元");
            }
            else
            {
                dataDto.put("zdesc","待开奖");
                dataDto.put("zjmoney","待开奖");
            }

            /**
             * 拼装对阵信息/投注选项
             */
            if("0".equals(schemeDto.getAsString("hideType"))
                || (StringUtil.isNotEmpty(params.get("userId")) && params.getAsLong("userId").longValue() == schemeDto.getAsLong("schemeUserId").longValue()))
            {
                schemeDto.put("hideType",0);//设置对阵选项隐藏模式为不隐藏
            }
            List<Dto> tzxxList = new ArrayList<Dto>();
            if(currentCalendar.after(calendar))
            {
                //方案已截止
                schemeDto.put("needSg",1);
                tzxxList.addAll(getYjzSchemeTzxxList(schemeDto));
            }
            else
            {
                //方案未截止
                tzxxList.addAll(getWjzSchemeTzxxList(schemeDto));
            }
            dataDto.put("tzxxs",tzxxList);

            //拼装神单跟买用户
            List<Dto> gmdataList = new ArrayList<Dto>();
            List<Dto> sdfollowList = schemeMapper.querySdFollow(new BaseDto("sid",schemeDto.get("id")));//查询神单跟买记录
            if(sdfollowList != null && sdfollowList.size() > 0)
            {
                Dto sdgmData = null;
                for(Dto sdfollowDto : sdfollowList)
                {
                    sdgmData = new BaseDto();
                    sdgmData.put("unick",sdfollowDto.getAsString("nickName").substring(0,1) + "***");//设置跟单人昵称
                    sdgmData.put("money",sdfollowDto.getAsInteger("schemeMoney"));//设置跟买金额
                    sdgmData.put("gmtime",sdfollowDto.get("createTime"));//设置跟买时间

                    //设置出票状态
                    int status = sdfollowDto.getAsInteger("schemeStatus");//提取出票状态
                    if(status == SchemeConstants.SCHEME_STATUS_CPCG)
                    {
                        sdgmData.put("zdesc","出票成功");//设置跟单状态
                    }
                    else if(status == SchemeConstants.SCHEME_STATUS_CDCG)
                    {
                        sdgmData.put("zdesc","出票失败");
                    }
                    else
                    {
                        sdgmData.put("zdesc","出票中");
                    }
                    //设置中奖金额
                    openStatus = sdfollowDto.getAsInteger("openStatus");//提取计奖状态
                    sdgmData.put("zstatus",openStatus);//设置中奖状态(0-待开奖 1-已中奖 2-未中奖)
                    if(0 == openStatus)
                    {
                        sdgmData.put("zjmoney","--");
                    }
                    else
                    {
                        sdgmData.put("zjmoney",String.format("%.2f",(sdfollowDto.getAsDoubleValue("prizeTax") + sdfollowDto.getAsDoubleValue("rewardPrize"))));
                    }
                    gmdataList.add(sdgmData);
                }
            }
            dataDto.put("gmrc",gmdataList.size());//设置神单跟买次数
            dataDto.put("gmlist",gmdataList);//设置神单跟买记录

            //拼装其它可跟方案
            List<Dto> otherSdDataList = new ArrayList<Dto>();
            Dto queryDto = new BaseDto("lid",LotteryConstants.JCZQ);
            queryDto.put("suserId",schemeDto.get("schemeUserId"));
            queryDto.put("completeFlag",1);
            queryDto.put("minEndTime",DateUtil.formatDate(currentCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
            queryDto.put("excludeIds",schemeDto.get("id"));
            List<Dto> otherSdList = schemeMapper.querySdSchemeInfo(queryDto);
            if(otherSdList != null && otherSdList.size() > 0)
            {
                Dto otherSdData = null;
                for(Dto otherSdDto : otherSdList)
                {
                    otherSdData = new BaseDto();
                    otherSdData.put("sid",otherSdDto.get("id"));//设置方案id
                    otherSdData.put("lname",otherSdDto.get("lotteryName"));//设置彩种名称
                    otherSdData.put("remuneration",(otherSdDto.getAsString("remuneration") + "%"));//设置提成比例
                    otherSdData.put("zgmoney",otherSdDto.get("schemeMoney") + "元");//设置自购金额/方案金额
                    otherSdData.put("qtmoney",otherSdDto.getAsInteger("minParticipant") + "元");//设置起投金额

                    // 拼装截止时间
                    calendar.setTime(DateUtil.parseDate(otherSdDto.getAsString("endTime"),DateUtil.DEFAULT_DATE_TIME));
                    otherSdData.put("etime",getDateTimeDetail(calendar,currentCalendar));

                    //拼装过关方式
                    tzspContent = otherSdDto.getAsString("schemeContent").split("\\|");//提取带sp的投注内容
                    otherSdData.put("ggfs",tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," "));//设置过关方式

                    otherSdDataList.add(otherSdData);
                }
            }
            dataDto.put("otherlist",otherSdDataList);//设置其它可跟方案
        }
        //设置返回数据
        result.setData(dataDto);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 获取我的晒单
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getUserSd(Dto params, ResultBean result) throws ServiceException,Exception
    {
        /**
         * 查询并封装数据
         */
        //查询最新推荐
        Dto dataDto = new BaseDto();
        List<Dto> dataList = new ArrayList<Dto>();
        Calendar currentCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        params.put("minEndTime",DateUtil.formatDate(currentCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置日期查询条件,只查询未截止的神单
        params.put("lid",LotteryConstants.JCZQ);
        params.put("suserId",params.get("userId"));
        //最多只查询最近3个月的方案
        calendar.add(Calendar.MONTH,-(SchemeConstants.SCHEME_QUERY_DATERANGE));
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("minCreateTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置方案起始时间
        List<Dto> schemeList = schemeMapper.queryUserSdSchemeInfo(params);//查询神单
        if(schemeList != null && schemeList.size() > 0)
        {
            Dto data = null;
            for(Dto schemeDto : schemeList)
            {
                data = new BaseDto();
                data.put("sid",schemeDto.get("id"));//设置方案id
                data.put("islq",LotteryUtils.isJczq(schemeDto.getAsString("lotteryId"))? 0 : 1);//设置是否为竞彩篮球(0-不是 1-是)
                data.put("remuneration",(schemeDto.getAsString("remuneration") + "%"));//设置提成比例
                data.put("zgmoney",schemeDto.getAsInteger("schemeMoney") + "元");//设置自购金额
                data.put("ygmoney",schemeDto.getAsInteger("redSafeHuardMoney") + "元");//设置已跟投金额
                data.put("symoney",schemeDto.getAsInteger("safeguardMoney") - schemeDto.getAsInteger("redSafeHuardMoney"));//设置剩余可跟投金额

                //拼装截止时间
                calendar.setTime(DateUtil.parseDate(schemeDto.getAsString("endTime"),DateUtil.DEFAULT_DATE_TIME));
                data.put("etime",getDateTimeDetail(calendar,currentCalendar));

                //拼装过关方式
                String[] tzspContent = schemeDto.getAsString("schemeContent").split("\\|");//提取带sp的投注内容
                data.put("ggfs",tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," "));//设置过关方式

                /**
                 * 拼装对阵信息/投注选项
                 */
                schemeDto.put("hideType","0");
                List<Dto> tzxxList = new ArrayList<Dto>();
                tzxxList.addAll(getYjzSchemeTzxxList(schemeDto));
                data.put("tzxxs",tzxxList);
                dataList.add(data);
            }
        }
        dataDto.put("newlysd",dataList);//设置最新推荐

        //查询最近战绩(已截止)
        dataList = new ArrayList<Dto>();
        params.remove("minEndTime");
        params.put("maxEndTime",DateUtil.formatDate(currentCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置日期查询条件,只查询已截止的神单
        params.put("psize",10);//只查最近10条记录
        params.put("pstart",0);
        schemeList = schemeMapper.queryUserSdSchemeInfo(params);//查询神单
        if(schemeList != null && schemeList.size() > 0)
        {
            Dto data = null;
            for(Dto schemeDto : schemeList)
            {
                data = new BaseDto();
                data.put("sid",schemeDto.get("id"));//设置方案id
                data.put("islq",LotteryUtils.isJczq(schemeDto.getAsString("lotteryId"))? 0 : 1);//设置是否为竞彩篮球(0-不是 1-是)
                data.put("remuneration",(schemeDto.getAsString("remuneration") + "%"));//设置提成比例
                data.put("ygmoney",schemeDto.getAsInteger("redSafeHuardMoney") + "元");//设置跟投金额
                data.put("gmrc",StringUtil.isEmpty(schemeDto.get("gmrc"))? 0 : schemeDto.get("gmrc"));//设置跟买人次

                //设置中奖状态
                int openStatus = schemeDto.getAsInteger("openStatus");//提取计奖状态
                data.put("zstatus",openStatus);//设置中奖状态,0-等待开奖 1-未中奖 2-已中奖
                if(openStatus == 2)
                {
                    double prize = schemeDto.getAsDouble("prizeTax") - schemeDto.getAsDouble("rewardPrize");//实际中奖税后金额
                    double money = schemeDto.getAsDouble("schemeMoney");
                    int yll = (int)((prize / money) * 100);
                    data.put("yll",yll + "%");//设置盈利率
                }
                else if(openStatus == 1)
                {
                    data.put("yll","0%");//设置盈利率
                }

                //拼装截止时间
                calendar.setTime(DateUtil.parseDate(schemeDto.getAsString("endTime"),DateUtil.DEFAULT_DATE_TIME));
                data.put("etime",getDateTimeDetail(calendar,currentCalendar));

                //拼装过关方式
                String[] tzspContent = schemeDto.getAsString("schemeContent").split("\\|");//提取带sp的投注内容
                data.put("ggfs",tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," "));//设置过关方式

                /**
                 * 拼装对阵信息/投注选项
                 */
                schemeDto.put("hideType",0);//设置对阵选项隐藏模式为不隐藏
                schemeDto.put("needSg",1);//设置需要显示赛果
                List<Dto> tzxxList = new ArrayList<Dto>();
                tzxxList.addAll(getYjzSchemeTzxxList(schemeDto));
                data.put("tzxxs",tzxxList);
                dataList.add(data);
            }
        }
        dataDto.put("recentlysd",dataList);//设置最近战绩

        //查询用户信息
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));
        dataDto.put("uid",user.getId());//设置用户编号
        dataDto.put("unick",user.getNickName());//设置用户昵称

        //设置用户头像
        if(StringUtil.isEmpty(user.getAvatar()))
        {
            dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
        }
        else
        {
            if(user.getAvatar().startsWith("http"))
            {
                dataDto.put("avatar",user.getAvatar());
            }
            else
            {
                dataDto.put("avatar",SysConfig.getHostStatic() + user.getAvatar());
            }
        }
        dataDto.put("gznum",user.getFollowNum());//设置用户关注数
        dataDto.put("fsnum",user.getFansNum());//设置用户粉丝数

        //查询用户战绩信息
        Dto userzjQueryDto = new BaseDto("lid",LotteryConstants.JCZQ);
        userzjQueryDto.put("userId",params.get("userId"));
        List<Dto> userzjDtoList = schemeMapper.querySdTj(userzjQueryDto);
        if(userzjDtoList != null && userzjDtoList.size() > 0)
        {
            Dto userzjDto = userzjDtoList.get(0);
            dataDto.put("wmzl",userzjDto.get("weekHitRate") + "%");//设置最近一周命中率
            dataDto.put("wyll",userzjDto.get("weekWinRate") + "%");//设置最近一周盈利率
            dataDto.put("whitmoney",userzjDto.getAsString("weekHitMoney") + "元");//设置最近一周中奖金额
            dataDto.put("mmzl",userzjDto.get("monthHitRate") + "%");//设置最近一月命中率
            dataDto.put("myll",userzjDto.get("monthWinRate") + "%");//设置最近一月盈利率
            dataDto.put("mhitmoney",userzjDto.getAsString("monthHitMoney") + "元");//设置最近一月中奖奖金
            dataDto.put("ttc",userzjDto.getAsString("rewardMoney") + "元");//设置累计收到佣金
            dataDto.put("recentlyzs",userzjDto.get("tenOrderTrend"));//设置近10单走势
        }
        else
        {
            dataDto.put("wmzl","--");//设置最近一周命中率
            dataDto.put("wyll","--");//设置最近一周盈利率
            dataDto.put("whitmoney","--");//设置最近一周中奖金额
            dataDto.put("mmzl","--");//设置最近一月命中率
            dataDto.put("myll","--");//设置最近一月盈利率
            dataDto.put("mhitmoney","--");//设置最近一月中奖奖金
            dataDto.put("ttc","--");//设置累计收到佣金
            dataDto.put("recentlyzs","");//设置近10单走势
        }

        //设置返回数据
        result.setData(dataDto);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 获取用户最近神单战绩
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getUserHistorySd(Dto params, ResultBean result) throws ServiceException,Exception
    {
        settingPageParams(params);//设置分页查询参数
        Calendar currentCalendar = Calendar.getInstance();
        params.put("maxEndTime",DateUtil.formatDate(currentCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置日期查询条件,只查询已截止的神单
        params.put("lid",LotteryConstants.JCZQ);

        //最多只查询最近3个月的方案
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-(SchemeConstants.SCHEME_QUERY_DATERANGE));
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("minCreateTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置方案起始时间

        //查询并封装数据
        List<Dto> dataList = new ArrayList<Dto>();
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("list",new ArrayList<SchemeBean>());
        List<Dto> schemeList = schemeMapper.queryUserSdSchemeInfo(params);//查询神单
        if(schemeList != null && schemeList.size() > 0)
        {
            Dto data = null;
            for(Dto schemeDto : schemeList)
            {
                data = new BaseDto();
                data.put("sid",schemeDto.get("id"));//设置方案id
                data.put("islq",LotteryUtils.isJczq(schemeDto.getAsString("lotteryId"))? 0 : 1);//设置是否为竞彩篮球(0-不是 1-是)
                data.put("remuneration",(schemeDto.getAsString("remuneration") + "%"));//设置提成比例
                data.put("ygmoney",schemeDto.getAsInteger("redSafeHuardMoney") + "元");//设置跟投金额
                data.put("gmrc",StringUtil.isEmpty(schemeDto.get("gmrc"))? 0 : schemeDto.get("gmrc"));//设置跟买人次

                //设置中奖状态
                int openStatus = schemeDto.getAsInteger("openStatus");//提取计奖状态
                data.put("zstatus",openStatus);//设置中奖状态,0-等待开奖 1-未中奖 2-已中奖
                if(openStatus == 2)
                {
                    double prize = schemeDto.getAsDouble("prizeTax") - schemeDto.getAsDouble("rewardPrize");//实际中奖税后金额
                    double money = schemeDto.getAsDouble("schemeMoney");
                    int yll = (int)((prize / money) * 100);
                    data.put("yll",yll + "%");//设置盈利率
                }
                else if(openStatus == 1)
                {
                    data.put("yll","0%");//设置盈利率
                }

                //拼装截止时间
                calendar.setTime(DateUtil.parseDate(schemeDto.getAsString("endTime"),DateUtil.DEFAULT_DATE_TIME));
                data.put("etime",getDateTimeDetail(calendar,currentCalendar));

                //拼装过关方式
                String[] tzspContent = schemeDto.getAsString("schemeContent").split("\\|");//提取带sp的投注内容
                data.put("ggfs",tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," "));//设置过关方式

                /**
                 * 拼装对阵信息/投注选项
                 */
                if("0".equals(params.getAsString("hideType")))
                {
                    schemeDto.put("hideType",0);//设置对阵选项隐藏模式为不隐藏
                }
                schemeDto.put("needSg",1);//设置需要显示赛果
                List<Dto> tzxxList = new ArrayList<Dto>();
                tzxxList.addAll(getYjzSchemeTzxxList(schemeDto));
                data.put("tzxxs",tzxxList);
                dataList.add(data);
            }
            dataMap.put("list",dataList);
        }

        //如果有分页标识,则查询总记录条数及总页数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = schemeMapper.queryUserSdSchemeInfoCount(params);
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置总条数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }

        //设置返回数据
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 获取我的关注
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getUserGz(Dto params, ResultBean result) throws ServiceException,Exception
    {
        settingPageParams(params);//设置分页查询参数

        //查询并封装数据
        List<Dto> dataList = new ArrayList<Dto>();
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("list",new ArrayList<SchemeBean>());
        List<Dto> gzdataList = schemeMapper.queryUserFollow(params);
        if(gzdataList != null && gzdataList.size() > 0)
        {
            Dto dataDto = null;
            for(Dto gzdata : gzdataList)
            {
                dataDto = new BaseDto();
                dataDto.put("uid",gzdata.get("userId"));//设置用户编号
                dataDto.put("unick",gzdata.get("nickName"));//设置用户昵称

                //设置用户头像
                if(StringUtil.isEmpty(gzdata.get("avatar")))
                {
                    dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                }
                else
                {
                    if(gzdata.getAsString("avatar").startsWith("http"))
                    {
                        dataDto.put("avatar",gzdata.getAsString("avatar"));
                    }
                    else
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + gzdata.getAsString("avatar"));
                    }
                }
                dataDto.put("sdcount",StringUtil.isEmpty(gzdata.get("sdcount"))? 0 : gzdata.get("sdcount"));//设置用户当前可跟投神单的数量
                dataDto.put("thitmoney",gzdata.getAsString("hitMoney"));//设置累计中奖金额
                dataList.add(dataDto);
            }
            dataMap.put("list",dataList);
        }

        //如果有分页标识,则查询总记录条数及总页数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = schemeMapper.queryUserFollowCount(params);
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置总条数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }

        //设置返回数据
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 获取我的粉丝
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getUserFans(Dto params, ResultBean result) throws ServiceException,Exception
    {
        settingPageParams(params);//设置分页查询参数

        //查询并封装数据
        List<Dto> dataList = new ArrayList<Dto>();
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("list",new ArrayList<SchemeBean>());
        params.put("suserId",params.get("userId"));
        List<Dto> gzdataList = schemeMapper.queryUserFans(params);
        if(gzdataList != null && gzdataList.size() > 0)
        {
            Dto dataDto = null;
            for(Dto fansdata : gzdataList)
            {
                dataDto = new BaseDto();
                dataDto.put("unick",fansdata.getAsString("nickName").substring(0,1) + "***");//设置用户昵称

                //设置用户头像
                if(StringUtil.isEmpty(fansdata.get("avatar")))
                {
                    dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
                }
                else
                {
                    if(fansdata.getAsString("avatar").startsWith("http"))
                    {
                        dataDto.put("avatar",fansdata.getAsString("avatar"));
                    }
                    else
                    {
                        dataDto.put("avatar",SysConfig.getHostStatic() + fansdata.getAsString("avatar"));
                    }
                }
                dataDto.put("gtime",fansdata.get("createTime"));//设置关注时间
                dataList.add(dataDto);
            }
            dataMap.put("list",dataList);
        }

        //如果有分页标识,则查询总记录条数及总页数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = schemeMapper.queryUserFansCount(params);
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置总条数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }

        //设置返回数据
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 达人主页
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getSdUserHome(Dto params, ResultBean result) throws ServiceException,Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("uid")))
        {
            logger.error("[达人主页]达人用户编号不能为空!");
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        /**
         * 查询并封装数据
         */
        //查询最新推荐
        Dto dataDto = new BaseDto();
        List<Dto> dataList = new ArrayList<Dto>();
        Calendar currentCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        params.put("minEndTime",DateUtil.formatDate(currentCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置日期查询条件,只查询未截止的神单
        params.put("lid",LotteryConstants.JCZQ);
        params.put("suserId",params.get("uid"));
        //最多只查询最近3个月的方案
        calendar.add(Calendar.MONTH,-(SchemeConstants.SCHEME_QUERY_DATERANGE));
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("minCreateTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置方案起始时间
        List<Dto> schemeList = schemeMapper.queryUserSdSchemeInfo(params);//查询神单
        if(schemeList != null && schemeList.size() > 0)
        {
            Dto data = null;
            for(Dto schemeDto : schemeList)
            {
                data = new BaseDto();
                data.put("sid",schemeDto.get("id"));//设置方案id
                data.put("islq",LotteryUtils.isJczq(schemeDto.getAsString("lotteryId"))? 0 : 1);//设置是否为竞彩篮球(0-不是 1-是)
                data.put("remuneration",(schemeDto.getAsString("remuneration") + "%"));//设置提成比例
                data.put("zgmoney",schemeDto.getAsInteger("schemeMoney") + "元");//设置自购金额
                data.put("ygmoney",schemeDto.getAsInteger("redSafeHuardMoney") + "元");//设置已跟投金额
                data.put("symoney",schemeDto.getAsInteger("safeguardMoney") - schemeDto.getAsInteger("redSafeHuardMoney"));//设置剩余可跟投金额

                //拼装截止时间
                calendar.setTime(DateUtil.parseDate(schemeDto.getAsString("endTime"),DateUtil.DEFAULT_DATE_TIME));
                data.put("etime",getDateTimeDetail(calendar,currentCalendar));

                //拼装过关方式
                String[] tzspContent = schemeDto.getAsString("schemeContent").split("\\|");//提取带sp的投注内容
                data.put("ggfs",tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," "));//设置过关方式

                /**
                 * 拼装对阵信息/投注选项
                 */
                List<Dto> tzxxList = new ArrayList<Dto>();
                tzxxList.addAll(getYjzSchemeTzxxList(schemeDto));
                data.put("tzxxs",tzxxList);
                dataList.add(data);
            }
        }
        dataDto.put("newlysd",dataList);//设置最新推荐

        //查询最近战绩(已截止)
        dataList = new ArrayList<Dto>();
        params.remove("minEndTime");
        params.put("maxEndTime",DateUtil.formatDate(currentCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//设置日期查询条件,只查询已截止的神单
        params.put("psize",10);//只查最近10条记录
        params.put("pstart",0);
        schemeList = schemeMapper.queryUserSdSchemeInfo(params);//查询神单
        if(schemeList != null && schemeList.size() > 0)
        {
            Dto data = null;
            for(Dto schemeDto : schemeList)
            {
                data = new BaseDto();
                data.put("sid",schemeDto.get("id"));//设置方案id
                data.put("islq",LotteryUtils.isJczq(schemeDto.getAsString("lotteryId"))? 0 : 1);//设置是否为竞彩篮球(0-不是 1-是)
                data.put("remuneration",(schemeDto.getAsString("remuneration") + "%"));//设置提成比例
                data.put("ygmoney",schemeDto.getAsInteger("redSafeHuardMoney") + "元");//设置跟投金额
                data.put("gmrc",StringUtil.isEmpty(schemeDto.get("gmrc"))? 0 : schemeDto.get("gmrc"));//设置跟买人次

                //设置中奖状态
                int openStatus = schemeDto.getAsInteger("openStatus");//提取计奖状态
                data.put("zstatus",openStatus);//设置中奖状态,0-等待开奖 1-未中奖 2-已中奖
                if(openStatus == 2)
                {
                    double prize = schemeDto.getAsDouble("prizeTax") - schemeDto.getAsDouble("rewardPrize");//实际中奖税后金额
                    double zjmoney = schemeDto.getAsDouble("schemeMoney");
                    int yll = (int)((prize / zjmoney) * 100);
                    data.put("yll",yll + "%");//设置盈利率
                }
                else if(openStatus == 1)
                {
                    data.put("yll","0%");//设置盈利率
                }

                //拼装截止时间
                calendar.setTime(DateUtil.parseDate(schemeDto.getAsString("endTime"),DateUtil.DEFAULT_DATE_TIME));
                data.put("etime",getDateTimeDetail(calendar,currentCalendar));

                //拼装过关方式
                String[] tzspContent = schemeDto.getAsString("schemeContent").split("\\|");//提取带sp的投注内容
                data.put("ggfs",tzspContent[2].replace("1*1","单关").replace("*","串").replace(","," "));//设置过关方式

                /**
                 * 拼装对阵信息/投注选项
                 */
                schemeDto.put("needSg",1);//设置需要显示赛果
                List<Dto> tzxxList = new ArrayList<Dto>();
                tzxxList.addAll(getYjzSchemeTzxxList(schemeDto));
                data.put("tzxxs",tzxxList);
                dataList.add(data);
            }
        }
        dataDto.put("recentlysd",dataList);//设置最近战绩

        //查询用户信息
        User user = userMapper.queryUserInfoById(params.getAsLong("uid"));
        dataDto.put("uid",user.getId());//设置用户编号
        dataDto.put("unick",user.getNickName());//设置用户昵称
        dataDto.put("fsnum",user.getFansNum());//设置用户粉丝数

        //设置用户头像
        if(StringUtil.isEmpty(user.getAvatar()))
        {
            dataDto.put("avatar",SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
        }
        else
        {
            if(user.getAvatar().startsWith("http"))
            {
                dataDto.put("avatar",user.getAvatar());
            }
            else
            {
                dataDto.put("avatar",SysConfig.getHostStatic() + user.getAvatar());
            }
        }

        //查询用户战绩信息
        Dto userzjQueryDto = new BaseDto("lid",LotteryConstants.JCZQ);
        userzjQueryDto.put("userId",params.get("uid"));
        List<Dto> userzjDtoList = schemeMapper.querySdTj(userzjQueryDto);
        if(userzjDtoList != null && userzjDtoList.size() > 0)
        {
            Dto userzjDto = userzjDtoList.get(0);
            dataDto.put("wmzl",userzjDto.get("weekHitRate") + "%");//设置最近一周命中率
            dataDto.put("wyll",userzjDto.get("weekWinRate") + "%");//设置最近一周盈利率
            dataDto.put("whitmoney",userzjDto.getAsString("weekHitMoney") + "元");//设置最近一周中奖金额
            dataDto.put("mmzl",userzjDto.get("monthHitRate") + "%");//设置最近一月命中率
            dataDto.put("myll",userzjDto.get("monthWinRate") + "%");//设置最近一月盈利率
            dataDto.put("mhitmoney",userzjDto.getAsString("monthHitMoney") + "元");//设置最近一月中奖奖金
            dataDto.put("ttc",userzjDto.getAsString("rewardMoney") + "元");//设置累计收到佣金
            dataDto.put("recentlyzs",userzjDto.get("tenOrderTrend"));//设置近10单走势
        }
        else
        {
            dataDto.put("wmzl","--");//设置最近一周命中率
            dataDto.put("wyll","--");//设置最近一周盈利率
            dataDto.put("whitmoney","--");//设置最近一周中奖金额
            dataDto.put("mmzl","--");//设置最近一月命中率
            dataDto.put("myll","--");//设置最近一月盈利率
            dataDto.put("mhitmoney","--");//设置最近一月中奖奖金
            dataDto.put("ttc","--");//设置累计收到佣金
            dataDto.put("recentlyzs","");//设置近10单走势
        }

        //判断当前登录用户是否已关注该达人
        if(StringUtil.isEmpty(params.get("userId")))
        {
            dataDto.put("gzstatus",0);//设置关注状态(0-未关注 1-已关注)
        }
        else
        {
            Dto queryDto = new BaseDto("userId",params.get("userId"));
            queryDto.put("suserId",params.get("uid"));
            long count = schemeMapper.queryUserFollowCount(queryDto);
            if(count > 0)
            {
                dataDto.put("gzstatus",1);//设置关注状态(0-未关注 1-已关注)
            }
            else
            {
                dataDto.put("gzstatus",0);//设置关注状态(0-未关注 1-已关注)
            }
        }

        //设置返回数据
        result.setData(dataDto);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 关注达人
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void insertFollowSdUser(Dto params, ResultBean result) throws ServiceException
    {
        //校验参数
        if(StringUtil.isEmpty(params.get("uid")))
        {
            logger.error("[关注达人]被关注者用户编号不能为空!用户编号=" + params.get("userId") + ",接收参数=" + params.toString());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验被关注者是否为本人
        if(params.getAsString("uid").equals(params.getAsString("userId")))
        {
            logger.error("[关注达人]无法关注自己!用户编号=" + params.get("userId"));
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验是否已经关注过
        params.put("suserId",params.get("uid"));
        long count = schemeMapper.queryUserFollowCount(params);
        if(count > 0)
        {
            logger.error("[关注达人]无法关注自己!用户编号=" + params.get("userId") + ",被关注者用户编号=" + params.get("uid"));
            throw new ServiceException(ErrorCode.SERVER_ERROR,"您已经关注过该达人");
        }

        /**
         * 保存所关注的达人并更新关注数和粉丝数
         */
        //保存关注的达人
        count = userMapper.insertFollowUser(params);
        if(count > 0)
        {
            //更新我的关注数
            Dto updateDto = new BaseDto("userId",params.get("userId"));
            updateDto.put("follownum",1);
            userMapper.updateUserFollowFans(updateDto);

            //更新被关注者的粉丝数
            updateDto = new BaseDto("userId",params.get("uid"));
            updateDto.put("fansnum",1);
            userMapper.updateUserFollowFans(updateDto);
        }

        //返回结果
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 取消关注达人
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void deletetFollowSdUser(Dto params, ResultBean result) throws ServiceException
    {
        //校验参数
        if(StringUtil.isEmpty(params.get("uid")))
        {
            logger.error("[取消关注达人]被关注者用户编号不能为空!用户编号=" + params.get("userId") + ",接收参数=" + params.toString());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }

        /**
         * 移除所关注的达人并更新关注数和粉丝数
         */
        //移除关注的达人
        params.put("suserId",params.get("uid"));
        int count = userMapper.deleteFollowUser(params);
        if(count > 0)
        {
            //更新我的关注数
            Dto updateDto = new BaseDto("userId",params.get("userId"));
            updateDto.put("follownum",-1);
            userMapper.updateUserFollowFans(updateDto);

            //更新被关注者的粉丝数
            updateDto = new BaseDto("userId",params.get("uid"));
            updateDto.put("fansnum",-1);
            userMapper.updateUserFollowFans(updateDto);
        }

        //返回结果
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 根据方案业务对象过滤用户优惠券
     * @author  mcdog
     * @param   couponDataList  优惠券源数据
     * @param   schemeBean      方案业务对象
     */
    public static List<Dto> filterCoupon(List<Dto> couponDataList,SchemeBean schemeBean) throws ServiceException
    {
        List<Dto> couponList = new ArrayList<Dto>();
        if(couponDataList != null && couponDataList.size() > 0)
        {
            Calendar current = Calendar.getInstance();
            Dto coupon = null;
            for(Dto couponData : couponDataList)
            {
                /**
                 * 判断优惠券是否满足使用条件
                 */
                //如果优惠券类型为发行限制期限,则只有当前时间在生效时间和过期时间范围内才能使用
                String cutype = couponData.getAsString("cuType");
                if("0".equals(cutype))
                {
                    if(DateUtil.parseCalendar(couponData.getAsString("cuBeginTime"),DateUtil.DEFAULT_DATE_TIME).after(current)
                            || DateUtil.parseCalendar(couponData.getAsString("cuEndTime"),DateUtil.DEFAULT_DATE_TIME).before(current))
                    {
                        continue;
                    }
                }
                //如果优惠券类型为为使用期限,则判断优惠券针对用户的过期时间
                else if("1".equals(cutype))
                {
                    if(DateUtil.parseCalendar(couponData.getAsString("cuExpireTime"),DateUtil.DEFAULT_DATE_TIME).before(current))
                    {
                        continue;
                    }
                }
                //如果优惠券的使用类型为满减,则判断方案金额是否满足限制金额
                if("1".equals(couponData.getAsString("cUseType")))
                {
                    if(schemeBean.getMoney() < couponData.getAsDouble("cLimitMoney"))
                    {
                        continue;
                    }
                }
                //如果优惠券面额大于方案金额,则过滤
                if(couponData.getAsDouble("cMoney") > schemeBean.getMoney())
                {
                    continue;
                }
                //封装用户优惠券前端展示对象
                coupon = new BaseDto();
                coupon.put("cuid",couponData.get("cuid"));//用户优惠券id
                coupon.put("name",couponData.getAsString("cName"));//优惠券名称
                coupon.put("faceValue",couponData.getAsString("cMoney"));//优惠券面值
                coupon.put("svalid",StringUtil.isEmpty(couponData.get("cuExpireTime"))? "" : ("过期时间：" + couponData.getAsString("cuExpireTime")));//优惠券有效期描述
                couponList.add(coupon);
            }
        }
        return couponList;
    }

    /**
     * 设置分页查询参数
     * @author  mcdog
     * @param   params      参数对象
     */
    public static void settingPageParams(Dto params) throws ServiceException
    {
        //判断是否有分页标识,如果有,则设置分页查询参数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            //pnum为空或值格式错误,则默认查询第一页
            if(params.get("pnum") == null || params.getAsInteger("pnum") <= 0)
            {
                params.put("pnum",1);
            }
            //设置读取起始位置
            long pstart = (params.getAsLong("pnum") - 1) * params.getAsLong("psize");
            params.put("pstart",pstart);//设置读取起始位置
        }
    }

    /**
     * 根据指定时间和当前时间获取详细时间
     * @author  mcdog
     * @param   calendar        指定时间
     * @param   currentCalendar 当前时间
     */
    public static String getDateTimeDetail(Calendar calendar,Calendar currentCalendar) throws ServiceException
    {
        String timestr = "";
        int days = currentCalendar.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
        if(days == 0)
        {
            timestr = "今天" + DateUtil.formatDate(calendar.getTime(),DateUtil.HM_FORMAT);
        }
        else if(days == -1)
        {
            timestr = "明天 " + DateUtil.formatDate(calendar.getTime(),DateUtil.HM_FORMAT);
        }
        else if(days == 1)
        {
            timestr = "昨天 " + DateUtil.formatDate(calendar.getTime(),DateUtil.HM_FORMAT);
        }
        else
        {
            timestr = DateUtil.formatDate(calendar.getTime(),DateUtil.MDHM_FORMAT);
        }
        return timestr;
    }

    /**
     * 根据方案获取未截止方案对阵及投注选项
     * @author  mcdog
     * @param   schemeDto   方案对象
     */
    public List<Dto> getWjzSchemeTzxxList(Dto schemeDto) throws ServiceException
    {
        List<Dto> tzxxList = new ArrayList<Dto>();
        if("0".equals(schemeDto.getAsString("hideType")))
        {
            tzxxList.addAll(getYjzSchemeTzxxList(schemeDto));
        }
        else
        {
            Dto tzxxDto = null;
            if("2".equals(schemeDto.getAsString("hideType")))
            {
                tzxxDto = new BaseDto();
                tzxxDto.put("smodel",1);//显示模式(0-正常显示 1-加锁模式(加锁图标和加锁文字上下显示))
                tzxxDto.put("lockico",SysConfig.getHostStatic() + "/image/scheme/lock.sx.png");//设置锁图标地址
                tzxxDto.put("lockdesc","对阵及选项开赛后显示详细");//设置锁描述
                tzxxList.add(tzxxDto);
            }
            else
            {
                Map<String,Dto> schemeMatchMaps = new HashMap<String,Dto>();
                List<SchemeMatches> chemeMatchList = schemeMapper.querySchemeMatches(new BaseDto("schemeId",schemeDto.get("id")));//查询方案对阵信息
                if(chemeMatchList != null && chemeMatchList.size() > 0)
                {
                    if(LotteryUtils.isJczq(schemeDto.getAsString("lotteryId")))
                    {
                        for(SchemeMatches schemeMatches : chemeMatchList)
                        {
                            Dto matchDto = (Dto)memcached.get(LotteryConstants.jczqSchemeMatchPrefix_short + schemeMatches.getMatchCode());
                            if(matchDto == null)
                            {
                                //查询对阵信息
                                matchDto = new BaseDto();
                                MatchFootBall match = (MatchFootBall) memcached.get(LotteryConstants.jczqMatchPrefix + schemeMatches.getMatchCode());//从缓存中获取场次信息
                                if(match == null)
                                {
                                    match = footBallMapper.queryMatchFootBallByMatchCode(schemeMatches.getMatchCode());//从数据库中查询场次信息
                                    memcached.set(LotteryConstants.jczqMatchPrefix + schemeMatches.getMatchCode(),match,30);//在缓存中保存30秒
                                }
                                matchDto.put("mcode",match.getMatchCode());//设置场次号
                                matchDto.put("weekday",match.getWeekDay());//设置星期信息
                                matchDto.put("jcId",match.getJcId());//设置竞彩id
                                matchDto.put("hostName",match.getHostName());//设置主队名称
                                matchDto.put("guestName",match.getGuestName());//设置客队名称
                                matchDto.put("leagueName",match.getLeagueName());//设置联赛名称
                                matchDto.put("matchTime",DateUtil.formatDate(match.getMatchTime(),DateUtil.DEFAULT_DATE_TIME));//设置比赛时间
                                matchDto.put("halfScore",match.getHalfScore());//设置半场比分
                                matchDto.put("score",match.getScore());//设置全场比分
                                memcached.set(LotteryConstants.jczqSchemeMatchPrefix_short + schemeMatches.getMatchCode(),matchDto,30);//在缓存中保存30秒
                            }
                            tzxxDto = new BaseDto();
                            tzxxDto.put("smodel",0);//显示模式(0-正常显示 1-加锁模式(加锁图标和加锁文字上下显示))
                            tzxxDto.put("week",matchDto.getAsString("weekday") + matchDto.getAsString("jcId"));//设置编号/周信息
                            tzxxDto.put("lname",matchDto.getAsString("leagueName"));//设置赛事名称
                            tzxxDto.put("xxs","保密");//设置选项为保密

                            //设置对阵球队
                            String hname = matchDto.getAsString("hostName");
                            String gname = matchDto.getAsString("guestName");
                            hname = hname.length() <= 2? hname : hname.substring(0,3);
                            gname = gname.length() <= 2? gname : gname.substring(0,3);
                            tzxxDto.put("vsname",hname + " vs " + gname);
                            tzxxList.add(tzxxDto);
                        }
                    }
                    else
                    {
                        for(SchemeMatches schemeMatches : chemeMatchList)
                        {
                            Dto matchDto = (Dto)memcached.get(LotteryConstants.jclqSchemeMatchPrefix_short + schemeMatches.getMatchCode());
                            if(matchDto == null)
                            {
                                //查询对阵信息
                                matchDto = new BaseDto();
                                MatchBasketBall match = (MatchBasketBall) memcached.get(LotteryConstants.jclqMatchPrefix + schemeMatches.getMatchCode());//从缓存中获取场次信息
                                if(match == null)
                                {
                                    match = basketBallMapper.queryMatchBasketBallByMatchCode(schemeMatches.getMatchCode());//从数据库中查询场次信息
                                    memcached.set(LotteryConstants.jclqMatchPrefix + schemeMatches.getMatchCode(),match,30);//在缓存中保存30秒
                                }
                                matchDto.put("mcode",match.getMatchCode());//设置场次号
                                matchDto.put("weekday",match.getWeekDay());//设置星期信息
                                matchDto.put("jcId",match.getJcId());//设置竞彩id
                                matchDto.put("hostName",match.getHostName());//设置主队名称
                                matchDto.put("guestName",match.getGuestName());//设置客队名称
                                matchDto.put("leagueName",match.getLeagueName());//设置联赛名称
                                matchDto.put("matchTime",DateUtil.formatDate(match.getMatchTime(),DateUtil.DEFAULT_DATE_TIME));//设置比赛时间
                                matchDto.put("halfScore",match.getHalfScore());//设置半场比分
                                matchDto.put("score",match.getScore());//设置全场比分
                                memcached.set(LotteryConstants.jclqSchemeMatchPrefix_short + schemeMatches.getMatchCode(),matchDto,30);//在缓存中保存30秒
                            }
                            tzxxDto = new BaseDto();
                            tzxxDto.put("smodel",0);//显示模式(0-正常显示 1-加锁模式(加锁图标和加锁文字上下显示))
                            tzxxDto.put("week",matchDto.getAsString("weekday") + matchDto.getAsString("jcId"));//设置编号/周信息
                            tzxxDto.put("lname",matchDto.getAsString("leagueName"));//设置赛事名称
                            tzxxDto.put("xxs","保密");//设置选项为保密

                            //设置对阵球队
                            String hname = matchDto.getAsString("hostName");
                            String gname = matchDto.getAsString("guestName");
                            hname = hname.length() <= 2? hname : hname.substring(0,3);
                            gname = gname.length() <= 2? gname : gname.substring(0,3);
                            tzxxDto.put("vsname",gname + " vs " + hname);
                            tzxxList.add(tzxxDto);
                        }
                    }
                }
            }
        }
        return tzxxList;
    }

    /**
     * 根据方案获取已截止方案对阵及投注选项
     * @author  mcdog
     * @param   schemeDto   方案对象
     */
    public List<Dto> getYjzSchemeTzxxList(Dto schemeDto) throws ServiceException
    {
        LotteryUtils lotteryUtils = InitPlugin.getLotteryUtils(lotteryUtilsMap,schemeDto.getAsString("lotteryId"));//初始化彩种工具类
        boolean needSg = "1".equals(schemeDto.getAsString("needSg"));//是否需要赛果,true-需要
        Map<String,Dto> matchMap = new HashMap<String,Dto>();
        Map<String,Dto> schemeMatchMaps = new HashMap<String,Dto>();
        List<SchemeMatches> chemeMatchList = schemeMapper.querySchemeMatches(new BaseDto("schemeId",schemeDto.get("id")));//查询方案对阵信息
        if(chemeMatchList != null && chemeMatchList.size() > 0)
        {
            if(LotteryUtils.isJczq(schemeDto.getAsString("lotteryId")))
            {
                MatchFootBall match = null;
                for(SchemeMatches schemeMatches : chemeMatchList)
                {
                    Dto matchDto = (Dto)memcached.get(LotteryConstants.jczqSchemeMatchPrefix_short + schemeMatches.getMatchCode());
                    if(matchDto == null)
                    {
                        //查询对阵信息
                        matchDto = new BaseDto();
                        match = (MatchFootBall) memcached.get(LotteryConstants.jczqMatchPrefix + schemeMatches.getMatchCode());//从缓存中获取场次信息
                        if(match == null)
                        {
                            match = footBallMapper.queryMatchFootBallByMatchCode(schemeMatches.getMatchCode());//从数据库中查询场次信息
                            memcached.set(LotteryConstants.jczqMatchPrefix + schemeMatches.getMatchCode(),match,30);//在缓存中保存30秒
                        }
                        matchDto.put("mcode",match.getMatchCode());//设置场次号
                        matchDto.put("weekday",match.getWeekDay());//设置星期信息
                        matchDto.put("jcId",match.getJcId());//设置竞彩id
                        matchDto.put("hostName",match.getHostName());//设置主队名称
                        matchDto.put("guestName",match.getGuestName());//设置客队名称
                        matchDto.put("leagueName",match.getLeagueName());//设置联赛名称
                        matchDto.put("matchTime",DateUtil.formatDate(match.getMatchTime(),DateUtil.DEFAULT_DATE_TIME));//设置比赛时间
                        matchDto.put("halfScore",match.getHalfScore());//设置半场比分
                        matchDto.put("score",match.getScore());//设置全场比分
                        memcached.set(LotteryConstants.jczqSchemeMatchPrefix_short + schemeMatches.getMatchCode(),matchDto,30);//在缓存中保存30秒
                    }
                    matchMap.put(matchDto.getAsString("mcode"),matchDto);
                }
            }
            else
            {
                MatchBasketBall match = null;
                for(SchemeMatches schemeMatches : chemeMatchList)
                {
                    Dto matchDto = (Dto)memcached.get(LotteryConstants.jclqSchemeMatchPrefix_short + schemeMatches.getMatchCode());
                    if(matchDto == null)
                    {
                        //查询对阵信息
                        matchDto = new BaseDto();
                        match = (MatchBasketBall) memcached.get(LotteryConstants.jclqMatchPrefix + schemeMatches.getMatchCode());//从缓存中获取场次信息
                        if(match == null)
                        {
                            match = basketBallMapper.queryMatchBasketBallByMatchCode(schemeMatches.getMatchCode());//从数据库中查询场次信息
                            memcached.set(LotteryConstants.jclqMatchPrefix + schemeMatches.getMatchCode(),match,30);//在缓存中保存30秒
                        }
                        matchDto.put("mcode",match.getMatchCode());//设置场次号
                        matchDto.put("weekday",match.getWeekDay());//设置星期信息
                        matchDto.put("jcId",match.getJcId());//设置竞彩id
                        matchDto.put("hostName",match.getHostName());//设置主队名称
                        matchDto.put("guestName",match.getGuestName());//设置客队名称
                        matchDto.put("leagueName",match.getLeagueName());//设置联赛名称
                        matchDto.put("matchTime",DateUtil.formatDate(match.getMatchTime(),DateUtil.DEFAULT_DATE_TIME));//设置比赛时间
                        matchDto.put("halfScore",match.getHalfScore());//设置半场比分
                        matchDto.put("score",match.getScore());//设置全场比分
                        memcached.set(LotteryConstants.jclqSchemeMatchPrefix_short + schemeMatches.getMatchCode(),matchDto,30);//在缓存中保存30秒
                    }
                    matchMap.put(matchDto.getAsString("mcode"),matchDto);
                }
            }
        }
        //截取投注选项
        Calendar currentCalendar = Calendar.getInstance();
        List<Dto> tzxxList = new ArrayList<Dto>();
        int wksmatchCount = 0;//未开赛的场次数
        int yksmatchCount = 0;//已开赛的场次数
        Dto tzxxDto = null;
        Dto matchDto = null;
        String[] tzcontents = schemeDto.getAsString("schemeSpContent").split("\\|");//提示带sp的投注内容
        String tzprefix = tzcontents[0];//玩法前缀
        boolean ishh = tzprefix.indexOf(LotteryConstants.JCWF_PREFIX_HH) > -1? true : false;//是否混投
        for(String tzcodes : tzcontents[1].split("\\$"))
        {
            String[] codes = tzcodes.split(",");
            for(String xxcodes : codes)
            {
                //获取场次信息
                String[] ccxxs = xxcodes.split("\\=");
                if(ishh)
                {
                    ccxxs = xxcodes.split("\\>");
                }
                matchDto = matchMap.get(ccxxs[0]);//获取场次信息

                //如果对阵已开赛/不隐藏对阵及投注选项,则显示完整的对阵及选项
                if("0".equals(schemeDto.getAsString("hideType"))
                        || matchDto.getAsDate("matchTime",DateUtil.DEFAULT_DATE_TIME).before(currentCalendar.getTime()))
                {
                    yksmatchCount ++;
                    tzxxDto = new BaseDto();
                    tzxxDto.put("smodel",0);//显示模式(0-正常显示 1-加锁模式(加锁图标和加锁文字上下显示))
                    tzxxDto.put("week",matchDto.getAsString("weekday") + matchDto.getAsString("jcId"));//设置编号/周信息
                    tzxxDto.put("lname",matchDto.getAsString("leagueName"));//设置赛事名称

                    //设置对阵球队
                    String hname = matchDto.getAsString("hostName");
                    String gname = matchDto.getAsString("guestName");
                    hname = hname.length() <= 2? hname : hname.substring(0,3);
                    gname = gname.length() <= 2? gname : gname.substring(0,3);
                    tzxxDto.put("vsname",hname + " vs " + gname);

                    //设置投注选项
                    if(ishh)
                    {
                        String[] tzxxs = ccxxs[1].split("\\+");
                        String xxstr = "";
                        String xxcg = "";
                        for(String tzxx : tzxxs)
                        {
                            String[] xxs = tzxx.split("\\=");
                            String realwf = xxs[0];
                            if(realwf.indexOf("(") > -1)
                            {
                                realwf = realwf.substring(0,realwf.indexOf("("));
                            }
                            //选项排序
                            String[] tempxxs = xxs[1].split("\\/");
                            if(xxs[0].equals(LotteryConstants.JCWF_PREFIX_JQS)
                                    || xxs[0].equals(LotteryConstants.JCWF_PREFIX_CBF))
                            {
                                LotteryUtils.sortArrayWithSpByAsc(tempxxs);//进球数/猜比分玩法,选项升序排列
                            }
                            else
                            {
                                LotteryUtils.sortArrayWithSpByDesc(tempxxs);//其它玩法,选项降序排列
                            }
                            //拼接选项
                            int mzcs = 0;
                            for(String xx : tempxxs)
                            {
                                if(needSg)
                                {
                                    int mzstatus = lotteryUtils.getJcMzStatus(matchDto,xxs[0],xx.substring(0,xx.indexOf("(")));//获取命中状态
                                    if(mzstatus == 1)
                                    {
                                        xxstr += " <font color='#FF0000'>" + LotteryConstants.jcXxNameMaps.get(realwf + xx.substring(0,xx.indexOf("("))) + "</font>";
                                    }
                                    else
                                    {
                                        xxstr += " " + LotteryConstants.jcXxNameMaps.get(realwf + xx.substring(0,xx.indexOf("(")));
                                    }
                                    mzcs += mzstatus;
                                }
                                else
                                {
                                    xxstr += " " + LotteryConstants.jcXxNameMaps.get(realwf + xx.substring(0,xx.indexOf("(")));
                                }
                            }
                            //拼接赛果
                            if(needSg)
                            {
                                if(mzcs > 0)
                                {
                                    xxcg += " <font color='#FF0000'>" + lotteryUtils.getJcWfcg(matchDto,xxs[0]) + "</font>";
                                }
                                else
                                {
                                    xxcg += " " + lotteryUtils.getJcWfcg(matchDto,xxs[0]);
                                }
                            }
                        }
                        tzxxDto.put("xxs",xxstr.substring(1));//设置投注选项
                        if(needSg)
                        {
                            tzxxDto.put("xxcg",xxcg.length() > 0? xxcg.substring(1) : xxcg);//设置赛果
                        }
                    }
                    else
                    {
                        String[] xxs = ccxxs[1].split("\\/");
                        String fullwf = tzprefix;
                        if(ccxxs[0].indexOf("(") > -1)
                        {
                            fullwf += ccxxs[0].substring(ccxxs[0].indexOf("("),ccxxs[0].indexOf(")") + 1);
                        }

                        //选项排序
                        if(tzprefix.equals(LotteryConstants.JCWF_PREFIX_JQS)
                                || tzprefix.equals(LotteryConstants.JCWF_PREFIX_CBF))
                        {
                            LotteryUtils.sortArrayWithSpByAsc(xxs);//进球数/猜比分玩法,选项升序排列
                        }
                        else
                        {
                            LotteryUtils.sortArrayWithSpByDesc(xxs);//其它玩法,选项降序排列
                        }
                        //拼接选项
                        int mzcs = 0;
                        String xxstr = "";
                        String fullxx = null;
                        for(String xx : xxs)
                        {
                            fullxx = xx;
                            if(fullxx.indexOf("(") > -1)
                            {
                                fullxx = fullxx.substring(0,fullxx.indexOf("("));
                            }
                            if(needSg)
                            {
                                int mzstatus = lotteryUtils.getJcMzStatus(matchDto,fullwf,xx);
                                if(mzstatus == 1)
                                {
                                    xxstr += " <font color='#FF0000'>" + LotteryConstants.jcXxNameMaps.get(tzprefix + fullxx) + "</font>";
                                }
                                else
                                {
                                    xxstr += " " + LotteryConstants.jcXxNameMaps.get(tzprefix + fullxx);
                                }
                                mzcs += mzstatus;
                            }
                            else
                            {
                                xxstr += " " + LotteryConstants.jcXxNameMaps.get(tzprefix + fullxx);
                            }
                        }
                        //拼接赛果
                        String xxcg = "";
                        if(needSg)
                        {
                            if(mzcs > 0)
                            {
                                xxcg += " <font color='#FF0000'>" + lotteryUtils.getJcWfcg(matchDto,fullwf) + "</font>";
                            }
                            else
                            {
                                xxcg += " " + lotteryUtils.getJcWfcg(matchDto,fullwf);
                            }
                        }
                        tzxxDto.put("xxs",xxstr.substring(1));//设置投注选项
                        if(needSg)
                        {
                            tzxxDto.put("xxcg",xxcg.length() > 0? xxcg.substring(1) : xxcg);//设置赛果
                        }
                    }
                    tzxxList.add(tzxxDto);
                }
                //对阵未开赛,则根据投注内容隐藏模式显示对阵及选项
                else
                {
                    tzxxDto = new BaseDto();
                    if("2".equals(schemeDto.getAsString("hideType")))
                    {
                        if(wksmatchCount > 0)
                        {
                            continue;
                        }
                        wksmatchCount ++;
                        tzxxDto.put("smodel",2);//显示模式(0-正常显示 1-加锁模式(加锁图标和加锁文字上下显示) 2-加锁模式(加锁图标和加锁文字左右显示))
                        tzxxDto.put("lockico",SysConfig.getHostStatic() + "/image/scheme/lock.zy.png");//设置锁图标地址
                        tzxxDto.put("lockdesc",(yksmatchCount > 0? "其他" : "") + "对阵及选项开赛后显示详细");//设置锁描述
                        tzxxList.add(tzxxDto);
                    }
                    else
                    {
                        tzxxDto.put("smodel",0);//显示模式(0-正常显示 1-加锁模式(加锁图标和加锁文字上下显示))
                        tzxxDto.put("week",matchDto.getAsString("weekday") + matchDto.getAsString("jcId"));//设置编号/周信息
                        tzxxDto.put("lname",matchDto.getAsString("leagueName"));//设置赛事名称
                        tzxxDto.put("xxs","保密");//设置选项为保密

                        //设置对阵球队
                        String hname = matchDto.getAsString("hostName");
                        String gname = matchDto.getAsString("guestName");
                        hname = hname.length() <= 2? hname : hname.substring(0,3);
                        gname = gname.length() <= 2? gname : gname.substring(0,3);
                        tzxxDto.put("vsname",hname + " vs " + gname);
                        tzxxList.add(tzxxDto);
                    }
                }
            }
        }
        return tzxxList;
    }

    /**
     * 查询派奖审核方案(app)
     * @author	mcdog
     */
    public List<Dto> queryPjshSchemes(Dto params) throws Exception
    {
        /**
         * 用户身份校验
         */
        if(StringUtil.isEmpty(params.get("userId")))
        {
            throw new Exception("非法操作");
        }
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//获取操作用户
        if(user == null || user.getIsAdmin() != 1)
        {
            throw new Exception("非法操作");
        }
        //设置参数
        params.remove("userId");
        settingPageParams(params);
        params.put("schemeStatus","3");//只查询出票成功的方案
        params.put("minOpenStatus","1");//只查询已计奖的方案
        params.put("openStatus","2");//只查询已中奖的方案
        params.put("prizeStatus","0");//只查询未派奖的方案
        params.put("sorts","openTime desc,createTime desc");

        //查询并处理数据
        List<Dto> dataList = new ArrayList<Dto>();
        List<Dto> schemeList = schemeMapper.queryUserPtAndZhSchemes(params);
        if(schemeList != null && schemeList.size() > 0)
        {
            Dto dataDto = null;
            for(Dto scheme : schemeList)
            {
                dataDto = new BaseDto("id",scheme.get("id"));//方案id
                dataDto.put("lotteryName",scheme.get("lotteryName"));//彩种名称
                dataDto.put("nickName",scheme.get("nickName"));//用户
                dataDto.put("schemeMoney",scheme.get("schemeMoney"));//方案金额

                //设置奖金描述
                String awarddesc = scheme.getAsString("prizeTax");
                double jiajiangPrize = scheme.getAsDoubleValue("prizeSubjoinTax") + scheme.getAsDoubleValue("prizeSubjoinSiteTax");
                double rewardPrize = scheme.getAsDoubleValue("rewardPrize");
                boolean flag = jiajiangPrize > 0 || rewardPrize > 0;
                awarddesc += flag? "(" : "";
                awarddesc += jiajiangPrize > 0? "加奖" + jiajiangPrize : "";
                awarddesc += rewardPrize > 0? ("+" + (scheme.getAsInteger("schemeType") == 3? ("支付赏金" + rewardPrize) : ("收取赏金" + rewardPrize))) : "";
                awarddesc += flag? ")" : "";
                dataDto.put("awarddesc",awarddesc);//奖金描述

                //设置派奖状态描述
                String prizeStatus = scheme.getAsString("prizeStatus");
                dataDto.put("psdesc","0".equals(prizeStatus)? "未派奖" : ("1".equals(prizeStatus)? "派奖中" : "已派奖"));//派奖状态描述
                if("2".equals(scheme.getAsString("openStatus")) && "0".equals(scheme.getAsString("prizeStatus")))
                {
                    dataDto.put("pflag",1);//是否允许派奖,0-不允许 1-允许
                }
                else
                {
                    dataDto.put("pflag",0);
                }
                dataList.add(dataDto);
            }
        }
        return dataList;
    }

    /**
     * 查询派奖审核方案总计(app)
     * @author  mcdog
     */
    public Dto queryPjshSchemesCount(Dto params) throws Exception
    {
        //设置参数
        params.put("schemeStatus","3");//只查询出票成功的方案
        params.put("minOpenStatus","1");//只查询已计奖的方案
        params.put("openStatus","2");//只查询已中奖的方案
        params.put("prizeStatus","0");//只查询未派奖的方案
        return schemeMapper.queryUserPtAndZhSchemesCount(params);
    }

    /**
     * 方案确认派奖(app)
     * @author  mcdog
     */
    public synchronized int updateSchemeForQrPj(Dto params) throws Exception
    {
        /**
         * 用户身份校验
         */
        if(StringUtil.isEmpty(params.get("userId")))
        {
            throw new Exception("非法操作");
        }
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//获取操作用户
        if(user == null || user.getIsAdmin() != 1)
        {
            throw new Exception("非法操作");
        }
        //校验参数
        params.put("opfullName",(user.getNickName() + "(" + user.getMobile() + ")"));
        int count = 0;
        if(StringUtil.isEmpty(params.get("id")))
        {
            logger.error("[方案确认派奖(app)]参数校验不通过!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            throw new Exception("缺少必要参数");
        }
        //判断方案是否存在
        boolean iszh = "1".equals(params.getAsString("iszh"));//是否为追号
        String zhtext = iszh? "追号" : "";
        List<Dto> schemeList = new ArrayList<Dto>();
        if(iszh)
        {
            schemeList.addAll(schemeMapper.querySchemeZhs(params));//查询追号方案
        }
        else
        {
            schemeList.addAll(schemeMapper.queryUserSchemes(new BaseDto("id",params.get("id"))));//查询非追号方案
        }
        if(schemeList == null || schemeList.size() == 0)
        {
            logger.error("[方案确认派奖(app)]" + zhtext + "查询不到相关的方案!操作帐户=" + params.getAsString("opfullName") + ",方案id=" + params.getAsString("id"));
            throw new Exception("查询不到相关的方案信息");
        }
        //判断方案是否允许确认派奖(必须是已计奖且已中奖的方案才允许确认派奖)
        Dto schemeDto = schemeList.get(0);
        if(schemeDto.getAsInteger("openStatus") != 2
                || schemeDto.getAsInteger("prizeStatus") != 0)
        {
            logger.error("[方案确认派奖(app)]" + zhtext + "方案未满足派奖条件!操作帐户=" + params.getAsString("opfullName") + ",方案id=" + params.getAsString("id"));
            throw new Exception("方案未满足派奖条件!");
        }
        /**
         * 派奖
         */
        //计算方案的中奖奖金(税后)及加奖奖金(税后)
        Calendar current = Calendar.getInstance();//当前时间
        long userId = schemeDto.getAsLong("schemeUserId");//用户编号
        double prizeTax = schemeDto.getAsDoubleValue("prizeTax");//税后总奖金
        double prizeSubjoinTax = schemeDto.getAsDoubleValue("prizeSubjoinTax");//官方税后加奖奖金
        double prizeSubjoinSiteTax = schemeDto.getAsDoubleValue("prizeSubjoinSiteTax");//网站税后加奖奖金
        boolean isgd = iszh? false : schemeDto.getAsInteger("schemeType") == SchemeConstants.SCHEME_TYPE_GD? true :false;//是否为跟单方案,true表示是
        boolean issd = iszh? false : schemeDto.getAsInteger("schemeType") == SchemeConstants.SCHEME_TYPE_SD? true :false;//是否为神单方案,true表示是
        double rewardPrize = schemeDto.getAsDoubleValue("rewardPrize");//打赏金额(方案类型为神单时表示收取的总赏金,方案类型为跟单时表示需支付的赏金)
        double sprizeTax = prizeTax - prizeSubjoinTax - prizeSubjoinSiteTax - (issd? rewardPrize : (isgd? -rewardPrize : 0));//方案实际中奖金额(税后)

        //派奖并更新账户流水
        count = iszh? schemeMapper.zhschemeQrPj(params) : schemeMapper.schemeQrPj(params);//派奖
        if(count > 0)
        {
            user = userMapper.queryUserInfoById(userId);//查询用户信息
            double withDraw = 0;//可提现金额
            double unWithDraw = 0;//不可提现金额
            //普通用户
            if(user.getUserType() != UserConstants.USER_TYPE_VIRTUAL)
            {
                //判断是否有网站加奖
                withDraw = sprizeTax + prizeSubjoinTax + (issd? rewardPrize : (isgd? -rewardPrize : 0));//可提现=实际中奖奖金+官方加奖奖金 +/- 收取赏金/支付赏金
                if(prizeSubjoinSiteTax > 0)
                {
                    //网站加奖=根据活动是否支持提现
                    Dto addBonus = activityMapper.queryUserAddBonusInfo(schemeDto);
                    if(StringUtil.isNotEmpty(addBonus) && CalculationUtils.sub(addBonus.getAsDoubleValue("addPrizeTax"),
                            prizeSubjoinSiteTax) == 0 && addBonus.getAsInteger("isWithDraw") == 0)
                    {
                        unWithDraw += prizeSubjoinSiteTax;//不可提现
                    }
                    else
                    {
                        withDraw += prizeSubjoinSiteTax;
                    }
                    //网站加奖额度从活动出款账户出
                    Long outUserId = addBonus.getAsLong("outAccountUserId");
                    User outUser = userMapper.queryUserInfoById(outUserId);
                    if(StringUtil.isNotEmpty(outUser) && outUser.getUserType() == UserConstants.USER_TYPE_OUTMONEY)
                    {
                        UserAccount bua = userAccountMapper.queryUserAccountInfoByUserId(outUserId);
                        Dto outDto = new BaseDto("userId", outUserId);
                        outDto.put("tbalance", -prizeSubjoinSiteTax);//减去余额
                        outDto.put("offsetUnWithDraw", -prizeSubjoinSiteTax);//不可提现金额
                        userAccountMapper.updateUserAccount(outDto);//更新出款账户余额
                        //添加账户流水
                        UserDetail userDetail = new UserDetail();
                        userDetail.setUserId(outUserId);//账户id
                        userDetail.setInType(true);//流水类型为取出
                        userDetail.setChannelCode(PayConstants.CHANNEL_CODE_IN_SCHEMEJIAJIANG);//业务渠道为416(方案加奖奖金)
                        userDetail.setChannelDesc(MessageFormat.format(PayConstants.channelCodeMap.get(userDetail.getChannelCode()), new String[]{"网站加奖" + prizeSubjoinSiteTax + "元 ",""}));//业务渠道描述
                        userDetail.setMoney(prizeSubjoinSiteTax);//交易金额
                        userDetail.setLastBalance(bua.getBalance());//交易前账户余额
                        userDetail.setBalance(CalculationUtils.sub(bua.getBalance(), prizeSubjoinSiteTax));//交易后账户余额
                        userDetail.setLastWithDraw(bua.getWithDraw());//交易前账户可提现金额
                        userDetail.setWithDraw(bua.getWithDraw());//交易后账户可提现金额
                        userDetail.setLastUnWithDraw(bua.getUnWithDraw());//交易前账户不可提现金额
                        userDetail.setUnWithDraw(CalculationUtils.sub(bua.getUnWithDraw(), prizeSubjoinSiteTax));//交易后账户不可提现金额
                        userDetail.setClientFrom(schemeDto.getAsInteger("clientSource"));//客户端来源
                        userDetail.setBusinessId(schemeDto.getAsString("id"));//业务关联编号
                        userDetail.setCreateTime(current.getTime());//流水时间
                        userDetail.setRemark("手工派奖[加奖出款户]:"+schemeDto.getAsString("lotteryName") + "加奖[" + schemeDto.getAsString("schemeOrderId") + "]");
                        userDetailMapper.insertUserDetail(userDetail);//添加出款账户加奖奖金流水
                    }
                }
            }
            else
            {
                unWithDraw = prizeTax;//不可提现=总奖金
            }
            //更新用户账户余额及累计中奖金额
            UserAccount beforeUserAccount = userAccountMapper.queryUserAccountInfoByUserId(userId);//用户账户更新前信息
            Dto userAccountUpdateDto = new BaseDto("userId",userId);
            userAccountUpdateDto.put("tbalance",prizeTax);//余额
            userAccountUpdateDto.put("taward",prizeTax);//累计中奖金额
            userAccountUpdateDto.put("offsetWithDraw", withDraw);//可提现金额
            userAccountUpdateDto.put("offsetUnWithDraw", unWithDraw);//不可提现金额
            userAccountMapper.updateUserAccount(userAccountUpdateDto);//更新账户余额等信息
            UserAccount afterUserAccount = userAccountMapper.queryUserAccountInfoByUserId(userId);//用户账户更新后信息

            //添加用户账户中奖奖金流水
            UserDetail userDetail = new UserDetail();
            userDetail.setUserId(userId);//账户id
            userDetail.setInType(false);//流水类型为存入
            userDetail.setChannelCode(PayConstants.CHANNEL_CODE_IN_DRAWING);//业务渠道为400(用户中奖)
            userDetail.setChannelDesc(PayConstants.channelCodeMap.get(userDetail.getChannelCode()));//业务渠道描述
            userDetail.setMoney(sprizeTax);//交易金额
            userDetail.setLastBalance(beforeUserAccount.getBalance());//交易前账户余额
            userDetail.setBalance(beforeUserAccount.getBalance() + sprizeTax);//交易后账户余额(交易前余额 + 方案实际中奖金额)
            userDetail.setLastWithDraw(beforeUserAccount.getWithDraw());//交易前账户可提现金额
            userDetail.setWithDraw(beforeUserAccount.getWithDraw() + sprizeTax);//交易后账户可提现金额(交易前可提现金额 + 方案实际中奖金额)
            userDetail.setLastUnWithDraw(beforeUserAccount.getUnWithDraw());//交易前账户不可提现金额
            userDetail.setUnWithDraw(beforeUserAccount.getUnWithDraw());//交易后账户不可提现金额(中奖全可提,不可提现交易前不变)
            userDetail.setClientFrom(schemeDto.getAsInteger("clientSource"));//客户端来源
            userDetail.setBusinessId(schemeDto.getAsString("id"));//业务关联编号
            userDetail.setCreateTime(current.getTime());//流水时间
            //设置流水备注
            userDetail.setRemark(schemeDto.getAsString("lotteryName") + "中奖[" + schemeDto.getAsString("schemeOrderId") + "]");
            userDetailMapper.insertUserDetail(userDetail);//添加用户账户中奖奖金流水

            //如果有加奖奖金,则添加加奖奖金流水
            if(prizeSubjoinTax > 0 || prizeSubjoinSiteTax > 0)
            {
                double beforeBalance = userDetail.getBalance();//交易前用户账户余额
                double beforeWithDraw = userDetail.getWithDraw();//交易前用户可提现金额
                String gfjiajiang = prizeSubjoinTax > 0 ? ("官方加奖" + prizeSubjoinTax + "元 ") : "";
                String wzjiajiang = prizeSubjoinSiteTax > 0? ("网站加奖" + prizeSubjoinSiteTax + "元 ") : "";
                userDetail = new UserDetail();
                userDetail.setUserId(userId);//账户id
                userDetail.setInType(false);//流水类型为存入
                userDetail.setChannelCode(PayConstants.CHANNEL_CODE_IN_SCHEMEJIAJIANG);//业务渠道为416(方案加奖奖金)
                userDetail.setChannelDesc(MessageFormat.format(PayConstants.channelCodeMap.get(userDetail.getChannelCode()),new String[]{gfjiajiang,wzjiajiang}));//业务渠道描述
                userDetail.setMoney(prizeSubjoinTax + prizeSubjoinSiteTax);//交易金额
                userDetail.setLastBalance(beforeBalance);//交易前账户余额
                userDetail.setBalance(afterUserAccount.getBalance());//交易后账户余额
                userDetail.setLastWithDraw(beforeWithDraw);//交易前账户可提现金额
                userDetail.setWithDraw(afterUserAccount.getWithDraw());//交易后账户可提现金额
                userDetail.setLastUnWithDraw(beforeUserAccount.getUnWithDraw());//交易前账户不可提现金额
                userDetail.setUnWithDraw(afterUserAccount.getUnWithDraw());//交易后账户不可提现金额
                userDetail.setClientFrom(schemeDto.getAsInteger("clientSource"));//客户端来源
                userDetail.setBusinessId(schemeDto.getAsString("id"));//业务关联编号
                userDetail.setCreateTime(current.getTime());//流水时间
                userDetail.setRemark(schemeDto.getAsString("lotteryName") + "加奖[" + schemeDto.getAsString("schemeOrderId") + "]");
                userDetailMapper.insertUserDetail(userDetail);//添加用户账户加奖奖金流水
            }
            //如果方案类型为跟单或神单,则添加扣减赏金/收取赏金流水
            if(rewardPrize > 0 && (isgd || issd))
            {
                beforeUserAccount = userAccountMapper.queryUserAccountInfoByUserId(userId);//用户账户更新前信息
                double beforeBalance = userDetail.getBalance();//交易前用户账户余额
                double beforeWithDraw = userDetail.getWithDraw();//交易前用户可提现金额
                userDetail = new UserDetail();
                userDetail.setUserId(userId);//账户id
                userDetail.setInType(isgd? true : false);//流水类型(跟单时为取出,神单时为存入)
                userDetail.setChannelCode(isgd? PayConstants.CHANNEL_CODE_IN_ZFDASHANG : PayConstants.CHANNEL_CODE_IN_SQDASHANG);//业务渠道
                userDetail.setChannelDesc(PayConstants.channelCodeMap.get(userDetail.getChannelCode()));//业务渠道描述
                userDetail.setMoney(rewardPrize);//交易金额
                userDetail.setLastBalance(beforeBalance);//交易前账户余额
                userDetail.setBalance(beforeBalance + (isgd? -rewardPrize : rewardPrize));//交易后账户余额
                userDetail.setLastWithDraw(beforeWithDraw);//交易前账户可提现金额
                userDetail.setWithDraw(beforeWithDraw + (isgd? -rewardPrize : rewardPrize));//交易后账户可提现金额
                userDetail.setLastUnWithDraw(beforeUserAccount.getUnWithDraw());//交易前账户不可提现金额
                userDetail.setUnWithDraw(beforeUserAccount.getUnWithDraw());//交易后账户不可提现金额
                userDetail.setClientFrom(schemeDto.getAsInteger("clientSource"));//客户端来源
                userDetail.setBusinessId(schemeDto.getAsString("id"));//业务关联编号
                userDetail.setCreateTime(current.getTime());//流水时间
                userDetail.setRemark(schemeDto.getAsString("lotteryName") + userDetail.getChannelDesc() + "[" + schemeDto.getAsString("schemeOrderId") + "]");
                userDetailMapper.insertUserDetail(userDetail);//添加用户账户赏金流水
            }
            logger.info("[方案确认派奖(app)]派奖成功!操作帐户=" + params.getAsString("opfullName")
                    + ",派奖金额=" + prizeTax
                    + "(含加奖:"+(prizeSubjoinTax+prizeSubjoinSiteTax)+"),"
                    + zhtext
                    + "方案id=" + schemeDto.getAsString("id")
                    + ",所属用户编号=" + userId);
        }
        return count;
    }

}