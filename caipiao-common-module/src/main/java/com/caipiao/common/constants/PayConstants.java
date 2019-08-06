package com.caipiao.common.constants;

import java.util.HashMap;

/**
 * Created by Kouyi on 2017/11/28.
 */
public final class PayConstants
{
    public static final String unionBankSelectPage = "/html/pay/bankselect.html";

    public static final String unionpayWapPage = "/html/pay/unionpay.html";

    public static final String alipayH5Page = "/html/pay/alipayh5.html";

    public static final String weixinH5Page = "/html/pay/weixinh5.html";

    //用户资金出账类-业务编号
    public static final int CHANNEL_CODE_OUT_DRAWING = 300;//余额支付-购彩
    public static final int CHANNEL_CODE_OUT_YHJZHIFU = 301;//优惠券支付
    public static final int CHANNEL_CODE_OUT_SCOREFU = 417;//积分兑换支付

    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_SHENGPAY = 302;//提现方式-盛付通提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_JUHEPAY = 303;//提现方式-聚合支付10381提现
    /**
     * 提现渠道业务编号
     */
    //public static final int CHANNEL_CODE_OUT_RENGONGPAY = 304;//提现方式-人工转账提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_HUICHAOPAY = 305;//提现方式-汇潮支付提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_RENGONGPAY = 3100;//提现方式-人工转账提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_WLPAY = 3101;//提现方式-万两支付提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_SHUANGQPAY = 3102;//提现方式-双乾支付提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_YIZHIYIMADAIPAY = 3103;//提现方式-易旨支付(一麻袋)提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_KUAIFUPAY = 3104;//提现方式-快付支付提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_KJ142PAY = 3105;//提现方式-kj412支付提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_TTPAY = 3106;//提现方式-ttpay支付提现
    /**
     * 提现渠道业务编号
     */
    public static final int CHANNEL_CODE_OUT_YPAY = 3107;//提现方式-ypay支付提现

    public static final int CHANNEL_CODE_OUT_SYSTEM = 306;//管理后台扣款
    public static final int CHANNEL_CODE_OUT_YONGJIN = 307;//佣金提现
    public static final int CHANNEL_CODE_OUT_DASHANG = 308;//打赏扣除

    //用户资金入账类-业务编号
    public static final int CHANNEL_CODE_IN_DRAWING = 400;//用户中奖

    /**
     * 充值方式业务编号
     */
    public static final int CHANNEL_CODE_IN_PAY_WEIXIN = 4100;//微信充值
    public static final int CHANNEL_CODE_IN_PAY_WEIXINH5 = 4101;//微信H5充值
    public static final int CHANNEL_CODE_IN_PAY_ALIPAY = 4102;//支付宝充值
    public static final int CHANNEL_CODE_IN_PAY_ALIPAYH5 = 4103;//支付宝H5充值
    public static final int CHANNEL_CODE_IN_PAY_QQWALLETH5 = 4104;//QQ钱包
    public static final int CHANNEL_CODE_IN_PAY_JDWALLETH5 = 4105;//京东钱包
    public static final int CHANNEL_CODE_IN_PAY_UNIONPAY = 4106;//银联

    public static final int CHANNEL_CODE_IN_SYSTEM = 406;//管理后台加款
    public static final int CHANNEL_CODE_IN_YYFAIL = 407;//预约失败退款
    //提现失败退款(408-411待定)
    public static final int CHANNEL_CODE_IN_YHJBACK = 412;//优惠券退回
    public static final int CHANNEL_CODE_IN_YONGJIN = 413;//佣金转入
    public static final int CHANNEL_CODE_IN_SQDASHANG = 414;//收取赏金
    public static final int CHANNEL_CODE_IN_ZHUCESONG = 415;//注册送彩金
    public static final int CHANNEL_CODE_IN_SCHEMEJIAJIANG = 416;//方案加奖奖金
    public static final int CHANNEL_CODE_IN_ZFDASHANG = 417;//支付赏金

    /**
     * 充值渠道编号
     */
    public static final int PAYCHANNEL_CODE_WEIXIN = 100;//微信官方支付
    public static final int PAYCHANNEL_CODE_KUAIJIE = 101;//快接支付
    public static final int PAYCHANNEL_CODE_PAYFUBAO = 102;//贝付宝
    public static final int PAYCHANNEL_CODE_SWIFTPASS = 103;//威富通
    public static final int PAYCHANNEL_CODE_DOUDOUPAY = 104;//豆豆平台
    public static final int PAYCHANNEL_CODE_XUNYOUTONG = 105;//迅游通
    public static final int PAYCHANNEL_CODE_JUHE10381PAY = 106;//聚合支付10381
    public static final int PAYCHANNEL_CODE_ZHIFUPAY = 107;//直付支付
    public static final int PAYCHANNEL_CODE_ZHINENGYUNPAY = 108;//智能云收银
    public static final int PAYCHANNEL_CODE_AOYOUPAY = 109;//傲游支付
    public static final int PAYCHANNEL_CODE_BBPAY = 110;//BB支付
    public static final int PAYCHANNEL_CODE_MOMOPAY = 111;//陌陌付
    public static final int PAYCHANNEL_CODE_ZHAOXINGJUHEPAY = 112;//兆行支付
    public static final int PAYCHANNEL_CODE_WLPAY = 113;//万两支付
    public static final int PAYCHANNEL_CODE_YIFUTONGPAY = 114;//亿富通支付
    public static final int PAYCHANNEL_CODE_YIZHIPAY = 115;//易旨支付
    public static final int PAYCHANNEL_CODE_KUAIJIE2 = 116;//快接支付-(D0,小额支付)
    public static final int PAYCHANNEL_CODE_YPAY = 117;//ypay
    public static final int PAYCHANNEL_CODE_KJ142PAY = 118;//kj412
    public static final int PAYCHANNEL_CODE_TTPAY = 119;//ttpay

    public static final int PAYWAY_CLLENT_TYPE_ALL = 0;//支付适用客户端类型-全部
    public static final int PAYWAY_CLLENT_TYPE_APP = 1;//支付适用客户端类型-app
    public static final int PAYWAY_CLLENT_TYPE_H5 = 2;//支付适用客户端类型-h5
    public static final int PAYWAY_CLLENT_TYPE_WEB = 3;//支付适用客户端类型-web

    public static final int PAY_TYPE_RECHARGE = 0;//支付类型-充值
    public static final int PAY_TYPE_ENCHASHMENT = 1;//支付类型-提现

    public static final int PAYORDER_STATUS_FAILURE = -1;//订单状态-处理失败
    public static final int PAYORDER_STATUS_DCL = 0;//订单状态-待处理
    public static final int PAYORDER_STATUS_CXCL = 1;//订单状态-等待重新处理
    public static final int PAYORDER_STATUS_CLZ = 2;//订单状态-处理中
    public static final int PAYORDER_STATUS_SUCCESS = 3;//订单状态-处理成功

    public static final HashMap<Integer, String> channelCodeMap = new HashMap<>();//渠道编号/渠道编号描述集合
    public static final HashMap<Integer, String> paywayClientTypeMap = new HashMap<>();//支付适用客户端类型/支付适用客户端类型描述集合
    public static final HashMap<Integer, String> payorderStatusMap = new HashMap<>();//订单状态/状态描述集合

    static {
        channelCodeMap.put(CHANNEL_CODE_OUT_DRAWING, "余额支付(购彩)");
        channelCodeMap.put(CHANNEL_CODE_OUT_YHJZHIFU, "优惠券支付");
        channelCodeMap.put(CHANNEL_CODE_OUT_SCOREFU, "积分支付");
        channelCodeMap.put(CHANNEL_CODE_OUT_RENGONGPAY, "线下人工转账提现");
        channelCodeMap.put(CHANNEL_CODE_OUT_WLPAY, "万两支付提现");
        channelCodeMap.put(CHANNEL_CODE_OUT_SHUANGQPAY, "双乾支付提现");
        channelCodeMap.put(CHANNEL_CODE_OUT_SYSTEM, "后台扣款");
        channelCodeMap.put(CHANNEL_CODE_OUT_YONGJIN, "佣金提现");
        channelCodeMap.put(CHANNEL_CODE_OUT_DASHANG, "打赏扣除");
        channelCodeMap.put(CHANNEL_CODE_IN_DRAWING, "中奖");

        channelCodeMap.put(CHANNEL_CODE_IN_PAY_WEIXIN, "微信充值");
        channelCodeMap.put(CHANNEL_CODE_IN_PAY_WEIXINH5, "微信H5充值");
        channelCodeMap.put(CHANNEL_CODE_IN_PAY_ALIPAY, "支付宝充值");
        channelCodeMap.put(CHANNEL_CODE_IN_PAY_ALIPAYH5, "支付宝H5充值");
        channelCodeMap.put(CHANNEL_CODE_IN_PAY_QQWALLETH5, "QQ钱包H5充值");
        channelCodeMap.put(CHANNEL_CODE_IN_PAY_JDWALLETH5, "京东钱包H5充值");
        channelCodeMap.put(CHANNEL_CODE_IN_PAY_UNIONPAY, "银联充值");

        channelCodeMap.put(CHANNEL_CODE_IN_SYSTEM, "后台加款");
        channelCodeMap.put(CHANNEL_CODE_IN_YYFAIL, "预约失败退款");
        channelCodeMap.put(CHANNEL_CODE_IN_YHJBACK, "优惠券退回");
        channelCodeMap.put(CHANNEL_CODE_IN_YONGJIN, "佣金转入");
        channelCodeMap.put(CHANNEL_CODE_IN_SQDASHANG, "收取赏金");
        channelCodeMap.put(CHANNEL_CODE_IN_ZFDASHANG, "支付赏金");
        channelCodeMap.put(CHANNEL_CODE_IN_ZHUCESONG, "注册送彩金");
        channelCodeMap.put(CHANNEL_CODE_IN_SCHEMEJIAJIANG, "{0}{1}");

        paywayClientTypeMap.put(PAYWAY_CLLENT_TYPE_ALL, "全部");
        paywayClientTypeMap.put(PAYWAY_CLLENT_TYPE_APP, "app");
        paywayClientTypeMap.put(PAYWAY_CLLENT_TYPE_H5, "h5");
        paywayClientTypeMap.put(PAYWAY_CLLENT_TYPE_WEB, "web");

        payorderStatusMap.put(PAYORDER_STATUS_FAILURE,"处理失败");
        payorderStatusMap.put(PAYORDER_STATUS_DCL,"待处理");
        payorderStatusMap.put(PAYORDER_STATUS_CXCL,"等待重新处理");
        payorderStatusMap.put(PAYORDER_STATUS_CLZ,"处理中");
        payorderStatusMap.put(PAYORDER_STATUS_SUCCESS,"处理成功");
    }
}