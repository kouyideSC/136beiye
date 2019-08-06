package com.caipiao.admin.service.user;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.encrypt.RSA;
import com.caipiao.common.user.UserUtils;
import com.caipiao.common.util.DoubleUtil;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.admin.account.AccountMapper;
import com.caipiao.dao.admin.login.LoginMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserRebate;
import com.caipiao.domain.user.UserRebateDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.caipiao.common.encrypt.RSA.decryptByPrivateKey;

/**
 * 后台用户相关服务
 * Created by kouyi on 2017/11/24.
 */
@Service("userService")
public class UserService
{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 查询用户列表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryUserList(Dto params)
    {
        return userMapper.queryUserList(params);
    }

    /**
     * 查询用户列表-总条数（后台管理）
     * @author kouyi
     */
    public int queryUserListCount(Dto params)
    {
        return userMapper.queryUserListCount(params);
    }

    /**
     * 查询用户信息（后台管理）
     * @author kouyi
     */
    public User queryUserInfo(Dto params) throws Exception
    {
        return userMapper.queryUserInfoById(params.getAsLong("id"));
    }

    /**
     * 根据手机号查询用户信息（后台管理）
     * @author kouyi
     */
    public User queryUserInfoByMobile(String mobile) throws Exception
    {
        return userMapper.queryUserInfoByMobile(mobile);
    }

    /**
     * 更新用户信息（后台管理）
     * @author kouyi
     */
    public int updateUserInfoByAdmin(Dto params) throws Exception
    {
        Integer isSale = params.getAsInteger("isSale");
        if(StringUtil.isNotEmpty(isSale) && isSale > 0) {
            String code = params.getAsString("code");
            if(StringUtil.isEmpty(code)) {
                code = NumberUtil.randomCode4();//第一次
                //是否已经存在
                if(StringUtil.isNotEmpty(userMapper.queryUserCodeIsExists(code))) {
                    code = NumberUtil.randomCode4();//第二次
                    //是否还存在
                    if(StringUtil.isNotEmpty(userMapper.queryUserCodeIsExists(code))) {
                        code = NumberUtil.randomCode4();//第三次
                    }
                }
                params.put("userCode", code);
            }
        }
        return userMapper.updateUserInfoByAdmin(params);
    }

    /**
     * 查询用户日报表数据（后台管理）
     * @author kouyi
     */
    public List<Dto> queryUserDayStatis(Dto params)
    {
        return userMapper.queryUserDayStatis(params);
    }

    /**
     * 查询用户日报表数据-总条数（后台管理）
     * @author kouyi
     */
    public int queryUserDayStatisCount(Dto params)
    {
        return userMapper.queryUserDayStatisCount(params);
    }

    /**
     * 用户返点比例查询
     * @param userId
     * @return
     */
    public List<Dto> queryUserLotteryRebateList(Long userId)
    {
        return userMapper.queryUserLotteryRebateList(userId);
    }

    /**
     * 查询销售下级用户列表（后台管理）
     * @author kouyi
     */
    public List<Dto> querySaleLowerUserList(Dto params)
    {
        return userMapper.querySaleLowerUserList(params);
    }

    /**
     * 查询销售下级用户列表-总条数（后台管理）
     * @author kouyi
     */
    public int querySaleLowerUserCount(Dto params)
    {
        return userMapper.querySaleLowerUserCount(params);
    }

    /**
     * 管理员-编辑用户返点（后台管理）
     * @author kouyi
     */
    public synchronized int saveUserRebateByAdmin(Dto params)
    {
        int result = 0;
        String userIds = params.getAsString("userId");
        String lotteryIds = params.getAsString("lotteryId");
        String rates = params.getAsString("rate");
        String ids = params.getAsString("id");
        if(StringUtil.isEmpty(userIds) || StringUtil.isEmpty(lotteryIds) || StringUtil.isEmpty(rates) || StringUtil.isEmpty(ids)) {
            return result;
        }

        String[] uids = userIds.split("\\,");
        String[] lotts = lotteryIds.split("\\,");
        String[] rts = rates.split(",", -1);
        String[] ds = ids.split("\\,", -1);
        for(int x=0; x<lotts.length; x++) {
            if(StringUtil.isEmpty(rts[x])) {
                continue;//未设置
            }

            double rt = DoubleUtil.roundNoDouble(Double.parseDouble(rts[x]),3);
            if(rt < 0) {
                continue;
            }
            UserRebate rabate = new UserRebate();
            if(StringUtil.isNotEmpty(ds[x])) {
                rabate.setId(Long.parseLong(ds[x]));
            }
            rabate.setLotteryId(lotts[x]);
            rabate.setUserId(Long.parseLong(uids[x]));
            rabate.setRate(rt);
            int row = 0;
            if(StringUtil.isEmpty(rabate.getId())) {
                row = userMapper.insertUserRebate(rabate);
            } else {
                row = userMapper.updateUserRebate(rabate);
            }
            result += row;
        }
        return result;
    }

    /**
     * 编辑用户返点（后台管理）
     * @author kouyi
     */
    public synchronized int saveUserRebate(Dto params)
    {
        int result = 0;
        String userIds = params.getAsString("userId");
        String lotteryIds = params.getAsString("lotteryId");
        String rates = params.getAsString("rate");
        String ids = params.getAsString("id");
        if(StringUtil.isEmpty(userIds) || StringUtil.isEmpty(lotteryIds) || StringUtil.isEmpty(rates) || StringUtil.isEmpty(ids)) {
            return result;
        }

        String[] uids = userIds.split("\\,");
        String[] lotts = lotteryIds.split("\\,");
        String[] rts = rates.split(",", -1);
        String[] ds = ids.split("\\,", -1);
        for(int x=0; x<lotts.length; x++) {
            if(StringUtil.isEmpty(rts[x])) {
                continue;//未设置
            }
            //判断销售能设置的最大返点数
            String mobile = params.getAsString("sale_mobile");
            UserRebate reb = userMapper.querySaleMaxRateRange(mobile, lotts[x],params.getAsString("isSale"));
            if(StringUtil.isEmpty(reb)) {
                continue;//当前彩种设置失败-销售管理员未分配返点数-该销售无法为用户设置返点
            }
            double rt = DoubleUtil.roundNoDouble(Double.parseDouble(rts[x]),3);
            if(rt < 0 || reb.getRate() < rt) {
                continue;//当前彩种设置失败-设置返点超出该销售能给用户设置的最大返点值
            }
            UserRebate rabate = new UserRebate();
            if(StringUtil.isNotEmpty(ds[x])) {
                rabate.setId(Long.parseLong(ds[x]));
            }
            rabate.setLotteryId(lotts[x]);
            rabate.setUserId(Long.parseLong(uids[x]));
            rabate.setRate(rt);
            int row = 0;
            if(StringUtil.isEmpty(rabate.getId()))
            {
                UserRebate userRebate = userMapper.queryUserRebateListForLotteryId(Long.parseLong(uids[x]),lotts[x]);//查询该用户已设置的返点信息
                if(userRebate == null)
                {
                    row = userMapper.insertUserRebate(rabate);//新增用户返点设置信息
                }
                else
                {
                    rabate.setUserId(userRebate.getUserId());
                    row = userMapper.updateUserRebate(rabate);//更新用户返点设置信息
                }
            }
            else
            {
                row = userMapper.updateUserRebate(rabate);//更新用户返点设置信息
            }
            result += row;
        }
        return result;
    }

    /**
     * 销售下属用户转出到新的销售员名下（后台管理）
     * @author kouyi
     */
    public int updateSaleUserChange(Dto params)
    {
        return userMapper.updateSaleUserChange(params);
    }

    /**
     * 将用户绑定上级归属（后台管理）
     * @author kouyi
     */
    public synchronized int updateUserHigherUser(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.getAsLong("uid")) || StringUtil.isEmpty(params.getAsString("smobile"))) {
            return 0;
        }
        User user = userMapper.queryUserInfoById(params.getAsLong("uid"));//源用户
        if(StringUtil.isEmpty(user)) {
            params.put("dmsg", "绑定源用户不存在");
            return -1;
        }
        if(user.getIsSale() == UserConstants.USER_PROXY_SALE) {
            params.put("dmsg", "不能为销售员绑定上级");
            return -1;//不能为销售绑定上级
        }
        User userTarget = userMapper.queryUserInfoByMobile(params.getAsString("smobile"));//目标用户
        if(StringUtil.isEmpty(userTarget)) {
            params.put("dmsg", "绑定目标用户不存在");
            return -1;
        }
        if(user.getIsSale() == UserConstants.USER_STATUS_AGENT && userTarget.getIsSale() != UserConstants.USER_PROXY_SALE) {
            params.put("dmsg", "代理只能绑定给销售员");
            return -1;//代理只能绑定给销售
        }
        return userMapper.updateUserHigherUser(params);
    }

    /**
     * 将用户设置为代理
     * @param params
     * @return
     */
    public synchronized int updateUserProxy(Dto params) throws Exception {
        Integer isSale = params.getAsInteger("isSale");
        if(StringUtil.isNotEmpty(isSale) && isSale == 2) {
            String code = params.getAsString("code");
            if(StringUtil.isEmpty(code)) {
                code = NumberUtil.randomCode4();//第一次
                //是否已经存在
                if(StringUtil.isNotEmpty(userMapper.queryUserCodeIsExists(code))) {
                    code = NumberUtil.randomCode4();//第二次
                    //是否还存在
                    if(StringUtil.isNotEmpty(userMapper.queryUserCodeIsExists(code))) {
                        code = NumberUtil.randomCode4();//第三次
                    }
                }
                params.put("userCode", code);
            }
        }
        return userMapper.updateSetUserProxy(params);
    }

    /**
     * 查询销售代理用户列表（后台管理）
     * @author kouyi
     */
    public List<Dto> querySaleProxyUserList(Dto params)
    {
        return userMapper.querySaleProxyUserList(params);
    }

    /**
     * 查询销售代理用户列表-总条数（后台管理）
     * @author kouyi
     */
    public int querySaleProxyUserCount(Dto params)
    {
        return userMapper.querySaleProxyUserCount(params);
    }

    /**
     * 查询销售人员列表（后台管理）
     * @author kouyi
     */
    public List<Dto> querySaleUserList(Dto params)
    {
        return userMapper.querySaleUserList(params);
    }

    /**
     * 查询销售人员列表-总条数（后台管理）
     * @author kouyi
     */
    public int querySaleUserCount(Dto params)
    {
        return userMapper.querySaleUserCount(params);
    }

    /**
     * 查询用户返点明细
     * @param params
     * @return
     */
    public List<Dto> queryUserBackDetail(Dto params) {
        return userMapper.queryUserBackDetail(params);
    }

    /**
     * 查询销售代理用户列表-总条数（后台管理）
     * @author kouyi
     */
    public int queryUserBackDetailCount(Dto params)
    {
        return userMapper.queryUserBackDetailCount(params);
    }

    /**
     * 查询销售当月和历史总销量
     * @param mobile
     * @return
     */
    public Dto queryUserSaleSumMoney(String mobile) {
        return userMapper.queryUserSaleSumMoney(mobile);
    }

    /**
     * 查询销售月销量明细
     * @param mobile
     * @return
     */
    public List<Dto> queryUserSaleMoneyDetail(String mobile) {
        return userMapper.queryUserSaleMoneyDetail(mobile);
    }

    /**
     * 查询用户返利列表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryUserFanliList(Dto params)
    {
        return userMapper.queryUserFanliList(params);
    }

    /**
     * 查询用户返利数据总数（后台管理）
     * @author kouyi
     */
    public List<Dto> queryUserFanliListCount(Dto params)
    {
        return userMapper.queryUserFanliListCount(params);
    }

    /**
     * 修改帐户密码
     * @author	sjq
     */
    public int editPassword(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("oldpassword")))
        {
            params.put("dmsg","原密码不能为空!");
            return 0;
        }
        if(StringUtil.isEmpty(params.get("newpassword")))
        {
            params.put("dmsg","新密码不能为空!");
            return 0;
        }

        /**
         * 判断原密码是否正确
         */
        Integer isSale = params.getAsInteger("isSale");
        if(isSale == null)
        {
            params.put("dmsg","未知的帐号!");
            return 0;
        }
        //如果帐号类型为管理平台帐号
        if(-1 == isSale)
        {
            //校验原密码是否正确
            BaseDto userDto = loginMapper.queryLoginUsers(new BaseDto("id",params.get("opaccountId")));
            if(userDto == null)
            {
                params.put("dmsg","未知的帐号!");
                return 0;
            }
            if(!userDto.getAsString("password").equals(MD5.md5(params.getAsString("oldpassword"))))
            {
                params.put("dmsg","原密码错误!");
                return 0;
            }
            //修改密码
            Dto updateAccountDto = new BaseDto("id",params.get("opaccountId"));
            updateAccountDto.put("password",MD5.md5(params.getAsString("newpassword")));
            updateAccountDto.put("opaccountId",params.get("opaccountId"));
            int count = accountMapper.updateAccountForPwd(updateAccountDto);//修改密码
            return count;
        }
        //如果帐号类型为销售/代理
        else if(1 == isSale || 2 == isSale)
        {
            //校验旧密码
            String oldpassword = params.getAsString("oldpassword");
            oldpassword = RSA.decryptByPrivateKey(oldpassword);
            if(!UserUtils.checkPassword(oldpassword))
            {
                params.put("dmsg","原密码不合法!");
                return 0;
            }
            //校验新密码
            String newpassword = params.getAsString("newpassword");
            newpassword = RSA.decryptByPrivateKey(newpassword);
            if(!UserUtils.checkPassword(newpassword))
            {
                params.put("dmsg","新密码不合法!");
                return 0;
            }
            //验证原密码是否正确
            User userInfo = userMapper.queryUserInfoById(params.getAsLong("opaccountId")); //查询帐号信息
            if(!MD5.verify(oldpassword,userInfo.getPassword()))
            {
                params.put("dmsg","原密码错误!");
                return 0;
            }
            //修改密码
            userMapper.updateResetUserPassword(MD5.md5Salt(newpassword), userInfo.getDevice(),userInfo.getId());//更改密码
            params.put("dmsg","修改成功!");
            return 1;
        }
        else
        {
            params.put("dmsg","密码修改失败!未知的帐号来源!");
            return 0;
        }
    }

    /**
     * 查询销售月提成列表（后台管理）
     * @author kouyi
     */
    public List<Dto> queryUserMonthCommList(Dto params)
    {
        return userMapper.queryUserMonthCommList(params);
    }

    /**
     * 查询销售月提成数据总数（后台管理）
     * @author kouyi
     */
    public List<Dto> queryUserMonthCommListCount(Dto params)
    {
        return userMapper.queryUserMonthCommListCount(params);
    }
}
