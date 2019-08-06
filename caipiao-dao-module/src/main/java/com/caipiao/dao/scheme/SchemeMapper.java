package com.caipiao.dao.scheme;

import com.caipiao.domain.base.SchemeBean;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeFollow;
import com.caipiao.domain.scheme.SchemeMatches;
import com.caipiao.domain.scheme.SchemeZhuiHao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户方案模块功能接口定义
 * @author kouyi 2017-11-04
 */
public interface SchemeMapper
{
    /**
     * 查询用户方案(管理后台)
     * @author  mcdog
     */
    List<Dto> queryUserSchemes(Dto params);
    /**
     * 查询用户方案总计(管理后台)
     * @author  mcdog
     */
    Dto queryUserSchemesCount(Dto params);
    /**
     * 查询用户普通方案和追号方案(管理后台)
     * @author  mcdog
     */
    List<Dto> queryUserPtAndZhSchemes(Dto params);
    /**
     * 查询用户普通方案和追号方案总计(管理后台)
     * @author  mcdog
     */
    Dto queryUserPtAndZhSchemesCount(Dto params);
    /**
     * 保存方案
     * @author  mcdog
     */
    int saveScheme(SchemeBean schemeBean);
    /**
     * 保存追号方案
     * @author  mcdog
     */
    int saveZhScheme(SchemeBean schemeBean);
    /**
     * 保存方案对阵信息
     * @author  mcdog
     */
    int saveSchemeMatches(Map<String,Object> match);
    /**
     * 非渠道方案确认购买(付款)
     * @author  mcdog
     */
    void schemePay(Dto params);
    /**
     * 渠道方案确认购买(付款)
     * @author  mcdog
     */
    void schemePayChannel(Dto params);
    /**
     * 查询用户方案
     * @author  mcdog
     */
    List<Scheme> querySchemeInfo(Dto params);
    /**
     * 查询用户方案总记录条数
     * @author  mcdog
     */
    long querySchemeInfoCount(Dto params);

    /**
     * 查询支付成功的方案-进行拆票
     * @author  mcdog
     */
    List<Scheme> querySchemePaySuccess(@Param("schemeType") Integer schemeType);

    /**
     * 查询支付成功的追号方案-进行拆票
     * @author  mcdog
     */
    List<SchemeZhuiHao> queryZhuiHaoSchemePaySuccess();

    /**
     * 更新方案出票状态-拆票
     * @param scheme
     * @return
     */
    int updateSchemeTicketStatus(Scheme scheme);

    /**
     * 更新追号方案出票状态-拆票
     * @param scheme
     * @return
     */
    int updateZhuiHaoSchemeTicketStatus(Scheme scheme);

    /**
     * 根据订单号更新普通方案出票状态-拆票
     * @param scheme
     * @return
     */
    int updateSchemeStatusBySchemeOrderId(Scheme scheme);

    /**
     * 根据订单号更新追号方案出票状态-拆票
     * @param scheme
     * @return
     */
    int updateZhuiHaoSchemeStatusBySchemeOrderId(Scheme scheme);

    /**
     * 查询场次截止后还是出票中的方案列表-竞彩
     * @param lotteryId
     * @param matchCode
     * @return
     */
    List<Scheme> queryJcNoSuccessSchemeForEndTime(@Param("lotteryId") String lotteryId, @Param("matchCode") String matchCode);

    /**
     * 查询场次截止后还是出票中的方案列表-数字彩
     * @param lotteryId
     * @param period
     * @return
     */
    List<Scheme> querySzcNoSuccessSchemeForEndTime(@Param("lotteryId") String lotteryId, @Param("period") String period);

    /**
     * 查询竞彩足球方案场次信息
     * @author  mcdog
     * @param   sid     方案id
     */
    List<Dto> queryJczqSchemeMatchesBySid(Long sid);

    /**
     * 查询竞彩足球方案场次信息-出票管理
     * @author  mcdog
     * @param   schemeOrderId   方案id
     */
    List<Dto> queryJczqMatchesForTicket(String schemeOrderId);

    /**
     * 查询竞彩篮球方案场次信息
     * @author  mcdog
     * @param   sid     方案id
     */
    List<Dto> queryJclqSchemeMatchesBySid(Long sid);

    /**
     * 查询竞彩篮球方案场次信息-出票管理
     * @author  mcdog
     * @param   schemeOrderId   方案id
     */
    List<Dto> queryJclqMatchesForTicket(String schemeOrderId);

    /**
     * 查询方案追号信息
     * @author  mcdog
     * @param   params    查询参数对象(schemeId-方案id minSchemeStatus-追号方案最小状态值)
     */
    List<SchemeZhuiHao> querySchemeZhuihaoInfo(Dto params);
    /**
     * 查询追号方案信息
     * @author  mcdog
     * @param   params    查询参数对象
     */
    Dto queryZhuihaoSchemeInfo(Dto params);

    /**
     * 查询场次相关的所有方案
     * @param lotteryId
     * @param matchCode
     * @return
     */
    List<SchemeMatches> querySchemeForMatch(@Param("lotteryId") String lotteryId, @Param("matchCode") String matchCode);

    /**
     * 查询场次包含的所有订单对应的全部场次-竞彩足球
     * @param lotteryId
     * @param matchCode
     * @return
     */
    List<MatchFootBall> querySchemeForJczqMatch(@Param("lotteryId") String lotteryId, @Param("matchCode") String matchCode);

    /**
     * 查询场次包含的所有订单对应的全部场次-竞彩篮球
     * @param lotteryId
     * @param matchCode
     * @return
     */
    List<MatchBasketBall> querySchemeForJclqMatch(@Param("lotteryId") String lotteryId, @Param("matchCode") String matchCode);

    /**
     * 查询该方案包含的未审核的场次数量-竞彩足球
     * @param lotteryId
     * @param schemeId
     * @param state
     * @return
     */
    Integer queryNoAuditMatchCountForJczqScheme(@Param("lotteryId") String lotteryId, @Param("schemeId") Long schemeId, @Param("state") Integer state);

    /**
     * 查询该方案包含的未审核的场次数量-竞彩篮球
     * @param lotteryId
     * @param schemeId
     * @param state
     * @return
     */
    Integer queryNoAuditMatchCountForJclqScheme(@Param("lotteryId") String lotteryId, @Param("schemeId") Long schemeId, @Param("state") Integer state);

    /**
     * 根据方案编号查询方案信息
     * @param schemeOrderId
     * @return
     */
    Scheme querySchemeInfoBySchemeOrderId(String schemeOrderId);

    /**
     * 根据方案编号查询追号方案信息
     * @param schemeOrderId
     * @return
     */
    SchemeZhuiHao queryZhuihaoSchemeInfoBySchemeOrderId(String schemeOrderId);

    /**
     * 更新方案计奖状态与奖金
     * @param scheme
     * @return
     */
    int updateSchemeStatusPrize(Scheme scheme);

    /**
     * 更新数字彩开奖号码至用户订单号-非追号订单
     * @param drawNumber
     * @param period
     * @return
     */
    int updateSchemeDrawNumber(@Param("drawNumber") String drawNumber, @Param("period") String period, @Param("lotteryId") String lotteryId);

    /**
     * 更新数字彩开奖号码至用户订单号-追号订单
     * @param drawNumber
     * @param period
     * @return
     */
    int updateZhuiHaoSchemeDrawNumber(@Param("drawNumber") String drawNumber, @Param("period") String period, @Param("lotteryId") String lotteryId);

    /**
     * 查询奖金低于maxPrize的待派奖方案列表-竞彩
     * @param lottId
     * @param maxPrize
     * @return
     * @throws Exception
     */
    List<Scheme> queryAutoSendMoneySchemeList(@Param("lotteryId") String lottId, @Param("maxPrize") int maxPrize);

    /**
     * 查询奖金低于maxPrize的待派奖方案列表-数字彩
     * @param period
     * @param maxPrize
     * @return
     * @throws Exception
     */
    List<Scheme> queryAutoSendMoneySzcSchemeList(@Param("period") String period, @Param("maxPrize") int maxPrize);


    /**
     * 更新追号方案计奖状态与奖金
     * @param scheme
     * @return
     */
    int updateZhuihaoSchemeStatusPrize(SchemeZhuiHao scheme);

    /**
     * 更新订单返点状态-非追号订单
     * @param id
     * @param backStatus
     * @return
     */
    int updateSchemeBackStatus(@Param("id") Long id, @Param("backStatus") Integer backStatus);

    /**
     * 更新订单返点状态-追号订单
     * @param id
     * @param backStatus
     * @return
     */
    int updateZhuiHaoSchemeBackStatus(@Param("id") Long id, @Param("backStatus") Integer backStatus);

    /**
     * 根据方案编号查询方案信息
     * @param id
     * @return
     */
    Scheme querySchemeInfoById(Long id);

    /**
     * 根据主键编号查询未预约成功的追号方案列表
     * @param schemeId
     * @param schemeStatus
     * @return
     * @throws Exception
     */
    List<SchemeZhuiHao> queryZhuihaoSchemeInfoById(@Param("schemeId") Long schemeId, @Param("schemeStatus") Integer schemeStatus);

    /**
     * 查询出票中的方案列表
     * @return
     * @throws Exception
     */
    List<Scheme> queryOutTicketingSchemeList();

    /**
     * 查询需要返利的方案列表
     * @param backStatus
     * @return
     * @throws Exception
     */
    List<Scheme> queryRebateSchemeList(Integer backStatus);

    /**
     * 查询方案追号信息(管理后台)
     * @author  mcdog
     * @param   params    查询参数对象(schemeId-方案id minSchemeStatus-追号方案最小状态值)
     */
    List<Dto> querySchemeZhs(Dto params);
    /**
     * 方案撤单(管理后台)
     * @author  mcdog
     */
    int cancelScheme(Dto params);
    /**
     * 追号方案撤单(管理后台)
     * @author  mcdog
     */
    int cancelSchemeZh(Dto params);
    /**
     * 方案大单审核
     * @author  mcdog
     */
    int auditBigOrder(Dto params);
    /**
     * 方案出票成功
     * @author  mcdog
     */
    int schemeCpcg(Dto params);
    /**
     * 追号方案出票成功
     * @author  mcdog
     */
    int zhschemeCpcg(Dto params);
    /**
     * 方案重新出票
     * @author  mcdog
     */
    int schemeCxcp(Dto params);
    /**
     * 追号方案重新出票
     * @author  mcdog
     */
    int zhschemeCxcp(Dto params);
    /**
     * 方案确认派奖
     * @author  mcdog
     */
    int schemeQrPj(Dto params);
    /**
     * 追号方案确认派奖
     * @author  mcdog
     */
    int zhschemeQrPj(Dto params);

    /**
     * 渠道查询订单出票状态
     * @return
     * @throws Exception
     */
    List<Dto> queryChannelSchemeList(Dto params);

    /**
     * 渠道查询订单中奖状态
     * @return
     * @throws Exception
     */
    List<Dto> queryChannelAwardSchemeList(Dto params);

    /**
     * 查询需要进行出票通知的渠道订单
     * @author kouyi
     */
    List<Dto> queryChannelSchemeToNotify(String appId);

    /**
     * 更新渠道订单出票通知次数
     * @param id
     * @param number
     * @return
     */
    int updateSchemeNotifyNumber(@Param("id") Long id, @Param("number") Integer number);

    /**
     * 查询竞彩足球订单相关的场次数据
     * @author kouyi
     */
    List<SchemeMatches> querySchemeInfoByZqMatches(Long schemeId);

    /**
     * 查询竞彩篮球订单相关的场次数据
     * @author kouyi
     */
    List<SchemeMatches> querySchemeInfoByLqMatches(Long schemeId);

    /**
     * 查询追号方案信息
     * @author  mcdog
     */
    Dto queryZhuihaoScheme(Dto params);
    /**
     * 更新追号方案(总方案)的已完成期次数
     * @author  mcdog
     */
    int updateSchemeForDonePeriod(Dto params);

    /**
     * 查询跟单关联列表
     * @param follow
     * @return
     */
    List<SchemeFollow> querySchemeFollowList(SchemeFollow follow);

    /**
     * 根据跟单方案编号查询跟单关联记录
     * @param schemeId
     * @return
     */
    SchemeFollow querySchemeFollowInfo(String schemeId);

    /**
     * 根据ID-更新跟单方案关联信息
     * @param follow
     * @return
     */
    int updateSchemeFollow(SchemeFollow follow);

    /**
     * 根据发单人订单编号-更新跟单方案计奖状态
     * @param senderSchemeId
     * @return
     */
    int updateSchemeFollowBySendSchemeOrderId(@Param("senderSchemeId") String senderSchemeId);

    /**
     * 更新跟单方案出票状态
     * @param followSchemeId
     * @param schemeStatus
     * @return
     */
    int updateSchemeStatusFollow(@Param("followSchemeId") String followSchemeId, @Param("schemeStatus") Integer schemeStatus);

    /**
     * 更新订单表-发单人已跟单金额
     * @param followSchemeId
     * @return
     */
    int updateSchemeFollowMoney(@Param("followSchemeId") String followSchemeId);

    /**
     * 保存跟单方案关联信息
     * @param follow
     * @return
     */
    int saveSchemeFollow(SchemeFollow follow);

    /**
     * 根据比赛场次查询中奖神单收获打赏列表
     * @param lotteryId
     * @param matchCode
     * @return
     */
    List<SchemeFollow> querySchemeFollowListByRewards(@Param("lotteryId") String lotteryId, @Param("matchCode") String matchCode);

    /**
     * 更新神单方案中奖金额和描述
     * @param scheme
     * @return
     */
    int updateSchemeFollowPrize(Scheme scheme);

    /**
     * 根据用户编号查询一天中未弹框的中奖订单信息
     * @param schemeUserId
     * @return
     */
    Scheme queryDayWinSchemeInfoByUserId(@Param("schemeUserId") Long schemeUserId);

    /**
     * 更新中奖订单弹框状态
     * @param schemeUserId
     * @return
     */
    int updateSchemeWinPopupStatus(@Param("schemeUserId") Long schemeUserId);

    /**
     * 查询用户单日已发神单的数量
     * @author  mcdog
     */
    int queryUserSdCountOfDay(Dto params);
    /**
     * 查询用户方案投注内容相同的神单
     * @author  mcdog
     */
    int queryUserSdCountOfSameCotent(Dto params);
    /**
     * 设置方案为神单(晒单)
     * @author  mcdog
     */
    int updateSchemeForSd(Dto params);
    /**
     * 查询方案场次信息
     * @author  mcdog
     */
    List<SchemeMatches> querySchemeMatches(Dto params);
    /**
     * 查询方案对阵即时比分信息
     * @author  mcdog
     */
    Dto querySchemeMatchBfInfo(Dto params);
    /**
     * 查询用户神单方案信息
     * @author  mcdog
     */
    List<Dto> querySdSchemeInfo(Dto params);
    /**
     * 查询用户神单方案信息总记录数
     * @author  mcdog
     */
    long querySdSchemeInfoCount(Dto params);
    /**
     * 查询用户神单方案信息
     * @author  mcdog
     */
    List<Dto> queryUserSdSchemeInfo(Dto params);
    /**
     * 查询用户神单方案信息总记录数
     * @author  mcdog
     */
    long queryUserSdSchemeInfoCount(Dto params);
    /**
     * 查询神单统计榜单
     * @author  mcdog
     */
    List<Dto> querySdTj(Dto params);
    /**
     * 查询神单统计榜单总记录数
     * @author  mcdog
     */
    long querySdTjCount(Dto params);
    /**
     * 保存跟单方案
     * @author  mcdog
     */
    int saveFollowScheme(SchemeBean schemeBean);
    /**
     * 保存神单跟单
     * @author  mcdog
     */
    int saveSdFollow(Dto params);
    /**
     * 更新神单
     * @author  mcdog
     */
    int updateSdScheme(Dto params);
    /**
     * 查询神单用户
     * @author  mcdog
     */
    List<Dto> querySdUser(Dto params);
    /**
     * 查询神单跟买记录
     * @author  mcdog
     */
    List<Dto> querySdFollow(Dto params);
    /**
     * 查询用户关注
     * @author  mcdog
     */
    List<Dto> queryUserFollow(Dto params);
    /**
     * 查询用户关注总记录数
     * @author  mcdog
     */
    long queryUserFollowCount(Dto params);
    /**
     * 查询用户粉丝
     * @author  mcdog
     */
    List<Dto> queryUserFans(Dto params);
    /**
     * 查询用户粉丝总记录数
     * @author  mcdog
     */
    long queryUserFansCount(Dto params);

    /**
     * 审核错误方案回退-数字彩
     * @param params
     * @return
     */
    int updateErrorReAwardBySzc(Dto params);

    /**
     * 审核错误方案回退-竞彩
     * @param params
     * @return
     */
    int updateErrorReAwardSchemeJc(Dto params);

    /**
     * 审核错误票回退-竞彩
     * @param params
     * @return
     */
    int updateErrorReAwardTicketJc(Dto params);

    /**
     * 审核错误普通方案回退-数字彩
     * @param params
     * @return
     */
    int updateErrorReAwardSchemeSzc(Dto params);

    /**
     * 审核错误追号方案回退-数字彩
     * @param params
     * @return
     */
    int updateErrorReAwardSchemeSzcZhuiHao(Dto params);

    /**
     * 审核错误普通票回退-数字彩
     * @param params
     * @return
     */
    int updateErrorReAwardTicketSzc(Dto params);

    /**
     * 审核错误追号票回退-数字彩
     * @param params
     * @return
     */
    int updateErrorReAwardTicketSzcZhuiHao(Dto params);
}