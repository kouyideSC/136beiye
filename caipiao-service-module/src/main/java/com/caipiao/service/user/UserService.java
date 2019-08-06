package com.caipiao.service.user;

import com.caipiao.common.constants.*;
import com.caipiao.common.encrypt.AuthSign;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.identity.jd.JdUtils;
import com.caipiao.common.identity.juku.JuKuUtils;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.pay.qq.QqUtils;
import com.caipiao.common.pay.weixin.WeixinUtils;
import com.caipiao.common.user.UserUtils;
import com.caipiao.common.util.*;
import com.caipiao.dao.check.CheckMapper;
import com.caipiao.dao.common.*;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserDetailMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.dao.user.UserTokenMapper;
import com.caipiao.dao.user.*;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.SchemeBean;
import com.caipiao.domain.base.UserBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.*;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserDetail;
import com.caipiao.domain.user.UserToken;
import com.caipiao.domain.user.*;
import com.caipiao.domain.vo.BankInfoVo;
import com.caipiao.domain.vo.UserVo;
import com.caipiao.memcache.MemCached;
import com.caipiao.redis.RedisSingle;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import com.sun.xml.internal.rngom.parse.host.Base;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.*;

import static com.caipiao.common.encrypt.RSA.decryptByPrivateKey;

/**
 * 用户相关业务处理服务
 * Created by kouyi on 2017/9/22.
 */
@Service("userService")
public class UserService
{
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String weixinlogin_redis_prefix = "weixinlogin_redis_prefix_";//微信联合登录缓存前缀
    private static final String qqlogin_redis_prefix = "qqlogin_redis_prefix";//QQ联合登录缓存前缀

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserTokenMapper tokenDao;
    @Autowired
    private UserCookieMapper cookieMapper;
    @Autowired
    private MessageCodeMapper messageDao;
    @Autowired
    private UserPayMapper userPayMapper;
    @Autowired
    private SchemeMapper schemeMapper;
    @Autowired
    private UserDetailMapper userDetailMapper;
    @Autowired
    private BankMapper bankMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private UserCouponMapper userCouponMapper;
    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;
    @Autowired
    private RedisSingle redis;
    @Autowired
    private ActivityCouponReissueMapper couponReissueMapper;
    @Autowired
    private UserFollowMapper userFollowMapper;

    /**
     * 查询用户基本信息
     * @param bean
     * @return
     */
    public void queryUserInfo(UserBean bean, ResultBean result) throws ServiceException, Exception {
        try {
            //用户编号
            if (StringUtil.isEmpty(bean.getUserId())) {
                result.setErrorCode(ErrorCode_API.ERROR_100001);
                return;
            }

            UserVo userInfo = userMapper.queryUserInfoBalaceById(bean.getUserId());
            if (StringUtil.isEmpty(userInfo)) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110007);
                return;
            }
            if (userInfo.getStatus().intValue() != 1) {//用户状态
                result.setErrorCode(ErrorCode_API.ERROR_USER_110006);
                return;
            }

            Integer number = userMapper.queryIsSetNumberForUserId(userInfo.getId());
            if((userInfo.getIsSale() == UserConstants.USER_PROXY_GENERAL || userInfo.getIsSale() == UserConstants.USER_STATUS_AGENT)
                    && number > 0) {
                userInfo.setShowRebate(1);
            }
            userInfo.setMobile(UserUtils.getSafeMobile(userInfo.getMobile()));
            //设置用户头像地址
            if(StringUtil.isEmpty(userInfo.getAvatar()))
            {
                userInfo.setAvatar(SysConfig.getHostStatic() + SysConfig.getString("USER_DEFAULT_AVATAR"));
            }
            else
            {
                if(!userInfo.getAvatar().startsWith("http"))
                {
                    userInfo.setAvatar(SysConfig.getHostStatic() + userInfo.getAvatar());
                }
            }
            result.setData(userInfo);
        } catch (Exception e) {
            logger.error("[查询用户基本信息异常] userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询用户返利账户余额
     * @param bean
     * @return
     */
    public void queryUserBackBalance(UserBean bean, ResultBean result) throws ServiceException, Exception {
        try {
            //用户编号
            if (StringUtil.isEmpty(bean.getUserId())) {
                result.setErrorCode(ErrorCode_API.ERROR_100001);
                return;
            }

            UserAccount account = userMapper.queryUserBackBalanceById(bean.getUserId());
            if (StringUtil.isEmpty(account)) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110007);
                return;
            }

            Dto dataDto = new BaseDto();
            dataDto.put("backBalance", CalculationUtils.formatOdds(account.getBalanceBack()));
            result.setData(dataDto);
        } catch (Exception e) {
            logger.error("[查询用户返利账户余额异常] userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 用户返利转出到账户余额
     * @param bean
     * @return
     */
    public void updateUserBackRollOut(UserBean bean, ResultBean result) throws ServiceException {
        try {
            //用户编号
            if (StringUtil.isEmpty(bean.getUserId())) {
                result.setErrorCode(ErrorCode_API.ERROR_100001);
                return;
            }

            UserAccount account = userMapper.queryUserBackBalanceById(bean.getUserId());
            if (StringUtil.isEmpty(account)) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110007);
                return;
            }
            if(account.getBalanceBack() <= 0) {
                result.setErrorCode(ErrorCode_API.ERR_USER_110019);
                return;
            }

            userMapper.updateUserBackBalance(bean.getUserId(), account.getBalanceBack());//更新余额
            UserRebateDetail rebateDetail = new UserRebateDetail();
            rebateDetail.setType(1);
            rebateDetail.setUserId(bean.getUserId());
            rebateDetail.setSchemeUserId(bean.getUserId());
            rebateDetail.setLastBalanceRebate(account.getBalanceBack());
            rebateDetail.setCurrentRebateMoney(account.getBalanceBack());
            Double balanceRebate = CalculationUtils.sub(account.getBalanceBack(), account.getBalanceBack());
            rebateDetail.setBalanceRebate(balanceRebate);
            userMapper.insertUserRebateDetail(rebateDetail);//保存明细

            UserDetail userDetail = new UserDetail();
            userDetail.setUserId(bean.getUserId());//账户id
            userDetail.setInType(false);//流水类型为存入
            userDetail.setChannelCode(PayConstants.CHANNEL_CODE_IN_YONGJIN);//业务渠道为413(佣金转入)
            userDetail.setChannelDesc(PayConstants.channelCodeMap.get(userDetail.getChannelCode()));//业务渠道描述
            userDetail.setMoney(account.getBalanceBack());//交易金额
            userDetail.setLastBalance(account.getBalance());//交易前账户余额
            userDetail.setBalance(CalculationUtils.add(account.getBalance(), account.getBalanceBack()));//交易后账户余额(交易前余额 + 方案实际中奖金额)
            userDetail.setLastWithDraw(account.getWithDraw());//交易前账户可提现金额
            userDetail.setWithDraw(CalculationUtils.add(account.getWithDraw(), account.getBalanceBack()));//交易后账户可提现金额(交易前可提现金额 + 方案实际中奖金额)
            userDetail.setLastUnWithDraw(account.getUnWithDraw());//交易前账户不可提现金额
            userDetail.setUnWithDraw(account.getUnWithDraw());//交易后账户不可提现金额
            userDetail.setClientFrom(KeyConstants.loginUserMap.get(bean.getAppId()));//客户端来源
            userDetail.setBusinessId("" + rebateDetail.getId());//业务关联编号
            userDetail.setRemark("返利转入");
            userDetail.setCreateTime(new Date());//流水时间
            userDetailMapper.insertUserDetail(userDetail);//添加用户账户佣金转入流水
        } catch (Exception e) {
            logger.error("[用户返利转出到账户余额异常] userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询用户邀请码
     * @param bean
     * @return
     */
    public void getUserCode(UserBean bean, ResultBean result) throws ServiceException, Exception {
        try {
            //用户编号
            if (StringUtil.isEmpty(bean.getUserId())) {
                result.setErrorCode(ErrorCode_API.ERROR_100001);
                return;
            }

            UserVo userInfo = userMapper.queryUserInfoBalaceById(bean.getUserId());
            if (StringUtil.isEmpty(userInfo)) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110007);
                return;
            }
            if (userInfo.getStatus().intValue() != 1) {//用户状态
                result.setErrorCode(ErrorCode_API.ERROR_USER_110006);
                return;
            }
            if(userInfo.getIsSale() == UserConstants.USER_PROXY_GENERAL) {//普通用户无邀请码
                userInfo.setCode("");
            } else {
                if(StringUtil.isEmpty(userInfo.getCode())) {
                    result.setErrorCode(ErrorCode_API.ERR_USER_110020);
                }
            }

            Dto dataDto = new BaseDto();
            dataDto.put("code", userInfo.getCode());
            result.setData(dataDto);
        } catch (Exception e) {
            logger.error("[查询用户邀请码异常] userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询虚拟用户编号列表（出票）
     * @return
     */
    public List<Integer> queryUserListByUserType() throws ServiceException, Exception {
        try {
            List<Integer> userList = new ArrayList<>();
            List<Integer> virUserList = userMapper.queryUserListByUserType();//虚拟用户
            if(StringUtil.isNotEmpty(virUserList)) {
                userList.addAll(virUserList);
            }
            return userList;
        } catch (Exception e) {
            logger.error("[根据用户类型查询用户列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询黑名单用户编号列表（出票）
     * @return
     */
    public List<Integer> queryUserListByBlackType() throws ServiceException, Exception {
        try {
            List<Integer> userList = new ArrayList<>();
            List<Integer> blackUserList = userMapper.queryUserListByBlackType();//黑名单用户
            if(StringUtil.isNotEmpty(blackUserList)) {
                userList.addAll(blackUserList);
            }
            return userList;
        } catch (Exception e) {
            logger.error("[根据用户类型查询用户列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 普通用户注册业务方法
     * @param bean
     * @return
     */
    public synchronized void saveUser(UserBean bean, ResultBean result) throws ServiceException {
        try {
            //手机号验证
            if(!UserUtils.checkMobile(bean.getMobile())) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110001);
                return;
            }
            //手机号是否已存在
            if(StringUtil.isNotEmpty(userMapper.queryUserInfoByMobile(bean.getMobile()))) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110002);
                return;
            }

            //验证登录密码
            bean.setPassword(decryptByPrivateKey(bean.getPassword()));
            if(!UserUtils.checkPassword(bean.getPassword())) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110003);
                return;
            }

            //验证验证码
            MessageCode message = new MessageCode();
            message.setMobile(bean.getMobile());
            message.setType(1);
            message.setContent(bean.getContent());
            message.setIsAuth(1);
            long mid = messageDao.checkMessageCode(message).longValue();
            if(mid == 0) {
                result.setErrorCode(ErrorCode_API.ERR_USER_110010);
                return;
            }
            message.setId(mid);//主键更新验证状态
            messageDao.updateMessageCode(message);

            User user = new User();
            user.setMobile(bean.getMobile());
            user.setPassword(MD5.md5Salt(bean.getPassword()));//密码MD5
            user.setRegisterIp(bean.getIpAddress());
            user.setMarketFrom(bean.getMarketFrom());
            user.setNickName(bean.getNickName().trim());

            //用户类型未定义则默认普通用户
            if(!UserConstants.userTypeMap.containsKey(bean.getUserType())) {
                bean.setUserType(UserConstants.USER_TYPE_GENERAL);
            }
            user.setUserType(bean.getUserType());
            user.setRegisterFrom(KeyConstants.loginUserMap.get(bean.getAppId()));

            //第三方注册时带昵称
            if(StringUtil.isNotEmpty(bean.getNickName())) {
                //昵称不合法|系统中已存在
                if(!UserUtils.checkNickName(bean.getNickName(), getSendsitiveWordList()) || userMapper.queryUserNickNameIsExists(bean.getNickName()) > 0) {
                    result.setErrorCode(ErrorCode_API.ERROR_USER_110004);
                    return;
                }
            } else {//没有带昵称 使用系统默认规则生成
                user.setNickName(UserUtils.randomNickName());
            }

            //手机设备号
            if(StringUtil.isNotEmpty(bean.getDevice())) {
                user.setDevice(bean.getDevice());
            }
            user.setIsWhite(SysConfig.getBlackUser());//默认黑白名单-参数配置获取
            //邀请码对应的用户是代理或销售时 将该用户挂到该代理或销售下面
            if(StringUtil.isNotEmpty(bean.getCode())) {
                List<User> userList = userMapper.queryUserCodeIsExists(bean.getCode());
                if(StringUtil.isNotEmpty(userList) && userList.size() == 1 && userList.get(0).getIsSale() > 0) {
                    user.setHigherUid(userList.get(0).getId());
                    user.setHigherTime(new Date());
                    logger.info("[新普通用户注册] mobile=" + user.getMobile() + " 设置上级用户UserId=" + userList.get(0).getId());
                }
            }
            if(StringUtil.isEmpty(user.getAvatar())) {//默认图片
                user.setAvatar(SysConfig.getString("USER_DEFAULT_AVATAR"));
            }
            userMapper.insertUserRegister(user);
            //初始化账户数据
            userAccountMapper.insertUserAccount(user.getId());
            //初始化用户TOKEN
            UserToken token = initUserToken(user);
            tokenDao.insertUserToken(token);
            //初始化神单统计数据
            saveFollowUserStatis(user.getId());
            //返回用户对象
            user.setMobile(UserUtils.getSafeMobile(user.getMobile()));
            user.setAvatar(SysConfig.getHostStatic() + user.getAvatar());
            UserVo uvo = new UserVo();
            BeanUtils.copyProperties(uvo, user);//返回UserVo对象
            uvo.setToken(token.getToken());
            uvo.setKey(token.getTkey());
            result.setData(uvo);
        } catch (Exception e) {
            logger.error("[新普通用户注册异常] mobile=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 微信/QQ登录用户信息绑定
     * @author  mcdog
     * @param   userBean    业务参数对象
     */
    public synchronized void bindWeixinQqUser(UserBean userBean, ResultBean result) throws ServiceException,Exception
    {
        try
        {
            /**
             * 参数校验
             */
            //校验手机号是否合法
            if(!UserUtils.checkMobile(userBean.getMobile()))
            {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110001);
                return;
            }
            //校验登录类型是否非空
            if(StringUtil.isEmpty(userBean.getLoginType()))
            {
                result.setErrorCode(ErrorCode_API.SERVER_ERROR);
                return;
            }
            //校验用户昵称
            if(StringUtil.isEmpty(userBean.getNickName()))
            {
                result.setErrorCode(ErrorCode_API.SERVER_ERROR);
                return;
            }
            //校验验证码是否正确
            MessageCode message = new MessageCode();
            message.setMobile(userBean.getMobile());
            message.setType(5);
            message.setContent(userBean.getContent());
            message.setIsAuth(1);
            long mid = messageDao.checkMessageCode(message).longValue();
            if(mid == 0)
            {
                result.setErrorCode(ErrorCode_API.ERR_USER_110010);
                return;
            }
            message.setId(mid);//主键更新验证状态
            messageDao.updateMessageCode(message);

            /**
             * 查询该手机号是否有对应的用户,如果没有,则创建一个新的用户,如果有,则更新对应用户的微信/QQ唯一标识
             */
            User user = userMapper.queryUserInfoByMobile(userBean.getMobile());
            if(user == null)
            {
                /**
                 * 根据登录类型获取联合登录用户信息
                 */
                //微信联合登录
                UserBean baseinfoBean = null;
                if(2 == userBean.getLoginType())
                {
                    Object object = redis.getObject(weixinlogin_redis_prefix + userBean.getWxcode());//从缓存中获取微信用户信息
                    if(object != null)
                    {
                        baseinfoBean = (UserBean)object;
                    }
                }
                //QQ联合登录
                else if(3 == userBean.getLoginType())
                {
                    Object object = redis.getObject(qqlogin_redis_prefix + userBean.getOpenId());//从缓存中获取微信用户信息
                    if(object != null)
                    {
                        baseinfoBean = (UserBean)object;
                    }
                }

                /**
                 * 校验用户信息是否存在
                 */
                if(baseinfoBean == null || StringUtil.isEmpty(baseinfoBean.getOpenId()))
                {
                    result.setErrorCode(ErrorCode_API.ERROR_USER_110008);
                    return;
                }

                /**
                 * 设置用户信息
                 */
                //设置用户基本信息
                user = new User();
                user.setMobile(userBean.getMobile());//设置手机号
                user.setRegisterIp(userBean.getIpAddress());//设置注册ip
                user.setMarketFrom(userBean.getMarketFrom());//设置安装包来源
                user.setRegisterFrom(KeyConstants.loginUserMap.get(userBean.getAppId()));
                user.setLoginType(userBean.getLoginType());//设置登录类型
                user.setIsWhite(SysConfig.getBlackUser());//默认黑白名单-参数配置获取
                user.setUserType(0);//设置用户类型为普通用户
                user.setNickName(userBean.getNickName());//设置用户昵称
                user.setPassword(MD5.md5Salt(baseinfoBean.getOpenId()));//将密码设置为联合登录用户唯一标识
                user.setAvatar(baseinfoBean.getAvatar());//设置用户头像
                user.setSex(baseinfoBean.getSex());//设置用户性别

                //根据联合登录类型设置联合登录用户唯一标识
                if(2 == userBean.getLoginType())
                {
                    user.setWeixinOpenId(baseinfoBean.getOpenId());//设置微信用唯一标识
                }
                else if(3 == userBean.getLoginType())
                {
                    user.setQqOpenId(baseinfoBean.getOpenId());//设置QQ用户唯一标识
                }
                //校验用户昵称合法性及是否已存在,不合法或已存在则设置一个默认的用户昵称
                if(StringUtil.isNotEmpty(user.getNickName()))
                {
                    //昵称不合法|系统中已存在
                    if(!UserUtils.checkNickName(user.getNickName(), getSendsitiveWordList()) || userMapper.queryUserNickNameIsExists(user.getNickName()) > 0) {
                        result.setErrorCode(ErrorCode_API.ERROR_USER_110004);
                        return;
                    }
                }
                else
                {
                    user.setNickName(UserUtils.randomNickName());
                }
                //设置手机设备信息
                if(StringUtil.isNotEmpty(userBean.getDevice()))
                {
                    user.setDevice(userBean.getDevice());
                }
                //邀请码对应的用户是代理或销售时 将该用户挂到该代理或销售下面
                if(StringUtil.isNotEmpty(userBean.getCode()))
                {
                    List<User> userList = userMapper.queryUserCodeIsExists(userBean.getCode());
                    if(StringUtil.isNotEmpty(userList) && userList.size() == 1 && userList.get(0).getIsSale() > 0)
                    {
                        user.setHigherUid(userList.get(0).getId());
                        user.setHigherTime(new Date());
                        logger.info("[微信/QQ用户信息绑定]mobile=" + user.getMobile() + ",设置上级用户UserId=" + userList.get(0).getId());
                    }
                }
                //校验用户头像是否为空,为空则设置默认头像
                if(StringUtil.isEmpty(user.getAvatar()))
                {
                    user.setAvatar(SysConfig.getString("USER_DEFAULT_AVATAR"));
                }

                /**
                 * 保存用户及帐号相关信息
                 */
                userMapper.insertUserRegister(user);//保存用户信息
                userAccountMapper.insertUserAccount(user.getId());//保存用户账户信息
                UserToken token = initUserToken(user);//初始化用户token
                tokenDao.insertUserToken(token);//用户token信息
                saveFollowUserStatis(user.getId());//初始化用户神单统计数据
            }
            else
            {
                /**
                 * 获取联合登录用户信息
                 */
                //微信联合登录
                UserBean baseinfoBean = null;
                if(2 == userBean.getLoginType())
                {
                    Object object = redis.getObject(weixinlogin_redis_prefix + userBean.getWxcode());//从缓存中获取微信用户信息
                    if(object != null)
                    {
                        baseinfoBean = (UserBean)object;
                    }
                }
                //QQ联合登录
                else if(3 == userBean.getLoginType())
                {
                    Object object = redis.getObject(qqlogin_redis_prefix + userBean.getOpenId());//从缓存中获取微信用户信息
                    if(object != null)
                    {
                        baseinfoBean = (UserBean)object;
                    }
                }

                /**
                 * 校验联合登录用户信息是否存在
                 */
                if(baseinfoBean == null || StringUtil.isEmpty(baseinfoBean.getOpenId()))
                {
                    result.setErrorCode(ErrorCode_API.ERROR_USER_110008);
                    return;
                }

                /**
                 * 将联合登录用户的唯一标识更新至现有的帐户
                 */
                Dto updateDto = new BaseDto("userId",user.getId());
                if(2 == userBean.getLoginType())
                {
                    updateDto.put("weixinOpenId",baseinfoBean.getOpenId());
                }
                else if(3 == userBean.getLoginType())
                {
                    updateDto.put("QqOpenId",baseinfoBean.getOpenId());
                }
                userMapper.updateUserOpenId(updateDto);
            }

            /**
             * 传递保存的用户信息
             */
            User userInfo = userMapper.queryUserInfoByMobile(userBean.getMobile());
            if(userInfo != null)
            {
                userInfo.setPassword(null);//密码不再传递
                userBean.setObj(userInfo);
                result.setErrorCode(ErrorCode_API.SUCCESS);
            }
            else
            {
                result.setErrorCode(ErrorCode_API.SERVER_ERROR);
            }
        }
        catch (Exception e)
        {
            logger.error("[微信/QQ用户信息绑定]发生异常!手机号=" + userBean.getMobile() + ",异常信息:",e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 登录返回业务数据
     * @param bean
     * @return
     * @throws ServiceException
     */
    public void userLoginResult(UserBean bean, ResultBean result) throws ServiceException {
        try {
            User user = null;
            if(bean.getLoginType()==1) {//令牌登录
                if(!bean.isFlag()) {//涉及用户登录权限的业务操作则不再执行
                    return;
                }
                Long userId = (Long) bean.getObj();
                user = userMapper.queryUserInfoById(userId);
            } else {//其他
                user = (User) bean.getObj();
            }

            UserAccount account = userAccountMapper.queryUserAccountInfoByUserId(user.getId());//账户信息
            if (StringUtil.isEmpty(account)) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_110007);
            }
            if(StringUtil.isNotEmpty(bean.getDevice())) {//设备号
                user.setDevice(bean.getDevice());
            }

            UserVo uvo = new UserVo();
            //处理登录返回vo对象
            BeanUtils.copyProperties(uvo, user);
            uvo.setBalance(CalculationUtils.rd(account.getBalance()));
            uvo.setWithDraw(CalculationUtils.rd(account.getWithDraw()));
            if(StringUtil.isNotEmpty(uvo.getAvatar()) && uvo.getAvatar().indexOf("http") < 0)
            {
                uvo.setAvatar(SysConfig.getHostStatic() + uvo.getAvatar());
            }
            if(bean.getLoginType() != 1) {//非令牌登录则重置令牌
                UserToken token = initUserToken(user);
                tokenDao.updateUserToken(token);
                uvo.setToken(token.getToken());
                uvo.setKey(token.getTkey());
            } else {
                uvo.setToken(bean.getToken());
                uvo.setKey(bean.getKey());
            }
            userMapper.updateUserLoginInfo(user.getId(), bean.getIpAddress(), bean.getDevice());//更新用户上次登录时间或次数
            //是否显示我的返利菜单
            Integer number = userMapper.queryIsSetNumberForUserId(uvo.getId());
            if((uvo.getIsSale() == UserConstants.USER_PROXY_GENERAL || uvo.getIsSale() == UserConstants.USER_STATUS_AGENT) && number > 0) {
                uvo.setShowRebate(1);
            }
            //一天内开奖的中奖弹框
            Scheme winScheme = schemeMapper.queryDayWinSchemeInfoByUserId(user.getId());
            if(StringUtil.isNotEmpty(winScheme)) {
                Dto popupInfo = new BaseDto();
                String name = winScheme.getLotteryName();
                if(StringUtil.isNotEmpty(winScheme.getPeriod())) {
                    name = name + "[" + winScheme.getPeriod() + "]";
                }
                popupInfo.put("lotteryName", name);
                popupInfo.put("prize", CalculationUtils.rd(winScheme.getPrizeTax())+"");
                uvo.setWinPopup(popupInfo);
                schemeMapper.updateSchemeWinPopupStatus(user.getId());
            }
            //格式化用户信息
            UserUtils.formatUserInfo(uvo, user);
            result.setData(uvo);
        } catch (Exception e) {
            logger.error("[获取用户数据异常] loginType=" + bean.getLoginType() + " errorDesc=" + e.getMessage());
            int code = ErrorCode_API.SERVER_ERROR;
            if(e instanceof ServiceException) {
                code = ((ServiceException) e).getErrorCode();
            }
            throw new ServiceException(code, e.getMessage());
        }
    }

    /**
     * 渠道用户H5注册业务方法
     * @param bean
     * @return
     */
    public synchronized void saveChannelUser(UserBean bean, HttpServletRequest request) throws ServiceException {
        try {
            if(StringUtil.isEmpty(bean.getAppId())) {
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            Channel channel = channelMapper.queryChannelInfo(bean.getAppId());
            if(StringUtil.isEmpty(channel) || channel.getStatus() != 1) {//未找到渠道或失效
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            //不再有效时间内<开始时间
            if(StringUtil.isNotEmpty(channel.getBeginTime()) && channel.getBeginTime().getTime() > new Date().getTime()) {
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            //不再有效时间内>结束时间
            if(StringUtil.isNotEmpty(channel.getEndTime()) && channel.getEndTime().getTime() < new Date().getTime()) {
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            //ip白名单权限
            /*boolean limit = true;//是否限制访问
            if(StringUtil.isNotEmpty(channel.getIpLimit())) {
                String[] ips = channel.getIpLimit().split("\\,");
                for(String ipl : ips) {
                    if(ipl.equalsIgnoreCase(bean.getIpAddress())) {
                        limit = false;
                        break;
                    }
                }
            }
            if(limit) {
                throw new ServiceException(ErrorCode_API.ERROR_100004);
            }*/
            //验签
            /*if(!AuthSign.checkSignChannel(request, channel.getAuthKey())) {
                throw new ServiceException(ErrorCode_API.ERROR_100003);
            }*/
            //手机号验证
            if(!UserUtils.checkMobile(bean.getMobile())) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_110001);
            }

            bean.setDevice(request.getRequestURL().toString());
            UserCookie userCookie = null;
            User user = userMapper.queryUserInfoByMobile(bean.getMobile());
            if(StringUtil.isEmpty(user)) {//新用户执行注册流程
                user = new User();
                user.setMobile(bean.getMobile());
                user.setRegisterIp(bean.getIpAddress());
                user.setMarketFrom("channelh5");
                user.setUserType(Integer.parseInt(channel.getChannelCode()));
                user.setRegisterFrom(KeyConstants.loginUserMap.get(KeyConstants.getAppidHr()));//默认H5
                user.setLoginType(6);
                //第三方注册时带昵称
                if(StringUtil.isNotEmpty(bean.getNickName())) {
                    //昵称不合法或系统中已存在则使用默认
                    if(!UserUtils.checkNickName(bean.getNickName(), getSendsitiveWordList()) || userMapper.queryUserNickNameIsExists(bean.getNickName()) > 0) {
                        user.setNickName(UserUtils.randomNickName());
                    }
                } else {//没有带昵称 使用系统默认规则生成
                    user.setNickName(UserUtils.randomNickName());
                }
                user.setIsWhite(1);//默认开通白名单
                user.setHigherUid(channel.getOutAccountUserId());
                user.setHigherTime(new Date());
                user.setDevice(bean.getDevice());
                logger.info("[新H5渠道用户注册] mobile=" + user.getMobile() + " 设置上级用户UserId=" + channel.getOutAccountUserId());
                if(StringUtil.isEmpty(user.getAvatar())) {//默认图片
                    user.setAvatar(SysConfig.getString("USER_DEFAULT_AVATAR"));
                }
                userMapper.insertUserRegister(user);
                //初始化账户数据
                userAccountMapper.insertUserAccount(user.getId());
                //初始化TOKEN
                tokenDao.insertUserToken(initUserToken(user));
                //初始化COOKIE
                userCookie = initUserCookie(user);
                cookieMapper.insertUserCookie(userCookie);
            } else {
                if (user.getStatus().intValue() != 1) {//用户状态
                    throw new ServiceException(ErrorCode_API.ERROR_USER_110006);
                }
                //更新COOKIE
                user.setDevice(bean.getDevice());
                userCookie = initUserCookie(user);
                cookieMapper.updateUserCookie(userCookie);
            }

            UserAccount account = userAccountMapper.queryUserAccountInfoByUserId(user.getId());//账户信息
            if (StringUtil.isEmpty(account)) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_110007);
            }

            UserVo vo = new UserVo();
            BeanUtils.copyProperties(vo, user);
            vo.setBalance(CalculationUtils.rd(account.getBalance()));
            vo.setWithDraw(CalculationUtils.rd(account.getWithDraw()));
            vo.setAvatar(SysConfig.getHostStatic() + vo.getAvatar());
            vo.setToken(userCookie.getCookie());
            vo.setKey(userCookie.getCkey());

            userMapper.updateUserLoginInfo(user.getId(), bean.getIpAddress(), bean.getDevice());//更新用户上次登录时间或次数

            //是否显示我的返利菜单
            Integer number = userMapper.queryIsSetNumberForUserId(vo.getId());
            if((vo.getIsSale() == UserConstants.USER_PROXY_GENERAL || vo.getIsSale() == UserConstants.USER_STATUS_AGENT) && number > 0) {
                vo.setShowRebate(1);
            }

            //格式化用户信息
            UserUtils.formatUserInfo(vo, user);
            bean.setObj(vo);
        } catch (Exception e) {
            logger.error("[新H5渠道用户注册] mobile=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            int code = ErrorCode_API.SERVER_ERROR;
            if(e instanceof ServiceException) {
                code = ((ServiceException) e).getErrorCode();
            }
            throw new ServiceException(code, e.getMessage());
        }
    }

    /**
     * 用户令牌验证登录
     * @param bean
     * @return
     */
    public boolean tokenAuthLogin(UserBean bean) throws ServiceException, Exception {
        //token参数验证
        if(StringUtil.isEmpty(bean.getToken()) || StringUtil.isEmpty(bean.getKey())) {
            throw new ServiceException(ErrorCode_API.ERROR_USER_110008);
        }

        //解密并检查token串
        if(!TokenUtil.checkValidToken(bean.getToken(), bean.getKey())) {
            throw new ServiceException(ErrorCode_API.ERROR_USER_110008);
        }

        UserToken uToken = tokenDao.queryUserTokenInfoByToken(bean.getToken(), bean.getKey());
        //token错误或手机号、密码有变更||用户状态锁定或注销||token过期失效
        if(StringUtil.isEmpty(uToken) || uToken.getId().longValue() != 1 ||
                ((new Date().getTime() - uToken.getLastTime().getTime())/1000) > uToken.getExpiresin()) {
            throw new ServiceException(ErrorCode_API.ERROR_USER_110008);
        }
        bean.setObj(uToken.getUserId());
        return true;
    }

    /**
     * 用户密码验证登录
     * @param bean
     * @return
     */
    public boolean passwordAuthLogin(UserBean bean) throws ServiceException, Exception {
        //手机号验证
        if(!UserUtils.checkMobile(bean.getMobile())) {
            throw new ServiceException(ErrorCode_API.ERROR_USER_110001);
        }

        //验证登录密码
        bean.setPassword(decryptByPrivateKey(bean.getPassword()));
        if(!UserUtils.checkPassword(bean.getPassword())) {
            throw new ServiceException(ErrorCode_API.ERROR_USER_110003);
        }

        bean.setPassword(bean.getPassword());
        User userInfo = userMapper.queryUserInfoByMobile(bean.getMobile());
        if(StringUtil.isEmpty(userInfo) || !MD5.verify(bean.getPassword(), userInfo.getPassword())) {
            throw new ServiceException(ErrorCode_API.ERROR_USER_110005);
        }
        if(userInfo.getStatus().intValue() != 1) {//用户状态
            throw new ServiceException(ErrorCode_API.ERROR_USER_110006);
        }
        userInfo.setPassword(null);//密码不再传递
        bean.setObj(userInfo);
        return true;
    }

    /**
     * 微信/QQ验证登录
     * @author  mcdog
     */
    public boolean weixinQqAuthLogin(UserBean userBean) throws ServiceException, Exception
    {
        /**
         * 根据登录类型验证登录
         */
        //微信联合登录
        if(2 == userBean.getLoginType())
        {
            //校验wxcode是否为空
            if(StringUtil.isEmpty(userBean.getWxcode()))
            {
                return false;
            }
            //获取微信授权用户唯一标识和accessToken
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
            url += "?appid=" + WeixinUtils.appNo;
            url += "&secret=" + WeixinUtils.appSecret;
            url += "&code=" + userBean.getWxcode();
            url += "&grant_type=authorization_code";
            String response = HttpClientUtil.callHttpGet(url);
            if(StringUtil.isNotEmpty(response))
            {
                Dto responseDto = JsonUtil.jsonToDto(response);
                if(responseDto != null
                        && StringUtil.isNotEmpty(responseDto.get("openid"))
                        && StringUtil.isNotEmpty(responseDto.get("access_token")))
                {
                    userBean.setOpenId(responseDto.getAsString("openid"));//设置授权用户唯一标识

                    //获取微信用户基本信息
                    //https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN;//刷新accessToken
                    url = "https://api.weixin.qq.com/sns/userinfo";
                    url += "?access_token=" + responseDto.getAsString("access_token");
                    url += "&openid=" + responseDto.getAsString("openid");
                    url += "&lang=zh_CN";
                    response = HttpClientUtil.callHttpGet(url);
                    if(StringUtil.isNotEmpty(response))
                    {
                        responseDto = JsonUtil.jsonToDto(response);
                        if(responseDto != null)
                        {
                            userBean.setNickName(responseDto.getAsString("nickname"));//设置用户昵称
                            userBean.setAvatar(responseDto.getAsString("headimgurl"));//设置用户头像
                            if(StringUtil.isNotEmpty(responseDto.get("sex")))
                            {
                                userBean.setSex("1".equals(responseDto.getAsString("sex"))? 0 : 1);//设置用户性别(0-男 1-女)
                            }
                            redis.set((weixinlogin_redis_prefix + userBean.getWxcode()),userBean,30 * 60);//将微信用户信息存入缓存,设置有效期为30分钟
                        }
                        else
                        {
                            logger.error("[微信/QQ验证登录]获取微信授权用户信息失败!响应结果=" + responseDto.toString());
                            return false;
                        }
                    }
                    else
                    {
                        logger.error("[微信/QQ验证登录]获取微信授权用户信息失败!响应结果=" + response);
                        return false;
                    }
                }
                else
                {
                    logger.error("[微信/QQ验证登录]获取微信授权用户唯一标识和accessToken失败!响应结果=" + responseDto.toString());
                    return false;
                }
            }
            else
            {
                logger.error("[微信/QQ验证登录]获取微信授权用户唯一标识和accessToken失败!响应结果=" + response);
                return false;
            }
        }
        //QQ联合登录
        else if(3 == userBean.getLoginType())
        {
            //验证QQ用户唯一标识是否非空
            if(StringUtil.isEmpty(userBean.getOpenId()))
            {
                return false;
            }
            //验证接口调用凭证是否非空
            if(StringUtil.isEmpty(userBean.getAccessToken()))
            {
                return false;
            }
            //获取QQ用户信息
            String url = "https://graph.qq.com/user/get_user_info";
            url += "?access_token=" + userBean.getAccessToken();
            url += "&oauth_consumer_key=" + QqUtils.appNo;
            url += "&openid=" + userBean.getOpenId();
            String response = HttpClientUtil.callHttpGet(url);
            if(StringUtil.isNotEmpty(response))
            {
                Dto responseDto = JsonUtil.jsonToDto(response);
                if(responseDto != null && "0".equals(responseDto.getAsString("ret")))
                {
                    userBean.setNickName(responseDto.getAsString("nickname"));//设置用户昵称
                    userBean.setSex("女".equals(responseDto.getAsString("gender"))? 1 : 0);//设置性别(0-男 1-女)

                    //设置用户头像(优先选取100x100的头像)
                    if(StringUtil.isNotEmpty(responseDto.get("figureurl_qq_2")))
                    {
                        userBean.setAvatar(responseDto.getAsString("figureurl_qq_2"));
                    }
                    //100x100的头像没有,则取40x40的头像
                    else if(StringUtil.isNotEmpty(responseDto.get("figureurl_qq_1")))
                    {
                        userBean.setAvatar(responseDto.getAsString("figureurl_qq_1"));
                    }
                    redis.set((qqlogin_redis_prefix + userBean.getOpenId()),userBean,30 * 60);//将QQ用户信息存入缓存,设置有效期为30分钟
                }
                else
                {
                    logger.error("[微信/QQ验证登录]获取QQ授权用户信息失败!响应结果=" + response);
                    return false;
                }
            }
            else
            {
                logger.error("[微信/QQ验证登录]获取QQ授权用户信息失败!应结果=" + response);
                return false;
            }
        }
        //参数校验
        if(StringUtil.isEmpty(userBean.getOpenId()))
        {
            return false;
        }

        //查询微信/QQ联合登录用户
        Dto params = new BaseDto();
        params.put("openId",userBean.getOpenId());//设置微信/QQ授权用户唯一标识
        params.put("loginType",userBean.getLoginType());
        User userInfo = userMapper.queryWeixinQqUserInfo(params);

        //判断用户是否存在,如果不存在,则提示绑定用户信息
        if(StringUtil.isEmpty(userInfo) || StringUtil.isEmpty(userInfo.getMobile()))
        {
            userBean.setValidFlag(1);
            return false;
        }
        //判断用户状态
        if(userInfo.getStatus().intValue() != 1)
        {
            throw new ServiceException(ErrorCode_API.ERROR_USER_110006);
        }
        userInfo.setPassword(null);//密码不再传递
        userBean.setObj(userInfo);
        return true;
    }

    /**
     * 初始化token对象
     * @param user
     * @return
     */
    private UserToken initUserToken(User user) throws Exception {
        if(StringUtil.isEmpty(user)) {
            return null;
        }
        UserToken token = new UserToken();
        token.setUserId(user.getId());
        token.setMobile(user.getMobile());
        token.setPassword(user.getPassword());
        String key = TokenUtil.generateRandomKey("UK");
        token.setTkey(key);
        token.setToken(TokenUtil.generateToken(key));
        token.setExpiresin(TokenUtil.EXPIRE);
        token.setDevice(user.getDevice());
        return token;
    }

    /**
     * 初始化cookie对象
     * @param user
     * @return
     */
    private UserCookie initUserCookie(User user) throws Exception {
        if(StringUtil.isEmpty(user) || StringUtil.isEmpty(user.getId())) {
            return null;
        }
        UserCookie cookie = new UserCookie();
        cookie.setUserId(user.getId());
        cookie.setMobile(user.getMobile());
        String key = CookieUtil.generateRandomKey();
        cookie.setCkey(key);
        cookie.setCookie(CookieUtil.generateCookie(key, user.getId()));
        cookie.setExpiresin(CookieUtil.EXPIRE);
        cookie.setDevice(user.getDevice());
        return cookie;
    }

    /**
     * 重置找回用户密码
     * @param bean
     * @return
     */
    public void resetUserPasswd(UserBean bean, ResultBean result) throws ServiceException, Exception {
        try {
            //手机号验证
            if(!UserUtils.checkMobile(bean.getMobile())) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110001);
                return;
            }
            //手机号是否存在
            User userInfo = userMapper.queryUserInfoByMobile(bean.getMobile());
            if(StringUtil.isEmpty(userInfo)) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110011);
                return;
            }
            if (userInfo.getStatus().intValue() != 1) {//用户状态
                result.setErrorCode(ErrorCode_API.ERROR_USER_110006);
                return;
            }

            //新密码
            bean.setPassword(decryptByPrivateKey(bean.getPassword()));
            if(!UserUtils.checkPassword(bean.getPassword())) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_110003);
            }

            //验证验证码
            MessageCode message = new MessageCode();
            message.setMobile(bean.getMobile());
            message.setType(3);
            message.setContent(bean.getContent());
            message.setIsAuth(1);
            long mid = messageDao.checkMessageCode(message).longValue();
            if(mid == 0) {
                result.setErrorCode(ErrorCode_API.ERR_USER_110010);
                return;
            }
            message.setId(mid);//主键更新验证状态
            messageDao.updateMessageCode(message);

            //重置密码
            userMapper.updateResetUserPassword(MD5.md5Salt(bean.getPassword()), bean.getDevice(), userInfo.getId());
        } catch (Exception e) {
            logger.error("[用户密码找回异常] mobile=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 修改用户密码
     * @param bean
     * @return
     */
    public void updatePasswd(UserBean bean, ResultBean result) throws ServiceException, Exception {
        try {
            //检查用户是否存在
            User userInfo = userMapper.queryUserInfoById(bean.getUserId());
            if(StringUtil.isEmpty(userInfo)) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110008);
                return;
            }
            if (userInfo.getStatus().intValue() != 1) {//用户状态
                result.setErrorCode(ErrorCode_API.ERROR_USER_110006);
                return;
            }

            //旧密码
            bean.setOldPassword(decryptByPrivateKey(bean.getOldPassword()));
            if(!UserUtils.checkPassword(bean.getOldPassword())) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_110003);
            }

            //新密码
            bean.setPassword(decryptByPrivateKey(bean.getPassword()));
            if(!UserUtils.checkPassword(bean.getPassword())) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_110003);
            }

            //验证当前密码是否正确
            if(!MD5.verify(bean.getOldPassword(), userInfo.getPassword())) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_110012);
            }
            //修改密码
            userMapper.updateResetUserPassword(MD5.md5Salt(bean.getPassword()), bean.getDevice(), userInfo.getId());
        } catch (Exception e) {
            logger.error("[修改用户密码异常] userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新用户头像
     * @author  mcdog
     * @param   params          业务参数对象
     * @param   multipartFile   待上传的头像文件
     * @param   resultBean      处理结果对象
     */
    public void updateUserAvatar(Dto params, MultipartFile multipartFile, ResultBean resultBean) throws ServiceException,Exception
    {
        //读取文件信息并上传
        if(multipartFile != null && multipartFile.getSize() > 0)
        {
            //文件目录不存在则创建
            String avatarPath = SysConfig.getString("user.avatar.file.path");//从配置文件中提取用户头像文件路径
            String realPath = SysConfig.getString("static.file.path") + avatarPath;
            File directory = new File(realPath);
            if (!directory.exists())
            {
                directory.mkdirs();
            }
            //校验文件格式
            String userId = params.getAsString("userId");//用户编号
            String fileName = multipartFile.getOriginalFilename();//提取文件名
            String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);//提取文件后缀
            String suffix = SysConfig.getString("user.avatar.file.suffix");//从配置文件中提取用户头像文件支持格式
            if(suffix.toLowerCase().indexOf(fileSuffix.toLowerCase()) < 0)
            {
                logger.error("[用户上传头像]上传图片格式不支持!用户编号=" + userId + ",文件名:" + fileName);
                throw new ServiceException(ErrorCode_API.ERROR_USER_110013,
                        MessageFormat.format(ErrorCode_API.ERROR_USER_110013_MSG, new Object[]{fileSuffix}));
            }
            //校验文件大小
            long maxSize = Long.parseLong(SysConfig.getString("user.avatar.file.maxsize"));//从配置文件中提取户头像文件最大大小(单位:M)
            if(multipartFile.getSize() > (maxSize * 1024 * 1024))
            {
                logger.error("[用户上传头像]上传图片超过限制大小!用户编号=" + userId + ",文件大小:" + multipartFile.getSize() + "byte");
                throw new ServiceException(ErrorCode_API.ERROR_USER_110014,
                        MessageFormat.format(ErrorCode_API.ERROR_USER_110014_MSG, new Object[]{(maxSize + "M")}));
            }
            //上传文件
            String newFileName = System.currentTimeMillis() + "." + fileSuffix;//文件名以用户id为名
            multipartFile.transferTo(new File(realPath + newFileName));

            //更新用户头像信息
            params.put("avatar",avatarPath + newFileName);
            userMapper.updateUserAvatar(params);
            resultBean.setErrorCode(ErrorCode.SUCCESS);
            resultBean.setData(new BaseDto("iconurl",SysConfig.getHostStatic() + params.getAsString("avatar")));
        }
    }

    /**
     * 用户绑定银行卡
     * @author  mcdog
     * @param   params          业务参数对象
     * @param   resultBean      处理结果对象
     */
    public void updateUserBank(Dto params, ResultBean resultBean) throws ServiceException,Exception
    {
        /**
         * 校验参数
         */
        if(StringUtil.isEmpty(params.get("bcode"))
                || StringUtil.isEmpty(params.get("acode"))
                || StringUtil.isEmpty(params.get("bankno")))
        {
            logger.error("[用户绑定银行卡]参数校验不通过!用户编号=" + params.getAsString("userId") + ",接收原始参数:" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 判断用户当前是否允许绑定银行卡信息
         */
        //判断用户是否存在
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//查询用户
        if(user == null)
        {
            logger.error("[用户绑定银行卡]用户不存在!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_USERNOTEXISTS,ErrorCode_API.ERROR_PAY_USERNOTEXISTS_MSG);
        }
        //判断用户是否注销(禁用)/冻结(锁定)
        else if(user.getStatus() == UserConstants.USER_STATUS_CANCEL || user.getStatus() == UserConstants.USER_STATUS_LOCK)
        {
            logger.error("[用户绑定银行卡]用户账户当前处于" + UserConstants.userStatusMap.get(user.getStatus()) + "状态!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_USER_NOTALLOW_BINDBANK,
                    MessageFormat.format(ErrorCode_API.ERROR_PAY_USERSTATUS_NOTALLOW_MSG,new String[]{"已" + UserConstants.userStatusMap.get(user.getStatus()) + "的用户"}));
        }
        //判断用户是否已经实名认证
        if(StringUtil.isEmpty(user.getRealName()))
        {
            logger.error("[用户绑定银行卡]用户尚未完成实名认证,无法绑定银行卡!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_USER_NOTALLOW_BINDBANK,
                    MessageFormat.format(ErrorCode_API.ERROR_PAY_USERSTATUS_NOTALLOW_MSG,new String[]{"未实名认证的用户"}));
        }
        //判断银行状态和银行
        List<Bank> bankList = bankMapper.queryBanks(new BaseDto("bcode",params.get("bcode")));//查询银行
        if(bankList == null || bankList.size() == 0)
        {
            logger.error("[用户绑定银行卡]不支持的银行卡!无相关的的银行记录!银行编号=" + params.getAsString("bcode"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        Bank bank = bankList.get(0);
        if(bank.getStatus() != 1)
        {
            logger.error("[用户绑定银行卡]银行当前状态不可用!银行编号=" + params.getAsString("bcode") + ",银行名称:" + bank.getBankName() + ",银行状态:" + bank.getStatus());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断银行转账是否需要支行信息
        else if(bank.getNeedSub() == 1 && StringUtil.isEmpty(params.getAsString("subname")))
        {
            logger.error("[用户绑定银行卡]缺少支行信息!银行编号=" + bank.getBankCode() + ",银行名称=" + bank.getBankName());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断区域信息是否存在
        List<City> cityList = areaMapper.queryCitys(new BaseDto("acode",params.getAsString("acode")));//查询城市信息
        if(cityList == null || cityList.size() == 0)
        {
            logger.error("[用户绑定银行卡]查询不到相关的城市记录!城市编号=" + params.getAsString("acode"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 验证银行卡信息
         */
        Dto validParams = new BaseDto();
        validParams.put("name",user.getRealName());//姓名
        validParams.put("idcard",user.getIdCard());//身份证
        validParams.put("bankno",params.getAsString("bankno").replace(" ",""));//卡号
        validParams.put("banknum",bank.getBankCode());//银行编号
        validParams.put("abbreviation",bank.getAbbreviation());//银行简称
        validParams.put("mobile",user.getMobile());//预留手机号
        //validParams.put("userip",SysConfig.getString("API_HOST_IP"));//用户机器的公网ip
        JdUtils.aucFourItems(validParams);//银行四要素验证

        /**
         * 根据银行卡信息验证结果更新用户银行卡信息
         */
        if("1000".equals(validParams.getAsString("dcode")))
        {
            /**
             * 更新用户银行卡信息
             */
            //设置银行卡信息
            BankInfoVo bankInfoVo = new BankInfoVo();
            bankInfoVo.setBankCode(bank.getBankCode());//设置银行编号
            bankInfoVo.setBankName(bank.getBankName());//设置银行名称
            bankInfoVo.setBankCard(params.getAsString("bankno").replace(" ",""));//设置银行卡号
            bankInfoVo.setSubBankName(params.getAsString("subname"));//设置支行名称
            bankInfoVo.setAccountHolder(user.getRealName());//设置开户人
            bankInfoVo.setLogo(bank.getLogo());//设置银行logo

            //设置开户行所在城市信息
            City city = cityList.get(0);
            List<Province> provinceList = areaMapper.queryProvinces(new BaseDto("pcode",city.getProvinceCode()));//查询省份信息
            Province province = provinceList.get(0);
            bankInfoVo.setBankProvinceCode(province.getProvinceCode());//设置银行所在省份编号
            bankInfoVo.setBankProvince(province.getProvinceName());//设置银行所在省份
            bankInfoVo.setBankCityCode(city.getCityCode());//设置银行所在城市编号
            bankInfoVo.setBankCity(city.getCityName());//设置银行所在城市

            //更新银行卡信息
            params.put("bankInfo", JsonUtil.JsonObject(bankInfoVo));
            int count = userMapper.updateUserBank(params);
            if(count > 0)
            {
                Dto dataDto = new BaseDto();

                //设置持卡人
                String realName = user.getRealName();
                realName = realName.length() <= 2? ("*" + realName.substring(1)) : ("**" + realName.substring(realName.length() - 1));
                dataDto.put("cuser",realName);//设置持卡人

                dataDto.put("bname",bankInfoVo.getBankName());//设置开户银行名称
                dataDto.put("logo",SysConfig.getHostStatic() + bankInfoVo.getLogo());//设置银行logo
                dataDto.put("kharea",bankInfoVo.getBankProvince() + " " + bankInfoVo.getBankCity());//设置开户银行所在地

                //如果银行转账需要支行,则设置支行信息
                if(bank.getNeedSub() == 1)
                {
                    dataDto.put("subname",bankInfoVo.getSubBankName());//设置开户银行的支行名称
                }
                //设置银行卡号(只显示后4位)
                String bankno = bankInfoVo.getBankCard();
                String newBankno = "";
                for(int i = 0; i < bankno.length() - 4; i ++)
                {
                    newBankno += "*";
                }
                newBankno += bankno.substring(bankno.length() - 4);
                dataDto.put("bankno",newBankno);

                resultBean.setData(dataDto);//设置返回数据
                resultBean.setErrorCode(ErrorCode_API.SUCCESS);
            }
        }
        else
        {
            logger.error("[用户绑定银行卡]银行卡信息验证不通过!用户编号=" + params.getAsString("userId") + ",验证消息=" + validParams.getAsString("dmsg"));
            if("-1001".equals(validParams.getAsString("dcode")))
            {
                throw new ServiceException(ErrorCode_API.ERROR_VALID_NOTIDENTICAL,validParams.getAsString("dmsg"));
            }
            else
            {
                throw new ServiceException(ErrorCode_API.ERROR_VALID_NOTIDENTICAL,ErrorCode_API.ERROR_VALID_NOTIDENTICAL_MSG);
            }
        }
    }

    /**
     * 用户实名验证
     * @author  mcdog
     * @param   params          业务参数对象
     * @param   resultBean      处理结果对象
     */
    public void updateUserIdentity(Dto params, ResultBean resultBean) throws ServiceException,Exception
    {
        /**
         * 校验参数
         */
        if(StringUtil.isEmpty(params.get("name"))
                || StringUtil.isEmpty(params.get("idcard")))
        {
            logger.error("[用户实名验证]用户编号=" + params.getAsString("userId") + ",参数校验不通过!接收原始参数:" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断用户是否已经实名过
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//获取用户
        if(user == null)
        {
            logger.error("[用户实名验证]用户不存在!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        else if(StringUtil.isNotEmpty(user.getRealName()))
        {
            logger.error("[用户实名验证]用户已经实名认证过!无法重复认证!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_USER_IDENTITYRZ_DUPLICATE,ErrorCode_API.ERROR_USER_IDENTITYRZ_DUPLICATE_MSG);
        }
        //判断验证码是否正确
        MessageCode message = new MessageCode();
        message.setContent(params.getAsString("code"));
        message.setType(4);
        message.setMobile(user.getMobile());
        long recordId = messageDao.checkMessageCode(message);
        if(recordId <= 0)
        {
            logger.error("[用户实名验证]验证码校验不通过!匹配不到符合条件的验证码记录!用户编号=" + params.getAsString("userId") + ",验证码=" + params.getAsString("code"));
            throw new ServiceException(ErrorCode_API.ERR_USER_110010);
        }
        //更新验证码的状态
        message.setIsAuth(1);
        message.setId(recordId);//主键更新验证状态
        messageDao.updateMessageCode(message);

        /**
         * 验证身份证号码和姓名是否一致
         */
        Dto validParams = new BaseDto();
        validParams.put("name",params.getAsString("name"));//姓名
        validParams.put("idcard",params.getAsString("idcard"));//身份证
        validParams.put("userip",SysConfig.getString("API_HOST_IP"));//用户机器的公网ip
        JdUtils.realnameVerify_2(validParams);//实名认证
        if(!"1000".equals(validParams.getAsString("dcode")))
        {
            //JdUtils.realnameVerify(validParams);//京东万象做备用验证
            //JdUtils.realnameVerify_2(validParams);//京东万象做备用验证
        }
        if("1000".equals(validParams.getAsString("dcode")))
        {
            //更新用户身份证和真实姓名
            int count = userMapper.updateUserIdentity(params);
            if(count > 0)
            {
                /**
                 * 根据条件给用户赠送优惠券
                 */
                //注册送活动
                logger.info("[用户实名验证]用户身份信息更新成功!用户编号=" + user.getId());
                params.put("opmethod", "用户实名验证");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType", "0");//设置优惠券赠送类型为注册送(0-注册送 1-充值送)
                //Channel channel = channelMapper.queryChannelInfo(user.getUserType()+"");
                sendCoupon(params, user.getId());//注册送活动优惠券赠送

                //活动优惠券补送
                params.put("opmethod", "用户实名验证-活动优惠券补送");
                params.put("couponType", "1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                sendCouponReissue(params,user.getId());//补送优惠券

                //设置真实姓名
                Dto dataDto = new BaseDto();
                String name = params.getAsString("name");
                name = name.length() <= 2? ("*" + name.substring(1)) : ("**" + name.substring(name.length() - 1));
                dataDto.put("name",name);

                //设置身份证号
                String idcard = params.getAsString("idcard");
                String newIdcard = "";
                for(int i = 4; i <= idcard.length() - 5; i ++)
                {
                    newIdcard += "*";
                }
                newIdcard = idcard.substring(0,4) + newIdcard + idcard.substring(idcard.length() - 4);
                dataDto.put("idcard",newIdcard);

                resultBean.setData(dataDto);//设置返回数据
                resultBean.setErrorCode(ErrorCode_API.SUCCESS);
            }
        }
        else
        {
            logger.error("[用户实名验证]实名验证不通过!用户编号=" + params.getAsString("userId") + ",验证消息:" + validParams.getAsString("dmsg"));
            throw new ServiceException(ErrorCode_API.ERROR_VALID_NOTIDENTICAL,ErrorCode_API.ERROR_VALID_NOTIDENTICAL_MSG);
        }
    }

    /**
     * 计奖任务自动派奖-官方加奖和网站加奖分开
     * @author kouyi
     */
    public synchronized int updateSchemeForQrPj(Dto params) throws Exception
    {
        //校验参数
        int count = 0;
        if(StringUtil.isEmpty(params.get("id")))
        {
            logger.error("[方案派奖] 派奖失败,找不到方案号!");
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
            logger.error("[方案派奖] 找不到" + zhtext + "方案信息 方案号=" + params.get("id"));
            return 0;
        }
        //判断方案是否允许派奖(必须是已计奖且已中奖的方案才允许确认派奖)
        Dto schemeDto = schemeList.get(0);
        if(schemeDto.getAsInteger("openStatus") != 2 || schemeDto.getAsInteger("prizeStatus") != 0)
        {
            logger.error("[方案派奖] " + zhtext + "方案未满足派奖条件 方案号=" + params.get("id"));
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
        if(iszh) {
            count = schemeMapper.zhschemeQrPj(params);//更新追号方案状态为已派奖
        } else {
            count = schemeMapper.schemeQrPj(params);//更新非追号方案状态为已派奖
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
                        Dto outDto = new BaseDto("userId",outUserId);
                        outDto.put("tbalance",-prizeSubjoinSiteTax);//减去余额
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
                        userDetail.setRemark("自动派奖[加奖出款户]:"+schemeDto.getAsString("lotteryName") + "加奖[" + schemeDto.getAsString("schemeOrderId") + "]");
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
            logger.info("[任务自动派奖]派奖成功!派奖金额=" + prizeTax + "(含加奖:"+(prizeSubjoinTax+prizeSubjoinSiteTax)+")," + zhtext + "方案id=" + schemeDto.getAsString("id") + ",所属用户编号=" + userId);
        }
        return count;
    }

    /**
     * 用户提现
     * @author  mcdog
     * @param   params      业务参数对象
     * @param   result      处理结果对象
     */
    public synchronized void updateUserForWithdraw(Dto params, ResultBean result) throws ServiceException,Exception
    {
        //校验
        //判断金额是否非空
        String smoney = params.getAsString("smoney");
        if(StringUtil.isEmpty(smoney))
        {
            logger.error("[用户提现]提现金额不能为空!用户编号=" + params.getAsString("userId") + ",接收原始参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断金额是否大于0
        else if(smoney.indexOf("-") > -1)
        {
            logger.error("[用户提现]提现金额格式错误!用户编号=" + params.getAsString("userId") + ",提现金额=" + smoney);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断金额是否最多只有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            logger.error("[用户提现]提现金额最多只能有2位小数!用户编号=" + params.getAsString("userId") + ",提现金额=" + smoney);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //检测系统当前提现功能是否可用
        String systxstatus = SysConfig.getString("WITHDRAW_STATUS_SWITCH");
        if("0".equals(systxstatus))
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"银行系统维护中，暂时不支持提现，请关注首页公告");
        }
        //校验提现金额是否超过系统设置的单次最多提现金额
        String maxtxMoneyStr = SysConfig.getString("MAX_WITHDRAW_SMONEY");
        if(StringUtil.isNotEmpty(maxtxMoneyStr))
        {
            if(params.getAsDoubleValue("smoney") > Double.parseDouble(maxtxMoneyStr))
            {
                throw new ServiceException(ErrorCode_API.SERVER_ERROR,"单笔提现金额不能超过" + maxtxMoneyStr + "元");
            }
        }
        //校验当前时间段是否允许提现
        systxstatus = SysConfig.getString("WITHDRAW_TIME_RANGE");
        if(StringUtil.isNotEmpty(systxstatus) && !"0".equals(systxstatus))
        {
            Calendar current = Calendar.getInstance();
            int currentHour = current.get(Calendar.HOUR_OF_DAY);//当前时间-时
            int currentMinute = current.get(Calendar.MINUTE);//当前时间-分
            String[] timeCharacters = systxstatus.split(";");//提取时间特征
            int count = 0;
            for(String timeCharacter : timeCharacters)
            {
                //判断当前时间点是否在时间特征表述的范围内,只有当前时间在时间特征的范围内,才允许提现
                String[] times = timeCharacter.split("~");
                String[] timeStart = times[0].split(":");
                String[] timeEnd = times[1].split(":");
                if((currentHour == Integer.parseInt(timeStart[0]) && currentMinute >= Integer.parseInt(timeStart[1]))
                        || (currentHour == Integer.parseInt(timeEnd[0]) && currentMinute <= Integer.parseInt(timeEnd[1]))
                        || (currentHour > Integer.parseInt(timeStart[0]) && currentHour < Integer.parseInt(timeEnd[0])))
                {
                    count ++;
                }
            }
            if(count == 0)
            {
                throw new ServiceException(ErrorCode_API.SERVER_ERROR,"提现时间段为" + systxstatus.replace(";","或"));
            }
        }

        /**
         * 调用扣款存储过程
         */
        Dto deductParams = new BaseDto();
        String timeStr = DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);
        deductParams.put("payId","TX" + timeStr + new Random().nextInt(10));//设置商户订单号(平流水号)
        deductParams.put("userId",params.get("userId"));//设置用户编号
        deductParams.put("smoney",smoney);//设置提款金额(交易金额)
        deductParams.put("clientFrom",KeyConstants.loginUserMap.get(params.getAsString("appId")));//设置客户端来源
        deductParams.put("requestIp",params.get("requestIp"));//设置客户端ip
        deductParams.put("dcode",-1000);//设置默认处理状态码
        deductParams.put("dmsg","处理失败");//设置默认处理状态描述
        userPayMapper.doDeduct(deductParams);

        //提取处理状态码,dcode=1000表示付款成功
        logger.info("[用户提现]处理结果:dcode:" + deductParams.getAsString("dcode") + ",dmsg:" + deductParams.getAsString("dmsg") + ",用户编号=" + params.getAsString("userId"));
        int dcode = deductParams.getAsInteger("dcode");
        if(dcode == 1000)
        {
            result.setErrorCode(ErrorCode.SUCCESS);
            return;
        }
        //用户不存在
        else if(dcode == 1001)
        {
            logger.error("[用户提现]用户不存在!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_USERNOTEXISTS,ErrorCode_API.ERROR_PAY_USERNOTEXISTS_MSG);
        }
        //用户当前状态不可提现
        else if(dcode == 1002)
        {
            logger.error("[用户提现]用户当前状态不可提现!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_USERSTATUS_NOTALLOW,deductParams.getAsString("dmsg"));
        }
        //用户尚未绑定银行卡
        else if(dcode == 1003)
        {
            logger.error("[用户提现]用户尚未绑定银行卡或银行卡信息更新绑定正在处理中!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_USERNEEDBINDBANK,ErrorCode_API.ERROR_PAY_USERNEEDBINDBANK_MSG);
        }
        //用户可提现越不足
        else if(dcode == 1004)
        {
            logger.error("[用户提现]用户可提现余额少于本次提现金额！" + deductParams.getAsString("dmsg"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_YEBU,ErrorCode_API.ERROR_PAY_YEBU_MSG);
        }
        //用户余额不足
        else if(dcode == 1005)
        {
            logger.error("[用户提现]用户余额少于本次提现金额！" + deductParams.getAsString("dmsg"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_YEBU,ErrorCode_API.ERROR_PAY_YEBU_MSG);
        }
        //超过一天提现次数
        else if(dcode == 1006)
        {
            logger.error("[用户提现]用户已达到一天最多提现次数!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_CGYTZDTXCS,ErrorCode_API.ERROR_PAY_CGYTZDTXCS_MSG);
        }
        //虚拟账户不能提现
        else if(dcode == 1007)
        {
            logger.error("[用户提现]虚拟账号不能提现!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_USERSTATUS_NOTALLOW,
                    MessageFormat.format(ErrorCode_API.ERROR_PAY_USERSTATUS_NOTALLOW_MSG,new String[]{"该账户"}));
        }
        else
        {
            result.setErrorCode(ErrorCode.SERVER_ERROR);
            return;
        }
    }

    /**
     * 获取用户账户流水
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getUserBalanceDetail(Dto params, ResultBean result) throws ServiceException,Exception
    {
        /**
         * 设置查询参数
         */
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
        //判断查询类型(qtype,0-中奖 1-购彩 2-充值 3-提现 4-退款 为空时查询全部)
        if(StringUtil.isNotEmpty(params.get("qtype")))
        {
            params.put("status", 1);
            //中奖
            int qtype = params.getAsInteger("qtype");//提取查询类型
            if(qtype == 0)
            {
                params.put("channelCode",PayConstants.CHANNEL_CODE_IN_DRAWING);//设置业务渠道用户中奖
            }
            //购彩
            else if(qtype == 1)
            {
                params.put("channelCode",PayConstants.CHANNEL_CODE_OUT_DRAWING);//设置业务渠道为购彩
            }
            //充值
            else if(qtype == 2)
            {
                //设置业务渠道为充值(4100-4199)
                params.put("minChannelCode",PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN);
                params.put("maxChannelCode",4199);
            }
            //提现
            else if(qtype == 3)
            {
                //设置业务渠道为提现(3100-3199)
                params.put("minChannelCode",PayConstants.CHANNEL_CODE_OUT_RENGONGPAY);
                params.put("maxChannelCode",3199);
                params.remove("status");
                params.put("txStatus",-1);
            }
            //退款
            else if(qtype == 4)
            {
                //设置业务渠道为退款(407-411)
                params.put("minChannelCode",PayConstants.CHANNEL_CODE_IN_YYFAIL);
                params.put("maxChannelCode",411);
            }
            //打赏
            else if(qtype == 5)
            {
                //设置业务渠道为收取赏金/支付赏金
                params.put("includeChannelCodes",(PayConstants.CHANNEL_CODE_IN_SQDASHANG + "," + PayConstants.CHANNEL_CODE_IN_ZFDASHANG));
            }
        }

        /**
         * 查询账户流水并设置返回数据
         */
        //查询账户流水
        Map<String,Object> dataMap = new HashMap<String,Object>();
        List<UserDetail> detailList = userDetailMapper.queryUserDetail(params);//查询账户流水
        List<Dto> dataList = new ArrayList<Dto>();
        if(detailList != null && detailList.size() > 0)
        {
            //封装为前端展示对象
            for(UserDetail userDetail : detailList)
            {
                Dto userDetailDto = new BaseDto();
                String time = DateUtil.formatDate(userDetail.getCreateTime(),DateUtil.DEFAULT_DATE_TIME);//设置流水时间
                userDetailDto.put("time",time);//设置流水时间
                userDetailDto.put("mdtime",StringUtil.isEmpty(time)? "" : time.substring(5,10));//设置流水时间(月-日)
                userDetailDto.put("hmtime",StringUtil.isEmpty(time)? "" : time.substring(11,19));//设置流水时间(时分秒)
                userDetailDto.put("remark",(StringUtil.isNotEmpty(userDetail.getRemark()) && userDetail.getRemark().indexOf("[") > -1)? (userDetail.getRemark().substring(0,userDetail.getRemark().indexOf("["))) : userDetail.getRemark());//设置交易备注
                userDetailDto.put("stype",userDetail.getInType()? 1 : 0);//交易类型(0-入账 1-出账)
                userDetailDto.put("money",String.format("%.2f",userDetail.getMoney()) + "元");//设置交易金额
                dataList.add(userDetailDto);
            }
        }
        dataMap.put("list",dataList);//设置账户流水

        //如果有分页标识,则查询方案总记录条数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = userDetailMapper.queryUserDetailCount(params);
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置账户流水总记录数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }
        //设置返回数据
        result.setData(dataMap);//设置账户流水数据
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 用户日报表数据统计
     * @param date 统计日期
     * @throws ServiceException
     */
    public void userDayDateStatis(String date) throws ServiceException {
        try {
            userMapper.userDayDateStatis(date);
        } catch (Exception e) {
            logger.error("[用户日报表数据统计异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 获取用户绑定的银行卡信息
     * @author  mcdog
     * @param   params          业务参数对象
     * @param   resultBean      处理结果对象
     */
    public void getUserBankInfo(Dto params, ResultBean resultBean) throws ServiceException,Exception
    {
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//获取用户
        if(user == null)
        {
            logger.error("[获取用户绑定的银行卡信息]用户不存在!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        else if(StringUtil.isEmpty(user.getRealName()) || StringUtil.isEmpty(user.getBankInfo()))
        {
            logger.info("[获取用户绑定的银行卡信息]用户尚未绑定银行卡!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_USER_NOT_BINDBANK,ErrorCode_API.ERROR_USER_NOT_BINDBANK_MSG);
        }
        //设置持卡人
        Dto dataDto = new BaseDto();
        String realName = user.getRealName();
        realName = realName.length() <= 2? ("*" + realName.substring(1)) : ("**" + realName.substring(realName.length() - 1));
        dataDto.put("cuser",realName);//设置持卡人

        BankInfoVo bankInfoVo = user.getBankInfo();
        dataDto.put("bname",bankInfoVo.getBankName());//设置开户银行名称
        dataDto.put("logo",SysConfig.getHostStatic() + bankInfoVo.getLogo());//设置开户银行logo
        dataDto.put("kharea",bankInfoVo.getBankProvince() + " " + bankInfoVo.getBankCity());//设置开户银行所在地
        dataDto.put("subname",StringUtil.isEmpty(bankInfoVo.getSubBankName())? "" : bankInfoVo.getSubBankName());//设置开户银行的支行名称

        //设置银行卡号(只显示后4位)
        String bankno = bankInfoVo.getBankCard();
        String newBankno = "";
        if(StringUtil.isNotEmpty(bankno))
        {
            for(int i = 0; i < bankno.length() - 4; i ++)
            {
                newBankno += "*";
            }
            newBankno += bankno.substring(bankno.length() - 4);
        }
        dataDto.put("bankno",newBankno);
        resultBean.setData(dataDto);//设置返回数据
        resultBean.setErrorCode(ErrorCode_API.SUCCESS);

    }

    /**
     * 获取用户的实名信息
     * @author  mcdog
     * @param   params          业务参数对象
     * @param   resultBean      处理结果对象
     */
    public void getUserIdentity(Dto params, ResultBean resultBean) throws ServiceException,Exception
    {
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//获取用户
        if(user == null)
        {
            logger.error("[获取用户的实名信息]用户不存在!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        else if(StringUtil.isEmpty(user.getRealName()) || StringUtil.isEmpty(user.getIdCard()))
        {
            logger.info("[获取用户的实名信息]用户尚未进行实名认证!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_USER_NOT_IDENTITYRZ,ErrorCode_API.ERROR_USER_NOT_IDENTITYRZ_MSG);
        }

        //设置真实姓名
        Dto dataDto = new BaseDto();
        String name = user.getRealName();
        name = name.length() <= 2? ("*" + name.substring(1)) : ("**" + name.substring(name.length() - 1));
        dataDto.put("name",name);

        //设置身份证号
        String newIdcard = "";
        String idcard = user.getIdCard();
        for(int i = 4; i <= idcard.length() - 5; i ++)
        {
            newIdcard += "*";
        }
        newIdcard = idcard.substring(0,4) + newIdcard + idcard.substring(idcard.length() - 4);
        dataDto.put("idcard",newIdcard);

        //设置返回数据
        resultBean.setData(dataDto);
        resultBean.setErrorCode(ErrorCode_API.SUCCESS);
    }

    /**
     * 获取用户当前的提现信息
     * @author  mcdog
     * @param   params          业务参数对象
     * @param   resultBean      处理结果对象
     */
    public void getUserTxInfo(Dto params, ResultBean resultBean) throws ServiceException,Exception
    {
        //检测用户当前是否可提现
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//获取用户
        if(user == null)
        {
            logger.info("[获取用户当前的提现信息]用户不存在!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_PAY_USERNOTEXISTS);
        }
        else if(StringUtil.isEmpty(user.getRealName()))
        {
            logger.info("[获取用户当前的提现信息]用户尚未实名!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_USER_NOT_IDENTITYRZ,ErrorCode_API.ERROR_USER_NOT_IDENTITYRZ_MSG);
        }
        else if(StringUtil.isEmpty(user.getBankInfo()))
        {
            logger.info("[获取用户当前的提现信息]用户尚未绑定银行卡!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_USER_NOT_BINDBANK,ErrorCode_API.ERROR_USER_NOT_BINDBANK_MSG);
        }
        //获取用户账户
        UserAccount userAccount = userAccountMapper.queryUserAccountInfoByUserId(params.getAsLong("userId"));
        if(userAccount == null)
        {
            logger.info("[获取用户当前的提现信息]用户账户不存在!用户编号=" + params.getAsString("userId"));
            throw new ServiceException(ErrorCode_API.ERROR_USER_110007);
        }
        //检测系统当前提现功能是否可用
        String systxstatus = SysConfig.getString("WITHDRAW_STATUS_SWITCH");
        if("0".equals(systxstatus))
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"银行系统维护中，暂时不支持提现，请关注首页公告");
        }
        //校验当前时间段是否允许提现
        systxstatus = SysConfig.getString("WITHDRAW_TIME_RANGE");
        if(StringUtil.isNotEmpty(systxstatus) && !"0".equals(systxstatus))
        {
            Calendar current = Calendar.getInstance();
            int currentHour = current.get(Calendar.HOUR_OF_DAY);//当前时间-时
            int currentMinute = current.get(Calendar.MINUTE);//当前时间-分
            String[] timeCharacters = systxstatus.split(";");//提取时间特征
            int count = 0;
            for(String timeCharacter : timeCharacters)
            {
                //判断当前时间点是否在时间特征表述的范围内,只有当前时间在时间特征的范围内,才允许提现
                String[] times = timeCharacter.split("~");
                String[] timeStart = times[0].split(":");
                String[] timeEnd = times[1].split(":");
                if((currentHour == Integer.parseInt(timeStart[0]) && currentMinute >= Integer.parseInt(timeStart[1]))
                        || (currentHour == Integer.parseInt(timeEnd[0]) && currentMinute <= Integer.parseInt(timeEnd[1]))
                        || (currentHour > Integer.parseInt(timeStart[0]) && currentHour < Integer.parseInt(timeEnd[0])))
                {
                    count ++;
                }
            }
            if(count == 0)
            {
                throw new ServiceException(ErrorCode_API.SERVER_ERROR,"提现时间段为" + systxstatus.replace(";","或"));
            }
        }
        //设置提现银行信息
        Dto dataDto = new BaseDto();
        BankInfoVo bankInfoVo = user.getBankInfo();
        dataDto.put("bname",bankInfoVo.getBankName());//设置提现银行
        dataDto.put("logo",SysConfig.getHostStatic() + bankInfoVo.getLogo());//设置提现银行logo
        dataDto.put("bankno",bankInfoVo.getBankCard().substring(bankInfoVo.getBankCard().length() - 4));//设置银行卡后四位
        dataDto.put("bdesc","储蓄卡");//设置银行卡类型

        /**
         * 设置提现额度信息
         */
        //查询用户当天有效的提现记录
        Dto userpayQueryDto = new BaseDto();
        userpayQueryDto.put("userId",params.get("userId"));
        userpayQueryDto.put("payType","1");
        userpayQueryDto.put("minStatus","0");
        userpayQueryDto.put("maxStatus","3");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        userpayQueryDto.put("minCreateTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
        calendar.add(Calendar.DAY_OF_MONTH,1);
        userpayQueryDto.put("maxCreateTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
        List<UserPay> userPayList = userPayMapper.queryUserPays(userpayQueryDto);

        //设置当前可提现次数
        String minMoney = SysConfig.getString("MIN_WITHDRAW");//获取系统配置的提现最小金额
        dataDto.put("minMoney",StringUtil.isEmpty(minMoney)? 10D : minMoney);//设置提现最少金额
        dataDto.put("withDraw",String.format("%.2f",userAccount.getWithDraw()));//设置可提现金额
        int maxAmount = SysConfig.getInt("MAX_WITHDRAW_AMOUNT_OFDAY");//提取系统设置的一天最多提款次数
        int rnumber = 0;
        if(userPayList == null || userPayList.size() == 0)
        {
            rnumber = maxAmount;
        }
        else if(userPayList.size() >= maxAmount)
        {
            rnumber = 0;
        }
        else
        {
            rnumber = maxAmount - userPayList.size();
        }
        dataDto.put("rnumber",rnumber);//设置可提现次数

        //设置当前是否可以进行提现(0-不可提现 1-可提现)
        dataDto.put("txflag",(userAccount.getWithDraw() < Double.parseDouble(minMoney) || rnumber <= 0)? 0 : 1);
        String desc = "满足提现条件";
        if(userAccount.getWithDraw() < Double.parseDouble(minMoney)) {
            desc = "单笔提现金额不可低于" + minMoney + "元";
        }
        if(rnumber <= 0){
            desc = "当日可提现次数不可超过" + maxAmount + "次";
        }
        dataDto.put("txflagDesc", desc);

        //设置提现规则说明
        String ruledesc = SysConfig.getString("WITHDRAW_RULE_DESC");
        dataDto.put("rules",StringUtil.isEmpty(ruledesc)? "" : ruledesc);

        //设置返回数据
        resultBean.setData(dataDto);
        resultBean.setErrorCode(ErrorCode_API.SUCCESS);
    }

    /**
     * 更新用户累计消费金额
     * @param userId
     * @param money
     * @return
     */
    public int updateUserConsume(Long userId, Double money) {
        return userMapper.updateUserConsume(userId, money);
    }

    /**
     * 处理用户返利业务
     * @param scheme
     * @return
     */
    public void updateUserRebateAccount(Scheme scheme, Logger logger) throws ServiceException {
        try {
            if (StringUtil.isEmpty(scheme)) {
                return;
            }
            //查询用户返点比例
            UserRebate rebate = userMapper.queryUserRebateListForLotteryId(scheme.getSchemeUserId(), scheme.getLotteryId());
            if(StringUtil.isNotEmpty(rebate)) {//用户有返点
                UserRebateDetail rebateDetail = new UserRebateDetail();
                rebateDetail.setType(0);
                rebateDetail.setUserId(scheme.getSchemeUserId());
                rebateDetail.setLotteryId(scheme.getLotteryId());
                rebateDetail.setSchemeUserId(scheme.getSchemeUserId());
                rebateDetail.setSchemeOrderId(scheme.getSchemeOrderId());
                rebateDetail.setSchemeMoney(scheme.getSchemeMoney());
                rebateDetail.setRate(rebate.getRate());
                double fdMoney = CalculationUtils.parseMoney(CalculationUtils.mul(scheme.getSchemeMoney(), rebate.getRate())).doubleValue();
                UserAccount account = userAccountMapper.queryUserAccountInfoByUserId(scheme.getSchemeUserId());//账户信息
                if (StringUtil.isNotEmpty(account)) {
                    rebateDetail.setLastBalanceRebate(account.getBalanceBack());
                    rebateDetail.setCurrentRebateMoney(fdMoney);
                    Double balanceRebate = CalculationUtils.add(account.getBalanceBack(), fdMoney);
                    rebateDetail.setBalanceRebate(balanceRebate);
                    userMapper.insertUserRebateDetail(rebateDetail);//保存返利明细
                    userMapper.updateUserBack(scheme.getSchemeUserId(), fdMoney);//更新账户信息
                    logger.info("[用户返利任务处理] 方案号=" + scheme.getSchemeOrderId() + " 购彩用户=" + scheme.getSchemeUserId() + " 返利比例=" + rebate.getRate() + " 返利金额=" + fdMoney);
                }
            }

            //如果用户有上级代理 需要给代理返点
            if (scheme.getClientSource() == UserConstants.USER_PROXY_GENERAL) {
                if(StringUtil.isNotEmpty(scheme.getCouponId())) {//上级用户编号
                    User user = userMapper.queryUserInfoById(scheme.getCouponId().longValue());
                    if(StringUtil.isNotEmpty(user)) {
                        if (user.getIsSale() == UserConstants.USER_STATUS_AGENT) {//如果上级是代理
                            //查询代理返点比例
                            UserRebate rebateProxy = userMapper.queryUserRebateListForLotteryId(user.getId(), scheme.getLotteryId());
                            if (StringUtil.isNotEmpty(rebateProxy)) {
                                double prate = CalculationUtils.sub(rebateProxy.getRate(), (StringUtil.isNotEmpty(rebate)?rebate.getRate():0.00));
                                if(prate > 0) {
                                    UserRebateDetail rebateDetail = new UserRebateDetail();
                                    rebateDetail.setType(0);
                                    rebateDetail.setLotteryId(scheme.getLotteryId());
                                    rebateDetail.setSchemeOrderId(scheme.getSchemeOrderId());
                                    rebateDetail.setSchemeMoney(scheme.getSchemeMoney());
                                    rebateDetail.setSchemeUserId(scheme.getSchemeUserId());
                                    rebateDetail.setUserId(user.getId());
                                    rebateDetail.setRate(prate);
                                    double dlfdMoney = CalculationUtils.parseMoney(CalculationUtils.mul(scheme.getSchemeMoney(), prate)).doubleValue();
                                    UserAccount dlaccount = userAccountMapper.queryUserAccountInfoByUserId(user.getId());//账户信息
                                    if (StringUtil.isNotEmpty(dlaccount)) {
                                        rebateDetail.setLastBalanceRebate(dlaccount.getBalanceBack());
                                        rebateDetail.setCurrentRebateMoney(dlfdMoney);
                                        Double dlbalanceRebate = CalculationUtils.add(dlaccount.getBalanceBack(), dlfdMoney);
                                        rebateDetail.setBalanceRebate(dlbalanceRebate);
                                        userMapper.insertUserRebateDetail(rebateDetail);//保存代理返利明细
                                        userMapper.updateUserBack(user.getId(), dlfdMoney);//更新代理账户信息
                                        logger.info("[用户返利任务处理] 方案号=" + scheme.getSchemeOrderId() + " 上级代理=" + user.getId() + " 返利比例=" + prate + " 返利金额=" + dlfdMoney);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            scheme.setBackStatus(2);
            updateSchemeBackStatus(scheme);
        } catch (Exception e) {
            logger.error("[处理用户返利业务异常] userId=" + scheme.getSchemeUserId() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新方案返点状态
     * @param scheme
     * @return
     * @throws ServiceException
     * @throws Exception
     */
    public int updateSchemeBackStatus(Scheme scheme) throws Exception {
        if(StringUtil.isEmpty(scheme)) {
            return 0;
        }
        if(scheme.getSchemeType() == 1) {//追号方案
            return schemeMapper.updateZhuiHaoSchemeBackStatus(scheme.getId(), scheme.getBackStatus());
        } else {
            return schemeMapper.updateSchemeBackStatus(scheme.getId(), scheme.getBackStatus());
        }
    }

    /**
     * 给用户赠送优惠券
     * @author  mcdog
     * @param   params  参数对象(opmethod-业务方法 couponType-优惠券赠送类型)
     * @param   userId  用户编号
     */
    public synchronized void sendCoupon(Dto params,Long userId)
    {
        String method = StringUtil.isEmpty(params.get("opmethod"))? "" : ("[" + params.getAsString("opmethod") + "]");

        //验证活动类型是否非空
        if(StringUtil.isEmpty(params.get("couponType")))
        {
            return;
        }
        try
        {
            //判断当前是否有正在进行的指定类型的活动,如果有,则根据条件给用户添加优惠券
            Dto activityQueryParams = new BaseDto();
            Calendar calendar = Calendar.getInstance();
            activityQueryParams.put("currentTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE));
            activityQueryParams.put("activityType",params.get("activityType"));
            activityQueryParams.put("couponType",params.get("couponType"));
            List<Activity> activityList = activityMapper.queryActivitys(activityQueryParams);
            if(activityList != null && activityList.size() > 0)
            {
                //循环活动,给用户添加相应的优惠券,如果用户已经领取过优惠券,则不再添加
                User user = userMapper.queryUserInfoById(userId);//查询用户信息
                List<Dto> usercouponList = null;
                Dto couponQueryParams = new BaseDto();
                int czsHdCount = 0;//单次优惠享受充值送活动的次数(有多个充值送活动的时候,单次优惠只能享受一个充值送活动)
                for(Activity activity : activityList)
                {
                    try
                    {
                        //单次优惠只能享受一个充值送活动
                        if(activity.getCouponType() != null && activity.getCouponType().intValue() == 1 && czsHdCount > 0)
                        {
                            continue;
                        }
                        /**
                         * 查询用户本次活动优惠券的赠送记录,如果有赠送过,则不再参数与本次活动,否则,按规则赠送用户优惠券
                         */
                        //查询用户本次活动优惠券的赠送记录
                        couponQueryParams.put("userId",userId);
                        couponQueryParams.put("activityId",activity.getId());
                        usercouponList = userCouponMapper.queryUserCoupons(couponQueryParams);

                        //判断是否有赠送记录,如果没有赠送记录,则按规则赠送优惠券
                        if(usercouponList == null || usercouponList.size() == 0)
                        {
                            /**
                             * 判断用户是否实名,如果尚未实名,则先暂存用户活动优惠券补送表(待后续实名后再补送)
                             */
                            //尚未实名,将活动优惠券信息暂存
                            /*if(StringUtil.isEmpty(user.getRealName()))
                            {
                                ActivityCouponReissue couponReissue = new ActivityCouponReissue();
                                couponReissue.setActivityId(activity.getId());//设置活动编号
                                couponReissue.setUserId(userId);//设置用户编号
                                couponReissue.setSmoney(params.getAsDouble("smoney"));//设置金额
                                couponReissue.setCouponType(activity.getCouponType());//设置优惠券赠送类型(0-注册送 1-充值送)
                                couponReissue.setCouponMode(activity.getCouponMode());//设置优惠券赠送模式(0-固定模式 1-自定义模式)
                                couponReissue.setCouponIds(activity.getCouponIds());//设置活动优惠券赠送信息
                                couponReissue.setCouponExpireTime(activity.getCouponExpireTime());//设置优惠券过期时间
                                couponReissueMapper.insertActivityCouponReissue(couponReissue);//保存活动优惠券补送
                            }*/
                            //如果已实名,则按规则给用户赠送优惠券
                            /*else
                            {*/
                                Dto userCouponDto = new BaseDto();
                                userCouponDto.put("userId",userId);//设置用户编号
                                userCouponDto.put("type",2);//设置优惠券类型(0-发行限制期限 1-使用期限 2-无限制)
                                userCouponDto.put("activityId",activity.getId());//设置活动编号

                                //判断优惠券赠送模式,如果为固定模式
                                if(activity.getCouponMode() == 0)
                                {
                                    //给用户赠送本次活动的优惠券
                                    int tcount = 0;
                                    List<String> couponIdList = new ArrayList<String>();
                                    couponIdList.addAll(Arrays.asList(activity.getCouponIds().split(",")));//提取需要赠送的优惠券编号
                                    for(String couponId : couponIdList)
                                    {
                                        //如果活动优惠券有设置过期时间,则给用户优惠券设置过期时间
                                        if(activity.getCouponExpireTime() != null)
                                        {
                                            userCouponDto.put("expireTime",DateUtil.formatDate(activity.getCouponExpireTime(), DateUtil.DEFAULT_DATE_TIME));
                                            userCouponDto.put("type",1);//设置优惠券类型(0-发行限制期限 1-使用期限 2-无限制)
                                        }
                                        userCouponDto.put("couponId",couponId);//设置优惠券编号
                                        int count = userCouponMapper.addUserCoupon(userCouponDto);//添加用户优惠券
                                        if(count > 0)
                                        {
                                            logger.info(method + "成功赠送优惠券!优惠券编号=" + couponId + ",所属活动编号=" + activity.getId() + ",用户编号=" + userId);
                                        }
                                        else
                                        {
                                            logger.error(method + "赠送优惠券失败!优惠券编号=" + couponId + ",所属活动编号=" + activity.getId() + ",用户编号=" + userId);
                                        }
                                        tcount += count;
                                    }
                                    if(activity.getCouponType() != null && activity.getCouponType().intValue() == 1 && tcount > 0)
                                    {
                                        czsHdCount ++;
                                    }
                                }
                                //如果优惠券赠送模式为自定义模式
                                else if(activity.getCouponMode() == 1)
                                {
                                    JSONArray couponInfoArray = JSONArray.fromObject(activity.getCouponIds());//提取优惠券赠送的条件及优惠券信息
                                    if(couponInfoArray != null && couponInfoArray.size() > 0)
                                    {
                                        int tcount = 0;
                                        double smoney = params.getAsDouble("smoney");
                                        for(Object object : couponInfoArray)
                                        {
                                            JSONObject couponInfo = JSONObject.fromObject(object);
                                            Object minczobj = couponInfo.get("mincz");//提取金额范围-最小金额
                                            Object maxczobj = couponInfo.get("maxcz");//提取金额范围-最大金额

                                            //判断金额是否满足送优惠券的条件
                                            boolean flag = false;//金额是否满足送优惠券的条件 true-满足 false-不满足
                                            if(StringUtil.isEmpty(minczobj) && StringUtil.isEmpty(maxczobj))
                                            {
                                                continue;
                                            }
                                            if(StringUtil.isNotEmpty(minczobj) && StringUtil.isNotEmpty(maxczobj)
                                                    && smoney >= couponInfo.getDouble("mincz") && smoney <= couponInfo.getDouble("maxcz"))
                                            {
                                                flag = true;
                                            }
                                            else if(StringUtil.isEmpty(minczobj) && smoney <= couponInfo.getDouble("maxcz"))
                                            {
                                                flag = true;
                                            }
                                            else if(StringUtil.isEmpty(maxczobj) && smoney >= couponInfo.getDouble("mincz"))
                                            {
                                                flag = true;
                                            }
                                            if(flag)
                                            {
                                                List<String> couponIdList = new ArrayList<String>();
                                                couponIdList.addAll(Arrays.asList(couponInfo.getString("couponIds").split(",")));//提取需要赠送的优惠券编号
                                                for(String couponId : couponIdList)
                                                {
                                                    try
                                                    {
                                                        //如果活动优惠券有设置过期时间,则给用户优惠券设置过期时间
                                                        if(activity.getCouponExpireTime() != null)
                                                        {
                                                            userCouponDto.put("expireTime",DateUtil.formatDate(activity.getCouponExpireTime(), DateUtil.DEFAULT_DATE_TIME));
                                                            userCouponDto.put("type",1);//设置优惠券类型(0-发行限制期限 1-使用期限 2-无限制)
                                                        }
                                                        userCouponDto.put("couponId",couponId);//设置优惠券编号
                                                        int count = userCouponMapper.addUserCoupon(userCouponDto);//添加用户优惠券
                                                        if(count > 0)
                                                        {
                                                            logger.info(method + "成功赠送优惠券!优惠券编号=" + couponId + ",所属活动编号=" + activity.getId() + ",用户编号=" + userId);
                                                        }
                                                        else
                                                        {
                                                            logger.error(method + "赠送优惠券失败!优惠券编号=" + couponId + ",所属活动编号=" + activity.getId() + ",用户编号=" + userId);
                                                        }
                                                        tcount += count;
                                                    }
                                                    catch (Exception e)
                                                    {
                                                        logger.error(method + "赠送优惠券发生异常!优惠券编号=" + couponId + ",所属活动编号=" + activity.getId() + ",用户编号=" + userId + ",异常信息:" + e);
                                                    }
                                                }
                                                break;//只匹配一个金额范围
                                            }
                                        }
                                        if(activity.getCouponType() != null && activity.getCouponType().intValue() == 1 && tcount > 0)
                                        {
                                            czsHdCount ++;
                                        }
                                    }
                                }
                            /*}*/
                        }
                        //有赠送记录,则不再参与本次活动
                        else
                        {
                            logger.info(method + "用户已经参加过本次活动!本次不再参加!" + "所属活动编号=" + activity.getId() + ",用户编号=" + userId);
                        }
                    }
                    catch(Exception e)
                    {
                        logger.error(method + "赠送优惠券发生异常!所属活动编号=" + activity.getId() + ",用户编号=" + userId + ",异常信息:" + e);
                    }
                }
            }
            else
            {
                logger.info(method + "当前无优惠活动!");
            }
        }
        catch(Exception e)
        {
            logger.error(method + "赠送优惠券发生异常!用户编号=" + userId + ",异常信息:" + e);
        }
    }

    /**
     * 给用户赠送优惠券(优惠券补送)
     * @author  mcdog
     * @param   params  参数对象(opmethod-业务方法)
     * @param   userId  用户编号
     */
    public synchronized void sendCouponReissue(Dto params,Long userId)
    {
        String method = StringUtil.isEmpty(params.get("opmethod"))? "" : ("[" + params.getAsString("opmethod") + "]");
        try
        {
            //查询该用户当前是否有待补送的优惠券
            Dto queryParams = new BaseDto();
            queryParams.put("userId","" + userId);
            queryParams.put("couponType",params.get("couponType"));
            queryParams.put("status","0");
            List<ActivityCouponReissue> couponReissueList = couponReissueMapper.queryActivityCouponReissues(queryParams);
            if(couponReissueList != null && couponReissueList.size() > 0)
            {
                //循环活动,给用户添加相应的优惠券,如果用户已经领取过优惠券,则不再赠送
                List<Dto> usercouponList = null;
                Dto couponQueryParams = new BaseDto();
                for(ActivityCouponReissue couponReissue : couponReissueList)
                {
                    try
                    {
                        /**
                         * 查询用户本次活动优惠券的赠送记录,如果有赠送过,则不再参数与本次活动,否则,按规则赠送用户优惠券
                         */
                        //查询用户本次活动优惠券的赠送记录
                        couponQueryParams.put("userId",userId);
                        couponQueryParams.put("activityId",couponReissue.getActivityId());
                        usercouponList = userCouponMapper.queryUserCoupons(couponQueryParams);

                        //判断是否有赠送记录,如果没有赠送记录,则按规则赠送优惠券
                        if(usercouponList == null || usercouponList.size() == 0)
                        {
                            Dto userCouponDto = new BaseDto();
                            userCouponDto.put("userId",userId);//设置用户编号
                            userCouponDto.put("type",2);//设置优惠券类型(0-发行限制期限 1-使用期限 2-无限制)
                            userCouponDto.put("activityId",couponReissue.getActivityId());//设置活动编号

                            //判断优惠券赠送模式,如果为固定模式
                            if(couponReissue.getCouponMode() == 0)
                            {
                                //给用户赠送本次活动的优惠券
                                List<String> couponIdList = new ArrayList<String>();
                                couponIdList.addAll(Arrays.asList(couponReissue.getCouponIds().split(",")));//提取需要赠送的优惠券编号
                                for(String couponId : couponIdList)
                                {
                                    userCouponDto.put("couponId",couponId);//设置优惠券编号
                                    //如果活动优惠券有设置过期时间,则给用户优惠券设置过期时间
                                    if(couponReissue.getCouponExpireTime() != null)
                                    {
                                        userCouponDto.put("expireTime",DateUtil.formatDate(couponReissue.getCouponExpireTime(), DateUtil.DEFAULT_DATE_TIME));
                                        userCouponDto.put("type",1);//设置优惠券类型(0-发行限制期限 1-使用期限 2-无限制)
                                    }
                                    int count = userCouponMapper.addUserCoupon(userCouponDto);//添加用户优惠券
                                    if(count > 0)
                                    {
                                        logger.info(method + "赠送优惠券成功!优惠券编号=" + couponId + ",所属活动编号=" + couponReissue.getActivityId() + ",用户编号=" + userId);
                                    }
                                    else
                                    {
                                        logger.error(method + "赠送优惠券失败!优惠券编号=" + couponId + ",所属活动编号=" + couponReissue.getActivityId() + ",用户编号=" + userId);
                                    }
                                }
                            }
                            //如果优惠券赠送模式为自定义模式
                            else if(couponReissue.getCouponMode() == 1)
                            {
                                JSONArray couponInfoArray = JSONArray.fromObject(couponReissue.getCouponIds());//提取优惠券赠送的条件及优惠券信息
                                if(couponInfoArray != null && couponInfoArray.size() > 0)
                                {
                                    double smoney = couponReissue.getSmoney() == null? 0d : couponReissue.getSmoney();
                                    for(Object object : couponInfoArray)
                                    {
                                        JSONObject couponInfo = JSONObject.fromObject(object);
                                        Object minczobj = couponInfo.get("mincz");//提取金额范围-最小金额
                                        Object maxczobj = couponInfo.get("maxcz");//提取金额范围-最大金额

                                        //判断金额是否满足送优惠券的条件
                                        boolean flag = false;//金额是否满足送优惠券的条件 true-满足 false-不满足
                                        if(StringUtil.isEmpty(minczobj) && StringUtil.isEmpty(maxczobj))
                                        {
                                            continue;
                                        }
                                        if(StringUtil.isNotEmpty(minczobj) && StringUtil.isNotEmpty(maxczobj)
                                                && smoney >= couponInfo.getDouble("mincz") && smoney <= couponInfo.getDouble("maxcz"))
                                        {
                                            flag = true;
                                        }
                                        else if(StringUtil.isEmpty(minczobj) && smoney <= couponInfo.getDouble("maxcz"))
                                        {
                                            flag = true;
                                        }
                                        else if(StringUtil.isEmpty(maxczobj) && smoney >= couponInfo.getDouble("mincz"))
                                        {
                                            flag = true;
                                        }
                                        if(flag)
                                        {
                                            List<String> couponIdList = new ArrayList<String>();
                                            couponIdList.addAll(Arrays.asList(couponInfo.getString("couponIds").split(",")));//提取需要赠送的优惠券编号
                                            for(String couponId : couponIdList)
                                            {
                                                try
                                                {
                                                    userCouponDto.put("couponId",couponId);//设置优惠券编号
                                                    //如果活动优惠券有设置过期时间,则给用户优惠券设置过期时间
                                                    if(couponReissue.getCouponExpireTime() != null)
                                                    {
                                                        userCouponDto.put("expireTime",DateUtil.formatDate(couponReissue.getCouponExpireTime(), DateUtil.DEFAULT_DATE_TIME));
                                                        userCouponDto.put("type",1);//设置优惠券类型(0-发行限制期限 1-使用期限 2-无限制)
                                                    }
                                                    int count = userCouponMapper.addUserCoupon(userCouponDto);//添加用户优惠券
                                                    if(count > 0)
                                                    {
                                                        logger.info(method + "成功赠送优惠券!优惠券编号=" + couponId + ",所属活动编号=" + couponReissue.getActivityId() + ",用户编号=" + userId);
                                                    }
                                                    else
                                                    {
                                                        logger.error(method + "赠送优惠券失败!优惠券编号=" + couponId + ",所属活动编号=" + couponReissue.getActivityId() + ",用户编号=" + userId);
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    logger.error(method + "赠送优惠券发生异常!优惠券编号=" + couponId + ",所属活动编号=" + couponReissue.getActivityId() + ",用户编号=" + userId + ",异常信息:" + e);
                                                }
                                            }
                                            break;//只匹配一个金额范围
                                        }
                                    }
                                }
                            }
                        }
                        //有赠送记录,则不再参与本次活动
                        else
                        {
                            logger.info(method + "用户已经参加过本次活动!本次不再参加!" + "所属活动编号=" + couponReissue.getActivityId() + ",用户编号=" + userId);
                        }

                        //更新活动优惠券补送状态
                        couponReissueMapper.updateActivityCouponReissue(new BaseDto("id",couponReissue.getId()));
                    }
                    catch(Exception e)
                    {
                        logger.error(method + "赠送优惠券发生异常!所属活动编号=" + couponReissue.getActivityId() + ",用户编号=" + userId + ",异常信息:" + e);
                    }
                }
            }
            else
            {
                logger.info(method + "当前无待补送的优惠券!");
            }
        }
        catch(Exception e)
        {
            logger.error(method + "赠送优惠券发生异常!用户编号=" + userId + ",异常信息:" + e);
        }
    }

    /**
     * 获取用户优惠券信息
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getUserCoupons(Dto params, ResultBean result) throws ServiceException,Exception
    {
        /**
         * 设置查询参数
         */
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
        //判断查询类型(qtype,0-未使用 1-已使用 2-过期 为空时查询全部)
        if(StringUtil.isNotEmpty(params.get("qtype")))
        {
            //未使用
            int qtype = params.getAsInteger("qtype");//提取查询类型
            if(qtype == 0)
            {
                params.put("useStatus","1");
            }
            //已使用
            else if(qtype == 1)
            {
                params.put("useStatus","2");
            }
            //过期
            else if(qtype == 2)
            {
                params.put("maxUseStatus","0");
            }
        }

        /**
         * 查询并设置返回数据
         */
        Map<String,Object> dataMap = new HashMap<String,Object>();
        List<Dto> dataList = new ArrayList<Dto>();
        List<Dto> couponDataList = userCouponMapper.queryUserCoupons(params);//查询记录
        if(couponDataList != null && couponDataList.size() > 0)
        {
            Dto data = null;
            for(Dto couponData : couponDataList)
            {

                //封装用户优惠券前端展示对象
                data = new BaseDto();
                int status = couponData.getAsInteger("cuStatus");
                data.put("cuid",couponData.get("cuid"));//用户优惠券id
                data.put("name",couponData.getAsString("cName"));//优惠券名称
                data.put("faceValue",couponData.getAsString("cMoney"));//优惠券面值
                data.put("status",status <= 0? 0 : status);//优惠券状态 0-过期 1-未使用 2-已使用
                data.put("svalid",StringUtil.isEmpty(couponData.get("cuExpireTime"))? "" : ("过期时间：" + couponData.getAsString("cuExpireTime")));//优惠券有效期描述
                dataList.add(data);
            }
        }
        dataMap.put("list",dataList);

        //如果有分页标识,则查询总记录数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = userCouponMapper.queryUserCouponsCount(params);//查询总记录数
            long psize = params.getAsLong("psize");
            dataMap.put("tsize",tsize);//设置总记录数
            dataMap.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }

        //设置返回数据
        result.setData(dataMap);
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 根据用户编号查询用户信息
     * @param userId
     * @return
     * @throws ServiceException
     */
    public User queryUserInfoByAward(Long userId) throws ServiceException {
        try {
            //用户编号
            if (StringUtil.isEmpty(userId)) {
                return null;
            }
            return userMapper.queryUserInfoById(userId);
        } catch (Exception e) {
            logger.error("[根据用户编号查询用户信息异常] userId=" + userId + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 销售月销量统计
     * @param beginTime
     * @param endTime
     * @param logger
     */
    public void statisSellMoneyCommission(Date beginTime, Date endTime, Logger logger){
        try {
            Dto params = new BaseDto();
            params.put("isSale", "1");
            List<Dto> sellUserList = userMapper.queryUserList(params);//查询所有销售及其返点列表
            if(StringUtil.isEmpty(sellUserList)) {
                return;
            }
            for(Dto dto : sellUserList) {
                logger.info("[销售月销量统计] 开始统计销售(userId=" + dto.getAsLong("id") + ")销量");
                List<UserRebate> rebateList = userMapper.queryUserRebateList(dto.getAsLong("id"));
                if(StringUtil.isEmpty(rebateList)) {
                    continue;//未配置返点比例 不计算提成
                }
                Map<String, Double> userRebateMap = formartUserRebate(rebateList);
                params.put("userId", dto.getAsLong("id"));
                params.put("beginTime", beginTime);
                params.put("endTime", endTime);
                Date saleTime = dto.getAsTimestamp("isSaleTime");
                if(saleTime.getTime() > beginTime.getTime()) {
                    params.put("beginTime", saleTime);
                }
                double sellOwnMoney = 0;//销售自己购彩返点
                double totalSell = 0;//销售自己总销量
                List<Dto> sellOwn = userMapper.querySellOwnTotalMoney(params);
                if(StringUtil.isNotEmpty(sellOwn)) {
                    for (Dto so : sellOwn) {
                        Double rate = userRebateMap.get(so.getAsString("lotteryId"));
                        if(StringUtil.isEmpty(rate)) {
                            continue;
                        }
                        totalSell += so.getAsDoubleValue("money");
                        sellOwnMoney += CalculationUtils.muld(so.getAsDoubleValue("money"), rate);
                    }
                }

                params.put("beginTime", beginTime);
                double totalMoney = userMapper.querySellTotalMoney(params);
                Map<String, Double> intervalMap = initIntervalMap(SysConfig.getSellIntervalRate());
                double sellLowerUserMoney = 0;//销售下级用户贡献提成
                double totalSellLowerUser = 0;//下级用户总销量
                List<Dto> sellLowerUser = userMapper.querySellLowerUserTotalMoney(params);
                if(StringUtil.isNotEmpty(sellLowerUser)) {
                    for (Dto so : sellLowerUser) {
                        if(SysConfig.getCommissionRate() < so.getAsDoubleValue("rate")) {
                            continue;//销售提成最高点位小于用户返点，则不计算提成
                        }
                        totalSellLowerUser += so.getAsDoubleValue("money");
                        sellLowerUserMoney += (SysConfig.getCommissionRate()-so.getAsDoubleValue("rate"))*getIntervalRate(intervalMap, totalMoney)*so.getAsDoubleValue("money");
                    }
                }
                double sellProxyUserMoney = 0;//销售下级代理及代理用户贡献提成
                double totalProxyUser = 0;//下级代理及代理用户总销量
                List<Dto> sellProxyUser = userMapper.querySellProxyUserTotalMoney(params);
                if(StringUtil.isNotEmpty(sellProxyUser)) {
                    for (Dto sp : sellProxyUser) {
                        if(SysConfig.getCommissionRate() < sp.getAsDoubleValue("rate")) {
                            continue;//销售提成最高点位小于用户返点，则不计算提成
                        }
                        totalProxyUser += sp.getAsDoubleValue("money");
                        sellProxyUserMoney += (SysConfig.getCommissionRate()-sp.getAsDoubleValue("rate"))*getIntervalRate(intervalMap, totalMoney)*sp.getAsDoubleValue("money");
                    }
                }

                params.put("month", DateUtil.formatDate(beginTime, DateUtil.DEFAULT_DATE2));
                params.put("proxyMoney", CalculationUtils.spValue(totalProxyUser));
                params.put("proxyMoneySellTc", CalculationUtils.spValue(sellProxyUserMoney));
                params.put("userMoney", CalculationUtils.spValue(totalSellLowerUser));
                params.put("userMoneySellTc", CalculationUtils.spValue(sellLowerUserMoney));
                params.put("saleZjMoney", CalculationUtils.spValue(totalSell));
                params.put("saleZjMoneyTc", CalculationUtils.spValue(sellOwnMoney));
                params.put("totalMoney", CalculationUtils.spValue(totalProxyUser+totalSellLowerUser+totalSell));
                params.put("totalMoneySellTc", CalculationUtils.spValue(sellProxyUserMoney+sellLowerUserMoney+sellOwnMoney));
                params.put("totalMoneyDetail", "");
                int row = userMapper.insertUserSellMoney(params);
                if(row > 0) {
                    logger.info("[销售月销量统计] 销售编号=" + dto.getAsLong("id") + " 销售昵称=" + dto.getAsString("nickName") + " 销量统计完成");
                }
            }
        } catch (Exception e) {
            logger.error("[销售月销量统计异常] errorDesc=" + e.getMessage());
        }
    }

    /**
     * 初始化用户神单统计信息
     * @param userId
     */
    public void saveFollowUserStatis(Long userId) throws ServiceException {
        try {
            int row = userFollowMapper.queryUserFollowIsExists(LotteryConstants.JCZQ, userId);
            if(row == 0) {
                userFollowMapper.insertUserFollowStatis(LotteryConstants.JCZQ, userId);
            }
            /*row = userFollowMapper.queryUserFollowIsExists(LotteryConstants.JCLQ, userId);
            if(row == 0) {
                userFollowMapper.insertUserFollowStatis(LotteryConstants.JCLQ, userId);
            }*/
        } catch (Exception e){
            logger.error("[初始化用户神单统计信息异常] userId=" + userId, e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 将用户返点LIST转换为MAP
     * @param rebateList
     * @return
     */
    private static Map<String, Double> formartUserRebate(List<UserRebate> rebateList) {
        if(StringUtil.isEmpty(rebateList)) {
            return null;
        }
        Map<String, Double> doubleMap = new HashMap<>();
        for(UserRebate rebate : rebateList) {
            doubleMap.put(rebate.getLotteryId(), rebate.getRate());
        }
        return doubleMap;
    }

    /**
     * 格式化提成百分比区间比例范围-返回map
     * @param interval
     * @return
     */
    private static Map<String, Double> initIntervalMap(String interval) {
        Map<String, Double> map = new HashMap<>();
        if(StringUtil.isEmpty(interval)) {
            return map;
        }
        String[] bonusRates = interval.split("\\/");
        for(String rate : bonusRates) {
            String[] rates = rate.split("\\$");
            if(rates.length != 3) {
                continue;
            }
            if(rates[1].equals("*")) {
                rates[1] = "999999999";
            }
            if(StringUtil.parseDouble(rates[0]) >= StringUtil.parseDouble(rates[1])) {
                continue;//开始金额不能大于结束金额
            }
            map.put(StringUtil.parseDouble(rates[0]) + "|" + StringUtil.parseDouble(rates[1]), StringUtil.parseDouble(rates[2]));
        }
        return map;
    }

    /**
     * 根据销量返回区间比例
     * @param money
     * @return
     */
    private static double getIntervalRate(Map<String, Double> map, double money) {
        if(StringUtil.isEmpty(map) || money <= 0) {
            return 0;
        }
        Iterator<Map.Entry<String, Double>> iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Double> entry = iterator.next();
            String[] keys = entry.getKey().split("\\|");
            if(money > StringUtil.parseDouble(keys[0]) && money <= StringUtil.parseDouble(keys[1])) {
                return entry.getValue();
            }
        }
        return 0;
    }

    /**
     * 获取敏感词列表
     */
    public List<SensitiveWord> getSendsitiveWordList() {
        List<SensitiveWord> sensitiveWordList = null;
        Object object = redis.getObject(Constants.SENSITIVEWORD_KEY);//从缓存中获取关键词列表
        if(StringUtil.isNotEmpty(object)) {
            sensitiveWordList = (List<SensitiveWord>) object;
        } else {
            sensitiveWordList = sensitiveWordMapper.querySensitiveWordList(new SensitiveWord());
            redis.set(Constants.SENSITIVEWORD_KEY, sensitiveWordList, 24*60*60);
        }
        return sensitiveWordList;
    }
}