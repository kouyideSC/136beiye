package com.caipiao.common.pay.weixin;

import java.util.HashMap;
import java.util.Map;

/**
 * weixin(微信)常量类
 */
public class WeixinConstants
{
    public static final String ICBC_DEBIT = "ICBC_DEBIT";//工商银行(借记卡)
    public static final String ICBC_CREDIT = "ICBC_CREDIT";//工商银行(信用卡)
    public static final String ABC_DEBIT = "ABC_DEBIT";//农业银行(借记卡)
    public static final String ABC_CREDIT = "ABC_CREDIT";//农业银行(信用卡)
    public static final String PSBC_DEBIT = "PSBC_DEBIT";//邮政储蓄银行(借记卡)
    public static final String PSBC_CREDIT = "PSBC_CREDIT";//邮政储蓄银行(信用卡)
    public static final String CCB_DEBIT = "CCB_DEBIT";//建设银行(借记卡)
    public static final String CCB_CREDIT = "CCB_CREDIT";//建设银行(信用卡)
    public static final String CMB_DEBIT = "CMB_DEBIT";//招商银行(借记卡)
    public static final String CMB_CREDIT = "CMB_CREDIT";//招商银行(信用卡)
    public static final String BOC_DEBIT = "BOC_DEBIT";//中国银行(借记卡)
    public static final String BOC_CREDIT = "BOC_CREDIT";//中国银行(信用卡)
    public static final String COMM_DEBIT = "COMM_DEBIT";//交通银行(借记卡)
    public static final String COMM_CREDIT = "COMM_CREDIT";//交通银行(信用卡)
    public static final String SPDB_DEBIT = "SPDB_DEBIT";//浦发银行(借记卡)
    public static final String SPDB_CREDIT = "SPDB_CREDIT";//浦发银行(信用卡)
    public static final String GDB_DEBIT = "GDB_DEBIT";//广发银行(借记卡)
    public static final String GDB_CREDIT = "GDB_CREDIT";//广发银行(信用卡)
    public static final String CMBC_DEBIT = "CMBC_DEBIT";//民生银行(借记卡)
    public static final String CMBC_CREDIT = "CMBC_CREDIT";//民生银行(信用卡)
    public static final String PAB_DEBIT = "PAB_DEBIT";//平安银行(借记卡)
    public static final String PAB_CREDIT = "PAB_CREDIT";//平安银行(信用卡)
    public static final String CEB_DEBIT = "CEB_DEBIT";//光大银行(借记卡)
    public static final String CEB_CREDIT = "CEB_CREDIT";//光大银行(信用卡)
    public static final String CIB_DEBIT = "CIB_DEBIT";//兴业银行(借记卡)
    public static final String CIB_CREDIT = "CIB_CREDIT";//兴业银行(信用卡)
    public static final String CITIC_DEBIT = "CITIC_DEBIT";//中信银行(借记卡)
    public static final String CITIC_CREDIT = "CITIC_CREDIT";//中信银行(信用卡)
    public static final String BOSH_DEBIT = "BOSH_DEBIT";//上海银行(借记卡)
    public static final String BOSH_CREDIT = "BOSH_CREDIT";//上海银行(信用卡)
    public static final String CRB_DEBIT = "CRB_DEBIT";//华润银行(借记卡)
    public static final String HZB_DEBIT = "HZB_DEBIT";//杭州银行(借记卡)
    public static final String HZB_CREDIT = "HZB_CREDIT";//杭州银行(信用卡)
    public static final String BSB_DEBIT = "BSB_DEBIT";//包商银行(借记卡)
    public static final String BSB_CREDIT = "BSB_CREDIT";//包商银行(信用卡)
    public static final String CQB_DEBIT = "CQB_DEBIT";//重庆银行(借记卡)
    public static final String SDEB_DEBIT = "SDEB_DEBIT";//顺德农商行(借记卡)
    public static final String SZRCB_DEBIT = "SZRCB_DEBIT";//深圳农商银行(借记卡)
    public static final String SZRCB_CREDIT = "SZRCB_CREDIT";//深圳农商银行(信用卡)
    public static final String HRBB_DEBIT = "HRBB_DEBIT";//哈尔滨银行(借记卡)
    public static final String BOCD_DEBIT = "BOCD_DEBIT";//成都银行(借记卡)
    public static final String GDNYB_DEBIT = "GDNYB_DEBIT";//南粤银行(借记卡)
    public static final String GDNYB_CREDIT = "GDNYB_CREDIT";//南粤银行(信用卡)
    public static final String GZCB_DEBIT = "GZCB_DEBIT";//广州银行(借记卡))
    public static final String GZCB_CREDIT = "GZCB_CREDIT";//广州银行(信用卡)
    public static final String JSB_DEBIT = "JSB_DEBIT";//江苏银行(借记卡)
    public static final String JSB_CREDIT = "JSB_CREDIT";//江苏银行(信用卡)
    public static final String NBCB_DEBIT = "NBCB_DEBIT";//宁波银行(借记卡)
    public static final String NBCB_CREDIT = "NBCB_CREDIT";//宁波银行(信用卡)
    public static final String NJCB_DEBIT = "NJCB_DEBIT";//南京银行(借记卡)
    public static final String QHNX_DEBIT = "QHNX_DEBIT";//青海农信(借记卡)
    public static final String ORDOSB_CREDIT = "ORDOSB_CREDIT";//鄂尔多斯银行(信用卡)
    public static final String ORDOSB_DEBIT = "ORDOSB_DEBIT";//鄂尔多斯银行(借记卡)
    public static final String BJRCB_CREDIT = "BJRCB_CREDIT";//北京农商(信用卡)
    public static final String BHB_DEBIT = "BHB_DEBIT";//河北银行(借记卡)
    public static final String BGZB_DEBIT = "BGZB_DEBIT";//贵州银行(借记卡)
    public static final String BEEB_DEBIT = "BEEB_DEBIT";//鄞州银行(借记卡)
    public static final String PZHCCB_DEBIT = "PZHCCB_DEBIT";//攀枝花银行(借记卡)
    public static final String QDCCB_CREDIT = "QDCCB_CREDIT";//青岛银行(信用卡)
    public static final String QDCCB_DEBIT = "QDCCB_DEBIT";//青岛银行(借记卡)
    public static final String SHINHAN_DEBIT = "SHINHAN_DEBIT";//新韩银行(借记卡)
    public static final String QLB_DEBIT = "QLB_DEBIT";//齐鲁银行(借记卡)
    public static final String QSB_DEBIT = "QSB_DEBIT";//齐商银行(借记卡)
    public static final String ZZB_DEBIT = "ZZB_DEBIT";//郑州银行(借记卡)
    public static final String CCAB_DEBIT = "CCAB_DEBIT";//长安银行(借记卡)
    public static final String RZB_DEBIT = "RZB_DEBIT";//日照银行(借记卡)
    public static final String SCNX_DEBIT = "SCNX_DEBIT";//四川农信(借记卡)
    public static final String BEEB_CREDIT = "BEEB_CREDIT";//鄞州银行(信用卡)
    public static final String SDRCU_DEBIT = "QLB_DEBIT";//山东农信(借记卡)
    public static final String BCZ_DEBIT = "BCZ_DEBIT";//沧州银行(借记卡)
    public static final String SJB_DEBIT = "SJB_DEBIT";//盛京银行(借记卡)
    public static final String LNNX_DEBIT = "LNNX_DEBIT";//辽宁农信(借记卡)
    public static final String JUFENGB_DEBIT = "JUFENGB_DEBIT";//临朐聚丰村镇银行(借记卡)
    public static final String ZZB_CREDIT = "ZZB_CREDIT";//郑州银行(信用卡)
    public static final String JXNXB_DEBIT = "JXNXB_DEBIT";//江西农信(借记卡)
    public static final String JZB_DEBIT = "JZB_DEBIT";//晋中银行(借记卡)
    public static final String JZCB_CREDIT = "JZCB_CREDIT";//锦州银行(信用卡)
    public static final String JZCB_DEBIT = "JZCB_DEBIT";//锦州银行(借记卡)
    public static final String KLB_DEBIT = "KLB_DEBIT";//昆仑银行(借记卡)
    public static final String KRCB_DEBIT = "KRCB_DEBIT";//昆山农商(借记卡)
    public static final String KUERLECB_DEBIT = "KUERLECB_DEBIT";//库尔勒市商业银行(借记卡)
    public static final String LJB_DEBIT = "LJB_DEBIT";//龙江银行(借记卡)
    public static final String NYCCB_DEBIT = "NYCCB_DEBIT";//南阳村镇银行(借记卡)
    public static final String LSCCB_DEBIT = "LSCCB_DEBIT";//乐山市商业银行(借记卡)
    public static final String LUZB_DEBIT = "LUZB_DEBIT";//柳州银行(借记卡)
    public static final String LWB_DEBIT = "LWB_DEBIT";//莱商银行(借记卡)
    public static final String LYYHB_DEBIT = "LYYHB_DEBIT";//辽阳银行(借记卡)
    public static final String LZB_DEBIT = "LZB_DEBIT";//兰州银行(借记卡)
    public static final String MINTAIB_CREDIT = "MINTAIB_CREDIT";//民泰银行(信用卡)
    public static final String MINTAIB_DEBIT = "MINTAIB_DEBIT";//民泰银行(借记卡)
    public static final String NCB_DEBIT = "NCB_DEBIT";//宁波通商银行(借记卡)
    public static final String NMGNX_DEBIT = "NMGNX_DEBIT";//内蒙古农信(借记卡)
    public static final String XAB_DEBIT = "XAB_DEBIT";//西安银行(借记卡)
    public static final String WFB_CREDIT = "WFB_CREDIT";//潍坊银行(信用卡)
    public static final String WFB_DEBIT = "WFB_DEBIT";//潍坊银行(借记卡)
    public static final String WHB_CREDIT = "WHB_CREDIT";//威海商业银行(信用卡)
    public static final String WHB_DEBIT = "WHB_DEBIT";//威海市商业银行(借记卡)
    public static final String WHRC_CREDIT = "WHRC_CREDIT";//武汉农商(信用卡)
    public static final String WHRC_DEBIT = "WHRC_DEBIT";//武汉农商行(借记卡)
    public static final String WJRCB_DEBIT = "WJRCB_DEBIT";//吴江农商行(借记卡)
    public static final String WLMQB_DEBIT = "WLMQB_DEBIT";//乌鲁木齐银行(借记卡)
    public static final String WRCB_DEBIT = "WRCB_DEBIT";//无锡农商(借记卡)
    public static final String WZB_DEBIT = "WZB_DEBIT";//温州银行(借记卡)
    public static final String XAB_CREDIT = "XAB_CREDIT";//西安银行(信用卡)
    public static final String WEB_DEBIT = "WEB_DEBIT";//微众银行(借记卡)
    public static final String XIB_DEBIT = "XIB_DEBIT";//厦门国际银行(借记卡)
    public static final String XJRCCB_DEBIT = "XJRCCB_DEBIT";//新疆农信银行(借记卡)
    public static final String XMCCB_DEBIT = "XMCCB_DEBIT";//厦门银行(借记卡)
    public static final String YNRCCB_DEBIT = "YNRCCB_DEBIT";//云南农信(借记卡)
    public static final String YRRCB_CREDIT = "YRRCB_CREDIT";//黄河农商银行(信用卡)
    public static final String YRRCB_DEBIT = "YRRCB_DEBIT";//黄河农商银行(借记卡)
    public static final String YTB_DEBIT = "YTB_DEBIT";//烟台银行(借记卡)
    public static final String ZJB_DEBIT = "ZJB_DEBIT";//紫金农商银行(借记卡)
    public static final String ZJLXRB_DEBIT = "ZJLXRB_DEBIT";//兰溪越商银行(借记卡)
    public static final String ZJRCUB_CREDIT = "ZJRCUB_CREDIT";//浙江农信(信用卡)
    public static final String AHRCUB_DEBIT = "AHRCUB_DEBIT";//安徽省农村信用社联合社(借记卡)
    public static final String BCZ_CREDIT = "BCZ_CREDIT";//沧州银行(信用卡)
    public static final String SRB_DEBIT = "SRB_DEBIT";//上饶银行(借记卡)
    public static final String ZYB_DEBIT = "ZYB_DEBIT";//中原银行(借记卡)
    public static final String ZRCB_DEBIT = "ZRCB_DEBIT";//张家港农商行(借记卡)
    public static final String SRCB_CREDIT = "SRCB_CREDIT";//上海农商银行(信用卡)
    public static final String SRCB_DEBIT = "SRCB_DEBIT";//上海农商银行(借记卡)
    public static final String ZJTLCB_DEBIT = "ZJTLCB_DEBIT";//浙江泰隆银行(借记卡)
    public static final String SUZB_DEBIT = "SUZB_DEBIT";//苏州银行(借记卡)
    public static final String SXNX_DEBIT = "SXNX_DEBIT";//山西农信(借记卡)
    public static final String SXXH_DEBIT = "SXXH_DEBIT";//陕西信合(借记卡)
    public static final String ZJRCUB_DEBIT = "ZJRCUB_DEBIT";//浙江农信(借记卡)
    public static final String AE_CREDIT = "AE_CREDIT";//AE(信用卡)
    public static final String TACCB_CREDIT = "TACCB_CREDIT";//泰安银行(信用卡)
    public static final String TACCB_DEBIT = "TACCB_DEBIT";//泰安银行(借记卡)
    public static final String TCRCB_DEBIT = "TCRCB_DEBIT";//太仓农商行(借记卡)
    public static final String TJBHB_CREDIT = "TJBHB_CREDIT";//天津滨海农商行(信用卡)
    public static final String TJBHB_DEBIT = "TJBHB_DEBIT";//天津滨海农商行(借记卡)
    public static final String TJB_DEBIT = "TJB_DEBIT";//天津银行(借记卡)
    public static final String TRCB_DEBIT = "TRCB_DEBIT";//天津农商(借记卡)
    public static final String TZB_DEBIT = "TZB_DEBIT";//台州银行(借记卡)
    public static final String URB_DEBIT = "URB_DEBIT";//联合村镇银行(借记卡)
    public static final String DYB_CREDIT = "DYB_CREDIT";//东营银行(信用卡)
    public static final String CSRCB_DEBIT = "CSRCB_DEBIT";//常熟农商银行(借记卡)
    public static final String CZB_CREDIT = "CZB_CREDIT";//浙商银行(信用卡)
    public static final String CZB_DEBIT = "CZB_DEBIT";//浙商银行(借记卡)
    public static final String CZCB_CREDIT = "CZCB_CREDIT";//稠州银行(信用卡)
    public static final String CZCB_DEBIT = "CZCB_DEBIT";//稠州银行(借记卡)
    public static final String DANDONGB_CREDIT = "DANDONGB_CREDIT";//丹东银行(信用卡)
    public static final String DANDONGB_DEBIT = "DANDONGB_DEBIT";//丹东银行(借记卡)
    public static final String DLB_CREDIT = "DLB_CREDIT";//大连银行(信用卡)
    public static final String DLB_DEBIT = "DLB_DEBIT";//大连银行(借记卡)
    public static final String DRCB_CREDIT = "DRCB_CREDIT";//东莞农商银行(信用卡)
    public static final String DRCB_DEBIT = "DRCB_DEBIT";//东莞农商银行(借记卡)
    public static final String CSRCB_CREDIT = "CSRCB_CREDIT";//常熟农商银行(信用卡)
    public static final String DYB_DEBIT = "DYB_DEBIT";//东营银行(借记卡)
    public static final String DYCCB_DEBIT = "DYCCB_DEBIT";//德阳银行(借记卡)
    public static final String FBB_DEBIT = "FBB_DEBIT";//富邦华一银行(借记卡)
    public static final String FDB_DEBIT = "FDB_DEBIT";//富滇银行(借记卡)
    public static final String FJHXB_CREDIT = "FJHXB_CREDIT";//福建海峡银行(信用卡)
    public static final String FJHXB_DEBIT = "FJHXB_DEBIT";//福建海峡银行(借记卡)
    public static final String FJNX_DEBIT = "FJNX_DEBIT";//福建农信银行(借记卡)
    public static final String FUXINB_DEBIT = "FUXINB_DEBIT";//阜新银行(借记卡)
    public static final String BOCDB_DEBIT = "BOCDB_DEBIT";//承德银行(借记卡)
    public static final String JSNX_DEBIT = "JSNX_DEBIT";//江苏农商行(借记卡)
    public static final String BOLFB_DEBIT = "BOLFB_DEBIT";//廊坊银行(借记卡)
    public static final String CCAB_CREDIT = "CCAB_CREDIT";//长安银行(信用卡)
    public static final String CBHB_DEBIT = "CBHB_DEBIT";//渤海银行(借记卡)
    public static final String CDRCB_DEBIT = "CDRCB_DEBIT";//成都农商银行(借记卡)
    public static final String BYK_DEBIT = "BYK_DEBIT";//营口银行(借记卡)
    public static final String BOZ_DEBIT = "BOZ_DEBIT";//张家口市商业银行(借记卡)
    public static final String CFT = "CFT";//零钱
    public static final String BOTSB_DEBIT = "BOTSB_DEBIT";//唐山银行(借记卡)
    public static final String BOSZS_DEBIT = "BOSZS_DEBIT";//石嘴山银行(借记卡)
    public static final String BOSXB_DEBIT = "BOSXB_DEBIT";//绍兴银行(借记卡)
    public static final String BONX_DEBIT = "BONX_DEBIT";//宁夏银行(借记卡)
    public static final String BONX_CREDIT = "BONX_CREDIT";//宁夏银行(信用卡)
    public static final String GDHX_DEBIT = "GDHX_DEBIT";//广东华兴银行(借记卡)
    public static final String BOLB_DEBIT = "BOLB_DEBIT";//洛阳银行(借记卡)
    public static final String BOJX_DEBIT = "BOJX_DEBIT";//嘉兴银行(借记卡)
    public static final String BOIMCB_DEBIT = "BOIMCB_DEBIT";//内蒙古银行(借记卡)
    public static final String BOHN_DEBIT = "BOHN_DEBIT";//海南银行(借记卡)
    public static final String BOD_DEBIT = "BOD_DEBIT";//东莞银行(借记卡)
    public static final String CQRCB_CREDIT = "CQRCB_CREDIT";//重庆农商银行(信用卡)
    public static final String CQRCB_DEBIT = "CQRCB_DEBIT";//重庆农商银行(借记卡)
    public static final String CQTGB_DEBIT = "CQTGB_DEBIT";//重庆三峡银行(借记卡)
    public static final String BOD_CREDIT = "BOD_CREDIT";//东莞银行(信用卡)
    public static final String CSCB_DEBIT = "CSCB_DEBIT";//长沙银行(借记卡)
    public static final String BOB_CREDIT = "BOB_CREDIT";//北京银行(信用卡)
    public static final String GDRCU_DEBIT = "GDRCU_DEBIT";//广东农信银行(借记卡)
    public static final String BOB_DEBIT = "BOB_DEBIT";//北京银行(借记卡)
    public static final String HRXJB_DEBIT = "HRXJB_DEBIT";//华融湘江银行(借记卡)
    public static final String HSBC_DEBIT = "HSBC_DEBIT";//恒生银行(借记卡)
    public static final String HSB_CREDIT = "HSB_CREDIT";//徽商银行(信用卡)
    public static final String HSB_DEBIT = "HSB_DEBIT";//徽商银行(借记卡)
    public static final String HUNNX_DEBIT = "HUNNX_DEBIT";//湖南农信(借记卡)
    public static final String HUSRB_DEBIT = "HUSRB_DEBIT";//湖商村镇银行(借记卡)
    public static final String HXB_CREDIT = "HXB_CREDIT";//华夏银行(信用卡)
    public static final String HXB_DEBIT = "HXB_DEBIT";//华夏银行(借记卡)
    public static final String HNNX_DEBIT = "HNNX_DEBIT";//河南农信(借记卡)
    public static final String BNC_DEBIT = "BNC_DEBIT";//江西银行(借记卡)
    public static final String BNC_CREDIT = "BNC_CREDIT";//江西银行(信用卡)
    public static final String BJRCB_DEBIT = "BJRCB_DEBIT";//北京农商行(借记卡)
    public static final String JCB_DEBIT = "JCB_DEBIT";//晋城银行(借记卡)
    public static final String JJCCB_DEBIT = "JJCCB_DEBIT";//九江银行(借记卡)
    public static final String JLB_DEBIT = "JLB_DEBIT";//吉林银行(借记卡)
    public static final String JLNX_DEBIT = "JLNX_DEBIT";//吉林农信(借记卡)
    public static final String JNRCB_DEBIT = "JNRCB_DEBIT";//江南农商(借记卡)
    public static final String JRCB_DEBIT = "JRCB_DEBIT";//江阴农商行(借记卡)
    public static final String JSHB_DEBIT = "JSHB_DEBIT";//晋商银行(借记卡)
    public static final String HAINNX_DEBIT = "HAINNX_DEBIT";//海南农信(借记卡)
    public static final String GLB_DEBIT = "GLB_DEBIT";//桂林银行(借记卡)
    public static final String GRCB_CREDIT = "GRCB_CREDIT";//广州农商银行(信用卡)
    public static final String GRCB_DEBIT = "GRCB_DEBIT";//广州农商银行(借记卡)
    public static final String GSB_DEBIT = "GSB_DEBIT";//甘肃银行(借记卡)
    public static final String GSNX_DEBIT = "GSNX_DEBIT";//甘肃农信(借记卡)
    public static final String GXNX_DEBIT = "GXNX_DEBIT";//广西农信(借记卡)
    public static final String GYCB_CREDIT = "GYCB_CREDIT";//贵阳银行(信用卡)
    public static final String GYCB_DEBIT = "GYCB_DEBIT";//贵阳银行(借记卡)
    public static final String GZNX_DEBIT = "GZNX_DEBIT";//贵州农信(借记卡)
    public static final String HAINNX_CREDIT = "HAINNX_CREDIT";//海南农信(信用卡)
    public static final String HKB_DEBIT = "HKB_DEBIT";//汉口银行(借记卡)
    public static final String HANAB_DEBIT = "HANAB_DEBIT";//韩亚银行(借记卡)
    public static final String HBCB_CREDIT = "HBCB_CREDIT";//湖北银行(信用卡)
    public static final String HBCB_DEBIT = "HBCB_DEBIT";//湖北银行(借记卡)
    public static final String HBNX_CREDIT = "HBNX_CREDIT";//湖北农信(信用卡)
    public static final String HBNX_DEBIT = "HBNX_DEBIT";//湖北农信(借记卡)
    public static final String HDCB_DEBIT = "HDCB_DEBIT";//邯郸银行(借记卡)
    public static final String HEBNX_DEBIT = "HEBNX_DEBIT";//河北农信(借记卡)
    public static final String HFB_DEBIT = "HFB_DEBIT";//恒丰银行(借记卡)
    public static final String HKBEA_DEBIT = "HKBEA_DEBIT";//东亚银行(借记卡)
    public static final String JCB_CREDIT = "JCB_CREDIT";//JCB(信用卡)
    public static final String MASTERCARD_CREDIT = "MASTERCARD_CREDIT";//MASTERCARD(信用卡)
    public static final String VISA_CREDIT = "VISA_CREDIT";//VISA(信用卡)

    public static Map<String,String> payTypeMaps = new HashMap<String,String>();//付款方式描述map

    static
    {
        payTypeMaps.put(ICBC_DEBIT,"工商银行(借记卡)");
        payTypeMaps.put(ICBC_CREDIT,"工商银行(信用卡)");
        payTypeMaps.put(ABC_DEBIT,"农业银行(借记卡)");
        payTypeMaps.put(ABC_CREDIT,"农业银行(信用卡)");
        payTypeMaps.put(PSBC_DEBIT,"邮政储蓄银行(借记卡)");
        payTypeMaps.put(PSBC_CREDIT,"邮政储蓄银行(信用卡)");
        payTypeMaps.put(CCB_DEBIT,"建设银行(借记卡)");
        payTypeMaps.put(CCB_CREDIT,"建设银行(信用卡)");
        payTypeMaps.put(CMB_DEBIT,"招商银行(借记卡)");
        payTypeMaps.put(CMB_CREDIT,"招商银行(信用卡)");
        payTypeMaps.put(BOC_DEBIT,"中国银行(借记卡)");
        payTypeMaps.put(BOC_CREDIT,"中国银行(信用卡)");
        payTypeMaps.put(COMM_DEBIT,"交通银行(借记卡)");
        payTypeMaps.put(COMM_CREDIT,"交通银行(信用卡)");
        payTypeMaps.put(SPDB_DEBIT,"浦发银行(借记卡)");
        payTypeMaps.put(SPDB_CREDIT,"浦发银行(信用卡)");
        payTypeMaps.put(GDB_DEBIT,"广发银行(借记卡)");
        payTypeMaps.put(GDB_CREDIT,"广发银行(信用卡)");
        payTypeMaps.put(CMBC_DEBIT,"民生银行(借记卡)");
        payTypeMaps.put(CMBC_CREDIT,"民生银行(信用卡)");
        payTypeMaps.put(PAB_DEBIT,"平安银行(借记卡)");
        payTypeMaps.put(PAB_CREDIT,"平安银行(信用卡)");
        payTypeMaps.put(CEB_DEBIT,"光大银行(借记卡)");
        payTypeMaps.put(CEB_CREDIT,"光大银行(信用卡)");
        payTypeMaps.put(CIB_DEBIT,"兴业银行(借记卡)");
        payTypeMaps.put(CIB_CREDIT,"兴业银行(信用卡)");
        payTypeMaps.put(CITIC_DEBIT,"中信银行(借记卡)");
        payTypeMaps.put(CITIC_CREDIT,"中信银行(信用卡)");
        payTypeMaps.put(BOSH_DEBIT,"上海银行(借记卡)");
        payTypeMaps.put(BOSH_CREDIT,"上海银行(信用卡)");
        payTypeMaps.put(CRB_DEBIT,"华润银行(借记卡)");
        payTypeMaps.put(HZB_DEBIT,"杭州银行(借记卡)");
        payTypeMaps.put(HZB_CREDIT,"杭州银行(信用卡)");
        payTypeMaps.put(BSB_DEBIT,"包商银行(借记卡)");
        payTypeMaps.put(BSB_CREDIT,"包商银行(信用卡)");
        payTypeMaps.put(CQB_DEBIT,"重庆银行(借记卡)");
        payTypeMaps.put(SDEB_DEBIT,"顺德农商行(借记卡)");
        payTypeMaps.put(SZRCB_DEBIT,"深圳农商银行(借记卡)");
        payTypeMaps.put(SZRCB_CREDIT,"深圳农商银行(信用卡)");
        payTypeMaps.put(HRBB_DEBIT,"哈尔滨银行(借记卡)");
        payTypeMaps.put(BOCD_DEBIT,"成都银行(借记卡)");
        payTypeMaps.put(GDNYB_DEBIT,"南粤银行(借记卡)");
        payTypeMaps.put(GDNYB_CREDIT,"南粤银行(信用卡)");
        payTypeMaps.put(GZCB_DEBIT,"广州银行(借记卡))");
        payTypeMaps.put(GZCB_CREDIT,"广州银行(信用卡)");
        payTypeMaps.put(JSB_DEBIT,"江苏银行(借记卡)");
        payTypeMaps.put(JSB_CREDIT,"江苏银行(信用卡)");
        payTypeMaps.put(NBCB_DEBIT,"宁波银行(借记卡)");
        payTypeMaps.put(NBCB_CREDIT,"宁波银行(信用卡)");
        payTypeMaps.put(NJCB_DEBIT,"南京银行(借记卡)");
        payTypeMaps.put(QHNX_DEBIT,"青海农信(借记卡)");
        payTypeMaps.put(ORDOSB_CREDIT,"鄂尔多斯银行(信用卡)");
        payTypeMaps.put(ORDOSB_DEBIT,"鄂尔多斯银行(借记卡)");
        payTypeMaps.put(BJRCB_CREDIT,"北京农商(信用卡)");
        payTypeMaps.put(BHB_DEBIT,"河北银行(借记卡)");
        payTypeMaps.put(BGZB_DEBIT,"贵州银行(借记卡)");
        payTypeMaps.put(BEEB_DEBIT,"鄞州银行(借记卡)");
        payTypeMaps.put(PZHCCB_DEBIT,"攀枝花银行(借记卡)");
        payTypeMaps.put(QDCCB_CREDIT,"青岛银行(信用卡)");
        payTypeMaps.put(QDCCB_DEBIT,"青岛银行(借记卡)");
        payTypeMaps.put(SHINHAN_DEBIT,"新韩银行(借记卡)");
        payTypeMaps.put(QLB_DEBIT,"齐鲁银行(借记卡)");
        payTypeMaps.put(QSB_DEBIT,"齐商银行(借记卡)");
        payTypeMaps.put(ZZB_DEBIT,"郑州银行(借记卡)");
        payTypeMaps.put(CCAB_DEBIT,"长安银行(借记卡)");
        payTypeMaps.put(RZB_DEBIT,"日照银行(借记卡)");
        payTypeMaps.put(SCNX_DEBIT,"四川农信(借记卡)");
        payTypeMaps.put(BEEB_CREDIT,"鄞州银行(信用卡)");
        payTypeMaps.put(SDRCU_DEBIT,"山东农信(借记卡)");
        payTypeMaps.put(BCZ_DEBIT,"沧州银行(借记卡)");
        payTypeMaps.put(SJB_DEBIT,"盛京银行(借记卡)");
        payTypeMaps.put(LNNX_DEBIT,"辽宁农信(借记卡)");
        payTypeMaps.put(JUFENGB_DEBIT,"临朐聚丰村镇银行(借记卡)");
        payTypeMaps.put(ZZB_CREDIT,"郑州银行(信用卡)");
        payTypeMaps.put(JXNXB_DEBIT,"江西农信(借记卡)");
        payTypeMaps.put(JZB_DEBIT,"晋中银行(借记卡)");
        payTypeMaps.put(JZCB_CREDIT,"锦州银行(信用卡)");
        payTypeMaps.put(JZCB_DEBIT,"锦州银行(借记卡)");
        payTypeMaps.put(KLB_DEBIT,"昆仑银行(借记卡)");
        payTypeMaps.put(KRCB_DEBIT,"昆山农商(借记卡)");
        payTypeMaps.put(KUERLECB_DEBIT,"库尔勒市商业银行(借记卡)");
        payTypeMaps.put(LJB_DEBIT,"龙江银行(借记卡)");
        payTypeMaps.put(NYCCB_DEBIT,"南阳村镇银行(借记卡)");
        payTypeMaps.put(LSCCB_DEBIT,"乐山市商业银行(借记卡)");
        payTypeMaps.put(LUZB_DEBIT,"柳州银行(借记卡)");
        payTypeMaps.put(LWB_DEBIT,"莱商银行(借记卡)");
        payTypeMaps.put(LYYHB_DEBIT,"辽阳银行(借记卡)");
        payTypeMaps.put(LZB_DEBIT,"兰州银行(借记卡)");
        payTypeMaps.put(MINTAIB_CREDIT,"民泰银行(信用卡)");
        payTypeMaps.put(MINTAIB_DEBIT,"民泰银行(借记卡)");
        payTypeMaps.put(NCB_DEBIT,"宁波通商银行(借记卡)");
        payTypeMaps.put(NMGNX_DEBIT,"内蒙古农信(借记卡)");
        payTypeMaps.put(XAB_DEBIT,"西安银行(借记卡)");
        payTypeMaps.put(WFB_CREDIT,"潍坊银行(信用卡)");
        payTypeMaps.put(WFB_DEBIT,"潍坊银行(借记卡)");
        payTypeMaps.put(WHB_CREDIT,"威海商业银行(信用卡)");
        payTypeMaps.put(WHB_DEBIT,"威海市商业银行(借记卡)");
        payTypeMaps.put(WHRC_CREDIT,"武汉农商(信用卡)");
        payTypeMaps.put(WHRC_DEBIT,"武汉农商行(借记卡)");
        payTypeMaps.put(WJRCB_DEBIT,"吴江农商行(借记卡)");
        payTypeMaps.put(WLMQB_DEBIT,"乌鲁木齐银行(借记卡)");
        payTypeMaps.put(WRCB_DEBIT,"无锡农商(借记卡)");
        payTypeMaps.put(WZB_DEBIT,"温州银行(借记卡)");
        payTypeMaps.put(XAB_CREDIT,"西安银行(信用卡)");
        payTypeMaps.put(WEB_DEBIT,"微众银行(借记卡)");
        payTypeMaps.put(XIB_DEBIT,"厦门国际银行(借记卡)");
        payTypeMaps.put(XJRCCB_DEBIT,"新疆农信银行(借记卡)");
        payTypeMaps.put(XMCCB_DEBIT,"厦门银行(借记卡)");
        payTypeMaps.put(YNRCCB_DEBIT,"云南农信(借记卡)");
        payTypeMaps.put(YRRCB_CREDIT,"黄河农商银行(信用卡)");
        payTypeMaps.put(YRRCB_DEBIT,"黄河农商银行(借记卡)");
        payTypeMaps.put(YTB_DEBIT,"烟台银行(借记卡)");
        payTypeMaps.put(ZJB_DEBIT,"紫金农商银行(借记卡)");
        payTypeMaps.put(ZJLXRB_DEBIT,"兰溪越商银行(借记卡)");
        payTypeMaps.put(ZJRCUB_CREDIT,"浙江农信(信用卡)");
        payTypeMaps.put(AHRCUB_DEBIT,"安徽省农村信用社联合社(借记卡)");
        payTypeMaps.put(BCZ_CREDIT,"沧州银行(信用卡)");
        payTypeMaps.put(SRB_DEBIT,"上饶银行(借记卡)");
        payTypeMaps.put(ZYB_DEBIT,"中原银行(借记卡)");
        payTypeMaps.put(ZRCB_DEBIT,"张家港农商行(借记卡)");
        payTypeMaps.put(SRCB_CREDIT,"上海农商银行(信用卡)");
        payTypeMaps.put(SRCB_DEBIT,"上海农商银行(借记卡)");
        payTypeMaps.put(ZJTLCB_DEBIT,"浙江泰隆银行(借记卡)");
        payTypeMaps.put(SUZB_DEBIT,"苏州银行(借记卡)");
        payTypeMaps.put(SXNX_DEBIT,"山西农信(借记卡)");
        payTypeMaps.put(SXXH_DEBIT,"陕西信合(借记卡)");
        payTypeMaps.put(ZJRCUB_DEBIT,"浙江农信(借记卡)");
        payTypeMaps.put(AE_CREDIT,"AE(信用卡)");
        payTypeMaps.put(TACCB_CREDIT,"泰安银行(信用卡)");
        payTypeMaps.put(TACCB_DEBIT,"泰安银行(借记卡)");
        payTypeMaps.put(TCRCB_DEBIT,"太仓农商行(借记卡)");
        payTypeMaps.put(TJBHB_CREDIT,"天津滨海农商行(信用卡)");
        payTypeMaps.put(TJBHB_DEBIT,"天津滨海农商行(借记卡)");
        payTypeMaps.put(TJB_DEBIT,"天津银行(借记卡)");
        payTypeMaps.put(TRCB_DEBIT,"天津农商(借记卡");
        payTypeMaps.put(TZB_DEBIT,"台州银行(借记卡)");
        payTypeMaps.put(URB_DEBIT,"联合村镇银行(借记卡)");
        payTypeMaps.put(DYB_CREDIT,"东营银行(信用卡)");
        payTypeMaps.put(CSRCB_DEBIT,"常熟农商银行(借记卡)");
        payTypeMaps.put(CZB_CREDIT,"浙商银行(信用卡)");
        payTypeMaps.put(CZB_DEBIT,"浙商银行(借记卡)");
        payTypeMaps.put(CZCB_CREDIT,"稠州银行(信用卡)");
        payTypeMaps.put(CZCB_DEBIT,"稠州银行(借记卡)");
        payTypeMaps.put(DANDONGB_CREDIT,"丹东银行(信用卡)");
        payTypeMaps.put(DANDONGB_DEBIT,"丹东银行(借记卡)");
        payTypeMaps.put(DLB_CREDIT,"大连银行(信用卡)");
        payTypeMaps.put(DLB_DEBIT,"大连银行(借记卡)");
        payTypeMaps.put(DRCB_CREDIT,"东莞农商银行(信用卡)");
        payTypeMaps.put(DRCB_DEBIT,"东莞农商银行(借记卡)");
        payTypeMaps.put(CSRCB_CREDIT,"常熟农商银行(信用卡)");
        payTypeMaps.put(DYB_DEBIT,"东营银行(借记卡)");
        payTypeMaps.put(DYCCB_DEBIT,"德阳银行(借记卡)");
        payTypeMaps.put(FBB_DEBIT,"富邦华一银行(借记卡)");
        payTypeMaps.put(FDB_DEBIT,"富滇银行(借记卡)");
        payTypeMaps.put(FJHXB_CREDIT,"福建海峡银行(信用卡)");
        payTypeMaps.put(FJHXB_DEBIT,"福建海峡银行(借记卡)");
        payTypeMaps.put(FJNX_DEBIT,"福建农信银行(借记卡)");
        payTypeMaps.put(FUXINB_DEBIT,"阜新银行(借记卡)");
        payTypeMaps.put(BOCDB_DEBIT,"承德银行(借记卡)");
        payTypeMaps.put(JSNX_DEBIT,"江苏农商行(借记卡)");
        payTypeMaps.put(BOLFB_DEBIT,"廊坊银行(借记卡)");
        payTypeMaps.put(CCAB_CREDIT,"长安银行(信用卡)");
        payTypeMaps.put(CBHB_DEBIT,"渤海银行(借记卡)");
        payTypeMaps.put(CDRCB_DEBIT,"成都农商银行(借记卡)");
        payTypeMaps.put(BYK_DEBIT,"营口银行(借记卡)");
        payTypeMaps.put(BOZ_DEBIT,"张家口市商业银行(借记卡)");
        payTypeMaps.put(CFT,"零钱");
        payTypeMaps.put(BOTSB_DEBIT,"唐山银行(借记卡)");
        payTypeMaps.put(BOSZS_DEBIT,"石嘴山银行(借记卡)");
        payTypeMaps.put(BOSXB_DEBIT,"绍兴银行(借记卡)");
        payTypeMaps.put(BONX_DEBIT,"宁夏银行(借记卡)");
        payTypeMaps.put(BONX_CREDIT,"宁夏银行(信用卡)");
        payTypeMaps.put(GDHX_DEBIT,"广东华兴银行(借记卡)");
        payTypeMaps.put(BOLB_DEBIT,"洛阳银行(借记卡)");
        payTypeMaps.put(BOJX_DEBIT,"嘉兴银行(借记卡)");
        payTypeMaps.put(BOIMCB_DEBIT,"内蒙古银行(借记卡)");
        payTypeMaps.put(BOHN_DEBIT,"海南银行(借记卡)");
        payTypeMaps.put(BOD_DEBIT,"东莞银行(借记卡)");
        payTypeMaps.put(CQRCB_CREDIT,"重庆农商银行(信用卡)");
        payTypeMaps.put(CQRCB_DEBIT,"重庆农商银行(借记卡)");
        payTypeMaps.put(CQTGB_DEBIT,"重庆三峡银行(借记卡)");
        payTypeMaps.put(BOD_CREDIT,"东莞银行(信用卡)");
        payTypeMaps.put(CSCB_DEBIT,"长沙银行(借记卡)");
        payTypeMaps.put(BOB_CREDIT,"北京银行(信用卡)");
        payTypeMaps.put(GDRCU_DEBIT,"广东农信银行(借记卡)");
        payTypeMaps.put(BOB_DEBIT,"北京银行(借记卡)");
        payTypeMaps.put(HRXJB_DEBIT,"华融湘江银行(借记卡)");
        payTypeMaps.put(HSBC_DEBIT,"恒生银行(借记卡)");
        payTypeMaps.put(HSB_CREDIT,"徽商银行(信用卡)");
        payTypeMaps.put(HSB_DEBIT,"徽商银行(借记卡)");
        payTypeMaps.put(HUNNX_DEBIT,"湖南农信(借记卡)");
        payTypeMaps.put(HUSRB_DEBIT,"湖商村镇银行(借记卡)");
        payTypeMaps.put(HXB_CREDIT,"华夏银行(信用卡)");
        payTypeMaps.put(HXB_DEBIT,"华夏银行(借记卡)");
        payTypeMaps.put(HNNX_DEBIT,"河南农信(借记卡)");
        payTypeMaps.put(BNC_DEBIT,"江西银行(借记卡)");
        payTypeMaps.put(BNC_CREDIT,"江西银行(信用卡)");
        payTypeMaps.put(BJRCB_DEBIT,"北京农商行(借记卡)");
        payTypeMaps.put(JCB_DEBIT,"晋城银行(借记卡)");
        payTypeMaps.put(JJCCB_DEBIT,"九江银行(借记卡)");
        payTypeMaps.put(JLB_DEBIT,"吉林银行(借记卡)");
        payTypeMaps.put(JLNX_DEBIT,"吉林农信(借记卡)");
        payTypeMaps.put(JNRCB_DEBIT,"江南农商(借记卡)");
        payTypeMaps.put(JRCB_DEBIT,"江阴农商行(借记卡)");
        payTypeMaps.put(JSHB_DEBIT,"晋商银行(借记卡)");
        payTypeMaps.put(HAINNX_DEBIT,"海南农信(借记卡)");
        payTypeMaps.put(GLB_DEBIT,"桂林银行(借记卡)");
        payTypeMaps.put(GRCB_CREDIT,"广州农商银行(信用卡)");
        payTypeMaps.put(GRCB_DEBIT,"广州农商银行(借记卡)");
        payTypeMaps.put(GSB_DEBIT,"甘肃银行(借记卡)");
        payTypeMaps.put(GSNX_DEBIT,"甘肃农信(借记卡)");
        payTypeMaps.put(GXNX_DEBIT,"广西农信(借记卡)");
        payTypeMaps.put(GYCB_CREDIT,"贵阳银行(信用卡)");
        payTypeMaps.put(GYCB_DEBIT,"贵阳银行(借记卡)");
        payTypeMaps.put(GZNX_DEBIT,"贵州农信(借记卡)");
        payTypeMaps.put(HAINNX_CREDIT,"海南农信(信用卡)");
        payTypeMaps.put(HKB_DEBIT,"汉口银行(借记卡)");
        payTypeMaps.put(HANAB_DEBIT,"韩亚银行(借记卡)");
        payTypeMaps.put(HBCB_CREDIT,"湖北银行(信用卡)");
        payTypeMaps.put(HBCB_DEBIT,"湖北银行(借记卡)");
        payTypeMaps.put(HBNX_CREDIT,"湖北农信(信用卡)");
        payTypeMaps.put(HBNX_DEBIT,"湖北农信(借记卡)");
        payTypeMaps.put(HDCB_DEBIT,"邯郸银行(借记卡)");
        payTypeMaps.put(HEBNX_DEBIT,"河北农信(借记卡)");
        payTypeMaps.put(HFB_DEBIT,"恒丰银行(借记卡)");
        payTypeMaps.put(HKBEA_DEBIT,"东亚银行(借记卡)");
        payTypeMaps.put(JCB_CREDIT,"JCB(信用卡)");
        payTypeMaps.put(MASTERCARD_CREDIT,"MASTERCARD(信用卡)");
        payTypeMaps.put(VISA_CREDIT,"VISA(信用卡)");
    }
}
