package com.caipiao.service.config;

import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ParameterMapper;
import com.caipiao.domain.common.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;


/**
 * 系统配置参数工具类-优先级为-》数据库-》config.properties文件
 * Created by kouyi on 2017-09-22
 */
@Component("sysConfig")
public class SysConfig {
    private static Logger logger = LoggerFactory.getLogger(SysConfig.class);
    private static final String fileName = "config.properties";//系统配置文件名
    private static volatile Map<String, String> systemMap = null;//系统参数容器
    private static final String MAX_ZHUSHU_VERIFY = "MAX_ZHUSHU_VERIFY";//系统配置一小时内投注方案超过最大注数则需要人工审核出票
    private static final String OUT_TICKET_OPEN = "OUT_TICKET_OPEN";//默认出票开关(0-关闭 1-打开)
    private static final String SELL_COMMISSION_RATE = "SELL_COMMISSION_RATE";//销售提成最高点位
    private static final String SELL_MONEY_INTERVAL = "SELL_MONEY_INTERVAL";//获取销售提成百分比区间
    private static final String FILTER_USER_ID = "FILTER_USER_ID";//出票-过滤测试用户编号
    private static final String BLACK_USER_ID = "BLACK_USER_ID";//出票-黑名单用户编号
    private static final String TICKET_RATE_RETURN = "TICKET_RATE_RETURN";//单张票最大回报率配置
    private static final String TICKET_RATE_RETURN_MIN = "TICKET_RATE_RETURN_MIN";//单张票最小回报率配置
    private static final String TICKET_FIXED_MONEY = "FIXED_MONEY_TICKET";//固定金额的票分配出票商
    private static final String FIXED_USER_TICKET = "FIXED_USER_TICKET";//某些用户的票固定到某个出票商，用户编号,...|出票商|状态
    private static final String FIXED_USER_TICKETTWO = "FIXED_USER_TICKETTWO";//某些用户的票固定到某个出票商，用户编号,...|出票商|状态

    @Autowired
    private ParameterMapper paramDao;

    /**
     * 获取静态资源域名地址
     * @return
     */
    public static String getHostStatic() {
        return getString("STATIC_HOST");
    }

    /**
     * 根据彩种id获取彩种logo地址
     * @param   lotteryId   彩种id
     */
    public static String getLotteryLogo(String lotteryId) {
        return getHostStatic() + "/image/lottery/" + lotteryId + ".png";
    }

    /**
     * 获取后台管理域名地址
     * @return
     */
    public static String getHostAdmin() {
        return getString("ADMIN_HOST");
    }

    /**
     * 获取app接口域名地址
     * @return
     */
    public static String getHostApp() {
        return getString("API_HOST");
    }

    /**
     * Spring加载好配置文件后-执行系统参数初始化入口
     */
    @PostConstruct
    public void initSysConfig() {
        initialize(paramDao);//先初始化完成一次,再启动守护线程
        new Thread(new InitParameterThread(paramDao)).start();
    }

    /**
     * 系统参数初始化方法
     */
    public void initialize(ParameterMapper paramDao) {
        logger.debug("----------------------------------------------");
        logger.debug("读取系统参数中...");
        logger.debug("----------------------------------------------");

        HashMap configMap = new HashMap<String, String>();

        logger.debug("从数据表(tb_parameter)加载配置参数");
        configMap = loadTableParameter(configMap, paramDao);
        logger.debug("加载数据表参数完成 总计" + configMap.size() + "个");

        logger.debug("从配置文件(config.properties)加载配置参数");
        configMap = loadSystemParameter(configMap);
        logger.debug("加载配置文件参数完成 总计" + configMap.size() + "个");
        systemMap = configMap;

        logger.debug("----------------------------------------------");
        logger.debug("读取系统参数完成!");
        logger.debug("----------------------------------------------");
    }

    /**
     * 加载数据库中的配置参数
     * @param hashMap
     * @return
     */
    private HashMap loadTableParameter(HashMap hashMap, ParameterMapper paramDao) {
        if(StringUtil.isEmpty(hashMap)) {
            hashMap = new HashMap<String, String>();
        }

        List<Parameter> parameterList = paramDao.queryParameterList();
        if(StringUtil.isEmpty(parameterList)) {
            return hashMap;
        }

        for(Parameter pa : parameterList) {
            hashMap.put(pa.getPmKey(), pa.getPmValue());
        }
        return hashMap;
    }

    /**
     * 加载配置文件中系统参数
     * @return
     */
    public HashMap loadSystemParameter(HashMap hashMap) {
        if(StringUtil.isEmpty(hashMap)) {
            hashMap = new HashMap<String, String>();
        }
        Properties properties = loadSystemProperties();
        if(StringUtil.isEmpty(properties)) {
            return hashMap;
        }
        hashMap.putAll(properties);
        return hashMap;
    }

    /**
     * 加载配置文件
     * @return
     */
    public Properties loadSystemProperties() {
        InputStream ins = null;
        try {
            Properties properties = new Properties();
            ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (StringUtil.isEmpty(ins)) {
                return properties;
            }
            properties.load(ins);
            return properties;
        } catch (Exception e) {
            logger.error("加载系统参数文件异常", e);
            return null;
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
            } catch (Exception ex) {
                logger.error("加载系统参数文件时-关闭流失败", ex);
            }
        }
    }

    /**
     * 获取int类型系统配置参数
     * @param key
     * @return
     */
    public static int getInt(String key) {
        try {
            return Integer.parseInt(getString(key));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取String类型系统配置参数
     * @param key
     * @return
     */
    public static String getString(String key) {
        try {
            if (StringUtil.isEmpty(key)) {
                return null;
            }
            return systemMap.get(key).toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取double类型系统配置参数
     * @param key
     * @return
     */
    public static double getDouble(String key) {
        try {
            return Double.parseDouble(getString(key));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取销售提成最高点位
     * @return
     */
    public static double getCommissionRate() {
        return SysConfig.getDouble(SELL_COMMISSION_RATE);
    }

    /**
     * 获取销售提成百分比区间
     * @return
     */
    public static String getSellIntervalRate() {
        return SysConfig.getString(SELL_MONEY_INTERVAL);
    }

    /**
     * 获取单张票最大回报率
     * @return
     */
    public static String getTicketRateReturn() {
        return SysConfig.getString(TICKET_RATE_RETURN);
    }

    /**
     * 获取单张票最小回报率
     * @return
     */
    public static String getTicketMinRateReturn() {
        return SysConfig.getString(TICKET_RATE_RETURN_MIN);
    }

    /**
     * 获取固定金额范围
     * @return
     */
    public static String getTicketFixedMoneyRange() {
        return SysConfig.getString(TICKET_FIXED_MONEY);
    }

    /**
     * 获取用户固定分配出票商
     * @return
     */
    public static String getTicketFixedUser() {
        return SysConfig.getString(FIXED_USER_TICKET);
    }

    /**
     * 获取用户固定分配出票商2
     * @return
     */
    public static String getTicketFixedUserTwo() {
        return SysConfig.getString(FIXED_USER_TICKETTWO);
    }

    /**
     * 判断是否符合大票规则
     * @param zhuShu
     * @param endTime
     * @return
     */
    public static boolean isBigTicket(Integer zhuShu ,Date endTime) {
        int maxZhuShu = SysConfig.getInt(MAX_ZHUSHU_VERIFY);
        if(StringUtil.isEmpty(endTime)) {
            endTime = new Date();//默认
        }
        if(endTime.getTime() - new Date().getTime() < 60 * 60 * 1000 && zhuShu > maxZhuShu){
            return true;
        }
        return false;
    }

    /**
     * 判断打开默认出票开关
     * @return
     */
    public static boolean isOpenOutTicket() {
        int open = SysConfig.getInt(OUT_TICKET_OPEN);
        if(open == 1){
            return true;
        }
        return false;
    }

    /**
     * 获取过滤测试用户列表
     * @return
     */
    public static List<Integer> getFilterUser(){
        List<Integer> userList = new ArrayList<>();
        String userStr = SysConfig.getString(FILTER_USER_ID);
        if(StringUtil.isEmpty(userStr)){
            return userList;
        }
        String[] userStrs = userStr.split("\\,");
        for(String uid : userStrs) {
            if(!NumberUtil.isNumber(uid)) {
                continue;
            }
            userList.add(StringUtil.parseInt(uid));
        }
        return userList;
    }

    /**
     * 获取黑名单用户列表
     * @return
     */
    public static int getBlackUser(){
        return SysConfig.getInt(BLACK_USER_ID);
    }
}
