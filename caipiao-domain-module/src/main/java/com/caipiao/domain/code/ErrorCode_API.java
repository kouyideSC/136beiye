package com.caipiao.domain.code;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * APP接口服务定义状态码
 * Created by kouyi on 2017/11/16.
 */
public class ErrorCode_API extends ErrorCode implements Serializable {
	private static final long serialVersionUID = 3867285300059808138L;
	// ===================用户模块接口返回失败状态码定义===================
	public static final int ERROR_USER_110001 = 110001;
	public static final String ERROR_USER_110001_MSG = "手机号码不合法";
	public static final int ERROR_USER_110002 = 110002;
	public static final String ERROR_USER_110002_MSG = "该手机号已被使用";
	public static final int ERROR_USER_110003 = 110003;
	public static final String ERROR_USER_110003_MSG = "密码不合法";
	public static final int ERROR_USER_110004 = 110004;
	public static final String ERROR_USER_110004_MSG = "昵称已被使用或不合法";
	public static final int ERROR_USER_110005 = 110005;
	public static final String ERROR_USER_110005_MSG = "用户不存在或密码错误";
	public static final int ERROR_USER_110006 = 110006;
	public static final String ERROR_USER_110006_MSG = "用户已被冻结或注销,请联系客服";
	public static final int ERROR_USER_110007 = 110007;
	public static final String ERROR_USER_110007_MSG = "用户账户信息不完整";
	public static final int ERROR_USER_110008 = 110008;
	public static final String ERROR_USER_110008_MSG = "登录已失效,请重新登录";
	public static final int ERR_USER_110009 = 110009;
	public static final String ERR_USER_110009_MSG = "验证码发送过于频繁";
	public static final int ERR_USER_110010 = 110010;
	public static final String ERR_USER_110010_MSG = "验证码不正确或已过期";
	public static final int ERROR_USER_110011 = 110011;
	public static final String ERROR_USER_110011_MSG = "手机号不存在";
	public static final int ERROR_USER_110012 = 110012;
	public static final String ERROR_USER_110012_MSG = "当前密码不正确";
	public static final int ERROR_USER_110013 = 110013;
	public static final String ERROR_USER_110013_MSG = "不支持{0}格式";
	public static final int ERROR_USER_110014 = 110014;
	public static final String ERROR_USER_110014_MSG = "文件大小不能超过{0}";
	public static final int ERROR_USER_110015 = 110015;
	public static final String ERROR_USER_110015_MSG = "appId不合法";

	public static final int ERROR_USER_IDENTITYRZ_DUPLICATE = 110016;
	public static final String ERROR_USER_IDENTITYRZ_DUPLICATE_MSG = "已实名认证过!请勿重复认证";

	public static final int ERROR_USER_NOT_IDENTITYRZ = 110017;
	public static final String ERROR_USER_NOT_IDENTITYRZ_MSG = "尚未实名认证";

	public static final int ERROR_USER_NOT_BINDBANK = 110018;
	public static final String ERROR_USER_NOT_BINDBANK_MSG = "尚未绑定银行卡";

	public static final int ERR_USER_110019 = 110019;
	public static final String ERR_USER_110019_MSG = "返利账户余额不足~无法转出";

	public static final int ERR_USER_110020 = 110020;
	public static final String ERR_USER_110020_MSG = "您的邀请码尚未生成~请联系客服";

	public static final int ERROR_USER_NOTALLOW_BINDBANK = 110021;
	public static final String ERROR_USER_NOTALLOW_BINDBANK_MSG = "{0}不能进行银行卡绑定";

	public static final int ERROR_USER_APPLY_BONUS = 110022;
	public static final String ERROR_USER_APPLY_BONUS_MSG = "领取活动资格失败,{0}";

	// ===================方案模块接口返回失败状态码定义===================
	public static final int ERROR_SCHEM_120000 = 120000;
	public static final String ERROR_SCHEM_XTSJWFYY_MSG = "系统升级中~彩种停止销售";

	public static final int ERROR_SCHEM_120001 = 120001;
	public static final String ERROR_SCHEM_120001_MSG = "彩种暂不支持";

	public static final int ERROR_SCHEM_120002 = 120002;
	public static final String ERROR_SCHEM_120002_MSG = "投注项格式错误";

	public static final int ERROR_SCHEM_120003 = 120003;
	public static final String ERROR_SCHEM_120003_MSG = "投注项中包含有未开售的玩法";

	public static final int ERROR_SCHEM_120004 = 120004;
	public static final String ERROR_SCHEM_120004_MSG = "单倍最大支持{0}注";

	public static final int ERROR_SCHEM_120005 = 120005;
	public static final String ERROR_SCHEM_120005_MSG = "不能超过{0}倍";

	public static final int ERROR_SCHEM_120006 = 120006;
	public static final String ERROR_SCHEM_120006_MSG = "不能超过{0}元";

	public static final int ERROR_SCHEM_120007 = 120007;
	public static final String ERROR_SCHEM_120007_MSG = "余额不足";

	public static final int ERROR_SCHEM_120008 = 120008;
	public static final String ERROR_SCHEM_120008_MSG = "追期数不能超过{0}期";

	public static final int ERROR_SCHEM_120009 = 120009;
	public static final String ERROR_SCHEM_120009_MSG = "期次{0}已截止";

	public static final int ERROR_SCHEM_120010 = 120010;
	public static final String ERROR_SCHEM_120010_MSG = "投注项中包含限号";

	public static final int ERROR_SCHEM_120011 = 120011;
	public static final String ERROR_SCHEM_120011_MSG = "请求过于频繁";

	public static final int ERROR_SCHEM_120012 = 120012;
	public static final String ERROR_SCHEM_120012_MSG = "期次{0}未开售";

	public static final int ERROR_SCHEM_120013 = 120013;
	public static final String ERROR_SCHEM_120013_MSG = "场次{0}已截止";

	public static final int ERROR_SCHEM_120014 = 120014;
	public static final String ERROR_SCHEM_120014_MSG = "场次{0}未开售";

	public static final int ERROR_SCHEM_120015 = 120015;
	public static final String ERROR_SCHEM_120015_MSG = "方案已截止";

	public static final int ERROR_SCHEM_120016 = 120016;
	public static final String ERROR_SCHEM_120016_MSG = "优惠券已过期";

	public static final int ERROR_SCHEM_120017 = 120017;
	public static final String ERROR_SCHEM_120017_MSG = "奖金优化只支持竞彩";

	public static final int ERROR_SCHEM_120018 = 120018;
	public static final String ERROR_SCHEM_120018_MSG = "多串过关不支持奖金优化";

	public static final int ERROR_SCHEM_120019 = 120019;
	public static final String ERROR_SCHEM_120019_MSG = "奖金优化最多支持6场比赛";

	public static final int ERROR_SCHEM_120020 = 120020;
	public static final String ERROR_SCHEM_120020_MSG = "单期次投入不能超过{0}元";

	public static final int ERROR_SCHEM_120021 = 120021;
	public static final String ERROR_SCHEM_120021_MSG = "投注金额错误";

	public static final int ERROR_SCHEM_120022 = 120022;
	public static final String ERROR_SCHEM_120022_MSG = "优化最低金额{0}元";

	public static final int ERROR_SCHEM_120023 = 120023;
	public static final String ERROR_SCHEM_120023_MSG = "优化方案最大支持500注";

	public static final int ERROR_SCHEM_120024 = 120024;
	public static final String ERROR_SCHEM_120024_MSG = "优化方案最小支持2注";

	public static final int ERROR_SCHEM_120026 = 120026;
	public static final String ERROR_SCHEM_120026_MSG = "一天不能超过{0}个晒单";

	public static final int ERROR_SCHEM_120027 = 120027;
	public static final String ERROR_SCHEM_120027_MSG = "不具备晒单条件";

	public static final int ERROR_SCHEM_120028 = 120028;
	public static final String ERROR_SCHEM_120028_MSG = "方案晒单时间已截止";

	public static final int ERROR_SCHEM_120029 = 120029;
	public static final String ERROR_SCHEM_120029_MSG = "跟投金额已满额";

	public static final int ERROR_SCHEM_120030 = 120030;
	public static final String ERROR_SCHEM_120030_MSG = "跟投金额不能超过剩余可跟投金额:{0}";

	public static final int ERROR_SCHEM_120031 = 120031;
	public static final String ERROR_SCHEM_120031_MSG = "至少{0}倍";

	public static final int ERROR_SCHEM_120032 = 120032;
	public static final String ERROR_SCHEM_120032_MSG = "起投金额至少{0}元";

	public static final int ERROR_SCHEM_120033 = 120033;
	public static final String ERROR_SCHEM_120033_MSG = "不支持多个过关方式";

	public static final int ERROR_SCHEM_120034 = 120034;
	public static final String ERROR_SCHEM_120034_MSG = "最多可选{0}场";

	// ===================充值/提现模块接口返回失败状态码定义===================
	public static final int ERROR_PAY_NOTSUPPORT = 130000;
	public static final String ERROR_PAY_NOTSUPPORT_MSG = "不支持{0}方式";

	public static final int ERROR_PAY_SIGNERROR = 130001;
	public static final String ERROR_PAY_SIGNERROR_MSG = "签名错误";

	public static final int ERROR_PAY_ORDERNOTEXIST = 130002;
	public static final String ERROR_PAY_ORDERNOTEXIST_MSG = "订单不存在";

	public static final int ERROR_PAY_MONEYNOTUNCONFORMITY = 130003;
	public static final String ERROR_PAY_MONEYNOTUNCONFORMITY_MSG = "订单金额不一致";

	public static final int ERROR_PAY_USERNOTEXISTS = 130004;
	public static final String ERROR_PAY_USERNOTEXISTS_MSG = "用户不存在";

	public static final int ERROR_PAY_USERSTATUS_NOTALLOW = 130005;
	public static final String ERROR_PAY_USERSTATUS_NOTALLOW_MSG = "{0}不能进行提现";

	public static final int ERROR_PAY_USERNEEDBINDBANK = 130006;
	public static final String ERROR_PAY_USERNEEDBINDBANK_MSG = "用户尚未绑定银行卡";

	public static final int ERROR_PAY_YEBU = 130007;
	public static final String ERROR_PAY_YEBU_MSG = "余额不足";

	public static final int ERROR_PAY_CGYTZDTXCS = 130008;
	public static final String ERROR_PAY_CGYTZDTXCS_MSG = "当日可提现次数";

	public static final int ERROR_PAY_NOPAYWAYCHANNEL = 130009;
	public static final String ERROR_PAY_NOPAYWAYCHANNEL_MSG = "该充值方式维护中,请更换其它充值方式";

	public static final int ERROR_PAY_MAXMONEY = 130011;
	public static final String ERROR_PAY_MAXMONEY_MSG = "本次最大充值金额为{0}";

	public static final int ERROR_PAY_MINMONEY = 130012;
	public static final String ERROR_PAY_MINMONEY_MSG = "本次最小充值金额为{0}";

	public static final int ERROR_PAY_ORDERCLOSED = 130010;
	public static final String ERROR_PAY_ORDERCLOSED_MSG = "该订单已关闭,无法支付";

	// ===================身份验证模块接口返回失败状态码定义===================
	public static final int ERROR_VALID_NOTIDENTICAL = 140000;
	public static final String ERROR_VALID_NOTIDENTICAL_MSG = "验证不通过";

	/**
	 * 获取状态码对应描述
	 */
	@Override
	public String getCodeMsg(int code) {
		try {
			Field[] fs = this.getClass().getFields();
			for (Field f : fs) {
				if (f.get(f.getName()).toString().equals(String.valueOf(code))) {
					return this.getClass().getField(f.getName() + "_MSG").get(f.getName() + "_MSG").toString();
				}
			}
		} catch (Exception e) {
			System.out.println("读取状态码描述异常");
		}
		return "找不到状态码 " + code;
	}
	
}
