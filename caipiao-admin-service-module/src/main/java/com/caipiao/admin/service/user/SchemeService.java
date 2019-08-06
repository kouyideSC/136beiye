package com.caipiao.admin.service.user;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.scheme.SchemeUtils;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ActivityMapper;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.match.MatchGyjMapper;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.dao.ticket.TicketMapper;
import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserCouponMapper;
import com.caipiao.dao.user.UserDetailMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.match.GyjMatch;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeZhuiHao;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;

/**
 * 用户方案-服务类
 */
@Service("schemeService")
@Transactional
public class SchemeService
{
    private static final Logger logger = LoggerFactory.getLogger(SchemeService.class);
    private HashMap<String,LotteryUtils> lotteryUtilsMap = new HashMap<String, LotteryUtils>();//彩种工具类集合
    @Autowired
    private SchemeMapper schemeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserDetailMapper userDetailMapper;
    @Autowired
    private UserCouponMapper userCouponMapper;
    @Autowired
    private PeriodMapper periodMapper;
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private MatchGyjMapper matchGyjMapper;

    /**
     * 查询用户方案
     * @author	sjq
     */
    public List<Dto> queryUserSchemes(Dto params)
    {
        //如果有理论奖金排序,则先做特殊处理
        if(StringUtil.isNotEmpty(params.get("sorts")) && params.getAsString("sorts").indexOf("theoryPrize") > -1)
        {
            String newsorts = "";
            String[] sorts = params.getAsString("sorts").split(",");
            for(String sort : sorts)
            {
                if(sort.indexOf("theoryPrize") > -1)
                {
                    newsorts += "," + "convert(replace(substring_index(" + (sort.substring(0,sort.indexOf(" "))) + ",'-',-1),'元',''),signed)" + sort.substring(sort.indexOf(" "));
                }
                else
                {
                    newsorts += "," + sort;
                }
            }
            params.put("sorts",newsorts.substring(1));
        }
        if("1".equals(params.getAsString("containsZh")))
        {
            return schemeMapper.queryUserPtAndZhSchemes(params);//查询普通方案和追号方案
        }
        else if("1".equals(params.getAsString("onlyZh")))
        {
            List<Dto> schemeList = new ArrayList<Dto>();
            schemeList.add(schemeMapper.queryZhuihaoSchemeInfo(params));
            return schemeList;
        }
        else
        {
            return schemeMapper.queryUserSchemes(params);
        }
    }

    /**
     * 查询用户方案总计
     * @author	sjq
     */
    public Dto queryUserSchemesCount(Dto params)
    {
        if("1".equals(params.getAsString("containsZh")))
        {
            return schemeMapper.queryUserPtAndZhSchemesCount(params);//查询普通方案和追号方案总计数据
        }
        else
        {
            return schemeMapper.queryUserSchemesCount(params);
        }
    }

    /**
     * 查询优惠券信息
     * @author	sjq
     */
    public List<Dto> queryCoupons(Dto params)
    {
        return userCouponMapper.queryCoupons(params);
    }

    /**
     * 查询用户优惠券信息
     * @author	sjq
     */
    public List<Dto> queryUserCoupons(Dto params)
    {
        return userCouponMapper.queryUserCoupons(params);
    }

    /**
     * 方案撤单
     * @author	sjq
     */
    public synchronized int updateSchemeForCancel(Dto params) throws Exception
    {
        int count = 0;

        //校验参数
        if(StringUtil.isEmpty(params.get("id")))
        {
            logger.error("[方案撤单]参数校验不通过!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","缺少必要参数");
            return 0;
        }
        //方案是否存在
        boolean iszh = "1".equals(params.getAsString("iszh"));//是否为追号
        String zhtext = iszh? "追号" : "";
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
            logger.error("[方案撤单]查询不到相关的方案!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","查询不到相关的方案信息!");
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
            logger.error("[方案撤单]" + zhtext + "方案未满足撤单条件!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","方案未满足撤单条件!");
            return 0;
        }
        //如果方案状态为出票中,则需要根据方案编号去查询该方案的所有出票情况,只有所有票张的状态都为系统撤单/出票失败/待提票时,才允许撤单
        /*if(schemeStatus == SchemeConstants.SCHEME_STATUS_CPZ
                && !"1".equals(params.getAsString("iscontinue")))
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
                        logger.error("[方案撤单]" + zhtext + "方案部分票单已不允许撤单!操作帐户=" + params.getAsString("opfullName")
                                + "票单id=" + ticketDto.getAsString("id") + ",票单状态描述=" + ticketDto.getAsString("ticketDesc"));
                        params.put("dcode",1001);
                        params.put("dmsg","方案未满足撤单条件!原因=部分票单已不满足撤单条件"
                                + ",票单id=" + ticketDto.getAsString("id") + ",票单状态描述=" + ticketDto.getAsString("ticketDesc"));
                        params.put("schemeOrderId",schemeDto.get("schemeOrderId"));
                        return 0;
                    }
                }
            }
        }*/
        //撤单
        params.put("schemeStatus",SchemeConstants.SCHEME_STATUS_CDCG);
        if(schemeStatus == SchemeConstants.SCHEME_STATUS_TKF)
        {
            params.put("schemeStatusDesc","截止未出票-撤单成功");
        }
        else if(schemeStatus == SchemeConstants.SCHEME_STATUS_ETF)
        {
            params.put("schemeStatusDesc","出票失败-撤单成功");
        }
        else
        {
            params.put("schemeStatusDesc",SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_CDCG));
        }
        if(iszh)
        {
            count = schemeMapper.cancelSchemeZh(params);//追号方案撤单
            if(count > 0)
            {
                Dto zhuihaoScheme = schemeMapper.queryZhuihaoScheme(new BaseDto("id",params.get("id")));
                if(zhuihaoScheme != null)
                {
                    //更新追号方案(总方案)的已完成期次数
                    schemeMapper.updateSchemeForDonePeriod(new BaseDto("id",zhuihaoScheme.get("schemeId")));
                }
                else
                {
                    throw new RuntimeException("追号方案查询不到对应的总方案信息!");
                }
            }
        }
        else
        {
            count = schemeMapper.cancelScheme(params);//非追号方案撤单
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
                //userAccountDto.put("tconsume",-schemeMoney);//方案所增加的累计消费金额
                userAccountDto.put("offsetWithDraw",schemeDto.getAsDouble("offsetWithDraw"));//方案抵消的可提现金额
                userAccountDto.put("offsetUnWithDraw",schemeDto.getAsDouble("offsetUnWithDraw"));//方案抵消的不可提现金额
                userAccountMapper.updateUserAccount(userAccountDto);

                //添加账户流水
                afterUserAccount = userAccountMapper.queryUserAccountInfoByUserId(schemeDto.getAsLong("schemeUserId"));//查询用户账户更新后信息
                UserDetail userDetail = new UserDetail();
                userDetail.setUserId(userId);//账户id
                userDetail.setInType(false);//流水类型为存入
                userDetail.setChannelCode(PayConstants.CHANNEL_CODE_IN_YYFAIL);//业务渠道为407(预约失败退款)
                userDetail.setChannelDesc(PayConstants.channelCodeMap.get(userDetail.getChannelCode()));//业务渠道描述
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
                userDetail.setRemark(schemeDto.getAsString("lotteryName") + "退款");//设置备注
                userDetailMapper.insertUserDetail(userDetail);//添加流水
            }

            /**
             * 如果该方案(非追号)有用优惠券支付,则退还用户优惠券并添加优惠券退还流水
             */
            if(!(schemeType == null || schemeType == SchemeConstants.SCHEME_TYPE_ZH))
            {
                Dto couponQueryDto = new BaseDto("userId",userId);
                couponQueryDto.put("useStatus","2");
                couponQueryDto.put("schemeId",schemeDto.get("id"));
                List<Dto> couponDataList = userCouponMapper.queryUserCoupons(couponQueryDto);
                if(couponDataList != null && couponDataList.size() > 0)
                {
                    Dto couponData = couponDataList.get(0);
                    Dto couponUpdateDto = new BaseDto("id",couponData.get("cuid"));
                    couponUpdateDto.put("status","1");

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
                    userDetail.setChannelDesc(PayConstants.channelCodeMap.get(userDetail.getChannelCode()));//业务渠道描述
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
     * 审核大单
     * @author	sjq
     */
    public synchronized int updateBigOrderForAudit(Dto params)
    {
        //校验参数
        if(StringUtil.isEmpty(params.get("id")))
        {
            logger.error("[审核大单]参数校验不通过!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","缺少必要参数");
            return 0;
        }
        //判断方案是否存在
        List<Dto> schemeList = schemeMapper.queryUserSchemes(new BaseDto("id",params.get("id")));
        if(schemeList == null || schemeList.size() == 0)
        {
            logger.error("[审核大单]查询不到相关的方案!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","查询不到相关的方案信息!");
            return 0;
        }
        //判断方案是否允许满足大单审核条件
        Dto schemeDto = schemeList.get(0);
        if(schemeDto.getAsInteger("bigOrderStatus") != 2)
        {
            logger.error("[审核大单]方案未满足大单审核条件!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","方案未满足大单审核条件!");
            return 0;
        }
        return schemeMapper.auditBigOrder(params);
    }

    /**
     * 退款
     * @author	sjq
     */
    public synchronized int updateSchemeForTk(Dto params) throws Exception
    {
        return updateSchemeForCancel(params);
    }

    /**
     * 设置方案出票成功
     * @author	sjq
     */
    public synchronized int updateSchemeForCpcg(Dto params)
    {
        //校验参数
        if(StringUtil.isEmpty(params.get("id")))
        {
            logger.error("[设置方案出票成功]参数校验不通过!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","缺少必要参数");
            return 0;
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
            logger.error("[设置方案出票成功]查询不到相关的方案!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","查询不到相关的方案信息!");
            return 0;
        }
        //判断方案是否允许设置为出票成功
        Dto schemeDto = schemeList.get(0);
        /*if(schemeDto.getAsInteger("schemeStatus") == SchemeConstants.SCHEME_STATUS_CPZ
                && !"1".equals(params.getAsString("iscontinue")))
        {
            logger.error("[设置方案出票成功]" + zhtext + "方案未满足出票成功条件!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dcode",1001);
            params.put("dmsg","方案未满足出票成功条件!原因=方案当前状态为出票中");
            params.put("schemeOrderId",schemeDto.get("schemeOrderId"));
            return 0;
        }*/
        params.put("schemeStatus",SchemeConstants.SCHEME_STATUS_CPCG);
        params.put("schemeStatusDesc","后台方案退款-手动出票成功");
        if(iszh)
        {
            return schemeMapper.zhschemeCpcg(params);//追号方案出票成功
        }
        else
        {
            return schemeMapper.schemeCpcg(params);//非追号出票成功
        }
    }

    /**
     * 重新出票
     * @author	sjq
     */
    public synchronized int updateSchemeForCxcp(Dto params) throws Exception
    {
        //校验参数
        if(StringUtil.isEmpty(params.get("id")))
        {
            logger.error("[重新出票]参数校验不通过!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","缺少必要参数");
            return 0;
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
            logger.error("[重新出票]查询不到相关的方案!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","查询不到相关的方案信息!");
            return 0;
        }
        //判断方案是否允许重新出票
        Dto schemeDto = schemeList.get(0);
        /*if(schemeDto.getAsInteger("schemeStatus") == 2)
        {
            logger.error("[重新出票]" + zhtext + "方案未满足重新出票条件!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","方案未满足重新出票条件!");
            params.put("schemeOrderId",schemeDto.get("schemeOrderId"));
            return 0;
        }*/

        //将旧票设置为废弃票
        ticketMapper.updateOutTicketStatusForSchemeId(SchemeConstants.TICKET_STATUS_REOUT,
                SchemeConstants.ticketStatusMap.get(SchemeConstants.TICKET_STATUS_REOUT), schemeDto.getAsString("schemeOrderId"));

        //重新出票
        params.put("schemeStatus",SchemeConstants.SCHEME_STATUS_ZFCG);
        params.put("schemeStatusDesc",SchemeConstants.schemeStatusMap.get(SchemeConstants.SCHEME_STATUS_ZFCG));
        if(iszh)
        {
            return schemeMapper.schemeCxcp(params);//追号方案重新出票
        }
        else
        {
            return schemeMapper.schemeCxcp(params);//非追号方案重新出票
        }
    }

    /**
     * 后台确认派奖
     * @author sjq
     */
    public synchronized int updateSchemeForQrPj(Dto params) throws Exception
    {
        //校验参数
        int count = 0;
        if(StringUtil.isEmpty(params.get("id")))
        {
            logger.error("[确认派奖]参数校验不通过!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","缺少必要参数");
            return 0;
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
            logger.error("[确认派奖]" + zhtext + "查询不到相关的方案!操作帐户=" + params.getAsString("opfullName") + ",方案id=" + params.getAsString("id"));
            params.put("dmsg","查询不到相关的方案信息!");
            return 0;
        }
        //判断方案是否允许确认派奖(必须是已计奖且已中奖的方案才允许确认派奖)
        Dto schemeDto = schemeList.get(0);
        if(schemeDto.getAsInteger("openStatus") != 2
                || schemeDto.getAsInteger("prizeStatus") != 0)
        {
            logger.error("[确认派奖]" + zhtext + "方案未满足派奖条件!操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("dmsg","方案未满足派奖条件!");
            return 0;
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
        if(iszh){
            count = schemeMapper.zhschemeQrPj(params);//更新追号方案状态为已派奖
        } else {
            count = schemeMapper.schemeQrPj(params);//更新方案状态为已派奖
        }
        //派奖成功-操作账户流水
        if(count > 0) {
            User user = userMapper.queryUserInfoById(userId);//查询用户信息
            //计算可/不可提现金额
            double withDraw = 0, unWithDraw = 0;
            if(user.getUserType() != UserConstants.USER_TYPE_VIRTUAL) {//普通用户
                withDraw = sprizeTax + prizeSubjoinTax + (issd? rewardPrize : (isgd? -rewardPrize : 0));//可提现=实际中奖奖金+官方加奖奖金 +/- 收取赏金/支付赏金
                if(prizeSubjoinSiteTax > 0) {//有网站加奖
                    //网站加奖=根据活动是否支持提现
                    Dto addBonus = activityMapper.queryUserAddBonusInfo(schemeDto);
                    if(StringUtil.isNotEmpty(addBonus) && CalculationUtils.sub(addBonus.getAsDoubleValue("addPrizeTax"),
                            prizeSubjoinSiteTax) == 0 && addBonus.getAsInteger("isWithDraw") == 0) {//不可提现
                        unWithDraw += prizeSubjoinSiteTax;
                    } else {
                        withDraw += prizeSubjoinSiteTax;
                    }
                    //网站加奖额度从活动出款账户出
                    Long outUserId = addBonus.getAsLong("outAccountUserId");
                    User outUser = userMapper.queryUserInfoById(outUserId);
                    if(StringUtil.isNotEmpty(outUser) && outUser.getUserType() == UserConstants.USER_TYPE_OUTMONEY) {
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
            } else {
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
            userDetail.setUnWithDraw(beforeUserAccount.getUnWithDraw());//交易后账户不可提现金额(中奖全可提，不可提现交易前不变)
            userDetail.setClientFrom(schemeDto.getAsInteger("clientSource"));//客户端来源
            userDetail.setBusinessId(schemeDto.getAsString("id"));//业务关联编号
            userDetail.setCreateTime(current.getTime());//流水时间
            //设置流水备注
            userDetail.setRemark(schemeDto.getAsString("lotteryName") + "中奖");
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
            logger.info("[后台确认派奖]派奖成功!操作帐户=" + params.getAsString("opfullName")
                    + ",派奖金额=" + prizeTax
                    + "(含加奖:"+(prizeSubjoinTax+prizeSubjoinSiteTax)+"),"
                    + zhtext
                    + "方案id=" + schemeDto.getAsString("id")
                    + ",所属用户编号=" + userId);
        }
        return count;
    }

    /**
     * 查询方案追号信息
     * @author  mcdog
     */
    public List<SchemeZhuiHao> querySchemeZhuihaoInfo(Dto params)
    {
        return schemeMapper.querySchemeZhuihaoInfo(params);
    }

    /**
     * 设置方案详情
     * @author  mcdog
     * @param   scheme      方案对象(源数据)
     * @param   schemeDto   方案详情对象(详情保存在该对象中)
     */
    public void settingSchemeDetail(Dto scheme,Dto schemeDto)
    {
        String lotteryId = scheme.getAsString("lotteryId");//彩种id
        if(LotteryUtils.isGyj(lotteryId))
        {
            settingGyjSchemeDetail(scheme,schemeDto);//设置冠亚军方案详情
        }
        else if(LotteryUtils.isJc(lotteryId))
        {
            settingJcSchemeDetail(scheme,schemeDto);//设置竞彩方案详情
        }
        else if(LotteryUtils.isZC(lotteryId))
        {
            settingZcSchemeDetail(scheme,schemeDto);//设置足彩方案详情
        }
        else if(LotteryUtils.isSzc(lotteryId))
        {
            settingSzcSchemeDetail(scheme,schemeDto);//设置数字彩方案详情
        }
    }

    /**
     * 设置冠亚军方案详情
     * @author  mcdog
     * @param   scheme      方案对象(源数据)
     * @param   schemeDto   方案详情对象(详情保存在该对象中)
     */
    private void settingGyjSchemeDetail(Dto scheme,Dto schemeDto)
    {
        //获取彩种工具类
        String lotteryId = scheme.getAsString("lotteryId");//彩种id
        LotteryUtils lotteryUtils = InitPlugin.getLotteryUtils(lotteryUtilsMap,lotteryId);
        if(lotteryUtils == null)
        {
            logger.error("[设置冠亚军方案详情]获取不到彩种工具类,彩种编号:" + lotteryId);
            return;
        }

        //设置过关方式
        schemeDto.put("ggfs","单关");//设置过关方式

        //设置方案场次信息
        Dto params = new BaseDto();
        params.put("period",scheme.getAsString("period"));
        params.put("lotteryId",lotteryId);
        Map<String,Dto> matchMaps = new HashMap<String,Dto>();
        List<Dto> matchList = matchGyjMapper.queryGyjMatchInfos(params);//查询对阵信息
        if(matchList != null && matchList.size() > 0)
        {
            //封装对阵信息
            for(Dto matchDto : matchList)
            {
                matchMaps.put(matchDto.getAsString("matchCode"),matchDto);
            }
        }

        //设置投注选项
        schemeDto.put("tzxxs",lotteryUtils.getJcTzxxList(scheme,matchMaps,params));//设置投注选项信息
    }

    /**
     * 设置竞彩方案详情
     * @author  mcdog
     * @param   scheme      方案对象(源数据)
     * @param   schemeDto   方案详情对象(详情保存在该对象中)
     */
    private void settingJcSchemeDetail(Dto scheme,Dto schemeDto)
    {
        //获取彩种工具类
        String lotteryId = scheme.getAsString("lotteryId");//彩种id
        LotteryUtils lotteryUtils = InitPlugin.getLotteryUtils(lotteryUtilsMap,lotteryId);
        if(lotteryUtils == null)
        {
            logger.error("[设置竞彩方案详情]获取不到彩种工具类,彩种编号:" + lotteryId);
            return;
        }
        //设置过关方式
        String[] tzspContent = scheme.getAsString("schemeSpContent").split("\\|");//提取带sp的投注内容
        boolean iszh = scheme.getAsString("schemeContent").indexOf(";") > -1? true : false;//是否为组合投注
        schemeDto.put("ggfs",iszh? "组合投注" : (tzspContent[2].replace("1*1","单关").replace("*","串").replace(",","，")));//设置过关方式

        //设置方案场次信息
        long sid = scheme.getAsLong("id");//方案id
        List<Dto> schemeMatchList = new ArrayList<Dto>();
        if(LotteryUtils.isJczq(lotteryId))
        {
            schemeDto.put("jctype",1);//设置竞彩类型 1-竞彩足球 2-竞彩篮球
            schemeMatchList.addAll(schemeMapper.queryJczqSchemeMatchesBySid(sid));
        }
        else if(LotteryUtils.isJclq(lotteryId))
        {
            schemeDto.put("jctype",2);
            schemeMatchList.addAll(schemeMapper.queryJclqSchemeMatchesBySid(sid));
        }
        //设置方案对阵Map
        Map<String,Dto> schemeMatchMaps = SchemeUtils.getSchemeMatchMaps(schemeMatchList);

        //设置投注选项
        Dto params = new BaseDto();
        schemeDto.put("tzxxs",lotteryUtils.getJcTzxxList(scheme,schemeMatchMaps,params));//设置投注选项信息

        //设置优化明细
        if(scheme.getAsInteger("schemeType") == SchemeConstants.SCHEME_TYPE_YH)
        {
            schemeDto.put("ggfs",schemeDto.getAsString("ggfs") + "<font color='#FF0000'>（奖金优化）</font>");
            schemeDto.put("yhinfos",lotteryUtils.getYhinfos(scheme,schemeMatchMaps,params));//设置设置优化明细信息
        }
    }

    /**
     * 设置足彩方案详情
     * @author  mcdog
     * @param   scheme      方案对象(源数据)
     * @param   schemeDto   方案详情对象(详情保存在该对象中)
     */
    private void settingZcSchemeDetail(Dto scheme,Dto schemeDto)
    {
        //获取彩种工具类
        String lotteryId = scheme.getAsString("lotteryId");//彩种id
        LotteryUtils lotteryUtils = InitPlugin.getLotteryUtils(lotteryUtilsMap,lotteryId);
        if(lotteryUtils == null)
        {
            logger.error("[设置竞彩方案详情]获取不到彩种工具类,彩种编号:" + lotteryId);
            return;
        }
        //设置场次信息和投注选项
        Period period = periodMapper.queryPeriodByPerod(lotteryId,scheme.getAsString("period"));//查询期次
        Map<String,Object> matchMaps = JsonUtil.jsonToMap(period.getMatches());//提取场次信息
        schemeDto.put("tzxxs",lotteryUtils.getZcTzxxList(scheme,matchMaps));//设置场次信息和投注选项
    }

    /**
     * 设置数字彩方案详情
     * @author  mcdog
     * @param   scheme      方案对象(源数据)
     * @param   schemeDto   方案详情对象(详情保存在该对象中)
     */
    private void settingSzcSchemeDetail(Dto scheme,Dto schemeDto)
    {
        //获取彩种工具类
        String lotteryId = scheme.getAsString("lotteryId");//彩种id
        LotteryUtils lotteryUtils = InitPlugin.getLotteryUtils(lotteryUtilsMap,lotteryId);
        if(lotteryUtils == null)
        {
            logger.error("[设置竞彩方案详情]获取不到彩种工具类,彩种编号:" + lotteryId);
            return;
        }
        //设置场次信息和投注选项
        schemeDto.put("tzxxs",lotteryUtils.getSzcTzxxList(scheme));//设置场次信息和投注选项

        /**
         * 追号方案,则设置方案追号期次/开奖/中奖/派奖等信息
         */
        if(scheme.getAsInteger("schemeType") == SchemeConstants.SCHEME_TYPE_ZH)
        {
            //查询方案追号信息
            Period period = periodMapper.queryPeriodByPerod(lotteryId,scheme.getAsString("period"));//查询期次
            Dto zhQueryDto = new BaseDto("schemeId",scheme.get("id"));
            List<SchemeZhuiHao> zhuiHaoList = schemeMapper.querySchemeZhuihaoInfo(zhQueryDto);

            //设置方案追号信息
            int periodSum = scheme.getAsInteger("periodSum");//总期次
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
                    zhuiHaoDto.put("scode",zhuiHaoScheme.getSchemeOrderId());//设置方案编号
                    zhuiHaoDto.put("multiple",zhuiHaoScheme.getSchemeMultiple());//设置追期方案倍数
                    zhuiHaoDto.put("money",zhuiHaoScheme.getSchemeMoney());//设置追期方案金额
                    zhuiHaoDto.put("status",zhuiHaoScheme.getSchemeStatus());//设置追期方案状态
                    zhuiHaoDto.put("sdesc",zhuiHaoScheme.getSchemeStatusDesc());//设置追期方案状态描述
                    zhuiHaoDto.put("kcode",StringUtil.isEmpty(zhuiHaoScheme.getDrawNumber())? "" : zhuiHaoScheme.getDrawNumber());//设置开奖号

                    //未开奖,慢频设置开奖描述为预计开奖时间
                    period = periodMapper.queryPeriodByPerod(lotteryId,zhuiHaoScheme.getPeriod());//查询期次信息
                    if(zhuiHaoScheme.getOpenStatus() == 0)
                    {
                        if(LotteryUtils.isMp(lotteryId))
                        {
                            zhuiHaoDto.put("ktime","预计" + DateUtil.formatDate(period.getDrawNumberTime(),DateUtil.DEFAULT_DATE_TIME_SECOND) + "开奖");
                        }
                        else
                        {
                            zhuiHaoDto.put("ktime","等待开奖");
                        }
                    }
                    //已开奖则取期次的开奖时间
                    else
                    {
                        zhuiHaoDto.put("ktime",DateUtil.formatDate(period.getDrawNumberTime(),DateUtil.DEFAULT_DATE_TIME));
                    }
                    zhuiHaoDto.put("zstatus",zhuiHaoScheme.getOpenStatus());//设置中奖状态 0-未计奖 1-未中奖 2-已中奖
                    zhuiHaoDto.put("zmoney",zhuiHaoScheme.getPrizeTax());//设置中奖奖金

                    //设置加奖奖金
                    double gfjj = zhuiHaoScheme.getPrizeSubjoinTax() == null? 0d : zhuiHaoScheme.getPrizeSubjoinTax();//官方税后加奖奖金
                    double wzjj = zhuiHaoScheme.getPrizeSubjoinSiteTax() == null? 0d : zhuiHaoScheme.getPrizeSubjoinSiteTax();//网站税后加奖奖金
                    zhuiHaoDto.put("jmoney",gfjj + wzjj);//设置加奖奖金
                    zhuiHaoDto.put("pstatus",zhuiHaoScheme.getPrizeStatus());//设置派奖状态
                    zhuiHaoDto.put("ptime",DateUtil.formatDate(zhuiHaoScheme.getPrizeTime(),DateUtil.DEFAULT_DATE_TIME));//设置派奖时间
                    zhList.add(zhuiHaoDto);
                }
            }
            schemeDto.put("zhinfos",zhList);//设置方案追号信息
        }
    }
}