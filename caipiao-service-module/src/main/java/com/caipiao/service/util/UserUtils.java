package com.caipiao.service.util;

import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.common.util.ValidatorIdUtil;
import com.caipiao.domain.user.User;
import com.caipiao.domain.vo.UserVo;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 用户工具类 Created by kouyi on 2017-09-22
 */
public class UserUtils {

    /**
     * 用户是否绑定身份证.
     *
     * @param user
     * @return 0-未绑定 1-已绑定
     */
    public static Integer isBindingIdCard(User user) {
        if (StringUtil.isEmpty(user)) {
            return 0;
        }
        if (StringUtils.isBlank(user.getRealName())) {
            return 0;
        }
        if (StringUtils.isBlank(user.getIdCard()) || user.getIdCard().length() < 15) {
            return 0;
        }
        return 1;
    }

    /**
     * 用户是否绑定银行卡.
     *
     * @param user
     * @return 0-未绑定 1-已绑定 2-申请更换待审核
     */
    public static Integer isBindingBank(User user) {
        if (user == null) {
            return 0;
        }
        if (user.getBankInfo() == null || StringUtil.isEmpty(user.getBankInfo().getBankCard())) {
            return 0;
        }

        return user.getBankIsBind();
    }

    /**
     * 用户密码是否保护
     *
     * @param user
     * @return 0-未保护 1-已保护
     */
    public static Integer isPasswordSafed(User user) {
        if (StringUtils.isEmpty(user.getQuestion()) || StringUtils.isEmpty(user.getAnswer())) {
            return 0;
        }
        return 1;
    }

    /**
     * 银行信息串
     *
     * @param user
     * @return
     */
    public static String getUserBankInfo(User user)
    {
        StringBuffer bankInfo = new StringBuffer();
        if(user.getBankInfo() != null)
        {
            if(StringUtil.isNotEmpty(user.getBankInfo().getBankName()))
            {
                bankInfo.append("|" + user.getBankInfo().getBankName());
            }
            if(StringUtil.isNotEmpty(user.getBankInfo().getBankProvince()))
            {
                bankInfo.append("|" + user.getBankInfo().getBankProvince());
            }
            if(StringUtil.isNotEmpty(user.getBankInfo().getBankCity()))
            {
                bankInfo.append("-" + user.getBankInfo().getBankCity());
            }
            if(StringUtil.isNotEmpty(user.getBankInfo().getSubBankName()))
            {
                bankInfo.append("|" + user.getBankInfo().getSubBankName());
            }
        }
        return bankInfo.length() > 0? bankInfo.toString().substring(1) : bankInfo.toString();
    }

    /**
     * 密码保护问题.
     *
     * @return
     */
    public static List<String> getSafeQuestion() {
        List<String> list = new ArrayList<String>();
        list.add("您的家乡是?");
        list.add("您母亲的名字是?");
        list.add("您配偶的名字是?");
        list.add("您最爱吃的菜是?");
        list.add("您最爱读的书是?");
        list.add("您最喜欢的一首歌是?");
        list.add("您最喜欢的运动是?");
        list.add("您最喜欢的电影是?");
        list.add("您最想去的地方是?");
        list.add("您初中的语文老师是?");
        list.add("您最喜欢玩的游戏是?");
        return list;
    }

    /**
     * 用户的安全级别
     *
     * @param user
     * @return
     */
    public static int getSecrityLevel(User user) {
        int securityLevel = 0;
        if (UserUtils.isBindingIdCard(user) == 1) {
            securityLevel++;
        }
        if (UserUtils.isBindingBank(user) == 1) {
            securityLevel++;
        }
        if (UserUtils.isPasswordSafed(user) == 1) {
            securityLevel++;
        }
        return securityLevel;
    }

    /**
     * 获得保护的用户真实名
     *
     * @param realName
     * @return
     */
    public static String getSafeName(String realName) {
        if (realName == null) {
            return realName;
        }
        if (realName.length() < 2)
            return realName;

        StringBuffer sb = new StringBuffer();
        sb.append("*");
        sb.append(realName.substring(1, realName.length()));
        return sb.toString();
    }

    /**
     * 获得保护的身份证号
     *
     * @param idCard
     * @return
     */
    public static String getSafeIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return null;
        }
        if (idCard.length() <= 4) {
            return idCard;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(idCard.substring(0, 4));
        sb.append("********");
        sb.append(idCard.substring(idCard.length() - 4, idCard.length()));
        return sb.toString();
    }

    /**
     * 获得保护的银行卡号
     *
     * @param bankCode
     * @return
     */
    public static String getSafeBankCode(String bankCode) {
        if (StringUtils.isBlank(bankCode)) {
            return null;
        }
        if (bankCode.length() <= 4) {
            return bankCode;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(bankCode.substring(0, 4));
        sb.append("*******");
        sb.append(bankCode.substring(bankCode.length() - 4, bankCode.length()));
        return sb.toString();
    }

    /**
     * 获得保护的手机号
     *
     * @param mobile
     * @return
     */
    public static String getSafeMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return null;
        }
        if (mobile.length() <= 4) {
            return mobile;
        }
        mobile = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        return mobile;
    }

    /**
     * 检查身份证号码合法性
     *
     * @param idCard
     * @return
     */
    public static boolean checkIdCardLegal(String idCard) {
        if (StringUtils.isBlank(idCard))
            return false;
        return ValidatorIdUtil.isValidateIdcard(idCard);
    }

    /**
     * 检查手机号码合法性
     *
     * @param mobile
     * @return
     */
    public static boolean checkMobile(String mobile) {
        if (StringUtils.isBlank(mobile))
            return false;
        return ValidatorIdUtil.isValidateMobile(mobile);
    }

    /**
     * 检查真实姓名合法性
     *
     * @param name
     * @return
     */
    public static boolean isRealName(String name) {
        int len = name.length();
        if (len < 2 || len > 20) {
            return false;
        }
        return name.matches("^([\\u4e00-\\u9fa5]+|[\\s]+|[\\.|\\·]+)+$");
    }

    /**
     * 检查用户昵称是否合法
     *
     * @param nickName
     * @return
     */
    public static boolean checkNickName(String nickName) {
        if (StringUtils.isBlank(nickName)) {
            return false;
        }
        // 长度3-16
        int len = nickName.length();
        if (len < 2 || len > 16) {
            return false;
        }
        // 只能由汉字字母数字下划线组成
        String reg = "^([a-z|A-Z]+|[ \\u4e00-\\u9fa5]+|[\\s]+|[\\.|\\·]+|[0-9]+|[_|_]+)+$";
        if (!nickName.matches(reg)) {
            return false;
        }
        // 不能全数字
        if (NumberUtil.isNumber(nickName)) {
            return false;
        }
        return true;
    }

    /**
     * 检查用户密码
     *
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        if (StringUtils.isBlank(password)) {
            return false;
        }

        int len = password.length();
        if (len < 8 || len > 16) {
            return false;
        }

        // 只能由字母、数字、下划线组成
        String reg = "^([a-z|A-Z]+|[\\s]+|[0-9]+|[_|_]+)+$";
        if (!password.matches(reg)) {
            return false;
        }
        return true;
    }

    /**
     * 检查银行卡号合法性
     *
     * @param bankCode
     * @return
     */
    public static boolean checkBankCode(String bankCode) {
        if (StringUtils.isBlank(bankCode)) {
            return false;
        }
        int len = bankCode.length();
        if (len < 15 || len > 18) {
            return false;
        }
        if (!NumberUtil.isNumber(bankCode)) {
            return false;
        }
        return true;
    }

    /**
     * 随机生成指定规则的昵称
     * @return
     */
    public static String randomNickName() {
        String CHARS = "abcdefghijklmnopqrstuvwxyz";
        boolean[] bools = new boolean[CHARS.length()];
        Random random = new Random();
        StringBuffer str = new StringBuffer();
        int i = random.nextInt(CHARS.length());
        // 循环5次，即生成5个不同的字符
        for (int j = 0; j < 6; j++) {
            // 如果这个位置的bools的值为true,说明这个位置的字符已经出现过来，需要重新产生一个随机数
            while (bools[i]) {
                i = random.nextInt(CHARS.length());
            }
            // 生成了一个随机数之后就把对应位置的bools的值改为true
            bools[i] = true;
            str.append(CHARS.charAt(i));
        }

        StringBuffer number = new StringBuffer();
        int a[] = new int[5];
        for (int m = 0; m < a.length; m++) {
            // 生成一个介于0到9的数字
            a[m] = random.nextInt(9);
            for (int j = 1; j < m; j++) {
                while (a[m] == a[j]) {// 如果重复，退回去重新生成随机数
                    m--;
                    break;
                }
            }
        }
        for (int m = 0; m < a.length; m++) {
            number.append(a[m]);
        }
        return str.toString() + number.toString();
    }

    /**
     * 格式化用户对象信息
     * @param vo
     * @param user
     */
    public static void formatUserInfo(UserVo vo, User user) {
        if(StringUtil.isEmpty(vo) || StringUtil.isEmpty(user)) {
            return;
        }
        vo.setMobile(UserUtils.getSafeMobile(user.getMobile()));
        vo.setBankCard(UserUtils.getSafeBankCode(user.getBankInfo() != null? user.getBankInfo().getBankCard() : ""));//银行卡安全保护
        vo.setIdCard(UserUtils.getSafeIdCard(user.getIdCard()));//身份证安全保护
        vo.setRealName(UserUtils.getSafeName(user.getRealName()));//真实姓名安全保护
        vo.setSecurityLevel(UserUtils.getSecrityLevel(user));//安全等级
        vo.setIsPasswordSafe(UserUtils.isPasswordSafed(user));//密码是否保护
        vo.setIdCardIsBind(UserUtils.isBindingIdCard(user));//
        vo.setBankIsBind(UserUtils.isBindingBank(user));
        //vo.setBankInfo(UserUtils.getUserBankInfo(user));
    }
}
