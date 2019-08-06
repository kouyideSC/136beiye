package com.caipiao.dao.user;

import com.caipiao.domain.base.UserBean;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserRebate;
import com.caipiao.domain.user.UserRebateDetail;
import com.caipiao.domain.vo.UserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户模块功能接口定义
 * @author kouyi 2017-09-21
 */
public interface UserMapper {
    /**
     * 用户注册
     * @param user
     * @return
     */
    void insertUserRegister(User user) throws Exception;

    /**
     * 根据用户编号查询用户信息
     * @param userId
     * @return
     */
    User queryUserInfoById(Long userId) throws Exception;

    /**
     * 根据用户编号查询完整用户信息-包括余额和token
     * @param userId
     * @return
     */
    UserVo queryUserInfoBalaceById(Long userId) throws Exception;

    /**
     * 查询用户昵称是否存在
     * @param nickName
     * @return
     */
    Integer queryUserNickNameIsExists(String nickName) throws Exception;

    /**
     * 查询用户邀请码是否存在
     * @param code
     * @return
     */
    List<User> queryUserCodeIsExists(String code) throws Exception;

    /**
     * 根据手机号查询用户信息
     * @param mobile
     * @return
     */
    User queryUserInfoByMobile(String mobile) throws Exception;
    /**
     * 查询微信/QQ联合登录用户
     * @author  mcdog
     */
    User queryWeixinQqUserInfo(Dto params) throws Exception;

    /**
     * 根据用户编号查询返利余额
     * @param userId
     * @return
     * @throws Exception
     */
    UserAccount queryUserBackBalanceById(Long userId) throws Exception;

    /**
     * 查询虚拟用户编号列表
     * @return
     * @throws Exception
     */
    List<Integer> queryUserListByUserType() throws Exception;

    /**
     * 查询黑名单用户编号列表
     * @return
     * @throws Exception
     */
    List<Integer> queryUserListByBlackType() throws Exception;

    /**
     * 更新用户登录信息
     * @param ip
     * @throws Exception
     */
    void updateUserLoginInfo(@Param("id")Long id, @Param("ip")String ip, @Param("device")String device) throws Exception;

    /**
     * 更新重置用户密码
     * @param password
     * @param device
     * @throws Exception
     */
    void updateResetUserPassword(@Param("password")String password, @Param("device")String device, @Param("id")Long id) throws Exception;


    /**
     * 查询用户列表（后台管理）
     * @author kouyi
     */
    List<Dto> queryUserList(Dto params);

    /**
     * 查询用户列表-总条数（后台管理）
     * @author kouyi
     */
    int queryUserListCount(Dto params);

    /**
     * 更新用户信息（后台管理）
     * @author kouyi
     */
    int updateUserInfoByAdmin(Dto params);
    /**
     * 更新用户头像信息
     * @author  mcdog
     */
    int updateUserAvatar(Dto params);
    /**
     * 更新用户银行卡信息
     * @author  mcdog
     */
    int updateUserBank(Dto params);
    /**
     * 更新用户实名信息
     * @author  mcdog
     */
    int updateUserIdentity(Dto params);
    /**
     * 更新用户用户联合登录标识信息
     * @author  mcdog
     */
    int updateUserOpenId(Dto params);

    /**
     * 用户日报表数据统计
     * @param date
     * @return
     */
    int userDayDateStatis(String date);

    /**
     * 查询用户日报表数据（后台管理）
     * @author kouyi
     */
    List<Dto> queryUserDayStatis(Dto params);

    /**
     * 查询用户日报表数据-总条数（后台管理）
     * @author kouyi
     */
    int queryUserDayStatisCount(Dto params);

    /**
     * 用户返点比例查询
     * @param userId
     * @return
     */
    List<Dto> queryUserLotteryRebateList(Long userId);

    /**
     * 查询销售下级用户列表（销售管理）
     * @author kouyi
     */
    List<Dto> querySaleLowerUserList(Dto params);

    /**
     * 查询销售下级用户列表-总条数（后台管理）
     * @author kouyi
     */
    int querySaleLowerUserCount(Dto params);

    /**
     * 保存用户对应彩种的返点信息
     * @param rebate
     * @return
     */
    int insertUserRebate(UserRebate rebate);

    /**
     * 根据用户编号查询是否设置返点比例
     * @param userId
     * @return
     */
    Integer queryIsSetNumberForUserId(Long userId);

    /**
     * 更新用户对应彩种的返点信息
     * @param rebate
     * @return
     */
    int updateUserRebate(UserRebate rebate);

    /**
     * 根据手机号查询销售的最大返点范围
     * @param mobile
     * @param lotteryId
     * @return
     */
    UserRebate querySaleMaxRateRange(@Param("mobile") String mobile, @Param("lotteryId") String lotteryId,@Param("isSale") String isSale);

    /**
     * 将用户设置为代理员
     * @param params
     * @return
     */
    int updateSetUserProxy(Dto params);

    /**
     * 销售下属用户转出到新的销售员名下
     * @param params
     * @return
     */
    int updateSaleUserChange(Dto params);

    /**
     * 为用户或代理绑定上级归属
     * @param params
     * @return
     */
    int updateUserHigherUser(Dto params);

    /**
     * 查询销售代理用户列表（销售管理）
     * @author kouyi
     */
    List<Dto> querySaleProxyUserList(Dto params);

    /**
     * 查询销售代理用户列表-总条数（后台管理）
     * @author kouyi
     */
    int querySaleProxyUserCount(Dto params);

    /**
     * 查询销售人员列表（销售管理）
     * @author kouyi
     */
    List<Dto> querySaleUserList(Dto params);

    /**
     * 查询销售人员列表-总条数（后台管理）
     * @author kouyi
     */
    int querySaleUserCount(Dto params);

    /**
     * 更新用户累计消费金额
     * @param userId
     * @param money
     * @return
     */
    int updateUserConsume(@Param("userId") Long userId, @Param("money") Double money);

    /**
     * 更新用户返利金额
     * @param userId
     * @param money
     * @return
     */
    int updateUserBack(@Param("userId") Long userId, @Param("money") Double money);


    /**
     * 根据用户编号和彩种查询返点比例
     * @param userId
     * @param lotteryId
     * @return
     */
    UserRebate queryUserRebateListForLotteryId(@Param("userId") Long userId, @Param("lotteryId") String lotteryId);

    /**
     * 保存用户返利明细
     * @param detail
     * @return
     */
    int insertUserRebateDetail(UserRebateDetail detail);

    /**
     * 查询用户返点明细
     * @param params
     * @return
     */
    List<Dto> queryUserBackDetail(Dto params);

    /**
     * 查询用户返点明细-总条数（后台管理）
     * @author kouyi
     */
    int queryUserBackDetailCount(Dto params);

    /**
     * 查询销售当月和历史总销量
     * @param mobile
     * @return
     */
    Dto queryUserSaleSumMoney(String mobile);

    /**
     * 查询销售月销量明细
     * @param mobile
     * @return
     */
    List<Dto> queryUserSaleMoneyDetail(String mobile);

    /**
     * 用户返利账户余额转出
     * @param money
     * @return
     */
    int updateUserBackBalance(@Param("userId") Long userId, @Param("money") Double money);

    /**
     * 查询用户返利列表（后台管理）
     * @author kouyi
     */
    List<Dto> queryUserFanliList(Dto params);

    /**
     * 查询用户返利数据总数（后台管理）
     * @author kouyi
     */
    List<Dto> queryUserFanliListCount(Dto params);

    /**
     * 查询销售自购的各彩种总销量以及各彩种提成比例
     * @param params
     * @return
     */
    List<Dto> querySellOwnTotalMoney(Dto params);

    /**
     * 查询销售下级用户的各彩种总销量以及各彩种提成比例
     * @param params
     * @return
     */
    List<Dto> querySellLowerUserTotalMoney(Dto params);

    /**
     * 查询销售下级代理及代理下的用户的各彩种总销量以及各彩种提成比例
     * @param params
     * @return
     */
    List<Dto> querySellProxyUserTotalMoney(Dto params);

    /**
     * 根据用户编号查询用户对应彩种的返点设置信息
     * @author  kouyi
     * @param   userId      用户编号
     */
    List<UserRebate> queryUserRebateList(@Param("userId") Long userId);

    /**
     * 查询某个销售总销量
     * @param params
     * @return
     */
    Double querySellTotalMoney(Dto params);

    /**
     * 保存销售月提成数据
     * @param params
     * @return
     */
    int insertUserSellMoney(Dto params);

    /**
     * 查询销售月提成列表（后台管理）
     * @author kouyi
     */
    List<Dto> queryUserMonthCommList(Dto params);

    /**
     * 查询销售月提成数据总数（后台管理）
     * @author kouyi
     */
    List<Dto> queryUserMonthCommListCount(Dto params);
    /**
     * 关注用户
     * @author  mcdog
     */
    int insertFollowUser(Dto params);
    /**
     * 取消关注用户
     * @author  mcdog
     */
    int deleteFollowUser(Dto params);
    /**
     * 更新用户关注数/粉丝数
     * @author  mcdog
     */
    int updateUserFollowFans(Dto params);
}